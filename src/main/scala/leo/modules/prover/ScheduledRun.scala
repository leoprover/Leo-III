package leo.modules.prover

import leo.datastructures.{AnnotatedClause, Signature}
import leo.modules.control.Control
import leo.{Configuration, Out}
import leo.modules.parsers.Input


object ScheduledRun {
  final def apply(startTime: Long, timeout: Int, schedule0: Control.RunSchedule = null): Unit = {
    implicit val sig: Signature = Signature.freshWithHOL()
    val state: State[AnnotatedClause] = State.fresh(sig)
    var curState: State[AnnotatedClause] = null
    try {
      if (Configuration.ATPS.nonEmpty) Control.registerExtProver(Configuration.ATPS)(state)

      // Read problem from file
      val input = Input.parseProblem(Configuration.PROBLEMFILE)
      val startTimeWOParsing = System.currentTimeMillis()
      // Split input in conjecture/definitions/axioms etc.
      val remainingInput: Seq[AnnotatedClause] = effectiveInput(input, state)
      // Typechecking: Throws and exception if not well-typed
      typeCheck(remainingInput, state)
      Out.info(s"Type checking passed. Searching for refutation ...")

      // problem is parsed and splitted:
      // state contains the conjecture (if existent)
      // remainingInput contains all the remaining clauses
      // definitions, types, etc. have been processed already
      // i.e. sig (== state.sig) contains all problem symbols,
      // typecheck done.

      // Now: receive schedule of strategies
      // (containing times per strategy, strategy contains parameters etc),
      // and invoke SeqLoop wrt to each strategy consecutively.
      // The schedule is calculated so that the sum of
      // all timeouts is <= Configuration.TIMEOUT
      val schedule1 = if (schedule0 != null) schedule0 else {
        import leo.modules.control.schedulingControl.StrategyControl.calculateExtraTime
        val extraTime = calculateExtraTime(remainingInput.size)
        leo.Out.debug(s"extraTime: $extraTime")
        Control.generateRunStrategies(timeout, extraTime)
      }
      leo.Out.config(s"Using strategy schedule: ${schedule1.map(_._1.name).mkString(",")}")

      var done = false
      val schedule = schedule1.iterator
      while (schedule.hasNext && !done) {
        val (currentStrategy, currentTimeout) = schedule.next()
        Out.info(s"Trying (${currentTimeout}s): ${currentStrategy.pretty} ...")
        val localState = state.copy
        curState = localState
        localState.setRunStrategy(currentStrategy)
        localState.setTimeout(currentTimeout)
        val localStartTime = System.currentTimeMillis()
        done = SeqLoop.run(localState, remainingInput, localStartTime)
        if (!done) Out.info(s"Failed: ${currentStrategy.pretty}")
        if (!done && schedule.hasNext) Control.resetIndexes(localState)
        if (done || !schedule.hasNext) SeqLoop.printResult(localState, startTime, startTimeWOParsing)
      }
    } catch {
      case e:Throwable => Out.debug(s"Signature used:\n${leo.modules.signatureAsString(curState.signature)}"); throw e
    } finally {
      if (state.externalProvers.nonEmpty)
        Control.killExternals()
    }
  }

  final def apply(startTime: Long, timeout: Int, strategy: RunStrategy): Unit = {
    apply(startTime, timeout, Iterable((strategy, timeout)))
  }


}
