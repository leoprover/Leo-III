package parsers

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import scala.util.parsing.input.CharArrayReader

/**
 * Created by lex on 22.04.14.
 */
@RunWith(classOf[JUnitRunner])
class ParserTestSuite extends FunSuite {
//
//  test("Parsing some syn files") {
//    val files = new java.io.File("./tptp/syn").listFiles.filter(_.getName.endsWith(".p"))
//    for (f <- files) {
//      val source = scala.io.Source.fromFile(f, "utf-8")
//      lazy val input = new CharArrayReader(source.toArray)
//      val parsed = TPTP.parseFile(input)
//      assert(parsed.isRight)
////      println("Result of " + f.getName + ": " + parsed)
//    }
//  }

  test("SYN115-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN115-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN111-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN111-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN145-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN145-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN032-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN032-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN065-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN065-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN114-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN114-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN240-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN240-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN053+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN053+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN205-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN205-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN234-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN234-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN076-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN076-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN052+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN052+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN122-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN122-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN045^4.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN045^4.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN009-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN009-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN131-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN131-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN059+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN059+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN138-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN138-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN233-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN233-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN098-1.002.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN098-1.002.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN001^4.004.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN001^4.004.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN002-1.007.008.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN002-1.007.008.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN046-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN046-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN189-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN189-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN074+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN074+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN058+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN058+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN033-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN033-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN161-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN161-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN179-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN179-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN007+1.014.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN007+1.014.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN064+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN064+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN177-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN177-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN066+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN066+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN252-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN252-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN012-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN012-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN004-1.007.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN004-1.007.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN077-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN077-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN099-1.003.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN099-1.003.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN062+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN062+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN057+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN057+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN174-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN174-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN036-2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN036-2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN056^5.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN056^5.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN246-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN246-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN152-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN152-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN009-3.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN009-3.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN192-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN192-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN178-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN178-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN096-1.008.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN096-1.008.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN014-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN014-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN069+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN069+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN064^5.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN064^5.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN248-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN248-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN220-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN220-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN188-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN188-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN072+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN072+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN180-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN180-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN133-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN133-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN208-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN208-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN054+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN054+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN000-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN000-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN095-1.002.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN095-1.002.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN107-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN107-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN061+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN061+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN055-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN055-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN003-1.006.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN003-1.006.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN166-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN166-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN093-1.002.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN093-1.002.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN164-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN164-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN036^7.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN036^7.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN059-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN059-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN201-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN201-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN229-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN229-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN194-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN194-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN149-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN149-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN045^7.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN045^7.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN000^1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN000^1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN041-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN041-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN204-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN204-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN150-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN150-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN048+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN048+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN231-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN231-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN051+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN051+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN128-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN128-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN082+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN082+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN119-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN119-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN047+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN047+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN083+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN083+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN109-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN109-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN084-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN084-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN217-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN217-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN083-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN083-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN078+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN078+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN191-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN191-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN219-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN219-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN186-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN186-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN001^4.003.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN001^4.003.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN028-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN028-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN176-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN176-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN036-3.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN036-3.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN071-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN071-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN129-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN129-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN009-4.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN009-4.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN132-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN132-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN125-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN125-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN159-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN159-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN074-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN074-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN163-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN163-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN011-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN011-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN244-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN244-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN036+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN036+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN207-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN207-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN214-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN214-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN039-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN039-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN079+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN079+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN092-1.003.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN092-1.003.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN063-2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN063-2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN213-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN213-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN228-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN228-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN168-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN168-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN197-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN197-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN200-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN200-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN167-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN167-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN052^7.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN052^7.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN190-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN190-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN162-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN162-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN198-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN198-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN102-1.007.007.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN102-1.007.007.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN237-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN237-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN221-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN221-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN008-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN008-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN037-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN037-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN113-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN113-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN040^4.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN040^4.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN210-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN210-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN000+2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN000+2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN236-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN236-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN036+2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN036+2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN077+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN077+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN081+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN081+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN034-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN034-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN065+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN065+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN037-2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN037-2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN001-1.005.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN001-1.005.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN212-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN212-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN075+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN075+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN172-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN172-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN250-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN250-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN041+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN041+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN040+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN040+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN127-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN127-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN031-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN031-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN001^4.002.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN001^4.002.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN211-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN211-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN061-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN061-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN137-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN137-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN055+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN055+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN045-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN045-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN073+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN073+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN036-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN036-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN146-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN146-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN041^4.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN041^4.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN044-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN044-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN089-1.002.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN089-1.002.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN158-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN158-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN153-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN153-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN097-1.002.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN097-1.002.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN054-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN054-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN047-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN047-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN232-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN232-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN001^4.001.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN001^4.001.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN222-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN222-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN060-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN060-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN123-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN123-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN058-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN058-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN215-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN215-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN142-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN142-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN148-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN148-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN000=2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN000=2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN105-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN105-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN223-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN223-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN045+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN045+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN000_1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN000_1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN091-1.003.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN091-1.003.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN063+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN063+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN187-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN187-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN171-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN171-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN175-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN175-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN139-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN139-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN052-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN052-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN038-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN038-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN044^4.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN044^4.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN196-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN196-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN006-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN006-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN245-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN245-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN040-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN040-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN046^4.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN046^4.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN136-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN136-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN068+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN068+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN000^2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN000^2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN230-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN230-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN084+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN084+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN101-1.002.002.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN101-1.002.002.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN049+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN049+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN094-1.005.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN094-1.005.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN000+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN000+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN202-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN202-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN165-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN165-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN104-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN104-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN058^5.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN058^5.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN035-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN035-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN134-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN134-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN151-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN151-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN147-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN147-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN157-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN157-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN108-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN108-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN251-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN251-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN135-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN135-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN013-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN013-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN156-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN156-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN081-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN081-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN067-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN067-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN225-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN225-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN086-1.003.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN086-1.003.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN181-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN181-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN216-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN216-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN154-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN154-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN075-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN075-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN079-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN079-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN049^5.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN049^5.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN100-1.005.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN100-1.005.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN235-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN235-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN184-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN184-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN082-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN082-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN063-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN063-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN242-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN242-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN071+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN071+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN143-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN143-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN036^5.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN036^5.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN117-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN117-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN126-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN126-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN185-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN185-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN141-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN141-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN106-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN106-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN015-2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN015-2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN057-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN057-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN144-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN144-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN155-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN155-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN059^5.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN059^5.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN124-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN124-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN007^4.014.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN007^4.014.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN050-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN050-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN047^4.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN047^4.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN118-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN118-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN064-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN064-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN010-1.005.005.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN010-1.005.005.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN067-3.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN067-3.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN044+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN044+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN060+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN060+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN087-1.003.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN087-1.003.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN009-2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN009-2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN130-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN130-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN247-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN247-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN070-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN070-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN243-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN243-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN241-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN241-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN000-2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN000-2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN072-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN072-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN085-1.010.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN085-1.010.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN121-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN121-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN169-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN169-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN224-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN224-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN073-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN073-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN249-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN249-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN088-1.010.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN088-1.010.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN193-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN193-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN080+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN080+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN036-4.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN036-4.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN195-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN195-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN029-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN029-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN206-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN206-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN209-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN209-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN226-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN226-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN218-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN218-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN170-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN170-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN057^5.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN057^5.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN182-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN182-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN080-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN080-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN120-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN120-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN238-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN238-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN053-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN053-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN046+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN046+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN103-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN103-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN056-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN056-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN056+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN056+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN030-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN030-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN112-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN112-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN070+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN070+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN015-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN015-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN050+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN050+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN078-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN078-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN005-1.010.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN005-1.010.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN160-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN160-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN051-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN051-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN001+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN001+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN116-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN116-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN203-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN203-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN066-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN066-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN055^5.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN055^5.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN051^5.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN051^5.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN068-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN068-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN069-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN069-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN239-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN239-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN173-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN173-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN084-2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN084-2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN110-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN110-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN014-2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN014-2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN227-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN227-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN062-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN062-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN049-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN049-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN067+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN067+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN048-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN048-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN183-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN183-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN067-2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN067-2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN090-1.008.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN090-1.008.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN199-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN199-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN140-1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN140-1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN076+1.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN076+1.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }
  test("SYN000_2.p") {
    val source = scala.io.Source.fromFile("./tptp/syn/SYN000_2.p", "utf-8")
    lazy val input = new CharArrayReader(source.toArray)
    val parsed = TPTP.parseFile(input)
    assert(parsed.isRight)
  }

}
