package tptp.cnf

import tptp.Commons._

/**
 * Created by lex on 3/23/14.
 */
case class Formula(literals: List[Literal]) {
  override def toString = literals.map(x => "(" + x.toString + ")").mkString(" | ")
}

sealed abstract class Literal
case class Positive(formula: AtomicFormula) extends Literal {
  override def toString = formula.toString
}
case class Negative(formula: AtomicFormula) extends Literal {
  override def toString = "~ " + formula.toString
}
case class Inequality(left: Term, right: Term) extends Literal {
  override def toString = left.toString + " != " + right.toString
}