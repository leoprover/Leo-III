package leo.modules.input

import leo.LeoTestSuite

import java.io.File

class KloeppelTHFTestSuite extends LeoTestSuite {
  val th0Files = new File("/home/lex/TPTP/Problems/SYN/").listFiles.filter(x => x.getName.endsWith(".p") && x.getName.contains("^")).toList

  th0Files.foreach(f =>
    test(f.getName) {
      try {
        val res = TPTPKloeppelParser.problem(io.Source.fromFile(f.getAbsolutePath))
        println(res.pretty)
      } catch {
        case e: TPTPKloeppelParser.TPTPParseException => println(s"Parse error at ${e.line},${e.offset}: ${e.getMessage}"); throw e
      }
    }
  )
}
