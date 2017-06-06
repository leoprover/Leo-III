package leo.modules.agent.rules

import leo.{Checked, LeoTestSuite}
import leo.agents.{AbstractAgent, Task}
import leo.datastructures.blackboard._
import leo.modules.SZSException
import leo.modules.output.SZS_Error


class NetworkTest extends LeoTestSuite{

  /**
    *
    * [  A  ] -- x => x+1 --> [  B  ]
    *
    */
  test("Test one manipulation", Checked){

    implicit val (blackboard, scheduler) = Blackboard.newBlackboard

    val self = this
    var done = false

    val aSet = new TypedSet(AType)
    val bSet = new TypedSet(BType)
    val move = new IntManipulationRule(AType, BType, x => x+1, "move")
    val mAgent = new RuleAgent(move)

    blackboard.addDS(aSet)
    blackboard.addDS(bSet)

    blackboard.addData(AType)(1)
    blackboard.addData(AType)(2)

    blackboard.registerAgent(mAgent)

    blackboard.registerAgent(
      new AbstractAgent {
        override val interest : Option[Seq[DataType[Any]]] = None
        override def init(): Iterable[Task] = Seq()
        override def filter(event: Event): Iterable[Task] = event match{
          case DoneEvent => println("Done"); self.synchronized{done = true; self.notifyAll()}; Seq()
          case _ => Seq()
        }

        override def name: String = "termination"
      })

    scheduler.signal()
    self.synchronized{while(!done) self.wait()}

    val data = bSet.get(BType)
    assert(data.size == 2)
    assert(data.contains(2))
    assert(data.contains(3))
  }

  test("Test : One Operation to Move", Checked) {
    implicit val (blackboard, scheduler) = Blackboard.newBlackboard

    val self = this
    var done = false

    val aSet = new TypedSet(AType)
    val bSet = new TypedSet(BType)
    val aBarrier = new AgentBarrier(AType, 1)
    val half = new IntManipulationRule(AType, AType, x => if(x % 2 == 0) x / 2 else x, "half-even")
    val hAgent = new RuleAgent(half)
    val move = new MovingRule(AType, BType, aBarrier)
    val mAgent = new RuleAgent(move)

    blackboard.addDS(aSet)
    blackboard.addDS(bSet)
    blackboard.addDS(aBarrier)

    blackboard.addData(AType)(7)
    blackboard.addData(AType)(20)

    blackboard.registerAgent(hAgent)
    blackboard.registerAgent(mAgent)

    blackboard.registerAgent(
      new AbstractAgent {
        override val interest : Option[Seq[DataType[Any]]] = None
        override def init(): Iterable[Task] = Seq()
        override def filter(event: Event): Iterable[Task] = event match{
          case DoneEvent => println("Done"); self.synchronized{done = true; self.notifyAll()}; Seq()
          case _ => Seq()
        }

        override def name: String = "termination"
      })

    scheduler.signal()
    self.synchronized{while(!done) self.wait()}

    val data = bSet.get(BType)
    assert(data.size == 2)
    assert(data.contains(7))
    assert(data.contains(5))
  }

  /**
    * Tests a simple network:
    *
    * [  A  ] -- R1 -- > [  B  ] -- M --> [  C  ]
    *                     |    |
    *                     |-R2-|
    *                     |-R3-|
    */
  test("Combined Test", Checked) {
    implicit val (blackboard, scheduler) = Blackboard.newBlackboard

    val self = this
    var done = false

    val aSet = new TypedSet(AType)
    val bSet = new TypedSet(BType)
    val cSet = new TypedSet(CType)
    val bBarrier = new AgentBarrier(BType, 1)
    val double = new IntManipulationRule(AType, BType, x => x * 2, "double")
    val dAgent = new RuleAgent(double)
    val half = new IntManipulationRule(BType, BType, x => if(x % 2 == 0) x / 2 else x, "half-even")
    val hAgent = new RuleAgent(half)
    val move = new MovingRule(BType, CType, bBarrier)
    val mAgent = new RuleAgent(move)

    blackboard.addDS(aSet)
    blackboard.addDS(bSet)
    blackboard.addDS(cSet)
    blackboard.addDS(bBarrier)

    blackboard.addData(AType)(7)
    blackboard.addData(AType)(20)

    blackboard.registerAgent(dAgent)
    blackboard.registerAgent(hAgent)
    blackboard.registerAgent(mAgent)

    blackboard.registerAgent(
      new AbstractAgent {
        override val interest : Option[Seq[DataType[Any]]] = None
        override def init(): Iterable[Task] = Seq()
        override def filter(event: Event): Iterable[Task] = event match{
          case DoneEvent => println("Done"); self.synchronized{done = true; self.notifyAll()}; Seq()
          case _ => Seq()
        }

        override def name: String = "termination"
      })

    scheduler.signal()
    self.synchronized{while(!done) self.wait()}

    val data = cSet.get(CType)
    assert(data.size == 2)
    assert(data.contains(7))
    assert(data.contains(5))
  }

  /**
    *
    *    |---R1--> [  B  ] --R3--|
    * [  A  ]                [  D  ]
    *    |---R2--> [  C  ] --R4--|
    *
    */
  test("Test Fork Flow", Checked) {
    implicit val (blackboard, scheduler) = Blackboard.newBlackboard

    val self = this
    var done = false

    val aSet = new TypedSet(AType)
    val bSet = new TypedSet(BType)
    val cSet = new TypedSet(CType)
    val dSet = new TypedSet(DType)
    blackboard.addDS(aSet)
    blackboard.addDS(bSet)
    blackboard.addDS(cSet)
    blackboard.addDS(dSet)

    val even = new RuleAgent(new IntManipulationRule(AType, BType, x => if(x % 2 == 0) x / 2 else x, "even"))
    val uneven = new RuleAgent(new IntManipulationRule(AType, CType, x => if( x % 2 != 0) 3*x + 1 else x, "uneven"))
    val merge1 = new RuleAgent(new IntManipulationRule(BType, DType, x => x-5, "mergeEven"))
    val merge2 = new RuleAgent(new IntManipulationRule(CType, DType, x => x-3, "mergeUneven"))


    blackboard.addData(AType)(7)
    blackboard.addData(AType)(20)

    blackboard.registerAgent(even)
    blackboard.registerAgent(uneven)
    blackboard.registerAgent(merge1)
    blackboard.registerAgent(merge2)

    blackboard.registerAgent(
      new AbstractAgent {
        override val interest : Option[Seq[DataType[Any]]] = None
        override def init(): Iterable[Task] = Seq()
        override def filter(event: Event): Iterable[Task] = event match{
          case DoneEvent => println("Done"); self.synchronized{done = true; self.notifyAll()}; Seq()
          case _ => Seq()
        }

        override def name: String = "termination"
      })

    scheduler.signal()
    self.synchronized{while(!done) self.wait()}

    val data = dSet.get(DType)
    assert(data.size == 2)
    assert(data.contains(19))
    assert(data.contains(5))
  }

  test("Test : Loop till nothing to be done", Checked) {
    implicit val (blackboard, scheduler) = Blackboard.newBlackboard

    val self = this
    var done = false

    val aSet = new TypedSet(AType)
    val bSet = new TypedSet(BType)
    val aBarrier = new AgentBarrier(AType, 2)

    blackboard.addDS(aSet)
    blackboard.addDS(bSet)
    blackboard.addDS(aBarrier)

    val even = new RuleAgent(new IntManipulationRule(AType, AType, x => if(x % 2 == 0) x / 2 else x, "even"))
    val uneven = new RuleAgent(new IntManipulationRule(AType, AType, x => if(x % 2 != 0 && x != 1) 3*x + 1 else x, "uneven"))
    val move = new RuleAgent(new MovingRule(AType, BType, aBarrier))

    blackboard.addData(AType)(7)
    blackboard.addData(AType)(20)

    blackboard.registerAgent(even)
    blackboard.registerAgent(uneven)
    blackboard.registerAgent(move)

    blackboard.registerAgent(
      new AbstractAgent {
        override val interest : Option[Seq[DataType[Any]]] = None
        override def init(): Iterable[Task] = Seq()
        override def filter(event: Event): Iterable[Task] = event match{
          case DoneEvent => println("Done"); self.synchronized{done = true; self.notifyAll()}; Seq()
          case _ => Seq()
        }

        override def name: String = "termination"
      })

    scheduler.signal()
    self.synchronized{while(!done) self.wait()}

    val data = bSet.get(BType)
    assert(data.size == 1)
    assert(data.contains(1))
    assert(aSet.get(AType).isEmpty)
  }



  class IntType extends DataType[Int]{
    def convert(d:Any) : Int = d match {
      case i : Int => i
      case _ => throw new SZSException(SZS_Error)
    }
  }

  case object AType extends IntType
  case object BType extends IntType
  case object CType extends IntType
  case object DType extends IntType


  class IntManipulationRule(inType : DataType[Int],
                           outType : DataType[Int],
                           compute : Int => Int,
                           val name : String = "none") extends Rule {
    override val inTypes: Seq[DataType[Any]] = Seq(inType)
    override val moving: Boolean = inType != outType
    override val outTypes: Seq[DataType[Any]] = Seq(outType)
    override def canApply(r: Delta): Seq[Hint] = {
      val inserts = r.inserts(inType)
      val it = inserts.iterator
      var hints : Seq[Hint] = Seq()
      while(it.hasNext){
        val v = it.next()
        val w = compute(v)
        if(v != w){
          println(s"[${name}] ${v} : ${inType} => ${w} : ${outType}")
          hints = new IntManipulationHint(v, w) +: hints
        } else {
//          println(s"[${name}] release ${v} : ${inType}")
          hints = new ReleaseLockHint(inType, v) +: hints
        }
      }
      hints
    }

    class IntManipulationHint(oldI : Int, newI : Int) extends Hint {
      override def apply(): Delta = {
        val r = Result()
        r.remove(inType)(oldI)
        r.insert(outType)(newI)
      }
      override val read: Map[DataType[Any], Set[Any]] = Map()
      override val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(oldI))
    }
  }
}
