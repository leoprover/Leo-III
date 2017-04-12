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
      fofAnnotated(ctx.fof_annotated())
    } else if (ctx.tff_annotated() != null) {
      tffAnnotated(ctx.tff_annotated())
    } else if (ctx.thf_annotated() != null) {
      thfAnnotated(ctx.thf_annotated())
    } else if (ctx.cnf_annotated() != null) {
      cnfAnnotated(ctx.cnf_annotated())
    } else throw new IllegalArgumentException
  }
  final def fofAnnotated(annotated: tptpParser.Fof_annotatedContext): AnnotatedFormula = {
    val name = getName(annotated.name())
    val annotation = annotations(annotated.annotations())
    val formula = fofFormula(annotated.fof_formula())
    FOFAnnotated(name, role(annotated.formula_role()), formula, annotation)
  }
  final def tffAnnotated(annotated: tptpParser.Tff_annotatedContext): AnnotatedFormula = {
    val name = getName(annotated.name())
    val annotation = annotations(annotated.annotations())
    val formula = tffFormula(annotated.tff_formula())
    TFFAnnotated(name, role(annotated.formula_role()), formula, annotation)
  }
  final def thfAnnotated(annotated: tptpParser.Thf_annotatedContext): AnnotatedFormula = {
    val name = getName(annotated.name())
    val annotation = annotations(annotated.annotations())
    val formula = thfFormula(annotated.thf_formula())
    THFAnnotated(name, role(annotated.formula_role()), formula, annotation)
  }
  final def cnfAnnotated(annotated: tptpParser.Cnf_annotatedContext): AnnotatedFormula = {
    val name = getName(annotated.name())
    val annotation = annotations(annotated.annotations())
    val formula = cnfFormula(annotated.cnf_formula())
    CNFAnnotated(name, role(annotated.formula_role()), formula, annotation)
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
    } else if (ctx.fof_term() != null) {
      FOTData(fofTerm(ctx.fof_term()))
    } else if (ctx.cnf_formula() != null) {
      CNFData(cnfFormula(ctx.cnf_formula()))
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
    else if (ctx.fof_atomic_formula() != null) {
      fofAtomicFormula(ctx.fof_atomic_formula())
    } else if (ctx.fof_quantified_formula() != null) {
      val matrix = fofUnitary(ctx.fof_quantified_formula().fof_unitary_formula())
      val quantifier = fofQuantifier(ctx.fof_quantified_formula().fof_quantifier())
      val varlist = ctx.fof_quantified_formula().fof_variable_list().variable().asScala.map(fofVariable)
      fof.Quantified(quantifier, varlist, matrix)
    } else if (ctx.fof_unary_formula() != null) {
      val unary = ctx.fof_unary_formula()
      if (unary.fof_infix_unary() != null) {
        val left = fofTerm(unary.fof_infix_unary().fof_term(0))
        val right = fofTerm(unary.fof_infix_unary().fof_term(1))
        fof.Inequality(left,right)
      } else {
        // Not is the only unary connective
        assert(unary.unary_connective().Not() != null)
        val body = fofUnitary(unary.fof_unitary_formula())
        fof.Unary(fof.Not, body)
      }
    } else throw new IllegalArgumentException
  }
  final def fofQuantifier(ctx: tptpParser.Fof_quantifierContext): fof.Quantifier = {
    if (ctx.Forall() != null) fof.!
    else if (ctx.Exists() != null) fof.?
    else throw new IllegalArgumentException
  }
  final def fofVariable(ctx: tptpParser.VariableContext): Variable = ctx.Upper_word().getText

  final def fofAtomicFormula(ctx: tptpParser.Fof_atomic_formulaContext): fof.Atomic = {
    if (ctx.fof_plain_atomic_formula() != null) {
      val plain = ctx.fof_plain_atomic_formula()
      fof.Atomic(Plain(fofPlainTerm(plain.fof_plain_term())))
    } else if (ctx.fof_defined_atomic_formula() != null) {
      val defined = ctx.fof_defined_atomic_formula()
      if (defined.fof_defined_infix_formula() != null) {
        // Equality is the only infix connective here
        assert(defined.fof_defined_infix_formula().defined_infix_pred().Infix_equality() != null)
        val left = fofTerm(defined.fof_defined_infix_formula().fof_term(0))
        val right = fofTerm(defined.fof_defined_infix_formula().fof_term(1))
        fof.Atomic(Equality(left,right))
      } else if (defined.fof_defined_plain_formula() != null) {
        val definedPlain = defined.fof_defined_plain_formula().fof_defined_term()
        val func = definedPlain.defined_functor().getText
        if (definedPlain.fof_arguments() == null) fof.Atomic(DefinedPlain(DefinedFunc(func, Seq())))
        else {
          val args = definedPlain.fof_arguments().fof_term().asScala.map(fofTerm)
          fof.Atomic(DefinedPlain(DefinedFunc(func, args)))
        }
      } else throw new IllegalArgumentException
    } else if (ctx.fof_system_atomic_formula() != null) {
      val system = ctx.fof_system_atomic_formula()
      val func = system.fof_system_term().system_functor().getText
      if (system.fof_system_term().fof_arguments() == null) fof.Atomic(SystemPlain(SystemFunc(func, Seq())))
      else {
        val args = system.fof_system_term().fof_arguments().fof_term().asScala.map(fofTerm)
        fof.Atomic(SystemPlain(SystemFunc(func, args)))
      }
    } else throw new IllegalArgumentException
  }
  final def fofPlainTerm(ctx: tptpParser.Fof_plain_termContext): Func = {
    val func = atomicWord(ctx.functor().atomic_word())
    if (ctx.fof_arguments() == null) Func(func, Seq())
    else {
      val args = ctx.fof_arguments().fof_term().asScala.map(fofTerm)
      Func(func, args)
    }
  }

  // term stuff
  final def fofTerm(ctx: tptpParser.Fof_termContext): Term = {
    if (ctx.tff_conditional_term() != null) {
      val cond = tffLogicFormula(ctx.tff_conditional_term().tff_logic_formula())
      val thn = fofTerm(ctx.tff_conditional_term().fof_term(0))
      val els = fofTerm(ctx.tff_conditional_term().fof_term(1))
      Cond(cond, thn, els)
    } else if (ctx.tff_let_term() != null) {
      tffLetTerm(ctx.tff_let_term())
    } else if (ctx.tff_tuple_term() != null) {
      tffTupleTerm(ctx.tff_tuple_term())
    } else if (ctx.variable() != null) {
      Var(ctx.variable().getText)
    } else if (ctx.defined_term() != null) {
      val defined = ctx.defined_term()
      if (defined.Distinct_object() != null)
        Distinct(defined.Distinct_object().getText)
      else if (defined.number() != null) {
        val n = number(defined.number())
        NumberTerm(n)
      } else throw new IllegalArgumentException
    } else if (ctx.fof_function_term() != null) {
      val fun = ctx.fof_function_term()
      if (fun.fof_plain_term() != null) {
        val plain = fun.fof_plain_term()
        val f = atomicWord(plain.functor().atomic_word())
        if (plain.fof_arguments() != null) {
          val args = plain.fof_arguments().fof_term().asScala.map(fofTerm)
          Func(f, args)
        } else {
          Func(f, Seq())
        }
      } else if (fun.fof_defined_term() != null) {
        val defined = fun.fof_defined_term()
        val f = defined.defined_functor().atomic_defined_word().getText
        if (defined.fof_arguments() != null) {
          val args = defined.fof_arguments().fof_term().asScala.map(fofTerm)
          DefinedFunc(f, args)
        } else
          DefinedFunc(f, Seq())
      } else if(fun.fof_system_term() != null) {
        val system = fun.fof_system_term()
        val f = system.system_functor().getText
        if (system.fof_arguments() != null) {
          val args = system.fof_arguments().fof_term().asScala.map(fofTerm)
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
      } else if (binary.thf_binary_type() != null) {
        val bintyp = binary.thf_binary_type()
        if (bintyp.thf_mapping_type() != null) thf.BinType(thfMappingType(bintyp.thf_mapping_type()))
        else if (bintyp.thf_union_type() != null) thf.BinType(thfUnionType(bintyp.thf_union_type()))
        else if (bintyp.thf_xprod_type() != null) thf.BinType(thfProdType(bintyp.thf_xprod_type()))
        else throw new IllegalArgumentException
      } else throw new IllegalArgumentException
    } else if (ctx.thf_unitary_formula() != null) {
      thfUnitary(ctx.thf_unitary_formula())
    } else if (ctx.thf_subtype() != null) {
      val left = thfAtom(ctx.thf_subtype().thf_atom(0))
      val right = thfAtom(ctx.thf_subtype().thf_atom(1))
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
    if (ctx.thf_formula_list() != null)
      thf.Tuple(ctx.thf_formula_list().thf_logic_formula().asScala.map(thfLogicFormula))
    else thf.Tuple(Seq())
  }
  final def thfQuantifier(ctx: tptpParser.Thf_quantifierContext): thf.Quantifier = {
    if (ctx.fof_quantifier() != null) {
      val q = ctx.fof_quantifier()
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
      val binding = transformUnitaryLetBinding(ctx.thf_let().thf_unitary_formula())
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
  final def transformUnitaryLetBinding(ctx: tptpParser.Thf_unitary_formulaContext): thf.Tuple = {
    // It may already be a tuple of formulas
    if (ctx.thf_tuple() != null) {
      // process each of it
      val transformed = ctx.thf_tuple().thf_formula_list().thf_logic_formula().asScala.map(transformUnitaryLetBinding0)
      thf.Tuple(transformed)
    } else if (ctx.thf_quantified_formula() != null) {
      thf.Tuple(Seq(transformUnitaryLetBinding1(ctx.thf_quantified_formula())))
    } else if (ctx.thf_logic_formula() != null) {
      thf.Tuple(Seq(transformUnitaryLetBinding0(ctx.thf_logic_formula())))
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
    if (ctx.thf_quantification().thf_quantifier().fof_quantifier().Forall() != null) {
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
    else if (ctx.defined_term() != null) {
      if (ctx.defined_term().Distinct_object() != null) thf.Distinct(ctx.defined_term().Distinct_object().getText)
      else if (ctx.defined_term().number() != null) thf.Number(number(ctx.defined_term().number()))
      else throw new IllegalArgumentException
    } else if (ctx.thf_function() != null) {
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
        if (fun.thf_arguments() == null) thf.Function(fun.defined_functor().getText, Seq())
        else {
          val args = fun.thf_arguments()
          val convertedArgs = args.thf_formula_list().thf_logic_formula().asScala.map(thfLogicFormula)
          thf.Function(fun.defined_functor().getText, convertedArgs)
        }
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
    } else if (ctx.thf_apply_type() != null) {
      thfApply(ctx.thf_apply_type().thf_apply_formula())
    } else throw new IllegalArgumentException
  }
  final def thfUnitaryType(typ: tptpParser.Thf_unitary_typeContext): thf.LogicFormula = {
    if (typ.thf_unitary_formula() != null) thfUnitary(typ.thf_unitary_formula())
    else throw new IllegalArgumentException
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
  final def tffFormula(ctx: tptpParser.Tff_formulaContext): tff.Formula = {
    if (ctx.tff_logic_formula() != null) tff.Logical(tffLogicFormula(ctx.tff_logic_formula()))
    else if (ctx.tff_sequent() != null) tffSequent(ctx.tff_sequent())
    else if (ctx.tff_typed_atom() != null) tffTypedAtom(ctx.tff_typed_atom())
    else throw new IllegalArgumentException
  }
  final def tffLogicFormula(ctx: tptpParser.Tff_logic_formulaContext): tff.LogicFormula = {
    if (ctx.tff_binary_formula() != null) {
      val binary = ctx.tff_binary_formula()
      if (binary.tff_binary_assoc() != null) {
        val assoc = binary.tff_binary_assoc()
        if (assoc.tff_and_formula() != null) tffAnd(assoc.tff_and_formula())
        else if (assoc.tff_or_formula() != null) tffOr(assoc.tff_or_formula())
        else throw new IllegalArgumentException
      } else if (binary.tff_binary_nonassoc() != null) {
        val nonassoc = binary.tff_binary_nonassoc()
        val left = tffUnitary(nonassoc.tff_unitary_formula(0))
        val right = tffUnitary(nonassoc.tff_unitary_formula(1))
        val conn = nonassoc.binary_connective()
        val convertedConn = if (conn.If() != null) tff.<=
        else if (conn.Iff() != null) tff.<=>
        else if (conn.Impl() != null) tff.Impl
        else if (conn.Nand() != null) tff.~&
        else if (conn.Niff() != null) tff.<~>
        else if (conn.Nor() != null) tff.~|
        else throw new IllegalArgumentException
        tff.Binary(left, convertedConn, right)
      } else throw new IllegalArgumentException
    } else if (ctx.tff_unitary_formula() != null) {
      tffUnitary(ctx.tff_unitary_formula())
    } else throw new IllegalArgumentException
  }
  final def tffAnd(ctx: tptpParser.Tff_and_formulaContext): tff.LogicFormula = {
    if (ctx.tff_and_formula() != null) {
      val left = tffAnd(ctx.tff_and_formula())
      val right = tffUnitary(ctx.tff_unitary_formula(0))
      tff.Binary(left, tff.&, right)
    } else {
      val left = tffUnitary(ctx.tff_unitary_formula(0))
      val right = tffUnitary(ctx.tff_unitary_formula(1))
      tff.Binary(left, tff.&, right)
    }
  }
  final def tffOr(ctx: tptpParser.Tff_or_formulaContext): tff.LogicFormula = {
    if (ctx.tff_or_formula() != null) {
      val left = tffOr(ctx.tff_or_formula())
      val right = tffUnitary(ctx.tff_unitary_formula(0))
      tff.Binary(left, tff.|, right)
    } else {
      val left = tffUnitary(ctx.tff_unitary_formula(0))
      val right = tffUnitary(ctx.tff_unitary_formula(1))
      tff.Binary(left, tff.|, right)
    }
  }

  final def tffUnitary(ctx: tptpParser.Tff_unitary_formulaContext): tff.LogicFormula = {
    if (ctx.tff_conditional() != null) {
      val cond = tffLogicFormula(ctx.tff_conditional().tff_logic_formula(0))
      val thn = tffLogicFormula(ctx.tff_conditional().tff_logic_formula(1))
      val els = tffLogicFormula(ctx.tff_conditional().tff_logic_formula(2))
      tff.Cond(cond, thn, els)
    } else if (ctx.tff_atomic_formula() != null) {
      val atomic = ctx.tff_atomic_formula().fof_atomic_formula()
      tff.Atomic(fofAtomicFormula(atomic).formula)
    } else if (ctx.tff_let() != null) {
      tffLet(ctx.tff_let())
    } else if (ctx.tff_logic_formula() != null) {
      tffLogicFormula(ctx.tff_logic_formula())
    } else if (ctx.tff_unary_formula() != null) {
      val unary = ctx.tff_unary_formula()
      if (unary.fof_infix_unary() != null) {
        assert(unary.fof_infix_unary().Infix_inequality() != null)
        val left = fofTerm(unary.fof_infix_unary().fof_term(0))
        val right = fofTerm(unary.fof_infix_unary().fof_term(1))
        tff.Inequality(left, right)
      } else if (unary.tff_unitary_formula() != null) {
        val body = tffUnitary(unary.tff_unitary_formula())
        assert(unary.unary_connective().Not() != null)
        tff.Unary(tff.Not, body)
      } else throw new IllegalArgumentException
    } else if (ctx.tff_quantified_formula() != null) {
      val quant = ctx.tff_quantified_formula()
      val quantifier = if (quant.fof_quantifier().Forall() != null) tff.!
      else if (quant.fof_quantifier().Exists() != null) tff.?
      else throw new IllegalArgumentException
      val vars = quant.tff_variable_list().tff_variable().asScala.map(tffVariable)
      val matrix = tffUnitary(quant.tff_unitary_formula())
      tff.Quantified(quantifier, vars, matrix)
    } else throw new IllegalArgumentException
  }
  final def tffLet(ctx: tptpParser.Tff_letContext): tff.Let = {
    val in = tffFormula(ctx.tff_formula())
    if (ctx.tff_let_formula_defns() != null) tff.Let(tffLetFormulaDefns(ctx.tff_let_formula_defns()), in)
    else if (ctx.tff_let_term_defns() != null) tff.Let(tffLetTermDefns(ctx.tff_let_term_defns()), in)
    else throw new IllegalArgumentException
  }
  final def tffLetTerm(ctx: tptpParser.Tff_let_termContext): Let = {
    val in = fofTerm(ctx.fof_term())
    if (ctx.tff_let_formula_defns() != null) Let(tffLetFormulaDefns(ctx.tff_let_formula_defns()), in)
    else if (ctx.tff_let_term_defns() != null) Let(tffLetTermDefns(ctx.tff_let_term_defns()), in)
    else throw new IllegalArgumentException
  }
  final def tffLetTermDefns(ctx: tptpParser.Tff_let_term_defnsContext): tff.Formula#LetBinding = {
    if (ctx.tff_let_term_defn() != null) {
      Right(Map(tffLetTermBinding(ctx.tff_let_term_defn().tff_let_term_binding()))).asInstanceOf[tff.Formula#LetBinding]
    } else if (ctx.tff_let_term_list() != null) {
      Right(Map(ctx.tff_let_term_list().tff_let_term_defn().asScala.map(x => tffLetTermBinding(x.tff_let_term_binding())):_*)).asInstanceOf[tff.Formula#LetBinding]
    } else throw new IllegalArgumentException
  }
  final def tffLetTermBinding(ctx: tptpParser.Tff_let_term_bindingContext): (Func, Term) = {
    if (ctx.tff_let_term_binding() != null) tffLetTermBinding(ctx.tff_let_term_binding())
    else {
      val left = fofPlainTerm(ctx.fof_plain_term())
      val in = fofTerm(ctx.fof_term())
      (left,in)
    }
  }

  final def tffLetFormulaDefns(ctx: tptpParser.Tff_let_formula_defnsContext): tff.Formula#LetBinding = {
    if (ctx.tff_let_formula_defn() != null) {
      Left(Map(tffLetFormulaBinding(ctx.tff_let_formula_defn().tff_let_formula_binding()))).asInstanceOf[tff.Formula#LetBinding]
    } else if (ctx.tff_let_formula_list() != null) {
      Left(Map(ctx.tff_let_formula_list().tff_let_formula_defn().asScala.map(x => tffLetFormulaBinding(x.tff_let_formula_binding())):_*)).asInstanceOf[tff.Formula#LetBinding]
    } else throw new IllegalArgumentException
  }
  final def tffLetFormulaBinding(ctx: tptpParser.Tff_let_formula_bindingContext): (Func, tff.LogicFormula) = {
    if (ctx.tff_let_formula_binding() != null) tffLetFormulaBinding(ctx.tff_let_formula_binding())
    else {
      val left = fofPlainTerm(ctx.fof_plain_atomic_formula().fof_plain_term())
      val in = tffUnitary(ctx.tff_unitary_formula())
      (left,in)
    }
  }
  final def tffTupleTerm(ctx: tptpParser.Tff_tuple_termContext): Tuple = {
    if (ctx.fof_arguments() != null) Tuple(Seq())
    else Tuple(ctx.fof_arguments().fof_term().asScala.map(fofTerm))
  }
  final def tffVariable(ctx: tptpParser.Tff_variableContext): (Variable, Option[tff.AtomicType]) = {
    val varname = ctx.variable().getText
    val ty = if (ctx.tff_atomic_type() == null) None else Some(tffAtomicType(ctx.tff_atomic_type()))
    (varname, ty)
  }

  final def tffTopLevelType(ctx: tptpParser.Tff_top_level_typeContext): tff.Type = {
    if (ctx.tff_top_level_type() != null) tffTopLevelType(ctx.tff_top_level_type())
    else if (ctx.tff_atomic_type() != null) {
      tffAtomicType(ctx.tff_atomic_type())
    } else if (ctx.tff_mapping_type() != null) {
      tffMappingType(ctx.tff_mapping_type())
    } else if (ctx.tf1_quantified_type() != null) {
      val quant = ctx.tf1_quantified_type()
      val vars = quant.tff_variable_list().tff_variable().asScala.map(tffVariable)
      val monotype = if (quant.tff_monotype().tff_atomic_type() != null) tffAtomicType(quant.tff_monotype().tff_atomic_type())
      else tffMappingType(quant.tff_monotype().tff_mapping_type())
      tff.QuantifiedType(vars, monotype)
    } else throw new IllegalArgumentException
  }
  final def tffAtomicType(ctx: tptpParser.Tff_atomic_typeContext): tff.AtomicType = {
    val atomic = ctx
    if (atomic.variable() != null) tff.AtomicType(atomic.variable().getText, Seq())
    else {
      val fun = if (atomic.defined_type() != null) atomic.defined_type().getText
      else atomicWord(atomic.type_functor().atomic_word())
      val args = if (atomic.tff_type_arguments() != null) atomic.tff_type_arguments().tff_atomic_type().asScala.map(tffAtomicType)
      else Seq()
      tff.AtomicType(fun, args)
    }
  }
  final def tffUnitaryType(ctx: tptpParser.Tff_unitary_typeContext): tff.Type = {
    if (ctx.tff_atomic_type() != null) tffAtomicType(ctx.tff_atomic_type())
    else if (ctx.tff_xprod_type() != null) tffProdType(ctx.tff_xprod_type())
    else throw new IllegalArgumentException
  }
  final def tffMappingType(ctx: tptpParser.Tff_mapping_typeContext): tff.-> = {
    val left = tffUnitaryType(ctx.tff_unitary_type())
    val right = tffAtomicType(ctx.tff_atomic_type())
    tff.->(Seq(left, right))
  }
  final def tffProdType(ctx: tptpParser.Tff_xprod_typeContext): tff.* = {
    val right = tffAtomicType(ctx.tff_atomic_type())
    if (ctx.tff_unitary_type() != null) {
      val left = tffUnitaryType(ctx.tff_unitary_type())
      tff.*(Seq(left,right))
    } else {
      val left = tffProdType(ctx.tff_xprod_type())
      tff.*(left.t :+ right)
    }
  }

  final def tffTypedAtom(ctx: tptpParser.Tff_typed_atomContext): tff.TypedAtom = {
    if (ctx.tff_typed_atom() != null) tffTypedAtom(ctx.tff_typed_atom())
    else {
      val atom = ctx.tff_untyped_atom().getText
      val typ = tffTopLevelType(ctx.tff_top_level_type())
      tff.TypedAtom(atom, typ)
    }
  }

  final def tffSequent(ctx: tptpParser.Tff_sequentContext): tff.Sequent = {
    if (ctx.tff_sequent() != null) tffSequent(ctx.tff_sequent())
    else {
      val left = ctx.tff_formula_tuple(0)
      val right = ctx.tff_formula_tuple(1)
      val convertedLeft = if (left.tff_formula_tuple_list() == null) Seq() else {
        left.tff_formula_tuple_list().tff_logic_formula().asScala.map(tffLogicFormula)
      }
      val convertedRight = if (right.tff_formula_tuple_list() == null) Seq() else {
        right.tff_formula_tuple_list().tff_logic_formula().asScala.map(tffLogicFormula)
      }
      tff.Sequent(convertedLeft, convertedRight)
    }
  }
  ////////////////////////////////////////////////////////
  /// CNF
  ////////////////////////////////////////////////////////
  import leo.datastructures.tptp.cnf
  final def cnfFormula(ctx: tptpParser.Cnf_formulaContext): cnf.Formula = {
    if (ctx.cnf_disjunction() != null) {
      cnfDisjunction(ctx.cnf_disjunction())
    } else throw new IllegalArgumentException
  }
  final def cnfDisjunction(ctx: tptpParser.Cnf_disjunctionContext): cnf.Formula = {
    if (ctx.cnf_disjunction() != null) {
      val disj = cnfDisjunction(ctx.cnf_disjunction())
      val lit = cnfLiteral(ctx.cnf_literal())
      cnf.Formula(disj.literals :+ lit)
    } else {
      cnf.Formula(Seq(cnfLiteral(ctx.cnf_literal())))
    }
  }
  final def cnfLiteral(ctx: tptpParser.Cnf_literalContext): cnf.Literal = {
    if (ctx.fof_atomic_formula() != null) {
      if (ctx.Not() != null) {
        // negative literal
        cnf.Negative(fofAtomicFormula(ctx.fof_atomic_formula()).formula)
      } else {
        // positive literal
        cnf.Positive(fofAtomicFormula(ctx.fof_atomic_formula()).formula)
      }
    } else if (ctx.fof_infix_unary() != null) {
      val unary = ctx.fof_infix_unary()
      assert(unary.Infix_inequality() != null)
      cnf.Inequality(fofTerm(unary.fof_term(0)), fofTerm(unary.fof_term(1)))
    } else throw new IllegalArgumentException
  }

}
