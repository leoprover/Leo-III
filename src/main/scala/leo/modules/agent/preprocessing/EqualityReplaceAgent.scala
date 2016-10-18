package leo.modules.agent.preprocessing

import leo.agents.{AbstractAgent, Agent, Task}
import leo.datastructures.ClauseAnnotation.InferredFrom
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.modules.calculus.{ReplaceAndrewsEq, ReplaceLeibnizEq}

/**
  * Created by mwisnie on 3/7/16.
  */
object EqualityReplaceAgent extends AbstractAgent{
  override def name: String = "equality_replace_agent"
  override val interest = Some(Seq(ClauseType))

  override def init(): Iterable[Task] = Seq()

  override def filter(event: Event): Iterable[Task] = event match {
    case r : Result  =>
      val ins = r.inserts(ClauseType).iterator
      val ups = r.updates(ClauseType).iterator

      var tasks = Seq[Task]()
      while(ins.nonEmpty){
        val t = commonFilter(ins.next().asInstanceOf[ClauseProxy], SignatureBlackboard.get)
        if(t != null) tasks = t +: tasks
      }
      while(ups.nonEmpty){
        val t = commonFilter(ups.next()._2.asInstanceOf[ClauseProxy], SignatureBlackboard.get)
        if(t != null) tasks = t +: tasks
      }
      tasks
    case _ => Seq()
  }

  private def commonFilter(cl : ClauseProxy, sig : Signature) : Task = {
    val (can1, map) = ReplaceLeibnizEq.canApply(cl.cl)(sig)
    if(can1){
      new LeibnitzEQTask(cl, cl.cl, map, this)
    } else {
      val (can2, map2) = ReplaceAndrewsEq.canApply(cl.cl)
      if(can2){
        new AndrewEQTask(cl, cl.cl, map2,this)
      } else {
        null
      }
    }
  }
}

abstract class EqualityReplaceTask(cl : ClauseProxy, a : Agent) extends Task {
  override val name: String = "equality_replace_task"
  override def getAgent: Agent = a
  override def writeSet(): Map[DataType, Set[Any]] = Map(ClauseType -> Set(cl))
  override def readSet(): Map[DataType, Set[Any]] = Map()
  override val bid: Double = 0.1
  override val pretty: String = s"equality_replace_task(${cl.cl.pretty})"
  override val toString : String = pretty
}

/**
  * Replaces Leibnitzequality and then andrew equality.
  */
class LeibnitzEQTask(cl : ClauseProxy, clause : Clause, map : Map[Int, Term], a : Agent) extends EqualityReplaceTask(cl, a){
  override def run: Result = {
    val (nc, _) = ReplaceLeibnizEq(clause, map)
    val (can, map2) = ReplaceAndrewsEq.canApply(nc)
    val fc = if(can){
      ReplaceAndrewsEq(nc, map2)._1
    } else {
      nc
    }
    Result().update(ClauseType)(cl)(AnnotatedClause(fc, cl.role, InferredFrom(ReplaceAndrewsEq, cl), ClauseAnnotation.PropNoProp))
  }
}

/**
  * Replaces only Andrew Equality
  */
class AndrewEQTask(cl : ClauseProxy, clause : Clause, map : Map[Int, Type], a : Agent) extends EqualityReplaceTask(cl, a){
  override def run: Result = {
    val (nc, _) = ReplaceAndrewsEq(clause, map)
    Result().update(ClauseType)(cl)(AnnotatedClause(nc, cl.role, InferredFrom(ReplaceAndrewsEq, cl), ClauseAnnotation.PropNoProp))
  }
}
