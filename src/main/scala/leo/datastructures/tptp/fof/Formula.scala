package leo.datastructures.tptp.fof

import leo.datastructures.tptp.Commons._
/**
 * Created by lex on 3/23/14.
 */
sealed abstract class Formula
case class Logical(formula: LogicFormula) extends Formula {
  override def toString = formula.toString
}
case class Sequent(tuple1: List[LogicFormula], tuple2: List[LogicFormula]) extends Formula {
  override def toString = "[" + tuple1.mkString(",") +"]" + " --> " + "[" + tuple2.mkString(",") + "]"
}

sealed abstract class LogicFormula
case class Binary(left: LogicFormula, connective: BinaryConnective, right: LogicFormula) extends LogicFormula  {
  override def toString = "(" + left.toString + ") " + connective.toString + " (" + right.toString + ")"
}
case class Unary(connective: UnaryConnective, formula: LogicFormula) extends LogicFormula {
  override def toString = connective.toString + " (" + formula.toString + ")"
}
case class Quantified(quantifier: Quantifier, varList: List[Variable], matrix: LogicFormula) extends LogicFormula {
  override def toString = quantifier.toString + " [" + varList.mkString(",") + "] : (" + matrix.toString + ")"
}
case class Atomic(formula: AtomicFormula) extends LogicFormula {
  override def toString = formula.toString
}
case class Inequality(left: Term, right: Term) extends LogicFormula {
  override def toString = left.toString + " != " + right.toString
}


sealed abstract class BinaryConnective
case object <=> extends BinaryConnective
case object Impl extends BinaryConnective {
  override def toString = "=>"
}
case object <= extends BinaryConnective
case object <~> extends BinaryConnective
case object ~| extends BinaryConnective
case object ~& extends BinaryConnective
case object | extends BinaryConnective
case object & extends BinaryConnective

sealed abstract class UnaryConnective
case object Not extends UnaryConnective  {
  override def toString = "~"
}

sealed abstract class Quantifier
case object ! extends Quantifier
case object ? extends Quantifier

