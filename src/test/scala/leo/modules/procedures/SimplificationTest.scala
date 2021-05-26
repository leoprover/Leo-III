package leo.modules.procedures

import leo.datastructures.Term._
import leo.datastructures._
import leo.modules.HOLSignature._
import leo.modules.input.Input.{readFormula => read}
import leo.{Checked, LeoTestSuite}

class SimplificationTest extends LeoTestSuite {
  implicit private val s: Signature = getFreshSignature

  mkAtom(s.addUninterpreted("p", o))
  mkAtom(s.addUninterpreted("q", o ->: o))
  mkAtom(s.addUninterpreted("r", i))

  private val tests : Map[Term,Term] = Map[Term, Term](
    (read("~$true"), read("$false")),
    (read("~$false"), read("$true")),
    (read("p & p"), read("p")),
    (read("p & ~p"), read("$false")),
    (read("p <=> p"), read("$true")),
    (read("p & $true"), read("p")),
    (read("p & $false"), read("$false")),
    (read("p => $true"), read("$true")),
    (read("p => $false"), read("~p")),
    (read("p <=> $true"), read("p")),
    (read("p <=> $false"), read("~p")),
    (read("p | p"), read("p")),
    (read("p | ~p"), read("$true")),
    (read("p => p"), read("$true")),
    (read("p | $true"), read("$true")),
    (read("p | $false"), read("p")),
    (read("$true => p"), read("p")),
    (read("$false => p"), read("$true")),
    (read("![X:$o]: p"), read("p")),
    (read("?[X:$o]: p"), read("p")),
    (read("![X:$o]: ((q @ X) <=> (q @ X))"), read("$true")),
    (read("! [X:$i]: (r = r)"), read("$true")),
    (read("! [X:$i]: (X = X)"), read("$true")),
    (read("! [X:$tType]: (r = r)"), read("$true")),
    (read("2"), read("2")),
    (read("1/2"), read("1/2")),
    (read("2/4"), read("1/2")),
    (read("2/2"), read("1/1"))
  )

  for ((input,expected) <- tests){
    test(s"Simplification Test: ${input.pretty(s)}", Checked) {
      val simp = Simplification.apply(input) //Simp.normalize(t)
      if (simp != expected) fail(s"The simplified Term '${input.pretty(s)}' should be '${expected.pretty(s)}', but was '${simp.pretty(s)}'.")
    }
  }
}
