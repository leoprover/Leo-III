package leo.agents.impl

import leo.Configuration
import leo.datastructures._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard.{Store, FormulaStore, FormulaEvent, Blackboard}
import leo.datastructures.impl.Signature
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
    UtilAgents.Conjecture()

    val leo = (new LeoAgent("/home/ryu/prover/leo2/bin/leo"))
    leo.register()


    //Problem and split
    val c = Context()
    c.split(AlphaSplit, 2)
    val child = c.childContext.toList
    val l = child(0)
    val r = child(1)

    Utility.add("fof(a,axiom,p&q).")
    val lF = Blackboard().addFormula(mkFormulaStoreFromTerm("b", mkAtom("p"), Role_Conjecture, l))
    val rF = Blackboard().addFormula(mkFormulaStoreFromTerm("c", mkAtom("q"), Role_Conjecture, r))


    Scheduler().signal()

    Thread.sleep(500)


    Out.trace("Blackboard contains:\n"+Blackboard().getFormulas.mkString("\n"))
    Blackboard().send(RemoteInvoke(lF),leo)
    Blackboard().send(RemoteInvoke(rF), leo)

    Thread.sleep(500)
    Scheduler().killAll()
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