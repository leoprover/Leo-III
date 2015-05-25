package leo
package agents
package impl

import leo.datastructures.{ClauseAnnotation, Role_Plain}
import leo.datastructures.blackboard._
import leo.modules.normalization.Normalize
import leo.modules.calculus.TrivRule

import scala.collection.mutable

/**
 *
 * <p>
 * Normalization Agent for one normalization function.
 * </p>
 *
 * <p>
 * This Agent should register for formula Adds/Changes and applies Clause Normalization
 * as long as its possible. (Predicate is full filled.
 * </p>
 *
 * @author Max Wisniewski
 * @since 5/14/14
 */
class NormalClauseAgent(norm : Normalize) extends Agent {

  override val name = norm.name + "Agent"

  override def run(t: Task): Result = t match {
    case NormalTask(f) =>
      val fstore = f
      val calc = norm(fstore)
      val ergCl = TrivRule.triv(TrivRule.teqf(calc.clause))

      // If the Result is trivial true, delete the initial clause
      if(TrivRule.teqt(ergCl)) return Result().remove(FormulaType)(fstore)

      // Else check if something happend and update the formula
      val r= if (fstore.clause.cong(ergCl)) {
        val erg =Store(fstore.name, ergCl, fstore.role, calc.context, calc.status, fstore.annotation)
        Out.trace(s"[$name]: : No change in Normalization.\n  ${fstore.pretty}(${fstore.status})\n to\n  ${erg.pretty}(${erg.status}).")
        Result().update(FormulaType)(fstore)(erg)
      } else {
        val erg = Store(ergCl, fstore.role, calc.context, calc.status, ClauseAnnotation(norm, Set(fstore)))
        Out.trace(s"[$name]: : Updated Formula.\n  ${fstore.pretty}\n to\n  ${erg.pretty}.")
        Result().update(FormulaType)(fstore)(erg)
      }
      return r
    case _ => throw new IllegalArgumentException("Executing wrong task.")
  }


  override def toFilter(e: Event): Iterable[Task] = e match {
    case DataEvent(event : FormulaStore, FormulaType) =>
      if (norm.applicable ( event.status ) && !event.clause.isEmpty) {
        List(new NormalTask(event))
      }
      else {
        Nil
      }
    case _ => Nil
  }

  /**
   * Normalization applies to one Formula only and this one is read and written.
   * @param f - Formula to be normalized
   */
  final private case class NormalTask(f : FormulaStore) extends Task {
    override def readSet(): Set[FormulaStore] = Set(f)
    override def writeSet(): Set[FormulaStore] = Set(f)
    override def bid(budget : Double) : Double = math.min(budget / 5,1)
    override lazy val pretty : String =  "NormalizationTask: Normalize " + f.toString + "."
    override val name : String = "Normalization"
  }

}

