package leo.modules.input

import leo.LeoTestSuite

import java.io.File

class KloeppelFOFTestSuite  extends LeoTestSuite {
  val fofFiles = new File("/home/lex/TPTP/Problems/SYN/").listFiles.filter(x => x.getName.endsWith(".p") && x.getName.contains("+")).toList
//  val fofFiles = Seq(new File("/home/lex/TPTP/Problems/SYN/SYN000+1.p"))
  fofFiles.foreach(f =>
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
