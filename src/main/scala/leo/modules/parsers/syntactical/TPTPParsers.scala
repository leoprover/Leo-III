package leo.modules.parsers.syntactical

import leo.datastructures.tptp._
import leo.modules.parsers.lexical._
import scala.util.parsing.combinator.syntactical.TokenParsers
import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.input.Reader

/**
 * This class offers several (token) parser combinators for parsing TPTP problems,
 * including complete fof, cnf, tff, thf and tpi expressions as described by
 * [[http://www.cs.miami.edu/~tptp/TPTP/SyntaxBNF.html]].
 * The `parse` method can be used to parse an input with respect to the
 * given combinator.
 * E.g., a complete tptp file can be parsed using the `tptpInput` combinator.
 *
 * @author Alexander Steen
 * @since April 2014
 * @see [[leo.modules.parsers.lexical.TPTPLexical]]  for the associated Scanner declaration
 * @see [[leo.datastructures.tptp]] for the data structures the parser generates
 * @note Last update on 22.04.2014
 */
object TPTPParsers extends TokenParsers with PackratParsers {
  type Tokens = TPTPTokens
  val lexical = new TPTPLexical

  /** Methods for parsing and tokenizing whole input streams */

  def parse[Target](input: String, parser: Parser[Target]) = {
    val tokens = new lexical.Scanner(input)
    phrase(parser)(tokens)
  }

  def parse[Target](input: Reader[Char], parser: Parser[Target]) = {
    val tokens = new lexical.Scanner(input)
    phrase(parser)(tokens)
  }

  def tokens(input: String) = {
    new lexical.Scanner(input)
  }

  def tokens(input: Reader[Char]) = {
    new lexical.Scanner(input)
  }

  // From here on the combinators are implemented according to the syntax bnf declaration
  // from http://www.cs.miami.edu/~tptp/TPTP/SyntaxBNF.html
  // (Almost) each single bnf rule is reflected by a combinator declaration, where the name is adjusted
  // to camelCase format. Where possible, several rules are contracted to a single rule.

  // A Packratparser is used when the bnf is left-recursive, such as in `thfOrFormula` and many other.

  import lexical._

  /////////////////////////////////////
  // General combinators
  /////////////////////////////////////

  //  Files
  def tptpFile: Parser[Commons.TPTPInput] = rep(tptpInput) ^^ {Commons.TPTPInput(_)}

  def tptpInput: Parser[Either[Commons.AnnotatedFormula, Commons.Include]] = (annotatedFormula | include) ^^ {
    case e1: Commons.AnnotatedFormula => Left(e1)
    case e2: Commons.Include  => Right(e2)
  }

  // Formula records
  def annotatedFormula: Parser[Commons.AnnotatedFormula] =
    tpiAnnotated | thfAnnotated | tffAnnotated | fofAnnotated | cnfAnnotated

  def tpiAnnotated: Parser[Commons.TPIAnnotated] =
    (elem(TPI) ~ elem(LeftParenthesis)) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ fofFormula ~ annotations <~ elem(RightParenthesis) ~ elem(Dot)  ^^ {
      case name ~ role ~ formula ~ annotations => Commons.TPIAnnotated(name,role,formula,annotations)
    }
  def thfAnnotated: Parser[Commons.THFAnnotated] =
    (elem(THF) ~ elem(LeftParenthesis)) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ thfFormula ~ annotations <~ elem(RightParenthesis) ~ elem(Dot)  ^^ {
      case name ~ role ~ formula ~ annotations => Commons.THFAnnotated(name,role,formula,annotations)
    }
  def tffAnnotated: Parser[Commons.TFFAnnotated] =
    (elem(TFF) ~ elem(LeftParenthesis)) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ tffFormula ~ annotations <~ elem(RightParenthesis) ~ elem(Dot)  ^^ {
      case name ~ role ~ formula ~ annotations => Commons.TFFAnnotated(name,role,formula,annotations)
    }
  def fofAnnotated: Parser[Commons.FOFAnnotated] =
    (elem(FOF) ~ elem(LeftParenthesis)) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ fofFormula ~ annotations <~ elem(RightParenthesis) ~ elem(Dot)  ^^ {
      case name ~ role ~ formula ~ annotations => Commons.FOFAnnotated(name,role,formula,annotations)
    }
  def cnfAnnotated: Parser[Commons.CNFAnnotated] =
    (elem(CNF) ~ elem(LeftParenthesis)) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ cnfFormula ~ annotations <~ elem(RightParenthesis) ~ elem(Dot)  ^^ {
      case name ~ role ~ formula ~ annotations => Commons.CNFAnnotated(name,role,formula,annotations)
    }

  def annotations: Parser[leo.datastructures.tptp.Commons.Annotations] =
    opt(elem(Comma) ~> source ~ optionalInfo) ^^ {
      case None => None
      case Some(src ~ info) => Some((src,info))
    }
  def formulaRole: Parser[String] = elem("Lower word", _.isInstanceOf[LowerWord]) ^^ {_.chars}

  // special formulae
  def thfConnTerm: Parser[thf.Connective] = (
      (thfPairConnective | assocConnective) ^^ {
        case Equals               => thf.Connective(Left(thf.Eq))
        case NotEquals            => thf.Connective(Left(thf.Neq))
        case Leftrightarrow       => thf.Connective(Left(thf.<=>))
        case Rightarrow           => thf.Connective(Left(thf.Impl))
        case Leftarrow            => thf.Connective(Left(thf.<=))
        case Leftrighttildearrow  => thf.Connective(Left(thf.<~>))
        case TildePipe            => thf.Connective(Left(thf.~|))
        case TildeAmpersand       => thf.Connective(Left(thf.~&))
        case Ampersand            => thf.Connective(Left(thf.&))
        case VLine                => thf.Connective(Left(thf.|))
      }
    | thfUnaryConnective ^^ {
        case Tilde                              => thf.Connective(Right(thf.~))
        case Exclamationmark ~ Exclamationmark  => thf.Connective(Right(thf.!!))
        case Questionmark ~ Questionmark        => thf.Connective(Right(thf.??))
      }
  )

  def folInfixUnary: Parser[Commons.Term ~ Commons.Term] =
    term ~ elem(NotEquals) ~ term ^^ {
      case l ~ _ ~ r => this.~(l,r)
    }

  // Connectives THF
  def thfQuantifier: Parser[Any] = (
      elem(Exclamationmark) ~ elem(Arrow)
    | elem(Questionmark) ~ elem(Star)
    | elem(Application) ~ elem(Plus)
    | elem(Application) ~ elem(Minus)
    | elem(Lambda)
    | folQuantifier
  )

  def thfPairConnective: Parser[Token] = (
      elem(Equals)
    | elem(NotEquals)
    | binaryConnective
  )

  def thfUnaryConnective: Parser[Any] = (
      unaryConnective
    | elem(Exclamationmark) ~ elem(Exclamationmark)
    | elem(Questionmark) ~ elem(Questionmark)
  )

  // Connectives TFF and THF
  def subtypeSign: Parser[String] = repN(2,elem(LessSign)) ^^ {_  => ""}

  // Connectives FOF
  def folQuantifier: Parser[Token] = elem(Exclamationmark) | elem(Questionmark)
  def binaryConnective: Parser[Token] = (
      elem(Leftrightarrow)
    | elem(Rightarrow)
    | elem(Leftarrow)
    | elem(Leftrighttildearrow)
    | elem(TildePipe)
    | elem(TildeAmpersand)
  )

  def assocConnective: Parser[Token] = elem(VLine) | elem(Ampersand)
  def unaryConnective: Parser[Token] = elem(Tilde)

  // Gentzen arrow
  def gentzenArrow: Parser[String] = elem(Minus) ~ elem(Minus) ~ elem(Rightarrow) ^^ {_ => ""}

  // Types for tff and thf
  def definedType: Parser[String] = atomicDefinedWord
  def systemType: Parser[String] = atomicSystemWord

  // First-order atoms
  def atomicFormula: Parser[Commons.AtomicFormula] =
    plainAtomicFormula ||| definedPlainFormula ||| definedInfixFormula ||| systemAtomicFormula

  def plainAtomicFormula: Parser[Commons.Plain] = plainTerm ^^ {Commons.Plain(_)}
  def definedPlainFormula: Parser[Commons.DefinedPlain] = definedPlainTerm ^^ {Commons.DefinedPlain(_)}
  def definedInfixFormula: Parser[Commons.Equality] =
    term ~ elem(Equals) ~ term ^^ {
      case t1 ~ _ ~ t2 => Commons.Equality(t1,t2)
    }
  def systemAtomicFormula: Parser[Commons.SystemPlain] = systemTerm ^^ {Commons.SystemPlain(_)}

  // First-order terms
  def term: Parser[Commons.Term] = functionTerm |||
    variable ^^ {Commons.Var(_)} |||
    conditionalTerm |||
    letTerm

  def functionTerm: Parser[Commons.Term] =
    plainTerm |
    definedPlainTerm |
    systemTerm |
    number ^^ {Commons.NumberTerm(_)} |
    elem("Distinct object", _.isInstanceOf[DistinctObject]) ^^ {x => Commons.Distinct(x.chars)}

  def plainTerm: Parser[Commons.Func] =
    constant ~ opt(elem(LeftParenthesis) ~> arguments <~ elem(RightParenthesis)) ^^ {
      case c ~ Some(x) => Commons.Func(c,x)
      case c ~ _       => Commons.Func(c,List())
    }

  def constant: Parser[String] = atomicWord
  def definedPlainTerm: Parser[Commons.DefinedFunc] =
    atomicDefinedWord ~ opt(elem(LeftParenthesis) ~> arguments <~ elem(RightParenthesis)) ^^ {
      case c ~ Some(x) => Commons.DefinedFunc(c,x)
      case c ~ _       => Commons.DefinedFunc(c,List())
    }
  def systemTerm: Parser[Commons.SystemFunc] =
    atomicSystemWord ~ opt(elem(LeftParenthesis) ~> arguments <~ elem(RightParenthesis)) ^^ {
      case c ~ Some(x) => Commons.SystemFunc(c,x)
      case c ~ _       => Commons.SystemFunc(c,List())
    }

  def variable: Parser[Commons.Variable] = elem("Upper word", _.isInstanceOf[UpperWord]) ^^ {_.chars}
  def arguments: Parser[List[Commons.Term]] = rep1sep(term, elem(Comma))

  def conditionalTerm: Parser[Commons.Cond] =
    (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$ite_t"))(_ => "Error in Conditional Term") ~ elem(LeftParenthesis)) ~>
      tffLogicFormula ~ elem(Comma) ~ term ~ elem(Comma) ~ term <~ elem(RightParenthesis) ^^ {
        case formula ~ _ ~ thn ~ _ ~ els => Commons.Cond(formula,thn,els)
    }
  def letTerm: Parser[Commons.Let] = (
      (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$let_ft"))(_ => "Error in Let Term") ~ elem(LeftParenthesis)) ~>
        tffLetFormulaDefn ~ elem(Comma) ~ term <~ elem(RightParenthesis) ^^ {
         case lets ~ _ ~ in => Commons.Let(lets,in)
        }
    | (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$let_tt"))(_ => "Error in Let Term") ~ elem(LeftParenthesis)) ~>
        tffLetTermDefn ~ elem(Comma) ~ term <~ elem(RightParenthesis) ^^ {
          case lets ~ _ ~ in => Commons.Let(lets,in)
        }
    )

  // Formula sources and infos
  def source: Parser[Commons.GeneralTerm] = generalTerm
  def optionalInfo: Parser[List[Commons.GeneralTerm]] = opt(elem(Comma) ~> usefulInfo) ^^ {
    case None => List.empty
    case Some(x) => x
  }

  def usefulInfo: Parser[List[Commons.GeneralTerm]] = generalList

  // Include directives
  def include: Parser[leo.datastructures.tptp.Commons.Include] = (
    (elem(Include) ~ elem(LeftParenthesis)) ~> elem("Single quoted", _.isInstanceOf[SingleQuoted])
      ~ opt((elem(Comma) ~ elem(LeftBracket)) ~> repsep(name,elem(Comma)) <~ elem(RightBracket))
      <~ (elem(RightParenthesis) ~ elem(Dot)) ^^ {
        case SingleQuoted(data) ~ Some(names) => (data, names)
        case SingleQuoted(data) ~ _           => (data, List.empty)
    }
  )
  // Non-logical data (GeneralTerm, General data)
  def generalTerm: Parser[Commons.GeneralTerm] = (
        generalList                             ^^ {x => Commons.GeneralTerm(List(Right(x)))}
    ||| generalData                             ^^ {x => Commons.GeneralTerm(List(Left(x)))}
    ||| generalData ~ elem(Colon) ~ generalTerm ^^ {case data ~ _ ~ gterm => Commons.GeneralTerm(Left(data) :: gterm.term)}
  )

  def generalData: Parser[Commons.GeneralData] = (
      atomicWord                                              ^^ {Commons.GWord(_)}
    ||| generalFunction
    ||| variable                                                ^^ {Commons.GVar(_)}
    ||| number                                                  ^^ {Commons.GNumber(_)}
    ||| elem("Distinct object", _.isInstanceOf[DistinctObject]) ^^ {x => Commons.GDistinct(x.chars)}
    ||| formulaData                                             ^^ {Commons.GFormulaData(_)}
  )

  def generalFunction: Parser[Commons.GFunc] =
    atomicWord ~ elem(LeftParenthesis) ~ generalTerms ~ elem(RightParenthesis) ^^ {
      case name ~ _ ~ args ~ _  => Commons.GFunc(name,args)
    }

  def formulaData: Parser[Commons.FormulaData] = (
      (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$thf"))(_ => "Parse error in formulaData") ~ elem(LeftParenthesis)) ~>
        thfFormula <~ elem(RightParenthesis) ^^ {Commons.THFData(_)}
    | (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$tff"))(_ => "Parse error in formulaData") ~ elem(LeftParenthesis)) ~>
        tffFormula <~ elem(RightParenthesis) ^^ {Commons.TFFData(_)}
    | (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$fof"))(_ => "Parse error in formulaData") ~ elem(LeftParenthesis)) ~>
        fofFormula <~ elem(RightParenthesis) ^^ {Commons.FOFData(_)}
    | (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$cnf"))(_ => "Parse error in formulaData") ~ elem(LeftParenthesis)) ~>
        cnfFormula <~ elem(RightParenthesis) ^^ {Commons.CNFData(_)}
    | (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$fot"))(_ => "Parse error in formulaData") ~ elem(LeftParenthesis)) ~>
        term <~ elem(RightParenthesis) ^^ {Commons.FOTData(_)}
  )

  def generalList: Parser[List[Commons.GeneralTerm]] =
    elem(LeftBracket) ~> opt(generalTerms) <~ elem(RightBracket) ^^ {
      case Some(gt)   => gt
      case _       => List.empty
    }
  def generalTerms: Parser[List[Commons.GeneralTerm]] = rep1sep(generalTerm, elem(Comma))

  // General purpose
  def name: Parser[String] = (
      atomicWord
    | elem("integer", _.isInstanceOf[Integer]) ^^ {_.chars}
  )
  def atomicWord: Parser[String] = (
      elem("lower word", _.isInstanceOf[LowerWord]) ^^ {_.chars}
    | elem("single quoted", _.isInstanceOf[SingleQuoted]) ^^ {_.chars}
  )
  def atomicDefinedWord: Parser[String] = elem("Dollar word", _.isInstanceOf[DollarWord]) ^^ {_.chars}
  def atomicSystemWord: Parser[String] = elem("Dollar Dollar word", _.isInstanceOf[DollarDollarWord]) ^^ {_.chars}
  def number: Parser[Commons.Number] = (
      elem("Integer", _.isInstanceOf[Integer]) ^^ {case i => Commons.IntegerNumber(i.asInstanceOf[Integer].value)}
    | elem("Real", _.isInstanceOf[Real]) ^^ {x => Commons.DoubleNumber((x.asInstanceOf[Real].coeff.toString + "E" + x.asInstanceOf[Real].exp.toString).toDouble)}
    | elem("Rational", _.isInstanceOf[Rational]) ^^ {case r => Commons.RationalNumber(r.asInstanceOf[Rational].p,r.asInstanceOf[Rational].q)}
  )

  def fileName: Parser[String] = elem("single quoted", _.isInstanceOf[SingleQuoted]) ^^ {_.chars}

  //////////////////////////////////////
  // Rules for THF formulae
  //////////////////////////////////////
  def thfFormula: Parser[thf.Formula] = thfLogicFormula ^^ {thf.Logical(_)} | thfSequent
  def thfLogicFormula: Parser[thf.LogicFormula] = thfBinaryFormula ||| thfUnitaryFormula |||
      thfTypeFormula ||| thfSubtype
  def thfBinaryFormula:Parser[thf.LogicFormula] = thfBinaryPair | thfBinaryTuple | thfBinaryType ^^ {thf.BinType(_)}

  def thfBinaryPair:Parser[thf.Binary] = thfUnitaryFormula ~ thfPairConnective ~ thfUnitaryFormula ^^ {
    case left ~ Equals         ~ right => thf.Binary(left, thf.Eq,right)
    case left ~ NotEquals      ~ right => thf.Binary(left, thf.Neq, right)
    case left ~ Leftrightarrow ~ right => thf.Binary(left, thf.<=>, right)
    case left ~ Rightarrow     ~ right => thf.Binary(left, thf.Impl, right)
    case left ~ Leftarrow      ~ right => thf.Binary(left, thf.<=, right)
    case left ~ Leftrighttildearrow ~ right => thf.Binary(left, thf.<~>, right)
    case left ~ TildePipe      ~ right => thf.Binary(left, thf.~|, right)
    case left ~ TildeAmpersand ~ right => thf.Binary(left, thf.~&, right)
  }

  def thfBinaryTuple: Parser[thf.Binary] = thfOrFormula | thfAndFormula | thfApplyFormula

  lazy val thfOrFormula: PackratParser[thf.Binary] = (
        thfUnitaryFormula ~ elem(VLine) ~ thfUnitaryFormula ^^ {case left ~ _ ~ right => thf.Binary(left, thf.|, right)}
    ||| thfOrFormula ~ elem(VLine) ~ thfUnitaryFormula      ^^ {case left ~ _ ~ right => thf.Binary(left, thf.|, right)}
  )

  lazy val thfAndFormula: PackratParser[thf.Binary] = (
        thfUnitaryFormula ~ elem(Ampersand) ~ thfUnitaryFormula ^^ {case left ~ _ ~ right => thf.Binary(left, thf.&, right)}
    ||| thfAndFormula ~ elem(Ampersand) ~ thfUnitaryFormula     ^^ {case left ~ _ ~ right => thf.Binary(left, thf.&, right)}
  )

  lazy val thfApplyFormula: PackratParser[thf.Binary] = (
        thfUnitaryFormula ~ elem(Application) ~ thfUnitaryFormula ^^ {case left ~ _ ~ right => thf.Binary(left, thf.App, right)}
    ||| thfApplyFormula ~ elem(Application) ~ thfUnitaryFormula   ^^ {case left ~ _ ~ right => thf.Binary(left, thf.App, right)}
  )

  def thfUnitaryFormula: Parser[thf.LogicFormula] = (
      thfQuantifiedFormula
    | thfUnaryFormula
    | thfLet
    | thfConditional
    | thfAtom
    | elem(LeftParenthesis) ~> thfLogicFormula <~ elem(RightParenthesis)
  )

  def thfQuantifiedFormula: Parser[thf.Quantified] =
    thfQuantifier ~ elem(LeftBracket) ~ rep1sep(thfVariable, elem(Comma)) ~ elem(RightBracket) ~ elem(Colon) ~ thfUnitaryFormula ^^ {
      case (Exclamationmark ~ Arrow) ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.!>,varList,matrix)
      case (Questionmark ~ Star)     ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.?*,varList,matrix)
      case (Application ~ Plus)      ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.@+,varList,matrix)
      case (Application ~ Minus)     ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.@-,varList,matrix)
      case Exclamationmark           ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.!,varList,matrix)
      case Questionmark              ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.?,varList,matrix)
      case Lambda                    ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.^,varList,matrix)
    }

  def thfVariable: Parser[(Commons.Variable, Option[thf.LogicFormula])] =
    thfTypedVariable | variable ^^ { (_, None)}

  def thfTypedVariable: Parser[(Commons.Variable, Option[thf.LogicFormula])] =
    variable ~ elem(Colon) ~ thfTopLevelType ^^ {
      case vari ~ _ ~ typ => (vari, Some(typ))
    }

  def thfUnaryFormula: Parser[thf.Unary] = thfUnaryConnective ~ elem(LeftParenthesis) ~ thfLogicFormula <~ elem(RightParenthesis) ^^ {
    case Tilde                               ~ _ ~ formula => thf.Unary(thf.~, formula)
    case (Exclamationmark ~ Exclamationmark) ~ _ ~ formula => thf.Unary(thf.!!, formula)
    case (Questionmark ~ Questionmark)       ~ _ ~ formula => thf.Unary(thf.??, formula)
  }

  def thfAtom: Parser[thf.LogicFormula] = term ^^ {thf.Term(_)} | thfConnTerm

  def thfConditional: Parser[thf.Cond] = (
       (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$ite_f"))(_ => "Parse error in thfConditional") ~ elem(LeftParenthesis))
    ~> thfLogicFormula ~ elem(Comma) ~ thfLogicFormula ~ elem(Comma) ~ thfLogicFormula  <~ elem(RightParenthesis) ^^ {
    case cond ~ _ ~ thn ~ _ ~ els => thf.Cond(cond,thn,els)
  }
  )

  def thfLet: Parser[thf.Let] = (
      (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$let_tf"))(_ => "Error in thfLet") ~ elem(LeftParenthesis)) ~>
        thfQuantifiedFormula ~ elem(Comma) ~ thfFormula <~ elem(RightParenthesis) ^^ {
          case let ~ _ ~ in => thf.Let(thf.TermBinding(let), in)
        }
    | (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$let_ff"))(_ => "Error in thfLet") ~ elem(LeftParenthesis)) ~>
        thfQuantifiedFormula ~ elem(Comma) ~ thfFormula <~ elem(RightParenthesis) ^^ {
        case let ~ _ ~ in => thf.Let(thf.FormulaBinding(let), in)
      }
  )

  def thfTypeFormula: Parser[thf.Typed] = thfTypeableFormula ~ elem(Colon) ~ thfTopLevelType ^^ {
    case formula ~ _ ~ typ => thf.Typed(formula, typ)
  }
  def thfTypeableFormula: Parser[thf.LogicFormula] = (
      thfAtom
    | elem(LeftParenthesis) ~> thfLogicFormula <~ elem(RightParenthesis)
  )

  def thfSubtype: Parser[thf.Subtype] = constant ~ subtypeSign ~ constant ^^ {
    case l ~ _ ~ r => thf.Subtype(l,r)
  }

  def thfTopLevelType: Parser[thf.LogicFormula] = thfLogicFormula
  def thfUnitaryType: Parser[thf.LogicFormula] = thfUnitaryFormula
  def thfBinaryType: Parser[thf.BinaryType] = thfMappingType | thfXProdType | thfUnionType

  lazy val thfMappingType: PackratParser[thf.->] = (
        thfUnitaryType ~ elem(Arrow) ~ thfUnitaryType ^^ {case l ~ _ ~ r => thf.->(List(l,r))}
    ||| thfUnitaryType ~ elem(Arrow) ~ thfMappingType ^^ {case l ~ _ ~ typ => thf.->(l::typ.t)}
  )

  lazy val thfXProdType: PackratParser[thf.*] = (
        thfUnitaryType ~ elem(Star) ~ thfUnitaryType ^^ {case l ~ _ ~ r => thf.*(List(l,r))}
    ||| thfXProdType   ~ elem(Star) ~ thfUnitaryType ^^ {case typ ~ _ ~ r => thf.*(typ.t ++ List(r))}
     //This may need to be optimized
  )

  lazy val thfUnionType: PackratParser[thf.+] = (
        thfUnitaryType ~ elem(Plus) ~ thfUnitaryType ^^ {case l ~ _ ~ r => thf.+(List(l,r))}
    ||| thfUnionType   ~ elem(Plus) ~ thfUnitaryType ^^ {case typ ~ _ ~ r => thf.+(typ.t ++ List(r))}
     //This may need to be optimized
  )

  def thfSequent: Parser[thf.Sequent] = (
        thfTuple ~ gentzenArrow ~ thfTuple           ^^ {case t1 ~ _ ~ t2 => thf.Sequent(t1,t2)}
    ||| elem(LeftParenthesis) ~> thfSequent <~ elem(RightParenthesis)
  )

  def thfTuple: Parser[List[thf.LogicFormula]] = repsep(thfLogicFormula, elem(Comma))

  //////////////////////////////////////
  // Rules for TFF formulae
  //////////////////////////////////////
  def tffFormula: Parser[tff.Formula] = tffLogicFormula ^^ {tff.Logical(_)} | tffTypedAtom | tffSequent

  def tffLogicFormula: Parser[tff.LogicFormula] = tffBinaryFormula | tffUnitaryFormula
  def tffBinaryFormula: Parser[tff.Binary] = tffBinaryNonAssoc | tffBinaryAssoc
  def tffBinaryNonAssoc: Parser[tff.Binary] = tffUnitaryFormula ~ binaryConnective ~ tffUnitaryFormula ^^ {
    case left ~ Leftrightarrow ~ right      => tff.Binary(left,tff.<=>,right)
    case left ~ Rightarrow ~ right          => tff.Binary(left,tff.Impl,right)
    case left ~ Leftarrow ~ right           => tff.Binary(left,tff.<=,right)
    case left ~ Leftrighttildearrow ~ right => tff.Binary(left,tff.<~>,right)
    case left ~ TildePipe ~ right           => tff.Binary(left,tff.~|,right)
    case left ~ TildeAmpersand ~ right      => tff.Binary(left,tff.~&,right)
  }

  def tffBinaryAssoc: Parser[tff.Binary] = tffOrFormula | tffAndFormula

  lazy val tffOrFormula: PackratParser[tff.Binary] = (
        tffUnitaryFormula ~ elem(VLine) ~ tffUnitaryFormula ^^ {case left ~ _ ~ right => tff.Binary(left,tff.|,right)}
    ||| tffOrFormula      ~ elem(VLine) ~ tffUnitaryFormula ^^ {case left ~ _ ~ right => tff.Binary(left,tff.|,right)}
  )

  lazy val tffAndFormula: PackratParser[tff.Binary] = (
        tffUnitaryFormula ~ elem(Ampersand) ~ tffUnitaryFormula ^^ {case left ~ _ ~ right => tff.Binary(left,tff.&,right)}
    ||| tffAndFormula     ~ elem(Ampersand) ~ tffUnitaryFormula ^^ {case left ~ _ ~ right => tff.Binary(left,tff.&,right)}
  )

  def tffUnitaryFormula: Parser[tff.LogicFormula] = (
      tffQuantifiedFormula
    | tffUnaryFormula
    | tffConditional
    | tffLet
    | atomicFormula ^^ {tff.Atomic(_)}
    | elem(LeftParenthesis) ~> tffLogicFormula <~ elem(RightParenthesis)
  )

  def tffQuantifiedFormula: Parser[tff.Quantified] =
    folQuantifier ~ elem(LeftBracket) ~ rep1sep(tffVariable, elem(Comma)) ~ elem(RightBracket) ~ elem(Colon) ~ tffUnitaryFormula ^^ {
      case Exclamationmark ~ _ ~ vars ~ _ ~ _ ~ matrix => tff.Quantified(tff.!,vars,matrix)
      case Questionmark    ~ _ ~ vars ~ _ ~ _ ~ matrix => tff.Quantified(tff.?,vars,matrix)
    }

  def tffVariable: Parser[(Commons.Variable,Option[tff.AtomicType])] = tffTypedVariable | variable ^^ {(_,None)}

  def tffTypedVariable: Parser[(Commons.Variable,Option[tff.AtomicType])] =
    variable ~ elem(Colon) ~ tffAtomicType ^^ {case variable ~ _ ~ typ  => (variable, Some(typ))}

  def tffUnaryFormula: Parser[tff.LogicFormula] = (
      unaryConnective ~ tffUnitaryFormula ^^ {case Tilde ~ formula => tff.Unary(tff.Not, formula)}
    | folInfixUnary                       ^^ {case left  ~ right   => tff.Inequality(left, right)}
  )

  def tffConditional: Parser[tff.Cond] =
    (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$ite_f"))(_ => "Error in tffConditional") ~ elem(LeftParenthesis)) ~>
    tffLogicFormula ~ elem(Comma) ~ tffLogicFormula ~ elem(Comma) ~ tffLogicFormula <~ elem(RightParenthesis) ^^ {
      case cond ~ _ ~ thn ~ _ ~ els => tff.Cond(cond,thn,els)
    }

  def tffLet: Parser[tff.Let] = (
      (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$let_tf"))(_ => "Error in tffLet") ~ elem(LeftParenthesis)) ~>
      tffLetTermDefn ~ elem(Comma) ~ tffFormula <~ elem(RightParenthesis) ^^ {
        case lets ~ _ ~ in => tff.Let(lets, in)
      }
    ||| (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$let_ff"))(_ => "Error in tffLet") ~ elem(LeftParenthesis)) ~>
      tffLetFormulaDefn ~ elem(Comma) ~ tffFormula <~ elem(RightParenthesis) ^^ {
        case lets ~ _ ~ in => tff.Let(lets, in)
      }
  )

  def tffLetTermDefn: Parser[tff.TermBinding] = (
        elem(Exclamationmark) ~ elem(LeftBracket) ~ rep1sep(tffVariable, elem(Comma)) ~ elem(RightBracket) ~ elem(Colon) ~ tffLetTermDefn ^^ {
           case _ ~ _ ~ vars ~ _ ~ _ ~ defn =>  tff.TermBinding(vars ++ defn.varList, defn.left, defn.right)
        }
    ||| tffLetTermBinding ^^ {case l ~ r => tff.TermBinding(List(), l, r)}
  )

  def tffLetTermBinding: Parser[Commons.Term ~ Commons.Term] = (
      term ~ elem(Equals) ~ term ^^ {case left ~ _ ~ right => this.~(left,right)}
    | elem(LeftParenthesis) ~> tffLetTermBinding <~ elem(RightParenthesis)
  )

  def tffLetFormulaDefn: Parser[tff.FormulaBinding] = (
        elem(Exclamationmark) ~ elem(LeftBracket) ~ rep1sep(tffVariable, elem(Comma)) ~ elem(RightBracket) ~ elem(Colon) ~ tffLetFormulaDefn ^^ {
           case _ ~ _ ~ vars ~ _ ~ _ ~ defn =>  tff.FormulaBinding(vars ++ defn.varList, defn.left, defn.right)
        }
    ||| tffLetFormulaBinding ^^ {case l ~ r => tff.FormulaBinding(List(), l, r)}
  )

  def tffLetFormulaBinding: Parser[tff.Atomic ~ tff.LogicFormula] = (
      atomicFormula ~ elem(Leftrightarrow) ~ tffUnitaryFormula ^^ {case left ~ _ ~ right => this.~(tff.Atomic(left),right)}
    ||| elem(LeftParenthesis) ~> tffLetFormulaBinding <~ elem(RightParenthesis)
  )

  def tffSequent: Parser[tff.Sequent] = (
        tffTuple ~ gentzenArrow ~ tffTuple ^^ {case t1 ~ _ ~ t2 => tff.Sequent(t1,t2)}
    ||| elem(LeftParenthesis) ~> tffSequent <~ elem(RightParenthesis)
  )

  def tffTuple: Parser[List[tff.LogicFormula]] = repsep(tffLogicFormula, elem(Comma))

  def tffTypedAtom: Parser[tff.TypedAtom] = (
      tffUntypedAtom ~ elem(Colon) ~ tffTopLevelType ^^ {case atom ~ _ ~ typ => tff.TypedAtom(atom, typ)}
    | elem(LeftParenthesis) ~> tffTypedAtom <~ elem(RightParenthesis)
  )

  def tffUntypedAtom: Parser[String] = atomicWord | atomicSystemWord

  def tffTopLevelType: Parser[tff.Type] = (
      tffAtomicType
    ||| tffMappingType
    ||| tffQuantifiedType
    ||| elem(LeftParenthesis) ~> tffTopLevelType <~ elem(RightParenthesis)
  )

  def tffQuantifiedType: Parser[tff.QuantifiedType] =
    (elem(Exclamationmark) ~ elem(Arrow)) ~>
     elem(LeftBracket) ~ rep1sep(tffTypedVariable, elem(Comma)) ~ elem(RightBracket) ~ elem(Colon) ~ tffMonotype ^^ {
      case _ ~ vars ~ _ ~ _ ~ typ => tff.QuantifiedType(vars, typ)
    }

  def tffMonotype: Parser[tff.Type] = (
      tffAtomicType
    | elem(LeftParenthesis) ~> tffMappingType <~ elem(RightParenthesis)
  )

  def tffUnitaryType: Parser[tff.Type] = (
      tffAtomicType
    | elem(LeftParenthesis) ~> tffXProdType <~ elem(RightParenthesis)
  )

  def tffAtomicType: Parser[tff.AtomicType] = (
      (atomicWord | definedType | variable) ^^ {tff.AtomicType(_, List())}
    | atomicWord ~ elem(LeftParenthesis) ~ tffTypeArguments <~ elem(RightParenthesis) ^^ {
          case name ~ _ ~ args => tff.AtomicType(name, args)
      }
  )

  def tffTypeArguments: Parser[List[tff.AtomicType]] = rep1sep(tffAtomicType, elem(Comma))

  def tffMappingType: Parser[tff.->] =
    tffUnitaryType ~ elem(Arrow) ~ tffAtomicType    ^^ {case l ~ _ ~ r => tff.->(List(l,r))}

  lazy val tffXProdType: PackratParser[tff.*] = (
        tffUnitaryType ~ elem(Star) ~ tffAtomicType ^^ {case l ~ _ ~ r => tff.*(List(l,r))}
    ||| tffXProdType   ~ elem(Star) ~ tffAtomicType ^^ {case l ~ _ ~ r => tff.*(l.t ++ List(r))}
  )

  //////////////////////////////////////
  // Rules for FOF formulae
  //////////////////////////////////////
  def fofFormula: Parser[fof.Formula] = fofLogicFormula ^^ {fof.Logical(_)} | fofSequent

  def fofLogicFormula: Parser[fof.LogicFormula] = fofBinaryFormula ||| fofUnitaryFormula

  def fofBinaryFormula: Parser[fof.Binary] = fofBinaryNonAssoc ||| fofBinaryAssoc
  def fofBinaryNonAssoc: Parser[fof.Binary] = fofUnitaryFormula ~ binaryConnective ~ fofUnitaryFormula ^^ {
    case left ~ Leftrightarrow      ~ right => fof.Binary(left,fof.<=>,right)
    case left ~ Rightarrow          ~ right => fof.Binary(left,fof.Impl,right)
    case left ~ Leftarrow           ~ right => fof.Binary(left,fof.<=,right)
    case left ~ Leftrighttildearrow ~ right => fof.Binary(left,fof.<~>,right)
    case left ~ TildePipe           ~ right => fof.Binary(left,fof.~|,right)
    case left ~ TildeAmpersand      ~ right => fof.Binary(left,fof.~&,right)
  }
  def fofBinaryAssoc: Parser[fof.Binary] = fofOrFormula | fofAndFormula

  lazy val fofOrFormula: PackratParser[fof.Binary] = (
        fofUnitaryFormula ~ elem(VLine) ~ fofUnitaryFormula ^^ {case left ~ _ ~ right => fof.Binary(left,fof.|,right)}
    ||| fofOrFormula      ~ elem(VLine) ~ fofUnitaryFormula ^^ {case left ~ _ ~ right => fof.Binary(left,fof.|,right)}
  )

  lazy val fofAndFormula: PackratParser[fof.Binary] = (
        fofUnitaryFormula ~ elem(Ampersand) ~ fofUnitaryFormula ^^ {case left ~ _ ~ right => fof.Binary(left,fof.&,right)}
    ||| fofAndFormula     ~ elem(Ampersand) ~ fofUnitaryFormula ^^ {case left ~ _ ~ right => fof.Binary(left,fof.&,right)}
  )

  def fofUnitaryFormula: Parser[fof.LogicFormula] = (
      elem(LeftParenthesis) ~> fofLogicFormula <~ elem(RightParenthesis)
    | fofQuantifiedFormula
    | fofUnaryFormula
    | atomicFormula ^^ {fof.Atomic(_)}
  )

  def fofQuantifiedFormula: Parser[fof.Quantified] =
    folQuantifier ~ elem(LeftBracket) ~ rep1sep(variable,elem(Comma)) ~ elem(RightBracket) ~ elem(Colon) ~ fofUnitaryFormula ^^ {
      case Exclamationmark ~ _ ~ vars ~ _ ~ _ ~ matrix => fof.Quantified(fof.!,vars,matrix)
      case Questionmark    ~ _ ~ vars ~ _ ~ _ ~ matrix => fof.Quantified(fof.?,vars,matrix)
    }

  def fofUnaryFormula: Parser[fof.LogicFormula] = (
      unaryConnective ~ fofUnitaryFormula ^^      {case Tilde ~ formula => fof.Unary(fof.Not, formula)}
    | folInfixUnary                       ^^      {case left  ~ right   => fof.Inequality(left,right)}
  )

  def fofSequent: Parser[fof.Sequent] = (
        fofTuple ~ gentzenArrow ~ fofTuple ^^ {case t1 ~ _ ~ t2 => fof.Sequent(t1,t2)}
    ||| elem(LeftParenthesis) ~> fofSequent <~ elem(RightParenthesis)
  )

  def fofTuple: Parser[List[fof.LogicFormula]] =
    elem(LeftBracket) ~> repsep(fofLogicFormula, elem(Comma)) <~ elem(RightBracket)

  //////////////////////////////////////
  // Rules for CNF formulae
  //////////////////////////////////////
  def cnfFormula: Parser[cnf.Formula] = (
        (elem(LeftParenthesis) ~> disjunction <~ elem(RightParenthesis)
    ||| disjunction) ^^ {cnf.Formula(_)}
  )

  lazy val disjunction: PackratParser[List[cnf.Literal]] = (
        literal                             ^^ {List(_)}
    ||| disjunction ~ elem(VLine) ~ literal ^^ {case dis ~ _ ~ l => dis ++ List(l)}
  )

  def literal: Parser[cnf.Literal] = (
        atomicFormula                ^^ {cnf.Positive(_)}
    ||| elem(Tilde) ~> atomicFormula ^^ {cnf.Negative(_)}
    ||| folInfixUnary                ^^ {case left ~ right => cnf.Inequality(left,right)}
  )
}
