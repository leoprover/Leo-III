package leo.datastructures.internal.terms


/**
 * A `TermBank` is a special term `Factory` that caches previously
 * created terms. Each term is only created once and reused for each
 * invocation of factory methods.
 * Unshared (local) terms can be created using the `local` factory.
 *
 * @author Alexander Steen
 * @since 20.08.2014
 */
trait TermBank extends Factory {
  /** Return the factory for local terms, that is, terms that are not globally shared */
  def local: Factory

  /** Insert (unshared) terms to the term bank. Has no effect on already shared terms.
    * Returns the syntactically equal but now shared term. */
  def insert(term: Term): Term = term.locality match {
    case LOCAL => insert0(term)
    case GLOBAL => term
  }
  protected[terms] def insert0(localTerm: Term): Term

  /** Clear the term bank, i.e. delete all cached terms */
  def reset(): Unit
}

abstract sealed class Locality
case object GLOBAL extends Locality
case object LOCAL extends Locality