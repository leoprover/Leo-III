package leo.modules.parsers.syntactical_new.termParser2

import leo.datastructures.tptp.Commons._
import leo.modules.parsers.lexical.TPTPLexical
import leo.modules.parsers.ParserInterface

object TermParser2
  extends TermParser2
  with ParserInterface[Term]
{
  def parse(input: String): Either[ParserError,(Term, Seq[Token])] =
    parseTerm(input)

  def parse(tokens: Seq[Token]): Either[ParserError,(Term, Seq[Token])] =
    parseTerm(tokens)

}

/**
  * Created by samuel on 10.03.16.
  */
class TermParser2
  //extends TPTPLexical
  //extends ParserInterface[Term]
{
  val lexical = new TPTPLexical

  type Token = lexical.Token

  import lexical._

  // internally used datatypes:
  private object Private {

    /* this is the type of the nodes of the abstract syntax tree: */
    sealed class StackEntry
    case class TokenEntry(data: Token) extends StackEntry
    case class TermEntry(data: Term) extends StackEntry
    case class NumberEntry(data: Number) extends StackEntry
    case class TermEntries(data: List[Term]) extends StackEntry

    type PStack = List[StackEntry]
    //type PP = Parser[Token, PStack]

    abstract sealed class RHS
    case class RHSAction(value: PStack => PStack) extends RHS
    case class RHSZSymbol(value: ZSymbol) extends RHS
    case class RHSMatch(value: ClassId) extends RHS

    type ZSymbol = Symbol
    //type ClassId = String
    type ClassId = Class[_]

    type ParseTableType = Map[(ZSymbol, Option[ClassId]), Seq[RHS]]

    lazy val rulesMap: ParseTableType = initMap

  }
  type ParserError = String
  type TokenStream[T] = Seq[T]

  import Private._

  import scala.reflect.ClassTag

  def tokenize(input: String): Seq[Token] = {
    var scanner = new lexical.Scanner(input)
    var tokStream: Seq[Token] = List[Token]()
    while(!scanner.atEnd) {
      tokStream = tokStream :+ scanner.first
      //tokStream = tokStream :+ (scanner.first.asInstanceOf[Token])
      scanner = scanner.rest
    }
    tokStream
  }

  def parseTerm(input: String): Either[ParserError,(Term, Seq[Token])] = {
    parseTerm(
      tokenize(input)
    )
  }

  def parseTerm(tokens: Seq[Token]): Either[ParserError,(Term, Seq[Token])] =
    zParser('z0, List.empty,tokens).right flatMap {
      case (TermEntry(term) :: Nil, restTokens) =>
        Right((term, restTokens))
      case (s, _) =>
        Left(s"Stack is not empty: ${s}")
    }

  private def zParser(currentState: ZSymbol, stack0: PStack, input0: TokenStream[Token]): Either[ParserError, (PStack,TokenStream[Token])] = {
    //println(currentState)
    //println(s"\tstack: ${stack0}\n\t input: ${input0}")
    var stack = stack0
    var input = input0
    val lookupRet: Option[Seq[RHS]] = input match {
      case sym :: rest =>
        rulesMap.get((currentState, Some(sym.getClass))) match {
          case None =>
            rulesMap.get((currentState, None))
          case Some(x) =>
            stack = TokenEntry(sym) :: stack
            input = rest
            Some(x)
        }
      case Nil =>
        rulesMap.get((currentState, None))
    }
    lookupRet match {
      case None => return Left("lookup failed")
      case Some(rule) =>
        for( ruleEntry <- rule ) {
          ruleEntry match {
            case RHSAction( action )
              => stack = action(stack)
            case RHSZSymbol( zSymbol )
              =>
                zParser( zSymbol, stack, input ) match {
                  case Left(err) => return Left(err)
                  case Right((s, rest)) =>
                    stack = s; input = rest
                }
            case RHSMatch( tokClassId )
              => input match {
                case head :: tail if head.getClass == tokClassId
                  =>
                    stack = TokenEntry(head) :: stack
                    input = tail
                case head :: _
                  => return Left(s"${tokClassId} expected but ${head} found!")
              }
          }
        }
    }
    Right(stack,input)
  }

  private def initMap: ParseTableType = {
    import Actions._
    implicit def act(f: (PStack) => PStack): RHSAction = RHSAction(f)
    implicit def toRHSMatch(x: Class[_]): RHSMatch = RHSMatch(x)
    implicit def toRHSZSymbol(x: ZSymbol): RHSZSymbol = RHSZSymbol(x)

    def terminalEntry[T <: Token](implicit ct: ClassTag[T]): Class[_] =
      ct.runtimeClass

    Map(

      /*
      Z0 ->
        "upper_word"  action_2
        |"single_quoted"   Z12
        |"real" action_20 action_16
        |"rational" action_19 action_16
        |"lower_word"   Z12
        |"integer" action_18 action_16
        |"dollar_word"   Z14
        |"dollar_dollar_word"   Z15
        |"distinct_object" action_17
      */
      ('z0, Some(terminalEntry[UpperWord])) -> Seq(action_2 _),
      ('z0, Some(terminalEntry[SingleQuoted])) -> Seq('z12),
      ('z0, Some(terminalEntry[Real])) -> Seq( action_20 _, action_16 _),
      ('z0, Some(terminalEntry[Rational])) -> Seq( action_19 _, action_16 _),
      ('z0, Some(terminalEntry[LowerWord])) -> Seq('z12),
      ('z0, Some(terminalEntry[Integer])) -> Seq(action_18 _, action_16 _),
      ('z0, Some(terminalEntry[DollarWord])) -> Seq('z14),
      ('z0, Some(terminalEntry[DollarDollarWord])) -> Seq('z15),
      ('z0, Some(terminalEntry[DistinctObject])) -> Seq( action_17 _),

      /*
      Z12 ->
          action_6
        |"(" Z2
      */
        ('z12, None) -> Seq(action_6 _),
        ('z12, Some(terminalEntry[LeftParenthesis.type])) -> Seq('z2),

      /*
      Z2 ->
         "upper_word"  action_2 Z16
        |"single_quoted"   Z17
        |"real" action_20 action_16    Z16
        |"rational" action_19 action_16    Z16
        |"lower_word"   Z17
        |"integer" action_18 action_16    Z16
        |"dollar_word"   Z22
        |"dollar_dollar_word"   Z23
        |"distinct_object" action_17    Z16
      */
      ('z2, Some(terminalEntry[UpperWord])) -> Seq(action_2 _ ,'z16),
      ('z2, Some(terminalEntry[SingleQuoted])) -> Seq(   'z17),
      ('z2, Some(terminalEntry[Real])) -> Seq( action_20 _ ,action_16 _ ,   'z16),
      ('z2, Some(terminalEntry[Rational])) -> Seq( action_19 _ ,action_16 _ ,   'z16),
      ('z2, Some(terminalEntry[LowerWord])) -> Seq(     'z17),
      ('z2, Some(terminalEntry[Integer])) -> Seq( action_18 _ ,action_16 _ ,   'z16),
      ('z2, Some(terminalEntry[DollarWord])) -> Seq(     'z22),
      ('z2, Some(terminalEntry[DollarDollarWord])) -> Seq(     'z23),
      ('z2, Some(terminalEntry[DistinctObject])) -> Seq( action_17 _ ,   'z16),

      /*
      Z16 ->
         action_12 Z3
        "," Z5 Z3
      */
      ('z16, None) -> Seq( action_12 _, 'z3),
      ('z16, Some(terminalEntry[Comma.type])) -> Seq(   'z5 ,'z3),

      /*
      Z3 ->
         ")" action_7
      */
      ('z3 , Some(terminalEntry[RightParenthesis.type])) -> Seq(   action_7 _),

      /*
      Z5 ->
         "upper_word"  action_2 z67
        "single_quoted"   z68
        "real" action_20 action_16    z67
        "rational" action_19 action_16    z67
        "lower_word"   z68
        "integer" action_18 action_16    z67
        "dollar_word"   z73
        "dollar_dollar_word"   z74
        "distinct_object" action_17    z67
      */
      ('z5 , Some(terminalEntry[UpperWord])) -> Seq(  action_2 _ ,'z67),
      ('z5 , Some(terminalEntry[SingleQuoted])) -> Seq(     'z68),
      ('z5 , Some(terminalEntry[Real])) -> Seq( action_20 _ ,action_16 _ ,   'z67),
      ('z5 , Some(terminalEntry[Rational])) -> Seq( action_19 _ ,action_16 _ ,   'z67),
      ('z5 , Some(terminalEntry[LowerWord])) -> Seq(     'z68),
      ('z5 , Some(terminalEntry[Integer])) -> Seq( action_18 _ ,action_16 _ ,   'z67),
      ('z5 , Some(terminalEntry[DollarWord])) -> Seq(     'z73),
      ('z5 , Some(terminalEntry[DollarDollarWord])) -> Seq(     'z74),
      ('z5 , Some(terminalEntry[DistinctObject])) -> Seq( action_17 _ ,   'z67),

      /*
      Z67 ->
         action_12 action_13
        "," Z5 action_13
      */
      ('z67, None) -> Seq(action_12 _ ,action_13 _),
      ('z67 , Some(terminalEntry[Comma.type])) -> Seq( 'z5 ,action_13 _),

      /*
      Z68 ->
          action_6   Z67
        "(" Z2   Z67
      */
      ('z68, None) -> Seq( action_6 _ ,  'z67),
      ('z68 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z2 ,  'z67),

      /*
      Z73 ->
          action_22     Z67
        "(" Z7     Z67
      */
      ('z73, None) -> Seq( action_22 _ ,    'z67),
      ('z73 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z7 ,    'z67),

      /*
      Z7 ->
         "upper_word"  action_2 Z33
        "single_quoted"   Z34
        "real" action_20 action_16    Z33
        "rational" action_19 action_16    Z33
        "lower_word"   Z34
        "integer" action_18 action_16    Z33
        "dollar_word"   Z39
        "dollar_dollar_word"   Z40
        "distinct_object" action_17    Z33
      */
      ('z7 , Some(terminalEntry[UpperWord])) -> Seq(  action_2 _ ,'z33),
      ('z7 , Some(terminalEntry[SingleQuoted])) -> Seq(     'z34),
      ('z7 , Some(terminalEntry[Real])) -> Seq( action_20 _ ,action_16 _ ,   'z33),
      ('z7 , Some(terminalEntry[Rational])) -> Seq( action_19 _ ,action_16 _ ,   'z33),
      ('z7 , Some(terminalEntry[LowerWord])) -> Seq(     'z34),
      ('z7 , Some(terminalEntry[Integer])) -> Seq( action_18 _ ,action_16 _ ,   'z33),
      ('z7 , Some(terminalEntry[DollarWord])) -> Seq(     'z39),
      ('z7 , Some(terminalEntry[DollarDollarWord])) -> Seq(     'z40),

      /*
      Z33 ->
         action_12 Z8
        "," Z5 Z8
      */
      ('z33, None) -> Seq( action_12 _ ,'z8),
      ('z33 , Some(terminalEntry[Comma.type])) -> Seq(   'z5 ,'z8),

      /*
      Z8 ->
         ")" action_23
      */
      ('z8 , Some(terminalEntry[RightParenthesis.type])) -> Seq(   action_23 _),

      /*
      Z34 ->
          action_6   Z33
        "(" Z2   Z33
      */
      ('z34, None) -> Seq( action_6 _ ,  'z33),
      ('z34 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z2 ,  'z33),

      /*
      Z39 ->
          action_22     Z33
        "(" Z7     Z33
      */
      ('z39, None) -> Seq( action_22 _ ,    'z33),
      ('z39 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z7 ,    'z33),

      /*
      Z40 ->
          action_27   Z33
        "(" Z10   Z33
      */
      ('z40, None) -> Seq( action_27 _ ,  'z33),
      ('z40 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z10 ,  'z33),

      /*
      Z10 ->
         "upper_word"  action_2 Z50
        "single_quoted"   Z51
        "real" action_20 action_16    Z50
        "rational" action_19 action_16    Z50
        "lower_word"   Z51
        "integer" action_18 action_16    Z50
        "dollar_word"   Z56
        "dollar_dollar_word"   Z57
        "distinct_object" action_17    Z50
      */
      ('z10 , Some(terminalEntry[UpperWord])) -> Seq(  action_2 _ ,'z50),
      ('z10 , Some(terminalEntry[SingleQuoted])) -> Seq(     'z51),
      ('z10 , Some(terminalEntry[Real])) -> Seq( action_20 _ ,action_16 _ ,   'z50),
      ('z10 , Some(terminalEntry[Rational])) -> Seq( action_19 _ ,action_16 _ ,   'z50),
      ('z10 , Some(terminalEntry[LowerWord])) -> Seq(     'z51),
      ('z10 , Some(terminalEntry[Integer])) -> Seq( action_18 _ ,action_16 _ ,   'z50),
      ('z10 , Some(terminalEntry[DollarWord])) -> Seq(     'z56),
      ('z10 , Some(terminalEntry[DollarDollarWord])) -> Seq(     'z57),
      ('z10 , Some(terminalEntry[DistinctObject])) -> Seq( action_17 _ ,   'z50),

      /*
      Z50 ->
         action_12 Z11
        "," Z5 Z11
      */
      ('z50, None) -> Seq( action_12 _ ,'z11),
      ('z50 , Some(terminalEntry[Comma.type])) -> Seq(   'z5 ,'z11),

      /*
      Z11 ->
         ")" action_28
      */
      ('z11 , Some(terminalEntry[RightParenthesis.type])) -> Seq(   action_28 _),

      /*
      Z51 ->
          action_6   Z50
        "(" Z2   Z50
      */
      ('z51, None) -> Seq( action_6 _ ,  'z50),
      ('z51 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z2 ,  'z50),

      /*
      Z56 ->
          action_22     Z50
        "(" Z7     Z50
      */
      ('z56, None) -> Seq( action_22 _ ,    'z50),
      ('z56 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z7 ,    'z50),

      /*
      Z57 ->
          action_27   Z50
        "(" Z10   Z50
      */
      ('z57, None) -> Seq( action_27 _ ,  'z50),
      ('z57 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z10 ,  'z50),

      /*
      Z74 ->
          action_27   Z67
        "(" Z10   Z67
      */
      ('z74, None) -> Seq( action_27 _ ,  'z67),
      ('z74 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z10 ,  'z67),

      /*
      Z17 ->
          action_6   Z16
        "(" Z2   Z16
      */
      ('z17, None) -> Seq( action_6 _ ,  'z16),
      ('z17 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z2 ,  'z16),

      /*
      Z22 ->
          action_22     Z16
        "(" Z7     Z16
      */
      ('z22, None) -> Seq( action_22 _ ,    'z16),
      ('z22 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z7 ,    'z16),

      /*
      Z23 ->
          action_27   Z16
        "(" Z10   Z16
      */
      ('z23, None) -> Seq( action_27 _ ,  'z16),
      ('z23 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z10 ,  'z16),

      /*
      Z14 ->
          action_22
        "(" Z7
      */
      ('z14, None) -> Seq( action_22 _),
      ('z14 , Some(terminalEntry[LeftParenthesis.type])) -> Seq( 'z7),

      /*
      Z15 ->
          action_27
        "(" Z10
      */
      ('z15, None) -> Seq( action_27 _),
      ('z15 , Some(terminalEntry[LeftParenthesis.type])) -> Seq(   'z10)

    )
  }

// -----------------------------------------------------------------
// actions
// -----------------------------------------------------------------

  private object Actions {

    def dbgAction(str: String) = ()

    //def dbgAction(str: String)  = println(str)

    /*
  action_2 { (variable:String) => Var(name=variable) }
    term -> variable

  */
    def action_2(s: PStack): PStack = {
      dbgAction("action_2");
      s match {
        case TokenEntry(x: UpperWord) :: rest
        => TermEntry(Var(name = x.data)) :: rest
        case _ => ???
      }
    }

    /*
  action_6 { (constant:LowerWord|SingleQuoted) => Func(name=constant.data, args=List[Term]()) }
	  plain_term -> constant

  */
    def action_6(s: PStack): PStack = {
      dbgAction("action_6");
      s match {
        case TokenEntry(x: LowerWord) :: rest
        => TermEntry(Func(x.data, List.empty)) :: rest
        case TokenEntry(x: SingleQuoted) :: rest
        => TermEntry(Func(x.data, List.empty)) :: rest
        case _ => ???
      }
    }

    /*
  action_7 { (functor:LowerWord|SingleQuoted, arguments:List[Term]) => Func(name=functor.data, args=arguments) }
	 plain_term -> functor "(" arguments ")"

  */
    def action_7(s: PStack) = {
      dbgAction("action_7");
      s match {
        case TokenEntry(RightParenthesis) :: TermEntries(args) :: TokenEntry(LeftParenthesis) :: TokenEntry(x: LowerWord) :: rest
        => TermEntry(Func(x.data, args)) :: rest
        case TokenEntry(RightParenthesis) :: TermEntries(args) :: TokenEntry(LeftParenthesis) :: TokenEntry(x: SingleQuoted) :: rest
        => TermEntry(Func(x.data, args)) :: rest
        case _ => ???
      }
    }

    /*
  action_12 { (x: Term) => List[Term](x) }
    arguments -> term
  */
    def action_12(s: PStack) = {
      dbgAction("action_12");
      s match {
        case TermEntry(term) :: rest => TermEntries(List(term)) :: rest
        case _ => ???
      }
    }

    /*
  action_13 { (term: Term, arguments: List[Term]) => (term :: arguments) }
    arguments -> term "," arguments
  */
    def action_13(s: PStack): PStack = {
      dbgAction("action_13");
      s match {
        case TermEntries(arguments) :: TokenEntry(Comma) :: TermEntry(term) :: rest => TermEntries(term :: arguments) :: rest
        case _ => ???
      }
    }

    /*
  action_16 { (number: Number) => NumberTerm(value=number) }
	  defined_atom -> number
  */
    def action_16(s: PStack) = {
      dbgAction("action_16");
      s match {
        case NumberEntry(x) :: rest
        => TermEntry(NumberTerm(x)) :: rest
        case _ => ???
      }
    }

    /*
  action_17 { (distinct_object:DistinctObject) => Distinct(data=distinct_object.data) }
	  defined_atom -> "distinct_object"
  */
    def action_17(s: PStack) = {
      dbgAction("action_17");
      s match {
        case TokenEntry(x: DistinctObject) :: rest
        => TermEntry(Distinct(x.data)) :: rest
        case _ => ???
      }
    }

    /*
  action_18 { (integer: Integer) => IntegerNumber(value=integer.value) }
	  number -> "integer"
   */
    def action_18(s: PStack) = {
      dbgAction("action_18");
      s match {
        case TokenEntry(x: Integer) :: rest
        => NumberEntry(IntegerNumber(x.value)) :: rest
        case _ => ???
      }
    }

    /*
  action_19 { (rational:Rational) => RationalNumber(rational.p: , rational.q) }
	  number -> "rational"
  */
    def action_19(s: PStack) = {
      dbgAction("action_19");
      s match {
        case TokenEntry(x: Rational) :: rest
        => NumberEntry(RationalNumber(x.p, x.q)) :: rest
        case _ => ???
      }
    }

    /*
  action_20 { (real:Real) => DoubleNumber(value=Math.power(real.coeff, real.exp)) }
	  number -> "real"
  */
    def action_20(s: PStack) = {
      dbgAction("action_20");
      s match {
        case TokenEntry(x: Real) :: rest
        => NumberEntry(DoubleNumber(Math.pow(x.coeff, x.exp))) :: rest
        case _ => ???
      }
    }

    /*
  action_22 { (defined_functor:DollarWord) => DefinedFunc(name=defined_functor.data, args=List[Term]()) }
    defined_plain_term -> defined_constant
  */
    def action_22(s: PStack) = {
      dbgAction("action_22");
      s match {
        case TokenEntry(x: DollarWord) :: rest
        => TermEntry(DefinedFunc(x.data, List[Term]())) :: rest
        case _ => ???
      }
    }

    /*
  action_23 { (defined_functor:DollarWord, arguments:List[Term]) => DefinedFunc(name=defined_functor.data, args=arguments) }
	  defined_plain_term -> defined_functor "(" arguments ")"
  */
    def action_23(s: PStack) = {
      dbgAction("action_23");
      s match {
        case TokenEntry(RightParenthesis) :: TermEntries(args) :: TokenEntry(LeftParenthesis) :: TokenEntry(x: DollarWord) :: rest
        => TermEntry(DefinedFunc(x.data, args)) :: rest
        case _ => ???
      }
    }

    /*
  action_27 { (system_functor:DollarDollarWord) => SystemFunc(name=system_functor.data, args=List[Term]()) }
    system_term -> system_constant
  */
    def action_27(s: PStack) = {
      dbgAction("action_27");
      s match {
        case TokenEntry(x: DollarDollarWord) :: rest
        => TermEntry(SystemFunc(x.data, List[Term]())) :: rest
        case _ => ???
      }
    }

    /*
  action_28 { (system_functor:DollarDollarWord) => SystemFunc(name=system_functor.data, args=arguments) }
	  system_term -> system_functor "(" arguments ")"
  */
    def action_28(s: PStack) = {
      dbgAction("action_28");
      s match {
        case TokenEntry(RightParenthesis) :: TermEntries(args) :: TokenEntry(LeftParenthesis) :: TokenEntry(x: DollarDollarWord) :: rest
        => TermEntry(SystemFunc(x.data, args)) :: rest
        case _ => ???
      }
    }
  }
}

