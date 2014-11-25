package leo.datastructures

import leo.ClauseWeight

///////////////////////////////
// Literal weights
///////////////////////////////


// to come ...


/////////////////////////////////
// Clause weights
/////////////////////////////////

object FIFOWeight extends ClauseWeight {
  def weightOf[A <: Clause](cl: A) = cl.id
}

