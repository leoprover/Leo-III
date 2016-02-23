package leo
package modules.seqpproc

import leo.datastructures.{Clause, Literal, Position, Term}
import leo.modules.seqpproc.OrderedParamod2.Side

/**
  * Created by lex on 22.02.16.
  */
object ParamodControl {
  final def paramodSet(cl: ClauseWrapper, withset: Set[ClauseWrapper]): Set[ClauseWrapper] = {
    var results: Set[ClauseWrapper] = Set()
    val withsetIt = withset.iterator

    while (withsetIt.hasNext) {
      val other = withsetIt.next()
      Out.debug(s"Paramod on ${cl.id} and ${other.id}")

      results = results ++ allParamods(cl,other)
    }

    results
  }

  final def allParamods(cl: ClauseWrapper, other: ClauseWrapper): Set[ClauseWrapper] = {

    // Do paramod with cl into other
    val res = allParamods0(cl, other)
    if (cl.id != other.id) {
      // do paramod with other into cl
      res ++ allParamods0(other, cl)
    } else res
  }

  final private def allParamods0(withWrapper: ClauseWrapper, intoWrapper: ClauseWrapper): Set[ClauseWrapper] = {
    val withClause = withWrapper.cl
    val intoClause = intoWrapper.cl

    val withConfigurationIt = withConfigurationIterator(Clause.maxOf(withClause))
    while (withConfigurationIt.hasNext) {
      val withConf = withConfigurationIt.next()

    }
    ???
  }

  type LiteralIndex = Int
  type WithConfiguration = (LiteralIndex, Literal, Side)
  type Subterm = Term
  type IntoConfiguration = (LiteralIndex, Literal, Side, Position, Subterm)

  final private def withConfigurationIterator(maximalLiterals: Seq[Literal]): Iterator[WithConfiguration] = new Iterator[WithConfiguration] {
    var litIndex = 0
    var lits = maximalLiterals
    var side = true

    final def hasNext: Boolean = {
      if (lits.isEmpty) false
      else {
        val hd = lits.head
        if (hd.polarity) true
        else {
          litIndex += 1
          lits = lits.tail
          hasNext
        }
      }
    }

    final def next(): WithConfiguration = {
      if (hasNext) {
        val res = (litIndex, lits.head, side)
        if (lits.head.oriented || !side) {
          litIndex += 1
          lits = lits.tail
        } else {
          side = !side
        }
        res
      } else {
        throw new NoSuchElementException
      }
    }
  }


  final private def intoConfigurationIterator(maximalLiterals: Seq[Literal]): Iterator[IntoConfiguration] = new Iterator[IntoConfiguration] {
    var litIndex = 0
    var lits = maximalLiterals
    var side = true
    var curSubterms: Seq[Term] = null
    var curPositionIndex = 0

    def hasNext: Boolean = if (curSubterms == null) {
      lits.nonEmpty
    } else {

    }

    def next(): IntoConfiguration = {
      if (hasNext) {
        val res = (litIndex, lits.head.left.fe, )
      } else {
        throw new NoSuchElementException
      }
    }
  }
}

object FactorizationControl {

}
