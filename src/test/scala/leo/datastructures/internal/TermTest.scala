package leo.datastructures.internal

import scala.language.implicitConversions
import Term.{mkTermAbs, mkTermApp, mkBound}

/**
 * Created by lex on 21.05.14.
 */
object TermTest {
  def main (args: Array[String]) {
    val sig = Signature.get

//    val B = sig.addUninterpreted("B", Type.i)
//
    val t1 = mkTermAbs(sig.i, mkTermApp((1,sig.i), (2, sig.i)))
//
//    val t2 = mkTermAbs(Type.i, mkTermApp(t1, mkAtom(B)))


    val neuB = mkTermAbs(sig.i, mkTermApp((2,sig.i), (1, sig.i)))
    val t3 = mkTermAbs(sig.i, mkTermApp(t1, neuB))

    val t1neu = mkTermAbs(sig.i, mkTermApp((2,sig.i), mkTermAbs(sig.i, (1,sig.i))))
    val t4 = mkTermAbs(sig.i, mkTermApp(t1neu, neuB))

    println(t4.pretty)
    println(t4.betaNormalize.pretty)
    println(t4.betaNormalize.betaNormalize.pretty)
    println(t4.betaNormalize.betaNormalize.betaNormalize.pretty)

  }


  implicit def intToBoundVar(in: (Int, Type)): Term = mkBound(in._2,in._1)
}