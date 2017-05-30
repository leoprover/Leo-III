package leo.modules.prover

import leo.datastructures.{AnnotatedClause, Signature}
import leo.modules.control.Control
import leo.{Configuration, Out}
import leo.modules.parsers.Input


object ScheduledRun {
  final def apply(startTime: Long, timeout: Int, schedule: Iterator[RunStrategy]): Unit = {
    implicit val sig: Signature = Signature.freshWithHOL()
    val state: State[AnnotatedClause] = State.fresh(sig)
    try {
      // Check if external provers were defined
      if (Configuration.ATPS.nonEmpty) {
        import leo.modules.external.ExternalProver
        Configuration.ATPS.foreach { case(name, path) =>
          try {
            val p = ExternalProver.createProver(name,path)
            state.addExternalProver(p)
            leo.Out.info(s"$name registered as external prover.")
            leo.Out.info(s"$name timeout set to:${Configuration.ATP_TIMEOUT(name)}.")
          } catch {
            case e: NoSuchElementException => leo.Out.warn(e.getMessage)
          }
        }
      }
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
      var done = false
      while (schedule.hasNext && !done) {
        val currentStrategy = schedule.next()
        Out.info(s"Trying strategy ${currentStrategy.pretty} for ${currentStrategy.timeout}s ...")
        val localState = state.copy
        localState.setRunStrategy(currentStrategy)
        val localStartTime = System.currentTimeMillis()
        done = SeqLoop.run(localState, remainingInput, localStartTime)
        if (!done) Out.info(s"Strategy ${currentStrategy.pretty} failed.")
        if (!done && schedule.hasNext) Control.resetIndexes(localState)
        if (done || !schedule.hasNext) SeqLoop.printResult(localState, startTime, startTimeWOParsing)
      }
    } catch {
      case e:Throwable => Out.severe(s"Signature used:\n${leo.modules.signatureAsString(sig)}"); throw e
    } finally {
      if (state.externalProvers.nonEmpty)
        Control.killExternals()
    }
  }

  final def apply(startTime: Long, timeout: Int): Unit = {
    val schedule = Control.generateRunStrategies
    apply(startTime, timeout, schedule)
  }

  final def apply(startTime: Long, timeout: Int, strategy: RunStrategy): Unit = {
    apply(startTime, timeout, Iterator(strategy))
  }


}
