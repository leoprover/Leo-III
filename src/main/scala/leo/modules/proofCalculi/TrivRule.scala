package leo.modules.proofCalculi

import leo.datastructures._

/**
 * Rule for an easy call to the TrivRule to minimize Clauses in other Rules.
 *
 * @author Max Wisniewski
 * @since 1/13/15
 */
object TrivRule {
  def apply(c : Clause) : Clause = ???

  /**
   * Removes all Literals of the form [ T = F ] or [ F = T ]
   *
   * @param c - sequence of literals
   *
   * @return the sequence of literals without trivial contradictions
   */
  def teqf(c : Seq[Literal]) : Seq[Literal] = c filter {l => l.term match{
    case LitTrue if !l.polarity => false
    case LitFalse if l.polarity => false
    case _                      => true
  }}

  /**
   * As teqf(Seq[Literal].
   *
   * @param c - Clause
   * @return `c` without trivial contradictions.
   */
  def teqf(c : Clause) : Clause = Clause.mkClause(teqf(c.lits),c.implicitBindings, Derived)

  /**
   *
   * Checks if a clause does not contribute to the context at all,
   * i.e. it is trivially true.
   *
   * @param c - The clause
   * @return true, iff [T = T] or [F = F] is contained.
   */
  def teqt(c : Clause) : Boolean = c.lits exists  {l => l.term match {
    case LitTrue if l.polarity => true
    case LitFalse if !l.polarity => true
    case _                    => false
  }}

  def triv(c : Seq[Literal]) : Seq[Literal] = c match {
    case x +: Seq() => Seq(x)
    case x +: xs => x +: xs.filter(_ != x)
  }
}
