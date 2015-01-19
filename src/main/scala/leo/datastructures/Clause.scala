package leo.datastructures

import leo.Configuration
import leo.datastructures.term.Term
import leo.datastructures.term.Term.{λ}

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
  /** The clause's weight. */
  def weight: Int = Configuration.CLAUSE_WEIGHTING.weightOf(this)
  /** The source from where the clause was created, See `ClauseOrigin`. */
  def origin: ClauseOrigin
  /** The types of the implicitly universally quantified variables. */
  def implicitBindings: Seq[Type]

  def isEmpty: Boolean = lits.isEmpty

  def compare(that: Clause) = Configuration.CLAUSE_ORDERING.compare(this, that)

  def map[A](f: Literal => A): Seq[A] = lits.map(f)
  def mapLit(f: Literal => Literal): Clause = Clause.mkClause(lits.map(f), Derived)
  def replace(what: Term, by: Term): Clause = Clause.mkClause(lits.map(_.replace(what, by)), implicitBindings, Derived)

  // TODO: Not right! Substitute has to mention newly introduced implicit bindings
  def substitute(s : Subst) : Clause = Clause.mkClause(lits.map(_.substitute(s)), implicitBindings, Derived)

  def merge(that : Clause) = {
    val newBindings = implicitBindings ++ that.implicitBindings
    val liftedThis = lits.map(_.termMap(_.closure(Subst.shift(that.implicitBindings.length))))
    val newLits = liftedThis ++ that.lits
    Clause.mkClause(newLits, newBindings, Derived)
  }

  lazy val pretty = s"[${lits.map(_.pretty).mkString(" , ")}]"

  lazy val toTerm: Term = mkPolyUnivQuant(implicitBindings, mkDisjunction(lits.map(_.toTerm)))

  // TODO: Optimized on sorted Literals.
  def cong(that : Clause) : Boolean =
    (lits forall { l1 =>
      that.lits exists { l2 =>
        l1.polarity == l2.polarity && l1.term == l2.term
      }
    })&&(
      that.lits forall { l1 =>
        lits exists { l2 =>
          l1.polarity == l2.polarity && l1.term == l2.term
        }
      })

  // TODO: Maybe move this to "utilities"?
  private def mkDisjunction(terms: Seq[Term]): Term = terms match {
    case Seq() => LitFalse()
    case Seq(t, ts@_*) => ts.foldLeft(t)({case (disj, t) => |||(disj, t)})
  }
  private def mkPolyUnivQuant(bindings: Seq[Type], term: Term): Term = bindings.foldRight(term)((ty,t) => Forall(λ(ty)(t)))
}

object Clause {
  import impl.{VectorClause => ClauseImpl}

  /** Create a clause containing the set of literals `lits` with origin `origin`. */
  def mkClause(lits: Iterable[Literal], implicitBindings: Seq[Type], origin: ClauseOrigin): Clause = ClauseImpl.mkClause(lits, implicitBindings, origin)
  def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause = ClauseImpl.mkClause(lits, Seq(), origin)
  def mkDerivedClause(lits: Iterable[Literal], implicitBindings: Seq[Type]): Clause = mkClause(lits, implicitBindings, Derived)

  def empty() = mkClause(Nil, Nil, Derived)

  def lastClauseId: Int = ClauseImpl.lastClauseId
}

