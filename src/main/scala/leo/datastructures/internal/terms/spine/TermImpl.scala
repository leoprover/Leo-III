package leo.datastructures.internal.terms.spine

import scala.language.implicitConversions

import leo.datastructures.Pretty
import leo.datastructures.internal.Signature
import leo.datastructures.internal.terms._
import leo.datastructures.internal.terms.BoundFront
import leo.datastructures.internal.terms.TermFront

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

  def betaNormalize: Term = normalize(liftedId, liftedId)
  def closure(subst: Subst) = TermClos(this, (subst, Subst.id))

//  type TermClosure = (TermImpl, Subst)
//  protected[internal] def preNormalize(subst: Subst): TermClosure

  /** Right-folding on terms. */
  def foldRight[A](symFunc: (Signature#Key) => A)(boundFunc: (Type, Int) => A)(absFunc: (Type, A) => A)(appFunc: (A, A) => A)(tAbsFunc: (A) => A)(tAppFunc: (A, Type) => A): A = ???

  // Substitutions
  def substitute(what: Term, by: Term): Term = ???
  protected[internal] def instantiate(scope: Int, by: Type): Term = ???

  // Other
  protected[internal] def inc(scopeIndex: Int): Term = ???
}

/////////////////////////////////////////////////
// Implementation of specific term constructors
/////////////////////////////////////////////////

/** Representation of terms that are in (weak) head normal form. */
protected[terms] case class Root(hd: Head, args: Spine) extends TermImpl {
  import TermImpl.{headToTerm, mkRedex, mkRoot}

  // Predicates on terms
  val isAtom = args == SNil
  val isTermAbs = false
  val isTypeAbs = false
  val isApp = args != SNil

  // Handling def. expansion
  lazy val δ_expandable = hd.δ_expandable || args.δ_expandable
  def partial_δ_expand(rep: Int) = mkRedex(hd.partial_δ_expand(rep), args.partial_δ_expand(rep))
//    hd.partial_δ_expand(rep) match {
//    case Root(h, SNil) => Root(h, args.partial_δ_expand(rep))
//    case Root(h, sp)   => Root(h, sp ++ args.partial_δ_expand(rep))
//    case other         => Redex(other, args.partial_δ_expand(rep))
//  }
  def full_δ_expand = δ_expandable match {
    case true => mkRedex(hd.full_δ_expand, args.full_δ_expand)
    case false => this
  }

  lazy val head_δ_expandable = hd.δ_expandable
  def head_δ_expand = hd.δ_expandable match {
    case true => mkRedex(hd.partial_δ_expand(1), args)
    case false => this
  }

  // Queries on terms
  lazy val ty = ty0(hd.ty, args)
  private def ty0(funty: Type, s: Spine): Type = s match {
    case SNil => funty
    case App(s0,tail) => funty match {
      case AbstractionTypeNode(_, out) => ty0(out, tail)
      case _ => throw new IllegalArgumentException(s"${this.pretty}: expression not well typed")// this should not happen if well-typed
    }
    case TyApp(s0,tail) => funty match {
      case tt@ForallTypeNode(body) => ty0(tt.instantiate(s0), tail)
      case _ => throw new IllegalArgumentException(s"${this.pretty}: expression not well typed")// this should not happen if well-typed
    }
    case _ => throw new IllegalArgumentException("closure occured in term")// other cases do not apply
  }
  lazy val freeVars = hd match {
    case BoundIndex(_,_) => args.freeVars
    case _             => args.freeVars + hd
  }
  lazy val boundVars = hd match {
    case BoundIndex(_,_) => args.freeVars + hd
    case _             => args.freeVars
  }
  lazy val headSymbol = TermImpl.headToTerm(hd)

  // Other operations
  lazy val typeCheck = typeCheck0(hd.ty, args)
  private def typeCheck0(t: Type, sp: Spine): Boolean = sp match {
    case SNil => true
    case App(head, rest) => t.isFunType && t._funDomainType == head.ty && typeCheck0(t._funCodomainType, rest) && head.typeCheck
    case TyApp(t2, rest) => t.isPolyType && typeCheck0(t.instantiate(t2), rest)
    case _ => true // other cases should not appear
  }


  def normalize(termSubst: Substitution, typeSubst: Substitution) = {
    val headTermSubst = termSubst._1
    val spineTermSubst= termSubst._2
    val headTypeSubst = typeSubst._1
    val spineTypeSUbst= typeSubst._2
    hd match {
      case b@BoundIndex(typ, _) => b.substitute(headTermSubst) match {
        case BoundFront(i) => mkRoot(BoundIndex(typ.substitute(headTypeSubst), i), args.normalize(headTermSubst, headTypeSubst))
        case TermFront(t) => mkRedex(t, args).normalize((Subst.id, headTermSubst), (headTypeSubst, headTypeSubst)) // Closure kann nach hinten gezogen werden
      }
      case _ => Root(hd, args.normalize(headTermSubst, headTypeSubst))
    }
  }

  /** Pretty */
  def pretty = s"${hd.pretty} ⋅ (${args.pretty})"
}


// For all terms that have not been normalized, assume they are a redex, represented
// by this term instance
protected[terms] case class Redex(body: Term, args: Spine) extends TermImpl {
  import TermImpl.{mkRedex, mkRoot}

  // Predicates on terms
  val isAtom = false
  val isTermAbs = false
  val isTypeAbs = false
  val isApp = true

  // Handling def. expansion
  lazy val δ_expandable = body.δ_expandable || args.δ_expandable
  def partial_δ_expand(rep: Int) = mkRedex(body.partial_δ_expand(rep), args.partial_δ_expand(rep))
  def full_δ_expand = mkRedex(body.full_δ_expand, args.full_δ_expand)

  lazy val head_δ_expandable = headSymbol.δ_expandable
  def head_δ_expand = mkRedex(body.head_δ_expand, args)

  // Queries on terms
  lazy val ty = ty0(body.ty, args)
  private def ty0(funty: Type, s: Spine): Type = s match {
    case SNil => funty
    case App(s0,tail) => funty match {
      case AbstractionTypeNode(_, out) => ty0(out, tail)
      case _ => throw new IllegalArgumentException(s"${this.pretty}: expression not well typed")// this should not happen if well-typed
    }
    case TyApp(s0,tail) => funty match {
      case tt@ForallTypeNode(body) => ty0(tt.instantiate(s0), tail)
      case _ => throw new IllegalArgumentException(s"${this.pretty}: expression not well typed")// this should not happen if well-typed
    }
    case _ => throw new IllegalArgumentException("closure occured in term")// other cases do not apply
  }
  val freeVars = body.freeVars ++ args.freeVars
  val boundVars = body.boundVars ++ args.boundVars
  lazy val headSymbol = body.headSymbol

  // Other operations
  lazy val typeCheck = typeCheck0(body.ty, args)
  private def typeCheck0(t: Type, sp: Spine): Boolean = sp match {
    case SNil => true
    case App(head, rest) => t.isFunType && t._funDomainType == head.ty && typeCheck0(t._funCodomainType, rest) && head.typeCheck
    case TyApp(t2, rest) => t.isPolyType && typeCheck0(t.instantiate(t2), rest)
    case SpineClos(s, (sub1, sub2)) => typeCheck0(t, s.normalize(sub1,sub2))
  }

  import leo.datastructures.internal.terms.{Cons, TermFront}
  def normalize(termSubst: Substitution, typeSubst: Substitution) = body match {
    case abs@TermAbstr(_, t) => args match {
      case SNil => abs.normalize(termSubst, typeSubst)
      case App(s0, SNil) => t.normalize((Cons(TermFront(s0).substitute(termSubst._2),termSubst._1), termSubst._2), typeSubst)
      case App(s0, s) => mkRedex(t, s).normalize((Cons(TermFront(s0).substitute(termSubst._2),termSubst._1), termSubst._2), typeSubst)
      case SpineClos(s, spSubst) => mkRedex(abs, s).normalize((termSubst._1, spSubst._1 o termSubst._2),(typeSubst._1, spSubst._2 o typeSubst._2))
      case _ => throw new IllegalArgumentException("application not well typed")// case TyApp not allowed here
    }
    case abs@TypeAbstr(t) => args match {
      case SNil => abs.normalize(termSubst, typeSubst)
      case TyApp(s0, SNil) => t.normalize(termSubst, ((Cons(TypeFront(s0),typeSubst._1)),typeSubst._2))
      case TyApp(s0, s) => mkRedex(t, s).normalize(termSubst, ((Cons(TypeFront(s0),typeSubst._1)),typeSubst._2))
      case SpineClos(s, spSubst) => mkRedex(abs, s).normalize((termSubst._1, spSubst._1 o termSubst._2),(typeSubst._1, spSubst._2 o typeSubst._2))
      case _ => throw new IllegalArgumentException("application not well typed")// case App not allowed here
    }
    case r@Root(hd, s) => args match {
      case SNil => r.normalize(termSubst, typeSubst)
      case SpineClos(s2, spSubst) => mkRedex(r, s2).normalize((termSubst._1, spSubst._1 o termSubst._2),(typeSubst._1, spSubst._2 o typeSubst._2))
      case other => mkRoot(hd, s.merge((termSubst._1, typeSubst._1),other,(termSubst._2, typeSubst._2))).normalize((termSubst._1, Subst.id), (typeSubst._1, Subst.id))
    }
    case red@Redex(t2, s) => args match {
      case SNil => red.normalize(termSubst, typeSubst)
      case SpineClos(s2, spSubst) => mkRedex(red, s2).normalize((termSubst._1, spSubst._1 o termSubst._2),(typeSubst._1, spSubst._2 o typeSubst._2))
      case other => mkRedex(t2, s.merge((termSubst._1, typeSubst._1),other,(termSubst._2, typeSubst._2))).normalize((termSubst._1, Subst.id), (typeSubst._1, Subst.id))
    }
    case tc@TermClos(t2, subst2) => mkRedex(t2, args).normalize((termSubst._1 o subst2._1, termSubst._2),(typeSubst._1 o subst2._2, typeSubst._2))
  }



  /** Pretty */
  def pretty = s"[${body.pretty}] ⋅ (${args.pretty})"
}



protected[terms] case class TermAbstr(typ: Type, body: Term) extends TermImpl {
  import TermImpl.mkTermAbstr

  // Predicates on terms
  val isAtom = false
  val isTermAbs = true
  val isTypeAbs = false
  val isApp = false

  // Handling def. expansion
  lazy val δ_expandable = body.δ_expandable
  def partial_δ_expand(rep: Int) = mkTermAbstr(typ, body.partial_δ_expand(rep))
  def full_δ_expand = mkTermAbstr(typ, body.full_δ_expand)

  lazy val head_δ_expandable = headSymbol.δ_expandable
  def head_δ_expand = mkTermAbstr(typ, body.head_δ_expand)

  // Queries on terms
  lazy val ty = typ ->: body.ty
  val freeVars = body.freeVars
  val boundVars = body.boundVars
  lazy val headSymbol = body.headSymbol

  // Other operations
  lazy val typeCheck = body.typeCheck

  def normalize(termSubst: Substitution, typeSubst: Substitution) = mkTermAbstr(typ.substitute(typeSubst._1), body.normalize((termSubst._1.sink, termSubst._2.sink), typeSubst))


  /** Pretty */
  def pretty = s"λ. (${body.pretty})"
}


protected[terms] case class TypeAbstr(body: Term) extends TermImpl {
  import Type.∀
  import TermImpl.mkTypeAbstr

  // Predicates on terms
  val isAtom = false
  val isTermAbs = false
  val isTypeAbs = true
  val isApp = false

  // Handling def. expansion
  lazy val δ_expandable = body.δ_expandable
  def partial_δ_expand(rep: Int) = mkTypeAbstr(body.partial_δ_expand(rep))
  def full_δ_expand = mkTypeAbstr(body.full_δ_expand)

  lazy val head_δ_expandable = headSymbol.δ_expandable
  def head_δ_expand = mkTypeAbstr(body.head_δ_expand)

  // Queries on terms
  lazy val ty = ∀(body.ty)
  val freeVars = body.freeVars
  val boundVars = body.boundVars
  lazy val headSymbol = body.headSymbol

  // Other operations
  lazy val typeCheck = body.typeCheck

  def normalize(termSubst: Substitution, typeSubst: Substitution) = mkTypeAbstr(body.normalize(termSubst, (typeSubst._1.sink, typeSubst._2.sink)))

  /** Pretty */
  def pretty = s"Λ. (${body.pretty})"
}


protected[spine] case class TermClos(term: Term, σ: (Subst, Subst)) extends TermImpl {
  // Closure should never be handed to the outside

  // Predicates on terms
  val isAtom = false
  val isTermAbs = false
  val isTypeAbs = false
  val isApp = false

  // Handling def. expansion
  lazy val δ_expandable = ???
  def partial_δ_expand(rep: Int) = ???
  def full_δ_expand = ???

  lazy val head_δ_expandable = ???
  def head_δ_expand = ???

  // Queries on terms
  lazy val ty = term.ty
  lazy val freeVars = Set[Term]()
  lazy val boundVars = Set[Term]()
  lazy val headSymbol = ???
//    term.headSymbol match {
//    case Root(head, SNil) => head match {
//      case b@BoundIndex(typ, scope) => b.substitute(σ) match {
//        case BoundFront(k) => TermImpl.headToTerm(BoundIndex(typ,k))
//        case TermFront(t) => t.headSymbol
//        // TODO this correct? reapply subst? dont think so
//      }
//      case other => TermImpl.headToTerm(other)
//    }
//    case _ => throw new UnknownError("head symbol not a root")
//  }

  // Other operations
  lazy val typeCheck = ???

//  def preNormalize(s: Subst) = term.preNormalize(σ o s)

  def normalize(termSubst: Substitution, typeSubst: Substitution) = term.normalize((σ._1 o termSubst._1, termSubst._2), (σ._2 o typeSubst._1, typeSubst._2))

  /** Pretty */
  def pretty = s"${term.pretty}[${σ._1.pretty}/${σ._2.pretty}]"
}


/////////////////////////////////////////////////
// Implementation of head symbols
/////////////////////////////////////////////////

protected[terms] sealed abstract class Head extends Pretty {
  // Handling def. expansion
  val δ_expandable: Boolean
  def partial_δ_expand(rep: Int): Term
  def full_δ_expand: Term

  // Queries
  def ty: Type
}


protected[terms] case class BoundIndex(typ: Type, scope: Int) extends Head {
  import leo.datastructures.internal.terms.{Cons, Shift}

  // Handling def. expansion
  val δ_expandable = false
  def partial_δ_expand(rep: Int) = full_δ_expand
  def full_δ_expand = TermImpl.headToTerm(this)

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

protected[terms] case class Atom(id: Signature#Key) extends Head {
  private lazy val meta = Signature.get(id)

  // Handling def. expansion
  lazy val δ_expandable = meta.hasDefn
  def partial_δ_expand(rep: Int) = rep match {
    case 0 => TermImpl.headToTerm(this)
    case -1 => meta.hasDefn match {
      case false => TermImpl.headToTerm(this)
      case true => meta._defn.partial_δ_expand(rep)
    }
    case n => meta.hasDefn match {
      case false => TermImpl.headToTerm(this)
      case true => meta._defn.partial_δ_expand(rep-1)
    }
  }
  def full_δ_expand = partial_δ_expand(-1)

  // Queries
  lazy val ty = meta._ty

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

  def normalize(termSubst: Subst, typeSubst: Subst): Spine

  // Handling def. expansion
  def δ_expandable: Boolean
  def partial_δ_expand(rep: Int): Spine
  def full_δ_expand: Spine

  // Queries
  def length: Int
  def freeVars: Set[Term]
  def boundVars: Set[Term]
  def asTerms: Seq[Either[Term, Type]]

  // Misc
  def merge(subst: (Subst, Subst), sp: Spine, spSubst: (Subst, Subst)): Spine

  def ++(sp: Spine): Spine
  def +(t: TermImpl): Spine = ++(cons(Left(t),SNil))


}

protected[terms] case object SNil extends Spine {
  import TermImpl.{mkSpineNil => nil}

  def normalize(termSubst: Subst, typeSubst: Subst) = nil

  // Handling def. expansion
  val δ_expandable = false
  def partial_δ_expand(rep: Int) = SNil
  val full_δ_expand = SNil

  // Queries
  val freeVars = Set[Term]()
  val boundVars = Set[Term]()
  val length = 0
  val asTerms = Seq()

  // Misc
  def merge(subst: (Subst, Subst), sp: Spine, spSubst: (Subst, Subst)) = SpineClos(sp, spSubst)
  def ++(sp: Spine) = sp

  // Pretty printing
  override val pretty = "⊥"
}

protected[terms] case class App(hd: Term, tail: Spine) extends Spine {
  import TermImpl.{mkSpineCons => cons}

  def normalize(termSubst: Subst, typeSubst: Subst) = cons(Left(hd.normalize((termSubst, termSubst),(typeSubst, typeSubst))), tail.normalize(termSubst, typeSubst))

  // Handling def. expansion
  lazy val δ_expandable = hd.δ_expandable || tail.δ_expandable
  def partial_δ_expand(rep: Int) = cons(Left(hd.partial_δ_expand(rep)), tail.partial_δ_expand(rep))
  lazy val full_δ_expand = cons(Left(hd.full_δ_expand), tail.full_δ_expand)


  // Queries
  val freeVars = hd.freeVars ++ tail.freeVars
  val boundVars = hd.boundVars ++ tail.boundVars
  val length = 1 + tail.length
  lazy val asTerms = Left(hd) +: tail.asTerms

  // Misc
  def merge(subst: (Subst, Subst), sp: Spine, spSubst: (Subst, Subst)) = App(TermClos(hd, subst), tail.merge(subst, sp, spSubst))
  def ++(sp: Spine) = cons(Left(hd), tail ++ sp)

  // Pretty printing
  override def pretty = s"${hd.pretty};${tail.pretty}"
}

protected[terms] case class TyApp(hd: Type, tail: Spine) extends Spine {
  import TermImpl.{mkSpineCons => cons}

  def normalize(termSubst: Subst, typeSubst: Subst) = cons(Right(hd), tail.normalize(termSubst, typeSubst))

  // Handling def. expansion
  val δ_expandable = tail.δ_expandable
  def partial_δ_expand(rep: Int) = cons(Right(hd), tail.partial_δ_expand(rep))
  lazy val full_δ_expand = cons(Right(hd), tail.full_δ_expand)

  // Queries
  val freeVars = tail.freeVars
  val boundVars = tail.boundVars
  val length = 1 + tail.length
  lazy val asTerms = Right(hd) +: tail.asTerms

  // Misc
  def merge(subst: (Subst, Subst), sp: Spine, spSubst: (Subst, Subst)) = TyApp(hd.closure(subst._2), tail.merge(subst, sp, spSubst))
  def ++(sp: Spine) = cons(Right(hd), tail ++ sp)

  // Pretty printing
  override def pretty = s"${hd.pretty};${tail.pretty}"
}


protected[spine] case class SpineClos(sp: Spine, s: (Subst, Subst)) extends Spine {
  import TermImpl.{mkSpineCons => cons}

  def normalize(termSubst: Subst, typeSubst: Subst) = sp.normalize(s._1 o termSubst, s._2 o typeSubst)

  // Handling def. expansion
  lazy val δ_expandable = ???
  def partial_δ_expand(rep: Int) = ???
  lazy val full_δ_expand = ???

  // Queries
  val freeVars = Set[Term]()
  val boundVars= Set[Term]() // TODO: implement
  lazy val length = sp.length
  lazy val asTerms = ???

  // Misc
  def merge(subst: (Subst, Subst), sp: Spine, spSubst: (Subst, Subst)) = sp.merge((s._1 o subst._1,s._2 o subst._2),sp, spSubst)
  def ++(sp: Spine) = ???

  // Pretty printing
  override def pretty = s"(${sp.pretty}[${s._1.pretty}/${s._2.pretty}])"
}


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
  protected[TermImpl] var termAbstractions: Map[Term, Map[Type, TermImpl]] = Map.empty
  protected[TermImpl] var typeAbstractions: Map[Term, TermImpl] = Map.empty
  protected[TermImpl] var roots: Map[Head, Map[Spine, TermImpl]] = Map.empty
  protected[TermImpl] var redexes: Map[Term, Map[Spine, TermImpl]] = Map.empty

  // Spines
  protected[TermImpl] var spines: Map[Either[Term, Type], Map[Spine, Spine]] = Map.empty

  /////////////////////////////////////////////
  // Implementation based constructor methods
  /////////////////////////////////////////////

  // primitive symbols (heads)
  protected[terms] def mkAtom0(id: Signature#Key): Head = symbolAtoms.get(id) match {
      case Some(hd) => hd
      case None     => val hd = Atom(id)
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
                          roots += ((hd,inner.+((args, root))))
                          root
    }
    case None        => val root = Root(hd, args)
                        roots += ((hd, Map((args, root))))
                        root
  }
  protected[terms] def mkRedex(left: Term, args: Spine): TermImpl = redexes.get(left) match {
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
  protected[terms] def mkTermAbstr(t: Type, body: Term): TermImpl = termAbstractions.get(body) match {
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
  protected[terms] def mkTypeAbstr(body: Term): TermImpl = typeAbstractions.get(body) match {
    case Some(abs) => abs
    case None      => val abs = TypeAbstr(body)
                      typeAbstractions += ((body, abs))
                      abs
  }

  // Spines
  protected[terms] def mkSpineNil: Spine = SNil
  protected[terms] def mkSpineCons(term: Either[Term, Type], tail: Spine): Spine = spines.get(term) match {
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

  private def mkSpine(args: Seq[Term]): Spine = args.foldRight[Spine](SNil)({case (t,sp) => mkSpineCons(Left(t),sp)})
  private def mkTySpine(args: Seq[Type]): Spine = args.foldRight[Spine](SNil)({case (t,sp) => mkSpineCons(Right(t),sp)})
  private def mkGenSpine(args: Seq[Either[Term,Type]]): Spine = args.foldRight[Spine](SNil)({case (t,sp) => mkSpineCons(t,sp)})

  /////////////////////////////////////////////
  // Public visible term constructors
  /////////////////////////////////////////////
  def mkAtom(id: Signature#Key): TermImpl = mkRoot(mkAtom0(id), SNil)

  def mkBound(typ: Type, scope: Int): TermImpl = mkRoot(mkBoundAtom(typ, scope), SNil)

  def mkTermApp(func: Term, arg: Term): TermImpl = mkTermApp(func, Seq(arg))
  def mkTermApp(func: Term, args: Seq[Term]): TermImpl = func match {
      case Root(h, SNil) => mkRoot(h, mkSpine(args))
      case Root(h,sp)  => mkRoot(h,sp ++ mkSpine(args))
      case Redex(r,sp) => mkRedex(r, sp ++ mkSpine(args))
      case other       => mkRedex(other, mkSpine(args))
    }
  def mkTermAbs(typ: Type, body: Term): TermImpl = mkTermAbstr(typ, body)

  def λ(hd: Type)(body: Term) = mkTermAbs(hd, body)
  def λ(hd: Type, hds: Type*)(body: Term): TermImpl = {
    λ(hd)(hds.foldRight(body)(λ(_)(_)))
  }

  def mkTypeApp(func: Term, arg: Type): TermImpl = mkTypeApp(func, Seq(arg))
  def mkTypeApp(func: Term, args: Seq[Type]): TermImpl  = func match {
      case Root(h, SNil) => mkRoot(h, mkTySpine(args))
      case Root(h,sp)  => mkRoot(h,sp ++ mkTySpine(args))
      case Redex(r,sp) => mkRedex(r, sp ++ mkTySpine(args))
      case other       => mkRedex(other, mkTySpine(args))
    }

  def mkTypeAbs(body: Term): TermImpl = mkTypeAbstr(body)
  def Λ(body: Term): TermImpl = mkTypeAbs(body)

  def mkApp(func: Term, args: Seq[Either[Term, Type]]): TermImpl  = func match {
      case Root(h, SNil) => mkRoot(h, mkGenSpine(args))
      case Root(h,sp)  => mkRoot(h,sp ++ mkGenSpine(args))
      case Redex(r,sp) => mkRedex(r, sp ++ mkGenSpine(args))
      case other       => mkRedex(other, mkGenSpine(args))
    }

  implicit def headToTerm(hd: Head): TermImpl = mkRoot(hd, mkSpineNil)


  def reset() = {
    boundAtoms = Map.empty
    symbolAtoms = Map.empty

    // composite terms
    termAbstractions = Map.empty
    typeAbstractions = Map.empty
    roots = Map.empty
    redexes = Map.empty

    // Spines
   spines = Map.empty
  }

}






