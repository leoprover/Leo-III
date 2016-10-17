package leo.modules.agent.preprocessing

import leo.agents.{Agent, TAgent, Task}
import leo.datastructures.ClauseAnnotation.{InferredFrom, NoAnnotation}
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.modules.calculus.CalculusRule
import leo.modules.preprocessing.FormulaRenaming

/**
  * Created by mwisnie on 3/7/16.
  */
class FormulaRenamingAgent(cs : Context*) extends Agent {
  override def name: String = "formula_renaming_agent"
  override val after : Set[TAgent] = Set(EqualityReplaceAgent)
  override val interest = Some(Seq(ClauseType))
  override def filter(event: Event): Iterable[Task] = event match {
    case DataEvent(cl : ClauseProxy, ClauseType) => commonFilter(cl, Context())(SignatureBlackboard.get)
    case DataEvent((cl : ClauseProxy, c : Context), ClauseType) => commonFilter(cl, c)(SignatureBlackboard.get)
    case _ => Seq()
  }

  private def commonFilter(cl : ClauseProxy, c : Context)(sig: Signature) : Iterable[Task] = {
    val (nc, defs) = FormulaRenaming(cl.cl)(sig)
    val toInsertContext = cs filter Context.isAncestor(c)
    if(defs.nonEmpty){
      toInsertContext map (ci => new FormulaRenamingTask(cl, nc, defs , ci, this))
    } else {
      Seq()
    }
  }
}

class FormulaRenamingTask(cl : ClauseProxy, clause : Clause, defs : Seq[Clause], c : Context, a : TAgent) extends Task {

  override def name: String = "formula_renaming_task"
  override def getAgent: TAgent = a
  override def writeSet(): Map[DataType, Set[Any]] = Map(ClauseType -> Set(cl))
  override def readSet(): Map[DataType, Set[Any]] = Map()
  override def run: Result = {
    var r : Result= Result()
    val defn : Set[ClauseProxy] = (defs map {d => AnnotatedClause(d, Role_Definition, NoAnnotation, ClauseAnnotation.PropNoProp)}).toSet
    r = r.update(ClauseType)((cl, c))((AnnotatedClause(clause, cl.role, InferredFrom(FormulaRenaming, defn + cl), ClauseAnnotation.PropNoProp)))
    val it = defn.iterator
    while(it.hasNext) {
      val d = it.next()
      r= r.insert(ClauseType)((d, c))
    }
    r
  }
  override def bid: Double = 0.1

  override val pretty: String = s"formula_renaming_task($name)"
  override val toString : String = pretty
}
