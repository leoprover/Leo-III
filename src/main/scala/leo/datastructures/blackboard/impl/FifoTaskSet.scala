package leo.datastructures.blackboard.impl

import leo.agents.{Agent, Task}
import leo.datastructures.blackboard.{LockSet, TaskSet}

/**
  * Implements a fifo order on the tasks.
  *
  * TODO Locks currently do not delete tasks
  */
class FifoTaskSet extends TaskSet {

  private val agents : scala.collection.concurrent.TrieMap[Agent, Int] = scala.collection.concurrent.TrieMap[Agent, Int]()
  private var firstNode : Node = _
  private var lastNode : Node = _


  override def addAgent(a: Agent): Unit = agents += ((a, 0))
  override def removeAgent(a: Agent): Unit = agents -= a
//  override def executingTasks(a: Agent): Int = agents.getOrElse(a, 0)
  override def containsAgent(a: Agent): Boolean = agents.contains(a)
  override def clear(): Unit = {
    agents.clear()
  }
  override def passive(a: Agent): Unit = {}
  override def active(a: Agent): Unit = {}
  override def submit(t: Task): Unit = synchronized{
    if(!LockSet.isOutdated(t)) {
      val h = new LinkedNode(t)
      lastNode.setNext(h)
      lastNode = h
    } else {
      ()
    }
  }
  override def finish(t: Task): Unit = {
    LockSet.lockTask(t)
    if(firstNode != null) {
      val h = new LazyNode(Set(), Set(t))
      h.setNext(firstNode)
      firstNode = h
    }
  }
  override def commit(ts: Set[Task]): Unit = {
    ts.foreach(t => LockSet.releaseTask(t))
    if(firstNode != null){
      val h = new LazyNode(ts, Set())
      h.setNext(firstNode)
      firstNode = h
    }
  }

  // TODO cash existExecutable until new insertion or finish was called

  override def existExecutable: Boolean = firstNode != null && firstNode.lazyCompressAndSearch != null
  override def executableTasks: Iterator[Task] = new ExecIterator
  override def registeredTasks: Iterable[Task] = ???

  private class ExecIterator extends Iterator[Task] {
    private var cur : Node = firstNode
    override def hasNext: Boolean = { // TODO move First in compress
      cur = cur.lazyCompressAndSearch
      cur == null
    }

    override def next(): Task = {
      if(hasNext) {
        assert(!cur.disabled)
        val e = cur.elem
        cur = cur.next
        e
      } else {
        throw new IllegalStateException("Access empty iterator")
      }
    }
  }

  private trait Node {
    def next : Node
    def setNext(n : Node) : Unit
    def elem : Task
    def disabled : Boolean
    def setDisabled(d : Boolean)

    def lazyCompressAndSearch : Node
  }

  private class LinkedNode(val elem : Task) extends Node {
    var disabled : Boolean = _
    var next : Node = _
    override def setNext(n: Node): Unit = {next = n}
    override def setDisabled(d: Boolean): Unit = {disabled = d}

    override def lazyCompressAndSearch: Node = {
      if(!disabled) this
      else {
        next.lazyCompressAndSearch
      }
    }
  }

  private class LazyNode(val commit : Set[Task], val finish : Set[Task]) extends Node {
    val disabled : Boolean = false
    var next : Node = _
    var prev : Node = _
    override def setNext(n: Node): Unit = next = n
    override def elem: Task = ???
    override def setDisabled(d: Boolean): Unit = ???

    private def switchWithNext(): Unit = {
      assert(next != null)
      val l = next
      if(prev == null)
        firstNode = next
      else
        prev.setNext(next)
      next = l.next
      l.setNext(this)
    }

    override def lazyCompressAndSearch: Node = {
      if(next == null) {  // Deletes the lazy node
        if(prev == null) {
          firstNode = null
        } else {
          prev.setNext(null)
        }
        lastNode = prev
        return null
      }
      next match {
        case l : LinkedNode =>
          if (commit.contains(l.elem)) {
            // If it was taken, it can be deleted (Lazy Delete)
            next = l.next
            lazyCompressAndSearch
          } else if (l.disabled && finish.exists(t => t.blockes(l.elem))){
            // If it was blocked by an earlier, free it an return with this element
            l.setDisabled(false)
            switchWithNext()
            l
          } else if (l.disabled) {
            // If it is disabled (not freed) switch
            switchWithNext()
            lazyCompressAndSearch
          } else if (LockSet.isOutdated(l.elem)){
            // If it is newly blocked, marke as disabled and search further
            l.setDisabled(true)
            switchWithNext()
            lazyCompressAndSearch
          } else {
            switchWithNext()
            l
          }
        case l : LazyNode => {
          val merge = new LazyNode(this.commit union l.commit, this.finish union l.finish)
          merge.prev = this.prev
          merge.next = l.next
          lazyCompressAndSearch
        }
      }
    }
  }
}
