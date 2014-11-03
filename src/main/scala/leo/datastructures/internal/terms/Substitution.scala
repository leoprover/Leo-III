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
  def +:(ft: Front): Subst = this.cons(ft)

  /** Sink substitution inside lambda abstraction, i.e. create 1.s o ↑*/
  def sink: Subst

  def normalize: Subst

  def isShift: Boolean
  def isConsd: Boolean = !isShift

  // last shift
  def shiftedBy: Int

  def length: Int
  def drop(n: Int): Subst

  def substBndIdx(i: Int): Front
  def fronts: Seq[Front]
}


/////////////////////////test


class RASubst(shift: Int, fts: Vector[Front] = Vector.empty) extends Subst {

  def normalize: Subst = new RASubst(shift, fts.map({_ match {
    case TermFront(t) => TermFront(t.betaNormalize)
    case a => a
  } }))

  def comp(other: Subst): Subst = other.isShift match {
    case true if other.shiftedBy == 0 => this
    case true if this.isShift => new RASubst(shift + other.shiftedBy)
    case true => new RASubst(shift + other.shiftedBy, fts.map(_.substitute(other)))
    case _ if this.isShift && this.shiftedBy == 0 => other
    case _ => (shift - other.length) match {
      case n if n >= 0 => new RASubst(n+other.shiftedBy, fts.map(_.substitute(other)))
      case _ => new RASubst(other.shiftedBy, fts.map(_.substitute(other)) ++ other.fronts.drop(shift))
    }
  }

  def cons(ft: Front): Subst = new RASubst(shift, ft +: fts)
  lazy val sink: Subst = BoundFront(1) +: (this o new RASubst(1))

  lazy val pretty: String = fts.isEmpty match {
    case true => shift match {
      case 0 => "id"
      case k => s"↑$k"
    }
    case false => fts.map(_.pretty).mkString("•") ++ s"↑$shift"
  }

  lazy val isShift = fts.isEmpty
  lazy val shiftedBy = shift
  lazy val length = fts.length
  def drop(n: Int): Subst = new RASubst(shift, fts.drop(n))

  def substBndIdx(i: Int) = fts.length >= i match {
    case true => fts(i-1)
    case false => BoundFront(i+shift)
  }
  lazy val fronts = fts
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

  val fronts = Seq.empty
  def substBndIdx(i: Int) = BoundFront(i + n)
  def drop(n: Int) = throw new IllegalArgumentException("Shift substitution does not contain any fronts to drop")
  val length = 0

  val shiftedBy = n
  val isShift = true

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

  lazy val fronts = ft +: subst.fronts
  def substBndIdx(i: Int) = i match {
    case 1 => ft
    case _ => subst.substBndIdx(i-1)
  }
  def drop(n: Int) = n match {
    case 0 => this
    case _ => subst.drop(n-1)
  }
  lazy val length = 1 + subst.length

  lazy val shiftedBy = subst.shiftedBy
  val isShift = false

  /** Pretty */
  override def pretty = s"${ft.pretty}•${subst.pretty}"
}


object Subst {
//  def id: Subst    = Shift(0)
//  def shift: Subst = shift(1)
//  def shift(n: Int): Subst = Shift(n)

  val id: Subst    = new RASubst(0)
  val shift: Subst = new RASubst(1)
  def shift(n: Int): Subst = new RASubst(n)

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
  def substitute(subst: Subst) = subst.substBndIdx(n)

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