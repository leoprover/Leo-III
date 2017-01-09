package leo.datastructures

import leo.{Checked, LeoTestSuite}
import leo.modules.HOLSignature.{i,o}
import Term.{mkBound, mkAtom, λ, mkTermApp}

/**
  * Created by lex on 12.12.16.
  */
class TermTest  extends  LeoTestSuite {
  test("Occurrences of λx. f x Y (λz. g x).", Checked) {
    implicit val sig = getFreshSignature
    val f = mkAtom(sig.addUninterpreted("f", i ->: i->: (i ->: i) ->: i))
    val g = mkAtom(sig.addUninterpreted("g", i ->: i))

    val t = λ(i)(
      mkTermApp(f, Seq(mkBound(i, 1), mkBound(i, 2), λ(i)(mkTermApp(g, mkBound(i, 2)))))
    ).betaNormalize

    assert(Term.wellTyped(t))
    println(t.pretty(sig))

    val occ = t.feasibleOccurrences
    // occurrences should be:
    // 1. whole term, 2. f and g symbols
    // 3. the free var Y
    // 4. nothing else.
    println("\t"+occ.map(oc => oc._1.pretty(sig) + " (at " + oc._2.map(_.pretty).mkString("+") +  ")").mkString("\n\t"))
    assert(occ.keys.size == 4)
  }
}
