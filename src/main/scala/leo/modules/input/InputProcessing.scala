package leo.modules.input

import leo.datastructures.{Kind, Role, Signature, TPTP, Term, Type}
import leo.datastructures.Term.{mkAtom, mkBound, mkInteger, mkRational, mkReal, mkTermApp, mkTypeApp, Λ, λ}
import leo.datastructures.Type.{mkFunType, mkNAryPolyType, mkType, mkVarType, mkProdType, typeKind}
import leo.modules.output.{SZS_Inappropriate, SZS_InputError, SZS_TypeError}
import leo.modules.SZSException
import leo.Out

import scala.annotation.tailrec

/**
  * Processing module for TPTP input:
  * Declarations are inserted into the given Signature,
  * terms are converted into internal term representation.
  *
  * @author Alexander Steen
  * @since 18.06.2014
  * @note Substantially revised in Dec 2020/Jan 2021
  * @see [[Term]]
  * @see [[Signature]]
  */
object InputProcessing {
  import leo.modules.HOLSignature
  import HOLSignature.{i, o, LitTrue, HOLUnaryConnective, HOLBinaryConnective}

  // (Formula name, Term, Formula Role)
  type Result = (String, Term, Role)
  type TermOrType = Either[Term, Type]
  type TypeOrKind = Either[Type, Kind]

  // Ad-hoc polymorphic arithmetic constants of TPTP.
  // This is a bit hacky, as they are defined on multiple different types; so we make a case distinction below
  // and list all of the relevant operators here.
  private[modules] final val adHocPolymorphicArithmeticConstants: Seq[Signature.Key] = Seq(
    HOLSignature.HOLLess.key,
    HOLSignature.HOLLessEq.key,
    HOLSignature.HOLGreater.key,
    HOLSignature.HOLGreaterEq.key,
    HOLSignature.HOLUnaryMinus.key,
    HOLSignature.HOLSum.key,
    HOLSignature.HOLDifference.key,
    HOLSignature.HOLProduct.key,
    HOLSignature.HOLQuotient.key
  )

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // Top-level processing
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  @inline private[this] final def processRole(role: String): Role = Role(role)

  @inline final def processAll(sig: Signature)(statements: Seq[TPTP.AnnotatedFormula]): Seq[Result] = statements.map(process(sig))

  final def process(sig: Signature)(statement: TPTP.AnnotatedFormula): Result = {
    import TPTP.{THFAnnotated, TFFAnnotated, FOFAnnotated, CNFAnnotated, TPIAnnotated, TCFAnnotated}
    import leo.datastructures.Role_Definition

    val name = statement.name
    val role = processRole(statement.role)
    try {
      val maybeFormula: Option[Term] = statement match {
        case f@THFAnnotated(_, _, _, _) => processAnnotatedTHF(sig)(f)
        case f@TFFAnnotated(_, _, _, _) => processAnnotatedTFF(sig)(f)
        case f@FOFAnnotated(_, _, _, _) => processAnnotatedFOF(sig)(f)
        case f@CNFAnnotated(_, _, _, _) => Some(processAnnotatedCNF(sig)(f))
        case TPIAnnotated(_, _, _, _) => throw new SZSException(SZS_Inappropriate, "TPI format not supported (yet).")
        case TCFAnnotated(_, _, _, _) => throw new SZSException(SZS_Inappropriate, "TCF format not supported (yet).")
      }
      maybeFormula match {
        case None => (name, LitTrue, role)
        case Some(formula) =>
          if (role == Role_Definition) (name, formula, processRole("axiom")) // If original role was definition but formula
          // is returned; then it was not recognized as definition and is now added as axiom
          else (name, formula, role)
      }
    } catch {
      case e: SZSException => throw new SZSException(e.status, s"Interpreter error in annotated TPTP formula '$name': ${e.getMessage}")
    }
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // THF processing
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  final def processAnnotatedTHF(sig: Signature)(statement: TPTP.THFAnnotated): Option[Term] = {
    import TPTP.THF.{Logical, Typing}

    statement.formula match {
      case Typing(atom, typ) =>
        if (statement.role == "type") {
          convertTHFType(sig)(typ) match {
            case Left(ty0) => sig.addUninterpreted(atom, ty0)
            case Right(k) => sig.addTypeConstructor(atom, k)
          }
          None
        } else throw new SZSException(SZS_InputError, s"Formula ${statement.name} contains a type specification but role is not 'type'." +
          s"This is considered an error; please check your input.")

      case Logical(formula) if statement.role == "definition" =>
        processTHFDef(sig)(formula) match {
          case Some((name, definition)) =>
            if (sig.exists(name)) {
              val meta = sig(name)
              if (meta.isUninterpreted) {
                if (meta._ty == definition.ty) sig.addDefinition(meta.key, definition.betaNormalize.etaExpand)
                else throw new SZSException(SZS_InputError, s"Trying to provide definition for symbol '$name', but type of definition " +
                  s"(${definition.ty.pretty(sig)}) does not match type declaration of symbol (${meta._ty.pretty}).")
              } else throw new SZSException(SZS_InputError, s"Trying to provide a definition for interpreted/defined symbol '$name'.")
            } else {
              sig.addDefined(name, definition.betaNormalize.etaExpand, definition.ty)
            }
            None
          case None =>
            val res = convertTHFFormula(sig)(formula)
            Out.info(s"No direction of definition ${statement.name} detected. Treating as axiom ...")
            Some(res)
        }

      case Logical(formula) => Some(convertTHFFormula(sig)(formula))
    }
  }

  @inline private[this] final def processTHFDef(sig: Signature)(formula: TPTP.THF.Formula): Option[(String, Term)] = {
    import TPTP.THF.{BinaryFormula, Eq, FunctionTerm}

    formula match {
      case BinaryFormula(Eq, FunctionTerm(name, Seq()), right) =>
        val definition = convertTHFFormula(sig)(right)
        Some((name, definition))
      case _ => None
    }
  }

  ////////////////////////////////////////////////////////////////////////
  // Term processing
  ////////////////////////////////////////////////////////////////////////

  final def convertTHFFormula(sig: Signature)(formula: TPTP.THF.Formula): Term = {
    convertTHFFormula0(sig)(formula, Vector.empty, Vector.empty, Map.empty)
  }

  private[this] final def convertTHFFormula0(sig: Signature)(formula: TPTP.THF.Formula,
                                                             termVars: Seq[String],
                                                             typeVars: Seq[String],
                                                             vars: Map[String, VariableMarker]): Term = {
    import TPTP.THF.{
      FunctionTerm, QuantifiedFormula, Variable, UnaryFormula, BinaryFormula,
      Tuple, ConditionalTerm, LetTerm, ConnectiveTerm, DistinctObject, NumberTerm,
      DefinedTH1ConstantTerm
    }

    formula match {
      case QuantifiedFormula(quantifier, variableList, body) =>
        val convertedQuantifier = processTHFQuantifier(quantifier)
        val variableListIt = variableList.iterator
        var newTermVars: Seq[String] = termVars
        var newTypeVars: Seq[String] = typeVars
        var newVars: Map[String, VariableMarker] = vars
        var localVariableMarkers: Seq[VariableMarker] = Vector.empty
        while (variableListIt.hasNext) {
          val (name, typeOrKind) = variableListIt.next()
          val convertedTypeOrKind = convertTHFType0(sig)(typeOrKind, newTypeVars)
          convertedTypeOrKind match {
            case Left(convertedType) =>
              newTermVars = newTermVars :+ name
              val marker = TermVariableMarker(convertedType)
              newVars = newVars + (name -> marker)
              localVariableMarkers = localVariableMarkers :+ marker
            case Right(convertedKind) =>
              if (convertedKind.isTypeKind) {
                newTypeVars = newTypeVars :+ name
                newVars = newVars + (name -> TypeVariableMarker)
                localVariableMarkers = localVariableMarkers :+ TypeVariableMarker
              } else throw new SZSException(SZS_InputError, s"Type variables can only range over '$$tType' in" +
                s" quantification, but $name was assigned ${convertedKind.pretty}.")
          }
        }
        val convertedBody = convertTHFFormula0(sig)(body, newTermVars, newTypeVars, newVars)
        mkPolyQuantified(convertedQuantifier, localVariableMarkers, convertedBody)

      case UnaryFormula(connective, body) =>
        val convertedConnective = convertTHFUnaryConnective(connective)
        assert(convertedConnective == leo.modules.HOLSignature.Not, s"Unexpected error: Expected to translate unary connective ~, but saw '${convertedConnective.pretty(sig)}' instead.")
        val convertedBody = convertTHFFormula0(sig)(body, termVars, typeVars, vars)
        convertedConnective.apply(convertedBody)

      case BinaryFormula(TPTP.THF.App, left, right) => // handle specially, as there might be polymorphic symbols involved
        val convertedLeft = convertTHFFormula0(sig)(left, termVars, typeVars, vars)
        if (convertedLeft.ty.isPolyType) {
          import leo.modules.HOLSignature.{=== => EQ, !=== => NEQ}
          // Special case (due to TH0 compliance): If (=) or (!=) used as prefix conn_term, we need to provide the type argument
          // explicitly as it is not expected to be given by the user.
          // E.g.: (=) @ t1 @ t2 ... instead of (=) @ type @ t1 @ t2.
          // For the TH1 function @=, this needs to be given explicitly by the user
          // as in (@=) @ type @ t1 @ t2. However, both @= and (=) are mapped to the same equality symbol.
          // Hence, we cannot know at this place whether to expect a type argument or not. So we try both in this case.
          if (convertedLeft == HOLBinaryConnective.toTerm(EQ)) {
            // Special case 1: Equality -- Either fill implicit type argument manually or use explicit type argument
            try {
              val convertedRight = convertTHFFormula0(sig)(right, termVars, typeVars, vars)
              val intermediate = mkTypeApp(convertedLeft, convertedRight.ty)
              mkTermApp(intermediate, convertedRight)
            } catch {
              case _: SZSException =>
                val convertedRight = convertTHFType0(sig)(right, typeVars)
                convertedRight match {
                  case Left(convertedRight0) =>
                    Left(mkTypeApp(convertedLeft, convertedRight0))
                    mkTypeApp(convertedLeft, convertedRight0)
                  case Right(k) => throw new SZSException(SZS_InputError, s"Unexpected type argument '${k.pretty}' where proper type was expected.")
                }
            }

          } else if (convertedLeft == HOLBinaryConnective.toTerm(NEQ)) {
            // Special case 2: Non-equality -- Fill implicit type argument manually
            val convertedRight = convertTHFFormula0(sig)(right, termVars, typeVars, vars)
            val intermediate = mkTypeApp(convertedLeft, convertedRight.ty)
            mkTermApp(intermediate, convertedRight)
          } else {
            convertedLeft match {
              case leo.datastructures.Term.Symbol(id) if adHocPolymorphicArithmeticConstants.contains(id) =>
                // AdHoc polymorphic case for arithmetic constants, applied with @
                val convertedRight = convertTHFFormula0(sig)(right, termVars, typeVars, vars)
                val intermediate = mkTypeApp(convertedLeft, convertedRight.ty)
                mkTermApp(intermediate, convertedRight)
              case _ => // Standard polymorphic case: Expect type argument
                val convertedRight = convertTHFType0(sig)(right, typeVars)
                convertedRight match {
                  case Left(convertedRight0) => mkTypeApp(convertedLeft, convertedRight0)
                  case Right(k) => throw new SZSException(SZS_InputError, s"Unexpected type argument '${k.pretty}' where proper type was expected.")
                }
            }
          }
        } else {
          // Standard monomorphic case: Expect term argument
          val convertedRight = convertTHFFormula0(sig)(right, termVars, typeVars, vars)
          mkTermApp(convertedLeft, convertedRight)
        }

      case BinaryFormula(connective, left, right) =>
        assert(connective != TPTP.THF.App)
        val convertedConnective = convertTHFBinaryConnective(connective)
        val convertedLeft = convertTHFFormula0(sig)(left, termVars, typeVars, vars)
        val convertedRight = convertTHFFormula0(sig)(right, termVars, typeVars, vars)
        convertedConnective.apply(convertedLeft, convertedRight)

      case FunctionTerm("$distinct", args) =>
        import leo.modules.HOLSignature.{!===, &}
        if (args.size >= 2) {
          val pairwiseCombinationsOfArgs = args.map(convertTHFFormula0(sig)(_, termVars, typeVars, vars)).combinations(2)
          val firstCombination = pairwiseCombinationsOfArgs.next()
          val firstInequality = !===(firstCombination.head, firstCombination.tail.head)
          pairwiseCombinationsOfArgs.foldLeft(firstInequality) { case (term, combination) =>
            val left = combination.head
            val right = combination.tail.head
            val intermediateResult: Term = !===(left, right)
            &(term, intermediateResult)
          }
        } else LitTrue
      case ft@FunctionTerm(f, args) =>
        if (sig.exists(f)) {
          val meta = sig(f)
          if (ft.isConstant) {
            mkAtom(meta.key)(sig)
          } else {
            val func = mkAtom(meta.key)(sig)
            if (func.ty.isPolyType) {
              if (adHocPolymorphicArithmeticConstants.contains(meta.key)) {
                numberWarning
                // We just assume that it's well-formed (applied to the right number of arguments etc.); so we
                // just get the type from the argument and apply it explicitly.
                // The ad-hoc constant have at least one argument, so we can do that.
                val convertedArgs = args.map(convertTHFFormula0(sig)(_, termVars, typeVars, vars))
                if (convertedArgs.nonEmpty) {
                  val argType = convertedArgs.head.ty
                  val intermediate = mkTypeApp(func, argType)
                  mkTermApp(intermediate, convertedArgs)
                } else throw new SZSException(SZS_InputError, s"Arithmetic TPTP symbol '${func.pretty(sig)}' is not applied to any argument.")
              } else {
                val expectedTypeArgumentCount = func.ty.polyPrefixArgsCount
                val expectedTypeArgs = args.take(expectedTypeArgumentCount).map(convertTHFType0(sig)(_, typeVars))
                val (typeArgs, kindArgs) = expectedTypeArgs.partitionMap(identity)
                if (kindArgs.isEmpty) {
                  val intermediate = mkTypeApp(func, typeArgs)
                  val remainingTermArgs = args.drop(expectedTypeArgumentCount)
                  if (remainingTermArgs.nonEmpty) {
                    val expectedTermArgs = remainingTermArgs.map(convertTHFFormula0(sig)(_, termVars, typeVars, vars))
                    mkTermApp(intermediate, expectedTermArgs)
                  } else intermediate
                } else {
                  throw new SZSException(SZS_InputError, s"Unexpected kind-like type arguments in function expression.")
                }
              }
            } else {
              val convertedArgs = args.map(convertTHFFormula0(sig)(_, termVars, typeVars, vars))
              mkTermApp(func, convertedArgs)
            }
          }
        } else {
          if (ft.isUninterpretedFunction) throw new SZSException(SZS_InputError, s"Symbol '$f' is unknown, please specify its type first.")
          else throw new SZSException(SZS_Inappropriate, s"System symbol or defined (TPTP) symbol '$f' is not supported or unknown.")
        }

      case Variable(name) =>
        vars.get(name) match {
          case Some(marker) => marker match {
            case TermVariableMarker(ty) =>
              val index = getDeBruijnIndexOf(termVars)(name)
              mkBound(ty, index)
            case TypeVariableMarker =>
              throw new SZSException(SZS_InputError, s"Unexpected type variable '$name' found in formula context.")
          }
          case None => throw new SZSException(SZS_InputError, s"Unbound variable '$name' in formula.")
        }

      case Tuple(_) => throw new SZSException(SZS_Inappropriate, "Leo-III currently does not support tuples.")

      case ConditionalTerm(condition, thn, els) =>
        val c = convertTHFFormula0(sig)(condition, termVars, typeVars, vars)
        val t = convertTHFFormula0(sig)(thn, termVars, typeVars, vars)
        val e = convertTHFFormula0(sig)(els, termVars, typeVars, vars)

        if (c.ty == o) {
          if (t.ty == e.ty) {
            import leo.modules.HOLSignature.{Choice, Impl, &, Not, ===}
            val cLift = c.lift(1)
            val tLift = t.lift(1)
            val eLift = e.lift(1)
            val ty = t.ty
            Choice(λ(ty)(&(Impl(cLift, ===(mkBound(ty, 1), tLift)),
              Impl(Not(cLift), ===(mkBound(ty, 1), eLift)))))
          } else throw new SZSException(SZS_TypeError, s"Alternatives of if-then-else are not of same type.")
        } else throw new SZSException(SZS_TypeError, s"Condition of if-then-else is not Boolean typed.")

      case LetTerm(typing, binding, body) =>
        import leo.datastructures.TPTP.THF
        // consolidatedBindingMap is a map: atom -> (type of atom, LHS of atom's binding, RHS of atom's binding)
        // e.g., could be (omitting constructors):
        // Map(
        //  'a' -> ($i > $i, a @ X, f @ X @ X),         // case 1. here X is an implicit variable. Only allowed top-level; all
                                                        // variables pair-wise distinct, and fv(RHS) ⊆ fv(LHS)
        //  'b' -> ($i, b, c)                           // case 2. no variables here
        //  'c' -> ($i > $i, a, ^[X:$i]: f @ X @ X)     // case 3. similar to case 1, only using lambda notation;
                                                        // handle exactly as case 2.
        // )
        // How to implement:
        // (0) create consolidatedBindingMap
        // (1) transform implicit variables to lambda form (i.e., reduce case 1 to case 3):
        //   ($i > $i, a @ X, f @ X @ X) => ($i > $i, a, ^[X:$i]: (f @ X @ X))
        // FAIL if LHS is not of shape 'atom' or '(atom @ X @ ... @ Z)'
        // (2) traverse body and replace every occurrence of LHS by transformed RHS (case 1 and case 3 are, in fact, identical cases)

        // (0) create consolidatedBindingMap
        val bindingWithHeadSymbol: Map[String, (THF.Formula, THF.Formula)] = binding.map { case (lhs, rhs) =>
          thfLetGetHeadsymbolFromSimpleTerm(lhs) match {
            case Some(atom) => atom -> (lhs, rhs)
            case None => throw new SZSException(SZS_InputError, s"Left-hand side of binding '${lhs.pretty} := ${rhs.pretty}' malformed in let binding '${formula.pretty}'" +
            s"Only atoms or atoms applied to (distinct) top-level variables allowed.")
          }
        }.toMap
        val consolidatedBindingMap: Map[String, (THF.Type, THF.Formula, THF.Formula)] = typing.map { case (atom, typing) =>
          bindingWithHeadSymbol.get(atom) match {
            case Some((lhs, rhs)) => atom -> (typing, lhs, rhs)
            case None => throw new SZSException(SZS_InputError, s"No binding found for atom '$atom' in let binding '${formula.pretty}'")
          }
        }
        // (1) transform implicit variables to lambda form (i.e., reduce case 1 to case 3):
        // transformedBindingMap is a map: atom -> (RHS of atom's binding in transformed lambda form)
        val transformedBindingMap: Map[String, THF.Formula] = consolidatedBindingMap.map { case (atom, (typ, lhs, rhs)) =>
          thfLetstripVariablesFromSimpleTerm(lhs) match {
            case Some((_, args)) =>
              if (args.isEmpty) {
                atom -> rhs
              } else {
                if (args.distinct == args) {
                  thfLetgetFirstNParamTypes(typ, args.length) match {
                    case Some(argTys) =>
                      val typedVariables = args.zip(argTys)
                      val transformedRHS = THF.QuantifiedFormula(THF.^, typedVariables, rhs)
                      atom -> transformedRHS
                    case None => throw new SZSException(SZS_InputError, s"Left-hand side of binding '${lhs.pretty} := ${rhs.pretty}' malformed in let binding '${formula.pretty}'" +
                      s"Only atoms or atoms applied to (distinct) top-level variables allowed.")
                  }
                } else {
                  throw new SZSException(SZS_InputError, s"Variables in LHS of let binding '${lhs.pretty} := ${rhs.pretty}' are not distinct.")
                }
              }
            case None => throw new SZSException(SZS_InputError, s"Left-hand side '${lhs.pretty}' malformed in let binding ${formula.pretty}.")
          }
        }
        // (2) traverse body and replace every occurrence of LHS by transformed RHS (case 1 and case 3 are, in fact, identical cases)
        val transformedBody = thfLetreplaceAll0(transformedBindingMap, body)
        letInfo
        convertTHFFormula0(sig)(transformedBody, termVars, typeVars, vars)

      case ConnectiveTerm(conn) =>
        import leo.datastructures.TPTP.THF
        conn match {
          case unaryConnective: THF.UnaryConnective => convertTHFUnaryConnective(unaryConnective)
          case binaryConnective: THF.BinaryConnective => convertTHFBinaryConnective(binaryConnective)
          case _ => throw new SZSException(SZS_Inappropriate, "Non-classical operators not supported.")
        }

      case DefinedTH1ConstantTerm(constant) => convertTH1DefinedConstant(constant)

      case DistinctObject(name) => mkAtom(getOrCreateSymbol(sig)(name, i))(sig)

      case NumberTerm(number) => convertNumber(number)
    }
  }

  lazy val letInfo: Unit = {
    leo.Out.info(s"Let expressions in the input problem have been expanded exhaustively.")
  }
  ////// Functions related to THF let-expression expansion BEGIN
  @tailrec private[this] def thfLetGetHeadsymbolFromSimpleTerm(formula: TPTP.THF.Formula): Option[String] = {
    import leo.datastructures.TPTP.THF
    formula match {
      case THF.FunctionTerm(f, _) => Some(f)
      case THF.BinaryFormula(THF.App, left, THF.Variable(_)) => thfLetGetHeadsymbolFromSimpleTerm(left)
      case _ => None
    }
  }
  private[this] def thfLetstripVariablesFromSimpleTerm(formula: TPTP.THF.Formula): Option[(String, Seq[String])] = {
    import leo.datastructures.TPTP.THF
    formula match {
      case THF.FunctionTerm(f, Seq()) => Some((f, Seq.empty))
      case THF.BinaryFormula(THF.App, left, THF.Variable(variable)) =>
        thfLetstripVariablesFromSimpleTerm(left).map { case (hs, args) => (hs, args :+ variable)}
      case _ => None
    }
  }
  private[this] def thfLetgetFirstNParamTypes(typ: TPTP.THF.Type, n: Int): Option[Seq[TPTP.THF.Type]] = {
    import leo.datastructures.TPTP.THF
    if (n == 0) Some(Seq.empty)
    else typ match {
      case THF.BinaryFormula(THF.FunTyConstructor, left, right) =>
        thfLetgetFirstNParamTypes(right, n-1).map(rest => left +: rest)
      case _ => None
    }
  }
  private[this] def thfLetreplaceAll0(replacements: Map[String, TPTP.THF.Formula], formula: TPTP.THF.Formula): TPTP.THF.Formula = {
    import leo.datastructures.TPTP.THF
    formula match {
      case THF.FunctionTerm(f, args) =>
        replacements.get(f) match {
          case Some(value) =>
            if (args.isEmpty) value
            else throw new SZSException(SZS_Inappropriate, "Leo-III cannot expand let expression when using FOF-style applications in THF formulas.")
          case None =>
            val replacedArgs = args.map(thfLetreplaceAll0(replacements, _))
            THF.FunctionTerm(f, replacedArgs)
        }
      case THF.QuantifiedFormula(quantifier, variableList, body) =>
        val replacedBody = thfLetreplaceAll0(replacements, body)
        THF.QuantifiedFormula(quantifier, variableList, replacedBody)
      case THF.UnaryFormula(connective, body) =>
        val replacedBody = thfLetreplaceAll0(replacements, body)
        THF.UnaryFormula(connective, replacedBody)
      case THF.BinaryFormula(connective, left, right) =>
        val replacedLeft = thfLetreplaceAll0(replacements, left)
        val replacedRight = thfLetreplaceAll0(replacements, right)
        THF.BinaryFormula(connective, replacedLeft, replacedRight)
      case THF.Tuple(elements) =>
        val replacedElements = elements.map(thfLetreplaceAll0(replacements, _))
        THF.Tuple(replacedElements)
      case THF.ConditionalTerm(condition, thn, els) =>
        val replacedCondition = thfLetreplaceAll0(replacements, condition)
        val replacedThn = thfLetreplaceAll0(replacements, thn)
        val replacedEls = thfLetreplaceAll0(replacements, els)
        THF.ConditionalTerm(replacedCondition, replacedThn, replacedEls)
      case THF.LetTerm(typing, binding, body) =>
        val replacedBinding = binding.map { case (lhs, rhs) => (lhs, thfLetreplaceAll0(replacements, rhs))}
        // Remove from replacements those which are bound by the new let binding
        val updatedReplacements = replacements -- typing.keys
        val replacedBody = thfLetreplaceAll0(updatedReplacements, body)
        THF.LetTerm(typing, replacedBinding, replacedBody)
      case _ => formula
    }
  }
  ////// Functions related to let-expression expansion END

  ////// Little workaround to have a corresponding HOLUnaryConnective for the equality as polymorphic prefix symbol
  // Though I don't understand why it's called unary ...
  private final object ===== extends HOLUnaryConnective {
    import leo.modules.HOLSignature.{=== => HOLEQ}

    val key: Signature.Key = HOLEQ.key // Dont care, we dont want to use unapply
    def ty: Type = HOLEQ.ty
    override def apply(arg: Term): Term = null
  }
  //////

  private[this] final def convertTHFUnaryConnective(connective: TPTP.THF.UnaryConnective): HOLUnaryConnective = {
    import leo.modules.HOLSignature.{Not => not}

    connective match {
      case TPTP.THF.~ => not
    }
  }

  private[this] final def convertTH1DefinedConstant(constant: TPTP.THF.DefinedTH1Constant): HOLUnaryConnective = {
    import leo.modules.HOLSignature.{Forall => forall, Exists => exists, Choice => choice, Description => desc}

    constant match {
      case TPTP.THF.!! => forall
      case TPTP.THF.?? => exists
      case TPTP.THF.@@+ => choice
      case TPTP.THF.@@- => desc
      case TPTP.THF.@= => =====
    }
  }

  private final object HOLLambda extends HOLUnaryConnective { // little hack here, to simulate a lambda, the apply function is the identity
    // this is because the mkPolyQuantified will apply a new abstraction
    val key: Signature.Key = Integer.MIN_VALUE
    def ty: Type = null
    override def apply(arg: Term): Term = arg
  }

  private[this] final def processTHFQuantifier(quantifier: TPTP.THF.Quantifier): HOLUnaryConnective = {
    import leo.modules.HOLSignature.{Forall, Exists, Choice, Description}

    quantifier match {
      case TPTP.THF.! => Forall
      case TPTP.THF.? => Exists
      case TPTP.THF.^ => HOLLambda
      case TPTP.THF.@+ => Choice
      case TPTP.THF.@- => Description
      case _ => throw new SZSException(SZS_InputError, s"Illegal quantifier symbol '${quantifier.pretty}' in formula quantification.")
    }
  }

  ////// Little workaround to have the usual application (s @ t) a corresponding HOLBinaryConnective
  private final object @@@ extends HOLBinaryConnective {
    val key: Signature.Key = Integer.MIN_VALUE // Dont care, we dont want to use unapply
    def ty: Type = null
    override def apply(left: Term, right: Term): Term = Term.mkTermApp(left, right)
  }
  //////

  private[this] final def convertTHFBinaryConnective(connective: TPTP.THF.BinaryConnective): HOLBinaryConnective = {
    import leo.modules.HOLSignature.{Impl => impl, <= => i_f, ||| => or, & => and, ~||| => nor, ~& => nand, ===, !===}

    connective match {
      case TPTP.THF.Eq => ===
      case TPTP.THF.Neq => !===
      case TPTP.THF.<=> => === //equiv
      case TPTP.THF.Impl => impl
      case TPTP.THF.<= => i_f
      case TPTP.THF.| => or
      case TPTP.THF.& => and
      case TPTP.THF.~| => nor
      case TPTP.THF.~& => nand
      case TPTP.THF.<~> => !=== //niff
      case TPTP.THF.App => @@@
      case TPTP.THF.:= => throw new SZSException(SZS_Inappropriate, s"Leo-III does not support assignments ':=' as binary connectives on formula-level.")
      case _ => throw new SZSException(SZS_InputError, s"Unexpected symbol '${connective.pretty}' used as binary connective.")
    }
  }

  ////////////////////////////////////////////////////////////////////////
  // Type processing
  ////////////////////////////////////////////////////////////////////////

  private[this] final def convertTHFType(sig: Signature)(typ: TPTP.THF.Type): TypeOrKind = {
    import TPTP.THF.QuantifiedFormula

    typ match {
      case QuantifiedFormula(quantifier, _, _) if quantifier == TPTP.THF.!> =>
        val (variables, body0) = collectVarsUnderTHFQuantifier(quantifier, typ)

        val variablesIt = variables.iterator
        var convertedVariables: Seq[String] = Vector.empty // We only need to store names; kind is always $tType
        while (variablesIt.hasNext) {
          val (name, ty) = variablesIt.next()
          val convertedTy = convertTHFType0(sig)(ty, Seq.empty)
          convertedTy match {
            case Left(_) => throw new SZSException(SZS_TypeError, s"In type quantification, only '$$tType' is allowed as type/kind, but '${ty.pretty}' was given.")
            case Right(kind) =>
              if (!kind.isTypeKind) throw new SZSException(SZS_TypeError, s"In type quantification, only '$$tType' is allowed as type/kind, but '${kind.pretty}' was given.")
          }
          convertedVariables = convertedVariables :+ name
        }

        val convertedBody = convertTHFType0(sig)(body0, convertedVariables)
        convertedBody match {
          case Left(convertedBody0) => Left(mkNAryPolyType(convertedVariables.length, convertedBody0))
          case Right(_) => throw new SZSException(SZS_TypeError, s"Body of type quantification does not yield a legal type: ${body0.pretty}")
        }

      case QuantifiedFormula(quantifier, _, _) =>
        if (quantifier == TPTP.THF.?*)
          throw new SZSException(SZS_Inappropriate, s"Existential types of form ?* ... not supported by Leo-III.")
        else
          throw new SZSException(SZS_InputError, s"Unexpected term quantification at type level.")

      case _ => convertTHFType0(sig)(typ, Seq.empty)
    }
  }

  private[this] final def convertTHFType0(sig: Signature)(typ: TPTP.THF.Type, vars: Seq[String]): TypeOrKind = {
    import TPTP.THF.{FunctionTerm, QuantifiedFormula, Variable, BinaryFormula, Tuple}

    typ match {
      case FunctionTerm(f, args) =>
        if (args.nonEmpty) throw new SZSException(SZS_TypeError, s"Malformed type expression: ${typ.toString}")
        else {
          if (f.startsWith("$$")) {
            if (sig.exists(f)) Left(mkType(sig(f).key))
            else throw new SZSException(SZS_InputError, s"Unknown system type/type operator $f")
          } else if (f.startsWith("$")) {
            if (f == "$tType") Right(typeKind)
            else if (sig.exists(f)) Left(mkType(sig(f).key))
            else throw new SZSException(SZS_Inappropriate, s"Unknown TPTP type/type operator $f")
          } else {
            if (sig.exists(f)) Left(mkType(sig(f).key))
            else throw new SZSException(SZS_InputError, s"Unknown type/type operator $f, please specify its kind before.")
          }
        }

      case QuantifiedFormula(_, _, _) => throw new SZSException(SZS_TypeError, s"Only rank-1 polymorphic types are supported; " +
        s"but the type expression contains a quantified type at non-prefix position.")

      case Variable(name) =>
        val index = getDeBruijnIndexOf(vars)(name)
        if (index == -1) throw new SZSException(SZS_InputError, s"Unbound variable '$name' in type expression.")
        else Left(mkVarType(index))

      case BinaryFormula(connective, left, right) =>
        import TPTP.THF.{FunTyConstructor, ProductTyConstructor, SumTyConstructor, App}

        connective match {
          case FunTyConstructor | App =>
            val convertedLeft = convertTHFType0(sig)(left, vars)
            val convertedRight = convertTHFType0(sig)(right, vars)
            (convertedLeft, convertedRight) match {
              case (Left(left0), Left(right0)) =>
                val result = (connective: @unchecked) match { // Exhaustiveness guaranteed by match-case condition a few lines above
                  case FunTyConstructor => mkFunType(left0, right0)
                  case App => left0.app(right0)
                }
                Left(result)
              case (Right(left0), Right(right0)) if connective == FunTyConstructor => Right(Kind.mkFunKind(left0, right0))
              case _ => throw new SZSException(SZS_TypeError, s"Binary type '${typ.pretty}' either contains both types and $$tType which is not allowed," +
                s"or contains $$tType outside the context of the function type constructor '>'.")
            }
          case ProductTyConstructor => throw new SZSException(SZS_Inappropriate, "Leo-III does not support legacy product type using the '*' type constructor." +
            "Consider using tuple types [ty1,ty2,...,tyN] instead.")
          case SumTyConstructor => throw new SZSException(SZS_Inappropriate, "Leo-III does not support union types. ")
          case _ => throw new SZSException(SZS_TypeError, s"Top-level binary type (one of '>', '*', '+', or '@') expected but term connective '${connective.pretty}' was given.")
        }

      case Tuple(elements) => // Tuple term doubles as product type expression.
        val convertedTys = elements.map(convertTHFType0(sig)(_, vars))
        val (tys, kinds) = convertedTys.partitionMap(identity)
        if (kinds.isEmpty) Left(mkProdType(tys))
        else throw new SZSException(SZS_TypeError, s"Tuple type '${typ.pretty}' contains kinds (such as $$tType) which is not allowed.")

      case _ => throw new SZSException(SZS_InputError, s"Unrecognized type expression: '${typ.pretty}'.")
    }
  }

  @inline private[this] final def collectVarsUnderTHFQuantifier(quantifier: TPTP.THF.Quantifier,
                                                                expression: TPTP.THF.Formula): (Seq[TPTP.THF.TypedVariable], TPTP.THF.Formula) = {
    collectVarsUnderTHFQuantifier0(quantifier, expression, Vector.empty)
  }

  @tailrec
  private[this] final def collectVarsUnderTHFQuantifier0(quantifier: TPTP.THF.Quantifier,
                                                         expression: TPTP.THF.Formula,
                                                         variables: Seq[TPTP.THF.TypedVariable]): (Seq[TPTP.THF.TypedVariable], TPTP.THF.Formula) = {
    import TPTP.THF.QuantifiedFormula

    expression match {
      case QuantifiedFormula(q, varList, body) if q == quantifier => collectVarsUnderTHFQuantifier0(quantifier, body, variables ++ varList)
      case _ => (variables, expression)
    }
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // TFF processing
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  final def processAnnotatedTFF(sig: Signature)(statement: TPTP.TFFAnnotated): Option[Term] = {
    import TPTP.TFF.{Typing, Logical}

    statement.formula match {
      case Typing(atom, typ) =>
        if (statement.role == "type") {
          convertTFFType(sig)(typ) match {
            case Left(ty0) => sig.addUninterpreted(atom, ty0)
            case Right(k) => sig.addTypeConstructor(atom, k)
          }
          None
        } else throw new SZSException(SZS_InputError, s"Formula ${statement.name} contains a type specification but role is not 'type'." +
          s"This is considered an error; please check your input.")
      case Logical(f) if statement.role == "definition" =>
        processTFFDef(sig)(f) match {
          case _ => // TODO: Implement definition handling
            val res = convertTFFFormula(sig)(f)
            Out.info(s"Definitions in FOF are currently treated as axioms.")
            Some(res)
        }
      case Logical(f) => Some(convertTFFFormula(sig)(f))
    }
  }

  @inline private[this] final def processTFFDef(sig: Signature)(formula: TPTP.TFF.Formula): Option[(String, Term)] = {
    // TODO: Implement definition processing
    formula match {
      case _ => None
    }
  }

  ////////////////////////////////////////////////////////////////////////
  // Term processing
  ////////////////////////////////////////////////////////////////////////

  private[this] final def convertTFFFormula(sig: Signature)(formula: TPTP.TFF.Formula): Term =
    convertTFFFormula0(sig)(formula, Vector.empty, Vector.empty, Map.empty)

  private[this] final def convertTFFFormula0(sig: Signature)(formula: TPTP.TFF.Formula,
                                                             termVars: Seq[String],
                                                             typeVars: Seq[String],
                                                             vars: Map[String, VariableMarker]): Term = {
    import TPTP.TFF.{AtomicFormula, QuantifiedFormula, UnaryFormula, BinaryFormula, Equality, Inequality, FormulaVariable, LetFormula, ConditionalFormula}
    import leo.modules.HOLSignature.{===, !===, Not}


    formula match {
      case QuantifiedFormula(quantifier, variableList, body) =>
        val convertedQuantifier = convertTFFQuantifier(quantifier)
        val variableListIt = variableList.iterator
        var newTermVars: Seq[String] = termVars
        var newTypeVars: Seq[String] = typeVars
        var newVars: Map[String, VariableMarker] = vars
        var localVariableMarkers: Seq[VariableMarker] = Vector.empty
        while (variableListIt.hasNext) {
          val (name, typeOrKind) = variableListIt.next()
          val convertedTypeOrKind = typeOrKind match {
            case Some(typeOrKind0) => convertTFFType0(sig)(typeOrKind0, newTypeVars)
            case None => Left(mkSimpleFunctionType(0))
          }
          convertedTypeOrKind match {
            case Left(convertedType) =>
              newTermVars = newTermVars :+ name
              val marker = TermVariableMarker(convertedType)
              newVars = newVars + (name -> marker)
              localVariableMarkers = localVariableMarkers :+ marker
            case Right(convertedKind) =>
              if (convertedKind.isTypeKind) {
                newTypeVars = newTypeVars :+ name
                newVars = newVars + (name -> TypeVariableMarker)
                localVariableMarkers = localVariableMarkers :+ TypeVariableMarker
              } else throw new SZSException(SZS_InputError, s"Type variables can only range over '$$tType' in" +
                s" quantification, but $name was assigned ${convertedKind.pretty}.")
          }
        }
        val convertedBody = convertTFFFormula0(sig)(body, newTermVars, newTypeVars, newVars)
        mkPolyQuantified(convertedQuantifier, localVariableMarkers, convertedBody)

      case UnaryFormula(connective, body) =>
        connective match {
          case TPTP.TFF.~ =>
            val convertedBody = convertTFFFormula0(sig)(body, termVars, typeVars, vars)
            Not(convertedBody)
        }

      case BinaryFormula(connective, left, right) =>
        val convertedLeft = convertTFFFormula0(sig)(left, termVars, typeVars, vars)
        val convertedRight = convertTFFFormula0(sig)(right, termVars, typeVars, vars)
        convertTFFBinaryConnective(connective)(convertedLeft, convertedRight)

      case Equality(left, right) =>
        val convertedLeft = convertTFFTerm(sig)(left, termVars, typeVars, vars)
        val convertedRight = convertTFFTerm(sig)(right, termVars, typeVars, vars)
        (convertedLeft, convertedRight) match {
          case (Left(convertedLeft0), Left(convertedRight0)) => ===(convertedLeft0, convertedRight0)
          case _ => throw new SZSException(SZS_InputError, s"Arguments of equality must both be terms: " +
            s"At least one of '${left.pretty}' and '${right.pretty}' were recognized as a type.")
        }


      case Inequality(left, right) =>
        val convertedLeft = convertTFFTerm(sig)(left, termVars, typeVars, vars)
        val convertedRight = convertTFFTerm(sig)(right, termVars, typeVars, vars)
        (convertedLeft, convertedRight) match {
          case (Left(convertedLeft0), Left(convertedRight0)) => !===(convertedLeft0, convertedRight0)
          case _ => throw new SZSException(SZS_InputError, s"Arguments of inequality must both be terms: " +
            s"At least one of '${left.pretty}' and '${right.pretty}' were recognized as a type.")
        }

      case AtomicFormula("$distinct", args) =>
        import leo.modules.HOLSignature.{!===, &}
        if (args.size >= 2) {
          val pairwiseCombinationsOfArgs = args.map{ arg =>
            val res = convertTFFTerm(sig)(arg, termVars, typeVars, vars)
            res match {
              case Left(termValue) => termValue
              case Right(typeValue) => throw new SZSException(SZS_InputError, s"Type argument '${typeValue.pretty(sig)}' not allowed in $$distinct predicate.")
            }
          }.combinations(2)
          val firstCombination = pairwiseCombinationsOfArgs.next()
          val firstInequality = !===(firstCombination.head, firstCombination.tail.head)
          pairwiseCombinationsOfArgs.foldLeft(firstInequality) { case (term, combination) =>
            val left = combination.head
            val right = combination.tail.head
            val intermediateResult: Term = !===(left, right)
            &(term, intermediateResult)
          }
        } else LitTrue
      case ft@AtomicFormula(f, args) =>
        val key = getOrCreateSymbolWithWarning(sig)(f, mkSimplePredicateType(args.size))
        val func = mkAtom(key)(sig)
        if (ft.isConstant) func
        else {
          if (func.ty.isPolyType) {
            if (adHocPolymorphicArithmeticConstants.contains(key)) {
              numberWarning
              // We just assume that it's well-formed (applied to the right number of arguments etc.); so we
              // just get the type from the argument and apply it explicitly.
              // The ad-hoc constant have at least one argument, so we can do that.
              val convertedArgs = args.map(convertTFFTerm(sig)(_, termVars, typeVars, vars))
              if (convertedArgs.nonEmpty) {
                convertedArgs.head match {
                  case Left(arg) =>
                    val argType = arg.ty
                    val intermediate = mkTypeApp(func, argType)
                    Term.local.mkApp(intermediate, convertedArgs)
                  case Right(_) => throw new SZSException(SZS_InputError, s"Arithmetic TPTP symbol '${func.pretty(sig)}' is applied to a type argument.")
                }
              } else throw new SZSException(SZS_InputError, s"Arithmetic TPTP symbol '${func.pretty(sig)}' is not applied to any argument.")
            } else {
              val expectedTypeArgumentCount = func.ty.polyPrefixArgsCount
              val expectedTypeArgs = args.take(expectedTypeArgumentCount).map(convertTFFTerm(sig)(_, termVars, typeVars, vars))
              val (termArgs, typeArgs) = expectedTypeArgs.partitionMap(identity)
              if (termArgs.isEmpty) {
                val intermediate = mkTypeApp(func, typeArgs)
                val remainingTermArgs = args.drop(expectedTypeArgumentCount)
                if (remainingTermArgs.nonEmpty) {
                  val expectedTermArgs = remainingTermArgs.map(convertTFFTerm(sig)(_, termVars, typeVars, vars))
                  val (termArgs2, typeArgs2) = expectedTermArgs.partitionMap(identity)
                  if (typeArgs2.isEmpty) mkTermApp(intermediate, termArgs2)
                  else throw new SZSException(SZS_TypeError, s"Unexpected type arguments in monomorphic body of polymorphic function application: ${typeArgs2.map(x => s"'${x.pretty(sig)}'").mkString(", ")}.")
                } else intermediate
              } else {
                throw new SZSException(SZS_InputError, s"Unexpected term arguments in polymorphic function application: ${termArgs.map(x => s"'${x.pretty(sig)}'").mkString(", ")}.")
              }
            }
          } else {
            val convertedArgs = args.map(convertTFFTerm(sig)(_, termVars, typeVars, vars))
            val (convertedTermArgs, convertedTypeArgs) = convertedArgs.partitionMap(identity)
            if (convertedTypeArgs.isEmpty) mkTermApp(func, convertedTermArgs)
            else throw new SZSException(SZS_TypeError, s"Unexpected type arguments in function application: ${convertedTypeArgs.map(x => s"'${x.pretty(sig)}'").mkString(", ")}.")
          }
        }

      // TFX world from here
      case LetFormula(typing, binding, body) =>
        import leo.datastructures.TPTP.TFF
        // (0) create consolidatedBindingMap
        val bindingWithHeadSymbol: Map[String, (TFF.Term, TFF.Term)] = binding.map { case (lhs, rhs) =>
          tffLetGetHeadsymbolFromSimpleTerm(lhs) match {
            case Some(atom) => atom -> (lhs, rhs)
            case None => throw new SZSException(SZS_InputError, s"Left-hand side of binding '${lhs.pretty} := ${rhs.pretty}' malformed in let binding '${formula.pretty}'" +
              s"Only atoms or atoms applied to (distinct) top-level variables allowed.")
          }
        }.toMap
        val consolidatedBindingMap: Map[String, (TFF.Type, TFF.Term, TFF.Term)] = typing.map { case (atom, typing) =>
          bindingWithHeadSymbol.get(atom) match {
            case Some((lhs, rhs)) => atom -> (typing, lhs, rhs)
            case None => throw new SZSException(SZS_InputError, s"No binding found for atom '$atom' in let binding '${formula.pretty}'")
          }
        }
        // (1) transform implicit variables to "lambda form":
        // transformedBindingMap is a map: atom -> (function that maps given arguments to the RHS in which the formal parameters are replaced by them.)
        val transformedBindingMap: Map[String, Seq[TFF.Term] => TFF.Term] = consolidatedBindingMap.map { case (atom, (typ, lhs, rhs)) =>
          tffLetstripVariablesFromSimpleTerm(lhs) match {
            case Some((_, params)) =>
              if (params.isEmpty) {
                atom -> (_ => rhs)
              } else {
                if (params.distinct == params) {
                  atom -> { args =>
                    val paramToArgs: Map[String, TFF.Term] = params.zip(args).toMap
                    tffInstantiateTerm(rhs, paramToArgs)
                  }
                } else throw new SZSException(SZS_InputError, s"Variables in LHS of let binding '${lhs.pretty} := ${rhs.pretty}' are not distinct.")
              }
            case None => throw new SZSException(SZS_InputError, s"Left-hand side '${lhs.pretty}' malformed in let binding ${formula.pretty}.")
          }
        }
        // (2) traverse body and replace every occurrence of LHS by transformed RHS
        val transformedBody0 = tffLetReplaceTermAll(transformedBindingMap, body)
        letInfo
        val transformedBody = transformedBody0 match { // On formula level: Check if results are indeed (potential) formulas
          case TFF.AtomicTerm(f, args) => TFF.AtomicFormula(f, args)
          case TFF.Variable(name) => TFF.FormulaVariable(name)
          case TFF.FormulaTerm(formula) => formula
          case _ => throw new SZSException(SZS_InputError, s"Let expansion of expression '${formula.pretty}' on formula level yielded unexpected term '${transformedBody0.pretty}'.")
        }
        Out.trace(s"[Input] Expanding $$let expression in ${formula.pretty} ...")
        Out.trace(s"[Input] $$let expansion result: ${transformedBody.pretty}")
        val res = convertTFFFormula0(sig)(transformedBody, termVars, typeVars, vars)
        res

      case FormulaVariable(name) =>
        if (vars.isDefinedAt(name)) {
          vars(name) match {
            case TermVariableMarker(typ) => mkBound(typ, getDeBruijnIndexOf(termVars)(name))
            case TypeVariableMarker => throw new SZSException(SZS_InputError, s"Naked type variable '$name' at formula-level.")
          }
        } else throw new SZSException(SZS_InputError, s"Unbound variable '$name' at formula-level.")

      case ConditionalFormula(condition, thn, els) =>
        Out.trace(s"[Input] Transforming TFF conditional expression ${formula.pretty} ...")
        val c = convertTFFFormula0(sig)(condition, termVars, typeVars, vars)
        val t0 = convertTFFTerm(sig)(thn, termVars, typeVars, vars)
        val e0 = convertTFFTerm(sig)(els, termVars, typeVars, vars)

        (t0,e0) match {
          case (Left(t), Left(e)) =>
            val cLift = c.lift(1)
            val tLift = t.lift(1)
            val eLift = e.lift(1)
            if (c.ty == o) {
              if (t.ty == e.ty) {
                import leo.modules.HOLSignature.{Choice, Impl, &, Not, ===}
                val ty = t.ty
                Choice(λ(ty)(&(Impl(cLift, ===(mkBound(ty, 1), tLift)),
                  Impl(Not(cLift), ===(mkBound(ty, 1), eLift)))))
              } else throw new SZSException(SZS_TypeError, s"Alternatives of conditional expression '${formula.pretty}' are not of same type.")
            } else throw new SZSException(SZS_TypeError, s"Condition of conditional expression '${formula.pretty}' is not Boolean typed.")

          case _ => throw new SZSException(SZS_InputError, s"At least one branch of the conditional expression '${formula.pretty}' yields" +
          s"an unexpected type (instead of a term or a formula).")
        }

      case _ => throw new SZSException(SZS_InputError, s"Unsupported input: '${formula.pretty}'.")

    }
  }

  private[this] final def convertTFFTerm(sig: Signature)(term: TPTP.TFF.Term,
                                                         termVars: Seq[String],
                                                         typeVars: Seq[String],
                                                         vars: Map[String, VariableMarker]): TermOrType = {
    import TPTP.TFF.{AtomicTerm, Variable, DistinctObject, NumberTerm, Tuple, FormulaTerm}

    term match {
      case AtomicTerm(f, args) => // Expect type constructors and polymorphic symbols here
        val meta = sig(getOrCreateSymbolWithWarning(sig)(f, mkSimpleFunctionType(args.size)))
        if (meta.isTypeConstructor) {
          val result = if (args.isEmpty) Type.mkType(meta.key)
          else {
            val convertedArgs = args.map(convertTFFTerm(sig)(_, termVars, typeVars, vars))
            val (termArgs, typeArgs) = convertedArgs.partitionMap(identity)
            if (termArgs.isEmpty) Type.mkType(meta.key, typeArgs)
            else throw new SZSException(SZS_TypeError, s"Unexpected term arguments in type constructor application: ${termArgs.map(x => s"'${x.pretty(sig)}'").mkString(", ")}.")
          }
          Right(result)
        } else {
          assert(meta.isTermSymbol)
          val result = if (args.isEmpty) mkAtom(meta.key)(sig)
          else {
            val func = mkAtom(meta.key)(sig)
            if (func.ty.isPolyType) {
              if (adHocPolymorphicArithmeticConstants.contains(meta.key)) {
                numberWarning
                // We just assume that it's well-formed (applied to the right number of arguments etc.); so we
                // just get the type from the argument and apply it explicitly.
                // The ad-hoc constant have at least one argument, so we can do that.
                val convertedArgs = args.map(convertTFFTerm(sig)(_, termVars, typeVars, vars))
                if (convertedArgs.nonEmpty) {
                  convertedArgs.head match {
                    case Left(arg) =>
                      val argType = arg.ty
                      val intermediate = mkTypeApp(func, argType)
                      Term.local.mkApp(intermediate, convertedArgs)
                    case Right(_) => throw new SZSException(SZS_InputError, s"Arithmetic TPTP symbol '${func.pretty(sig)}' is applied to a type argument.")
                  }
                } else throw new SZSException(SZS_InputError, s"Arithmetic TPTP symbol '${func.pretty(sig)}' is not applied to any argument.")
              } else {
                val expectedTypeArgumentCount = func.ty.polyPrefixArgsCount
                val expectedTypeArgs = args.take(expectedTypeArgumentCount).map(convertTFFTerm(sig)(_, termVars, typeVars, vars))
                val (termArgs, typeArgs) = expectedTypeArgs.partitionMap(identity)
                if (termArgs.isEmpty) {
                  val intermediate = mkTypeApp(func, typeArgs)
                  val remainingTermArgs = args.drop(expectedTypeArgumentCount)
                  if (remainingTermArgs.nonEmpty) {
                    val expectedTermArgs = remainingTermArgs.map(convertTFFTerm(sig)(_, termVars, typeVars, vars))
                    val (termArgs2, typeArgs2) = expectedTermArgs.partitionMap(identity)
                    if (typeArgs2.isEmpty) mkTermApp(intermediate, termArgs2)
                    else throw new SZSException(SZS_TypeError, s"Unexpected type arguments in monomorphic body of polymorphic function application: ${typeArgs2.map(x => s"'${x.pretty(sig)}'").mkString(", ")}.")
                  } else intermediate
                } else {
                  throw new SZSException(SZS_InputError, s"Unexpected term arguments in polymorphic function application: ${termArgs.map(x => s"'${x.pretty(sig)}'").mkString(", ")}.")
                }
              }
            } else {
              val convertedArgs = args.map(convertTFFTerm(sig)(_, termVars, typeVars, vars))
              val (convertedTermArgs, convertedTypeArgs) = convertedArgs.partitionMap(identity)
              if (convertedTypeArgs.isEmpty) mkTermApp(func, convertedTermArgs)
              else throw new SZSException(SZS_TypeError, s"Unexpected type arguments in function application: ${convertedTypeArgs.map(x => s"'${x.pretty(sig)}'").mkString(", ")}.")
            }
          }
          Left(result)
        }

      case Variable(name) =>
        if (vars.isDefinedAt(name)) {
          vars(name) match {
            case TypeVariableMarker => Right(mkVarType(getDeBruijnIndexOf(typeVars)(name)))
            case TermVariableMarker(typ) => Left(mkBound(typ, getDeBruijnIndexOf(termVars)(name)))
          }
        } else throw new SZSException(SZS_InputError, s"Unbound variable '$name' in term.")

      case DistinctObject(name) => Left(mkAtom(getOrCreateSymbol(sig)(name, i))(sig))

      case NumberTerm(number) => Left(convertNumber(number))

      case Tuple(_) => throw new SZSException(SZS_Inappropriate, "Leo-III currently does not support tuples.")

      case FormulaTerm(formula) =>
        val convertedFormula = convertTFFFormula0(sig)(formula, termVars, typeVars, vars)
        Left(convertedFormula)
    }
  }

  ////// Functions related to TFF let-expression expansion BEGIN
  private[this] def tffLetGetHeadsymbolFromSimpleTerm(formula: TPTP.TFF.Term): Option[String] = {
    import leo.datastructures.TPTP.TFF
    formula match {
      case TFF.AtomicTerm(f, _) => Some(f)
      case _ => None
    }
  }
  private[this] def tffLetstripVariablesFromSimpleTerm(formula: TPTP.TFF.Term): Option[(String, Seq[String])] = {
    import leo.datastructures.TPTP.TFF
    formula match {
      case TFF.AtomicTerm(f, args) =>
        val maybeVars = args.map {
          case TFF.Variable(name) => Some(name)
          case _ => None
        }
        if (maybeVars.forall(_.isDefined)) Some((f, maybeVars.map(_.get)))
        else None
      case _ => None
    }
  }
  private[this] def tffInstantiateTerm(term: TPTP.TFF.Term, instances: Map[String, TPTP.TFF.Term]): TPTP.TFF.Term = {
    import leo.datastructures.TPTP.TFF
    term match {
      case TFF.AtomicTerm(f, args) =>
        val instantiatedArgs = args.map(tffInstantiateTerm(_, instances))
        TFF.AtomicTerm(f, instantiatedArgs)
      case TFF.Variable(name) => instances.get(name) match {
        case Some(value) => value
        case None => term
      }
      case TFF.Tuple(elements) =>
        val instantiatedElements = elements.map(tffInstantiateTerm(_, instances))
        TFF.Tuple(instantiatedElements)
      case TFF.FormulaTerm(formula) => TFF.FormulaTerm(tffInstantiateFormula(formula, instances))
      case _ => term
    }
  }
  private[this] def tffInstantiateFormula(formula: TPTP.TFF.Formula, instances: Map[String, TPTP.TFF.Term]): TPTP.TFF.Formula = {
    import leo.datastructures.TPTP.TFF
    formula match {
      case TFF.AtomicFormula(f, args) =>
        val instantiatedArgs = args.map(tffInstantiateTerm(_, instances))
        TFF.AtomicFormula(f, instantiatedArgs)
      case TFF.QuantifiedFormula(quantifier, variableList, body) =>
        val instances0 = instances.removedAll(variableList.map(_._1))
        val instantiatedBody = tffInstantiateFormula(body, instances0)
        TFF.QuantifiedFormula(quantifier, variableList, instantiatedBody)
      case TFF.UnaryFormula(connective, body) =>
        TFF.UnaryFormula(connective, tffInstantiateFormula(body, instances))
      case TFF.BinaryFormula(connective, left, right) =>
        TFF.BinaryFormula(connective, tffInstantiateFormula(left, instances), tffInstantiateFormula(right, instances))
      case TFF.Equality(left, right) =>
        TFF.Equality(tffInstantiateTerm(left, instances), tffInstantiateTerm(right, instances))
      case TFF.Inequality(left, right) =>
        TFF.Inequality(tffInstantiateTerm(left, instances), tffInstantiateTerm(right, instances))
      case TFF.FormulaVariable(name) => instances.get(name) match {
        case Some(value) => value match {
          case TFF.AtomicTerm(f, args) => TFF.AtomicFormula(f, args)
          case TFF.Variable(name) => TFF.FormulaVariable(name)
          case TFF.FormulaTerm(formula) => formula
          case _ => throw new SZSException(SZS_InputError, "Unexpected binding of term shape when a formula was expected.")
        }
        case None => formula
      }
      case TFF.ConditionalFormula(condition, thn, els) =>
        TFF.ConditionalFormula(tffInstantiateFormula(condition, instances),
          tffInstantiateTerm(thn, instances), tffInstantiateTerm(els, instances))
      case TFF.LetFormula(typing, binding, body) =>
        val updatedBinding = binding.map { case (lhs, rhs) =>
          val varsOfLHS = tffLetstripVariablesFromSimpleTerm(lhs)
          varsOfLHS match {
            case Some((_, vars)) =>
              val instances0 = instances.removedAll(vars)
              (lhs, tffInstantiateTerm(rhs, instances0))
            case None => throw new SZSException(SZS_InputError)
          }
        }
        TFF.LetFormula(typing, updatedBinding, tffInstantiateTerm(body, instances))
      case TFF.NonclassicalPolyaryFormula(connective, args) =>
        TFF.NonclassicalPolyaryFormula(connective, args.map(tffInstantiateFormula(_, instances)))
      case _ => formula
    }
  }
  private[this] def tffLetReplaceTermAll(replacements: Map[String, Seq[TPTP.TFF.Term] => TPTP.TFF.Term], term: TPTP.TFF.Term): TPTP.TFF.Term = {
    import leo.datastructures.TPTP.TFF
    term match {
      case TFF.AtomicTerm(f, args) => replacements.get(f) match {
        case Some(instantiateFunction) => instantiateFunction(args)
        case None =>
          val replacedArgs = args.map(tffLetReplaceTermAll(replacements, _))
          TFF.AtomicTerm(f, replacedArgs)
      }
      case TFF.FormulaTerm(formula) => TFF.FormulaTerm(tffLetReplaceFormulaAll(replacements, formula))
      case _ => term
    }
  }
  private[this] def tffLetReplaceFormulaAll(replacements: Map[String, Seq[TPTP.TFF.Term] => TPTP.TFF.Term], formula: TPTP.TFF.Formula): TPTP.TFF.Formula = {
    import leo.datastructures.TPTP.TFF
    formula match {
      case TFF.AtomicFormula(f, args) => replacements.get(f) match {
        case Some(instantiateFunction) => instantiateFunction(args) match {
          case TFF.AtomicTerm(f, args) => TFF.AtomicFormula(f, args)
          case TFF.Variable(name) => TFF.FormulaVariable(name)
          case TFF.FormulaTerm(formula) => formula
          case _ => throw new SZSException(SZS_InputError, "Unexpected term binding when a formula was expected.")
        }
        case None =>
          val replacedArgs = args.map(tffLetReplaceTermAll(replacements, _))
          TFF.AtomicFormula(f, replacedArgs)
      }
      case TFF.QuantifiedFormula(quantifier, variableList, body) =>
        TFF.QuantifiedFormula(quantifier, variableList, tffLetReplaceFormulaAll(replacements, body))
      case TFF.UnaryFormula(connective, body) =>
        TFF.UnaryFormula(connective, tffLetReplaceFormulaAll(replacements, body))
      case TFF.BinaryFormula(connective, left, right) =>
        TFF.BinaryFormula(connective, tffLetReplaceFormulaAll(replacements, left), tffLetReplaceFormulaAll(replacements, right))
      case TFF.Equality(left, right) =>
        TFF.Equality(tffLetReplaceTermAll(replacements, left), tffLetReplaceTermAll(replacements, right))
      case TFF.Inequality(left, right) =>
        TFF.Inequality(tffLetReplaceTermAll(replacements, left), tffLetReplaceTermAll(replacements, right))
      case TFF.ConditionalFormula(condition, thn, els) =>
        TFF.ConditionalFormula(tffLetReplaceFormulaAll(replacements, condition),
          tffLetReplaceTermAll(replacements, thn), tffLetReplaceTermAll(replacements,els))
      case TFF.LetFormula(typing, binding, body) =>
        val updatedBinding = binding.map { case (lhs, rhs) =>
          (lhs, tffLetReplaceTermAll(replacements, rhs))
        }
        val updatedReplacements = replacements.removedAll(typing.keys)
        val replacedBody = tffLetReplaceTermAll(updatedReplacements, body)
        TFF.LetFormula(typing, updatedBinding, replacedBody)
      case TFF.NonclassicalPolyaryFormula(connective, args) =>
        TFF.NonclassicalPolyaryFormula(connective, args.map(tffLetReplaceFormulaAll(replacements, _)))
      case _ => formula
    }
  }
  ////// Functions related to TFF let-expression expansion END


  private[this] final def convertTFFBinaryConnective(connective: TPTP.TFF.BinaryConnective): HOLBinaryConnective = {
    import leo.modules.HOLSignature.{<=> => equiv, Impl => impl, <= => i_f, ||| => or, & => and, ~||| => nor, ~& => nand, <~> => niff}

    connective match {
      case TPTP.TFF.<=>   => equiv
      case TPTP.TFF.Impl  => impl
      case TPTP.TFF.<=    => i_f
      case TPTP.TFF.|     => or
      case TPTP.TFF.&     => and
      case TPTP.TFF.~|    => nor
      case TPTP.TFF.~&    => nand
      case TPTP.TFF.<~>   => niff
    }
  }

  private[this] final def convertTFFQuantifier(quantifier: TPTP.TFF.Quantifier): HOLUnaryConnective = {
    import leo.modules.HOLSignature.{Forall, Exists}

    quantifier match {
      case TPTP.TFF.! => Forall
      case TPTP.TFF.? => Exists
    }
  }

  ////////////////////////////////////////////////////////////////////////
  // Type processing
  ////////////////////////////////////////////////////////////////////////

  private[this] final def convertTFFType(sig: Signature)(typ: TPTP.TFF.Type): TypeOrKind = {
    import TPTP.TFF.QuantifiedType

    typ match {
      case QuantifiedType(_, _) =>
        val (variables, body0) = collectVarsUnderTFFTypeForall(typ)

        val variablesIt = variables.iterator
        var convertedVariables: Seq[String] = Vector.empty // We only need to store names; kind is always $tType
        while (variablesIt.hasNext) {
          val (name, ty) = variablesIt.next()
          val convertedTy = ty match {
            case Some(ty0) => convertTFFType0(sig)(ty0, Seq.empty)
            case None => throw new SZSException(SZS_TypeError, s"In type quantification, only '$$tType' is allowed as type/kind of type variables, but nothing was given.")
          }
          convertedTy match {
            case Left(_) => throw new SZSException(SZS_TypeError, s"In type quantification, only '$$tType' is allowed as type/kind of type variable, but '${ty.get.pretty}' was given.")
            case Right(kind) =>
              if (!kind.isTypeKind) throw new SZSException(SZS_TypeError, s"In type quantification, only '$$tType' is allowed as type/kind of type variables, but '${kind.pretty}' was given.")
          }
          convertedVariables = convertedVariables :+ name
        }

        val convertedBody = convertTFFType0(sig)(body0, convertedVariables)
        convertedBody match {
          case Left(convertedBody0) => Left(mkNAryPolyType(convertedVariables.length, convertedBody0))
          case Right(_) => throw new SZSException(SZS_TypeError, s"Body of type quantification does not yield a legal type: ${body0.pretty}")
        }
      case _ => convertTFFType0(sig)(typ, Vector.empty)
    }
  }


  private[this] final def convertTFFType0(sig: Signature)(typ: TPTP.TFF.Type,
                                                         typeVars: Seq[String]): TypeOrKind = {
    import TPTP.TFF.{AtomicType, MappingType, QuantifiedType, TypeVariable, TupleType}

    typ match {
      case QuantifiedType(_, _) => throw new SZSException(SZS_TypeError, s"Only rank-1 polymorphic types are supported; " +
        s"but the type expression contains a quantified type at non-prefix position.")

      case TypeVariable(name) =>
        val index = getDeBruijnIndexOf(typeVars)(name)
        if (index == -1) throw new SZSException(SZS_InputError, s"Unbound variable '$name' in type expression.")
        else Left(mkVarType(index))

      case AtomicType("$tType", Seq()) => Right(typeKind)

      case AtomicType(name, args) =>
        if (sig.exists(name)) {
          val convertedArgs = args.map(convertTFFType0(sig)(_, typeVars))
          val (convertedTypeArgs, convertedKindArgs) = convertedArgs.partitionMap(identity)
          if (convertedKindArgs.isEmpty) {
            Left(mkType(sig(name).key, convertedTypeArgs))
          } else {
            throw new SZSException(SZS_TypeError, s"Unexpected type argument: Only proper types are allowed, but ${convertedKindArgs.map(_.pretty).mkString(", ")} were found.")
          }
        } else {
          if (name.startsWith("$$")) throw new SZSException(SZS_InputError, s"Unknown system type or system type constructor '$name'.")
          else if (name.startsWith("$")) throw new SZSException(SZS_Inappropriate, s"Unsupported TPTP type or TPTP type constructor '$name'.")
          else throw new SZSException(SZS_InputError, s"Unknown type or type constructor '$name'. Please specify its kind first.")
        }

      case mt@MappingType(left, right) =>
        val convertedLeft = left.map(convertTFFType0(sig)(_, typeVars))
        val convertedRight = convertTFFType0(sig)(right, typeVars)

        val (convertedLeftTypeArgs, convertedLeftKindArgs) = convertedLeft.partitionMap(identity)
        if (convertedLeftTypeArgs.nonEmpty && convertedLeftKindArgs.isEmpty) {
          // all types: function type
          convertedRight match {
            case Left(convertedRight0) => Left(mkFunType(convertedLeftTypeArgs, convertedRight0))
            case _ => throw new SZSException(SZS_TypeError, s"Unexpected kind on right-hand side of mapping type '${mt.pretty}'" +
              s" when all left-hand sides were types.")
          }
        } else if (convertedLeftTypeArgs.isEmpty && convertedLeftKindArgs.nonEmpty) {
          // all kinds: type constructor
          convertedRight match {
            case Right(convertedRight0) => Right(Kind.mkFunKind(convertedLeftKindArgs, convertedRight0))
            case _ => throw new SZSException(SZS_TypeError, s"Unexpected type on right-hand side of mapping type '${mt.pretty}'" +
              s" when all left-hand sides were kinds.")
          }
        } else throw new SZSException(SZS_TypeError, s"Unexpected mixed types and kinds on left-hand side of mapping type '${mt.pretty}'. ")

      case TupleType(tys) =>
        val convertedTys = tys.map(convertTFFType0(sig)(_, typeVars))
        val (convertedTypeArgs, convertedKindArgs) = convertedTys.partitionMap(identity)
        if (convertedTypeArgs.nonEmpty && convertedKindArgs.isEmpty) {
          Left(Type.mkProdType(convertedTypeArgs))
        } else throw new SZSException(SZS_TypeError, s"Unexpected mixed types and kinds in tuple type '${typ.pretty}'. ")
    }
  }

  @inline private[this] final def collectVarsUnderTFFTypeForall(typ: TPTP.TFF.Type): (Seq[TPTP.TFF.TypedVariable], TPTP.TFF.Type) = {
    collectVarsUnderTFFTypeForall0(typ, Vector.empty)
  }

  @tailrec
  private[this] final def collectVarsUnderTFFTypeForall0(typ: TPTP.TFF.Type,
                                                         variables: Seq[TPTP.TFF.TypedVariable]): (Seq[TPTP.TFF.TypedVariable], TPTP.TFF.Type) = {
    import TPTP.TFF.QuantifiedType

    typ match {
      case QuantifiedType(varList, body) => collectVarsUnderTFFTypeForall0(body, variables ++ varList)
      case _ => (variables, typ)
    }
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // FOF processing
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  final def processAnnotatedFOF(sig: Signature)(statement: TPTP.FOFAnnotated): Option[Term] = {
    import TPTP.FOF.Logical

    statement.formula match {
      case Logical(f) if statement.role == "definition" =>
        processFOFDef(sig)(f) match {
          case _ => // TODO: Implement definition handling
            val res = convertFOFFormula(sig)(f)
            Out.info(s"Definitions in FOF are currently treated as axioms.")
            Some(res)
        }
      case Logical(f) => Some(convertFOFFormula(sig)(f))
    }
  }

  @inline private[this] final def processFOFDef(sig: Signature)(formula: TPTP.FOF.Formula): Option[(String, Term)] = {
    // TODO: Implement definition processing
    formula match {
      case _ => None
    }
  }

  ////////////////////////////////////////////////////////////////////////
  // Term processing
  ////////////////////////////////////////////////////////////////////////

  @inline private[this] final def convertFOFFormula(sig: Signature)(formula: TPTP.FOF.Formula): Term =
    convertFOFFormula0(sig)(formula, Vector.empty)

  private[this] final def convertFOFFormula0(sig: Signature)(formula: TPTP.FOF.Formula,
                                                                     termVars: Seq[String]): Term = {
    import TPTP.FOF.{AtomicFormula, QuantifiedFormula, UnaryFormula, BinaryFormula, Equality, Inequality}
    import leo.modules.HOLSignature.{===, !===, Not}

    formula match {
      case QuantifiedFormula(quantifier, variableList, body) =>
        val convertedQuantifier = convertFOFQuantifier(quantifier)
        val updatedTermVars = termVars ++ variableList
        val convertedBody = convertFOFFormula0(sig)(body, updatedTermVars)
        variableList.foldRight(convertedBody){ (_, acc) => convertedQuantifier.apply(λ(i)(acc)) }

      case UnaryFormula(connective, body) =>
        connective match {
          case TPTP.FOF.~ =>
            val convertedBody = convertFOFFormula0(sig)(body, termVars)
            Not(convertedBody)
        }

      case BinaryFormula(connective, left, right) =>
        val convertedLeft = convertFOFFormula0(sig)(left, termVars)
        val convertedRight = convertFOFFormula0(sig)(right, termVars)
        convertFOFBinaryConnective(connective)(convertedLeft, convertedRight)

      case Equality(left, right) =>
        val convertedLeft = convertFOFTerm(sig)(left, termVars)
        val convertedRight = convertFOFTerm(sig)(right, termVars)
        ===(convertedLeft, convertedRight)

      case Inequality(left, right) =>
        val convertedLeft = convertFOFTerm(sig)(left, termVars)
        val convertedRight = convertFOFTerm(sig)(right, termVars)
        !===(convertedLeft, convertedRight)

      case AtomicFormula(f, args) =>
        val convertedF = mkAtom(getOrCreateSymbol(sig)(f, mkSimplePredicateType(args.size)))(sig)
        val convertedArgs = args.map(convertFOFTerm(sig)(_, termVars))
        mkTermApp(convertedF, convertedArgs)
    }
  }

  private[this] final def convertFOFTerm(sig: Signature)(term: TPTP.FOF.Term, termVars: Seq[String]): Term = {
    import TPTP.FOF.{AtomicTerm, Variable, DistinctObject, NumberTerm}

    term match {
      case AtomicTerm(f, args) =>
        val convertedF = mkAtom(getOrCreateSymbol(sig)(f, mkSimpleFunctionType(args.size)))(sig)
        val convertedArgs = args.map(convertFOFTerm(sig)(_, termVars))
        mkTermApp(convertedF, convertedArgs)
      case Variable(name) =>
        val index = getDeBruijnIndexOf(termVars)(name)
        if (index == -1) throw new SZSException(SZS_InputError, s"Unbound variable '$name' in term expression.")
        else mkBound(i, index)
      case DistinctObject(name) => mkAtom(getOrCreateSymbol(sig)(name, i))(sig)
      case NumberTerm(value) => convertNumber(value)
    }
  }

  private[this] final def convertFOFBinaryConnective(connective: TPTP.FOF.BinaryConnective): HOLBinaryConnective = {
    import leo.modules.HOLSignature.{<=> => equiv, Impl => impl, <= => i_f, ||| => or, & => and, ~||| => nor, ~& => nand, <~> => niff}

    connective match {
      case TPTP.FOF.<=>   => equiv
      case TPTP.FOF.Impl  => impl
      case TPTP.FOF.<=    => i_f
      case TPTP.FOF.|     => or
      case TPTP.FOF.&     => and
      case TPTP.FOF.~|    => nor
      case TPTP.FOF.~&    => nand
      case TPTP.FOF.<~>   => niff
    }
  }

  private[this] final def convertFOFQuantifier(quantifier: TPTP.FOF.Quantifier): HOLUnaryConnective = {
    import leo.modules.HOLSignature.{Forall, Exists}

    quantifier match {
      case TPTP.FOF.! => Forall
      case TPTP.FOF.? => Exists
    }
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // CNF processing
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  final def processAnnotatedCNF(sig: Signature)(statement: TPTP.CNFAnnotated): Term = {
    import TPTP.CNF.Logical

    statement.formula match {
      case Logical(f) => convertCNFFormula(sig)(f) // No other kind of CNF formula exists
    }
  }

  @inline private[this] final def convertCNFFormula(sig: Signature)(formula: TPTP.CNF.Formula): Term = {
    var convertedLiterals: Seq[Term] = Vector.empty
    var termVars: Seq[String] = List.empty // We only prepend, this is more efficient in List
    // The general approach is: We don't pass through all literals before processing them (in order to collect the variables)
    // as this might be inefficient in very large formulas.
    // Instead, we keep track of all already seen variables in an explicit argument 'termVars'
    // which is also returned by the convertCNFX functions (containing the updated list of variables).
    // As we don't want to change already converted variables, new variables are prepended (i.e., incremented de Bruijn index).
    formula.foreach { lit =>
      val (convertedLiteral, updatedTermVars) = convertCNFLiteral(sig)(lit, termVars)
      convertedLiterals = convertedLiterals :+ convertedLiteral
      termVars = updatedTermVars
    }
    leo.datastructures.mkDisjunction(convertedLiterals)
  }

  private[this] final def convertCNFLiteral(sig: Signature)(literal: TPTP.CNF.Literal,
                                                            termVars: Seq[String]): (Term, Seq[String]) = {
    import TPTP.CNF.{PositiveAtomic, NegativeAtomic, Equality, Inequality}
    import leo.modules.HOLSignature.{===, !===, Not}

    literal match {
      case PositiveAtomic(formula) => convertCNFAtomicFormula(sig)(formula, termVars)
      case NegativeAtomic(formula) =>
        val (result, updatedTermVars) = convertCNFAtomicFormula(sig)(formula, termVars)
        (Not(result), updatedTermVars)
      case Equality(left, right) =>
        val (convertedLeft, updatedTermVars) = convertCNFTerm(sig)(left, termVars)
        val (convertedRight, updatedTermVars2) = convertCNFTerm(sig)(right, updatedTermVars)
        (===(convertedLeft, convertedRight), updatedTermVars2)
      case Inequality(left, right) =>
        val (convertedLeft, updatedTermVars) = convertCNFTerm(sig)(left, termVars)
        val (convertedRight, updatedTermVars2) = convertCNFTerm(sig)(right, updatedTermVars)
        (!===(convertedLeft, convertedRight), updatedTermVars2)
    }
  }

  private[this] final def convertCNFAtomicFormula(sig: Signature)(term: TPTP.CNF.AtomicFormula,
                                                                  termVars: Seq[String]): (Term, Seq[String]) = {
    val convertedFunc = mkAtom(getOrCreateSymbol(sig)(term.f, mkSimplePredicateType(term.args.size)))(sig)
    var convertedArgs: Seq[Term] = Vector.empty
    var termVars0 = termVars
    term.args.foreach { t =>
      val (convertedT, updatedTermVars) = convertCNFTerm(sig)(t, termVars0)
      convertedArgs = convertedArgs :+ convertedT
      termVars0 = updatedTermVars
    }
    (mkTermApp(convertedFunc, convertedArgs), termVars0)
  }

  private[this] final def convertCNFTerm(sig: Signature)(term: TPTP.CNF.Term,
                                                                 termVars: Seq[String]): (Term, Seq[String]) = {
    import TPTP.CNF.{AtomicTerm, Variable, DistinctObject}

    term match {
      case AtomicTerm(f, args) =>
        val convertedFunc = mkAtom(getOrCreateSymbol(sig)(f, mkSimpleFunctionType(args.size)))(sig)
        var convertedArgs: Seq[Term] = Vector.empty
        var termVars0 = termVars
        args.foreach { t =>
          val (convertedT, updatedTermVars) = convertCNFTerm(sig)(t, termVars0)
          convertedArgs = convertedArgs :+ convertedT
          termVars0 = updatedTermVars
        }
        (mkTermApp(convertedFunc, convertedArgs), termVars0)
      case Variable(name) => // Check if already seen before or if new: Add to termVars in the latter case.
        val index = getDeBruijnIndexOf(termVars)(name)
        var updatedTermVars = termVars
        val result = if (index == -1) {
          updatedTermVars = name +: termVars
          mkBound(i, getDeBruijnIndexOf(updatedTermVars)(name))
        } else mkBound(i, index)
        (result, updatedTermVars)
      case DistinctObject(name) =>
        (mkAtom(getOrCreateSymbol(sig)(name, i))(sig), termVars)
    }
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // Auxiliary methods
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  private[this] sealed abstract class VariableMarker
  private[this] case class TermVariableMarker(typ: Type) extends VariableMarker
  private[this] case object TypeVariableMarker extends VariableMarker

  @inline private[this] final def getOrCreateSymbol(sig: Signature)(name: String, ty: Type, force: Boolean = false): Signature.Key = {
    if (sig.exists(name)) sig(name).key
    else {
      if (name.startsWith("$") && !force) throw new SZSException(SZS_Inappropriate, s"System symbol or defined (TPTP) symbol '$name' is not supported or unknown.")
      else sig.addUninterpreted(name, ty)
    }
  }

  /** Analogous to [[getOrCreateSymbol]] only that a warning is issued additionally (used for TFF). */
  @inline private[this] final def getOrCreateSymbolWithWarning(sig: Signature)(name: String, ty: Type): Signature.Key = {
    if (sig.exists(name)) sig(name).key
    else {
      if (name.startsWith("$")) throw new SZSException(SZS_Inappropriate, s"System symbol or defined (TPTP) symbol '$name' is not supported or unknown.")
      else {
        leo.Out.warn(s"Symbol '$name' was used without specifying its type first. Assuming default type '${ty.pretty(sig)}' ...")
        sig.addUninterpreted(name, ty)
      }
    }
  }

  lazy val numberWarning: Unit = {
    leo.Out.warn(s"Leo-III currently does not fully support arithmetic. " +
      s"Numbers in the problem file are recognized, but there is no guarantee for adequateness of processing them whatsoever.")
  }
  @inline private[this] final def convertNumber(number: TPTP.Number): Term = {
    numberWarning
    number match {
      case TPTP.Integer(value) => mkInteger(value)
      case TPTP.Rational(numerator, denominator) => mkRational(numerator, denominator)
      case TPTP.Real(wholePart, decimalPlaces, exponent) => mkReal(wholePart, decimalPlaces, exponent)
    }
  }

  @inline private[this] final def mkSimplePredicateType(n: Int): Type = {
    val from = Seq.fill(n)(i)
    mkFunType(from, o)
  }

  @inline private[this] final def mkSimpleFunctionType(n: Int): Type = {
    val from = Seq.fill(n)(i)
    mkFunType(from, i)
  }

  @inline private[this] final def getDeBruijnIndexOf[A](vars: Seq[A])(variable: A): Int = {
    val idx = vars.lastIndexOf(variable)
    if (idx == -1) -1
    else vars.length - idx
  }

  private final def mkPolyQuantified(q: HOLUnaryConnective, variableMarkers: Seq[VariableMarker], body: Term): Term = {
    import leo.modules.HOLSignature.{Forall, TyForall}
    def mkPolyHelper(a: VariableMarker, b: Term): Term = a match {
      case TermVariableMarker(ty) => q.apply(λ(ty)(b))
      case TypeVariableMarker if q == Forall => TyForall(Λ(b))
      case TypeVariableMarker if q == HOLLambda => Λ(b)
      case _ => throw new IllegalArgumentException(s"Formalization of kinds other than * and ${q.pretty} not yet implemented.")
    }

    variableMarkers.foldRight(body)(mkPolyHelper)
  }
}
