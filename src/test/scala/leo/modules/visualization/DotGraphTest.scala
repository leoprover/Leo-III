package leo.modules.visualization

import leo.datastructures.internal.Signature
import leo.modules.churchNumerals.Numerals
import leo.datastructures.internal.terms.Term._
import leo.modules.churchNumerals.Numerals._

/**
 * Created by lex on 11.09.14.
 */
object DotGraphTest {
  def main(args: Array[String]) {

    val sig = Signature.get

    Numerals()
    val mult = sig("mult").key
    val term = mkTermApp(mkAtom(mult), Seq(fromInt(3), fromInt(4)))

    val g = DotGraph.apply(term)

    println(g.toString())
  }
}
