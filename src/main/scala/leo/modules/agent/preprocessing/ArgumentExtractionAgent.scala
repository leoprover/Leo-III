package leo.modules.agent.preprocessing

import leo.agents.{TAgent, Task, Agent}
import leo.datastructures.ClauseAnnotation.InferredFrom
import leo.datastructures.{Term, Clause, ClauseProxy, Literal}
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.modules.calculus.CalculusRule
import leo.modules.preprocessing.ArgumentExtraction

/**
  * Created by mwisnie on 3/7/16.
  */
object ArgumentExtractionAgent extends Agent {
  override def name: String = "argument_extraction_agent"
  override def filter(event: Event): Iterable[Task] = event match {
    case DataEvent((cl : ClauseProxy), ClauseType) => commonFilter(cl, Context())
    case DataEvent((cl : ClauseProxy, c : Context), ClauseType) => commonFilter(cl, c)
    case _ => Seq()
  }

  private def commonFilter(cl : ClauseProxy, c : Context) : Iterable[Task] = {
    // TODO If the signature is split look out for using the same definitions
    val (nc, defs) : (Clause, Set[(Term, Term)]) = ArgumentExtraction(cl.cl)
    if(defs.isEmpty){
      Iterable()
    } else{
      Iterable(new ArgumentExtractionTask(cl, nc, defs, c, this))
    }
  }
}

class ArgumentExtractionTask(cl : ClauseProxy, nc : Clause, defs : Set[(Term, Term)], c : Context, a : TAgent) extends Task {
  import leo.datastructures.Role_Definition
  override val name: String = "argument_extraction_task"
  override val getAgent: TAgent = a
  override def writeSet(): Map[DataType, Set[Any]] = Map(ClauseType -> Set(cl))
  override def readSet(): Map[DataType, Set[Any]] = Map()
  override def run: Result = {
    var r : Result= Result()
    val defn : Set[ClauseProxy] = defs map {case (t1, t2) => Store(Clause(Literal(t1, t2, true)), Role_Definition, c)}
    r = r.update(ClauseType)((cl, c))((Store(nc, cl.role, c, InferredFrom(ArgumentExtractionRule, defn + cl))))
    val it = defn.iterator
    while(it.hasNext) {
      val d = it.next()
      r= r.insert(ClauseType)((d, c))
    }
    r
  }
  override val bid: Double = 0.1

  override val pretty: String = s"argument_extraction(${cl.cl.pretty})"
}

object ArgumentExtractionRule extends CalculusRule {
  override def name: String = "argument_extraction"
}