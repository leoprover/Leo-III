package leo.datastructures.tptp.tff

import leo.datastructures.tptp.Commons._

/**
 * Created by lex on 3/23/14.
 */
sealed abstract class Formula
case class Logical(formula: LogicFormula) extends Formula {
  override def toString = formula.toString
}
case class TypedAtom(formula: String, typ: Type) extends Formula {
  override def toString = formula.toString + " : " + typ.toString
}
case class Sequent(tuple1: List[LogicFormula], tuple2: List[LogicFormula]) extends Formula {
  override def toString = "[" + tuple1.mkString(",") +"]" + " --> " + "[" + tuple2.mkString(",") + "]"
}

sealed abstract class LogicFormula
case class Binary(left: LogicFormula, connective: BinaryConnective, right: LogicFormula) extends LogicFormula {
  override def toString = "(" + left.toString + ") " + connective.toString + " (" + right.toString + ")"
}
case class Quantified(quantifier: Quantifier, varList: List[(Variable,Option[AtomicType])], matrix: LogicFormula) extends LogicFormula {
  override def toString = quantifier.toString + " [" + varList.mkString(",") + "] : (" + matrix.toString + ")"
}
case class Unary(connective: UnaryConnective, formula: LogicFormula) extends LogicFormula {
  override def toString = connective.toString + " (" + formula.toString + ")"
}
case class Inequality(left: Term, right: Term) extends LogicFormula {
  override def toString = left.toString + " != " + right.toString
}
case class Atomic(formula: AtomicFormula) extends LogicFormula {
  override def toString = formula.toString
}
case class Cond(cond: LogicFormula, thn: LogicFormula, els: LogicFormula) extends LogicFormula {
  override def toString = "$ite_f(" + List(cond,thn,els).mkString(",") + ")"
}
case class Let(binding: LetBinding, in: Formula) extends LogicFormula

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
case object Not extends UnaryConnective {
  override def toString = "~"
}

sealed abstract class Quantifier
case object ! extends Quantifier
case object ? extends Quantifier

sealed abstract class Type
case class AtomicType(typ: String, args: List[AtomicType]) extends Type {
  override def toString = funcToString(typ, args)
}
case class ->(t: List[Type]) extends Type {
  override def toString = "(" + t.mkString(" > ") + ")"
}
case class *(t: List[Type]) extends Type {
  override def toString = "(" + t.mkString(" * ") + ")"
}
case class QuantifiedType(varList: List[(Variable,Option[AtomicType])], typ: Type) extends Type {
  override def toString = "!> [" + varList.map(typedVarToString).mkString(",")+ "] : " + typ.toString
}


sealed abstract class LetBinding
case class FormulaBinding(varList: List[(Variable,Option[AtomicType])], left: Atomic, right: LogicFormula) extends LetBinding
case class TermBinding(varList: List[(Variable,Option[AtomicType])], left: Term, right: Term) extends LetBinding