package leo.datastructures
package term.spine

import leo.datastructures.Position.{HeadPos}

import scala.language.implicitConversions
import scala.annotation.tailrec

import leo.datastructures.Type._
import leo.datastructures.impl.Signature
import leo.datastructures.term.Term


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
protected[term] sealed abstract class TermImpl(private var _locality: Locality) extends Term {

  // Predicates on terms
  def isLocal = _locality == LOCAL
  def locality = _locality

  lazy val betaNormalize: Term = {
    val erg = normalize(Subst.id, Subst.id)
    if (isGlobal)
     TermImpl.insert0(erg)
    else
     erg
  }
  lazy val topEtaContract: Term = this

  def closure(subst: Subst) = TermClos(this, (subst, Subst.id))
//    this.normalize(subst, Subst.id)

  // Substitutions
  def substitute(what: Term, by: Term): Term = ???
  protected[datastructures] def instantiate(scope: Int, by: Type): Term = ???

  // Other
  protected[datastructures] def inc(scopeIndex: Int): Term = ???
  def order = ???
}

/////////////////////////////////////////////////
// Implementation of specific term constructors
/////////////////////////////////////////////////

/** Representation of terms that are in (weak) head normal form. */
protected[term] case class Root(hd: Head, args: Spine) extends TermImpl(LOCAL) {
  import TermImpl.{headToTerm, mkRedex, mkRoot}

  // Predicates on terms
  val isAtom = args == SNil
  val isConstant = isAtom && hd.isConstant
  val isVariable = isAtom && hd.isBound
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
      case (t -> out) if t.isProdType => ty0(out, s.drop(t.numberOfComponents))
      case (_ -> out) => ty0(out, tail)
      case _ => throw new IllegalArgumentException(s"${this.pretty}: expression not well typed")// this should not happen if well-typed
    }
    case TyApp(s0,tail) => funty match {
      case tt@(∀(body)) => ty0(tt.instantiate(s0), tail)
      case _ => throw new IllegalArgumentException(s"${this.pretty}: expression not well typed")// this should not happen if well-typed
    }
    case _ => throw new IllegalArgumentException("closure occured in term")// other cases do not apply
  }
  lazy val freeVars = hd match {
    case BoundIndex(_,_) => args.freeVars
    case _             => args.freeVars + hd
  }
  lazy val symbols=  hd match {
    case BoundIndex(_,_) => args.symbols
    case Atom(key)             => args.symbols + key
  }
  lazy val boundVars = hd match {
    case BoundIndex(_,_) => args.freeVars + hd
    case _             => args.freeVars
  }
  lazy val looseBounds = hd match {
    case BoundIndex(_,i) => args.looseBounds + i
    case _ => args.looseBounds
  }
  lazy val headSymbol = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    Root(hd, SNil)
  }
  val headSymbolDepth = 0
  lazy val occurrences = if (args.length == 0)
                           Map(this.asInstanceOf[Term] -> Set(Position.root))
                         else
                           fuseMaps(Map(this.asInstanceOf[Term] -> Set(Position.root), headToTerm(hd) -> Set(Position.root.headPos)), args.occurrences)
  val scopeNumber = (Math.min(hd.scopeNumber._1, args.scopeNumber._1),Math.min(hd.scopeNumber._2, args.scopeNumber._2))
  lazy val size = 2 + args.size

  // Other operations
  lazy val typeCheck = typeCheck0(hd.ty, args)
  private def typeCheck0(t: Type, sp: Spine): Boolean = sp match {
    case SNil => true
    case App(head, rest) => t.isFunType && t._funDomainType == head.ty && typeCheck0(t._funCodomainType, rest) && head.typeCheck
    case TyApp(t2, rest) => t.isPolyType && typeCheck0(t.instantiate(t2), rest)
    case _ => true // other cases should not appear
  }

  def replace(what: Term, by: Term): Term = if (this == what)
                                              by
                                            else
                                              hd.replace(what, by) match {
                                                case Some(repl) => Redex(repl, args.replace(what, by))
                                                case None => Root(hd, args.replace(what, by))
                                              }
  def replaceAt(at: Position, by: Term): Term = if (at == Position.root)
                                                  by
                                                else
                                                  at match {
                                                    case HeadPos() => Redex(by, args)
                                                    case _ => Root(hd, args.replaceAt(at, by))
                                                  }

  def instantiateWith(subst: Subst) = ???


  def normalize(termSubst: Subst, typeSubst: Subst) = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    val termSubstNF = termSubst //.normalize
    val typeSubstNF = typeSubst //.normalize

    hd match {
      case h@Atom(id) => Root(h, normalizeSpine(args,termSubstNF, typeSubstNF))
      case b@BoundIndex(t, scope) => b.substitute(termSubstNF) match {
        case BoundFront(j) => Root(BoundIndex(t.substitute(typeSubstNF), j), normalizeSpine(args,termSubstNF, typeSubstNF))
        case TermFront(t) => Redex(t, args).normalize0(Subst.id,Subst.id, termSubstNF, typeSubstNF)
        case _ => throw new IllegalArgumentException("type front found where it was not expected")
      }
      case HeadClosure(h2, (termSubst2, typeSubst2)) => h2 match {
        case h@Atom(id) => Root(h, normalizeSpine(args,termSubst, typeSubst))
        case b@BoundIndex(t, scope) => b.substitute(termSubst2.comp(termSubst)) match {
          case BoundFront(j) => Root(BoundIndex(t.substitute(typeSubst2 o typeSubst), j), args.normalize(termSubst, typeSubst))
          case TermFront(t) => Redex(t, args).normalize0(Subst.id, Subst.id, termSubst, typeSubst)
          case _ => throw new IllegalArgumentException("type front found where it was not expected")
        }
        case HeadClosure(h3, (termSubst3, typeSubst3)) => Root(HeadClosure(h3, (termSubst3 o termSubst2, typeSubst3 o typeSubst2)), args).normalize(termSubst, typeSubst)
      }
    }
  }

//  private def normalizeSpine(sp: Spine, termSubst: Subst, typeSubst: Subst): Spine = _indexing match {
//    case PLAIN => sp.normalize(termSubst, typeSubst)
//    case INDEXED => sp.scopeNumber match {
//      case (a,b) if a >= 0 && b >= 0 => sp
//      case _ => sp.normalize(termSubst, typeSubst)
//    }
//  }

  private def normalizeSpine(sp: Spine, termSubst: Subst, typeSubst: Subst): Spine = sp.normalize(termSubst, typeSubst)

  /** Pretty */
  lazy val pretty = s"${hd.pretty} ⋅ (${args.pretty})"
}


// For all terms that have not been normalized, assume they are a redex, represented
// by this term instance
protected[term] case class Redex(body: Term, args: Spine) extends TermImpl(LOCAL) {
  import TermImpl.{mkRedex, mkRoot}

  // Predicates on terms
  val isAtom = false
  val isConstant = false
  val isVariable = false
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
      case (_ -> out) => ty0(out, tail)
      case _ => throw new IllegalArgumentException(s"${this.pretty}: expression not well typed")// this should not happen if well-typed
    }
    case TyApp(s0,tail) => funty match {
      case tt@(∀(_)) => ty0(tt.instantiate(s0), tail)
      case _ => throw new IllegalArgumentException(s"${this.pretty}: expression not well typed")// this should not happen if well-typed
    }
    case _ => throw new IllegalArgumentException("closure occured in term")// other cases do not apply
  }
  lazy val freeVars = body.freeVars ++ args.freeVars
  lazy val boundVars = body.boundVars ++ args.boundVars
  lazy val looseBounds = body.looseBounds ++ args.looseBounds
  lazy val symbols = body.symbols ++ args.symbols
  lazy val headSymbol = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    body.headSymbol
  }
  lazy val headSymbolDepth = 1 + body.headSymbolDepth
  val scopeNumber = (Math.min(body.scopeNumber._1, args.scopeNumber._1),Math.min(body.scopeNumber._2, args.scopeNumber._2))
  lazy val size = 1 + body.size + args.size
  lazy val occurrences = fuseMaps(fuseMaps(Map(this.asInstanceOf[Term] -> Set(Position.root)), body.occurrences.mapValues(_.map(_.prependHeadPos))), args.occurrences)

  // Other operations
  lazy val typeCheck = typeCheck0(body.ty, args)
  private def typeCheck0(t: Type, sp: Spine): Boolean = sp match {
    case SNil => true
    case App(head, rest) => t.isFunType && t._funDomainType == head.ty && typeCheck0(t._funCodomainType, rest) && head.typeCheck
    case TyApp(t2, rest) => t.isPolyType && typeCheck0(t.instantiate(t2), rest)
    case SpineClos(s, (sub1, sub2)) => typeCheck0(t, s.normalize(sub1,sub2))
  }

  def replace(what: Term, by: Term): Term = if (this == what)
                                              by
                                            else
                                              Redex(body.replace(what, by), args.replace(what, by))
  def replaceAt(at: Position, by: Term): Term = if (at == Position.root)
                                                  by
                                                else
                                                  at match {
                                                    case HeadPos() => Redex(by, args)
                                                    case _ => Redex(body, args.replaceAt(at, by))
                                                  }

  def normalize(termSubst: Subst, typeSubst: Subst) = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    normalize0(termSubst, typeSubst, termSubst, typeSubst)
  }

  @tailrec
  protected[spine] final def normalize0(headTermSubst: Subst, headTypeSubst: Subst, spineTermSubst: Subst, spineTypeSubst: Subst): Term = args match {
    case SNil => body.normalize(headTermSubst, headTypeSubst)
    case SpineClos(sp2, (spTermSubst, spTypeSubst)) => Redex(body, sp2).normalize0(headTermSubst, headTypeSubst, spTermSubst o spineTermSubst, spTypeSubst o spineTypeSubst)
    case other => body match {
      case TermAbstr(t,b) => other match {
        case App(s0, tail) => Redex(b, tail).normalize0((TermFront(TermClos(s0, (spineTermSubst, spineTypeSubst))) +: headTermSubst), headTypeSubst, spineTermSubst, spineTypeSubst)
        case _ => throw new IllegalArgumentException("malformed expression")
      }
      case TypeAbstr(b)   => other match {
        case TyApp(t, tail) => Redex(b, tail).normalize0(headTermSubst, (TypeFront(t.substitute(spineTypeSubst)) +: headTypeSubst), spineTermSubst, spineTypeSubst)
        case _ => throw new IllegalArgumentException("malformed expression")
      }
      case Root(h,s) => Root(HeadClosure(h, (headTermSubst, headTypeSubst)), s.merge((headTermSubst, headTypeSubst),args,(spineTermSubst, spineTypeSubst))).normalize(Subst.id, Subst.id)
      case Redex(b,args2) => Redex(b, args2.merge((headTermSubst, headTypeSubst),args,(spineTermSubst, spineTypeSubst))).normalize0(headTermSubst, headTypeSubst, Subst.id, Subst.id)
      case TermClos(t, (termSubst2, typeSubst2)) => Redex(t, args).normalize0(termSubst2 o headTermSubst, typeSubst2 o headTypeSubst, spineTermSubst, spineTypeSubst)
    }
  }


  /** Pretty */
  lazy val pretty = s"[${body.pretty}] ⋅ (${args.pretty})"
}



protected[term] case class TermAbstr(typ: Type, body: Term) extends TermImpl(LOCAL) {
  import TermImpl.mkTermAbstr

  // Predicates on terms
  val isAtom = false
  val isConstant = false
  val isVariable = false
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
  lazy val looseBounds = body.looseBounds.map(_ - 1).filter(_ > 0)
  lazy val symbols = body.symbols
  lazy val headSymbol = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    body.headSymbol
  }
  lazy val headSymbolDepth = 1 + body.headSymbolDepth
  val scopeNumber = (body.scopeNumber._1 + 1,Math.min(typ.scopeNumber,body.scopeNumber._2))
  lazy val size = 1 + body.size
  lazy val occurrences = body.occurrences.mapValues(_.map(_.prependAbstrPos))

  // Other operations
  lazy val typeCheck = body.typeCheck

  def replace(what: Term, by: Term): Term = if (this == what)
                                              by
                                            else
                                              TermAbstr(typ, body.replace(what, by))
  def replaceAt(at: Position, by: Term): Term = if (at == Position.root)
                                                  by
                                                else
                                                  TermAbstr(typ, body.replaceAt(at.tail, by))

  def normalize(termSubst: Subst, typeSubst: Subst) = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
//    if (isIndexed) { // TODO: maybe optimize re-normalization if term is indexed?
//      ???
//    } else {
      TermAbstr(typ.substitute(typeSubst), body.normalize((termSubst.sink), typeSubst))
//    }

  }
  override lazy val topEtaContract: Term = {
    body match {
      case Root(h,sp) if sp != SNil => sp.last match {
        case Left(t) if t.isVariable => t match {
          case Root(BoundIndex(_,sc),SNil) if sc == 1 && !Root(h,sp.init).looseBounds.contains(1) => Root(h,sp.init)
          case _ => this
        }
        case _ => this
      }
      case Redex(rx,sp) if sp != SNil => sp.last match {
        case Left(t) if t.isVariable => t match {
          case Root(BoundIndex(_,sc),SNil) if sc == 1 && !Redex(rx,sp.init).looseBounds.contains(1) => Redex(rx,sp.init)
          case _ => this
        }
        case _ => this
      }
      case _ => this
    }
  }


  /** Pretty */
  lazy val pretty = s"λ. (${body.pretty})"
}


protected[term] case class TypeAbstr(body: Term) extends TermImpl(LOCAL) {
  import Type.∀
  import TermImpl.mkTypeAbstr

  // Predicates on terms
  val isAtom = false
  val isConstant = false
  val isVariable = false
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
  lazy val looseBounds = body.looseBounds
  lazy val symbols = body.symbols
  lazy val headSymbol = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    body.headSymbol
  }
  lazy val headSymbolDepth = 1 + body.headSymbolDepth

  val scopeNumber = (body.scopeNumber._1, body.scopeNumber._2+1)
  lazy val size = 1 + body.size
  lazy val occurrences = body.occurrences.mapValues(_.map(_.prependAbstrPos))

  // Other operations
  lazy val typeCheck = body.typeCheck

  def replace(what: Term, by: Term): Term = if (this == what)
                                              by
                                            else
                                              TypeAbstr(body.replace(what, by))
  def replaceAt(at: Position, by: Term): Term = if (at == Position.root)
                                                  by
                                                else
                                                  TypeAbstr(body.replaceAt(at.tail, by))

  def normalize(termSubst: Subst, typeSubst: Subst) = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
//    if (isIndexed) { // TODO: maybe optimize re-normalization if term is indexed?
//      ???
//    } else {
      TypeAbstr(body.normalize(termSubst, typeSubst.sink))
//    }
  }

  /** Pretty */
  lazy val pretty = s"Λ. (${body.pretty})"
}


protected[term] case class TermClos(term: Term, σ: (Subst, Subst)) extends TermImpl(LOCAL) {
  // Closure should never be handed to the outside

  // Predicates on terms
  val isAtom = false
  val isConstant = false
  val isVariable = false
  val isTermAbs = false
  val isTypeAbs = false
  val isApp = false

  // Handling def. expansion
  lazy val δ_expandable = false // TODO
  def partial_δ_expand(rep: Int) = ???
  def full_δ_expand = ???

  lazy val head_δ_expandable = ???
  def head_δ_expand = ???

  // Queries on terms
  lazy val ty = term.ty
  lazy val freeVars = Set[Term]()
  lazy val boundVars = Set[Term]()
  lazy val looseBounds = Set.empty[Int]
  lazy val symbols = Set[Signature#Key]()
  lazy val headSymbol = ???
  lazy val headSymbolDepth = 1 + term.headSymbolDepth
  val scopeNumber = term.scopeNumber
  val occurrences = Map[Term, Set[Position]]()
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
  lazy val size = term.size // this might not be senseful, but will never occur when used properly

  // Other operations
  lazy val typeCheck = ???

  def replace(what: Term, by: Term): Term = ???
  def replaceAt(at: Position, by: Term): Term = ???

//  def preNormalize(s: Subst) = term.preNormalize(σ o s)

  def normalize(termSubst: Subst, typeSubst: Subst) = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    term.normalize(σ._1 o termSubst, σ._2 o typeSubst)
  }

  /** Pretty */
  def pretty = s"${term.pretty}[${σ._1.pretty}/${σ._2.pretty}]"
}


/////////////////////////////////////////////////
// Implementation of head symbols
/////////////////////////////////////////////////

protected[term] sealed abstract class Head extends Pretty {
  // Handling def. expansion
  val δ_expandable: Boolean
  def partial_δ_expand(rep: Int): Term
  def full_δ_expand: Term

  def isBound: Boolean
  def isConstant: Boolean

  // Queries
  def ty: Type
  def scopeNumber: (Int, Int)

  def replace(what: Term, by: Term): Option[Term] = if (TermImpl.headToTerm(this) == what)
                                              Some(by)
                                            else
                                              None
}


protected[term] case class BoundIndex(typ: Type, scope: Int) extends Head {
  val isBound = true
  val isConstant = false

  // Handling def. expansion
  val δ_expandable = false
  def partial_δ_expand(rep: Int) = full_δ_expand
  def full_δ_expand = TermImpl.headToTerm(this)

  // Queries
  val ty = typ
  val scopeNumber = (-scope, typ.scopeNumber)

  def substitute(s: Subst) = s.substBndIdx(scope)

  // Pretty printing
  override val pretty = s"$scope"
}

protected[term] case class Atom(id: Signature#Key) extends Head {
  private lazy val meta = Signature.get(id)

  val isBound = false
  val isConstant = true

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
  val scopeNumber = (0,0)

  // Pretty printing
  override lazy val pretty = s"${meta.name}"
}


protected[term] case class HeadClosure(hd: Head, subst: (Subst, Subst)) extends Head {
  val isBound = false
  val isConstant = false

  // Handling def. expansion
  lazy val δ_expandable = ???
  def partial_δ_expand(rep: Int) = ???
  def full_δ_expand = ???

  // Queries
  lazy val ty = ???
  lazy val scopeNumber = hd.scopeNumber

  // Pretty printing
  override def pretty = s"${hd.pretty}[${subst._1.pretty}/${subst._2.pretty}}]"
}


/////////////////////////////////////////////////
// Implementation of spines
/////////////////////////////////////////////////

/**
 * // TODO Documentation
 */
protected[spine] sealed abstract class Spine extends Pretty {
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
  def looseBounds: Set[Int]
  def symbols: Set[Signature#Key]
  def asTerms: Seq[Either[Term, Type]]
  def scopeNumber: (Int, Int)
  def size: Int
  lazy val occurrences: Map[Term, Set[Position]] = occurrences0(1)
  def occurrences0(pos: Int): Map[Term, Set[Position]]

  // Misc
  def merge(subst: (Subst, Subst), sp: Spine, spSubst: (Subst, Subst)): Spine

  def ++(sp: Spine): Spine
  def +(t: TermImpl): Spine = ++(cons(Left(t),SNil))

  /** Drop n arguments from spine, fails with IllegalArgumentException if n > length */
  def drop(n: Int): Spine

  def last: Either[Term, Type]
  def init: Spine

  def replace(what: Term, by: Term): Spine
  def replaceAt(at: Position, by: Term): Spine = replaceAt0(at.posHead, at.tail, by)

  protected[spine] def replaceAt0(pos: Int, tail: Position, by: Term): Spine
}

protected[spine] case object SNil extends Spine {
  import TermImpl.{mkSpineNil => nil}

  def normalize(termSubst: Subst, typeSubst: Subst) = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    nil
  }

  // Handling def. expansion
  val δ_expandable = false
  def partial_δ_expand(rep: Int) = SNil
  val full_δ_expand = SNil

  // Queries
  val freeVars = Set[Term]()
  val boundVars = Set[Term]()
  val looseBounds = Set[Int]()
  val symbols = Set[Signature#Key]()
  val length = 0
  val asTerms = Seq()
  val scopeNumber = (0, 0)
  val size = 1
  def occurrences0(pos: Int) = Map()

  // Misc
  def merge(subst: (Subst, Subst), sp: Spine, spSubst: (Subst, Subst)) = SpineClos(sp, spSubst)
  def ++(sp: Spine) = sp

  def drop(n: Int) = n match {
    case 0 => SNil
    case _ => throw new IllegalArgumentException("Trying to drop elements from nil spine.")
  }

  def last = throw new IllegalArgumentException("Trying to access last element of Nil")
  def init = throw new IllegalArgumentException("Trying to access init of Nil")
  def replace(what: Term, by: Term): Spine = SNil
  def replaceAt0(pos: Int, tail: Position, by: Term): Spine = SNil

  // Pretty printing
  override val pretty = "⊥"
}

protected[spine] case class App(hd: Term, tail: Spine) extends Spine {
  import TermImpl.{mkSpineCons => cons}

  def normalize(termSubst: Subst, typeSubst: Subst) = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
//    if (isIndexed) { // TODO: maybe optimize re-normalization if term is indexed?
//      ???
//    } else {
    cons(Left(hd.normalize((termSubst), (typeSubst))), tail.normalize(termSubst, typeSubst))
//    }
  }

  // Handling def. expansion
  lazy val δ_expandable = hd.δ_expandable || tail.δ_expandable
  def partial_δ_expand(rep: Int) = cons(Left(hd.partial_δ_expand(rep)), tail.partial_δ_expand(rep))
  lazy val full_δ_expand = cons(Left(hd.full_δ_expand), tail.full_δ_expand)


  // Queries
  val freeVars = hd.freeVars ++ tail.freeVars
  val boundVars = hd.boundVars ++ tail.boundVars
  lazy val looseBounds = hd.looseBounds ++ tail.looseBounds
  val symbols = hd.symbols ++ tail.symbols
  val length = 1 + tail.length
  lazy val asTerms = Left(hd) +: tail.asTerms
  lazy val scopeNumber = (Math.min(hd.scopeNumber._1, tail.scopeNumber._1),Math.min(hd.scopeNumber._2, tail.scopeNumber._2))
  lazy val size = 1+ hd.size + tail.size
  def occurrences0(pos: Int) = fuseMaps(hd.occurrences.mapValues(_.map(_.preprendArgPos(pos))), tail.occurrences0(pos+1))


  // Misc
  def merge(subst: (Subst, Subst), sp: Spine, spSubst: (Subst, Subst)) = App(TermClos(hd, subst), tail.merge(subst, sp, spSubst))
  def ++(sp: Spine) = cons(Left(hd), tail ++ sp)

  def drop(n: Int) = n match {
    case 0 => this
    case _ => tail.drop(n-1)
  }
  lazy val last = tail match {
    case SNil => Left(hd)
    case _ => tail.last
  }
  lazy val init = tail match {
    case App(_,SNil) => App(hd, SNil)
    case TyApp(_,SNil) => App(hd, SNil)
    case _ => App(hd, tail.init)
  }

  def replace(what: Term, by: Term): Spine = if (hd == what)
                                              App(by, tail.replace(what,by))
                                             else
                                              App(hd.replace(what,by), tail.replace(what,by))

  def replaceAt0(pos: Int, posTail: Position, by: Term): Spine = pos match {
    case 1 if posTail == Position.root => App(by, tail)
    case 1 => App(hd.replaceAt(posTail, by), tail)
    case _ => App(hd, tail.replaceAt0(pos-1, posTail, by))
  }



  // Pretty printing
  override lazy val pretty = s"${hd.pretty};${tail.pretty}"
}

protected[spine] case class TyApp(hd: Type, tail: Spine) extends Spine {
  import TermImpl.{mkSpineCons => cons}

  def normalize(termSubst: Subst, typeSubst: Subst) = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    cons(Right(hd), tail.normalize(termSubst, typeSubst))
  }

  // Handling def. expansion
  val δ_expandable = tail.δ_expandable
  def partial_δ_expand(rep: Int) = cons(Right(hd), tail.partial_δ_expand(rep))
  lazy val full_δ_expand = cons(Right(hd), tail.full_δ_expand)

  // Queries
  val freeVars = tail.freeVars
  val boundVars = tail.boundVars
  lazy val looseBounds = tail.looseBounds
  val symbols = tail.symbols
  val length = 1 + tail.length
  lazy val asTerms = Right(hd) +: tail.asTerms
  lazy val scopeNumber = (tail.scopeNumber._1,Math.min(hd.scopeNumber, tail.scopeNumber._2))
  lazy val size = 1 + tail.size
  def occurrences0(pos: Int) = tail.occurrences0(pos+1)

  // Misc
  def merge(subst: (Subst, Subst), sp: Spine, spSubst: (Subst, Subst)) = TyApp(hd.substitute(subst._2), tail.merge(subst, sp, spSubst))
  def ++(sp: Spine) = cons(Right(hd), tail ++ sp)

  def drop(n: Int) = n match {
    case 0 => this
    case _ => tail.drop(n-1)
  }
  lazy val last = tail match {
    case SNil => Right(hd)
    case _ => tail.last
  }

  lazy val init = tail match {
    case App(_,SNil) => TyApp(hd, SNil)
    case TyApp(_,SNil) => TyApp(hd, SNil)
    case _ => TyApp(hd, tail.init)
  }
  def replace(what: Term, by: Term): Spine = TyApp(hd, tail.replace(what,by))
  def replaceAt0(pos: Int, posTail: Position, by: Term): Spine = pos match {
    case 1 => throw new IllegalArgumentException("Trying to replace term inside of type.")
    case _ => TyApp(hd, tail.replaceAt0(pos-1, posTail, by))
  }

  // Pretty printing
  override lazy val pretty = s"${hd.pretty};${tail.pretty}"
}


protected[spine] case class SpineClos(sp: Spine, s: (Subst, Subst)) extends Spine {
  import TermImpl.{mkSpineCons => cons}

  def normalize(termSubst: Subst, typeSubst: Subst) = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    sp.normalize(s._1 o termSubst, s._2 o typeSubst)
  }

  // Handling def. expansion
  lazy val δ_expandable = false // TODO
  def partial_δ_expand(rep: Int) = ???
  lazy val full_δ_expand = ???

  // Queries
  val freeVars = Set[Term]()
  val symbols = Set[Signature#Key]()
  lazy val looseBounds = ???
  val boundVars= Set[Term]() // TODO: implement
  lazy val length = sp.length
  lazy val asTerms = ???
  lazy val scopeNumber = sp.scopeNumber
  lazy val size = sp.size // todo: properly implement
  def occurrences0(pos: Int) = Map.empty

  // Misc
  def merge(subst: (Subst, Subst), sp: Spine, spSubst: (Subst, Subst)) = sp.merge((s._1 o subst._1,s._2 o subst._2),sp, spSubst)
  def ++(sp: Spine) = ???

  def drop(n: Int) = ???
  def last = ???
  def init = ???

  def replace(what: Term, by: Term): Spine = ???
  def replaceAt0(pos: Int, posTail: Position, by: Term): Spine = ???


  // Pretty printing
  override def pretty = s"(${sp.pretty}[${s._1.pretty}/${s._2.pretty}])"
}

object Spine {
  val nil: Spine = SNil
}


/**
 * //TODO Documentation
 */
object TermImpl extends TermBank {

  /////////////////////////////////////////////
  // Hash tables for DAG representation of perfectly
  // shared terms
  /////////////////////////////////////////////
  protected[TermImpl] var terms: Set[Term] = Set.empty

  // primitive symbols (heads)
  protected[TermImpl] var boundAtoms: Map[Type, Map[Int, Head]] = Map.empty
  protected[TermImpl] var symbolAtoms: Map[Signature#Key, Head] = Map.empty

  // composite terms
  protected[TermImpl] var termAbstractions: Map[Term, Map[Type, TermImpl]] = Map.empty
  protected[TermImpl] var typeAbstractions: Map[Term, TermImpl] = Map.empty
  protected[TermImpl] var roots: Map[Head, Map[Spine, TermImpl]] = Map.empty
  protected[TermImpl] var redexes: Map[Term, Map[Spine, Redex]] = Map.empty

  // Spines
  protected[TermImpl] var spines: Map[Either[Term, Type], Map[Spine, Spine]] = Map.empty

  /////////////////////////////////////////////
  // Implementation based constructor methods
  /////////////////////////////////////////////

  // primitive symbols (heads)
  protected[term] def mkAtom0(id: Signature#Key): Head = // Atom(id)
    symbolAtoms.get(id) match {
      case Some(hd) => hd
      case None     => val hd = Atom(id)
                       symbolAtoms += ((id, hd))
                       hd
  }

  protected[term] def mkBoundAtom(t: Type, scope: Int): Head = //BoundIndex(t,scope)
    boundAtoms.get(t) match {
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
  protected[term] def mkRoot(hd: Head, args: Spine): TermImpl = //Root(hd, args)
    global(roots.get(hd) match {
    case Some(inner) => inner.get(args) match {
      case Some(root)  => root
      case None        => val root = Root(hd, args)
                          roots += ((hd,inner.+((args, root))))
                          root
    }
    case None        => val root = Root(hd, args)
                        roots += ((hd, Map((args, root))))
                        root
  })
  protected[term] def mkRedex(left: Term, args: Spine): Redex = //Redex(left, args)
    global(redexes.get(left) match {
    case Some(inner) => inner.get(args) match {
      case Some(redex) => redex
      case None        => val redex = Redex(left, args)
                          redexes += ((left,inner.+((args, redex))))
                          redex
    }
    case None        => val redex = Redex(left, args)
                        redexes += ((left, Map((args, redex))))
                        redex
  })
  protected[term] def mkTermAbstr(t: Type, body: Term): TermImpl = //TermAbstr(t, body)
    global(termAbstractions.get(body) match {
    case Some(inner) => inner.get(t) match {
      case Some(abs)   => abs
      case None        => val abs = TermAbstr(t, body)
                          termAbstractions += ((body,inner.+((t, abs))))
                          abs
    }
    case None        => val abs = TermAbstr(t, body)
                        termAbstractions += ((body, Map((t, abs))))
                        abs
  })
  protected[term] def mkTypeAbstr(body: Term): TermImpl = //TypeAbstr(body)
    global(typeAbstractions.get(body) match {
    case Some(abs) => abs
    case None      => val abs = TypeAbstr(body)
                      typeAbstractions += ((body, abs))
                      abs
  })

  // Spines
  protected[term] def mkSpineNil: Spine = SNil
  protected[term] def mkSpineCons(term: Either[Term, Type], tail: Spine): Spine = //term.fold(App(_, tail),TyApp(_, tail))
    spines.get(term) match {
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

  private def global[A <: TermImpl](t: A): A = {
    t._locality = GLOBAL
    t
  }

  private def mkSpine(args: Seq[Term]): Spine = args.foldRight[Spine](SNil)({case (t,sp) => mkSpineCons(Left(t),sp)})
  private def mkTySpine(args: Seq[Type]): Spine = args.foldRight[Spine](SNil)({case (t,sp) => mkSpineCons(Right(t),sp)})
  private def mkGenSpine(args: Seq[Either[Term,Type]]): Spine = args.foldRight[Spine](SNil)({case (t,sp) => mkSpineCons(t,sp)})

  /////////////////////////////////////////////
  // Public visible term constructors
  /////////////////////////////////////////////
  val local = new TermFactory {
    def mkApp(func: Term, args: Seq[Either[Term, Type]]): Term = Redex(func, args.foldRight(mkSpineNil)((termOrTy,sp) => termOrTy.fold(App(_,sp),TyApp(_,sp))))
    def mkBound(t: Type, scope: Int): Term = Root(BoundIndex(t, scope), SNil)
    def mkAtom(id: Signature#Key): Term = Root(Atom(id), SNil)
    def mkTypeApp(func: Term, args: Seq[Type]): Term = Redex(func, args.foldRight(mkSpineNil)((ty,sp) => TyApp(ty,sp)))
    def mkTypeApp(func: Term, arg: Type): Term =  Redex(func, TyApp(arg, SNil))
    def mkTypeAbs(body: Term): Term = TypeAbstr(body)
    def mkTermAbs(t: Type, body: Term): Term = TermAbstr(t, body)
    def mkTermApp(func: Term, args: Seq[Term]): Term = Redex(func, args.foldRight(mkSpineNil)((t,sp) => App(t,sp)))
    def mkTermApp(func: Term, arg: Term): Term = Redex(func, App(arg, SNil))
  }


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

  def mkTypeApp(func: Term, arg: Type): TermImpl = mkTypeApp(func, Seq(arg))
  def mkTypeApp(func: Term, args: Seq[Type]): TermImpl  = func match {
      case Root(h, SNil) => mkRoot(h, mkTySpine(args))
      case Root(h,sp)  => mkRoot(h,sp ++ mkTySpine(args))
      case Redex(r,sp) => mkRedex(r, sp ++ mkTySpine(args))
      case other       => mkRedex(other, mkTySpine(args))
    }

  def mkTypeAbs(body: Term): TermImpl = mkTypeAbstr(body)

  def mkApp(func: Term, args: Seq[Either[Term, Type]]): TermImpl  = func match {
      case Root(h, SNil) => mkRoot(h, mkGenSpine(args))
      case Root(h,sp)  => mkRoot(h,sp ++ mkGenSpine(args))
      case Redex(r,sp) => mkRedex(r, sp ++ mkGenSpine(args))
      case other       => mkRedex(other, mkGenSpine(args))
    }

  def contains(term: Term): Boolean = terms.contains(term)

  def insert(term: Term): Term = {
    val t = if (term.isLocal)
      insert0(term)
    else
      term

    terms = terms + t
    t
  }

  protected[TermImpl] def insert0(localTerm: Term): TermImpl = {
    localTerm match {
      case Root(h, sp) => val sp2 = insertSpine0(sp)
        val h2 = h match {
          case BoundIndex(ty, scope) => mkBoundAtom(ty, scope)
          case Atom(id) => mkAtom0(id)
          case hc@HeadClosure(chd, s) => hc // TODO: do we need closures in bank?
        }
        global(mkRoot(h2, sp2))

      case Redex(rx, sp) => val sp2 = insertSpine0(sp)
        val rx2 = insert0(rx)
        global(mkRedex(rx2, sp2))

      case TermAbstr(ty, body) => val body2 = insert0(body)
        global(mkTermAbstr(ty, body2))

      case TypeAbstr(body) => val body2 = insert0(body)
        global(mkTypeAbstr(body2))
      case tc@TermClos(ct, s) => global(tc)
      case _ => throw new IllegalArgumentException("trying to insert a non-spine term to spine termbank")
    }
  }

  protected def insertSpine0(sp: Spine): Spine = {
    sp match {
      case SNil => mkSpineNil
      case App(hd, tail) => val hd2 = insert0(hd)
                            val tail2 = insertSpine0(tail)
                            mkSpineCons(Left(hd2), tail2)
      case TyApp(ty, tail) => val tail2 = insertSpine0(tail)
                              mkSpineCons(Right(ty), tail2)
      case sc@SpineClos(csp, s) => sc
    }
  }

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

  ///////////////
  // Utility  ///
  ///////////////
  implicit def headToTerm(hd: Head): TermImpl = mkRoot(hd, mkSpineNil)

  /**
   * Statistics type. Components and meanings:
   * comp 1: number of terms, Int
   * comp 2: avg. size of terms, Int
   * comp 3: min. size of terms, Int
   * comp 4: max. size of terms, Int
   * comp 5: number of nodes, Int
   * comp 6: number of edges, Int
   * comp 6: # of parents to count map, Map[Int, Int]
   */
  type TermBankStatistics = (Int, Int, Int, Int, Int, Int, Map[Int, Int])

  def statistics: TermBankStatistics = {
    val numberOfTerms = terms.size +1

    val parentNodeCountMap: Map[Int, Int] = Map.empty

    // Term sizes
    // start element (min, max, X)
    val intermediate = terms.foldRight((-1,-1,-1))((t,acc) => {val s = t.size
                                            val min = Math.min(acc._1, s)
                                            val max = Math.max(acc._2, s)
                                            (min, max, acc._3  + s)
                                           })
    val minSizeOfTerms = intermediate._1
    val maxSizeOfTerms = intermediate._2
    val avgSizeOfTerms = intermediate._3 / numberOfTerms

    var termAbstractionsSize = 0
    termAbstractions.foreach({ case (term, map) =>
      termAbstractionsSize += map.size
    })
    var rootsSize = 0
    roots.foreach({ case (term, map) =>
      rootsSize += map.size
    })
    var redexesSize = 0
    redexes.foreach({ case (term, map) =>
      redexesSize += map.size
    })
    var spinesSize = 0
    spines.foreach({ case (_, map) =>
      spinesSize += map.size
    })

    val numberOfNodes = { boundAtoms.size + symbolAtoms.size
                        + termAbstractionsSize + typeAbstractions.size
                        + rootsSize + redexesSize
                        + spinesSize }

    // edges:
    // for each Root(h,sp) we have 2 edges
    // for each Redex(rx, sp) we have 2 edges
    // for each TermAbstr(ty, body) we have one edge (types are not counted)
    // for each TypeAbstr(body) we have one edge
    // for each TermCons(t, tail) we have two edges
    // for each TypeCons(ty,tail) we have two edges
    val numberOfEdges = {
      termAbstractionsSize
      +typeAbstractions.size
      +2 * (
        rootsSize
          + redexesSize
          + spinesSize
        )
    }

    (numberOfTerms,avgSizeOfTerms,minSizeOfTerms,maxSizeOfTerms,numberOfNodes,numberOfEdges,parentNodeCountMap)
  }

}






