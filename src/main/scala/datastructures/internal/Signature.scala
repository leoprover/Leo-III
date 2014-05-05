package datastructures.internal

import scala.collection.immutable.{HashSet, BitSet, IntMap, HashMap}

/**
 * Implementation of the Leo III signature table. When created with `Signature.createWithHOL`
 * it contains some predefined symbols (including types, fixed symbols and defined symbols).
 * For details on that predefined symbols, check [[datastructures.internal.HOLSignature]].
 *
 * @author Alexander Steen
 * @since 02.05.2014
 * @note  Updated on 05.05.2014 (Moved case classes from `IsSignature` to this class)
 */
abstract sealed class Signature extends IsSignature with HOLSignature {
  override type ConstKey = Int // Invariant: Positive integers
  override type VarKey = Int // Invariant: Negative integers
  protected var curConstKey = 1
  protected var curVarKey = -1

  protected var keyMap: Map[String, Int] = new HashMap[String, Int]
  protected var metaMap: IntMap[Meta[Int]] = IntMap.empty

  protected var typeVarsSet, termVarsSet: BitSet = BitSet.empty
  protected var typeSet, fixedSet, definedSet, uiSet: BitSet = BitSet.empty

  ///////////////////////////////
  // Meta information
  ///////////////////////////////

  /** Case class for meta information for type variables */
  protected[internal] case class TypeVarMeta(name: String,
                                             key: VarKey,
                                             typ: Kind)
    extends VarMeta {
    def getName = name
    def getKey = key
    def getSymType = TypeVariable

    def hasType: Boolean = true
  }


  /** Case class for meta information for term variables */
  protected[internal] case class TermVarMeta(name: String,
                                             key: VarKey,
                                             typ: Option[Type])
    extends VarMeta {
    def getName = name
    def getKey = key
    def getSymType = TermVariable

    def hasType: Boolean = typ.isDefined
  }

  /** Case class for meta information for base types that are indexed in the signature */
  protected[internal] case class TypeMeta(name: String,
                                          key: ConstKey,
                                          typ:  Kind,
                                          typeRep: Type) extends ConstMeta {
    def getName = name
    def getKey = key
    def getSymType = BaseType
    def hasType = true
    def hasDefn = false
  }

  /** Case class for meta information for uninterpreted symbols */
  protected[internal] case class UninterpretedMeta(name: String,
                                                   key: ConstKey,
                                                   typ: Type) extends ConstMeta {
    def getName = name
    def getKey = key
    def getSymType = Uninterpreted
    def hasType = true
    def hasDefn = false
  }

  /** Case class for meta information for defined symbols */
  protected[internal] case class DefinedMeta(name: String,
                                             key: ConstKey,
                                             typ: Option[Type],
                                             defn: Term) extends ConstMeta {
    def getName = name
    def getKey = key
    def getSymType = Defined
    def hasType = typ.isDefined
    def hasDefn = true
  }

  /** Case class for meta information for fixed (interpreted) symbols */
  protected[internal] case class FixedMeta(name: String,
                                           key: ConstKey,
                                           typ: Type) extends ConstMeta {
    def getName = name
    def getKey = key
    def getSymType = Fixed
    def hasType = true
    def hasDefn = false
  }

  ///////////////////////////////
  // Maintenance methods for the signature
  ///////////////////////////////

  protected def addVariable0(identifier: String, typ: Option[Type]): VarKey = {
    if (keyMap.contains(identifier)) {
      throw new IllegalArgumentException("Identifier " + identifier + " is already present in signature.")
    }

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

  def removeVariable(key: VarKey): Boolean = {
    metaMap.get(key) match {
      case None => false
      case Some(meta) => {
        val id = meta.getName
        metaMap -= key
        keyMap -= id
        true
      }
    }
  }

  def varExists(identifier: String): Boolean = {
    keyMap.get(identifier) match {
      case None => false
      case Some(k) => metaMap(k).isInstanceOf[VarMeta]
    }
  }

  protected def addConstant0(identifier: String, typ: Option[Type], defn: Option[Term]): ConstKey = {
    if (keyMap.contains(identifier)) {
      throw new IllegalArgumentException("Identifier " + identifier + " is already present in signature.")
    }

    val key = curConstKey
    curConstKey += 1
    keyMap += ((identifier, key))

    defn match {
      case None => { // Uninterpreted or type
        typ match {
          case None => throw new IllegalArgumentException("Neither definition nor type was passed to addConstant0.")
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
              case _ => { // it is neither a base or funKind, then it's a super kind.
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

  def removeConstant(key: ConstKey): Boolean = {
     metaMap.get(key) match {
       case None => false
       case Some(meta) => {
         val id = meta.getName
         metaMap -= key
         keyMap -= id
         true
       }
     }
  }

  def constExists(identifier: String): Boolean = {
    keyMap.get(identifier) match {
      case None => false
      case Some(k) => metaMap(k).isInstanceOf[ConstMeta]
    }
  }


  def symbolExists(identifier: String): Boolean = keyMap.contains(identifier)

  def symbolType(identifier: String): SymbolType = metaMap(keyMap(identifier)).getSymType

  /** Adds a symbol to the signature that is then marked as `Fixed` symbol type */
  protected def addFixed(identifier: String, typ: Type): Unit = {
    val key = curConstKey
    curConstKey += 1
    keyMap += ((identifier, key))

    val meta = FixedMeta(identifier, key, typ)
    metaMap += (key, meta)
  }

  ///////////////////////////////
  // Utility methods for variable symbols
  ///////////////////////////////

  def getVarMeta(key: VarKey): VarMeta = metaMap(key).asInstanceOf[VarMeta]
  def getVarMeta(identifier: String): VarMeta =  getVarMeta(keyMap(identifier))

  ///////////////////////////////
  // Utility methods for constant symbols
  ///////////////////////////////

  def getConstMeta(key: ConstKey): ConstMeta = metaMap(key).asInstanceOf[ConstMeta]
  def getConstMeta(identifier: String): ConstMeta = getConstMeta(keyMap(identifier))

  ///////////////////////////////
  // Dumping of indexed symbols
  ///////////////////////////////
  // naive implementation, will change in the future

  def getAllVariables: Set[VarKey]  = getTypeVariables | getTermVariables
  def getTypeVariables: Set[VarKey] = asHashSet(typeVarsSet).map(i => -i).toSet
  def getTermVariables: Set[VarKey] = asHashSet(termVarsSet).map(i => -i).toSet

  def getAllConstants: Set[ConstKey] = uiSet | fixedSet | definedSet | typeSet
  def getFixedDefinedSymbols: Set[ConstKey] = fixedSet.toSet
  def getDefinedSymbols: Set[ConstKey] = definedSet.toSet
  def getUninterpretedSymbols: Set[ConstKey] = uiSet.toSet
  def getBaseTypes: Set[ConstKey] = typeSet.toSet

  /** Returns a new HashSet containing the elements of `set`. It is used because
    * BitSets cannot store negative integers */
  protected def asHashSet(set: Set[Int]) = {
    val e: HashSet[Int] = HashSet.empty
    set.foldLeft(e)(_+_)
  }
}


object Signature {
  private case object Nil extends Signature

  def empty: Signature = Signature.Nil
  def createWithHOL: Signature = {
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
