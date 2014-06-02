package datastructures.internal

import datastructures.Pretty

/**
 *
 *
 * @author Alexander Steen
 * @since 30.04.2014
 */
object Variable {
  def mkTypeVar(name: String, varKind: Kind = TypeKind) = TypeVar(name, varKind)
  def mkVar     = TermVar(_,_)
}

abstract sealed class Variable extends Pretty {
  def isTypeVar: Boolean
  def isTermVar: Boolean

  def hasType: Boolean
  def hasKind: Boolean
  def getName: String

  def getType: Option[Type]
  def _getType: Type = getType.get

  def getKind: Option[Kind]
  def _getKind: Kind = getKind.get

  def #!(body: Type): Type = {
    Type.mkPolyType(this, body)
  }

}

protected[internal] case class TypeVar(name: String, varKind: Kind) extends Variable {
  def isTypeVar = true
  def isTermVar = false

  def hasType = false
  def hasKind = true

  def getName = name
  def getType = None
  def getKind = Some(varKind)

  def pretty = name + ":" + varKind.pretty
}
protected[internal] case class TermVar(name: String, varType: Option[Type]) extends Variable {
  def isTypeVar = false
  def isTermVar = true

  def hasType = varType.isDefined
  def hasKind = false

  def getName = name
  def getType = varType
  def getKind = None

  import Signature.{get => signature}

  def pretty = varType match {
    case None      => name
    case Some(typ) => name + ":" + typ.pretty
  }
}
