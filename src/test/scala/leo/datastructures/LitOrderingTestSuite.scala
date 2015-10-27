package leo.datastructures

import leo.Benchmark
import leo.LeoTestSuite
import leo._
import leo._

import leo.datastructures.Term.:::>
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.impl.orderings.TO_CPO_Naive
import leo.datastructures.impl.orderings.TO_CPO_Naive
import leo.modules.SZSException
import leo.modules.Utility
import leo.modules.output.Output
import leo.datastructures.Term.{:::>}
import leo.datastructures.{=== => EQ}
import leo.modules.output.Output
import leo.modules.{SZSException, Utility}

/**
 * Created by lex on 10/27/15.
 */
class LitOrderingTestSuite extends LeoTestSuite {
  val source = "/home/lex/TPTP/Problems/GRP/"
  val problem_suffix = ".p"
  val problems = Seq( "GRP685+1")//, "COM003_1", "KRS003_1", "SYN000^1" )
  // GRP656+1 eqlits comparable
  // GRP657+1 eqlits comparable
  // GRP658+1 eqlits comparable
  // GRP659+1 eqlits comparable
  // GRP660+1 eqlits comparable
  // GRP660+2 eqlits comparable
  // GRP660+3 eqlits comparable
  // GRP685+1 eqlits comparable


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
      Utility.printSignature()

      printHeading("Parsed terms")
      val fsIt = FormulaDataStore.getFormulas.iterator

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

      var eqlits: Set[Literal] = Set()
      var nceqlits: Set[(Literal, Literal)] = Set()
      while (fsIt.hasNext) {
        val f = fsIt.next()

        Out.output(s"Unit clause: ${f.clause.pretty}")
        assert(f.clause.unit)
        Out.output(s"equational? ${f.clause.lits.head.equational}")
        if (!f.clause.lits.head.equational) {
          Out.output(s"Leading quantifiers? ${hasLeadingQuant(f.clause.lits.head.left)}")
          if (hasLeadingQuant(f.clause.lits.head.left)) {
            Out.output(s"Removing leading quantifiers...")
            val l =  f.clause.lits.head.leftTermMap(removeLeadingQuants)
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
              eqlits += newlit
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
      printHeading("Compare equational literals")
      Out.output(s"${eqlits.size} equational lits found. Now comparing them mutually ...")




      val eqlitIt = eqlits.iterator
      while (eqlitIt.hasNext) {
        val l1 = eqlitIt.next()
        val eqlitIt2 = eqlits.iterator
        while (eqlitIt2.hasNext) {
          val l2 = eqlitIt2.next()
          Out.output("Comparing")
          Out.output("\t\t" + l1.pretty)
          Out.output("\t\t" + l2.pretty)
          if (l1 == l2) {
            Out.output(s"Literals equal, skipping.")
          } else {
            val res = l1.compare(l2)
            if (res == CMP_NC) {
              nceqlits += ((l1,l2))
            }
            Out.output(cmpResToStr(res))
          }
          printLongHLine()
        }
      }
      if (nceqlits.isEmpty) {
        Out.output("All literals could be compared")
      } else {
        Out.warn(s"${nceqlits.size} literals could not be compared.")
        for ((l1,l2) <- nceqlits) {
          Out.warn(s"Could not compare\n\t${l1.pretty}\n\t${l2.pretty}")
        }
      }
    }
  }

  private def cmpResToStr(cmpRes: CMP_Result): String = {
    cmpRes match {
      case CMP_EQ => "EQ"
      case CMP_GT => "GT"
      case CMP_LT => "LT"
      case CMP_NC => "NC"
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
