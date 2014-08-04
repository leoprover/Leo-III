package leo.datastructures.internal

import scala.language.implicitConversions

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

  def δ_expandable: Boolean // TODO: Implement efficiently
  def head_δ_expand: TermImpl
  def full_δ_expand: TermImpl

  protected[internal] def inc(scopeIndex: Int) = ???

  def expandDefinitions(rep: Int) = ???

  /** Right-folding on terms. */
  def foldRight[A](symFunc: (Signature#Key) => A)(boundFunc: (Type, Int) => A)(absFunc: (Type, A) => A)(appFunc: (A, A) => A)(tAbsFunc: (A) => A)(tAppFunc: (A, Type) => A) = ???

  /** Return the β-nf of the term */
  def betaNormalize = ???

  /** Returns true iff the term is well-typed. */
  def typeCheck = ???

  protected[internal] def instantiate(scope: Int, by: Type) = ???

  // Substitutions
  def substitute(what: Term, by: Term) = ???

  def freeVars = ???

  // Queries on terms
  def ty = ???

  val isTypeAbs: Boolean = _
  val isTypeApp: Boolean = _
  val isTermAbs: Boolean = _
  val isTermApp: Boolean = _
  val isAtom: Boolean = _

  def pretty = ???
}

/////////////////////////////////////////////////
// Implementation of specific term constructors
/////////////////////////////////////////////////

protected[internal] case class Root(hd: Head, args: Spine) extends TermImpl {
  def headSym = Some(hd)

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
}


// For all terms that have not been normalized, assume they are a redex, represented
// by this term instance
protected[internal] case class Redex(body: TermImpl, args: Spine) extends TermImpl {
  def headSym = None

  override lazy val δ_expandable = body.δ_expandable || args.δ_expandable

  def head_δ_expand = Redex(body.head_δ_expand, args)
  def full_δ_expand = Redex(body.full_δ_expand, args.δ_expand)
}



protected[internal] case class TermAbstr(typ: Type, body: TermImpl) extends TermImpl {
  def headSym = body.headSym

  override lazy val δ_expandable = body.δ_expandable

  def head_δ_expand = TermAbstr(typ, body.head_δ_expand)
  def full_δ_expand = TermAbstr(typ, body.full_δ_expand)
}



protected[internal] case class TypeAbstr(body: TermImpl) extends TermImpl {
  def headSym = body.headSym

  override lazy val δ_expandable = body.δ_expandable

  def head_δ_expand = TypeAbstr(body.head_δ_expand)
  def full_δ_expand = TypeAbstr(body.full_δ_expand)
}


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

//case class TermClos(term: VAL, subst: Subst) extends TermImpl


protected[internal] sealed abstract class Head {
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
}
protected[internal] case class UiAtom(id: Head#Const) extends Head {
  val δ_expandable = false
  def δ_expand = this
}
protected[internal] case class DefAtom(id: Head#Const) extends Head {
  private lazy val meta = Signature.get(id)

  val δ_expandable = true
  def δ_expand = ??? //Some(meta._defn) TODO: checkout types: term vs. termImpl
}

protected[internal] sealed abstract class Spine {
  val δ_expandable: Boolean
  def δ_expand: Spine
}

protected[internal] case object SNil extends Spine
case class App(hd: TermImpl, tail: Spine) extends Spine
protected[internal] case class TyApp(hd: Type, tail: Spine) extends Spine
/*case class SpineClos(spine: Spine, subst: Subst) extends Spine


sealed abstract class Subst {
  /** s.comp(s') = t
    * where t = s o s' */
  def comp(other: Subst): Subst
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
}
case class Cons(ft: Front, subst: Subst) extends Subst {
  def comp(other: Subst) = other match {
    case Shift(0) => this
    case s => Cons(ft.substitute(s), subst.comp(s))
  }
}


object Subst {
  val id: Subst    = Shift(0)
  val shift: Subst = shift(1)
  def shift(n: Int): Subst = Shift(n)
}


sealed abstract class Front {
  def substitute(subst: Subst): Front
}
case class BoundFront(n: Int) extends Front {
  def substitute(subst: Subst) = subst match {
    case Cons(ft, s) if n == 1 => ft
    case Cons(_, s)           => BoundFront(n-1).substitute(s)
    case Shift(k) => BoundFront(n+k)
  }
}
case class TermFront(term: VAL) extends Front {
  def substitute(subst: Subst) = TermFront(TermClos(term, subst))
}

/**
case object UNBOUND extends Front {
  def substitute(subst: Subst) = UNBOUND
}
*/
*/


