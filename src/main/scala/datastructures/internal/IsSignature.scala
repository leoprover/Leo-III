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
  /** `SymbolType` of a (term) variable symbol, e.g. `X: $i` */
  case object TermVariable extends SymbolType
  /** `SymbolType` of a type variable symbol, e.g. `X: *` */
  case object TypeVariable extends SymbolType

  // Constants
  /** `SymbolType` of a fixed symbol, e.g. `$true: $o` or `$false: $o` */
  case object Fixed extends SymbolType
  /** `SymbolType` of a defined symbol, e.g. `&: $o -> $o -> $o`  */
  case object Defined extends SymbolType
  /** `SymbolType` of a uninterpreted symbol, e.g. `p: $i -> $o`*/
  case object Uninterpreted extends SymbolType
  /** `SymbolType` of a base type symbol, e.g. `$o: *` */
  case object BaseType extends SymbolType


  /**
   * Entry base class for meta information saved along with symbols in the signature table.
   *
   * @tparam Key    The type of key that is used to index the `Meta` in the signature table
   */
  sealed abstract class Meta[+Key] {
    /** The name of the symbol (i.e. string representation of it) */
    def getName: String
    /** The key of type `Key` that is used as table key for that entry */
    def getKey: Key
    /** The type of symbol the entry describes */
    def getSymType: SymbolType
  }

  /** Entry type for variable meta information */
  abstract class VarMeta extends Meta[VarKey] {
    /** Returns true iff the variable has a type (or kind) associated with it */
    def hasType: Boolean

    /** Returns true iff the symbol is a variable symbol */
    def isTermVariable: Boolean = getSymType == TermVariable
    /** Returns true iff the symbol is a type variable symbol */
    def isTypeVariable: Boolean = getSymType == TypeVariable
  }

  /** Entry type for meta information for (possibly uninterpreted) constant symbols */
  abstract class ConstMeta extends Meta[ConstKey] {
    /** Returns true iff the constant has a type associated with it */
    def hasType: Boolean
    /** Returns true iff the constant has a definition term associated with it */
    def hasDefn: Boolean


    /** Returns true iff the symbol is a fixed (interpreted) symbol */
    def isFixed: Boolean = getSymType == Fixed
    /** Returns true iff the symbol is a defined symbol */
    def isDefined: Boolean = getSymType == Defined
    /** Returns true iff the symbol is a uninterpreted symbol */
    def isUninterpreted: Boolean = getSymType == Uninterpreted
    /** Returns true iff the symbol is a type symbol */
    def isType: Boolean = getSymType == BaseType
  }

  // method names and parameters will change!

  protected def addVariable0(identifier: String, typ: Option[Type]):                     VarKey
  protected def addConstant0(identifier: String, typ: Option[Type], defn: Option[Term]): ConstKey

  // Utility for variable symbols

  def addVariable(identifier: String, typ: Type): VarKey = addVariable0(identifier, Some(typ))
  def addVariable(identifier: String):            VarKey = addVariable0(identifier, None)

  def getVarMeta(key: VarKey):             VarMeta
  def getVarMeta(identifier: String):      VarMeta

  def isVariable(key: VarKey):             Boolean = getVarMeta(key).isTermVariable
  def isVariable(identifier: String):      Boolean = getVarMeta(identifier).isTermVariable
  def isTypeVariable(key: VarKey):         Boolean = getVarMeta(key).isTypeVariable
  def isTypeVariable(identifier: String):  Boolean = getVarMeta(identifier).isTypeVariable


  // Utility for constant symbols

  def addConstant(identifier: String, defn: Term, typ: Type): ConstKey = addConstant0(identifier, Some(typ), Some(defn))
  def addConstant(identifier: String, defn: Term):            ConstKey = addConstant0(identifier, None, Some(defn))
  def addUninterpreted(identifier: String, typ: Type):        ConstKey = addConstant0(identifier, Some(typ), None)
  def addBaseType(typ: String):                               ConstKey = addUninterpreted(typ, Type.getBaseKind)

  def getConstMeta(key: ConstKey):         ConstMeta
  def getConstMeta(identifier: String):    ConstMeta

  def isDefinedSymbol(identifier: String): Boolean = getConstMeta(identifier).isDefined
  def isFixedSymbol(identifier: String):   Boolean = getConstMeta(identifier).isFixed
  def isUninterpreted(identifier: String): Boolean = getConstMeta(identifier).isUninterpreted
  def isBaseType(identifier: String):      Boolean = getConstMeta(identifier).isType


  def getAllVariables:                     Set[VarKey]
  def getTermVariables:                    Set[VarKey]
  def getTypeVariables:                    Set[VarKey]

  def getAllConstants:                     Set[ConstKey]
  def getBaseTypes:                        Set[ConstKey]
  def getDefinedSymbols:                   Set[ConstKey]
  def getFixedDefinedSymbols:              Set[ConstKey]
  def getUninterpretedSymbols:             Set[ConstKey]
}