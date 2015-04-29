package leo.datastructures

import scala.language.implicitConversions

import leo.LeoTestSuite

/**
 * Created by lex on 23.04.15.
 */
class TermTestSuite extends LeoTestSuite {


  // Meta variable instantiation test
  import Term.{λ, intToBoundVar, mkMetaVar}
  import impl.Signature

  val sig = Signature.get
  val o = sig.o
  val i = sig.i
  val metaVarInstTestTerms = Map(
    1 -> λ(o)(|||((1,o), mkMetaVar(o, 1))).betaNormalize,
    2 -> &(Forall(λ(o)(|||((1,o),Not(mkMetaVar(o,1))))), mkMetaVar(o,2)).betaNormalize
  )

  val metaVarInstTestSubst = Map(
    1 -> Subst.id,
    2 -> (TermFront(LitTrue) +: Subst.id),
    3 -> (TermFront(LitFalse) +: TermFront(LitTrue) +: Subst.id),
    4 -> (BoundFront(1) +: TermFront(LitTrue) +: Subst.id),
    5 -> (BoundFront(2) +: TermFront(LitTrue) +: Subst.id)
  )

  val metaVarInstRes = Map(
    1 -> Map(
      1 -> λ(o)(|||((1,o), mkMetaVar(o, 1))).betaNormalize,
      2 -> λ(o)(|||((1,o), LitTrue)).betaNormalize,
      3 -> λ(o)(|||((1,o), LitFalse)).betaNormalize,
      4 -> λ(o)(|||((1,o), mkMetaVar(o, 1))).betaNormalize,
      5 -> λ(o)(|||((1,o), mkMetaVar(o, 2))).betaNormalize
    ),
    2 -> Map(
      1 -> &(Forall(λ(o)(|||((1,o),Not(mkMetaVar(o,1))))), mkMetaVar(o,2)).betaNormalize,
      2 -> &(Forall(λ(o)(|||((1,o),Not(LitTrue)))), mkMetaVar(o,2)).betaNormalize,
      3 -> &(Forall(λ(o)(|||((1,o),Not(LitFalse)))), LitTrue).betaNormalize,
      4 -> &(Forall(λ(o)(|||((1,o),Not(mkMetaVar(o,1))))), LitTrue).betaNormalize,
      5 -> &(Forall(λ(o)(|||((1,o),Not(mkMetaVar(o,2))))), LitTrue).betaNormalize
    )
  )

  for (t <- metaVarInstTestTerms) {
    for (s <- metaVarInstTestSubst) {
      test(s"Meta-variable instantiation test ${t._1}/${s._1}") {
        val term = t._2
        val subst = s._2
        println(s"Instantiate ${term.pretty} with ${subst.pretty}")
        val inst = term.substitute(subst).betaNormalize
        assertResult(metaVarInstRes(t._1)(s._1))(inst)
      }
    }
  }

}
