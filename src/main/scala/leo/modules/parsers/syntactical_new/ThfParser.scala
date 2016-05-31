package leo.modules.parsers.syntactical_new

import leo.datastructures.tptp.thf.{! => All, + => SumType, _}
import leo.datastructures.tptp.Commons
import leo.modules.parsers.ParserInterface
import leo.modules.parsers.syntactical_new.termParser2.TermParser2

object ParserUtils {

  implicit class MultiMap[A, B](val map: Map[A, Set[B]]) extends AnyVal {
    def addBinding(key: A, value: B): Map[A, Set[B]] =
      (map + (key -> {
        map.getOrElse(key, Set.empty) + value
      }))

    /*
    def removeBinding(key: A, value: B): ListMultiMap[A, B] = map.get(key) match {
      case None => map
      case Some(List(value)) => map - key
      case Some(list) => map + (key -> list.diff(List(value)))
    }
    */
  }

  def dbgPrintP(print: (Any) => Unit)(depth: Int)(x: Any): Unit = {
    val tab =
      if (depth > 1) {
        val count = depth.toString()
        count + "." * (depth - count.length)
      }
      else
        "." * depth
    print(
       tab +
        x.toString.replaceAll("\n", "\n" + tab)
    )
  }

}

object ThfParser
  extends ThfParser
  with ParserInterface[Formula]
{
  override type TokenStream[T] = ParserInterface[Formula]#TokenStream[T]

  def parse(tokens: TokenStream[Token]): Either[ParserError,(Formula, TokenStream[Token])] =
    parseThfFormula(tokens)
}

/**
  * Created by samuel on 10.03.16.
  */
class ThfParser
  //extends TPTPLexical
  extends TermParser2
{
  private type ReturnType = Formula
  private val tableFilename = "/thfFormulaParseTable"

  private def dbgPrint(x: Any) =
    ()
    //println(x)

  //def dbgAction(str: String) = ()
  private def dbgAction(x: String) =
    ()
    //println(x)

  private val dbgPrintP = ParserUtils.dbgPrintP(dbgPrint _) _

  import lexical.{Include => IncludeToken, _}

  private object PrivateTypes {

    /* this is the type of the nodes of the abstract syntax tree: */
    sealed class StackEntry
    case class TokenEntry(data: Token) extends StackEntry
    case class FormulaEntry(data: Formula) extends StackEntry
    case class LogicFormulaEntry(data: LogicFormula) extends StackEntry
    case class VarsEntry(data: List[(Commons.Variable, Option[LogicFormula])]) extends StackEntry
    case class VarEntry(data: (Commons.Variable, Option[LogicFormula])) extends StackEntry
    case class UnaryConnectiveEntry(data: UnaryConnective) extends StackEntry
    case class BinaryConnectiveEntry(data: BinaryConnective) extends StackEntry
    case class QuantifierEntry(data: Quantifier) extends StackEntry
    case class TermEntry(data: Commons.Term) extends StackEntry
    case class LogicFormulasEntry(data: List[LogicFormula]) extends StackEntry

    type PStack = List[StackEntry]

    abstract sealed class RHSEntry
    case class RHSAction(value: PStack => PStack) extends RHSEntry
    case class RHSZSymbol(value: ZSymbol) extends RHSEntry
    case class RHSMatch(value: FirstEntryKey) extends RHSEntry

    type ZSymbol = Symbol
    type TokenId = Class[_]

    sealed class FirstEntryKey
    case class TokenByType(t: TokenId) extends FirstEntryKey
    case class SpecificToken(t: Token) extends FirstEntryKey
    case object TermPseudoToken extends FirstEntryKey
    case object AnyToken extends FirstEntryKey

    type ParseTableType = Map[(ZSymbol, FirstEntryKey), Set[Seq[RHSEntry]]]
  }

  import ParserUtils._
  import PrivateTypes._
  import scala.reflect.ClassTag

  /*
  def parseThfFormula(input: String): Either[ParserError,(ReturnType, TokenStream[Token])] =
    parseThfFormula(
      tokenize(input)
    )
  */

  def parseThfFormula(tokens: TokenStream[Token]): Either[ParserError,(ReturnType, TokenStream[Token])] =
    zParser('z0, List.empty, tokens, 0).right flatMap {
      case (FormulaEntry(f) :: Nil, restTokens) =>
        Right ((f, restTokens) )
      case (s, _) =>
        Left(s"Stack is not empty: ${s}")
    }

  private val rulesMap: ParseTableType = initMap

  private def zParser(
    currentState: ZSymbol,
    stack0: PStack,
    input0: TokenStream[Token],
    depth: Int
  ): Either[ParserError, (PStack,TokenStream[Token])] = {
    def dbgPrint(x: Any): Unit = dbgPrintP(depth)(x)
    dbgPrint(currentState)
    dbgPrint(s"\tstack: ${stack0}\n\tinput: ${input0.take(5)} ...")
    val possibleRules = lookupRule(currentState, input0)
    if( possibleRules == Set.empty ) {
      dbgPrint(s"no rule with terminal")
      return Left("lookup failed")
    }

    def parseWithRule(rule: Seq[RHSEntry]): Either[ParserError, (PStack,TokenStream[Token])] = {
      var stack = stack0
      var input = input0
      for( ruleEntry <- rule ) {
        ruleEntry match {
          case RHSAction(action) =>
            stack = action(stack)
          case RHSZSymbol(zSymbol) =>
            zParser(zSymbol, stack, input, depth+1) match {
              case Left(err) =>
                return Left(err)
              case Right((s, rest)) =>
                stack = s
                input = rest
            }
          case RHSMatch(TokenByType(tokClassId)) =>
            input match {
              case head #:: tail if head.getClass == tokClassId =>
                stack = TokenEntry(head) :: stack
                input = tail
              case head :: _
              => return Left(s"${tokClassId} expected but ${head} found!")
            }
          case RHSMatch(SpecificToken(t)) =>
            input match {
              case head #:: tail if head == t =>
                stack = TokenEntry(head) :: stack
                input = tail
              case head :: _
              => return Left(s"${t} expected but ${head} found!")
            }
          case RHSMatch(TermPseudoToken) =>
            val termRes = super.parseTerm(input)
            termRes match {
              case Right((res, rest)) =>
                stack = TermEntry(res) :: stack
                input = rest
              case Left(errMsg) =>
                return Left(s"error while parsing term: ${errMsg}")
            }
        }
      }
      Right(stack,input)
    }

    if(possibleRules.size > 1)
      dbgPrint(s"${possibleRules.size} rules:")
    if(possibleRules.size == 0)
      throw new Exception(s"no rules for ${currentState}")
      //dbgPrint(s"${possibleRules.size} rules:")
    // possibleRules foreach ((x: Seq[RHS]) => dbgPrint(x))
    var it = possibleRules.iterator

    var results: Seq[(PStack,TokenStream[Token])] = Seq()
    while ( it.hasNext ) {
      val rule = it.next
      dbgPrint(s"trying rule ${currentState} -> ${rule}")
      dbgPrint(s"\tstack: ${stack0}\n\tinput: ${input0.take(5)} ...")
      try {
        parseWithRule(rule) match {
          case Right(parseRes) =>
            dbgPrint("done")
            results = results :+ parseRes
          //return Right(parseRes)
          case Left(err) =>
            dbgPrint(s"option failed, error: ${err}")
            ()
        }
      }
      catch {
        case e: Exception => Left(s"exception: ${e.toString}")
      }
    }
    if( results.length == 1)
      return Right(results(0))
    if( results.length > 1) {
      dbgPrint("multiple parsers succeeded!")
      val sorted = results.sortBy((x) => x._2.length)
      return Right(sorted(0))
    }
    Left(s"parser failed! state:${ currentState }, input: ${ input0 }")
  }

  private def lookupRule(currentState: ZSymbol, input: TokenStream[Token]): Set[Seq[RHSEntry]] = {
    input match {
      case sym #:: _ =>
        val key = SpecificToken(sym)
        rulesMap.get(currentState, key).getOrElse{
          {
            val key = TokenByType(sym.getClass)
            rulesMap.get(currentState, key) getOrElse Set.empty
          } ++ {
          rulesMap.get(currentState, TermPseudoToken) getOrElse Set.empty} ++ {
          rulesMap.get(currentState, AnyToken) getOrElse Set.empty}
        }
      case Nil =>
        rulesMap.get(currentState, AnyToken) getOrElse Set.empty
    }
  }

  private def initMap: ParseTableType = {
    var ret: ParseTableType = Map()
    import scala.io.Source

    def parseFirstSym(x: String): Seq[String] = {
      var ret = Seq[String]()
      var temp = x
      while (temp != "") {
        temp = temp.stripPrefix("\"")
        val regexAny = "(anyToken)(.*)".r
        val regex = "([^\"]*)\"(.*)".r
        val (next, rest): (String, String) = temp match {
          case regexAny(next,rest) => (next, rest)
          case regex(next,rest) => (next, rest)
          case _ => throw new Exception("unknown Syntax: temp")
        }
        //println(next)
        ret = ret :+ next
        temp = rest trim
      }
      ret
    }

    val stream = getClass().getResourceAsStream(tableFilename)
    val bufferedSource = Source.fromInputStream( stream )
    var conflictsList = List.empty[(Symbol, String)]
    for( line <- bufferedSource.getLines ) {
      line match {
        case "" =>
        case _ if line.startsWith("//") =>
        case _ =>
          val l = line.split("->").toList map (_.trim)
          l match {
            case lhsStr :: rhsStr :: _ =>
              val temp = lhsStr.stripPrefix("(").stripSuffix(")").split(",")
              import MapImplicits._
              val (zSym, firstSym): (ZSymbol, Seq[String]) =
                ( Symbol(temp.head.toLowerCase),
                  parseFirstSym( temp.tail reduce ( _ + _ ) trim)
                  )
              val rhsStrings = rhsStr.split(" ") match {
                case Array("\"\"") => Seq()
                case Array("") => Seq()
                case x => x.toSeq
              }
              val rhs: Seq[RHSEntry] =
                 rhsStrings map {
                  case str if str == "\"\"" =>
                    throw new Exception(s"epsilon! ${str}")
                  case str if str.startsWith("\"") && str.endsWith("\"") => strToMatch(str.stripPrefix("\"").stripSuffix("\""))
                  case str if str.startsWith("Z") => toRHSZSymbol(Symbol(str.toLowerCase))
                  case str if str.startsWith("action_") => RHSAction(strToAction(str))
                  case str =>
                    throw new Exception(s"error reading right hand side entry ${str}")
                }
              /*if(zSym == 'z9)
                println(s"( ${zSym}, ${firstSym} ) -> ${rhs}")*/
              for( symbolKey <- firstSym) {
                val firstSymKey = tokenKey(symbolKey)

                // check for conflicts:
                ret.get((zSym, firstSymKey)) foreach {
                  oldRHS =>
                    val entry = (zSym, rhsStrings.headOption.getOrElse(""))
                    if(!conflictsList.contains(entry))
                        conflictsList = conflictsList :+ entry
                    //println(s"conflict:\n\t(${zSym}, ${firstSymKey}) -> ${oldRHS}\n\tand\n\t(${zSym}, ${firstSymKey}) -> ${rhs}")
                    //println(s"conflict:\n\t(${zSym}, ${firstSymKey}) -> ${oldRHS}\n\tand\n\t(${zSym}, ${firstSymKey}) -> ${rhs}")
                }

                ret = ret.addBinding((zSym, firstSymKey), rhs)
              }
            case _ => throw new Exception(s"error reading parse table file: ${l}")
          }
      }
    }
    conflictsList foreach {
      entry => dbgPrint(s"conflict in ${entry}")
    }
    bufferedSource.close()
    ret
  }

  import Actions._

  private object MapImplicits {
    //implicit def action(f: (PStack) => PStack): RHSAction = RHSAction(f)
    def toRHSZSymbol(x: ZSymbol): RHSZSymbol = RHSZSymbol(x)
    
    def type_of[T <: Token](implicit ct: ClassTag[T]): Class[_] =
      ct.runtimeClass
    def strToMatch(x: String): RHSMatch = {
      RHSMatch(tokenKey(x))
    }
    def tokenKey(str: String): FirstEntryKey = {
      str match {
        case "anyToken" => AnyToken
        case "" => AnyToken
        case _ if str.startsWith("$$") =>
          SpecificToken(DollarDollarWord(str))
          //SpecificToken(DollarDollarWord(str.stripPrefix("$$")))
        case _ if str.startsWith("$") =>
          SpecificToken(DollarWord(str))
          //SpecificToken(DollarWord(str.stripPrefix("$")))

        case "single_quoted" => TokenByType(type_of[SingleQuoted])
        case "distinct_object" => TokenByType(type_of[DistinctObject])
        case "dollar_word" => TokenByType(type_of[DollarWord])
        case "dollar_dollar_word" => TokenByType(type_of[DollarDollarWord])
        case "real" => TokenByType(type_of[Real])
        case "rational" => TokenByType(type_of[Rational])
        case "integer" => TokenByType(type_of[Integer])
        case "upper_word" => TokenByType(type_of[UpperWord])
        case "lower_word" => TokenByType(type_of[LowerWord])

        case "include" => TokenByType(type_of[IncludeToken.type])
        case "fof" => TokenByType(type_of[FOF.type])
        case "cnf" => TokenByType(type_of[CNF.type])
        case "thf" => TokenByType(type_of[THF.type])
        case "tff" => TokenByType(type_of[TFF.type])
        case "tpi" => TokenByType(type_of[TPI.type])

        case "term" => TermPseudoToken

        case "star" => TokenByType(type_of[Star.type])
        case "plus" => TokenByType(type_of[Plus.type])
        case "-" => TokenByType(type_of[Minus.type])
        case "@" => TokenByType(type_of[Application.type])
        case "^" => TokenByType(type_of[Lambda.type])
        case "(" => TokenByType(type_of[LeftParenthesis.type])
        case ")" => TokenByType(type_of[RightParenthesis.type])
        case "[" => TokenByType(type_of[LeftBracket.type])
        case "]" => TokenByType(type_of[RightBracket.type])
        case "," => TokenByType(type_of[Comma.type])
        case "." => TokenByType(type_of[Dot.type])
        case ":" => TokenByType(type_of[Colon.type])
        case "?" => TokenByType(type_of[Questionmark.type])

        case "less_eq_greater" => TokenByType(type_of[Leftrightarrow.type])
        case "less_eq" => TokenByType(type_of[Leftarrow.type])
        case "=>" => TokenByType(type_of[Rightarrow.type])
        case "less_tilde_greater" => TokenByType(type_of[Leftrighttildearrow.type])
        case "less_sign" => TokenByType(type_of[LessSign.type])
        //case "less" => TokenByType(type_of[LessSign.type])
        case "~&" => TokenByType(type_of[TildeAmpersand.type])
        case "~vline" => TokenByType(type_of[TildePipe.type])
        case "~" => TokenByType(type_of[Tilde.type])
        case "vline" => TokenByType(type_of[VLine.type])
        case "&" => TokenByType(type_of[Ampersand.type])
        case "arrow" => TokenByType(type_of[Arrow.type])
        case "!=" => TokenByType(type_of[NotEquals.type])
        case "!" => TokenByType(type_of[Exclamationmark.type])
        case "=" => TokenByType(type_of[Equals.type])
        case _ => throw new Exception(s"no token with name ${str}")
      }
    }

    def strToAction(str: String): (PStack) => PStack = {
      val extractNumber = "action_([0-9]+)".r
      str match {
        case extractNumber(actionIndex) => actionIndex.toInt match {
          case 1 => action_1 _
          case 10 => action_10 _
          case 16 => action_16 _
          case 17 => action_17 _
          case 18 => action_18 _
          case 19 => action_19 _
          case 20 => action_20 _
          case 21 => action_21 _
          case 22 => action_22 _
          case 23 => action_23 _
          case 24 => action_24 _
          case 25 => action_25 _
          case 26 => action_26 _
          case 27 => action_27 _
          case 30 => action_30 _
          case 31 => action_31 _
          case 33 => action_33 _
          case 35 => action_35 _
          case 36 => action_36 _
          case 37 => action_37 _
          case 38 => action_38 _
          case 40 => action_40 _
          case 41 => action_41 _
          case 42 => action_42 _
          case 46 => action_46 _
          case 47 => action_47 _
          case 48 => action_48 _
          case 49 => action_49 _
          case 50 => action_50 _
          case 51 => action_51 _
          case 52 => action_52 _
          case 53 => action_53 _
          case 54 => action_54 _
          case 55 => action_55 _
          case 56 => action_56 _
          case 57 => action_57 _
          case 58 => action_58 _
          case 64 => action_64 _
          case 65 => action_65 _
          case 66 => action_66 _
          case 67 => action_67 _
          case 68 => action_68 _
          case 69 => action_69 _
          case 73 => action_73 _
          case 74 => action_74 _
          case 76 => action_76 _
          case 77 => action_77 _
          case 78 => action_78 _
          case 79 => action_79 _
          case 80 => action_80 _
          case 82 => action_82 _
          case 83 => action_83 _
          case 89 => action_89 _
          case 90 => action_90 _
          case 91 => action_91 _
          case 92 => action_92 _
          case 93 => action_93 _
          case 94 => action_94 _
        }
      }
    }
		val anyToken = AnyToken
		//implicit def setKeyToManyKeys(x: (Symbol, Set[String])): Symbol
    //val strToToken: PartialFunction[String, TokenByType] = ???
  }

// -----------------------------------------------------------------
// actions
// -----------------------------------------------------------------

  private object Actions {


    /* example
    //thf_binary_pair -> thf_unitary_formula thf_pair_connective thf_unitary_formula
    def action_9(s: PStack): PStack = s match {
      case LogicFormulaEntry(f2) :: BinaryConnectiveEntry(conn) :: LogicFormulaEntry(f1) :: rest
        => LogicFormulaEntry( Binary(left=f1, connective=conn, right=f2 ) ) :: rest
    }
    */

    def quantifierFromToken_1(x: Token): Quantifier =
      x match {
        case Questionmark => ?
        case Exclamationmark => All
        case Lambda => ^
        case _ => throw new Exception(s"quantifier not recognized: ${x}")
      }

    def quantifierFromToken_2(x1: Token, x2: Token): Quantifier =
      (x1, x2) match {
        case (Exclamationmark, Arrow) => !>
        case (Questionmark, Star) => ?*
        case (Application, Plus) => @+
        case (Application, Minus) => @-
        case _ => throw new Exception(s"quantifier not recognized: ${(x1, x2)}")
      }

    def binaryConnectiveFromToken_1(x: Token): BinaryConnective =
      x match {
        case Equals => Eq
        case NotEquals => Neq
        case Leftrightarrow => <=>
        case Rightarrow => Impl
        case Leftarrow => <=
        case Leftrighttildearrow => <~>
        case TildePipe => ~|
        case TildeAmpersand => ~&
        case VLine => |
        case Ampersand => &
        case Application => App
        case _ => throw new Exception(s"binary connective not recognized: ${x}")
      }

    // thf_formula -> thf_logic_formula
    def action_1(s: PStack): PStack = { dbgAction("action_1"); s match {
      case LogicFormulaEntry(f) :: rest => FormulaEntry(Logical(f)) :: rest
      case _ => throw new Exception(s"action_1: got ${s}")
    }}

    // thf_binary_pair -> thf_unitary_formula thf_pair_connective thf_unitary_formula
    def action_10(s: PStack): PStack = { dbgAction("action_10"); s match {
      case LogicFormulaEntry(f2) :: BinaryConnectiveEntry(conn) :: LogicFormulaEntry(f1) :: rest
      => LogicFormulaEntry(Binary(left = f1, connective = conn, right = f2)) :: rest
      case _ => throw new Exception(s"action_10: got ${s}")
    }}

    // thf_unitary_formula -> "(" thf_logic_formula ")"
    def action_16(s: PStack): PStack = { dbgAction("action_16"); s match {
      case TokenEntry(RightParenthesis) :: LogicFormulaEntry(f) :: TokenEntry(LeftParenthesis) :: rest
      => LogicFormulaEntry(f) :: rest
      case _ => throw new Exception(s"action_16: got ${s}")
    }}

    // thf_quantified_formula -> thf_quantifier "[" thf_variable_list "]" ":" thf_unitary_formula
    def action_17(s: PStack): PStack = { dbgAction("action_17"); s match {
      case LogicFormulaEntry(f) :: TokenEntry(Colon) :: TokenEntry(RightBracket) :: VarsEntry(vars) :: TokenEntry(LeftBracket) :: QuantifierEntry(q) :: rest
      => LogicFormulaEntry(Quantified(quantifier = q, varList = vars, matrix = f)) :: rest
      case _ => throw new Exception(s"action_17: got ${s}")
    }}

    // thf_quantifier -> fol_quantifier
    def action_18(s: PStack): PStack = { dbgAction("action_18"); s match {
      case TokenEntry(x) :: rest
      => QuantifierEntry(quantifierFromToken_1(x)) :: rest
      case _ => throw new Exception(s"action_18: got ${s}")
    }}

    // thf_quantifier -> "^"
    def action_19(s: PStack): PStack = { dbgAction("action_19"); action_18(s)}

    // thf_quantifier -> "!" ">"
    def action_20(s: PStack): PStack = { dbgAction("action_20"); s match {
      case TokenEntry(x2) :: TokenEntry(x1) :: rest
      => QuantifierEntry(quantifierFromToken_2(x1, x2)) :: rest
      case _ => throw new Exception(s"action_20: got ${s}")
    }}

    // thf_quantifier -> "?" "*"
    def action_21(s: PStack): PStack = { dbgAction("action_21"); action_20(s)}

    // thf_quantifier -> "@" "+"
    def action_22(s: PStack): PStack = { dbgAction("action_22"); action_20(s)}

    // thf_quantifier -> "@" "-"
    def action_23(s: PStack): PStack = { dbgAction("action_23"); action_20(s)}

    // fol_quantifier -> "!"
    def action_24(s: PStack): PStack = { dbgAction("action_24"); s}

    // fol_quantifier -> "?"
    def action_25(s: PStack): PStack = { dbgAction("action_25"); s}

    // thf_variable_list -> thf_variable
    def action_26(s: PStack): PStack = { dbgAction("action_26"); s match {
      case VarEntry(v) :: rest
      => VarsEntry(List(v)) :: rest
      case _ => throw new Exception(s"action_26: got ${s}")
    }}

    // thf_variable_list -> thf_variable "," thf_variable_list
    def action_27(s: PStack): PStack = { dbgAction("action_27"); s match {
      case VarsEntry(vars) :: TokenEntry(Comma) :: VarEntry(v) :: rest
      => VarsEntry(v :: vars) :: rest
      case _ => throw new Exception(s"action_27: got ${s}")
    }}

    // thf_typed_variable -> variable ":" thf_top_level_type
    def action_30(s: PStack): PStack = { dbgAction("action_30"); s match {
      case LogicFormulaEntry(t) :: TokenEntry(Colon) :: VarEntry((v, None)) :: rest
      => VarEntry((v, Some(t))) :: rest
      case _ => throw new Exception(s"action_30: got ${s}")
    }}

    // variable -> "upper_word"
    def action_31(s: PStack): PStack = { dbgAction("action_31"); s match {
      case TokenEntry(UpperWord(v)) :: rest
      => VarEntry((v, None)) :: rest
      case _ => throw new Exception(s"action_31: got ${s}")
    }}

    // thf_unary_formula -> thf_unary_connective "(" thf_logic_formula ")"
    def action_33(s: PStack): PStack = { dbgAction("action_33"); s match {
      case TokenEntry(RightParenthesis) :: LogicFormulaEntry(f) :: TokenEntry(LeftParenthesis) :: UnaryConnectiveEntry(conn) :: rest
      => LogicFormulaEntry(Unary(connective = conn, formula = f)) :: rest
      case _ => throw new Exception(s"action_33: got ${s}")
    }}

    // thf_unary_connective -> "!" "!"
    def action_35(s: PStack): PStack = { dbgAction("action_35"); s match {
      case TokenEntry(Exclamationmark) :: TokenEntry(Exclamationmark) :: rest
      => UnaryConnectiveEntry(!!) :: rest
      case _ => throw new Exception(s"action_35: got ${s}")
    }}

    // thf_unary_connective -> "?" "?"
    def action_36(s: PStack): PStack = { dbgAction("action_36"); s match {
      case TokenEntry(Questionmark) :: TokenEntry(Questionmark) :: rest
      => UnaryConnectiveEntry(??) :: rest
      case _ => throw new Exception(s"action_36: got ${s}")
    }}

    // unary_connective -> "~"
    def action_37(s: PStack): PStack = { dbgAction("action_37"); s match {
      case TokenEntry(Tilde) :: rest
      => UnaryConnectiveEntry(leo.datastructures.tptp.thf.~) :: rest
      case _ => throw new Exception(s"action_37: got ${s}")
    }}

    // thf_atom -> "term"
    def action_38(s: PStack): PStack = { dbgAction("action_38"); s match {
      case TermEntry(t) :: rest
      => LogicFormulaEntry(Term(t)) :: rest
      case _ => throw new Exception(s"action_38: got ${s}")
    }}

    // thf_conn_term -> thf_pair_connective
    def action_40(s: PStack): PStack = { dbgAction("action_40"); s match {
      case BinaryConnectiveEntry(conn) :: rest
      => LogicFormulaEntry(Connective(Left(conn))) :: rest
      case _ => throw new Exception(s"action_40: got ${s}")
    }}

    // thf_conn_term -> assoc_connective
    def action_41(x: PStack) = { dbgAction("action_41"); action_40(x)}

    // thf_conn_term -> thf_unary_connective
    def action_42(s: PStack): PStack = { dbgAction("action_42"); s match {
      case UnaryConnectiveEntry(conn) :: rest => LogicFormulaEntry(Connective(Right(conn))) :: rest
    }}

    // infix_equality -> "="
    def action_46(s: PStack): PStack = { dbgAction("action_46"); s match {
      case TokenEntry(x) :: rest
      => BinaryConnectiveEntry(binaryConnectiveFromToken_1(x)) :: rest
      case _ => throw new Exception(s"action_46: got ${s}")
    }}

    // infix_inequality -> "!="
    def action_47(x: PStack) = { dbgAction("action_47"); action_46(x)}

    // binary_connective -> "less_eq_greater"
    def action_48(x: PStack) = { dbgAction("action_48"); action_46(x)}

    // binary_connective -> "=>"
    def action_49(x: PStack) = { dbgAction("action_49"); action_46(x)}

    // binary_connective -> "less_eq"
    def action_50(x: PStack) = { dbgAction("action_50"); action_46(x)}

    // binary_connective -> "less_tilde_greater"
    def action_51(x: PStack) = { dbgAction("action_51"); action_46(x)}

    // binary_connective -> "~vline"
    def action_52(x: PStack): PStack = { dbgAction("action_52"); action_46(x)}

    // binary_connective -> "~" "&"
    def action_53(x: PStack) = { dbgAction("action_53"); action_46(x)}

    // assoc_connective -> "vline"
    def action_54(x: PStack) = { dbgAction("action_54"); action_46(x)}

    // assoc_connective -> "&"
    def action_55(x: PStack): PStack = { dbgAction("action_55"); action_46(x)}

    // thf_conditional -> "$ite_f" "(" thf_logic_formula "," thf_logic_formula "," thf_logic_formula ")"
    def action_56(s: PStack): PStack = { dbgAction("action_56"); s match {
      case TokenEntry(RightParenthesis) :: LogicFormulaEntry(elseFormula) :: TokenEntry(Comma) :: LogicFormulaEntry(thenFormula) :: TokenEntry(Comma) :: LogicFormulaEntry(condFormula) :: TokenEntry(LeftParenthesis) :: TokenEntry(DollarWord("$ite_f")) :: rest
        => LogicFormulaEntry(Cond(cond = condFormula, thn = thenFormula, els = elseFormula)) :: rest
      case _ => throw new Exception(s"action_56: got ${s}")
    }}

    // thf_let -> "$let_tf" "(" thf_let_term_defn "," thf_formula ")"
    def action_57(s: PStack): PStack = { dbgAction("action_57"); s match {
      case TokenEntry(RightParenthesis) :: FormulaEntry(f) :: TokenEntry(Comma) :: LogicFormulaEntry(let_def: Quantified) :: TokenEntry(LeftParenthesis) :: TokenEntry(DollarWord("$let_tf")) :: rest
      => LogicFormulaEntry(Let(TermBinding(let_def), f)) :: rest
      case _ => throw new Exception(s"action_57: got ${s}")
    }}

    // thf_let -> "$let_ff" "(" thf_let_formula_defn "," thf_formula ")"
    def action_58(s: PStack): PStack = { dbgAction("action_58"); s match {
      case TokenEntry(RightParenthesis) :: FormulaEntry(f) :: TokenEntry(Comma) :: LogicFormulaEntry(let_def: Quantified) :: TokenEntry(LeftParenthesis) :: TokenEntry(DollarWord("$let_ff")) :: rest
      => LogicFormulaEntry(Let(FormulaBinding(let_def), f)) :: rest
      case _ => throw new Exception(s"action_58: got ${s}")
    }}

    // thf_or_formula -> thf_unitary_formula "vline" thf_unitary_formula
    def action_64(s: PStack): PStack = { dbgAction("action_64"); s match {
      case LogicFormulaEntry(f2) :: TokenEntry(conn) :: LogicFormulaEntry(f1) :: rest
      => LogicFormulaEntry(Binary(left = f1, connective = binaryConnectiveFromToken_1(conn), right = f2)) :: rest
      case _ => throw new Exception(s"action_64: got ${s}")
    }}

    // thf_or_formula -> thf_or_formula "vline" thf_unitary_formula
    def action_65(s: PStack): PStack = { dbgAction("action_65"); action_64(s)}

    // thf_and_formula -> thf_unitary_formula "&" thf_unitary_formula
    def action_66(s: PStack): PStack = { dbgAction("action_66"); action_64(s)}

    // thf_and_formula -> thf_and_formula "&" thf_unitary_formula
    def action_67(s: PStack): PStack = { dbgAction("action_67"); action_64(s)}

    // thf_apply_formula -> thf_unitary_formula "@" thf_unitary_formula
    def action_68(s: PStack): PStack = { dbgAction("action_68"); action_64(s)}

    // thf_apply_formula -> thf_apply_formula "@" thf_unitary_formula
    def action_69(s: PStack): PStack = { dbgAction("action_69"); action_64(s)}

    // thf_mapping_type -> thf_unitary_type "arrow" thf_unitary_type
    def action_73(s: PStack): PStack = { dbgAction("action_73"); s match {
      case LogicFormulaEntry(t2) :: TokenEntry(Arrow) :: LogicFormulaEntry(t1) :: rest
      => LogicFormulaEntry(BinType(->(List(t1, t2)))) :: rest
      case _ => throw new Exception(s"action_73: got ${s}")
    }}

    // thf_mapping_type -> thf_unitary_type "arrow" thf_mapping_type
    def action_74(s: PStack): PStack = { dbgAction("action_74"); s match {
      case LogicFormulaEntry(BinType(->(l))) :: TokenEntry(Arrow) :: LogicFormulaEntry(t1) :: rest
      => LogicFormulaEntry(BinType(->(t1 :: l))) :: rest
      case _ => throw new Exception(s"action_74: got ${s}")
    }}

    // thf_xprod_type -> thf_unitary_type "star" thf_unitary_type
    def action_76(s: PStack): PStack = { dbgAction("action_76"); s match {
      case LogicFormulaEntry(t2) :: TokenEntry(Star) :: LogicFormulaEntry(t1) :: rest
      => LogicFormulaEntry(BinType(*(List(t1, t2)))) :: rest
      case _ => throw new Exception(s"action_76: got ${s}")
    }}

    // thf_xprod_type -> thf_xprod_type "star" thf_unitary_type
    def action_77(s: PStack): PStack = { dbgAction("action_77"); s match {
      case LogicFormulaEntry(t1) :: TokenEntry(Star) :: LogicFormulaEntry(BinType(*(l))) :: rest
      => LogicFormulaEntry(BinType(*(l :+ t1))) :: rest
      case _ => throw new Exception(s"action_77: got ${s}")
    }}

    // thf_union_type -> thf_unitary_type "plus" thf_unitary_type
    def action_78(s: PStack): PStack = { dbgAction("action_78"); s match {
      case LogicFormulaEntry(t2) :: TokenEntry(Plus) :: LogicFormulaEntry(t1) :: rest
      => LogicFormulaEntry(BinType(SumType(List(t1, t2)))) :: rest
      case _ => throw new Exception(s"action_78: got ${s}")
    }}

    // thf_union_type -> thf_union_type "plus" thf_unitary_type
    def action_79(s: PStack): PStack = { dbgAction("action_79"); s match {
      case LogicFormulaEntry(t1) :: TokenEntry(Plus) :: LogicFormulaEntry(BinType(SumType(l))) :: rest
      => LogicFormulaEntry(BinType(SumType(l :+ t1))) :: rest
      case _ => throw new Exception(s"action_79: got ${s}")
    }}

    // thf_type_formula -> thf_typeable_formula ":" thf_top_level_type
    def action_80(s: PStack): PStack = { dbgAction("action_80"); s match {
      case LogicFormulaEntry(t) :: TokenEntry(Colon) :: LogicFormulaEntry(f) :: rest
      => LogicFormulaEntry(Typed(formula = f, typ = t)) :: rest
      case _ => throw new Exception(s"action_80: got ${s}")
    }}

    // thf_typeable_formula -> "(" thf_logic_formula ")"
    def action_82(s: PStack): PStack = { dbgAction("action_82"); s match {
      case TokenEntry(RightParenthesis) :: LogicFormulaEntry(f) :: TokenEntry(LeftParenthesis) :: rest
      => LogicFormulaEntry(f) :: rest
      case _ => throw new Exception(s"action_82: got ${s}")
    }}

    // thf_subtype -> constant subtype_sign constant
    def action_83(s: PStack): PStack = { dbgAction("action_83"); s match {
      case TokenEntry(LowerWord(x2)) :: TokenEntry(LessSign) :: TokenEntry(LessSign) :: TokenEntry(LowerWord(x1)) :: rest
      => LogicFormulaEntry(Subtype(x1, x2)) :: rest
      case _ => throw new Exception(s"action_83: got ${s}")
    }}

    // thf_sequent -> thf_tuple gentzen_arrow thf_tuple
    def action_89(s: PStack): PStack = { dbgAction("action_89"); s match {
      case LogicFormulasEntry(f2) :: TokenEntry(Arrow) :: TokenEntry(Minus) :: TokenEntry(Minus) :: LogicFormulasEntry(f1) :: rest
      => FormulaEntry(Sequent(tuple1 = f1, tuple2 = f2)) :: rest
      case _ => throw new Exception(s"action_89: got ${s}")
    }}

    // thf_sequent -> "(" thf_sequent ")"
    def action_90(s: PStack): PStack = { dbgAction("action_90"); s match {
      case TokenEntry(RightParenthesis) :: FormulaEntry(sequent: Sequent) :: TokenEntry(LeftParenthesis) :: rest
      => FormulaEntry(sequent) :: rest
      case _ => throw new Exception(s"action_90: got ${s}")
    }}

    // thf_tuple -> "[" "]"
    def action_91(s: PStack): PStack = { dbgAction("action_91"); s match {
      case TokenEntry(RightBracket) :: TokenEntry(LeftBracket) :: rest
      => LogicFormulasEntry(List[LogicFormula]()) :: rest
      case _ => throw new Exception(s"action_91: got ${s}")
    }}

    // thf_tuple -> "[" thf_tuple_list "]"
    def action_92(s: PStack): PStack = { dbgAction("action_92"); s match {
      case TokenEntry(RightBracket) :: LogicFormulasEntry(tuple) :: TokenEntry(LeftBracket) :: rest
      => LogicFormulasEntry(tuple) :: rest
      case _ => throw new Exception(s"action_92: got ${s}")
    }}

    // thf_tuple_list -> thf_logic_formula
    def action_93(s: PStack): PStack = { dbgAction("action_93"); s match {
      case LogicFormulaEntry(tuple) :: rest
      => LogicFormulasEntry(List(tuple)) :: rest
      case _ => throw new Exception(s"action_93: got ${s}")
    }}

    // thf_tuple_list -> thf_logic_formula "," thf_tuple_list
    def action_94(s: PStack): PStack = { dbgAction("action_94"); s match {
      case LogicFormulasEntry(list) :: TokenEntry(Comma) :: LogicFormulaEntry(formula) :: rest
      => LogicFormulasEntry(formula :: list) :: rest
      case _ => throw new Exception(s"action_94: got ${s}")
    }}

  }
}

