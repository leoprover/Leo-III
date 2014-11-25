package leo.datastructures

import leo.{LiteralWeight, ClauseWeight}

///////////////////////////////
// Literal weights
///////////////////////////////

/** Simple weighting function that gives every literal the same weight. */
object LitWeight_Constant extends LiteralWeight {
  def weightOf[A <: Literal](lit: A) = 1
}

/** Literal weighting that gives preference (i.e. gives lower weight) to older literals. */
object LitWeight_FIFO extends LiteralWeight {
  def weightOf[A <: Literal](lit: A) = lit.id
}

/** Literal weighting that uses the enclosed term's size as weight. */
object LitWeight_TermSize extends LiteralWeight {
  def weightOf[A <: Literal](lit: A) = lit.term.size
}

// more to come ...

/////////////////////////////////
// Clause weights
/////////////////////////////////

/** Weighting that gives a higher ('worse') weight for newer clauses. */
object CLWeight_FIFO extends ClauseWeight {
  def weightOf[A <: Clause](cl: A) = cl.id
}

/** Clause weighting that assigns the maximum of all literals weights as weight. */
object ClWeight_MaxLitWeight extends ClauseWeight {
  def weightOf[A <: Clause](cl: A) = cl.lits.map(_.weight).max
}

/** Clause weighting that assigns the sum of all literals weights as weight. */
object CLWeight_LitWeightSum extends ClauseWeight {
  def weightOf[A <: Clause](cl: A) = cl.lits.map(_.weight).sum
}

// more to come ...
