package leo.datastructures.internal

/**
 * Naive implementation of nameless lambda terms.
 * Uses inefficient reduction und substitution methods
 *
 * @author Alexander Steen
 * @since 02.06.2014
 */
sealed abstract class NaiveTerm extends Term {
  // Predicates on terms
  val isAtom = false
  val isTermApp = false
  val isTermAbs = false
  val isTypeApp = false
  val isTypeAbs = false
}

///////////////////
// Term symbols
///////////////////

protected[internal] case class SymbolNode(id: Signature#Key) extends NaiveTerm {

  // Predicates on terms
  override val isAtom = true
  override def is(symbol: Signature#Key) = id == symbol

  private lazy val sym = Signature.get.meta(id)
  // Queries on terms
  def ty = sym._ty
  def freeVars = Set(this)

  // Substitutions
  def substitute(what: Term, by: Term) = what match {
    case SymbolNode(s) if s == id => by
    case _                    => this
  }
  def inc(scopeIndex: Int) = this
  def instantiate(scope: Int, by: Type) = this

  // Other operations
  def betaNormalize = this

  def foldRight[A](symFunc: Signature#Key => A)
                  (boundFunc: (Type, Int) => A)
                  (absFunc: (Type, A) => A)
                  (appFunc: (A,A) => A)
                  (tAbsFunc: A => A)
                  (tAppFunc: (A, Type) => A) = symFunc(id)
  // Pretty printing
  def pretty = sym.name
}

protected[internal] case class BoundNode(t: Type, scope: Int) extends NaiveTerm {
  override val isAtom = true

  // Queries on terms
  def ty = t
  def freeVars = Set.empty

  // Substitutions
  def substitute(what: Term, by: Term) = what match {
    case BoundNode(_, i) if i == scope => by
    case BoundNode(_, i) if i < scope => BoundNode(t, scope-1)
    case _                             => this
  }

  def inc(scopeIndex: Int) = scopeIndex match {
    case s if s <= scope => BoundNode(t, scope+1)
    case _ => this
  }

  def instantiate(sc: Int, by: Type) = {
    t match {
//      case ForallTypeNode(body) => BoundNode(body.substitute(BoundTypeNode(scope),by),scope)
      case _ => BoundNode(t.substitute(BoundTypeNode(sc),by),scope)
    }
  }




  // Other operations
  def betaNormalize = this

  def foldRight[A](symFunc: Signature#Key => A)
                  (boundFunc: (Type, Int) => A)
                  (absFunc: (Type, A) => A)
                  (appFunc: (A,A) => A)
                  (tAbsFunc: A => A)
                  (tAppFunc: (A, Type) => A) = boundFunc(t,scope)
  // Pretty printing
  def pretty = scope.toString
}

protected[internal] case class AbstractionNode(absType: Type, term: Term) extends NaiveTerm {
  override val isTermAbs = true

  // Queries on terms
  def ty = absType ->: term.ty
  def freeVars = term.freeVars

  // Substitutions
  def substitute(what: Term, by: Term) = what match {
   case BoundNode(t,i) => AbstractionNode(absType, term.substitute(BoundNode(t,i+1), by.inc(1)))
   case _ => AbstractionNode(absType, term.substitute(what,by))
  }

  def inc(scopeIndex: Int) = AbstractionNode(absType, term.inc(scopeIndex+1))

  def instantiate(scope: Int, by: Type) = AbstractionNode(absType.substitute(BoundTypeNode(scope),by),term.instantiate(scope,by))

   // Other operations
  def betaNormalize = AbstractionNode(absType, term.betaNormalize)

  def foldRight[A](symFunc: Signature#Key => A)
                  (boundFunc: (Type, Int) => A)
                  (absFunc: (Type, A) => A)
                  (appFunc: (A,A) => A)
                  (tAbsFunc: A => A)
                  (tAppFunc: (A, Type) => A) = absFunc(absType, term.foldRight(symFunc)(boundFunc)(absFunc)(appFunc)(tAbsFunc)(tAppFunc))
  // Pretty printing
  def pretty = "[λ." + term.pretty + "]"
}



protected[internal] case class ApplicationNode(left: Term, right: Term) extends NaiveTerm {
  override val isTermApp = true

  // Queries on terms
  def ty = {
    require(left.ty.isFunType, "Application node not well typed: "+this.pretty)
    left.ty._funCodomainType
  } // assume everything is well-typed

  def freeVars = left.freeVars ++ right.freeVars

  // Substitutions
  def substitute(what: Term, by: Term) = ApplicationNode(left.substitute(what,by), right.substitute(what,by))

  def inc(scopeIndex: Int) = ApplicationNode(left.inc(scopeIndex), right.inc(scopeIndex))

  def instantiate(scope: Int, by: Type) = ApplicationNode(left.instantiate(scope,by), right.instantiate(scope,by))

  // Other operations
  def betaNormalize = {
    val leftNF = left.betaNormalize
    val rightNF = right.betaNormalize

    leftNF match {
      case AbstractionNode(ty, body) => body.substitute(BoundNode(ty ,1), rightNF).betaNormalize
      case _ => ApplicationNode(leftNF, rightNF)
    }
  }

  def foldRight[A](symFunc: Signature#Key => A)
                  (boundFunc: (Type, Int) => A)
                  (absFunc: (Type, A) => A)
                  (appFunc: (A,A) => A)
                  (tAbsFunc: A => A)
                  (tAppFunc: (A, Type) => A) = appFunc(left.foldRight(symFunc)(boundFunc)(absFunc)(appFunc)(tAbsFunc)(tAppFunc),
                                                       right.foldRight(symFunc)(boundFunc)(absFunc)(appFunc)(tAbsFunc)(tAppFunc))
  // Pretty printing
  def pretty = "(" + left.pretty + " " + right.pretty + ")"
}


///////////////////
// Type symbols
///////////////////

protected[internal] case class TypeAbstractionNode(term: Term) extends NaiveTerm {
  override val isTypeAbs = true

  // Queries on terms
  def ty = Type.mkPolyType(term.ty)
  def freeVars = term.freeVars

  // Substitutions
  def substitute(what: Term, by: Term) = TypeAbstractionNode(term.substitute(what,by))
  def inc(scopeIndex: Int) = TypeAbstractionNode(term.inc(scopeIndex))
  def instantiate(scope: Int, by: Type) = TypeAbstractionNode(term.instantiate(scope+1,by))
  // Other operations
  def betaNormalize = TypeAbstractionNode(term.betaNormalize)

  def foldRight[A](symFunc: Signature#Key => A)
                  (boundFunc: (Type, Int) => A)
                  (absFunc: (Type, A) => A)
                  (appFunc: (A,A) => A)
                  (tAbsFunc: A => A)
                  (tAppFunc: (A, Type) => A) = tAbsFunc(term.foldRight(symFunc)(boundFunc)(absFunc)(appFunc)(tAbsFunc)(tAppFunc))

  // Pretty printing
  def pretty = "[Λ." + term.pretty + "]"
}

protected[internal] case class TypeApplicationNode(left: Term, right: Type) extends NaiveTerm {
  override val isTypeApp = true

  // Queries on terms
  def ty = {
    require(left.ty.isPolyType, "Type Application node not well typed: "+this.pretty)
    left.ty.instantiate(right)
  } // assume everything is well-typed

  def freeVars = left.freeVars

  // Substitutions
  def substitute(what: Term, by: Term) = TypeApplicationNode(left.substitute(what,by), right)

  def inc(scopeIndex: Int) = TypeApplicationNode(left.inc(scopeIndex), right)

  def instantiate(scope: Int, by: Type) = TypeApplicationNode(left.instantiate(scope,by), right.substitute(BoundTypeNode(scope),by))
  // Other operations
  def betaNormalize = {
    val leftNF = left.betaNormalize

    leftNF match {
      case TypeAbstractionNode(term) => term.instantiateBy(right).betaNormalize
      case _ => TypeApplicationNode(leftNF, right)
    }
  }

  def foldRight[A](symFunc: Signature#Key => A)
                  (boundFunc: (Type, Int) => A)
                  (absFunc: (Type, A) => A)
                  (appFunc: (A,A) => A)
                  (tAbsFunc: A => A)
                  (tAppFunc: (A, Type) => A) = tAppFunc(left.foldRight(symFunc)(boundFunc)(absFunc)(appFunc)(tAbsFunc)(tAppFunc), right)

  // Pretty printing
  def pretty = "(" + left.pretty + " " + right.pretty + ")"
}



