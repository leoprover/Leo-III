package datastructures.internal

import scala.collection.immutable.{HashSet, BitSet, IntMap, HashMap}

/**
 * Created by lex on 29.04.14.
 */
abstract sealed class Signature extends IsSignature with HOLSignature {
  override type ConstKey = Int // Invariant: Positive integers
  override type VarKey = Int // Invariant: Negative integers
  var curConstKey = 1
  var curVarKey = -1

  protected var keyMap: Map[String, Int] = new HashMap[String, Int]
  protected var metaMap: IntMap[Meta[Int]] = IntMap.empty

  protected var typeVarsSet, termVarsSet: BitSet = BitSet.empty
  protected var typeSet, fixedSet, definedSet, uiSet: BitSet = BitSet.empty

  protected def addConstant0(identifier: String, typ: Option[Type], defn: Option[Term]): ConstKey = {
    val key = curConstKey
    curConstKey += 1
    keyMap += ((identifier, key))

    defn match {
      case None => { // Uninterpreted or type
        typ match {
          case None => assert(false, "This should not happen")
          case Some(k:Kind) => { // Type
            true match {
              case k.isBaseKind => {
                val meta = TypeMeta(identifier, key, k, Type.mkBaseType(identifier))
                metaMap += (key, meta)
              }
              case k.isFunKind => {
                val meta = TypeMeta(identifier, key, k, Type.mkConstructorType(identifier))
                metaMap += (key, meta)
              }
              case true => {
                val meta = TypeMeta(identifier, key, Type.getSuperKind, Type.getSuperKind)
                metaMap += (key, meta)
              }
            }
            typeSet += key
          }
          case Some(t:Type) => { // Uninterpreted symbol
            val meta = UninterpretedMeta(identifier, key, t)
            metaMap += (key, meta)
            uiSet += key
          }
        }
      }

      case Some(fed) => { // Defined
        val meta = DefinedMeta(identifier, key, typ, fed)
        metaMap += (key, meta)
        definedSet += key
      }
    }

    key
  }



  protected def addVariable0(identifier: String, typ: Option[Type]): VarKey = {
    val key = curVarKey
    curVarKey -= 1
    keyMap += ((identifier, key))
    typ match {
      case None => {
        val meta = TermVarMeta(identifier, key, None) // A variable with no type is assumed
        metaMap += (key, meta)                        // to be a `TermVariable`
        termVarsSet += -key
      }
      case Some(k: Kind) => {
        val meta = TypeVarMeta(identifier, key, k)
        metaMap += (key, meta)
        typeVarsSet += -key
      }
      case Some(ty: Type) => {
        val meta = TermVarMeta(identifier, key, Some(ty))
        metaMap += (key, meta)
        termVarsSet += -key
      }
    }
    key
  }

  protected def addFixed(identifier: String, typ: Type): Unit = {
    val key = curConstKey
    curConstKey += 1
    keyMap += ((identifier, key))

    val meta = FixedMeta(identifier, key, typ)
    metaMap += (key, meta)
  }

  // naive implementation, will change in the future
  def getUninterpretedSymbols: Set[ConstKey] = uiSet.toSet

  def getFixedDefinedSymbols: Set[ConstKey] = fixedSet.toSet

  def getDefinedSymbols: Set[ConstKey] = definedSet.toSet

  def getBaseTypes: Set[ConstKey] = typeSet.toSet

  def getAllConstants: Set[ConstKey] = uiSet | fixedSet | definedSet | typeSet

  def getTypeVariables: Set[VarKey] = asHashSet(typeVarsSet).map(i => -i).toSet
  def getTermVariables: Set[VarKey] = asHashSet(termVarsSet).map(i => -i).toSet

  def getAllVariables: Set[VarKey] = getTypeVariables | getTermVariables


  protected def asHashSet(set: Set[Int]) = {
    val e: HashSet[Int] = HashSet.empty
    set.foldLeft(e)(_+_)
  }


  def getConstMeta(identifier: String): ConstMeta = getConstMeta(keyMap(identifier))

  def getConstMeta(key: ConstKey): ConstMeta = metaMap(key).asInstanceOf[ConstMeta]

  def getVarMeta(identifier: String): VarMeta =  getVarMeta(keyMap(identifier))

  def getVarMeta(key: VarKey): VarMeta = metaMap(key).asInstanceOf[VarMeta]
}


object Signature {
  private case object Nil extends Signature

  def empty: Signature = Signature.Nil
  def create: Signature = {
    val sig = empty
    for ((name, k) <- sig.types) {
      sig.addConstant0(name, Some(k), None)
    }

    for ((name, ty) <- sig.fixedConsts) {
      sig.addFixed(name, ty)
    }

    for ((name, fed, ty) <- sig.definedConsts) {
      sig.addConstant0(name, Some(ty), Some(fed))
    }
    sig
  }
}
