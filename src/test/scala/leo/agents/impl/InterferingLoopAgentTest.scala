package leo.agents.impl

import leo.LeoTestSuite
import leo.agents._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard._

/**
  * Testing a simple loop like program
  */
class InterferingLoopAgentTest extends LeoTestSuite {

  test("Count to 100"){
    val self = this
    NumberStore.clear()
    Blackboard().addDS(NumberStore)
    val incAgent = new InterferingLoopAgent[LoopState](new IncrementLoop(100))
    incAgent.register()
    new AbstractAgent {
      override val interest : Option[Seq[DataType]] = None
      override def filter(event: Event): Iterable[Task] = event match{
        case _ : DoneEvent => self.synchronized(self.notifyAll()); Seq()
        case _ => Seq()
      }

      override def name: String = "termination"
    }.register()

    Scheduler().signal()
    self.synchronized(self.wait())
  }

}



class IncrementLoop(maxNumber : Int) extends InterferingLoop [LoopState] {
  override def name: String = "IncrementingLoop"
  override def canApply: Option[LoopState] = {
    val n = NumberStore.getNumber
    println(s"Test $n < $maxNumber")
    if(n < maxNumber) {
//      println(s"Create task Increment(${n})")
      Some(new LoopState(NumberStore.getNumber))
    } else{
//      println("Created no task.")
      None
    }
  }
  override def apply(opState: LoopState): Result = {
    val n = opState.n
    val next = n+1
    println("n = "+next)
    Result().update(NumberType)(n)(next)
  }
}

case class LoopState(n : Int) extends OperationState {
  override def datatypes: Iterable[DataType] = List(NumberType)
  override def writeData(ty: DataType): Set[Any] = if(ty == NumberType) Set(n) else Set()
  override def readData(ty: DataType): Set[Any] = Set()
  override val toString : String = s"$n"
}



object NumberType extends DataType
object NumberStore extends DataStore {
  private var num : Int = 0
  def getNumber : Int = synchronized(num)
  override val storedTypes: Seq[DataType] = Seq(NumberType)
  override def update(o: Any, n: Any): Boolean = synchronized {(o,n) match {
    case (o1 : Int,n1 : Int) =>
      if(o1 == num) {
        println(s"Num set to ${n1}")
        num = n1
        true
      } else
        false
    case _ => false
  }}
  override def insert(n: Any): Boolean = synchronized(n match {
    case n1 : Int if num == 0 =>
      num = n1
      true
    case _ => false
  })
  override def clear(): Unit = synchronized {num = 0}
  override def all(t: DataType): Set[Any] = synchronized(if(t == NumberType) Set(num) else Set())
  override def delete(d: Any): Unit = synchronized {num = 0}
}