package leo
package datastructures

import java.nio.file.Path

import leo.datastructures.ClauseAnnotation.NoAnnotation
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.context.Context
import leo.datastructures.impl.orderings.TO_CPO_Naive
import leo.datastructures.Type._
import leo.datastructures.impl.SignatureImpl$
import leo.modules.{Parsing, SZSException, Utility}
import leo.modules.output.Output

/**
 * Created by lex on 9/29/15.
 */
class TermOrderingTestSuite extends LeoTestSuite {
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

//  val source = getClass.getResource("/problems").getPath
  val source = "/home/lex/TPTP/Problems/GRP/"
  val problem_suffix = ".p"
  val problems = Seq( "GRP685+1")//, "COM003_1", "KRS003_1", "SYN000^1" )
  // PUZ085^1: 0 NC
  // COM001_1: 0 NC
  // COM003_1: 0 NC
  // KRS003_1: 0 NC
  // SYN000^1: 0 NC


  for (p <- problems) {
   test(s"Ordering test for $p", Benchmark) {
     implicit val sig = getFreshSignature
      printHeading(s"Ordering test for $p")
      var (eq,gt,lt,nc): (Set[(Term,Term)],Set[(Term,Term)],Set[(Term,Term)],Set[(Term,Term)]) = (Set(), Set(), Set(), Set())
     var fs : Seq[AnnotatedClause] = Seq()
     try {
       fs = Parsing.parseProblem(source + "/" + p + ".p").map{case (name, term, role) => AnnotatedClause(Clause(Literal(term, true)), role, NoAnnotation, ClauseAnnotation.PropNoProp)}
     } catch {
        case e: SZSException =>
          Out.output(s"Loading $p failed\n   Status=${e.status}\n   Msg=${e.getMessage}\n   DbgMsg=${e.debugMessage}")
          fail()
      }
     Utility.printSignature(sig)

//     val pc = Term.mkAtom(Signature("p").key)
//     val h = Term.mkAtom(Signature("h").key)
//     val a = Term.mkTermApp(pc,h)
//     val b = Term.mkBound(Signature.get.i, 1) //Term.mkTermApp(pc,Term.mkBound(Signature.get.i, 1))
//     Out.output("a: " + a.pretty)
//     Out.output("b: " + b.pretty)
//     val res = TO_CPO_Naive.compare(a,b)
//     Out.output(TermCMPResult(a, b, res))

//     val f1 = FormulaDataStore.getFormulaByName("refl_john").get
//     val a = f1.clause.lits.head.term
//     val f2 = FormulaDataStore.getFormulaByName("refl_peter").get
//     val b = f2.clause.lits.head.term
//
//     val ta = Term.TermApp.unapply(a).get._2.head
//     val tb = Term.TermApp.unapply(b).get._2.head
//
//     val res = TO_CPO_Naive.compare(a, b)
//     Out.output(s"## ${f1.name} w/ ${f2.name}")
//     Out.output(TermCMPResult(a, b, res))

     val fsIt = fs.iterator
     while (fsIt.hasNext) {
       val f = fsIt.next()

       val fsIt2 = FormulaDataStore.getFormulas.iterator
       while (fsIt2.hasNext) {
         val f2 = fsIt2.next()
         if (f != f2) {
           val (a, b) = (Literal.asTerm(f.cl.lits.head), Literal.asTerm(f2.cl.lits.head))
           val res = TO_CPO_Naive.compare(a, b)
           res match {
             case CMP_EQ => eq += ((a, b))
             case CMP_GT => gt += ((a, b))
             case CMP_LT => lt += ((a, b))
             case CMP_NC => nc += ((a, b))
             case _ => assert(false)
           }

//           Out.output(s"## ${f.name} w/ ${f2.name}")
//           Out.output(TermCMPResult(a, b, res))
         }
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
     val symCheck = gt.forall(g => lt.contains(g.swap))
     Out.output(s"At least as many equals than formulas: ${if(eq.size >= FormulaDataStore.getFormulas.size) "Y" else "N"}")
     Out.output(s"Antisymmetry of counter-comparison: ${if (symCheck) "Y" else "N"}")

     def where(s: Term, t: Term): String = {
       if (gt.contains(s,t)) "GT"
       else if (lt.contains(s,t)) "LT"
       else if (nc.contains(s,t)) "NC"
       else if (eq.contains(s,t)) "EQ"
       else "UK"
     }

     if (!symCheck) {
       Out.severe("###################################################")
       Out.severe("Antisymmetry of counter-comparison failed! Awkward pairs:")
       val awk = gt.filter(g => !lt.contains(g.swap))
       for ((s,t) <- awk) {
         Out.severe(s"GT contained\n\t${s.pretty}\n\t${t.pretty}")
         Out.severe(s"but LT did not contained inverted tuple.")
         Out.severe(s"Instead, inverted tuple was contained in ${where(t,s)}")
         Out.severe("##########")
       }
     }

     if (nc.nonEmpty) {
       Out.severe("###################################################")
       Out.severe("Not comparable pairs:")
       for ((s,t) <- nc) {
         Out.severe(s"Pair\n\t${s.pretty}\n\t${t.pretty}")
         Out.severe(s"Not comparable")
         Out.severe("##########")
       }
     }

     val terms: Seq[Term] = FormulaDataStore.getFormulas.map(c => Literal.asTerm(c.cl.lits.head)).toSeq
     import scala.util.Sorting
     def localLT(a: Term, b: Term): Boolean = TO_CPO_Naive.lt(a,b)
     val sorted = Sorting.stableSort[Term](terms, (a:Term,b:Term) => TO_CPO_Naive.lt(a,b) )

     TestUtility.printHLine()
     Out.output("Sort it via internal sorting function")
     val output = "\t" + sorted.map(a =>  a.pretty).mkString("\n\t")
     Out.output(s"Sorted:\n ${output}")
     TestUtility.printHLine()


     val reallysorted = {
       var really = true
       for (i <- 0 to sorted.length-1) {
         for (j <- i+1 to sorted.length-1) {
           if (!TO_CPO_Naive.lt(sorted(i), sorted(j))) {
             really = false
           }
         }
       }
       really
     }

     Out.output(s"Check if really sorted afterwards: ${reallysorted}")
     val max = sorted.last
     val min = sorted.head
     Out.output(s"Maximal Term: ${max.pretty} . Really? ${sorted.init.forall(TO_CPO_Naive.lt(_,max))}")
     Out.output(s"Minimal Term: ${min.pretty} . Really? ${sorted.tail.forall(TO_CPO_Naive.lt(min,_))}")
    }
  }


  private case class TypeCMPResult(a: Type, b: Type) extends Output {
    lazy val apply = s"Comparing\t${a.pretty}\t with \t${b.pretty}\tResult: ${cmpResToStr(TO_CPO_Naive.compare(a,b))}"

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
    lazy val apply = s"Comparing\n\t${a.pretty}\n\t${b.pretty}\nResult: ${cmpResToStr(res)}"

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
