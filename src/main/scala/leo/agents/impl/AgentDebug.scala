package leo.agents.impl

import leo.Configuration
import leo.agents.{EmptyResult, Result, Task, FifoAgent}
import leo.datastructures._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard._
import leo.datastructures.impl.Signature
import leo.modules.output.SZS_Theorem
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

  var finish = false

  def main(args : Array [String]) {
    Configuration.init(new CLParameterParser(Array("arg0", "-v", "5")))
    Scheduler()
    Blackboard()

    val file = "tptp/ex1.p"

    Utility.load(file)


    // Init - Preprocess

    UtilAgents.Conjecture()

    Out.output("Loaded File")
    Utility.formulaContext()

    Scheduler().signal()

    Thread.sleep(500)

    UtilAgents.Conjecture().setActive(false)
    Out.output("After Conjecture")
    Utility.formulaContext

    val scriptAgent = new SZSScriptAgent("scripts/leoexec.sh")
    scriptAgent.register()

    val f = Blackboard().getAll(_.role == Role_NegConjecture).head

    Blackboard().send(SZSScriptMessage(f),scriptAgent)

    Thread.sleep(2000)

    Out.output("\n\nOutput:\n\n")
    Out.output(s"%SZS Status ${Blackboard().getStatus(Context()).fold("Unkown")(_.output)} for $file")
    Blackboard().getAll{p => p.clause.isEmpty}.foreach(Utility.printDerivation(_))
  }

  def mkFormulaStoreFromTerm(name : String, t : Term, r : Role, context : Context) : FormulaStore = {
    val c = Clause.mkClause(List(Literal(t, true)), FromConjecture)
    return Store(name, c, r, context)
  }

  def mkAtom(st : String) : Term = {
    val s = Signature.get
    Term.mkAtom(s(st).key)
  }

  private object WaitForProof extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case StatusEvent(c,s) =>
        if (c.parentContext == null && s == SZS_Theorem) {
          finish = true
          AgentDebug.synchronized(AgentDebug.notify())
          List()
        } else List()
      case _ => List()
    }
    override def name: String = "DebugControlAgent"
    override def run(t: Task): Result = EmptyResult
  }
}