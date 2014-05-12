package datastructures.internal

import scala.language.implicitConversions

/**
 * Abstract type interface for internal HOL formula types.
 * At the moment, types are by:
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
 * @since 12.05.2014
 */
trait HOLType {
  /** The (fixed) type of individuals */
  val i: Type
  /** The (fixed) type of truth values. */
  val o: Type

  /** Create type with name `identifier`. */
  def mkType(identifier: String): Type
  /** Build type `in -> out`. */
  def mkFunType(in: Type, out: Type): Type
  /** Build type `in1 -> in2 -> in3 -> ... -> out`. */
  def mkFunType(in: List[Type], out: Type): Type = in match {
    case Nil => out
    case x::xs      => mkFunType(x, mkFunType(xs, out))
  }
  /** Build `forall $1. $2` (i.e. a universally quantified type) */
  def mkPolyType(typeVar: Variable, bodyType: Type): Type

  /** The (bound) type a type variable represents. This should always be bound by a `mkPolyType`*/
  def mkVarType(typeVar: Variable): Type

  /** Represents the kind `*` or `type` (i.e. the type of types). */
  def typeKind: Kind
  /** Represents the type of kinds. Only needed internally so that we can type kinds correctly */
  def superKind: Kind
  /** Build kind k1 -> k2
    * @deprecated Not needed yet, there are no terms that can produce this type
    */
  def mkFunKind(in: Kind, out: Kind): Kind


  implicit def strToType(in: String): Type = {
    in.head.isUpper match {
      case true => mkVarType(Variable.mkTypeVar(in))
      case false => mkType(in) // Do types names have special restrictions?
    }
  }

  implicit def typeVarToType(typeVar: Variable): Type = mkVarType(typeVar)
}