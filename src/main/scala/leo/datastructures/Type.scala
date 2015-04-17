package leo.datastructures

import leo.datastructures.impl._

import scala.language.implicitConversions

/**
 * Abstract type for modeling types.
 * At the moment, types are constructed by:
 *
 * t1, t2 ::= a | t1 -> t2 | t1 * t2 | t1 + t2 | forall a. t1
 * with a = some set of base type symbols containing i (type of individuals)
 * and o (type of truth values).
 *
 * Kinds are created by
 * k := * | #
 * where * is the kind of types and # the kind of kinds (for internal use only).
 *
 * It is planned to further enhance the type system, e.g. with
 * 1. type classes
 * 2. type constructors
 * 3. subtyping
 *
 * @author Alexander Steen
 * @since 30.04.2014
 * @note Updated 15.05.2014 (Re-Re-organized structure of classes for types: abstract type with
 *       companion object here [without explicit interfaces], implementation in extra file.
 *       Type language is now fixed: System F (omega).
 * @note Updated 30.06.2014 Inserted type constructors for product types (*) and union types (+). These
 *       will be removed from the type language as soon as it is expressive enough for general type constructors.
 */
abstract class Type extends Pretty {

  // Predicates on types
  val isBaseType: Boolean = false
  val isFunType: Boolean = false
  val isProdType: Boolean = false
  val isUnionType: Boolean = false
  val isPolyType: Boolean = false
  val isBoundTypeVar: Boolean = false
  def isApplicableWith(arg: Type): Boolean

  // Queries on types
  def typeVars: Set[Type]

  def funDomainType: Option[Type]
  def _funDomainType: Type = funDomainType.get
  def funCodomainType: Option[Type]
  def _funCodomainType: Type = funCodomainType.get
  def funArity: Int
  def funParamTypesWithResultType: Seq[Type]
  def funParamTypes: Seq[Type] = funParamTypesWithResultType.init

  def scopeNumber: Int

  /** Returns true iff `ty` appears somewhere as subtype (e.g. as part of an abstraction type). */
  def occurs(ty: Type): Boolean

  // Substitutions
  /**
   * Substitute (free) occurences of `what` by `by`, e.g.
   * {{{
   *   (Type.o ->: Type.i).substitute(Type.o, Type.i ->: Type.i)
   * }}}
   * yields {{{(Type.i ->: Type.i) ->: Type.i}}}
   */
  def substitute(what: Type, by: Type): Type
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

  val numberOfComponents: Int = 1


  protected[datastructures] def closure(subst: Subst): Type
}

/**
 * Constructor methods the `Type` class.
 */
object Type {
  type Impl = Type // fix by introducing super-type on types TODO

  /** Create type with name `identifier`. */
  def mkType(identifier: Signature#Key): Type = BaseTypeNode(identifier)
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


  /** Build kind k1 -> k2
    * @deprecated Not needed yet, there are no terms that can produce this type
    */
  def mkFunKind(in: Kind, out: Kind): Kind = ???

  implicit def typeVarToType(typeVar: Int): Type = mkVarType(typeVar)


  ///////////////////////////////
  // Pattern matchers for types
  ///////////////////////////////

  object BaseType {
    def unapply(ty: Type): Option[Signature#Key] = ty match {
      case BaseTypeNode(id) => Some(id)
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
}
