package leo.datastructures.internal.terms

import leo.datastructures.Pretty
import scala.annotation.tailrec


// TODO: Normalisation on subst needed?
/**
 * // TODO Doc
 */
sealed abstract class Subst extends Pretty {
  /** s.comp(s') = t
    * where t = s o s' */
  def comp(other: Subst): Subst
  def o = comp(_)

  def cons(ft: Front): Subst

  /** Sink substitution inside lambda abstraction, i.e. create 1.s o ↑*/
  def sink: Subst

  def normalize: Subst

  def isShift: Boolean
  def isConsd: Boolean = !isShift

  // last shift
  def shiftedBy: Int

  def length: Int
  def drop(n: Int): Subst

  def frontAt(i: Int): Front
}


/////////////////////////test


class RASubst(shift: Int, fronts: Vector[Front] = Vector.empty) extends Subst {

  def normalize: Subst = new RASubst(shift, fronts.map({_ match {
    case TermFront(t) => TermFront(t.betaNormalize)
    case a => a
  } }))

  def comp(other: Subst): Subst = fronts.isEmpty match {
    case true => shift match {
      case 0 => other
      case _ => other.isShift match {
        case true => new RASubst(shift+other.shiftedBy)
        case false => (shift - other.length) match {
          case n if n >= 0 => new RASubst(shift-other.length+other.shiftedBy) // its a new shift
          case n if n < 0 => other.drop(shift) // its a Cons with n dropped fronts
        }
      }
    }
    case false => other.isShift match {
      case true if other.shiftedBy == 0 => this
      case _ => new RASubst(shift, fronts.map(_.substitute(other))); ??? // TODO adjust front and merge subst, maybe merge with above code to a simpler block
    }
  }

  def cons(ft: Front): Subst = new RASubst(shift, ft +: fronts)
  lazy val sink: Subst = (this o new RASubst(1)).cons(BoundFront(1))

  lazy val pretty: String = fronts.isEmpty match {
    case true => shift match {
      case 0 => "id"
      case k => s"↑$k"
    }
    case false => fronts.map(_.pretty).mkString("•") ++ s"↑$shift"
  }

  lazy val isShift = fronts.isEmpty
  lazy val shiftedBy = shift
  lazy val length = fronts.length
  def drop(n: Int): Subst = new RASubst(shift, fronts.drop(n))

  def frontAt(i: Int) = fronts(i)
}


//class MySubst(shift: Int) extends Subst with Vector[Front] {
//
//  def pretty = this.isEmpty match {
//    case true => shift match {
//      case 0 => "id"
//      case k => s"↑$k"
//    }
//    case false => this.map(_.pretty).mkString("•") ++ s"↑$shift"
//  }
//
//  def comp(other: Subst) = this.isEmpty match {
//    case true => ??? // shift
//    case false => ???  // cons
//  }
//
//  lazy val normalize = this.map({_ match {
//    case TermFront(t) => TermFront(t.betaNormalize)
//    case a => a
//  }})
//}

/////////////////////////////////////////////////
// Implementation of substitutions
/////////////////////////////////////////////////

abstract class AlgebraicSubst extends Subst {
  def sink: Subst = (this o Shift(1)).cons(BoundFront(1))
}

case class Shift(n: Int) extends AlgebraicSubst {
  def comp(other: Subst) = n match {
    case 0 => other
    case _ => other match {
      case Shift(0) => this
      case Shift(m) => Shift(n+m)
      case Cons(ft, s) => Shift(n-1).comp(s)
    }
  }

  def cons(ft: Front) = Cons(ft, this)

  val normalize = this

  /** Pretty */
  override def pretty = n match {
    case 0 => "id"
    case k => s"↑$k"
  }
}

case class Cons(ft: Front, subst: Subst) extends AlgebraicSubst {
  def comp(other: Subst) = other match {
    case Shift(0) => this
    case s => Cons(ft.substitute(s), subst.comp(s))
  }

  def cons(ft: Front) = Cons(ft, this)

  lazy val normalize = ft match {
    case BoundFront(_) => Cons(ft, subst.normalize)
    case TermFront(t)  => Cons(TermFront(t.betaNormalize), subst.normalize) //TODO: eta contract here
    case TypeFront(_) => Cons(ft, subst.normalize)
  }

  /** Pretty */
  override def pretty = s"${ft.pretty}•${subst.pretty}"
}


object Subst {
  def id: Subst    = Shift(0)
  def shift: Subst = shift(1)
  def shift(n: Int): Subst = Shift(n)
//
//  def consWithEta(ft: Front, onto: Subst): Subst = ft match {
//    case tf@TermFront(t) => Cons(TermFront(t.weakEtaContract(Subst.id, 0)), onto)
//    case a => Cons(a, onto)
//  }
}


sealed abstract class Front extends Pretty {
  def substitute(subst: Subst): Front
}
case class BoundFront(n: Int) extends Front {
  def substitute(subst: Subst) = substitute0(n, subst)

  @tailrec
  private def substitute0(scope: Int, subst: Subst): Front = subst match {
    case Cons(ft, s) if scope == 1 => ft
    case Cons(_, s) => substitute0(scope-1, s)
    case Shift(k) => BoundFront(scope+k)
  }

  /** Pretty */
  override def pretty = s"$n"
}
case class TermFront(term: Term) extends Front {
  def substitute(subst: Subst) = TermFront(term.closure((subst)))

  /** Pretty */
  override def pretty = term.pretty
}
case class TypeFront(typ: Type) extends Front {
  def substitute(subst: Subst) = TypeFront(typ.closure((subst)))

  /** Pretty */
  override def pretty = typ.pretty
}