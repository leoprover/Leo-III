package leo.modules.phase
import java.io.File
import java.nio.file.Files

import leo.agents.impl.SZSScriptAgent
import leo.agents.{DoItYourSelfAgent, DoItYourSelfMessage, ProofProcedure, Agent}
import leo.datastructures._
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.context.Context
import leo.Configuration
import leo.datastructures.blackboard.scheduler.Scheduler

import scala.io.Source


/**
  * Created by mwisnie on 5/24/16.
  */
class MultiSearchPhase(proofProcedure: ProofProcedure*) extends CompletePhase {
  /**
    * Returns the name of the phase.
    *
    * @return
    */
  override def name: String = "multi-search"

  /**
    * A list of all agents to be started.
    *
    * @return
    */
  override protected val agents: Seq[Agent] = {
    val ext = Configuration.ATPS.map{case (name, prover) => SZSScriptAgent(name, prover)}
    proofProcedure.map(proc => new DoItYourSelfAgent(proc)) ++: ext
  }

  override protected def init() = {
    import leo.datastructures.{AnnotatedClause, Clause, Literal}
    super.init()
    agents foreach {a =>
      Blackboard().send(DoItYourSelfMessage(Context()), a)
    }
    Scheduler().submitIndependentFree(DelayedRun)
  }

  private object DelayedRun extends Runnable {
    override def run(): Unit = {
      try{
//        println("Before sleep ()")
        Thread.sleep(Configuration.TIMEOUT * 600)
//        println("After sleep")
        val fs = FormulaDataStore.getFormulas.toSet
        SZSScriptAgent.allScriptAgents.foreach(a => a.kill())
        SZSScriptAgent.execute(fs)
      } catch {
        case e : ThreadDeath => return
        case e : InterruptedException => return
        case _ : Throwable => leo.Out.trace("Unexpected Exception in delayed External Run.")
      }
    }
  }
}