package leo.datastructures

import leo.{LiteralWeight, ClauseWeight}

///////////////////////////////
// Literal weights
///////////////////////////////


object LitWeight_Constant extends LiteralWeight {
  def weightOf[A <: Literal](lit: A) = 1
}


/////////////////////////////////
// Clause weights
/////////////////////////////////

object CLWeight_FIFO extends ClauseWeight {
  def weightOf[A <: Clause](cl: A) = cl.id
}

