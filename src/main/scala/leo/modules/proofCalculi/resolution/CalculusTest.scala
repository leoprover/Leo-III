package leo.modules.proofCalculi

import leo.datastructures.term.Term
import leo.datastructures._

trait TermComparison {
  type Substitute = (Subst,Seq[Type])

  /**
   *
   * @param t - First term to unify
   * @param s - Second term to unify
   * @param n - Offset for new implicit Bindings (n+1 will be the next binding)
   * @return None, if not unifiable, Some(sub, tys) with s substitution s.t. sub[s] = sub[t] and tys the new additional implicit bindings.
   */
  def equals(t : Term, s : Term, n : Int) : Option[Substitute]
}

/**
 * Tests solely for equality
 */
object IdComparison extends TermComparison{
  override def equals(t : Term, s : Term, n : Int) : Option[Substitute] = if (s == t) Some((Subst.id, Nil)) else None
}

object PropResolution {
  /**
   *
   * Executes Propositional Resolution in Superposition
   *
   *
   *    C[l']    D \/ [l] = \alpha  s(l') = s(l)
   * --------------------------------------------
   *          Simp( (C[\alpha] \/D) s)
   *
   * @param c - First Clause
   * @param d - Second Clause
   * @param lc - Term to be replaced in first clause
   * @param ld  - Literal of form [l] = \alpha
   * @param s - s(lc) = s(ld.term) according to comparrison
   * @return
   */
  def exec(c : Clause , d : Clause, lc : Term, ld : Literal , s : TermComparison#Substitute) : Clause = {

    val alpha : Term = if (ld.polarity) LitTrue else LitFalse
    val cSub = c.replace(lc, alpha)
    val merged = cSub.merge(d)
    val res = Clause.mkClause(merged.substitute(s._1).lits,s._2++merged.implicitBindings, Derived)
    return res
  }


  def find(c1 : Clause, c2 : Clause, comp : TermComparison) : Option[(Term, Literal, TermComparison#Substitute)] = {

    

    return None
  }
}

/**
 *
 * Test implementation for Superposition.
 *
 * @author Max Wisniewski
 * @since 12/3/14
 */
class CalculusTest {

}
