package leo.modules.indexing

import leo.datastructures._
import leo.modules.myAssert

import scala.collection.mutable

/**
  * {{{
  *                /\
  * term          /  \
  * level        /    \
  *             /      \
  *            /        \
  * (sub)term  ..  t  ...
  *               / \
  *              /   \
  * clause      /     \
  * level      /       \
  * t occurs in .. cl ..
  *               / \
  *              /   \
  *             /     \
  * at position ...p...
  *
  *
  * }}}
  */
class STIndex {
  type ClauseEntry = mutable.Map[Clause, ClausePositionSet]
  private final val termToOccurrenceMap: mutable.Map[Term, ClauseEntry] = mutable.Map.empty

  private final val clauseToSubtermMap =
    new mutable.HashMap[Clause, mutable.Set[Term]] with mutable.MultiMap[Clause, Term]

  final def addClause(cl: Clause,
                      positiveOnly: Boolean,
                      maxOnly: Boolean,
                      withSubterms:  Boolean)
                     (implicit sig: Signature): Unit = {
    var lits = cl.lits
    val maxLits = if (maxOnly) Literal.maxOf(cl.lits)(sig) else Seq.empty

    var litIdx = 0
    while (lits.nonEmpty) {
      val lit = lits.head

      if (!positiveOnly || lit.polarity) { // only select positive if requested
        if (!maxOnly || maxLits.contains(lit)) { // only select maximal if requested
          // process left
          val left = lit.left
          insert(cl, litIdx, Literal.leftSide, left)
          if (withSubterms) insertSubterms(cl, litIdx, Literal.leftSide, left)

          if (!maxOnly || (lit.equational && !lit.oriented)) { // only select right side if not oriented
                                                               // ... or maxOnly is false
            // process right
            val right = lit.right
            insert(cl, litIdx, Literal.rightSide, right)
            if (withSubterms) insertSubterms(cl, litIdx, Literal.rightSide, right)
          }
        }
      }

      lits = lits.tail
      litIdx = litIdx + 1
    }
  }
  private final def insert0(cl: Clause, idx: Int, side: Literal.Side, t: Term, pos: Position): Unit = {
    if (termToOccurrenceMap.contains(t)) {
      val clauseEntry = termToOccurrenceMap(t)
      if (clauseEntry.contains(cl)) {
        val occ = clauseEntry(cl)
        occ.insert(ClausePosition(cl, idx, side, pos))
      } else {
        val newSet = ClausePositionSet.empty
        newSet.insert(ClausePosition(cl, idx, side, pos))
        clauseEntry += (cl -> newSet)
      }
    } else {
      val newSet = ClausePositionSet.empty
      newSet.insert(ClausePosition(cl, idx, side, pos))
      termToOccurrenceMap += (t -> mutable.Map(cl -> newSet))
    }
    clauseToSubtermMap.addBinding(cl, t)
  }
  private final def insert(cl: Clause, idx: Int, side: Literal.Side, t: Term): Unit =
    insert0(cl, idx, side, t, Position.root)
  private final def insertSubterms(cl: Clause, idx: Int, side: Literal.Side, t: Term): Unit = ???

  final def removeClause(cl: Clause): Unit = {
    if (clauseToSubtermMap.contains(cl)) {
      val subterms = clauseToSubtermMap(cl)
      val subTermIt = subterms.iterator
      while (subTermIt.hasNext) {
        val subterm = subTermIt.next()
        myAssert(termToOccurrenceMap.isDefinedAt(subterm))
        val clEntry = termToOccurrenceMap(subterm)
        myAssert(clEntry.isDefinedAt(cl))
        clEntry.remove(cl)
        if (clEntry.isEmpty) {
          termToOccurrenceMap.remove(subterm)
        }
      }
      clauseToSubtermMap.remove(cl)
    }
  }

  final def iterator(): Iterator[Term] = termToOccurrenceMap.keysIterator
  final def occurrences(t: Term): Iterator[ClausePositionSet] = {
    if (termToOccurrenceMap.contains(t))
      termToOccurrenceMap(t).valuesIterator
    else Iterator.empty
  }

}

object STIndex {

}


// input: paramod literal l
// calculuate fingerprint of l: fp(l)
// return all entries {s1,...,sn} compatible with fp(l)