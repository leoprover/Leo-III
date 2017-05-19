package leo.datastructures

import leo.LeoTestSuite
import leo.modules.termToClause
import leo.modules.parsers.Input
import leo.modules.calculus.{freshVarGenFromBlank, freshVarGen,FullCNF,BoolExt,LiftEq}

/**
  * Created by lex on 05.04.17.
  */
class LiteralOrderingTest extends LeoTestSuite {
  test("Test 1") {
    implicit val sig: Signature = getFreshSignature
    Input.readAnnotated("thf(a_type,type,(a: $tType )).")
    val (_,f,_) = Input.readAnnotated(
      """
        |thf(cTHM185_pme,conjecture,(
        |    ! [Xr: a > a > $o] :
        |      ( ? [Xx: a,Xy: a] :
        |          ( Xr @ Xx @ Xy )
        |    <=> ? [Xp: ( a > a > a ) > a] :
        |          ( Xr
        |          @ ( Xp
        |            @ ^ [Xx: a,Xy: a] : Xx )
        |          @ ( Xp
        |            @ ^ [Xx: a,Xy: a] : Xy ) ) ) )).
      """.stripMargin)
//    Out.finest(f.pretty(sig))
    Term.wellTyped(f)

    val problem = FullCNF(freshVarGenFromBlank, termToClause(f)).toSet
    assert(problem.size == 1)
    val clause = problem.head
    val (liftCa, posLiftLits, negLiftLits, nonLiftLits) = LiftEq.canApply(clause)
    assert(liftCa)
    val lifted = Clause(LiftEq.apply(posLiftLits, negLiftLits, nonLiftLits))
    val (ca, boolextLits,otherLits) = BoolExt.canApply(lifted)
    assert(ca)
    val resultBoolExt = BoolExt(boolextLits, otherLits)
    val ready = resultBoolExt.flatMap(cl => FullCNF(freshVarGen(cl),cl))
    Out.output(ready.map(_.pretty(sig)).mkString("\n"))
    assert(ready.size == 2)
    val cl1 = ready.head
    val cl2 = ready.tail.head
    assert(cl1.lits.size == 2)
    assert(cl2.lits.size == 2)
    val cl1lit1 = cl1.lits.head
    val cl1lit2 = cl1.lits.tail.head

    val cl2lit1 = cl2.lits.head
    val cl2lit2 = cl2.lits.tail.head

    println(s"Cl1 Lit1:\n${cl1lit1.pretty(sig)}")
    println(s"Cl1 Lit2:\n${cl1lit2.pretty(sig)}")
    println(s"CL1 fvs: ${cl1.implicitlyBound.map(_._1).mkString(",")}")
    println(s"CL1 Lit1 fvs: ${cl1lit1.fv.map(_._1).mkString(",")}")
    println(s"CL1 Lit2 fvs: ${cl1lit2.fv.map(_._1).mkString(",")}")
    if (cl1lit1.compare(cl1lit2) > 0) println("Lit 1 bigger")
    else if (cl1lit1.compare(cl1lit2) < 0) println("lit 2 bigger")
    else println("both equal")

    println(s"Cl2 Lit1:\n${cl2lit1.pretty(sig)}")
    println(s"Cl2 Lit2:\n${cl2lit2.pretty(sig)}")
    println(s"CL2 fvs: ${cl2.implicitlyBound.map(_._1).mkString(",")}")
    println(s"CL2 Lit1 fvs: ${cl2lit1.fv.map(_._1).mkString(",")}")
    println(s"CL2 Lit2 fvs: ${cl2lit2.fv.map(_._1).mkString(",")}")
    if (cl2lit1.compare(cl2lit2) > 0) println("Lit 1 bigger")
    else if (cl2lit1.compare(cl2lit2) < 0) println("lit 2 bigger")
    else println("both equal")
  }
}
