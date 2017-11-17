package leo.datastructures.impl

import leo.datastructures.{Kind, Subst, Type, TypeFront, Signature}

protected[datastructures] abstract class TypeImpl extends Type {
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
protected[datastructures] case class GroundTypeNode(id: Signature.Key, args: Seq[Type]) extends TypeImpl {
  // Pretty printing
  final def pretty: String = if (args.isEmpty) s"ty($id)"
                            else s"ty($id)(${args.map(_.pretty).mkString(",")})"
  final def pretty(sig: Signature): String = {
    if (args.isEmpty) sig(id).name
    else s"${sig(id).name}(${args.map(_.pretty(sig)).mkString(",")})"
  }

  // Predicates on types
  override final val isBaseType: Boolean     = args.isEmpty
  override final val isComposedType: Boolean = args.nonEmpty
  final def isApplicableWith(arg: Type) = false

  // Queries on types
  lazy final val typeVars = args.flatMap(_.typeVars).toSet
  lazy final val symbols = Set(id)

  final val funDomainType = None
  final val codomainType  = this
  final val arity         = 0
  final val funParamTypesWithResultType = Vector(this)
  final val order         = 0
  final val polyPrefixArgsCount = 0

  final def app(ty: Type): Type = GroundTypeNode(id, args :+ ty)
  final def occurs(ty: Type): Boolean = ty match {
    case GroundTypeNode(key, args2) if key == id => args == args2 || args.exists(_.occurs(ty))
    case _ => args.exists(_.occurs(ty))
  }

  // Substitutions
  final def replace(what: Type, by: Type): Type = if (what == this) by
    else GroundTypeNode(id, args.map(_.replace(what, by)))

  final def substitute(subst: Subst) = GroundTypeNode(id, args.map(_.substitute(subst)))
}

/** Type of a (bound) type variable when itself used as type in polymorphic function */
protected[datastructures] case class BoundTypeNode(scope: Int) extends TypeImpl {
  // Pretty printing
  final def pretty: String = scope.toString
  final def pretty(sig: Signature): String = scope.toString

  // Predicates on types
  override final val isBoundTypeVar     = true
  final def isApplicableWith(arg: Type) = false

  // Queries on types
  final val typeVars: Set[Type] = Set(this)
  final val symbols: Set[Signature.Key] = Set.empty

  final val funDomainType = None
  final val codomainType: Type = this
  final val arity = 0
  final val funParamTypesWithResultType = Vector(this)
  final val order = 0
  final val polyPrefixArgsCount = 0

  final def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to type variable")
  final def occurs(ty: Type) = false

  // Substitutions
  final def replace(what: Type, by: Type): Type = if (what == this) by else this
  import leo.datastructures.{BoundFront, TypeFront}
  final def substitute(subst: Subst): Type = subst.substBndIdx(scope) match {
    case BoundFront(j) => BoundTypeNode(j)
    case TypeFront(t)  => t
    case _ => throw new IllegalArgumentException("type substitution contains terms")
  }
}

/** Function type `in -> out` */
protected[datastructures] case class AbstractionTypeNode(in: Type, out: Type) extends TypeImpl {
  // Pretty printing
  final def pretty: String = in match {
    case _:AbstractionTypeNode => s"(${in.pretty}) -> ${out.pretty}"
    case _              => s"${in.pretty} -> ${out.pretty}"
  }
  final def pretty(sig: Signature): String = in match {
    case _:AbstractionTypeNode => s"(${in.pretty(sig)}) -> ${out.pretty(sig)}"
    case _:Type              => s"${in.pretty(sig)} -> ${out.pretty(sig)}"
  }

  // Predicates on types
  override final val isFunType          = true
  final def isApplicableWith(arg: Type): Boolean = arg == in

  // Queries on types
  final lazy val typeVars: Set[Type] = in.typeVars ++ out.typeVars
  final lazy val symbols: Set[Signature.Key] = in.symbols ++ out.symbols

  final val funDomainType   = Some(in)
  final val codomainType = out
  final lazy val arity = 1 + out.arity
  final lazy val funParamTypesWithResultType = in +: out.funParamTypesWithResultType
  final lazy val order = Math.max(1+in.order,out.order)
  final val polyPrefixArgsCount = 0

  final override protected[impl] def splitFunParamTypesAt0(n: Int, acc: Seq[Type]): (Seq[Type], Type) = if (n == 0) (acc, this) else
    out.asInstanceOf[TypeImpl].splitFunParamTypesAt0(n-1, acc :+ in)

  final def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to abstraction type")
  final def occurs(ty: Type): Boolean = in.occurs(ty) || out.occurs(ty)

  // Substitutions
  final def replace(what: Type, by: Type): Type = if (what == this) by
  else AbstractionTypeNode(in.replace(what,by), out.replace(what,by))
  final def substitute(subst: Subst) = AbstractionTypeNode(in.substitute(subst), out.substitute(subst))
}

/** Product type `l * r` */
protected[datastructures] case class ProductTypeNode(l: Type, r: Type) extends TypeImpl {
  // Pretty printing
  final def pretty = s"(${l.pretty} * ${r.pretty})"
  final def pretty(sig: Signature) =  s"(${l.pretty(sig)} * ${r.pretty(sig)})"

  // Predicates on types
  final override val isProdType          = true
  final def isApplicableWith(arg: Type) = false

  // Queries on types
  final lazy val typeVars = l.typeVars ++ r.typeVars
  final lazy val symbols = l.symbols ++ r.symbols

  final val funDomainType   = None
  final val codomainType = this
  final val arity = 0
  final val funParamTypesWithResultType = Vector(this)
  final val order = 0
  final val polyPrefixArgsCount = 0

  final def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to product type")
  final def occurs(ty: Type) = l.occurs(ty) || r.occurs(ty)

  // Substitutions
  final def replace(what: Type, by: Type) = if (what == this) by
  else ProductTypeNode(l.replace(what,by), r.replace(what,by))
  final def substitute(subst: Subst) = ProductTypeNode(l.substitute(subst), r.substitute(subst))

  // Other operations
  final override val numberOfComponents: Int = 1 + l.numberOfComponents
}

/** Product type `l + r` */
protected[datastructures] case class UnionTypeNode(l: Type, r: Type) extends TypeImpl {
  // Pretty printing
  final def pretty: String = s"(${l.pretty} + ${r.pretty})"
  final def pretty(sig: Signature): String =  s"(${l.pretty(sig)} + ${r.pretty(sig)})"

  // Predicates on types
  final override val isUnionType        = true
  final def isApplicableWith(arg: Type) = false

  // Queries on types
  final lazy val typeVars = l.typeVars ++ r.typeVars
  final lazy val symbols = l.symbols ++ r.symbols

  final val funDomainType   = None
  final val codomainType = this
  final val arity = 0
  final val funParamTypesWithResultType = Vector(this)
  final val order = 0
  final val polyPrefixArgsCount = 0

  final def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to union type")
  final def occurs(ty: Type) = l.occurs(ty) || r.occurs(ty)

  // Substitutions
  final def replace(what: Type, by: Type): Type = if (what == this) by
  else UnionTypeNode(l.replace(what,by), r.replace(what,by))
  final def substitute(subst: Subst) = UnionTypeNode(l.substitute(subst), r.substitute(subst))
}

/**
 * Type of a polymorphic function
 * @param body The type in which a type variable is now bound to this binder
 */
protected[datastructures] case class ForallTypeNode(body: Type) extends TypeImpl {
  // Pretty printing
  final def pretty = s"∀. ${body.pretty}"
  final def pretty(sig: Signature) = s"∀. ${body.pretty(sig)}"

  // Predicates on types
  final override val isPolyType         = true
  final def isApplicableWith(arg: Type) = arg match { // we dont allow instantiating type variables with polymorphic types
    case ForallTypeNode(_) => false
    case _ => true
  }

  // Queries on types
  final lazy val typeVars: Set[Type] = body.typeVars.map(t => BoundTypeNode.unapply(t.asInstanceOf[BoundTypeNode]).get-1).filter(_ > 0).map(BoundTypeNode)
  final lazy val symbols = body.symbols

  final val funDomainType   = None
  final val codomainType = this
  final val arity = 0
  final val funParamTypesWithResultType = Vector(this)
  final val order = 0
  final lazy val polyPrefixArgsCount = 1 + body.polyPrefixArgsCount

  final override lazy val monomorphicBody: Type = body.monomorphicBody

  final def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to type abstraction")
  final def occurs(ty: Type) = body.occurs(ty)

  // Substitutions
  final def replace(what: Type, by: Type) = if (what == this) by
  else ForallTypeNode(body.replace(what, by))
  final def substitute(subst: Subst) = ForallTypeNode(body.substitute(subst.sink))

  final override def instantiate(by: Seq[Type]): Type = if (by.isEmpty) this
  else body.substitute(TypeFront(by.head) +: Subst.id).instantiate(by.tail)
}

object TypeImpl {
  import scala.collection.mutable
  import mutable.{Map => MMap, TreeMap => MTreeMap}
  private var types: MMap[Signature.Key, MMap[Seq[Type], Type]] = MTreeMap.empty
  private var absTypes: MMap[Type, MMap[Type, Type]] = MMap.empty
  private var varTypes: MMap[Int, Type] = MTreeMap.empty
  private var polyTypes: MMap[Type, Type] = MMap.empty

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

  final def mkProdType(t1: Type, t2: Type): Type = ProductTypeNode(t1,t2)
  final def mkUnionType(t1: Type, t2: Type): Type = UnionTypeNode(t1,t2)

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
