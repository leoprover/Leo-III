package leo.modules.proofCalculi

import leo.datastructures._
import leo.modules.output.Output




trait ParamodStep extends Output{

  /**
   * Executes a step of the Paramodulation.
   *
   * @param c - First clause
   * @param d - Second clause
   * @param lc - Term in first clause
   * @param ld - Literal in second clause (not contained)
   * @param s - Substitution of the paramodulation
   * @return new generated clause
   */
  def exec(c : Clause, d : Clause, lc : Term, ld : Literal, s :Unification#Substitute) : Clause

  def find(c1: Clause, c2: Clause, comp: Unification): Option[(Term, Literal, Unification#Substitute)]
}

object PropParamodulation extends ParamodStep{
  /**
   *
   * Executes Propositional Resolution in Superposition
   *
   *
   * C[l']    D \/ [l] = \alpha  s(l') = s(l)
   * --------------------------------------------
   * (C[\alpha] \/ D) s
   *
   * @param c - First Clause
   * @param d - Second Clause
   * @param lc - Term to be replaced in first clause
   * @param ld  - Literal of form [l] = \alpha, NOT CONTAINED IN d
   * @param s - s(lc) = s(ld.term) according to comparrison
   * @return
   */
  override def exec(c: Clause, d: Clause, lc: Term, ld: Literal, s: Unification#Substitute): Clause = {

    val alpha: Term = if (ld.polarity) LitTrue else LitFalse
    val cSub = c.replace(lc, alpha)
    val merged = cSub.merge(d)
    //    leo.Out.severe("What: "+lc.pretty)
    //    leo.Out.severe("By: "+alpha.pretty)
    val res = Clause.mkClause(merged.substitute(s._1).lits, s._2 ++ merged.implicitBindings, Derived)
    return TrivRule.triv(TrivRule.teqf(Simp(res)))
  }

  /**
   * TODO: Use Term comparison. Currently simple equality is used.
   *
   * @param c1 - First clause
   * @param c2 - Second clause
   * @param comp - comparison object, if two terms are unifiable
   * @return (t,l,s), where t is the selected first term, l is the literal and s is a substitiontion, that makes both equal.
   */
  override def find(c1: Clause, c2: Clause, comp: Unification): Option[(Term, Literal, Unification#Substitute)] = {
    if(c1.lits.isEmpty || c2.lits.isEmpty) return None

    val lits = c2.lits.iterator
    while (lits.hasNext) {
      val lit = lits.next()
      val t = lit.term
      if (c1.lits.exists { l => (l.term.occurrences.keys.toSet).contains(t)})
        return Some(t, lit, (Subst.id, Nil))
    }

    return None
  }

  override def output: String = "Paramod-Propositional"
}

  object Paramodulation extends ParamodStep{

    private def decomp(l: Literal): Option[(Term, Term)] = l.term match {
      case ===(t1,t2) => Some(t1,t2)
      case _ => None
    }

    /**
     *
     * Executes Propositional Resolution in Superposition
     *
     *
     * C[l']    D \/ [l = r] = T  s(l') = s(l)
     * --------------------------------------------
     *            (C[r] \/ D) s
     *
     * @param c - First Clause
     * @param d - Second Clause
     * @param lc - Term to be replaced in first clause
     * @param ld  - Literal in the form [l = r] = T, NOT CONTAINED IN d
     * @param s - s(lc) = s(ld.term) according to comparrison
     * @return
     */
    override def exec(c: Clause, d: Clause, lc: Term, ld: Literal, s: Unification#Substitute): Clause = {
      val (l,r) = decomp(ld).get


      val cSub = c.replace(lc, r)
      val merged = cSub.merge(d)
      //    leo.Out.severe("What: "+lc.pretty)
      //    leo.Out.severe("By: "+alpha.pretty)
      val res = Clause.mkClause(merged.substitute(s._1).lits, s._2 ++ merged.implicitBindings, Derived)
      return TrivRule.triv(TrivRule.teqf(Simp(res)))
    }

    /**
     * TODO: Use Term comparison. Currently simple equality is used.
     *
     * @param c1 - First clause
     * @param c2 - Second clause
     * @param comp - comparison object, if two terms are unifiable
     * @return (t,l,s), where t is the selected first term, l is the literal and s is a substitiontion, that makes both equal.
     */
    override def find(c1: Clause, c2: Clause, comp: Unification): Option[(Term, Literal, Unification#Substitute)] = {

      if(c1.lits.isEmpty || c2.lits.isEmpty) return None

      val lits = c2.lits.iterator
      while (lits.hasNext) {
        val lit = lits.next()
        decomp(lit) match {
          case Some((l,r)) if lit.polarity =>
            if (c1.lits.exists { lt => (lt.term.occurrences.keys.toSet).contains(l)})
              return Some(l, lit, (Subst.id, Nil))
          case _ =>
        }
      }

      return None
    }

    override def output: String = "Paramod-Full"
  }

trait CalculusRule {
  def name: String
}
trait UnaryCalculusRule[R] extends (Clause => R) with CalculusRule {
    def canApply(cl: Clause): Boolean
}

/**
 * {{{
 *    C \/ [Q U^k]^\alpha , P general binding for `hdSymb`
 *   ------------------------------------
 *     V[Q/P] \/ [P U^k]^\alpha
 * }}}
 */
class PrimSubst(hdSymbs: Set[Term]) extends UnaryCalculusRule[Set[Clause]] {
  val name = "prim_subst"

    def canApply(cl: Clause) = cl.flexHeadLits.nonEmpty

    def apply(cl: Clause): Set[Clause] = hdSymbs.map{hdSymb =>
      val vars = cl.flexHeadLits.map(_.term.headSymbol)
      vars.map{case hd =>
        val binding = HuetsPreUnification.partialBinding(hd.ty, hdSymb)
        val subst = Subst.singleton(hd.metaIndices.head, binding)
        Clause.mkClause(cl.lits.map(_.termMap(_.substitute(subst).betaNormalize)), cl.implicitBindings, Derived, ClauseAnnotation(this, cl))
      }
    }.flatten
}

object StdPrimSubst extends PrimSubst(Set(Not, LitFalse, LitTrue, |||))


  // TODO: Optimize
  object Simp {
    def apply (c : Clause) : Clause = {
      import leo.modules.normalization.Simplification

      val litNorm = Simplification.normalize(c).mapLit(flipNeg)

      // Remove unnused Quantifiers.

      val looseBounds : Set[Int] = litNorm.map(_.term.looseBounds).toSet.flatten
      val implicitQuan : Seq[Type] = c.implicitBindings

      val misBound = looseBounds.diff(Range.apply(1,implicitQuan.size).toSet)

      val liftLits = litNorm.map(_.termMap(_.closure(liftMissingBound(misBound, implicitQuan.size)).betaNormalize))

      return Clause.mkClause(liftLits, removeBounds(implicitQuan, misBound, implicitQuan.length), Derived)
    }

    private def flipNeg(l : Literal) : Literal = l.term match {
      case Not(f) => l.flipPolarity.termMap(_ => f)
      case _ => l
    }

    /*
     * Returns subsitution and positions of implicitQuan to delete
     */
    private def liftMissingBound(m : Set[Int], maxBind : Int) : Subst = {
      var pos : Int = 1
      var free : Int = 1
      var s = Subst.id
      while(pos <= maxBind) {
        s = s.cons(BoundFront(free))    // If it is not contained, it will never substitute this value
        if(m.contains(pos)) free += 1
      }
      s
    }

    private def removeBounds(b : Seq[Type], m : Set[Int], pos : Int) : Seq[Type] = b match {
      case Seq() => Seq()
      case x +: xs if m.contains(pos) => removeBounds(xs, m, pos-1)
      case x +: xs => x +: removeBounds(xs, m, pos-1)
    }
  }
