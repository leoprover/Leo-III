package leo.datastructures

/**
 * Interface for signature representations.
 * The signature table can contain types, variables, and constants (which may be uninterpreted symbols or defined symbols).
 * Type variables will follow in next revision.
 *
 * @author Alexander Steen
 * @since 29.04.2014
 * @note Updated 02.06.2014 Signature does not track variables names anymore, they are parsed to nameless terms instead
 */
trait IsSignature {
  type Key

  type TypeOrKind = Either[Type, Kind]

  ///////////////////////////////
  // Symbol types
  ///////////////////////////////

  /** Type of symbols in the signature table */
  sealed abstract class SymbolType {
    /** Returns true iff type `this` is equal to `typ` */
    def is(typ: SymbolType): Boolean = this.equals(typ)
  }

  /** `SymbolType` of a fixed symbol, e.g. `$true: $o` or `$false: $o` */
  case object Fixed extends SymbolType
  /** `SymbolType` of a defined symbol, e.g. `&: $o -> $o -> $o`  */
  case object Defined extends SymbolType
  /** `SymbolType` of a uninterpreted symbol, e.g. `p: $i -> $o`*/
  case object Uninterpreted extends SymbolType
  /** `SymbolType` of a base type symbol, e.g. `$o: *` */
  case object BaseType extends SymbolType
  /** `SymbolType` of a constructor symbol, e.g. 'map: * -> * -> * */
  case object TypeConstructor extends SymbolType

  ///////////////////////////////
  // Meta information
  ///////////////////////////////

  /**
   * Entry base class for meta information saved along with symbols in the signature table.
   */
  abstract class Meta {
    /** The name of the symbol (i.e. string representation of it) */
    def name: String
    /** The key of type `Key` that is used as table key for that entry */
    def key: Key
    /** The type of symbol the entry describes */
    def symType: SymbolType
    /** Returns the type saved with the symbol if any, `None` otherwise */
    def ty: Option[Type]
    /** Unsafe variant of `getType`.
      * @throws NoSuchElementException if no type is available
      */
    def _ty: Type = ty.get
    def kind: Option[Kind]
    def _kind: Kind = kind.get
    /** Returns the definition saved with the symbol if any, `None` otherwise */
    def defn: Option[Term]
    /**
     * Unsafe variant of `getDefn`. Gets the definition term directly
     * @throws NoSuchElementException if no definition is available
     */
    def _defn: Term = defn.get
    /** Returns the status of the symbol, where 0 = mult, 1 = lex.*/
    def status: Int

    /** Returns true iff the symbol has a type associated with it */
    def hasType: Boolean
    /** Returns true iff the symbol has a kind associated with it */
    def hasKind: Boolean
    /** Returns true iff the constant has a definition term associated with it */
    def hasDefn: Boolean
    /** Returns true iff the symbol has lex status. */
    def hasLexStatus: Boolean = status == 1
    /** Returns true iff the symbol has mult status. */
    def hasMultStatus: Boolean = status == 0


    /** Returns true iff the symbol is a fixed (interpreted) symbol */
    def isFixed: Boolean         = symType == Fixed
    /** Returns true iff the symbol is a defined symbol */
    def isDefined: Boolean       = symType == Defined
    /** Returns true iff the symbol is a uninterpreted symbol */
    def isUninterpreted: Boolean = symType == Uninterpreted
    /** Returns true iff the symbol is a type symbol */
    def isType: Boolean          = symType == BaseType
    /** Returns true iff the symbol is a type symbol */
    def isTypeConstructor: Boolean          = symType == TypeConstructor
  }


  ///////////////////////////////
  // Maintenance methods for the signature
  ///////////////////////////////

  /** Multi-purpose method for adding all types of constant symbols (i.e. defined, uninterpreted, types).
    * The actual symbol type is determined by the typ and whether a definition is supplied.
    * If some definition is passed, the constant is a `Defined`, otherwise it is a `Basetype` or a `Uninterpreted`
    * (depending on the type supplied).
    * `status` represents the status of the constant symbol, where 0 stands for multiset status and 1 for lexicographic
    * status.
    *
    * @return the generated key that the freshly added symbol is indexed by
    * @throws IllegalArgumentException if the symbol described by `identifier` already exists or a
    *                                  illegal typ/definition combination is supplied
    */
  protected def addConstant0(identifier: String, typ: Option[TypeOrKind], defn: Option[Term], status: Int): Key
  /** Removes the symbol indexed by `key` it it exists; does nothing otherwise.
    * @return `true` if `key` was associated to a symbol (that got removed), false otherwise
    */
  def remove(key: Key): Boolean
  /** Returns true iff a indexed constant symbol with name `identifier` exists */
  def exists(identifier: String): Boolean

  /** Returns the symbol type of  `identifier`, if it exists in signature */
  def symbolType(identifier: String): SymbolType
  /** Returns the symbol type of  `identifier`, if it exists in signature */
  def symbolType(identifier: Key): SymbolType

  ///////////////////////////////
  // Utility methods
  ///////////////////////////////

  /** Adds a defined constant with type `typ` to the signature.
    * @return The key the symbol is indexed by
    */
  def addDefined(identifier: String, defn: Term, typ: Type): Key = addConstant0(identifier, Some(Left(typ)), Some(defn), 0)
  /** Adds a term variable without type to the signature.
    * @return The key the symbol is indexed by
    */
  def addDefined(identifier: String, defn: Term): Key            = addConstant0(identifier, None, Some(defn), 0)
  /** Adds an uninterpreted constant with type `typ` to the signature,
    * multiset status is default, but can be overridden by `status` parameter.
    * @return The key the symbol is indexed by
    */
  def addUninterpreted(identifier: String, typ: Type, status: Int = 0): Key       = addConstant0(identifier, Some(Left(typ)), None, status)
  /** Adds an uninterpreted constant with kind `k` to the signature.
    * @return The key the symbol is indexed by
    */
  def addTypeConstructor(identifier: String, k: Kind): Key         = addConstant0(identifier, Some(Right(k)), None, -1)
  /** Adds a base type constant (i.e. of type `*`) to the signature.
    * @return The key the symbol is indexed by
    */
  def addBaseType(identifier: String): Key                       = addConstant0(identifier, Some(Right(Type.typeKind)), None, -1)

  /** If the symbol indexed by `key` is a uninterpreted symbol, then `addDefinition(key, defn)` turns this symbol
    * into a defined symbol with definition `defn`.*/
  def addDefinition(key: Key, defn: Term): Key

  /** Returns the meta information stored under key `key`*/
  def meta(identifier: Key): Meta
  /** Returns the meta information stored with symbol with id `identifier`
    * @throws IllegalArgumentException  if the symbol described by `identifier` does not exist in the signature */
  def meta(identifier: String): Meta

  /** Returns true iff the symbol index by `key` is a defined symbol */
  def isDefinedSymbol(key: Key): Boolean = meta(key).isDefined
  /** Returns true iff the symbol index by `key` is a fixed symbol */
  def isFixedSymbol(key: Key): Boolean   = meta(key).isFixed
  /** Returns true iff the symbol index by `key` is a uninterpreted symbol */
  def isUninterpreted(key: Key): Boolean = meta(key).isUninterpreted
  /** Returns true iff the symbol index by `key` is a base type symbol */
  def isBaseType(key: Key): Boolean      = meta(key).isType
  /** Returns true iff the symbol index by `key` is a type operator (constructor) symbol */
  def isTypeConstructor(key: Key): Boolean      = meta(key).isTypeConstructor

  /** Empty the signature (deletes all symbols from signature and resets all indexing key counters. */
  def empty(): Unit

  ///////////////////////////////
  // Dumping of indexed symbols
  ///////////////////////////////

  /** Returns a set of all indexed constants keys */
  def allConstants: Set[Key]
  /** Returns a set of all indexed constants that are supplied by the user/problem. */
  def allUserConstants: Set[Key]
  /** Returns a set of all indexed constants with given type*/
  def constantsOfType(ty: Type): Set[Key] = allConstants.filter(meta(_).ty match {
    case None => false
    case Some(t) => t == ty
  })
  /** Returns a set of all indexed fixed constants keys */
  def fixedSymbols: Set[Key]
  /** Returns a set of all indexed fixed constants with given type */
  def fixedSymbolsOfType(ty: Type): Set[Key] = fixedSymbols.filter(meta(_).ty match {
    case None => false
    case Some(t) => t == ty
  })

  /** Returns a set of all indexed defined constants keys */
  def definedSymbols: Set[Key]
  /** Returns a set of all indexed defined constants with given type */
  def definedSymbolsOfType(ty: Type): Set[Key] = definedSymbols.filter(meta(_).ty match {
    case None => false
    case Some(t) => t == ty
  })
  /** Returns a set of all indexed uninterpreted constants keys */
  def uninterpretedSymbols: Set[Key]
  /** Returns a set of all indexed uninterpreted constants with given type */
  def uninterpretedSymbolsOfType(ty: Type): Set[Key] = uninterpretedSymbols.filter(meta(_).ty match {
    case None => false
    case Some(t) => t == ty
  })
  /** Returns a set of all indexed base type constants keys */
  def baseTypes: Set[Key]


  ///////////////////////////////
  // Creating of fresh variables
  ///////////////////////////////

  /** Create fresh uninterpreted symbol of type `ty` */
  def freshSkolemVar(ty: Type): Key
  /** Create fresh base type symbol */
  def freshTypeVar: Key
}