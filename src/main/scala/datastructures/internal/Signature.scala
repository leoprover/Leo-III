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
                                             k: Kind)
    extends VarMeta {
    def getName = name
    def getKey = key
    def getSymType = TypeVariable
    def getType: Option[Type] = None
    def getKind: Option[Kind] = Some(k)

    def hasType: Boolean = false
    def hasKind: Boolean = true
  }


  /** Case class for meta information for term variables */
  protected[internal] case class TermVarMeta(name: String,
                                             key: VarKey,
                                             typ: Option[Type])
    extends VarMeta {
    def getName = name
    def getKey = key
    def getSymType = TermVariable
    def getType: Option[Type] = typ
    def getKind: Option[Kind] = None

    def hasType: Boolean = typ.isDefined
    def hasKind: Boolean = false
  }

  /** Case class for meta information for base types that are indexed in the signature */
  protected[internal] case class TypeMeta(name: String,
                                          key: ConstKey,
                                          k:  Kind,
                                          typeRep: Option[Type]) extends ConstMeta {
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
                                                   key: ConstKey,
                                                   typ: Type) extends ConstMeta {
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
                                             key: ConstKey,
                                             typ: Option[Type],
                                             defn: Term) extends ConstMeta {
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
                                           key: ConstKey,
                                           typ: Type) extends ConstMeta {
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

  // note that not the key itself but the *inverted keys*
  // are saved in the xSets, since they cant store negative numbers
  protected def addVariable0(identifier: String, typ: Option[TypeOrKind]): VarKey = {
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
      case Some(Right(k: Kind)) => { // It's a type variable symbol -- check first, since Kind <: Type
        val meta = TypeVarMeta(identifier, key, k)
        metaMap += (key, meta)
        typeVarsSet += -key
      }
      case Some(Left(ty: Type)) => { // It's term variable symbol
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

  protected def addConstant0(identifier: String, typ: Option[TypeOrKind], defn: Option[Term]): ConstKey = {
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
    fixedSet += key
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
  // since the inverted keys are stored, we need to inverted them here again
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

trait SignatureTypes {
  type Var = Signature#VarKey
  type Const = Signature#ConstKey
}