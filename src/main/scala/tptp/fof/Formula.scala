package tptp.fof

import tptp.Commons._
/**
 * Created by lex on 3/23/14.
 */
sealed abstract class Formula
case class Logical(formula: LogicFormula) extends Formula
case class Sequent(tuple1: List[LogicFormula], tuple2: List[LogicFormula]) extends Formula

sealed abstract class LogicFormula
case class Binary(left: LogicFormula, connective: BinaryConnective, right: LogicFormula) extends LogicFormula
case class Unary(connective: UnaryConnective, formula: LogicFormula) extends LogicFormula
case class Quantified(quantifier: Quantifier, varList: List[Variable], matrix: LogicFormula) extends LogicFormula
case class Atomic(formula: AtomicFormula) extends LogicFormula
case class Inequality(left: Term, right: Term) extends LogicFormula


sealed abstract class BinaryConnective
case object <=> extends BinaryConnective
case object Impl extends BinaryConnective
case object <= extends BinaryConnective
case object <~> extends BinaryConnective
case object ~| extends BinaryConnective
case object ~& extends BinaryConnective
case object | extends BinaryConnective
case object & extends BinaryConnective

sealed abstract class UnaryConnective
case object Not extends UnaryConnective

sealed abstract class Quantifier
case object Forall extends Quantifier
case object Exists extends Quantifier

