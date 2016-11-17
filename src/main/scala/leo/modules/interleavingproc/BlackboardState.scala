package leo.modules.interleavingproc

import leo.datastructures.blackboard.{DataStore, DataType, Result}
import leo.datastructures.{AnnotatedClause, Clause, ClauseProxy, Signature}
import leo.modules.output.StatusSZS
import leo.modules.output.logger.Out
import leo.modules.seqpproc.State
import leo.modules.control.Control

/**
  *
  * A capsule to enclose a state for the interleaving loop
  * in the blackboard to handle the transactions.
  *
  */
class BlackboardState[T <: ClauseProxy](val state : State[T]) extends DataStore {
  val apply : State[T] = state

  // Store the next unprocessed, until the task was really executed (Result is written)
  protected var nextUnprocessed : Option[T] = None  // TODO ClauseProxy inherits AnyRef for null reference
  protected var nextUnprocessedSet : Boolean = false

  var conjecture : Option[T] = None

  def getNextUnprocessed : T = synchronized{
    if(!nextUnprocessedSet){
      nextUnprocessed = Some(state.nextUnprocessed)
      nextUnprocessedSet = true
    }
    nextUnprocessed.get
  }

  @inline def hasNextUnprocessed : Boolean = synchronized {
    state.unprocessedLeft
  }

  override val storedTypes: Seq[DataType] = Seq(UnprocessedClause, ProcessedClause, RewriteRule, SZSStatus, DerivedClause, StatisticType)
  override def clear(): Unit = {
    Out.info("Could not clear the state. Not yet implemented.")
  }
  override def all(t: DataType): Set[Any] = ???     // TODO implement
  override def updateResult(r: Result): Boolean = synchronized {
    // Unprocessed can only be added
    val newUnprocessed = r.inserts(UnprocessedClause).iterator
    while(newUnprocessed.nonEmpty){
      state.addUnprocessed(newUnprocessed.next().asInstanceOf[T])
    }
    val rmUnprocessed = r.removes(UnprocessedClause).iterator
    while(rmUnprocessed.nonEmpty){
      val rm = rmUnprocessed.next().asInstanceOf[T]
      if(nextUnprocessed.nonEmpty & nextUnprocessed.get == rm){
        nextUnprocessedSet = false
      }
    }
    // Processed should only be one and should correspond to the variable [[nextUnprocessed]]
    val newProcessed = r.inserts(ProcessedClause).iterator
    if(newProcessed.nonEmpty){
      val n = newProcessed.next().asInstanceOf[T]
      state.addProcessed(n)
      if(n.isInstanceOf[AnnotatedClause]) Control.fvIndexInsert(n.asInstanceOf[AnnotatedClause])
    }

    // Adding new rewrite Rules
    val newRewrite = r.inserts(RewriteRule).iterator
    while(newRewrite.nonEmpty){
      val nR = newRewrite.next().asInstanceOf[T]
      state.addRewriteRule(nR)
    }
    // Backward Subsumption TODO implement in state
    val subsumed = r.removes(ProcessedClause)
    if(subsumed.nonEmpty){
      val subsumedCast : Set[T] = subsumed.map(_.asInstanceOf[T]).toSet
      state.setProcessed(state.processed -- subsumedCast)
      if(subsumedCast.isInstanceOf[Set[AnnotatedClause]]) Control.fvIndexRemove(subsumedCast.asInstanceOf[Set[AnnotatedClause]])
    }
    // Check for a found Result
    val status = r.inserts(SZSStatus).iterator
    if(status.nonEmpty){
      val s =status.next().asInstanceOf[StatusSZS]
      state.setSZSStatus(s)
      // If a result is found, check for a proof
      val derivedClauses = r.inserts(DerivedClause).iterator
      if(derivedClauses.nonEmpty){
        val dC = derivedClauses.next().asInstanceOf[T]
        state.setDerivationClause(dC)
      }
    }

    // TODO Statistic
    val statistc = r.inserts(StatisticType).iterator
    if(statistc.nonEmpty){
      val stat = statistc.next().asInstanceOf[Statistic]
      // TODO do something with the statistic
      state.incForwardSubsumedCl(stat.forwardSubsumedClauses)
      state.incBackwardSubsumedCl(stat.backwardSubsumedClauses)
      state.incFactor(stat.factorClauses)
      state.incGeneratedCl(stat.generatedClauses)
      state.incParamod(stat.paramodClauses)
      // TODO checken, welche Fehlder überhaupt gefüllt sind um so etwas von der linearen suche zu verkleinern.
    }
    true
  }
}



object BlackboardState {
  def fresh[T <: ClauseProxy](sig: Signature) : BlackboardState[T] = {
    new BlackboardState[T](State.fresh[T](sig))
  }
}

/**
  * Type for unprocessed clauses in the state.
  */
case object UnprocessedClause extends DataType {}
/**
  * Type for processed clauses in the state
  */
case object ProcessedClause extends DataType {}

/**
  * Type for rewrite rules
  */
case object RewriteRule extends DataType {}
/**
  * Setting the SZS status
  */
case object SZSStatus extends DataType {}
/**
  * Setting the Derived Clause (contains the proof tree in the derived empty clause)
  */
case object DerivedClause extends DataType {}

/**
  * Type for statistical information
  */
case object StatisticType extends DataType {}

/**
  *
  * Used to Update (insert) new statistical data into the state
  *
  * @param generatedClauses The number of newly generated clauses
  * @param trivialClauses The number of deleted trivial clauses
  * @param paramodClauses The number of paramod clauses
  * @param factorClauses The number of factored clauses
  * @param forwardSubsumedClauses The number of ForwardSubsumedClauses
  * @param backwardSubsumedClauses The number of BackwardSubsumedClauses
  */
case class Statistic(generatedClauses : Int,
                     trivialClauses : Int,
                     paramodClauses : Int,
                     factorClauses : Int,
                     forwardSubsumedClauses : Int,
                     backwardSubsumedClauses : Int)