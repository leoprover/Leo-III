package datastructures.internal

import scala.collection.immutable.{IntMap, HashMap}

/**
 * Created by lex on 29.04.14.
 */
class Signature extends IsSignature with HOLSignature {
  protected var keyMap: Map[String, Int] = new HashMap[String, Int]
  protected var metaMap: IntMap[Meta[Int]] = IntMap.empty

  override def getUninterpretedSymbols: Set[UninterpretedMeta] = ???

  override def getFixedDefinedSymbols: Set[FixedMeta] = ???

  override def getDefinedSymbols: Set[DefinedMeta] = ???

  override def getAllConstants: Set[ConstMeta] = ???

  override def getVariables: Set[VarMeta] = ???

  override def getBaseTypes: Set[TypeMeta] = ???

  override def getConstMeta(identifier: String): ConstMeta = ???

  override def getConstMeta(key: ConstKey): ConstMeta = ???

  override def isUninterpreted(identifier: String): Boolean = ???

  override def isFixedSymbol(identifier: String): Boolean = ???

  override def isDefinedSymbol(identifier: String): Boolean = ???

  override protected def addConstant0(identifier: String, typ: Option[Type], defn: Option[Term]): ConstKey = ???

  override def getVarMeta(key: VarKey): VarMeta = ???

  override def isVariable(identifier: String): Boolean = ???

  override protected def addVariable0(identifier: String, typ: Option[Type]): VarKey = ???

  override def getTypeMeta(key: ConstKey): TypeMeta = ???

  override def getTypeMeta(typ: String): TypeMeta = ???

  override def isBaseType(typ: String): Boolean = ???

  override type ConstKey = Int
  override type VarKey = Int

  // method names and parameters will change!
  override def addBaseType(typ: String): ConstKey = ???
}

