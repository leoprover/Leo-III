package leo.datastructures.blackboard
package impl

import java.util.concurrent.atomic.AtomicInteger

import leo.datastructures.{Subst, Term}
import leo.modules.calculus.HuetsPreUnification

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

  private val openUniMap : mutable.Map[FormulaStore, mutable.Map[(Term,Term),Iterator[Subst]]] = new mutable.HashMap[FormulaStore, mutable.Map[(Term,Term),Iterator[Subst]]]()
  private val doneUniMap : mutable.Map[FormulaStore, mutable.Map[(Term,Term), Set[Subst]]] = new mutable.HashMap[FormulaStore, mutable.Map[(Term,Term),Set[Subst]]]()
  val debugCounter : AtomicInteger = new AtomicInteger(0)
  /**
   * Returns the next unused unifier for a unification constrained [t1 = t2] = F in f.
   *
   * @param f - The formula store the unification is applied.
   * @param t1 - The left hand side of the equation.
   * @param t2 - The right hand side of the equation.
   * @return Some(subst) to further compute the unification. None if no unifier exists.
   */
  def nextUnifier(f: FormulaStore, t1: Term, t2: Term) : Option[Subst] = synchronized {   // TODO better organization of synchronized
    // Obtein the map to the formulastore, and initialize, if not existant.
    val t2u : mutable.Map[(Term,Term),Iterator[Subst]] = openUniMap.get(f).getOrElse({val nm = new mutable.HashMap[(Term,Term),Iterator[Subst]](); openUniMap.put(f,nm); nm})
    t2u.get((t1,t2)) match {
      case Some(us) if us.hasNext =>
//        println(s"######One more unifier for ${t1.pretty} and ${t2.pretty}")
        Some(us.next())
      case Some(_) =>
//        println(s"##### no more unifier for ${t1.pretty} and ${t2.pretty}")
        None
      case None     => // Create new
        val i = debugCounter.incrementAndGet()
//        println(s"###### create unification task $i for \n ${t1.pretty}\n${t2.pretty}")
//<<<<<<< HEAD
        val us = HuetsPreUnification.unify(t1,t2).iterator
//        println(s"###### unify $i")
        t2u.put((t1,t2), us)
//        println(s"###### put $i")
        if (us.hasNext) {
          val res = us.next()
//          println(s"###### unification result $i: ${res}")
          Some(res)
        } else {
          None
        }

//=======
//        val us = HuetsPreUnification.unify(t1,t2)
////        println(s"###### unify $i")
//        t2u.put((t1,t2), us)
////        println(s"###### put $i")
//        val res = us.headOption
////        println(s"###### unification result $i: ${res}")
//        res
//>>>>>>> master
    }
  }

  /**
   * Returns the number of applicable unifier for a unification constrained.
   * @return Number of open unifiers or None, if the task was not yet considered.
   */
  def openUnifier(f : FormulaStore, t1 : Term, t2 : Term) : Option[Int] = synchronized(openUniMap.get(f).fold(None : Option[Int])(_.get((t1,t2)).map(_.size)))

  /**
   * Returns the number of already applied unifiers on a unification constrained.
   *
   * @return The number of applied unifiers or None, if the task was not yet considered.
   */
  def appliedUnifierSize(f : FormulaStore, t1 : Term, t2 : Term) : Option[Int] = synchronized(doneUniMap.get(f).fold(None : Option[Int])(_.get((t1,t2)).map(_.size)))

  def appliedUnifier(f : FormulaStore, t1 : Term, t2 : Term) : Option[Set[Subst]] = synchronized(doneUniMap.get(f).fold(None : Option[Set[Subst]])(_.get((t1,t2))))

  private def finishUni(f : FormulaStore, t1 : Term, t2 : Term, u : Subst) : Boolean= true
//    synchronized {
//    openUniMap.get(f).fold(false){usmap => usmap.get((t1,t2)).fold(false){it => it.headOption.fold(false){h =>
//      if (h==u) {
//        usmap.update((t1,t2), it.tail)
//        true
//      } else {
//        false
//      }
//    }}}}

  private def updateFormula(fo : FormulaStore, fn : FormulaStore) = synchronized {
    openUniMap.remove(fo).map{usmap => if(openUniMap.get(fn).isEmpty) openUniMap.put(fn, usmap)}
    doneUniMap.remove(fo).map{usmap => if(doneUniMap.get(fn).isEmpty) doneUniMap.put(fn, usmap)}
  }

  //=================================
  //  Blackboard System handling
  //=================================
  override val storedTypes: Seq[DataType] = List(UnifierType, FormulaType)
  override def update(o: Any, n: Any): Boolean = (o,n) match {
    case (fo : FormulaStore, fn : FormulaStore) => // TODO, what should happen, if fo is updated? recalculate all unifier or just move the contents?
      updateFormula(fo, fn)
      false
    case _ => false
  }
  override def insert(n: Any): Boolean = n match {
    case UnifierStore(f, t1, t2, subst) =>
      finishUni(f,t1,t2,subst)
    case _  => false
  }
  override def clear(): Unit = synchronized{
    openUniMap.clear()
    doneUniMap.clear()
  }
  override protected[blackboard] def all(t: DataType): Set[Any] = t match {
    case FormulaType => openUniMap.keySet.toSet   // Important, since mutable
    case UnifierType => // Get all inforamtion from the unifier map.
      openUniMap.flatMap{case (f : FormulaStore, t2u : mutable.Map[(Term,Term), Iterator[Subst]]) =>
        t2u.flatMap{case ((t1,t2),usmap) => usmap.map(subst => UnifierStore(f,t1,t2,subst))}
      }.toSet
  }

  override def delete(d: Any): Unit = d match {
    case f : FormulaStore => openUniMap.remove(f)
    case _ => ()
  }
}

/**
 * Type declaration for all unifier data handled by this store.
 */
case object UnifierType extends DataType {}

class UnifierStore(val f : FormulaStore, val t1 : Term, val t2 : Term, val u : Subst) {}

/**
 * Represents the the data of the UnificationStore in the agent system.
 */
object UnifierStore {
  def apply(f: FormulaStore, t1 : Term, t2 : Term, subst : Subst)= new UnifierStore(f,t1,t2, subst)

  /**
   * Deconstructs a UnifierStore(FormulaStore, Term, Term)
   */
  def unapply(a : Any) : Option[(FormulaStore,Term,Term, Subst)] = a match {
    case us : UnifierStore => Some((us.f, us.t1, us.t2, us.u))
    case _ => None
  }
}