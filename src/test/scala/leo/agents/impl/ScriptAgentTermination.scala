package leo.agents.impl

import java.io.{File, PrintWriter}

import scala.io.Source
import scala.sys.process._
import leo.{Ignored, LeoTestSuite}


/**
 * Created by mwisnie on 10/27/15.
 */
class ScriptAgentTermination extends LeoTestSuite {

  test("ScriptAgentTermination", Ignored) {
    val file = File.createTempFile("test_process",".sh")
    file.deleteOnExit()
    val writer = new PrintWriter(file)

    try{
      writer.append("#!/bin/bash\n\n")
      writer.append("while [ : ] \ndo\n\techo \"Test\"\n\techo \"Error\" 1>&2\n\tsleep 0.0001\ndone")
    } finally writer.close()
    file.setExecutable(true)

    var goodLines = 0
    var badLines = 0
    val res = Seq(file.getAbsolutePath)
    val process = res.run(new ProcessIO(in => in.close(),
      stdout => {
        val reader = Source.fromInputStream(stdout).getLines().foreach{l => goodLines += 1}
      },
      stderr => {
        val reader = Source.fromInputStream(stderr).getLines().foreach{l => badLines += 1}
      }
    ))

    Thread.sleep(10000)
    process.destroy()
    println(s"Read\n\t$goodLines lines of input stream\n\t$badLines lines of error stream\n")
  }

}
