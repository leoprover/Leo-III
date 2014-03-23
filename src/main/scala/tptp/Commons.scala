package tptp

/**
 * Created by lex on 3/21/14.
 */
object Commons {

  sealed abstract class Formula
  case class TPIFormula(name: Name, role: Role, formula: FOF, annotations: Annotations) extends Formula
  case class THFFormula(name: Name, role: Role, formula: THF, annotations: Annotations) extends Formula
  case class TFFFormula(name: Name, role: Role, formula: TFF, annotations: Annotations) extends Formula
  case class FOFFormula(name: Name, role: Role, formula: FOF, annotations: Annotations) extends Formula
  case class CNFFormula(name: Name, role: Role, formula: CNF, annotations: Annotations) extends Formula

  // Annotations for annotated formulae
  type Annotations = Option[(Source, List[GeneralTerm])]
  type Role = String


  // First-order atoms


  // First-order terms
  type Variable = String

  // System terms

  // General purpose things
  type Source = GeneralTerm


  // Non-logical data (GeneralTerm, General data)
  sealed abstract class GeneralTerm
  case class SingleTerm(term: Either[GeneralData, (GeneralData, GeneralTerm)]) extends GeneralTerm
  case class ListTerm(terms: List[GeneralTerm]) extends GeneralTerm


  sealed abstract class GeneralData
  case class GWord(gWord: String) extends GeneralData
  case class GFunc(gFunc: GeneralFunction) extends GeneralData
  case class GVar(gVar: Variable) extends GeneralData
  case class GNumber(gNumber: Double) extends GeneralData
  case class GDistinct(data: String) extends GeneralData
  case class GFormulaData(data: FormulaData) extends GeneralData


  sealed abstract class FormulaData
  case class THFData(formula: THF) extends FormulaData
  case class TFFData(formula: TFF) extends FormulaData
  case class FOFData(formula: FOF) extends FormulaData
  case class CNFData(formula: CNF) extends FormulaData

  sealed case class GeneralFunction(name: String, args: List[GeneralTerm])

  // General purpose
  type Name = Either[String, Int]

  // File input
  type TPTPInput = String

}