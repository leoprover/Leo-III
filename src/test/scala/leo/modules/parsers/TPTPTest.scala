package leo.modules.parsers


import syntactical.TPTPParsers
import java.io.File
import scala.io.Source

import scala.util.parsing.input.CharArrayReader
import scala.util.parsing.input.Reader

/**
 * Created by lex on 3/25/14.
 */
object TPTPTest {
  private val parser = TPTPParsers
  def main(args: Array[String]) {
//    testruns
    fileTests
  }

  def testruns() {
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

    // THF
    println("#### Test on syn000power2 ####")
    runTestOn(syn000power2)

    // TFF
    println("#### Test on ari022Eq1 ####")
    runTestOn(ari022Eq1)

    println("#### Test on ari175Eq1 ####")
    runTestOn(ari175Eq1)
  }

  def fileTest(file: String) {
    val source = Source.fromFile(file, "utf-8")
    lazy val input = new CharArrayReader(source.toArray)

    println(parser.parse(input, parser.tptpFile))
  }

  def fileTests() {
    val files = new File("./tptp/syn").listFiles.filter(_.getName.endsWith(".p"))
    for (f <- files) {
      val source = Source.fromFile(f, "utf-8")
      lazy val input = new CharArrayReader(source.toArray)
      val parsed = TPTP.parseFile(input)
      println("Result of " + f.getName + ": "+ parsed)
    }
  }

  def tokensOf(input: Reader[Char]) {
    tokensOf(input.source.toString)
  }
  def tokensOf(input: String) {
    var tokenstream = parser.tokens(input)

        while (!tokenstream.atEnd) {
          println(tokenstream.first)
          tokenstream = tokenstream.rest
        }

  }

  def runTestOn(input: String) {
    val result = TPTP.parseFile(input)
    result match {
      case Left(x) => println("Error:" + x)
      case Right(x) => {
        println("Includes:" + x.getIncludeCount)
        println(x.getIncludes)
        println("Formulae:" + x.getFormulaeCount)
        println(x.getFormulae)
        println("")
        println("Complete parse output:")
        println(x)
        println()
      }
    }
  }

  /// Inputs

  // CNF
  val includeTest: String =
    """
      |include('test').
      |include('test2',['asd','asd2']).
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


  // THF
  val syn000power2: String =
    """
      |%----Quoted symbols
      |thf(distinct_object,axiom,(
      |    "An Apple" != "A \"Microsoft \\ escape\"" )).
      |
      |%----Numbers
      |thf(p_int_type,type,(
      |    p_int: $int > $o )).
      |
      |thf(p_rat_type,type,(
      |    p_rat: $rat > $o )).
      |
      |thf(p_real_type,type,(
      |    p_real: $real > $o )).
      |
      |thf(integers,axiom,
      |    ( ( p_int @ 123 )
      |    | ( p_int @ -123 ) )).
      |
      |thf(rationals,axiom,
      |    ( ( p_rat @ 123/456 )
      |    | ( p_rat @ -123/456 )
      |    | ( p_rat @ +123/456 ) )).
      |
      |thf(reals,axiom,
      |    ( ( p_real @ 123.456 )
      |    | ( p_real @ -123.456 )
      |    | ( p_real @ 123.456E789 )
      |    | ( p_real @ 123.456e789 )
      |    | ( p_real @ -123.456E789 )
      |    | ( p_real @ 123.456E-789 )
      |    | ( p_real @ -123.456E-789 ) )).
    """.stripMargin

  val ari022Eq1: String =
    """
      |tff(n4_lesseq_n2,conjecture,(
      |    $lesseq(-4,-2) )).
    """.stripMargin

  val ari175Eq1: String =
    """
      |tff(co1,conjecture,(
      |    ? [U: $int,V: $int] : $sum($product(3,U),$product(5,V)) = 23 )).
    """.stripMargin


  val tptptest: String =
    """
      |
      |%----Propositional
      |fof(propositional,axiom,
      |    ( ( p0
      |      & ~ q0 )
      |   => ( r0
      |      | ~ s0 ) )).
      |
      |%----First-order
      |fof(first_order,axiom,(
      |    ! [X] :
      |      ( ( p(X)
      |        | ~ q(X,a) )
      |     => ? [Y,Z] :
      |          ( r(X,f(Y),g(X,f(Y),Z))
      |          & ~ s(f(f(f(b)))) ) ) )).
      |
      |%----Equality
      |fof(equality,axiom,(
      |    ? [Y] :
      |    ! [X,Z] :
      |      ( f(Y) = g(X,f(Y),Z)
      |      | f(f(f(b))) != a
      |      | X = f(Y) ) )).
      |
      |%----True and false
      |fof(true_false,axiom,
      |    ( $true
      |    | $false )).
      |
      |%----Quoted symbols
      |fof(single_quoted,axiom,
      |    ( 'A proposition'
      |    | 'A predicate'(a)
      |    | p('A constant')
      |    | p('A function'(a))
      |    | p('A \'quoted \\ escape\'') )).
      |
      |%----Connectives - seen |, &, =>, ~ already
      |fof(useful_connectives,axiom,(
      |    ! [X] :
      |      ( ( p(X)
      |       <= ~ q(X,a) )
      |    <=> ? [Y,Z] :
      |          ( r(X,f(Y),g(X,f(Y),Z))
      |        <~> ~ s(f(f(f(b)))) ) ) )).
      |
      |%----Annotated formula names
      |fof(123,axiom,(
      |    ! [X] :
      |      ( ( p(X)
      |        | ~ q(X,a) )
      |     => ? [Y,Z] :
      |          ( r(X,f(Y),g(X,f(Y),Z))
      |          & ~ s(f(f(f(b)))) ) ) )).
      |
      |%----Roles
      |fof(role_hypothesis,hypothesis,(
      |    p(h) )).
      |
      |fof(role_conjecture,conjecture,(
      |    ? [X] : p(X) )).
      |
      |include('testdir/test2.tptp').
      |
    """.stripMargin
}

