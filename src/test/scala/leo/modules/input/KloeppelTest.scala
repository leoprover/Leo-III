package leo.modules.input

import java.nio.file.{Files, Paths}

import leo.LeoTestSuite
import leo.modules.parsers.antlr.tptpLexer
import org.antlr.v4.runtime.CharStreams

class KloeppelTest extends LeoTestSuite {
  test("Lexer stream test 1") {
    val s = "thf(a, asd989asd_asd, asd992__asd, 'hallo'(X):X:'Y':[10,1,2,3,12.1,12/7], [132.132E2, \"hallo\", hallo_welt, 'asda\\'sd9\\\\9&a'])."
    val x = new TPTPParser.TPTPLexer(io.Source.fromString(s))
    while (x.hasNext) {
      print(x.next())
    }
    println()
    val y = new TPTPParser.TPTPParser(new TPTPParser.TPTPLexer(io.Source.fromString(s)))
    try {
      println(y.annotatedTHF())
    } catch {
      case e: TPTPParser.TPTPParseException => println(s"Parse error at ${e.line},${e.offset}: ${e.getMessage}")
    }
  }

  test("Annotated formula") {
    val s = "thf(a, asd989asd_asd, asd992__asd, 'hallo'(X):X:'Y':[10,1,2,3,12.1,12/7], [132.132E2, \"hallo\", hallo_welt, 'asda\\'sd9\\\\9&a'])."
    val x = new TPTPParser.TPTPLexer(io.Source.fromString(s))
    while (x.hasNext) {
      print(x.next())
    }
    println()
    val y = new TPTPParser.TPTPParser(new TPTPParser.TPTPLexer(io.Source.fromString(s)))
    try {
      val res = y.annotatedFormula()
      println(res)
      println(res.pretty)
    } catch {
      case e: TPTPParser.TPTPParseException => println(s"Parse error at ${e.line},${e.offset}: ${e.getMessage}")
    }
  }

  test("include") {
    val s = "include('asdasd', ['asd',123])."
    val x = new TPTPParser.TPTPLexer(io.Source.fromString(s))
    while (x.hasNext) {
      print(x.next())
    }
    println()
    val y = new TPTPParser.TPTPParser(new TPTPParser.TPTPLexer(io.Source.fromString(s)))
    try {
      val res = y.include()
      println(res)
    } catch {
      case e: TPTPParser.TPTPParseException => println(s"Parse error at ${e.line},${e.offset}: ${e.getMessage}")
    }
  }

  test("TPTP File 1") {
    val s =
      """ thf(1, role, a, b, [c,d]).
        | thf(2, role, b, a:b:X:132).
        | thf(3, type, a:b).
        | include('asd/asd.p').
        | thf(3, role, (a = b) | ((~c) = d)).
        | include('asd/asd.p', ['asd']).
        |""".stripMargin
    val x = new TPTPParser.TPTPLexer(io.Source.fromString(s))
    while (x.hasNext) {
      print(x.next())
    }
    println()
    val y = new TPTPParser.TPTPParser(new TPTPParser.TPTPLexer(io.Source.fromString(s)))
    try {
      val res = y.tptpFile()
      println(res)
      println(res.pretty)
    } catch {
      case e: TPTPParser.TPTPParseException => println(s"Parse error at ${e.line},${e.offset}: ${e.getMessage}")
    }
  }
  val TPTP = sys.env("TPTP")
  test("SYN000^1") {
    val x = new TPTPParser.TPTPLexer(io.Source.fromFile(s"$TPTP/Problems/SYN/SYN000^1.p"))
    while (x.hasNext) {
      print(x.next())
    }
    println()
    val y = new TPTPParser.TPTPParser(new TPTPParser.TPTPLexer(io.Source.fromFile(s"$TPTP/Problems/SYN/SYN000^1.p")))
    try {
      val res = y.tptpFile()
      println(res)
      println(res.pretty)
    } catch {
      case e: TPTPParser.TPTPParseException => println(s"Parse error at ${e.line},${e.offset}: ${e.getMessage}")
    }
  }

  test("SYN000^2") {
    val x = new TPTPParser.TPTPLexer(io.Source.fromFile(s"$TPTP/Problems/SYN/SYN000^2.p"))
    while (x.hasNext) {
      print(x.next())
    }
    println()
    val y = new TPTPParser.TPTPParser(new TPTPParser.TPTPLexer(io.Source.fromFile(s"$TPTP/Problems/SYN/SYN000^2.p")))
    try {
      val res = y.tptpFile()
      println(res)
      println(res.pretty)
    } catch {
      case e: TPTPParser.TPTPParseException => println(s"Parse error at ${e.line},${e.offset}: ${e.getMessage}")
    }
  }

  test("LEG101") {
    try {
      val res = TPTPParser.problem(io.Source.fromFile(s"/home/lex/Downloads/LEG-THF/LEG/LEG101~1.embed.p"))

      println(res.pretty)
    } catch {
      case e: TPTPParser.TPTPParseException => println(s"Parse error at ${e.line},${e.offset}: ${e.getMessage}")
    }
  }

}
