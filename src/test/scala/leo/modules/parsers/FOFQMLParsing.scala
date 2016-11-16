package leo.modules.parsers

import leo.LeoTestSuite
import leo.datastructures.Signature
import leo.modules.parsers.syntactical.TPTPParsers

/**
  * Created by lex on 11/15/16.
  */
class FOFQMLParsing extends LeoTestSuite {


  import TPTPParsers.{tptpFile, parse}
  import syntactical.TPTPParsers._
  def parseFile(input: String) = {
    extract(parse(input, tptpFile))
  }

  // give simplified parsing result representations to the outside
  private def extract[T](res: ParseResult[T]): Either[String, T] = {
    res match {
      case Success(x, _) => Right(x)
      case noSu: NoSuccess => Left(noSu.msg)
    }
  }



 test("asd") {
   val input =
     """
       |%--------------------------------------------------------------------------
       |% File    APM001+1QMLTP v1.1
       |% Domain  Applications mixed
       |% Problem Belief Change in man-machine-dialogues
       |% Version Especial.
       |% English
       |
       |% Refs    [FHL+98] L. Farinas del Cerro, A. Herzig, D. Longin, O. Rifi.
       |%             Belief Reconstruction in Cooperative Dialogues. AIMSA 1998,
       |%             LNCS 1480, pp. 254-266. Springer, 1998.
       |% Source  [FHL98]
       |% Names
       |
       |% Status       varying      cumulative   constant
       |%             K   Theorem      Theorem      Theorem       v1.1
       |%             D   Theorem      Theorem      Theorem       v1.1
       |%             T   Theorem      Theorem      Theorem       v1.1
       |%             S4  Theorem      Theorem      Theorem       v1.1
       |%             S5  Theorem      Theorem      Theorem       v1.1
       |%
       |% Rating       varying      cumulative   constant
       |%             K   0.00         0.00         0.00          v1.1
       |%             D   0.00         0.17         0.17          v1.1
       |%             T   0.00         0.00         0.00          v1.1
       |%             S4  0.00         0.00         0.00          v1.1
       |%             S5  0.00         0.00         0.00          v1.1
       |%
       |%  term conditions for all terms: designation: rigid, extension: local
       |%
       |% Comments
       |
       |%--------------------------------------------------------------------------
       |
       |thf(law1, axiom,(box @ (((dest @ (paris)) & (class @ (first))) => (price @ (ninetyfive))))).
     """.stripMargin

   val parseResult = parseFile(input)
   implicit val sig: Signature = getFreshSignature
   val r = InputProcessing.processAll(sig)(parseResult.right.get.getFormulae)
    println(parseResult)
   println(r)
 }

}
