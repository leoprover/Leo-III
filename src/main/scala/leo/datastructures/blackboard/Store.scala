package leo
package datastructures
package blackboard

import java.util.concurrent.atomic.AtomicInteger

import leo.datastructures.context.Context


object Store {
  protected[blackboard] var unnamedFormulas : AtomicInteger = new AtomicInteger(0)

  def apply(cl: Clause, role: Role, context: Context, annotation: ClauseAnnotation = ClauseAnnotation.NoAnnotation): AnnotatedClause
  = new AnnotatedClause("gen_formula_"+unnamedFormulas.incrementAndGet(), cl, TimeStamp(), role, context, annotation)

  def apply(name: String, cl: Clause, role: Role, context: Context, annotation: ClauseAnnotation): AnnotatedClause
  = new AnnotatedClause(name, cl, TimeStamp(), role, context, annotation)

  def apply(cl: Clause, created : TimeStamp, role: Role, context: Context, annotation: ClauseAnnotation): AnnotatedClause
  = new AnnotatedClause("gen_formula_"+unnamedFormulas.incrementAndGet(), cl, created, role, context, annotation)

}

/**
 * Class to Store Formula Information.
 *
 *
 */
class AnnotatedClause(val name : String, val clause : Clause, val created : TimeStamp, val role : Role, val context : Context, val annotation : ClauseAnnotation)
  extends ClauseProxy with Ordered[AnnotatedClause] {//} with HasCongruence[AnnotatedClause] {


  def id = name
  def cl = clause
  def properties = ClauseAnnotation.PropNoProp

//  lazy val pretty : String = "leo("+name+","+role.pretty+",("+clause.pretty+"), status="+status+")."

  override lazy val toString : String = "leo("+name+","+role.pretty+",("+clause.pretty+"))."

  def compare(that: AnnotatedClause): Int = this.clause compare that.clause

  /** Returns `true` iff `this` is congruent to `that`. */
//  override def cong(that: AnnotatedClause): Boolean = clause.cong(that.clause) && context.contextID == that.context.contextID

  override def equals(o : Any) : Boolean = o match {
    case fo : AnnotatedClause => (this.clause cong fo.clause) && (this.role == fo.role) && (context.contextID == fo.context.contextID)
    case _ => false
  }

  override def hashCode() : Int = this.clause.hashCode() ^ this.role.hashCode() ^this.context.contextID.hashCode()
}