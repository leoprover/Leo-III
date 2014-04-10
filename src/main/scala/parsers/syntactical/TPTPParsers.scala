package parsers.syntactical

import tptp._
import parsers.lexical._
import scala.util.parsing.combinator.syntactical.TokenParsers
import scala.util.parsing.combinator.PackratParsers

class TPTPParsers extends TokenParsers with PackratParsers {
  type Tokens = TPTPTokens
  val lexical = new TPTPLexical


  def parse[Target](input: String, parser: Parser[Target]) = {
    val tokens = new lexical.Scanner(input)
    phrase(parser)(tokens)
  }

  import lexical._

  //  Files
    def tptpFile: Parser[Commons.TPTPInput] = rep(tptpInput) ^^ {tptp.Commons.TPTPInput(_)}

    def tptpInput: Parser[Either[Commons.AnnotatedFormula, Commons.Include]] = (annotatedFormula ||| include) ^^ {
      case e1: Commons.AnnotatedFormula => Left(e1)
      case e2: Commons.Include  => Right(e2)
    }

    // Formula records
    def annotatedFormula: Parser[Commons.AnnotatedFormula] =
      tpiAnnotated ||| thfAnnotated ||| tffAnnotated ||| fofAnnotated ||| cnfAnnotated

    def tpiAnnotated: Parser[Commons.TPIAnnotated] =
      elem(TPI) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ fofFormula ~ annotations <~ elem(LeftParenthesis) ~ elem(Dot)  ^^ {
        case name ~ role ~ formula ~ annotations => Commons.TPIAnnotated(name,role,formula,annotations)
      }
    def thfAnnotated: Parser[Commons.THFAnnotated] =
      elem(THF) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ thfFormula ~ annotations <~ elem(LeftParenthesis) ~ elem(Dot)  ^^ {
        case name ~ role ~ formula ~ annotations => Commons.THFAnnotated(name,role,formula,annotations)
      }
    def tffAnnotated: Parser[Commons.TFFAnnotated] =
      elem(TFF) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ tffFormula ~ annotations <~ elem(LeftParenthesis) ~ elem(Dot)  ^^ {
        case name ~ role ~ formula ~ annotations => Commons.TFFAnnotated(name,role,formula,annotations)
      }
    def fofAnnotated: Parser[Commons.FOFAnnotated] =
      elem(FOF) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ fofFormula ~ annotations <~ elem(LeftParenthesis) ~ elem(Dot)  ^^ {
        case name ~ role ~ formula ~ annotations => Commons.FOFAnnotated(name,role,formula,annotations)
      }
    def cnfAnnotated: Parser[Commons.CNFAnnotated] =
      elem(CNF) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ cnfFormula ~ annotations <~ elem(LeftParenthesis) ~ elem(Dot)  ^^ {
        case name ~ role ~ formula ~ annotations => Commons.CNFAnnotated(name,role,formula,annotations)
      }

    def annotations: Parser[tptp.Commons.Annotations] =
      opt(elem(Comma) ~> source ~ optionalInfo) ^^ {
        case None => None
        case Some(src ~ info) => Some((src,info))
      }
    def formulaRole: Parser[String] = elem("Lower word", _.isInstanceOf[LowerWord]) ^^ {_.chars}

    // special formulae
    def thfConnTerm: Parser[thf.Connective] = (thfPairConnective | assocConnective) ^^ { _ match {
      case Equals => thf.Connective(Left(thf.Eq))
      case NotEquals => thf.Connective(Left(thf.Neq))
      case Leftrightarrow => thf.Connective(Left(thf.<=>))
      case Rightarrow => thf.Connective(Left(thf.Impl))
      case Leftarrow => thf.Connective(Left(thf.<=))
      case Leftrighttildearrow => thf.Connective(Left(thf.<~>))
      case TildePipe => thf.Connective(Left(thf.~|))
      case TildeAmpersand => thf.Connective(Left(thf.~&))
      case Ampersand => thf.Connective(Left(thf.&))
      case VLine => thf.Connective(Left(thf.|))
    } } | thfUnaryConnective ^^ {
      case Tilde                              => thf.Connective(Right(thf.~))
      case Exclamationmark ~ Exclamationmark  => thf.Connective(Right(thf.!!))
      case Questionmark ~ Questionmark        => thf.Connective(Right(thf.??))
    }

    def folInfixUnary: Parser[Commons.Term ~ Commons.Term] =
      term ~ elem(NotEquals) ~ term ^^ {
        case l ~ _ ~ r => this.~(l,r)
      }

    // Connectives THF
    def thfQuantifier: Parser[Any] = (
        folQuantifier
      | elem(Lambda)
      | elem(Exclamationmark) ~ elem(Arrow)
      | elem(Questionmark) ~ elem(Star)
      | elem(Application) ~ elem(Plus)
      | elem(Application) ~ elem(Minus)
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
      plainTerm |||
      definedPlainTerm |||
      systemTerm |||
      number ^^ {Commons.Number(_)} |||
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
    def letTerm: Parser[Commons.Let] =
      (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$let_ft"))(_ => "Error in Let Term") ~ elem(LeftParenthesis)) ~>
        (tffLetFormulaDefn ||| tffLetTermDefn) ~ elem(Comma) ~ term <~ elem(RightParenthesis) ^^ {
          case lets ~ _ ~ in => Commons.Let(lets,in)
      }

    // Formula sources and infos
    def source: Parser[Commons.GeneralTerm] = generalTerm
    def optionalInfo: Parser[List[Commons.GeneralTerm]] = opt(elem(Comma) ~> usefulInfo) ^^ {
      case None => List.empty
      case Some(x) => x
    }

    def usefulInfo: Parser[List[Commons.GeneralTerm]] = generalList

    // Include directives
    def include: Parser[tptp.Commons.Include] = (
      (elem(Include) ~ elem(LeftParenthesis)) ~> elem("Single quoted", _.isInstanceOf[SingleQuoted])
        ~ opt((elem(Comma) ~ elem(LeftBracket)) ~> repsep(name,elem(Comma)) <~ elem(RightBracket))
        <~ (elem(RightParenthesis) ~ elem(Dot)) ^^ {
          case SingleQuoted(data) ~ Some(names) => (data.substring(1,data.length-1), names)
          case SingleQuoted(data) ~ _           => (data.substring(1,data.length-1), List.empty)
      }
    )
    // Non-logical data (GeneralTerm, General data)
    def generalTerm: Parser[tptp.Commons.GeneralTerm] =
      generalList ^^ {x => Commons.GeneralTerm(List(Right(x)))} |||
      generalData ^^ {x => Commons.GeneralTerm(List(Left(x)))} |||
      generalData ~ elem(Colon) ~ generalTerm ^^ {
        case data ~ _ ~ gterm => Commons.GeneralTerm(Left(data) :: gterm.term)
      }

    def generalData: Parser[tptp.Commons.GeneralData] = (
          atomicWord                                              ^^ {tptp.Commons.GWord(_)}
      ||| generalFunction
      ||| variable                                                ^^ {tptp.Commons.GVar(_)}
      ||| number                                                  ^^ {tptp.Commons.GNumber(_)}
      ||| elem("Distinct object", _.isInstanceOf[DistinctObject]) ^^ {x => tptp.Commons.GDistinct(x.chars)}
      ||| formulaData                                             ^^ {tptp.Commons.GFormulaData(_)}
    )

    def generalFunction: Parser[tptp.Commons.GFunc] =
      atomicWord ~ elem(LeftParenthesis) ~ generalTerms ~ elem(RightParenthesis) ^^ {
        case name ~ _ ~ args ~ _  => tptp.Commons.GFunc(name,args)
      }

    def formulaData: Parser[tptp.Commons.FormulaData] = (
        (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$thf"))(_ => "Parse error in formulaData") ~ elem(LeftParenthesis)) ~>
          thfFormula <~ elem(RightParenthesis) ^^ {tptp.Commons.THFData(_)}
      | (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$tff"))(_ => "Parse error in formulaData") ~ elem(LeftParenthesis)) ~>
          tffFormula <~ elem(RightParenthesis) ^^ {tptp.Commons.TFFData(_)}
      | (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$fof"))(_ => "Parse error in formulaData") ~ elem(LeftParenthesis)) ~>
          fofFormula <~ elem(RightParenthesis) ^^ {tptp.Commons.FOFData(_)}
      | (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$cnf"))(_ => "Parse error in formulaData") ~ elem(LeftParenthesis)) ~>
          cnfFormula <~ elem(RightParenthesis) ^^ {tptp.Commons.CNFData(_)}
      | (acceptIf(x => x.isInstanceOf[DollarWord] && x.chars.equals("$fot"))(_ => "Parse error in formulaData") ~ elem(LeftParenthesis)) ~>
          term <~ elem(RightParenthesis) ^^ {tptp.Commons.FOTData(_)}
    )

    def generalList: Parser[List[tptp.Commons.GeneralTerm]] =
      elem(LeftBracket) ~> opt(generalTerms) <~ elem(RightBracket) ^^ {
        case Some(gt)   => gt
        case _       => List.empty
      }
    def generalTerms: Parser[List[tptp.Commons.GeneralTerm]] = rep1sep(generalTerm, elem(Comma))

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
    def number: Parser[Double] = (
        elem("Integer", _.isInstanceOf[Integer]) ^^ {_.asInstanceOf[Integer].value.toDouble}
      | elem("Real", _.isInstanceOf[Real]) ^^ {_.asInstanceOf[Real].value}
      | elem("Rational", _.isInstanceOf[Rational]) ^^ {x => (x.asInstanceOf[Rational]).p / (x.asInstanceOf[Rational]).q}
    )

    def fileName: Parser[String] = elem("single quoted", _.isInstanceOf[SingleQuoted]) ^^ {_.chars}

    /**
     * THF BNFs
     */
    def thfFormula: Parser[thf.Formula] = thfLogicFormula ^^ {thf.Logical(_)} | thfSequent
    def thfLogicFormula: Parser[thf.LogicFormula] = thfBinaryFormula ||| thfUnitaryFormula |||
        thfTypeFormula ||| thfSubtype
    def thfBinaryFormula:Parser[thf.LogicFormula] = thfBinaryPair ||| thfBinaryTuple ||| thfBinaryType ^^ {thf.BinType(_)}

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
          thfUnitaryFormula ~ elem(Lambda) ~ thfUnitaryFormula ^^ {case left ~ _ ~ right => thf.Binary(left, thf.App, right)}
      ||| thfApplyFormula ~ elem(Lambda) ~ thfUnitaryFormula   ^^ {case left ~ _ ~ right => thf.Binary(left, thf.App, right)}
    )

    def thfUnitaryFormula: Parser[thf.LogicFormula] = (
        thfQuantifiedFormula
      | thfUnaryFormula
      | thfAtom
      | thfConditional
      | elem(LeftParenthesis) ~> thfLogicFormula <~ elem(RightParenthesis)
    )

    def thfQuantifiedFormula: Parser[thf.Quantified] =
      thfQuantifier ~ elem(LeftBracket) ~ rep1sep(thfVariable, elem(Comma)) ~ elem(RightBracket) ~ elem(Colon) ~ thfUnitaryFormula ^^ {
        case Exclamationmark           ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.!,varList,matrix)
        case Questionmark              ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.?,varList,matrix)
        case Lambda                    ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.^,varList,matrix)
        case (Exclamationmark ~ Arrow) ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.!>,varList,matrix)
        case (Questionmark ~ Star)     ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.?*,varList,matrix)
        case (Application ~ Plus)      ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.@+,varList,matrix)
        case (Application ~ Minus)     ~ _ ~ varList ~ _ ~ _ ~ matrix => thf.Quantified(thf.@-,varList,matrix)
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

    def thfTypeFormula: Parser[thf.Typed] = thfTypeableFormula ~ ":" ~ thfTopLevelType ^^ {
      case formula ~ _ ~ typ => thf.Typed(formula, typ)
    }
    def thfTypeableFormula: Parser[thf.LogicFormula] = thfAtom | "(" ~> thfLogicFormula <~ ")"
    def thfSubtype: Parser[thf.Subtype] = constant ~ subtypeSign ~ constant ^^ {
      case l ~ _ ~ r => thf.Subtype(l,r)
    }
    def thfTopLevelType: Parser[thf.LogicFormula] = thfLogicFormula
    def thfUnitaryType: Parser[thf.LogicFormula] = thfUnitaryFormula
    def thfBinaryType: Parser[thf.BinaryType] = thfMappingType | thfXProdType | thfUnionType

    lazy val thfMappingType: PackratParser[thf.->] = thfUnitaryType ~ ">" ~ thfUnitaryType ^^ {
      case l ~ _ ~ r => thf.->(List(l,r))
    } ||| thfUnitaryType ~ ">" ~ thfMappingType ^^ {
      case l ~ _ ~ typ => thf.->(l::typ.t)
    }
    lazy val thfXProdType: PackratParser[thf.*] = thfUnitaryType ~ "*" ~ thfUnitaryType ^^ {
      case l ~ _ ~ r => thf.*(List(l,r))
    } ||| thfXProdType ~ "*" ~ thfUnitaryType ^^ {
      case typ ~ _ ~ r => thf.*(typ.t ++ List(r)) //This may need to be optimized
    }
    lazy val thfUnionType: PackratParser[thf.+] = thfUnitaryType ~ "+" ~ thfUnitaryType ^^ {
      case l ~ _ ~ r => thf.+(List(l,r))
    } ||| thfUnionType ~ "+" ~ thfUnitaryType ^^ {
      case typ ~ _ ~ r => thf.+(typ.t ++ List(r)) //This may need to be optimized
    }

    def thfSequent: Parser[thf.Sequent] =
      thfTuple ~ gentzenArrow ~ thfTuple ^^ {
        case t1 ~ _ ~ t2 => thf.Sequent(t1,t2)
      } ||| "(" ~> thfSequent <~ ")"
    def thfTuple: Parser[List[thf.LogicFormula]] =
      repsep(thfLogicFormula, ",")
    /**
     * TFF BNFs
     */
    def tffFormula: Parser[tff.Formula] = tffLogicFormula ^^ {tff.Logical(_)} ||| tffTypedAtom ||| tffSequent

    def tffLogicFormula: Parser[tff.LogicFormula] = tffBinaryFormula | tffUnitaryFormula
    def tffBinaryFormula: Parser[tff.Binary] = tffBinaryNonAssoc | tffBinaryAssoc
    def tffBinaryNonAssoc: Parser[tff.Binary]  = tffUnitaryFormula ~ binaryConnective ~ tffUnitaryFormula ^^ {
      case left ~ "<=>" ~ right => tff.Binary(left,tff.<=>,right)
      case left ~ "=>" ~ right => tff.Binary(left,tff.Impl,right)
      case left ~ "<=" ~ right => tff.Binary(left,tff.<=,right)
      case left ~ "<~>" ~ right => tff.Binary(left,tff.<~>,right)
      case left ~ "~|" ~ right => tff.Binary(left,tff.~|,right)
      case left ~ "~&" ~ right => tff.Binary(left,tff.~&,right)
    }
    def tffBinaryAssoc: Parser[tff.Binary] = tffOrFormula | tffAndFormula
    lazy val tffOrFormula: PackratParser[tff.Binary] = tffUnitaryFormula ~ "|" ~ tffUnitaryFormula ^^ {
      case left ~ "|" ~ right => tff.Binary(left,tff.|,right)
    } |||
      tffOrFormula ~ "|" ~ tffUnitaryFormula ^^ {
        case left ~ "|" ~ right => tff.Binary(left,tff.|,right)
      }
    lazy val tffAndFormula: PackratParser[tff.Binary] = tffUnitaryFormula ~ "&" ~ tffUnitaryFormula ^^ {
      case left ~ "&" ~ right => tff.Binary(left,tff.&,right)
    } |||
      tffAndFormula ~ "&" ~ tffUnitaryFormula ^^ {
        case left ~ "&" ~ right => tff.Binary(left,tff.&,right)
      }

    def tffUnitaryFormula: Parser[tff.LogicFormula] = tffQuantifiedFormula | tffUnaryFormula |
      atomicFormula ^^ {tff.Atomic(_)} | tffConditional | tffLet |
      "(" ~> tffLogicFormula <~ ")"
    def tffQuantifiedFormula: Parser[tff.Quantified] =
      folQuantifier ~ "[" ~ rep1sep(tffVariable, ",") ~ "]" ~ ":" ~ tffUnitaryFormula ^^{
        case "!" ~ "[" ~ vars ~ "]" ~ ":" ~ matrix => tff.Quantified(tff.!,vars,matrix)
        case "?" ~ "[" ~ vars ~ "]" ~ ":" ~ matrix => tff.Quantified(tff.?,vars,matrix)
      }
    def tffVariable: Parser[(Commons.Variable,Option[tff.AtomicType])] = tffTypedVariable | variable ^^ {(_,None)}
    def tffTypedVariable: Parser[(Commons.Variable,Option[tff.AtomicType])] =
      variable ~ ":" ~ tffAtomicType ^^ {
        case variable ~ _ ~ typ  => (variable, Some(typ))
      }
    def tffUnaryFormula: Parser[tff.LogicFormula] = unaryConnective ~ tffUnitaryFormula ^^ {
      case "~" ~ formula => tff.Unary(tff.Not, formula)
    } |
    folInfixUnary ^^ {
      case left ~ right => tff.Inequality(left, right)
    }
    def tffConditional: Parser[tff.Cond] =
      "$ite_f(" ~> tffLogicFormula ~ "," ~ tffLogicFormula ~ "," ~ tffLogicFormula <~ ")" ^^ {
        case cond ~ _ ~ thn ~ _ ~ els => tff.Cond(cond,thn,els)
      }

    def tffLet: Parser[tff.Let] = "$let_tf(" ~> tffLetTermDefn ~ "," ~ tffFormula <~ ")" ^^ {
      case lets ~ _ ~ in => tff.Let(lets, in)
    } | "$let_ff(" ~> tffLetFormulaDefn ~ "," ~ tffFormula <~ ")" ^^ {
      case lets ~ _ ~ in => tff.Let(lets, in)
    }

    def tffLetTermDefn: Parser[tff.TermBinding] = "!" ~ "[" ~ rep1sep(tffVariable, ",") ~ "]" ~ ":" ~ tffLetTermDefn ^^ {
      case _ ~ _ ~ vars ~ _ ~ _ ~ defn =>  tff.TermBinding(vars ++ defn.varList, defn.left, defn.right)
    } ||| tffLetTermBinding ^^ {
      case l ~ r => tff.TermBinding(List(), l, r)
    }
    def tffLetTermBinding: Parser[Commons.Term ~ Commons.Term] = term ~ "=" ~ term ^^ {
      case left ~ _ ~ right => this.~(left,right)
    } | "(" ~> tffLetTermBinding <~ ")"

    def tffLetFormulaDefn: Parser[tff.FormulaBinding] = "!" ~ "[" ~ rep1sep(tffVariable, ",") ~ "]" ~ ":" ~ tffLetFormulaDefn ^^ {
      case _ ~ _ ~ vars ~ _ ~ _ ~ defn =>  tff.FormulaBinding(vars ++ defn.varList, defn.left, defn.right)
    } ||| tffLetFormulaBinding ^^ {
      case l ~ r => tff.FormulaBinding(List(), l, r)
    }
    def tffLetFormulaBinding: Parser[tff.Atomic ~ tff.LogicFormula] = atomicFormula ~ "=" ~ tffUnitaryFormula ^^ {
      case left ~ _ ~ right => this.~(tff.Atomic(left),right)
    } | "(" ~> tffLetFormulaBinding <~ ")"


    def tffSequent: Parser[tff.Sequent] =  tffTuple ~ gentzenArrow ~ tffTuple ^^ {
      case t1 ~ _ ~ t2 => tff.Sequent(t1,t2)
    } ||| "(" ~> tffSequent <~ ")"
    def tffTuple: Parser[List[tff.LogicFormula]] = repsep(tffLogicFormula, ",")

    def tffTypedAtom: Parser[tff.TypedAtom] = tffUntypedAtom ~ ":" ~ tffTopLevelType ^^ {
      case atom ~ _ ~ typ => tff.TypedAtom(atom, typ)
    }
    def tffUntypedAtom: Parser[String] = atomicWord | atomicSystemWord

    def tffTopLevelType: Parser[tff.Type] = tffAtomicType | tffMappingType | tffQuantifiedType |
      "(" ~> tffTopLevelType <~ ")"

    def tffQuantifiedType: Parser[tff.QuantifiedType] =
      "!>" ~> "[" ~ rep1sep(tffTypedVariable, ",")  ~ "]" ~ ":" ~ tffMonotype ^^ {
        case _ ~ vars ~ _ ~ _ ~ typ => tff.QuantifiedType(vars, typ)
      }

    def tffMonotype: Parser[tff.Type] = (
        tffAtomicType
      | elem(LeftParenthesis) ~> tffMappingType <~ elem(RightParenthesis)
    )

    def tffUnitaryType: Parser[tff.Type] = (
        tffAtomicType
      | elem(LeftParenthesis) ~> tffXProdType <~ elem(RightParanthesis)
    )

    def tffAtomicType: Parser[tff.AtomicType] =
      (atomicWord | definedType | variable) ^^ {tff.AtomicType(_, List())} |
      atomicWord ~ "(" ~ tffTypeArguments <~ ")" ^^ {
        case name ~ _ ~ args => tff.AtomicType(name, args)
      }

    def tffTypeArguments: Parser[List[tff.AtomicType]] = rep1sep(tffAtomicType, elem(Comma))

    def tffMappingType: Parser[tff.->] =
      tffUnitaryType ~ elem(Arrow) ~ tffAtomicType    ^^ {case l ~ _ ~ r => tff.->(List(l,r))}

    lazy val tffXProdType: PackratParser[tff.*] = (
          tffUnitaryType ~ elem(Star) ~ tffAtomicType ^^ {case l ~ _ ~ r => tff.*(List(l,r))}
      ||| tffXProdType   ~ elem(Star) ~ tffAtomicType ^^ {case l ~ _ ~ r => tff.*(l.t ++ List(r))}
    )

    /**
     * FOF BNFs
     */
    def fofFormula: Parser[fof.Formula] = fofLogicFormula ^^ {fof.Logical(_)} ||| fofSequent

    def fofLogicFormula: Parser[fof.LogicFormula] = fofBinaryFormula ||| fofUnitaryFormula

    def fofBinaryFormula: Parser[fof.Binary] = fofBinaryNonAssoc ||| fofBinaryAssoc
    def fofBinaryNonAssoc: Parser[fof.Binary] = fofUnitaryFormula ~ binaryConnective ~ fofUnitaryFormula ^^ {
      case left ~ "<=>" ~ right => fof.Binary(left,fof.<=>,right)
      case left ~ "=>" ~ right => fof.Binary(left,fof.Impl,right)
      case left ~ "<=" ~ right => fof.Binary(left,fof.<=,right)
      case left ~ "<~>" ~ right => fof.Binary(left,fof.<~>,right)
      case left ~ "~|" ~ right => fof.Binary(left,fof.~|,right)
      case left ~ "~&" ~ right => fof.Binary(left,fof.~&,right)
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
      folQuantifier ~ elem(LeftBracket) ~ rep1sep(variable,",") ~ elem(RightBracket) ~ elem(Colon) ~ fofUnitaryFormula ^^ {
        case "!" ~ _ ~ vars ~ _ ~ _ ~ matrix => fof.Quantified(fof.!,vars,matrix)
        case "?" ~ _ ~ vars ~ _ ~ _ ~ matrix => fof.Quantified(fof.?,vars,matrix)
      }

    def fofUnaryFormula: Parser[fof.LogicFormula] = (
        unaryConnective ~ fofUnitaryFormula ^^      {case "~" ~ formula => fof.Unary(fof.Not, formula)}
      | folInfixUnary                       ^^      {case left ~ right => fof.Inequality(left,right)}
    )

    def fofSequent: Parser[fof.Sequent] =
      fofTuple ~ gentzenArrow ~ fofTuple ^^ {
        case t1 ~ _ ~ t2 => fof.Sequent(t1,t2)
      } ||| elem(LeftParenthesis) ~> fofSequent <~ elem(RightParenthesis)
    def fofTuple: Parser[List[fof.LogicFormula]] =
      elem(LeftBracket) ~> repsep(fofLogicFormula, ",") <~ elem(RightBracket)

    /**
     * CNF formula BNFs
     */
    def cnfFormula: Parser[cnf.Formula] =
      (elem(LeftParenthesis) ~> disjunction <~ elem(RightParanthesis) ||| disjunction) ^^ {cnf.Formula(_)}
    lazy val disjunction: PackratParser[List[cnf.Literal]] =
      literal ^^ {List(_)} ||| disjunction ~ elem(VLine) ~ literal ^^ {
        case dis ~ _ ~ l => dis ++ List(l)
      }
    def literal: Parser[cnf.Literal] =
      atomicFormula ^^ {cnf.Positive(_)} |||
      elem(Tilde) ~> atomicFormula ^^ {cnf.Negative(_)} |||
      folInfixUnary ^^ {
        case left ~ right => cnf.Inequality(left,right)
      }
}

