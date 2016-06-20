package leo.modules.parsers

import leo.datastructures.tptp.Commons._
import leo.modules.output.logger.Out
/*
import leo.modules.parsers.syntactical.{TPTPParsers => TermParser0}
import leo.modules.parsers.syntactical_new.termParser2.TermParser2
import leo.modules.parsers.syntactical_new.termParser_functional.{TermParser => TermParser1}
*/

import leo.modules.parsers.syntactical_new.{TPTPParser2, ThfParser}
import leo.modules.parsers.syntactical.{TPTPParsers => OldParser}

import leo.modules.parsers.utils.GenerateTerm
import leo.{Checked, LeoTestSuite}
import scala.io.Source

/**
  * Created by samuel on 17.03.16.
  */
class ParseTHFBenchmark
  extends LeoTestSuite {
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

  object Parser0Wrapper {
    def parse(str: String): Either[String, (Term, Seq[OldParser.lexical.Token])] = {
      OldParser.parse[Term](str, OldParser.term) match {
        case OldParser.Success(res, rest) => Right((res, Seq()))
        case _ => Left("parser0 failed!")
      }
    }
  }

  test("oldParser", Checked) {
    for (p <- problems) {
      Out.output(s"testing OLD parser with problem file ${p}")
      import scala.util.parsing.input.CharArrayReader
      val stream = getClass.getResourceAsStream(source + "/" + p._1 + ".p")
      val inputAsString = Source.fromInputStream(stream).mkString
      val charArrayReader = new CharArrayReader(inputAsString.toCharArray)

      val numRuns = 10;
      {
        var dt: Long = 0
        for (_ <- 0 to numRuns) {
          val t0 = System.nanoTime
          val res = OldParser.parse(charArrayReader, OldParser.tptpFile).get
          val t1 = System.nanoTime
          dt = (t1 - t0)
          res
        }
        Out.output(s"length of the input: ${inputAsString.length}, parsing time: ${(dt / numRuns) / 1e6}ms")
      }
      stream.close()
    }
  }

  test("newParser", Checked) {
    for (p <- problems) {
      Out.output(s"testing NEW parser with problem file ${p}")
      val stream = getClass.getResourceAsStream(source + "/" + p._1 + ".p")
      val inputAsString = Source.fromInputStream(stream).mkString
      val numRuns = 10;
      {
        var dt: Long = 0
        for (_ <- 0 to numRuns) {
          val t0 = System.nanoTime
          val res = TPTPParser2.parse(inputAsString).right.get._1
          val t1 = System.nanoTime
          dt = (t1 - t0)
          res
        }
        Out.output(s"length of the input: ${inputAsString.length}, parsing time: ${(dt / numRuns) / 1e6}ms")
      }
      stream.close()
    }
  }
}
