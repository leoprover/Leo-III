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

  ///////////////////////////////
  // Symbol types
  ///////////////////////////////

  /** Type of symbols in the signature table */
  sealed abstract class SymbolType {
    /** Returns true iff type `this` is equal to `typ` */
    def is(typ: SymbolType): Boolean = this.equals(typ)
  }

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

  ///////////////////////////////
  // Meta information
  ///////////////////////////////

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
    def isFixed: Boolean         = getSymType == Fixed
    /** Returns true iff the symbol is a defined symbol */
    def isDefined: Boolean       = getSymType == Defined
    /** Returns true iff the symbol is a uninterpreted symbol */
    def isUninterpreted: Boolean = getSymType == Uninterpreted
    /** Returns true iff the symbol is a type symbol */
    def isType: Boolean          = getSymType == BaseType
  }

  ///////////////////////////////
  // Maintenance methods for the signature
  ///////////////////////////////

  /** Multi-purpose method for adding all types of variables (i.e. term variables and type variables).
    * The actual type of variable is determined by the `typ` parameter. It no type is given (i.e.
    * `typ = None`), it is assumed that a term variable is added.
    *
    * @return the generated key that the freshly added symbol is indexed by
    * @throws IllegalArgumentException if the symbol described by `identifier` already exists in the signature
    */
  protected def addVariable0(identifier: String, typ: Option[Type]): VarKey
  /** Removes the symbol indexed by `key` it it exists; does nothing otherwise.
    * @return `true` if `key` was associated to a symbol (that got removed), false otherwise
    */
  def removeVariable(key: VarKey): Boolean
  /** Returns true iff a indexed variable with name `identifier` exists */
  def varExists(identifier: String): Boolean

  /** Multi-purpose method for adding all types of constant symbols (i.e. defined, uninterpreted, types).
    * The actual symbol type is determined by the typ and whether a definition is supplied.
    * If some definition is passed, the constant is a `Defined`, otherwise it is a `Basetype` or a `Uninterpreted`
    * (depending on the type supplied).
    *
    * @return the generated key that the freshly added symbol is indexed by
    * @throws IllegalArgumentException if the symbol described by `identifier` already exists or a
    *                                  illegal typ/definition combination is supplied
    */
  protected def addConstant0(identifier: String, typ: Option[Type], defn: Option[Term]): ConstKey
  /** Removes the symbol indexed by `key` it it exists; does nothing otherwise.
    * @return `true` if `key` was associated to a symbol (that got removed), false otherwise
    */
  def removeConstant(key: ConstKey): Boolean
  /** Returns true iff a indexed constant symbol with name `identifier` exists */
  def constExists(identifier: String): Boolean


  /** Returns true iff some indexed symbol with name `identifier` exists */
  def symbolExists(identifier: String): Boolean
  /** Returns the symbol type of  `identifier`, if it exists in signature */
  def symbolType(identifier: String): SymbolType

  ///////////////////////////////
  // Utility methods for variable symbols
  ///////////////////////////////

  /** Add a variable of type (or kind) `typ` to the signature.
    * @return The key the symbol is indexed by
    */
  def addVariable(id: String, typ: Type): VarKey = addVariable0(id, Some(typ))
  /** Adds a term variable to the signature.
    * @return The key the symbol is indexed by
    */
  def addVariable(id: String): VarKey            = addVariable0(id, None)

  /** Returns the meta information stored under key `key`*/
  def getVarMeta(key: VarKey): VarMeta
  /** Returns the meta information stored with symbol with id `identifier`
    * @throws IllegalArgumentException  if the symbol described by `identifier` does not exist in the signature */
  def getVarMeta(identifier: String): VarMeta

  /** Returns true iff the symbol index by `key` is a term variable */
  def isTermVariable(key: VarKey): Boolean = getVarMeta(key).isTermVariable
  /** Returns true iff the symbol index by `key` is a type variable */
  def isTypeVariable(key: VarKey): Boolean = getVarMeta(key).isTypeVariable

  ///////////////////////////////
  // Utility methods for constant symbols
  ///////////////////////////////

  /** Adds a defined constant with type `typ` to the signature.
    * @return The key the symbol is indexed by
    */
  def addDefined(identifier: String, defn: Term, typ: Type): ConstKey = addConstant0(identifier, Some(typ), Some(defn))
  /** Adds a term variable without type to the signature.
    * @return The key the symbol is indexed by
    */
  def addDefined(identifier: String, defn: Term): ConstKey            = addConstant0(identifier, None, Some(defn))
  /** Adds an uninterpreted constant with type `typ` to the signature.
    * @return The key the symbol is indexed by
    */
  def addUninterpreted(identifier: String, typ: Type): ConstKey       = addConstant0(identifier, Some(typ), None)
  /** Adds a base type constant (i.e. of type `*`) to the signature.
    * @return The key the symbol is indexed by
    */
  def addBaseType(identifier: String): ConstKey                       = addUninterpreted(identifier, Type.getBaseKind)

  /** Returns the meta information stored under key `key`*/
  def getConstMeta(key: ConstKey): ConstMeta
  /** Returns the meta information stored with symbol with id `identifier`
    * @throws IllegalArgumentException  if the symbol described by `identifier` does not exist in the signature */
  def getConstMeta(identifier: String): ConstMeta

  /** Returns true iff the symbol index by `key` is a defined symbol */
  def isDefinedSymbol(key: ConstKey): Boolean = getConstMeta(key).isDefined
  /** Returns true iff the symbol index by `key` is a fixed symbol */
  def isFixedSymbol(key: ConstKey): Boolean   = getConstMeta(key).isFixed
  /** Returns true iff the symbol index by `key` is a uninterpreted symbol */
  def isUninterpreted(key: ConstKey): Boolean = getConstMeta(key).isUninterpreted
  /** Returns true iff the symbol index by `key` is a base type symbol */
  def isBaseType(key: ConstKey): Boolean      = getConstMeta(key).isType

  ///////////////////////////////
  // Dumping of indexed symbols
  ///////////////////////////////

  /** Returns a set of all indexed variable keys */
  def getAllVariables: Set[VarKey]
  /** Returns a set of all indexed term variable keys */
  def getTermVariables: Set[VarKey]
  /** Returns a set of all indexed type variable keys */
  def getTypeVariables: Set[VarKey]

  /** Returns a set of all indexed constants keys */
  def getAllConstants: Set[ConstKey]
  /** Returns a set of all indexed fixed constants keys */
  def getFixedDefinedSymbols: Set[ConstKey]
  /** Returns a set of all indexed defined constants keys */
  def getDefinedSymbols: Set[ConstKey]
  /** Returns a set of all indexed uninterpreted constants keys */
  def getUninterpretedSymbols: Set[ConstKey]
  /** Returns a set of all indexed base type constants keys */
  def getBaseTypes: Set[ConstKey]
}