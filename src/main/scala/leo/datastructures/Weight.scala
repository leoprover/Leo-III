package leo.datastructures

/**
 * Interface for weighting objects such as clauses or literals.
 * A smaller weight means that the object should have "more priority" depending
 * on the current context.
 * Every weight defines an ordering by `x <= y :<=> x.weight <= y.weight`,
 * this can be obtained by using the `SimpleOrdering`.
 *
 * @author Alexander Steen
 * @since 25.11.2014
 */
trait Weight[What] {
  def weightOf[A <: What](w: A): Int
}