package leo.modules.parsers.syntactical_new

//import leo.modules.parsers.syntactical_new.ThfParser
import leo.datastructures.tptp.thf
import leo.datastructures.tptp.Commons._ //.{TPTPInput, Term}
//import leo.datastructures.tptp.Commons.TPTPInput
import leo.modules.parsers.ParserInterface

/**
  * Created by samuel on 28.04.16.
  */

object TPTPParser2
  extends TPTPParser2
  with ParserInterface[TPTPInput]
{
  override type TokenStream[T] = ParserInterface[TPTPInput]#TokenStream[T]

  def parse(tokens: TokenStream[Token]): Either[ParserError,(TPTPInput, TokenStream[Token])] =
    parseTPTPInput(tokens)
}

class TPTPParser2
  extends ThfParser
{
  private type ReturnType = TPTPInput
  private val tableFilename = "/TPTPInput_formulaAndTerm_asTerminals_parseTable"

  //def dbgPrint(x: Any) = ()
  private def dbgPrint(x: Any) =
    ()
    //println(x)
    //println("TPTPParser: " + x)

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
    case class TPTPEntry(data: Either[AnnotatedFormula, Include]) extends StackEntry
    case class FormulaEntry(data: thf.Formula) extends StackEntry
    case class TermEntry(data: Term) extends StackEntry
    case class AnnotationsEntry(data: Annotations) extends StackEntry
    case class GeneralTermEntry(data: GeneralTerm) extends StackEntry
    case class GeneralTermsEntry(data: List[GeneralTerm]) extends StackEntry
    case class GeneralDataEntry(data: GeneralData) extends StackEntry
    case class NumberEntry(data: Number) extends StackEntry
    case class StringEntry(data: String) extends StackEntry
    case class StringsEntry(data: List[String]) extends StackEntry

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
    case object ThfFormulaPseudoToken extends FirstEntryKey
    case object AnyToken extends FirstEntryKey

    type ParseTableType = Map[(ZSymbol, FirstEntryKey), Set[Seq[RHSEntry]]]
  }

  import ParserUtils._
  import PrivateTypes._
  import scala.reflect.ClassTag

  def parseTPTPInput(tokens: TokenStream[Token]): Either[ParserError,(TPTPInput, TokenStream[Token])] = {
    var stream = tokens
    var resList = Seq[Either[AnnotatedFormula, Include]]()
    while (!stream.isEmpty) {
      val newRes = parseAnnotatedFormulaOrInclude(stream)
      newRes match {
        case Right((res,restStream)) =>
          resList = resList :+ res
          stream = restStream
        case Left(err) =>
          return Left(err)
      }
    }
    Right(TPTPInput(resList), stream)
  }

  def parseAnnotatedFormulaOrInclude(tokens: TokenStream[Token]): Either[ParserError,(Either[AnnotatedFormula, Include], TokenStream[Token])] =
    zParser('z0, List.empty, tokens, 0).right flatMap {
      case (TPTPEntry(tptp) :: Nil, restTokens) =>
        Right ((tptp, restTokens) )
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
          case RHSMatch(ThfFormulaPseudoToken) =>
            val thfFormula = super.parseThfFormula(input)
            thfFormula match {
              case Right((res, rest)) =>
                stack = FormulaEntry(res) :: stack
                input = rest
              case Left(errMsg) =>
                return Left(s"error while parsing term: ${errMsg}")
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
            dbgPrint(s"option failed")
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
    Left(s"parser failed! state:${ currentState }, input: ${ input0 }, results of the strategies tried: ${results}")
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
            rulesMap.get(currentState, ThfFormulaPseudoToken) getOrElse Set.empty} ++ {
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
      //dbgPrint(s"reading line: ${line}")
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
              val rhsStrings = rhsStr.split(" ").toList match {
                case "\"\"" :: others  => others
                //case Array("\"\"") => Seq()
                case List("") => Seq()
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
              //println(s"( ${zSym}, ${firstSym} ) -> ${rhs}")
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
    implicit def act(f: (PStack) => PStack): RHSAction = RHSAction(f)
    implicit def toRHSZSymbol(x: ZSymbol): RHSZSymbol = RHSZSymbol(x)

    def type_of[T <: Token](implicit ct: ClassTag[T]): Class[_] =
      ct.runtimeClass
    def strToMatch(x: String): RHSMatch = {
      RHSMatch(tokenKey(x))
    }
    def tokenKey(str: String): FirstEntryKey = {
      str match {
        case "anyToken" => AnyToken
        case "" => AnyToken
        case _ if str.startsWith("$$")
          => SpecificToken(DollarDollarWord(str))
        case _ if str.startsWith("$")
        => SpecificToken(DollarWord(str))

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
        case "thf_formula" => ThfFormulaPseudoToken

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

case 4 => action_4 _
case 5 => action_5 _
case 6 => action_6 _
case 9 => action_9 _
case 10 => action_10 _
case 11 => action_11 _
case 13 => action_13 _
case 14 => action_14 _
case 15 => action_15 _
case 16 => action_16 _
case 18 => action_18 _
case 19 => action_19 _
case 20 => action_20 _
case 22 => action_22 _
case 23 => action_23 _
case 24 => action_24 _
case 26 => action_26 _
case 27 => action_27 _
case 28 => action_28 _
case 29 => action_29 _
case 30 => action_30 _
case 31 => action_31 _
case 32 => action_32 _
case 33 => action_33 _
case 34 => action_34 _
case 37 => action_37 _
case 39 => action_39 _
case 40 => action_40 _
case 41 => action_41 _
case 42 => action_42 _
        }
      }
    }
		val anyToken = AnyToken
		//implicit def setKeyToManyKeys(x: (Symbol, Set[String])): Symbol
    //val strToToken: PartialFunction[String, TokenByType] = ???
  }

  private object Actions {

// thf_annotated -> "thf" "(" name "," formula_role "," "thf_formula" annotations ")" "."
def action_4(s: PStack): PStack = { dbgAction("action_4"); s match {
    case TokenEntry(Dot) :: TokenEntry(RightParenthesis) :: AnnotationsEntry(annotations) :: FormulaEntry(f) :: TokenEntry(Comma) :: StringEntry(role) :: TokenEntry(Comma) :: StringEntry(name) :: TokenEntry(LeftParenthesis) :: TokenEntry(THF) :: rest =>
      TPTPEntry(Left(THFAnnotated(name, role, f, annotations))) :: rest
    case _ => throw new Exception(s"action_4: got ${s}")
}}

//name -> atomic_word
def action_5(s: PStack):PStack = { dbgAction("action_5"); s match {
  case TokenEntry(tok: LowerWord ) :: rest => StringEntry(tok.data) :: rest
  case TokenEntry(tok: SingleQuoted) :: rest => StringEntry(tok.data) :: rest
  case _ => throw new Exception(s"action_5: got ${s}")
}}

// name -> "integer"
def action_6(s: PStack):PStack = { dbgAction("action_6"); s match {
case TokenEntry(Integer(x)) :: rest => StringEntry(x.toString) :: rest
case _ => throw new Exception(s"action_6: got ${s}")
}}

// formula_role -> "lower_word"
def action_9(s: PStack):PStack = { dbgAction("action_9"); s match {
case TokenEntry(t: LowerWord) :: rest => StringEntry(t.data) :: rest
case _ => throw new Exception(s"action_9: got ${s}")
}}

// annotations -> "," source optional_info
def action_10(s: PStack):PStack = { dbgAction("action_10"); s match {
case GeneralTermsEntry(optInfo) :: GeneralTermEntry(source) :: TokenEntry(Comma) :: rest => AnnotationsEntry(Some((source,optInfo))) :: rest
case _ => throw new Exception(s"action_10: got ${s}")
}}

// annotations -> null
def action_11(s: PStack):PStack = { dbgAction("action_11"); s match {
case rest => AnnotationsEntry(None) :: rest
}}

// general_term -> general_data
def action_13(s: PStack):PStack = { dbgAction("action_13"); s match {
case GeneralDataEntry(data) :: rest => GeneralTermEntry(GeneralTerm(List(Left(data)))) :: rest
case _ => throw new Exception(s"action_13: got ${s}")
}}

// general_term -> general_data ":" general_term
def action_14(s: PStack):PStack = { dbgAction("action_14"); s match {
case GeneralTermEntry(gterm) :: TokenEntry(Colon) :: GeneralDataEntry(data) :: rest => GeneralTermEntry(GeneralTerm(Left(data) :: gterm.term)) :: rest
case _ => throw new Exception(s"action_14: got ${s}")
}}

// general_term -> general_list
def action_15(s: PStack):PStack = { dbgAction("action_15"); s match {
case GeneralTermsEntry(terms) :: rest => GeneralTermEntry(GeneralTerm(List(Right(terms)))) :: rest
case _ => throw new Exception(s"action_15: got ${s}")
}}

// general_data -> atomic_word
def action_16(s: PStack):PStack = { dbgAction("action_16"); s match {
case TokenEntry(x: LowerWord) :: rest => GeneralDataEntry(GWord(x.data)) :: rest
case TokenEntry(x: SingleQuoted) :: rest => GeneralDataEntry(GWord(x.data)) :: rest
case _ => throw new Exception(s"action_16: got ${s}")
}}

// general_data -> variable
def action_18(s: PStack):PStack = { dbgAction("action_18"); s match {
case TokenEntry(UpperWord(x)) :: rest => GeneralDataEntry(GVar(x)) :: rest
case _ => throw new Exception(s"action_18: got ${s}")
}}

// general_data -> number
def action_19(s: PStack):PStack = { dbgAction("action_19"); s match {
case NumberEntry(x) :: rest => GeneralDataEntry(GNumber(x)) :: rest
case _ => throw new Exception(s"action_19: got ${s}")
}}

// general_data -> "distinct_object"
def action_20(s: PStack):PStack = { dbgAction("action_20"); s match {
case TokenEntry(x : DistinctObject) :: rest => GeneralDataEntry(GDistinct(x.chars)) :: rest
case _ => throw new Exception(s"action_20: got ${s}")
}}

// general_function -> atomic_word "(" general_terms ")"
def action_22(s: PStack):PStack = { dbgAction("action_22"); s match {
case TokenEntry(RightParenthesis) :: GeneralTermsEntry(terms) :: TokenEntry(LeftParenthesis) :: TokenEntry(LowerWord(name)) :: rest =>
  GeneralDataEntry(GFunc(name, terms)) :: rest
case _ => throw new Exception(s"action_22: got ${s}")
}}

// general_terms -> general_term
def action_23(s: PStack):PStack = { dbgAction("action_23"); s match {
case GeneralTermEntry(term) :: rest => GeneralTermsEntry(List(term)) :: rest
case _ => throw new Exception(s"action_23: got ${s}")
}}

// general_terms -> general_term "," general_terms
def action_24(s: PStack):PStack = { dbgAction("action_24"); s match {
case GeneralTermsEntry(terms) :: TokenEntry(Comma) :: GeneralTermEntry(term) :: rest => GeneralTermsEntry(term :: terms) :: rest
case _ => throw new Exception(s"action_24: got ${s}")
}}

// number -> "integer"
def action_26(s: PStack):PStack = { dbgAction("action_26"); s match {
case TokenEntry(x: Integer) :: rest => NumberEntry(IntegerNumber(x.value)) :: rest
case _ => throw new Exception(s"action_26: got ${s}")
}}

// number -> "rational"
def action_27(s: PStack):PStack = { dbgAction("action_27"); s match {
case TokenEntry(Rational(p,q)) :: rest => NumberEntry(RationalNumber(p,q)) :: rest
case _ => throw new Exception(s"action_27: got ${s}")
}}

// number -> "real"
def action_28(s: PStack):PStack = { dbgAction("action_28"); s match {
case TokenEntry(Real(coeff,exp)) :: rest => NumberEntry(DoubleNumber(coeff * Math.pow(10,exp))) :: rest
case _ => throw new Exception(s"action_28: got ${s}")
}}

// formula_data -> "$thf" "(" "thf_formula" ")"
def action_29(s: PStack):PStack = { dbgAction("action_29"); s match {
case TokenEntry(RightParenthesis) :: FormulaEntry(f) :: TokenEntry(LeftParenthesis) :: TokenEntry(DollarWord("$thf")) :: rest =>
  GeneralDataEntry(GFormulaData(THFData(f))) :: rest
case _ => throw new Exception(s"action_29: got ${s}")
}}

// formula_data -> "$fot" "(" "term" ")"
def action_30(s: PStack):PStack = { dbgAction("action_30"); s match {
case TokenEntry(RightParenthesis) :: TermEntry(t) :: TokenEntry(LeftParenthesis) :: TokenEntry(DollarWord("$fot")) :: rest =>
  GeneralDataEntry(GFormulaData(FOTData(t))) :: rest
case _ => throw new Exception(s"action_30: got ${s}")
}}

// general_list -> "[" "]"
def action_31(s: PStack):PStack = { dbgAction("action_31"); s match {
case TokenEntry(RightBracket) :: TokenEntry(LeftBracket) :: rest => GeneralTermsEntry(List.empty) :: rest
case _ => throw new Exception(s"action_31: got ${s}")
}}

// general_list -> "[" general_terms "]"
def action_32(s: PStack):PStack = { dbgAction("action_32"); s match {
case TokenEntry(RightBracket) :: GeneralTermsEntry(terms) :: TokenEntry(LeftBracket) :: rest => GeneralTermsEntry(terms) :: rest
case _ => throw new Exception(s"action_32: got ${s}")
}}

// optional_info -> "," useful_info
def action_33(s: PStack):PStack = { dbgAction("action_33"); s match {
case (x: GeneralTermsEntry) :: TokenEntry(Comma) :: rest => x :: rest
case _ => throw new Exception(s"action_33: got ${s}")
}}

// optional_info -> null
def action_34(s: PStack):PStack = { dbgAction("action_34"); s match {
case rest => GeneralTermsEntry(List.empty) :: rest
}}

// include -> "include" "(" file_name formula_selection ")" "."
def action_37(s: PStack):PStack = { dbgAction("action_37"); s match {
case TokenEntry(Dot) :: TokenEntry(RightParenthesis) :: StringsEntry(formulaSelections) :: TokenEntry(SingleQuoted(fileName)) :: TokenEntry(LeftParenthesis) :: TokenEntry(IncludeToken) :: rest =>
  TPTPEntry(Right((fileName, formulaSelections))) :: rest
case _ => throw new Exception(s"action_37: got ${s}")
}}

// formula_selection -> "," "[" name_list "]"
def action_39(s: PStack):PStack = { dbgAction("action_39"); s match {
case TokenEntry(RightBracket) :: (names: StringsEntry) :: TokenEntry(LeftBracket) :: TokenEntry(Comma) :: rest => names :: rest
case _ => throw new Exception(s"action_39: got ${s}")
}}

// formula_selection -> null
def action_40(s: PStack):PStack = { dbgAction("action_40"); s match {
case rest => StringsEntry(List.empty) :: rest
}}

// name_list -> name
def action_41(s: PStack):PStack = { dbgAction("action_41"); s match {
case StringEntry(str) :: rest => StringsEntry(List(str)) :: rest
case _ => throw new Exception(s"action_41: got ${s}")
}}

// name_list -> name "," name_list
def action_42(s: PStack):PStack = { dbgAction("action_42"); s match {
case StringsEntry(strRest) :: TokenEntry(Comma) :: StringEntry(str) :: rest => StringsEntry(str :: strRest) :: rest
case _ => throw new Exception(s"action_42: got ${s}")
}}

  }
}
