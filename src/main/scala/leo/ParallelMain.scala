package leo

import java.nio.file.Files

import leo.datastructures.blackboard.{Blackboard, DoneEvent, SignatureBlackboard}
import leo.datastructures.blackboard.impl.SZSDataStore
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
import leo.modules._
import leo.modules.external.ExternalCall
import leo.modules.output._
import leo.modules.phase._
import leo.modules.interleavingproc._
import leo.agents.InterferingLoopAgent
import leo.modules.control.Control
import leo.datastructures.AnnotatedClause
import leo.modules.agent.multisearch.SchedulingPhase
import leo.modules.agent.rules.control_rules._
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
      case e: IllegalArgumentException => {
        Configuration.help()
        return
      }
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
    * @param startTime
    */
  def runMultiSearch(startTime : Long) {
    import leo.datastructures.Signature

    implicit val sig: Signature = Signature.freshWithHOL()
    val timeout = if (Configuration.TIMEOUT == 0) Double.PositiveInfinity else Configuration.TIMEOUT

    // Blackboard and Scheduler
    val (blackboard, scheduler) = Blackboard.newBlackboard

    val TimeOutProcess = new DeferredKill(timeout, timeout, blackboard, scheduler)
    TimeOutProcess.start()

    val initState : State[AnnotatedClause] = State.fresh(sig)

    val tactics : Iterator[RunStrategy] = Control.generateRunStrategies.map(strat =>
      new RunStrategy(timeout.toInt, strat.primSubst, strat.sos, strat.unifierCount, strat.uniDepth, strat.boolExt, strat.choice))

    val schedPhase = new SchedulingPhase(tactics, initState)(scheduler, blackboard)

    printPhase(schedPhase)
    if (!schedPhase.execute()) {
      scheduler.killAll()
      TimeOutProcess.kill()
      unexpectedEnd(System.currentTimeMillis() - startTime)
      return
    }


    TimeOutProcess.kill()
    val endTime = System.currentTimeMillis()
    val time = System.currentTimeMillis() - startTime
    scheduler.killAll()

    //      val szsStatus: StatusSZS = SZSDataStore.getStatus(Context()).fold(SZS_Unknown: StatusSZS) { x => x }
    val szsStatus  = initState.szsStatus
    Out.output("")
    Out.output(SZSOutput(szsStatus, Configuration.PROBLEMFILE, s"${time} ms"))

    //      val proof = FormulaDataStore.getAll(_.cl.lits.isEmpty).headOption // Empty clause suchen

    Out.comment(s"No. of loop iterations: ${initState.noProofLoops}")
    Out.comment(s"No. of processed clauses: ${initState.noProcessedCl}")
    Out.comment(s"No. of generated clauses: ${initState.noGeneratedCl}")
    Out.comment(s"No. of forward subsumed clauses: ${initState.noForwardSubsumedCl}")
    Out.comment(s"No. of backward subsumed clauses: ${initState.noBackwardSubsumedCl}")
    Out.comment(s"No. of subsumed descendants deleted: ${initState.noDescendantsDeleted}")
    Out.comment(s"No. of rewrite rules in store: ${initState.rewriteRules.size}")
    Out.comment(s"No. of other units in store: ${initState.nonRewriteUnits.size}")
    Out.comment(s"No. of choice functions detected: ${initState.choiceFunctionCount}")
    Out.comment(s"No. of choice instantiations: ${initState.choiceInstantiations}")
    val proof = initState.derivationClause
    if (szsStatus == SZS_Theorem && Configuration.PROOF_OBJECT && proof.isDefined) {
      Out.comment(s"SZS output start CNFRefutation for ${Configuration.PROBLEMFILE}")
      //      Out.output(makeDerivation(derivationClause).drop(1).toString)
      Out.output(userConstantsForProof(sig))
      Out.output(proofToTPTP(compressedProofOf(CompressProof.stdImportantInferences)(proof.get)))
      Out.comment(s"SZS output end CNFRefutation for ${Configuration.PROBLEMFILE}")
    }

  }

  /**
    *
    * Runs a single main loop,
    * but paralellizes on Subsidiary tasks
    *
    * @param startTime
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

    // Datastrucutres
    val state = BlackboardState.fresh(sig)
    val uniStore = new UnificationStore[InterleavingLoop.A]()
    val iLoop : InterleavingLoop = new InterleavingLoop(state, uniStore, sig)
    val iLoopAgent = new InterferingLoopAgent[StateView[InterleavingLoop.A]](iLoop, blackboard)
    val uniAgent = new DelayedUnificationAgent(uniStore, state, sig)
    val extAgent = new ExternalAgent(state, sig)

    val iPhase = new InterleavableLoopPhase(iLoopAgent, state, sig, uniAgent, extAgent)(blackboard, scheduler)

    state.state.setRunStrategy(Control.defaultStrategy(Configuration.TIMEOUT))

    blackboard.addDS(state)
    blackboard.addDS(uniStore)
    printPhase(iPhase)
    if (!iPhase.execute()) {
      scheduler.killAll()
      TimeOutProcess.kill()
      unexpectedEnd(System.currentTimeMillis() - startTime)
      return
    }

    TimeOutProcess.kill()
    val endTime = System.currentTimeMillis()
    val time = System.currentTimeMillis() - startTime
    scheduler.killAll()

//      val szsStatus: StatusSZS = SZSDataStore.getStatus(Context()).fold(SZS_Unknown: StatusSZS) { x => x }
    val szsStatus  = state.state.szsStatus
    Out.output("")
    Out.output(SZSOutput(szsStatus, Configuration.PROBLEMFILE, s"${time} ms"))

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
    implicit val state : FVState[AnnotatedClause] = FVState.fresh(sig, Control.defaultStrategy(timeout.toInt))

    // Blackboard and Scheduler
    implicit val (blackboard, scheduler) = Blackboard.newBlackboard

    val TimeOutProcess = new DeferredKill(timeout, timeout, blackboard, scheduler)
    TimeOutProcess.start()

    // RuleGraph creation
    val graph : SimpleControlGraph = new SimpleControlGraph

    val phase : RuleAgentPhase = new RuleAgentPhase(graph)


    printPhase(phase)
    if (!phase.execute()) {
      scheduler.killAll()
      TimeOutProcess.kill()
      unexpectedEnd(System.currentTimeMillis() - startTime)
      return
    }

    TimeOutProcess.kill()
    val endTime = System.currentTimeMillis()
    val time = System.currentTimeMillis() - startTime
    scheduler.killAll()


    val proof : Option[AnnotatedClause] = graph.fetchResult.headOption
    val szsStatus  = if (proof.nonEmpty){
      if(phase.negSet) SZS_Theorem
      else SZS_CounterSatisfiable
    } else if(time > timeout){
      SZS_Timeout
    } else {
      SZS_Unknown
    }

    leo.Out.debug(s"Ended after ${graph.select.actRound} rounds")
    leo.Out.debug(s"\nProcessed :\n  ${graph.activeSet.get.map(_.pretty(sig)).mkString("\n  ")}")
    leo.Out.debug(s"\nUnprocessed :\n  ${graph.passiveSet.unprocessed.map(_.pretty(sig)).mkString("\n  ")}")

    leo.Out.debug(s"\nNormalize :\n ${graph.normalizeSet.get(graph.Normalize).map(_.pretty(sig)).mkString("\n  ")}")
//    leo.Out.debug(s"\nNormalize Locks :\n ${graph.normalizeBarrier.get(graph.normalizeBarrier.lockType).mkString("\n  ")}")

    leo.Out.debug(s"\nGenerate :\n ${graph.generateSet.get(graph.Normalize).map(_.pretty(sig)).mkString("\n  ")}")
//    leo.Out.debug(s"\nGenerate Locks :\n ${graph.generateBarrier.get(graph.generateBarrier.lockType).mkString("\n  ")}")

    leo.Out.debug(s"\nUnify :\n ${graph.unifySet.get(graph.Normalize).map(_.pretty(sig)).mkString("\n  ")}")

    Out.output("")
    Out.output(SZSOutput(szsStatus, Configuration.PROBLEMFILE, s"${time} ms"))

    //      val proof = FormulaDataStore.getAll(_.cl.lits.isEmpty).headOption // Empty clause suchen
    if (szsStatus == SZS_Theorem && Configuration.PROOF_OBJECT && proof.isDefined) {
      Out.comment(s"SZS output start CNFRefutation for ${Configuration.PROBLEMFILE}")
      //      Out.output(makeDerivation(derivationClause).drop(1).toString)
      Out.output(userConstantsForProof(sig))
      Out.output(proofToTPTP(compressedProofOf(CompressProof.stdImportantInferences)(proof.get)))
      Out.comment(s"SZS output end CNFRefutation for ${Configuration.PROBLEMFILE}")
    }
  }

  private def unexpectedEnd(time : Long) {
    val szsStatus : StatusSZS = SZSDataStore.getStatus(Context()).fold(SZS_Timeout : StatusSZS){x => x}
    Out.output("")
    Out.output(SZSOutput(szsStatus, Configuration.PROBLEMFILE, s"${time} ms"))
  }

  private def printPhase(p : Phase) = {
    Out.debug(" ########################")
    Out.debug(s" Starting Phase ${p.name}")
    Out.debug(p.description)
  }

  private def testExternalProvers(): Unit ={
    Configuration.ATPS foreach { case (name, cmd) =>
      val r = ExternalCall.exec(cmd+" "+Configuration.PROBLEMFILE)
        println(s"Output ($name) ${r.out.mkString("\n")}\n\n Error ($name)\n ${r.error.mkString("\n")}")
    }
  }

  /**
    * Thread to kill leo.Scheduler()
    *
    * @param interval
    * @param timeout
    */
  private class DeferredKill(interval : Double, timeout : Double, blackboard: Blackboard, scheduler: Scheduler) extends Thread {

    var remain : Double = timeout
    var exit : Boolean = false

    private var finished = false

    def isFinished = synchronized(finished)

    def kill() : Unit = {
      synchronized{
        exit = true
        this.interrupt()
        Out.finest("Scheduler killed before timeout.")
        blackboard.filterAll(_.filter(DoneEvent))
        scheduler.killAll()
      }
    }

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
        SZSDataStore.setIfEmpty(SZS_Timeout)
        Out.finest(s"Timeout: Killing all Processes.")
        finished = true
        //TODO: Better mechanism
        blackboard.filterAll(_.filter(DoneEvent))
        scheduler.killAll()
      }
    }
  }
}
