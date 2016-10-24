package leo.datastructures.impl

import leo.datastructures.{Kind, Subst, Type, TypeFront, Signature}

protected[datastructures] abstract class TypeImpl extends Type {
  def splitFunParamTypesAt(n: Int): (Seq[Type], Type) = splitFunParamTypesAt0(n, Seq())
  protected[impl] def splitFunParamTypesAt0(n: Int, acc: Seq[Type]): (Seq[Type], Type) = if (n == 0) (acc, this) else
    throw new UnsupportedOperationException("splitFunParamTypesAt0 with non-zero n on non-Function type")
  // to be overridden by abstraction type below

  def closure(subst: Subst) = substitute(subst)

  def monomorphicBody: Type = this
}

/** Literal type, i.e. `$o` */
protected[datastructures] case class GroundTypeNode(id: Signature#Key, args: Seq[Type]) extends TypeImpl {
  // Pretty printing
  lazy val pretty = {
    if (args.isEmpty)
      s"ty($id)"
    else
      s"ty($id)" +"(" + args.map(_.pretty).mkString(",") + ")"
  }
  final def pretty(sig: Signature) = {
    if (args.isEmpty)
      sig(id).name
    else
      s"${sig(id).name}(${args.map(_.pretty).mkString(",")})"
  }

  // Predicates on types
  override val isBaseType         = args.isEmpty
  override val isComposedType     = args.nonEmpty
  def isApplicableWith(arg: Type) = false

  // Queries on types
  lazy val typeVars = args.flatMap(_.typeVars).toSet
  lazy val symbols = Set(id)

  val funDomainType = None
  val codomainType  = this
  val arity         = 0
  val funParamTypesWithResultType = Seq(this)
  val order         = 0
  val polyPrefixArgsCount = 0

  val scopeNumber = 0

  def app(ty: Type): Type = GroundTypeNode(id, args :+ ty)

  def occurs(ty: Type) = ty match {
    case GroundTypeNode(key, args2) if key == id => args == args2
    case _ => args.exists(_.occurs(ty))
  }

  // Substitutions
  def replace(what: Type, by: Type) = if (what == this) by
    else GroundTypeNode(id, args.map(_.replace(what, by)))

  def substitute(subst: Subst) = GroundTypeNode(id, args.map(_.substitute(subst)))
  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A) // FIXME
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = baseFunc(id)
}

/** Type of a (bound) type variable when itself used as type in polymorphic function */
protected[datastructures] case class BoundTypeNode(scope: Int) extends TypeImpl {
  // Pretty printing
  def pretty = scope.toString
  final def pretty(sig: Signature) = scope.toString

  // Predicates on types
  override val isBoundTypeVar     = true
  def isApplicableWith(arg: Type) = false

  // Queries on types
  val typeVars: Set[Type] = Set(this)
  val symbols = Set[Signature#Key]()

  val funDomainType   = None
  val codomainType = this
  val arity = 0
  val funParamTypesWithResultType = Seq(this)
  val order = 0
  val polyPrefixArgsCount = 0

  val scopeNumber = -scope

  def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to type variable")

  def occurs(ty: Type) = false

  // Substitutions
  def replace(what: Type, by: Type) = if (what == this) by else this
  import leo.datastructures.{BoundFront, TypeFront}
  def substitute(subst: Subst) = subst.substBndIdx(scope) match {
    case BoundFront(j) => BoundTypeNode(j)
    case TypeFront(t)  => t
    case _ => throw new IllegalArgumentException("type substitution contains terms")
  }


  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = boundFunc(scope)
}

/** Function type `in -> out` */
protected[datastructures] case class AbstractionTypeNode(in: Type, out: Type) extends TypeImpl {
  // Pretty printing
  def pretty = in match {
    case funTy:AbstractionTypeNode => "(" + funTy.pretty + ") -> " + out.pretty
    case otherTy:Type              => otherTy.pretty + " -> " + out.pretty
  }
  final def pretty(sig: Signature) = in match {
    case funTy:AbstractionTypeNode => "(" + funTy.pretty(sig) + ") -> " + out.pretty(sig)
    case otherTy:Type              => otherTy.pretty(sig) + " -> " + out.pretty(sig)
  }

  // Predicates on types
  override val isFunType          = true
  def isApplicableWith(arg: Type) = arg == in

  // Queries on types
  lazy val typeVars = in.typeVars ++ out.typeVars
  lazy val symbols = in.symbols ++ out.symbols

  lazy val funDomainType   = Some(in)
  lazy val codomainType = out
  lazy val arity = 1 + out.arity
  lazy val funParamTypesWithResultType = Seq(in) ++ out.funParamTypesWithResultType
  lazy val order = Math.max(1+in.order,out.order)
  val polyPrefixArgsCount = 0

  override protected[impl] def splitFunParamTypesAt0(n: Int, acc: Seq[Type]): (Seq[Type], Type) = if (n == 0) (acc, this) else
    out.asInstanceOf[TypeImpl].splitFunParamTypesAt0(n-1, in +: acc)

  val scopeNumber = Math.min(in.scopeNumber, out.scopeNumber)

  def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to abstraction type")

  def occurs(ty: Type) = in.occurs(ty) || out.occurs(ty)

  // Substitutions
  def replace(what: Type, by: Type) = if (what == this) by
  else AbstractionTypeNode(in.replace(what,by), out.replace(what,by))
  def substitute(subst: Subst) = AbstractionTypeNode(in.substitute(subst), out.substitute(subst))
  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = absFunc(in.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc),out.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc))
}

/** Product type `l * r` */
protected[datastructures] case class ProductTypeNode(l: Type, r: Type) extends TypeImpl {
  // Pretty printing
  final def pretty = s"(${l.pretty} * ${r.pretty})"
  final def pretty(sig: Signature) =  s"(${l.pretty(sig)} * ${r.pretty(sig)})"

  // Predicates on types
  override val isProdType          = true
  def isApplicableWith(arg: Type) = false

  // Queries on types
  lazy val typeVars = l.typeVars ++ r.typeVars
  lazy val symbols = l.symbols ++ r.symbols

  val funDomainType   = None
  val codomainType = this
  val arity = 0
  val funParamTypesWithResultType = Seq(this)
  val order = 0
  val polyPrefixArgsCount = 0

  val scopeNumber = Math.min(l.scopeNumber, r.scopeNumber)

  def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to product type")

  def occurs(ty: Type) = l.occurs(ty) || r.occurs(ty)

  // Substitutions
  def replace(what: Type, by: Type) = if (what == this) by
  else ProductTypeNode(l.replace(what,by), r.replace(what,by))
  def substitute(subst: Subst) = ProductTypeNode(l.substitute(subst), r.substitute(subst))
  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = prodFunc(l.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc),r.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc))

  override val numberOfComponents: Int = 1 + l.numberOfComponents
}

/** Product type `l + r` */
protected[datastructures] case class UnionTypeNode(l: Type, r: Type) extends TypeImpl {
  // Pretty printing
  final def pretty = s"(${l.pretty} + ${r.pretty})"
  final def pretty(sig: Signature) =  s"(${l.pretty(sig)} + ${r.pretty(sig)})"

  // Predicates on types
  override val isUnionType        = true
  def isApplicableWith(arg: Type) = false

  // Queries on types
  lazy val typeVars = l.typeVars ++ r.typeVars
  lazy val symbols = l.symbols ++ r.symbols

  val funDomainType   = None
  val codomainType = this
  val arity = 0
  val funParamTypesWithResultType = Seq(this)
  val order = 0
  val polyPrefixArgsCount = 0

  val scopeNumber = Math.min(l.scopeNumber, r.scopeNumber)

  def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to union type")

  def occurs(ty: Type) = l.occurs(ty) || r.occurs(ty)

  // Substitutions
  def replace(what: Type, by: Type) = if (what == this) by
  else UnionTypeNode(l.replace(what,by), r.replace(what,by))
  def substitute(subst: Subst) = UnionTypeNode(l.substitute(subst), r.substitute(subst))

  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = unionFunc(l.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc),r.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc))
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
  override val isPolyType         = true
  def isApplicableWith(arg: Type) = arg match { // we dont allow instantiating type variables with polymorphic types
    case ForallTypeNode(_) => false
    case _ => true
  }

  // Queries on types
  val typeVars = body.typeVars
  val symbols = body.symbols

  val funDomainType   = None
  val codomainType = this
  val arity = 0
  val funParamTypesWithResultType = Seq(this)
  val order = 0
  lazy val polyPrefixArgsCount = 1 + body.polyPrefixArgsCount

  override lazy val monomorphicBody: Type = body.monomorphicBody

  val scopeNumber = body.scopeNumber + 1

  def app(ty: Type): Type = throw new IllegalArgumentException("Typed applied to type abstraction") //TODO: refine, since its basically beta reduction

  def occurs(ty: Type) = body.occurs(ty)

  // Substitutions
  def replace(what: Type, by: Type) = if (what == this) by
  else ForallTypeNode(body.replace(what, by))
  def substitute(subst: Subst) = ForallTypeNode(body.substitute(subst.sink))

  def instantiate(by: Type) = body.substitute(TypeFront(by) +: Subst.id )

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = forAllFunc(body.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc))
}




//////////////////////////////////
/// Kinds
//////////////////////////////////

/** Represents the kind `*` (i.e. the type of a type) */
protected[datastructures] case object TypeKind extends Kind {
  def pretty = "*"

  val isTypeKind = true
  val isSuperKind = false
  val isFunKind = false

  val arity = 0
}

protected[datastructures] case class FunKind(from: Kind, to: Kind) extends Kind {
  def pretty = from.pretty + " > " + to.pretty

  val isTypeKind = false
  val isSuperKind = false
  val isFunKind = true

  lazy val arity = 1 + to.arity
}

/** Artificial kind that models the type of `*` (i.e. []) */
protected[datastructures] case object SuperKind extends Kind {
  def pretty = "#"

  val isTypeKind = false
  val isSuperKind = true
  val isFunKind = false

  val arity = 0
}


