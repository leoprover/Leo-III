package datastructures.internal

import scala.language.implicitConversions
import datastructures.Pretty

/**
 * Abstract type for modeling types.
 * At the moment, types are constructed by:
 *
 * t1, t2 ::= a | t1 -> t2 | forall a. t1
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
 */
abstract class Type extends Pretty with SignatureTypes {
  // Predicates on types
  val isBaseType: Boolean
  val isFunType: Boolean
  val isPolyType: Boolean
  val isBoundTypeVar: Boolean
  def isApplicableWith(arg: Type): Boolean

  // Queries on types
  def typeVars: Set[Variable]

  def funDomainType: Option[Type]
  def _funDomainType: Type = funDomainType.get
  def funCodomainType: Option[Type]
  def _funCodomainType: Type = funCodomainType.get

  // Substitutions
  // ....

  // Syntactic nice constructors
  def ->:(hd: Type) = Type.mkFunType(hd, this)
}

/**
 * Constructor methods the `Type` class.
 */
object Type extends SignatureTypes {
  import Signature.{get => signature}
  /** The (fixed) type of individuals */
  lazy val i: Type = BaseType(signature.iKey)
  /** The (fixed) type of truth values. */
  lazy val o: Type = BaseType(signature.oKey)

  /** Create type with name `identifier`. */
  def mkType(identifier: Const): Type = BaseType(identifier)
  /** Build type `in -> out`. */
  def mkFunType(in: Type, out: Type): Type = FunType(in, out)
  /** Build type `in1 -> in2 -> in3 -> ... -> out`. */
  def mkFunType(in: List[Type], out: Type): Type = in match {
    case Nil => out
    case x::xs      => mkFunType(x, mkFunType(xs, out))
  }
  /** Build `forall $1. $2` (i.e. a universally quantified type) */
  def mkPolyType(typeVar: Variable, bodyType: Type): Type = {
    require(typeVar.isTypeVar, "Attempting to create forall-type with non-type variable.")
    ForallType(typeVar.asInstanceOf[TypeVar], bodyType)
  }

  /** The (bound) type a type variable represents. This should always be bound by a `mkPolyType`*/
  def mkVarType(typeVar: Variable): Type = {
    require(typeVar.isTypeVar, "Attempting to use term variable as type.")
    TypeVarType(typeVar.asInstanceOf[TypeVar])
  }

  /** Represents the kind `*` or `type` (i.e. the type of types). */
  def typeKind: Kind = TypeKind
  /** Represents the type of kinds. Only needed internally so that we can type kinds correctly */
  def superKind: Kind = SuperKind


  /** Build kind k1 -> k2
    * @deprecated Not needed yet, there are no terms that can produce this type
    */
  def mkFunKind(in: Kind, out: Kind): Kind = ???

  implicit def typeVarToType(typeVar: Variable): Type = mkVarType(typeVar)
}


abstract class Kind extends Pretty {
  val isTypeKind: Boolean
  val isSuperKind: Boolean
}

/*
  // Predicates on types
  val isBaseType: Boolean
  val isFunType: Boolean
  val isPolyType: Boolean
  val isBoundTypeVar: Boolean
  def isApplicableWith(arg: Type): Boolean

  // Queries on types
  def getTypeVars: Set[Variable]
  def getKind: Kind = TypeKind // since we dont have any (unsaturated) type constructors, the kind is always * for types

  // Substitutions
  // ....


  // Syntactic nice constructors
  def ->:(hd: Type) = Type.mkFunType(hd, this)
}

/**
* Type of a polymorphic function
* @param typeVar The type variable that is introduced to the type in `in`
* @param body The type that can now use the type variable `typeVar` as bound type variable
*/
protected[internal] case class ForallType(typeVar: TypeVar, body: Type) extends Type {
  def pretty = "forall " + typeVar.pretty + ". " + body.pretty

  val isBaseType     = false
  val isFunType      = false
  val isPolyType     = true
  val isBoundTypeVar = false
  def isApplicableWith(arg: Type): Boolean = (arg.getKind == typeVar.getType.get)

  def getTypeVars: Set[Variable] = Set(typeVar) ++ body.getTypeVars
}

/**Constructor for a function type `in -> out` */
protected[internal] case class FunType(in: Type, out: Type) extends Type {
  def pretty = in match {
    case funTy:FunType => "(" + funTy.pretty + ") -> " + out.pretty
    case otherTy:Type  => otherTy.pretty + " -> " + out.pretty
  }

  val isBaseType     = false
  val isFunType      = true
  val isPolyType     = false
  val isBoundTypeVar = false
  def isApplicableWith(arg: Type): Boolean = (arg == in)

  def getTypeVars: Set[Variable] = in.getTypeVars ++ out.getTypeVars
}

/** Type of a (bound) type variable when itself used as type in polymorphic function */
protected[internal] case class TypeVarType(typeVar: TypeVar) extends Type {
  def pretty = typeVar.name // name instead of pretty to avoid repitition of types

  val isBaseType     = false
  val isFunType      = false
  val isPolyType     = false
  val isBoundTypeVar = true
  def isApplicableWith(arg: Type): Boolean = false

  def getTypeVars: Set[Variable] = Set.empty
}

/** Literal type, i.e. `$o` */
protected[internal] case class BaseType(name: String) extends Type {// string???
  def pretty = name

  val isBaseType     = true
  val isFunType      = false
  val isPolyType     = false
  val isBoundTypeVar = false
  def isApplicableWith(arg: Type): Boolean = false

  def getTypeVars: Set[Variable] = Set.empty
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
  def isApplicableWith(arg: Type): Boolean = false
  def getTypeVars: Set[Variable] = Set.empty
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
  override def getKind = this
}

/** Artificial kind that models the type of `*` (represented by `BaseKind`) */
protected[internal] case object SuperKind extends Kind {
  def pretty = "**"

  val isTypeKind = false
  val isSuperKind = true
  val isFunKind = false
  override def getKind = this
}
*/