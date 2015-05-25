package leo.modules.calculus

import leo.datastructures._
import leo.modules.normalization.{NegationNormal, Simplification}
import leo.modules.output.Output
import leo.modules.calculus.enumeration.SimpleEnum


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
    val res = NegationNormal.normalize(Simplification.normalize(Clause.mkClause(merged.substitute(s._1).lits, s._2 ++ merged.implicitBindings, Derived)))
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
      val res = NegationNormal.normalize(Simp(Clause.mkClause(merged.substitute(s._1).lits, s._2 ++ merged.implicitBindings, Derived)))
      return TrivRule.triv(TrivRule.teqf(res))
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




trait ParamodRule extends BinaryCalculusRule[Set[Clause], (Set[(Literal, (Term, Term), Term)],Set[(Literal, (Term, Term), Term)])]

object NewParamod extends ParamodRule {
  type EqLit = Literal  // the equality literal that causes the paramodulation
  type TTR = Term // term to replace in literal (since it unifies with a side of the EqLit)
  type DirEq = (Term, Term)  // the terms of that equality sorted by: (term that unifies with TTR, term that will be replaced for TTR)

  type sideHint = Set[(EqLit, DirEq, TTR)]

  def canApply(cl1: Clause, cl2: Clause): (Boolean, HintType) = {
    var left_termsThatMayUnify: Set[(EqLit, DirEq, TTR)] = Set()
    var right_termsThatMayUnify: Set[(EqLit, DirEq, TTR)] = Set()

    val (eqLits1, eqLits2) = (cl1.eqLits, cl2.eqLits)
    // for equalities from left clause
    val eqLits1It = eqLits1.iterator

    while(eqLits1It.hasNext) {
      val eqLit = eqLits1It.next()
      val (l,r) = eqLit.eqComponents.get

      val lits2 = cl2.lits.iterator

      while(lits2.hasNext) {
        val otherLit = lits2.next()
        val subterms = otherLit.term.occurrences.keySet.iterator
        while (subterms.hasNext) {
          val st = subterms.next()
          if (!st.isVariable && mayUnify(st, l)) {
            left_termsThatMayUnify = left_termsThatMayUnify + ((eqLit, (l, r), st))
          }
          if (!st.isVariable && mayUnify(st, r)) {
            left_termsThatMayUnify = left_termsThatMayUnify + ((eqLit, (r, l), st))
          }
        }
      }
    }

    val eqLits2It = eqLits1.iterator

    while(eqLits2It.hasNext) {
      val eqLit = eqLits2It.next()
      val (l,r) = eqLit.eqComponents.get

      val lits1 = cl1.lits.iterator

      while(lits1.hasNext) {
        val otherLit = lits1.next()
        val subterms = otherLit.term.occurrences.keySet.iterator
        while (subterms.hasNext) {
          val st = subterms.next()
          if (!st.isVariable && mayUnify(st, l)) {
            right_termsThatMayUnify = right_termsThatMayUnify + ((eqLit, (l, r), st))
          }
          if (!st.isVariable && mayUnify(st, r)) {
            right_termsThatMayUnify = right_termsThatMayUnify + ((eqLit, (r, l), st))
          }
        }
      }
    }
    (right_termsThatMayUnify.nonEmpty || left_termsThatMayUnify.nonEmpty,(left_termsThatMayUnify,right_termsThatMayUnify))
  }

  def apply(cl1: Clause, cl2: Clause, hint: (Set[(EqLit, DirEq, TTR)],Set[(EqLit, DirEq, TTR)])) = {
    var newCls : Set[Clause] = Set()

    // for equalities from left clause
    val leftHint = hint._1
    val leftIt = leftHint.iterator
    while (leftIt.hasNext) {
      val (eqLit, (left,right), ttr) = leftIt.next()
      val restLits = cl1.lits.filterNot(_ == eqLit)
      val uniConstraint = Literal.mkUniLit(left, ttr)
      val replLits = cl2.replace(ttr, right).lits
      newCls = newCls + TrivRule.triv(TrivRule.teqf(Simp(Clause.mkClause(restLits ++ replLits :+ uniConstraint, Derived))))
    }

    // for equalities from right clause
    val rightHint = hint._2
    val rightIt = rightHint.iterator
    while (rightIt.hasNext) {
      val (eqLit, (left,right), ttr) = rightIt.next()
      val restLits = cl2.lits.filterNot(_ == eqLit)
      val uniConstraint = Literal.mkUniLit(left, ttr)
      val replLits = cl1.replace(ttr, right).lits
      newCls = newCls + TrivRule.triv(TrivRule.teqf(Simp(Clause.mkClause(restLits ++ replLits :+ uniConstraint, Derived))))
    }

    newCls.filterNot(TrivRule.teqt)
  }

  def name = "new_paramod"
}

object NewPropParamod extends ParamodRule {
  type EqLit = Literal  // the equality literal that causes the paramodulation
  type TTR = Term // term to replace in literal (since it unifies with a side of the EqLit)
  type DirEq = (Term, Term)  // the terms of that equality sorted by: (term that unifies with TTR, term that will be replaced for TTR)

  type sideHint = Set[(EqLit, DirEq, TTR)]


  def canApply(cl1: Clause, cl2: Clause): (Boolean, HintType) = {

    var left_termsThatMayUnify: Set[(EqLit, DirEq, TTR)] = Set()
    var right_termsThatMayUnify: Set[(EqLit, DirEq, TTR)] = Set()

    val (eqLits1, eqLits2) = (cl1.lits, cl2.lits)
    // for equalities from left clause
    val eqLits1It = eqLits1.iterator

    while(eqLits1It.hasNext) {
      val eqLit = eqLits1It.next()
      val (l,r): (Term, Term) = (eqLit.term, if (eqLit.polarity) LitTrue else LitFalse)

      val lits2 = cl2.lits.iterator

      while(lits2.hasNext) {
        val otherLit = lits2.next()
        val subterms = otherLit.term.occurrences.keySet.iterator
        while (subterms.hasNext) {
          val st = subterms.next()
          if (!st.isVariable && mayUnify(st, l)) {

//            println(s"paramod ${cl1.pretty} with ${cl2.pretty}")
//println(s"subterms of otherLit: ${ otherLit.term.occurrences.keySet.map(_.pretty).mkString("\n")}")
//            println(s"1. may unify: ${st.pretty} with ${l.pretty}")
            left_termsThatMayUnify = left_termsThatMayUnify + ((eqLit, (l, r), st))
          }
        }
      }
    }

    val eqLits2It = eqLits2.iterator

    while(eqLits2It.hasNext) {
      val eqLit = eqLits2It.next()
      val (l,r): (Term, Term) = (eqLit.term, if (eqLit.polarity) LitTrue else LitFalse)

      val lits1 = cl1.lits.iterator

      while(lits1.hasNext) {
        val otherLit = lits1.next()
        val subterms = otherLit.term.occurrences.keySet.iterator
        while (subterms.hasNext) {
          val st = subterms.next()
          if (!st.isVariable && mayUnify(st, l)) {
//            println(s"2. may unify: ${st.pretty} with ${l.pretty}")
            right_termsThatMayUnify = right_termsThatMayUnify + ((eqLit, (l, r), st))
          }
        }
      }
    }
    (right_termsThatMayUnify.nonEmpty || left_termsThatMayUnify.nonEmpty,(left_termsThatMayUnify,right_termsThatMayUnify))
  }

  def apply(cl1: Clause, cl2: Clause, hint: (Set[(EqLit, DirEq, TTR)],Set[(EqLit, DirEq, TTR)])) = {
    var newCls : Set[Clause] = Set()

    // for equalities from left clause
    val leftHint = hint._1
    val leftIt = leftHint.iterator
    while (leftIt.hasNext) {
      val (eqLit, (left,right), ttr) = leftIt.next()
      val restLits = cl1.lits.filterNot(_ == eqLit)
      val uniConstraint = Literal.mkUniLit(left, ttr)
      val replLits = cl2.replace(ttr, right).lits
      newCls = newCls + TrivRule.triv(TrivRule.teqf(Simp(Clause.mkClause(restLits ++ replLits :+ uniConstraint, Derived))))
    }

    // for equalities from right clause
    val rightHint = hint._2
    val rightIt = rightHint.iterator
    while (rightIt.hasNext) {
      val (eqLit, (left,right), ttr) = rightIt.next()
      val restLits = cl2.lits.filterNot(_ == eqLit)
      val uniConstraint = Literal.mkUniLit(left, ttr)
      val replLits = cl1.replace(ttr, right).lits
      newCls = newCls + TrivRule.triv(TrivRule.teqf(Simp(Clause.mkClause(restLits ++ replLits :+ uniConstraint, Derived))))
    }

    newCls.filterNot(cl => TrivRule.teqt(cl))
  }

  def name = "new_paramod_prop"
}


/**
 * {{{
 *    C \/ [Q U^k]^\alpha , P general binding for `hdSymb`
 *   ------------------------------------
 *     V[Q/P] \/ [P U^k]^\alpha
 * }}}
 *
 * Hint not needed since its implemented in clause.
 */
class PrimSubst(hdSymbs: Set[Term]) extends UnaryCalculusHintRule[Set[Clause], Set[Term]] {
  val name = "prim_subst"

  def canApply(cl: Clause) = (cl.flexHeadLits.nonEmpty, cl.flexHeadLits.map(_.term.headSymbol))

  def apply(cl: Clause, hint: HintType): Set[Clause] = hdSymbs.map{hdSymb =>
      val vars = hint
      vars.map{case hd =>
        val binding = HuetsPreUnification.partialBinding(hd.ty, hdSymb)
        val subst = Subst.singleton(hd.metaIndices.head, binding)
        TrivRule.teqf((Simp(Clause.mkClause(cl.lits.map(_.termMap(_.substitute(subst).betaNormalize)), cl.implicitBindings, Derived))))
      }
    }.flatten.filterNot(TrivRule.teqt)
}

object StdPrimSubst extends PrimSubst(Set(Not, LitFalse, LitTrue, |||))

object RestrFac extends UnaryCalculusHintRule[Clause, Option[(Literal, Literal)]] {
  val name = "restr_fac"

  def canApply(cl: Clause) = if (cl.lits.length == 2) {
    val (l1, l2) = (cl.lits(0), cl.lits(1))
    if (l1.polarity == l2.polarity) {
      (true, None)
    } else {
      (l1.flexHead || l2.flexHead, if (l1.flexHead) Some(l1, l2) else Some(l2, l1))
    }
  } else
    (false, None)

  def apply(cl: Clause, hint: HintType): Clause = {
    if (hint.isEmpty) {
      // same polarity
      val (l1, l2) = (cl.lits(0), cl.lits(1))
      val keep = if (l1.flexHead) l1 else l2
      Clause.mkClause(Seq(keep, Literal.mkUniLit(l1.term, l2.term)), Derived)
    } else {
      val (l1, l2) = hint.get
      Clause.mkClause(Seq(l1, Literal.mkUniLit(l1.term, Not(l2.term))), Derived)
    }
  }
}


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
