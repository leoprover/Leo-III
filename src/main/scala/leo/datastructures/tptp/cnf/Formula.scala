package leo.datastructures.tptp.cnf

import leo.datastructures.tptp.Commons._

/**
 * Created by lex on 3/23/14.
 */
case class Formula(literals: Seq[Literal]) {
  override def toString = literals.mkString(" | ")

  val function_symbols: Set[String] = literals.toSet[Literal].flatMap(l => l.function_symbols)
  def vars = literals.flatMap(_.vars).toSet
}

sealed abstract class Literal {
  def function_symbols : Set[String]
  def vars: Set[String]
}
case class Positive(formula: AtomicFormula) extends Literal {
  override def toString = formula.toString

  override val function_symbols: Set[String] = formula.function_symbols
  def vars = formula.vars
}
case class Negative(formula: AtomicFormula) extends Literal {
  override def toString = "~ " + formula.toString

  override val function_symbols: Set[String] = formula.function_symbols
  def vars = formula.vars
}
case class Inequality(left: Term, right: Term) extends Literal {
  override def toString = left.toString + " != " + right.toString

  override val function_symbols: Set[String] = left.function_symbols union right.function_symbols
  def vars = left.vars union right.vars
}