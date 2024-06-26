package leo.datastructures

import scala.annotation.tailrec
import scala.language.implicitConversions

/**
 * Abstract type for modeling types.
 * At the moment, types are constructed by:
 *
 * t1, t2, ... ::= s (ti) | t1 -> t2 | ty1 x ty2 x ... x tyN | forall a. t1
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
 * @note Updated October 2021. Removed union types (will never be supported), simplified product types.
 */
trait Type extends Pretty with Prettier {

  // Predicates on types
  def isBaseType: Boolean
  def isComposedType: Boolean
  def isFunType: Boolean
  def isProdType: Boolean
  def isPolyType: Boolean
  def isBoundTypeVar: Boolean
  def isApplicableWith(arg: Type): Boolean

  // Queries on types
  def typeVars: Set[Type]
  def symbols: Set[Signature.Key]

  def funDomainType: Option[Type]
  def _funDomainType: Type = funDomainType.get
  def codomainType: Type
  def arity: Int
  def funParamTypesWithResultType: Seq[Type]
  def funParamTypes: Seq[Type] = funParamTypesWithResultType.init
  def splitFunParamTypes: (Seq[Type], Type) = {
    val tys = funParamTypesWithResultType
    (tys.init, tys.last)
  }
  def splitFunParamTypesAt(n: Int): (Seq[Type], Type)

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
  def ->:(hd: Type): Type = Type.mkFunType(hd, this)

  /** Create type application: If `this` is a sort symbol t = `s` of non-zero arity (or not fully applied type t = `s a1 a2 ...`)
    * the, it creates the type application t @ ty. Otherwise, it fails. */
  def app(ty: Type): Type

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
  import leo.datastructures.impl.TypeImpl

  /** Create type `h arg1 arg2 ... argn` with head symbol `head` and type arguments `argi`. */
  final def mkType(identifier: Signature.Key, args: Seq[Type]): Type = TypeImpl.mkType(identifier, args)
  final def mkType(identifier: Signature.Key, arg: Type): Type = TypeImpl.mkType(identifier, Seq(arg))
  /** Create type with name `identifier`. */
  final def mkType(identifier: Signature.Key): Type = mkType(identifier, Seq.empty)

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

  /** Create product type `t1 x t2 x ... x tn` (type of an n-ary tuple with respective element types). */
  final def mkProdType(tys: Seq[Type]): Type = TypeImpl.mkProdType(tys)

  final def clear(): Unit = TypeImpl.clear()

  @inline final def ground(ty: Type): Boolean = ty.typeVars.isEmpty

  /** Build `forall. ty` (i.e. a universally quantified type) */
  final def mkPolyType(bodyType: Type): Type = TypeImpl.mkPolyType(bodyType)
  /** Build `forall. ty` (i.e. a universally quantified type). Pretty variant of `mkPolytype` */
  final def ∀(bodyType: Type): Type = mkPolyType(bodyType)

  @tailrec
  final def mkNAryPolyType(n: Int, bodyType: Type): Type = n match {
    case n if n <= 0 => bodyType
    case _ => mkNAryPolyType(n-1, mkPolyType(bodyType))
  }

  /** The (bound) type a type variable represents. This should always be bound by a `mkPolyType`*/
  final def mkVarType(scope: Int): Type = TypeImpl.mkVarType(scope)

  /** Represents the kind `*` or `type` (i.e. the type of types). */
  final val typeKind: Kind = Kind.*

  implicit def typeVarToType(typeVar: Int): Type = mkVarType(typeVar)

  ///////////////////////////////
  // Pattern matchers for types
  ///////////////////////////////
  import leo.datastructures.impl.{GroundTypeNode, BoundTypeNode, ProductTypeNode,
  AbstractionTypeNode, ForallTypeNode}

  object BaseType {
    def unapply(ty: Type): Option[Signature.Key] = ty match {
      case GroundTypeNode(id, args) if args.isEmpty => Some(id)
      case _ => None
    }
  }

  object ComposedType {
    def unapply(ty: Type): Option[(Signature.Key, Seq[Type])] = ty match {
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

  object ProductType {
    def unapply(ty: Type): Option[Seq[Type]] = ty match {
      case ProductTypeNode(tys) => Some(tys)
      case _ => None
    }
  }

  object ∀ {
    def unapply(ty: Type): Option[Type] = ty match {
      case ForallTypeNode(t) => Some(t)
      case _ => None
    }
  }
}


/**
  * Kinds are "types of type constructors", i.e. domain restrictions for type constructors.
  * Type constructors with arity zero are called types and have kind `*`.
  * Kinds (k) are created by
  *
  *   k := * | k -> k | #
  *
  * where * is the kind of types, # the kind of kinds (for internal use only), and
  * -> the syntactical constructor for function kinds (left-associative).
  * Although this allows higher-order kinds such as (* -> *) -> *, we
  * first restrict ourselves to first-order kinds, i.e. kinds of the form
  * *^k^ -> * (i.e. * -> * -> ... -> *).
  */
abstract class Kind extends Pretty {
  /** Returns `true` if `this == *`, `false` otherwise. */
  def isTypeKind: Boolean
  /** Returns `true` if `this == k1 -> k2` for some `k1, k2`, `false` otherwise. */
  def isFunKind: Boolean
  /** Returns `true` if `this == #`, `false` otherwise. */
  def isSuperKind: Boolean

  /** Returns the arity of the kind. Arity `arity(k)` of a kind `k` is defined by:
    * {{{
    * arity(*) = 0
    * arity(k1 -> k2) = 1 + arity(k2)}}} */
  def arity: Int

  /** Given two kinds `k1` and `k2`, `k1 ->: k2` creates the function kind from `k1` two `k2`, i.e.
    * the kind `k1 -> k2`. */
  @inline final def ->:(hd: Kind): Kind = Kind.mkFunKind(hd, this)
}

object Kind {
  import leo.datastructures.impl.{TypeKind, FunKind, SuperKind}
  final val * : Kind = TypeKind
  final val superKind : Kind = SuperKind

  /** Build kind k1 -> k2  */
  final def mkFunKind(k1: Kind, k2: Kind): Kind = FunKind(k1, k2)
  /** Build kind `in1 -> in2 -> in3 -> ... -> out`. */
  final def mkFunKind(in: Seq[Kind], out: Kind): Kind = in match {
    case Seq()           => out
    case Seq(x, xs @ _*) => mkFunKind(x, mkFunKind(xs, out))
  }
  final def mkFunKind(in: Seq[Kind]): Kind = in match {
    case Seq(ty)            => ty
    case Seq(ty, tys @ _*)  => mkFunKind(ty, mkFunKind(tys))
  }

  object -> {
    final def unapply(k: Kind): Option[(Kind, Kind)] = k match {
      case FunKind(l,r) => Some((l,r))
      case _ => None
    }
  }
}
