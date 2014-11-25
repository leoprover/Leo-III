package leo.datastructures

import leo.{LiteralWeight, ClauseWeight}

///////////////////////////////
// Literal weights
///////////////////////////////

/** Simple weighting function that gives every literal the same weight. */
object LitWeight_Constant extends LiteralWeight {
  def weightOf[A <: Literal](lit: A) = 1
}

// more to come ...

/////////////////////////////////
// Clause weights
/////////////////////////////////

/** Weighting that gives a higher ('worse') weight for newer clauses. */
object CLWeight_FIFO extends ClauseWeight {
  def weightOf[A <: Clause](cl: A) = cl.id
}

// more to come ...
