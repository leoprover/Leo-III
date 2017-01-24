package leo.modules.agent.preprocessing

import leo.agents.{AbstractAgent, Agent, Task}
import leo.datastructures.ClauseAnnotation.{InferredFrom, NoAnnotation}
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.modules.calculus.ArgumentExtraction

/**
  * Created by mwisnie on 3/7/16.
  */
class ArgumentExtractionAgent extends AbstractAgent {
  override def name: String = "argument_extraction_agent"
  override val interest = Some(Seq(ClauseType))

  override def init(): Iterable[Task] = Seq()

  override def filter(event: Event): Iterable[Task] = event match {
    case r : Result =>
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
    // TODO If the signature is split look out for using the same definitions
    val (nc, defs) : (Clause, Set[(Term, Term)]) = ArgumentExtraction(cl.cl)(sig)
    if(defs.isEmpty){
      null
    } else{
     new ArgumentExtractionTask(cl, nc, defs, this)
    }
  }
}

class ArgumentExtractionTask(cl : ClauseProxy, nc : Clause, defs : Set[(Term, Term)], a : Agent) extends Task {
  import leo.datastructures.Role_Definition
  override val name: String = "argument_extraction_task"
  override val getAgent: Agent = a
  override def writeSet(): Map[DataType, Set[Any]] = Map(ClauseType -> Set(cl))
  override def readSet(): Map[DataType, Set[Any]] = Map()
  override def run: Result = {
    var r : Result= Result()
    val defn : Set[ClauseProxy] = defs map {case (t1, t2) => AnnotatedClause(Clause(Literal(t1, t2, true)), Role_Definition, NoAnnotation, ClauseAnnotation.PropNoProp)}
    r = r.update(ClauseType)(cl)(AnnotatedClause(nc, cl.role, InferredFrom(ArgumentExtraction, defn + cl), ClauseAnnotation.PropNoProp))
    val it = defn.iterator
    while(it.hasNext) {
      val d = it.next()
      r= r.insert(ClauseType)(d)
    }
    r
  }
  override val bid: Double = 0.1

  override val pretty: String = s"argument_extraction(${cl.cl.pretty})"
  override val toString : String = pretty
}