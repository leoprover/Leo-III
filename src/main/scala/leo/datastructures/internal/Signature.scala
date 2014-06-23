package leo.datastructures.internal

import scala.collection.immutable.{HashSet, BitSet, IntMap, HashMap}

/**
 * Implementation of the Leo III signature table. When created with `Signature.createWithHOL`
 * it contains some predefined symbols (including types, fixed symbols and defined symbols).
 * For details on that predefined symbols, check [[leo.datastructures.internal.HOLSignature]].
 *
 * @author Alexander Steen
 * @since 02.05.2014
 * @note  Updated on 05.05.2014 (Moved case classes from `IsSignature` to this class)
 */
abstract sealed class Signature extends IsSignature with HOLSignature with Function1[Int, IsSignature#Meta] {
  override type Key = Int

  protected var curConstKey = 1

  protected var keyMap: Map[String, Int] = new HashMap[String, Int]
  protected var metaMap: IntMap[Meta] = IntMap.empty

  protected var typeSet, fixedSet, definedSet, uiSet: BitSet = BitSet.empty

  ///////////////////////////////
  // Meta information
  ///////////////////////////////

  /** Case class for meta information for base types that are indexed in the signature */
  protected[internal] case class TypeMeta(identifier: String,
                                          index: Key,
                                          k:  Kind,
                                          typeRep: Option[Type]) extends Meta {
    def name = identifier
    def key = index
    def symType = BaseType
    /** Return type representation of the symbol */
    def ty: Option[Type] = typeRep
    def kind: Option[Kind] = Some(k)
    def defn: Option[Term] = None

    def hasType = true // Since we provide type representation
    def hasKind = true
    def hasDefn = false
  }

  /** Case class for meta information for uninterpreted symbols */
  protected[internal] case class UninterpretedMeta(identifier: String,
                                                   index: Key,
                                                   typ: Type) extends Meta {
    def name = identifier
    def key = index
    def symType = Uninterpreted
    def ty: Option[Type] = Some(typ)
    def kind: Option[Kind] = None
    def defn: Option[Term] = None

    def hasType = true
    def hasKind = false
    def hasDefn = false
  }

  /** Case class for meta information for defined symbols */
  protected[internal] case class DefinedMeta(identifier: String,
                                             index: Key,
                                             typ: Option[Type],
                                             definition: Term) extends Meta {
    def name = identifier
    def key = index
    def symType = Defined
    def ty: Option[Type] = typ
    def kind: Option[Kind] = None
    def defn: Option[Term] = Some(definition)

    def hasType = typ.isDefined
    def hasKind = false
    def hasDefn = true
  }

  /** Case class for meta information for fixed (interpreted) symbols */
  protected[internal] case class FixedMeta(identifier: String,
                                           index: Key,
                                           typ: Type) extends Meta {
    def name = identifier
    def key = index
    def symType = Fixed
    def ty: Option[Type] = Some(typ)
    def kind: Option[Kind] = None
    def defn: Option[Term] = None

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
              case k.isFunKind => {
                  throw new IllegalArgumentException("General constructors not yet supported")
//                val meta = TypeMeta(identifier, key, k, Type.mkConstructorType(identifier))
//                metaMap += (key, meta)
              }
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

  def remove(key: Key): Boolean = {
     metaMap.get(key) match {
       case None => false
       case Some(meta) => {
         val id = meta.name
         metaMap -= key
         keyMap -= id
         true
       }
     }
  }

  def exists(identifier: String): Boolean = keyMap.contains(identifier)

  def symbolType(identifier: String): SymbolType = metaMap(keyMap(identifier)).symType
  def symbolType(identifier: Key): SymbolType = metaMap(identifier).symType

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

  def meta(key: Key): Meta = metaMap(key)
  def meta(identifier: String): Meta = meta(keyMap(identifier))

  ///////////////////////////////
  // Dumping of indexed symbols
  ///////////////////////////////

  def allConstants: Set[Key] = uiSet | fixedSet | definedSet | typeSet
  def fixedSymbols: Set[Key] = fixedSet.toSet
  def definedSymbols: Set[Key] = definedSet.toSet
  def uninterpretedSymbols: Set[Key] = uiSet.toSet
  def baseTypes: Set[Key] = typeSet.toSet


  ///////////////////////////////
  // Creating of fresh variables
  ///////////////////////////////
  // Skolem variables start with 'SK'
  var skolemVarCounter = 0
  val skolemVarPrefix = "SK"
  /** Returns a fresh uninterpreted symbol of type `ty`. That symbol will be
    * named `SKi` where i is some positive number. */
  def freshSkolemVar(ty: Type): Key = {
    while(exists(skolemVarPrefix + (skolemVarCounter +1).toString)) {
      skolemVarCounter += 1
    }
    skolemVarCounter += 1
    addUninterpreted(skolemVarPrefix + skolemVarCounter.toString, ty)
  }
  // Skolem variables start with 'TV'
  var typeVarCounter = 0
  val typeVarPrefix = "TV"
  /** Returns a fresh base type symbol. That symbol will be
    * named `TVi` where i is some positive number. */
  def freshTypeVar: Key = {
    while(exists(typeVarPrefix + (typeVarCounter + 1).toString)) {
      typeVarCounter += 1
    }
    typeVarCounter += 1
    addBaseType(typeVarPrefix + typeVarCounter.toString)
  }

  ////////////////////////////////
  // Sugar methods
  ////////////////////////////////
  def apply(symbol: String) = meta(symbol)
  def apply(key: Key) = meta(key)
}


object Signature {
  private case object Nil extends Signature

  /** Create an empty signature */
  def empty: Signature = Nil

  protected val globalSignature = withHOL(empty)
  def get = globalSignature

  def apply(symbol: Signature#Key): Signature#Meta = get.meta(symbol)
  def apply(symbol: String): Signature#Meta = get.meta(symbol)

  /** Enriches the given signature with predefined symbols as described by [[leo.datastructures.internal.HOLSignature]] */
  def withHOL(sig: Signature): Signature = {
    for ((name, k) <- sig.types) {
      sig.addConstant0(name, Some(Right(k)), None)
    }

    for ((name, ty) <- sig.fixedConsts) {
      sig.addFixed(name, ty)
    }

    for ((name, fed, ty) <- sig.definedConsts) {
      sig.addConstant0(name, Some(Left(ty)), Some(fed))
    }
   sig
  }
}