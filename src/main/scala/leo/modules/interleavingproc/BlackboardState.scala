package leo.modules.interleavingproc

import leo.datastructures.blackboard.{DataStore, DataType, Delta}
import leo.datastructures.{AnnotatedClause, Clause, Signature}
import leo.modules.SZSException
import leo.modules.output.{SZS_Error, StatusSZS}
import leo.modules.seqpproc.State
import leo.modules.control.Control

/**
  *
  * A capsule to enclose a state for the interleaving loop
  * in the blackboard to handle the transactions.
  *
  */
class BlackboardState(val state : State[AnnotatedClause]) extends DataStore {
  val apply : State[AnnotatedClause] = state
  implicit val sig = state.signature

  // Store the next unprocessed, until the task was really executed (Result is written)
  protected var nextUnprocessed : Option[AnnotatedClause] = None  // TODO ClauseProxy inherits AnyRef for null reference
  protected var nextUnprocessedSet : Boolean = false

  def getNextUnprocessed : AnnotatedClause = synchronized{
    if(!nextUnprocessedSet){
      nextUnprocessed = Some(state.nextUnprocessed)
      nextUnprocessedSet = true
    }
    nextUnprocessed.get
  }

  def realeaseUnprocessed : Unit = {nextUnprocessedSet = false}

  @inline def hasNextUnprocessed : Boolean = synchronized {
    state.unprocessedLeft
  }

  override val storedTypes: Seq[DataType[Any]] = Seq(UnprocessedClause, ProcessedClause, RewriteRule, SZSStatus, DerivedClause, StatisticType)
  override def clear(): Unit = {
    leo.Out.info("Could not clear the state. Not yet implemented.")
  }
  override def all[T](t: DataType[T]): Set[T] = ???     // TODO implement
  override def updateResult(r: Delta): Boolean = synchronized {
    val newUnprocessedSeq = r.inserts(UnprocessedClause)
    state.incGeneratedCl(newUnprocessedSeq.size)
    val newUnprocessed = newUnprocessedSeq.iterator
    while(newUnprocessed.nonEmpty){
      state.addUnprocessed(newUnprocessed.next())
    }
    val rmUnprocessed = r.removes(UnprocessedClause).iterator
    while(rmUnprocessed.nonEmpty){
      val rm = rmUnprocessed.next()
      if(nextUnprocessed.nonEmpty & nextUnprocessed.get == rm){
        nextUnprocessedSet = false
      }
    }
    // Processed should only be one and should correspond to the variable [[nextUnprocessed]]
    val newProcessed = r.inserts(ProcessedClause).iterator
    if(newProcessed.nonEmpty){
      val n = newProcessed.next()
      state.incProofLoopCount() // TODO move to statistic?
      state.addProcessed(n)
      if(Clause.unit(n.cl) && !Clause.rewriteRule(n.cl)){
        state.addNonRewriteUnit(n)
      }
    }

    // Adding new rewrite Rules
    val newRewrite = r.inserts(RewriteRule).iterator
    while(newRewrite.nonEmpty){
      val nR = newRewrite.next()
      state.addRewriteRule(nR)
    }
    // Backward Subsumption TODO implement in state
    val subsumed = r.removes(ProcessedClause).toSet
    if (subsumed.nonEmpty) {
      leo.Out.trace(s"[Redundancy] Subsumed processed clauses")
      state.incBackwardSubsumedCl(subsumed.size)
      leo.Out.finest(s"[Redundancy] Processed subsumed:" +
        s"\n\t${subsumed.map(_.pretty).mkString("\n\t")}")
      // Remove from processed set, from indexes etc.
      state.removeProcessed(subsumed)
      state.removeUnits(subsumed)
      Control.removeFromIndex(subsumed)
      // Remove all direct descendants of clauses in `bachSubsumedClauses` from unprocessed
//      val descendants = Control.descendants(subsumed)
//      state.incDescendantsDeleted(descendants.size)
//      state.removeUnprocessed(descendants)
    }
    // Check for a found Result
    val status = r.inserts(SZSStatus).iterator
    if(status.nonEmpty){
      val s =status.next().asInstanceOf[StatusSZS]
      state.setSZSStatus(s)
      // If a result is found, check for a proof
      val derivedClauses = r.inserts(DerivedClause).iterator
      if(derivedClauses.nonEmpty){
        val dC = derivedClauses.next()
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
  def fresh(sig: Signature) : BlackboardState = {
    new BlackboardState(State.fresh[AnnotatedClause](sig))
  }
}

/**
  * Type for unprocessed clauses in the state.
  */
case object UnprocessedClause extends DataType[AnnotatedClause] {
  override def convert(d: Any): AnnotatedClause = d match {
    case c : AnnotatedClause => c
    case _ => throw new SZSException(SZS_Error, s"Expected AnnotatedClause, but got $d")
  }
}
/**
  * Type for processed clauses in the state
  */
case object ProcessedClause extends DataType[AnnotatedClause] {
  override def convert(d: Any): AnnotatedClause = d match {
    case c : AnnotatedClause => c
    case _ => throw new SZSException(SZS_Error, s"Expected AnnotatedClause, but got $d")
  }
}

/**
  * Type for rewrite rules
  */
case object RewriteRule extends DataType[AnnotatedClause] {
  override def convert(d: Any): AnnotatedClause = d match {
    case c : AnnotatedClause => c
    case _ => throw new SZSException(SZS_Error, s"Expected AnnotatedClause, but got $d")
  }
}
/**
  * Setting the SZS status
  */
case object SZSStatus extends DataType[StatusSZS] {
  override def convert(d: Any): StatusSZS = d match {
    case c : StatusSZS => c
    case _ => throw new SZSException(SZS_Error, s"Expected AnnotatedClause, but got $d")
  }
}
/**
  * Setting the Derived Clause (contains the proof tree in the derived empty clause)
  */
case object DerivedClause extends DataType [AnnotatedClause] {
  override def convert(d: Any): AnnotatedClause = d match {
    case c : AnnotatedClause => c
    case _ => throw new SZSException(SZS_Error, s"Expected AnnotatedClause, but got $d")
  }
}

/**
  * Type for statistical information
  */
case object StatisticType extends DataType[Statistic] {
  override def convert(d: Any): Statistic = d match {
    case s : Statistic => s
    case _ => throw new SZSException(SZS_Error, s"Expected Statistic, but got $d")
  }
}

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