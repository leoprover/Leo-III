package leo.modules.parsers

import antlr.tptpParser
import leo.datastructures.tptp.Commons._
import scala.collection.JavaConverters._
/**
  * Created by lex on 14.12.16.
  */
object TPTPASTConstructor {
  final def tptpFile(ctx: tptpParser.Tptp_fileContext): TPTPInput = {
    val input = ctx.tptp_input()
    TPTPInput(input.asScala.map(tptpInput))
  }
  final def tptpInput(ctx: tptpParser.Tptp_inputContext): Either[AnnotatedFormula, Include] = {
    if (ctx.annotated_formula() != null) {
      Left(annotatedFormula(ctx.annotated_formula()))
    } else Right(include(ctx.include()))
  }
  final def include(ctx: tptpParser.IncludeContext): Include = {
    val filename = ctx.file_name().getText
    val formulaSelection = ctx.formula_selection().name().asScala.map(_.getText)
    (filename, formulaSelection)
  }
  final def annotatedFormula(ctx: tptpParser.Annotated_formulaContext): AnnotatedFormula = {
    if (ctx.fof_annotated() != null) {
      val annotated = ctx.fof_annotated()
      val name = annotated.name().getText
      val annotation = annotations(annotated.annotations())
      val formula = fofFormula(annotated.fof_formula())
      FOFAnnotated(name, role(annotated.formula_role()), formula, annotation)
    } else if (ctx.tff_annotated() != null) {
      val annotated = ctx.tff_annotated()
      val name = annotated.name().getText
      val annotation = annotations(annotated.annotations())
      val formula = tffFormula(annotated.tff_formula())
      TFFAnnotated(name, role(annotated.formula_role()), formula, annotation)
    } else if (ctx.thf_annotated() != null) {
      val annotated = ctx.thf_annotated()
      val name = annotated.name().getText
      val annotation = annotations(annotated.annotations())
      val formula = thfFormula(annotated.thf_formula())
      THFAnnotated(name, role(annotated.formula_role()), formula, annotation)
    } else throw new IllegalArgumentException
  }
  final def role(ctx: tptpParser.Formula_roleContext): Role = ctx.getText
  final def annotations(ctx: tptpParser.AnnotationsContext): Annotations = {
    if (ctx == null) None
    else {
      lazy val source = generalTerm(ctx.source().general_term())
      if (ctx.optional_info() != null) {
        val optional_info = ctx.optional_info().general_list().general_term().asScala.map(generalTerm)
        Some((source, optional_info))
      } else Some((source, Seq()))
    }
  }
  final def generalTerm(ctx: tptpParser.General_termContext): GeneralTerm = {
    ???
  }
  final def generalData(ctx: tptpParser.General_dataContext): GeneralData = {
    if (ctx.atomic_word() != null) {
      GWord(ctx.atomic_word().getText)
    } else if (ctx.variable() != null) {
      GVar(ctx.variable().getText)
    } else if (ctx.number() != null) {
      GNumber(number(ctx.number()))
    } else if (ctx.Distinct_object() != null) {
      GDistinct(ctx.Distinct_object().getText)
    } else if (ctx.formula_data() != null) {
      GFormulaData(formulaData(ctx.formula_data()))
    } else if (ctx.general_function() != null) {
      val fun = ctx.general_function().atomic_word().getText
      val args = ctx.general_function().general_term().asScala.map(generalTerm)
      GFunc(fun, args)
    } else throw new IllegalArgumentException
  }
  final def formulaData(ctx: tptpParser.Formula_dataContext): FormulaData = {
    if (ctx.fof_formula() != null) {
      FOFData(fofFormula(ctx.fof_formula()))
    } else if (ctx.tff_formula() != null) {
      TFFData(tffFormula(ctx.tff_formula()))
    } else if (ctx.thf_formula() != null) {
      THFData(thfFormula(ctx.thf_formula()))
    } else if (ctx.term() != null) {
      FOTData(term(ctx.term()))
    } else throw new IllegalArgumentException
  }
  // Numbers
  final def number(ctx: tptpParser.NumberContext): Number = ???
  ////////////////////////////////////////////////////////
  /// FOF
  ////////////////////////////////////////////////////////
  import leo.datastructures.tptp.fof
  final def fofFormula(ctx: tptpParser.Fof_formulaContext): fof.Formula = ???

  // term stuff
  final def term(ctx: tptpParser.TermContext): Term = {
    if (ctx.conditional_term() != null) {
      val cond = tffLogicFormula(ctx.conditional_term().tff_logic_formula())
      val thn = term(ctx.conditional_term().term(0))
      val els = term(ctx.conditional_term().term(1))
      Cond(cond, thn, els)
    } else if (ctx.let_term() != null) {
      val let = ctx.let_term()
      val in = term(let.term())
      if (let.tff_let_formula_defns() != null) {
        val binding = ???
        Let(binding, in)
      } else if (let.tff_let_term_defns() != null) {
        val binding = ???
        Let(binding, in)
      } else throw new IllegalArgumentException
    } else if (ctx.variable() != null) {
      Var(ctx.variable().getText)
    } else if (ctx.function_term() != null) {
      val fun = ctx.function_term()
      if (fun.plain_term() != null) {
        val plain = fun.plain_term()
        val f = plain.functor().atomic_word().getText
        if (plain.arguments() != null) {
          val args = plain.arguments().term().asScala.map(term)
          Func(f, args)
        } else {
          Func(f, Seq())
        }
      } else if (fun.defined_term() != null) {
        val defined = fun.defined_term()
        if (defined.defined_atom() != null) {
          if (defined.defined_atom().Distinct_object() != null)
            Distinct(defined.defined_atom().Distinct_object().getText)
          else if (defined.defined_atom().number() != null) {
            val n = number(defined.defined_atom().number())
            NumberTerm(n)
          } else throw new IllegalArgumentException
        } else if (defined.defined_atomic_term() != null) {
          val definedPlain = defined.defined_atomic_term().defined_plain_term()
          val f = definedPlain.defined_functor().atomic_defined_word().getText
          if (definedPlain.arguments() != null) {
            val args = definedPlain.arguments().term().asScala.map(term)
            DefinedFunc(f, args)
          } else
            DefinedFunc(f, Seq())
        } else throw new IllegalArgumentException
      } else if(fun.system_term() != null) {
        val system = fun.system_term()
        val f = system.system_functor().getText
        if (system.arguments() != null) {
          val args = system.arguments().term().asScala.map(term)
          SystemFunc(f, args)
        } else {
          SystemFunc(f, Seq())
        }
      } else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }
  ////////////////////////////////////////////////////////
  /// THF
  ////////////////////////////////////////////////////////
  import leo.datastructures.tptp.thf
  final def thfFormula(ctx: tptpParser.Thf_formulaContext): thf.Formula = {
    if (ctx.thf_logic_formula() != null) thf.Logical(thfLogicFormula(ctx.thf_logic_formula()))
    else if (ctx.thf_sequent() != null) thfSequent(ctx.thf_sequent())
    else throw new IllegalArgumentException
  }
  final def thfSequent(ctx: tptpParser.Thf_sequentContext): thf.Sequent = {
    if (ctx.thf_sequent() != null) thfSequent(ctx.thf_sequent())
    else if (ctx.thf_tuple() != null) {
      val left = ctx.thf_tuple(0).thf_formula_list().thf_logic_formula().asScala.map(thfLogicFormula)
      val right = ctx.thf_tuple(1).thf_formula_list().thf_logic_formula().asScala.map(thfLogicFormula)
      thf.Sequent(left, right)
    } else throw new IllegalArgumentException
  }
  final def thfLogicFormula(ctx: tptpParser.Thf_logic_formulaContext): thf.LogicFormula = {
    if (ctx.thf_binary_formula() != null) {
      val binary = ctx.thf_binary_formula()
      if (binary.thf_binary_pair() != null) {
        val pair = binary.thf_binary_pair()
        val left = thfUnitary(pair.thf_unitary_formula(0))
        val right = thfUnitary(pair.thf_unitary_formula(1))
        val connective = pair.thf_pair_connective()
        if (connective.Assignment() != null) thf.Binary(left, thf.:=, right)
        else if (connective.Infix_equality() != null) thf.Binary(left, thf.Eq, right)
        else if (connective.Infix_inequality() != null) thf.Binary(left, thf.Neq, right)
        else if (connective.binary_connective() != null) {
          val bincon = connective.binary_connective()
          if (bincon.If() != null) thf.Binary(left, thf.<=, right)
          else if (bincon.Iff() != null) thf.Binary(left, thf.<=>, right)
          else if (bincon.Impl() != null) thf.Binary(left, thf.Impl, right)
          else if (bincon.Nand() != null) thf.Binary(left, thf.~&, right)
          else if (bincon.Niff() != null) thf.Binary(left, thf.<~>, right)
          else if (bincon.Nor() != null) thf.Binary(left, thf.~|, right)
          else throw new IllegalArgumentException
        } else throw new IllegalArgumentException
      } else if (binary.thf_binary_tuple() != null) {
        val tuple = binary.thf_binary_tuple()
        if (tuple.thf_and_formula() != null)
          thfAnd(tuple.thf_and_formula())
        else if (tuple.thf_or_formula() != null)
          thfOr(tuple.thf_or_formula())
        else if (tuple.thf_apply_formula() != null)
          thfApply(tuple.thf_apply_formula())
        else throw new IllegalArgumentException
      } else throw new IllegalArgumentException
    } else if (ctx.thf_unitary_formula() != null) {
      thfUnitary(ctx.thf_unitary_formula())
    } else if (ctx.thf_subtype() != null) {
      val left = ctx.thf_subtype().constant(0).getText
      val right = ctx.thf_subtype().constant(1).getText
      thf.Subtype(left,right)
    } else if (ctx.thf_type_formula() != null) {
      val formula0 = ctx.thf_type_formula().thf_typeable_formula()
      val typ = thfTopLevelType(ctx.thf_type_formula().thf_top_level_type())
      if (formula0.thf_atom() != null) {
        val formula = thfAtom(formula0.thf_atom())
        thf.Typed(formula, typ)
      } else if (formula0.thf_logic_formula() != null)
        thf.Typed(thfLogicFormula(formula0.thf_logic_formula()), typ)
      else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }

  final def thfAnd(ctx: tptpParser.Thf_and_formulaContext): thf.LogicFormula = ???
  final def thfOr(ctx: tptpParser.Thf_or_formulaContext): thf.LogicFormula = ???
  final def thfApply(ctx: tptpParser.Thf_apply_formulaContext): thf.LogicFormula = ???

  final def thfUnitary(ctx: tptpParser.Thf_unitary_formulaContext): thf.LogicFormula = ???
  final def thfAtom(ctx: tptpParser.Thf_atomContext): thf.LogicFormula = {
    if (ctx.variable() != null) thf.Term(Var(ctx.variable().getText))
    else if (ctx.thf_function() != null) {
      val thfFun = ctx.thf_function()
      if (thfFun.thf_plain_term() != null) {
        val fun = thfFun.thf_plain_term().functor().getText
        if (thfFun.thf_plain_term().thf_arguments() == null) thf.Term(Func(fun, Seq()))
        else thf.Term(Func(fun, ???)) // FIXME: Term not applicable
      } else if (thfFun.thf_defined_term() != null) {
        ???
      } else if (thfFun.thf_system_term() != null) {
        ???
      } else throw new IllegalArgumentException
    } else if (ctx.thf_conn_term() != null) {
      val conn = ctx.thf_conn_term()
      if (conn.assoc_connective() != null) {
        if (conn.assoc_connective().And() != null) thf.Connective(Left(thf.&))
        else if (conn.assoc_connective().Or() != null) thf.Connective(Left(thf.|))
        else throw new IllegalArgumentException
      } else if (conn.thf_pair_connective() != null) {
        ???
      } else if (conn.thf_unary_connective() != null) {
        ???
      } else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }
  final def thfFunction(ctx: tptpParser.Thf_functionContext): thf.LogicFormula = ???

  final def thfTopLevelType(ctx: tptpParser.Thf_top_level_typeContext): thf.LogicFormula = ???
  ////////////////////////////////////////////////////////
  /// TFF
  ////////////////////////////////////////////////////////
  import leo.datastructures.tptp.tff
  final def tffFormula(ctx: tptpParser.Tff_formulaContext): tff.Formula = ???
  final def tffLogicFormula(ctx: tptpParser.Tff_logic_formulaContext): tff.LogicFormula = ???
}
