package leo.modules.calculus

import leo.datastructures.Clause

/**
 * Created by lex on 25.05.15.
 */
trait Subsumption extends PolyadicCalculusRule[Unit] {
  def subsumes(cl1: Clause, cl2: Clause): Boolean
}

object Subsumption extends Subsumption {
  val impl = TrivialSubsumption

  def canApply(cl: Clause, cls: Set[Clause]) = impl.canApply(cl, cls)
  def apply(cl: Clause, cls: Set[Clause]) = impl(cl, cls)
  def name = impl.name
  def subsumes(cl1: Clause, cl2: Clause) = impl.subsumes(cl1, cl2)
}

object TrivialSubsumption extends Subsumption {
  def canApply(cl: Clause, cls: Set[Clause]) = cls.exists(subsumes(cl, _))

  def apply(v1: Clause, v2: Set[Clause]) = ()

  val name = "Trivial Subsumption"

  def subsumes(cl1: Clause, cl2: Clause): Boolean = {
    val (lits1, lits2) = (cl1.lits, cl2.lits)
    if (lits1.length <= lits2.length) {
      lits1.forall(l1 => lits2.exists(l2 => l1.polarity == l2.polarity && l1.term == l2.term))
    } else {
      false
    }
  }
}
