package leo.datastructures.tptp.thf

import leo.datastructures.tptp.Commons.{
  Variable => CommonVariable,
  Number => CommonNumber}
import leo.modules.SZSException
import leo.modules.output.SZS_InputError

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
case class Sequent(tuple1: Seq[LogicFormula], tuple2: Seq[LogicFormula]) extends Formula {
  override def toString = "[" + tuple1.mkString(",") +"]" + " --> " + "[" + tuple2.mkString(",") + "]"
  val function_symbols : Set[String] = tuple1.toSet[LogicFormula].flatMap(_.function_symbols) union tuple2.toSet[LogicFormula].flatMap(_.function_symbols)
}

sealed abstract class LogicFormula {
  def function_symbols : Set[String]
}

case class Typed(formula: LogicFormula, typ: LogicFormula) extends LogicFormula {
  override def toString = formula.toString + " : " + typ.toString

  override val function_symbols: Set[String] = formula.function_symbols // TODO Do we need the typ symbols as well?
}

case class Binary(left: LogicFormula, connective: BinaryConnective, right: LogicFormula) extends LogicFormula {
  override def toString = "(" + left.toString + ") " + connective.toString + " (" + right.toString + ")"

  override val function_symbols: Set[String] = left.function_symbols union right.function_symbols
}
case class Unary(connective: UnaryConnective, formula: LogicFormula) extends LogicFormula {
  override def toString = connective.toString + " (" + formula.toString + ")"

  override val function_symbols: Set[String] = formula.function_symbols
}
case class Quantified(quantifier: Quantifier, varList: Seq[(CommonVariable,Option[LogicFormula])], matrix: LogicFormula) extends LogicFormula {
  override def toString = quantifier.toString + " [" + varList.mkString(",") + "] : (" + matrix.toString + ")"

  // TODO are we considering types as well? (Remove `union decl` if we do not want to check types)
  override val function_symbols: Set[String] = {
    val vars = varList.map(_._1).toSet
    // TODO Do we need the typ symbols as well?
    //val decl = varList.toSet[(Commons.Variable,Option[LogicFormula])].flatMap{case (_, ty) => ty.fold(Set[String]())(_.function_symbols)}
    (matrix.function_symbols -- vars) //union decl
  }

  val blocked_symbols : Set[String] = varList.map(_._1).toSet
}
case class Tuple(entries: Seq[LogicFormula]) extends LogicFormula{
  override def toString = s"[${entries.map(_.toString()).mkString(",")}]"

  override val function_symbols: Set[String] = entries.flatMap(_.function_symbols).toSet
}
case class Connective(c: Either[BinaryConnective, UnaryConnective]) extends LogicFormula {
  override def function_symbols: Set[String] = Set()
}
case class Function(func: String, args: Seq[LogicFormula]) extends LogicFormula {
  override def toString = s"$func(${args.map(_.toString).mkString(",")})"
  override val function_symbols: Set[String] = args.flatMap(_.function_symbols).toSet + func
}
case class Var(name: String) extends LogicFormula {
  override def toString = name
  override val function_symbols: Set[String] = Set.empty
}
case class Distinct(data: String) extends LogicFormula {
  override def toString = data
  override val function_symbols: Set[String] = Set.empty
}
case class Number(number: CommonNumber) extends LogicFormula {
  override def toString = number.toString
  override val function_symbols: Set[String] = Set.empty
}
case class Cond(cond: LogicFormula, thn: LogicFormula, els: LogicFormula) extends LogicFormula{
  override def toString = "$ite_f(" + List(cond,thn,els).mkString(",") + ")"

  override val function_symbols: Set[String] = cond.function_symbols union thn.function_symbols union els.function_symbols
}
/** Invariant: Only bindings supported of the form `c = ....`, this may have to be transformed into this form before. */
case class NewLet(binding: Tuple, in: Formula) extends LogicFormula {
  override def toString: String = s"$$let(${binding.toString},${in.toString})"
  override val function_symbols: Set[String] = {
    var introducedSymbols: Set[String] = Set()
    var symbolsInDef: Set[String] = Set()
    in.function_symbols
    try {
      binding.entries.foreach { case Binary(Function(symbol, Seq()), Eq, right) =>
        introducedSymbols = introducedSymbols + symbol
        symbolsInDef = symbolsInDef union right.function_symbols
      }
      (in.function_symbols -- introducedSymbols) union symbolsInDef
    } catch {
      case _:scala.MatchError => throw new SZSException(SZS_InputError, "Illegal use of let-specification")
    }
  }
}

case class Subtype(left: LogicFormula, right: LogicFormula) extends LogicFormula {
  override def toString = left + " << " + right

  override val function_symbols: Set[String] = Set()  // TODO What do we do in this case?
}

case class BinType(t: BinaryType) extends LogicFormula {
  override def toString = t.toString

  override val function_symbols: Set[String] = t.function_symbols
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
case object := extends BinaryConnective

sealed abstract class UnaryConnective
case object ~ extends UnaryConnective
case object !! extends UnaryConnective
case object ?? extends UnaryConnective
case object @@+ extends UnaryConnective // Choice
case object @@- extends UnaryConnective // Description
case object @@= extends UnaryConnective // Prefix equality

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
case class ->(t: Seq[LogicFormula]) extends BinaryType {
  override def toString = "(" + t.mkString(" > ") + ")"

  override val function_symbols: Set[String] = t.flatMap(_.function_symbols).toSet
}
case class *(t: Seq[LogicFormula]) extends BinaryType {
  override def toString = "(" + t.mkString(" * ") + ")"
  override val function_symbols: Set[String] = t.flatMap(_.function_symbols).toSet
}
case class +(t: Seq[LogicFormula]) extends BinaryType {
  override def toString = "(" + t.mkString(" + ") + ")"
  override val function_symbols: Set[String] = t.flatMap(_.function_symbols).toSet
}
