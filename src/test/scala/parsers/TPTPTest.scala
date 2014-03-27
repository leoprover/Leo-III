package parsers

/**
 * Created by lex on 3/25/14.
 */
object TPTPTest extends TPTPParser {
  def main(args: Array[String]) {
    // CNF
    println("#### Test on includeTest ####")
    runTestOn(includeTest)
    println("#### Test on col065_1 ####")
    runTestOn(col065_1)
    println("#### Test on col074_2 ####")
    runTestOn(col074_2)
    // FOF
    println("#### Test on mgt011p1 ####")
    runTestOn(mgt011p1)
  }

  def runTestOn(input: String) {
    val result = TPTP.parseFile(input)
      println("Includes:" + result.get.getIncludeCount)
      println(result.get.getIncludes)
      println("Formulae:" + result.get.getFormulaeCount)
      println(result.get.getFormulae)
      println("")
      println("Complete parse output:")
      println(result)
      println()
  }

  /// Inputs

  // CNF
  val includeTest: String =
    """
      |include('test')
      |include('test2',['asd','asd2'])
      |
      |cnf(b_definition,axiom,
      |    ( apply(apply(apply(b,X),Y),Z) = apply(X,apply(Y,Z)) )).
    """.stripMargin
  val col065_1: String =
    """
      |cnf(b_definition,axiom,
      |    ( apply(apply(apply(b,X),Y),Z) = apply(X,apply(Y,Z)) )).
      |
      |cnf(t_definition,axiom,
      |    ( apply(apply(t,X),Y) = apply(Y,X) )).
      |
      |cnf(prove_g_combinator,negated_conjecture,
      |    (  apply(apply(apply(apply(X,f(X)),g(X)),h(X)),i(X)) != apply(apply(f(X),i(X)),apply(g(X),h(X))) )).
    """.stripMargin

  val  col074_2: String =
    """
      |cnf(k_definition,negated_conjecture,
      |    ( apply(apply(k,X),Y) = X )).
      |
      |cnf(projection1,axiom,
      |    ( apply(projection1,pair(X,Y)) = X )).
      |
      |cnf(projection2,axiom,
      |    ( apply(projection2,pair(X,Y)) = Y )).
      |
      |cnf(pairing,axiom,
      |    ( pair(apply(projection1,X),apply(projection2,X)) = X )).
      |
      |cnf(pairwise_application,axiom,
      |    ( apply(pair(X,Y),Z) = pair(apply(X,Z),apply(Y,Z)) )).
      |
      |cnf(abstraction,negated_conjecture,
      |    ( apply(apply(apply(abstraction,X),Y),Z) = apply(apply(X,apply(k,Z)),apply(Y,Z)) )).
      |
      |cnf(equality,axiom,
      |    ( apply(eq,pair(X,X)) = projection1 )).
      |
      |cnf(extensionality1,axiom,
      |    ( X = Y
      |    | apply(eq,pair(X,Y)) = projection2 )).
      |
      |cnf(extensionality2,axiom,
      |    ( X = Y
      |    | apply(X,n(X,Y)) != apply(Y,n(X,Y)) )).
      |
      |cnf(different_projections,axiom,
      |    (  projection1 != projection2 )).
      |
      |cnf(diagonal_combinator,axiom,
      |    ( apply(apply(f,X),Y) = apply(X,X) )).
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
