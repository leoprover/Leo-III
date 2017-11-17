package leo.modules.calculus

import leo.datastructures.{Clause, Literal, Term}

/** Trait for subsumption algorithms. */
trait Subsumption {
  def subsumes(cl1: Clause, cl2: Clause): Boolean
}

object Subsumption extends Subsumption {
  val impl = HOPatternSubsumption
  var subsumptiontests = 0
  def subsumes(cl1: Clause, cl2: Clause): Boolean = {subsumptiontests += 1; impl.subsumes(cl1, cl2)}
}

object TrivialSubsumption extends Subsumption {
  def subsumes(cl1: Clause, cl2: Clause): Boolean = {
    val (lits1, lits2) = (cl1.lits, cl2.lits)
    if (lits1.length < lits2.length) {
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
      val forbiddenVars = cl2.implicitlyBound.map(_._1).toSet
      val result = subsumes0(vargen, liftedLits1.toVector, lits2.toVector, Vector.empty, forbiddenVars)
      if (result) {
        leo.Out.finest(s"subsumes: true")
        true
      } else {
        leo.Out.finest(s"subsumes: false")
        false
      }
    } else
      false
  }

  private final def subsumes0(vargen: FreshVarGen, lits1: Seq[Literal], lits2: Seq[Literal],
                              visited: Seq[Literal], forbiddenVars: Set[Int]): Boolean = {
    leo.Out.finest(s"vargen: ${vargen.existingVars.toString()}")
    leo.Out.finest(s"lits1: ${lits1.map(_.pretty).mkString(",")}")
    leo.Out.finest(s"lits2: ${lits2.map(_.pretty).mkString(",")}")
    leo.Out.finest(s"visited: ${visited.map(_.pretty).mkString(",")}")

    if (lits1.isEmpty) true
    else {
      if (lits2.isEmpty) false
      else {
        val (hd1, tail1) = (lits1.head, lits1.tail)
        val (hd2, tail2) = (lits2.head, lits2.tail)
        if (hd1.polarity == hd2.polarity) {
          val (term1, term2) = (Literal.asTerm(hd1), Literal.asTerm(hd2))
          val matchingResult = doMatch(vargen, term1, term2, forbiddenVars)
          if (matchingResult.isDefined) {
            val (termSubst, typeSubst) = matchingResult.get
            val newTail1 = tail1.map(_.substitute(termSubst, typeSubst))
            if (subsumes0(vargen, newTail1, lits2 ++ visited, Vector.empty, forbiddenVars))
              true
            else
              subsumes0(vargen, lits1, tail2, hd2 +: visited, forbiddenVars)
          } else {
            subsumes0(vargen, lits1, tail2, hd2 +: visited, forbiddenVars)
          }
        } else {
          subsumes0(vargen, lits1, tail2, hd2 +: visited, forbiddenVars)
        }
      }
    }
  }

  def doMatch(vargen: FreshVarGen, term: Term, term1: Term, forbiddenVars: Set[Int]): Option[(TermSubst, TypeSubst)]
}

object HOPatternSubsumption extends AbstractMatchingSubsumption {
  override final def doMatch(vargen: FreshVarGen, s: Term, t: Term, forbiddenVars: Set[Int]): Option[(TermSubst, TypeSubst)] = {
    val result0 = HOPatternMatching.matchTerms(vargen, s, t, forbiddenVars).iterator
    if (result0.isEmpty) None
    else Some(result0.next())
  }
}

@deprecated("Subsumption based on full HO (pre-) unification is broken at the moment (gives false positives).", "Leo-III 1.2")
object HOMatchingSubsumption extends AbstractMatchingSubsumption {
  override final def doMatch(vargen: FreshVarGen, s: Term, t: Term, forbiddenVars: Set[Int]): Option[(TermSubst, TypeSubst)] = {
    val result0 = HOMatching.matchTerms(vargen, s, t, forbiddenVars).iterator
    if (result0.isEmpty) None
    else Some(result0.next())
  }
}
