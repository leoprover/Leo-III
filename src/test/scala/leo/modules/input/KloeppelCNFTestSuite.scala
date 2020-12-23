package leo.modules.input

import leo.LeoTestSuite

import java.io.File

class KloeppelCNFTestSuite  extends LeoTestSuite {
  val cnfFiles = new File("/home/lex/TPTP/Problems/SYN/").listFiles.filter(x => x.getName.endsWith(".p") && x.getName.contains("-")).toList

  cnfFiles.foreach(f =>
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
