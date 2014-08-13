package leo.datastructures.internal.terms

import leo.datastructures.internal.Signature

/**
 * Created by lex on 13.08.14.
 */
object Asd {

  import leo.datastructures.internal.terms._
  import spine._
  def main(args: Array[String]) {
    val sig = Signature.get
    val q = sig.addUninterpreted("q", sig.o)

    val t: Term =  Root(Atom(12),App(Root(Atom(q),SNil),App(Root(Atom(10),App(Root(Atom(q),SNil),SNil)),SNil)))

  }
}
