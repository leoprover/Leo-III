package leo.datastructures.blackboard

import java.util.concurrent.atomic.AtomicInteger
import leo.datastructures.internal.terms.Term


object Store {
  private var unnamedFormulas : AtomicInteger = new AtomicInteger(0)

  def apply(name : String, initFormula : Term) : FormulaStore
    = new FormulaStore(name, initFormula, "plain", 0)

  def apply(initFormula : Term, role : String, status : Int) : FormulaStore
    = new FormulaStore("gen_formula_"+unnamedFormulas.incrementAndGet(),initFormula, role, status)

  def apply(initFormula : Term) : FormulaStore
  = Store(initFormula,"plain", 0)

  def apply(initFormula : Term, status : Int) : FormulaStore
    = Store(initFormula, "plain" ,status)

  def apply(initFormula : Term, role : String) : FormulaStore
    = Store(initFormula, role, 0)

  def apply(name : String, initFormula : Term, role : String) : FormulaStore
    = Store(name, initFormula, role, 0)

  def apply(name : String, initFormula : Term, role : String, status : Int) : FormulaStore
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
class FormulaStore(_name : String, _formula : Term, _role : String, _status : Int){

  def name : String = _name
  def formula : Term = _formula
  def status : Int = _status
  def role : String = _role


  def newName(nname : String) : FormulaStore = new FormulaStore(nname, formula, _role, _status)
  def newFormula(nformula : Term) : FormulaStore = new FormulaStore(name, nformula, _role, _status)
  def newStatus(nstatus : Int) : FormulaStore = new FormulaStore(_name, _formula, _role, nstatus)
  def newRole(nrole : String) : FormulaStore = new FormulaStore(_name, _formula, nrole, _status)
}