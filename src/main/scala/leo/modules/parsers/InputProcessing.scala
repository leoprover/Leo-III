package leo.modules.parsers

import scala.language.implicitConversions

import leo.Out
import leo.datastructures.tptp.Commons.{AnnotatedFormula, Term => TPTPTerm, Variable}
import leo.datastructures.tptp.Commons.{THFAnnotated, TPIAnnotated, TFFAnnotated, FOFAnnotated, CNFAnnotated}
import leo.datastructures.tptp.Commons.{Var, Func, DefinedFunc, SystemFunc, Equality}
import leo.datastructures._
import Term.{mkAtom,λ,Λ, mkBound,mkTermApp}
import Type.{mkFunType,mkType,∀,mkVarType, typeKind,mkProdType, mkUnionType}

import leo.modules.SZSException
import leo.modules.output.{SZS_InputError,SZS_TypeError}


/**
  * Processing module from TPTP input.
  * Declarations are inserted into the given Signature,
  * terms are returned in internal term representation.
  *
  * @author Alexander Steen
  * @since 18.06.2014
  * @todo - Cannot handle implicit quantified variables in FOF dialect
  * @todo - Cannot handle CNF and TPI
  */
object InputProcessing {
  import leo.modules.HOLSignature.{i,o, rat, int, real, LitTrue, IF_THEN_ELSE, HOLUnaryConnective, HOLBinaryConnective}

  // (Formula name, Term, Formula Role)
  type Result = (String, Term, Role)

  // Types for replacing bound variables by de bruijn indices
  type TermVarReplaces = (Map[Variable, (Type, Int)], Int) // (varname -> (vartype, position in list), index offset)
  type TypeVarReplaces = (Map[Variable, (Kind, Int)], Int)
  type Replaces = (TermVarReplaces, TypeVarReplaces)
  def termMapping(r: Replaces) = (r._1)._1
  def typeMapping(r: Replaces) = (r._2)._1
  def termOffset(r: Replaces) = (r._1)._2
  def typeOffset(r: Replaces) = (r._2)._2
  def noRep: Replaces = ((Map.empty, 0), (Map.empty, 0))

  type TypeOrKind = Either[Type, Kind]
  type TermOrType = Either[Term , Type]
  type ProcessedVar = (Variable, TypeOrKind)
  /**
   * Assumptions:
   * - To guarantee coherence, the processing is invoked in the right order (i.e. included files are parsed an processed before all
   * following tptp statements)
   *
   * Side effects: All declarations that are not representable as term (e.g. type declarations, subtype declarations) are
   * inserted into the signature `sig` while processing.
   *
   * @param sig The signature declarations are inserted into
   * @param input The TPTP formula to process/translate
   * @return A List of tuples (name, term, role) of translated terms
   */
  def processAll(sig: Signature)(input: Seq[AnnotatedFormula]): Seq[Result] = {
    input.map(process(sig)(_))
  }

  private final def processRole(role: String): Role = Role(role)
  private final def roleToClauseOrigin(role: Role): ClauseOrigin = role match {
    case Role_Conjecture => FromConjecture
    case Role_NegConjecture => FromConjecture
    case _ => FromAxiom
  }
  private final def singleTermToClause(t: Term, role: Role): Clause = {
    Clause.mkClause(Seq(Literal.mkLit(t, true)), roleToClauseOrigin(role))
  }

  def process(sig: Signature)(input: AnnotatedFormula): Result = {
    val p = input match {
      case _:TPIAnnotated => processTPI(sig)(input.asInstanceOf[TPIAnnotated])
      case _:THFAnnotated => processTHF(sig)(input.asInstanceOf[THFAnnotated])
      case _:TFFAnnotated => processTFF(sig)(input.asInstanceOf[TFFAnnotated])
      case _:FOFAnnotated => processFOF(sig)(input.asInstanceOf[FOFAnnotated])
      case _:CNFAnnotated => processCNF(sig)(input.asInstanceOf[CNFAnnotated])
    }
    p match {
//      case None => val role = processRole(input.role); (input.name, singleTermToClause(LitTrue, role), role)
      case None => val role = processRole(input.role); (input.name, LitTrue, role)
      case Some(res) => res
    }
  }


  //////////////////////////
  // TPI Formula processing
  //////////////////////////

  def processTPI(sig: Signature)(input: TPIAnnotated): Option[Result] = ???


  //////////////////////////
  // THF Formula processing
  //////////////////////////

  def processTHF(sig: Signature)(input: THFAnnotated): Option[Result] = {
//    println(input.formula.toString)
    import leo.datastructures.tptp.thf.{Sequent, Logical, Typed, Term}

    input.formula match {
      case Logical(lf) if input.role == "definition" => {
        processTHFDef(sig)(lf) match {
          case None => {
            Out.info(s"No direction of definition ${input.name} detected. Treating as axiom.")
            val role = processRole("axiom")
            val res = processTHF0(sig)(lf, noRep)
            if (res.isLeft)
              Some((input.name, res.left.get, role))
            else
              throw new SZSException(SZS_InputError)
          }
          case Some((defName, defDef)) => {
            if (sig.exists(defName)) {
              val meta = sig(defName)
              if (meta.isUninterpreted && meta._ty == defDef.ty) {
                sig.addDefinition(meta.key, defDef.betaNormalize.etaExpand)
              } else {
                Out.debug("Old type: " + meta._ty.pretty)
                Out.debug("Symbol: " + defName)
                Out.debug("Def:" +defDef.pretty)
                Out.debug("Type of def: "+defDef.ty.pretty)
                Out.warn("Symbol "+defName + " already defined. Redefinition ignored.")
              }
            } else {
              sig.addDefined(defName, defDef.betaNormalize.etaExpand, defDef.ty)
            }
            None
          }
        }
                                                        }
      case Logical(Typed(Term(Func(atom, _)),ty)) if input.role == "type" => {
                                                        convertTHFType(sig)(ty, noRep) match {
                                                          case Left(ty) => sig.addUninterpreted(atom, ty)
                                                          case Right(k) => sig.addTypeConstructor(atom, k)
                                                        }
                                                        None
                                                      }
      case Logical(lf)                               => val role = processRole(input.role)
                                                        val res = processTHF0(sig)(lf, noRep)
                                                        if (res.isLeft)
                                                          Some((input.name, res.left.get, role))
                                                        else
                                                          throw new SZSException(SZS_InputError)
      case Sequent(_,_)                              => throw new IllegalArgumentException("Processing of THF sequents not implemented")
    }
  }

  import leo.datastructures.tptp.thf.{LogicFormula => THFLogicFormula}
  protected[parsers] def processTHFDef(sig: Signature)(input: THFLogicFormula): Option[(String, Term)] = {
    import leo.datastructures.tptp.thf.{Binary, Term, Eq}
    input match {
      case Binary(Term(Func(name, Seq())), Eq, right) => {
        val res = processTHF0(sig)(right, noRep)
        if (res.isLeft)
          Some(name, res.left.get)
        else
          throw new SZSException(SZS_InputError, "Type detected on right side of definition statement.")
      }
      case _                                        => None
    }
  }

  protected[parsers] def processTHF0(sig: Signature)(input: THFLogicFormula, replaces: Replaces): TermOrType = {
    import leo.datastructures.tptp.thf.{Typed, Binary, Unary, Quantified, Connective, Term, BinType, Subtype, Cond, Let, App => THFApp}

    input match {
      case Typed(f, ty) => processTHF0(sig)(f,replaces) // TODO: What to do with type information?
      case Binary(left, conn, right) if conn == THFApp => {
        val processedLeft = processTHF0(sig)(left, replaces)
        if (processedLeft.isLeft) {
          val processedLeft2 = processedLeft.left.get
          import leo.datastructures.Term.{mkTermApp, mkTypeApp}
          if (processedLeft2.ty.isPolyType) {
            val processedRight = convertTHFType(sig)(right, replaces)
            if (processedRight.isLeft)
              mkTypeApp(processedLeft2, processedRight.left.get)
            else
              throw new SZSException(SZS_TypeError, "Type argument expected but kind was found")
          }
          else {
            val res = processTHF0(sig)(right, replaces)
            if (res.isLeft)
              mkTermApp(processedLeft2, res.left.get)
            else
              mkTypeApp(processedLeft2, res.right.get)
              //throw new SZSException(SZS_InputError, "type on non-poly left side found.")
          }

        } else
          throw new SZSException(SZS_InputError, "THFAPP left side type.")

      }
      case Binary(left, conn, right) => try {
        processTHFBinaryConn(conn).apply(processTHF0(sig)(left, replaces).left.get,processTHF0(sig)(right, replaces).left.get)
        } catch {
          case e:java.util.NoSuchElementException => throw new SZSException(SZS_InputError, e.toString)
        }
      case Unary(conn, f) => try {
        processTHFUnaryConn(conn).apply(processTHF0(sig)(f, replaces).left.get)
        } catch {
        case e:java.util.NoSuchElementException => throw new SZSException(SZS_InputError, e.toString)
        }
      case Quantified(q, vars, matrix) => {
        val quantifier = processTHFUnaryConn(q)
        var newReplaces = replaces

        // Fold through the variables to propagate bindings trough variable list
        // and save bindings to `newReplaces` for body conversion
        val processedVars = vars.map(_ match { // FIXME: fold like I said two years ago, not map!
          case (name, None) => {
            termMapping(newReplaces).get(name) match {
              case None => newReplaces = ((termMapping(newReplaces).+((name,(i, termMapping(newReplaces).size+1))),termOffset(newReplaces)),newReplaces._2)
              case _ =>  newReplaces = ((termMapping(newReplaces).+((name,(i, termMapping(newReplaces).size+1))),termOffset(newReplaces)+1),newReplaces._2)
            }
            (name, Left(i))
          }
          case (name, Some(ty)) => convertTHFType(sig)(ty, newReplaces) match {
            case Left(t) => {
              termMapping(newReplaces).get(name) match {
                case None => newReplaces = ((termMapping(newReplaces).+((name,(t, termMapping(newReplaces).size+1))),termOffset(newReplaces)),newReplaces._2)
                case _ =>  newReplaces = ((termMapping(newReplaces).+((name,(t, termMapping(newReplaces).size+1))),termOffset(newReplaces)+1),newReplaces._2)
              }
              (name, Left(t))
            }
            case Right(k) => {
              typeMapping(newReplaces).get(name) match {
                case None => newReplaces = (newReplaces._1,(typeMapping(newReplaces).+((name,(k, typeMapping(newReplaces).size+1))),typeOffset(newReplaces)))
                case _ =>  newReplaces = (newReplaces._1,(typeMapping(newReplaces).+((name,(k, typeMapping(newReplaces).size+1))),typeOffset(newReplaces) +1))
              }
              (name, Right(k))
            }
          }

          })
        val intermediateRes = processTHF0(sig)(matrix, newReplaces)
        if (intermediateRes.isLeft)
          mkPolyQuantified(quantifier, processedVars, intermediateRes.left.get)
        else
          throw new SZSException(SZS_InputError)
      }
      case Connective(c) => {
        c.fold(cc => Left(processTHFBinaryConn(cc)),cc => Left(processTHFUnaryConn(cc)))
      }
      case Term(Var(name)) => termMapping(replaces).get(name) match {
        case None => typeMapping(replaces).get(name) match {
          case Some((k, scope))  => Type.mkVarType(scope)
          case _ => throw new IllegalArgumentException("Unbound variable found in formula: "+input.toString)
        }
        case Some((ty, scope)) => {
          assert(typeMapping(replaces).get(name).isEmpty)
          mkBound(ty, termMapping(replaces).size + termOffset(replaces) - scope +1)
        }
      }
      case Term(t) => processTerm(sig)(t, replaces, false)
      case BinType(binTy) => throw new IllegalArgumentException("Binary Type formulae should not appear on top-level")
      case Subtype(left,right) => ???
      case Cond(c, thn, els) => {
        try {
          IF_THEN_ELSE(processTHF0(sig)(c, replaces).left.get, processTHF0(sig)(thn, replaces).left.get, processTHF0(sig)(els, replaces).left.get)
        } catch {
          case e:java.util.NoSuchElementException => throw new SZSException(SZS_InputError,e.toString)
        }
      }
      case Let(binding, in) => Out.warn("Unsupported let-definition in term, treated as $true."); LitTrue()
    }
  }

  ////// Little workaround to have the usual application (s @ t) a corresponding HOLBinbaryConnective
  final object @@@ extends HOLBinaryConnective {
    val key = Integer.MIN_VALUE // Dont care, we dont want to use unapply
    val ty = ???
    override def apply(left: Term, right: Term): Term = Term.mkTermApp(left, right)
  }
  //////

  import leo.datastructures.tptp.thf.{BinaryConnective => THFBinaryConnective}
  protected[parsers] def processTHFBinaryConn(conn: THFBinaryConnective): HOLBinaryConnective = {
    import leo.datastructures.tptp.thf.{Eq => THFEq, Neq => THFNeq, <=> => THFEquiv, Impl => THFImpl, <= => THFIf, <~> => THFNiff, ~| => THFNor, ~& => THFNand, | => THFOr, & => THFAnd, App => THFApp}
    import leo.modules.HOLSignature.{<=> => equiv, Impl => impl, <= => i_f, ||| => or, & => and, ~||| => nor, ~& => nand, <~> => niff, ===, !===}

    conn match {
      case THFEq => ===
      case THFNeq => !===
      case THFEquiv => === //equiv
      case THFImpl  => impl
      case THFIf    => i_f
      case THFOr    => or
      case THFAnd   => and
      case THFNor   => nor
      case THFNand  => nand
      case THFNiff  => !=== //niff
      case THFApp   => @@@
    }
  }

  import leo.datastructures.tptp.thf.{UnaryConnective => THFUnaryConnective}
  protected[parsers] def processTHFUnaryConn(conn: THFUnaryConnective): HOLUnaryConnective = {
    import leo.datastructures.tptp.thf.{~ => THFNot, !! => THFAllComb, ?? => THFExistsComb}
    import leo.modules.HOLSignature.{Not => not, Forall => forall, Exists => exists}

    conn match {
      case THFNot => not
      case THFAllComb => forall
      case THFExistsComb => exists
    }
  }

  import leo.datastructures.tptp.thf.{Quantifier => THFQuantifier}
  protected[parsers] def processTHFUnaryConn(conn: THFQuantifier): HOLUnaryConnective = {
    import leo.datastructures.tptp.thf.{! => THFAll, ? => THFExists, ^ => THFLambda, @+ => THFChoice, @- => THFDesc}
    import leo.modules.HOLSignature.{Forall, Exists, Choice, Description}

    conn match {
      case THFAll => Forall
      case THFExists => Exists
      case THFLambda => new HOLUnaryConnective { // little hack here, to simulate a lambda, the apply function is the identity
                                                 // this is because the mkPolyQuantified will apply a new abstraction
        val key: Signature#Key = Integer.MIN_VALUE // just for fun!
        lazy val ty = ???
        override def apply(arg: Term) = arg
      }

      case THFChoice => Choice
      case THFDesc => Description

      case _ => throw new IllegalArgumentException("Illegal quantifier symbol:" +conn.toString)
    }
  }

  protected[parsers] def convertTHFType(sig: Signature)(typ: THFLogicFormula, replaces: Replaces): TypeOrKind = {
    import leo.datastructures.tptp.thf.{Quantified, Term, BinType, Binary, App}

    typ match {
      case Quantified(q, vars, matrix) => {
        import leo.datastructures.tptp.thf.{!> => THFTyForAll, ?* => THFTyExists}

        q match {
          case THFTyForAll => {
            val processedVars = vars.map(_ match {
              case (name, None) => (name, Right(typeKind)) // * is assumed when no type is given
              case (name, Some(ty)) => (name, convertTHFType(sig)(ty, replaces))
            })
            require(processedVars.forall(_._2.isRight), "Only '$tType' as type assertion is allowed for type variables in quantified types")
            val newReplaces = processedVars.foldLeft(replaces)({case (repl,vari) => vari match {
              case (name, Left(ty)) => {
                termMapping(repl).get(name) match {
                  case None => ((termMapping(repl).+((name,(ty, termMapping(repl).size+1))),termOffset(repl)),repl._2)
                  case _ =>  ((termMapping(repl).+((name,(ty, termMapping(repl).size+1))),termOffset(repl)+1),repl._2)
                }
              }
              case (name, Right(k)) => {
                typeMapping(repl).get(name) match {
                  case None => (repl._1,(typeMapping(repl).+((name,(k, typeMapping(repl).size+1))),typeOffset(repl)))
                  case _ =>  (repl._1,(typeMapping(repl).+((name,(k, typeMapping(repl).size+1))),typeOffset(repl)+1))
                }
              }
            }})
            processedVars.foldRight(convertTHFType(sig)(matrix,newReplaces).left.get)({case (_,b) => ∀(b)}) // NOTE: this is only allowed on top-level
            // the body of quantification must be a type.
            // TODO: better error treating
          }
          case THFTyExists => ???
          case _ => throw new SZSException(SZS_InputError, "Illegal quantifier on type level: " + typ.toString)
        }
      }
      case Term(t) => {
        t match {
//          case Func(k, args) => {
//            if (sig(k).hasKind)
//              if (sig(k)._kind.arity == args.length) {
//                val converted = args.map(x => convertTHFType(sig)(Term(x), replaces))
//                if (converted.forall(_.isLeft))
//                  mkType(sig(k).key, converted.map(_.left.get))
//                else throw new SZSException(SZS_TypeError)
//              } else throw new SZSException(SZS_TypeError, s"Arity of sort symbol does not match argument count: ${t.toString}")
//            else
//              throw new SZSException(SZS_TypeError, s"Using term constant inside type: ${t.toString}")
//          }
          case Func(ty, List()) => mkType(sig(ty).key)
          case DefinedFunc(ty, List()) if ty == "$tType" =>  typeKind // kind *
          case DefinedFunc(ty, List()) =>  mkType(sig(ty).key) // defined type
          case SystemFunc(ty, List()) =>  mkType(sig(ty).key) // system type
          case Var(name) =>  mkVarType(typeMapping(replaces).size + typeOffset(replaces) - typeMapping(replaces)(name)._2 + 1)
          case _ => throw new IllegalArgumentException("malformed/unsupported term type expression: "+typ.toString)
        }
      }
      case BinType(binTy) => {
        import leo.datastructures.tptp.thf.{->, *, +}
        binTy match {
          case ->(tys) => {
            val converted = tys.map(convertTHFType(sig)(_, replaces))
            //require(converted.forall(_.isLeft) || converted.forall(_.isRight), "Function constructor only applicable on types at the moment")
            if (converted.forall(_.isLeft)) {
              // Function type
              mkFunType(converted.map(_.left.get))
            } else if (converted.forall(_.isRight)) {
              // Function kind
              Kind.mkFunKind(converted.map(_.right.get))
            } else {
              throw new IllegalArgumentException(s"mixed types and kinds in function constructor: ${converted.map(_.fold(_.pretty, _.pretty)).mkString(" > ")}")
            }
          }
          case *(tys) => {
            val converted = tys.map(convertTHFType(sig)(_, replaces))
            // TODO: we consider only types here, is this correct?
            require(converted.forall(_.isLeft), "Sum constructor only allowed on types")
            mkProdType(converted.map(_.left.get))
          }
          case +(tys) => {
            val converted = tys.map(convertTHFType(sig)(_, replaces))
            // TODO: we consider only types here, is this correct?
            require(converted.forall(_.isLeft), "Union constructor only allowed on types")
            mkUnionType(converted.map(_.left.get))
          }
        }
      } //arrow type etc
      case Binary(l, App, r) => {
        val leftTy = convertTHFType(sig)(l, replaces)
        val rightty = convertTHFType(sig)(r, replaces)
        if(leftTy.isLeft && rightty.isLeft) {
          leftTy.left.get.app(rightty.left.get)
        } else throw new SZSException(SZS_InputError)
      }
      case _ => throw new IllegalArgumentException("malformed type expression: "+typ.toString)
    }
  }

  //////////////////////////
  // TFF Formula processing
  //////////////////////////

  def processTFF(sig: Signature)(input: TFFAnnotated): Option[Result] = {
    import leo.datastructures.tptp.tff.{Logical, TypedAtom, Sequent}

    input.formula match {
      // Logical formulae can either be terms (axioms, conjecture, ...) or definitions.
      case Logical(lf) if input.role == "definition" => processTFFDef(sig)(lf) match {
        case None => Out.info(s"No direction of definition ${input.name} detected. Treating as axiom.");
//                     val role = processRole("axiom"); Some((input.name, singleTermToClause(processTFF0(sig)(lf, noRep),role), role))
val role = processRole("axiom"); Some((input.name, processTFF0(sig)(lf, noRep), role))
        case Some((defName, defDef)) => sig.addDefined(defName, defDef, defDef.ty)
                                        None
      }
//      case Logical(lf) => val role = processRole(input.role); Some((input.name, singleTermToClause(processTFF0(sig)(lf, noRep),role), role))
      case Logical(lf) => val role = processRole(input.role); Some((input.name, processTFF0(sig)(lf, noRep), role))
      // Typed Atoms are top-level declarations, put them into signature
      case TypedAtom(atom, ty) => {
        convertTFFType(sig)(ty, noRep) match {
          case Left(ty) => sig.addUninterpreted(atom, ty)
          case Right(k) => sig.addTypeConstructor(atom, k) // TODO: constructors get own method and symbol type!
        }
        None
      }
      // Sequents
      case Sequent(_, _) => throw new IllegalArgumentException("Processing of TFF sequents not yet implemented")
    }


  }

  import leo.datastructures.tptp.tff.{LogicFormula => TFFLogicFormula}
  // Formula definitions
  protected[parsers] def processTFFDef(sig: Signature)(input: TFFLogicFormula): Option[(String, Term)] = {
    import leo.datastructures.tptp.tff.Atomic
    input match {
      case Atomic(Equality(Func(name, Nil),right)) => Some(name, processTerm(sig)(right, noRep, false))  // TODO Is this the right term to construct equalities in tff?
      case _ => None
    }
  }

  // Ordinary terms
  protected[parsers] def processTFF0(sig: Signature)(input: TFFLogicFormula, replaces: Replaces): Term = {
    import leo.datastructures.tptp.tff.{Binary, Quantified, Unary, Inequality, Atomic, Cond, Let}
    input match {
      case Binary(left, conn, right) => processTFFBinaryConn(conn).apply(processTFF0(sig)(left,replaces),processTFF0(sig)(right,replaces))
      case Quantified(q, vars, matrix) => {
        val quantifier = processTFFUnary(q)
        var newReplaces = replaces

        // Fold through the variables to propagate bindings trough variable list
        // and save bindings to `newReplaces` for body conversion
        val processedVars = vars.map(_ match {
          case (name, None) => {
            termMapping(newReplaces).get(name) match {
              case None => newReplaces = ((termMapping(newReplaces).+((name,(i, termMapping(newReplaces).size+1))),termOffset(newReplaces)),newReplaces._2)
              case _ =>  newReplaces = ((termMapping(newReplaces).+((name,(i, termMapping(newReplaces).size+1))),termOffset(newReplaces)+1),newReplaces._2)
            }
            (name, Left(i))
          }
          case (name, Some(ty)) => convertTFFType(sig)(ty, newReplaces) match {
            case Left(t) => {
              termMapping(newReplaces).get(name) match {
                case None => newReplaces = ((termMapping(newReplaces).+((name,(t, termMapping(newReplaces).size+1))),termOffset(newReplaces)),newReplaces._2)
                case _ =>  newReplaces = ((termMapping(newReplaces).+((name,(t, termMapping(newReplaces).size+1))),termOffset(newReplaces)+1),newReplaces._2)
              }
              (name, Left(t))
            }
            case Right(k) => {
              typeMapping(newReplaces).get(name) match {
                case None => newReplaces = (newReplaces._1,(typeMapping(newReplaces).+((name,(k, typeMapping(newReplaces).size+1))),typeOffset(newReplaces)))
                case _ =>  newReplaces = (newReplaces._1,(typeMapping(newReplaces).+((name,(k, typeMapping(newReplaces).size+1))),typeOffset(newReplaces) +1))
              }
              (name, Right(k))
            }
          }

        })
        mkPolyQuantified(quantifier, processedVars, processTFF0(sig)(matrix, newReplaces))
      }
      case Unary(conn, formula) => processTFFUnary(conn).apply(processTFF0(sig)(formula,replaces))
      case Inequality(left, right) => {
        val (l,r) = (processTerm(sig)(left, replaces, false),processTerm(sig)(right, replaces, false))
        import leo.modules.HOLSignature.{Not, ===}
        Not(===(l,r))
      }
      case Atomic(atomic) => processAtomicFormula(sig)(atomic, replaces, false)
      case Cond(cond, thn, els) => {
        IF_THEN_ELSE(processTFF0(sig)(cond, replaces),processTFF0(sig)(thn, replaces),processTFF0(sig)(els, replaces))
      }
      case Let(binding, in) =>  Out.warn("Unsupported let-definition in term, treated as $true."); LitTrue()
    }
  }

  import leo.datastructures.tptp.tff.{BinaryConnective => TFFBinaryConnective}
  protected[parsers] def processTFFBinaryConn(conn: TFFBinaryConnective): HOLBinaryConnective = {
    import leo.datastructures.tptp.tff.{<=> => TFFEquiv, Impl => TFFImpl, <= => TFFIf, | => TFFOr, & => TFFAnd, ~| => TFFNor, ~& => TFFNand, <~> => TFFNiff}
    import leo.modules.HOLSignature.{<=> => equiv, Impl => impl, <= => i_f, ||| => or, & => and, ~||| => nor, ~& => nand, <~> => niff}

    conn match {
      case TFFEquiv => equiv
      case TFFImpl  => impl
      case TFFIf    => i_f
      case TFFOr    => or
      case TFFAnd   => and
      case TFFNor   => nor
      case TFFNand  => nand
      case TFFNiff  => niff
    }
  }

  import leo.datastructures.tptp.tff.{UnaryConnective => TFFUnaryConnective}
  protected[parsers] def processTFFUnary(conn: TFFUnaryConnective): HOLUnaryConnective = {
    import leo.datastructures.tptp.tff.{Not => TFFNot}
    import leo.modules.HOLSignature.{Not => not}

    conn match {
      case TFFNot => not
    }
  }

  import leo.datastructures.tptp.tff.{Quantifier => TFFQuantifier}
  protected[parsers] def processTFFUnary(conn: TFFQuantifier): HOLUnaryConnective = {
    import leo.datastructures.tptp.tff.{! => TFFAll, ? => TFFAny}
    import leo.modules.HOLSignature.{Forall => forall, Exists => exists}

    conn match {
      case TFFAll => forall
      case TFFAny => exists
    }
  }

  // Type processing
  import leo.datastructures.tptp.tff.{Type => TFFType}
  type TFFBoundTyReplaces = Seq[Variable]
  protected[parsers] def convertTFFType(sig: Signature)(tffType: TFFType, replace: Replaces): Either[Type,Kind] = {
    import leo.datastructures.tptp.tff.{AtomicType,->,*,QuantifiedType}
    tffType match {
      // "AtomicType" constructs: Type variables, Base types, type kinds, or type/kind applications
      case AtomicType(ty, List()) if ty.charAt(0).isUpper => mkVarType(typeMapping(replace).size + typeOffset(replace) - typeMapping(replace)(ty)._2 + 1)  // Type Variable
      case AtomicType(ty, List()) if ty == "$tType" => typeKind // kind *
      case AtomicType(ty, List())  => mkType(sig.meta(ty).key)  // Base type
      case AtomicType(_, _) => throw new IllegalArgumentException("Processing of applied types not implemented yet") // TODO
      // Function type / kind
      case ->(tys) => { // Tricky here: It might be functions of "sort" * -> [], * -> *, [] -> [], [] -> *
                        // We only plan to support variant 1 (polymorphism),2 (constructors), 3 (ordinary functions) in a medium time range (4 is dependent type)
                        // Case 1 is captured by 'case QuantifiedType' due to TFF1's syntax
                        // So, only consider case 3 for now, but keep case 2 in mind
        val convertedTys = tys.map(convertTFFType(sig)(_, replace))
        require(convertedTys.forall(_.isLeft), "Constructors are not yet supported, but kind found inside a function: " +tffType.toString) // TODO
        mkFunType(convertedTys.map(_.left.get)) // since we only want case 3
      }
      // Product type / kind
      case *(tys) => {
        val converted = tys.map(convertTFFType(sig)(_, replace))
        // TODO: we consider only types here, is this correct?
        require(converted.forall(_.isLeft), "Sum constructor only allowed on types")
        mkProdType(converted.map(_.left.get))
      }
      // Quantified type
      case QuantifiedType(vars, body) => {
        val processedVars = vars.map(_ match {
          case (name, None) => (name, Right(typeKind)) // * is assumed when no type is given
          case (name, Some(ty)) => (name, convertTFFType(sig)(ty, replace))
        })
        require(processedVars.forall(_._2.isRight), "Only '$tType' as type assertion is allowed for type variables in quantified types")
        val newReplaces = processedVars.foldLeft(replace)({case (repl,vari) => vari match {
          case (name, Left(ty)) => termMapping(repl).get(name) match {
            case None => ((termMapping(repl).+((name,(ty, termMapping(repl).size+1))),termOffset(repl)),repl._2)
            case _ =>  ((termMapping(repl).+((name,(ty, termMapping(repl).size+1))),termOffset(repl)+1),repl._2)
          }
          case (name, Right(k)) => typeMapping(repl).get(name) match {
            case None => (repl._1,(typeMapping(repl).+((name,(k, typeMapping(repl).size+1))),typeOffset(repl)))
            case _ =>  (repl._1,(typeMapping(repl).+((name,(k, typeMapping(repl).size+1))),typeOffset(repl)+1))
          }
        }})
        processedVars.foldRight(convertTFFType(sig)(body,newReplaces).left.get)({case (_,b) => ∀(b)}) // NOTE: this is only allowed on top-level
        // the body of quantification must be a type.
        // TODO: better error treating
      }
    }
  }

  implicit def kindToTypeOrKind(k: Kind): Either[Type, Kind] = Right(k)
  implicit def typeToTypeOrKind(ty: Type): Either[Type, Kind] = Left(ty)
  implicit def termToTermOrType(t: Term): TermOrType = Left(t)
  implicit def typeToTermOrType(ty: Type): TermOrType = Right(ty)

  //////////////////////////
  // FOF Formula processing
  //////////////////////////

  def processFOF(sig: Signature)(input: FOFAnnotated): Option[Result] = {
    import leo.datastructures.tptp.fof.{Logical, Sequent}
    input.formula match {
//      case Logical(lf) if input.role == "definition" => {  // TODO: Commented out -- how do definitions look like in FOF? See COM021+1.p, RNG126+1.p
//                                                          val (defName, defDef) = processFOFDef(sig)(lf)
//                                                          sig.addDefined(defName, defDef, defDef.ty)
//                                                          None
//                                                        }
//      case Logical(lf) => val role = processRole(input.role); Some((input.name, singleTermToClause(processFOF0(sig)(lf, noRep), role), role))
      case Logical(lf) => val role = processRole(input.role); Some((input.name, processFOF0(sig)(lf, noRep), role))
      case Sequent(_,_) => throw new IllegalArgumentException("Processing of fof sequents not yet implemented.")
    }
  }

  import leo.datastructures.tptp.fof.{LogicFormula => FOFLogicalFormula}
  protected[parsers] def processFOFDef(sig: Signature)(input: FOFLogicalFormula): (String, Term) = {
    import leo.datastructures.tptp.fof.Atomic
    input match {
      case Atomic(Equality(Func(name, Nil),right)) => (name, processTerm(sig)(right, noRep))  // TODO See above TODO
      case _ => throw new IllegalArgumentException("Malformed definition")
    }
  }

  protected[parsers] def processFOF0(sig: Signature)(input: FOFLogicalFormula, replaces: Replaces): Term = {
    import leo.datastructures.tptp.fof.{Binary, Unary, Quantified, Atomic, Inequality}
    input match {
      case Binary(left, conn, right) => processFOFBinaryConn(conn).apply(processFOF0(sig)(left, replaces),processFOF0(sig)(right, replaces))
      case Unary(conn, f) => processFOFUnary(conn).apply(processFOF0(sig)(f, replaces))
      case Quantified(q, varList, matrix) => {
        val quantifier = processFOFUnary(q)
        val processedVars = varList.map((_, i))
        val newReplaces = processedVars.foldLeft(replaces)({case (repl,vari) => vari match {
          case (name, ty) => termMapping(repl).get(name) match {
            case None => ((termMapping(repl).+((name,(ty, termMapping(repl).size+1))),termOffset(repl)),repl._2)
            case _ =>  ((termMapping(repl).+((name,(ty, termMapping(repl).size+1))),termOffset(repl)+1),repl._2)
          }
        }})
        mkPolyQuantifiedFOF(quantifier, processedVars, processFOF0(sig)(matrix, newReplaces))
      }
      case Atomic(atomic) => processAtomicFormula(sig)(atomic, replaces)
      case Inequality(left,right) => {
        val (l,r) = (processTermArgs(sig)(left, replaces),processTermArgs(sig)(right, replaces))
        leo.modules.HOLSignature.!===(l,r)
      }
    }
  }

  import leo.datastructures.tptp.fof.{BinaryConnective => FOFBinaryConnective}
  protected[parsers] def processFOFBinaryConn(conn: FOFBinaryConnective): HOLBinaryConnective = {
    import leo.datastructures.tptp.fof.{<=> => FOFEquiv, Impl => FOFImpl, <= => FOFIf, | => FOFOr, & => FOFAnd, ~| => FOFNor, ~& => FOFNand, <~> => FOFNiff}
    import leo.modules.HOLSignature.{<=> => equiv, Impl => impl, <= => i_f, ||| => or, & => and, ~||| => nor, ~& => nand, <~> => niff}

    conn match {
      case FOFEquiv => equiv
      case FOFImpl  => impl
      case FOFIf    => i_f
      case FOFOr    => or
      case FOFAnd   => and
      case FOFNor   => nor
      case FOFNand  => nand
      case FOFNiff  => niff
    }
  }

  import leo.datastructures.tptp.fof.{UnaryConnective => FOFUnaryConnective}
  protected[parsers] def processFOFUnary(conn: FOFUnaryConnective): HOLUnaryConnective = {
    import leo.datastructures.tptp.fof.{Not => FOFNot}
    import leo.modules.HOLSignature.{Not => not}

    conn match {
      case FOFNot => not
    }
  }

  import leo.datastructures.tptp.fof.{Quantifier => FOFQuantifier}
  protected[parsers] def processFOFUnary(conn: FOFQuantifier): HOLUnaryConnective = {
    import leo.datastructures.tptp.fof.{! => FOFAll, ? => FOFAny}
    import leo.modules.HOLSignature.{Forall => forall, Exists => exists}

    conn match {
      case FOFAll => forall
      case FOFAny => exists
    }
  }


  //////////////////////////
  // CNF Formula processing
  //////////////////////////

  import leo.datastructures.tptp.cnf.{ Formula => CNFLogicalFormula}
  def processCNF(sig: Signature)(input: CNFAnnotated): Option[Result] = {
    val role = processRole(input.role)
    ???
//    Some((input.name, processCNF0(sig)(input.formula, roleToClauseOrigin(role)), role))
  }

  protected[parsers] def processCNF0(sig: Signature)(input: CNFLogicalFormula, origin: ClauseOrigin): Clause = {
    import leo.datastructures.tptp.cnf.{Positive, Negative, Inequality}
//    val lits = input.literals.map { _ match {
//      case Positive(f) => mkPosLit(processAtomicFormula(sig)(f, ???))
//      case Negative(f) => mkNegLit(processAtomicFormula(sig)(f, ???))
//      case Inequality(l, r) => mkUniLit(processTerm(sig)(l, ???), processTerm(sig)(r, ???))
//    }
//    }
//    import leo.datastructures.Clause.{mkClause}
//    mkClause(lits, origin)
    ???
  }


  ////////////////////////////
  // Common 'term' processing
  ////////////////////////////
  import leo.datastructures.tptp.Commons.{NumberTerm, RationalNumber, IntegerNumber, DoubleNumber}
  import leo.datastructures.tptp.Commons.{Distinct, Cond, Let, Plain, DefinedPlain, SystemPlain, AtomicFormula}

  def processTermArgs(sig: Signature)(input: TPTPTerm, replace: Replaces, adHocDefs: Boolean = true): Term = input match {
    case Func(name, vars) => {
      val converted = vars.map(processTermArgs(sig)(_, replace, adHocDefs))
      if (sig.exists(name) || !adHocDefs) {
        mkTermApp(mkAtom(sig(name).key)(sig), converted)
      } else {
        mkTermApp(mkAtom(sig.addUninterpreted(name, mkFunType(vars.map(_ => i), i)))(sig), converted)
      }
    }
    case other => processTerm(sig)(other, replace, adHocDefs)
  }

  def processTerm(sig: Signature)(input: TPTPTerm, replace: Replaces, adHocDefs: Boolean = true): Term = input match {
    case Func(name, vars) => {
      val converted = vars.map(processTermArgs(sig)(_, replace, adHocDefs))
      if (sig.exists(name) || !adHocDefs) {
        mkTermApp(mkAtom(sig(name).key)(sig), converted)
      } else {
        mkTermApp(mkAtom(sig.addUninterpreted(name, mkFunType(vars.map(_ => i), o)))(sig), converted)
      }

    }
    case DefinedFunc(name, vars) => {
      import leo.modules.HOLSignature.{HOLUnaryMinus, HOLFloor, HOLCeiling, HOLTruncate, HOLRound, HOLToInt, HOLToRat, HOLToReal}
      import leo.modules.HOLSignature.{HOLIsRat, HOLIsInt, HOLLess, HOLLessEq, HOLGreater, HOLGreaterEq, HOLSum, HOLDifference}
      import leo.modules.HOLSignature.{HOLProduct, HOLQuotient, HOLQuotientE, HOLQuotientF, HOLQuotientT, HOLRemainderE}
      import leo.modules.HOLSignature.{HOLRemainderF, HOLRemainderT}
      if (sig(name)._ty.isPolyType) {
        val converted = vars.map(processTerm(sig)(_, replace, adHocDefs))
        // converted only contains terms
        // currently, there are only unary and binary TPTP defined functions (Dollarwords) that are polymorphic (all arithmetic related)
        if (converted.size == 1) {
          val func = name match {
            case "$uminus" => HOLUnaryMinus
            case "$floor" => HOLFloor
            case "$ceiling" => HOLCeiling
            case "$truncate" => HOLTruncate
            case "$round" => HOLRound
            case "$to_int" => HOLToInt
            case "$to_rat" => HOLToRat
            case "$to_real" => HOLToReal
            case "$is_rat" => HOLIsRat
            case "$is_int" => HOLIsInt
            case _ => Out.severe("A problem used an unknown polymorphic TPTP-defined function (Dollarword) with arity = 1");
              throw new SZSException(SZS_InputError)
          }
          func.apply(converted(0))
        } else if (converted.size == 2) {
          val func = name match {
            case "$less" => HOLLess
            case "$lesseq" => HOLLessEq
            case "$greater" => HOLGreater
            case "$greatereq" => HOLGreaterEq
            case "$sum" => HOLSum
            case "$difference" => HOLDifference
            case "$product" => HOLProduct
            case "$quotient" => HOLQuotient
            case "$quotient_e" => HOLQuotientE
            case "$quotient_t" => HOLQuotientT
            case "$quotient_f" => HOLQuotientF
            case "$remainder_e" => HOLRemainderE
            case "$remainder_t" => HOLRemainderT
            case "$remainder_f" => HOLRemainderF
            case _ => Out.severe("A problem used an unknown polymorphic TPTP-defined function (Dollarword) with arity = 2");
              throw new SZSException(SZS_InputError)
          }
          func.apply(converted(0), converted(1))
        } else {
          // This should not happen
          Out.severe("A problem used an unknown polymorphic TPTP-defined function (Dollarword) with arity > 2")
          throw new SZSException(SZS_InputError)
        }

      } else {
        val converted = vars.map(processTerm(sig)(_, replace, adHocDefs))
        mkTermApp(mkAtom(sig(name).key)(sig), converted)
      }
    }
    case SystemFunc(name, vars) => {
      val converted = vars.map(processTerm(sig)(_, replace, adHocDefs))
      mkTermApp(mkAtom(sig(name).key)(sig), converted)
    }
    case Var(name) => termMapping(replace).get(name) match {
      case None => typeMapping(replace).get(name) match {
        case Some((k, scope))  => ???
        case _ => throw new IllegalArgumentException("Unbound variable found in formula: "+input.toString)
      }
      case Some((ty, scope)) => {
        assert(typeMapping(replace).get(name).isEmpty)
        mkBound(ty, termMapping(replace).size + termOffset(replace) - scope +1)
      }
    }

    case NumberTerm(value) => value match {
      case IntegerNumber(value) => {
        val constName = "$$int(" + value.toString + ")"
        if (sig.exists(constName)) {
          mkAtom(sig(constName).key)(sig)
        } else {
          // Note: that this is a hack, we use the fact that untyped
          // languages use ad-hoc definitions of symbols
          // Here, we must use type $i for numbers of all kinds
          if (adHocDefs) {
            mkAtom(sig.addUninterpreted(constName, i))(sig)
          } else {
            mkAtom(sig.addUninterpreted(constName, int))(sig)
          }

        }
      }
      case DoubleNumber(value) => {
        val constName = "$$real(" + value.toString + ")"
        if (sig.exists(constName)) {
          mkAtom(sig(constName).key)(sig)
        } else {
          // See note above
          if (adHocDefs) {
            mkAtom(sig.addUninterpreted(constName, i))(sig)
          } else {
            mkAtom(sig.addUninterpreted(constName, real))(sig)
          }
        }
      }
      case RationalNumber(p,q) =>  {
        val constName = "$$rational(" + p.toString + "/" + q.toString +")"
        if (sig.exists(constName)) {
          mkAtom(sig(constName).key)(sig)
        } else {
          // See note above
          if (adHocDefs) {
            mkAtom(sig.addUninterpreted(constName, i))(sig)
          } else {
            mkAtom(sig.addUninterpreted(constName, rat))(sig)
          }
        }
      }
    }
    case Distinct(data) => // NOTE: Side-effects may occur if this is the first occurence of '"data"'
                            if (sig.exists("\""+data+"\"")) {
                              mkAtom(sig.apply("\""+data+"\"").key)(sig)
                            } else {
                              mkAtom(sig.addUninterpreted("\""+data+"\"", i))(sig)
                            }
    case Cond(cond, thn, els) => {
      IF_THEN_ELSE(processTFF0(sig)(cond, replace),processTerm(sig)(thn, replace, adHocDefs),processTerm(sig)(els, replace, adHocDefs))
    }
    case Let(binding, in) =>  Out.warn("Unsupported let-definition in term, treated as $true."); LitTrue()
  }

  def processAtomicFormula(sig: Signature)(input: AtomicFormula, replace: Replaces, adHocDefs: Boolean = true): Term = input match {
    case Plain(func) => processTerm(sig)(func, replace,adHocDefs)
    case DefinedPlain(func) => processTerm(sig)(func, replace, adHocDefs)
    case SystemPlain(func) => processTerm(sig)(func, replace, adHocDefs)
    case Equality(left,right) => {
      import leo.modules.HOLSignature.===
      ===(processTermArgs(sig)(left, replace, adHocDefs),processTermArgs(sig)(right, replace, adHocDefs))
    }
  }

  ///////////
  // Utility
  ///////////

  protected[parsers] def mkPolyQuantified(q: HOLUnaryConnective, varList: Seq[ProcessedVar], body: Term): Term = {
    def mkPolyHelper(a: ProcessedVar, b: Term): Term = a match {
      case (_, Left(ty)) => q.apply(λ(ty)(b))
      case (_, Right(`typeKind`)) => Λ(b)
      case (_, Right(_))        => throw new IllegalArgumentException("Formalization of kinds other than * not yet implemented.")
    }

    varList.foldRight(body)(mkPolyHelper)
  }

  protected[parsers] def mkPolyQuantifiedFOF(q: HOLUnaryConnective, varList: Seq[(Variable, Type)], body: Term): Term = {
    varList.foldRight(body)({case ((_, ty), term) => q.apply(λ(ty)(term))})
  }

  protected[parsers] def mkITE(sig: Signature)(cond: Term, thn: Term, els: Term): Term = {
      IF_THEN_ELSE(cond,thn,els)
  }
}

