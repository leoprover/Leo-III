package datastructures.internal

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

protected[internal] case class SymbolNode(id: Signature#ConstKey) extends NaiveTerm {
  override val isAtom = true

  private val sym = Signature.get.getConstMeta(id)
  // Queries on terms
  def ty = sym._getType
  def freeVars = Set(this)
  def herbrandUniverse = ???

  // Substitutions
  def substitute(what: Term, by: Term) = what match {
    case SymbolNode(s) if s == id => by
    case _                    => this
  }
  def inc(scopeIndex: Int) = this

  // Other operations
  def betaNormalize = this
  // Pretty printing
  def pretty = sym.getName
}

protected[internal] case class BoundNode(t: Type, scope: Int) extends NaiveTerm {
  override val isAtom = true

  // Queries on terms
  def ty = t
  def freeVars = Set.empty
  def herbrandUniverse = ???

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

  // Other operations
  def betaNormalize = this
  // Pretty printing
  def pretty = scope.toString
}

protected[internal] case class AbstractionNode(absType: Type, term: Term) extends NaiveTerm {
  override val isTermAbs = true

  // Queries on terms
  def ty = absType ->: term.ty
  def freeVars = term.freeVars
  def herbrandUniverse = ???
 
  // Substitutions
  def substitute(what: Term, by: Term) = what match {
   case BoundNode(t,i) => AbstractionNode(absType, term.substitute(BoundNode(t,i+1), by.inc(1)))
   case _ => AbstractionNode(absType, term.substitute(what,by))
  }
  def inc(scopeIndex: Int) = AbstractionNode(absType, term.inc(scopeIndex+1))
   // Other operations
  def betaNormalize = AbstractionNode(absType, term.betaNormalize)
  // Pretty printing
  def pretty = "[\\." + term.pretty + "]"
}



protected[internal] case class ApplicationNode(left: Term, right: Term) extends NaiveTerm {
  override val isTermApp = true

  // Queries on terms
  def ty = {
    require(left.ty.isFunType, "Application node not well typed: "+this.pretty)
    left.ty._funCodomainType
  } // assume everything is well-typed

  def freeVars = left.freeVars ++ right.freeVars
  def herbrandUniverse = ???

  // Substitutions
  def substitute(what: Term, by: Term) = ApplicationNode(left.substitute(what,by), right.substitute(what,by))

  def inc(scopeIndex: Int) = ApplicationNode(left.inc(scopeIndex), right.inc(scopeIndex))

  // Other operations
  def betaNormalize = left match {
    case AbstractionNode(ty, body) => body.substitute(BoundNode(ty ,1), right)
    case _ => this
  }
  // Pretty printing
  def pretty = "(" + left.pretty + " " + right.pretty + ")"
}

