package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, MultiPriorityQueue}
import leo.datastructures.blackboard.{DataStore, DataType, Result}

import scala.collection.mutable

case object Unprocessed extends DataType

/**
  * Stores the unprocessed Formulas for
  * the algorithm execution in [[leo.modules.control.Control]]
  */
class UnprocessedSet extends DataStore{

  private final val mpq: MultiPriorityQueue[AnnotatedClause] = MultiPriorityQueue.empty
  mpq.addPriority(leo.datastructures.ClauseProxyOrderings.lex_weightAge.reverse.asInstanceOf[Ordering[AnnotatedClause]])
  mpq.addPriority(leo.datastructures.ClauseProxyOrderings.fifo.asInstanceOf[Ordering[AnnotatedClause]])
  mpq.addPriority(leo.datastructures.ClauseProxyOrderings.goalsfirst.reverse.asInstanceOf[Ordering[AnnotatedClause]])
  mpq.addPriority(leo.datastructures.ClauseProxyOrderings.nongoalsfirst.reverse.asInstanceOf[Ordering[AnnotatedClause]])
  final private val prio_weights = Seq(8,1,2,2)
  private var cur_prio = 0
  private var cur_weight = 0

  final def unprocessedLeft: Boolean = synchronized(!mpq.isEmpty)

  /**
    * Gets the set of unprocessed clauses.
    * The returned set is immutable.
    *
    * @return Set of unprocessed clauses
    */
  final def unprocessed: Set[AnnotatedClause] = {
    if (mpq == null) leo.Out.comment("MPQ null")
    mpq.toSet
  }

  final def nextUnprocessed: AnnotatedClause = {
    leo.Out.debug(s"[###] Selecting with priority $cur_prio: element $cur_weight")
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
  override val storedTypes: Seq[DataType] = Seq(Unprocessed)

  /**
    *
    * Inserts all results produced by an agent into the datastructure.
    *
    * @param r - A result inserted into the datastructure
    */
  override def updateResult(r: Result): Boolean = synchronized {
    val ins1 = r.inserts(Unprocessed)
    val del1 = r.removes(Unprocessed)
    val ins2 = r.updates(Unprocessed).map(_._2)

    val ins = (ins1 ++ ins2).iterator


    var change = false

    while(ins.hasNext) {
      ins.next match {
        case c: AnnotatedClause =>
          mpq.insert(c)
          change |= true
        case x => leo.Out.debug(s"Tried to add $x to Unprocessed Set, but was no clause.")
      }
    }
    change
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
  override def all(t: DataType): Set[Any] = t match{
    case Unprocessed => synchronized(mpq.toSet.toSet)
    case _ => Set()

  }
}
