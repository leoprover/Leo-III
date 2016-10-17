package leo.modules.agent.preprocessing

import leo.agents.{Agent, TAgent, Task}
import leo.datastructures.ClauseAnnotation.{InferredFrom, NoAnnotation}
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.modules.preprocessing.ArgumentExtraction

/**
  * Created by mwisnie on 3/7/16.
  */
class ArgumentExtractionAgent(cs : Context*) extends Agent {
  override def name: String = "argument_extraction_agent"
  override val after : Set[TAgent] = Set(EqualityReplaceAgent)
  override val interest = Some(Seq(ClauseType))
  override def filter(event: Event): Iterable[Task] = event match {
    case DataEvent((cl : ClauseProxy), ClauseType) => commonFilter(cl, Context())(SignatureBlackboard.get)
    case DataEvent((cl : ClauseProxy, c : Context), ClauseType) => commonFilter(cl, c)(SignatureBlackboard.get)
    case _ => Seq()
  }

  private def commonFilter(cl : ClauseProxy, c : Context)(sig: Signature) : Iterable[Task] = {
    // TODO If the signature is split look out for using the same definitions
    val (nc, defs) : (Clause, Set[(Term, Term)]) = ArgumentExtraction(cl.cl)(sig)
    val toInsertContext = cs filter Context.isAncestor(c)
    if(defs.isEmpty){
      Iterable()
    } else{
      toInsertContext map (ci =>new ArgumentExtractionTask(cl, nc, defs, ci, this))
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
    val defn : Set[ClauseProxy] = defs map {case (t1, t2) => AnnotatedClause(Clause(Literal(t1, t2, true)), Role_Definition, NoAnnotation, ClauseAnnotation.PropNoProp)}
    r = r.update(ClauseType)((cl, c))((AnnotatedClause(nc, cl.role, InferredFrom(ArgumentExtraction, defn + cl), ClauseAnnotation.PropNoProp), c))
    val it = defn.iterator
    while(it.hasNext) {
      val d = it.next()
      r= r.insert(ClauseType)((d, c))
    }
    r
  }
  override val bid: Double = 0.1

  override val pretty: String = s"argument_extraction(${cl.cl.pretty})"
  override val toString : String = pretty
}