package leo.datastructures.blackboard

import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * This object can be used to track overall active status of the program.
 *
 * Each process can raise the flag "Active", to prevent an DoneEvent from beeing fired.
 *
 * @since 8/31/15
 * @author Max Wisniewski
 */
object ActiveTracker {
  private val counter : AtomicInteger = new AtomicInteger(0)
  private val msg : StringBuilder = new StringBuilder

  def incAndGet() : Int = {
    counter.incrementAndGet()
  }

  def incAndGet(debug : String) : Int = {
    val i = incAndGet()
    leo.Out.finest(s"ActiveTracker(${i-1}->$i): $debug")
    //msg.append(s"ActiveTracker(${i-1}->$i): $debug\n")
    i
  }

  def addAndGet(delta : Int) : Int = {
    counter.addAndGet(delta)
  }

  def decAndGet() : Int = {
    counter.decrementAndGet()
  }

  def decAndGet(debug : String) : Int = {
    val i = decAndGet()
    leo.Out.finest(s"ActiveTracker(${i+1}->$i): $debug")
    //msg.append(s"ActiveTracker(${i+1}->$i): $debug\n")
    i
  }

  def subAndGet(delta : Int) : Int = {
    counter.addAndGet(-delta)
  }

  def get : Int = {
    counter.get()
  }

  def isActive : Boolean = get > 0
  def isNotActive : Boolean = get <= 0

  def getmsg : String = msg.toString()
  def addComment(comment : String) : Unit = {}//msg.append(comment+"\n")}
}
