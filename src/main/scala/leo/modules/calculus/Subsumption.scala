package leo.modules.calculus

import leo.datastructures.{Clause, Literal, Term}

/** Trait for subsumption algorithms. */
trait Subsumption {
  def subsumes(cl1: Clause, cl2: Clause): Boolean
}

object Subsumption extends Subsumption {
  val impl = FOMatchingSubsumption
  var subsumptiontests = 0
  def subsumes(cl1: Clause, cl2: Clause): Boolean = {subsumptiontests += 1; impl.subsumes(cl1, cl2)}
}

object TrivialSubsumption extends Subsumption {
  def subsumes(cl1: Clause, cl2: Clause): Boolean = {
    val (lits1, lits2) = (cl1.lits, cl2.lits)
    if (lits1.length <= lits2.length) {
      lits1.forall(l1 => lits2.exists(l2 => l1.polarity == l2.polarity && l1.unsignedEquals(l2)))
    } else {
      false
    }
  }
}

abstract class AbstractMatchingSubsumption extends Subsumption {
  import leo.datastructures.Subst

  def subsumes(cl1: Clause, cl2: Clause): Boolean = {
    val (lits1, lits2) = (cl1.lits, cl2.lits)

    if (lits1.length < lits2.length) {
      val liftedLits1 = lits1.map(_.substitute(Subst.shift(cl2.maxImplicitlyBound)))
      val vargen = freshVarGen(Clause(liftedLits1 ++ lits2))
      subsumes0(vargen, liftedLits1, lits2, Vector.empty)
    } else
      false
  }

  private final def subsumes0(vargen: FreshVarGen, lits1: Seq[Literal], lits2: Seq[Literal], visited: Seq[Literal]): Boolean = {
    if (lits1.isEmpty) true
    else {
      if (lits2.isEmpty) false
      else {
        val (hd1, tail1) = (lits1.head, lits1.tail)
        val (hd2, tail2) = (lits2.head, lits2.tail)
        if (hd1.polarity == hd2.polarity) {
          val (term1, term2) = (Literal.asTerm(hd1), Literal.asTerm(hd2))
          val matchingResult = doMatch(vargen, term1, term2)
          if (matchingResult.isDefined) {
            val subst = matchingResult.get
            val newTail1 = tail1.map(_.substitute(subst))
            if (subsumes0(vargen, newTail1, lits2 ++ visited, Vector.empty))
              true
            else
              subsumes0(vargen, lits1, tail2, hd2 +: visited)
          } else {
            subsumes0(vargen, lits1, tail2, hd2 +: visited)
          }
        } else {
          subsumes0(vargen, lits1, tail2, hd2 +: visited)
        }
      }
    }
  }

  def doMatch(vargen: FreshVarGen, term: Term, term1: Term): Option[TermSubst]
}

object FOMatchingSubsumption extends AbstractMatchingSubsumption {
  override final def doMatch(vargen: FreshVarGen, s: Term, t: Term): Option[TermSubst] = FOMatching.matches(s,t)
}

object HOMatchingSubsumption extends AbstractMatchingSubsumption {
  override final def doMatch(vargen: FreshVarGen, s: Term, t: Term): Option[TermSubst] = {
    val result0 = HOMatching.matchTerms(vargen, s,t).iterator
    if (result0.isEmpty) None
    else Some(result0.next()._1)
  }
}
