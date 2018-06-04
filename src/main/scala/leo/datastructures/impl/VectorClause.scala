package leo.datastructures.impl

import leo.datastructures._


/**
  * Implementation of clauses using indexed linear sequences (vectors).
  *
  * @param lits The literals that this clause consists of.
  * @param origin The origin of the clause
  *               (i.e. from which data source it was constructed).
  * @param fvs The free term variables of the clause.
  * @param tyFvs The free type variable of the clause.
  *
  * @author Alexander Steen
  * @since 23.11.2014
  */
 protected[datastructures] final case class VectorClause(lits: Seq[Literal],
                         origin: ClauseOrigin,
                         fvs: Seq[(Int, Type)],
                         tyFvs: Seq[Int]) extends Clause {

  /** Convenience constructor if free (type) variables of clause are not known
    * or should be determined automatically. Consider using the primary constructor
    * to save computation time. */
  def this(lits: Seq[Literal], origin: ClauseOrigin) = this(lits, origin, null, null)

  /** The types of the implicitly universally quantified variables. */
  val implicitlyBound: Seq[(Int, Type)] = {
    if (fvs == null) {
      lits.flatMap(_.fv).distinct.toVector.sortWith {case ((i1, _), (i2, _)) => i1 > i2}
    } else {
      fvs.distinct.toVector.sortWith {case ((i1, _), (i2, _)) => i1 > i2}
    }
  }

  val typeVars: Seq[Int] = {
    if (tyFvs == null)
      lits.flatMap(_.tyFV).distinct.toVector.sortWith{case (x,y) => x > y}
    else
      tyFvs.distinct.toVector.sortWith{case (x,y) => x > y}
  }


  /** Those literals in `lits` that are positive. */
  @inline val posLits: Seq[Literal] = lits.filter(_.polarity)
  /** Those literals in `lits` that are negative. */
  @inline val negLits: Seq[Literal] = lits.filter(!_.polarity)

  private[this] var maxLits0: Seq[Literal] = _
  /** Those literals in `lits` that are maximal wrt to the underlying clause.
    * IMPORTANT: This method caches the result after the first call. So if you
    * need maximal literals wrt multiple signatures use [[leo.datastructures.Literal.maxOf()]]
    * instead. */
  def maxLits(implicit sig: Signature): Seq[Literal] = {
    if (maxLits0 == null) {
      maxLits0 =  Literal.maxOf(lits)(sig)
      maxLits0
    } else maxLits0
  }
}

object VectorClause {

  /** Primary constructor for clauses.
    *
    * @param lits The literals that this clause consists of.
    * @param origin The origin of the clause
    *               (i.e. from which data source it was constructed).
    * @param fvs The free term variables of the clause.
    * @param tyFvs The free type variable of the clause.
    */
  @inline final def mkClause(lits: Iterable[Literal], origin: ClauseOrigin,
                     fvs: Seq[(Int, Type)],
                     tyFvs: Seq[Int]): Clause = {
    new VectorClause(lits.toVector, origin, fvs, tyFvs)
  }

  /** Convenience constructor if free (type) variables of clause are not known
    * or should be determined automatically. Consider using the primary constructor
    * to save computation time.
    *
    * @param lits The literals that this clause consists of.
    * @param origin The origin of the clause
    *               (i.e. from which data source it was constructed).
    */
  @inline final def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause = {
    new VectorClause(lits.toVector, origin)
  }
}
