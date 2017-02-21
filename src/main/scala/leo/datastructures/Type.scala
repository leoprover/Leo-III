package leo.datastructures

import leo.datastructures.impl._

import scala.language.implicitConversions

/**
 * Abstract type for modeling types.
 * At the moment, types are constructed by:
 *
 * t1, t2 ::= s (ti) | t1 -> t2 | t1 * t2 | t1 + t2 | forall a. t1
 * with s is a sort from a set S of sort symbols. If s is of kind * -> ... -> * (n times)
 * then the ti is a sequence of (n-1) type arguments. If s is of kind *, then s is
 * called base type.
 *
 * Kinds are created by
 * k := * | k -> k | #
 * where * is the kind of types and # the kind of kinds (for internal use only).
 *
 *
 * @author Alexander Steen
 * @since 30.04.2014
 * @note Updated 15.05.2014 (Re-Re-organized structure of classes for types: abstract type with
 *       companion object here [without explicit interfaces], implementation in extra file.
 * @note Updated 30.06.2014 Inserted type constructors for product types (*) and union types (+). These
 *       will be removed from the type language as soon as it is expressive enough for general type constructors. Probably. Or not. We'll see.
 * @note Updated 14.06.2016 Introduced sort symbols to support TH1
 */
abstract class Type extends Pretty with Prettier {

  // Predicates on types
  val isBaseType: Boolean = false
  val isComposedType: Boolean = false
  val isFunType: Boolean = false
  val isProdType: Boolean = false
  val isUnionType: Boolean = false
  val isPolyType: Boolean = false
  val isBoundTypeVar: Boolean = false
  def isApplicableWith(arg: Type): Boolean

  // Queries on types
  def typeVars: Set[Type]
  def symbols: Set[Signature#Key]

  def funDomainType: Option[Type]
  def _funDomainType: Type = funDomainType.get
  def codomainType: Type
  def arity: Int
  def funParamTypesWithResultType: Seq[Type]
  def funParamTypes: Seq[Type] = funParamTypesWithResultType.init
  def splitFunParamTypesAt(n: Int): (Seq[Type], Type)

  def scopeNumber: Int

  /** Returns true iff `ty` appears somewhere as subtype (e.g. as part of an abstraction type). */
  def occurs(ty: Type): Boolean

  // Substitutions
  /**
   * syntactical replacement of  `what` by `by`, e.g.
   * {{{
   *   (Type.o ->: Type.i).substitute(Type.o, Type.i ->: Type.i)
   * }}}
   * yields {{{(Type.i ->: Type.i) ->: Type.i}}}
   */
  def replace(what: Type, by: Type): Type
  def substitute(subst: Subst): Type

  /** if `this` is a polymorphic type (i.e. a forall type), the method returns the abstracted type where all type parameters bound
    * by the head quantifier are replaced by `by`. In any other case, it does nothing */
  def instantiate(by: Type): Type
  /** if `this` is a n-fold polymorphic type (i.e. nested forall type), the method returns the abstracted type where all type parameters i bound
    * by (at most) `by.size` prefix quantifiers are replaced by `by(i)`. If `by.size` is smaller than the prefix of
    * type abstraction, only `by.size` many are instantiated. If `by.size` is larger than the `n` prefix
    * of type abstraction, only the first `n` types are instantiated, the rest is discarded. */
  def instantiate(by: Seq[Type]): Type

  // Syntactic nice constructors
  /** Create abstraction type from `hd` to `this` */
  def ->:(hd: Type) = Type.mkFunType(hd, this)

  /** Create product type `this * ty` */
  def *(ty: Type) = Type.mkProdType(this, ty)
  /** Create union type `this + ty`*/
  def +(ty: Type) = Type.mkUnionType(this, ty)
  /** Create type application: If `this` is a sort symbol t = `s` of non-zero arity (or not fully applied type t = `s a1 a2 ...`)
    * the, it creates the type application t @ ty. Otherwise, it fails. */
  def app(ty: Type): Type

  val numberOfComponents: Int = 1
  def order: Int
  /**
   * The number of "prefix" type abstractions, i.e. the length
   * of the longest prefix of this type only containing
   * type abstractions (corresponds to the number
   * of type variables if the type is rank-1).
   * @return
   */
  def polyPrefixArgsCount: Int
  /* Return the body of the type without any prefix-type abstractions, e.g.
  * for /\./\. c 1 2 return (c 1 2). */
  def monomorphicBody: Type

  protected[datastructures] def closure(subst: Subst): Type
}

/**
 * Constructor methods the `Type` class.
 */
object Type {
  /** Create type `h arg1 arg2 ... argn` with head symbol `head` and type arguments `argi`. */
  final def mkType(identifier: Signature#Key, args: Seq[Type]): Type = TypeImpl.mkType(identifier, args)
  final def mkType(identifier: Signature#Key, arg: Type): Type = TypeImpl.mkType(identifier, Seq(arg))
  /** Create type with name `identifier`. */
  final def mkType(identifier: Signature#Key): Type = mkType(identifier, Seq.empty)

  /** Build type `in -> out`. */
  final def mkFunType(in: Type, out: Type): Type = TypeImpl.mkFunType(in, out)
  /** Build type `in1 -> in2 -> in3 -> ... -> out`. */
  final def mkFunType(in: Seq[Type], out: Type): Type = in match {
    case Seq()           => out
    case Seq(x, xs @ _*) => mkFunType(x, mkFunType(xs, out))
  }
  /** Build type `in(0) -> in(1) -> ... -> in(n-1)` where `n = in.size`. */
  final def mkFunType(in: Seq[Type]): Type = in match {
    case Seq(ty)            => ty
    case Seq(ty, tys @ _*)  => mkFunType(ty, mkFunType(tys))
  }

  /** Create product type (t1,t2). */
  final def mkProdType(t1: Type, t2: Type): Type = TypeImpl.mkProdType(t1,t2)
  /** Creates a product type ((...((t1 * t2) * t3)....)*tn) */
  final def mkProdType(t1: Type, t2: Type, ti: Seq[Type]): Type = {
    ti.foldLeft(mkProdType(t1, t2))((arg,f) => mkProdType(arg,f))
  }
  /** Creates a product type ((...((t1 * t2) * t3)....)*tn) */
  final def mkProdType(ti: Seq[Type]): Type = ti match {
    case Seq(ty)        => ty
    case Seq(ty1, ty2, tys @ _*) => mkProdType(ty1, ty2, tys)
  }

  /** Create union type (t1+t2). */
  final def mkUnionType(t1: Type, t2: Type): Type = TypeImpl.mkUnionType(t1,t2)
  /** Creates a union type ((...((t1 + t2) + t3)....)+tn) */
  final def mkUnionType(t1: Type, t2: Type, ti: Seq[Type]): Type = {
    ti.foldLeft(mkUnionType(t1, t2))((arg,f) => mkUnionType(arg,f))
  }
  /** Creates a union type ((...((t1 + t2) + t3)....)+tn) */
  final def mkUnionType(ti: Seq[Type]): Type = ti match {
    case Seq(ty)        => ty
    case Seq(ty1, ty2, tys @ _*) => mkUnionType(ty1, ty2, tys)
  }

  @inline final def ground(ty: Type): Boolean = ty.typeVars.isEmpty

  /** Build `forall. ty` (i.e. a universally quantified type) */
  final def mkPolyType(bodyType: Type): Type = TypeImpl.mkPolyType(bodyType)
  /** Build `forall. ty` (i.e. a universally quantified type). Pretty variant of `mkPolytype` */
  final def ∀(bodyType: Type): Type = mkPolyType(bodyType)

  /** The (bound) type a type variable represents. This should always be bound by a `mkPolyType`*/
  final def mkVarType(scope: Int): Type = TypeImpl.mkVarType(scope)

  /** Represents the kind `*` or `type` (i.e. the type of types). */
  final val typeKind: Kind = TypeKind
  /** Represents the type of kinds. Only needed internally so that we can type kinds correctly */
  final val superKind: Kind = SuperKind

  implicit def typeVarToType(typeVar: Int): Type = mkVarType(typeVar)

  ///////////////////////////////
  // Pattern matchers for types
  ///////////////////////////////

  object BaseType {
    def unapply(ty: Type): Option[Signature#Key] = ty match {
      case GroundTypeNode(id, args) if args.isEmpty => Some(id)
      case _ => None
    }
  }

  object ComposedType {
    def unapply(ty: Type): Option[(Signature#Key, Seq[Type])] = ty match {
      case GroundTypeNode(id, args) if args.nonEmpty => Some((id, args))
      case _ => None
    }
  }

  object BoundType {
    def unapply(ty: Type): Option[Int] = ty match {
      case BoundTypeNode(scope) => Some(scope)
      case _ => None
    }
  }

  object -> {
    def unapply(ty: Type): Option[(Type, Type)] = ty match {
      case AbstractionTypeNode(l, r) => Some((l,r))
      case _ => None
    }
  }

  object * {
    def unapply(ty: Type): Option[(Type, Type)] = ty match {
      case ProductTypeNode(l, r) => Some((l,r))
      case _ => None
    }
  }

  object + {
    def unapply(ty: Type): Option[(Type, Type)] = ty match {
      case UnionTypeNode(l, r) => Some((l,r))
      case _ => None
    }
  }

  object ∀ {
    def unapply(ty: Type): Option[Type] = ty match {
      case ForallTypeNode(t) => Some(t)
      case _ => None
    }
  }

  /** A lexicographical ordering of types. Its definition is arbitrary, but should form
   * a total order on types.
   * */
  object LexicographicalOrdering extends Ordering[Type] {
    private def compareSeq(a : Seq[Type], b: Seq[Type]) : Int = (a,b) match {
      case (h1::t1, h2::t2) =>
        val c = this.compare(h1,h2)
        if(c!=0) c else compareSeq(t1, t2)
      case (h::t, Nil) => 1
      case (Nil, h::t) => -1
      case (Nil, Nil) => 0
    }

    private def compareTwo(x1: Type, y1: Type, x2: Type, y2: Type) : Int = {
      val c = this.compare(x1, x2)
      if(c != 0) c else this.compare(y1, y2)}

    def compare(a : Type, b:Type) : Int = (a ,b) match {
      case (BaseType(x), BaseType(y)) => x compare y
      case (ComposedType(k1, t1), ComposedType(k2, t2)) =>
        val c = k1 compare k2
        if(c != 0) c else compareSeq(t1, t2)
      case (BoundType(t1), BoundType(t2)) => t1 compare t2
      case (->(h1,t1), ->(h2,t2)) => compareTwo(h1,t1, h2, t2)
      case (*(h1,t1), *(h2,t2)) => compareTwo(h1,t1, h2, t2)
      case (+(h1,t1), +(h2,t2)) => compareTwo(h1,t1, h2, t2)
      case (∀(x), ∀(y)) => this.compare(x, y)
      case (BaseType(x), _) => 1
      case (_, BaseType(x)) => -1
      case (ComposedType(k,t), _) => 1
      case (_, ComposedType(k,t)) => -1
      case (BoundType(x), _) => 1
      case (_, BoundType(x)) => -1
      case (->(k,t), _) => 1
      case (_, ->(k,t)) => -1
      case (*(k,t), _) => 1
      case (_, *(k,t)) => -1
      case (+(k,t), _) => 1
      case (_, +(k,t)) => -1
      case (∀(x), _) => 1
      case (_, ∀(x)) => -1
    }
  }

}


abstract class Kind extends Pretty {
  val isTypeKind: Boolean
  val isFunKind: Boolean
  val isSuperKind: Boolean

  def arity: Int

  def ->:(hd: Kind): Kind = Kind.mkFunKind(hd, this)
}

object Kind {

  val typeKind: Kind = TypeKind
  val * = TypeKind.asInstanceOf[Kind]

  /** Build kind k1 -> k2  */
  def mkFunKind(k1: Kind, k2: Kind): Kind = FunKind(k1, k2)
  /** Build kind `in1 -> in2 -> in3 -> ... -> out`. */
  def mkFunKind(in: Seq[Kind], out: Kind): Kind = in match {
    case Seq()           => out
    case Seq(x, xs @ _*) => mkFunKind(x, mkFunKind(xs, out))
  }
  def mkFunKind(in: Seq[Kind]): Kind = in match {
    case Seq(ty)            => ty
    case Seq(ty, tys @ _*)  => mkFunKind(ty, mkFunKind(tys))
  }
  object -> {
    def unapply(k: Kind): Option[(Kind, Kind)] = k match {
      case FunKind(l,r) => Some((l,r))
      case _ => None
    }
  }
}
