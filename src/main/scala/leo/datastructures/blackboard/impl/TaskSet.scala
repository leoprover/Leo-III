package leo.datastructures.blackboard.impl

import leo.agents._
import scala.collection.mutable
import scala.collection.mutable.{HashMap, Map, Set, HashSet}
import scala.collection.parallel.immutable

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

  private val depSet : DependencySet = new DependencySetImpl()

  /**
    * Stores the tasks for an agent, that has turned passive
    */
  private val passiveTasks : mutable.Map[TAgent, Iterable[Task]] = new mutable.HashMap[TAgent, Iterable[Task]]()


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
    depSet.addAgent(a)
    agentWork.put(a, agentWork.getOrElse(a, 0))
  }

  /**
    * Removes a [[leo.agents.TAgent]] from the TaskGraph
    * @param a
    */
  def remove(a : TAgent) : Unit = {
    depSet.rmAgent(a)
    agentWork.remove(a)
    passiveTasks.remove(a)
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

/**
  * Maintains the Dependecies between the currently active tasks.
  */
trait DependencySet {
  /**
    * Adds an agent for dependency resolvement.
    *
    * @param a The new agent.
    */
  def addAgent(a : TAgent)

  /**
    * Removes an agent from the dependecy set
    * @param a
    */
  def rmAgent(a : TAgent)

  /**
    * Adds a new Task / Node to the dependecy set.
    * @param t
    * @return Returns all tasks, that previously were independent, but are now dependent on `t`
    */
  def add(t : Task, a : TAgent) : Iterable[Task]

  /**
    * Removes a finished task from the dependecy set
    * @param t
    * @return Returns all tasks, that were only dependent on `t` and are hence independent
    */
  def rm(t : Task, a : TAgent) : Iterable[Task]

  /**
    * Returns all tasks, `t` depends on.
    * @param t
    * @return
    */
  def getDep(t : Task) : Iterable[Task]

  /**
    * Checks, if the given task has dependecies.
    *
    * @param t
    * @return
    */
  def existDep(t : Task) : Boolean
}


class DependencySetImpl extends DependencySet {

  private val ta : mutable.Map[Task, TAgent] = new mutable.HashMap[Task, TAgent]

  private val in : mutable.Map[TAgent, scala.collection.immutable.Set[TAgent]] = new mutable.HashMap[TAgent, scala.collection.immutable.Set[TAgent]]()

  private val out : mutable.Map[TAgent, scala.collection.immutable.Set[TAgent]] = new mutable.HashMap[TAgent, scala.collection.immutable.Set[TAgent]]()

  // TODO split another map (nonintersection) for data types
  private val write : mutable.Map[TAgent, mutable.Map[Any, mutable.Set[Task]]] = new mutable.HashMap()

  private val read : mutable.Map[TAgent, mutable.Map[Any, mutable.Set[Task]]] = new mutable.HashMap()


  override def addAgent(a: TAgent): Unit = {
    write.put(a, write.getOrElse(a,new mutable.HashMap[Any, mutable.Set[Task]]()))   // Initialize write and read
    read.put(a, read.getOrElse(a,new mutable.HashMap[Any, mutable.Set[Task]]()))

    var ins : scala.collection.immutable.Set[TAgent] = scala.collection.immutable.Set.empty[TAgent]
    in.keys.foreach{a1 =>
      if((a1.before contains a) || (a.after contains a1)) {
        ins += a1
        out.get(a1).map{f => out.put(a1, f + a)}  // Collect only the agents currently added (no unnecessary lookups later)
      }
    }
    in.put(a, ins)


    var outs : scala.collection.immutable.Set[TAgent] = scala.collection.immutable.Set.empty[TAgent]
    out.keys.foreach{a1 =>
      if((a1.after contains a) || (a.before contains a1)) {
        outs += a1
        in.get(a1).map{f => in.put(a1, f + a)}
      }
    }
    out.put(a, outs)
  }


  override def rmAgent(a: TAgent): Unit = {
    write.remove(a)
    read.remove(a)

    in.get(a) foreach (_.foreach{ a1 =>
      out.get(a1) map {f => out.put(a1, f - a)}
    })
    in.remove(a)

    out.get(a) foreach (_.foreach{ a1 =>
      in.get(a1) map {f => in.put(a1, f - a)}
    })
    out.remove(a)
  }

  private def getImpl(t : Task) : Iterable[Task] = ???

  override def getDep(t: Task): Iterable[Task] = {

    val ws = t.writeSet().flatMap(_._2)
    val rs = t.readSet().flatMap(_._2)

    ta.get(t).fold(Nil : Iterable[Task]){ a : TAgent =>

      null
    }
  }

  override def existDep(t: Task): Boolean = {
    // TODO Scan for one element instead of checking size
    getDep(t).nonEmpty
  }



  override def rm(t: Task, a : TAgent) : Iterable[Task] = {
    ta.remove(t)
    write.get(a) foreach {m =>
      t.writeSet() foreach {case (dt,dw) =>
        m.get(dw) foreach { ds =>
          ds.remove(t)
        }
      }
    }

    read.get(a) foreach {m =>
      t.readSet() foreach {case (dt,dw) =>
        m.get(dw) foreach { ds =>
          ds.remove(t)
        }
      }
    }

    getDep(t).filter{t1 => !existDep(t1)} // TODO optimize
  }

  override def add(t: Task, a : TAgent) : Iterable[Task] = {
    ta.put(t,a)
    //Insert into write structure
    write.get(a) foreach {m =>
      t.writeSet() foreach { case (dt,dw) =>
        m.getOrElse(dw, mutable.Set.empty[Task]).add(t)
      }
    }

    //Insert into read structure
    read.get(a) foreach {m =>
      t.readSet() foreach { case (dt,dw) =>
        m.getOrElse(dw, mutable.Set.empty[Task]).add(t)
      }
    }

    getImpl(t) // TODO optimize, only return the ones that are now with 1 dependency
  }
}
