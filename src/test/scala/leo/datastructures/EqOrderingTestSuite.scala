package leo.datastructures

import leo._
import leo.datastructures.ClauseAnnotation.NoAnnotation
import leo.datastructures.impl.orderings.TO_CPO_Naive
import leo.modules.output.Output
import leo.datastructures.Term.{:::>}
import leo.datastructures.{=== => EQ}
import leo.modules.{Parsing, SZSException, Utility}

/**
 * Created by lex on 10/27/15.
 */
class EqOrderingTestSuite extends LeoTestSuite {
  val source = "/home/lex/TPTP/Problems/GRP/"
  val problem_suffix = ".p"
  val problems = Seq( "GRP660+3")//, "COM003_1", "KRS003_1", "SYN000^1" )
  // GRP656+1 all eq and orient
  // GRP657+1 all eq and orient
  // GRP658+1 eq => orient
  // GRP659+1 eq => orient
  // GRP660+1 1 eq not orient
  // GRP660+2 1 eq not orient
  // GRP660+3 1 eq not orient
  // GRP685+1 eq => orient


  for (p <- problems) {
    test(s"Ordering test for $p", Benchmark) {
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
      Utility.printSignature()

      printHeading("Parsed terms")
      val fsIt = fs.iterator

      def hasLeadingQuant(t: Term): Boolean = {
        t match {
          case Forall(t) => true
          case Exists(t) => true
          case _ => false
        }
      }

      def removeLeadingQuants(term: Term): Term = term match {
        case Forall(_ :::> t) => removeLeadingQuants(t)
        case Exists(_ :::> t) => removeLeadingQuants(t)
        case t => t
      }

      def eqOnTop(term: Term): Boolean = {
        EQ.unapply(term).isDefined
      }


      while (fsIt.hasNext) {
        val f = fsIt.next()

        Out.output(s"Unit clause: ${f.cl.pretty}")
        assert(Clause.unit(f.cl))
        Out.output(s"equational? ${f.cl.lits.head.equational}")
        if (!f.cl.lits.head.equational) {
          Out.output(s"Leading quantifiers? ${hasLeadingQuant(f.cl.lits.head.left)}")
          if (hasLeadingQuant(f.cl.lits.head.left)) {
            Out.output(s"Removing leading quantifiers...")
            val l =  f.cl.lits.head.leftTermMap(removeLeadingQuants)
            Out.output(s"Resulting literal: ${l.pretty}")
            Out.output(s"Now equality on top?: ${eqOnTop(l.left)}")
            if (!eqOnTop(l.left)) {
              Out.output("Skipping this literal since other symbols on top")
            } else {
              Out.output("Making literal equational...")
              assert(l.right == LitTrue())
              val (newl, newr) = EQ.unapply(l.left).get
              Out.output(s"Assumed left: ${newl.pretty}")
              Out.output(s"Assumed right: ${newr.pretty}")
              val newlit = l.termMap {case (l,r) => (newl, newr)}
              Out.output(s"New literal: ${newlit.pretty}")
              Out.output(s"Literal now equational (should be)? ${newlit.equational}")
              assert(newlit.equational)
              Out.output(s"new Literal could be oriented? ${newlit.oriented}")

              if (newlit.oriented) {
                Out.output(s"... then left > right? ${newlit.left.compareTo(newlit.right) == CMP_GT}")
                if (newlit.left.compareTo(newlit.right) != CMP_GT) {
                  Out.warn("Oriented but greater-relation not given.")
                }
              } else {
                  Out.warn("Could not be oriented")
              }

            }
          }
        }
        printLongHLine()

      }

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
