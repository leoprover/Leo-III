package leo.modules.parsers

import leo.modules.parsers.syntactical.TPTPParsers
import leo.datastructures.internal.Signature

/**
 * Created by lex on 01.07.14.
 */
object ParseAndProcess {
  private val parser = TPTPParsers
  private val p = InputProcessing

  def main(args: Array[String]) {
    val sig = Signature.get
    val erg = TPTP.parseFile(a)
    erg match {
      case Left(err) => println(err)
      case Right(res) => {
        println("parsed result:")
        println(res.toString)
        println("#########################")
        val temp = p.processAll(sig)(res.getFormulae)
        for (t <- temp) {
          println(t._1 + ":\t\t" +  t._2.pretty + " as " + t._3)
        }
        println("#########################")
        for (b <- sig.allConstants) {
          print(sig(b).key.toString + "\t\t")
          print(sig(b).name + "\t\t:\t")
          sig(b).ty.foreach({ case ty => print(ty.pretty)})
          sig(b).kind.foreach({ case ty => print(ty.pretty)})
          println()
        }
        println("#########################")
      }
    }
  }

  // THF
  val a: String =
    """
      |thf(p_int_type,type,(
      |    p_int: $int > $o )).
      |
      |thf(p_rat_type,type,(
      |    p_rat: $rat > $o )).
      |
      |thf(p_real_type,type,(
      |    p_real: $real > $o )).
      |
    """.stripMargin


  // FOF
  val mgt011p1 : String =
    """
      |fof(mp5,axiom,
      |    ( ! [X,T] :
      |        ( organization(X,T)
      |       => ? [I] : inertia(X,I,T) ) )).
      |
      |fof(mp6_1,axiom,
      |    ( ! [X,Y] : ~ ( greater(X,Y)
      |        & X = Y ) )).
      |
      |fof(mp6_2,axiom,
      |    ( ! [X,Y] : ~ ( greater(X,Y)
      |        & greater(Y,X) ) )).
      |
      |fof(mp9,axiom,
      |    ( ! [X,T] :
      |        ( organization(X,T)
      |       => ? [C] : class(X,C,T) ) )).
      |
      |fof(mp10,axiom,
      |    ( ! [X,T1,T2,C1,C2] :
      |        ( ( organization(X,T1)
      |          & organization(X,T2)
      |          & reorganization_free(X,T1,T2)
      |          & class(X,C1,T1)
      |          & class(X,C2,T2) )
      |       => C1 = C2 ) )).
      |
      |fof(a5_FOL,hypothesis,
      |    ( ! [X,Y,C,S1,S2,I1,I2,T1,T2] :
      |        ( ( organization(X,T1)
      |          & organization(Y,T2)
      |          & class(X,C,T1)
      |          & class(Y,C,T2)
      |          & size(X,S1,T1)
      |          & size(Y,S2,T2)
      |          & inertia(X,I1,T1)
      |          & inertia(Y,I2,T2)
      |          & greater(S2,S1) )
      |       => greater(I2,I1) ) )).
      |
      |fof(t2_FOL,hypothesis,
      |    ( ! [X,I1,I2,T1,T2] :
      |        ( ( organization(X,T1)
      |          & organization(X,T2)
      |          & reorganization_free(X,T1,T2)
      |          & inertia(X,I1,T1)
      |          & inertia(X,I2,T2)
      |          & greater(T2,T1) )
      |       => greater(I2,I1) ) )).
      |
      |fof(t11_FOL,conjecture,
      |    ( ! [X,S1,S2,T1,T2] :
      |        ( ( organization(X,T1)
      |          & organization(X,T2)
      |          & reorganization_free(X,T1,T2)
      |          & size(X,S1,T1)
      |          & size(X,S2,T2)
      |          & greater(T2,T1) )
      |       => ~ greater(S1,S2) ) )).
    """.stripMargin
}
