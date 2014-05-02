package datastructures.internal

import scala.collection.immutable.{IntMap, HashMap}

/**
 * Created by lex on 29.04.14.
 */
class Signature extends IsSignature with HOLSignature {
  protected var keyMap: Map[String, Int] = new HashMap[String, Int]
  protected var metaMap: IntMap[Meta[Int]] = IntMap.empty

  override type ConstKey = Int
  override type VarKey = Int

  def getUninterpretedSymbols: Set[ConstKey] = ???

  def getFixedDefinedSymbols: Set[ConstKey] = ???

  def getDefinedSymbols: Set[ConstKey] = ???

  def getBaseTypes: Set[ConstKey] = ???

  def getAllConstants: Set[ConstKey] = ???

  def getTypeVariables: Set[VarKey] = ???

  def getTermVariables: Set[VarKey] = ???

  def getAllVariables: Set[VarKey] = ???

  def getConstMeta(identifier: String): ConstMeta = ???

  def getConstMeta(key: ConstKey): ConstMeta = ???

  protected def addConstant0(identifier: String, typ: Option[Type], defn: Option[Term]): ConstKey = ???

  def getVarMeta(identifier: String): VarMeta = ???

  def getVarMeta(key: VarKey): VarMeta = ???

  protected def addVariable0(identifier: String, typ: Option[Type]): VarKey = ???
}

