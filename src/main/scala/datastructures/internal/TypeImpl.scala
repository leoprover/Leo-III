package datastructures.internal


/** Literal type, i.e. `$o` */
protected[internal] case class BaseType(id: Signature#ConstKey) extends Type {
  // Pretty printing
  import Signature.{get => signature}
  def pretty = signature.getConstMeta(id).getName

  // Predicates on types
  val isBaseType     = true
  val isFunType      = false
  val isPolyType     = false
  val isBoundTypeVar = false
  def isApplicableWith(arg: Type): Boolean = false

  // Queries on types
  def typeVars: Set[Variable] = Set.empty

  def funDomainType: Option[Type] = None
  def funCodomainType: Option[Type] = None

  // Substitutions
  // ....
}


/** Function type `in -> out` */
protected[internal] case class FunType(in: Type, out: Type) extends Type {
  // Pretty printing
  def pretty = in match {
    case funTy:FunType => "(" + funTy.pretty + ") -> " + out.pretty
    case otherTy:Type  => otherTy.pretty + " -> " + out.pretty
  }

  // Predicates on types
  val isBaseType     = false
  val isFunType      = true
  val isPolyType     = false
  val isBoundTypeVar = false
  def isApplicableWith(arg: Type): Boolean = (arg == in)

  // Queries on types
  def typeVars: Set[Variable] = in.typeVars ++ out.typeVars

  def funDomainType: Option[Type] = Some(in)
  def funCodomainType: Option[Type] = Some(out)

  // Substitutions
  // ....
}


/**
 * Type of a polymorphic function
 * @param typeVar The type variable that is introduced to the type in `in`
 * @param body The type that can now use the type variable `typeVar` as bound type variable
 */
protected[internal] case class ForallType(typeVar: TypeVar, body: Type) extends Type {
  // Pretty printing
  def pretty = "forall " + typeVar.pretty + ". " + body.pretty

  // Predicates on types
  val isBaseType     = false
  val isFunType      = false
  val isPolyType     = true
  val isBoundTypeVar = false
  def isApplicableWith(arg: Type): Boolean = typeVar.getKind.get.isTypeKind

  // Queries on types
  def typeVars: Set[Variable] = Set(typeVar) ++ body.typeVars

  def funDomainType: Option[Type] = None
  def funCodomainType: Option[Type] = None

  // Substitutions
  // ....
}

/** Type of a (bound) type variable when itself used as type in polymorphic function */
protected[internal] case class TypeVarType(typeVar: TypeVar) extends Type {
  // Pretty printing
  import Signature.{get => signature}
  def pretty = signature.getVarMeta(typeVar.getName).getName // name instead of pretty to avoid repitition of types

  // Predicates on types
  val isBaseType     = false
  val isFunType      = false
  val isPolyType     = false
  val isBoundTypeVar = true
  def isApplicableWith(arg: Type): Boolean = false

  // Queries on types
  def typeVars: Set[Variable] = Set.empty

  def funDomainType: Option[Type] = None
  def funCodomainType: Option[Type] = None

  // Substitutions
  // ....
}


//////////////////////////////////
/// Kinds
//////////////////////////////////

/** Represents the kind `*` (i.e. the type of a type) */
protected[internal] case object TypeKind extends Kind {
  def pretty = "*"

  val isTypeKind = true
  val isSuperKind = false
}
/** Artificial kind that models the type of `*` (i.e. []) */
protected[internal] case object SuperKind extends Kind {
  def pretty = "#"

  val isTypeKind = false
  val isSuperKind = true
}