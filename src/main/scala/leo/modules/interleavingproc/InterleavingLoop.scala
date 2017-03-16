package leo.modules.interleavingproc

import leo.{Configuration, Out}
import leo.agents.{InterferingLoop, OperationState, Task}
import leo.datastructures._
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.calculus._
import leo.modules.output._
import leo.modules.seqpproc.State
import leo.modules.control.Control
import leo.modules.proof_object.CompressProof

import scala.collection.mutable

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
class InterleavingLoop(state : BlackboardState, unification : UnificationStore[InterleavingLoop.A], sig : Signature) extends InterferingLoop[StateView[InterleavingLoop.A]] {

  @inline private final val forwardSubsumed = true
  @inline private final val notForwardSubsumed = false

  implicit val signature : Signature = sig

  override def terminated : Boolean = synchronized(terminatedFlag)
  private var terminatedFlag: Boolean = false

  override def name: String = "interleaving-seq-loop"
  override def init: Option[StateView[InterleavingLoop.A]] = {
   // val n : InterleavingLoop.A = state.conjecture.fold(state.getNextUnprocessed)(c => c)
    val n = state.getNextUnprocessed
    movedToProcessed.add(n.id)
    commonFilter(n)
  }

  private val maxRound = try {Configuration.valueOf("ll").get.head.toInt} catch {case e : Exception => -1}
  private var actRound = 1

  //TODO Remove after merge with alex
  private val movedToProcessed : mutable.Set[Long] = new mutable.HashSet[Long]
  final val importantInferences : Set[CalculusRule] = Set(PatternUni, PreUni, Choice, PrimSubst, OrderedEqFac, OrderedParamod, NegateConjecture)

  override def canApply: Option[StateView[InterleavingLoop.A]] = {
    // Selecting the next Clause from unprocessed
    if(unification.getOpenUni.nonEmpty) {
      return None
    }
    val sb = new StringBuilder("\n")
    if(actRound > maxRound && maxRound > 0) {
      sb.append("-----------------------------------------------------\n")
      sb.append("Finished Rounds\n")
      sb.append(s"Unprocessed:\n  ${state.state.unprocessed.filter{cl => !movedToProcessed.contains(cl.id)}.map(cl =>
        CompressProof.compressAnnotation(cl)(CompressProof.lastImportantStep(importantInferences)).pretty(sig)).mkString("\n  ")}\n")
      sb.append(s"Open Unifications:\n  ${unification.getOpenUni.map(cl =>
        CompressProof.compressAnnotation(cl)(CompressProof.lastImportantStep(importantInferences)).pretty(sig)).mkString("\n  ")}\n")
      sb.append(s"Processed:\n  ${state.state.processed.map(cl =>
        CompressProof.compressAnnotation(cl)(CompressProof.lastImportantStep(importantInferences)).pretty(sig)).mkString("\n  ")}\n")
      sb.append("-----------------------------------------------------\n")
      leo.Out.debug(sb.toString())
      terminatedFlag = true
      return None
    }
    sb.append(s" ------------- Start Round ${actRound}-------------------\n")
    actRound += 1
    sb.append(s"Unprocessed:\n  ${state.state.unprocessed.filter{cl => !movedToProcessed.contains(cl.id)}.map(cl =>
      CompressProof.compressAnnotation(cl)(CompressProof.lastImportantStep(importantInferences)).pretty(sig)).mkString("\n  ")}\n")
    sb.append(s"Open Unifications:\n  ${unification.getOpenUni.map(cl =>
      CompressProof.compressAnnotation(cl)(CompressProof.lastImportantStep(importantInferences)).pretty(sig)).mkString("\n  ")}\n")
    sb.append(s"Processed:\n  ${state.state.processed.map(cl =>
      CompressProof.compressAnnotation(cl)(CompressProof.lastImportantStep(importantInferences)).pretty(sig)).mkString("\n  ")}\n")

    if(!state.state.unprocessedLeft) return None
    val select = state.getNextUnprocessed // Last if not yet reinserted
    movedToProcessed.add(select.id)



    sb.append(s"Select next Unprocessed:\n  >  ${CompressProof.compressAnnotation(select)(CompressProof.lastImportantStep(importantInferences)).pretty(sig)}\n")
    sb.append("-----------------------------------------------------\n\n")
    if(state.state.szsStatus != SZS_Unknown) return None      // TODO Check for less failure prone value
    leo.Out.output(sb.toString())
    // The normal loop from seqpproc
    commonFilter(select)
  }

  private def commonFilter(c : InterleavingLoop.A) : Option[StateView[InterleavingLoop.A]] = {

    // Simplify and rewrite
    var cur = Control.rewriteSimp(c, state.state.rewriteRules)

    /* Functional Extensionality */
    cur = Control.funcext(cur)
    /* To equality if possible */
    cur = Control.liftEq(cur)

    val curCNF = Control.cnf(cur)


    if (curCNF.size == 1 && curCNF.head == cur) {
      // No CNF step, do main loop inferences
      // Check if `cur` is an empty clause
      if (Clause.effectivelyEmpty(cur.cl)) {
        if (state.conjecture == null) {
          return Some(new StateView[InterleavingLoop.A](c, cur, Set(), Set(), Some(SZS_ContradictoryAxioms, Some(cur))))}
        else {
          return Some(new StateView[InterleavingLoop.A](c, cur, Set(), Set(), Some(SZS_Theorem, Some(cur))))}
      } else {
        // Not an empty clause, detect choice definition or do reasoning step.
        val choiceCandidate = Control.detectChoiceClause(cur)
        if (choiceCandidate.isDefined) {
          val choiceFun = choiceCandidate.get
          state.state.addChoiceFunction(choiceFun)  // TODO Insert in transaction
          leo.Out.debug(s"Choice function detected: ${choiceFun.pretty(sig)}\n ====> Invoking next round")
          state.realeaseUnprocessed
          canApply
        } else {
          // Redundancy check: Check if cur is redundant wrt to the set of processed clauses
          // e.g. by forward subsumption
          if (!Control.redundant(cur, state.state.processed)) {
            //Control.submit(state.state.processed, state.state)    // TODO External Prover call
            return mainLoopInferences(c, cur, state.state)
          } else {
            Out.debug(s"Clause ${cur.id} redundant, skipping.")
            return Some(new StateView[InterleavingLoop.A](c, cur, Set(), Set(), None, true))
          }
        }
      }
    }
    else {
      return Some(new StateView(c, cur, Set(), Set(), None, false, Set(), Map(), curCNF))
    }
  }

  @inline private final def mainLoopInferences(c : InterleavingLoop.A, cl: InterleavingLoop.A, state: State[InterleavingLoop.A]): Option[StateView[InterleavingLoop.A]] = {
    var cur: InterleavingLoop.A = cl
    var newclauses: Set[InterleavingLoop.A] = Set()
    val actRewrite = state.rewriteRules

    // Check backward subsumption
    val backSubsumedClauses = Control.backwardSubsumptionTest(cur, state.processed)

    assert(!cur.cl.lits.exists(leo.modules.calculus.FullCNF.canApply), s"\n\tcl ${cur.pretty(sig)} not in cnf")

    val considerClauses = (state.processed -- backSubsumedClauses) + cur  // SeqPProc 463: state.addProcessed(cur)
    Control.insertIndexed(cur)

    cur = Control.funcext(cur)
    cur = Control.liftEq(cur)

    // TODO Split into possible partners and results
    val paramod_result = Control.paramodSet(cur, considerClauses)
    newclauses = newclauses union paramod_result

    Some(StateView[InterleavingLoop.A](c, cur , newclauses, backSubsumedClauses, None, notForwardSubsumed, actRewrite, state.choiceFunctions))
  }




  @inline override def apply(opState: StateView[InterleavingLoop.A]): Delta = {
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

    if(opState.wasNormalized.nonEmpty){
      opState.wasNormalized.foreach {cl => result.insert(UnprocessedClause)(cl)}  // TODO Real loop
      return result
    }

    // Forward Subsumption
    if(opState.forwardSubsumption){

      result.insert(StatisticType)(Statistic(0,0,0,0,1,0))  // Increase one forward Subsumption TODO Insert the generated clauses nontheless
      return result
    }

    // selected clause is processed
    result.insert(ProcessedClause)(Control.rewriteSimp(opState.select, opState.actRewrite))  // TODO Store this simplified version?
    // Remove backward subsumed
    val subIt : Iterator[InterleavingLoop.A] = opState.subsumed.iterator
    while(subIt.hasNext) {
      result.remove(ProcessedClause)(subIt.next())
    }

    // Otherwise do real work
    val cur = opState.processedSelect

    val rewrite =
    if(Clause.unit(cur.cl) && Clause.rewriteRule(cur.cl)){
      result.insert(RewriteRule)(cur)
      opState.actRewrite + cur
    } else {opState.actRewrite}

    var newclauses = opState.paramodPartners                    // TODO Perform real paramod after splitting the step

    val boolext_result = Control.boolext(cur)
    newclauses = newclauses union boolext_result

    val factor_result = Control.factor(cur)
    newclauses = newclauses union factor_result

    /* Prim subst */
    val primSubst_result = Control.primsubst(cur, Configuration.PRIMSUBST_LEVEL)
    newclauses = newclauses union primSubst_result


    newclauses = newclauses union Control.convertDefinedEqualities(newclauses)

    newclauses = newclauses.map(Control.liftEq)

    val choice_result = Control.instantiateChoice(cur, opState.actChoice)
    // TODO Move to statistics state.incChoiceInstantiations(choice_result.size)
    newclauses = newclauses union choice_result

    newclauses = newclauses.filterNot(cw => Clause.trivial(cw.cl))

    // Preunify new Clauses
    // TODO Perform pattern uni here and queue the complete pre unification for later

    // Split unifiable and non-unifiable clauses
    val cIt = newclauses.iterator
    newclauses = Set.empty
    while(cIt.hasNext){
      var ncl = cIt.next()
      ncl = Control.rewriteSimp(ncl, rewrite)
      if(!Clause.trivial(ncl.cl)) {
        if (leo.datastructures.isPropSet(ClauseAnnotation.PropNeedsUnification, ncl.properties)) {
          result.insert(OpenUnification)(ncl)
        } else {
          newclauses += ncl
        }
      }
    }

    /* exhaustively CNF new clauses */
    newclauses = newclauses.flatMap(cw => Control.cnf(cw))
    newclauses = newclauses.map(cw => Control.shallowSimp(Control.liftEq(cw)))
    Control.updateDescendants(cur, newclauses)
    /* Replace eq symbols on top-level by equational literals. */
//    newclauses = newclauses.map(Control.liftEq)

    /* Pre-unify new clauses */
//    newclauses = Control.preunifyNewClauses(newclauses)


    val newIt = newclauses.iterator
    while (newIt.hasNext) {
      var newCl = newIt.next()
      newCl = Control.rewriteSimp(newCl, rewrite)
      assert(Clause.wellTyped(newCl.cl), s"Clause [${newCl.id}] is not well-typed")
      if (Clause.effectivelyEmpty(newCl.cl)){
        result.insert(DerivedClause)(newCl)
        if(state.conjecture.isDefined) {
          result.insert(SZSStatus)(SZS_Theorem)
        } else {
          result.insert(SZSStatus)(SZS_ContradictoryAxioms)
        }
      }
        if (!Clause.trivial(newCl.cl)) {
        result.insert(UnprocessedClause)(newCl)
      }
    }

    println(s"To Unify:\n  >>${result.inserts(OpenUnification).map(_.pretty(sig)).mkString("\n   >>")}")

    result
  }

  override def taskFinished(t: Task): Unit = {
    val sb = new StringBuilder
    sb.append(s" ------------- End Round ${actRound}-------------------\n")
    actRound += 1
    sb.append(s"Unprocessed:\n  ${state.state.unprocessed.filter{cl => !movedToProcessed.contains(cl.id)}.map(cl =>
      CompressProof.compressAnnotation(cl)(CompressProof.lastImportantStep(importantInferences)).pretty(sig)).mkString("\n  ")}\n")
    sb.append(s"Open Unifications:\n  ${unification.getOpenUni.map(cl =>
      CompressProof.compressAnnotation(cl)(CompressProof.lastImportantStep(importantInferences)).pretty(sig)).mkString("\n  ")}\n")
    sb.append(s"Processed:\n  ${state.state.processed.map(cl =>
      CompressProof.compressAnnotation(cl)(CompressProof.lastImportantStep(importantInferences)).pretty(sig)).mkString("\n  ")}\n")
//    leo.Out.info(sb.toString())
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
case class StateView[T <: ClauseProxy](select : T, processedSelect : T, paramodPartners : Set[T] = Set(), subsumed : Set[T] = Set(), closed : Option[(StatusSZS, Option[T])] = None, forwardSubsumption : Boolean = false, actRewrite : Set[T] = Set[T](), actChoice : Map[Type, Set[Term]] = Map[Type, Set[Term]](), wasNormalized : Set[T] = Set[T]()) extends OperationState {

  override val toString: String = s"processed = ${select.pretty}"

  override def datatypes: Iterable[DataType[Any]] = Seq(ProcessedClause, UnprocessedClause)
  override def readData[T](ty: DataType[T]): Set[T] = if(ty == UnprocessedClause) (/*paramodPartners + */Set(select)).asInstanceOf[Set[T]] else Set()
  override def writeData[T](ty: DataType[T]): Set[T] = if(ty == ProcessedClause) subsumed.asInstanceOf[Set[T]] else Set() // TODO type check?
}


