package leo

package modules.parsers

/**
  * Created by samuel on 03.04.16.
  */

import leo.datastructures.tptp.Commons.TPTPInput
import leo.modules.parsers.syntactical_new.{TPTPParser2, ThfParser}
import leo.modules.parsers.syntactical.{TPTPParsers => OldParser}
import leo.{Checked, LeoTestSuite}

import scala.io.Source

/**
  * Created by samuel on 08.03.16.
  */
class ParseThf2Test
  extends LeoTestSuite
{
  val source = "/problems"
  val problem_suffix = ".p"

  val problems = Seq(
    // "SYN000-1" -> "TPTP CNF basic syntax features",
    //"SYN000+1" -> "TPTP FOF basic syntax features",
    //"SYN000_1" -> "TPTP TF0 basic syntax features",
    "SYN000^1" -> "TPTP THF basic syntax features",
    "SYN000^2" -> "TPTP THF advanced syntax features"
    //"SYN000+2" -> "TPTP FOF advanced syntax features",
    //"SYN000_2" -> "TPTP TF0 advanced syntax features",
    //"SYN000=2" -> "TPTP TFA with arithmetic advanced syntax features"
  )

  test("compareParserOutput", Checked) {
    Out.output("comparing the output of both parsers...")
    for (p <- problems) {
      Out.output(s"testing with problem file ${p}")
      import scala.util.parsing.input.CharArrayReader
      def parseWithOld: TPTPInput = {
        val stream = Source.fromFile(getClass.getResource(source).getPath() + "/" + p._1 + ".p")
        val res = OldParser.parse(new CharArrayReader(stream.toArray), OldParser.tptpFile).get
        stream.close()
        res
      }
      def parseWithNew: TPTPInput = {
        val stream = getClass.getResourceAsStream(source + "/" + p._1 + ".p")
        val res = TPTPParser2.parseSource(Source.fromInputStream(stream)).right.get._1
        stream.close()
        res
      }
      var oldRes = parseWithOld.inputs
      var newRes = parseWithNew.inputs

      var countSuccess = 0
      while(
        (!oldRes.isEmpty)
        ||
        (!newRes.isEmpty)
      ) {
        //assert( oldRes.head == newRes.head )
        if( oldRes.head == newRes.head ) {
          Out.output(s"success: ${oldRes.head}")
          countSuccess += 1
        }
        else {
          Out.output(s"failed: \n\t${oldRes.head}\n\t${newRes.head}")
        }

        oldRes = oldRes.tail
        newRes = newRes.tail
      }
      if( countSuccess < parseWithNew.inputs.length )
        fail(s"${parseWithNew.inputs.length - countSuccess} tests failed!")
      //println(s"res: ${res}")
    }
  }

}
