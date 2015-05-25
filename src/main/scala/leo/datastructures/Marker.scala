package leo.datastructures

import leo.datastructures.blackboard.FormulaStore


/////////////////////////////////////////////
// Collection of potentially globally used
// markers/properties.
/////////////////////////////////////////////

/**
 * Formula roles as described by TPTP.
 *
 * @author Alexander Steen
 * @since 11.11.2014
 * @see [[http://www.cs.miami.edu/~tptp/TPTP/SyntaxBNF.html]]
 */
sealed abstract class Role extends Pretty

/**
 * `Role_Axiom`s are accepted, without proof. There is no guarantee that the
 * axioms of a problem are consistent.
 */
case object Role_Axiom extends Role {
  final val pretty = "axiom"
}

/**
 * `Role_Definition`s are intended to define symbols. They are either universally
 * quantified equations, or universally quantified equivalences with an
 * atomic lefthand side.
 */
case object Role_Definition extends Role {
  final val pretty = "definition"
}

/**
 * `Role_Assumption`s can be used like axioms, but must be discharged before a
 * derivation is complete.
 */
case object Role_Assumption extends Role {
  final val pretty = "assumption"
}

/**
 * `Role_Conjecture`s are to be proven from the "axiom"(-like) formulae. A problem
 * is solved only when all "conjecture"s are proven.
 */
case object Role_Conjecture extends Role {
  final val pretty = "conjecture"
}

/**
 * `Negated_Conjecture`s are formed from negation of a "conjecture" (usually
 * in a FOF to CNF conversion).
 */
case object Role_NegConjecture extends Role {
  final val pretty = "negated_conjecture"
}

/**
 * `Role_Type`s define the type globally for one symbol.
 */
case object Role_Type extends Role {
  final val pretty = "type"
}

/**
 * `Role_Plain`s have no specified user semantics.
 */
case object Role_Plain extends Role {
  final val pretty = "plain"
}

/**
 * `Role_Unknown`s are considered an error.
 */
case object Role_Unknown extends Role {
  final val pretty = "unknown"
}

object Role {
  def apply(role: String): Role = role.trim match {
    case "axiom" => Role_Axiom
    case "hypothesis" => Role_Axiom // Note: Implicit mapping to axiom
    case "definition" => Role_Definition
    case "assumption" => Role_Assumption
    case "lemma" => Role_Axiom // Note: Implicit mapping to axiom
    case "theorem" => Role_Axiom // Note: Implicit mapping to axiom
    case "conjecture" => Role_Conjecture
    case "negated_conjecture" => Role_NegConjecture
    case "plain" => Role_Plain
    case "type" => Role_Type
    case "unknown" => Role_Unknown
    case _ => Role_Unknown // Note: fi_* roles are not handled at the moment
  }
}


//////////////////////////////////////////////
//////////////////////////////////////////////


abstract sealed class ClauseOrigin extends Ordered[ClauseOrigin] {
  protected[ClauseOrigin] def priority: Int

  def compare(that: ClauseOrigin) = this.priority - that.priority
}
case object FromAxiom extends ClauseOrigin { val priority = 3 }
case object FromConjecture extends ClauseOrigin { val priority = 2 }
case object Derived extends ClauseOrigin { val priority = 1 }


abstract sealed class ClauseAnnotation extends Pretty
case class InferredFrom(rule: leo.modules.calculus.CalculusRule, fs: Set[FormulaStore]) extends ClauseAnnotation {
  def pretty: String = s"inference(${rule.name},[${rule.inferenceStatus.fold("")("status("+_.pretty.toLowerCase+")")}],[${fs.map(_.name).mkString(",")}])"
}
case object NoAnnotation extends ClauseAnnotation {
  val pretty: String = ""
}
case class FromFile(fileName: String, formulaName: String) extends ClauseAnnotation {
  def pretty = s"file('$fileName',$formulaName)"
}

object ClauseAnnotation {
  def apply(rule: leo.modules.calculus.CalculusRule, cls: Set[FormulaStore]): ClauseAnnotation =
    new InferredFrom(rule, cls)

  def apply(rule: leo.modules.calculus.CalculusRule, cl: FormulaStore): ClauseAnnotation =
    new InferredFrom(rule, Set(cl))

  def apply(file: String, name: String): ClauseAnnotation = new FromFile(file, name)
}


//////////////////////////////////////////////
//////////////////////////////////////////////


abstract sealed class Indexing
case object INDEXED extends Indexing
case object PLAIN extends Indexing


//////////////////////////////////////////////
//////////////////////////////////////////////


abstract sealed class Locality
case object GLOBAL extends Locality
case object LOCAL extends Locality


//////////////////////////////////////////////
//////////////////////////////////////////////


/**
 * Marker type for the 'language level' of terms.
 * A Term is flagged `PROPOSITIONAL` iff it is a propositional formula,
 * analogously for `FIRSTORDER` and `HIGHERORDER`.
 *
 * @author Alexander Steen
 */
sealed abstract class LangOrder extends Ordered[LangOrder]

case object Lang_Prop extends LangOrder {
  def compare(that: LangOrder) = that match {
    case Lang_Prop => 0
    case _ => -1
  }
}

case object Lang_FO extends LangOrder {
  def compare(that: LangOrder) = that match {
    case Lang_Prop => 1
    case Lang_FO => 0
    case Lang_ManySortedFO | Lang_HO => -1
  }
}

case object Lang_ManySortedFO extends LangOrder {
  def compare(that: LangOrder) = that match {
    case Lang_Prop | Lang_FO => 1
    case Lang_ManySortedFO => 0
    case Lang_HO => -1
  }
}

case object Lang_HO extends LangOrder {
  def compare(that: LangOrder) = that match {
    case Lang_HO => 0
    case _ => 1
  }
}


//////////////////////////////////////////////
//////////////////////////////////////////////
