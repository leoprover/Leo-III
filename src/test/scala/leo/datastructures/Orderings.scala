package leo
package datastructures

import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.impl.orderings.TO_CPO_Naive
import leo.datastructures.Type._
import leo.datastructures.impl.Signature
import leo.modules.{SZSException, Utility}
import leo.modules.output.Output

/**
 * Created by lex on 9/29/15.
 */
class Orderings extends LeoTestSuite {
//  test("Type order test", Checked) {
//    val s = Signature.get
//
//    Out.output(TypeCMPResult(s.o,s.i))
//    Out.output(TypeCMPResult(s.i,s.o))
//    Out.output(TypeCMPResult(s.i,s.i))
//    Out.output(TypeCMPResult(s.o,s.o))
//    Out.output(TypeCMPResult(s.o ->: s.o,s.o))
//    Out.output(TypeCMPResult(s.o,s.o ->: s.o))
//    Out.output(TypeCMPResult(s.o ->: s.o,s.o ->: s.o))
//    Out.output(TypeCMPResult(s.o ->: (s.o ->: s.o),s.o ->: s.o))
//    Out.output(TypeCMPResult((s.o ->: s.o) ->: s.o,s.o ->: s.o))
//    Out.output(TypeCMPResult(s.o ->: s.o,s.o ->: s.i))
//    Out.output(TypeCMPResult(s.i ->: s.o,s.o ->: s.i))
//    Out.output(TypeCMPResult(∀(s.i ->: 1),∀(s.i ->: s.i ->: s.i)))
//    Out.output(TypeCMPResult(∀(1 ->: s.i),∀(s.i ->: s.i ->: s.i)))
//  }

  val source = getClass.getResource("/problems").getPath
  val problem_suffix = ".p"
  val problems = Seq( "SYN000^1")//, "COM003_1", "KRS003_1", "SYN000^1" )

  for (p <- problems) {
   test(s"Ordering test for $p", Benchmark) {
      printHeading(s"Ordering test for $p")
      var (eq,gt,lt,nc): (Set[(Term,Term)],Set[(Term,Term)],Set[(Term,Term)],Set[(Term,Term)]) = (Set(), Set(), Set(), Set())
      try {
        Utility.load(source + "/" + p + ".p")
      } catch {
        case e: SZSException =>
          Out.output(s"Loading $p failed\n   Status=${e.status}\n   Msg=${e.getMessage}\n   DbgMsg=${e.debugMessage}")
          fail()
      }
     Utility.printUserDefinedSignature()
     val fsIt = FormulaDataStore.getFormulas.iterator
     while (fsIt.hasNext) {
       val f = fsIt.next()

       val fsIt2 = FormulaDataStore.getFormulas.iterator
       while (fsIt2.hasNext) {
         val f2 = fsIt2.next()
         val (a,b) = (f.clause.lits.head.term, f2.clause.lits.head.term)
         val res = TO_CPO_Naive.compare(a,b)
         res match {
           case CMP_EQ => eq += ((a,b))
           case CMP_GT => gt += ((a,b))
           case CMP_LT => lt += ((a,b))
           case CMP_NC => nc += ((a,b))
           case _ => assert(false)
         }
         Out.output(TermCMPResult(a, b,res))
       }
     }

     printHeading("Statistics")

     Out.output(s"Formula count: ${FormulaDataStore.getFormulas.size}")
     Out.output(s"Comparisons: ${FormulaDataStore.getFormulas.size*FormulaDataStore.getFormulas.size}")
     Out.output(s"Equal: ${eq.size}")
     Out.output(s"Greater: ${gt.size}")
     Out.output(s"Less: ${lt.size}")
     Out.output(s"Uncomparable: ${nc.size}")

     printHeading("Sanity Check")

     Out.output(s"At least as many equals than formulas: ${if(eq.size >= FormulaDataStore.getFormulas.size) "Y" else "N"}")
     Out.output(s"Symmetry of comparison: ${if (gt.forall(g => lt.contains(g.swap))) "Y" else "N"}")

    }
  }


  private case class TypeCMPResult(a: Type, b: Type) extends Output {
    lazy val output = s"Comparing\t${a.pretty}\t with \t${b.pretty}\tResult: ${cmpResToStr(TO_CPO_Naive.compare(a,b))}"

    private final def cmpResToStr(cmpRes: CMP_Result): String = {
      cmpRes match {
        case CMP_EQ => "EQ"
        case CMP_GT => "GT"
        case CMP_LT => "LT"
        case CMP_NC => "NC"
      }
    }
  }
  private case class TermCMPResult(a: Term, b: Term, res: CMP_Result) extends Output {
    lazy val output = s"Comparing\n\t${a.pretty}\n\t${b.pretty}\nResult: ${cmpResToStr(res)}"

    private final def cmpResToStr(cmpRes: CMP_Result): String = {
      cmpRes match {
        case CMP_EQ => "EQ"
        case CMP_GT => "GT"
        case CMP_LT => "LT"
        case CMP_NC => "NC"
      }
    }
  }
}
