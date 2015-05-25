package leo.modules.calculus

import leo.datastructures._

/**
 * Rule for an easy call to the TrivRule to minimize Clauses in other Rules.
 *
 * @author Max Wisniewski
 * @since 1/13/15
 */
object TrivRule {

  /**
   * Removes all Literals of the form [ T = F ] or [ F = T ]
   *
   * @param c - sequence of literals
   *
   * @return the sequence of literals without trivial contradictions
   */
  def teqf(c : Seq[Literal]) : Seq[Literal] = c filter {l => l.term match{
    case LitTrue() if !l.polarity => false
    case LitFalse() if l.polarity => false
    case ===(LitTrue(), LitFalse()) if l.polarity => false
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
  def teqt(c : Clause) : Boolean = if(!c.lits.isEmpty) {
    c.lits exists  {l => l.term match {
      case LitTrue() if l.polarity => true
      case LitFalse() if !l.polarity => true
      case ===(s,t) if s == t && l.polarity => true
      case _                    => false
    }}
  }
  else false

  def triv(c : Seq[Literal]) : Seq[Literal] = ltriv(c.toList)

  private def ltriv(c : List[Literal]) : List[Literal] = c match {
    case Nil => Nil
    case x :: xs => x :: ltriv(xs.filterNot(_.cong(x)))
  }

  def triv(c : Clause) : Clause = Clause.mkClause(triv(c.lits),c.implicitBindings, Derived)


  private def simpEq(lt : Literal) : Literal = lt.termMap(_ match {
    case ===(l,r) => if (l==r) LitTrue() else ===(l,r)
    case t => t
  })

  def simpEq(c : Clause) : Clause = c.mapLit(simpEq(_))
}
