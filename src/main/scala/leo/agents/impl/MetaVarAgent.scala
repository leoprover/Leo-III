package leo
package agents
package impl

import leo.datastructures.Term._
import leo.datastructures.blackboard.impl
import leo.datastructures.impl.Signature
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.modules.output.{SZS_Theorem, SZS_CounterSatisfiable}
import leo.modules.calculus.CalculusRule

/**
 * This Agent removes all initial all quantifiers and replaces them by MetaVariables.
 *
 *
 */
object MetaVarAgent extends Agent {
  override val name: String = "MetavarAgent"
  override val interest : Option[Seq[DataType]] = Some(List(FormulaType))

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  override def run(t: Task): Result = t match {
    case MetavarTask(f) => {
      trace("applied cnf_forall on "+f.pretty)
      Result().update(FormulaType)(f)(CNFForall(f))
    }
    case _ => Result()
  }

  /**
   * Triggers the filtering of the Agent.
   *
   * Upon an Event the Agent can generate Tasks, he wants to execute.
   * @param event on the blackboard concerning change of data.
   * @return a List of Tasks the Agent wants to execute.
   */
  override def toFilter(event: Event): Iterable[Task] = event match {
    case DataEvent(f : FormulaStore, FormulaType) =>
      if(CNFForall.canApply(f)) {
        trace("Can apply cnf_forall")
        List(MetavarTask(f))
      } else {
        Nil
      }
    case _ => Nil
  }


  private object CNFForall extends CalculusRule {
    val name = "cnf_forall"
    override val inferenceStatus = Some(SZS_Theorem)
    def canApply(fs: FormulaStore) = fs.clause.lits.exists{l => initQuant(l)} && (fs.status & 31) == 31
    def apply(f: FormulaStore) = Store(f.clause.mapLit(replaceQuants(_)), Role_Plain, f.context, f.status, ClauseAnnotation(this, f))
  }

  private def initQuant(l : Literal) : Boolean = l.term match {
    case Forall(ty :::> t1) if l.polarity => true
    case Exists(ty :::> t1) if !l.polarity => true
    case _  => false
  }

  private def replaceQuants(l : Literal) : Literal = l.termMap{t => replaceQuant(l.polarity)(t).betaNormalize}

  private def replaceQuant(pol : Boolean)(t : Term) : Term = t match {
    case Forall(ty :::> t1) if pol =>  mkTermApp(\(ty)(replaceQuant(pol)(t1)),Term.mkFreshMetaVar(ty))
    case Exists(ty :::> t1) if !pol => mkTermApp(\(ty)(replaceQuant(pol)(t1)),Term.mkFreshMetaVar(ty))
    case _ => t
  }

  final private case class MetavarTask(f : FormulaStore) extends Task {
    override val name: String = "Metavar replace"
    override def writeSet(): Set[FormulaStore] = Set(f)
    override def readSet(): Set[FormulaStore] = Set()
    override def bid(budget: Double): Double = budget / 5
    override val pretty: String = "metavar replace"
  }
}

