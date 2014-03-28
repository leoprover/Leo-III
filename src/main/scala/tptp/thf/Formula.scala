package tptp.thf

import tptp._

/**
 * Created by lex on 3/23/14.
 */
sealed abstract class Formula
case class Logical(formula: LogicFormula) extends Formula
case class Sequent(tuple1: List[LogicFormula], tuple2: List[LogicFormula]) extends Formula

sealed abstract class LogicFormula
case class Typed(formula: LogicFormula, typ: LogicFormula) extends LogicFormula
case class Binary(left: LogicFormula, connective: BinaryConnective, right: LogicFormula) extends LogicFormula
case class Unary(connective: UnaryConnective, formula: LogicFormula) extends LogicFormula
case class Quantified(quantifier: Quantifier, varList: List[(Commons.Variable,Option[LogicFormula])], matrix: LogicFormula) extends LogicFormula
case class Connective(c: Either[BinaryConnective, UnaryConnective]) extends LogicFormula
case class Term(t: Commons.Term) extends LogicFormula
case class BinType(t: BinaryType) extends LogicFormula
case class Subtype(left: String, right: String) extends LogicFormula
case class Cond(cond: LogicFormula, thn: LogicFormula, els: LogicFormula) extends LogicFormula
// Let omitted

sealed abstract class BinaryConnective
case object Eq extends BinaryConnective
case object Neq extends BinaryConnective
case object <=> extends BinaryConnective
case object Impl extends BinaryConnective
case object <= extends BinaryConnective
case object <~> extends BinaryConnective
case object ~| extends BinaryConnective
case object ~& extends BinaryConnective
case object | extends BinaryConnective
case object & extends BinaryConnective
case object App extends BinaryConnective

sealed abstract class UnaryConnective
case object ~ extends UnaryConnective
case object !! extends UnaryConnective
case object ?? extends UnaryConnective

sealed abstract class Quantifier
case object All extends Quantifier  // !
case object Exists extends Quantifier // ?
case object Lambda extends Quantifier // ^
case object BigPi extends Quantifier // !>
case object BigSigma extends Quantifier // ?*
case object Choice extends Quantifier // @+
case object Description extends Quantifier // @-

// type TopType = LogicFormula
sealed abstract class BinaryType
case class ->(t: List[LogicFormula]) extends BinaryType
case class *(t: List[LogicFormula]) extends BinaryType
case class +(t: List[LogicFormula]) extends BinaryType

