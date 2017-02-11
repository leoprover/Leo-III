package leo.modules.calculus

import leo.datastructures.{Clause, Literal}

/** Trait for subsumption algorithms. */
trait Subsumption {
  def subsumes(cl1: Clause, cl2: Clause): Boolean
}

object Subsumption extends Subsumption {
  val impl = TrivialSubsumption
  var subsumptiontests = 0
  def name = impl.name
  def subsumes(cl1: Clause, cl2: Clause) = {subsumptiontests += 1; impl.subsumes(cl1, cl2)}
}

object TrivialSubsumption extends Subsumption {
  def canApply(cl: Clause, cls: Set[Clause]) = cls.exists(subsumes(cl, _))
  val name = "Trivial Subsumption"

  def subsumes(cl1: Clause, cl2: Clause): Boolean = {
    val (lits1, lits2) = (cl1.lits, cl2.lits)
    if (lits1.length <= lits2.length) {
      lits1.forall(l1 => lits2.exists(l2 => l1.polarity == l2.polarity && l1.unsignedEquals(l2)))
    } else {
      false
    }
  }
}

object FOMatchingSubsumption extends Subsumption {
  import leo.datastructures.{Literal, Subst}
  val name = "FO matching subsumption"

  def subsumes(cl1: Clause, cl2: Clause): Boolean = {
    val (lits1, lits2) = (cl1.lits, cl2.lits)

    if (lits1.length <= lits2.length) {
      val liftedLits1 = lits1.map(_.substitute(Subst.shift(cl2.maxImplicitlyBound)))
      subsumes0(liftedLits1, lits2, Seq())
    } else
      false
  }

  private final def subsumes0(lits1: Seq[Literal], lits2: Seq[Literal], visited: Seq[Literal]): Boolean = {
    if (lits1.isEmpty) true
    else {
      if (lits2.isEmpty) false
      else {
        val (hd1, tail1) = (lits1.head, lits1.tail)
        val (hd2, tail2) = (lits2.head, lits2.tail)
        if (hd1.polarity == hd2.polarity) {
          val (term1, term2) = (Literal.asTerm(hd1), Literal.asTerm(hd2))
          val matchingResult = FOMatching.matches(term1, term2)
          if (matchingResult.isDefined) {
            val subst = matchingResult.get
            val newTail1 = tail1.map(_.substitute(subst))
            if (subsumes0(newTail1, lits2 ++ visited, Seq()))
              true
            else
              subsumes0(lits1, tail2, hd2 +: visited)
          } else {
            subsumes0(lits1, tail2, hd2 +: visited)
          }
        } else {
          subsumes0(lits1, tail2, hd2 +: visited)
        }
      }
    }
  }
}

