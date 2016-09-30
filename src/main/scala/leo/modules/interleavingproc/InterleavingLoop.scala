package leo.modules.interleavingproc

import leo._
import leo.agents.{InterferingLoop, OperationState}
import leo.datastructures.{AnnotatedClause, Clause, ClauseProxy}
import leo.datastructures.blackboard.{DataType, Result}
import leo.modules.output.{SZS_ContradictoryAxioms, SZS_Theorem, StatusSZS}
import leo.modules.seqpproc.{Control, State}

object InterleavingLoop {
  type A = AnnotatedClause
}

/**
  *
  * Implementation of @[[leo.modules.seqpproc.SeqPProc]] with interleavable
  * loop parts.
  *
  * Assumes all clauses have initially have been preprocessed and inserted into the fvIndex
  *
  * TODO Preprocess Clauses and Initialize FVIndex before this agent works or put it into the init
  *
  * @author Max Wisniewski
  * @since 7/18/16
  */
class InterleavingLoop(state : BlackboardState[InterleavingLoop.A]) extends InterferingLoop[StateView[InterleavingLoop.A]] {

  @inline private final val forwardSubsumed = true
  @inline private final val notForwardSubsumed = false

  override def name: String = "interleaving-seq-loop"
  override def init: Option[StateView[InterleavingLoop.A]] = {
   // val n : InterleavingLoop.A = state.conjecture.fold(state.getNextUnprocessed)(c => c)
    val n = state.getNextUnprocessed
    commonFilter(n)
  }
  override def canApply: Option[StateView[InterleavingLoop.A]] = {
    // Selecting the next Clause from unprocessed
    if(state.state.unprocessed.isEmpty) return None
    val select = state.getNextUnprocessed // Last if not yet reinserted

    // The normal loop from seqpproc
    commonFilter(select)
  }

  private def commonFilter(c : InterleavingLoop.A) : Option[StateView[InterleavingLoop.A]] = {

    // Simplify and rewrite

    val simpRewrite = Control.rewriteSimp(c, state.state.rewriteRules)

    // Check for newly generated empty clause
    if(Clause.effectivelyEmpty(simpRewrite.cl)){
      // We found the empty clause
      if (state.conjecture.isEmpty) {
        return Some(StateView(c, simpRewrite, Set(), Set(), Some(SZS_ContradictoryAxioms, Some(simpRewrite)), notForwardSubsumed, Set()))
      } else {
        return Some(StateView(c, simpRewrite, Set(), Set(), Some(SZS_Theorem, Some(simpRewrite)), notForwardSubsumed, Set()))
      }
    }

    // Check for forward subsumption
    val subsumed = Control.forwardSubsumptionTest(simpRewrite, state.state.processed)
    if(subsumed.nonEmpty){
      // Create a Forward Subsumption task
      return Some(StateView(c, simpRewrite, Set(), Set(), None, forwardSubsumed, Set()))
    }

    // Main inference tests
    mainLoopInferences(c, simpRewrite, state.state)
  }

  @inline private final def mainLoopInferences(c : InterleavingLoop.A, cl: InterleavingLoop.A, state: State[InterleavingLoop.A]): Option[StateView[InterleavingLoop.A]] = {
    var cur: InterleavingLoop.A = cl
    var newclauses: Set[InterleavingLoop.A] = Set()
    val actRewrite = state.rewriteRules

    // Check backward subsumption
    val backSubsumedClauses = Control.backwardSubsumptionTest(cur, state.processed)
    val considerClauses = state.processed -- backSubsumedClauses

    cur = Control.funcext(cur)
    cur = Control.liftEq(cur)

    // TODO Split into possible partners and results
    val paramod_result = Control.paramodSet(cur, considerClauses)
    newclauses = newclauses union paramod_result

    Some(StateView(c, cur , newclauses, backSubsumedClauses, None, notForwardSubsumed, actRewrite))
  }




  override def apply(opState: StateView[InterleavingLoop.A]): Result = {
    val result = Result()

    // First check for empty clause

    if(opState.closed.nonEmpty){
      val (status, optResult) = opState.closed.get
      result.insert(SZSStatus)(status)
      if(optResult.nonEmpty){
        result.insert(DerivedClause)(optResult.get)
      }
      return result
    }


    // Remove the current processed in any case
    result.remove(UnprocessedClause)(opState.select)

    // Forward Subsumption
    if(opState.forwardSubsumption){
      result.insert(StatisticType)(Statistic(0,0,0,0,1,0))  // Increase one forward Subsumption TODO Insert the generated clauses nontheless
      return result
    }

    // selected clause is processed
    result.insert(ProcessedClause)(Control.rewriteSimp(opState.select, opState.actRewrite))  // TODO Store this simplified version?
    // Remove backward subsumed
    val subIt : Iterator[InterleavingLoop.A] = opState.subsumed.iterator
    while(subIt.hasNext)
      result.remove(ProcessedClause)(subIt.next())


    // Otherwise do real work
    val cur = opState.processedSelect
    val rewrite = opState.actRewrite

    var newclauses = opState.paramodPartners // TODO Perform real paramod after splitting the step

    val boolext_result = Control.boolext(cur)
    newclauses = newclauses union boolext_result

    val factor_result = Control.factor(cur)
    newclauses = newclauses union factor_result

    newclauses = newclauses union Control.convertDefinedEqualities(newclauses)


    val newIt = newclauses.iterator
    while (newIt.hasNext) {
      var newCl = newIt.next()
      newCl = Control.rewriteSimp(newCl, rewrite)

      if (!Clause.trivial(newCl.cl)) {
        result.insert(UnprocessedClause)(newCl)
      }
    }

    result
  }
}

/**
  * A fixed view of the current state for next execution of the loop
  *
  * @param processedSelect The set of the processed and simplified parts of the selected Term
  * @param select The original selected clause
  * @param paramodPartners The possible paramodulation partners for the selected clause ATM the results, later it will be the partners
  * @param subsumed the set of possible subsumed processed clauses
  * @param closed The result + an optional reason
  * @tparam T Type of the AnnotatedClauses
  */
case class StateView[T <: ClauseProxy](select : T, processedSelect : T, paramodPartners : Set[T] = Set(), subsumed : Set[T] = Set(), closed : Option[(StatusSZS, Option[T])] = None, forwardSubsumption : Boolean = false, actRewrite : Set[T] = Set[T]()) extends OperationState {
  override def datatypes: Iterable[DataType] = Seq(ProcessedClause, UnprocessedClause)
  override def readData(ty: DataType): Set[Any] = if(ty == UnprocessedClause) (/*paramodPartners + */Set(select)).asInstanceOf[Set[Any]] else Set()
  override def writeData(ty: DataType): Set[Any] = if(ty == ProcessedClause) subsumed.asInstanceOf[Set[Any]] else Set() // TODO type check?
}


