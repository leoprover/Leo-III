package leo.datastructures.blackboard

import leo.datastructures.internal.{ Term => Formula }
import java.util.concurrent.atomic.AtomicInteger


object Store {
  private var unnamedFormulas : AtomicInteger = new AtomicInteger(0)

  def apply(name : String, initFormula : Formula) : FormulaStore
    = new FormulaStore(name, initFormula, "plain", 0)

  def apply(initFormula : Formula, role : String, status : Int) : FormulaStore
    = new FormulaStore("gen_formula_"+unnamedFormulas.incrementAndGet(),initFormula, role, status)

  def apply(initFormula : Formula) : FormulaStore
  = Store(initFormula,"plain", 0)

  def apply(initFormula : Formula, status : Int) : FormulaStore
    = Store(initFormula, "plain" ,status)

  def apply(initFormula : Formula, role : String) : FormulaStore
    = Store(initFormula, role, 0)

  def apply(name : String, initFormula : Formula, role : String) : FormulaStore
    = Store(name, initFormula, role, 0)

  def apply(name : String, initFormula : Formula, role : String, status : Int) : FormulaStore
    = new FormulaStore(name,initFormula, role, status)
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
class FormulaStore(_name : String, _formula : Formula, _role : String, _status : Int){

  def name : String = _name
  def formula : Formula = _formula
  def status : Int = _status
  def role : String = _role

  /**
   *
   * This method returns the flag set for a fully normalized term
   *
   * @return 29, all normalize fields are set
   */
  def normalized : Int = 29

  def newName(nname : String) : FormulaStore = new FormulaStore(nname, formula, _role, _status)
  def newFormula(nformula : Formula) : FormulaStore = new FormulaStore(name, nformula, _role, _status)
  def newStatus(nstatus : Int) : FormulaStore = new FormulaStore(_name, _formula, _role, nstatus)
  def newRole(nrole : String) : FormulaStore = new FormulaStore(_name, _formula, nrole, _status)
}