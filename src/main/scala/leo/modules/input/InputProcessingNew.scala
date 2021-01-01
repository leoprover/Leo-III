package leo.modules.input

import leo.datastructures.{Kind, Role, Signature, Term, Type, TPTPAST => TPTP}
import leo.datastructures.Term.{mkApp, mkAtom, mkBound, mkTermApp, mkTypeApp, Λ, λ}
import leo.datastructures.Type.{mkFunType, mkNAryPolyType, mkProdType, mkType, mkUnionType, mkVarType, typeKind, ∀}
import leo.modules.output.{SZS_Inappropriate, SZS_InputError, SZS_SyntaxError, SZS_TypeError}
import leo.modules.SZSException
import leo.Out

import scala.annotation.tailrec

object InputProcessingNew {
  import leo.modules.HOLSignature.{i,o, rat, int, real, LitTrue, IF_THEN_ELSE, HOLUnaryConnective, HOLBinaryConnective}

  type TypeOrKind = Either[Type, Kind]
  type TermOrType = Either[Term, Type]
  // (Formula name, Term, Formula Role)
  type Result = (String, Term, Role)

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // Top-level processing
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  @inline private[this] final def processRole(role: String): Role = Role(role)

  @inline final def processAll(sig: Signature)(statements: Seq[TPTP.AnnotatedFormula]): Seq[Result] = statements.map(process(sig))

  final def process(sig: Signature)(statement: TPTP.AnnotatedFormula): Result = {
    import TPTP.{THFAnnotated, TFFAnnotated, FOFAnnotated, CNFAnnotated, TPIAnnotated}
    import leo.datastructures.Role_Definition

    val name = statement.name
    val role = processRole(statement.role)
    try {
      val maybeFormula: Option[Term] = statement match {
        case f@THFAnnotated(_, _, _, _) => processAnnotatedTHF(sig)(f)
        case f@TFFAnnotated(_, _, _, _) => processAnnotatedTFF(sig)(f)
        case f@FOFAnnotated(_, _, _, _) => processAnnotatedFOF(sig)(f)
        case f@CNFAnnotated(_, _, _, _) => Some(processAnnotatedCNF(sig)(f))
        case f@TPIAnnotated(_, _, _, _) => throw new SZSException(SZS_Inappropriate, "TPI format not supported (yet).")
      }
      maybeFormula match {
        case None => (name, LitTrue, role)
        case Some(formula) =>
          if (role == Role_Definition) (name, formula, processRole("axiom")) // If original role was definition but formula
          // is returned; then it was not recognized as definition and is now added as axiom
          else (name, formula, role)
      }
    } catch {
      case e: SZSException => throw new SZSException(e.status, s"Interpreter error in annotated TPTP formula '${name}': ${e.getMessage}")
    }
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // THF processing
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  private[this] final def processAnnotatedTHF(sig: Signature)(statement: TPTP.THFAnnotated): Option[Term] = {
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
                definition match {
                  case Left(def0) =>
                    if (meta._ty == def0.ty) sig.addDefinition(meta.key, def0.betaNormalize.etaExpand)
                    else throw new SZSException(SZS_InputError, s"Trying to provide definition for symbol '$name', but type of definition " +
                      s"(${def0.ty.pretty(sig)}) does not match type declaration of symbol (${meta._ty.pretty}).")
                  case Right(_) => throw new SZSException(SZS_InputError, s"Type definition in formula ${statement.name} is not supported, yet.")
                }
              } else throw new SZSException(SZS_InputError, s"Trying to provide a definition for interpreted/defined symbol '$name'.")
            } else {
              definition match {
                case Left(def0) => sig.addDefined(name, def0.betaNormalize.etaExpand, def0.ty)
                case Right(_) => throw new SZSException(SZS_InputError, s"Type definition in formula ${statement.name} is not supported, yet.")
              }
            }
            None
          case None =>
            val res = convertTHFFormula(sig)(formula)
            res match {
              case Left(axiom) =>
                Out.info(s"No direction of definition ${statement.name} detected. Treating as axiom ...")
                Some(axiom)
              case Right(_) => throw new SZSException(SZS_InputError, s"Definition ${statement.name} contains a type statement on top-level, where a formula was expected.")
            }
        }

      case Logical(formula) =>
        val res = convertTHFFormula(sig)(formula)
        res match {
          case Left(formula0) => Some(formula0)
          case Right(_) => throw new SZSException(SZS_InputError, s"${statement.name} contains a type statement on top-level, where a formula was expected.")
        }
    }
  }

  @inline private[this] final def processTHFDef(sig: Signature)(formula: TPTP.THF.Formula): Option[(String, TermOrType)] = {
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

  private[this] final def convertTHFFormula(sig: Signature)(formula: TPTP.THF.Formula): TermOrType = {
    convertTHFFormula0(sig)(formula, Vector.empty, Vector.empty, Map.empty)
  }
  private[this] final def convertTHFFormula0(sig: Signature)(formula: TPTP.THF.Formula,
                                                             termVars: Seq[String],
                                                             typeVars: Seq[String],
                                                             vars: Map[String, VariableMarker]): TermOrType = {
    import TPTP.THF.{FunctionTerm, QuantifiedFormula, Variable, UnaryFormula, BinaryFormula,
      Tuple, ConditionalTerm, LetTerm, ConnectiveTerm, DistinctObject, NumberTerm}

    formula match {
      case QuantifiedFormula(quantifier, variableList, body) =>
        val convertedQuantifier = processTHFQuantifier(quantifier)
        val varibleListIt = variableList.iterator
        var newTermVars: Seq[String] = termVars
        var newTypeVars: Seq[String] = typeVars
        var newVars: Map[String, VariableMarker] = vars
        var localVariableMarkers: Seq[VariableMarker] = Vector.empty
        while (varibleListIt.hasNext) {
          val (name, typeOrKind) = varibleListIt.next()
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
                s" quantifications, but $name was assigned ${convertedKind.pretty}.")
          }
        }
        val convertedBody = convertTHFFormula0(sig)(body, newTermVars, newTypeVars, newVars)
        convertedBody match {
          case Left(convertedBody0) => Left(mkPolyQuantified(convertedQuantifier, localVariableMarkers, convertedBody0))
          case Right(_) => throw new SZSException(SZS_InputError, s"Body of quantification not recognized as well-formed formula.")
        }

      case UnaryFormula(connective, body) =>
        val convertedConnective = convertTHFUnaryConnective(connective)
        assert(convertedConnective == leo.modules.HOLSignature.Not, s"Unexpected error: Expected to translate unary connective ~, but saw '${convertedConnective.pretty(sig)}' instead.")
        val convertedBody = convertTHFFormula0(sig)(body, termVars, typeVars, vars)
        convertedBody match {
          case Left(convertedBody0) => Left(convertedConnective.apply(convertedBody0))
          case Right(_) => throw new SZSException(SZS_InputError, s"Term expected in unary formula but type expressions found.")
        }

      case BinaryFormula(TPTP.THF.App, left, right) => // handle specially, as there might be polymoprhic symbols involved
        val convertedLeft = convertTHFFormula0(sig)(left, termVars, typeVars, vars)
        convertedLeft match {
          case Left(convertedLeft0) =>
            if (convertedLeft0.ty.isPolyType) {
              import leo.modules.HOLSignature.{=== => EQ, !=== => NEQ}
              // Special case (due to TH0 compliance): If (=) or (!=) used as prefix conn_term, we need to provide the type argument
              // explicitly as it is not expected to be given by the user.
              // E.g.: (=) @ t1 @ t2 ... instead of (=) @ type @ t1 @ t2.
              // For the TH1 function @=, this needs to be given explicitly by the user
              // as in (@=) @ type @ t1 @ t2. However, both @= and (=) are mapped to the same equality symbol.
              // Hence, we cannot know at this place whether to expect a type argument or not. So we try both in this case.
              if (convertedLeft0 == HOLBinaryConnective.toTerm(EQ)) {
                // Special case 1: Equality -- Either fill implicit type argument manually or use explicit type argument
                try {
                  val convertedRight = convertTHFFormula0(sig)(right, termVars, typeVars, vars)
                  convertedRight match {
                    case Left(convertedRight0) =>
                      val intermediate = mkTypeApp(convertedLeft0, convertedRight0.ty)
                      Left(mkTermApp(intermediate, convertedRight0))
                    case Right(_) => ???
                  }
                } catch {
                  case e: SZSException =>
                    val convertedRight = convertTHFType0(sig)(right, typeVars)
                    convertedRight match {
                      case Left(convertedRight0) =>
                        Left(mkTypeApp(convertedLeft0, convertedRight0))
                      case Right(_) => ???
                    }
                }

              } else if (convertedLeft0 == HOLBinaryConnective.toTerm(NEQ)) {
                // Special case 2: Non-equality -- Fill implicit type argument manually
                val convertedRight = convertTHFFormula0(sig)(right, termVars, typeVars, vars)
                convertedRight match {
                  case Left(convertedRight0) =>
                    val intermediate = mkTypeApp(convertedLeft0, convertedRight0.ty)
                    Left(mkTermApp(intermediate, convertedRight0))
                  case Right(_) => ???
                }
              } else {
                // Standard polymorphic case: Expect type argument
                val convertedRight = convertTHFType0(sig)(right, typeVars)
                convertedRight match {
                  case Left(convertedRight0) => Left(mkTypeApp(convertedLeft0, convertedRight0))
                  case Right(_) => ???
                }
              }
            } else {
              // Standard monomorphic case: Expect term argument
              val convertedRight = convertTHFFormula0(sig)(right, termVars, typeVars, vars)
              convertedRight match {
                case Left(convertedRight0) => Left(mkTermApp(convertedLeft0, convertedRight0))
                case Right(_) => ???
              }
            }
          case Right(_) => throw new SZSException(SZS_InputError, s"Found an application with type expression as left-side argument at formula-level.")
        }

      case BinaryFormula(connective, left, right) =>
        assert(connective != TPTP.THF.App)
        val convertedConnective = convertTHFBinaryConnective(connective)
        val convertedLeft = convertTHFFormula0(sig)(left, termVars, typeVars, vars)
        val convertedRight = convertTHFFormula0(sig)(right, termVars, typeVars, vars)
        (convertedLeft, convertedRight) match {
          case (Left(convertedLeft0), Left(convertedRight0)) => Left(convertedConnective.apply(convertedLeft0, convertedRight0))
          case _ => throw new SZSException(SZS_InputError, s"Terms expected in binary formula but type expressions found.")
        }

      case ft@FunctionTerm(f, args) => {
        if (sig.exists(f)) {
          val meta = sig(f)
          if (ft.isConstant) {
            val func = mkAtom(meta.key)(sig)
            Left(func)
          } else {
            val func = mkAtom(meta.key)(sig)
            ???
          }
        } else {
          if (ft.isUninterpretedFunction) throw new SZSException(SZS_InputError, s"Symbol '$f' is unknown, please specify its type first.")
          else throw new SZSException(SZS_InputError, s"System symbol or defined (TPTP) symbol '$f' is not supported and/or unknown. Did you spell it correctly?")
        }
      }

      case Variable(name) =>
        vars.get(name) match {
          case Some(marker) => marker match {
            case TermVariableMarker(ty) =>
              val index = getDeBruijnIndexOf(termVars)(name)
              Left(mkBound(ty, index))
            case TypeVariableMarker =>
              val index = getDeBruijnIndexOf(typeVars)(name)
              Right(mkVarType(index))
          }
          case None => throw new SZSException(SZS_InputError, s"Unbound variable '$name' in formula.")
        }

      case Tuple(elements) => throw new SZSException(SZS_Inappropriate, "Leo-III currently does not support tuples.")

      case ConditionalTerm(condition, thn, els) =>
        val convertedCondition = convertTHFFormula0(sig)(condition, termVars, typeVars, vars)
        val convertedThn = convertTHFFormula0(sig)(thn, termVars, typeVars, vars)
        val convertedEls = convertTHFFormula0(sig)(els, termVars, typeVars, vars)

        (convertedCondition, convertedThn, convertedEls) match {
          case (Left(c), Left(t), Left(e)) =>
            if (c.ty == o) {
              if (t.ty == e.ty) {
                import leo.datastructures.Term.{λ, mkBound}
                import leo.modules.HOLSignature.{Choice, Impl, &, Not, ===}
                val ty = t.ty
                Left(Choice(λ(ty)(&(Impl(c, ===(mkBound(ty, 1), t)),
                  Impl(Not(c), ===(mkBound(ty, 1), e))))))
              } else throw new SZSException(SZS_TypeError, s"Alternatives of if-then-else are not of same type.")
            } else throw new SZSException(SZS_TypeError, s"Condition of if-then-else is not Boolean typed.")
          case _ => throw new SZSException(SZS_TypeError, s"Arguments of if-then-else were not all recognized to be well-formed terms.")
        }

      case LetTerm(typing, binding, body) => ???

      case ConnectiveTerm(conn) => // This is not pretty ... but well, life is hard.
        if (conn.isInstanceOf[TPTP.THF.UnaryConnective]) Left(convertTHFUnaryConnective(conn.asInstanceOf[TPTP.THF.UnaryConnective]))
        else Left(convertTHFBinaryConnective(conn.asInstanceOf[TPTP.THF.BinaryConnective]))

      case DistinctObject(name) =>
        if (sig.exists(name)) Left(mkAtom(sig(name).key)(sig))
        else Left(mkAtom(sig.addUninterpreted(name, i))(sig))

      case NumberTerm(value) => throw new SZSException(SZS_Inappropriate, s"Arithmetic not supported by Leo-III.")
    }
  }

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
    import leo.modules.HOLSignature.{Not => not, Forall => forall, Exists => exists, Choice => choice, Description => desc}

    connective match {
      case TPTP.THF.~ => not
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

  ////// Little workaround to have the usual application (s @ t) a corresponding HOLBinbaryConnective
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
      case TPTP.THF.Impl  => impl
      case TPTP.THF.<=    => i_f
      case TPTP.THF.|    => or
      case TPTP.THF.&   => and
      case TPTP.THF.~|   => nor
      case TPTP.THF.~&  => nand
      case TPTP.THF.<~>  => !=== //niff
      case TPTP.THF.App   => @@@
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
            case Left(ty0) => throw new SZSException(SZS_TypeError, s"In type quantification, only '$$tType' is allowed as type/kind, but '${ty.pretty}' was given.")
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
    import TPTP.THF.{FunctionTerm, QuantifiedFormula, Variable, UnaryFormula, BinaryFormula,
      Tuple, ConditionalTerm, LetTerm, ConnectiveTerm, DistinctObject, NumberTerm}

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

      case QuantifiedFormula(_, _, _) => throw new SZSException(SZS_TypeError, s"Only rank-1 polymorphic types are suported; " +
        s"but the type expression contains a quantified type at non-prefix position.")

      case Variable(name) =>
        val index = getDeBruijnIndexOf(vars)(name)
        if (index == -1) throw new SZSException(SZS_InputError, s"Unbound variable '$name' in type expression.")
        else  Left(mkVarType(index))

      case BinaryFormula(connective, left, right) =>
        import TPTP.THF.{FunTyConstructor, ProductTyConstructor, SumTyConstructor, App}

        connective match {
          case FunTyConstructor | ProductTyConstructor | SumTyConstructor | App =>
            val convertedLeft = convertTHFType0(sig)(left, vars)
            val convertedRight = convertTHFType0(sig)(right, vars)
            (convertedLeft, convertedRight) match {
              case (Left(left0), Left(right0)) =>
                val result = (connective: @unchecked) match { // Exhaustiveness guaranteed by match-case condition a few lines above
                  case FunTyConstructor => mkFunType(left0, right0)
                  case ProductTyConstructor => mkProdType(left0, right0)
                  case SumTyConstructor => mkUnionType(left0, right0)
                  case App => left0.app(right0)
                }
                Left(result)
              case (Right(left0), Right(right0)) if connective == FunTyConstructor => Right(Kind.mkFunKind(left0, right0))
              case _ => throw new SZSException(SZS_TypeError, s"Binary type '${typ.pretty}' either contains both types and $$tType which is not allowed," +
                s"or contains $$tType outside the context of the function type constructor '>'.")
            }
          case _ => throw new SZSException(SZS_TypeError, s"Top-level binary type (one of '>', '*', '+', or '@') expected but term connective '${connective.pretty}' was given.")
        }

      case Tuple(elements) =>
        val convertedTys = elements.map(convertTHFType0(sig)(_, vars))
        val (tys, kinds) = convertedTys.partitionMap(identity)
        if (kinds.isEmpty) {
          throw new SZSException(SZS_Inappropriate, "Leo-III currently does not support tuples.")
        } else throw new SZSException(SZS_TypeError, s"Tuple type '${typ.pretty}' contains kinds (such as $$tType) which is not allowed.")

      //      case LetTerm(typing, binding, body) => ??? // TODO: We could, in principle, allow let-terms in type expressions.
      //      case DistinctObject(name) => ??? // TODO: Can a distinct object serve as type identifier?
      case _ => throw new SZSException(SZS_InputError, s"Malformed type-expression: '${typ.pretty}'.")
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

  ////////////////////////////////////////////////////////////////////////
  // Term processing
  ////////////////////////////////////////////////////////////////////////

  final def processAnnotatedTFF(sig: Signature)(statement: TPTP.TFFAnnotated): Option[Term] = ???

  ////////////////////////////////////////////////////////////////////////
  // Type processing
  ////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // FOF processing
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  final def processAnnotatedFOF(sig: Signature)(statement: TPTP.FOFAnnotated): Option[Term] = ???

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // CNF processing
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  final def processAnnotatedCNF(sig: Signature)(statement: TPTP.CNFAnnotated): Term = {
    import TPTP.CNF.Logical

    statement.formula match {
      case Logical(f) => processCNF(f) // No other kind of CNF formula exists
    }
  }

  private[this] final def processCNF(cnfFormula: TPTP.CNF.Formula): Term = ???

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // Auxiliary methods
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  private[this] sealed abstract class VariableMarker
  private[this] case class TermVariableMarker(typ: Type) extends VariableMarker
  private[this] case object TypeVariableMarker extends VariableMarker

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
      case _       => throw new IllegalArgumentException(s"Formalization of kinds other than * and ${q.pretty} not yet implemented.")
    }
    variableMarkers.foldRight(body)(mkPolyHelper)
  }
}
