package datastructures.internal
import scala.language.implicitConversions

/**
 * Created by lex on 21.05.14.
 */
object TermTest {
  def main (args: Array[String]) {
    val sig = Signature.get
    Signature.withHOL(sig)
    val B = sig.addUninterpreted("B", Type.i)

    val t1 = Term.mkTermAbs(Type.i, Term.mkTermApp((1,Type.i), (2, Type.i)))

    val t2 = Term.mkTermAbs(Type.i, Term.mkTermApp(t1, Term.mkAtom(B)))


    val neuB = Term.mkTermAbs(Type.i, Term.mkTermApp((2,Type.i), (1, Type.i)))
    val t3 = Term.mkTermAbs(Type.i, Term.mkTermApp(t1, neuB))

    val t1neu = Term.mkTermAbs(Type.i, Term.mkTermApp((1,Type.i), Term.mkTermAbs(Type.i, (1,Type.i))))
    val t4 = Term.mkTermAbs(Type.i, Term.mkTermApp(t1neu, neuB))

    println(t4.pretty)
    println(t4.betaNormalize.pretty)
    println(t4.betaNormalize.betaNormalize.pretty)

  }


  implicit def intToBoundVar(in: (Int, Type)): Term = Term.mkBound(in._2,in._1)
}
