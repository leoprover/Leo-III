package datastructures.internal

/**
 * Created by lex on 29.05.14.
 */
sealed abstract class RefTermImpl extends RefTerm {
  // Predicates on terms
  val isAtom = false
  val isTermApp = false
  val isTermAbs = false
  val isTypeApp = false
  val isTypeAbs = false
}

protected[internal] case class SymbolNode(id: Signature#ConstKey) extends RefTermImpl {
  override val isAtom = true

  private val sym = Signature.get.getConstMeta(id)
  // Queries on terms
  def ty = sym._getType
  def freeVars = Set(this)
  def herbrandUniverse = ???

  // Substitutions
  def substitute(what: RefTerm, by: RefTerm) = what match {
    case SymbolNode(s) if s == id => by
    case _                    => this
  }
  def substituteBound(scopeIndex: Int, by: RefTerm) = this
  def inc(scopeIndex: Int) = this

  // Other operations
  def betaNormalize = this
  // Pretty printing
  def pretty = sym.getName
}

protected[internal] case class BoundNode(t: Type, scope: Int) extends RefTermImpl {
  override val isAtom = true

  // Queries on terms
  def ty = t
  def freeVars = Set.empty
  def herbrandUniverse = ???

  // Substitutions
  def substitute(what: RefTerm, by: RefTerm) = what match {
    case BoundNode(_, i) if i == scope => by
    case _                             => this
  }
  def substituteBound(scopeIndex: Int, by: RefTerm) = scopeIndex match {
    case `scope` => by
    case s if s < scope => BoundNode(t, scope-1)
    case _ => this
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

protected[internal] case class AbstractionNode(absType: Type, term: RefTerm) extends RefTermImpl {
  override val isTermAbs = true

  // Queries on terms
  def ty = absType ->: term.ty
  def freeVars = term.freeVars
  def herbrandUniverse = ???
 //  \.\.\.\.\.([\.1 (\.2)] [\.6])
 //  \.\.\.\.\.([[\.6] (\.[\.7])])
  // Substitutions
  def substitute(what: RefTerm, by: RefTerm) = AbstractionNode(absType, term.substitute(what,by))
  def substituteBound(scopeIndex: Int, by: RefTerm) = AbstractionNode(absType, term.substituteBound(scopeIndex+1, by.inc(1)))
  def inc(scopeIndex: Int) = AbstractionNode(absType, term.inc(scopeIndex+1)) // TODO ????
   // Other operations
  def betaNormalize = AbstractionNode(absType, term.betaNormalize)
  // Pretty printing
  def pretty = "[\\." + term.pretty + "]"
}



protected[internal] case class ApplicationNode(left: RefTerm, right: RefTerm) extends RefTermImpl {
  override val isTermApp = true

  // Queries on terms
  def ty = {
    require(left.ty.isFunType)
    left.ty._funCodomainType
  } // assume everything is well-typed

  def freeVars = left.freeVars ++ right.freeVars
  def herbrandUniverse = ???

  // Substitutions
  def substitute(what: RefTerm, by: RefTerm) = ApplicationNode(left.substitute(what,by), right.substitute(what,by))

  def substituteBound(scopeIndex: Int, by: RefTerm) = ApplicationNode(left.substituteBound(scopeIndex, by), right.substituteBound(scopeIndex, by))
  def inc(scopeIndex: Int) = ApplicationNode(left.inc(scopeIndex), right.inc(scopeIndex))

  // Other operations
  def betaNormalize = left match {
    case AbstractionNode(_, body) => body.substituteBound(1, right)
    case _ => this
  }
  // Pretty printing
  def pretty = "(" + left.pretty + " " + right.pretty + ")"
}

