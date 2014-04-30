package datastructures.internal

/**
 * Interface for signature representations.
 * The signature table can contain types, variables, and constants (which may be uninterpreted symbols or defined symbols).
 *
 * @author Alexander Steen
 * @since 29.04.2014
 */
trait IsSignature {
  /** This type is used as indexing key for base types in the underlying dictionary implementation */
  type TypeKey
  /** This type is used as indexing key for variables in the underlying dictionary implementation */
  type VarKey
  /** This type is used as indexing key for (possibly uninterpreted) constant symbols in the underlying dictionary implementation */
  type ConstKey

  /** Type of symbols in the signature table */
  sealed abstract class SymbolType
  /** A base type symbol, e.g. `$o` */
  case object BaseType extends SymbolType
  /** A variable symbol, e.g. `X` */
  case object Variable extends SymbolType
  /** A fixed symbol, e.g. `$true` or `$false` */
  case object Fixed extends SymbolType
  case object Defined extends SymbolType
  case object Uninterpreted extends SymbolType

  /**
   * Entry base class for Meta information saved along with symbols in signature table
   * @param name    The name of the symbol (i.e. string representation of it)
   * @param key     The key of type `Key` that is used as table key for that entry
   * @param symType The type of symbol the entry describes
   * @tparam Key    The type of key that is used to save the `Meta` in the signature table
   */
  sealed abstract class Meta[+Key](val name: String,
                                   val key: Key,
                                   val symType: SymbolType)


  /** Case class for meta information for base types that are indexed in the signature */
  case class TypeMeta(override val name: String,
                      override val key: TypeKey,
                      typeRep: Type)
    extends Meta[TypeKey](name, key, BaseType)


  /** Case class for meta information for variables that are indexed in the signature */
  case class VarMeta(override val name: String,
                     override val key: VarKey,
                     typ: Option[Type])
    extends Meta[VarKey](name, key, Variable) {
      def hasType: Boolean = typ.isDefined
  }

  /** Case class for meta information for (possibly uninterpreted) constant symbols that are indexed in the signature */
  protected sealed abstract class ConstMeta(override val name: String,
                                            override val key: ConstKey,
                                            override val symType: SymbolType,
                                            typ: Option[Type],
                                            defn: Option[Term])
    extends Meta[ConstKey](name, key, symType) {
      def hasType: Boolean = typ.isDefined

      def isUninterpreted: Boolean
      def isDefinedConstant: Boolean
      def isFixed: Boolean
  }

  case class UninterpretedMeta(override val name: String, override val key: ConstKey, typ: Type) extends ConstMeta(name, key, Uninterpreted, Some(typ), None) {
   def isUninterpreted = true
   def isDefinedConstant = false
   def isFixed = false
  }

  case class DefinedMeta(override val name: String, override val key: ConstKey, typ: Option[Type], defn: Term) extends ConstMeta(name, key, Defined, typ, Some(defn)) {
    def isUninterpreted = false
    def isDefinedConstant = true
    def isFixed = false
  }

  case class FixedMeta(override val name: String, override val key: ConstKey, typ: Type) extends ConstMeta(name, key, Fixed, Some(typ), None) {
    def isUninterpreted = false
    def isDefinedConstant = false
    def isFixed = true
  }

  def addBaseType(typ: String): TypeKey
  def isBaseType(typ: String): Boolean
  def getTypeMeta(typ: String): TypeMeta
  def getTypeMeta(key: TypeKey): TypeMeta

  protected def addVariable0(identifier: String, typ: Option[Type]): VarKey

  def addVariable(identifier: String, typ: Type): VarKey =
    addVariable0(identifier, Some(typ))
  def addVariable_(identifier: String): VarKey =
    addVariable0(identifier, None)
  def isVariable(identifier: String): Boolean
  def getVarMeta(key: VarKey): VarMeta

  protected def addConstant0(identifier: String, typ: Option[Type], defn: Option[Term]): ConstKey

  def addConstant(identifier: String, typ: Type, defn: Term): ConstKey =
    addConstant0(identifier, Some(typ), Some(defn))

  def addConstant_(identifier: String, defn: Term): ConstKey =
    addConstant0(identifier, None, Some(defn))
  def isDefinedSymbol(identifier: String): Boolean

  def addUninterpreted(identifier: String, typ: Type): ConstKey =
    addConstant0(identifier, Some(typ), None)
  def isUninterpreted(identifier: String): Boolean
  def getConstMeta(key: ConstKey): ConstMeta
  def getConstMeta(identifier: String): ConstMeta

  def getBaseTypes: Set[TypeMeta]
  def getVariables: Set[VarMeta]
  def getAllConstants: Set[ConstMeta]
  def getDefinedSymbols: Set[ConstMeta]
  def getFixedDefinedSymbols: Set[ConstMeta]
  def getUninterpretedSymbols: Set[ConstMeta]
}