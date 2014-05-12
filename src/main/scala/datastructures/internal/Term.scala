package datastructures.internal

import datastructures.Pretty

/**
 * Created by lex on 29.04.14.
 */
abstract sealed class Term extends Pretty {

  // Predicates on terms
  def isVar: Boolean = false
  def isConst: Boolean = false
  def isTermApp: Boolean = false
  def isTermAbs: Boolean = false
  def isTypeApp: Boolean = false
  def isTypeAbs: Boolean = false
  def isApplicable(arg: Term): Boolean

  // Queries on terms
  def getType: Option[Type]
  def getFreeVars: Set[Variable]

  // Substitutions
  // ...

  // Other operations
  def betaNormalize: Term
  // compare, order...
}

protected[internal] case class TermApp(left: Term, right: Term) extends Term {
  def pretty = left.pretty + " " + right.pretty

  override def isTermApp = true
  def isApplicable(arg: Term): Boolean = ???

  def getType: Boolean = ???
  def getFreeVars: Set[Variable] = left.getFreeVars ++ right.getFreeVars

  def betaNormalize: Term = this // do not normalize yet

}



object Term { // extends HOLTerm {

}
