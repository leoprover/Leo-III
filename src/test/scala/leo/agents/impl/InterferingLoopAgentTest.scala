package leo.agents.impl

import leo.{Checked, Ignored, LeoTestSuite}
import leo.agents._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard._

/**
  * Testing a simple loop like program
  */
class InterferingLoopAgentTest extends LeoTestSuite {

  test("Count to 10", Checked){
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    NumberStore.clear()
    blackboard.addDS(NumberStore)
    val incAgent = new InterferingLoopAgent[LoopState](new IncrementLoop(10), blackboard)
    blackboard.registerAgent(incAgent)
    blackboard.registerAgent(
    new AbstractAgent {
      override val interest : Option[Seq[DataType[Any]]] = None
      override def init(): Iterable[Task] = Seq()
      override def filter(event: Event): Iterable[Task] = event match{
        case DoneEvent => self.synchronized{done = true; self.notifyAll()}; Seq()
        case _ => Seq()
      }
      override def name: String = "termination"
    })

    scheduler.signal()
    self.synchronized(while(!done) self.wait())
    scheduler.killAll()
    assert(NumberStore.getNumber == 10, "Incrementing to 10 should hold 10.")
  }

  test("Ripple count to 10", Checked){
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    NumberStore.clear()
    blackboard.addDS(NumberStore)
    val incAgent = new InterferingLoopAgent[LoopState](new IncrementLoop(10), blackboard)
    blackboard.registerAgent(incAgent)
    blackboard.registerAgent(AnoyingAgent)
    blackboard.registerAgent(
    new AbstractAgent {
      override val interest : Option[Seq[DataType[Any]]] = None
      override def init(): Iterable[Task] = Seq()
      override def filter(event: Event): Iterable[Task] = event match{
        case DoneEvent => self.synchronized{done = true; self.notifyAll()}; Seq()
        case _ => Seq()
      }

      override def name: String = "termination"
    })

    scheduler.signal()
    self.synchronized{while(!done) self.wait()}
    scheduler.killAll()
    val n = NumberStore.getNumber
    assert(n == 10 || n == 11 || n == 14, "Incrementing to 10 should hold 10.")
  }

  test("Count to 100", Checked){
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    NumberStore.clear()
    blackboard.addDS(NumberStore)
    val incAgent = new InterferingLoopAgent[LoopState](new IncrementLoop(100), blackboard)
    blackboard.registerAgent(incAgent)
    blackboard.registerAgent(
    new AbstractAgent {
      override val interest : Option[Seq[DataType[Any]]] = None
      override def init(): Iterable[Task] = Seq()
      override def filter(event: Event): Iterable[Task] = event match{
        case DoneEvent => self.synchronized{done = true; self.notifyAll()}; Seq()
        case _ => Seq()
      }

      override def name: String = "termination"
    })

    scheduler.signal()
    self.synchronized{while(!done) self.wait()}
    scheduler.killAll()
    assert(NumberStore.getNumber == 100, "Incrementing to 100 should hold 100.")
  }


}

object AnoyingAgent extends AbstractAgent {
  override def name: String = "AnoyingAgent"
  override def init(): Iterable[Task] = {
    val n = NumberStore.getNumber
    if(n % 3 == 0){
      Seq(new AnoyingTask(n))
    } else
      Seq()
  }

  override def filter(event: Event): Iterable[Task] = event match {
    case r : Delta =>
      val inV = r.inserts(NumberType) ++ r.updates(NumberType).map(_._2)
      if(inV.nonEmpty){
        val n = inV.head.asInstanceOf[Int]
        if( n % 3 == 0)
          return List(new AnoyingTask(n))
      }
      return Seq()
    case _ => Seq()
  }

  class AnoyingTask(n : Int) extends Task {
    override def name: String = s"anoy"
    override def run: Delta = Result().update(NumberType)(n)(n+5)
    override def readSet(): Map[DataType[Any], Set[Any]] = Map.empty
    override def writeSet(): Map[DataType[Any], Set[Any]] = Map(NumberType -> Set(n))
    override def bid: Double = 1
    override def getAgent: Agent = AnoyingAgent
    override def pretty: String = s"anoy(${n} --> ${n+5})"
  }
}

class IncrementLoop(maxNumber : Int) extends InterferingLoop [LoopState] {
  override def name: String = "IncrementingLoop"
  override def canApply: Option[LoopState] = {
    val n = NumberStore.getNumber
    println(s"Test $n < $maxNumber")
    if(n < maxNumber) {
      println(s"Create task Increment(${n})")   // TODO not executed
      Some(new LoopState(NumberStore.getNumber))
    } else{
      println("Created no task.")
      None
    }
  }
  override def apply(opState: LoopState): Delta = {
    val n = opState.n
    val next = n+1
    println("n = "+next)
    Result().update(NumberType)(n)(next)
  }

  override def init: Option[LoopState] = canApply
}

case class LoopState(n : Int) extends OperationState {
  override def datatypes: Iterable[DataType[Any]] = List(NumberType)
  override def writeData[T](ty: DataType[T]): Set[T] = if(ty == NumberType) Set(n.asInstanceOf[T]) else Set()
  override def readData[T](ty: DataType[T]): Set[T] = Set()
  override val toString : String = s"$n"
}



object NumberType extends DataType[Int]{
  override def convert(d: Any): Int = d.asInstanceOf[Int]
}
object NumberStore extends DataStore {
  private var num : Int = 0
  def getNumber : Int = synchronized(num)
  override val storedTypes: Seq[DataType[Any]] = Seq(NumberType)
  override def updateResult(r: Delta): Boolean = {
    val ups = r.updates(NumberType)
    if(ups.nonEmpty){
      val (_, u) = ups.head.asInstanceOf[(Int, Int)]
      num = u
    }
    true
  }

  override def clear(): Unit = synchronized {num = 0}
  override def all[T](t: DataType[T]): Set[T] = synchronized(if(t == NumberType) Set(num.asInstanceOf[T]) else Set())
}