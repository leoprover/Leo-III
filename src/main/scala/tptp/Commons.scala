package tptp

object Commons {

  // Files
  sealed case class TPTPInput(inputs: List[Either[AnnotatedFormula, Include]]) {
    def getIncludes:List[Include] = inputs.filter(x => x.isRight).map(_.merge).asInstanceOf[List[Include]]
    def getIncludeCount: Int = getIncludes.size
    def getFormulae:List[AnnotatedFormula] = inputs.filter(x => x.isLeft).map(_.merge).asInstanceOf[List[AnnotatedFormula]]
    def getFormulaeCount: Int = getFormulae.size
  }

  // Formula records
  sealed abstract class AnnotatedFormula(val name: Name, val role: Role,val annotations: Annotations) {
    type FormulaType
    def f: FormulaType
  }
  case class TPIAnnotated(override val name: Name,override val role: Role,formula: fof.Formula,override val annotations: Annotations) extends AnnotatedFormula(name, role, annotations) {
    override type FormulaType = fof.Formula
    override def f = formula
  }
  case class THFAnnotated(override val name: Name, override val role: Role, formula: thf.Formula,override val annotations: Annotations) extends AnnotatedFormula(name, role, annotations) {
    override type FormulaType = thf.Formula
    override def f = formula
  }
  case class TFFAnnotated(override val name: Name, override val role: Role, formula: tff.Formula, override val annotations: Annotations) extends AnnotatedFormula(name, role, annotations) {
    override type FormulaType = tff.Formula
    override def f = formula
  }
  case class FOFAnnotated(override val name: Name, override val role: Role, formula: fof.Formula, override val annotations: Annotations) extends AnnotatedFormula(name, role, annotations) {
    override type FormulaType = fof.Formula
    override def f = formula
  }
  case class CNFAnnotated(override val name: Name, override val role: Role, formula: cnf.Formula,override val annotations: Annotations) extends AnnotatedFormula(name, role, annotations) {
    override type FormulaType = cnf.Formula
    override def f = formula
  }

  type Annotations = Option[(Source, List[GeneralTerm])]
  type Role = String

  // First-order atoms
  sealed abstract class AtomicFormula
  case class Plain(data: Func) extends AtomicFormula
  case class DefinedPlain(data: DefinedFunc) extends AtomicFormula
  case class Equality(left: Term, right: Term) extends AtomicFormula
  case class SystemPlain(data: SystemFunc) extends AtomicFormula

  // First-order terms
  sealed abstract class Term
  case class Func(name: String, args: List[Term]) extends Term
  case class DefinedFunc(name: String, args: List[Term]) extends Term
  case class SystemFunc(name: String, args: List[Term]) extends Term
  case class Var(name: Variable) extends Term
  case class Number(value: Double) extends Term
  case class Distinct(data: String) extends Term
  case class Cond(cond: tff.LogicFormula, thn: Term, els: Term) extends Term // Cond used by TFF only
  // can Let be modeled like this?
  case class Let(let: tff.LetBinding, in: Term) extends Term // Let used by TFF only

  type Variable = String

  // System terms

  // General purpose things
  type Source = GeneralTerm

  // Include directives
  type Include = (String, List[Name])

  // Non-logical data (GeneralTerm, General data)
  sealed case class GeneralTerm(term: List[Either[GeneralData, List[GeneralTerm]]])
  //case class SingleTerm(term: Either[GeneralData, (GeneralData, GeneralTerm)]) extends GeneralTerm
  //case class ListTerm(terms: List[GeneralTerm]) extends GeneralTerm


  sealed abstract class GeneralData
  case class GWord(gWord: String) extends GeneralData
  case class GFunc(name: String, args: List[GeneralTerm]) extends GeneralData
  case class GVar(gVar: Variable) extends GeneralData
  case class GNumber(gNumber: Double) extends GeneralData
  case class GDistinct(data: String) extends GeneralData
  case class GFormulaData(data: FormulaData) extends GeneralData

  sealed abstract class FormulaData
  case class THFData(formula: thf.Formula) extends FormulaData
  case class TFFData(formula: tff.Formula) extends FormulaData
  case class FOFData(formula: fof.Formula) extends FormulaData
  case class CNFData(formula: cnf.Formula) extends FormulaData
  case class FOTData(term: Term) extends FormulaData

  // General purpose
  type Name = String

}