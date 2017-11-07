package leo.modules.parsers

import scala.language.implicitConversions
import leo.Out
import leo.datastructures.tptp.Commons.{AnnotatedFormula, Variable, Term => TPTPTerm}
import leo.datastructures.tptp.Commons.{CNFAnnotated, FOFAnnotated, TFFAnnotated, THFAnnotated, TPIAnnotated}
import leo.datastructures.tptp.Commons.{DefinedFunc, Equality, Func, SystemFunc, Var}
import leo.datastructures._
import Term.{mkApp, mkAtom, mkBound, mkTermApp, Λ, λ}
import Type.{mkFunType, mkProdType, mkType, mkUnionType, mkVarType, typeKind, ∀}
import leo.modules.SZSException
import leo.modules.output.{SZS_Inappropriate, SZS_InputError, SZS_TypeError}

/**
  * Processing module from TPTP input.
  * Declarations are inserted into the given Signature,
  * terms are returned in internal term representation.
  *
  * @author Alexander Steen
  * @since 18.06.2014
  */
object InputProcessing {
  import leo.modules.HOLSignature.{i,o, rat, int, real, LitTrue, IF_THEN_ELSE, HOLUnaryConnective, HOLBinaryConnective}

  // (Formula name, Term, Formula Role)
  type Result = (String, Term, Role)

  // Types for replacing bound variables by de bruijn indices
  type TermVarReplaces = (Map[Variable, (Type, Int)], Int) // (varname -> (vartype, position in list), index offset)
  type TypeVarReplaces = (Map[Variable, (Kind, Int)], Int)
  type Replaces = (TermVarReplaces, TypeVarReplaces)
  def termMapping(r: Replaces) = r._1._1
  def typeMapping(r: Replaces) = r._2._1
  def termOffset(r: Replaces) = r._1._2
  def typeOffset(r: Replaces) = r._2._2
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
  protected[parsers] final def processAll(sig: Signature)(input: Seq[AnnotatedFormula]): Seq[Result] = {
    input.map(process(sig)(_))
  }

  @inline private final def processRole(role: String): Role = Role(role)

  protected[parsers] final def process(sig: Signature)(input: AnnotatedFormula): Result = {
    val p = input match {
      case _:TPIAnnotated => processTPI(sig)(input.asInstanceOf[TPIAnnotated])
      case _:THFAnnotated => processTHFAnnotated(sig)(input.asInstanceOf[THFAnnotated])
      case _:TFFAnnotated => processTFF(sig)(input.asInstanceOf[TFFAnnotated])
      case _:FOFAnnotated => processFOF(sig)(input.asInstanceOf[FOFAnnotated])
      case _:CNFAnnotated => processCNF(sig)(input.asInstanceOf[CNFAnnotated])
    }
    p match {
      case None => val role = processRole(input.role); (input.name, LitTrue, role)
      case Some(res) => res
    }
  }


  //////////////////////////
  // TPI Formula processing
  //////////////////////////

  protected[parsers] final def processTPI(sig: Signature)(input: TPIAnnotated): Option[Result] = throw new SZSException(SZS_Inappropriate, "TPI format not supported.")

  //////////////////////////
  // THF Formula processing
  //////////////////////////

  protected[parsers] final def processTHFAnnotated(sig: Signature)(input: THFAnnotated): Option[Result] = {
    import leo.datastructures.tptp.thf.{Sequent, Logical, Typed, Function}

    input.formula match {
      case Logical(lf) if input.role == "definition" =>
        processTHFDef(sig)(lf) match {
          case None =>
            Out.info(s"No direction of definition ${input.name} detected. Treating as axiom.")
            val role = processRole("axiom")
            val res = processTHF(sig)(lf, noRep)
            if (res.isLeft)
              Some((input.name, res.left.get, role))
            else
              throw new SZSException(SZS_InputError)
          case Some((defName, defDef)) =>
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
            } else sig.addDefined(defName, defDef.betaNormalize.etaExpand, defDef.ty)
            None
        }
      case Logical(Typed(Function(atom, _),ty)) if input.role == "type" =>
        convertTHFType(sig)(ty, noRep) match {
          case Left(ty0) => sig.addUninterpreted(atom, ty0)
          case Right(k) => sig.addTypeConstructor(atom, k)
        }
        None
      case Logical(lf)                               => val role = processRole(input.role)
                                                        val res = processTHF(sig)(lf)
                                                        if (res.isLeft)
                                                          Some((input.name, res.left.get, role))
                                                        else
                                                          throw new SZSException(SZS_InputError)
      case Sequent(_,_)                              => throw new IllegalArgumentException("Processing of THF sequents not implemented")
    }
  }

  import leo.datastructures.tptp.thf.{LogicFormula => THFLogicFormula}
  protected[InputProcessing] final def processTHFDef(sig: Signature)(input: THFLogicFormula): Option[(String, Term)] = {
    import leo.datastructures.tptp.thf.{Binary, Eq, Function}
    input match {
      case Binary(Function(name, Seq()), Eq, right) =>
        val res = processTHF(sig)(right, noRep)
        if (res.isLeft) Some(name, res.left.get)
        else throw new SZSException(SZS_InputError, "Type detected on right side of definition statement.")
      case _                                        => None
    }
  }

  protected[modules] final def processTHF(sig: Signature)(input: THFLogicFormula, replaces: Replaces = noRep): TermOrType = {
    import leo.datastructures.tptp.thf.{Typed, Binary, Unary, Tuple, Number => THFNumber, Distinct, Function, Var => THFVar, Quantified, Connective, BinType, Subtype, Cond, NewLet, App => THFApp}

    input match {
      case Typed(f, _) => processTHF(sig)(f,replaces) // Type information was already extracted before
      case Binary(left, conn, right) if conn == THFApp =>
        val processedLeft = processTHF(sig)(left, replaces)
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
            val res = processTHF(sig)(right, replaces)
            if (res.isLeft)
              mkTermApp(processedLeft2, res.left.get)
            else
              mkTypeApp(processedLeft2, res.right.get)
              //throw new SZSException(SZS_InputError, "type on non-poly left side found.")
          }
        } else throw new SZSException(SZS_InputError, "THFAPP left side type.")
      case Binary(left, conn, right) => //try {
        val processedLeft = processTHF(sig)(left, replaces)
        val processedRight = processTHF(sig)(right, replaces)
        processTHFBinaryConn(conn).apply(processedLeft.left.get, processedRight.left.get)
//        } catch {
//          case e:java.util.NoSuchElementException => throw new SZSException(SZS_InputError, e.toString, s"Inout: ${input.toString}\n" +
//            s"Left:${left.toString}\nRight:${right.toString}\nConn:${conn.toString}\n") //+
//            //s"Transformed left: ${processTHF(sig)(left, replaces).toString}\n" +
//            //s"Transformed right: ${processTHF(sig)(right, replaces).toString}")
//        }
      case Unary(conn, f) => try {
        processTHFUnaryConn(conn).apply(processTHF(sig)(f, replaces).left.get)
        } catch {
        case e:java.util.NoSuchElementException => throw new SZSException(SZS_InputError, e.toString)
        }
      case Quantified(q, vars, matrix) =>
        val quantifier = processTHFUnaryConn(q)
        var newReplaces = replaces

        // Fold through the variables to propagate bindings trough variable list
        // and save bindings to `newReplaces` for body conversion
        val processedVars = vars.map{
          case (name, None) =>
            termMapping(newReplaces).get(name) match {
              case None => newReplaces = ((termMapping(newReplaces).+((name, (i, termMapping(newReplaces).size + 1 + termOffset(newReplaces)))), termOffset(newReplaces)), newReplaces._2)
              case _ => newReplaces = ((termMapping(newReplaces).+((name, (i, termMapping(newReplaces).size + 1 + termOffset(newReplaces)))), termOffset(newReplaces) + 1), newReplaces._2)
            }
            (name, Left(i))
          case (name, Some(ty)) => convertTHFType(sig)(ty, newReplaces) match {
            case Left(t) =>
              termMapping(newReplaces).get(name) match {
                case None => newReplaces = ((termMapping(newReplaces).+((name, (t, termMapping(newReplaces).size + 1 + termOffset(newReplaces)))), termOffset(newReplaces)), newReplaces._2)
                case _ => newReplaces = ((termMapping(newReplaces).+((name, (t, termMapping(newReplaces).size + 1 + termOffset(newReplaces)))), termOffset(newReplaces) + 1), newReplaces._2)
              }
              (name, Left(t))
            case Right(k) =>
              typeMapping(newReplaces).get(name) match {
                case None => newReplaces = (newReplaces._1, (typeMapping(newReplaces).+((name, (k, typeMapping(newReplaces).size + 1 + typeOffset(newReplaces)))), typeOffset(newReplaces)))
                case _ => newReplaces = (newReplaces._1, (typeMapping(newReplaces).+((name, (k, typeMapping(newReplaces).size + 1 + typeOffset(newReplaces)))), typeOffset(newReplaces) + 1))
              }
              (name, Right(k))
          }
        }
        val intermediateRes = processTHF(sig)(matrix, newReplaces)
        if (intermediateRes.isLeft)
          mkPolyQuantified(quantifier, processedVars, intermediateRes.left.get)
        else
          throw new SZSException(SZS_InputError)
      case Connective(c) => c.fold(cc => Left(processTHFBinaryConn(cc)),cc => Left(processTHFUnaryConn(cc)))
      case THFVar(name) => termMapping(replaces).get(name) match {
        case None => typeMapping(replaces).get(name) match {
          case Some((k, scope))  => assert(k.isTypeKind); Type.mkVarType(scope)
          case _ => throw new IllegalArgumentException("Unbound variable found in formula: "+input.toString)
        }
        case Some((ty, scope)) =>
          assert(typeMapping(replaces).get(name).isEmpty)
          mkBound(ty, termMapping(replaces).size + termOffset(replaces) - scope +1)
      }
      case Function(func, args) =>
        if (func.startsWith("$$")) {
          // system function
          if (sig.exists(func)) {
            val converted = args.map(processTHF(sig)(_, replaces))
            mkApp(mkAtom(sig(func).key)(sig), converted)
          } else throw new SZSException(SZS_InputError, s"System function $func is unknown.")
        } else if (func.startsWith("$")) {
          // Defined function
          if (!sig.exists(func)) throw new SZSException(SZS_Inappropriate, s"TPTP functor $func not supported.")
          else {
            val f = sig(func)
            if (f._ty.isPolyType) {
              leo.Out.finest(s"func: ${func.toString}, args: ${args.map(_.toString).mkString(",")}")
              val convertedArgs = args.map(processTHF(sig)(_, replaces))
              if (convertedArgs.nonEmpty)
                mkApp(mkAtom(f.key)(sig), Right(convertedArgs.head.left.get.ty) +: convertedArgs)
              else
                mkAtom(f.key)(sig)
            } else {
              val convertedArgs = args.map(processTHF(sig)(_, replaces))
              mkApp(mkAtom(f.key)(sig), convertedArgs)
            }
          }
        } else {
          // system or plain function
          if (sig.exists(func)) {
            val converted = args.map(processTHF(sig)(_, replaces))
            mkApp(mkAtom(sig(func).key)(sig), converted)
          } else throw new SZSException(SZS_InputError, s"Function $func is unknown, please specify its type first.")
        }
      case Distinct(data) => // NOTE: Side-effects may occur if this is the first occurence of '"data"'
        if (sig.exists("\""+data+"\"")) mkAtom(sig.apply("\""+data+"\"").key)(sig)
        else mkAtom(sig.addUninterpreted("\""+data+"\"", i))(sig)
      case THFNumber(n) =>
        import leo.datastructures.tptp.Commons.{IntegerNumber, DoubleNumber, RationalNumber}
        n match {
        case IntegerNumber(value) =>
          val constName = s"$$$$int(${value.toString})"
          if (sig.exists(constName)) mkAtom(sig(constName).key)(sig)
          else mkAtom(sig.addUninterpreted(constName, int))(sig)
        case DoubleNumber(value) =>
          val constName = s"$$$$real(${value.toString})"
          if (sig.exists(constName)) mkAtom(sig(constName).key)(sig)
          else mkAtom(sig.addUninterpreted(constName, real))(sig)
        case RationalNumber(p,q) =>
          val constName = s"$$$$rational(${p.toString}/${q.toString})"
          if (sig.exists(constName)) mkAtom(sig(constName).key)(sig)
          else mkAtom(sig.addUninterpreted(constName, rat))(sig)
      }
      case Tuple(_) => throw new SZSException(SZS_Inappropriate, "Tuples are not supported")
      case BinType(_) => throw new IllegalArgumentException("Binary Type formulae should not appear on top-level")
      case Subtype(_, _) => throw new SZSException(SZS_Inappropriate, "Subtyping is not supported")
      case Cond(c, thn, els) =>
        try {
          val convertedCondition = processTHF(sig)(c, replaces).left.get
          val convertedThen = processTHF(sig)(thn, replaces).left.get
          val convertedElse = processTHF(sig)(els, replaces).left.get
          import leo.modules.HOLSignature.o
          val conditionType = convertedCondition.ty
          val thenType = convertedThen.ty
          val elseType = convertedElse.ty
          if (thenType == elseType) {
            if (conditionType == o) {
//              IF_THEN_ELSE(convertedCondition, convertedThen, convertedElse)
              import leo.datastructures.Term.{λ, mkBound}
              import leo.modules.HOLSignature.{Choice, Impl, &, Not, ===}
              Choice(λ(thenType)(&(Impl(convertedCondition, ===(mkBound(thenType, 1), convertedThen)),
                Impl(Not(convertedCondition), ===(mkBound(thenType, 1), convertedElse)))))
            } else throw new SZSException(SZS_TypeError, "Condition in IF-THEN-ElSE is not Boolean typed.")
          } else throw new SZSException(SZS_TypeError, "THEN and ELSE case types do not match in IF-THEN-ELSE")
        } catch {

          case e:java.util.NoSuchElementException => throw new SZSException(SZS_InputError,e.toString)
        }
      case NewLet(binding, leo.datastructures.tptp.thf.Logical(body)) =>
        import leo.datastructures.tptp.thf.{Eq => THFEq}
        var localBindingMap: Map[Function, THFLogicFormula] = Map()
        binding.entries.foreach {
          case Binary(f@Function(_, Seq()), THFEq, right) =>
            localBindingMap = localBindingMap + (f -> right)
          case _ => throw new SZSException(SZS_InputError, s"Malformed let-expression in ${input.toString}")
        }
        processTHF(sig)(expandLetDefs(body, localBindingMap), replaces)
      case _ => throw new SZSException(SZS_InputError, s"Unrecognized input ${input.toString}")
    }
  }
  private final def expandLetDefs(t: THFLogicFormula, binding: Map[leo.datastructures.tptp.thf.Function, THFLogicFormula]): THFLogicFormula = {
    import leo.datastructures.tptp.thf.{Logical, Typed, Binary, Eq, *,+ => THFSum,->,Function, Unary, BinType, Quantified, Tuple, Cond, NewLet}
    t match {
      case f@Function(fname,fargs) => if (binding.isDefinedAt(f)) binding(f)
                                      else Function(fname, fargs.map(expandLetDefs(_, binding)))
        // All other just recurse
      case Typed(f,ty) => Typed(expandLetDefs(f, binding), expandLetDefs(ty, binding))
      case Binary(l,op,r) => Binary(expandLetDefs(l,binding), op, expandLetDefs(r,binding))
      case Unary(op, body) => Unary(op, expandLetDefs(body, binding))
      case Quantified(q,v,matrix) => Quantified(q,v,expandLetDefs(matrix, binding))
      case Tuple(entries) => Tuple(entries.map(expandLetDefs(_, binding)))
      case Cond(cond,thn,els) => Cond(expandLetDefs(cond,binding), expandLetDefs(thn, binding), expandLetDefs(els, binding))
      case NewLet(letBinding, Logical(body)) =>
        val newLetBinding = Tuple(letBinding.entries.map{ // See invariant in NewLet
          case Binary(f, Eq, right) => Binary(f, Eq, expandLetDefs(right, binding))
          case _ => throw new IllegalArgumentException
        })
        NewLet(newLetBinding, Logical(expandLetDefs(body, binding)))
      case BinType(ty) => ty match {
        case ->(args) => BinType(->(args.map(expandLetDefs(_, binding))))
        case THFSum(args) => BinType(THFSum(args.map(expandLetDefs(_, binding))))
        case *(args) => BinType(*(args.map(expandLetDefs(_, binding))))
      }
      case _ => t
    }
  }


  ////// Little workaround to have the usual application (s @ t) a corresponding HOLBinbaryConnective
  object @@@ extends HOLBinaryConnective {
    val key: Signature.Key = Integer.MIN_VALUE // Dont care, we dont want to use unapply
    val ty: Type = null
    override def apply(left: Term, right: Term): Term = Term.mkTermApp(left, right)
  }
  //////

  import leo.datastructures.tptp.thf.{BinaryConnective => THFBinaryConnective}
  protected[InputProcessing] final def processTHFBinaryConn(conn: THFBinaryConnective): HOLBinaryConnective = {
    import leo.datastructures.tptp.thf.{:= => THFAssign, Eq => THFEq, Neq => THFNeq, <=> => THFEquiv, Impl => THFImpl, <= => THFIf, <~> => THFNiff, ~| => THFNor, ~& => THFNand, | => THFOr, & => THFAnd, App => THFApp}
    import leo.modules.HOLSignature.{Impl => impl, <= => i_f, ||| => or, & => and, ~||| => nor, ~& => nand, ===, !===}

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
      case THFAssign => throw new NotImplementedError("Assignment operator not supported.")
    }
  }

  import leo.datastructures.tptp.thf.{UnaryConnective => THFUnaryConnective}
  protected[InputProcessing] final def processTHFUnaryConn(conn: THFUnaryConnective): HOLUnaryConnective = {
    import leo.datastructures.tptp.thf.{~ => THFNot, !! => THFAllComb,
    ?? => THFExistsComb, @@+ => THFChoiceComb, @@- => THFDescComb, @@= => THFEqComb}
    import leo.modules.HOLSignature.{Not => not, Forall => forall, Exists => exists, Choice => choice, Description => desc, === => eq}

    conn match {
      case THFNot => not
      case THFAllComb => forall
      case THFExistsComb => exists
      case THFChoiceComb => choice
      case THFDescComb => desc
      case THFEqComb => ???
    }
  }

  import leo.datastructures.tptp.thf.{Quantifier => THFQuantifier}
  protected[InputProcessing] final def processTHFUnaryConn(conn: THFQuantifier): HOLUnaryConnective = {
    import leo.datastructures.tptp.thf.{! => THFAll, ? => THFExists, ^ => THFLambda, @+ => THFChoice, @- => THFDesc}
    import leo.modules.HOLSignature.{Forall, Exists, Choice, Description}

    conn match {
      case THFAll => Forall
      case THFExists => Exists
      case THFLambda => HOLLambda
      case THFChoice => Choice
      case THFDesc => Description

      case _ => throw new IllegalArgumentException("Illegal quantifier symbol:" +conn.toString)
    }
  }
  private final val HOLLambda = new HOLUnaryConnective { // little hack here, to simulate a lambda, the apply function is the identity
  // this is because the mkPolyQuantified will apply a new abstraction
    val key: Signature.Key = Integer.MIN_VALUE // just for fun!
    lazy val ty: Type = null
    override def apply(arg: Term): Term = arg
  }

  protected[InputProcessing] final def convertTHFType(sig: Signature)(typ: THFLogicFormula, replaces: Replaces): TypeOrKind = {
    import leo.datastructures.tptp.thf.{Quantified, Var => THFVar, Function, BinType, Binary, App}

    typ match {
      case Quantified(q, vars, matrix) =>
        import leo.datastructures.tptp.thf.{!> => THFTyForAll, ?* => THFTyExists}

        q match {
          case THFTyForAll =>
            val processedVars = vars.map{
              case (name, None) => (name, Right(typeKind)) // * is assumed when no type is given
              case (name, Some(ty)) => (name, convertTHFType(sig)(ty, replaces))
            }
            require(processedVars.forall(_._2.isRight), "Only '$tType' as type assertion is allowed for type variables in quantified types")
            val newReplaces = processedVars.foldLeft(replaces)({case (repl,vari) => vari match {
              case (name, Left(ty)) =>
                termMapping(repl).get(name) match {
                  case None => ((termMapping(repl).+((name,(ty, termMapping(repl).size+1+termOffset(repl)))),termOffset(repl)),repl._2)
                  case _ =>  ((termMapping(repl).+((name,(ty, termMapping(repl).size+1+termOffset(repl)))),termOffset(repl)+1),repl._2)
                }
              case (name, Right(k)) =>
                typeMapping(repl).get(name) match {
                  case None => (repl._1,(typeMapping(repl).+((name,(k, typeMapping(repl).size+1+typeOffset(repl)))),typeOffset(repl)))
                  case _ =>  (repl._1,(typeMapping(repl).+((name,(k, typeMapping(repl).size+1+typeOffset(repl)))),typeOffset(repl)+1))
                }
            }})
            processedVars.foldRight(convertTHFType(sig)(matrix,newReplaces).left.get)({case (_,b) => ∀(b)}) // NOTE: this is only allowed on top-level
            // the body of quantification must be a type.
            // TODO: better error treating
          case THFTyExists => ???
          case _ => throw new SZSException(SZS_InputError, "Illegal quantifier on type level: " + typ.toString)
        }
      case THFVar(name) => mkVarType(typeMapping(replaces).size + typeOffset(replaces) - typeMapping(replaces)(name)._2 + 1)
      case Function(func, args) =>
        if (args.nonEmpty) throw new SZSException(SZS_TypeError, s"Malformed type expression: ${typ.toString}")
        else {
          if (func.startsWith("$$")) {
            if (sig.exists(func)) mkType(sig(func).key)
            else throw new SZSException(SZS_InputError, s"Unknown system type/type operator $func")
          } else if (func.startsWith("$")) {
            if (func == "$tType") typeKind
            else if (sig.exists(func)) mkType(sig(func).key)
            else throw new SZSException(SZS_Inappropriate, s"Unknown TPTP type/type operator $func")
          } else {
            if (sig.exists(func)) mkType(sig(func).key)
            else throw new SZSException(SZS_InputError, s"Unknown type/type operator $func, please specify its kind before.")
          }
        }
      case BinType(binTy) =>
        import leo.datastructures.tptp.thf.{->, *, +}
        binTy match {
          case ->(tys) =>
            val converted = tys.map(convertTHFType(sig)(_, replaces))
            if (converted.forall(_.isLeft)) {
              // Function type
              mkFunType(converted.map(_.left.get))
            } else if (converted.forall(_.isRight)) {
              // Function kind
              Kind.mkFunKind(converted.map(_.right.get))
            } else throw new IllegalArgumentException(s"mixed types and kinds in function constructor: ${converted.map(_.fold(_.pretty, _.pretty)).mkString(" > ")}")
          case *(tys) =>
            val converted = tys.map(convertTHFType(sig)(_, replaces))
            // TODO: we consider only types here, is this correct?
            require(converted.forall(_.isLeft), "Sum constructor only allowed on types")
            mkProdType(converted.map(_.left.get))
          case +(tys) =>
            val converted = tys.map(convertTHFType(sig)(_, replaces))
            // TODO: we consider only types here, is this correct?
            require(converted.forall(_.isLeft), "Union constructor only allowed on types")
            mkUnionType(converted.map(_.left.get))
        }
      //arrow type etc
      case Binary(l, App, r) =>
        val leftTy = convertTHFType(sig)(l, replaces)
        val rightty = convertTHFType(sig)(r, replaces)
        if(leftTy.isLeft && rightty.isLeft) {
          leftTy.left.get.app(rightty.left.get)
        } else throw new SZSException(SZS_InputError)
      case _ => throw new IllegalArgumentException("malformed type expression: "+typ.toString)
    }
  }

  //////////////////////////
  // TFF Formula processing
  //////////////////////////

  protected[parsers] final def processTFF(sig: Signature)(input: TFFAnnotated): Option[Result] = {
    import leo.datastructures.tptp.tff.{Logical, TypedAtom, Sequent}

    input.formula match {
      // Logical formulae can either be terms (axioms, conjecture, ...) or definitions.
      case Logical(lf) if input.role == "definition" => processTFFDef(sig)(lf) match {
        case None => Out.info(s"No direction of definition ${input.name} detected. Treating as axiom.")
          val role = processRole("axiom")
          Some((input.name, processTFF0(sig)(lf, noRep), role))
        case Some((defName, defDef)) => sig.addDefined(defName, defDef, defDef.ty)
                                        None
      }
//      case Logical(lf) => val role = processRole(input.role); Some((input.name, singleTermToClause(processTFF0(sig)(lf, noRep),role), role))
      case Logical(lf) => val role = processRole(input.role); Some((input.name, processTFF0(sig)(lf, noRep), role))
      // Typed Atoms are top-level declarations, put them into signature
      case TypedAtom(atom, ty) =>
        convertTFFType(sig)(ty, noRep) match {
          case Left(ty0) => sig.addUninterpreted(atom, ty0)
          case Right(k) => sig.addTypeConstructor(atom, k)
        }
        None
      // Sequents
      case Sequent(_, _) => throw new IllegalArgumentException("Processing of TFF sequents not yet implemented")
    }
  }

  import leo.datastructures.tptp.tff.{LogicFormula => TFFLogicFormula}
  // Formula definitions
  protected[InputProcessing] final def processTFFDef(sig: Signature)(input: TFFLogicFormula): Option[(String, Term)] = {
    import leo.datastructures.tptp.tff.Atomic
    input match {
      case Atomic(Equality(Func(name, Seq()),right)) => Some(name, processTerm(sig)(right, noRep, false))
      case _ => None
    }
  }

  // Ordinary terms
  protected[InputProcessing] final def processTFF0(sig: Signature)(input: TFFLogicFormula, replaces: Replaces): Term = {
    import leo.datastructures.tptp.tff.{Binary, Quantified, Unary, Inequality, Atomic, Cond, Let}
    input match {
      case Binary(left, conn, right) => processTFFBinaryConn(conn).apply(processTFF0(sig)(left,replaces),processTFF0(sig)(right,replaces))
      case Quantified(q, vars, matrix) =>
        val quantifier = processTFFUnary(q)
        var newReplaces = replaces

        // Fold through the variables to propagate bindings trough variable list
        // and save bindings to `newReplaces` for body conversion
        val processedVars = vars.map{
          case (name, None) =>
            termMapping(newReplaces).get(name) match {
              case None => newReplaces = ((termMapping(newReplaces).+((name,(i, termMapping(newReplaces).size+1+termOffset(newReplaces)))),termOffset(newReplaces)),newReplaces._2)
              case _ =>  newReplaces = ((termMapping(newReplaces).+((name,(i, termMapping(newReplaces).size+1+termOffset(newReplaces)))),termOffset(newReplaces)+1),newReplaces._2)
            }
            (name, Left(i))
          case (name, Some(ty)) => convertTFFType(sig)(ty, newReplaces) match {
            case Left(t) =>
              termMapping(newReplaces).get(name) match {
                case None => newReplaces = ((termMapping(newReplaces).+((name,(t, termMapping(newReplaces).size+1+termOffset(newReplaces)))),termOffset(newReplaces)),newReplaces._2)
                case _ =>  newReplaces = ((termMapping(newReplaces).+((name,(t, termMapping(newReplaces).size+1+termOffset(newReplaces)))),termOffset(newReplaces)+1),newReplaces._2)
              }
              (name, Left(t))
            case Right(k) =>
              typeMapping(newReplaces).get(name) match {
                case None => newReplaces = (newReplaces._1,(typeMapping(newReplaces).+((name,(k, typeMapping(newReplaces).size+1+typeOffset(newReplaces)))),typeOffset(newReplaces)))
                case _ =>  newReplaces = (newReplaces._1,(typeMapping(newReplaces).+((name,(k, typeMapping(newReplaces).size+1+typeOffset(newReplaces)))),typeOffset(newReplaces) +1))
              }
              (name, Right(k))
          }
        }
        mkPolyQuantified(quantifier, processedVars, processTFF0(sig)(matrix, newReplaces))
      case Unary(conn, formula) => processTFFUnary(conn).apply(processTFF0(sig)(formula,replaces))
      case Inequality(left, right) =>
        val convertedLeft = processTermArgs(sig)(left, replaces, false)
        val convertedRight = processTermArgs(sig)(right, replaces, false)
        assert(convertedLeft.isLeft && convertedRight.isLeft)
        import leo.modules.HOLSignature.{Not, ===}
        Not(===(convertedLeft.left.get, convertedRight.left.get))
      case Atomic(atomic) => processAtomicFormula(sig)(atomic, replaces, false)
      case Cond(cond, thn, els) => IF_THEN_ELSE(processTFF0(sig)(cond, replaces),processTFF0(sig)(thn, replaces),processTFF0(sig)(els, replaces))
      case Let(_, _) => throw new SZSException(SZS_Inappropriate, "TFF Tuples not supported at the moment.")
    }
  }

  import leo.datastructures.tptp.tff.{BinaryConnective => TFFBinaryConnective}
  protected[InputProcessing] final def processTFFBinaryConn(conn: TFFBinaryConnective): HOLBinaryConnective = {
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
  protected[InputProcessing] final def processTFFUnary(conn: TFFUnaryConnective): HOLUnaryConnective = {
    import leo.datastructures.tptp.tff.{Not => TFFNot}
    import leo.modules.HOLSignature.{Not => not}

    conn match {
      case TFFNot => not
    }
  }

  import leo.datastructures.tptp.tff.{Quantifier => TFFQuantifier}
  protected[InputProcessing] final def processTFFUnary(conn: TFFQuantifier): HOLUnaryConnective = {
    import leo.datastructures.tptp.tff.{! => TFFAll, ? => TFFAny}
    import leo.modules.HOLSignature.{Forall => forall, Exists => exists}

    conn match {
      case TFFAll => forall
      case TFFAny => exists
    }
  }

  // Type processing
  import leo.datastructures.tptp.tff.{Type => TFFType, AtomicType => TFFAtomicType}

  protected[InputProcessing] final def convertTFFAtomicType(sig: Signature)(atomicType: TFFAtomicType, replace: Replaces): Either[Type, Kind] = {
    import leo.datastructures.tptp.tff.AtomicType
    atomicType match {
      case AtomicType(ty, Seq()) if ty.charAt(0).isUpper => mkVarType(typeMapping(replace).size + typeOffset(replace) - typeMapping(replace)(ty)._2 + 1)  // Type Variable
      case AtomicType(ty, Seq()) if ty == "$tType" => typeKind // kind *
      case AtomicType(ty, Seq())  => mkType(sig.meta(ty).key)  // Base type
      case AtomicType(ty, args) =>
        val convertedArgs = args.map(convertTFFAtomicType(sig)(_, replace))
        mkType(sig.meta(ty).key, convertedArgs.map(_.left.get))
    }
  }
  type TFFBoundTyReplaces = Seq[Variable]
  protected[InputProcessing] final def convertTFFType(sig: Signature)(tffType: TFFType, replace: Replaces): Either[Type,Kind] = {
    import leo.datastructures.tptp.tff.{AtomicType,->,*,QuantifiedType}
    tffType match {
      // "AtomicType" constructs: Type variables, Base types, type kinds, or type/kind applications
      case at@AtomicType(_,_) => convertTFFAtomicType(sig)(at, replace)
      // Function type / kind
      case ->(Seq(in,out)) => // Tricky here: It might be functions of "sort" * -> [], * -> *, [] -> [], [] -> *
                        // We only plan to support variant 1 (polymorphism),2 (constructors), 3 (ordinary functions) in a medium time range (4 is dependent type)
                        // Case 1 is captured by 'case QuantifiedType' due to TFF1's syntax
                        // So, only consider case 3 for now, but keep case 2 in mind
        val convertedOut = convertTFFType(sig)(out, replace)
        in match {
          case at@AtomicType(_,_) =>
            val convertedIn = convertTFFAtomicType(sig)(at, replace)
            require((convertedIn.isLeft && convertedOut.isLeft) || (convertedIn.isRight && convertedOut.isRight), "mixed type/kind setting in abstractin type " +tffType.toString)
            if (convertedIn.isLeft)
              mkFunType(convertedIn.left.get, convertedOut.left.get) // case 3
            else
              Kind.mkFunKind(convertedIn.right.get, convertedOut.right.get) // case 2
          case *(tys) =>
            val convertedIns = tys.map(convertTFFType(sig)(_, replace))
            require((convertedIns.forall(_.isLeft) && convertedOut.isLeft) || (convertedIns.forall(_.isRight) && convertedOut.isRight), "mixed type/kind setting in abstractin type " +tffType.toString)
            if (convertedIns.head.isLeft)
              mkFunType(convertedIns.map(_.left.get), convertedOut.left.get) // case 3
            else
              Kind.mkFunKind(convertedIns.map(_.right.get), convertedOut.right.get) // case 2
          case _ => throw new IllegalArgumentException
        }
      case ->(_) => throw new IllegalArgumentException("more than two arguments in function type or kind. should not happen in first-order.")
      // Product type / kind
      case *(tys) =>
        assert(false, tffType.toString) // TODO: Legacy: Should be removed, check if used anywhere
        val converted = tys.map(convertTFFType(sig)(_, replace))

        require(converted.forall(i => i.isLeft || i.isRight), "Sum constructor only allowed on mixed type/kind")
        if (converted.head.isLeft)
          mkProdType(converted.map(_.left.get))
        else
          Kind.mkFunKind(converted.map(_.right.get))
      // Quantified type
      case QuantifiedType(vars, body) =>
        val processedVars = vars.map{
          case (name, None) => (name, Right(typeKind)) // * is assumed when no type is given
          case (name, Some(ty)) => (name, convertTFFType(sig)(ty, replace))
        }
        require(processedVars.forall(_._2.isRight), "Only '$tType' as type assertion is allowed for type variables in quantified types")
        val newReplaces = processedVars.foldLeft(replace)({case (repl,vari) => vari match {
          case (name, Left(ty)) => termMapping(repl).get(name) match {
            case None => ((termMapping(repl).+((name,(ty, termMapping(repl).size+1+termOffset(repl)))),termOffset(repl)),repl._2)
            case _ =>  ((termMapping(repl).+((name,(ty, termMapping(repl).size+1+termOffset(repl)))),termOffset(repl)+1),repl._2)
          }
          case (name, Right(k)) => typeMapping(repl).get(name) match {
            case None => (repl._1,(typeMapping(repl).+((name,(k, typeMapping(repl).size+1+typeOffset(repl)))),typeOffset(repl)))
            case _ =>  (repl._1,(typeMapping(repl).+((name,(k, typeMapping(repl).size+1+typeOffset(repl)))),typeOffset(repl)+1))
          }
        }})
        processedVars.foldRight(convertTFFType(sig)(body,newReplaces).left.get)({case (_,b) => ∀(b)}) // NOTE: this is only allowed on top-level
        // the body of quantification must be a type.
        // TODO: better error treating
    }
  }

  implicit final def kindToTypeOrKind(k: Kind): TypeOrKind = Right(k)
  implicit final def typeToTypeOrKind(ty: Type): TypeOrKind = Left(ty)
  implicit final def termToTermOrType(t: Term): TermOrType = Left(t)
  implicit final def typeToTermOrType(ty: Type): TermOrType = Right(ty)

  //////////////////////////
  // FOF Formula processing
  //////////////////////////

  protected[parsers] final def processFOF(sig: Signature)(input: FOFAnnotated): Option[Result] = {
    import leo.datastructures.tptp.fof.{Logical, Sequent}
    input.formula match {
//      case Logical(lf) if input.role == "definition" => {  // TODO: Commented out -- how do definitions look like in FOF? See COM021+1.p, RNG126+1.p
//                                                          val (defName, defDef) = processFOFDef(sig)(lf)
//                                                          sig.addDefined(defName, defDef, defDef.ty)
//                                                          None
//                                                        }
//      case Logical(lf) => val role = processRole(input.role); Some((input.name, singleTermToClause(processFOF0(sig)(lf, noRep), role), role))
      case Logical(lf) => val role = processRole(input.role); Some((input.name, processFOF0(sig)(lf, noRep), role))
      case Sequent(_,_) => throw new SZSException(SZS_Inappropriate,"Processing of fof sequents not supported.")
    }
  }

  import leo.datastructures.tptp.fof.{LogicFormula => FOFLogicalFormula}
  protected[InputProcessing] final def processFOFDef(sig: Signature)(input: FOFLogicalFormula): (String, Term) = {
    import leo.datastructures.tptp.fof.Atomic
    input match {
      case Atomic(Equality(Func(name, Seq()),right)) => (name, processTerm(sig)(right, noRep))  // TODO See above TODO
      case _ => throw new IllegalArgumentException("Malformed definition")
    }
  }

  protected[InputProcessing] final def processFOF0(sig: Signature)(input: FOFLogicalFormula, replaces: Replaces): Term = {
    import leo.datastructures.tptp.fof.{Binary, Unary, Quantified, Atomic, Inequality}
    input match {
      case Binary(left, conn, right) => processFOFBinaryConn(conn).apply(processFOF0(sig)(left, replaces),processFOF0(sig)(right, replaces))
      case Unary(conn, f) => processFOFUnary(conn).apply(processFOF0(sig)(f, replaces))
      case Quantified(q, vars, matrix) =>
        val quantifier = processFOFUnary(q)
        var newReplaces = replaces
        // Fold through the variables to propagate bindings trough variable list
        // and save bindings to `newReplaces` for body conversion
        val processedVars = vars.map{
          name =>
            termMapping(newReplaces).get(name) match {
              case None => newReplaces = ((termMapping(newReplaces).+((name, (i, termMapping(newReplaces).size + 1 + termOffset(newReplaces)))), termOffset(newReplaces)), newReplaces._2)
              case _ => newReplaces = ((termMapping(newReplaces).+((name, (i, termMapping(newReplaces).size + 1 + termOffset(newReplaces)))), termOffset(newReplaces) + 1), newReplaces._2)
            }
            (name, i)
        }
        val intermediateRes = processFOF0(sig)(matrix, newReplaces)
        mkPolyQuantifiedFOF(quantifier, processedVars, intermediateRes)
      case Atomic(atomic) => processAtomicFormula(sig)(atomic, replaces)
      case Inequality(left,right) =>
        val convertedLeft = processTermArgs(sig)(left, replaces)
        val convertedRight = processTermArgs(sig)(right, replaces)
        assert(convertedLeft.isLeft && convertedRight.isLeft)
        leo.modules.HOLSignature.!===(convertedLeft.left.get,convertedRight.left.get)
    }
  }

  import leo.datastructures.tptp.fof.{BinaryConnective => FOFBinaryConnective}
  protected[InputProcessing] final def processFOFBinaryConn(conn: FOFBinaryConnective): HOLBinaryConnective = {
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
  protected[InputProcessing] final def processFOFUnary(conn: FOFUnaryConnective): HOLUnaryConnective = {
    import leo.datastructures.tptp.fof.{Not => FOFNot}
    import leo.modules.HOLSignature.{Not => not}

    conn match {
      case FOFNot => not
    }
  }

  import leo.datastructures.tptp.fof.{Quantifier => FOFQuantifier}
  protected[InputProcessing] final def processFOFUnary(conn: FOFQuantifier): HOLUnaryConnective = {
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

  import leo.datastructures.tptp.cnf.{ Formula => CNFLogicalFormula, Literal => CNFLiteral}
  protected[parsers] final def processCNF(sig: Signature)(input: CNFAnnotated): Option[Result] = {
    val role = processRole(input.role)
    val varsInClause = input.formula.vars
    Some((input.name, processCNF0(sig)(input.formula, varsInClause), role))
  }

  protected[InputProcessing] final def processCNF0(sig: Signature)(input: CNFLogicalFormula, vars: Set[String]): Term = {
    val indexSeq: Seq[(Type, Int)] = (1 to vars.size).map{idx => (i, idx)}
    val varMap: TermVarReplaces = (vars.toSeq.zip(indexSeq).toMap, 0)
    val replaces: Replaces = (varMap, (Map.empty, 0))
    val lits = input.literals.map(processLiteral(sig)(_, replaces))
    leo.datastructures.mkDisjunction(lits)
  }

  protected[InputProcessing] final def processLiteral(sig: Signature)(lit: CNFLiteral, replaces: Replaces): Term = {
    import leo.datastructures.tptp.cnf.{Positive, Negative, Inequality}
    import leo.modules.HOLSignature.Not
    lit match {
      case Positive(f) => processAtomicFormula(sig)(f, replaces)
      case Negative(f) => Not(processAtomicFormula(sig)(f, replaces))
      case Inequality(l, r) =>
        val convertedLeft = processTermArgs(sig)(l, replaces)
        val convertedRight = processTermArgs(sig)(r, replaces)
        assert(convertedLeft.isLeft && convertedRight.isLeft)
        leo.modules.HOLSignature.!===(convertedLeft.left.get,convertedRight.left.get)
    }
  }


  ////////////////////////////
  // Common 'term' processing
  ////////////////////////////
  import leo.datastructures.tptp.Commons.{NumberTerm, RationalNumber, IntegerNumber, DoubleNumber}
  import leo.datastructures.tptp.Commons.{Distinct, Cond, Let, Plain, DefinedPlain, SystemPlain, AtomicFormula, Tuple}

  // process term level
  protected[InputProcessing] final def processTermArgs(sig: Signature)(input: TPTPTerm, replace: Replaces, adHocDefs: Boolean = true): TermOrType = input match {
    case Func(name, Seq()) =>
      if (sig.exists(name) || !adHocDefs) {
        val meta = sig(name)
        if (meta.hasType) mkAtom(meta.key)(sig)
        else Type.mkType(meta.key)
      } else {
        // fof world here
        mkAtom(sig.addUninterpreted(name, i))(sig)
      }
    case Func(name, vars) =>
      val converted = vars.map(processTermArgs(sig)(_, replace, adHocDefs))
      if (sig.exists(name) || !adHocDefs) {
        val meta = sig(name)
        if (meta.hasType) mkApp(mkAtom(sig(name).key)(sig), converted)
        else Type.mkType(meta.key, converted.map(_.right.get))
      } else {
        // fof world here
        assert(converted.forall(_.isLeft))
        mkApp(mkAtom(sig.addUninterpreted(name, mkFunType(vars.map(_ => i), i)))(sig), converted)
      }
    case Var(name) => termMapping(replace).get(name) match {
      case None => typeMapping(replace).get(name) match {
        case Some((k, scope))  => assert(k == Kind.*); Type.mkVarType(typeMapping(replace).size + typeOffset(replace) - scope + 1)
        case _ => throw new SZSException(SZS_InputError,"Unbound variable found in formula: "+input.toString)
      }
      case Some((ty, scope)) =>
        assert(typeMapping(replace).get(name).isEmpty)
        mkBound(ty, termMapping(replace).size + termOffset(replace) - scope +1)
    }
    case other => processTerm(sig)(other, replace, adHocDefs)
  }

  // should actually be called processFormula since we are at formula level here
  protected[InputProcessing] final def processTerm(sig: Signature)(input: TPTPTerm, replace: Replaces, adHocDefs: Boolean = true): Term = input match {
    case Func(name, vars) =>
      val converted = vars.map(processTermArgs(sig)(_, replace, adHocDefs))
      if (sig.exists(name) || !adHocDefs) {
        mkApp(mkAtom(sig(name).key)(sig), converted)
      } else {
        assert(converted.forall(_.isLeft))
        mkApp(mkAtom(sig.addUninterpreted(name, mkFunType(vars.map(_ => i), o)))(sig), converted)
      }
    case DefinedFunc(name, vars) =>
      if (sig.exists(name)) {
        // FIXME: Legacy code
        if (sig(name)._ty.isPolyType) {

          val converted = vars.map(processTerm(sig)(_, replace, adHocDefs))
          // FIXME: Hack
          val intermediate = Term.mkTypeApp(mkAtom(sig(name).key)(sig), converted.head.ty)
          mkTermApp(intermediate, converted)
          /*// converted only contains terms
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
            func.apply(converted.head)
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
            func.apply(converted.head, converted(1))
          } else {
            // This should not happen
            Out.severe("A problem used an unknown polymorphic TPTP-defined function (Dollarword) with arity > 2")
            throw new SZSException(SZS_InputError)
          }*/

        } else {
          val converted = vars.map(processTerm(sig)(_, replace, adHocDefs))
          mkTermApp(mkAtom(sig(name).key)(sig), converted)
        }
      } else throw new SZSException(SZS_Inappropriate, s"TPTP functor $name not supported")

    case SystemFunc(name, vars) =>
      val converted = vars.map(processTerm(sig)(_, replace, adHocDefs))
      mkTermApp(mkAtom(sig(name).key)(sig), converted)
    case Tuple(_) => throw new SZSException(SZS_Inappropriate, "Tuples not supported.")
    case Var(_) => throw new SZSException(SZS_InputError, "Variable on top-level detected.")
    case NumberTerm(value) => value match {
      case IntegerNumber(value0) =>
        val constName = "$$int(" + value0.toString + ")"
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
      case DoubleNumber(value0) =>
        val constName = "$$real(" + value0.toString + ")"
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
      case RationalNumber(p,q) =>
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
    case Distinct(data) => // NOTE: Side-effects may occur if this is the first occurence of '"data"'
                            if (sig.exists("\""+data+"\"")) {
                              mkAtom(sig.apply("\""+data+"\"").key)(sig)
                            } else {
                              mkAtom(sig.addUninterpreted("\""+data+"\"", i))(sig)
                            }
    case Cond(cond, thn, els) =>
      IF_THEN_ELSE(processTFF0(sig)(cond, replace),processTerm(sig)(thn, replace, adHocDefs),processTerm(sig)(els, replace, adHocDefs))
    case Let(_, _) =>  throw new SZSException(SZS_Inappropriate, "Unsupported let-definition.")
  }

  protected[InputProcessing] final def processAtomicFormula(sig: Signature)(input: AtomicFormula, replace: Replaces, adHocDefs: Boolean = true): Term = input match {
    case Plain(func) => processTerm(sig)(func, replace,adHocDefs)
    case DefinedPlain(func) => processTerm(sig)(func, replace, adHocDefs)
    case SystemPlain(func) => processTerm(sig)(func, replace, adHocDefs)
    case Equality(left,right) =>
      import leo.modules.HOLSignature.===
      val convertedLeft = processTermArgs(sig)(left, replace, adHocDefs)
      val convertedRight = processTermArgs(sig)(right, replace, adHocDefs)
      assert(convertedLeft.isLeft && convertedRight.isLeft)
      ===(convertedLeft.left.get, convertedRight.left.get)
  }

  ///////////
  // Utility
  ///////////
  private final def mkPolyQuantified(q: HOLUnaryConnective, varList: Seq[ProcessedVar], body: Term): Term = {
    import leo.modules.HOLSignature.{Forall, TyForall}
    def mkPolyHelper(a: ProcessedVar, b: Term): Term = a match {
      case (_, Left(ty)) => q.apply(λ(ty)(b))
      case (_, Right(`typeKind`)) if q == Forall => TyForall(Λ(b))
      case (_, Right(`typeKind`)) if q == HOLLambda => Λ(b)
      case (_, Right(_))        => throw new IllegalArgumentException(s"Formalization of kinds other than * and ${q.pretty} not yet implemented.")
    }
    varList.foldRight(body)(mkPolyHelper)
  }
  private final def mkPolyQuantifiedFOF(q: HOLUnaryConnective, varList: Seq[(Variable, Type)], body: Term): Term = {
    varList.foldRight(body)({case ((_, ty), term) => q.apply(λ(ty)(term))})
  }
}
