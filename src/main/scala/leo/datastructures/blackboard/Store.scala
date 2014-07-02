package leo.datastructures.blackboard

import leo.datastructures.internal.{ Term => Formula }
import java.util.concurrent.atomic.AtomicInteger


object Store {
  private var unnamedFormulas : AtomicInteger = new AtomicInteger(0)

  def apply(name : String, initFormula : Formula) : FormulaStore
    = new FormulaStore(name, initFormula, 0)

  def apply(initFormula : Formula) : FormulaStore
    = new FormulaStore("gen_formula_"+unnamedFormulas.incrementAndGet(),initFormula,0)

  def apply(initFormula : Formula, status : Int) : FormulaStore
    = new FormulaStore("gen_formula_"+unnamedFormulas.incrementAndGet(),initFormula, status)
  def apply(name : String, initFormula : Formula, status : Int) : FormulaStore
    = new FormulaStore(name,initFormula, status)
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
 *     <tr><th>2.</th><th> Term is in Negation Normal Form </th></tr>
 *     <tr><th>3.</th><th> Term is Skolemized </th></tr>
 *     <tr><th>4.</th><th> Term is in PrenexNormalform </th></tr>
 *   </tbody>
 * </table>
 *
 */
class FormulaStore(_name : String, _formula : Formula, _status : Int){

  def name : String = _name
  def formula : Formula = _formula
  def status : Int = _status


  def newName(nname : String) : FormulaStore = new FormulaStore(nname, formula, _status)
  def newFormula(nformula : Formula) : FormulaStore = new FormulaStore(name, nformula, _status)
  def newStatus(nstatus : Int) : FormulaStore = new FormulaStore(_name, _formula, nstatus)
}