package leo.datastructures.internal.terms

import scala.language.implicitConversions
import leo.datastructures.Pretty
import leo.datastructures.internal.{Type, Signature, Term}

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
protected[terms] sealed abstract class TermImpl extends Term {
  def headSym: Head // only on normal forms?
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

  val isTypeAbs: Boolean = false
  val isTypeApp: Boolean = false
  val isTermAbs: Boolean = false
  val isTermApp: Boolean = false
  val isAtom: Boolean = false


}

/////////////////////////////////////////////////
// Implementation of specific term constructors
/////////////////////////////////////////////////

/** Representation of terms that are in (weak) head normal form. */
protected[terms] case class Root(hd: Head, args: Spine) extends TermImpl {
  import TermImpl.{headToTerm}

  def headSym = hd

  def preNormalize(s: Subst) = (this, s)

  def normalize(subst: Subst) = hd match {
    case b@BoundIndex(typ,_) => b.substitute(subst) match {
      case BoundFront(i) => Root(BoundIndex(typ,i), args.normalize(subst))
      case TermFront(t)  => Redex(t, args.normalize(subst)).normalize(Subst.id)
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
  override val isAtom = args == SNil
  override val isTermApp = args != SNil

  lazy val ty = ty0(hd.ty, args.length)

  private def ty0(funty: Type, arglen: Int): Type = arglen match {
    case 0 => funty
    case k => ty0(funty._funCodomainType, arglen-1)
  }

  def freeVars = hd match {
    case BoundIndex(_,_) => args.freeVars
    case _             => args.freeVars + hd
  }

  /** Pretty */
  def pretty = s"${hd.pretty} ⋅ (${args.pretty})"
}


// For all terms that have not been normalized, assume they are a redex, represented
// by this term instance
protected[terms] case class Redex(body: TermImpl, args: Spine) extends TermImpl {
  def headSym = body.headSym

  def preNormalize(s: Subst): TermClosure = {
    val (bodyPNF, t) = (body, s)//body.preNormalize(s)

    bodyPNF match {
      case r@Root(hd,sp) => args match {
        case SNil => (r,t)
        case App(h, tail) => (Root(hd, sp ++ args),t)
//        case SpineClos(_,s2) => ???
      }
      case TermAbstr(_,b) => args  match {
        case SNil => (bodyPNF,t)
        case App(hd, SNil) => b.preNormalize(Cons(TermFront(hd),t)) // eta contract
        case App(hd, tail) => Redex(b,tail).preNormalize(Cons(TermFront(hd),t))
//        case SpineClos(_,_) => ???
      }
      case Redex(_,_) => ??? // Not possible if body is prenormalized
      case TermClos(term,substi) => term.preNormalize(substi o t)
    }
  }

  private def preNormalize0(s: Subst, spS: Subst): TermClosure = ???

  def normalize(subst: Subst) = {
    val (a,b) = preNormalize(subst)
    a.normalize(b)

  }

  /** Handling def. expansion */
  override lazy val δ_expandable = body.δ_expandable || args.δ_expandable

  def head_δ_expand = Redex(body.head_δ_expand, args)
  def full_δ_expand = Redex(body.full_δ_expand, args.δ_expand)

  /** Queries on terms */
  lazy val ty = ty0(body.ty, args.length)

  private def ty0(funty: Type, arglen: Int): Type = arglen match {
    case 0 => funty
    case k => ty0(funty._funCodomainType, arglen-1)
  }

  override val isTermApp = true

  def freeVars = body.freeVars ++ args.freeVars

  /** Pretty */
  def pretty = s"[${body.pretty}] ⋅ (${args.pretty})"
}



protected[terms] case class TermAbstr(typ: Type, body: TermImpl) extends TermImpl {
  def headSym = body.headSym

  def preNormalize(s: Subst) = (this,s)

  def normalize(subst: Subst) = TermAbstr(typ, body.normalize(subst.sink))

  /** Handling def. expansion */
  override lazy val δ_expandable = body.δ_expandable

  def head_δ_expand = TermAbstr(typ, body.head_δ_expand)
  def full_δ_expand = TermAbstr(typ, body.full_δ_expand)

  /** Queries on terms */
  lazy val ty = typ ->: body.ty

  override val isTermAbs = true

  def freeVars = body.freeVars

  /** Pretty */
  def pretty = s"λ. (${body.pretty})"
}


protected[terms] case class TypeAbstr(body: TermImpl) extends TermImpl {
  import Type.∀

  def headSym = body.headSym

  def preNormalize(s: Subst) = (this,s)

  def normalize(subst: Subst) = body.normalize(subst)

  /** Handling def. expansion */
  override lazy val δ_expandable = body.δ_expandable

  def head_δ_expand = TypeAbstr(body.head_δ_expand)
  def full_δ_expand = TypeAbstr(body.full_δ_expand)

  /** Queries on terms */
  lazy val ty = ∀(body.ty)

  override val isTypeAbs = true
  def freeVars = body.freeVars

  /** Pretty */
  def pretty = s"Λ. (${body.pretty})"
}


protected[terms] case class TermClos(term: TermImpl, σ: Subst) extends TermImpl {
  def headSym = term.headSym match {
    case b@BoundIndex(typ, scope) => b.substitute(σ) match {
      case BoundFront(k) => BoundIndex(typ,k)
      case TermFront(t) => t.headSym
    }
    case other => other
  }

  def preNormalize(s: Subst) = term.preNormalize(σ o s)

  def normalize(subst: Subst) = term.normalize(σ.comp(subst))

  /** Handling def. expansion */
  override lazy val δ_expandable = ???

  def head_δ_expand = ???
  def full_δ_expand = ???

  /** Queries on terms */
  lazy val ty = term.ty

  def freeVars = term.freeVars

  /** Pretty */
  def pretty = s"${term.pretty}[${σ.pretty}]"
}


/////////////////////////////////////////////////
// Implementation of head symbols
/////////////////////////////////////////////////

protected[terms] sealed abstract class Head extends Pretty {
  // Handling def. expansion
  val δ_expandable: Boolean
  def δ_expand: TermImpl

  // Queries
  def ty: Type
}


protected[terms] case class BoundIndex(typ: Type, scope: Int) extends Head {
  // Handling def. expansion
  val δ_expandable = false
  def δ_expand = this

  // Queries
  val ty = typ

  def substitute(s: Subst) = s match {
    case Cons(ft, s) if scope == 1 => ft
    case Cons(_, s)           => BoundFront(scope-1).substitute(s)
    case Shift(k) => BoundFront(scope+k)
  }

  // Pretty printing
  override val pretty = s"$scope"
}
protected[terms] case class UiAtom(id: Signature#Key) extends Head {
  private lazy val meta = Signature.get(id)

  // Handling def. expansion
  val δ_expandable = false
  def δ_expand = this

  // Queries
  val ty = meta._ty

  // Pretty printing
  override def pretty = s"${meta.name}"
}
protected[terms] case class DefAtom(id: Signature#Key) extends Head {
  private lazy val meta = Signature.get(id)

  // Handling def. expansion
  val δ_expandable = true
  def δ_expand = ??? //Some(meta._defn) TODO: checkout types: term vs. termImpl

  // Queries
  val ty = meta._ty

  // Pretty printing
  override def pretty = s"${meta.name}"
}






/////////////////////////////////////////////////
// Implementation of spines
/////////////////////////////////////////////////

/**
 * // TODO Documentation
 */
protected[terms] sealed abstract class Spine extends Pretty {
  import TermImpl.{mkSpineCons => cons}

  def normalize(subst: Subst): Spine

  // Handling def. expansion
  def δ_expandable: Boolean
  def δ_expand: Spine

  // Queries
  def length: Int
  def freeVars: Set[Term]
  def asTerms: Seq[Either[Term, Type]]

  // Misc
  def ++(sp: Spine): Spine
  def +(t: TermImpl): Spine = ++(cons(Left(t),SNil))
}

protected[terms] case object SNil extends Spine {
  import TermImpl.{mkSpineNil => nil}

  def normalize(subst: Subst) = nil

  // Handling def. expansion
  val δ_expandable = false
  val δ_expand = SNil

  // Queries
  val freeVars = Set[Term]()
  val length = 0
  val asTerms = Seq()

  // Misc
  def ++(sp: Spine) = sp

  // Pretty printing
  override val pretty = "⊥"
}

protected[terms] case class App(hd: TermImpl, tail: Spine) extends Spine {
  import TermImpl.{mkSpineCons => cons}

  def normalize(subst: Subst) = cons(Left(hd.normalize(subst)), tail.normalize(subst))

  // Handling def. expansion
  def δ_expandable = hd.δ_expandable || tail.δ_expandable
  def δ_expand = cons(Left(hd.full_δ_expand), tail.δ_expand)

  // Queries
  def freeVars = hd.freeVars ++ tail.freeVars
  val length = 1 + tail.length
  def asTerms = Left(hd) +: tail.asTerms

  // Misc
  def ++(sp: Spine) = cons(Left(hd), tail ++ sp)

  // Pretty printing
  override def pretty = s"${hd.pretty};${tail.pretty}"
}

protected[terms] case class TyApp(hd: Type, tail: Spine) extends Spine {
  def normalize(subst: Subst) = TyApp(hd, tail.normalize(subst))

  // Handling def. expansion
  def δ_expandable = tail.δ_expandable
  def δ_expand = TyApp(hd, tail.δ_expand)

  // Queries
  def freeVars = tail.freeVars
  val length = 1 + tail.length
  def asTerms = Right(hd) +: tail.asTerms

  // Misc
  def ++(sp: Spine) = TyApp(hd, tail ++ sp)

  // Pretty printing
  override def pretty = s"${hd.pretty};${tail.pretty}"
}

//protected[internal] case class SpineClos(spine: Spine, subst: Subst) extends Spine {
//
//  def normalize(subst2: Subst) = spine.normalize(subst.comp(subst2))
//
//  // Handling def. expansion
//  def δ_expandable = ???
//  def δ_expand = ???
//
//  // Queries
//  def freeVars = ???
//  val length = spine.length
//
//  // Misc
//  def ++(sp: Spine) = ???
//
//  // Pretty printing
//  override def pretty = s"(${spine.pretty}[${subst.pretty}])"
//}


/**
 * //TODO Documentation
 */
object TermImpl {

  /////////////////////////////////////////////
  // Hash tables for DAG representation of perfectly
  // shared terms
  /////////////////////////////////////////////

  // primitive symbols (heads)
  protected[TermImpl] var boundAtoms: Map[Type, Map[Int, Head]] = Map.empty
  protected[TermImpl] var symbolAtoms: Map[Signature#Key, Head] = Map.empty

  // composite terms
  protected[TermImpl] var termAbstractions: Map[TermImpl, Map[Type, TermImpl]] = Map.empty
  protected[TermImpl] var typeAbstractions: Map[TermImpl, TermImpl] = Map.empty
  protected[TermImpl] var roots: Map[Head, Map[Spine, TermImpl]] = Map.empty
  protected[TermImpl] var redexes: Map[TermImpl, Map[Spine, TermImpl]] = Map.empty

  // Spines
  protected[TermImpl] var spines: Map[Either[TermImpl, Type], Map[Spine, Spine]] = Map.empty

  /////////////////////////////////////////////
  // Implementation based constructor methods
  /////////////////////////////////////////////

  // primitive symbols (heads)
  protected[terms] def mkDefAtom(id: Signature#Key): Head = symbolAtoms.get(id) match {
      case Some(hd) => hd
      case None     => val hd = DefAtom(id)
                       symbolAtoms += ((id, hd))
                       hd
  }
  protected[terms] def mkUiAtom(id: Signature#Key): Head = symbolAtoms.get(id) match {
    case Some(hd) => hd
    case None     => val hd =  UiAtom(id)
                     symbolAtoms += ((id, hd))
                     hd
  }
  protected[terms] def mkBoundAtom(t: Type, scope: Int): Head = boundAtoms.get(t) match {
    case Some(inner) => inner.get(scope) match {
      case Some(hd)   => hd
      case None       => val hd = BoundIndex(t, scope)
                         boundAtoms += ((t,inner.+((scope, hd))))
                         hd
    }
    case None        => val hd = BoundIndex(t, scope)
                        boundAtoms += ((t, Map((scope, hd))))
                        hd
  }

  // composite terms
  protected[terms] def mkRoot(hd: Head, args: Spine): TermImpl = roots.get(hd) match {
    case Some(inner) => inner.get(args) match {
      case Some(root)  => root
      case None        => val root = Root(hd, args)
                          redexes += ((hd,inner.+((args, root))))
                          root
    }
    case None        => val root = Root(hd, args)
                        redexes += ((hd, Map((args, root))))
                        root
  }
  protected[terms] def mkRedex(left: TermImpl, args: Spine): TermImpl = redexes.get(left) match {
    case Some(inner) => inner.get(args) match {
      case Some(redex) => redex
      case None        => val redex = Redex(left, args)
                          redexes += ((left,inner.+((args, redex))))
                          redex
    }
    case None        => val redex = Redex(left, args)
                        redexes += ((left, Map((args, redex))))
                        redex
  }
  protected[terms] def mkTermAbstr(t: Type, body: TermImpl): TermImpl = termAbstractions.get(body) match {
    case Some(inner) => inner.get(t) match {
      case Some(abs)   => abs
      case None        => val abs = TermAbstr(t, body)
                          termAbstractions += ((body,inner.+((t, abs))))
                          abs
    }
    case None        => val abs = TermAbstr(t, body)
                        termAbstractions += ((body, Map((t, abs))))
                        abs
  }
  protected[terms] def mkTypeAbstr(body: TermImpl): TermImpl = typeAbstractions.get(body) match {
    case Some(abs) => abs
    case None      => val abs = TypeAbstr(body)
                      typeAbstractions += ((body, abs))
                      abs
  }

  // Spines
  protected[terms] def mkSpineNil: Spine = SNil
  protected[terms] def mkSpineCons(term: Either[TermImpl, Type], tail: Spine): Spine = spines.get(term) match {
    case Some(inner) => inner.get(tail) match {
      case Some(sp)   => sp
      case None       => val sp = term.fold(App(_, tail),TyApp(_, tail))
                         spines += ((term,inner.+((tail, sp))))
                         sp
    }
    case None       => val sp = term.fold(App(_, tail),TyApp(_, tail))
                       spines += ((term, Map((tail, sp))))
                       sp
  }

  private def mkSpine(args: Seq[TermImpl]): Spine = args.foldRight[Spine](SNil)({case (t,sp) => mkSpineCons(Left(t),sp)})
  private def mkTySpine(args: Seq[Type]): Spine = args.foldRight[Spine](SNil)({case (t,sp) => mkSpineCons(Right(t),sp)})
  private def mkGenSpine(args: Seq[Either[TermImpl,Type]]): Spine = args.foldRight[Spine](SNil)({case (t,sp) => mkSpineCons(t,sp)})

  /////////////////////////////////////////////
  // Public visible term constructors
  /////////////////////////////////////////////
  def mkAtom(id: Signature#Key): TermImpl = Signature.get(id).isDefined match {
    case true  => mkRoot(mkDefAtom(id), SNil)
    case false => mkRoot(mkUiAtom(id), SNil)
  }

  def mkBound(typ: Type, scope: Int): TermImpl = mkRoot(mkBoundAtom(typ, scope), SNil)

  def mkTermApp(func: TermImpl, arg: TermImpl): TermImpl = func.isAtom match {
    case true  => mkRoot(func.headSym, mkSpineCons(Left(arg),SNil))
    case false => func match {
      case Root(h,sp)  => mkRoot(h,sp + arg)
      case Redex(r,sp) => mkRedex(r, sp + arg)
      case other       => mkRedex(other, mkSpineCons(Left(arg), SNil))
    }
  }
  def mkTermApp(func: TermImpl, args: Seq[TermImpl]): TermImpl = func.isAtom match {
    case true  => mkRoot(func.headSym, mkSpine(args))
    case false => func match {
      case Root(h,sp)  => mkRoot(h,sp ++ mkSpine(args))
      case Redex(r,sp) => mkRedex(r, sp ++ mkSpine(args))
      case other       => mkRedex(other, mkSpine(args))
    }
  }
  def mkTermAbs(typ: Type, body: TermImpl): TermImpl = mkTermAbstr(typ, body)

  def λ(hd: Type)(body: TermImpl) = mkTermAbs(hd, body)
  def λ(hd: Type, hds: Type*)(body: TermImpl): TermImpl = {
    λ(hd)(hds.foldRight(body)(λ(_)(_)))
  }

  def mkTypeAbs(body: TermImpl): TermImpl = mkTypeAbstr(body)
  def mkTypeApp(func: TermImpl, args: Seq[Type]): TermImpl  = func.isAtom match {
    case true  => mkRoot(func.headSym, mkTySpine(args))
    case false => func match {
      case Root(h,sp)  => mkRoot(h,sp ++ mkTySpine(args))
      case Redex(r,sp) => mkRedex(r, sp ++ mkTySpine(args))
      case other       => mkRedex(other, mkTySpine(args))
    }
  }

  def mkApp(func: TermImpl, args: Seq[Either[TermImpl, Type]]): TermImpl  = func.isAtom match {
    case true  => mkRoot(func.headSym, mkGenSpine(args))
    case false => func match {
      case Root(h,sp)  => mkRoot(h,sp ++ mkGenSpine(args))
      case Redex(r,sp) => mkRedex(r, sp ++ mkGenSpine(args))
      case other       => mkRedex(other, mkGenSpine(args))
    }
  }

  implicit def headToTerm(hd: Head): TermImpl = mkRoot(hd, mkSpineNil)

  // TODO: Remote this from here
  implicit def intToBoundVar(in: (Int, Type)) = mkBound(in._2,in._1)
  implicit def intsToBoundVar(in: (Int, Int)) = mkBound(in._2,in._1)
  implicit def keyToAtom(in: Signature#Key) = mkAtom(in)


}






