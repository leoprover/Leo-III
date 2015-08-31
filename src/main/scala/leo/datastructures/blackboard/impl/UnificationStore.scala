package leo.datastructures.blackboard
package impl

import leo.datastructures.{Subst, Term}
import leo.modules.calculus.HuetsPreUnification

import scala.collection.concurrent.TrieMap
import scala.collection.mutable

/**
 *
 * Higher-Order unification is an undecidable problem, since there exist infinitly many.
 *
 * This datastructure stores, for each formula store, the unifier applied up to this point between two terms.
 * Agents might query for the next possible unifier, that was not used until now.
 *
 * @author Max Wisniewski
 * @since 5/13/15
 */
object UnificationStore extends DataStore {

  private val openUniMap: TrieMap[FormulaStore, TrieMap[(Term, Term), Iterator[Subst]]] =
    new TrieMap()
  private val doneUniMap: TrieMap[FormulaStore, TrieMap[(Term, Term), Set[Subst]]] =
    new TrieMap()

  /**
   * Returns the next unused unifier for a unification constrained [t1 = t2] = F in f.
   *
   * @param f - The formula store the unification is applied.
   * @param t1 - The left hand side of the equation.
   * @param t2 - The right hand side of the equation.
   * @return Some(subst) to further compute the unification. None if no unifier exists.
   */
  def nextUnifier(f: FormulaStore, t1: Term, t2: Term): Option[Subst] = {
    val t2u =
      openUniMap.getOrElseUpdate(f, new TrieMap())
    val unifier = t2u.getOrElseUpdate((t1, t2), HuetsPreUnification.unify(t1, t2).iterator)
    unifier.synchronized {
      if (unifier.hasNext)
        Some(unifier.next())
      else
        None
    }
  }

  /**
   * Returns the number of applicable unifier for a unification constrained.
   * @return Number of open unifiers or None, if the task was not yet considered.
   */
  def openUnifier(f: FormulaStore, t1: Term, t2: Term): Option[Int] =
    for (
      termMap <- openUniMap.get(f);
      openUnifiers <- termMap.get((t1, t2))
    ) yield openUnifiers.size

  /**
   * Returns the number of already applied unifiers on a unification constrained.
   *
   * @return The number of applied unifiers or None, if the task was not yet considered.
   */
  def appliedUnifierSize(f: FormulaStore, t1: Term, t2: Term): Option[Int] =
    appliedUnifier(f, t1, t2).map(_.size)

  def appliedUnifier(f: FormulaStore, t1: Term, t2: Term): Option[Set[Subst]] =
    for (
      termMap <- doneUniMap.get(f);
      appliedUnifiers <- termMap.get((t1, t2))
    ) yield appliedUnifiers

  private def finishUni(f: FormulaStore, t1: Term, t2: Term, u: Subst): Boolean = true

  private def updateFormula(fo: FormulaStore, fn: FormulaStore) = synchronized {
    openUniMap.remove(fo).foreach { usmap => if (openUniMap.get(fn).isEmpty) openUniMap.put(fn, usmap) }
    doneUniMap.remove(fo).foreach { usmap => if (doneUniMap.get(fn).isEmpty) doneUniMap.put(fn, usmap) }
  }

  //=================================
  //  Blackboard System handling
  //=================================
  override val storedTypes: Seq[DataType] = List(UnifierType, FormulaType)

  override def update(o: Any, n: Any): Boolean = (o, n) match {
    case (fo: FormulaStore, fn: FormulaStore) => // TODO, what should happen, if fo is updated? recalculate all unifier or just move the contents?
      updateFormula(fo, fn)
      false
    case _ => false
  }

  override def insert(n: Any): Boolean = n match {
    case UnifierStore(f, t1, t2, subst) =>
      finishUni(f, t1, t2, subst)
    case _ => false
  }

  override def clear(): Unit = synchronized {
    openUniMap.clear()
    doneUniMap.clear()
  }

  override protected[blackboard] def all(t: DataType): Set[Any] = t match {
    case FormulaType => openUniMap.keySet.toSet // Important, since mutable
    case UnifierType => // Get all inforamtion from the unifier map.
      openUniMap.flatMap { case (f: FormulaStore, t2u: mutable.Map[(Term, Term), Iterator[Subst]]) =>
        t2u.flatMap { case ((t1, t2), usmap) => usmap.map(subst => UnifierStore(f, t1, t2, subst)) }
      }.toSet
  }

  override def delete(d: Any): Unit = d match {
    case f: FormulaStore => openUniMap.remove(f)
    case _ => ()
  }
}

/**
 * Type declaration for all unifier data handled by this store.
 */
case object UnifierType extends DataType {}

class UnifierStore(val f: FormulaStore, val t1: Term, val t2: Term, val u: Subst) {}

/**
 * Represents the the data of the UnificationStore in the agent system.
 */
object UnifierStore {
  def apply(f: FormulaStore, t1: Term, t2: Term, subst: Subst) = new UnifierStore(f, t1, t2, subst)

  /**
   * Deconstructs a UnifierStore(FormulaStore, Term, Term)
   */
  def unapply(a: Any): Option[(FormulaStore, Term, Term, Subst)] = a match {
    case us: UnifierStore => Some((us.f, us.t1, us.t2, us.u))
    case _ => None
  }
}