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
abstract class Type extends Pretty {


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
object Type {
  import Signature.{get => signature}
  /** The (fixed) type of individuals */
  lazy val i: Type = BaseType(signature.iKey)
  /** The (fixed) type of truth values. */
  lazy val o: Type = BaseType(signature.oKey)

  /** Create type with name `identifier`. */
  def mkType(identifier: Signature#Key): Type = BaseType(identifier)
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

