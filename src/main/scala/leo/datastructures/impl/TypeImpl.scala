package leo.datastructures.impl

import leo.datastructures.{Kind, Subst, Type}

/** Literal type, i.e. `$o` */
protected[datastructures] case class BaseTypeNode(id: Signature#Key) extends Type {
  // Pretty printing
  import Signature.{get => signature}
  def pretty = signature.meta(id).name

  // Predicates on types
  override val isBaseType         = true
  def isApplicableWith(arg: Type) = false

  // Queries on types
  def typeVars = Set.empty

  val funDomainType   = None
  val funCodomainType = None
  val funArity = 0
  val funParamTypesWithResultType = Seq(this)

  val scopeNumber = 0

  def occurs(ty: Type) = ty match {
    case BaseTypeNode(key) if key == id => true
    case _                              => false
  }

  // Substitutions
  def substitute(what: Type, by: Type) = what match {
    case BaseTypeNode(key) if key == id => by
    case _ => this
  }
  def substitute(subst: Subst) = this
  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = baseFunc(id)

  def closure(subst: Subst) = substitute(subst)
}

/** Type of a (bound) type variable when itself used as type in polymorphic function */
protected[datastructures] case class BoundTypeNode(scope: Int) extends Type {
  // Pretty printing
  def pretty = scope.toString

  // Predicates on types
  override val isBoundTypeVar     = true
  def isApplicableWith(arg: Type) = false

  // Queries on types
  def typeVars: Set[Type] = Set(this)

  val funDomainType   = None
  val funCodomainType = None
  val funArity = 0
  val funParamTypesWithResultType = Seq(this)

  val scopeNumber = -scope

  def occurs(ty: Type) = false

  // Substitutions
  def substitute(what: Type, by: Type) = what match {
    case BoundTypeNode(i) if i == scope => by
    case _ => this
  }
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

  def closure(subst: Subst) = substitute(subst)
}

/** Function type `in -> out` */
protected[datastructures] case class AbstractionTypeNode(in: Type, out: Type) extends Type {
  // Pretty printing
  def pretty = in match {
    case funTy:AbstractionTypeNode => "(" + funTy.pretty + ") -> " + out.pretty
    case otherTy:Type              => otherTy.pretty + " -> " + out.pretty
  }

  // Predicates on types
  override val isFunType          = true
  def isApplicableWith(arg: Type) = arg == in

  // Queries on types
  def typeVars = in.typeVars ++ out.typeVars

  lazy val funDomainType   = Some(in)
  lazy val funCodomainType = Some(out)
  lazy val funArity = 1 + out.funArity
  lazy val funParamTypesWithResultType = Seq(in) ++ out.funParamTypesWithResultType

  val scopeNumber = Math.min(in.scopeNumber, out.scopeNumber)

  def occurs(ty: Type) = in.occurs(ty) || out.occurs(ty)

  // Substitutions
  def substitute(what: Type, by: Type) = AbstractionTypeNode(in.substitute(what,by), out.substitute(what,by))
  def substitute(subst: Subst) = AbstractionTypeNode(in.substitute(subst), out.substitute(subst))
  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = absFunc(in.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc),out.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc))

  def closure(subst: Subst) = substitute(subst)
}

/** Product type `l * r` */
protected[datastructures] case class ProductTypeNode(l: Type, r: Type) extends Type {
  // Pretty printing
  def pretty = "(" + l.pretty + " * " + r.pretty + ")"

  // Predicates on types
  override val isProdType          = true
  def isApplicableWith(arg: Type) = false

  // Queries on types
  def typeVars = l.typeVars ++ r.typeVars

  val funDomainType   = None
  val funCodomainType = None
  val funArity = 0
  val funParamTypesWithResultType = Seq(this)

  val scopeNumber = Math.min(l.scopeNumber, r.scopeNumber)

  def occurs(ty: Type) = l.occurs(ty) || r.occurs(ty)

  // Substitutions
  def substitute(what: Type, by: Type) = ProductTypeNode(l.substitute(what,by), r.substitute(what,by))
  def substitute(subst: Subst) = ProductTypeNode(l.substitute(subst), r.substitute(subst))
  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = prodFunc(l.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc),r.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc))

  def closure(subst: Subst) = substitute(subst)

  override val numberOfComponents: Int = 1 + l.numberOfComponents
}

/** Product type `l + r` */
protected[datastructures] case class UnionTypeNode(l: Type, r: Type) extends Type {
  // Pretty printing
  def pretty = "(" + l.pretty + " + " + r.pretty + ")"

  // Predicates on types
  override val isUnionType        = true
  def isApplicableWith(arg: Type) = false

  // Queries on types
  def typeVars = l.typeVars ++ r.typeVars

  val funDomainType   = None
  val funCodomainType = None
  val funArity = 0
  val funParamTypesWithResultType = Seq(this)

  val scopeNumber = Math.min(l.scopeNumber, r.scopeNumber)

  def occurs(ty: Type) = l.occurs(ty) || r.occurs(ty)

  // Substitutions
  def substitute(what: Type, by: Type) = UnionTypeNode(l.substitute(what,by), r.substitute(what,by))
  def substitute(subst: Subst) = UnionTypeNode(l.substitute(subst), r.substitute(subst))

  def instantiate(by: Type) = this

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = unionFunc(l.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc),r.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc))

  def closure(subst: Subst) = substitute(subst)
}

/**
 * Type of a polymorphic function
 * @param body The type in which a type variable is now bound to this binder
 */
protected[datastructures] case class ForallTypeNode(body: Type) extends Type {
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

  val funDomainType   = None
  val funCodomainType = None
  val funArity = 0
  val funParamTypesWithResultType = Seq(this)

  val scopeNumber = body.scopeNumber + 1

  def occurs(ty: Type) = body.occurs(ty)

  // Substitutions
  def substitute(what: Type, by: Type) = what match {
    case BoundTypeNode(i) => ForallTypeNode(body.substitute(BoundTypeNode(i+1), by))
    case _ => ForallTypeNode(body.substitute(what,by))
  }
  def substitute(subst: Subst) = ForallTypeNode(body.substitute(subst.sink))

  def instantiate(by: Type) = body.substitute(BoundTypeNode(1), by)

  // Other operations
  def foldRight[A](baseFunc: Signature#Key => A)
                  (boundFunc: Int => A)
                  (absFunc: (A,A) => A)
                  (prodFunc: (A,A) => A)
                  (unionFunc: (A,A) => A)
                  (forAllFunc: A => A) = forAllFunc(body.foldRight(baseFunc)(boundFunc)(absFunc)(prodFunc)(unionFunc)(forAllFunc))

  def closure(subst: Subst) = substitute(subst)
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
}
/** Artificial kind that models the type of `*` (i.e. []) */
protected[datastructures] case object SuperKind extends Kind {
  def pretty = "#"

  val isTypeKind = false
  val isSuperKind = true
  val isFunKind = false
}


