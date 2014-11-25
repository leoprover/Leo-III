package leo.datastructures.impl

import leo.datastructures.{Literal, Clause, ClauseOrigin}

/**
 * Created by lex on 23.11.14.
 */
abstract sealed class VectorClause extends Clause

object VectorClause {
  private var clauseCounter : Int = 0

  def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause = {
    clauseCounter += 1
    new VectorClause0(lits, origin, clauseCounter)
  }

  private class VectorClause0(literals: Iterable[Literal], val origin: ClauseOrigin, val id: Int) extends VectorClause {
    def lits = literals.toVector.sorted
  }
}
