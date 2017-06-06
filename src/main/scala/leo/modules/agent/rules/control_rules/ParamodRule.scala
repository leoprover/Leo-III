package leo.modules.agent.rules
package control_rules

import java.util.concurrent.atomic.AtomicInteger

import leo.modules.control.Control
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard._
import leo.modules.GeneralState
import leo.modules.control.inferenceControl.ParamodControl

import scala.collection.mutable

/**
  * Applies paramodulation for a newly selected formula
  * with all previously processed formulas.
  */
class ParamodRule(inType : DataType[AnnotatedClause],
                  unifyType : DataType[AnnotatedClause],
                  doneType : DataType[(Long, AnnotatedClause)],
                  noUnifyType : DataType[AnnotatedClause])
                 (processed : ProcessedSet)(implicit state : GeneralState[AnnotatedClause]) extends Rule {
  override val name: String = "paramod"

  implicit val sig : Signature = state.signature
  override val observedDataStructures: Seq[DataStore] = Seq(processed)
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(unifyType, doneType, noUnifyType)
  override final val moving: Boolean = false

  override def canApply(r: Delta): Seq[Hint] = {
    // All new selected clauses
    val ins = r.inserts(inType).iterator
    val maxID = processed.maxID
    val p = processed.get.toSeq
    val anz = (p.size + 1)  // Amount of Paramodulations
    var res: Seq[Hint] = Seq()
    //
    while (ins.hasNext) {
      val c = ins.next()
      res = new ParamodHint(c, c) +: res
      // Paramod not all, but parallel
      res = p.map{aClause => new ParamodHint(c, aClause)} ++ res
      res = new CopyHint((maxID, c), doneType) +: res
      ParamodHint.createHints(c, anz) // Anz is always > 0, since c,c is always considered
    }
    res
  }

  // Paramodulation of all processed clauses with a new one
  class ParamodHint(sClause: AnnotatedClause, aClause: AnnotatedClause) extends Hint {

    // Test for previously generation
    private val alreadyDone : mutable.Set[(Long, Long)] = mutable.Set[(Long,Long)]()

    override def apply(): Delta = {
      val id1 = sClause.id
      val id2 = aClause.id

      val r = Result()
      val it = ParamodControl.allParamods(sClause, aClause).iterator
      //    if(it.hasNext){
      leo.Out.debug(s"[Paramod] Apply to\n   ${sClause.pretty(sig)}\n   ${aClause.pretty(sig)}")
      //    }
      if(ParamodHint.freeHint(sClause)){
        leo.Out.debug(s"[Paramod] Last Paramod. Release lock on ${sClause.pretty(sig)}")
        r.remove(LockType(inType))(sClause)
      }

      //      r.remove(LockType(inType))(sClause)
      while (it.hasNext) {
        val c = Control.liftEq(it.next)
        if(c.cl.lits.exists(l => l.uni)) {
          leo.Out.debug(s"[Paramod] From [$id1],[$id2] to unify ${c.pretty(sig)}")
          r.insert(unifyType)(c)
        } else {
          var newclauses = Control.cnf(c)
          newclauses = newclauses.map(cw => Control.simp(Control.liftEq(cw)))
          var newIt = newclauses.iterator
          while (newIt.hasNext) {
            val nc = newIt.next
            leo.Out.debug(s"[Paramod] From [$id1],[$id2] no unification ${c.pretty(sig)}")
            r.insert(noUnifyType)(nc)
          }
        }
      }
      r
    }

    override lazy val read: Map[DataType[Any], Set[Any]] = Map(inType -> Set(sClause))
    override lazy val write: Map[DataType[Any], Set[Any]] = Map()
  }
}

protected[control_rules] object ParamodHint {
  private val toDoPerClause : mutable.Map[AnnotatedClause, AtomicInteger] = new mutable.HashMap[AnnotatedClause,AtomicInteger]()
  def createHints(c : AnnotatedClause, n : Int) = {
    toDoPerClause.put(c, new AtomicInteger(n))
  }
  def isFree(c : AnnotatedClause) : Boolean = toDoPerClause.get(c).fold(true)(ai => ai.get <= 0)

  /**
    * Returns true, iff the hint is freed in this moment
    */
  def freeHint(c : AnnotatedClause) : Boolean = {
    toDoPerClause.get(c) match {
      case Some(ai) =>
        val newC = ai.decrementAndGet()
        if(newC == 0) {
          toDoPerClause.remove(c)
          true
        } else {
          false
        }
      case _ => false
    }

  }


}

class ParamodDoneRule(from : DataType[(Long, AnnotatedClause)],
                      unifyType : DataType[AnnotatedClause],
                      blockingType : DataType[AnnotatedClause],
                      noUnifyType : DataType[AnnotatedClause],
                      done : TypedSet[(Long, AnnotatedClause)],
                      generate : TypedSet[AnnotatedClause])
                     (processed : ProcessedSet)
                     (implicit state : GeneralState[AnnotatedClause]) extends Rule {
  override val name: String = "paramod_done"
  implicit val sig : Signature = state.signature
  override val inTypes: Seq[DataType[Any]] = Seq(from, blockingType)
  override val moving: Boolean = true
  override val outTypes: Seq[DataType[Any]] = Seq(unifyType)
  override def canApply(r: Delta): Seq[Hint] = synchronized {
    if(generate.isEmpty){
      var res : Set[AnnotatedClause] = Set()
      val elems = done.get(from).filter{case (m,c) => ParamodHint.isFree(c)}
      val doneElems = elems.map{case (m, c) =>
        (processed.getID(c), m,c)
      }

      var doneElemsArray = doneElems.toArray

      // Generate the right instances
      // (active, max, c)
      // if active1 > max2 && active2 > max1 then paramod(c1, c2)
      // => no clause has seen the other during generate
      for(i <- doneElemsArray.indices) {
        // Generate is empty, so delete the element
        for(j <- (i+1) until doneElemsArray.length) {
          val (a1, m1,c1) = doneElemsArray(i)
          val (a2, m2,c2) = doneElemsArray(j)
          if(a1 > m2 && a2 > m1) {
            res = ParamodControl.allParamods(c1, c2) union res
          }
        }
      }
      Seq(new ParamodDoneHint(elems, res))
    } else {
      Seq()
    }
  }

  class ParamodDoneHint(orgClauses : Set[(Long, AnnotatedClause)], resClauses : Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      val r = Result()

      orgClauses foreach (c => r.remove(from)(c))

      val it = resClauses.iterator
      while (it.hasNext) {
        val c = Control.liftEq(it.next)
        if(c.cl.lits.exists(l => l.uni)) {
          leo.Out.debug(s"[Paramod] From " +
            s"${c.annotation.parents.map(c => s"[${c.id}]").mkString(",")} " +
            s"to unify ${c.pretty(state.signature)}")
          r.insert(unifyType)(c)
        } else {
          var newclauses = Control.cnf(c)
          newclauses = newclauses.map(cw => Control.simp(Control.liftEq(cw)))
          var newIt = newclauses.iterator
          while (newIt.hasNext) {
            val nc = newIt.next
            leo.Out.debug(s"[Paramod] From " +
              s"${nc.annotation.parents.map(c => s"[${c.id}]").mkString(",")} " +
              s" no unification ${c.pretty(state.signature)}")
            r.insert(noUnifyType)(nc)
          }
        }
      }
      r
    }
    override def read: Map[DataType[Any], Set[Any]] = Map()
    override def write: Map[DataType[Any], Set[Any]] = Map(from -> orgClauses.asInstanceOf[Set[Any]])
  }
}

