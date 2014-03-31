package tptp.tff

import tptp.Commons._

/**
 * Created by lex on 3/23/14.
 */
sealed abstract class Formula
case class Logical(formula: LogicFormula) extends Formula
case class TypedAtom(formula: String, typ: Type) extends Formula
case class Sequent(tuple1: List[LogicFormula], tuple2: List[LogicFormula]) extends Formula

sealed abstract class LogicFormula
case class Binary(left: LogicFormula, connective: BinaryConnective, right: LogicFormula) extends LogicFormula
case class Quantified(quantifier: Quantifier, varList: List[(Variable,Option[AtomicType])], matrix: LogicFormula) extends LogicFormula
case class Unary(connective: UnaryConnective, formula: LogicFormula) extends LogicFormula
case class Inequality(left: Term, right: Term) extends LogicFormula
case class Atomic(formula: AtomicFormula) extends LogicFormula
case class Cond(cond: LogicFormula, thn: LogicFormula, els: LogicFormula) extends LogicFormula
case class Let(binding: LetBinding, in: LogicFormula) extends LogicFormula

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
case object ! extends Quantifier
case object ? extends Quantifier

sealed abstract class Type
case class AtomicType(typ: String, args: List[AtomicType]) extends Type
case class ->(t: List[Type]) extends Type
case class *(t: List[Type]) extends Type
case class QuantifiedType(varList: List[(Variable,Option[AtomicType])], typ: Type)


sealed abstract class LetBinding
case class FormulaBinding(left: Atomic, right: LogicFormula) extends LetBinding
case class TermBinding(left: Term, right: Term) extends LetBinding