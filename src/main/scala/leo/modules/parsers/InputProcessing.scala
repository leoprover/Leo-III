package leo.modules.parsers

import leo.datastructures.tptp.Commons._
import leo.datastructures.internal.{Signature, IsSignature, Term, Type}

import Term.{mkAtom}
import Type.{mkFunType,mkType,∀,mkVarType}
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
      case Logical(lf) if input.role == "definition" => ??? //sig.addDefined()
      case Logical(lf) => processTFF0(sig)(lf).map((input.name, _, input.role))
      case TypedAtom(atom, ty) if ty == AtomicType("$tType", List()) => sig.addBaseType(atom); None
      case TypedAtom(atom, ty) => sig.addUninterpreted(atom, convertTFFType(sig)(ty,Seq.empty)); None
      case Sequent(_, _) => throw new IllegalArgumentException("Processing of TFF sequents not yet implemented")
    }


  }

  import leo.datastructures.tptp.tff.{LogicFormula => TFFLogicFormula}
  protected[parsers] def processTFF0(sig: Signature)(input: TFFLogicFormula): Option[Term] = ???

  import leo.datastructures.tptp.tff.{Type => TFFType}
  type TFFBoundReplaces = Seq[Variable]
  protected[parsers] def convertTFFType(sig: Signature)(tffType: TFFType, replace: TFFBoundReplaces): Type = {
    import leo.datastructures.tptp.tff.{AtomicType,->,*,QuantifiedType}
    tffType match {
      case AtomicType(ty, List()) if ty.charAt(0).isUpper => mkVarType(replace.length - replace.indexOf(ty))  // Type Variable
      case AtomicType(ty, List())  => mkType(sig.meta(ty).key)  // Atomic Type
      case AtomicType(_, _) => throw new IllegalArgumentException("Processing of applied types not implemented yet") // TODO
      case ->(tys) => mkFunType(tys.init.map(convertTFFType(sig)(_,replace)), convertTFFType(sig)(tys.last,replace))
      case *(_) => throw new IllegalArgumentException("Processing of product types not implemented yet") // TODO
      case QuantifiedType(vars, body) => {
        val vars2 = vars.map(_._1)
        vars2.foldRight(convertTFFType(sig)(body,vars2))({case (_,b) => ∀(b)}) // NOTE: this is only allowed on top-level
        // thats why we ignore the previous vars
      }
    }
  }

  //////////////////////////
  // FOF Formula processing
  //////////////////////////

  def processFOF(sig: Signature)(input: FOFAnnotated): Option[Result] = ???

  //////////////////////////
  // CNF Formula processing
  //////////////////////////

  def processCNF(sig: Signature)(input: CNFAnnotated): Option[Result] = ???
}