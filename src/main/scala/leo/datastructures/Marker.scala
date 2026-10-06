package leo.datastructures

import leo.datastructures.TPTP.AnnotatedFormula.FormulaType.FormulaType
import leo.modules.output.Output

import scala.annotation.tailrec

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




trait ClauseProxy extends Pretty with Prettier {
  def id: Long
  def cl: Clause
  def role: Role
  def annotation: ClauseAnnotation
  def properties: ClauseAnnotation.ClauseProp
  override final def pretty: String = s"[$id]:\t${cl.pretty}\t(${annotation.pretty}) (Flags: ${ClauseAnnotation.prettyProp(properties)})"
  override final def pretty(sig: Signature): String = s"[$id]:\t${cl.pretty(sig)}\t(${annotation.pretty}) (Flags: ${ClauseAnnotation.prettyProp(properties)})"
}

case class AnnotatedClause(id: Long, cl: Clause, role: Role, annotation: ClauseAnnotation,
                           var properties: ClauseAnnotation.ClauseProp) extends ClauseProxy {
  override def equals(o: Any): Boolean = o match {
    case cw: ClauseProxy => cw.id == id // Equality is defined in term of id. This implies that the clauses of two
                                        // annotatedclauses may be equal while the annotatedclauses are not equal.
                                        // This is done due to performance considerations: Clause equality is quite
                                        // expensive to calculate. On the other hand, the unprocessed set will
                                        // contain duplicated clauses now, this will nevertheless be handled by
                                        // subsumption. This way, equality on clauses is only calculated for
                                        // selected clauses.
    case _ => false
  }

  override def hashCode(): Int = id.hashCode()
}

object AnnotatedClause {
  @volatile private var counter: Long = 0

  def apply(cl: Clause, r: Role, annotation: ClauseAnnotation, propFlag: ClauseAnnotation.ClauseProp): AnnotatedClause = {
    counter += 1 // lets try it without sync ... see what happens
    AnnotatedClause(counter, cl, r, annotation, propFlag)
  }

  def apply(cl: Clause, annotation: ClauseAnnotation, propFlag: ClauseAnnotation.ClauseProp = ClauseAnnotation.PropNoProp): AnnotatedClause =
    apply(cl, Role_Plain, annotation, propFlag)
}

abstract sealed class ClauseAnnotation extends Pretty {
  def fromRule: leo.modules.calculus.CalculusRule
  def parents: Seq[_ <: ClauseProxy]
}

object ClauseAnnotation {
  import leo.modules.calculus.CalculusRule
  case class InferredFrom[A <: ClauseProxy](rule: leo.modules.calculus.CalculusRule, cws: Seq[(A, Output)]) extends ClauseAnnotation {
    def pretty: String = s"inference(${rule.name},[status(${rule.inferenceStatus.pretty.toLowerCase})],[${
      cws.map { case (cw, add) => if (add == null) {
        cw.id
      } else {
        s"${cw.id}:[${add.apply()}]"
      }
      }.mkString(",")
    }])"

    def fromRule: CalculusRule = rule
    def parents = cws.map(_._1)
  }
  object InferredFrom {
    def apply[A <: ClauseProxy](rule: leo.modules.calculus.CalculusRule, cs: Seq[A]): ClauseAnnotation =
      InferredFrom(rule, cs.map((_, null)))

    def apply[A <: ClauseProxy](rule: leo.modules.calculus.CalculusRule, c: A): ClauseAnnotation =
      InferredFrom(rule, Seq((c,null)))
  }

  /**
    * A rule that compresses a linear sequence of CalculusRule Applications to
    * one singe application, queuing all of the Rules together.
    *
    */
  case class CompressedRule[A <: ClauseProxy](rules : Seq[(leo.modules.calculus.CalculusRule, Output)], annotation : ClauseAnnotation) extends ClauseAnnotation {
    final val pretty : String = {
      val sb : StringBuilder = new StringBuilder
      val remainingBrackets : StringBuilder = new StringBuilder
      val it = rules.iterator
      while(it.hasNext){
        val (rule, info) = it.next()
        remainingBrackets.append(')')
        sb.append("inference(")
        sb.append(rule.name)
        sb.append(",[status(")
        sb.append(rule.inferenceStatus.pretty.toLowerCase)
        sb.append(")]")
        if(info != null && info.apply() != "") remainingBrackets.insert(0, s":[${info.apply()}]")
        sb.append(", ")
      }
      sb.append(annotation.pretty)
      sb.append(remainingBrackets)
      sb.toString()
    }
    final val fromRule : CalculusRule = annotation.fromRule
    final def parents = annotation.parents
  }

  case object NoAnnotation extends ClauseAnnotation {
    final val pretty: String = ""
    final val fromRule: CalculusRule = null
    final def parents = Seq.empty
  }
  case class FromFile(fileName: String, formulaName: String) extends ClauseAnnotation {
    final def pretty = s"file('$fileName',${leo.modules.output.escapeTPTPName(formulaName)})"
    final val fromRule: CalculusRule = null
    final def parents = Seq.empty
  }
  case class FromSystem[A <: ClauseProxy](hint: String, parents: Seq[A]) extends ClauseAnnotation {
    final def pretty = s"introduced($hint)"
    final val fromRule: CalculusRule = null
  }

  type ClauseProp = Int
  final val PropNoProp: ClauseProp = 0
  final val PropUnified: ClauseProp = 1
  final val PropBoolExt: ClauseProp = 2
  final val PropSOS: ClauseProp = 4
  final val PropNeedsUnification: ClauseProp = 8
  final val PropFuncExt: ClauseProp = 16
  final val PropShallowSimplified: ClauseProp = 32
  final val PropFullySimplified: ClauseProp = 64

  final def prettyProp(prop: ClauseProp): String = {
    val sb = new StringBuilder
    if (isPropSet(PropUnified, prop)) sb.append(" U ")
    if (isPropSet(PropBoolExt, prop)) sb.append(" BE ")
    if (isPropSet(PropSOS, prop)) sb.append(" SOS ")
    if (isPropSet(PropNeedsUnification, prop)) sb.append(" NU ")
    if (isPropSet(PropFuncExt, prop)) sb.append(" FE ")

    if (isPropSet(PropShallowSimplified, prop)) sb.append(" S- ")
    else if (isPropSet(PropFullySimplified, prop)) sb.append(" S+ ")

    sb.toString()
  }
}


//////////////////////////////////////////////
//////////////////////////////////////////////


/**
  * Marker type for the 'language level' of input problems. Used in the prover state to record
  * what kind of problem was given to the system. Can be one of:
  *   - [[Lang_Prop]] for CNF problems,
  *   - [[Lang_FO]] for FOF problems,
  *   - [[Lang_ManySortedFO]] for TFF problems,
  *   - [[Lang_HO]] for TH0 problems,
  *   - [[Lang_Mixed]] for mixed input problems (the maximal level is recorded as argument), and
  *   - [[Lang_Unknown]] for an (yet) unknown kind of problem (replaces null value).
  *
  * @author Alexander Steen
 */
sealed abstract class LanguageLevel extends TotalPreorder[LanguageLevel] with Pretty {
  def flatten: LanguageLevel = this
}
object LanguageLevel {
  final def fromFormulaType(formulaType: FormulaType): LanguageLevel = formulaType match {
    case leo.datastructures.TPTP.AnnotatedFormula.FormulaType.THF => Lang_HO
    case leo.datastructures.TPTP.AnnotatedFormula.FormulaType.TFF => Lang_ManySortedFO
    case leo.datastructures.TPTP.AnnotatedFormula.FormulaType.FOF => Lang_FO
    case leo.datastructures.TPTP.AnnotatedFormula.FormulaType.CNF => Lang_Prop
    case leo.datastructures.TPTP.AnnotatedFormula.FormulaType.TPI => Lang_Unknown
  }
}

case object Lang_Prop extends LanguageLevel {
  @tailrec final def compare(that: LanguageLevel): Int = that match {
    case Lang_Unknown => 1
    case Lang_Prop => 0
    case Lang_Mixed(greatest) => compare(greatest)
    case _ => -1
  }
  final def pretty: String = "propositional (TPTP CNF)"
}

case object Lang_FO extends LanguageLevel {
  @tailrec final def compare(that: LanguageLevel): Int = that match {
    case Lang_Unknown => 1
    case Lang_Prop => 1
    case Lang_FO => 0
    case Lang_ManySortedFO | Lang_HO => -1
    case Lang_Mixed(greatest) => compare(greatest)
  }
  final def pretty: String = "first-order (TPTP FOF)"
}

case object Lang_ManySortedFO extends LanguageLevel {
  @tailrec final def compare(that: LanguageLevel): Int = that match {
    case Lang_Prop | Lang_FO | Lang_Unknown => 1
    case Lang_ManySortedFO => 0
    case Lang_HO => -1
    case Lang_Mixed(greatest) => compare(greatest)
  }
  final def pretty: String = "typed first-order (TPTP TFF)"
}

case object Lang_HO extends LanguageLevel {
  @tailrec final def compare(that: LanguageLevel): Int = that match {
    case Lang_HO => 0
    case Lang_Mixed(greatest) => compare(greatest)
    case _ => 1
  }
  final def pretty: String = "higher-order (TPTP THF)"
}

final case class Lang_Mixed(greatest: LanguageLevel) extends LanguageLevel {
  def compare(that: LanguageLevel): Int = greatest.compare(that)

  def pretty: String = s"mixed-order [with most expressive language: ${greatest.pretty}]"
  override def flatten: LanguageLevel = greatest
}

case object Lang_Unknown extends LanguageLevel {
  final def compare(that: LanguageLevel): Int = that match {
    case Lang_Unknown => 0
    case _ => -1
  }
  def pretty: String = s"of unsupported format"
}


//////////////////////////////////////////////
//////////////////////////////////////////////
