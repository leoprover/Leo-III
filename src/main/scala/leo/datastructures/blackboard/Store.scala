package leo
package datastructures
package blackboard

import java.util.concurrent.atomic.AtomicInteger

import leo.datastructures.{Role_Plain, Role, Clause, Pretty}
import leo.datastructures.context.Context


object Store {
  protected[blackboard] var unnamedFormulas : AtomicInteger = new AtomicInteger(0)

  def apply(name : String, initClause : Clause, context : Context) : FormulaStore
    = new FormulaStore(name, initClause, Role_Plain, 0, context, List(), "initial")

  def apply(initClause : Clause, role : Role, status : Int, context : Context) : FormulaStore
    = new FormulaStore("gen_formula_"+unnamedFormulas.incrementAndGet(), initClause, role, status, context : Context, List(), "initial")

  def apply(initClause : Clause, context : Context) : FormulaStore
  = Store(initClause,Role_Plain, 0, context)

  def apply(initClause : Clause, status : Int, context : Context) : FormulaStore
    = Store(initClause, Role_Plain ,status, context)

  def apply(initClause : Clause, role : Role, context : Context) : FormulaStore
    = Store(initClause, role, 0, context)

  def apply(name : String, initClause : Clause, role : Role, context : Context) : FormulaStore
    = Store(name, initClause, role, 0, context)

  def apply(name : String, initClause : Clause, role : Role, status : Int, context : Context) : FormulaStore
    = new FormulaStore(name, initClause, role, status, context, List(), "initial")
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
 *     <tr><th>6.</th><th> Term should be proven as a conjecture in an external prover</th></tr>
 *   </tbody>
 * </table>
 *
 */
class FormulaStore(val name : String, val clause : Clause, val role : Role, val status : Int, val context : Context, val origin : List[FormulaStore], val reason : String)
  extends Pretty with Ordered[FormulaStore] with HasCongruence[FormulaStore] {

  /**
   *
   * This method returns the flag set for a fully normalized term
   *
   * (ATM only the first two, therefor 1 + 4 = 5)
   *
   * @return true, if normalized
   */
  def normalized : Boolean = (status & 3)== 3

  def newName(nname : String) : FormulaStore = new FormulaStore(nname, clause, role, status, context, origin, reason)
  def newClause(nclause : Clause) : FormulaStore = new FormulaStore(name,nclause, role, status, context, origin, reason)
  def newStatus(nstatus : Int) : FormulaStore = new FormulaStore(name, clause, role, nstatus,context,origin, reason)
  def newRole(nrole : Role) : FormulaStore = new FormulaStore(name, clause, nrole, status,context,origin, reason)
  def newOrigin(norigin : List[FormulaStore], nreason : String) = new FormulaStore(name, clause, role, status, context, norigin, nreason)
  def newContext(ncontext : Context) : FormulaStore = new FormulaStore(name, clause, role, status, ncontext, origin, reason)

  def randomName() : FormulaStore = new FormulaStore("gen_formula_"+Store.unnamedFormulas.incrementAndGet(), clause, role, status, context, origin, reason)

  lazy val pretty : String = "leo("+name+","+role.pretty+",("+clause.pretty+"), contextID="+context.contextID+")."

  override lazy val toString : String = "leo("+name+","+role.pretty+",("+clause.pretty+"), contextID="+context.contextID+")."

  def compare(that: FormulaStore): Int = this.clause compare that.clause

  /** Returns `true` iff `this` is congruent to `that`. */
  override def cong(that: FormulaStore): Boolean = clause.cong(that.clause)
}