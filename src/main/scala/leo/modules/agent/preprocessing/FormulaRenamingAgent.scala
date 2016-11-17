package leo.modules.agent.preprocessing

import leo.agents.{AbstractAgent, Agent, Task}
import leo.datastructures.ClauseAnnotation.{InferredFrom, NoAnnotation}
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.modules.calculus.FormulaRenaming

/**
  * Created by mwisnie on 3/7/16.
  */
class FormulaRenamingAgent(cs : Context*) extends AbstractAgent {
  override def name: String = "formula_renaming_agent"
  override val interest = Some(Seq(ClauseType))

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
    val (nc, defs) = FormulaRenaming(cl.cl)(sig)
    if(defs.nonEmpty){
      new FormulaRenamingTask(cl, nc, defs , this)
    } else {
      null
    }
  }
}

class FormulaRenamingTask(cl : ClauseProxy, clause : Clause, defs : Seq[Clause], a : Agent) extends Task {

  override def name: String = "formula_renaming_task"
  override def getAgent: Agent = a
  override def writeSet(): Map[DataType, Set[Any]] = Map(ClauseType -> Set(cl))
  override def readSet(): Map[DataType, Set[Any]] = Map()
  override def run: Result = {
    var r : Result= Result()
    val defn : Set[ClauseProxy] = (defs map {d => AnnotatedClause(d, Role_Definition, NoAnnotation, ClauseAnnotation.PropNoProp)}).toSet
    r = r.update(ClauseType)(cl)(AnnotatedClause(clause, cl.role, InferredFrom(FormulaRenaming, defn + cl), ClauseAnnotation.PropNoProp))
    val it = defn.iterator
    while(it.hasNext) {
      val d = it.next()
      r= r.insert(ClauseType)(d)
    }
    r
  }
  override def bid: Double = 0.1

  override val pretty: String = s"formula_renaming_task($name)"
  override val toString : String = pretty
}
