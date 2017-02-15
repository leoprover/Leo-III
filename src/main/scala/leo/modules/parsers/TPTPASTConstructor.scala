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
    val filename = ctx.file_name().Single_quoted().getText.tail.init
    if (ctx.formula_selection() != null) {
      val formulaSelection = ctx.formula_selection().name().asScala.map(_.getText)
      (filename, formulaSelection)
    } else (filename, Seq())
  }
  final def getName(ctx: tptpParser.NameContext): String = {
    if (ctx.Integer() != null) ctx.Integer().getText
    else if (ctx.atomic_word() != null) atomicWord(ctx.atomic_word())
    else throw new IllegalArgumentException
  }
  final def annotatedFormula(ctx: tptpParser.Annotated_formulaContext): AnnotatedFormula = {
    if (ctx.fof_annotated() != null) {
      val annotated = ctx.fof_annotated()
      val name = getName(annotated.name())
      val annotation = annotations(annotated.annotations())
      val formula = fofFormula(annotated.fof_formula())
      FOFAnnotated(name, role(annotated.formula_role()), formula, annotation)
    } else if (ctx.tff_annotated() != null) {
      val annotated = ctx.tff_annotated()
      val name = getName(annotated.name())
      val annotation = annotations(annotated.annotations())
      val formula = tffFormula(annotated.tff_formula())
      TFFAnnotated(name, role(annotated.formula_role()), formula, annotation)
    } else if (ctx.thf_annotated() != null) {
      val annotated = ctx.thf_annotated()
      val name = getName(annotated.name())
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
    if (ctx.general_data() != null) {
      val data = generalData(ctx.general_data())
      if (ctx.general_term() != null) {
        val convertedGT = generalTerm(ctx.general_term())
        GeneralTerm(Left(data) +: convertedGT.term)
      } else GeneralTerm(Seq(Left(data)))
    } else if (ctx.general_list() != null) {
      GeneralTerm(Seq(Right(ctx.general_list().general_term().asScala.map(generalTerm))))
    } else throw new IllegalArgumentException
  }
  final def atomicWord(ctx: tptpParser.Atomic_wordContext): String = {
    if (ctx.Lower_word() != null) ctx.Lower_word().getText
    else if (ctx.Single_quoted() != null) ctx.Single_quoted().getText.tail.init
    else throw new IllegalArgumentException
  }
  final def generalData(ctx: tptpParser.General_dataContext): GeneralData = {
    if (ctx.atomic_word() != null) {
      GWord(atomicWord(ctx.atomic_word()))
    } else if (ctx.variable() != null) {
      GVar(ctx.variable().getText)
    } else if (ctx.number() != null) {
      GNumber(number(ctx.number()))
    } else if (ctx.Distinct_object() != null) {
      GDistinct(ctx.Distinct_object().getText)
    } else if (ctx.formula_data() != null) {
      GFormulaData(formulaData(ctx.formula_data()))
    } else if (ctx.general_function() != null) {
      val fun = atomicWord(ctx.general_function().atomic_word())
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
  final def number(ctx: tptpParser.NumberContext): Number = {
    if (ctx.Integer() != null) {
      IntegerNumber(ctx.Integer().getText.toInt)
    } else if (ctx.Rational() != null) {
      val input = ctx.Rational().getText
      val slashIdx = input.indexOf('/')
      val numerator = input.substring(0,slashIdx).toInt
      val denominator = input.substring(slashIdx+1).toInt
      RationalNumber(numerator, denominator)
    } else if (ctx.Real() != null) {
      DoubleNumber(ctx.Real().getText.toDouble)
    } else throw new IllegalArgumentException
  }
  ////////////////////////////////////////////////////////
  /// FOF
  ////////////////////////////////////////////////////////
  import leo.datastructures.tptp.fof
  final def fofFormula(ctx: tptpParser.Fof_formulaContext): fof.Formula = {
    if (ctx.fof_logic_formula() != null) fof.Logical(fofLogicFormula(ctx.fof_logic_formula()))
    else if (ctx.fof_sequent() != null) fofSequent(ctx.fof_sequent())
    else throw new IllegalArgumentException
  }
  final def fofSequent(ctx: tptpParser.Fof_sequentContext): fof.Sequent = {
    if (ctx.fof_sequent() != null) fofSequent(ctx.fof_sequent())
    else if (ctx.fof_formula_tuple() != null) {
      val left = ctx.fof_formula_tuple(0).fof_formula_tuple_list().fof_logic_formula().asScala
      val right = ctx.fof_formula_tuple(1).fof_formula_tuple_list().fof_logic_formula().asScala
      fof.Sequent(left.map(fofLogicFormula), right.map(fofLogicFormula))
    } else throw new IllegalArgumentException
  }

  final def fofLogicFormula(ctx: tptpParser.Fof_logic_formulaContext): fof.LogicFormula = {
    if (ctx.fof_binary_formula() != null) {
      val binary = ctx.fof_binary_formula()
      if (binary.fof_binary_assoc() != null) {
        val assoc = binary.fof_binary_assoc()
        if (assoc.fof_and_formula() != null) {
          fofAnd(assoc.fof_and_formula())
        } else if (assoc.fof_or_formula() != null) {
          fofOr(assoc.fof_or_formula())
        } else throw new IllegalArgumentException
      } else if (binary.fof_binary_nonassoc() != null) {
        val nonassoc = binary.fof_binary_nonassoc()
        val connective = fofBinaryConnective(nonassoc.binary_connective())
        val left = fofUnitary(nonassoc.fof_unitary_formula(0))
        val right = fofUnitary(nonassoc.fof_unitary_formula(1))
        fof.Binary(left, connective, right)
      } else throw new IllegalArgumentException
    } else if (ctx.fof_unitary_formula() != null) {
      fofUnitary(ctx.fof_unitary_formula())
    } else throw new IllegalArgumentException
  }
  final def fofBinaryConnective(ctx: tptpParser.Binary_connectiveContext): fof.BinaryConnective = {
    if (ctx.If() != null) fof.<=
    else if (ctx.Iff() != null) fof.<=>
    else if (ctx.Impl() != null) fof.Impl
    else if (ctx.Nand() != null) fof.~&
    else if (ctx.Niff() != null) fof.<~>
    else if (ctx.Nor() != null) fof.~|
    else throw new IllegalArgumentException
  }
  final def fofAnd(ctx: tptpParser.Fof_and_formulaContext): fof.LogicFormula = {
    if (ctx.fof_and_formula()  != null) {
      val left = fofAnd(ctx.fof_and_formula())
      val right = fofUnitary(ctx.fof_unitary_formula(0))
      fof.Binary(left, fof.&, right)
    } else {
      val left = fofUnitary(ctx.fof_unitary_formula(0))
      val right = fofUnitary(ctx.fof_unitary_formula(1))
      fof.Binary(left, fof.&, right)
    }
  }
  final def fofOr(ctx: tptpParser.Fof_or_formulaContext): fof.LogicFormula = {
    if (ctx.fof_or_formula()  != null) {
      val left = fofOr(ctx.fof_or_formula())
      val right = fofUnitary(ctx.fof_unitary_formula(0))
      fof.Binary(left, fof.|, right)
    } else {
      val left = fofUnitary(ctx.fof_unitary_formula(0))
      val right = fofUnitary(ctx.fof_unitary_formula(1))
      fof.Binary(left, fof.|, right)
    }
  }

  final def fofUnitary(ctx: tptpParser.Fof_unitary_formulaContext): fof.LogicFormula = {
    if (ctx.fof_logic_formula() != null) fofLogicFormula(ctx.fof_logic_formula())
    else if (ctx.atomic_formula() != null) {
      atomicFormula(ctx.atomic_formula())
    } else if (ctx.fof_quantified_formula() != null) {
      val matrix = fofUnitary(ctx.fof_quantified_formula().fof_unitary_formula())
      val quantifier = fofQuantifier(ctx.fof_quantified_formula().fol_quantifier())
      val varlist = ctx.fof_quantified_formula().fof_variable_list().variable().asScala.map(fofVariable)
      fof.Quantified(quantifier, varlist, matrix)
    } else if (ctx.fof_unary_formula() != null) {
      val unary = ctx.fof_unary_formula()
      if (unary.fol_infix_unary() != null) {
        val left = term(unary.fol_infix_unary().term(0))
        val right = term(unary.fol_infix_unary().term(1))
        fof.Inequality(left,right)
      } else {
        // Not is the only unary connective
        assert(unary.unary_connective().Not() != null)
        val body = fofUnitary(unary.fof_unitary_formula())
        fof.Unary(fof.Not, body)
      }
    } else throw new IllegalArgumentException
  }
  final def fofQuantifier(ctx: tptpParser.Fol_quantifierContext): fof.Quantifier = {
    if (ctx.Forall() != null) fof.!
    else if (ctx.Exists() != null) fof.?
    else throw new IllegalArgumentException
  }
  final def fofVariable(ctx: tptpParser.VariableContext): Variable = ctx.Upper_word().getText

  final def atomicFormula(ctx: tptpParser.Atomic_formulaContext): fof.LogicFormula = {
    if (ctx.plain_atomic_formula() != null) {
      val plain = ctx.plain_atomic_formula()
      val func = atomicWord(plain.plain_term().functor().atomic_word())
      if (plain.plain_term().arguments() == null) fof.Atomic(Plain(Func(func, Seq())))
      else {
        val args = plain.plain_term().arguments().term().asScala.map(term)
        fof.Atomic(Plain(Func(func, args)))
      }
    } else if (ctx.defined_atomic_formula() != null) {
      val defined = ctx.defined_atomic_formula()
      if (defined.defined_infix_formula() != null) {
        // Equality is the only infix connective here
        assert(defined.defined_infix_formula().defined_infix_pred().Infix_equality() != null)
        val left = term(defined.defined_infix_formula().term(0))
        val right = term(defined.defined_infix_formula().term(1))
        fof.Atomic(Equality(left,right))
      } else if (defined.defined_plain_formula() != null) {
        val definedPlain = defined.defined_plain_formula().defined_plain_term()
        val func = definedPlain.defined_functor().getText
        if (definedPlain.arguments() == null) fof.Atomic(DefinedPlain(DefinedFunc(func, Seq())))
        else {
          val args = definedPlain.arguments().term().asScala.map(term)
          fof.Atomic(DefinedPlain(DefinedFunc(func, args)))
        }
      } else throw new IllegalArgumentException
    } else if (ctx.system_atomic_formula() != null) {
      val system = ctx.system_atomic_formula()
      val func = system.system_term().system_functor().getText
      if (system.system_term().arguments() == null) fof.Atomic(SystemPlain(SystemFunc(func, Seq())))
      else {
        val args = system.system_term().arguments().term.asScala.map(term)
        fof.Atomic(SystemPlain(SystemFunc(func, args)))
      }
    } else throw new IllegalArgumentException
  }

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
        val f = atomicWord(plain.functor().atomic_word())
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
        val connective = thfPairConnective(pair.thf_pair_connective())
        thf.Binary(left, connective, right)
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
      val left = atomicWord(ctx.thf_subtype().constant(0).functor().atomic_word())
      val right = atomicWord(ctx.thf_subtype().constant(1).functor().atomic_word())
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
  final def thfPairConnective(ctx: tptpParser.Thf_pair_connectiveContext): thf.BinaryConnective = {
    val connective = ctx
    if (connective.Assignment() != null) thf.:=
    else if (connective.Infix_equality() != null) thf.Eq
    else if (connective.Infix_inequality() != null) thf.Neq
    else if (connective.binary_connective() != null) {
      val bincon = connective.binary_connective()
      if (bincon.If() != null) thf.<=
      else if (bincon.Iff() != null) thf.<=>
      else if (bincon.Impl() != null) thf.Impl
      else if (bincon.Nand() != null) thf.~&
      else if (bincon.Niff() != null) thf.<~>
      else if (bincon.Nor() != null) thf.~|
      else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }
  final def thfUnaryConnective(ctx: tptpParser.Thf_unary_connectiveContext): thf.UnaryConnective = {
    if (ctx.unary_connective() != null) {
      assert(ctx.unary_connective().Not() != null) // Not is the only unary connective
      thf.~
    } else if (ctx.th1_unary_connective() != null) {
      val th1con = ctx.th1_unary_connective()
      if (th1con.ForallComb() != null) thf.!!
      else if (th1con.ExistsComb() != null) thf.??
      else if (th1con.ChoiceComb() != null) thf.@@+
      else if (th1con.DescriptionComb() != null) thf.@@-
      else if (th1con.EqComb() != null) thf.@@=
      else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }

  final def thfAnd(ctx: tptpParser.Thf_and_formulaContext): thf.LogicFormula = {
    if (ctx.thf_and_formula()  != null) {
      val left = thfAnd(ctx.thf_and_formula())
      val right = thfUnitary(ctx.thf_unitary_formula(0))
      thf.Binary(left, thf.&, right)
    } else {
      val left = thfUnitary(ctx.thf_unitary_formula(0))
      val right = thfUnitary(ctx.thf_unitary_formula(1))
      thf.Binary(left, thf.&, right)
    }
  }
  final def thfOr(ctx: tptpParser.Thf_or_formulaContext): thf.LogicFormula = {
    if (ctx.thf_or_formula()  != null) {
      val left = thfOr(ctx.thf_or_formula())
      val right = thfUnitary(ctx.thf_unitary_formula(0))
      thf.Binary(left, thf.|, right)
    } else {
      val left = thfUnitary(ctx.thf_unitary_formula(0))
      val right = thfUnitary(ctx.thf_unitary_formula(1))
      thf.Binary(left, thf.|, right)
    }
  }
  final def thfApply(ctx: tptpParser.Thf_apply_formulaContext): thf.LogicFormula = {
    if (ctx.thf_apply_formula()  != null) {
      val left = thfApply(ctx.thf_apply_formula())
      val right = thfUnitary(ctx.thf_unitary_formula(0))
      thf.Binary(left, thf.App, right)
    } else {
      val left = thfUnitary(ctx.thf_unitary_formula(0))
      val right = thfUnitary(ctx.thf_unitary_formula(1))
      thf.Binary(left, thf.App, right)
    }
  }

  final def thfTuple(ctx: tptpParser.Thf_tupleContext): thf.Tuple = {
    val formulaList = ctx.thf_formula_list().thf_logic_formula().asScala.map(thfLogicFormula)
    thf.Tuple(formulaList)
  }
  final def thfQuantifier(ctx: tptpParser.Thf_quantifierContext): thf.Quantifier = {
    if (ctx.fol_quantifier() != null) {
      val q = ctx.fol_quantifier()
      if (q.Forall() != null) thf.!
      else if (q.Exists() != null) thf.?
      else throw new IllegalArgumentException
    } else if (ctx.th0_quantifier() != null) {
      val q = ctx.th0_quantifier()
      if (q.Choice() != null) thf.@+
      else if (q.Description() != null) thf.@-
      else if (q.Lambda() != null) thf.^
      else throw new IllegalArgumentException
    } else if (ctx.th1_quantifier() != null) {
      val q = ctx.th1_quantifier()
      if (q.TyForall() != null) thf.!>
      else if (q.TyExists() != null) thf.?*
      else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }
  final def thfVariable(ctx: tptpParser.Thf_variableContext): (Variable, Option[thf.LogicFormula]) = {
    val variable = ctx.variable().Upper_word().getText
    val typ = thfTopLevelType(ctx.thf_top_level_type())
    (variable, Some(typ))
  }
  final def thfUnitary(ctx: tptpParser.Thf_unitary_formulaContext): thf.LogicFormula = {
    if (ctx.thf_atom() != null) {
      thfAtom(ctx.thf_atom())
    } else if (ctx.thf_conditional() != null) {
      val condition  = thfLogicFormula(ctx.thf_conditional().thf_logic_formula(0))
      val thn = thfLogicFormula(ctx.thf_conditional().thf_logic_formula(1))
      val els = thfLogicFormula(ctx.thf_conditional().thf_logic_formula(1))
      thf.Cond(condition, thn, els)
    } else if (ctx.thf_let() != null) {
      val binding = thf.Tuple(Seq(transformUnitaryLetBinding(ctx.thf_let().thf_unitary_formula())))
      val body =thfFormula(ctx.thf_let().thf_formula())
      thf.NewLet(binding, body)
    } else if (ctx.thf_logic_formula() != null) {
      thfLogicFormula(ctx.thf_logic_formula())
    } else if (ctx.thf_quantified_formula() != null) {
      val quantification = ctx.thf_quantified_formula().thf_quantification()
      val matrix = thfUnitary(ctx.thf_quantified_formula().thf_unitary_formula())
      thf.Quantified(thfQuantifier(quantification.thf_quantifier()),
        quantification.thf_variable().asScala.map(thfVariable),
        matrix)
    } else if (ctx.thf_tuple() != null) {
      thfTuple(ctx.thf_tuple())
    } else if (ctx.thf_unary_formula() != null) {
      val connective = thfUnaryConnective(ctx.thf_unary_formula().thf_unary_connective())
      val body = thfLogicFormula(ctx.thf_unary_formula().thf_logic_formula())
      thf.Unary(connective, body)
    } else throw new IllegalArgumentException
  }
  final def transformUnitaryLetBinding(ctx: tptpParser.Thf_unitary_formulaContext): thf.LogicFormula = {
    // It may already be a tuple of formulas
    if (ctx.thf_tuple() != null) {
      // process each of it
      val transformed = ctx.thf_tuple().thf_formula_list().thf_logic_formula().asScala.map(transformUnitaryLetBinding0)
      thf.Tuple(transformed)
    } else if (ctx.thf_quantified_formula() != null) {
      transformUnitaryLetBinding1(ctx.thf_quantified_formula())
    } else if (ctx.thf_logic_formula() != null) {
      transformUnitaryLetBinding0(ctx.thf_logic_formula())
    } else throw new IllegalArgumentException
    // If it is a "plain unitary" formula, it is either Forall [...]: Phi = Psi/Phi <=> Phi
    //
  }
  final def transformUnitaryLetBinding0(ctx: tptpParser.Thf_logic_formulaContext): thf.LogicFormula = {
    if (ctx.thf_binary_formula() != null) {
      // Equality binding
      if (ctx.thf_binary_formula().thf_binary_pair() != null) {
        if (ctx.thf_binary_formula().thf_binary_pair().thf_pair_connective() != null && ctx.thf_binary_formula().thf_binary_pair().thf_pair_connective().Infix_equality() != null) {
          thfLogicFormula(ctx) // just transform it
        } else throw new IllegalArgumentException
      }else throw new IllegalArgumentException
    } else if (ctx.thf_unitary_formula() != null) {
      // forall binding
      if (ctx.thf_unitary_formula().thf_quantified_formula() != null) {
        transformUnitaryLetBinding1(ctx.thf_unitary_formula().thf_quantified_formula())
      } else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }
  final def transformUnitaryLetBinding1(ctx: tptpParser.Thf_quantified_formulaContext): thf.LogicFormula = {
    if (ctx.thf_quantification().thf_quantifier().fol_quantifier().Forall() != null) {
      val matrix = ctx.thf_unitary_formula()
      if (matrix.thf_logic_formula() != null) {
        if (matrix.thf_logic_formula().thf_binary_formula() != null) {
          if (matrix.thf_logic_formula().thf_binary_formula().thf_binary_pair() != null) {
            val pair = matrix.thf_logic_formula().thf_binary_formula().thf_binary_pair()
            if (pair.thf_pair_connective().Infix_equality() != null || (pair.thf_pair_connective().binary_connective() != null && pair.thf_pair_connective().binary_connective().Iff() != null)) {
              val right = thfUnitary(pair.thf_unitary_formula(1))
              val head = headSymbolForLetBinding(pair.thf_unitary_formula(0))
              thf.Binary(head, thf.Eq, thf.Quantified(thf.^,ctx.thf_quantification().thf_variable().asScala.map(thfVariable),right))
            } else throw new IllegalArgumentException
          } else throw new IllegalArgumentException
        } else throw new IllegalArgumentException
      } else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }
  final def headSymbolForLetBinding(ctx: tptpParser.Thf_unitary_formulaContext): thf.Function = {
    if (ctx.thf_atom() != null && ctx.thf_atom().thf_function() != null && ctx.thf_atom().thf_function().thf_plain_term() != null
    && ctx.thf_atom().thf_function().thf_plain_term().thf_arguments() == null) {
      val fun = atomicWord(ctx.thf_atom().thf_function().thf_plain_term().functor().atomic_word())
      thf.Function(fun, Seq())
    } else if (ctx.thf_logic_formula() != null) {
      if (ctx.thf_logic_formula().thf_binary_formula() != null && ctx.thf_logic_formula().thf_binary_formula().thf_binary_tuple() != null
      && ctx.thf_logic_formula().thf_binary_formula().thf_binary_tuple().thf_apply_formula() != null) {
        val apply = ctx.thf_logic_formula().thf_binary_formula().thf_binary_tuple().thf_apply_formula()
        headSymbolForLetBinding0(apply)
      } else if (ctx.thf_logic_formula().thf_unitary_formula() != null) headSymbolForLetBinding(ctx.thf_logic_formula().thf_unitary_formula())
      else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }
  final def headSymbolForLetBinding0(ctx: tptpParser.Thf_apply_formulaContext): thf.Function = {
    val apply = ctx
    if (apply.thf_apply_formula() != null) {
      val left = apply.thf_apply_formula()
      headSymbolForLetBinding0(left)
    } else {
      val left = apply.thf_unitary_formula(0)
      headSymbolForLetBinding(left)
    }
  }
  final def thfAtom(ctx: tptpParser.Thf_atomContext): thf.LogicFormula = {
    if (ctx.variable() != null) thf.Var(ctx.variable().getText)
    else if (ctx.thf_function() != null) {
      val thfFun = ctx.thf_function()
      if (thfFun.thf_plain_term() != null) {
        val fun = atomicWord(thfFun.thf_plain_term().functor().atomic_word())
        if (thfFun.thf_plain_term().thf_arguments() == null) thf.Function(fun, Seq())
        else {
          val args = thfFun.thf_plain_term().thf_arguments()
          val convertedArgs = args.thf_formula_list().thf_logic_formula().asScala.map(thfLogicFormula)
          thf.Function(fun, convertedArgs)
        }
      } else if (thfFun.thf_defined_term() != null) {
        val fun = thfFun.thf_defined_term()
        if (fun.defined_atom() != null) {
          val atom = fun.defined_atom()
          if (atom.Distinct_object() != null) thf.Distinct(atom.Distinct_object().getText)
          else if (atom.number() != null) thf.Number(number(atom.number()))
          else throw new IllegalArgumentException
        } else if (fun.defined_functor() != null) {
          val fun2 = fun.defined_functor().getText
          if (fun.thf_arguments() == null) thf.Function(fun2, Seq())
          else {
            val args = fun.thf_arguments()
            val convertedArgs = args.thf_formula_list().thf_logic_formula().asScala.map(thfLogicFormula)
            thf.Function(fun2, convertedArgs)
          }
        } else throw new IllegalArgumentException
      } else if (thfFun.thf_system_term() != null) {
        val fun = thfFun.thf_system_term().system_functor().getText
        if (thfFun.thf_system_term().thf_arguments() == null) thf.Function(fun, Seq())
        else {
          val args = thfFun.thf_system_term().thf_arguments()
          val convertedArgs = args.thf_formula_list().thf_logic_formula().asScala.map(thfLogicFormula)
          thf.Function(fun, convertedArgs)
        }
      } else throw new IllegalArgumentException
    } else if (ctx.thf_conn_term() != null) {
      val conn = ctx.thf_conn_term()
      if (conn.assoc_connective() != null) {
        if (conn.assoc_connective().And() != null) thf.Connective(Left(thf.&))
        else if (conn.assoc_connective().Or() != null) thf.Connective(Left(thf.|))
        else throw new IllegalArgumentException
      } else if (conn.thf_pair_connective() != null) {
        thf.Connective(Left(thfPairConnective(conn.thf_pair_connective())))
      } else if (conn.thf_unary_connective() != null) {
        thf.Connective(Right(thfUnaryConnective(conn.thf_unary_connective())))
      } else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }
//  final def thfFunction(ctx: tptpParser.Thf_functionContext): thf.LogicFormula = ???

  final def thfTopLevelType(ctx: tptpParser.Thf_top_level_typeContext): thf.LogicFormula = {
    if (ctx.thf_mapping_type() != null) {
      thf.BinType(thfMappingType(ctx.thf_mapping_type()))
    } else if (ctx.thf_unitary_type() != null) {
      thfUnitaryType(ctx.thf_unitary_type())
    } else throw new IllegalArgumentException
  }
  final def thfUnitaryType(typ: tptpParser.Thf_unitary_typeContext): thf.LogicFormula = {
    if (typ.thf_unitary_formula() != null) thfUnitary(typ.thf_unitary_formula())
    else if (typ.thf_binary_type() != null) {
      val bintyp = typ.thf_binary_type()
      if (bintyp.thf_mapping_type() != null) thf.BinType(thfMappingType(bintyp.thf_mapping_type()))
      else if (bintyp.thf_union_type() != null) thf.BinType(thfUnionType(bintyp.thf_union_type()))
      else if (bintyp.thf_xprod_type() != null) thf.BinType(thfProdType(bintyp.thf_xprod_type()))
      else throw new IllegalArgumentException
    } else throw new IllegalArgumentException
  }
  final def thfMappingType(ctx: tptpParser.Thf_mapping_typeContext): thf.-> = {
    if (ctx.thf_mapping_type() != null) {
      val left = thfUnitaryType(ctx.thf_unitary_type(0))
      val right = thfMappingType(ctx.thf_mapping_type())
      thf.->(left +: right.t)
    } else {
      val left = thfUnitaryType(ctx.thf_unitary_type(0))
      val right = thfUnitaryType(ctx.thf_unitary_type(1))
      thf.->(Seq(left,right))
    }
  }
  final def thfUnionType(ctx: tptpParser.Thf_union_typeContext): thf.+ = {
    if (ctx.thf_union_type() != null) {
      val right = thfUnitaryType(ctx.thf_unitary_type(0))
      val left = thfUnionType(ctx.thf_union_type())
      thf.+(left.t :+ right)
    } else {
      val left = thfUnitaryType(ctx.thf_unitary_type(0))
      val right = thfUnitaryType(ctx.thf_unitary_type(1))
      thf.+(Seq(left,right))
    }
  }
  final def thfProdType(ctx: tptpParser.Thf_xprod_typeContext): thf.* = {
    if (ctx.thf_xprod_type() != null) {
      val right = thfUnitaryType(ctx.thf_unitary_type(0))
      val left = thfProdType(ctx.thf_xprod_type())
      thf.*(left.t :+ right)
    } else {
      val left = thfUnitaryType(ctx.thf_unitary_type(0))
      val right = thfUnitaryType(ctx.thf_unitary_type(1))
      thf.*(Seq(left,right))
    }
  }
  ////////////////////////////////////////////////////////
  /// TFF
  ////////////////////////////////////////////////////////
  import leo.datastructures.tptp.tff
  final def tffFormula(ctx: tptpParser.Tff_formulaContext): tff.Formula = ???
  final def tffLogicFormula(ctx: tptpParser.Tff_logic_formulaContext): tff.LogicFormula = ???
}
