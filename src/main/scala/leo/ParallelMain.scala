package leo

import java.nio.file.Files

import leo.datastructures.blackboard.{Blackboard, DoneEvent}
import leo.datastructures.blackboard.impl.SZSDataStore
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
import leo.modules._
import leo.modules.external.SchedulerTranslationImpl
import leo.modules.output._
import leo.modules.phase._
import leo.modules.interleavingproc._
import leo.agents.InterferingLoopAgent
import leo.modules.control.{Control, schedulingControl}
import leo.datastructures.AnnotatedClause
import leo.modules.agent.multisearch.{Schedule, SchedulingPhase}
import leo.modules.agent.rules.control_rules._
import leo.modules.control.externalProverControl.ExtProverControl
import leo.modules.control.schedulingControl.ParStrategyControl
import leo.modules.parsers.CLParameterParser
import leo.modules.proof_object.CompressProof
import leo.modules.prover.{RunStrategy, State}


/**
  * Created by mwisnie on 3/7/16.
  */
object ParallelMain {
  private var hook: scala.sys.ShutdownHookThread = _

  def main(args : Array[String]): Unit = {
    try {
      Configuration.init(new CLParameterParser(args))
    } catch {
      case e: IllegalArgumentException =>
        Configuration.help()
        return
    }
    if ((args(0) == "-h") || Configuration.HELP) {
      Configuration.help()
      return
    }
    val leodir = Configuration.LEODIR
    if (!Files.exists(leodir)) Files.createDirectory(leodir)

    val config = {
      val sb = new StringBuilder()
      sb.append(s"problem(${Configuration.PROBLEMFILE}),")
      sb.append(s"time(${Configuration.TIMEOUT}),")
      sb.append(s"proofObject(${Configuration.PROOF_OBJECT}),")
      sb.append(s"sos(${Configuration.SOS}),")
      // TBA ...
      sb.init.toString()
    }
    Out.config(s"Configuration: $config")

    hook = sys.addShutdownHook({
      Out.output(SZSOutput(SZS_Forced, Configuration.PROBLEMFILE, "Leo-III stopped externally."))
    })

    val startTime: Long = System.currentTimeMillis()
    runParallel(startTime)
  }


  /**
    * Employs agents only to perform a multisearch with
    * a defined set of RunStrategies
    *
    * @param startTime Time the program started
    */
  def runMultiSearch(startTime : Long) {
    import leo.datastructures.Signature

    implicit val sig: Signature = Signature.freshWithHOL()
    val timeout = if (Configuration.TIMEOUT == 0) Double.PositiveInfinity else Configuration.TIMEOUT

    // Blackboard and Scheduler
    val (blackboard, scheduler) = Blackboard.newBlackboard

    val TimeOutProcess = new DeferredKill(timeout, timeout, blackboard, scheduler)
    TimeOutProcess.start()

    try {
      val initState: State[AnnotatedClause] = State.fresh(sig)

      val defaultStrat = schedulingControl.StrategyControl.defaultStrategy
      val tactics : Schedule = ParStrategyControl.generateRunStrategies()

//      println(tactics.size)

//  TODO    ExtProverControl.registerAsyncTranslation(new SchedulerTranslationImpl(scheduler))

      val schedPhase = new SchedulingPhase(tactics, initState)(scheduler, blackboard)

      printPhase(schedPhase)
      if (!schedPhase.execute()) {
        scheduler.killAll()
        TimeOutProcess.killOnly()
        unexpectedEnd(System.currentTimeMillis() - startTime)
        return
      }


      TimeOutProcess.killOnly()
      val endTime = System.currentTimeMillis()
      val time = System.currentTimeMillis() - startTime
      scheduler.killAll()

      val resultState = schedPhase.resultState
      if(TimeOutProcess.timedOut && resultState.szsStatus != SZS_Theorem) resultState.setSZSStatus(SZS_Timeout)

      //      val proof = FormulaDataStore.getAll(_.cl.lits.isEmpty).headOption // Empty clause suchen
      resultState match {
        case s : State[AnnotatedClause] => printResult(s, time)
        case _ => printResult(resultState, time)
      }
    } finally {
      if(!TimeOutProcess.isFinished)
        TimeOutProcess.killOnly()
    }
  }

  /**
    *
    * Runs a single main loop,
    * but paralellizes on Subsidiary tasks
    *
    * @param startTime Time the program started
    */
  def runParallel(startTime : Long){

    import leo.datastructures.Signature

    implicit val sig: Signature = Signature.freshWithHOL()
    //    SignatureBlackboard.set(sig)
    val timeout = if (Configuration.TIMEOUT == 0) Double.PositiveInfinity else Configuration.TIMEOUT

    // Blackboard and Scheduler
    val (blackboard, scheduler) = Blackboard.newBlackboard

    val TimeOutProcess = new DeferredKill(timeout, timeout, blackboard, scheduler)
    TimeOutProcess.start()

    try {

      // Datastrucutres
      val state = BlackboardState.fresh(sig)
      val uniStore = new UnificationStore[InterleavingLoop.A]()
      val iLoop: InterleavingLoop = new InterleavingLoop(state, uniStore, sig)
      val iLoopAgent = new InterferingLoopAgent[StateView[InterleavingLoop.A]](iLoop, blackboard)
      val uniAgent = new DelayedUnificationAgent(uniStore, state, sig)
      val extAgent = new ExternalAgent(state, sig)

      val iPhase = new InterleavableLoopPhase(iLoopAgent, state, sig, uniAgent, extAgent)(blackboard, scheduler)

      state.state.setRunStrategy(Control.defaultStrategy)
      state.state.setTimeout(Configuration.TIMEOUT)

      blackboard.addDS(state)
      blackboard.addDS(uniStore)
      printPhase(iPhase)
      if (!iPhase.execute()) {
        scheduler.killAll()
        TimeOutProcess.killOnly()
        unexpectedEnd(System.currentTimeMillis() - startTime)
        return
      }

      TimeOutProcess.killOnly()
      val endTime = System.currentTimeMillis()
      val time = System.currentTimeMillis() - startTime
      scheduler.killAll()

      //      val szsStatus: StatusSZS = SZSDataStore.getStatus(Context()).fold(SZS_Unknown: StatusSZS) { x => x }
      val szsStatus = if(TimeOutProcess.timedOut) SZS_Timeout else state.state.szsStatus
      Out.output("")
      Out.output(SZSOutput(szsStatus, Configuration.PROBLEMFILE, s"${time.toInt} ms"))

      //      val proof = FormulaDataStore.getAll(_.cl.lits.isEmpty).headOption // Empty clause suchen

      Out.comment(s"No. of loop iterations: ${state.state.noProofLoops}")
      Out.comment(s"No. of processed clauses: ${state.state.noProcessedCl}")
      Out.comment(s"No. of generated clauses: ${state.state.noGeneratedCl}")
      Out.comment(s"No. of forward subsumed clauses: ${state.state.noForwardSubsumedCl}")
      Out.comment(s"No. of backward subsumed clauses: ${state.state.noBackwardSubsumedCl}")
      Out.comment(s"No. of subsumed descendants deleted: ${state.state.noDescendantsDeleted}")
      Out.comment(s"No. of rewrite rules in store: ${state.state.rewriteRules.size}")
      Out.comment(s"No. of other units in store: ${state.state.nonRewriteUnits.size}")
      Out.comment(s"No. of choice functions detected: ${state.state.choiceFunctionCount}")
      Out.comment(s"No. of choice instantiations: ${state.state.choiceInstantiations}")
      val proof = state.state.derivationClause
      if (szsStatus == SZS_Theorem && Configuration.PROOF_OBJECT && proof.isDefined) {
        Out.comment(s"SZS output start CNFRefutation for ${Configuration.PROBLEMFILE}")
        //      Out.output(makeDerivation(derivationClause).drop(1).toString)
        Out.output(userConstantsForProof(sig))
        Out.output(proofToTPTP(compressedProofOf(CompressProof.stdImportantInferences)(proof.get)))
        Out.comment(s"SZS output end CNFRefutation for ${Configuration.PROBLEMFILE}")
      }
    } finally {
      if(!TimeOutProcess.isFinished)
        TimeOutProcess.killOnly()
    }
  }

  /**
    *
    * Implementation of Saturation Network
    *
    * @param startTime
    */
  def agentRuleRun(startTime : Long): Unit ={
    import leo.datastructures.Signature

    implicit val sig: Signature = Signature.freshWithHOL()
    val timeout = if (Configuration.TIMEOUT == 0) Double.PositiveInfinity else Configuration.TIMEOUT
    implicit val state : FVState[AnnotatedClause] = FVState.fresh(sig, Control.defaultStrategy)
    state.setTimeout(Configuration.TIMEOUT)

    // Blackboard and Scheduler
    implicit val (blackboard, scheduler) = Blackboard.newBlackboard

    val TimeOutProcess = new DeferredKill(timeout, timeout, blackboard, scheduler)
    TimeOutProcess.start()

    try {
      // RuleGraph creation
      val graph: SimpleControlGraph = new SimpleControlGraph

      val phase: RuleAgentPhase = new RuleAgentPhase(graph)


      printPhase(phase)
      if (!phase.execute()) {
        scheduler.killAll()
        TimeOutProcess.killOnly()
        unexpectedEnd(System.currentTimeMillis() - startTime)
        return
      }

      TimeOutProcess.killOnly()
      val endTime = System.currentTimeMillis()
      val time = System.currentTimeMillis() - startTime
      scheduler.killAll()


      val proof: Option[AnnotatedClause] = graph.fetchResult.headOption
      val szsStatus = if (proof.nonEmpty) {
        if (state.negConjecture != null) SZS_Theorem
        else SZS_CounterSatisfiable
      } else if (time > timeout) {
        SZS_Timeout
      } else {
        SZS_Unknown
      }
      state.setSZSStatus(szsStatus)
      proof.foreach(state.setDerivationClause)

      leo.Out.debug(s"Ended after ${graph.select.actRound} rounds")
      leo.Out.debug(s"\nProcessed :\n  ${graph.activeSet.get.map(_.pretty(sig)).mkString("\n  ")}")
      leo.Out.debug(s"\nUnprocessed :\n  ${graph.passiveSet.unprocessed.map(_.pretty(sig)).mkString("\n  ")}")

      leo.Out.debug(s"\nNormalize :\n ${graph.normalizeSet.get(graph.Normalize).map(_.pretty(sig)).mkString("\n  ")}")
      //    leo.Out.debug(s"\nNormalize Locks :\n ${graph.normalizeBarrier.get(graph.normalizeBarrier.lockType).mkString("\n  ")}")

      leo.Out.debug(s"\nGenerate :\n ${graph.generateSet.get(graph.Normalize).map(_.pretty(sig)).mkString("\n  ")}")
      //    leo.Out.debug(s"\nGenerate Locks :\n ${graph.generateBarrier.get(graph.generateBarrier.lockType).mkString("\n  ")}")

      leo.Out.debug(s"\nUnify :\n ${graph.unifySet.get(graph.Normalize).map(_.pretty(sig)).mkString("\n  ")}")

      printResult(state, time)
    } finally {
      if(!TimeOutProcess.isFinished)
        TimeOutProcess.killOnly()
    }
  }

  private def unexpectedEnd(time : Long) {
    val szsStatus : StatusSZS = SZSDataStore.getStatus(Context()).fold(SZS_Timeout : StatusSZS){x => x}
    Out.output("")
    Out.output(SZSOutput(szsStatus, Configuration.PROBLEMFILE, s"${time.toInt} ms"))
  }

  private def printPhase(p : Phase) = {
//    Out.debug(" ########################")
//    Out.debug(s" Starting Phase ${p.name}")
//    Out.debug(p.description)
  }

  private def printResult(state : GeneralState[AnnotatedClause], time : Double): Unit ={
    Out.comment(s"No. of loop iterations: 0")
    Out.comment(s"No. of processed clauses: 0")
    Out.comment(s"No. of generated clauses: 0")
    Out.comment(s"No. of forward subsumed clauses: 0")
    Out.comment(s"No. of backward subsumed clauses: 0")
    Out.comment(s"No. of subsumed descendants deleted: 0")
    Out.comment(s"No. of rewrite rules in store: 0")
    Out.comment(s"No. of other units in store: 0")
    Out.comment(s"No. of choice functions detected: 0")
    Out.comment(s"No. of choice instantiations: 0")

    printSZSAndProof(state, time)
  }


  private def printResult(state : State[AnnotatedClause], time : Double): Unit = {
    Out.comment(s"No. of loop iterations: ${state.noProofLoops}")
    Out.comment(s"No. of processed clauses: ${state.noProcessedCl}")
    Out.comment(s"No. of generated clauses: ${state.noGeneratedCl}")
    Out.comment(s"No. of forward subsumed clauses: ${state.noForwardSubsumedCl}")
    Out.comment(s"No. of backward subsumed clauses: ${state.noBackwardSubsumedCl}")
    Out.comment(s"No. of subsumed descendants deleted: ${state.noDescendantsDeleted}")
    Out.comment(s"No. of rewrite rules in store: ${state.rewriteRules.size}")
    Out.comment(s"No. of other units in store: ${state.nonRewriteUnits.size}")
    Out.comment(s"No. of choice functions detected: ${state.choiceFunctionCount}")
    Out.comment(s"No. of choice instantiations: ${state.choiceInstantiations}")

    printSZSAndProof(state, time)
  }


  private def printSZSAndProof(state : GeneralState[AnnotatedClause], time : Double): Unit = {
    import modules._
    implicit val sig = state.signature
    val szsStatus = state.szsStatus
    Out.output("")
    Out.output(SZSOutput(szsStatus, Configuration.PROBLEMFILE, s"${time.toInt} ms"))
    if (state.szsStatus == SZS_Theorem) Out.comment(s"Solved by ${state.runStrategy.pretty}")

    val proof = if (state.derivationClause.isDefined) proofOf(state.derivationClause.get) else null

    //      val proof = FormulaDataStore.getAll(_.cl.lits.isEmpty).headOption // Empty clause suchen
    if (szsStatus == SZS_Theorem && Configuration.PROOF_OBJECT && proof != null) {
      Out.comment(s"SZS output start CNFRefutation for ${Configuration.PROBLEMFILE}")
      Out.output(userSignatureToTPTP(symbolsInProof(proof))(sig))
      if (Configuration.isSet("compressProof")) Out.output(proofToTPTP(compressedProofOf(CompressProof.stdImportantInferences)(state.derivationClause.get)))
      else Out.output(proofToTPTP(proof))
      Out.comment(s"SZS output end CNFRefutation for ${Configuration.PROBLEMFILE}")
    }
  }


  private class DeferredKill(interval : Double, timeout : Double, blackboard: Blackboard, scheduler: Scheduler) extends Thread {

    private val THRESHHOLD_FOR_TIMEOUT = 5  // TODO Move?

    var remain : Double = timeout + THRESHHOLD_FOR_TIMEOUT
    var exit : Boolean = false

    private var finished = false
    private var timeOut = false

    def isFinished : Boolean = synchronized(finished)

    def timedOut : Boolean = synchronized(timeOut)

    def killOnly() : Unit = synchronized {
      exit = true
      finished = true
      this.interrupt()
    }

//    def kill() : Unit = {
//      synchronized{
//        exit = true
//        finished = true
//        this.interrupt()
//        Out.finest("Scheduler killed before timeout.")
//        blackboard.filterAll(_.filter(DoneEvent))
//        scheduler.killAll()
//      }
//    }

    override def run(): Unit = {
      //      println("Init delay kill.")
      synchronized{
        while(remain > 0 && !exit) {
          try {
            val w : Double = if (remain > interval) interval else remain
            wait((w * 1000).toInt)
          } catch {
            case e: InterruptedException => if(exit) return else Thread.interrupted()
            case _: Throwable => ()
          } finally {
            if(!exit) {
              scheduler.signal()
              remain -= interval
              Out.finest(s"Leo-III is still working. (Remain=$remain)")
            }
          }
        }
        timeOut = true
        Out.finest(s"Timeout: Killing all Processes.")
        finished = true
        //TODO: Better mechanism
        blackboard.filterAll(_.filter(DoneEvent))
        scheduler.killAll()
      }
    }
  }
}
