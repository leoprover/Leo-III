package leo.modules.normalization

import leo.datastructures._
import leo.datastructures.impl.Signature
import leo.datastructures.term.Term
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import leo.datastructures.internal._
import Term._

/**
 * Created by ryu on 6/16/14.
 */
@RunWith(classOf[JUnitRunner])
class SkolemizationTestSuite extends FunSuite {
  val s = Signature.get

  val p = mkAtom(s.addUninterpreted("t", s.o ->: s.o ->: s.o ->: s.o))
  val r = mkAtom(s.addUninterpreted("u", s.i ->: s.o))
  val t = mkAtom(s.addUninterpreted("v", s.i ->: s.i ->: s.o))
  val q = mkAtom(s.addUninterpreted("q1", s.i ->: s.i ->: s.o))


  def eqa(t : Term, s : Term) = ===((t,s))
  def vari(i : Int) : Term= mkBound(s.o, i)

  var toNorm : Map[Term,(Term,Term)] = Map[Term, (Term,Term)]()

  def addTest(what : Term, calc : Term, exp : Term) {
    toNorm += ((what, (calc, exp)))
  }

//  println("\n------------------\nSkolemization Test.\n---------------------")

  // Test 1
  val test1 = Exists(\(s.o)(Forall(\(s.o)(Exists(\(s.o)(mkTermApp(p,List(vari(1),vari(2),vari(3)))))))))
  val test1Sk = Skolemization.normalize(termToClause(test1)).lits.head.term
  val erg1 = Forall(\(s.o)(mkTermApp(p,List(mkTermApp(mkAtom(s("SK1").key), List(vari(1),mkAtom(s("SK2").key))),vari(1),mkAtom(s("SK2").key)))))
  addTest(test1, test1Sk, erg1)


  // Test 2
  val test2 = Forall(\(s.i)(Exists(\(s.i)(|||((mkTermApp(r,mkBound(s.i, 1))), (mkTermApp(t,List(mkBound(s.i,1), mkBound(s.i,2)))))))))
  val test2Sk = Skolemization.normalize(termToClause(test2)).lits.head.term
  val erg2 = ||| (mkTermApp(r, mkAtom(s("SK3").key)), Forall(\(s.i)(mkTermApp(t, List(mkTermApp(mkAtom(s("SK4").key), mkBound(s.i,1)), mkBound(s.i,1))))))
  addTest(test2, test2Sk, erg2)


  // Test SYN993^1.p
  val test3 = |||(
    Exists(\(s.i)(Forall(\(s.i)(mkTermApp(q, List(mkBound(s.i,2),mkBound(s.i,1))))))),
    Exists(\(s.i)(Forall(\(s.i)(Not(mkTermApp(q, List(mkBound(s.i,1),mkBound(s.i,2))))))))
  )
  val test3Sk = Skolemization.normalize(termToClause(test3)).lits.head.term
  val erg3 = |||(
    Forall(\(s.i)(mkTermApp(q, List(mkAtom(s("SK5").key), mkBound(s.i,1))))),
    Forall(\(s.i)(Not(mkTermApp(q, List(mkBound(s.i,1),mkAtom(s("SK6").key))))))
  )
  addTest(test3,test3Sk,erg3)

  // SYN994^1.p
  val test4 = Exists(\(s.i)(
    Forall(\(s.i)(
      Forall(\(s.i)(
        ||| (
          mkTermApp(q, List(mkBound(s.i,2),mkBound(s.i,1))),
          Not(mkTermApp(q, List(mkBound(s.i,2),mkBound(s.i,3))))
        )
      ))
    ))
  ))

  val test4Sk = Skolemization.normalize(termToClause(test4)).lits.head.term
  val erg4 = Forall(\(s.i)(
    |||(
      Forall(\(s.i)(
        mkTermApp(q, List(mkBound(s.i,2),mkBound(s.i,1)))
      )),
      Not(mkTermApp(q,List(mkBound(s.i,1), mkAtom(s("SK7").key))))
    )
  ))
  addTest(test4,test4Sk,erg4)

  // SYN996^1.p
  // As in TPTP needs a NegationNormalForm first
  val test5 = Impl(
    Forall(\(s.i)(
      Exists(\(s.i)(
        mkTermApp(q, List(mkBound(s.i,2),mkBound(s.i,1)))
      ))
    )),
    Exists(\(s.i ->: s.i)(
      Forall(\(s.i)(
        mkTermApp(q, List(
          mkBound(s.i,1),
          mkTermApp(mkBound(s.i ->: s.i, 2), mkBound(s.i,1))
        ))
      ))
    ))
  )
  val test5Sk = Skolemization.normalize(NegationNormal.normalize(termToClause(test5))).lits.head.term
  val erg5 = |||(
    Forall(\(s.i)(
      Not(mkTermApp(q,List(mkAtom(s("SK8").key), mkBound(s.i,1))))
    )),
    Forall(\(s.i)(
      mkTermApp(q,List(
        mkBound(s.i,1),
        mkTermApp(mkAtom(s("SK9").key), mkBound(s.i,1))
      ))
    ))
  )
  addTest(test5,test5Sk,erg5)

  // SYN997^1.p
  val test6 = Exists(\((s.i ->: s.o) ->: s.i)(
    Forall(\(s.i ->: s.o)(
      Impl(
        Exists(\(s.i)(
          mkTermApp(
            mkBound(s.i ->: s.o, 2),
            mkBound(s.i,1)
          )
        )),
        mkTermApp(
          mkBound(s.i ->: s.o, 1),
          mkTermApp(
            mkBound((s.i ->: s.o) ->: s.i, 2),
            mkBound(s.i ->: s.o, 1)
          )
        )
      )
    ))
  ))
  val test6Sk = Skolemization.normalize(NegationNormal.normalize(termToClause(test6))).lits.head.term
  val erg6 = Forall(\(s.i ->: s.o)(
    |||(
      Forall(\(s.i)(
        Not(
          mkTermApp(mkBound(s.i ->: s.o,2),mkBound(s.i,1))
        )
      )),
      mkTermApp(
        mkBound(s.i ->: s.o, 1),
        mkTermApp(mkAtom(s("SK10").key),mkBound(s.i ->: s.o,1))
      )
    )
  ))
  addTest(test6,test6Sk,erg6)


  for ((t,(t1,t2)) <- toNorm){
    //    println("('"+t.pretty+"' , '"+t1.pretty+"')")
    //Signature.get.allConstants foreach {println(_)}
    test("Skolemization Test:"+t.pretty) {
      val st = t1
      println("Skolem: The Term '" + t.pretty + "' was skolemized to '" + st.pretty + "'.")
      assert(st == t2, "\nThe skolemized Term '" + t.pretty + "'\n   should be '" + t2.pretty + "',\n    but was '" + st.pretty + "'.")
    }
  }

  def termToClause(t : Term) : Clause = Clause.mkClause(List(Literal(t, true)),Derived)
}
