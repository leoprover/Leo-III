package leo.datastructures.blackboard.impl

import leo.agents._
import scala.collection.mutable
import scala.collection.mutable.{HashMap, Map, Set, HashSet}

/**
  * Set to hold all [[leo.agents.Task]] commited by the [[leo.agents.TAgent]].
  * <p>
  * Upon call of the scheduler each active [[leo.agents.TAgent]]
  * supports a prefix of his sorted [[leo.agents.Task]].
  * </p>
  * <p>
  * The returned list fullfills two properties:
  * <ol>
  *   <li>For every [[leo.agents.Task]] `i` contained in this list, there exist no [[leo.agents.Task]] `j` in this TaskSet, with `j.before contains i` or
  *   `i.after contains j`</li>
  *   <li> For every [[leo.agents.TAgent]] `a1` containing a task in this list, there exist no [[leo.agents.TAgent]] `a2` that has a task  and (`a.before contains a2`
  *   or `a2.after contains a`) holds.</li>
  * </ol>
  * </p>
  */
class TaskSet {


  /* -----------------------------------------------------------------------------------
   *
   *                         Data Structures
   *
   * -----------------------------------------------------------------------------------
   */

  /**
    * Saves the in degree of a task (Node) in the Dependency Graph.
    */
  private val inDegree : mutable.Map[Task,Int] = new mutable.HashMap[Task,Int]()

  /**
    * Set of all tasks (Node) with in degree of 0.
    */
  private val zero : mutable.Set[Task] = new mutable.HashSet[Task]()

  /**
    * Assoziates an agent with its agent.
    */
  private val agent : mutable.Map[Task, TAgent] = new mutable.HashMap[Task, TAgent]()

  /**
    * Saves the amount of tasks for one agent
    */
  private val agentWork : mutable.Map[TAgent, Int] = new mutable.HashMap[TAgent, Int]()

  /**
    * Assoziates for each agent a map, which data is written by which task.
    */
  private val write : mutable.Map[TAgent, mutable.Map[Any, Task]] = new mutable.HashMap[TAgent,Map[Any,Task]]()

  /**
    * Assoziates for each agent a map, which data is read by which task.
    */
  private val read : mutable.Map[TAgent, mutable.Map[Any, Task]] = new mutable.HashMap[TAgent, mutable.Map[Any,Task]]()

  /**
    * Stores the tasks for an agent, that has turned passive
    */
  private val passiveTasks : mutable.Map[TAgent, Iterable[Task]] = new mutable.HashMap[TAgent, Iterable[Task]]()

  /**
    * Stores the dependecy graph between the agents.
    * Restores a full symmetric version of the incomplete information in [[leo.agents.TAgent]]
    */
  private val in : mutable.Map[TAgent, scala.collection.immutable.Set[TAgent]] = new mutable.HashMap[TAgent, scala.collection.immutable.Set[TAgent]]()

  /* ----------------------------------------------------------------------------------
   *
   *                             Interface
   *
   * -----------------------------------------------------------------------------------
   */

  /**
    * Adds a new [[leo.agents.TAgent]] to the TaskGraph.
    * @param a
    */
  def addAgent(a : TAgent) : Unit = synchronized {
    write.put(a, write.getOrElse(a,new mutable.HashMap[Any, Task]()))   // Initialize write and read
    read.put(a, read.getOrElse(a,new mutable.HashMap[Any, Task]()))
    agentWork.put(a, agentWork.getOrElse(a, 0))

    var ins : scala.collection.immutable.Set[TAgent] = a.after
    in.keys.foreach{a1 =>
      if(a1.before contains a)
        ins += a1
    }
    in.put(a, ins)
  }

  /**
    * Removes a [[leo.agents.TAgent]] from the TaskGraph
    * @param a
    */
  def remove(a : TAgent) : Unit = {
    write.remove(a)
    read.remove(a)
    agentWork.remove(a)
    passiveTasks.remove(a)
    in.remove(a)

    // TODO Removes tasks or remove on demand?
    in.keys.foreach{a1 =>
      if(a1.before contains a)
        in.put(a1, in.getOrElse(a1, scala.collection.immutable.Set.empty) - a)
    }
  }


  /**
    * Marks an agent as passive and considers its tasks no longer for execution.
    * @param a The agent to be turned passive
    */
  def passive(a : TAgent) : Unit = {
    // TODO Implement
    ???
  }

  /**
    * Marks an agent as active and reanables its tasks for exectuion.
    * @param a The agent to be turned active
    */
  def active(a : TAgent) : Unit = {
    // TODO implement
    ???
  }

  /**
    * Submits a new task created by an agent to the scheduler.
    *
    * @param a The agent that created the task.
    * @param t The task the agent `a` wants to execute.
    */
  def submit(a : TAgent, t : Task) : Unit = {
    // TODO Implement
    ???
  }

  /**
    * Submits mutliple tasks created by an agent at once.
    *
    * @param a The agent that created the tasks
    * @param ts The tasks agent `a` wants to execute
    */
  def submit(a : TAgent, ts : Iterable[Task]) : Unit = ts.foreach(submit(a,_))


  /**
    *
    * Finishes a task afer its execution.
    * It is henceforth removed from the TaskSet and no longer considered
    * for dependency consideration.
    *
    * @param t the newly finished task
    */
  def finish(t : Task) : Unit = {
    // TODO Implement
    ???
  }

  /**
    * Finishes a set of tasks after their execution.
    * They are henceforth removed from the TaskSet and no longer
    * considered for dependecy consideration.
    *
    * @param ts the newly finished tasks
    */
  def finish(ts : Iterable[Task]) : Unit = {
    // TODO Implement
    ???
  }

  /**
    * Marks a set of tasks as commited to the scheduler.
    * There are not removed from dependency considerations until
    * a `finish` is called on them.
    *
    * @param ts The set of tasks committed to the scheduler.
    */
  def commit(ts : Set[Task]) : Unit = {
    // TODO implement
    ???
  }

  /**
    * Checks through all [[leo.agents.Task]] and [[leo.agents.TAgent]] for
    * executable tasks, after dependency check.
    *
    * @return true, iff there exist executable task
    */
  def existExecutable : Boolean = {
    // TODO Implement
    // FIXME How to handle currently executing tasks?
    ???
  }

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
  def executableTasks : Iterable[Task] = {
    //TODO implement
    ???
  }
}
