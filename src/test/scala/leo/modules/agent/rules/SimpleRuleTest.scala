package leo.modules.agent.rules

import leo.{Checked, LeoTestSuite}
import leo.agents.{AbstractAgent, Task}
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.scheduler.Scheduler

import scala.collection.mutable

/**
  * Created by mwisnie on 12/8/16.
  */
class SimpleRuleTest extends LeoTestSuite {

  test("Double Insertion of ds.", Checked) {
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    val store = new SimpleStore
    blackboard.addDS(store)
    blackboard.addDS(store)

    assert(blackboard.getDS.size == 1, "A double inserted data structure should only be contained once.")
  }

  test("Prepend: a", Checked){
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    val store = new SimpleStore
    val prep = new PrependRule("a", store)

    store.strings.add("hallo")
    store.strings.add("test")
    store.strings.add("nice")

    blackboard.addDS(store)
    val prepAgent = new RuleAgent(prep)
    blackboard.registerAgent(prepAgent)

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


    assert(store.strings.size == 3, "The store should only contain 3 strings.")
    assert(store.strings.contains("ahallo"), "The store should contain 'ahello'.")
    assert(store.strings.contains("atest"), "The store should contain 'atest'.")
    assert(store.strings.contains("anice"), "The store should contain 'anice'.")

  }

  test("Append: b", Checked){
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    val store = new SimpleStore
    val prep = new AppendRule("b", store)

    store.strings.add("hallo")
    store.strings.add("test")
    store.strings.add("nice")

    blackboard.addDS(store)
    val prepAgent = new RuleAgent(prep)
    blackboard.registerAgent(prepAgent)

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
    self.synchronized{ while(!done) self.wait()}


    assert(store.strings.size == 3, "The store should only contain 3 strings.")
    assert(store.strings.contains("hallob"), "The store should contain 'ahello'.")
    assert(store.strings.contains("testb"), "The store should contain 'atest'.")
    assert(store.strings.contains("niceb"), "The store should contain 'anice'.")

  }

  test("Pre/Append: a/b"){
    val (blackboard, scheduler) = Blackboard.newBlackboard
    val self = this
    var done = false
    val store = new SimpleStore
    val prep = new PrependRule("a", store)
    val app = new AppendRule("b", store)

    store.strings.add("hallo")
    store.strings.add("test")
    store.strings.add("nice")

    blackboard.addDS(store)
    val prepAgent = new RuleAgent(prep)
    val appAgent = new RuleAgent(app)
    blackboard.registerAgent(prepAgent)
    blackboard.registerAgent(appAgent)

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


    assert(store.strings.size == 3, "The store should only contain 3 strings.")
    assert(store.strings.contains("ahallob"), "The store should contain 'ahello'.")
    assert(store.strings.contains("atestb"), "The store should contain 'atest'.")
    assert(store.strings.contains("aniceb"), "The store should contain 'anice'.")

  }
}


case object StringType extends DataType[String]{
  override def convert(d: Any): String = d.asInstanceOf[String]
}
class SimpleStore extends DataStore {

  val strings : mutable.Set[String] = mutable.HashSet[String]()

  override val storedTypes: Seq[DataType[Any]] = Seq(StringType)
  override def updateResult(r: Delta): Boolean = synchronized{
    var (del, ins) : (Seq[String], Seq[String])= r.updates(StringType).map{case (a,b) => (a.asInstanceOf[String], b.asInstanceOf[String])}.unzip
    del = del ++ r.removes(StringType).map(_.asInstanceOf[String])
    ins = ins ++ r.inserts(StringType).map(_.asInstanceOf[String])

    del foreach (strings.remove(_))
    ins foreach (strings.add(_))

    true
  }
  override def clear(): Unit = synchronized(strings.clear())
  override def all[T](t: DataType[T]): Set[T] = if(t == StringType) synchronized(strings.toSet.asInstanceOf[Set[T]]) else Set()
}


class PrependRule(letter : String, observe : SimpleStore) extends Rule {
  override val name: String = s"Prepend(${letter})"
  override def canApply(r: Delta): Seq[Hint] = {
    if(r.isEmpty){
      synchronized{
        val undone = observe.strings.filterNot(s => s.startsWith(letter)).toSeq
        undone map {s => new ChangeStringHint(s, letter + s)}
      }
    } else {
      var (_, ins): (Seq[String], Seq[String]) = r.updates(StringType).map { case (a, b) => (a.asInstanceOf[String], b.asInstanceOf[String]) }.unzip
      ins = ins ++ r.inserts(StringType).map(_.asInstanceOf[String])
      val undone = observe.strings.filterNot(s => s.startsWith(letter)).toSeq
      undone map {s => new ChangeStringHint(s, letter + s)}
    }
  }
}

class AppendRule(letter : String, observe : SimpleStore) extends Rule {
  override val name: String = s"Append(${letter})"
  override def canApply(r: Delta): Seq[Hint] = {
    if(r.isEmpty){
      synchronized{
        val undone = observe.strings.filterNot(s => s.endsWith(letter)).toSeq
        undone map {s => new ChangeStringHint(s, s + letter)}
      }
    } else {
      var (_, ins): (Seq[String], Seq[String]) = r.updates(StringType).map { case (a, b) => (a.asInstanceOf[String], b.asInstanceOf[String]) }.unzip
      ins = ins ++ r.inserts(StringType).map(_.asInstanceOf[String])
      val undone = observe.strings.filterNot(s => s.endsWith(letter)).toSeq
      undone map {s => new ChangeStringHint(s, s + letter)}
    }
  }
}

class ChangeStringHint(olds : String, news : String) extends Hint {
  override def apply(): Delta = {
    Result().update(StringType)(olds)(news)
  }
  override val read: Map[DataType[Any], Set[Any]] = Map()
  override val write: Map[DataType[Any], Set[Any]] = Map(StringType -> Set(olds))
}