package datastructures.internal

import Variable.{mkTypeVar}
import datastructures.Pretty

object Type {
  val o = mkBaseType("$o")
  val i = mkBaseType("$i")

  def mkBaseType = BaseType(_)
  def mkFunType = FunType(_,_)
  def mkFunType(in: List[Type], out: Type): Type = in match {
    case Nil => out
    case x::xs      => FunType(x, mkFunType(xs, out))
  }
  def mkTypeVarType = TypeVarType(_)
  def mkTypeVarType(name: String): Type = mkTypeVarType(mkTypeVar(name))
  def mkForAllType = ForallType(_,_)

  def mkConstructorType = ConstructorType(_)
  def mkConstructorType(name: String): Type = mkConstructorType(TypeConstructor.mkConstructor(name))
  def getBaseKind = BaseKind
  def getSuperKind = SuperKind
  def mkFunKind = FunKind(_,_)

  implicit def strToType(in: String): Type = {
    in.head.isUpper match {
      case true => TypeVarType(Variable.mkTypeVar(in))
      case false => BaseType(in)
    }
  }
}


/**
 * Abstract type for modeling types
 *
 * @author Alexander Steen
 * @since 30.04.2014
 */
sealed abstract class Type extends Pretty {
  def ->:(hd: Type) = FunType(hd, this)
}

/**
 * Type of a polymorphic function
 * @param typeVar The type variable that is introduced to the type in `in`
 * @param body The type that can now use the type variable `typeVar` as bound type variable
 */
case class ForallType(typeVar: TypeVar, body: Type) extends Type {
  def pretty = "forall " + typeVar.pretty + ". " + body.pretty
}

/**Constructor for a function type `in -> out` */
case class FunType(in: Type, out: Type) extends Type {
  def pretty = in match {
    case funTy:FunType => "(" + funTy.pretty + ") -> " + out.pretty
    case otherTy:Type  => otherTy.pretty + " -> " + out.pretty
  }
}

/** Application of type arguments to a constructor, yielding a Type
 * @deprecated Not yet implemented, but planned for future use */
case class ContructorAppType(typeCon: TypeConstructor, args: List[Type]) extends Type {
  def pretty = typeCon.pretty + " " + args.map(x => x.pretty).mkString(" ")
}

case class ConstructorType(typeCon: TypeConstructor) extends Type {
  def pretty = typeCon.pretty
}

/** Type of a (bound) type variable when itself used as type in polymorphic function */
case class TypeVarType(typeVar: TypeVar) extends Type {
  def pretty = typeVar.name // name instead of pretty to avoid repitition of types
}

/** Literal type, i.e. `$o` */
case class BaseType(name: String) extends Type {// string???
  def pretty = name
}
/** Abstract type of kinds (i.e. types of types).
  * The `Kind` class extends the `Type` class to allow unified handling */
sealed abstract class Kind extends Type {
  val isBaseKind: Boolean
  val isFunKind: Boolean
}
/** Represents the kind `*` (i.e. the type of a type) */
case object BaseKind extends Kind {
  val name = "#*"
  def pretty = "*"
  val isBaseKind = true
  val isFunKind = false
}
/** Constructs function kinds.
  * Invariant: `out` must not be `SuperKind`. */
case class FunKind(in: Type, body: Kind) extends Kind {
  def pretty = in.pretty + " -> " + body.pretty
  val isBaseKind = false
  val isFunKind = true
}

/** Artificial kind that models the type of `*` (represented by `BaseKind`) */
case object SuperKind extends Kind {
  def pretty = "**"

  val isBaseKind = false
  val isFunKind = false
}


object TypeConstructor {
  def mkConstructor = TypeCon(_)
}

/** For future use: Type constructor representation */
sealed abstract class TypeConstructor extends Pretty
case class TypeCon(name: String) extends TypeConstructor {
  def pretty = name
}
