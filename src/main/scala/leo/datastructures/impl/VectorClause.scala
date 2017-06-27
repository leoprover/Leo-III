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

  private var maxLits0: Seq[Literal] = _
  /** Those literals in `lits` that are maximal wrt to the underlying clause.
    * IMPORTANT: This method caches the result after the first call. So if you
    * need maximal literals wrt multiple signatures use [[leo.datastructures.Literal.maxOf()]]
    * instead. */
  final def maxLits(implicit sig: Signature): Seq[Literal] = {
    if (maxLits0 == null) {
      maxLits0 =  Literal.maxOf(lits)(sig)
      maxLits0
    } else maxLits0
  }
}

object VectorClause {
  final def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause = {
    VectorClause(lits.toVector, origin)
  }
}
