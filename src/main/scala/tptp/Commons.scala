package tptp

/**
 * Created by lex on 3/21/14.
 */
object Commons {

  // Files
  sealed case class TPTPInput(inputs: List[Either[AnnotatedFormula, Include]])

  // Formula records
  sealed abstract class AnnotatedFormula
  case class TPIFormula(name: Name, role: Role, formula: FOF, annotations: Annotations) extends AnnotatedFormula
  case class THFFormula(name: Name, role: Role, formula: THF, annotations: Annotations) extends AnnotatedFormula
  case class TFFFormula(name: Name, role: Role, formula: TFF, annotations: Annotations) extends AnnotatedFormula
  case class FOFFormula(name: Name, role: Role, formula: FOF, annotations: Annotations) extends AnnotatedFormula
  case class CNFFormula(name: Name, role: Role, formula: CNF, annotations: Annotations) extends AnnotatedFormula

  type Annotations = Option[(Source, List[GeneralTerm])]
  type Role = String

  // First-order atoms
  sealed abstract class AtomicFormula
  case class Plain(name: String, args: List[Term]) extends AtomicFormula
  case class DefinedPlain(name: String, args: List[Term]) extends AtomicFormula
  case class Equality(left: Term, right: Term) extends AtomicFormula
  case class SystemPlain(name: String, args: List[Term]) extends AtomicFormula

  // First-order terms
  sealed abstract class Term
  case class Func(name: String, args: List[Term]) extends Term
  case class DefinedFunc(name: String, args: List[Term]) extends Term
  case class SystemFunc(name: String, args: List[Term]) extends Term
  case class Var(name: Variable) extends Term
  case class Number(value: Double) extends Term
  case class Distinct(data: String) extends Term
  case class Cond(cond: TFF, then: Term, els: Term) extends Term // Cond used by TFF only
  // can Let be modeled like this?
  case class Let(let: TFF, in: Term) extends Term // Let used by TFF only

  type Variable = String

  // System terms

  // General purpose things
  type Source = GeneralTerm

  // Include directives
  type Include = (String, List[Name])

  // Non-logical data (GeneralTerm, General data)
  sealed abstract class GeneralTerm
  case class SingleTerm(term: Either[GeneralData, (GeneralData, GeneralTerm)]) extends GeneralTerm
  case class ListTerm(terms: List[GeneralTerm]) extends GeneralTerm


  sealed abstract class GeneralData
  case class GWord(gWord: String) extends GeneralData
  case class GFunc(name: String, args: List[GeneralTerm]) extends GeneralData
  case class GVar(gVar: Variable) extends GeneralData
  case class GNumber(gNumber: Double) extends GeneralData
  case class GDistinct(data: String) extends GeneralData
  case class GFormulaData(data: FormulaData) extends GeneralData

  sealed abstract class FormulaData
  case class THFData(formula: THF) extends FormulaData
  case class TFFData(formula: TFF) extends FormulaData
  case class FOFData(formula: FOF) extends FormulaData
  case class CNFData(formula: CNF) extends FormulaData
  case class FOTData(term: Term) extends FormulaData

  // General purpose
  type Name = Either[String, Int]

}