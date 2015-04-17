package leo.modules.proofCalculi

import leo.datastructures.term.Term
import leo.datastructures.{Type, Subst}

trait Unification {
  type Substitute = (Subst,Seq[Type])

  /**
   *
   * @param t - First term to unify
   * @param s - Second term to unify
   * @param n - Offset for new implicit Bindings (n+1 will be the next binding)
   * @return a stream of Substitution to make both terms equal, empty stream if they are not unifiable
   */
  def unify(t : Term, s : Term, n : Int) : Stream[Substitute]
}

/**
 * Tests solely for equality
 */
object IdComparison extends Unification{
  override def unify(t : Term, s : Term, n : Int) : Stream[Substitute] = if (s == t) Stream((Subst.id, Nil)) else Stream.empty
}