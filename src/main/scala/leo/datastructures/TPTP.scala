package leo.datastructures

object TPTP {
  type Include = (String, Seq[String])
  type Annotations = Option[(GeneralTerm, Option[Seq[GeneralTerm]])]

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // TPTP problem file AST
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

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

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // Top-level annotated formula ASTs
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  sealed abstract class AnnotatedFormula extends Pretty {
    import AnnotatedFormula.FormulaType.FormulaType

    type F
    def name: String
    def role: String
    def formula: F
    def annotations: Annotations

    def formulaType: FormulaType

    /** Contains every constant symbol (i.e., no variables) occurring in the formula, including term and type symbols. */
    def symbols: Set[String]
  }
  object AnnotatedFormula {
    final object FormulaType extends Enumeration {
      type FormulaType = Value
      final val THF, TFF, FOF, CNF, TPI = Value
    }
  }

  final case class THFAnnotated(override val name: String,
                                override val role: String,
                                override val formula: THF.Statement,
                                override val annotations: Annotations) extends AnnotatedFormula {
    import AnnotatedFormula.FormulaType.FormulaType
    type F = THF.Statement

    def formulaType: FormulaType = AnnotatedFormula.FormulaType.THF
    override def pretty: String = prettifyAnnotated("thf", name, role, formula, annotations)
    override def symbols: Set[String] = formula.symbols
  }

  final case class TFFAnnotated(override val name: String,
                                override val role: String,
                                override val formula: TFF.Statement,
                                override val annotations: Annotations) extends AnnotatedFormula {
    import AnnotatedFormula.FormulaType.FormulaType
    type F = TFF.Statement

    def formulaType: FormulaType = AnnotatedFormula.FormulaType.TFF
    override def pretty: String = prettifyAnnotated("tff", name, role, formula, annotations)
    override def symbols: Set[String] = formula.symbols
  }

  final case class FOFAnnotated(override val name: String,
                                override val role: String,
                                override val formula: FOF.Statement,
                                override val annotations: Annotations) extends AnnotatedFormula {
    import AnnotatedFormula.FormulaType.FormulaType
    type F = FOF.Statement

    def formulaType: FormulaType = AnnotatedFormula.FormulaType.FOF
    override def pretty: String = prettifyAnnotated("fof", name, role, formula, annotations)
    override def symbols: Set[String] = formula.symbols
  }

  /*final case class TCFAnnotated(override val name: String,
                                override val role: String,
                                override val formula: TCF.Statement,
                                override val annotations: Annotations) extends AnnotatedFormula {
    type F = TCF.Statement

    override def pretty: String = prettifyAnnotated("tcf", name, role, formula, annotations)
  }*/

  final case class CNFAnnotated(override val name: String,
                                override val role: String,
                                override val formula: CNF.Statement,
                                override val annotations: Annotations) extends AnnotatedFormula {
    import AnnotatedFormula.FormulaType.FormulaType
    type F = CNF.Statement

    def formulaType: FormulaType = AnnotatedFormula.FormulaType.CNF
    override def pretty: String = prettifyAnnotated("cnf", name, role, formula, annotations)
    override def symbols: Set[String] = formula.symbols
  }

  final case class TPIAnnotated(override val name: String,
                          override val role: String,
                          override val formula: FOF.Statement,
                          override val annotations: Annotations) extends AnnotatedFormula {
    import AnnotatedFormula.FormulaType.FormulaType
    type F = FOF.Statement

    def formulaType: FormulaType = AnnotatedFormula.FormulaType.TPI
    override def pretty: String = prettifyAnnotated("tpi", name, role, formula, annotations)
    override def symbols: Set[String] = formula.symbols
  }

  @inline private[this] final def prettifyAnnotated(prefix: String, name: String, role: String, formula: Pretty, annotations: Annotations): String = {
    if (annotations.isEmpty) s"$prefix($name, $role, ${formula.pretty})."
    else {
      if (annotations.get._2.isEmpty) s"$prefix($name, $role, ${formula.pretty}, ${annotations.get._1.pretty})."
      else s"$prefix($name, $role, ${formula.pretty}, ${annotations.get._1.pretty}, [${annotations.get._2.get.map(_.pretty).mkString(",")}])."
    }
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // General TPTP stuff AST
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  sealed abstract class Number extends Pretty
  final case class Integer(value: Int) extends Number {
    override def pretty: String = value.toString
  }
  final case class Rational(numerator: Int, denominator: Int) extends Number {
    override def pretty: String = s"$numerator/$denominator"
  }
  final case class Real(wholePart: Int, decimalPlaces: Int, exponent: Int) extends Number {
    override def pretty: String = if (exponent == 1) s"$wholePart.$decimalPlaces"
                                  else s"$wholePart.${decimalPlaces}E$exponent"
  }

  final case class GeneralTerm(data: Seq[GeneralData], list: Option[Seq[GeneralTerm]]) extends Pretty {
    override def pretty: String = {
      val sb: StringBuilder = new StringBuilder()
      if (data.nonEmpty) {
        sb.append(data.map(_.pretty).mkString(":"))
      }
      if (list.isDefined) {
        if(data.nonEmpty) sb.append(":")
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
    override def pretty: String = {
      val escapedF = escapeAtomicWord(f)
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
    override def pretty: String = {
      assert(name.startsWith("\"") && name.endsWith("\""), "Distinct object without enclosing double quotes.")
      s""""${escapeDistinctObject(name.tail.init)}""""
    }
  }
  /** @see [[GeneralData]] */
  final case class GeneralFormulaData(data: FormulaData) extends GeneralData {
    override def pretty: String = data.pretty
  }

  sealed abstract class FormulaData extends Pretty
  final case class THFData(formula: THF.Statement) extends FormulaData {
    override def pretty: String = s"$$thf(${formula.pretty})"
  }
  final case class TFFData(formula: TFF.Statement) extends FormulaData {
    override def pretty: String = s"$$tff(${formula.pretty})"
  }
  final case class FOFData(formula: FOF.Statement) extends FormulaData {
    override def pretty: String = s"$$fof(${formula.pretty})"
  }
  final case class CNFData(formula: CNF.Statement) extends FormulaData {
    override def pretty: String = s"$$cnf(${formula.pretty})"
  }
  final case class FOTData(formula: FOF.Term) extends FormulaData {
    override def pretty: String = s"$$fot(${formula.pretty})"
  }

  private def escapeAtomicWord(word: String): String = {
    val simpleLowerWordRegex = "^[a-z][a-zA-Z\\d_]*$"
    if (word.matches(simpleLowerWordRegex)) word
    else s"'${word.replace("\\","\\\\").replace("'", "\\'")}'"
  }
  private def escapeDistinctObject(name: String): String = {
    name.replace("\\","\\\\").replace("\"", "\\\"")
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // THF AST
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  object THF {
    type TypedVariable = (String, Type)
    type Type = Formula

    sealed abstract class Statement extends Pretty {
      def symbols: Set[String]
    }
    final case class Typing(atom: String, typ: Type) extends Statement {
      override def pretty: String = {
        val escapedName = if (atom.startsWith("$") || atom.startsWith("$$")) atom else escapeAtomicWord(atom)
        s"$escapedName: ${typ.pretty}"
      }
      override def symbols: Set[String] = typ.symbols + atom
    }
    final case class Logical(formula: Formula) extends Statement {
      override def pretty: String = formula.pretty
      override def symbols: Set[String] = formula.symbols
    }

    // Types as terms; for TH1 parsing. That's why we dont have a clean separation between terms and types here.
    // We don't care for well-typedness etc. in parsing. We can parse syntactically correct but completely meaningless
    // and ill-typed inputs. This will be addressed in the interpretation step.
    sealed abstract class Formula extends Pretty {
      def symbols: Set[String]
    }

    final case class FunctionTerm(f: String, args: Seq[Formula]) extends Formula  {
      override def pretty: String = {
        val escapedF = if (f.startsWith("$") || f.startsWith("$$")) f else escapeAtomicWord(f)
        if (args.isEmpty) escapedF else s"$escapedF(${args.map(_.pretty).mkString(",")})"
      }

      override def symbols: Set[String] = args.flatMap(_.symbols).toSet + f

      @inline def isUninterpretedFunction: Boolean = !isDefinedFunction && !isSystemFunction
      @inline def isDefinedFunction: Boolean = f.startsWith("$") && !isSystemFunction
      @inline def isSystemFunction: Boolean = f.startsWith("$$")
      @inline def isConstant: Boolean = args.isEmpty
    }
    final case class QuantifiedFormula(quantifier: Quantifier, variableList: Seq[TypedVariable], body: Formula) extends Formula {
      override def pretty: String = s"(${quantifier.pretty} [${variableList.map{case (n,t) => s"$n:${t.pretty}"}.mkString(",")}]: (${body.pretty}))"
      override def symbols: Set[String] = body.symbols
    }
    final case class Variable(name: String) extends Formula {
      override def pretty: String = name
      override def symbols: Set[String] = Set.empty
    }
    final case class UnaryFormula(connective: UnaryConnective, body: Formula) extends Formula {
      assert(connective == ~) // TODO: Ask Geoff about this.
      override def pretty: String = s"${connective.pretty} (${body.pretty})"
      override def symbols: Set[String] = body.symbols
    }
    final case class BinaryFormula(connective: BinaryConnective, left: Formula, right: Formula) extends Formula {
      override def pretty: String = s"(${left.pretty} ${connective.pretty} ${right.pretty})"
      override def symbols: Set[String] = left.symbols ++ right.symbols
    }
    final case class Tuple(elements: Seq[Formula]) extends Formula {
      override def pretty: String = s"[${elements.map(_.pretty).mkString(",")}]"
      override def symbols: Set[String] = elements.flatMap(_.symbols).toSet
    }
    final case class ConditionalTerm(condition: Formula, thn: Formula, els: Formula) extends Formula {
      override def pretty: String = s"$$ite(${condition.pretty}, ${thn.pretty}, ${els.pretty})"
      override def symbols: Set[String] = condition.symbols ++ thn.symbols ++ els.symbols
    }
    final case class LetTerm(typing: Map[String, Type], binding: Seq[(Formula, Formula)], body: Formula) extends Formula {
      override def pretty: String = s"$$let(...,${body.pretty})" // TODO
      override def symbols: Set[String] = Set.empty // TODO
    }
    final case class ConnectiveTerm(conn: Connective) extends Formula {
      override def pretty: String = s"(${conn.pretty})"
      override def symbols: Set[String] = Set.empty
    }
    final case class DistinctObject(name: String) extends Formula {
      override def pretty: String = {
        assert(name.startsWith("\"") && name.endsWith("\""), "Distinct object without enclosing double quotes.")
        s""""${escapeDistinctObject(name.tail.init)}""""
      }
      override def symbols: Set[String] = Set(name)
    }
    final case class NumberTerm(value: Number) extends Formula {
      override def pretty: String = value.pretty
      override def symbols: Set[String] = Set.empty
    }

    sealed abstract class Connective extends Pretty
    sealed abstract class UnaryConnective extends Connective
    final case object ~ extends UnaryConnective { override def pretty: String = "~" }
    final case object !! extends UnaryConnective { override def pretty: String = "!!" } // big pi
    final case object ?? extends UnaryConnective { override def pretty: String = "??" } // big sigma
    final case object @@+ extends UnaryConnective { override def pretty: String = "@@+" } // Choice
    final case object @@- extends UnaryConnective { override def pretty: String = "@@-" } // Description
    final case object @= extends UnaryConnective { override def pretty: String = "@=" } // Prefix equality

    sealed abstract class BinaryConnective extends Connective
    final case object Eq extends BinaryConnective { override def pretty: String = "=" }
    final case object Neq extends BinaryConnective { override def pretty: String = "!=" }
    // non-assoc
    final case object <=> extends BinaryConnective { override def pretty: String = "<=>" }
    final case object Impl extends BinaryConnective { override def pretty: String = "=>" }
    final case object <= extends BinaryConnective { override def pretty: String = "<=" }
    final case object <~> extends BinaryConnective { override def pretty: String = "<~>" }
    final case object ~| extends BinaryConnective { override def pretty: String = "~|" }
    final case object ~& extends BinaryConnective { override def pretty: String = "~&" }
    final case object := extends BinaryConnective { override def pretty: String = ":=" }
    // assoc
    final case object | extends BinaryConnective { override def pretty: String = "|" }
    final case object & extends BinaryConnective { override def pretty: String = "&" }
    final case object App extends BinaryConnective { override def pretty: String = "@" } // left-assoc
    // term-as-type
    final case object FunTyConstructor extends BinaryConnective { override def pretty: String = ">" }
    final case object ProductTyConstructor extends BinaryConnective { override def pretty: String = "*" }
    final case object SumTyConstructor extends BinaryConnective { override def pretty: String = "+" }

    sealed abstract class Quantifier extends Pretty
    final case object ! extends Quantifier { override def pretty: String = "!" } // All
    final case object ? extends Quantifier { override def pretty: String = "?" } // Exists
    final case object ^ extends Quantifier { override def pretty: String = "^" } // Lambda
    final case object @+ extends Quantifier { override def pretty: String = "@+" } // Choice
    final case object @- extends Quantifier { override def pretty: String = "@-" } // Description
    final case object !> extends Quantifier { override def pretty: String = "!>" } // type forall
    final case object ?* extends Quantifier { override def pretty: String = "?*" } // type exists
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // TFF AST
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  object TFF {
    type TypedVariable = (String, Type)

    sealed abstract class Statement extends Pretty {
      def symbols: Set[String]
    }
    final case class Typing(atom: String, typ: Type) extends Statement {
      override def pretty: String = {
        val escapedName = if (atom.startsWith("$") || atom.startsWith("$$")) atom else escapeAtomicWord(atom)
        s"$escapedName: ${typ.pretty}"
      }
      override def symbols: Set[String] = typ.symbols + atom
    }
    final case class Logical(formula: Formula) extends Statement {
      override def pretty: String = formula.pretty
      override def symbols: Set[String] = formula.symbols
    }

    sealed abstract class Formula extends Pretty {
      def symbols: Set[String]
    }
    final case class AtomicFormula(f: String, args: Seq[Term]) extends Formula  {
      override def pretty: String = {
        val escapedF = if (f.startsWith("$") || f.startsWith("$$")) f else escapeAtomicWord(f)
        if (args.isEmpty) escapedF else s"$escapedF(${args.map(_.pretty).mkString(",")})"
      }
      override def symbols: Set[String] = args.flatMap(_.symbols).toSet + f

      @inline def isUninterpretedFunction: Boolean = !isDefinedFunction && !isSystemFunction
      @inline def isDefinedFunction: Boolean = f.startsWith("$") && !isSystemFunction
      @inline def isSystemFunction: Boolean = f.startsWith("$$")
      @inline def isConstant: Boolean = args.isEmpty
    }
    final case class QuantifiedFormula(quantifier: Quantifier, variableList: Seq[TypedVariable], body: Formula) extends Formula {
      override def pretty: String = s"(${quantifier.pretty} [${variableList.map{case (n,t) => s"$n:${t.pretty}"}.mkString(",")}]: (${body.pretty}))"
      override def symbols: Set[String] = body.symbols
    }
    final case class UnaryFormula(connective: UnaryConnective, body: Formula) extends Formula {
      override def pretty: String = s"${connective.pretty} (${body.pretty})"
      override def symbols: Set[String] = body.symbols
    }
    final case class BinaryFormula(connective: BinaryConnective, left: Formula, right: Formula) extends Formula {
      override def pretty: String = s"(${left.pretty} ${connective.pretty} ${right.pretty})"
      override def symbols: Set[String] = left.symbols ++ right.symbols
    }
    final case class Equality(left: Term, right: Term) extends Formula {
      override def pretty: String = s"(${left.pretty} = ${right.pretty})"
      override def symbols: Set[String] = left.symbols ++ right.symbols
    }
    final case class Inequality(left: Term, right: Term) extends Formula {
      override def pretty: String = s"(${left.pretty} != ${right.pretty})"
      override def symbols: Set[String] = left.symbols ++ right.symbols
    }
    // Conditional only really makes sense in TFX. We don't support the full first-class Booleans for now.
    // Same for let-statements.

    sealed abstract class Term extends Pretty {
      def symbols: Set[String]
    }
    final case class AtomicTerm(f: String, args: Seq[Term]) extends Term  {
      override def pretty: String = {
        val escapedF = if (f.startsWith("$") || f.startsWith("$$")) f else escapeAtomicWord(f)
        if (args.isEmpty) escapedF else s"$escapedF(${args.map(_.pretty).mkString(",")})"
      }

      override def symbols: Set[String] = args.flatMap(_.symbols).toSet + f

      @inline def isUninterpretedFunction: Boolean = !isDefinedFunction && !isSystemFunction
      @inline def isDefinedFunction: Boolean = f.startsWith("$") && !isSystemFunction
      @inline def isSystemFunction: Boolean = f.startsWith("$$")
      @inline def isConstant: Boolean = args.isEmpty
    }
    final case class Variable(name: String) extends Term {
      override def pretty: String = name
      override def symbols: Set[String] = Set.empty
    }
    final case class DistinctObject(name: String) extends Term {
      override def pretty: String = {
        assert(name.startsWith("\"") && name.endsWith("\""), "Distinct object without enclosing double quotes.")
        s""""${escapeDistinctObject(name.tail.init)}""""
      }
      override def symbols: Set[String] = Set(name)
    }
    final case class NumberTerm(value: Number) extends Term {
      override def pretty: String = value.pretty
      override def symbols: Set[String] = Set.empty
    }
    final case class Tuple(elements: Seq[Term]) extends Term {
      override def pretty: String = s"[${elements.map(_.pretty).mkString(",")}]"
      override def symbols: Set[String] = elements.flatMap(_.symbols).toSet
    }

    sealed abstract class Connective extends Pretty
    sealed abstract class UnaryConnective extends Connective
    final case object ~ extends UnaryConnective { override def pretty: String = "~" }

    sealed abstract class BinaryConnective extends Connective
    // non-assoc
    final case object <=> extends BinaryConnective { override def pretty: String = "<=>" }
    final case object Impl extends BinaryConnective { override def pretty: String = "=>" }
    final case object <= extends BinaryConnective { override def pretty: String = "<=" }
    final case object <~> extends BinaryConnective { override def pretty: String = "<~>" }
    final case object ~| extends BinaryConnective { override def pretty: String = "~|" }
    final case object ~& extends BinaryConnective { override def pretty: String = "~&" }
    // assoc
    final case object | extends BinaryConnective { override def pretty: String = "|" }
    final case object & extends BinaryConnective { override def pretty: String = "&" }

    sealed abstract class Quantifier extends Pretty
    final case object ! extends Quantifier { override def pretty: String = "!" } // All
    final case object ? extends Quantifier { override def pretty: String = "?" } // Exists

    sealed abstract class Type extends Pretty {
      def symbols: Set[String]
    }
    final case class AtomicType(name: String, args: Seq[Type]) extends Type {
      override def pretty: String = if (args.isEmpty) name else s"$name(${args.map(_.pretty).mkString(",")})"
      override def symbols: Set[String] = args.flatMap(_.symbols).toSet + name
    }
    final case class MappingType(left: Seq[Type], right: Type) extends Type { // right-assoc
      override def pretty: String = if (left.length == 1) s"(${left.head.pretty} > ${right.pretty})"
      else s"((${left.map(_.pretty).mkString(" * ")}) > ${right.pretty})"
      override def symbols: Set[String] = left.flatMap(_.symbols).toSet ++ right.symbols
    }
    // TH1
    final case class QuantifiedType(variables: Seq[TypedVariable], body: Type) extends Type {
      override def pretty: String = s"!> [${variables.map(v => s"${v._1}: ${v._2.pretty}").mkString(",")}]: ${body.pretty}"
      override def symbols: Set[String] = body.symbols
    }
    final case class TypeVariable(name: String) extends Type {
      override def pretty: String = name
      override def symbols: Set[String] = Set.empty
    }
    // TFX
    final case class TupleType(components: Seq[Type]) extends Type {
      override def pretty: String = s"[${components.map(_.pretty).mkString(",")}]"
      override def symbols: Set[String] = components.flatMap(_.symbols).toSet
    }
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // TCF AST: TODO. Let's see if I will implement this at some point.
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

//  object TCF {
//    sealed abstract class Statement extends Pretty
//    final case class Typing(atom: String, typ: Type) extends Statement {
//      override def pretty: String = {
//        val escapedName = if (atom.startsWith("$") || atom.startsWith("$$")) atom else escapeAtomicWord(atom)
//        s"$escapedName: ${typ.pretty}"
//      }
//    }
//    final case class Logical(formula: Formula) extends Statement { override def pretty: String = formula.pretty }
//
//    sealed abstract class Formula extends Pretty
//
//  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // FOF AST
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  object FOF {
    sealed abstract class Statement extends Pretty {
      def symbols: Set[String]
    }
    final case class Logical(formula: Formula) extends Statement {
      override def pretty: String = formula.pretty
      override def symbols: Set[String] = formula.symbols
    }

    sealed abstract class Formula extends Pretty {
      def symbols: Set[String]
    }
    final case class AtomicFormula(f: String, args: Seq[Term]) extends Formula  {
      override def pretty: String = {
        val escapedF = if (f.startsWith("$") || f.startsWith("$$")) f else escapeAtomicWord(f)
        if (args.isEmpty) escapedF else s"$escapedF(${args.map(_.pretty).mkString(",")})"
      }

      override def symbols: Set[String] = args.flatMap(_.symbols).toSet + f

      @inline def isUninterpretedFunction: Boolean = !isDefinedFunction && !isSystemFunction
      @inline def isDefinedFunction: Boolean = f.startsWith("$") && !isSystemFunction
      @inline def isSystemFunction: Boolean = f.startsWith("$$")
      @inline def isConstant: Boolean = args.isEmpty
    }
    final case class QuantifiedFormula(quantifier: Quantifier, variableList: Seq[String], body: Formula) extends Formula {
      override def pretty: String = s"(${quantifier.pretty} [${variableList.mkString(",")}]: (${body.pretty}))"
      override def symbols: Set[String] = body.symbols
    }
    final case class UnaryFormula(connective: UnaryConnective, body: Formula) extends Formula {
      override def pretty: String = s"${connective.pretty} (${body.pretty})"
      override def symbols: Set[String] = body.symbols
    }
    final case class BinaryFormula(connective: BinaryConnective, left: Formula, right: Formula) extends Formula {
      override def pretty: String = s"(${left.pretty} ${connective.pretty} ${right.pretty})"
      override def symbols: Set[String] = left.symbols ++ right.symbols
    }
    final case class Equality(left: Term, right: Term) extends Formula {
      override def pretty: String = s"(${left.pretty} = ${right.pretty})"
      override def symbols: Set[String] = left.symbols ++ right.symbols
    }
    final case class Inequality(left: Term, right: Term) extends Formula {
      override def pretty: String = s"(${left.pretty} != ${right.pretty})"
      override def symbols: Set[String] = left.symbols ++ right.symbols
    }

    sealed abstract class Term extends Pretty {
      def symbols: Set[String]
    }
    final case class AtomicTerm(f: String, args: Seq[Term]) extends Term  {
      override def pretty: String = {
        val escapedF = if (f.startsWith("$") || f.startsWith("$$")) f else escapeAtomicWord(f)
        if (args.isEmpty) escapedF else s"$escapedF(${args.map(_.pretty).mkString(",")})"
      }

      override def symbols: Set[String] = args.flatMap(_.symbols).toSet + f

      @inline def isUninterpretedFunction: Boolean = !isDefinedFunction && !isSystemFunction
      @inline def isDefinedFunction: Boolean = f.startsWith("$") && !isSystemFunction
      @inline def isSystemFunction: Boolean = f.startsWith("$$")
      @inline def isConstant: Boolean = args.isEmpty
    }
    final case class Variable(name: String) extends Term {
      override def pretty: String = name
      override def symbols: Set[String] = Set.empty
    }
    final case class DistinctObject(name: String) extends Term {
      override def pretty: String = {
        assert(name.startsWith("\"") && name.endsWith("\""), "Distinct object without enclosing double quotes.")
        s""""${escapeDistinctObject(name.tail.init)}""""
      }
      override def symbols: Set[String] = Set(name)
    }
    final case class NumberTerm(value: Number) extends Term {
      override def pretty: String = value.pretty
      override def symbols: Set[String] = Set.empty
    }

    sealed abstract class Connective extends Pretty
    sealed abstract class UnaryConnective extends Connective
    final case object ~ extends UnaryConnective { override def pretty: String = "~" }

    sealed abstract class BinaryConnective extends Connective
    // non-assoc
    final case object <=> extends BinaryConnective { override def pretty: String = "<=>" }
    final case object Impl extends BinaryConnective { override def pretty: String = "=>" }
    final case object <= extends BinaryConnective { override def pretty: String = "<=" }
    final case object <~> extends BinaryConnective { override def pretty: String = "<~>" }
    final case object ~| extends BinaryConnective { override def pretty: String = "~|" }
    final case object ~& extends BinaryConnective { override def pretty: String = "~&" }
    // assoc
    final case object | extends BinaryConnective { override def pretty: String = "|" }
    final case object & extends BinaryConnective { override def pretty: String = "&" }

    sealed abstract class Quantifier extends Pretty
    final case object ! extends Quantifier { override def pretty: String = "!" } // All
    final case object ? extends Quantifier { override def pretty: String = "?" } // Exists
  }

  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////
  // CNF AST
  ////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////

  object CNF {
    sealed abstract class Statement extends Pretty {
      def symbols: Set[String]
    }
    final case class Logical(formula: Formula) extends Statement {
      override def pretty: String = formula.map(_.pretty).mkString(" | ")
      override def symbols: Set[String] = formula.flatMap(_.symbols).toSet
    }

    type Formula = Seq[Literal]

    sealed abstract class Literal extends Pretty {
      def symbols: Set[String]
    }
    final case class PositiveAtomic(formula: AtomicFormula) extends Literal {
      override def pretty: String = formula.pretty
      override def symbols: Set[String] = formula.symbols
    }
    final case class NegativeAtomic(formula: AtomicFormula) extends Literal {
      override def pretty: String = s"~ ${formula.pretty}"
      override def symbols: Set[String] = formula.symbols
    }
    final case class Equality(left: Term, right: Term) extends Literal {
      override def pretty: String = s"${left.pretty} = ${right.pretty}"
      override def symbols: Set[String] = left.symbols ++ right.symbols
    }
    final case class Inequality(left: Term, right: Term) extends Literal {
      override def pretty: String = s"${left.pretty} != ${right.pretty}"
      override def symbols: Set[String] = left.symbols ++ right.symbols
    }

    final case class AtomicFormula(f: String, args: Seq[Term]) extends Pretty  {
      override def pretty: String = {
        val escapedF = if (f.startsWith("$") || f.startsWith("$$")) f else escapeAtomicWord(f)
        if (args.isEmpty) escapedF else s"$escapedF(${args.map(_.pretty).mkString(",")})"
      }

      def symbols: Set[String] = args.flatMap(_.symbols).toSet + f

      @inline def isUninterpretedFunction: Boolean = !isDefinedFunction && !isSystemFunction
      @inline def isDefinedFunction: Boolean = f.startsWith("$") && !isSystemFunction
      @inline def isSystemFunction: Boolean = f.startsWith("$$")
      @inline def isConstant: Boolean = args.isEmpty
    }

    sealed abstract class Term extends Pretty {
      def symbols: Set[String]
    }
    final case class AtomicTerm(f: String, args: Seq[Term]) extends Term  {
      override def pretty: String = {
        val escapedF = if (f.startsWith("$") || f.startsWith("$$")) f else escapeAtomicWord(f)
        if (args.isEmpty) escapedF else s"$escapedF(${args.map(_.pretty).mkString(",")})"
      }

      override def symbols: Set[String] = args.flatMap(_.symbols).toSet + f

      @inline def isUninterpretedFunction: Boolean = !isDefinedFunction && !isSystemFunction
      @inline def isDefinedFunction: Boolean = f.startsWith("$") && !isSystemFunction
      @inline def isSystemFunction: Boolean = f.startsWith("$$")
      @inline def isConstant: Boolean = args.isEmpty
    }
    final case class Variable(name: String) extends Term {
      override def pretty: String = name
      override def symbols: Set[String] = Set.empty
    }
    final case class DistinctObject(name: String) extends Term {
      override def pretty: String = {
        assert(name.startsWith("\"") && name.endsWith("\""), "Distinct object without enclosing double quotes.")
        s""""${escapeDistinctObject(name.tail.init)}""""
      }
      override def symbols: Set[String] = Set(name)
    }
  }
}
