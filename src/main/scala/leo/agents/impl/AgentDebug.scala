package leo.agents.impl

import leo.datastructures.blackboard.Blackboard

/**
 * Debugging and Live Testing of Agents
 *
 * @author Max Wisniewski
 * @since 11/12/14
 */
object AgentDebug {
  import leo.Main._
  def main(args : Array [String]) {

    load("tptp/ex2.p")
//    val script = ScriptAgent("scripts/leoexec.sh").get
    val script = ScriptAgent("scripts/leoexec.sh").get
    val task = new ScriptTask(Set(Blackboard().getFormulaByName("test").get))
    script.run(task)
  }
}
