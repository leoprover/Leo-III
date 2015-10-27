package leo.datastructures

import leo.Configuration
import Literal.LitMaxFlag

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
  def implicitlyBound: Set[(Type, Int)]
  /** The source from where the clause was created, See `ClauseOrigin`. */
  def origin: ClauseOrigin

  // Further properties
  /** Those literals in `lits` that are positive. */
  def posLits: Seq[Literal]
  /** Those literals in `lits` that are negative. */
  def negLits: Seq[Literal]

  def maxLits: Map[LitMaxFlag, Seq[Literal]]

  /** True iff this clause is ground. */
  def ground: Boolean
  /** True iff this clause is purely positive. i.e.
    * if all literals are positive. */
  def positive: Boolean
  /** True iff this clause is purely negative. i.e.
    * if all literals are negative. */
  def negative: Boolean

  /** Returns a term representation of this clause.
    * @return Term `[l1] || [l2] || ... || [ln]` where `[.]` is the term representation of a literal,
    * and li are the literals in `lits`, `n = lits.length`. */
  final lazy val term: Term = mkPolyUnivQuant(implicitlyBound.map(_._1).toSeq, mkDisjunction(lits.map(_.term)))

  // Operations on clauses
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
  import Literal.{LitMax, LitStrictlyMax}

  /** Create a clause containing the set of literals `lits` with origin `origin`. */
  @inline final def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause =  ClauseImpl.mkClause(lits, origin)
  @inline final def mkClause(lits: Iterable[Literal]): Clause = mkClause(lits, Derived)

  @inline final val empty = mkClause(Seq.empty)

  @inline final def lastClauseId: Int = ClauseImpl.lastClauseId

  // Utility
  @inline final def empty(c: Clause): Boolean = c.lits.isEmpty
  final def effectivelyEmpty(c: Clause): Boolean = empty(c) || c.lits.forall(_.flexflex)
  final def trivial(c: Clause): Boolean = c.lits.exists(Literal.isTrue) || c.posLits.exists(l => c.negLits.exists(l2 => l.unsignedEquals(l2)))

  /** True iff this clause is horn. */
  @inline final def horn(c: Clause): Boolean = c.posLits.length <= 1
  /** True iff this clause is a unit clause. */
  @inline final def unit(c: Clause): Boolean = c.lits.length == 1
  /** True iff this clause is a demodulator. */
  @inline final def demodulator(c: Clause): Boolean = c.posLits.length == 1 && c.negLits.isEmpty
  /** True iff this clause is a rewrite rule. */
  @inline final def rewriteRule(c: Clause): Boolean = demodulator(c) && c.posLits.head.oriented

  @inline final def strictMaxOf(c: Clause): Seq[Literal] = c.maxLits(LitStrictlyMax)
  @inline final def maxOf(c: Clause): Seq[Literal] = c.maxLits(LitMax)
}

