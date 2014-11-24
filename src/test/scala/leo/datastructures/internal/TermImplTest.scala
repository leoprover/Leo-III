package leo.datastructures.internal

import leo.datastructures.impl.Signature
import leo.datastructures.term.Term
import leo.modules.churchNumerals.Numerals
import leo.modules.churchNumerals.Numerals.fromInt
import Term.{mkTermApp => ap,mkAtom}
//import leo.datastructures.internal.{LitFalse, LitTrue, === => EQUALS, Signature}

/**
 * Created by lex on 05.08.14.
 */
object TermImplTest  {

  def main(args: Array[String]) {
    val sig = Signature.get

    Numerals() // include numerals in signature

    val add = mkAtom(sig("add").key)
    val mult = mkAtom(sig("mult").key)
    val power = mkAtom(sig("power").key)

//    val t = EQUALS(ap(power, Seq(fromInt(2),fromInt(3))), ap(mult, Seq(ap(mult, Seq(fromInt(2), fromInt(2))), fromInt(2))))
//
//    println(t.pretty)
//    val t2 = t.full_δ_expand
//    println(t2.pretty)
//
//    t2.betaNormalize match {
//      case EQUALS(a,b) => println(s"left: ${a.pretty} \n right: ${b.pretty}")
//        println(s"left: ${a.ty.pretty} \n right: ${b.ty.pretty}")
//                          println(a)
//        println(b)
//        println(fromInt(8))
//      case other => println(other.pretty)
//    }
//    println("#####################")

    println(ap(power,Seq(fromInt(2),fromInt(3))).full_δ_expand.betaNormalize)
//    println(ap(power,Seq(fromInt(2),fromInt(3))).ty.pretty)
//    println(ap(ap(mult,ap(ap(mult,2),2)),2).full_δ_expand.betaNormalize)
//    println("#########")
//    println(fromInt(8))
  }
}
