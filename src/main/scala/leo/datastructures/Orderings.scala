package leo.datastructures

import leo.{ClauseOrdering, TermOrdering}
import leo.datastructures.term.Term

/**
 * Collection of Ordering relations of terms, clauses, etc.
 *
 * @author Alexander Steen
 * @since 20.08.14
 */



/////////////////////
// Clause Orderings
/////////////////////

/** Lexicographic clause ordering on the 3-tuple (clause weight, clause age, clause origin). */
object CLOrdering_Lex_Weight_Age_Origin extends ClauseOrdering {
  import scala.math.Ordered.orderingToOrdered
  def compare(a: Clause, b: Clause) = ((a.weight, a.id, a.origin)) compare ((b.weight, b.id, b.origin))
}

///////////////////////
/// Term Orderings
///////////////////////

/** Only for debugging and compiling purposes. Will be removed soon. */
object SenselessOrdering extends TermOrdering {
  def compare(a: Term, b: Term) = 0
}

/** Polymorphic higher-order recursive path ordering on terms of same type, as given by Jouannaud and Rubio
  * in "The Higher-Order Recursive Path Ordering",1999, doi: 10.1109/LICS.1999.782635  */
object PolyHORecPathOrdering extends TermOrdering {
  def compare(a: Term, b: Term) = ???
}

///////////////////////
/// Generic Orderings
///////////////////////

/** `SimpleOrdering`s are orderings that are induced by a weighting. */
class SimpleOrdering[A](weighting: Weight[A]) extends Ordering[A] {
  def compare(a: A, b: A) = weighting.weightOf(a) - weighting.weightOf(b)
}

//////////////////////
// Associated traits
//////////////////////

/**
 *  A trait for representing quasi orderings.
 * A quasi ordering is:
 *
 * - reflexive
 * - transitive
 */
trait QuasiOrder[A] {
  /** Result of comparing `this` with operand `that`.
    *
    * Returns `Some(x)` where:
    *   - `x < 0` when `this < that`
    *   - `x == 0` when `this == that`
    *   - `x > 0` when  `this > that`
    * Returns `None` if arguments are not comparable
    */
  def compare(x: A, y: A): Option[Int]

  /** Strict comparison `x < y` w.r.t. the underlying ordering. */
  def lt (x: A, y: A): Boolean = compare(x,y).getOrElse(Int.MaxValue) < 0
  /** Comparison `x <= y` w.r.t. the underlying ordering. */
  def lteq (x: A, y: A): Boolean = compare(x,y).getOrElse(Int.MaxValue) < 0
  /** Strict comparison `x > y` w.r.t. the underlying ordering. */
  def gt (x: A, y: A): Boolean = compare(x,y).getOrElse(Int.MinValue) > 0
  /** CCmparison `x >= y` w.r.t. the underlying ordering. */
  def gteq (x: A, y: A): Boolean = compare(x,y).getOrElse(Int.MinValue) >= 0
}
