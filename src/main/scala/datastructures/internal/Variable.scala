package datastructures.internal

import datastructures.Pretty

/**
 *
 *
 * @author Alexander Steen
 * @since 30.04.2014
 */
object Variable extends SignatureTypes{
  def mkTypeVar(id: Var, varKind: Kind = TypeKind) = TypeVar(id, varKind)
  def mkVar     = TermVar(_,_)
}

abstract sealed class Variable extends Pretty {
  def isTypeVar: Boolean
  def isTermVar: Boolean

  def hasType: Boolean
  def hasKind: Boolean
  def getName: Signature#VarKey

  def getType: Option[Type]
  def _getType: Type = getType.get

  def getKind: Option[Kind]
  def _getKind: Kind = getKind.get

  def #!(body: Type): Type = {
    Type.mkPolyType(this, body)
  }

  //vielleicht local/global scope speichern? (Alex)
}

protected[internal] case class TypeVar(id: Signature#VarKey, varKind: Kind) extends Variable {
  def isTypeVar = true
  def isTermVar = false

  def hasType = false
  def hasKind = true

  def getName = id
  def getType = None
  def getKind = Some(varKind)

  import Signature.{get => signature}
  val prettyName = signature.getVarMeta(id).getName
  def pretty = prettyName + ":" + varKind.pretty
}
protected[internal] case class TermVar(id: Signature#VarKey, varType: Option[Type]) extends Variable {
  def isTypeVar = false
  def isTermVar = true

  def hasType = varType.isDefined
  def hasKind = false

  def getName = id
  def getType = varType
  def getKind = None

  import Signature.{get => signature}
  val prettyName = signature.getVarMeta(id).getName
  def pretty = varType match {
    case None      => prettyName
    case Some(typ) => prettyName + ":" + typ.pretty
  }
}
