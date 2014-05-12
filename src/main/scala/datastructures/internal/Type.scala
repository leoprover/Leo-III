package datastructures.internal

import datastructures.Pretty
/**
  * Abstract type for modeling types
  *
  * @author Alexander Steen
  * @since 30.04.2014
 *  @note Updated 12.05.2014 (Re-organized structure of classes)
  */
sealed abstract class Type extends Pretty {
  def ->:(hd: Type) = Type.mkFunType(hd, this)

  val isBaseType: Boolean
  val isFunType: Boolean
  val isPolyType: Boolean
  val isBoundTypeVar: Boolean
}

/**
* Type of a polymorphic function
* @param typeVar The type variable that is introduced to the type in `in`
* @param body The type that can now use the type variable `typeVar` as bound type variable
*/
protected[internal] case class ForallType(typeVar: TypeVar, body: Type) extends Type {
  def pretty = "forall " + typeVar.pretty + ". " + body.pretty

  val isBaseType = false
  val isFunType = false
  val isPolyType = true
  val isBoundTypeVar = false
}

/**Constructor for a function type `in -> out` */
protected[internal] case class FunType(in: Type, out: Type) extends Type {
  def pretty = in match {
    case funTy:FunType => "(" + funTy.pretty + ") -> " + out.pretty
    case otherTy:Type  => otherTy.pretty + " -> " + out.pretty
  }

  val isBaseType = false
  val isFunType = true
  val isPolyType = false
  val isBoundTypeVar = false
}

/** Type of a (bound) type variable when itself used as type in polymorphic function */
protected[internal] case class TypeVarType(typeVar: TypeVar) extends Type {
  def pretty = typeVar.name // name instead of pretty to avoid repitition of types

  val isBaseType = false
  val isFunType = false
  val isPolyType = false
  val isBoundTypeVar = true
}

/** Literal type, i.e. `$o` */
protected[internal] case class BaseType(name: String) extends Type {// string???
  def pretty = name

  val isBaseType = true
  val isFunType = false
  val isPolyType = false
  val isBoundTypeVar = false
}

object Type extends HOLType {
  /** Build kind k1 -> k2
    * @deprecated Not needed yet, there are no terms that can produce this type
    */
  def mkFunKind(in: Kind, out: Kind): Kind = FunKind(in,out)

  /** Represents the type of kinds. Only needed internally so that we can type kinds correctly */
  def superKind = SuperKind

  /** Represents the kind `*` or `type` (i.e. the type of types). */
  def typeKind = TypeKind

  /** The (bound) type a type variable represents. This should always be bound by a `mkPolyType` */
  def mkVarType(typeVar: Variable): TypeVarType = {
    require(typeVar.isTypeVar, "Attempting to use non-type variable as type")
    TypeVarType(typeVar.asInstanceOf[TypeVar])
  }

  /** Build `forall $1. $2` (i.e. a universally quantified type) */
  def mkPolyType(typeVar: Variable, bodyType: Type): ForallType = {
    require(typeVar.isTypeVar, "Attempting to create forall-type with non-type variable.")
    ForallType(typeVar.asInstanceOf[TypeVar], bodyType)
  }

  /** Build type `in -> out`. */
  def mkFunType(in: Type, out: Type): FunType = FunType(in,out)

  /** Create type with name `identifier`. */
  def mkType(identifier: String): BaseType = BaseType(identifier)

  val o: BaseType = mkType("$o")
  val i: BaseType = mkType("$i")
}

/** Abstract type of kinds (i.e. types of types).
  * The `Kind` class extends the `Type` class to allow unified handling */
sealed abstract class Kind extends Type with Pretty {
  val isTypeKind: Boolean
  val isSuperKind: Boolean
  val isFunKind: Boolean
  val isBaseType = false
  val isFunType = false
  val isPolyType = false
  val isBoundTypeVar = false
}


/** Represents the kind `*` (i.e. the type of a type) */
protected[internal] case object TypeKind extends Kind {
  val name = "#*"
  def pretty = "*"

  val isTypeKind = true
  val isSuperKind = false
  val isFunKind = false
}
/** Constructs function kinds.
  * Invariant: `out` must not be `SuperKind`. */
protected[internal]case class FunKind(in: Kind, out: Kind) extends Kind {
  def pretty = in.pretty + " -> " + out.pretty

  val isTypeKind = false
  val isSuperKind = false
  val isFunKind = true
}

/** Artificial kind that models the type of `*` (represented by `BaseKind`) */
protected[internal] case object SuperKind extends Kind {
  def pretty = "**"

  val isTypeKind = false
  val isSuperKind = true
  val isFunKind = false
}