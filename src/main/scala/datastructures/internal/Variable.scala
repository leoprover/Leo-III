package datastructures.internal

import datastructures.Pretty

/**
 *
 *
 * @author Alexander Steen
 * @since 30.04.2014
 */
object Variable {
  def mkTypeVar(name: String = {Variable.lastUsedIndex += 1; Variable.typeVarName + Variable.lastUsedIndex.toString}, varType: Kind = TypeKind) = TypeVar(name, varType)
  def mkVar     = TermVar(_,_)
  def newTyVar = mkTypeVar()

  val typeVarName: String = "TV"
  var lastUsedIndex = -1
}

abstract sealed class Variable extends Pretty {
  def isTypeVar: Boolean
  def isTermVar: Boolean

  def hasType: Boolean

  //vielleicht local/global scope speichern? (Alex)
}

protected[internal] case class TypeVar(name: String, varType: Kind) extends Variable {
  def isTypeVar = true
  def isTermVar = false

  def hasType = true

  def #!(body: Type): ForallType = {
    ForallType(this, body)
  }

  def pretty = name + ":" + varType.pretty
}
protected[internal] case class TermVar(name: String, varType: Option[Type]) extends Variable {
  def isTypeVar = false
  def isTermVar = true

  def hasType = varType.isDefined

  def pretty = varType match {
    case None      => name
    case Some(typ) => name + ":" + typ.pretty
  }
}
