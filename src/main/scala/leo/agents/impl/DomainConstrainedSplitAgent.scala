package leo
package agents.impl

import leo.agents._
import leo.datastructures.blackboard.{FormulaStore, Event, Message}
import leo.datastructures.context.{AlphaSplit, NoSplit, Context}
import leo.datastructures.impl.Signature
import leo.datastructures.{HOLSignature, Type}
import leo.modules.Utility
import leo.modules.proofCalculi.splitting.DomainConstrainedSplitting

/**
 *
 * This agent reacts on messages and introduces new axioms
 * to proof counter-satisfiability for the conjecture.
 * He thereby introduces cardinality constrains for the domains
 * (currently only $o) and splits the context to test a given
 * number of cardinalities in parallel.
 *
 * @author Max Wisniewski
 * @since 2/19/2015
 */
class DomainConstrainedSplitAgent extends FifoAgent{
  override def name: String = "DomainConstrainedAgent"

  override protected def toFilter(event: Event): Iterable[Task] = event match {
    case DomainConstrainedMessage(n) if Context().splitKind == NoSplit => List(new DomainConstrainedTask(n))
    case _ => Nil
  }


  override def run(t: Task): Result = {
    if(Context().splitKind != NoSplit) return EmptyResult
    t match {
      case t1 : DomainConstrainedTask =>
        // Utility.printSignature()
        val s1 : Set[Signature#Key] = Signature.get.baseTypes - 0 - 1 - 3 - 4 - 5// Without kind
        val s : Set[Type]= s1.map {k => Type.mkType(k)}
        Out.output(s"[$name]: Get types. ${s.map(_.pretty).mkString(", ")}")
        // TODO: Give the combination of domain constraints to the agent. At the moment same size
        val b = Context().split(AlphaSplit, t1.maxCard)
        if(!b) {return EmptyResult}


        val children = Context().childContext
        var cardAx : Set[FormulaStore] = Set.empty
        val it = children.iterator
        var i : Int = 1
        while(it.hasNext && i <= t1.maxCard) {
          val c = it.next()
          val ax = s.map(DomainConstrainedSplitting.cardinalityAxioms(i)(_)).flatten.toList.map(_.newContext(c))
          cardAx = cardAx ++ ax
          i = i+1
        }

        return new StdResult(cardAx, Map.empty, Set.empty)
      case _ => EmptyResult
    }
  }
}

private class DomainConstrainedMessage(val maxCard : Int) extends Message {}

object DomainConstrainedMessage {
  def apply(maxCard : Int) : Message = new DomainConstrainedMessage(maxCard)

  def unapply(e : Event) : Option[Int] = e match {
    case d : DomainConstrainedMessage => Some(d.maxCard)
    case _  => None
  }
}

private class DomainConstrainedTask(val maxCard : Int) extends Task {
  override def name: String = "DomainConstrainedTask"
  override def writeSet(): Set[FormulaStore] = Set.empty
  override def readSet(): Set[FormulaStore] = Set.empty

  override def bid(budget: Double): Double = budget / 10

  override def pretty: String = "Context Split: Domain cardinality set to a maximum of "+maxCard
}
