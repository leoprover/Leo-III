package datastructures.internal

/**
 * Abstract type for modeling types
 *
 * @author Alexander Steen
 * @since 30.04.2014
 */
sealed abstract class Type

/**
 * Type of a polymorphic function
 * @param typeVar The type variable that is introduced to the type in `in`
 * @param in The type that can now use the type variable `typeVar` as bound type variable
 */
case class ForallType(typeVar: TypeVar, in: Type) extends Type

/**Constructor for a function type `in -> out` */
case class FunType(in: Type, out: Type) extends Type

/** Application of type arguments to a constructor, yielding a Type
 * @deprecated Not yet implemented, but planned for future use */
case class ContructorAppType(typeCon: TypeConstructor, args: List[Type]) extends Type

/** Type of a (bound) type variable when itself used as type in polymorphic function */
case class TypeVarType(typeVar: TypeVar) extends Type

/** Literal type, i.e. `$o` */
case class BaseType(name: String) extends Type // string???

/** Abstract type of kinds (i.e. types of types).
  * The `Kind` class extends the `Type` class to allow unified handling */
sealed abstract class Kind extends Type
/** Represents the kind `*` (i.e. the type of a type) */
case object KindType extends Kind
/** Constructs function kinds.
  * Invariant: at least one of `in` or `out` must (transitively) contain `TypeKind`,
  * otherwise it's a `FunType`. */
case class FunKind(in: Type, out: Type) extends Kind


/** For future use: Type constructor representation */
sealed abstract class TypeConstructor
