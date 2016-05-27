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

  val specialProblemFormula = "(g: ( $i * $i ) > $i )"

  val ite_formula =
    """! [Z: $i] :
     $ite_f(
        ? [X: $i] : ( p @ X)
      , ! [X: $i] : (q @ X @ X)
      , ( q @ Z @ $ite_f(! [X: $i] : ( p @ X), ( f @ a), ( f@ Z))) )"""

  val connective_terms =
    """(
    ! [P: $o,C: $i] :
      ( ( & @ ( p @ C ) @ P )
      = ( ~ @ ( ~& @ ( p @ C ) @ P ) ) ) )
     """
  val source_test =
    """thf(source,axiom,(
      p ),
    file('SYN000-1.p')).
    """.stripMargin


  val source_inference_with_bind_test =
    """thf(source_inference_with_bind,axiom,
    ( p @ a ),
  inference(magic,[status(thm)],[theory(equality),source_unknown:[bind(X,$fot(a))]]))."""

  /*
  test("specialTPTPTest", Checked) {
    var tokens = TPTPParser2.tokenize(source_inference_with_bind_test)
    Out.output(s"parsing: ${tokens.take(5)} ...")
    var parsed = TPTPParser2.parseAnnotatedFormulaOrInclude(tokens)
      if (parsed.isLeft) {
        fail(s"FAILED. Cause: ${parsed.left.get}")
      } else {
        val res = parsed.right.get
        Out.output(s"parse result: ${res._1}")
        tokens = res._2
        //Out.output(s"Parsing succeeded. Parsed ${res.getFormulaeCount} formulae and ${res.getIncludeCount} include statements.")
      }
  }

  test("specialThfTest", Checked) {
    var tokens = ThfParser.tokenize(connective_terms)
    Out.output(s"parsing: ${tokens.take(5)} ...")
    var parsed = ThfParser.parse(tokens)
      if (parsed.isLeft) {
        fail(s"FAILED. Cause: ${parsed.left.get}")
      } else {
        val res = parsed.right.get
        Out.output(s"parse result: ${res._1}")
        tokens = res._2
        //Out.output(s"Parsing succeeded. Parsed ${res.getFormulaeCount} formulae and ${res.getIncludeCount} include statements.")
      }
  }
  */


  /*
  val tokensOld = OldParser.tokens( inputString )
  var parsedOld = OldParser.tptpFile(tokensOld)
  */

  //val parsed = TPTP.parseFile(new CharArrayReader(fromFile(source + "/" +  p._1 + ".p").toArray))

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
        val res = TPTPParser2.parse(Source.fromInputStream(stream).mkString).right.get._1
        stream.close()
        res
      }
      var oldRes = parseWithOld.inputs
      var newRes = parseWithNew.inputs

      //assert( oldRes == newRes )

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

  /*
  test("parse with new parser", Checked) {
    for (p <- problems) {
      val stream = getClass.getResourceAsStream(source+ "/" + p._1 + ".p")
      val res = TPTPParser2.parse(Source.fromInputStream(stream).mkString)
      println(s"res: ${res}")
    }
  }
  */

  /*
  for (p <- problems) {
    Out.output(s"testing ${p._2}")
    val stream = getClass().getResourceAsStream(source + "/" + p._1 + ".p")
    val input = Source.fromInputStream( stream )
    var tokens = TPTPParser2.tokenize(input.mkString)

    while( ! tokens.isEmpty ) {

      Out.output(s"parsing: ${tokens.take(5)} ...")
      var parsed = TPTPParser2.parseAnnotatedFormulaOrInclude(tokens)
      if (parsed.isLeft) {
        fail(s"FAILED. Cause: ${parsed.left.get}")
      } else {
        val res = parsed.right.get
        Out.output(s"parse result: ${res._1}")
        tokens = res._2
        //Out.output(s"Parsing succeeded. Parsed ${res.getFormulaeCount} formulae and ${res.getIncludeCount} include statements.")
      }

    }
    input.close()
  }
  */

}
