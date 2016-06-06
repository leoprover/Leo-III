package leo.modules.calculus


package object matching {
  import leo.datastructures.Term

  type UEq = (Term, Term)
}

package matching {

  import scala.annotation.tailrec

  /**
    * Created by lex on 6/4/16.
    */
  object FOMatching {
    import leo.datastructures.{Term, Type, Subst}
    import Term.{Bound, ∙}


    /**
      * `s` matches `t` iff there exists a substitution sigma such that `s[sigma] = t`.
      *
      * @param s A term.
      * @param t The term to be matched against (i.e. the term that may be an instance of `s`)
      * @param fv If used in clausal context, specify the set of free variables. Used for extensionality treatment.
      */
    final def decideMatch(s: Term, t: Term, fv:  Seq[(Int, Type)] = Seq()): Boolean = decideMatch0(Vector((s,t)), fv)


    // apply exaustively delete, comp and bind on the set. If at the end uproblems is empty,
    // we succeeded, else we fail.
    // Invariant: UEq (l,r) always never swapper to (r',l') where r',l' originate from r,l, respectively.
    @tailrec
    final private def decideMatch0(uproblems: Seq[UEq], fv:  Seq[(Int, Type)]): Boolean  = {
      leo.Out.trace(s"Unsolved: ${uproblems.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
      // apply delete
      val ind1 = uproblems.indexWhere(DeleteRule.canApply)
      if (ind1 > -1) {
        leo.Out.finest("Apply Delete")
        decideMatch0(uproblems.take(ind1) ++ uproblems.drop(ind1 + 1), fv)
        // apply decomp
      } else {
        val ind2 = uproblems.indexWhere(DecompRule.canApply)
        if (ind2 > -1) {
          leo.Out.finest("Apply Decomp")
          decideMatch0(DecompRule(uproblems(ind2)) ++ uproblems.take(ind2) ++ uproblems.drop(ind2 + 1), fv)
          // apply bind
        } else {
          val ind3 = uproblems.indexWhere(BindRule.canApply)
          if (ind3 > -1) {
            leo.Out.finest("Apply Bind")
            leo.Out.finest(s"Bind on " +
              s"\n\tLeft: ${uproblems(ind3)._1.pretty}\n\tRight: ${uproblems(ind3)._2.pretty}")
            val be = BindRule(uproblems(ind3))
            leo.Out.finest(s"Resulting equation: ${be._1.pretty} = ${be._2.pretty}")
            val sb = computeSubst(be)
            decideMatch0(applySubstToList(sb, uproblems.take(ind3) ++ uproblems.drop(ind3 + 1)), fv)
          } else {
            val ind4 = uproblems.indexWhere(FuncRule.canApply)
            if (ind4 > -1) {
              leo.Out.finest(s"Can apply func on: ${uproblems(ind4)._1.pretty} == ${uproblems(ind4)._2.pretty}")
              decideMatch0((uproblems.take(ind4) :+ FuncRule(fv, uproblems(ind4))) ++ uproblems.drop(ind4 + 1), fv)
            } else
              uproblems.isEmpty
          }
        }
      }
    }

    private final def computeSubst(eqn: UEq): Subst = {
      val (t,s) = (eqn._1, eqn._2)
      assert(isVariable(t))
      val (_, idx) = Bound.unapply(t).get
      Subst.singleton(idx, s)
    }

    private final def applySubstToList(s: Subst, l: Seq[UEq]): Seq[UEq] =
          l.map(e => (e._1.substitute(s),e._2.substitute(s)))

    private final def isVariable(t: Term): Boolean = Bound.unapply(t).isDefined
    private final def isFlexible(t: Term): Boolean = Bound.unapply(t.headSymbol).isDefined

    /**
      * 3
      * BindRule tells if Bind is applicable
      * equation is not oriented
      * return an equation (x,s) substitution is computed from this equation later
      */
    private object BindRule {
      def apply(e: UEq) = {
        val (t,s) = (e._1,e._2)
        (t.headSymbol,s)
      }
      def canApply(e: UEq) = {
        val (t,s) = (e._1, e._2)
        if (!isFlexible(t)) false
        else {
          val (_,x) = Bound.unapply(t.headSymbol).get
          if (!t.headSymbol.etaExpand.equals(t) && !t.equals(t.headSymbol)) false // t might be of function type,
            // hence it might be eta expanded to a function, we check that here.

          // check it doesnt occur in s
          else !s.looseBounds.contains(x)
        }
      }
    }

    /**
      * 1
      * returns true if the equation can be deleted
      */
    private object DeleteRule  {
      def apply(e: UEq) = ()
      def canApply(e: UEq) = {
        val (t,s) = e
        t.equals(s)
      }
    }

    /**
      * 2
      * returns the list of equations if the head symbols are the same function symbol.
      */
    private object DecompRule {
      def apply(e: UEq) = e match {
        case (_ ∙ sq1, _ ∙ sq2) => (simplifyArguments(sq1)).zip(simplifyArguments(sq2))
        case _ => throw new IllegalArgumentException("impossible")
      }
      def canApply(e: UEq) = e match {
        case (hd1 ∙ _, hd2 ∙ _) if (hd1 == hd2) && !isFlexible(hd1) => true
        case _ => false
      }
    }

    private object FuncRule {

      def apply(freevars:  Seq[(Int, Type)], e: UEq): UEq = {
        leo.Out.trace(s"Apply Func on ${e._1.pretty} = ${e._2.pretty}")
        val funArgTys = e._1.ty.funParamTypes
        val skTerms = funArgTys.map(leo.modules.calculus.skTerm(_, freevars))
        (Term.mkTermApp(e._1, skTerms).betaNormalize, Term.mkTermApp(e._2, skTerms).betaNormalize)
      }

      def canApply(e: UEq) = {
        // we can apply it if the sides of the equation have functional type
        assert(e._1.ty == e._2.ty, s"Func Rule: Both UEq sides have not-matching type:\n\t${e._1.pretty}\n\t${e._1.ty.pretty}\n\t${e._2.pretty}\n\t${e._2.ty.pretty}")
        e._1.ty.isFunType
      }
    }

    private final def simplifyArguments(l: Seq[Either[Term,Type]]): Seq[Term] = l.filter(_.isLeft).map(_.left.get)

  }


}