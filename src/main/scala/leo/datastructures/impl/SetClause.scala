package leo.datastructures.impl

import leo.datastructures.{Literal, Clause, ClauseOrigin}

/**
 * Created by lex on 23.11.14.
 */
abstract sealed class SetClause extends Clause {

  def weight = ???

}

object SetClause {
  private var clauseCounter : Int = 0


  def mkClause(lits: Seq[Literal], origin: ClauseOrigin): Clause = {
    clauseCounter += 1

    SetClause0(lits, origin, clauseCounter)
  }

  private case class SetClause0(lits: Seq[Literal], origin: ClauseOrigin, id: Int) extends SetClause
}
