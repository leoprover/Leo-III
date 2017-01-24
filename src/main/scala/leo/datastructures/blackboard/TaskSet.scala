package leo.datastructures.blackboard

import leo.agents.{Agent, Task}

/**
  * Created by mwisnie on 5/23/16.
  */
trait TaskSet {

  /**
    * Adds a new [[leo.agents.Agent]] to the TaskGraph.
    *
    * @param a The agent to be added
    */
  def addAgent(a : Agent) : Unit

  /**
    * Removes a [[leo.agents.Agent]] from the TaskGraph
    *
    * @param a The agent to be removed
    */
  def removeAgent(a : Agent) : Unit

  /**
    * Number of tasks executed by the agent a
 *
    * @param a The agent the number of tasks we want to know
    * @return The number of tasks
    */
  def executingTasks(a: Agent) : Int

  def containsAgent(a: Agent) : Boolean

  def dependOn(before: Agent, after: Agent)

  def clear() : Unit

  /**
    * Marks an agent as passive and considers its tasks no longer for execution.
    *
    * @param a The agent to be turned passive
    */
  def passive(a: Agent) : Unit


  /**
    * Marks an agent as active and reanables its tasks for exectuion.
    *
    * @param a The agent to be turned active
    */
  def active(a: Agent) : Unit

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
    * Checks through all [[leo.agents.Task]] and [[leo.agents.Agent]] for
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
    *   <li> For every [[leo.agents.Agent]] `a1` containing a task in this list, there exist no [[leo.agents.Agent]] `a2` that has a task  and (`a.before contains a2`
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
