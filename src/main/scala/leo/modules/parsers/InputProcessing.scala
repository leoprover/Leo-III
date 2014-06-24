package leo.modules.parsers

import scala.language.implicitConversions

import leo.datastructures.tptp.Commons._
import leo.datastructures.tptp.Commons.{Term => TPTPTerm}
import leo.datastructures.internal.{Signature, Term, Type, Kind}
import leo.datastructures.internal.HOLBinaryConnective
import leo.datastructures.internal.HOLUnaryConnective

import Term.{mkAtom,λ, mkBound,mkTermApp}
import Type.{mkFunType,mkType,∀,mkVarType, typeKind}
import leo.datastructures.tptp.Commons.THFAnnotated
import leo.datastructures.tptp.Commons.TPIAnnotated
import leo.datastructures.tptp.Commons.TFFAnnotated

/**
 * Processing module from TPTP input.
 * Declarations are inserted into the given Signature,
 * terms are returned in internal term representation.
 *
 * @author Alexander Steen
 * @since 18.06.2014
 */
object InputProcessing {
  // (Formula name, Term, Formula Role)
  type Result = (String, Term, String)
  type BoundReplaces = Seq[(Variable, Type)]
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
  def processAll(sig: Signature)(input: Seq[AnnotatedFormula]): Seq[Result] = ???

  def process(sig: Signature)(input: AnnotatedFormula): Option[Result] = {
    input match {
      case _:TPIAnnotated => processTPI(sig)(input.asInstanceOf[TPIAnnotated])
      case _:THFAnnotated => processTHF(sig)(input.asInstanceOf[THFAnnotated])
      case _:TFFAnnotated => processTFF(sig)(input.asInstanceOf[TFFAnnotated])
      case _:FOFAnnotated => processFOF(sig)(input.asInstanceOf[FOFAnnotated])
      case _:CNFAnnotated => processCNF(sig)(input.asInstanceOf[CNFAnnotated])
    }
  }


  //////////////////////////
  // TPI Formula processing
  //////////////////////////

  def processTPI(sig: Signature)(input: TPIAnnotated): Option[Result] = ???


  //////////////////////////
  // THF Formula processing
  //////////////////////////

  def processTHF(sig: Signature)(input: THFAnnotated): Option[Result] = ???

  //////////////////////////
  // TFF Formula processing
  //////////////////////////

  def processTFF(sig: Signature)(input: TFFAnnotated): Option[Result] = {
    import leo.datastructures.tptp.tff.{Logical, TypedAtom, Sequent, AtomicType}

    input.formula match {
      // Logical formulae can either be terms (axioms, conjecture, ...) or definitions.
      case Logical(lf) if input.role == "definition" => {
                                                          val (defName, defDef) = processTFFDef(sig)(lf)
                                                          sig.addDefined(defName, defDef, defDef.ty)
                                                          None
                                                        }
      case Logical(lf) => Some((input.name, processTFF0(sig)(lf, Seq.empty), input.role))
      // Typed Atoms are top-level declarations, put them into signature
      case TypedAtom(atom, ty) => {
        convertTFFType(sig)(ty, Seq.empty) match {
          case Left(ty) => sig.addUninterpreted(atom, ty)
          case Right(k) => sig.addUninterpreted(atom, k)
        }
        None
      }
      // Sequents
      case Sequent(_, _) => throw new IllegalArgumentException("Processing of TFF sequents not yet implemented")
    }


  }

  import leo.datastructures.tptp.tff.{LogicFormula => TFFLogicFormula}
  // Formula definitions
  protected[parsers] def processTFFDef(sig: Signature)(input: TFFLogicFormula): (String, Term) = {
    import leo.datastructures.tptp.tff.Atomic
    input match {
      case Atomic(Equality(Func(name, Nil),right)) => (name, processTerm(sig)(right, Seq.empty))  // TODO Is this the right term to construct equalities in tff?
      case _ => throw new IllegalArgumentException("Malformed definition")
    }
  }

  // Ordinary terms
  protected[parsers] def processTFF0(sig: Signature)(input: TFFLogicFormula, replaces: BoundReplaces): Term = {
    import leo.datastructures.tptp.tff.{Binary, Quantified, Unary, Inequality, Atomic, Cond, Let,AtomicType}
    input match {
      case Binary(left, conn, right) => processTFFBinaryConn(conn).apply(processTFF0(sig)(left,replaces),processTFF0(sig)(right,replaces))
      case Quantified(q, vars, matrix) => {
        val quantifier = processTFFUnary(q)
        val processedVars = vars.map(_ match {
          case (name, None) => (name, mkType(2)) // $i is assumed when no type is given
          case (name, Some(ty)) => {
            val converted = convertTFFType(sig)(ty, Seq.empty)
            converted match {
              case Left(t) => (name, t)
              case Right(k) => throw new IllegalArgumentException("mixed quantification of type vars and term vars not yet supported.") // TODO
            }
          }
        })
        processedVars.foldRight(processTFF0(sig)(matrix,replaces.++(processedVars)))({case (v,t) => quantifier.apply(λ(v._2)(t))})
      }
      case Unary(conn, formula) => processTFFUnary(conn).apply(processTFF0(sig)(formula,replaces))
      case Inequality(left, right) => {
        val (l,r) = (processTerm(sig)(left, replaces),processTerm(sig)(right, replaces))
        import leo.datastructures.internal.{===, Not}
        Not(===(l,r))
      }
      case Atomic(atomic) => processAtomicFormula(sig)(atomic, replaces)
      case Cond(cond, thn, els) => ???
      case Let(binding, in) => ???
    }
  }

  import leo.datastructures.tptp.tff.{BinaryConnective => TFFBinaryConnective}
  protected[parsers] def processTFFBinaryConn(conn: TFFBinaryConnective): HOLBinaryConnective = {
    import leo.datastructures.tptp.tff.{<=> => TFFEquiv, Impl => TFFImpl, <= => TFFIf, | => TFFOr, & => TFFAnd, ~| => TFFNor, ~& => TFFNand, <~> => TFFNiff}
    import leo.datastructures.internal.{<=> => equiv, Impl => impl, <= => i_f, ||| => or, & => and, ~||| => nor, ~& => nand, <~> => niff}

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
    import leo.datastructures.internal.{Not => not}
    conn match {
      case TFFNot => not
    }
  }

  import leo.datastructures.tptp.tff.{Quantifier => TFFQuantifier}
  protected[parsers] def processTFFUnary(conn: TFFQuantifier): HOLUnaryConnective = {
    import leo.datastructures.tptp.tff.{! => TFFAll, ? => TFFAny}
    import leo.datastructures.internal.{Forall => forall, Exists => exists}
    conn match {
      case TFFAll => forall
      case TFFAny => exists
    }
  }

  // Type processing
  import leo.datastructures.tptp.tff.{Type => TFFType}
  type TFFBoundTyReplaces = Seq[Variable]
  protected[parsers] def convertTFFType(sig: Signature)(tffType: TFFType, replace: TFFBoundTyReplaces): Either[Type,Kind] = {
    import leo.datastructures.tptp.tff.{AtomicType,->,*,QuantifiedType}
    tffType match {
      // "AtomicType" constructs: Type variables, Base types, type kinds, or type/kind applications
      case AtomicType(ty, List()) if ty.charAt(0).isUpper => mkVarType(replace.length - replace.indexOf(ty))  // Type Variable
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
      case *(_) => throw new IllegalArgumentException("Processing of product types not implemented yet") // TODO
      // Quantified type
      case QuantifiedType(vars, body) => {
        val vars2 = vars.map(_._1)
        // In the following: body must be a type, otherwise we could not quantify over it
        vars2.foldRight(convertTFFType(sig)(body,vars2).left.get)({case (_,b) => ∀(b)}) // NOTE: this is only allowed on top-level
        // thats why we ignore the previous vars
      }
    }
  }

  implicit def kindToTypeOrKind(k: Kind): Either[Type, Kind] = Right(k)
  implicit def typeToTypeOrKind(ty: Type): Either[Type, Kind] = Left(ty)

  //////////////////////////
  // FOF Formula processing
  //////////////////////////

  def processFOF(sig: Signature)(input: FOFAnnotated): Option[Result] = {
    import leo.datastructures.tptp.fof.{Logical, Sequent}
    input.formula match {
      case Logical(lf) if input.role == "definition" => {
                                                          val (defName, defDef) = processFOFDef(sig)(lf)
                                                          sig.addDefined(defName, defDef, defDef.ty)
                                                          None
                                                        }
      case Logical(lf) => Some((input.name, processFOF0(sig)(lf, Seq.empty), input.role))
      case Sequent(_,_) => throw new IllegalArgumentException("Processing of fof sequents not yet implemented.")
    }
  }

  import leo.datastructures.tptp.fof.{LogicFormula => FOFLogicalFormula}
  protected[parsers] def processFOFDef(sig: Signature)(input: FOFLogicalFormula): (String, Term) = {
    import leo.datastructures.tptp.fof.Atomic
    input match {
      case Atomic(Equality(Func(name, Nil),right)) => (name, processTerm(sig)(right, Seq.empty))  // TODO Is this the right term to construct equalities in tff?
      case _ => throw new IllegalArgumentException("Malformed definition")
    }
  }

  protected[parsers] def processFOF0(sig: Signature)(input: FOFLogicalFormula, replaces: BoundReplaces): Term = {
    import leo.datastructures.tptp.fof.{Binary, Unary, Quantified, Atomic, Inequality}
    input match {
      case Binary(left, conn, right) => processFOFBinaryConn(conn).apply(processFOF0(sig)(left, replaces),processFOF0(sig)(right, replaces))
      case Unary(conn, f) => processFOFUnary(conn).apply(processFOF0(sig)(f, replaces))
      case Quantified(q, varList, matrix) => {
        val quantifier = processFOFUnary(q)
        val processedVars = varList.map((_, mkType(2)))
        processedVars.foldRight(processFOF0(sig)(matrix, replaces.++(processedVars)))({case (v,t) => quantifier.apply(λ(v._2)(t))})
      }
      case Atomic(atomic) => processAtomicFormula(sig)(atomic, replaces)
      case Inequality(left,right) => {
        val (l,r) = (processTerm(sig)(left, replaces),processTerm(sig)(right, replaces))
        import leo.datastructures.internal.{===, Not}
        Not(===(l,r))
      }
    }
  }

  import leo.datastructures.tptp.fof.{BinaryConnective => FOFBinaryConnective}
  protected[parsers] def processFOFBinaryConn(conn: FOFBinaryConnective): HOLBinaryConnective = {
    import leo.datastructures.tptp.fof.{<=> => FOFEquiv, Impl => FOFImpl, <= => FOFIf, | => FOFOr, & => FOFAnd, ~| => FOFNor, ~& => FOFNand, <~> => FOFNiff}
    import leo.datastructures.internal.{<=> => equiv, Impl => impl, <= => i_f, ||| => or, & => and, ~||| => nor, ~& => nand, <~> => niff}

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
    import leo.datastructures.internal.{Not => not}
    conn match {
      case FOFNot => not
    }
  }

  import leo.datastructures.tptp.fof.{Quantifier => FOFQuantifier}
  protected[parsers] def processFOFUnary(conn: FOFQuantifier): HOLUnaryConnective = {
    import leo.datastructures.tptp.fof.{! => FOFAll, ? => FOFAny}
    import leo.datastructures.internal.{Forall => forall, Exists => exists}
    conn match {
      case FOFAll => forall
      case FOFAny => exists
    }
  }


  //////////////////////////
  // CNF Formula processing
  //////////////////////////

  def processCNF(sig: Signature)(input: CNFAnnotated): Option[Result] = ???


  ////////////////////////////
  // Common 'term' processing
  ////////////////////////////
  def processTerm(sig: Signature)(input: TPTPTerm, replace: BoundReplaces): Term = input match {
    case Func(name, vars) => {
      val converted = vars.map(processTerm(sig)(_, replace))
      mkTermApp(mkAtom(sig(name).key), converted)
    }
    case DefinedFunc(name, vars) => ???
    case SystemFunc(name, vars) => ???
    case Var(name) => replace.indexWhere(_._1 == name) match {
      case -1 => throw new IllegalArgumentException("Unbound variable found in formula: "+input.toString)
      case n => mkBound(replace(n)._2, replace.length - n)
    }

    case Number(value) => ???
    case Distinct(data) => ???
    case Cond(cond, thn, els) => ???
    case Let(binding, in) => ???
  }

  def processAtomicFormula(sig: Signature)(input: AtomicFormula, replace: BoundReplaces): Term = input match {
    case Plain(func) => processTerm(sig)(func, replace)
    case DefinedPlain(func) => processTerm(sig)(func, replace)
    case SystemPlain(func) => processTerm(sig)(func, replace)
    case Equality(left,right) => {
      import leo.datastructures.internal.===
      ===(processTerm(sig)(left, replace),processTerm(sig)(right, replace))
    }
  }
}

