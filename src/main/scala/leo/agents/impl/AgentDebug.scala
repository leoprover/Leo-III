package leo.agents.impl

import leo.Configuration
import leo.agents.{EmptyResult, Result, Task}
import leo.datastructures.blackboard.{FormulaStore, Blackboard}
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
    Configuration.init(new CLParameterParser(Array("arg0", "-v", "3")))

    load("tptp/ex2.p")
//    val script = new LeoAgent("scripts/leoexec.sh")
    val script = new LeoAgent("/home/ryu/prover/leo2/bin/leo")
    val task = new ScriptTask(Set(Blackboard().getFormulaByName("test").get))
    script.run(task)
  }
}

/**
 * Testing LeoAgent
 * @param path
 */
class LeoAgent(path : String) extends ScriptAgent(path) {
  override def handle(input: Stream[String], err: Stream[String], exit: Int): Result = {
    input foreach {l => Out.output(l)}
    Out.output(s"The exit code is $exit")
    EmptyResult
  }

  override protected def toFilter(event: FormulaStore): Iterable[Task] = ???
}
