package leo.datastructures.impl

import scala.collection.immutable.{BitSet, HashMap, IntMap}

import leo.datastructures.{HOLSignature, IsSignature, Kind, Type, Term}

/**
 * Implementation of the Leo III signature table. When created with `Signature.createWithHOL`
 * it contains some predefined symbols (including types, fixed symbols and defined symbols).
 * For details on that predefined symbols, check [[HOLSignature]].
 *
 * @author Alexander Steen
 * @since 02.05.2014
 * @note  Updated on 05.05.2014 (Moved case classes from `IsSignature` to this class)
 */
abstract sealed class Signature extends IsSignature with HOLSignature with Function1[Int, IsSignature#Meta] {
  override type Key = Int

  protected var curConstKey = 0

  protected var keyMap: Map[String, Int] = new HashMap[String, Int]
  protected var metaMap: IntMap[Meta] = IntMap.empty

  protected var typeSet, fixedSet, definedSet, uiSet: BitSet = BitSet.empty

  ///////////////////////////////
  // Meta information
  ///////////////////////////////

  /** Case class for meta information for base types that are indexed in the signature */
  protected[impl] case class TypeMeta(name: String,
                                          key: Key,
                                          k:  Kind,
                                          flag: IsSignature.SymbProp) extends Meta {
    val ty: Option[Type] = None
    val kind: Option[Kind] = Some(k)
    val defn: Option[Term] = None
  }

  /** Case class for meta information for (un)-interpreted symbols,
    * i.e. symbols without definition regardless whether system or user provided. */
  protected[impl] case class UninterpretedMeta(name: String,
                                                   key: Key,
                                                   typ: Type,
                                                   flag: IsSignature.SymbProp) extends Meta {
    val ty: Option[Type] = Some(typ)
    val kind: Option[Kind] = None
    val defn: Option[Term] = None
  }

  /** Case class for meta information for defined symbols */
  protected[impl] case class DefinedMeta(name: String,
                                             key: Key,
                                             typ: Type,
                                             definition: Term,
                                             flag: IsSignature.SymbProp) extends Meta {
    val ty: Option[Type] = Some(typ)
    val kind: Option[Kind] = None
    val defn: Option[Term] = Some(definition)
  }

  ///////////////////////////////
  // Maintenance methods for the signature
  ///////////////////////////////

  protected def addConstant0(identifier: String, typ: TypeOrKind, defn: Option[Term], status: Int): Key = {
    if (keyMap.contains(identifier)) {
      throw new IllegalArgumentException("Identifier " + identifier + " is already present in signature.")
    }

    val key = curConstKey
    curConstKey += 1
    keyMap += ((identifier, key))

    defn match {
      case None => { // Uninterpreted or type
        typ match {
          case Right(k:Kind) => { // Type
            true match {
              case k.isTypeKind | k.isFunKind => {
                val meta = TypeMeta(identifier, key, k, IsSignature.PropNoProp)
                metaMap += ((key, meta))
              }
              case _ => { // it is neither a base or funKind, then it's a super kind.
              val meta = TypeMeta(identifier, key, Type.superKind, IsSignature.PropNoProp)
                metaMap += ((key, meta))
              }
            }
            typeSet += key
          }
          case Left(t:Type) => { // Uninterpreted symbol
          val meta = UninterpretedMeta(identifier, key, t, status*IsSignature.PropStatus)
            metaMap += ((key, meta))
            uiSet += key
          }
        }
      }

      case Some(fed) => { // Defined
        val ty = typ.left.get
        val meta = DefinedMeta(identifier, key, ty, fed, status*IsSignature.PropStatus)
          metaMap += ((key, meta))
          definedSet += key
        }
    }

    key
  }

  def addDefinition(key: Key, defn: Term) = {
    metaMap.get(key) match {
      case Some(meta) if meta.isUninterpreted && meta._ty == defn.ty => {
        val newMeta = DefinedMeta(meta.name, key, meta._ty, defn, meta.flag)
        metaMap += ((key, newMeta))
        definedSet += key
        uiSet -= key
        key
      }
      case _ => key
    }
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

  /** Adds a term symbol to the signature that is then marked as system symbol type */
  protected def addFixed(identifier: String, typ: Type, defn: Option[Term], flag: IsSignature.SymbProp): Unit = {
    val key = curConstKey
    curConstKey += 1
    keyMap += ((identifier, key))
    if (defn.isDefined) {
      val deff = defn.get
      val meta = DefinedMeta(identifier, key, typ, deff, flag)
      metaMap += ((key, meta))
      fixedSet += key
    } else {
      val meta = UninterpretedMeta(identifier, key, typ, flag)
      metaMap += ((key, meta))
      fixedSet += key
    }
  }

  /** Adds a type symbol to the signature that is then marked as system symbol type */
  protected def addFixedType(identifier: String): Unit = {
    val key = curConstKey
    curConstKey += 1
    keyMap += ((identifier, key))
    val meta = TypeMeta(identifier, key, Type.typeKind, IsSignature.PropFixed)
    metaMap += ((key, meta))
    fixedSet += key
  }

  ///////////////////////////////
  // Utility methods for constant symbols
  ///////////////////////////////

  def meta(key: Key): Meta = try {metaMap(key)} catch {case e:Throwable => throw new RuntimeException("Tried to access meta with key: "+key.toString + ". ",e)}
  def meta(identifier: String): Meta = meta(keyMap(identifier))

  def empty() = {
    curConstKey = 0
    keyMap = keyMap.empty
    metaMap = metaMap.empty

    typeSet = typeSet.empty
    fixedSet = fixedSet.empty
    definedSet = definedSet.empty
    uiSet = uiSet.empty

    Term.reset()
  }

  ///////////////////////////////
  // Dumping of indexed symbols
  ///////////////////////////////

  def allConstants: Set[Key] = uiSet | fixedSet | definedSet | typeSet
  def allUserConstants = (uiSet | definedSet | typeSet).filter(_ > HOLSignature.lastId)
  def fixedSymbols: Set[Key] = fixedSet.toSet
  def definedSymbols: Set[Key] = definedSet.toSet
  def uninterpretedSymbols: Set[Key] = uiSet.toSet
  def baseTypes: Set[Key] = typeSet.toSet


  ///////////////////////////////
  // Creating of fresh variables
  ///////////////////////////////
  // Skolem variables start with 'sk'
  var skolemVarCounter = 0
  val skolemVarPrefix = "sk"
  /** Returns a fresh uninterpreted symbol of type `ty`. That symbol will be
    * named `SKi` where i is some positive number. */
  def freshSkolemVar(ty: Type): Key = {
    while(exists(skolemVarPrefix + (skolemVarCounter +1).toString)) {
      skolemVarCounter += 1
    }
    skolemVarCounter += 1
    addUninterpreted(skolemVarPrefix + skolemVarCounter.toString, ty)
  }
  // Skolem variables start with 'tv'
  var typeVarCounter = 0
  val typeVarPrefix = "tv"
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
  private case class Nil() extends Signature

  /** Create an empty signature */
  def empty: Signature = Nil()

  protected val globalSignature = withHOL(empty)
  def get = globalSignature

  def resetWithHOL(sig: Signature): Signature = {
    sig.empty
    sig.skolemVarCounter=0
    sig.typeVarCounter=0
    withHOL(sig)
  }

  def apply(symbol: Signature#Key): Signature#Meta = get.meta(symbol)
  def apply(symbol: String): Signature#Meta = get.meta(symbol)

  /** Enriches the given signature with predefined symbols as described by [[HOLSignature]] */
  def withHOL(sig: Signature): Signature = {
    for ((name, k) <- sig.types) {
      sig.addFixedType(name)
    }

    for ((name, ty, flag) <- sig.fixedConsts) {
      sig.addFixed(name, ty, None, flag | IsSignature.PropFixed)
    }

    for ((name, fed, ty, flag) <- sig.definedConsts) {
      sig.addFixed(name, ty, Some(fed), flag | IsSignature.PropFixed)
    }
   sig
  }
}