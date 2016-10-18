package leo.modules.agent.preprocessing

import leo.agents.{AbstractAgent, Agent, Task}
import leo.datastructures.Signature
import leo.datastructures.ClauseAnnotation.InferredFrom
import leo.datastructures.{AnnotatedClause, Clause, ClauseAnnotation, ClauseProxy}
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.modules.calculus.CalculusRule
import leo.modules.preprocessing._

/**
  * Created by mwisnie on 3/7/16.
  */
class NormalizationAgent(cs : Context*) extends AbstractAgent {
  override def name: String = "normalization_agent"
  val norms : Seq[Normalization] = Seq() //FIXME What to do with this: Simplification, DefExpSimp, NegationNormal, Skolemization, PrenexNormal) // TODO variable?


  override def init(): Iterable[Task] = Seq()

  override def filter(event: Event): Iterable[Task] = event match {
    case r : Result  =>
      val ins = r.inserts(ClauseType).iterator
      val ups = r.updates(ClauseType).iterator

      var tasks = Seq[Task]()
      while(ins.nonEmpty){
        val t = commonFilter(ins.next().asInstanceOf[ClauseProxy])(SignatureBlackboard.get)
        if(t != null) tasks = t +: tasks
      }
      while(ups.nonEmpty){
        val t = commonFilter(ups.next()._2.asInstanceOf[ClauseProxy])(SignatureBlackboard.get)
        if(t != null) tasks = t +: tasks
      }
      tasks
    case _ => Seq()
  }

  private def commonFilter(cl : ClauseProxy)(sig : Signature) : Task = {
    var openNorm : Seq[Normalization] = norms
    var clause = cl.cl
    while(openNorm.nonEmpty && cl.cl == clause){
      val norm = openNorm.head
      openNorm = openNorm.tail
      clause = norm(clause)(sig)
    }
    if(cl.cl == clause)
      null
    else
      new NormalizationTask(cl, clause, openNorm,this, sig)
  }
}

class NormalizationTask(cl : ClauseProxy, nc : Clause, openNorm : Seq[Normalization],  a : Agent, sig : Signature) extends Task{
  override def name: String = "normalization_task"
  override def getAgent: Agent = a
  override def writeSet(): Map[DataType, Set[Any]] = Map(ClauseType -> Set(cl))
  override def readSet(): Map[DataType, Set[Any]] = Map()
  override def run: Result = {
    val clause = openNorm.foldRight(nc){(norm, c) => norm(c)(sig)}
    val cp = AnnotatedClause(clause, cl.role, InferredFrom(NormalizationRule, cl), ClauseAnnotation.PropNoProp)
    Result().update(ClauseType)(cl)(cp)
  }
  override def bid: Double = 0.1

  override val pretty: String = s"normalization_task(${cl.cl.pretty})"
  override val toString : String = pretty
}

object NormalizationRule extends CalculusRule {
  override val name: String = "normalization"
}
