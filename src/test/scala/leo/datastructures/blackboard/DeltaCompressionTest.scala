package leo.datastructures.blackboard

import leo.LeoTestSuite
import leo.agents.{Agent, Task}
import leo.datastructures.blackboard.scheduler.Scheduler

/**
  * Created by mwisnie on 2/14/17.
  */
class DeltaCompressionTest extends LeoTestSuite{


  test("No compression") {
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    val store = new StringStore
    blackboard.addDS(store)
    val agent = new ManyTaskAgent(Seq("a", "b"),
      {d => d.inserts(StringType).size == 1},
      {d => s"Each Delta should hold 1 insert, but it only holds ${d.inserts(StringType).size} inserts."},
      2)
    blackboard.registerAgent(agent)

    scheduler.signal()
    agent.synchronized(agent.wait(3000))
    scheduler.killAll()
    assert(agent.fcond, agent.fmsg)
  }

  test("Compression") {
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    val store = new StringStore
    blackboard.addDS(store)
    val agent = new ManyTaskAgent(Seq("a", "b", "c", "d"),
      {d => d.inserts(StringType).size != 0},
      {d => s"Each Delta should hold 4 insert, but it only holds ${d.inserts(StringType).size} inserts."},
      4)
    blackboard.registerAgent(agent)

    scheduler.signal()
    agent.synchronized(agent.wait(6000))
//    println("Kill all")
    scheduler.killAll()
    assert(agent.fcond, agent.fmsg)
  }



  class ManyTaskAgent(val cl : Seq[String], cond : Delta => Boolean, msg : Delta => String, numberOfDeltas : Int) extends Agent {
    var fcond : Boolean = false
    var fmsg : String = "Nothing happend. Error in scheduling?"
    var currentDelta : Int = 0
    override val name: String = s"insertAll(${cl.mkString(", ")})"
    private val self = this
    override def kill(): Unit = {}
    override val interest: Option[Seq[DataType[Any]]] = Some(Nil)
    override def filter(event: Event): Iterable[Task] = event match {
      case DoneEvent => this.synchronized(this.notifyAll()); Seq()
      case d : Delta =>
        println("Filter : " + d.toString)
        fcond = cond(d)
        fmsg = msg(d)
        assert(fcond, fmsg)
        if(!fcond) return Seq()
        synchronized{
          currentDelta += 1
          fcond = currentDelta <= numberOfDeltas
          fmsg = "There were to many Deltas."
          assert(fcond, fmsg)

        }
        Seq()
    }
    override def init(): Iterable[Task] = cl.map(new OneTask(_))
    override def maxMoney: Double = Double.MaxValue
    override def taskFinished(t: Task): Unit = {}
    override def taskChoosen(t: Task): Unit = {}
    override def taskCanceled(t: Task): Unit = {}

    class OneTask(s : String) extends Task {
      override val name: String = "insert"
      override def run: Delta = Result().insert(StringType)(s)
      override def readSet(): Map[DataType[Any], Set[Any]] = Map()
      override def writeSet(): Map[DataType[Any], Set[Any]] = Map()
      override val bid: Double = 1.0 / self.cl.size
      override val getAgent: Agent = self
      override val pretty: String = s"insert($s)"
    }
  }



  case object StringType extends DataType[String] {
    override def convert(d: Any): String = d.toString
  }
  class StringStore extends DataStore {
    override def storedTypes: Seq[DataType[Any]] = Seq(StringType)
    override def updateResult(r: Delta): Boolean = true
    override def clear(): Unit = {}
    override protected[blackboard] def all[T](t: DataType[T]): Set[T] = Set()
  }
}



