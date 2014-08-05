package leo.datastructures.internal

import scala.language.implicitConversions
import leo.datastructures.Pretty

///////////////////////////////////////////////
// Shared-implementation based specialization
// of term interface
///////////////////////////////////////////////

/**
 * Abstract implementation class for DAG-based shared terms
 * in spine notation.
 *
 * @author Alexander Steen
 * @since 04.08.2014
 */
protected[internal] sealed abstract class TermImpl extends Term {
  def headSym: Option[Head] // only on normal forms?
  // TODO: All :D
  //def δ_expand(count: Int): VAL
  def betaNormalize: TermImpl = normalize(Subst.id)
  def normalize(subst: Subst): TermImpl

  type TermClosure = (TermImpl, Subst)
  protected[internal] def preNormalize(subst: Subst): TermClosure

  def δ_expandable: Boolean // TODO: Implement efficiently
  def head_δ_expand: TermImpl
  def full_δ_expand: TermImpl

  protected[internal] def inc(scopeIndex: Int): Term = ???

  def expandDefinitions(rep: Int): Term = ???

  /** Right-folding on terms. */
  def foldRight[A](symFunc: (Signature#Key) => A)(boundFunc: (Type, Int) => A)(absFunc: (Type, A) => A)(appFunc: (A, A) => A)(tAbsFunc: (A) => A)(tAppFunc: (A, Type) => A): A = ???

  /** Returns true iff the term is well-typed. */
  def typeCheck: Boolean = ???

  protected[internal] def instantiate(scope: Int, by: Type): Term = ???

  // Substitutions
  def substitute(what: Term, by: Term): Term = ???

  // Queries on terms
  def ty: Type = ???

  def isTypeAbs: Boolean = ???
  def isTypeApp: Boolean = ???
  def isTermAbs: Boolean = ???
  def isTermApp: Boolean = ???
  def isAtom: Boolean = ???


}

/////////////////////////////////////////////////
// Implementation of specific term constructors
/////////////////////////////////////////////////

/** Representation of terms that are in (weak) head normal form. */
protected[internal] case class Root(hd: Head, args: Spine) extends TermImpl {
  def headSym = Some(hd)

  def preNormalize(s: Subst) = (this, s)

  def normalize(subst: Subst) = hd match {
    case b@BoundIndex(_) => b.substitute(subst) match {
      case BoundFront(i) => Root(BoundIndex(i), args.normalize(subst))
      case TermFront(t)  => Redex(t, args).normalize(subst)
    }
    case _             => Root(hd, args.normalize(subst))
  }

  /** Handling def. expansion */
  override lazy val δ_expandable = hd.δ_expandable || args.δ_expandable

  def head_δ_expand = hd.δ_expandable match {
    case true => Redex(hd.δ_expand, args)
    case false => this
  }
  def full_δ_expand = δ_expandable match {
    case true => Redex(hd.δ_expand, args.δ_expand)
    case false => this
  }

  /** Queries on terms */
  def freeVars = hd match {
    case BoundIndex(_) => args.freeVars
    case _             => args.freeVars + hd
  }

  /** Pretty */
  def pretty = s"${hd.pretty} ⋅ (${args.pretty})"
}


// For all terms that have not been normalized, assume they are a redex, represented
// by this term instance
protected[internal] case class Redex(body: TermImpl, args: Spine) extends TermImpl {
  def headSym = None

  def preNormalize(s: Subst) = {
    val (bodyPNF, t) = (body, s)//body.preNormalize(s)

    bodyPNF match {
      case r@Root(hd,sp) => args match {
        case SNil => (r,t)
        case App(h, tail) => (Root(hd, SpineClos(sp ++ args,t)),Subst.id)
        case SpineClos(sp,s2) => ???
      }
      case TermAbstr(_,b) => args match {
        case SNil => (bodyPNF,t)
        case App(hd, SNil) => b.preNormalize(Cons(TermFront(hd),t))
        case App(hd, tail) => Redex(b,tail).preNormalize(Cons(TermFront(hd),t))
      }
      case Redex(_,_) => ??? // Not possible if body is prenormalized
      case TermClos(term,substi) => (term.preNormalize(substi o t))
    }
  }

  private def preNormalize0(s: Subst, spS: Subst): TermClosure = ???

  def normalize(subst: Subst) = {
    val a = preNormalize(subst)
    a._1.normalize(a._2)
    

//    nBody match {
//      case TermAbstr(_, b) => nArgs match {
//        case App(hd, SNil) => TermClos(b, Cons(TermFront(hd), subst))
//        case App(hd, tail) => ???
//        case SNil  => nBody
//      }
//      case Root(hd,args2) => nArgs match {
//        case SNil => nBody
//        case _    => ??? // append spine
//      }
//    }
  }

  /** Handling def. expansion */
  override lazy val δ_expandable = body.δ_expandable || args.δ_expandable

  def head_δ_expand = Redex(body.head_δ_expand, args)
  def full_δ_expand = Redex(body.full_δ_expand, args.δ_expand)

  /** Queries on terms */
  def freeVars = body.freeVars ++ args.freeVars

  /** Pretty */
  def pretty = s"[${body.pretty}] ⋅ (${args.pretty})"
}



protected[internal] case class TermAbstr(typ: Type, body: TermImpl) extends TermImpl {
  def headSym = body.headSym

  def preNormalize(s: Subst) = (this,s)

  def normalize(subst: Subst) = TermAbstr(typ, body.normalize(subst.sink))

  /** Handling def. expansion */
  override lazy val δ_expandable = body.δ_expandable

  def head_δ_expand = TermAbstr(typ, body.head_δ_expand)
  def full_δ_expand = TermAbstr(typ, body.full_δ_expand)

  /** Queries on terms */
  def freeVars = body.freeVars

  /** Pretty */
  def pretty = s"λ. ${body.pretty}"
}



//protected[internal] case class TypeAbstr(body: TermImpl) extends TermImpl {
//  def headSym = body.headSym
//
//  def normalize(subst: Subst) = ???
//
//  /** Handling def. expansion */
//  override lazy val δ_expandable = body.δ_expandable
//
//  def head_δ_expand = TypeAbstr(body.head_δ_expand)
//  def full_δ_expand = TypeAbstr(body.full_δ_expand)
//
//  /** Queries on terms */
//  def freeVars = body.freeVars
//
//  /** Pretty */
//  def pretty = s"Λ. ${body.pretty}"
//}


protected[internal] case class TermClos(term: TermImpl, σ: Subst) extends TermImpl {
  def headSym = None

  def preNormalize(s: Subst) = term.preNormalize(σ o s)

  def normalize(subst: Subst) = term.normalize(σ.comp(subst))

  /** Handling def. expansion */
  override lazy val δ_expandable = ???

  def head_δ_expand = ???
  def full_δ_expand = ???

  /** Queries on terms */
  def freeVars = ???

  /** Pretty */
  def pretty = s"${term.pretty}[${σ.pretty}]"
}


////// objects for construction


protected[internal] object Root {
  def mkRoot(hd: Head, args: Spine): TermImpl = Root(hd, args)
  def mkRoot(hd: Head): TermImpl              = mkRoot(hd, SNil)
}


protected[internal] object Redex {
  def mkRedex(body: TermImpl, args: Spine): TermImpl = args match {
    case SNil  => mkRedex(body)
    case _ => Redex(body, args)
  }
  def mkRedex(body: TermImpl): TermImpl              = body
  // TODO: Is this valid? (eliminating redex node around nil application)
}






/////////////////////////////////////////////////
// Implementation of head symbols
/////////////////////////////////////////////////

protected[internal] sealed abstract class Head extends Pretty {
  type Const = Signature#Key

  val δ_expandable: Boolean
  def δ_expand: TermImpl
}

protected[internal] object Head {
  implicit def headToTerm(hd: Head): TermImpl = Root.mkRoot(hd)
}



protected[internal] case class BoundIndex(scope: Int) extends Head {
  val δ_expandable = false
  def δ_expand = this

  def substitute(s: Subst) = s match {
    case Cons(ft, s) if scope == 1 => ft
    case Cons(_, s)           => BoundFront(scope-1).substitute(s)
    case Shift(k) => BoundFront(scope+k)
  }

  /** Pretty */
  override val pretty = s"$scope"
}
protected[internal] case class UiAtom(id: Head#Const) extends Head {
  private lazy val meta = Signature.get(id)

  val δ_expandable = false
  def δ_expand = this

  /** Pretty */
  override def pretty = s"${meta.name}"
}
protected[internal] case class DefAtom(id: Head#Const) extends Head {
  private lazy val meta = Signature.get(id)

  val δ_expandable = true
  def δ_expand = ??? //Some(meta._defn) TODO: checkout types: term vs. termImpl

  /** Pretty */
  override def pretty = s"${meta.name}"
}






/////////////////////////////////////////////////
// Implementation of spines
/////////////////////////////////////////////////

protected[internal] sealed abstract class Spine extends Pretty {
  def normalize(subst: Subst): Spine

  def ++(sp: Spine): Spine

  /** Handling def. expansion */
  def δ_expandable: Boolean
  def δ_expand: Spine

  /** Queries on terms */
  def freeVars: Set[Term]
}

protected[internal] case object SNil extends Spine {
  def normalize(subst: Subst) = SNil

  def ++(sp: Spine) = sp

  /** Handling def. expansion */
  val δ_expandable = false
  val δ_expand = SNil

  /** Queries on terms */
  val freeVars = Set[Term]()

  /** Pretty */
  override val pretty = "⊥"
}

protected[internal] case class App(hd: TermImpl, tail: Spine) extends Spine {
  def normalize(subst: Subst) = App(hd.normalize(subst), tail.normalize(subst))

  def ++(sp: Spine) = App(hd, tail ++ sp)

  /** Handling def. expansion */
  def δ_expandable = hd.δ_expandable || tail.δ_expandable
  def δ_expand = App(hd.full_δ_expand, tail.δ_expand)

  /** Queries on terms */
  def freeVars = hd.freeVars ++ tail.freeVars

  /** Pretty */
  override def pretty = s"${hd.pretty};${tail.pretty}"
}
//protected[internal] case class TyApp(hd: Type, tail: Spine) extends Spine {
//  def normalize(subst: Subst) = TyApp(hd, tail.normalize(subst))
//
//  /** Handling def. expansion */
//  def δ_expandable = tail.δ_expandable
//  def δ_expand = TyApp(hd, tail.δ_expand)
//
//  /** Queries on terms */
//  def freeVars = tail.freeVars
//
//  /** Pretty */
//  override def pretty = s"${hd.pretty};${tail.pretty}"
//}

protected[internal] case class SpineClos(spine: Spine, subst: Subst) extends Spine {
  def normalize(subst2: Subst) = spine.normalize(subst.comp(subst2))

  def ++(sp: Spine) = ???

  /** Handling def. expansion */
  def δ_expandable = ???
  def δ_expand = ???

  /** Queries on terms */
  def freeVars = ???

  /** Pretty */
  override def pretty = s"(${spine.pretty}[${subst.pretty}])"
}






/////////////////////////////////////////////////
// Implementation of substitutions
/////////////////////////////////////////////////
// TODO: Normalisation on subst needed?
sealed abstract class Subst extends Pretty {
  /** s.comp(s') = t
    * where t = s o s' */
  def comp(other: Subst): Subst
  def o = comp(_)
  /** Sink substitution inside lambda abstraction, i.e. create 1.s o ↑*/
  def sink = Cons(BoundFront(1),this.comp(Shift(1)))
}
case class Shift(n: Int) extends Subst {
  def comp(other: Subst) = n match {
    case 0 => other
    case _ => other match {
      case Shift(0) => this
      case Shift(m) => Shift(n+m)
      case Cons(ft, s) => Shift(n-1).comp(s)
    }
  }

  /** Pretty */
  override def pretty = s"↑$n"
}
case class Cons(ft: Front, subst: Subst) extends Subst {
  def comp(other: Subst) = other match {
    case Shift(0) => this
    case s => Cons(ft.substitute(s), subst.comp(s))
  }

  /** Pretty */
  override def pretty = s"${ft.pretty}•${subst.pretty}"
}


object Subst {
  def id: Subst    = Shift(0)
  def shift: Subst = shift(1)
  def shift(n: Int): Subst = Shift(n)
}


sealed abstract class Front extends Pretty {
  def substitute(subst: Subst): Front
}
case class BoundFront(n: Int) extends Front {
  def substitute(subst: Subst) = subst match {
    case Cons(ft, s) if n == 1 => ft
    case Cons(_, s)           => BoundFront(n-1).substitute(s)
    case Shift(k) => BoundFront(n+k)
  }

  /** Pretty */
  override def pretty = s"$n"
}
case class TermFront(term: TermImpl) extends Front {
  def substitute(subst: Subst) = TermFront(TermClos(term, subst))

  /** Pretty */
  override def pretty = term.pretty
}

/**
case object UNBOUND extends Front {
  def substitute(subst: Subst) = UNBOUND
}
*/



