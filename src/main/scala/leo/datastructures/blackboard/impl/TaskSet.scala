package leo.datastructures.blackboard.impl

import leo.agents._

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
    * Checks through all [[Task]] and [[TAgent]] for
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
    * @return a set of non dependent [[Task]], ready for selection.
    */
  def executableTasks : Iterable[Task] = {
    //TODO implement
    ???
  }
}
