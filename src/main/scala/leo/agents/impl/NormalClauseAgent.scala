package leo.agents
package impl

import leo.datastructures.blackboard.{FormulaStore, Blackboard}
import leo.modules.normalization.Normalize

import scala.collection.mutable

object NormalClauseAgent {

  private var simp : Agent = null;
  private var neg : Agent = null;
  private var prenex : Agent = null;
  private var skolem : Agent = null;
  private var defExp : Agent = null;

  import leo.modules.normalization._
  def SimplificationAgent () : Agent = {
    if(simp == null) {
      simp = new NormalClauseAgent(Simplification)
      simp.register()
    }
    simp
  }

  def NegationNormalAgent () : Agent = {
    if(neg == null) {
      neg = new NormalClauseAgent(NegationNormal)
      neg.register()
    }
    neg
  }

  def PrenexAgent () : Agent =  {
    if(prenex == null) {
      prenex = new NormalClauseAgent(PrenexNormal)
      prenex.register()
    }
    prenex
  }

  def SkolemAgent () : Agent =  {
    if(skolem == null) {
      skolem = new NormalClauseAgent(Skolemization)
      skolem.register()
    }
    skolem
  }

  def DefExpansionAgent () : Agent =  {
    if(defExp == null) {
      defExp = new NormalClauseAgent(DefExpansion)
      defExp.register()
    }
    defExp
  }
}

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
class NormalClauseAgent(norm : Normalize) extends AbstractAgent {

  override def run(t: Task): Result = t match {
    case t1: NormalTask =>
      val fstore = t1.get()
      val erg = norm.normalize(fstore.simpleFormula)
      if (fstore.simpleFormula == erg) {
        println(norm.getClass.getName() + " : No change in Normalization.")
        return new StdResult(Set.empty, Map((fstore, fstore.newStatus(norm.markStatus(fstore.status)))), Set.empty)
      } else {
        println(norm.getClass.getName() + " : Updated '" + fstore.simpleFormula.pretty + "' to '" + erg.pretty + "'.")
        return new StdResult(Set.empty, Map((fstore, fstore.newFormula(erg).newStatus(norm.markStatus(fstore.status)))), Set.empty)
      }
    case _ => throw new IllegalArgumentException("Executing wrong task.")
  }

  override protected def toFilter(event: FormulaStore): Iterable[Task] = event.formula match {
    case Left(f) => if (norm.applicable(event.simpleFormula, event.status)) List(new NormalTask(event)) else Nil
    case Right(_) => Nil
  }
}

/**
 * Normalization applies to one Formula only and this one is read and written.
 * @param f - Formula to be normalized
 */
class NormalTask(f : FormulaStore) extends Task {

  def get() = f

  override def readSet(): Set[FormulaStore] = Set(f)
  override def writeSet(): Set[FormulaStore] = Set(f)

  override def bid(budget : Double) : Double = 1
}
