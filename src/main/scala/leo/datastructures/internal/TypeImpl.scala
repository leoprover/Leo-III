package leo.datastructures.internal


/** Literal type, i.e. `$o` */
protected[internal] case class BaseTypeNode(id: Signature#Key) extends Type {
  // Pretty printing
  import Signature.{get => signature}
  def pretty = signature.meta(id).name

  // Predicates on types
  override val isBaseType         = true
  def isApplicableWith(arg: Type) = false

  // Queries on types
  def typeVars = Set.empty

  def funDomainType   = None
  def funCodomainType = None

  def occurs(ty: Type) = ty match {
    case BaseTypeNode(key) if key == id => true
    case _                              => false
  }

  // Substitutions
  def substitute(what: Type, by: Type) = what match {
    case BaseTypeNode(key) if key == id => by
    case _ => this
  }

  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (forAllFunc: A => A) = baseFunc(id)
}

/** Type of a (bound) type variable when itself used as type in polymorphic function */
protected[internal] case class BoundTypeNode(scope: Int) extends Type {
  // Pretty printing
  def pretty = scope.toString

  // Predicates on types
  override val isBoundTypeVar     = true
  def isApplicableWith(arg: Type) = false

  // Queries on types
  def typeVars: Set[Type] = Set(this)

  def funDomainType   = None
  def funCodomainType = None

  def occurs(ty: Type) = false

  // Substitutions
  def substitute(what: Type, by: Type) = what match {
    case BoundTypeNode(i) if i == scope => by
    case _ => this
  }

  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (forAllFunc: A => A) = boundFunc(scope)
}

/** Function type `in -> out` */
protected[internal] case class AbstractionTypeNode(in: Type, out: Type) extends Type {
  // Pretty printing
  def pretty = in match {
    case funTy:AbstractionTypeNode => "(" + funTy.pretty + ") -> " + out.pretty
    case otherTy:Type              => otherTy.pretty + " -> " + out.pretty
  }

  // Predicates on types
  override val isFunType          = true
  def isApplicableWith(arg: Type) = (arg == in)

  // Queries on types
  def typeVars = in.typeVars ++ out.typeVars

  def funDomainType   = Some(in)
  def funCodomainType = Some(out)

  def occurs(ty: Type) = in.occurs(ty) || out.occurs(ty)

  // Substitutions
  def substitute(what: Type, by: Type) = AbstractionTypeNode(in.substitute(what,by), out.substitute(what,by))

  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (forAllFunc: A => A) = absFunc(in.foldRight(baseFunc)(boundFunc)(absFunc)(forAllFunc),out.foldRight(baseFunc)(boundFunc)(absFunc)(forAllFunc))
}


/**
 * Type of a polymorphic function
 * @param body The type in which a type variable is now bound to this binder
 */
protected[internal] case class ForallTypeNode(body: Type) extends Type {
  // Pretty printing
  def pretty = "∀. " + body.pretty

  // Predicates on types
  override val isPolyType         = true
  def isApplicableWith(arg: Type) = arg match { // we dont allow instantiating type variables with polymorphic types
    case ForallTypeNode(_) => false
    case _ => true
  }

  // Queries on types
  def typeVars = body.typeVars

  def funDomainType   = None
  def funCodomainType = None

  def occurs(ty: Type) = body.occurs(ty)

  // Substitutions
  def substitute(what: Type, by: Type) = what match {
    case BoundTypeNode(i) => ForallTypeNode(body.substitute(BoundTypeNode(i+1), by))
    case _ => ForallTypeNode(body.substitute(what,by))
  }

  def instantiate(by: Type) = body.substitute(BoundTypeNode(1), by)

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (forAllFunc: A => A) = forAllFunc(body.foldRight(baseFunc)(boundFunc)(absFunc)(forAllFunc))
}




//////////////////////////////////
/// Kinds
//////////////////////////////////

/** Represents the kind `*` (i.e. the type of a type) */
protected[internal] case object TypeKind extends Kind {
  def pretty = "*"

  val isTypeKind = true
  val isSuperKind = false
  val isFunKind = false
}
/** Artificial kind that models the type of `*` (i.e. []) */
protected[internal] case object SuperKind extends Kind {
  def pretty = "#"

  val isTypeKind = false
  val isSuperKind = true
  val isFunKind = false
}


