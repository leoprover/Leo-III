package leo.modules.calculus
package superposition

import leo.datastructures.{Clause, Literal, Term, Type, Position}


object Superposition extends BinaryCalculusRule[Clause, (Literal, Literal, Position)] {
  type MnemoicHint = (Equation, Modulated, SubtermPos)
  type Equation = Literal
  type Modulated = Literal
  type SubtermPos = Position

  override final val name: String = "superpos"

  override def canApply(c: Clause, d: Clause): (Boolean, HintType) = ???

  override def apply(c: Clause, d: Clause, hint: MnemoicHint): Clause = {
    val equ = hint._1
    val into = hint._2
    val where = hint._3


  }
}