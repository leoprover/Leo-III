package leo.datastructures.impl

import leo.datastructures._


// TODO FVs are computed every time anew (from the literals), could be given as parameter
/**
 * Preliminary implementation of clauses using indexed linear sequences (vectors).
 *
 * @author Alexander Steen
 * @since 23.11.2014
 */
 case class VectorClause(lits: Seq[Literal], origin: ClauseOrigin) extends Clause {
  /** The types of the implicitly universally quantified variables. */
  final lazy val implicitlyBound: Seq[(Int, Type)] = {
    val fvs = lits.map(_.fv).fold(Set())((s1,s2) => s1 ++ s2)
    fvs.toVector.sortWith {case ((i1, _), (i2, _)) => i1 > i2}
  }
  @inline final def maxImplicitlyBound: Int = if (implicitlyBound.isEmpty) 0 else implicitlyBound.head._1
  @inline final def maxTypeVar: Int = if (typeVars.isEmpty) 0 else typeVars.head

  @inline final lazy val typeVars: Seq[Int] = lits.flatMap(_.tyFV).distinct.sortWith{case (x,y) => x > y}

  /** Those literals in `lits` that are positive. */
  @inline final lazy val posLits: Seq[Literal] = lits.filter(_.polarity)
  /** Those literals in `lits` that are negative. */
  @inline final lazy val negLits: Seq[Literal] = lits.filter(!_.polarity)
}

object VectorClause {
  final def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause = {
    VectorClause(lits.toVector, origin)
  }
}
