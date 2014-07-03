package leo.modules.parsers

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

import java.io.File

import scala.util.parsing.input.CharArrayReader
import scala.io.Source._
import leo.datastructures.internal.Signature

@RunWith(classOf[JUnitRunner])
class InputProcessingTestSuite extends FunSuite {
  // Path to the problem library
  val __TPTPPROBLEMPATH__ = "/home/lex/Downloads/TPTP-v6.0.0/Problems"

  val files = new File(__TPTPPROBLEMPATH__ +"/SYN").listFiles.filter(_.getName.endsWith(".p")).toSet
  val blacklist: Set[File] = (files.filter(_.getName.contains("-")) ++
                              files.filter({
                                      case f =>
                                        Set("SYN000_2.p" // contains product types
                                          , "SYN000_1.p" // contains product types
                                          , "SYN357^7.p" // contains imports
                                          , "SYN367^7.p" // contains imports
                                          , "SYN036^7.p" // contains imports
                                          , "SYN052^7.p" // contains imports
                                          , "SYN377^7.p" // contains imports
                                          , "SYN407^7.p" // contains imports
                                          , "SYN397^7.p" // contains imports
                                          , "SYN000=2.p" // we dont have all tptp defined symbols
                                          , "SYN000^2.p" // let not yet implemented
                                        ).contains(f.getName)}))
  val sig = Signature.get

  for (f <- files -- blacklist) {
    test(f.getName) {
      Signature.resetWithHOL(sig)
      val source = fromFile(f, "utf-8")
      val input = new CharArrayReader(source.toArray)
      val parsed = TPTP.parseFile(input)

      parsed match {
        case Left(err) => println(err); fail("Parsing error")
        case Right(res) => {
          val formulae = res.getFormulae
          try {
            val processed = InputProcessing.processAll(sig)(formulae)
            // do sth here
            assert(processed.size == formulae.size)
          } catch {
            case e: Throwable => e.printStackTrace(); {for (s <- sig.allConstants) {
              print(sig(s).key.toString + "\t\t")
              print(sig(s).name + "\t\t:\t")
              sig(s).ty.foreach({ case ty => print(ty.pretty)})
              sig(s).kind.foreach({ case ty => print(ty.pretty)})
              println()
            }}; fail("Processing error")
          }

        }
      }
    }
  }
}
