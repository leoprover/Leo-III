package leo.datastructures.impl

import leo.datastructures._


// TODO FVs are computed every time anew (from the literals), could be given as parameter
/**
 * Preliminary implementation of clauses using indexed linear sequences (vectors).
 *
 * @author Alexander Steen
 * @since 23.11.2014
 */
 abstract sealed class VectorClause extends Clause {
  /** The types of the implicitly universally quantified variables. */
  final val implicitlyBound: Seq[(Int, Type)] = {
    val fvs = lits.map(_.fv).fold(Set())((s1,s2) => s1 ++ s2)
    fvs.toSeq.sortWith {case ((i1, _), (i2, _)) => i1 > i2}
  }
  @inline final def maxImplicitlyBound: Int = if (implicitlyBound.isEmpty) 0 else implicitlyBound.head._1

  @inline final lazy val typeVars: Set[Int] = lits.flatMap(_.tyFV).distinct.toSet

  /** Those literals in `lits` that are positive. */
  @inline final lazy val posLits: Seq[Literal] = lits.filter(_.polarity)
  /** Those literals in `lits` that are negative. */
  @inline final lazy val negLits: Seq[Literal] = lits.filter(!_.polarity)
}

object VectorClause {
  final def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause = {
    new VectorClause0(lits, origin)
  }

  private final class VectorClause0(literals: Iterable[Literal], val origin: ClauseOrigin) extends VectorClause {
    lazy val lits = literals.toVector
  }
}
