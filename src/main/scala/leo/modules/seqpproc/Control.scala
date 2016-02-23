package leo
package modules.seqpproc

import leo.datastructures.{Clause, Literal, Position, Term}
import Literal.Side

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
    var results: Set[ClauseWrapper] = Set()

    val withClause = withWrapper.cl
    val intoClause = intoWrapper.cl

    val withConfigurationIt = withConfigurationIterator(Clause.maxOf(withClause))
    while (withConfigurationIt.hasNext) {
      val (withIndex, withLit, withSide) = withConfigurationIt.next()
      val withTerm = if (withSide) withLit.left else withLit.right

      val intoConfigurationIt = intoConfigurationIterator(Clause.maxOf(intoClause))
      while (intoConfigurationIt.hasNext) {
        val (intoIndex, intoLit, intoSide, intoPos, intoTerm) = intoConfigurationIt.next()

        if (!intoTerm.isVariable && leo.modules.calculus.mayUnify(withTerm, intoTerm)) {
          val newCl = OrderedParamod2(withClause, withIndex, withSide,
            intoClause, intoIndex, intoSide, intoPos, intoTerm)

          val newClWrapper = ClauseWrapper(newCl, InferredFrom(OrderedParamod2, Set(withWrapper, intoWrapper)))

          results = results + newClWrapper
        }

      }
    }

    results
  }

  type LiteralIndex = Int
  type WithConfiguration = (LiteralIndex, Literal, Side)
  type Subterm = Term
  type IntoConfiguration = (LiteralIndex, Literal, Side, Position, Subterm)

  final private def withConfigurationIterator(maximalLiterals: Seq[Literal]): Iterator[WithConfiguration] = new Iterator[WithConfiguration] {
    import Literal.{leftSide, rightSide}

    var litIndex = 0
    var lits = maximalLiterals
    var side = leftSide

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
        if (lits.head.oriented || side == rightSide) {
          litIndex += 1
          lits = lits.tail
          side = leftSide
        } else {
          side = rightSide
        }
        res
      } else {
        throw new NoSuchElementException
      }
    }
  }


  final private def intoConfigurationIterator(maximalLiterals: Seq[Literal]): Iterator[IntoConfiguration] = new Iterator[IntoConfiguration] {
    import Literal.{leftSide, rightSide, selectSide}
    var litIndex = 0
    var lits = maximalLiterals
    var side = leftSide
    var curSubterms: Set[Term] = null
    var curPositions: Set[Position] = null
    var curPositionIndex = 0

    def hasNext: Boolean = if (lits.isEmpty) false
    else {
      if (curSubterms == null) {
        // first init
        curSubterms = selectSide(lits.head,side).feasibleOccurences.keySet
        curPositions = selectSide(lits.head,side).feasibleOccurences(curSubterms.head)
        true
      } else {
        if (curPositions.isEmpty) {
          curSubterms = curSubterms.tail
          if (curSubterms.isEmpty) {
            if (lits.head.oriented || side == rightSide) {
              lits = lits.tail
              litIndex += 1
              side = leftSide
            } else {
              side = rightSide
            }
            curSubterms = selectSide(lits.head,side).feasibleOccurences.keySet
            curPositions = selectSide(lits.head,side).feasibleOccurences(curSubterms.head)
            hasNext
          } else {
            curPositions = selectSide(lits.head,side).feasibleOccurences(curSubterms.head)
            assert(hasNext)
            true
          }
        } else {
          true
        }
      }
    }

    def next(): IntoConfiguration = {
      if (hasNext) {
        val res = ???
        ???
      } else {
        throw new NoSuchElementException
      }
    }
  }
}

object FactorizationControl {

}
