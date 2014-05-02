package datastructures.internal

/**
 * Created by lex on 02.05.14.
 */
object HOLSignatureTest extends HOLSignature {
    def main(args: Array[String]) {
      definedConsts.map(f).foreach(println(_))
      println("#######")
      fixedConsts.map(g).foreach(println(_))
    }

    def f(in: (String,Term, Type)): (String, String) =(in._1,in._3.pretty)
    def g(in: (String,Type)): (String, String) =(in._1,in._2.pretty)

}
