package leo.modules.agent.rules.control_rules

import leo.datastructures.ClauseProxyOrderings._
import leo.datastructures.{AnnotatedClause, MultiPriorityQueue}
import leo.datastructures.blackboard._
import leo.modules.GeneralState

import scala.collection.mutable

//case object Unprocessed extends ClauseType

/**
  * Stores the unprocessed Formulas for
  * the algorithm execution in [[leo.modules.control.Control]]
  */
class UnprocessedSet(unprocessedType : DataType[AnnotatedClause])(implicit state : GeneralState[AnnotatedClause]) extends DataStore{

  // Keeping track of data inside of mpq, to efficiently query update changes
  private final val valuesStored : mutable.Set[AnnotatedClause] = mutable.HashSet[AnnotatedClause]()

  private final val mpq: MultiPriorityQueue[AnnotatedClause] = MultiPriorityQueue.empty
  mpq.addPriority(litCount_conjRelSymb(state.symbolsInConjecture, 0.005f, 100, 50).asInstanceOf[Ordering[AnnotatedClause]])
  mpq.addPriority(goals_SymbWeight(100,20).asInstanceOf[Ordering[AnnotatedClause]])
  mpq.addPriority(goals_litCount_SymbWeight(100,20).asInstanceOf[Ordering[AnnotatedClause]])
  mpq.addPriority(nongoals_litCount_SymbWeight(100,20).asInstanceOf[Ordering[AnnotatedClause]])
  mpq.addPriority(conjRelSymb(state.symbolsInConjecture, 0.005f, 100, 50).asInstanceOf[Ordering[AnnotatedClause]])
  mpq.addPriority(sos_conjRelSymb(state.symbolsInConjecture, 0.05f, 2, 1).asInstanceOf[Ordering[AnnotatedClause]])
  mpq.addPriority(oldest_first.asInstanceOf[Ordering[AnnotatedClause]])
  final private val prio_weights = Seq(10,1,1,2,10,2,1)
  private var cur_prio = 0
  private var cur_weight = 0


  override def isEmpty: Boolean = synchronized(valuesStored.isEmpty)

  final def unprocessedLeft: Boolean = synchronized(!mpq.isEmpty)

  /**
    * Gets the set of unprocessed clauses.
    * The returned set is immutable.
    *
    * @return Set of unprocessed clauses
    */
  final def unprocessed: Set[AnnotatedClause] = {
    valuesStored.toSet  // Cloning into immutable
  }

  final def nextUnprocessed: AnnotatedClause = synchronized {
//    println(s"[###] Selecting with priority $cur_prio: element $cur_weight")
//    println(mpq.pretty)
    if (cur_weight >= prio_weights(cur_prio)) {
      cur_weight = 0
      cur_prio = (cur_prio + 1) % mpq.priorities
    }
    val result = mpq.dequeue(cur_prio)
    cur_weight = cur_weight+1
    result
  }

  /**
    * This method returns all Types stored by this data structure.
    *
    * @return all stored types
    */
  override val storedTypes: Seq[DataType[Any]] = Seq(unprocessedType)

  /**
    *
    * Inserts all results produced by an agent into the datastructure.
    *
    * @param r - A result inserted into the datastructure
    */
  override def updateResult(r: Delta) : Delta = synchronized {
    val ins1 = r.inserts(unprocessedType)
    val del1 = r.removes(unprocessedType)
    val ins2 = r.updates(unprocessedType).map(_._2)
    val del2 = r.updates(unprocessedType).map(_._1)

    val ins = ins1 ++ ins2
    val del = del1 ++ del2

    // Performs an update on valuesStored and filters for the ones with changes.
    val filterDel = del.filter(c => valuesStored.remove(c))
    val filterIns = ins.filter(c => valuesStored.add(c))


    mpq.remove(filterDel)
    mpq.insert(filterIns)

//    println(s"Unprocessed after update (size=${valuesStored.size}):\n  ${valuesStored.map(_.pretty).mkString("\n  ")}")

    val res = if(filterIns.isEmpty && filterDel.isEmpty) {
      EmptyDelta
    } else {
      new ImmutableDelta(
        if(filterIns.nonEmpty) Map(unprocessedType -> filterIns) else Map(),
        if(filterDel.nonEmpty) Map(unprocessedType -> filterDel) else Map())
    }
    if(!res.isEmpty && ins.nonEmpty)
      leo.Out.debug(s"Unprocessed after update:\n  ${valuesStored.map(_.pretty(state.signature)).mkString("\n  ")}")
    res
  }

  /**
    * Removes everything from the data structure.
    * After this call the ds should behave as if it was newly created.
    */
  override def clear(): Unit = synchronized(while(!mpq.isEmpty) nextUnprocessed)

  /**
    * Returns a list of all stored data.
    *
    * @param t
    * @return
    */
  override def get[T](t: DataType[T]): Set[T] = t match{
    case `unprocessedType` => synchronized(mpq.toSet.asInstanceOf[Set[T]])
    case _ => Set()

  }
}
