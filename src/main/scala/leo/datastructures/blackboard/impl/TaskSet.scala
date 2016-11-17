//package leo.datastructures.blackboard.impl
//
//import leo._
//import leo.agents._
//import leo.datastructures.blackboard.{ActiveTracker, TaskSet}
//
//import scala.collection.mutable
//
///**
//  * Set to hold all [[leo.agents.Task]] commited by the [[leo.agents.Agent]].
//  * <p>
//  * Upon call of the scheduler each active [[leo.agents.Agent]]
//  * supports a prefix of his sorted [[leo.agents.Task]].
//  * </p>
//  * <p>
//  * The returned list fullfills two properties:
//  * <ol>
//  *   <li>For every [[leo.agents.Task]] `i` contained in this list, there exist no [[leo.agents.Task]] `j` in this TaskSet, with `j.before contains i` or
//  *   `i.after contains j`</li>
//  *   <li> For every [[leo.agents.Agent]] `a1` containing a task in this list, there exist no [[leo.agents.Agent]] `a2` that has a task  and (`a.before contains a2`
//  *   or `a2.after contains a`) holds.</li>
//  * </ol>
//  * </p>
//  */
//class TaskSelectionSet extends TaskSet{
//  // TODO handle turning passive / active correctly
//
//  /* -----------------------------------------------------------------------------------
//   *
//   *                         Data Structures
//   *
//   * -----------------------------------------------------------------------------------
//   */
//
//  /**
//    * Set of all tasks (Node) with in degree of 0. Stored by agent to preselect tasks (sorted)
//    */
//  private val zero : mutable.Map[Agent, AgentTaskQueue] = new mutable.HashMap[Agent, AgentTaskQueue]()
//
//
//  private val depSet : DependencySet = new DependencySetImpl()
//
//  /**
//    * Stores the tasks for an agent, that has turned passive
//    */
//  private val passiveTasks : mutable.Map[Agent, Iterable[Task]] = new mutable.HashMap[Agent, Iterable[Task]]()
//
//  private val currentlyExecution : mutable.Set[Task] = new mutable.HashSet[Task]()
//
//  private val executingNumber : mutable.Map[Agent, Int] = new mutable.HashMap[Agent, Int]()
//
//
//  /* ----------------------------------------------------------------------------------
//   *
//   *                             Interface
//   *
//   * -----------------------------------------------------------------------------------
//   */
//
//  /**
//    * Adds a new [[leo.agents.Agent]] to the TaskGraph.
//    *
//    * @param a The agent to be added
//    */
//  def addAgent(a : Agent) : Unit = synchronized {
//    depSet.addAgent(a)
//    executingNumber.put(a, 0)
//  }
//
//  /**
//    * Removes a [[leo.agents.Agent]] from the TaskGraph
//    *
//    * @param a The agent to be removed
//    */
//  def removeAgent(a : Agent) : Unit = synchronized {
//    depSet.rmAgent(a)
//    passiveTasks.remove(a)
//    zero.remove(a)
//    executingNumber.remove(a)
//  }
//
//  def containsAgent(a : Agent) : Boolean = synchronized {
//    depSet.containsAgent(a)
//  }
//
//  def dependOn(before : Agent, after : Agent) = synchronized {
//    depSet.dependAgent(before, after)
//  }
//
//  def clear() : Unit = synchronized {
//    zero.clear()
//    passiveTasks.clear()
//    currentlyExecution.clear()
//    depSet.clear()
//  }
//
//
//  /**
//    * Marks an agent as passive and considers its tasks no longer for execution.
//    *
//    * @param a The agent to be turned passive
//    */
//  def passive(a : Agent) : Unit = synchronized {
//    // Fixme Recalculate the dependecy
//    passiveTasks.put(a, zero.get(a).fold(Iterable.empty[Task])(_.clear))
//    zero.remove(a)
//  }
//
//  /**
//    * Marks an agent as active and reanables its tasks for exectuion.
//    *
//    * @param a The agent to be turned active
//    */
//  def active(a : Agent) : Unit = synchronized {
//    // Fixme Recalculate the dependency
//    val ats = new AgentTaskQueue()
//    passiveTasks.remove(a).foreach(_.foreach(ats.add))
//    if(!ats.isEmpty)
//      zero.put(a, ats)
//  }
//
//  override def executingTasks(a : Agent) : Int = synchronized(executingNumber.getOrElse(a, 0))
//
//  /**
//    * Submits a new task created by an agent to the scheduler.
//    *
//    * @param t The task the agent `a` wants to execute.
//    */
//  def submit(t : Task) : Unit = synchronized {
//    //First test clash with currently executing tasks
////    Out.finest(s"Submitting task:\n  agent -> ${a.name}\n  task -> ${t.name}")
////    println(s"Currently Executing: \n     ${currentlyExecution.mkString("\n     ")}\n  while\n   $t")
//    if(currentlyExecution.exists{t1 =>
//      t.writeSet().exists{case (dt, dws) => t1.writeSet().getOrElse(dt, Set.empty).intersect(dws).nonEmpty} ||
//      t.readSet().exists{case (dt, rws) => t1.writeSet().getOrElse(dt, Set.empty).intersect(rws).nonEmpty}
//    })
//      return
//
//    depSet.add(t).foreach{t1 =>
//      val a1 = t.getAgent
//      zero.get(a1).foreach{aq =>
////          Out.finest(s"Removed from current executable task:\n  collision -> ${t.pretty}\n  agent -> ${a1.name}\n   task -> ${t1.pretty}")
//        aq.rm(t1)
//      }
//    }
////    Out.finest(s"Add task:\n  task -> ${t.name}")
//    if(!depSet.existDep(t)) {
////      Out.finest(s"Add task to current executable:\n  task -> ${t.pretty}")
//      zero.getOrElseUpdate(t.getAgent, new AgentTaskQueue).add(t)
//    }
//  }
//
//
//  /**
//    *
//    * Finishes a task afer its execution.
//    * It is henceforth removed from the TaskSet and no longer considered
//    * for dependency consideration.
//    *
//    * @param t the newly finished task
//    */
//  def finish(t : Task) : Unit = synchronized {
//    executingNumber.put(t.getAgent, executingNumber.getOrElse(t.getAgent, 1)-1)
//    currentlyExecution.remove(t)
//    depSet.rm(t).foreach{t1 =>
//      val a1 = t1.getAgent
//      zero.getOrElseUpdate(a1, new AgentTaskQueue).add(t1)
//    }
//  }
//
//  /**
//    * Marks a set of tasks as commited to the scheduler.
//    * There are not removed from dependency considerations until
//    * a `finish` is called on them.
//    *
//    * @param ts The set of tasks committed to the scheduler.
//    */
//  def commit(ts : scala.collection.immutable.Set[Task]) : Unit = synchronized {
//    ts.foreach{t =>
//      executingNumber.put(t.getAgent, executingNumber.getOrElse(t.getAgent, 0)+1)   // TODO group by agent
//      leo.Out.finest(s"Selected for execution: $t ")
//      currentlyExecution.add(t)
//      zero.get(t.getAgent).foreach{ta => ta.rm(t)}
//      depSet.obsoleteTasks(t).foreach{to =>
//        depSet.rm(to).foreach{tz =>
//          val a = tz.getAgent
//          zero.getOrElseUpdate(a, new AgentTaskQueue).add(tz)
//        }
//        zero.get(to.getAgent).foreach(_.rm(to))
//      }
//    }
//  }
//
//  /**
//    * Checks through all [[leo.agents.Task]] and [[leo.agents.Agent]] for
//    * executable tasks, after dependency check.
//    *
//    * @return true, iff there exist executable task
//    */
//  def existExecutable : Boolean = synchronized {
//    executableTasks.nonEmpty  // TODO optimize
//  }
//
//  /**
//    * Dependecy Preselection for the scheduling algorithm.
//    *
//    * <p>
//    * The returned list fullfills two properties:
//    * <ol>
//    *   <li>For every [[leo.agents.Task]] `i` contained in this list, there exist no [[leo.agents.Task]] `j` in this TaskSet, with `j.before contains i` or
//    *   `i.after contains j`</li>
//    *   <li> For every [[leo.agents.Agent]] `a1` containing a task in this list, there exist no [[leo.agents.Agent]] `a2` that has a task  and (`a.before contains a2`
//    *   or `a2.after contains a`) holds.</li>
//    * </ol>
//    * </p>
//    *
//    * @return a set of non dependent [[leo.agents.Task]], ready for selection.
//    */
//  def executableTasks : Iterable[Task] = synchronized {
//    zero.values.flatMap(_.all).toSet[Task] -- currentlyExecution.toSet
//  }
//
//  def registeredTasks : Iterable[Task] = synchronized {
//    depSet.getAllTasks.toSet -- currentlyExecution.toSet
//  }
//}
//
///**
//  * Maintains the Dependecies between the currently active tasks.
//  */
//trait DependencySet {
//  /**
//    * Adds an agent for dependency resolvement.
//    *
//    * @param a The new agent.
//    */
//  def addAgent(a : Agent)
//
//  /**
//    * Removes an agent from the dependecy set
//    *
//    * @param a The agent to be removed
//    */
//  def rmAgent(a : Agent)
//
//  /**
//    * Returns true, iff the agent is currently added
//    *
//    * @param a - The added agent
//    * @return Returns true, if the agent was added
//    */
//  def containsAgent(a : Agent) : Boolean
//
//  /**
//    * Checks if two agents depent on each other
//    *
//    * @param before the agent has to be executed first
//    * @param after the agent has to be executed last
//    * @return true if the order supports the statement
//    */
//  def dependAgent(before : Agent, after : Agent) : Boolean
//
//  /**
//    * Adds a new Task / Node to the dependecy set.
//    *
//    * @param t The task to be added
//    * @return Returns all tasks, that previously were independent, but are now dependent on `t`
//    */
//  def add(t : Task) : Iterable[Task]
//
//  /**
//    * Removes a finished task from the dependecy set
//    *
//    * @param t The task to be added
//    * @return Returns all tasks, that were only dependent on `t` and are hence independent
//    */
//  def rm(t : Task) : Iterable[Task]
//
//  /**
//    * Returns all tasks, `t` depends on.
//    *
//    * @param t The task the dependencies are calculated for
//    * @return
//    */
//  def getDep(t : Task) : Iterable[Task]
//
//  /**
//    * Checks, if the given task has dependecies.
//    *
//    * @param t The task that is checked for dependencies
//    * @return
//    */
//  def existDep(t : Task) : Boolean
//
//  /**
//    * Determines all task, that are colliding with the given task.
//    * Not considering any dependency.
//    * Can be used to remove all tasks obsolte after commiting a task.
//    *
//    * @param t The given Task
//    * @return all task colliding
//    */
//  def obsoleteTasks(t : Task) : Iterable[Task]
//
//  /**
//    * Brings the Set back to the initial state.
//    */
//  def clear() : Unit
//
//  /**
//    * A list of all registered Tasks
// *
//    * @return all registered Tasks
//    */
//  def getAllTasks : Seq[Task]
//}
//
//
//class DependencySetImpl extends DependencySet {
//
//  private val allAgents : mutable.Set[Agent] = new mutable.HashSet[Agent]
//
//  private val in : mutable.Map[Agent, scala.collection.immutable.Set[Agent]] = new mutable.HashMap[Agent, scala.collection.immutable.Set[Agent]]()
//
//  private val out : mutable.Map[Agent, scala.collection.immutable.Set[Agent]] = new mutable.HashMap[Agent, scala.collection.immutable.Set[Agent]]()
//
//  // TODO split another map (nonintersection) for data types
//  private val write : mutable.Map[Agent, mutable.Map[Any, mutable.Set[Task]]] = new mutable.HashMap()
//
//  private val read : mutable.Map[Agent, mutable.Map[Any, mutable.Set[Task]]] = new mutable.HashMap()
//
//  private val allTasks : mutable.Set[Task] = new mutable.HashSet[Task]()
//
//  override def clear() : Unit = {
//    allAgents.clear()
//    in.clear()
//    out.clear()
//    write.clear()
//    read.clear()
//  }
//
//  override def getAllTasks : Seq[Task] = allTasks.toSeq
//
//  // TODO Hier funktioniert das symmetrisch machen noch nicht!
//  override def addAgent(a: Agent): Unit = {
//    write.put(a, write.getOrElse(a,new mutable.HashMap[Any, mutable.Set[Task]]()))   // Initialize write and read
//    read.put(a, read.getOrElse(a,new mutable.HashMap[Any, mutable.Set[Task]]()))
//
//    var ins : scala.collection.immutable.Set[Agent] = scala.collection.immutable.Set.empty[Agent]
//    allAgents.foreach{a1 =>
//      if((a1.before contains a) || (a.after contains a1)) {
//        ins += a1
//        out.get(a1).map{f => out.put(a1, f + a)}  // Collect only the agents currently added (no unnecessary lookups later)
//      }
//    }
//    in.put(a, ins)
//
//
//    var outs : scala.collection.immutable.Set[Agent] = scala.collection.immutable.Set.empty[Agent]
//    allAgents.foreach{a1 =>
//      if((a1.after contains a) || (a.before contains a1)) {
//        outs += a1
//        in.get(a1).map{f => in.put(a1, f + a)}
//      }
//    }
//    out.put(a, outs)
//    allAgents.add(a)
//  }
//
//
//  override def rmAgent(a: Agent): Unit = {
//    write.remove(a)
//    read.remove(a)
//
//    in.get(a) foreach (_.foreach{ a1 =>
//      out.get(a1) map {f => out.put(a1, f - a)}
//    })
//    in.remove(a)
//
//    out.get(a) foreach (_.foreach{ a1 =>
//      in.get(a1) map {f => in.put(a1, f - a)}
//    })
//    out.remove(a)
//    allAgents.remove(a)
//  }
//
//  override def containsAgent(a: Agent) : Boolean = {
//    write.contains(a)
//  }
//
//  private def getColliding(t : Task)(as : scala.collection.immutable.Set[Agent]) = {
//    val ws = t.writeSet().flatMap(_._2).toSet
//    val rs = t.readSet().flatMap(_._2).toSet
//    val empty = scala.collection.immutable.Set.empty[Task]
//
//    val a: Agent = t.getAgent
//    as.flatMap{a1 =>
//      // Collect all Task colliding of the agent
//      write.get(a1).fold(empty){wsa1 =>
//        //We have the map from data -> Set(Task) for agent a1
//        val wts = ws.foldLeft(empty){(ts, d) => {ts.union(wsa1.get(d).fold(empty)(_.toSet))}}
//        val rts = rs.foldLeft(empty){(ts, d) => ts.union(wsa1.get(d).fold(empty)(_.toSet))}
//        wts.union(rts)
//      }.union(read.get(a1).fold(empty){rsa1 =>
//        ws.foldLeft(empty){(ts, d) => ts.union(rsa1.get(d).fold(empty)(_.toSet))}
//      }).toIterable
//    }
//  }
//
//
//  private def getImpl(t : Task) : Iterable[Task] = {
//    val a = t.getAgent
//    out.get(a).fold(Iterable.empty[Task]){depA =>
//      getColliding(t)(depA)
//    }
//  }
//
//  override def getDep(t: Task): Iterable[Task] = {
//    val a = t.getAgent
//    in.get(a).fold(Iterable.empty[Task]){depA =>
//      getColliding(t)(depA)
//    }
//  }
//
//  override def existDep(t: Task): Boolean = {
//    // TODO Scan for one element instead of checking size
//    getDep(t).nonEmpty
//  }
//
//
//
//  override def rm(t: Task) : Iterable[Task] = {
//    write.get(t.getAgent) foreach {m =>
//      t.writeSet() foreach {case (dt,dws) =>
//        dws foreach { dw =>
//          m.get(dw) foreach { ds =>
//            ds.remove(t)
//            if(ds.isEmpty) m.remove(dw)
//          }
//        }
//      }
//    }
//
//    read.get(t.getAgent) foreach {m =>
//      t.readSet() foreach {case (dt,dws) =>
//        dws foreach { dw =>
//          m.get(dw) foreach { ds =>
//            ds.remove(t)
//          }
//          if(m.get(dw).isEmpty) m.remove(dw)
//        }
//      }
//    }
//    allTasks.remove(t)
//    val impls = getImpl(t).filter{t1 =>
//      !existDep(t1)} // TODO optimize
//    impls
//  }
//
//  override def add(t: Task) : Iterable[Task] = {
//    //Insert into write structure
//    write.get(t.getAgent) foreach {m =>
//      t.writeSet() foreach { case (dt,dws) =>
//        dws foreach { dw =>
//          m.getOrElseUpdate(dw, mutable.Set.empty[Task]).add(t)
//        }
//      }
//    }
//
//    //Insert into read structure
//    read.get(t.getAgent) foreach {m =>
//      t.readSet() foreach { case (dt,dws) =>
//        dws foreach { dw =>
//          m.getOrElseUpdate(dw, mutable.Set.empty[Task]).add(t)
//        }
//      }
//    }
//    allTasks.add(t)
//
//    val imp = getImpl(t) // TODO optimize, only return the ones that are now with 1 dependency
//    imp
//  }
//
//  override def obsoleteTasks(t : Task) : Iterable[Task] = {
//    val empty = scala.collection.immutable.Set.empty[Task]
//    val ws = write.values.foldLeft(empty){(ws,wm) =>
//      ws.union(t.writeSet().foldLeft(empty){(ws,w) => ws.union(wm.getOrElse(w,empty))})
//    }
//    val rs = read.values.foldLeft(empty){(rs,rm) =>
//      rs.union(t.writeSet().foldLeft(empty){(rs,r) => ws.union(rm.getOrElse(r,empty))})
//    }
//    ws.union(rs)
//  }
//
//  /**
//    * Checks if two agents depent on each other
//    *
//    * @param before the agent has to be executed first
//    * @param after the agent has to be executed last
//    * @return true if the order supports the statement
//    */
//  override def dependAgent(before: Agent, after: Agent): Boolean = {
//    out.get(before).fold[Boolean](false){as => as.contains(after)}
//  }
//}
//
//class AgentTaskQueue {
//
//  private var q : mutable.PriorityQueue[Task] = new mutable.PriorityQueue[Task]()(Ordering.by{(x : Task) => x.bid})
//  private var ts : mutable.Set[Task] = new mutable.HashSet[Task]
//
//  def all : Iterable[Task] = q.toSeq
//
//  def add(t : Task) : Unit = if(!ts.contains(t)){
//    ActiveTracker.incAndGet(s"Inserted task\n $t")
//    q.enqueue(t)
//    ts.add(t)
//  }
//
//  def rm(t : Task) : Unit = if(ts.contains(t)){
//    ts.remove(t)
//    val empty = scala.collection.immutable.Set.empty[Any]
//    q = q.filter { tbe =>
//      if(t.eq(tbe)) {
//        false
//      } else {
//        val sharedTypes = tbe.lockedTypes & t.lockedTypes
//        sharedTypes exists { d =>
//          val we = t.writeSet().getOrElse(d, empty)
//          val wtb = tbe.writeSet().getOrElse(d, empty)
//          val rtb = tbe.readSet().getOrElse(d, empty)
//
//          val take = (we & wtb).isEmpty && (we & rtb).isEmpty // If the tbe task excesses any data, that will be updated.
//          if (!take && !t.eq(tbe)) Out.trace(s"The task\n  $tbe\n collided with\n  $t\n and was therefore removed.")
//          if(!take) ActiveTracker.decAndGet(s"Remove due to collsion ${tbe.pretty}")
//          take
//        }
//      }
//    }
//  }
//
//  def head : Option[Task] = q.headOption
//
//  def isEmpty : Boolean = q.isEmpty
//
//  def initAfforadble : List[Task] = {
//    var erg = List[Task]()
//    var costs : Double = 0
//      for (t <- q) {
//        // TODO Change to iterator since for is inefficient
//        if (costs > 1) return erg
//        else {
//          costs += t.bid
//          erg = t :: erg
//        }
//      }
//    erg
//  }
//
//  def clear : Iterable[Task] = {
//    ts.clear()
//    q.foreach{t => ActiveTracker.decAndGet(s"Clear: Task \n  $t\n  was removed.")}
//    val tss = q.toSeq
//    q.clear()
//    tss
//  }
//}
