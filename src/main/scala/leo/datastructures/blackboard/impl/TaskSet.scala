package leo.datastructures.blackboard.impl

import leo._
import leo.agents._
import leo.datastructures.blackboard.ActiveTracker
import scala.collection.mutable

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
class TaskSelectionSet {
  // TODO handle turning passive / active correctly

  /* -----------------------------------------------------------------------------------
   *
   *                         Data Structures
   *
   * -----------------------------------------------------------------------------------
   */

  /**
    * Set of all tasks (Node) with in degree of 0. Stored by agent to preselect tasks (sorted)
    */
  private val zero : mutable.Map[TAgent, AgentTaskQueue] = new mutable.HashMap[TAgent, AgentTaskQueue]()

  /**
    * Assoziates an agent with its agent.
    */
  private val agent : mutable.Map[Task, TAgent] = new mutable.HashMap[Task, TAgent]()

  private val depSet : DependencySet = new DependencySetImpl()

  /**
    * Stores the tasks for an agent, that has turned passive
    */
  private val passiveTasks : mutable.Map[TAgent, Iterable[Task]] = new mutable.HashMap[TAgent, Iterable[Task]]()

  private val currentlyExecution : mutable.Set[Task] = new mutable.HashSet[Task]()


  /* ----------------------------------------------------------------------------------
   *
   *                             Interface
   *
   * -----------------------------------------------------------------------------------
   */

  /**
    * Adds a new [[leo.agents.TAgent]] to the TaskGraph.
    * @param a The agent to be added
    */
  def addAgent(a : TAgent) : Unit = synchronized {
    depSet.addAgent(a)
  }

  /**
    * Removes a [[leo.agents.TAgent]] from the TaskGraph
    * @param a The agent to be removed
    */
  def removeAgent(a : TAgent) : Unit = synchronized {
    depSet.rmAgent(a)
    passiveTasks.remove(a)
    zero.remove(a)
  }

  def containsAgent(a : TAgent) : Boolean = synchronized {
    depSet.containsAgent(a)
  }

  def dependOn(before : TAgent, after : TAgent) = synchronized {
    depSet.dependAgent(before, after)
  }


  /**
    * Marks an agent as passive and considers its tasks no longer for execution.
    * @param a The agent to be turned passive
    */
  def passive(a : TAgent) : Unit = synchronized {
    // Fixme Recalculate the dependecy
    passiveTasks.put(a, zero.get(a).fold(Iterable.empty[Task])(_.clear))
    zero.remove(a)
  }

  /**
    * Marks an agent as active and reanables its tasks for exectuion.
    * @param a The agent to be turned active
    */
  def active(a : TAgent) : Unit = synchronized {
    // Fixme Recalculate the dependency
    val ats = new AgentTaskQueue()
    passiveTasks.remove(a).foreach(_.foreach(ats.add))
    if(!ats.isEmpty)
      zero.put(a, ats)
  }

  /**
    * Submits a new task created by an agent to the scheduler.
    *
    * @param a The agent that created the task.
    * @param t The task the agent `a` wants to execute.
    */
  def submit(a : TAgent, t : Task) : Unit = synchronized {
    //First test clash with currently executing tasks
    if(currentlyExecution.exists{t1 =>
      t.writeSet().exists{case (dt, dws) => t1.writeSet().getOrElse(dt, Set.empty).intersect(dws).nonEmpty} ||
      t.readSet().exists{case (dt, rws) => t1.writeSet().getOrElse(dt, Set.empty).intersect(rws).nonEmpty}
    })
      return

    depSet.add(t,a).foreach{t1 =>
      agent.get(t1).foreach{a1 =>
        zero.get(a1).foreach{aq =>
          aq.rm(t1)
        }
      }
    }
    agent.put(t,a)
    if(!depSet.existDep(t))
      zero.getOrElseUpdate(a, new AgentTaskQueue).add(t)
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
  def finish(t : Task) : Unit = synchronized {
    depSet.rm(t, agent.get(t).get).foreach{t1 =>
      agent.get(t1).foreach{a1 =>
        // Consider the new free independent Tasks
        zero.getOrElseUpdate(a1, new AgentTaskQueue).add(t1)
      }
    }
    agent.remove(t)
  }

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
  def commit(ts : scala.collection.immutable.Set[Task]) : Unit = synchronized {
    ts.foreach{t =>
      currentlyExecution.add(t)
      depSet.obsoleteTasks(t).foreach{t =>
        val a = agent.get(t).get
        depSet.rm(t, a).foreach{tz =>
          val a = agent.get(tz).get
          zero.getOrElseUpdate(a, new AgentTaskQueue).add(tz)
        }
        zero.get(a).foreach(_.rm(t))
      }
    }
  }

  /**
    * Checks through all [[leo.agents.Task]] and [[leo.agents.TAgent]] for
    * executable tasks, after dependency check.
    *
    * @return true, iff there exist executable task
    */
  def existExecutable : Boolean = synchronized {
    executableTasks.nonEmpty  // TODO optimize
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
  def executableTasks : Iterable[Task] = synchronized {
    zero.values.flatMap(_.all).toSet[Task] -- currentlyExecution.toSet
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
    * @param a The agent to be removed
    */
  def rmAgent(a : TAgent)

  /**
    * Returns true, iff the agent is currently added
    *
    * @param a - The added agent
    * @return Returns true, if the agent was added
    */
  def containsAgent(a : TAgent) : Boolean

  /**
    * Checks if two agents depent on each other
    *
    * @param before the agent has to be executed first
    * @param after the agent has to be executed last
    * @return true if the order supports the statement
    */
  def dependAgent(before : TAgent, after : TAgent) : Boolean

  /**
    * Adds a new Task / Node to the dependecy set.
    * @param t The task to be added
    * @param a The Agent this task is added for
    * @return Returns all tasks, that previously were independent, but are now dependent on `t`
    */
  def add(t : Task, a : TAgent) : Iterable[Task]

  /**
    * Removes a finished task from the dependecy set
    * @param t The task to be added
    * @param a The Agent this task is added for
    * @return Returns all tasks, that were only dependent on `t` and are hence independent
    */
  def rm(t : Task, a : TAgent) : Iterable[Task]

  /**
    * Returns all tasks, `t` depends on.
    * @param t The task the dependencies are calculated for
    * @return
    */
  def getDep(t : Task) : Iterable[Task]

  /**
    * Checks, if the given task has dependecies.
    *
    * @param t The task that is checked for dependencies
    * @return
    */
  def existDep(t : Task) : Boolean

  /**
    * Determines all task, that are colliding with the given task.
    * Not considering any dependency.
    * Can be used to remove all tasks obsolte after commiting a task.
    *
    * @param t The given Task
    * @return all task colliding
    */
  def obsoleteTasks(t : Task) : Iterable[Task]
}


class DependencySetImpl extends DependencySet {

  private val allAgents : mutable.Set[TAgent] = new mutable.HashSet[TAgent]

  private val ta : mutable.Map[Task, TAgent] = new mutable.HashMap[Task, TAgent]

  private val in : mutable.Map[TAgent, scala.collection.immutable.Set[TAgent]] = new mutable.HashMap[TAgent, scala.collection.immutable.Set[TAgent]]()

  private val out : mutable.Map[TAgent, scala.collection.immutable.Set[TAgent]] = new mutable.HashMap[TAgent, scala.collection.immutable.Set[TAgent]]()

  // TODO split another map (nonintersection) for data types
  private val write : mutable.Map[TAgent, mutable.Map[Any, mutable.Set[Task]]] = new mutable.HashMap()

  private val read : mutable.Map[TAgent, mutable.Map[Any, mutable.Set[Task]]] = new mutable.HashMap()

  // TODO Hier funktioniert das symmetrisch machen noch nicht!
  override def addAgent(a: TAgent): Unit = {
    write.put(a, write.getOrElse(a,new mutable.HashMap[Any, mutable.Set[Task]]()))   // Initialize write and read
    read.put(a, read.getOrElse(a,new mutable.HashMap[Any, mutable.Set[Task]]()))

    var ins : scala.collection.immutable.Set[TAgent] = scala.collection.immutable.Set.empty[TAgent]
    allAgents.foreach{a1 =>
      if((a1.before contains a) || (a.after contains a1)) {
        ins += a1
        out.get(a1).map{f => out.put(a1, f + a)}  // Collect only the agents currently added (no unnecessary lookups later)
      }
    }
    in.put(a, ins)


    var outs : scala.collection.immutable.Set[TAgent] = scala.collection.immutable.Set.empty[TAgent]
    allAgents.foreach{a1 =>
      if((a1.after contains a) || (a.before contains a1)) {
        outs += a1
        in.get(a1).map{f => in.put(a1, f + a)}
      }
    }
    out.put(a, outs)
    allAgents.add(a)
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
    allAgents.remove(a)
  }

  override def containsAgent(a : TAgent) : Boolean = {
    write.contains(a)
  }

  private def getColliding(t : Task)(as : scala.collection.immutable.Set[TAgent]) = {
    val ws = t.writeSet().flatMap(_._2).toSet
    val rs = t.readSet().flatMap(_._2).toSet
    val empty = scala.collection.immutable.Set.empty[Task]

    ta.get(t).fold(Nil : Iterable[Task]){ a : TAgent =>
      as.flatMap{a1 =>
        // Collect all Task colliding of the agent
        write.get(a1).fold(empty){wsa1 =>
          //We have the map from data -> Set(Task) for agent a1
          val wts = ws.foldLeft(empty){(ts, d) => {ts.union(wsa1.get(d).fold(empty)(_.toSet))}}
          val rts = rs.foldLeft(empty){(ts, d) => ts.union(wsa1.get(d).fold(empty)(_.toSet))}
          wts.union(rts)
        }.union(read.get(a1).fold(empty){rsa1 =>
          ws.foldLeft(empty){(ts, d) => ts.union(rsa1.get(d).fold(empty)(_.toSet))}
        }).toIterable
      }
    }
  }


  private def getImpl(t : Task) : Iterable[Task] = {
    ta.get(t).fold(Iterable.empty[Task]){a =>
      out.get(a).fold(Iterable.empty[Task]){depA =>
        getColliding(t)(depA)
      }
    }
  }

  override def getDep(t: Task): Iterable[Task] = {
    ta.get(t).fold(Iterable.empty[Task]){a =>
      in.get(a).fold(Iterable.empty[Task]){depA =>
        getColliding(t)(depA)
      }
    }
  }

  override def existDep(t: Task): Boolean = {
    // TODO Scan for one element instead of checking size
    getDep(t).nonEmpty
  }



  override def rm(t: Task, a : TAgent) : Iterable[Task] = {
    write.get(a) foreach {m =>
      t.writeSet() foreach {case (dt,dws) =>
        dws foreach { dw =>
          m.get(dw) foreach { ds =>
            ds.remove(t)
          }
        }
      }
    }

    read.get(a) foreach {m =>
      t.readSet() foreach {case (dt,dws) =>
        dws foreach { dw =>
          m.get(dw) foreach { ds =>
            ds.remove(t)
          }
        }
      }
    }

    val impls = getImpl(t).filter{t1 =>
      !existDep(t1)} // TODO optimize
    ta.remove(t)
    impls
  }

  override def add(t: Task, a : TAgent) : Iterable[Task] = {
    ta.put(t,a)
    //Insert into write structure
    write.get(a) foreach {m =>
      t.writeSet() foreach { case (dt,dws) =>
        dws foreach { dw =>
          m.getOrElseUpdate(dw, mutable.Set.empty[Task]).add(t)
        }
      }
    }

    //Insert into read structure
    read.get(a) foreach {m =>
      t.readSet() foreach { case (dt,dws) =>
        dws foreach { dw =>
          m.getOrElseUpdate(dw, mutable.Set.empty[Task]).add(t)
        }
      }
    }

    val imp = getImpl(t) // TODO optimize, only return the ones that are now with 1 dependency
    imp
  }

  override def obsoleteTasks(t : Task) : Iterable[Task] = {
    val empty = scala.collection.immutable.Set.empty[Task]
    val ws = write.values.foldLeft(empty){(ws,wm) =>
      ws.union(t.writeSet().foldLeft(empty){(ws,w) => ws.union(wm.getOrElse(w,empty))})
    }
    val rs = read.values.foldLeft(empty){(rs,rm) =>
      rs.union(t.writeSet().foldLeft(empty){(rs,r) => ws.union(rm.getOrElse(r,empty))})
    }
    ws.union(rs)
  }

  /**
    * Checks if two agents depent on each other
    *
    * @param before the agent has to be executed first
    * @param after the agent has to be executed last
    * @return true if the order supports the statement
    */
  override def dependAgent(before: TAgent, after: TAgent): Boolean = {
    out.get(before).fold[Boolean](false){as => as.contains(after)}
  }
}

class AgentTaskQueue {

  private var q : mutable.PriorityQueue[Task] = new mutable.PriorityQueue[Task]()(Ordering.by{(x : Task) => x.bid})

  def all : Iterable[Task] = q.toIterable

  def add(t : Task) : Unit = {
    ActiveTracker.incAndGet(s"Inserted task\n $t")
    q.enqueue(t)
  }

  def rm(t : Task) : Unit = {
    val empty = scala.collection.immutable.Set.empty[Any]
    q = q.filter { tbe =>
      if(t.eq(tbe)) {
        false
      } else {
        val sharedTypes = tbe.lockedTypes & t.lockedTypes
        sharedTypes exists { d =>
          val we = t.writeSet().getOrElse(d, empty)
          val wtb = tbe.writeSet().getOrElse(d, empty)
          val rtb = tbe.readSet().getOrElse(d, empty)

          val take = (we & wtb).isEmpty && (we & rtb).isEmpty // If the tbe task excesses any data, that will be updated.
          if (!take && !t.eq(tbe)) Out.trace(s"The task\n  $tbe\n collided with\n  $t\n and was therefore removed.")
          if(!take) ActiveTracker.decAndGet(s"Remove due to collsion ${tbe.pretty}")
          take
        }
      }
    }
  }

  def head : Option[Task] = q.headOption

  def isEmpty : Boolean = q.isEmpty

  def initAfforadble : List[Task] = {
    var erg = List[Task]()
    var costs : Double = 0
      for (t <- q) {
        // TODO Change to iterator since for is inefficient
        if (costs > 1) return erg
        else {
          costs += t.bid
          erg = t :: erg
        }
      }
    erg
  }

  def clear : Iterable[Task] = {
    q.foreach{t => ActiveTracker.decAndGet(s"Clear: Task \n  $t\n  was removed.")}
    val ts = q.toIterable
    q.clear()
    ts
  }
}
