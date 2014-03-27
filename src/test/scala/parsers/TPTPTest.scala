package parsers

/**
 * Created by lex on 3/25/14.
 */
object TPTPTest extends TPTPParser {
  def main(args: Array[String]) {
    println("#### Test on includeTest ####")
    runTestOn(includeTest)
    println("#### Test on col065_1 ####")
    runTestOn(col065_1)
    println("#### Test on col074_2 ####")
    runTestOn(col074_2)
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
}
