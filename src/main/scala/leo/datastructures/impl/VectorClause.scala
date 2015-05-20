package leo.datastructures.impl

import leo.datastructures._

/**
 * Preliminary implementation of clauses using indexed linear sequences (vectors).
 *
 * @author Alexander Steen
 * @since 23.11.2014
 */
abstract sealed class VectorClause extends Clause {
  lazy val flexHeadLits: Set[Literal] = lits.filter(_.flexHead).toSet
  lazy val uniLits: Set[Literal] = lits.filter(_.isUni).toSet
  lazy val freeVars: Set[Term] = lits.map(_.term.freeVars).flatten.toSet
  lazy val eqLits: Set[Literal] = lits.filter(_.isEq).toSet
}

object VectorClause {
  private var clauseCounter : Int = 0

  def mkClause(lits: Iterable[Literal], implicitBindings: Seq[Type], origin: ClauseOrigin): Clause = {
    clauseCounter += 1
    new VectorClause0(lits, origin, implicitBindings, clauseCounter)
  }


  def lastClauseId = clauseCounter

  private class VectorClause0(literals: Iterable[Literal], val origin: ClauseOrigin, val implicitBindings: Seq[Type], val id: Int) extends VectorClause {
    lazy val lits = literals.toVector.sorted
  }
}
