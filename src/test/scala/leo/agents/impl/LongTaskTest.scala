package leo.agents.impl

import leo.LeoTestSuite
import leo.agents.{AbstractAgent, Agent, Task}
import leo.datastructures.blackboard._

/**
  * Created by mwisnie on 4/13/17.
  */
class LongTaskTest extends LeoTestSuite{

  test("Count 10s"){
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    NumberStore.clear()
    blackboard.addDS(NumberStore)

    blackboard.registerAgent(CountAgent)
    blackboard.registerAgent(new AbstractAgent {
      override val interest : Option[Seq[DataType[Any]]] = None
      override def init(): Iterable[Task] = Seq()
      override def filter(event: Event): Iterable[Task] = event match{
        case DoneEvent => self.synchronized{done = true; self.notifyAll()}; Seq()
        case _ => Seq()
      }
      override def name: String = "termination"
    })
    println("Start")
    scheduler.signal()
    synchronized(while(!done) wait(10000))
    println("Finish")
    scheduler.killAll()
  }

  test("Interfering count 10s") {
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    NumberStore.clear()
    blackboard.addDS(NumberStore)

    blackboard.registerAgent(CountAgent)
    blackboard.registerAgent(BlockingAgent)
    blackboard.registerAgent(new AbstractAgent {
      override val interest : Option[Seq[DataType[Any]]] = None
      override def init(): Iterable[Task] = Seq()
      override def filter(event: Event): Iterable[Task] = event match{
        case DoneEvent => self.synchronized{done = true; self.notifyAll()}; Seq()
        case _ => Seq()
      }
      override def name: String = "termination"
    })
    println("Start")
    scheduler.signal()
    synchronized(while(!done) wait(10000))
    println("Finish")
    scheduler.killAll()
  }

  object BlockingAgent extends AbstractAgent {
    override def name: String = "blocking-agent"
    override def filter(event: Event): Iterable[Task] = event match {
      case d : Delta =>
        val ups = d.updates(NumberType)
        if(ups.nonEmpty && ups.head._2 % 1000 == 0){
          List(new BlockTask)
        } else {
          Nil
        }
      case _ => Nil
    }
    override def init(): Iterable[Task] = Nil

    class BlockTask extends Task {
      override val name: String = "blocktask"
      override def run: Delta = {
        println(">>>>>>>>>>>>>>>Going into block")
        synchronized(wait(5000))
        println(">>>>>>>>>>>>>>>Block finished")
        EmptyDelta
      }
      override val readSet: Map[DataType[Any], Set[Any]] = Map()
      override val writeSet: Map[DataType[Any], Set[Any]] = Map()
      override val bid: Double = 1
      override val getAgent: Agent = BlockingAgent
      override val pretty: String = "blocktask"
    }
  }

  object CountAgent extends AbstractAgent {
    override def name: String = "countagent"
    override def maxParTasks = Some(1)
    override def filter(event: Event): Iterable[Task] = {
      if(NumberStore.getNumber > 1900) Nil
      else
        List(new CountTask(NumberStore.getNumber))
    }
    override def init(): Iterable[Task] = List(new CountTask(NumberStore.getNumber))
    class CountTask(val num : Int) extends Task {
      override val name: String = "counttask"
      override def run: Delta = {
        println(s"${num} -> ${num+1}")
        Result().update(NumberType)(num)(num+1)
      }
      override val readSet: Map[DataType[Any], Set[Any]] = Map()
      override val writeSet: Map[DataType[Any], Set[Any]] = Map(NumberType -> Set(1))
      override val bid: Double = 1
      override val getAgent: Agent = CountAgent
      override val pretty: String = s"counttask(${num} -> ${num + 1})"
    }
  }

  object NumberType extends DataType[Int]{
    override def convert(d: Any): Int = d.asInstanceOf[Int]
  }
  object NumberStore extends DataStore {
    private var num : Int = 0
    def getNumber : Int = synchronized(num)
    override val storedTypes: Seq[DataType[Any]] = Seq(NumberType)
    override def updateResult(r: Delta): Delta = {
      val ups = r.updates(NumberType)
      if(ups.nonEmpty){
        val (_, u) = ups.head
        num = u
      }
      r
    }

    override def clear(): Unit = synchronized {num = 0}
    override def get[T](t: DataType[T]): Set[T] = synchronized(if(t == NumberType) Set(num.asInstanceOf[T]) else Set())
  }
}


