package leo.datastructures.blackboard

import leo.agents.{TAgent, Task}

/**
  * Created by mwisnie on 5/23/16.
  */
trait TaskSet {

  /**
    * Adds a new [[leo.agents.TAgent]] to the TaskGraph.
    *
    * @param a The agent to be added
    */
  def addAgent(a : TAgent) : Unit

  /**
    * Removes a [[leo.agents.TAgent]] from the TaskGraph
    *
    * @param a The agent to be removed
    */
  def removeAgent(a : TAgent) : Unit

  def containsAgent(a : TAgent) : Boolean

  def dependOn(before : TAgent, after : TAgent)

  def clear() : Unit

  /**
    * Marks an agent as passive and considers its tasks no longer for execution.
    *
    * @param a The agent to be turned passive
    */
  def passive(a : TAgent) : Unit


  /**
    * Marks an agent as active and reanables its tasks for exectuion.
    *
    * @param a The agent to be turned active
    */
  def active(a : TAgent) : Unit

  /**
    * Submits a new task created by an agent to the scheduler.
    *
    * @param t The task the agent `a` wants to execute.
    */
  def submit(t : Task) : Unit

  /**
    * Submits mutliple tasks created by an agent at once.
    *
    * @param ts The tasks agent `a` wants to execute
    */
  def submit(ts : Iterable[Task]) : Unit = ts.foreach(submit)

  /**
    *
    * Finishes a task afer its execution.
    * It is henceforth removed from the TaskSet and no longer considered
    * for dependency consideration.
    *
    * @param t the newly finished task
    */
  def finish(t : Task) : Unit

  /**
    * Finishes a set of tasks after their execution.
    * They are henceforth removed from the TaskSet and no longer
    * considered for dependecy consideration.
    *
    * @param ts the newly finished tasks
    */
  def finish(ts : Iterable[Task]) : Unit = ts.foreach(finish)


  /**
    * Marks a set of tasks as commited to the scheduler.
    * There are not removed from dependency considerations until
    * a `finish` is called on them.
    *
    * @param ts The set of tasks committed to the scheduler.
    */
  def commit(ts : scala.collection.immutable.Set[Task]) : Unit

  /**
    * Checks through all [[leo.agents.Task]] and [[leo.agents.TAgent]] for
    * executable tasks, after dependency check.
    *
    * @return true, iff there exist executable task
    */
  def existExecutable : Boolean

  /**
    * Dependecy Preselection for the scheduling algorithm.
    *
    * <p>
    * The returned list fullfills two properties:
    * <ol>
    *   <li>For every [[leo.agents.Task]] `i` contained in this list, there exist no [[leo.agents.Task]] `j` in this TaskSet, with `j.before contains i` or
    *   `i.after contains j`</li>
    *   <li> For every [[leo.agents.TAgent]] `a1` containing a task in this list, there exist no [[leo.agents.TAgent]] `a2` that has a task  and (`a.before contains a2`
    *   or `a2.after contains a`) holds.</li>
    * </ol>
    * </p>
    *
    * @return a set of non dependent [[leo.agents.Task]], ready for selection.
    */
  def executableTasks : Iterable[Task]

  /**
    *
    * List of all tasks in the system
    *
    * @return
    */
  def registeredTasks : Iterable[Task]
}
