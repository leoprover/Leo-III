package datastructures.internal

/**
 * Interface for signature representations.
 * The signature table can contain types, variables, and constants (which may be uninterpreted symbols or defined symbols).
 * Type variables will follow in next revision.
 *
 * @author Alexander Steen
 * @since 29.04.2014
 */
trait IsSignature {
  /** This type is used as indexing key for variables in the underlying dictionary implementation */
  type VarKey
  /** This type is used as indexing key for (possibly uninterpreted) constant symbols in the underlying dictionary implementation */
  type ConstKey

  /** Type of symbols in the signature table */
  sealed abstract class SymbolType
  // Variables
  /** A variable symbol, e.g. `X` */
  case object Variable extends SymbolType
  case object TypeVariable extends SymbolType
  // Constants
  /** A fixed symbol, e.g. `$true` or `$false` */
  case object Fixed extends SymbolType
  case object Defined extends SymbolType
  case object Uninterpreted extends SymbolType
  /** A base type symbol, e.g. `$o` */
  case object BaseType extends SymbolType

  /**
   * Entry base class for Meta information saved along with symbols in signature table
   * @tparam Key    The type of key that is used to save the `Meta` in the signature table
   */
  sealed abstract class Meta[+Key] {
    /**
     * The name of the symbol (i.e. string representation of it)
     */
    def getName: String

    /**
      * The key of type `Key` that is used as table key for that entry
      */
    def getKey: Key

    /**
     * The type of symbol the entry describes
     */
    def getSymType: SymbolType
  }

  sealed abstract class VarMeta extends Meta[VarKey] {
    /** Returns true iff the variable has a type (or kind) associated with it */
    def hasType: Boolean

    /** Returns true iff the symbol is a variable symbol */
    def isVariable: Boolean = getSymType == Variable
    /** Returns true iff the symbol is a tyoe variable symbol */
    def isTypeVariable: Boolean = getSymType == TypeVariable
  }

  /** Case class for meta information for type variables */
  case class TypeVarMeta(name: String,
                         key: VarKey,
                         typ: Kind)
    extends VarMeta {
      def getName = name
      def getKey = key
      def getSymType = TypeVariable

      def hasType: Boolean = true
  }


  /** Case class for meta information for term variables */
  case class TermVarMeta(name: String,
                         key: VarKey,
                         typ: Option[Type])
    extends VarMeta {
      def getName = name
      def getKey = key
      def getSymType = Variable

      def hasType: Boolean = typ.isDefined
  }

  /** abstract type for meta information for (possibly uninterpreted) constant symbols */
  sealed abstract class ConstMeta extends Meta[ConstKey] {
    /** Returns true iff the constant has a type associated with it */
    def hasType: Boolean
    /** Returns true iff the constant has a definition term associated with it */
    def hasDefn: Boolean

    /** Returns true iff the symbol is a uninterpreted symbol */
    def isUninterpreted: Boolean = getSymType == Uninterpreted
    /** Returns true iff the symbol is a defined symbol */
    def isDefinedConstant: Boolean = getSymType == Defined
    /** Returns true iff the symbol is a fixed (interpreted) symbol */
    def isFixed: Boolean = getSymType == Fixed
    /** Returns true iff the symbol is a type symbol */
    def isType: Boolean = getSymType == BaseType
  }

  /** Case class for meta information for base types that are indexed in the signature */
  case class TypeMeta(name: String,
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
  case class UninterpretedMeta(name: String,
                               key: ConstKey,
                               typ: Type) extends ConstMeta {
    def getName = name
    def getKey = key
    def getSymType = Uninterpreted
    def hasType = true
    def hasDefn = false
  }

  /** Case class for meta information for defined symbols */
  case class DefinedMeta(name: String,
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
  case class FixedMeta(name: String,
                       key: ConstKey,
                       typ: Type) extends ConstMeta {
    def getName = name
    def getKey = key
    def getSymType = Fixed
    def hasType = true
    def hasDefn = false
  }

  // method names and parameters will change!

  protected def addVariable0(identifier: String, typ: Option[Type]): VarKey

  def addVariable(identifier: String, typ: Type): VarKey =
    addVariable0(identifier, Some(typ))
  def addVariable_(identifier: String): VarKey =
    addVariable0(identifier, None)

  def getVarMeta(key: VarKey): VarMeta
  def getVarMeta(identifier: String): VarMeta

  def isVariable(key: VarKey): Boolean = getVarMeta(key).isVariable
  def isVariable(identifier: String): Boolean = getVarMeta(identifier).isVariable
  def isTypeVariable(key: VarKey): Boolean = getVarMeta(key).isTypeVariable
  def isTypeVariable(identifier: String): Boolean = getVarMeta(identifier).isTypeVariable

  protected def addConstant0(identifier: String, typ: Option[Type], defn: Option[Term]): ConstKey

  def addConstant(identifier: String, typ: Type, defn: Term): ConstKey =
    addConstant0(identifier, Some(typ), Some(defn))
  def addConstant_(identifier: String, defn: Term): ConstKey =
    addConstant0(identifier, None, Some(defn))
  def addUninterpreted(identifier: String, typ: Type): ConstKey =
    addConstant0(identifier, Some(typ), None)
  def addBaseType(typ: String): ConstKey = addUninterpreted(typ, Type.getBaseKind)

  def getConstMeta(key: ConstKey): ConstMeta
  def getConstMeta(identifier: String): ConstMeta

  def isDefinedSymbol(identifier: String): Boolean = getConstMeta(identifier).isDefinedConstant
  def isFixedSymbol(identifier: String): Boolean = getConstMeta(identifier).isFixed
  def isUninterpreted(identifier: String): Boolean = getConstMeta(identifier).isUninterpreted
  def isBaseType(typ: String): Boolean = getConstMeta(typ).isType


  def getAllVariables: Set[VarKey]
  def getTermVariables: Set[VarKey]
  def getTypeVariables: Set[VarKey]

  def getAllConstants: Set[ConstKey]
  def getBaseTypes: Set[ConstKey]
  def getDefinedSymbols: Set[ConstKey]
  def getFixedDefinedSymbols: Set[ConstKey]
  def getUninterpretedSymbols: Set[ConstKey]
}