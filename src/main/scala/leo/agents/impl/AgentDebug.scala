package leo.agents.impl

import leo.Configuration
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard.{Blackboard}
import leo.modules.CLParameterParser
import leo.modules.output.logger.Out

/**
 * Debugging and Live Testing of Agents
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

    (new LeoAgent("/home/ryu/prover/leo2/bin/leo")).register()
    load("tptp/ex1.p")
    Scheduler().signal()

    Thread.sleep(500)


    Blackboard().getFormulas foreach {f => Out.output(f.toString)}
    val f = Blackboard().getFormulaByName("test").get
    Blackboard().rmFormulaByName("test")
    val nf = f.newStatus(32 & f.status)
    Blackboard().addFormula(nf)
    Blackboard().filterAll(a => a.filter(nf))

  }
}