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
trait Signature {
  type Key = Int

  type TypeOrKind = Either[Type, Kind]

  ///////////////////////////////
  // Meta information
  ///////////////////////////////

  /**
   * Entry base class for meta information saved along with symbols in the signature table.
   */
  abstract class Meta {
    import Signature.SymbProp
    // Key information about the symbol
    /** The name of the symbol (i.e. string representation of it) */
    def name: String
    /** The key of type [[Key]] that is used as table key for that entry */
    def key: Key
    /** Flag holding additional information in bit-encoding */
    def flag: SymbProp
    // Additional information about the symbol
    /** Returns the type saved with the symbol if any, [[None]] otherwise */
    def ty: Option[Type]
    /** Unsafe variant of [[ty]].
      * @throws NoSuchElementException if no type is available
      */
    def _ty: Type = ty.get

    /** Returns the kind saved with the symbol if any, [[None]] otherwise */
    def kind: Option[Kind]
    /** Unsafe variant of [[kind]].
      * @throws NoSuchElementException if no type is available
      */
    def _kind: Kind = kind.get

    /** Returns the definition saved with the symbol if any, [[None]] otherwise */
    def defn: Option[Term]
    /**
     * Unsafe variant of [[defn]]. Gets the definition term directly
     * @throws NoSuchElementException if no definition is available
     */
    def _defn: Term = defn.get

    // Query functions
    /** Update the `flag` property of the underlying meta. */
    def updateProp(newProp: SymbProp): Unit
    /** Returns the status of the symbol, where 0 = mult, 1 = lex.*/
    lazy val status: Int = if (isPropSet(Signature.PropStatus, flag)) Signature.lexStatus else Signature.multStatus

    /** Returns true iff the symbol has a type associated with it */
    @inline final def hasType: Boolean = ty.isDefined
    /** Returns true iff the symbol has a kind associated with it */
    @inline final def hasKind: Boolean = kind.isDefined
    /** Returns true iff the constant has a definition term associated with it */
    @inline final def hasDefn: Boolean = defn.isDefined
    /** Returns true iff the symbol has lex status. */
    @inline final def hasLexStatus: Boolean = isPropSet(Signature.PropStatus, flag)
    /** Returns true iff the symbol has mult status. */
    @inline final def hasMultStatus: Boolean = !isPropSet(Signature.PropStatus, flag)


    /** Returns true iff the symbol is a primitive (interpreted) symbol provided by the system */
    @inline final def isPrimitive: Boolean     = isPropSet(Signature.PropFixed, flag) && !hasDefn
    /** Returns true iff the symbol is a symbol provided by the system */
    @inline final def isFixedSymbol: Boolean  = isPropSet(Signature.PropFixed, flag)
    /** Returns true iff the symbol is a user provided symbol */
    @inline final def isUserSymbol: Boolean    = !isPropSet(Signature.PropFixed, flag)
    /** Returns true iff the symbol is a defined symbol */
    @inline final def isDefined: Boolean          = hasDefn
    /** Returns true iff the symbol is an uninterpreted term symbol */
    @inline final def isUninterpreted: Boolean    = !isPropSet(Signature.PropFixed, flag) && !hasDefn
    /** Returns true iff the symbol refers to an external object */
    @inline final def isExternal: Boolean    = isPropSet(Signature.PropExternal, flag)

    /** Returns true iff the symbol is associative */
    @inline final def isASymbol: Boolean    = isPropSet(Signature.PropAssociative, flag)
    /** Returns true iff the symbol is commutative */
    @inline final def isCSymbol: Boolean    = isPropSet(Signature.PropCommutative, flag)
    /** Returns true iff the symbol is associative */
    @inline final def isACSymbol: Boolean    = isPropSet(Signature.PropAssociative | Signature.PropCommutative, flag)

    /** Returns true iff the symbol is a term symbol */
    @inline final def isTermSymbol: Boolean             = hasType
    /** Returns true iff the symbol is a type constructor symbol */
    @inline final def isTypeConstructor: Boolean  = hasKind
    /** Returns true iff the symbol is a type symbol */
    @inline final def isType: Boolean             = isTypeConstructor && _kind.isTypeKind
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
  protected def addConstant0(identifier: String, typ: TypeOrKind, defn: Option[Term], prop: Signature.SymbProp): Key
  /** Removes the symbol indexed by `key` it it exists; does nothing otherwise.
    * @return `true` if `key` was associated to a symbol (that got removed), false otherwise
    */
  def remove(key: Key): Boolean
  /** Returns true iff a indexed constant symbol with name `identifier` exists */
  def exists(identifier: String): Boolean

  ///** Returns the symbol type of  `identifier`, if it exists in signature */
  //def symbolType(identifier: String): SymbolType
  ///** Returns the symbol type of  `identifier`, if it exists in signature */
  //def symbolType(identifier: Key): SymbolType

  ///////////////////////////////
  // Utility methods
  ///////////////////////////////

  /** Adds a defined constant with type `typ` to the signature.
    * @return The key the symbol is indexed by
    */
  final def addDefined(identifier: String, defn: Term, typ: Type, prop: Signature.SymbProp = Signature.PropNoProp): Key = addConstant0(identifier, Left(typ), Some(defn), prop)
  /** Adds an uninterpreted constant with type `typ` to the signature,
    * multiset status is default, but can be overridden by `status` parameter.
    * @return The key the symbol is indexed by
    */
  final def addUninterpreted(identifier: String, typ: Type, prop: Signature.SymbProp = Signature.PropNoProp): Key       = addConstant0(identifier, Left(typ), None, prop)
  /** Adds an uninterpreted constant with kind `k` to the signature.
    * @return The key the symbol is indexed by
    */
  final def addTypeConstructor(identifier: String, k: Kind): Key         = addConstant0(identifier, Right(k), None, -1)
  /** Adds a base type constant (i.e. of type `*`) to the signature.
    * @return The key the symbol is indexed by
    */
  final def addBaseType(identifier: String): Key                       = addConstant0(identifier, Right(Type.typeKind), None, -1)

  /** If the symbol indexed by `key` is a uninterpreted symbol, then `addDefinition(key, defn)` turns this symbol
    * into a defined symbol with definition `defn`.*/
  def addDefinition(key: Key, defn: Term): Key

  /** Shorthand for meta */
  final def apply(identifier: String): Meta = meta(identifier)
  /** Shorthand for meta */
  final def apply(identifier: Key): Meta = meta(identifier)
  /** Returns the meta information stored under key `key`*/
  def meta(identifier: Key): Meta
  /** Returns the meta information stored with symbol with id `identifier`
    * @throws IllegalArgumentException  if the symbol described by `identifier` does not exist in the signature */
  def meta(identifier: String): Meta

  /** Returns true iff the symbol indexed by `key` is a primitive symbol of the system,
    * i.e. not having a definition. */
  final def isPrimitiveSymbol(key: Key): Boolean   = meta(key).isFixedSymbol && !meta(key).isDefined
  /** Returns true iff the symbol indexed by `key` is a symbol provided by the system  */
  final def isFixedSymbol(key: Key): Boolean   = meta(key).isFixedSymbol
  /** Returns true iff the symbol was provided by the user. */
  final def isUserSymbol(key: Key): Boolean = meta(key).isUserSymbol
  /** Returns true iff the symbol indexed by `key` has a definition */
  final def isDefinedSymbol(key: Key): Boolean = meta(key).isDefined
  /** Returns true iff the symbol indexed by `key` is an uninterpreted term symbol */
  final def isUninterpretedSymbol(key: Key): Boolean = meta(key).isUninterpreted
  /** Returns true iff the symbol is associative */
  final def isASymbol(key: Key): Boolean = meta(key).isASymbol
  /** Returns true iff the symbol is commutative */
  final def isCSymbol(key: Key): Boolean = meta(key).isCSymbol
  /** Returns true iff the symbol is associative and commutative */
  final def isACSymbol(key: Key): Boolean = meta(key).isACSymbol

  /** Returns true iff the symbol indexed by `key` is a type symbol */
  final def isType(key: Key): Boolean      = meta(key).isType
  /** Returns true iff the symbol indexed by `key` is a type operator (constructor) symbol */
  final def isTypeConstructor(key: Key): Boolean      = meta(key).isTypeConstructor

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
  final def constantsOfType(ty: Type): Set[Key] = allConstants.filter(meta(_).ty match {
    case None => false
    case Some(t) => t == ty
  })

  /** Returns a set of all primtive symbols. */
  def primitiveSymbols: Set[Key]
  /** Returns a set of all indexed fixed constants keys */
  def fixedSymbols: Set[Key]
  /** Returns a set of all indexed fixed constants with given type */
  final def fixedSymbolsOfType(ty: Type): Set[Key] = fixedSymbols.filter(meta(_).ty match {
    case None => false
    case Some(t) => t == ty
  })

  /** Returns a set of all indexed defined constants keys */
  def definedSymbols: Set[Key]
  /** Returns a set of all indexed defined constants with given type */
  final def definedSymbolsOfType(ty: Type): Set[Key] = definedSymbols.filter(meta(_).ty match {
    case None => false
    case Some(t) => t == ty
  })

  /** Returns a set of all indexed uninterpreted constants keys */
  def uninterpretedSymbols: Set[Key]
  /** Returns a set of all indexed uninterpreted constants with given type */
  final def uninterpretedSymbolsOfType(ty: Type): Set[Key] = uninterpretedSymbols.filter(meta(_).ty match {
    case None => false
    case Some(t) => t == ty
  })

  /** Returns a set of all associative symbols */
  def aSymbols: Set[Key]
  /** Returns a set of all commutative symbols */
  def cSymbols: Set[Key]
  /** Returns a set of all AC symbols */
  final def acSymbols: Set[Key] = aSymbols & cSymbols

  def typeConstructors: Set[Key]
  /** Returns a set of all indexed base type constants keys */
  final def typeSymbols: Set[Key] = typeConstructors.filter(isType)

  ///////////////////////////////
  // Creating of fresh variables
  ///////////////////////////////

  /** Create fresh uninterpreted symbol of type `ty` */
  def freshSkolemConst(ty: Type, prop: Signature.SymbProp = Signature.PropNoProp): Key
  /** Create fresh base type symbol */
  def freshSkolemTypeConst(k: Kind): Key
}

object Signature {
  type SymbProp = Int
  final val PropNoProp: SymbProp = 0
  final val PropStatus: SymbProp = 1 /* Lexstatus if set. Multstatus otherwise */
  final val PropAssociative: SymbProp = 2
  final val PropCommutative: SymbProp = 4
  final val PropAC: SymbProp = PropAssociative | PropCommutative
  final val PropFixed: SymbProp = 8 /* Fixed term of type symbol of the system */
  final val PropExternal: SymbProp = 16 /* Term or type symbol that refers to a external object */
  final val PropSkolemConstant: SymbProp = 32 /* Term symbol that was introduced within skolemization */
  final val PropChoice: SymbProp = 64 /* The symbol is a choice function. */

  final val multStatus: Int = 0
  final val lexStatus: Int = 1

  ////////////////////
  // Default implementation, SignatureImpl
  ///////////////////
  import leo.datastructures.impl.{SignatureImpl => Impl}

  final def fresh(): Signature = Impl.empty
  final def freshWithHOL(): Signature = {
    import leo.modules.HOLSignature
    val sig = Impl.empty
    for ((name, k) <- HOLSignature.types) {
      sig.addFixedTypeConstructor(name, k)
    }

    for ((name, ty, flag) <- HOLSignature.fixedConsts) {
      sig.addFixed(name, ty, None, flag | Signature.PropFixed)
    }

    for ((name, fed, ty, flag) <- HOLSignature.definedConsts) {
      sig.addFixed(name, ty, Some(fed), flag | Signature.PropFixed)
    }
    sig
  }

}