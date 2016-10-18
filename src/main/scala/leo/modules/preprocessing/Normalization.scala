package leo.modules.preprocessing

import leo.datastructures.{Clause, Signature}
import leo.modules.calculus.CalculusRule

/**
  * Created by mwisnie on 1/5/16.
  */
trait Normalization extends CalculusRule {
  override def name : String = "normalization"
  /**
    * Performs the normalization
 *
    * @param c The given clause
    * @return The normalized clause
    */
  def apply(c : Clause)(implicit sig: Signature) : Clause
}
