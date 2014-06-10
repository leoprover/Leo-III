package leo.modules.parsers

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

import java.io.File

import scala.util.parsing.input.CharArrayReader
import scala.io.Source._

/**
 * This suite tests parsing of various sample problems of the tptp syn package.
 * The module [[leo.modules.parsers.TPTP]], i.e., the method parseFile() is tested.
 * A tests succeeds if the parser can successfully parse the input and
 * fails otherwise (i.e. if a parse error occurs).
 *
 * @author Alexander Steen
 * @since 22.04.2014
 */
@RunWith(classOf[JUnitRunner])
class ParserTestSuite extends FunSuite {
  val files = new File("./tptp/syn").listFiles.filter(_.getName.endsWith(".p"))

  for (f <- files) {
    test(f.getName) {
      val source = fromFile(f, "utf-8")
      val input = new CharArrayReader(source.toArray)
      val parsed = TPTP.parseFile(input)
      assert(parsed.isRight)
    }
  }
}
