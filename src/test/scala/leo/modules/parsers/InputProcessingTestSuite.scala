package leo.modules.parsers

import leo.datastructures.impl.Signature
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

import java.io.File

import scala.util.parsing.input.CharArrayReader
import scala.io.Source._
import org.scalatest.exceptions.TestFailedException

@RunWith(classOf[JUnitRunner])
class InputProcessingTestSuite extends FunSuite {
  // Path to the problem library
  val __TPTPPROBLEMPATH__ = "/home/lex/Downloads/TPTP-v6.0.0/Problems"

  val files = new File(__TPTPPROBLEMPATH__ +"/SYN").listFiles.filter(_.getName.endsWith(".p")).toSet
  val blacklist: Set[File] = (files.filter(_.getName.contains("-")) ++ // exclude CNF at the moment
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
                                          , "SYN045^7.p" // contains import BEGIN
                                          , "SYN393^4.003.p"
                                          , "SYN047^4.p"
                                          , "SYN978^4.p"
                                          , "SYN387^4.p"
                                          , "SYN392^4.p"
                                          , "SYN977^4.p"
                                          , "SYN040^4.p"
                                          , "SYN046^4.p"
                                          , "SYN741^7.p"
                                          , "SYN388^4.p"
                                          , "SYN393^4.002.p"
                                          , "SYN915^4.p"
                                          , "SYN001^4.002.p"
                                          , "SYN001^4.004.p"
                                          , "SYN393^4.004.p"
                                          , "SYN007^4.014.p"
                                          , "SYN041^4.p"
                                          , "SYN389^4.p"
                                          , "SYN044^4.p"
                                          , "SYN390^4.p"
                                          , "SYN387^7.p"
                                          , "SYN001^4.001.p"
                                          , "SYN001^4.003.p"
                                          , "SYN391^4.p"
                                          , "SYN045^4.p"
                                          , "SYN416^4.p"
                                          , "SYN916^4.p"
                                          , "SYN416^7.p" // contains import END
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

            assert(processed.size == formulae.size)
            try {
              assert(processed.forall(_._2.lits.forall(_.term.typeCheck)))
            } catch {
              case e:TestFailedException => {
                for (t <- processed) {
                  if (!t._2.lits.forall(_.term.typeCheck)) {
                    println("Name: " + t._1)
                    println("Clause: " + t._2)
                    println("Pretty: " + t._2.pretty)
                    println("Types: " + t._2.map(_.term.ty).mkString(" , "))
                    println("Pretty: " + t._2.map(_.term.ty.pretty).mkString(" , "))
                  }
                }
                throw e
              }
            }
          } catch {
            case e: Throwable => {
              println("CAUGHT EXCEPTION:" + e.getMessage)
              println()
              for (s <- sig.allConstants) {
                print(sig(s).key.toString + "\t\t")
                print(sig(s).name + "\t\t:\t")
                sig(s).ty.foreach({ case ty => print(ty.pretty)})
                sig(s).kind.foreach({ case ty => print(ty.pretty)})
                println()
              }
              println()
              e.printStackTrace()
              fail(e)}
          }

        }
      }
    }
  }
}
