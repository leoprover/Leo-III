package datastructures.internal

/**
 * Created by lex on 29.05.14.
 */
sealed abstract class RefTermImpl(val abs: RefTermImpl, val leftApp: RefTermImpl, val rightApp: RefTermImpl) extends RefTerm {
  // Predicates on terms
  val isAtom: Boolean
  val isTermApp = (leftApp != null) && (rightApp != null)
  val isTermAbs = abs != null
  val isTypeApp = ???
  val isTypeAbs = ???


  // Queries on terms
  def ty: Type
  def freeVars = Set.empty
  def herbrandUniverse = Set.empty
  // Substitutions
  def substitute(what: RefTerm, by: RefTerm) = this

}

object RefTermImpl {
  private case class AbstractionNode(absType: Type, term: RefTermImpl) extends RefTermImpl(term, null, null) {

    def ty = absType ->: term.ty


    // Other operations
    def betaNormalize = AbstractionNode(absType, term.betaNormalize)

    // Pretty printing
    def pretty = "\\. " + term.pretty
  }
}