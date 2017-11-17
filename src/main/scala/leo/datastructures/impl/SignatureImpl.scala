package leo.datastructures.impl

import scala.collection.immutable.{BitSet, HashMap, IntMap}

import leo.datastructures.{Signature, Kind, Type, Term}
import leo.modules.HOLSignature

/**
 * Implementation of the Leo III signature table. When created with `Signature.createWithHOL`
 * it contains some predefined symbols (including types, fixed symbols and defined symbols).
 * For details on that predefined symbols, check [[HOLSignature]].
 *
 * @author Alexander Steen
 * @since 02.05.2014
 * @note  Updated on 05.05.2014 (Moved case classes from `IsSignature` to this class)
 */
class SignatureImpl extends Signature with Function1[Int, Signature.Meta] {
  import Signature.{Meta, Key}
  import SignatureImpl.{TypeMeta, DefinedMeta, UninterpretedMeta}

  protected var curConstKey = 0

  protected var keyMap: Map[String, Int] = new HashMap[String, Int]
  protected var metaMap: IntMap[Meta] = IntMap.empty

  /* typeSet: set of all type constructors,
  * fixedSet: set of all built-in symbols (regardless if primitive or not)
  * definedSet: set of all symbols having a definition
  * uiSet: set of all symbols not having a definition
  * aSet: set of all associative symbols
  * cSet: set of all commutative symbols
  */
  protected var typeSet, fixedSet, definedSet, uiSet, aSet, cSet: Set[Key] = BitSet.empty



  ///////////////////////////////
  // Maintenance methods for the signature
  ///////////////////////////////

  final def copy: Signature = {
    val copySig: SignatureImpl = SignatureImpl.empty
    copySig.curConstKey = curConstKey
    copySig.keyMap = keyMap
    copySig.metaMap = metaMap

    copySig.typeSet = typeSet
    copySig.fixedSet = fixedSet
    copySig.definedSet = definedSet
    copySig.uiSet = uiSet
    copySig.aSet = aSet
    copySig.cSet = cSet

    copySig
  }

  protected def addConstant0(identifier: String, typ: TypeOrKind, defn: Option[Term], prop: Signature.SymbProp): Key = {
    import leo.datastructures.isPropSet
    if (keyMap.contains(identifier)) {
      throw new IllegalArgumentException("Identifier " + identifier + " is already present in signature.")
    }

    val key = curConstKey
    curConstKey += 1
    keyMap += ((identifier, key))

    val meta = defn match {
      case None => // Uninterpreted or type
        typ match {
          case Right(k:Kind) => // Type
            typeSet += key
            TypeMeta(identifier, key, k, prop)
          case Left(t:Type) =>  // (possibly) uninterpreted symbol
            uiSet += key
            UninterpretedMeta(identifier, key, t, prop)
        }
      case Some(fed) => // Defined
        if (typ.isLeft) {
          val ty = typ.left.get
          definedSet += key
          DefinedMeta(identifier, key, ty, fed, prop)
        } else {
          // Type constructor definitions not supported
          throw new IllegalArgumentException("Type constructor definitions not supported")
        }
    }
    metaMap += ((key, meta))
    if (isPropSet(Signature.PropFixed, prop)) {
      fixedSet += key
      uiSet -= key // This may be set if no definition was provided. If not, it has no effect since key is not contained
    }
    if (isPropSet(Signature.PropAssociative, prop)) aSet += key
    if (isPropSet(Signature.PropCommutative, prop)) cSet += key
    key
  }

  def addDefinition(key: Key, defn: Term): Key = {
    metaMap.get(key) match {
      case Some(meta) if meta.isUninterpreted && meta._ty == defn.ty =>
        val newMeta = DefinedMeta(meta.name, key, meta._ty, defn, meta.flag)
        metaMap += ((key, newMeta))
        definedSet += key
        uiSet -= key
        key
      case _ => key
    }
  }

  def remove(key: Key): Boolean = {
     metaMap.get(key) match {
       case None => false
       case Some(meta) =>
         if (meta.isFixedSymbol) return false

         if (meta.isASymbol) aSet -= key
         if (meta.isCSymbol) cSet -= key
         if (meta.isDefined) definedSet -= key
         if (meta.isUninterpreted) uiSet -= key
         if (meta.isTypeConstructor) typeSet -= key

         val id = meta.name
         metaMap -= key
         keyMap -= id
         true
     }
  }

  def exists(identifier: String): Boolean = keyMap.contains(identifier)

  ///////////////////////////////
  // Utility methods for constant symbols
  ///////////////////////////////

  def meta(key: Key): Meta = try {metaMap(key)} catch {case e:Throwable => throw new RuntimeException("Tried to access meta with key: "+key.toString + ". ",e)}
  def meta(identifier: String): Meta = meta(keyMap(identifier))

  def empty(): Unit = {
    curConstKey = 0
    keyMap = keyMap.empty
    metaMap = metaMap.empty

    typeSet = typeSet.empty
    fixedSet = fixedSet.empty
    definedSet = definedSet.empty
    uiSet = uiSet.empty
    aSet = aSet.empty
    cSet = cSet.empty

    Term.reset()
  }

  ///////////////////////////////
  // Dumping of indexed symbols
  ///////////////////////////////

  def allConstants: Set[Key] = uiSet | fixedSet | definedSet | typeSet
  def allUserConstants: Set[Key] = allConstants &~ fixedSet
  def primitiveSymbols: Set[Key] = fixedSet &~ definedSet
  def fixedSymbols: Set[Key] = fixedSet
  def definedSymbols: Set[Key] = definedSet
  def uninterpretedSymbols: Set[Key] = uiSet
  def typeConstructors: Set[Key] = typeSet
  def aSymbols: Set[Key] = aSet
  def cSymbols: Set[Key] = cSet

  ///////////////////////////////
  // Creating of fresh variables
  ///////////////////////////////
  // Skolem variables start with 'sk'
  var skolemVarCounter = 0
  val skolemVarPrefix = "sk"
  /** Returns a fresh uninterpreted symbol of type `ty`. That symbol will be
    * named `SKi` where i is some positive number. */
  def freshSkolemConst(ty: Type, prop: Signature.SymbProp = Signature.PropNoProp): Key = synchronized {
    assert(ty.typeVars.isEmpty)
    while(exists(skolemVarPrefix + (skolemVarCounter +1).toString)) {
      skolemVarCounter += 1
    }
    skolemVarCounter += 1
    addUninterpreted(skolemVarPrefix + skolemVarCounter.toString, ty, prop | Signature.PropSkolemConstant | Signature.PropStatus)
  }
  // Skolem variables start with 'tv'
  var typeVarCounter = 0
  val typeVarPrefix = "skt"
  /** Returns a fresh base type symbol. That symbol will be
    * named `tvi` where i is some positive number. */
  def freshSkolemTypeConst(k: Kind): Key = synchronized {
    while(exists(typeVarPrefix + (typeVarCounter + 1).toString)) {
      typeVarCounter += 1
    }
    typeVarCounter += 1
    addTypeConstructor(typeVarPrefix + typeVarCounter.toString, k)
  }
}

object SignatureImpl {
  /** Create an empty signature */
  def empty: SignatureImpl = new SignatureImpl

  ///////////////////////////////
  // Meta information
  ///////////////////////////////
  import leo.datastructures.Signature.{Meta, Key}

  /** Case class for meta information for type constructors
    * that are indexed in the signature */
  protected[impl] case class TypeMeta(name: String,
                                      key: Key,
                                      k:  Kind,
                                      var flag: Signature.SymbProp) extends Meta {
    val ty: Option[Type] = None
    val kind: Option[Kind] = Some(k)
    val defn: Option[Term] = None
    final def updateProp(newProp: Signature.SymbProp): Unit = {flag = newProp}
  }

  /** Case class for meta information for (un)-interpreted term symbols,
    * i.e. symbols without definition regardless whether system or user provided. */
  protected[impl] case class UninterpretedMeta(name: String,
                                               key: Key,
                                               typ: Type,
                                               var flag: Signature.SymbProp) extends Meta {
    val ty: Option[Type] = Some(typ)
    val kind: Option[Kind] = None
    val defn: Option[Term] = None
    final def updateProp(newProp: Signature.SymbProp): Unit = {flag = newProp}
  }

  /** Case class for meta information for defined symbols */
  protected[impl] case class DefinedMeta(name: String,
                                         key: Key,
                                         typ: Type,
                                         definition: Term,
                                         var flag: Signature.SymbProp) extends Meta {
    val ty: Option[Type] = Some(typ)
    val kind: Option[Kind] = None
    val defn: Option[Term] = Some(definition)
    final def updateProp(newProp: Signature.SymbProp): Unit = {flag = newProp}
  }
}
