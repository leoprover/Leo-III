package leo.agents.impl

import leo.Configuration
import leo.datastructures._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard.{Store, FormulaStore, FormulaEvent, Blackboard}
import leo.datastructures.impl.Signature
import leo.modules.proofCalculi.{IdComparison, Paramodulation, PropParamodulation}
import leo.modules.{Utility, CLParameterParser}
import leo.modules.output.logger.Out
import leo.datastructures.context.{AlphaSplit, Context}
import leo.datastructures.term.Term

/**
 * Debugging and Live Testing of Agents
 *
 *
 * Testing Remote Theorem Prover different contexts.
 *
 * @author Max Wisniewski
 * @since 11/12/14
 */
object AgentDebug {
  import leo.Main._
  def main(args : Array [String]) {
    Configuration.init(new CLParameterParser(Array("arg0", "-v", "4")))
    Scheduler()
    Blackboard()

    Utility.load("tptp/ex1.p")


    // Init - Preprocess

    UtilAgents.Conjecture()

    Out.output("Loaded File")
    Utility.formulaContext()

    Scheduler().signal()

    Thread.sleep(500)

    UtilAgents.Conjecture().setActive(false)
    Out.output("After Conjecture")
    Utility.formulaContext

    // Run

    val p1 = new ParamodulationAgent(Paramodulation, IdComparison)
    val p2 = new ParamodulationAgent(PropParamodulation, IdComparison)
    p1.register()
    p2.register()
    ClausificationAgent()
    NormalClauseAgent.DefExpansionAgent()
    //NormalClauseAgent.NegationNormalAgent()
    NormalClauseAgent.SimplificationAgent()

    Utility.agentStatus()

    Thread.sleep(5000)
    Scheduler().killAll()

    Out.output("After 5s of calculus.")
    Utility.formulaContext
  }

  def mkFormulaStoreFromTerm(name : String, t : Term, r : Role, context : Context) : FormulaStore = {
    val c = Clause.mkClause(List(Literal(t, true)), FromConjecture)
    return Store(name, c, r, context)
  }

  def mkAtom(st : String) : Term = {
    val s = Signature.get
    Term.mkAtom(s(st).key)
  }
}