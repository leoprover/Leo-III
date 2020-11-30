package leo.datastructures

object TPTPParseTree {
  type Include = (String, Seq[String])
  type Annotations = Option[(GeneralTerm, Option[Seq[GeneralTerm]])]

  final case class Problem(includes: Seq[Include], formulas: Seq[AnnotatedFormula]) extends Pretty {
    override def pretty: String = {
      val sb: StringBuilder = new StringBuilder()
      includes.foreach { case (filename, inc) =>
        if (inc.isEmpty) {
          sb.append(s"include('$filename').\n")
        } else {
          sb.append(s"include('$filename', [${inc.map(s => s"'$s'").mkString(",")}]).\n")
        }
      }
      formulas.foreach { f =>
        sb.append(f.pretty)
        sb.append("\n")
      }
      if (sb.nonEmpty) sb.init.toString()
      else sb.toString()
    }
  }

//  sealed abstract class AnnotatedFormula[+A](val name: String, val role: String, val formula: A, val annotations: Annotations)
  sealed abstract class AnnotatedFormula extends Pretty {
    type F
    def name: String
    def role: String
    def formula: F
    def annotations: Annotations
  }
  final case class THFAnnotated(override val name: String,
                          override val role: String,
                          override val formula: THF.Formula,
                          override val annotations: Annotations) extends AnnotatedFormula {
    type F = THF.Formula

    override def pretty: String = prettifyAnnotated("thf", name, role, formula, annotations)
//      if (annotations.isEmpty) s"thf($name, $role, ${formula.pretty})."
//    else {
//      if (annotations.get._2.isEmpty) s"thf($name, $role, ${formula.pretty}, ${annotations.get._1.pretty})."
//      else s"thf($name, $role, ${formula.pretty}, ${annotations.get._1.pretty}, [${annotations.get._2.get.map(_.pretty).mkString(",")}])."
//    }
  }

  final case class TFFAnnotated(override val name: String,
                          override val role: String,
                          override val formula: TFF.Formula,
                          override val annotations: Annotations) extends AnnotatedFormula {
    type F = TFF.Formula

    override def pretty: String = prettifyAnnotated("tff", name, role, formula, annotations)
  }

  final case class FOFAnnotated(override val name: String,
                          override val role: String,
                          override val formula: FOF.Formula,
                          override val annotations: Annotations) extends AnnotatedFormula {
    type F = FOF.Formula

    override def pretty: String = prettifyAnnotated("fof", name, role, formula, annotations)
  }

  final case class TCFAnnotated(override val name: String,
                          override val role: String,
                          override val formula: TCF.Formula,
                          override val annotations: Annotations) extends AnnotatedFormula {
    type F = TCF.Formula

    override def pretty: String = prettifyAnnotated("tcf", name, role, formula, annotations)
  }

  final case class CNFAnnotated(override val name: String,
                          override val role: String,
                          override val formula: CNF.Formula,
                          override val annotations: Annotations) extends AnnotatedFormula {
    type F = CNF.Formula

    override def pretty: String = prettifyAnnotated("cnf", name, role, formula, annotations)
  }

  final case class TPIAnnotated(override val name: String,
                          override val role: String,
                          override val formula: TPI.Formula,
                          override val annotations: Annotations) extends AnnotatedFormula {
    type F = TPI.Formula

    override def pretty: String = prettifyAnnotated("tpi", name, role, formula, annotations)
  }

  @inline private[this] final def prettifyAnnotated(prefix: String, name: String, role: String, formula: Pretty, annotations: Annotations): String = {
    if (annotations.isEmpty) s"$prefix($name, $role, ${formula.pretty})."
    else {
      if (annotations.get._2.isEmpty) s"$prefix($name, $role, ${formula.pretty}, ${annotations.get._1.pretty})."
      else s"$prefix($name, $role, ${formula.pretty}, ${annotations.get._1.pretty}, [${annotations.get._2.get.map(_.pretty).mkString(",")}])."
    }
  }

  sealed abstract class Number extends Pretty
  final case class Integer(value: Int) extends Number {
    override def pretty: String = value.toString
  }
  final case class Rational(numerator: Int, denominator: Int) extends Number {
    override def pretty: String = s"$numerator/$denominator"
  }
  final case class Real(wholePart: Int, decimalPlaces: Int, exponent: Int) extends Number {
    override def pretty: String = s"$wholePart.${decimalPlaces}E$exponent"
  }

  final case class GeneralTerm(data: Seq[GeneralData], list: Option[Seq[GeneralTerm]]) extends Pretty {
    override def pretty: String = {
      val sb: StringBuilder = new StringBuilder()
      if (data.nonEmpty) {
        sb.append(data.map(_.pretty).mkString(":"))
      }
      if (list.isDefined) {
        sb.append(":")
        sb.append("[")
        sb.append(list.get.map(_.pretty).mkString(","))
        sb.append("]")
      }
      sb.toString()
    }
  }

  /** General formula annotation data. Can be one of the following:
    *   - [[MetaFunctionData]], a term-like meta expression: either a (meta-)function or a (meta-)constant.
    *   - [[MetaVariable]], a term-like meta expression that captures a variable.
    *   - [[NumberData]], a numerical value.
    *   - [[DistinctObjectData]], an expression that represents itself.
    *   - [[GeneralFormulaData]], an expression that contains object-level formula expressions.
    *
    *   @see See [[GeneralTerm]] for some context and
    *        [[http://tptp.org/TPTP/SyntaxBNF.html#general_term]] for a use case.
    */
  sealed abstract class GeneralData extends Pretty
  /** @see [[GeneralData]] */
  final case class MetaFunctionData(f: String, args: Seq[GeneralTerm]) extends GeneralData {
    private[this] final val simpleLowerWordRegex = "^[a-z][a-zA-Z\\d_]*$"
    override def pretty: String = {
      val escapedF = if (f.matches(simpleLowerWordRegex)) f
      else s"'${f.replace("\\","\\\\").replace("'", "\\'")}'"
      if (args.isEmpty) escapedF else s"$escapedF(${args.map(_.pretty).mkString(",")})"
    }
  }
  /** @see [[GeneralData]] */
  final case class MetaVariable(variable: String) extends GeneralData {
    override def pretty: String = variable
  }
  /** @see [[GeneralData]] */
  final case class NumberData(number: Number) extends GeneralData {
    override def pretty: String = number.pretty
  }
  /** @see [[GeneralData]] */
  final case class DistinctObjectData(name: String) extends GeneralData {
    override def pretty: String = name
  }
  /** @see [[GeneralData]] */
  final case class GeneralFormulaData(data: Any) extends GeneralData {
    override def pretty: String = data.toString // TODO: make Pretty
  }


  object THF {
    sealed abstract class Formula extends Pretty
    final case class Typing(atom: String, typ: Any) extends Formula {
      override def pretty: String = s"$atom: ${typ.toString}" // TODO: make pretty
    }
    final case class Subtype(subtype: String, supertype: String) extends Formula {
      override def pretty: String = s"$subtype << $supertype"
    }
    final case class Sequent(left: Seq[Term], right: Seq[Term]) extends Formula {
      override def pretty: String = s"[${left.map(_.pretty).mkString(",")}] --> [${right.map(_.pretty).mkString(",")}]"
    }
    final case class Logical(term: Term) extends Formula {
      override def pretty: String = term.pretty
    }

    sealed abstract class Term extends Pretty
    final case class FunctionTerm(f: String, args: Seq[Term]) extends Term  {
      override def pretty: String = ???
    }
    final case class QuantifiedFormula(quantifier: Any, variableList: Seq[Any], body: Term) extends Term {
      override def pretty: String = ???
    }
    final case class Variable(name: String) extends Term {
      override def pretty: String = name
    }
    final case class UnaryFormula(connective: Any, body: Term) extends Term {
      override def pretty: String = ???
    }
    final case class BinaryFormula(connective: Any, left: Term, right: Term) extends Term {
      override def pretty: String = ???
    }
    final case class Tuple(elements: Seq[Term]) extends Term {
      override def pretty: String = s"[${elements.map(_.pretty).mkString(",")}]"
    }
    final case class ConditionalTerm(condition: Term, thn: Term, els: Term) extends Term {
      override def pretty: String = s"$$ite(${condition.pretty}, ${thn.pretty}, ${els.pretty})"
    }
    final case class LetTerm(typing: Map[String, Any], binding: Map[Term, Term], body: Term) extends Term {
      override def pretty: String = ???
    }
    final case class Connective(conn: Any) extends Term {
      override def pretty: String = ???
    }
    final case class DistinctObject(name: String) extends Term {
      override def pretty: String = name
    }
    final case class NumberTerm(value: Number) extends Term {
      override def pretty: String = value.pretty
    }
  }

  object TFF {
    sealed abstract class Formula extends Pretty
  }

  object FOF {
    sealed abstract class Formula extends Pretty
  }

  object TCF {
    sealed abstract class Formula extends Pretty
  }

  object CNF {
    sealed abstract class Formula extends Pretty
  }

  object TPI {
    sealed abstract class Formula extends Pretty
  }
}
