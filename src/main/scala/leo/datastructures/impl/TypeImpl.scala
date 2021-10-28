package leo.datastructures.impl

import leo.datastructures.{Kind, Subst, Type, TypeFront, Signature}

protected[datastructures] sealed abstract class TypeImpl extends Type {
  def splitFunParamTypesAt(n: Int): (Seq[Type], Type) = splitFunParamTypesAt0(n, Vector.empty)
  protected[impl] def splitFunParamTypesAt0(n: Int, acc: Seq[Type]): (Seq[Type], Type) = if (n == 0) (acc, this) else
    throw new UnsupportedOperationException("splitFunParamTypesAt0 with non-zero n on non-Function type")
  // to be overridden by abstraction type below

  final def closure(subst: Subst): Type = substitute(subst)
  def instantiate(by: Seq[Type]): Type = this
  final def instantiate(by: Type): Type =  instantiate(Seq(by))
  def monomorphicBody: Type = this
}

/** Ground type, e.g. `$o` or `list @ $i`. */
protected[datastructures] final case class GroundTypeNode(id: Signature.Key, args: Seq[Type]) extends TypeImpl {
  // Pretty printing
  def pretty: String = if (args.isEmpty) s"ty($id)" else s"ty($id)(${args.map(_.pretty).mkString(",")})"
  def pretty(sig: Signature): String = if (args.isEmpty) sig(id).name else s"${sig(id).name}(${args.map(_.pretty(sig)).mkString(",")})"

  // Predicates on types
  def isBaseType: Boolean = args.isEmpty
  def isComposedType: Boolean = args.nonEmpty
  def isFunType: Boolean = false
  def isProdType: Boolean = false
  def isPolyType: Boolean = false
  def isBoundTypeVar: Boolean = false
  def isApplicableWith(arg: Type): Boolean = false

  // Queries on types
  lazy val typeVars: Set[Type] = args.flatMap(_.typeVars).toSet
  def symbols: Set[Signature.Key] = Set(id)

  def funDomainType: Option[Type] = None
  def codomainType: Type = this
  def arity: Int = 0
  def funParamTypesWithResultType: Seq[Type] = Vector(this)
  def order: Int = 0
  def polyPrefixArgsCount: Int = 0

  def app(ty: Type): Type = GroundTypeNode(id, args :+ ty)
  def occurs(ty: Type): Boolean = ty match {
    case GroundTypeNode(key, args2) if key == id => args == args2 || args.exists(_.occurs(ty))
    case _ => args.exists(_.occurs(ty))
  }

  // Substitutions
  def replace(what: Type, by: Type): Type = if (what == this) by else GroundTypeNode(id, args.map(_.replace(what, by)))

  def substitute(subst: Subst): Type = GroundTypeNode(id, args.map(_.substitute(subst)))
}

/** Type of a (bound) type variable when itself used as type in polymorphic function */
protected[datastructures] final case class BoundTypeNode(scope: Int) extends TypeImpl {
  // Pretty printing
  def pretty: String = scope.toString
  def pretty(sig: Signature): String = scope.toString

  // Predicates on types
  def isBaseType: Boolean = false
  def isComposedType: Boolean = false
  def isFunType: Boolean = false
  def isProdType: Boolean = false
  def isPolyType: Boolean = false
  def isBoundTypeVar: Boolean = true
  def isApplicableWith(arg: Type): Boolean = false

  // Queries on types
  def typeVars: Set[Type] = Set(this)
  def symbols: Set[Signature.Key] = Set.empty

  def funDomainType: Option[Type] = None
  def codomainType: Type = this
  def arity: Int = 0
  def funParamTypesWithResultType: Seq[Type] = Vector(this)
  def order: Int = 0
  def polyPrefixArgsCount: Int = 0

  def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to type variable")
  def occurs(ty: Type) = false

  // Substitutions
  def replace(what: Type, by: Type): Type = if (what == this) by else this
  import leo.datastructures.{BoundFront, TypeFront}
  def substitute(subst: Subst): Type = subst.substBndIdx(scope) match {
    case BoundFront(j) => BoundTypeNode(j)
    case TypeFront(t)  => t
    case _ => throw new IllegalArgumentException("type substitution contains terms")
  }
}

/** Function type `in -> out` */
protected[datastructures] final case class AbstractionTypeNode(in: Type, out: Type) extends TypeImpl {
  // Pretty printing
  def pretty: String = in match {
    case _:AbstractionTypeNode => s"(${in.pretty}) -> ${out.pretty}"
    case _              => s"${in.pretty} -> ${out.pretty}"
  }
  def pretty(sig: Signature): String = in match {
    case _:AbstractionTypeNode => s"(${in.pretty(sig)}) -> ${out.pretty(sig)}"
    case _:Type              => s"${in.pretty(sig)} -> ${out.pretty(sig)}"
  }

  // Predicates on types
  def isBaseType: Boolean = false
  def isComposedType: Boolean = false
  def isFunType: Boolean = true
  def isProdType: Boolean = false
  def isPolyType: Boolean = false
  def isBoundTypeVar: Boolean = false
  def isApplicableWith(arg: Type): Boolean = arg == in

  // Queries on types
  lazy val typeVars: Set[Type] = in.typeVars union out.typeVars
  lazy val symbols: Set[Signature.Key] = in.symbols union out.symbols

  def funDomainType: Option[Type] = Some(in)
  def codomainType: Type = out
  lazy val arity: Int = 1 + out.arity
  lazy val funParamTypesWithResultType: Seq[Type] = in +: out.funParamTypesWithResultType
  lazy val order: Int = Math.max(1+in.order,out.order)
  def polyPrefixArgsCount: Int = 0

  override protected[impl] def splitFunParamTypesAt0(n: Int, acc: Seq[Type]): (Seq[Type], Type) = if (n == 0) (acc, this) else
    out.asInstanceOf[TypeImpl].splitFunParamTypesAt0(n-1, acc :+ in)

  def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to abstraction type")
  def occurs(ty: Type): Boolean = in.occurs(ty) || out.occurs(ty)

  // Substitutions
  def replace(what: Type, by: Type): Type = if (what == this) by else AbstractionTypeNode(in.replace(what,by), out.replace(what,by))
  def substitute(subst: Subst): Type = AbstractionTypeNode(in.substitute(subst), out.substitute(subst))
}

/** Product type `ty1 x ty2 x ... x tyN` (type of n-ary tuple). */
protected[datastructures] final case class ProductTypeNode(tys: Seq[Type]) extends TypeImpl {
  assert(tys.nonEmpty, "Empty product type.")

  // Pretty printing
  def pretty: String = tys.map(_.pretty).mkString("(", " × ", ")")
  def pretty(sig: Signature): String = tys.map(_.pretty(sig)).mkString("(", " × ", ")")

  // Predicates on types
  def isBaseType: Boolean = false
  def isComposedType: Boolean = false
  def isFunType: Boolean = false
  def isProdType: Boolean = true
  def isPolyType: Boolean = false
  def isBoundTypeVar: Boolean = false
  def isApplicableWith(arg: Type): Boolean = false

  // Queries on types
  final lazy val typeVars = Set.concat(tys.map(_.typeVars):_*)
  final lazy val symbols = Set.concat(tys.map(_.symbols):_*)

  final val funDomainType = None
  final val codomainType = this
  final val arity = 0
  final val funParamTypesWithResultType = Vector(this)
  final val order = 0
  final val polyPrefixArgsCount = 0

  def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to product type")
  def occurs(ty: Type): Boolean = tys.exists(_.occurs(ty))

  // Substitutions
  def replace(what: Type, by: Type): Type = if (what == this) by
  else ProductTypeNode(tys.map(_.replace(what,by)))
  def substitute(subst: Subst): Type = ProductTypeNode(tys.map(_.substitute(subst)))
}

/**
 * Type of a polymorphic function
 * @param body The type in which a type variable is now bound to this binder
 */
protected[datastructures] final case class ForallTypeNode(body: Type) extends TypeImpl {
  // Pretty printing
  def pretty: String = s"∀. ${body.pretty}"
  def pretty(sig: Signature): String = s"∀. ${body.pretty(sig)}"

  // Predicates on types
  def isBaseType: Boolean = false
  def isComposedType: Boolean = false
  def isFunType: Boolean = false
  def isProdType: Boolean = false
  def isPolyType: Boolean = true
  def isBoundTypeVar: Boolean = false
  def isApplicableWith(arg: Type): Boolean = arg match { // we dont allow instantiating type variables with polymorphic types
    case ForallTypeNode(_) => false
    case _ => true
  }

  // Queries on types
  lazy val typeVars: Set[Type] = body.typeVars.map(t => BoundTypeNode.unapply(t.asInstanceOf[BoundTypeNode]).get-1).filter(_ > 0).map(BoundTypeNode)
  def symbols: Set[Signature.Key] = body.symbols

  def funDomainType: Option[Type] = None
  def codomainType: Type = this
  def arity: Int = 0
  def funParamTypesWithResultType: Seq[Type] = Vector(this)
  def order: Int = 0
  lazy val polyPrefixArgsCount: Int = 1 + body.polyPrefixArgsCount

  override def monomorphicBody: Type = body.monomorphicBody

  def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to type abstraction")
  def occurs(ty: Type): Boolean = body.occurs(ty)

  // Substitutions
  def replace(what: Type, by: Type): Type = if (what == this) by
  else ForallTypeNode(body.replace(what, by))
  def substitute(subst: Subst): Type = ForallTypeNode(body.substitute(subst.sink))

  override def instantiate(by: Seq[Type]): Type = if (by.isEmpty) this else body.substitute(TypeFront(by.head) +: Subst.id).instantiate(by.tail)
}

object TypeImpl {
  import scala.collection.mutable
  import mutable.{Map => MMap, TreeMap => MTreeMap}
  private val types: MMap[Signature.Key, MMap[Seq[Type], Type]] = MTreeMap.empty
  private val absTypes: MMap[Type, MMap[Type, Type]] = MMap.empty
  private val varTypes: MMap[Int, Type] = MTreeMap.empty
  private val polyTypes: MMap[Type, Type] = MMap.empty

  final def mkType(identifier: Signature.Key, args: Seq[Type]): Type = {
    types.get(identifier) match {
      case None => mkType0(identifier, args)
      case Some(inner) =>
        inner.get(args) match {
          case None => mkType1(identifier, args)
          case Some(ty) => ty
        }
    }
  }
  @inline private final def mkType0(identifier: Signature.Key, args: Seq[Type]): Type = {
    val ty = GroundTypeNode(identifier, args)
    types += (identifier -> MMap(args -> ty))
    ty
  }

  @inline private final def mkType1(identifier: Signature.Key, args: Seq[Type]): Type = {
    val ty = GroundTypeNode(identifier, args)
    assert(types.contains(identifier))
    val inner = types(identifier)
    inner += (args -> ty)
    ty
  }

  final def mkFunType(in: Type, out: Type): Type = {
    absTypes.get(in) match {
      case None => mkFunType0(in, out)
      case Some(inner) =>
        inner.get(out) match {
          case None => mkFunType1(in, out)
          case Some(ty) => ty
        }
    }
  }
  @inline final def mkFunType0(in: Type, out: Type): Type = {
    val ty = AbstractionTypeNode(in, out)
    absTypes += (in -> MMap(out -> ty))
    ty
  }
  @inline final def mkFunType1(in: Type, out: Type): Type = {
    val ty = AbstractionTypeNode(in, out)
    assert(absTypes.contains(in))
    val inner = absTypes(in)
    inner += (out -> ty)
    ty
  }

  final def mkPolyType(body: Type): Type = {
    polyTypes.get(body) match {
      case None => mkPolyType0(body)
      case Some(ty) => ty
    }
  }
  @inline final def mkPolyType0(body: Type): Type = {
    val ty = ForallTypeNode(body)
    polyTypes += (body -> ty)
    ty
  }

  final def mkVarType(scope: Int): Type = {
    varTypes.get(scope) match {
      case None => mkVarType0(scope)
      case Some(ty) => ty
    }
  }
  @inline final def mkVarType0(scope: Int): Type = {
    val ty = BoundTypeNode(scope)
    varTypes += (scope -> ty)
    ty
  }

  final def mkProdType(tys: Seq[Type]): Type = ProductTypeNode(tys)

  final def clear(): Unit = {
    types.clear()
    absTypes.clear()
    varTypes.clear()
    polyTypes.clear()
  }
}


//////////////////////////////////
/// Kinds
//////////////////////////////////

/** Represents the kind `*` (i.e. the type of a type) */
protected[datastructures] case object TypeKind extends Kind {
  final val pretty = "*"

  final val isTypeKind = true
  final val isSuperKind = false
  final val isFunKind = false

  final val arity = 0
}

protected[datastructures] case class FunKind(from: Kind, to: Kind) extends Kind {
  final def pretty = s"${from.pretty} > ${to.pretty}"

  final val isTypeKind = false
  final val isSuperKind = false
  final val isFunKind = true

  final def arity: Int = 1 + to.arity
}

/** Artificial kind that models the type of `*` (i.e. []) */
protected[datastructures] case object SuperKind extends Kind {
  final val pretty = "#"

  final val isTypeKind = false
  final val isSuperKind = true
  final val isFunKind = false

  final val arity = 0
}
