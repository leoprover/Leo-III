package leo.datastructures

import leo.Configuration

import scala.collection.LinearSeq


/**
 * Clause interface, the companion object `Clause` offers several constructors methods.
 * The `id` of a clause is unique and is monotonously increasing.
 *
 * @author Alexander Steen
 * @since 07.11.2014
 */
trait Clause extends Ordered[Clause] with Pretty with HasCongruence[Clause] {
  /** The unique, increasing clause number. */
  def id: Int
  /** The underlying sequence of literals. */
  def lits: Seq[Literal]
  /** The types of the implicitly universally quantified variables. */
  def implicitlyBound: LinearSeq[Type]
  /** The source from where the clause was created, See `ClauseOrigin`. */
  def origin: ClauseOrigin

  // Further properties
  /** Those literals in `lits` that are positive. */
  def posLits: Seq[Literal]
  /** Those literals in `lits` that are negative. */
  def negLits: Seq[Literal]

  /** True iff this clause is horn. */
  @inline final val horn: Boolean = posLits.length <= 1
  /** True iff this clause is a unit clause. */
  @inline final val unit: Boolean = lits.length == 1
  /** True iff this clause is a demodulator. */
  @inline final val demodulator: Boolean = posLits.length == 1 && negLits.isEmpty
  /** True iff this clause is a rewrite rule. */
  @inline final val rewriteRule: Boolean = demodulator && posLits.head.oriented
  /** True iff this clause is ground. */
  @inline final val ground: Boolean = lits.view.forall(_.ground)
  /** True iff this clause is purely positive. i.e.
    * if all literals are positive. */
  @inline final val positive: Boolean = lits.view.forall(_.polarity)
  /** True iff this clause is purely negative. i.e.
    * if all literals are negative. */
  @inline final val negative: Boolean = lits.view.forall(!_.polarity)

  /** Returns a term representation of this clause.
    * @return Term `[l1] || [l2] || ... || [ln]` where `[.]` is the term representation of a literal,
    * and li are the literals in `lits`, `n = lits.length`. */
  final lazy val term: Term = mkPolyUnivQuant(implicitlyBound, mkDisjunction(lits.map(_.term)))

  // Operations on clauses
  // FIXME: Not right! Substitute has to mention newly introduced implicit bindings
  def substitute(s : Subst) : Clause = Clause.mkClause(lits.map(_.substitute(s)))

  @inline final def map[A](f: Literal => A): Seq[A] = lits.map(f)
  @inline final def mapLit(f: Literal => Literal): Clause = Clause.mkClause(lits.map(f), Derived)
  @inline final def replace(what: Term, by: Term): Clause = Clause.mkClause(lits.map(_.replaceAll(what, by)))

  /** The clause's weight. */
  @inline final def weight: Int = Configuration.CLAUSE_WEIGHTING.weightOf(this)
  @inline final def compare(that: Clause) = Configuration.CLAUSE_ORDERING.compare(this, that)

  final lazy val pretty = s"[${lits.map(_.pretty).mkString(" , ")}]"


  // System function adaptions
  override final def equals(obj : Any): Boolean = obj match {
    case co : Clause =>
      cong(co)
    case _ => false
  }
  override final def hashCode(): Int = if (lits.isEmpty) 0
  else lits.tail.foldLeft(lits.head.hashCode()){case (h,l) => h^l.hashCode()}

  // TODO: Do we still need this?
  // TODO: Optimized on sorted Literals.
  def cong(that : Clause) : Boolean =
    (lits forall { l1 =>
      that.lits exists { l2 =>
        l1.polarity == l2.polarity && l1 == l2
      }
    })&&(
      that.lits forall { l1 =>
        lits exists { l2 =>
          l1.polarity == l2.polarity && l1 == l2
        }
      })

  // TODO: Maybe move this to "utilities"?
  private def mkDisjunction(terms: Seq[Term]): Term = terms match {
    case Seq() => LitFalse()
    case Seq(t, ts@_*) => ts.foldLeft(t)({case (disj, t) => |||(disj, t)})
  }
  private def mkPolyUnivQuant(bindings: Seq[Type], term: Term): Term = {
    import Term.λ
    bindings.foldRight(term)((ty,t) => Forall(λ(ty)(t)))
  }
}

object Clause {
  import impl.{VectorClause => ClauseImpl}

  /** Create a clause containing the set of literals `lits` with origin `origin`. */
  @inline final def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause = ???
  @inline final def mkClause(lits: Iterable[Literal]): Clause = mkClause(lits, Derived)

  @inline final val empty = mkClause(Seq.empty)

  @inline final def lastClauseId: Int = ClauseImpl.lastClauseId
}

