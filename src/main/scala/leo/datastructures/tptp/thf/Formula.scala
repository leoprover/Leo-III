package leo.datastructures.tptp.thf

import leo.datastructures.tptp._

/**
 * Created by lex on 3/23/14.
 */
sealed abstract class Formula {
  /**
    * Collects all Function symbols of the formula.
    * Usefull for relevance filtering
    * @return All function symbols of the formula
    */
  def function_symbols : Set[String]
}
case class Logical(formula: LogicFormula) extends Formula {
  override def toString = formula.toString
  val function_symbols : Set[String] = formula.function_symbols
}
case class Sequent(tuple1: List[LogicFormula], tuple2: List[LogicFormula]) extends Formula {
  override def toString = "[" + tuple1.mkString(",") +"]" + " --> " + "[" + tuple2.mkString(",") + "]"
  val function_symbols : Set[String] = tuple1.toSet[LogicFormula].flatMap(_.function_symbols) union tuple2.toSet[LogicFormula].flatMap(_.function_symbols)
}

sealed abstract class LogicFormula {
  def function_symbols : Set[String]
}
case class Typed(formula: LogicFormula, typ: LogicFormula) extends LogicFormula {
  override def toString = formula.toString + " : " + typ.toString

  override val function_symbols: Set[String] = formula.function_symbols union formula.function_symbols
}
case class Binary(left: LogicFormula, connective: BinaryConnective, right: LogicFormula) extends LogicFormula {
  override def toString = "(" + left.toString + ") " + connective.toString + " (" + right.toString + ")"

  override val function_symbols: Set[String] = left.function_symbols union right.function_symbols
}
case class Unary(connective: UnaryConnective, formula: LogicFormula) extends LogicFormula {
  override def toString = connective.toString + " (" + formula.toString + ")"

  override val function_symbols: Set[String] = formula.function_symbols
}
case class Quantified(quantifier: Quantifier, varList: List[(Commons.Variable,Option[LogicFormula])], matrix: LogicFormula) extends LogicFormula {
  override def toString = quantifier.toString + " [" + varList.mkString(",") + "] : (" + matrix.toString + ")"

  // TODO are we considering types as well? (Remove `union decl` if we do not want to check types)
  override val function_symbols: Set[String] = {
    val vars = varList.map(_._1).toSet
    val decl = varList.toSet[(Commons.Variable,Option[LogicFormula])].flatMap{case (_, ty) => ty.fold(Set[String]())(_.function_symbols)}
    (matrix.function_symbols -- vars) union decl
  }

  val blocked_symbols : Set[String] = varList.map(_._1).toSet
}
case class Connective(c: Either[BinaryConnective, UnaryConnective]) extends LogicFormula {
  override def function_symbols: Set[String] = Set()
}
case class Term(t: Commons.Term) extends LogicFormula {
  override def toString = t.toString

  override val function_symbols: Set[String] = t.function_symbols
}
case class BinType(t: BinaryType) extends LogicFormula {
  override def toString = t.toString

  override val function_symbols: Set[String] = t.function_symbols
}
case class Subtype(left: String, right: String) extends LogicFormula {
  override def toString = left + " << " + right

  override val function_symbols: Set[String] = Set()  // TODO What do we do in this case?
}
case class Cond(cond: LogicFormula, thn: LogicFormula, els: LogicFormula) extends LogicFormula{
  override def toString = "$ite_f(" + List(cond,thn,els).mkString(",") + ")"

  override val function_symbols: Set[String] = cond.function_symbols union thn.function_symbols union els.function_symbols
}

case class Let(binding: LetBinding, in: Formula) extends LogicFormula {
  override def toString = binding match {
    case _:TermBinding => "$let_tf(" + List(binding,in).mkString(",") + ")"
    case _:FormulaBinding => "$let_ff(" + List(binding,in).mkString(",") + ")"
  }

  override val function_symbols: Set[String] = {
    val (vars, symbs) = binding.function_symbols
    (in.function_symbols -- vars) union symbs
  }
}

sealed abstract class BinaryConnective
case object Eq extends BinaryConnective {
  override def toString = "="
}
case object Neq extends BinaryConnective {
  override def toString = "!="
}
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
case object App extends BinaryConnective {
  override def toString = "@"
}

sealed abstract class UnaryConnective
case object ~ extends UnaryConnective
case object !! extends UnaryConnective
case object ?? extends UnaryConnective

sealed abstract class Quantifier
case object ! extends Quantifier  // All
case object ? extends Quantifier // Exists
case object ^ extends Quantifier // Lambda
case object !> extends Quantifier // Big pi
case object ?* extends Quantifier // Big sigma
case object @+ extends Quantifier // Choice
case object @- extends Quantifier // Description

// type TopType = LogicFormula
sealed abstract class BinaryType {
  def function_symbols : Set[String]
}
case class ->(t: List[LogicFormula]) extends BinaryType {
  override def toString = "(" + t.mkString(" > ") + ")"

  override val function_symbols: Set[String] = t.flatMap(_.function_symbols).toSet
}
case class *(t: List[LogicFormula]) extends BinaryType {
  override def toString = "(" + t.mkString(" * ") + ")"
  override val function_symbols: Set[String] = t.flatMap(_.function_symbols).toSet
}
case class +(t: List[LogicFormula]) extends BinaryType {
  override def toString = "(" + t.mkString(" + ") + ")"
  override val function_symbols: Set[String] = t.flatMap(_.function_symbols).toSet
}

sealed abstract class LetBinding {
  /**
    * Returns a set of new defined symbols and a set of symbols, used in the definition of those
    * @return
    */
  def function_symbols : (Set[String], Set[String])
}
case class FormulaBinding(binding: Quantified) extends LetBinding {
  override def toString = binding.toString
  override val function_symbols: (Set[String], Set[String]) = (binding.blocked_symbols, binding.function_symbols)
}
case class TermBinding(binding: Quantified) extends LetBinding {
  override def toString = binding.toString
  override val function_symbols: (Set[String], Set[String]) = (binding.blocked_symbols, binding.function_symbols)
}
