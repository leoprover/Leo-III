package leo.modules.input

import leo.LeoTestSuite

import java.io.File

class KloeppelTFFTestSuite  extends LeoTestSuite {
  val tffFiles0 = new File("/home/lex/TPTP/Problems/SYN/").listFiles.filter(x => x.getName.endsWith(".p") && x.getName.contains("_")).toList
  val tffFiles = tffFiles0.filter(_.getName != "SYN000_4.p")

  tffFiles.foreach(f =>
    test(f.getName) {
      try {
        val res = TPTPParser.problem(io.Source.fromFile(f.getAbsolutePath))
        println(res.pretty)
      } catch {
        case e: TPTPParser.TPTPParseException => println(s"Parse error at ${e.line},${e.offset}: ${e.getMessage}"); throw e
      }
    }
  )
}
