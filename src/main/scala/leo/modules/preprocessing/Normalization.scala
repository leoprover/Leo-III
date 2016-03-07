package leo.modules.preprocessing

import leo.datastructures.Clause

/**
  * Created by mwisnie on 1/5/16.
  */
trait Normalization extends Function1[Clause,Clause] {
  /**
    * Performs the normalization
    * @param c The given clause
    * @return The normalized clause
    */
  def apply(c : Clause) : Clause
}
