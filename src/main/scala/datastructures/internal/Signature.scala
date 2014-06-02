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
  override type Key = Int

  protected var curConstKey = 1

  protected var keyMap: Map[String, Int] = new HashMap[String, Int]
  protected var metaMap: IntMap[Meta] = IntMap.empty

  protected var typeSet, fixedSet, definedSet, uiSet: BitSet = BitSet.empty

  ///////////////////////////////
  // Meta information
  ///////////////////////////////

  /** Case class for meta information for base types that are indexed in the signature */
  protected[internal] case class TypeMeta(name: String,
                                          key: Key,
                                          k:  Kind,
                                          typeRep: Option[Type]) extends Meta {
    def getName = name
    def getKey = key
    def getSymType = BaseType
    /** Return type representation of the symbol */
    def getType: Option[Type] = typeRep
    def getKind: Option[Kind] = Some(k)
    def getDefn: Option[Term] = None

    def hasType = true // Since we provide type representation
    def hasKind = true
    def hasDefn = false
  }

  /** Case class for meta information for uninterpreted symbols */
  protected[internal] case class UninterpretedMeta(name: String,
                                                   key: Key,
                                                   typ: Type) extends Meta {
    def getName = name
    def getKey = key
    def getSymType = Uninterpreted
    def getType: Option[Type] = Some(typ)
    def getKind: Option[Kind] = None
    def getDefn: Option[Term] = None

    def hasType = true
    def hasKind = false
    def hasDefn = false
  }

  /** Case class for meta information for defined symbols */
  protected[internal] case class DefinedMeta(name: String,
                                             key: Key,
                                             typ: Option[Type],
                                             defn: Term) extends Meta {
    def getName = name
    def getKey = key
    def getSymType = Defined
    def getType: Option[Type] = typ
    def getKind: Option[Kind] = None
    def getDefn: Option[Term] = Some(defn)

    def hasType = typ.isDefined
    def hasKind = false
    def hasDefn = true
  }

  /** Case class for meta information for fixed (interpreted) symbols */
  protected[internal] case class FixedMeta(name: String,
                                           key: Key,
                                           typ: Type) extends Meta {
    def getName = name
    def getKey = key
    def getSymType = Fixed
    def getType: Option[Type] = Some(typ)
    def getKind: Option[Kind] = None
    def getDefn: Option[Term] = None

    def hasType = true
    def hasKind = false
    def hasDefn = false
  }

  ///////////////////////////////
  // Maintenance methods for the signature
  ///////////////////////////////

  protected def addConstant0(identifier: String, typ: Option[TypeOrKind], defn: Option[Term]): Key = {
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
          case Some(Right(k:Kind)) => { // Type
            true match {
              case k.isTypeKind => {
                val meta = TypeMeta(identifier, key, k, Some(Type.mkType(key)))
                metaMap += (key, meta)
              }
              /*case k.isFunKind => {
                  throw new IllegalArgumentException("Constructor types not yet supported")
//                val meta = TypeMeta(identifier, key, k, Type.mkConstructorType(identifier))
//                metaMap += (key, meta)
              }*/
              case _ => { // it is neither a base or funKind, then it's a super kind.
              val meta = TypeMeta(identifier, key, Type.superKind, None)
                metaMap += (key, meta)
              }
            }
            typeSet += key
          }
          case Some(Left(t:Type)) => { // Uninterpreted symbol
          val meta = UninterpretedMeta(identifier, key, t)
            metaMap += (key, meta)
            uiSet += key
          }
        }
      }

      case Some(fed) => { // Defined
        val Left(ty) = typ.get
        val meta = DefinedMeta(identifier, key, Some(ty), fed)
          metaMap += (key, meta)
          definedSet += key
        }
    }

    key
  }

  def removeConstant(key: Key): Boolean = {
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
      case _    => true
    }
  }


  def symbolExists(identifier: String): Boolean = keyMap.contains(identifier)

  def symbolType(identifier: String): SymbolType = metaMap(keyMap(identifier)).getSymType
  def symbolType(identifier: Key): SymbolType = metaMap(identifier).getSymType

  def getMeta(identifier: Key): Meta = getConstMeta(identifier)

  /** Adds a symbol to the signature that is then marked as `Fixed` symbol type */
  protected def addFixed(identifier: String, typ: Type): Unit = {
    val key = curConstKey
    curConstKey += 1
    keyMap += ((identifier, key))

    val meta = FixedMeta(identifier, key, typ)
    metaMap += (key, meta)
    fixedSet += key
  }

  ///////////////////////////////
  // Utility methods for constant symbols
  ///////////////////////////////

  def getConstMeta(key: Key): Meta = metaMap(key)
  def getConstMeta(identifier: String): Meta = getConstMeta(keyMap(identifier))

  ///////////////////////////////
  // Dumping of indexed symbols
  ///////////////////////////////

  def getAllConstants: Set[Key] = uiSet | fixedSet | definedSet | typeSet
  def getFixedDefinedSymbols: Set[Key] = fixedSet.toSet
  def getDefinedSymbols: Set[Key] = definedSet.toSet
  def getUninterpretedSymbols: Set[Key] = uiSet.toSet
  def getBaseTypes: Set[Key] = typeSet.toSet

  ////////////////////////////////
  // Hard wired fixed keys
  ////////////////////////////////
  lazy val oKey = 1
  lazy val iKey = 2
}


object Signature {
  private case object Nil extends Signature

  /** Create an empty signature */
  def empty: Signature = Signature.Nil
  protected val globalSignature = empty
  def get = globalSignature

  /** Enriches the given signature with predefined symbols as described by [[datastructures.internal.HOLSignature]] */
  def withHOL(sig: Signature): Unit = {
    for ((name, k) <- sig.types) {
      sig.addConstant0(name, Some(Right(k)), None)
    }

    for ((name, ty) <- sig.fixedConsts) {
      sig.addFixed(name, ty)
    }

    for ((name, fed, ty) <- sig.definedConsts) {
      sig.addConstant0(name, Some(Left(ty)), Some(fed))
    }
  }
}