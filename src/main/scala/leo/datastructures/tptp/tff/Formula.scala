package leo.datastructures.tptp.tff

import leo.datastructures.tptp.Commons._

/**
 * Created by lex on 3/23/14.
 */
sealed abstract class Formula{
  type LetBinding = Either[Formula#LetFormulaBinding, Formula#LetTermBinding]
  type LetFormulaBinding = Map[Func, LogicFormula]
  type LetTermBinding = Map[Func, Term]

  def function_symbols : Set[String]
}
case class Logical(formula: LogicFormula) extends Formula {
  override def toString = formula.toString

  override val function_symbols: Set[String] = formula.function_symbols
}
case class TypedAtom(formula: String, typ: Type) extends Formula {
  override def toString = formula.toString + " : " + typ.toString

  override val function_symbols: Set[String] = Set(formula)
}
case class Sequent(tuple1: Seq[LogicFormula], tuple2: Seq[LogicFormula]) extends Formula {
  override def toString = "[" + tuple1.mkString(",") +"]" + " --> " + "[" + tuple2.mkString(",") + "]"

  override val function_symbols: Set[String] = tuple1.toSet[LogicFormula].flatMap(_.function_symbols) union tuple2.toSet[LogicFormula].flatMap(_.function_symbols)
}

sealed abstract class LogicFormula {
  def function_symbols : Set[String]
}
case class Binary(left: LogicFormula, connective: BinaryConnective, right: LogicFormula) extends LogicFormula {
  override def toString = "(" + left.toString + ") " + connective.toString + " (" + right.toString + ")"

  override val function_symbols: Set[String] = left.function_symbols union right.function_symbols
}
case class Quantified(quantifier: Quantifier, varList: Seq[(Variable,Option[AtomicType])], matrix: LogicFormula) extends LogicFormula {
  override def toString = quantifier.toString + " [" + varList.mkString(",") + "] : (" + matrix.toString + ")"

  override val function_symbols: Set[String] = {
    val vars = varList.map(_._1).toSet
    matrix.function_symbols -- vars
  }

  val blocked_symbols : Set[String] = varList.map(_._1).toSet
}
case class Unary(connective: UnaryConnective, formula: LogicFormula) extends LogicFormula {
  override def toString = connective.toString + " (" + formula.toString + ")"

  override val function_symbols: Set[String] = formula.function_symbols
}
case class Inequality(left: Term, right: Term) extends LogicFormula {
  override def toString = left.toString + " != " + right.toString

  override val function_symbols: Set[String] = left.function_symbols union right.function_symbols
}
case class Atomic(formula: AtomicFormula) extends LogicFormula {
  override def toString = formula.toString

  override val function_symbols: Set[String] = formula.function_symbols
}
case class Cond(cond: LogicFormula, thn: LogicFormula, els: LogicFormula) extends LogicFormula {
  override def toString = "$ite_f(" + List(cond,thn,els).mkString(",") + ")"

  override val function_symbols: Set[String] = cond.function_symbols union thn.function_symbols union els.function_symbols
}

case class Let(binding: Formula#LetBinding,
               in: Formula) extends LogicFormula {
  override val function_symbols: Set[String] = {
    val (definedSymbols, newSymbols) = binding.fold({ fBinding =>
      (fBinding.keySet.map(_.name), fBinding.values.toSet.flatMap{x:LogicFormula => x.function_symbols})
    }, { tBinding =>
      (tBinding.keySet.map(_.name), tBinding.values.toSet.flatMap{x:Term => x.function_symbols})
    })
    (in.function_symbols -- definedSymbols) union newSymbols
  }
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
case object Not extends UnaryConnective {
  override def toString = "~"
}

sealed abstract class Quantifier
case object ! extends Quantifier
case object ? extends Quantifier

sealed abstract class Type
case class AtomicType(typ: String, args: Seq[AtomicType]) extends Type {
  override def toString = funcToString(typ, args)
}
case class ->(t: Seq[Type]) extends Type {
  override def toString = "(" + t.mkString(" > ") + ")"
}
case class *(t: Seq[Type]) extends Type {
  override def toString = "(" + t.mkString(" * ") + ")"
}
case class QuantifiedType(varList: Seq[(Variable,Option[AtomicType])], typ: Type) extends Type {
  override def toString = "!> [" + varList.map(typedVarToString).mkString(",")+ "] : " + typ.toString
}
