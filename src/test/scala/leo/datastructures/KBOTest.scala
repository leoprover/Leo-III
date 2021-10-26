package leo.datastructures.impl.orderings

import leo.datastructures.{Term, Type}
import Type._
import leo.datastructures.Term._
import leo.datastructures.Signature
import leo.datastructures.Signature.Key
import leo.modules.HOLSignature.i
import leo.modules.input.Input
import leo.datastructures.{Orderings, Precedence}
import leo.modules.calculus.freshVarGenFromBlank

object KBOTest {
  def main(args: Array[String]): Unit = {
    implicit val sig: Signature = Signature.freshWithHOL()
    val f1 = mkAtom(sig.addUninterpreted("f1", i ->: i))
    val f2 = mkAtom(sig.addUninterpreted("f2", i ->: i ->: i))
    val f3 = mkAtom(sig.addUninterpreted("f3", (i ->: i) ->: i))
    val f4 = mkAtom(sig.addUninterpreted("f4", (i ->: i ->: i) ->: i))
    val f5 = mkAtom(sig.addUninterpreted("f5", (i ->: i) ->: (i ->: i) ->: i))
    val f6 = mkAtom(sig.addUninterpreted("f6", (i ->: i ->: i) ->: (i ->: i) ->: i))
    val f7 = mkAtom(sig.addUninterpreted("f7", ((i ->: i) ->: i) ->: i))
    val c = mkAtom(sig.addUninterpreted("c", i))
    val t = Input.readFormula("f1 @ c")

    val simpleweight = new SymbolWeighting {
      override def apply(symbol: Key)(implicit sig: Signature): Int = sig(symbol).key
    }
    val ordering = new KBOTermOrdering(simpleweight,Precedence.apply().arity_UnaryFirst)

    def compare(s: String, t: String): Unit = {
      println(s"compare($s,$t)",Orderings.pretty(ordering.compare(Input.readFormula(s), Input.readFormula(t))))
    }
    def compareTerms(s: Term, t: Term): Unit = {
      println(s"compare(${s.pretty(sig)},${t.pretty(sig)})",Orderings.pretty(ordering.compare(s,t)))
    }

    println("f1", ordering.termWeight(Input.readFormula("f1")))
    println("c", ordering.termWeight(Input.readFormula("c")))
    println("f1 @ c", ordering.termWeight(Input.readFormula("f1 @ c")))

    println("f2", ordering.termWeight(Input.readFormula("f2")))
    println("c", ordering.termWeight(Input.readFormula("c")))
    println("f2 @ c", ordering.termWeight(Input.readFormula("f2 @ c")))
    println("f2 @ c @ c", ordering.termWeight(Input.readFormula("f2 @ c @ c")))


    println("f3", ordering.termWeight(Input.readFormula("f3")))
    println("f1", ordering.termWeight(Input.readFormula("f1")))
    println("f3 @ f1", ordering.termWeight(Input.readFormula("f3 @ f1")))

    println("f5", ordering.termWeight(Input.readFormula("f5")))
    println("f1", ordering.termWeight(Input.readFormula("f1")))
    println("f5 @ f1", ordering.termWeight(Input.readFormula("f5 @ f1")))
    println("f5 @ f1 @ f1", ordering.termWeight(Input.readFormula("f5 @ f1 @ f1")))

    println("f4", ordering.termWeight(Input.readFormula("f4")))
    println("f2", ordering.termWeight(Input.readFormula("f2")))
    println("f4 @ f2", ordering.termWeight(Input.readFormula("f4 @ f2")))

    println("f7", ordering.termWeight(Input.readFormula("f7")))
    println("f3", ordering.termWeight(Input.readFormula("f3")))
    println("f7 @ f3", ordering.termWeight(Input.readFormula("f7 @ f3")))

    println(Orderings.pretty(ordering.compare(Input.readFormula("f7 @ f3"), Input.readFormula("f4 @ f2"))))
    println(Orderings.pretty(ordering.compare(Input.readFormula("f7 @ f3"), Input.readFormula("f7 @ f3"))))
    println(Orderings.pretty(ordering.compare(Input.readFormula("f7 @ f3"), Input.readFormula("f3"))))
    compare("f7 @ f3", "f3")
    compare("f5 @ f1 @ f1", "f1")
    compare("f1", "f5 @ f1 @ f1")

    val list = Type.mkType(sig.addBaseType("list"))

    val vargen = freshVarGenFromBlank
    val F = vargen(i ->: i)
    val X = vargen(i)
    val XS = vargen(list)
    val nil = mkAtom(sig.addUninterpreted("nil", list))
    val cons = mkAtom(sig.addUninterpreted("cons", i ->: list ->: list))

    val map = mkAtom(sig.addUninterpreted("map", (i ->: i) ->: list ->: list))
    val eq1 = (mkTermApp(map, Seq(F,nil)), nil)
    val eq2 = (mkTermApp(map, Seq(F,mkTermApp(cons, Seq(X,XS)))), mkTermApp(cons, Seq(mkTermApp(F, Seq(X)), mkTermApp(map, Seq(F, XS)))))
    assert(wellTyped(eq1._1)); assert(wellTyped(eq1._2))
    assert(wellTyped(eq2._1), "eq21"); assert(wellTyped(eq2._2), "eq22")
    println(ordering.termWeight(eq1._1))
    println(ordering.termWeight(eq1._2))
    compareTerms(eq1._1, eq1._2)
    compareTerms(eq2._1, eq2._2)

    compare("f1", "^[X:$i]: (f1 @ X)")
  }


}
