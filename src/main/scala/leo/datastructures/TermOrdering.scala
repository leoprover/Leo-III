package leo.datastructures

import leo.datastructures.term.Term

/**
 * Created by lex on 20.08.14.
 */
trait TermOrdering extends Ordering[Term] {}

object SimpleOrdering extends TermOrdering {
  def compare(x: Term, y: Term): Int = 0 // TODO
}
