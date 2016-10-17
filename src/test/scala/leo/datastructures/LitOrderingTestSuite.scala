package leo.datastructures

import leo.Benchmark
import leo.LeoTestSuite
import leo._
import leo._
import leo.datastructures.ClauseAnnotation.NoAnnotation


import leo.datastructures.context.Context
import leo.modules._
import leo.datastructures.Term.:::>
import leo.modules.HOLSignature.{=== => EQ, Forall, Exists, LitTrue}

/**
 * Created by lex on 10/27/15.
 */
class LitOrderingTestSuite extends LeoTestSuite {
  val source = "/home/lex/TPTP/Problems/GRP/"
  val problem_suffix = ".p"
  val problems = Seq( "GRP659+1")//, "COM003_1", "KRS003_1", "SYN000^1" )
  // GRP656+1 eqlits comparable
  // GRP657+1 eqlits comparable
  // GRP658+1 eqlits comparable
  // GRP659+1 eqlits comparable
  // GRP660+1 eqlits comparable
  // GRP660+2 eqlits comparable
  // GRP660+3 eqlits comparable
  // GRP685+1 eqlits comparable
  // LCL895+1
  // REL027+4


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

      var eqlits: Set[Literal] = Set()
      var nceqlits: Set[(Literal, Literal)] = Set()
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
      printHeading("Maximal literals")
      val maxlits = Literal.maxOf(eqlits.toSeq)
      for (ll <- maxlits) {
        Out.output(ll.pretty)
        val reallymaximal = eqlits.filterNot(_ == ll).forall(a => Orderings.isGE(ll.compare(a)))
          Out.output(s"Check if really maximal? ${reallymaximal}")
        assert(reallymaximal)
      }


      printHeading("Strictly Maximal literals")
      val smaxlits = Literal.strictlyMaxOf(eqlits.toSeq)
      for (lll <- smaxlits) {
        Out.output(lll.pretty)
        val reallymaximal = eqlits.filterNot(_ == lll).forall(a => (lll.compare(a) == CMP_GT))
        Out.output(s"Check if really strictly maximal? ${reallymaximal}")
        assert(reallymaximal)
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

}
