package leo.datastructures.blackboard

import leo.datastructures.internal.{ Term => Formula }
import java.util.concurrent.atomic.AtomicInteger


object Store {
  protected[blackboard] var unnamedFormulas : AtomicInteger = new AtomicInteger(0)

  def apply(name : String, initFormula : Formula) : FormulaStore
    = new FormulaStore(name, Left(initFormula), "plain", 0)

  def apply(initFormula : Formula, role : String, status : Int) : FormulaStore
    = new FormulaStore("gen_formula_"+unnamedFormulas.incrementAndGet(), Left(initFormula), role, status)

  def apply(initFormula : Formula) : FormulaStore
  = Store(initFormula,"plain", 0)

  def apply(initFormula : Formula, status : Int) : FormulaStore
    = Store(initFormula, "plain" ,status)

  def apply(initFormula : Formula, role : String) : FormulaStore
    = Store(initFormula, role, 0)

  def apply(name : String, initFormula : Formula, role : String) : FormulaStore
    = Store(name, initFormula, role, 0)

  def apply(name : String, initFormula : Formula, role : String, status : Int) : FormulaStore
    = new FormulaStore(name,Left(initFormula), role, status)
}

/**
 * Class to Store Formula Information.
 *
 * Table for status Int. Please extend as you like
 * <table>
 *   <thead>
 *    <tr><th>Bit</th><th>Descritpion</th></tr>
 *   </thead>
 *   <tbody>
 *     <tr><th>1.</th><th> Term is simplified </th></tr>
 *     <tr><th>2.</th><th> Term is def expanded </th></tr>
 *     <tr><th>3.</th><th> Term is in Negation Normal Form </th></tr>
 *     <tr><th>4.</th><th> Term is Skolemized </th></tr>
 *     <tr><th>5.</th><th> Term is in PrenexNormalform </th></tr>
 *   </tbody>
 * </table>
 *
 */
class FormulaStore(_name : String, _formula : Either[Formula,Seq[Formula]], _role : String, _status : Int){

  def name : String = _name
  def formula : Either[Formula,Seq[Formula]] = _formula
  def status : Int = _status
  def role : String = _role

  def simpleFormula : Formula = formula match {
    case Left(f) => f
    case Right(_) => throw new IllegalArgumentException("Expected Simple Formula, but got CNF")
  }

  def cnfFormula : Seq[Formula] = formula match {
    case Left(f)  => List(f)
    case Right(fs)=> fs
  }

  /**
   *
   * This method returns the flag set for a fully normalized term
   *
   * (ATM only the first two, therefor 1 + 4 = 5)
   *
   * @return 29, all normalize fields are set
   */
  def normalized : Int = 5

  def newName(nname : String) : FormulaStore = new FormulaStore(nname, formula, _role, _status)
  def newFormula(nformula : Formula) : FormulaStore = new FormulaStore(_name, Left(nformula), _role, _status)
  def newCNF(cformula : Seq[Formula]) : FormulaStore = new FormulaStore(_name, Right(cformula), _role, _status)
  def newStatus(nstatus : Int) : FormulaStore = new FormulaStore(_name, _formula, _role, nstatus)
  def newRole(nrole : String) : FormulaStore = new FormulaStore(_name, _formula, nrole, _status)

  def randomName() : FormulaStore = new FormulaStore("gen_formula_"+Store.unnamedFormulas.incrementAndGet(), formula, _role, _status)
}