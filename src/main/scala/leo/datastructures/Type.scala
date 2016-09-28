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
abstract class Type extends Pretty {

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

  // Other operation
  /** Right folding on types. This may change if the type system is changed. */
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A): A

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
  type Impl = Type // fix by introducing super-type on types TODO

  /** Create type with name `identifier`. */
  def mkType(identifier: Signature#Key): Type = GroundTypeNode(identifier, Seq())
  /** Create type `h arg1 arg2 ... argn` with head symbol `head` and type arguments `argi`. */
  def mkType(identifier: Signature#Key, args: Seq[Type]): Type = GroundTypeNode(identifier, args)
  /** Build type `in -> out`. */
  def mkFunType(in: Type, out: Type): Type = AbstractionTypeNode(in, out)
  /** Build type `in1 -> in2 -> in3 -> ... -> out`. */
  def mkFunType(in: Seq[Type], out: Type): Type = in match {
    case Seq()           => out
    case Seq(x, xs @ _*) => mkFunType(x, mkFunType(xs, out))
  }
  def mkFunType(in: Seq[Type]): Type = in match {
    case Seq(ty)            => ty
    case Seq(ty, tys @ _*)  => mkFunType(ty, mkFunType(tys))
  }

  def mkProdType(t1: Type, t2: Type): Type = ProductTypeNode(t1,t2)

  /** Creates a product type ((...((t1 * t2) * t3)....)*tn) */
  def mkProdType(t1: Type, t2: Type, ti: Seq[Type]): Type = {
    ti.foldLeft(ProductTypeNode(t1, t2))((arg,f) => ProductTypeNode(arg,f))
  }

  def mkProdType(ti: Seq[Type]): Type = ti match {
    case Seq(ty)        => ty
    case Seq(ty1, ty2, tys @ _*) => mkProdType(ty1, ty2, tys)
  }

  def mkUnionType(t1: Type, t2: Type): Type = UnionTypeNode(t1,t2)

  /** Creates a union type ((...((t1 + t2) + t3)....)+tn) */
  def mkUnionType(t1: Type, t2: Type, ti: Seq[Type]): Type = {
    ti.foldLeft(UnionTypeNode(t1, t2))((arg,f) => UnionTypeNode(arg,f))
  }

  def mkUnionType(ti: Seq[Type]): Type = ti match {
    case Seq(ty)        => ty
    case Seq(ty1, ty2, tys @ _*) => mkUnionType(ty1, ty2, tys)
  }

  /** Build `forall. ty` (i.e. a universally quantified type) */
  def mkPolyType(bodyType: Type): Type = ForallTypeNode(bodyType)
  /** Build `forall. ty` (i.e. a universally quantified type). Pretty variant of `mkPolytype` */
  def ∀(bodyType: Type): Type = ForallTypeNode(bodyType)

  /** The (bound) type a type variable represents. This should always be bound by a `mkPolyType`*/
  def mkVarType(scope: Int): Type = BoundTypeNode(scope)

  /** Represents the kind `*` or `type` (i.e. the type of types). */
  val typeKind: Kind = TypeKind
  /** Represents the type of kinds. Only needed internally so that we can type kinds correctly */
  val superKind: Kind = SuperKind

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

}


abstract class Kind extends Pretty {
  val isTypeKind: Boolean
  val isFunKind: Boolean
  val isSuperKind: Boolean

  def arity: Int
}

object Kind {

  val typeKind: Kind = TypeKind

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
