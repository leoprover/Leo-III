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
   * @param ld  - Literal of form [l] = \alpha, NOT CONTAINED IN d
   * @param s - s(lc) = s(ld.term) according to comparrison
   * @return
   */
  def exec(c : Clause , d : Clause, lc : Term, ld : Literal , s : TermComparison#Substitute) : Clause = {

    val alpha : Term = if (ld.polarity) LitTrue else LitFalse
    val cSub = c.replace(lc, alpha)
    val merged = cSub.merge(d)
//    leo.Out.severe("What: "+lc.pretty)
//    leo.Out.severe("By: "+alpha.pretty)
    val res = Clause.mkClause(merged.substitute(s._1).lits,s._2++merged.implicitBindings, Derived)
    return res
  }

  /**
   * TODO: Use Term comparison. Currently simple equality is used.
   *
   * @param c1 - First clause
   * @param c2 - Second clause
   * @param comp - comparison object, if two terms are unifiable
   * @return (t,l,s), where t is the selected first term, l is the literal and s is a substitiontion, that makes both equal.
   */
  def find(c1 : Clause, c2 : Clause, comp : TermComparison) : Option[(Term, Literal, TermComparison#Substitute)] = {

    val lits = c2.lits.iterator
    while(lits.hasNext) {
      val lit = lits.next()
      val t = lit.term
      if (c1.lits.exists{l => (l.term.occurrences.keys.toSet).contains(t)})
        return Some(t, lit, (Subst.id, Nil))
    }

    return None
  }
}
