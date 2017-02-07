package leo.datastructures

import leo.modules.output.Output


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
  @inline lazy val weight: Int = leo.Configuration.CLAUSEPROXY_WEIGHTING.weightOf(this)
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
  private var counter: Long = 0

  def apply(cl: Clause, r: Role, annotation: ClauseAnnotation, propFlag: ClauseAnnotation.ClauseProp): AnnotatedClause = {
    counter += 1
    AnnotatedClause(counter, cl, r, annotation, propFlag)
  }

  def apply(cl: Clause, annotation: ClauseAnnotation, propFlag: ClauseAnnotation.ClauseProp = ClauseAnnotation.PropNoProp): AnnotatedClause =
    apply(cl, Role_Plain, annotation, propFlag)
}

abstract sealed class ClauseAnnotation extends Pretty {
  /** A [[leo.modules.calculus.CalculusRule]] that generated the AnnotatedClause annotated with the
    * ClauseAnnotation. May be `null` if no such rule is known. */
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
        cw.id  + ":[" + add.apply + "]"
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

  case object NoAnnotation extends ClauseAnnotation {
    final val pretty: String = ""
    final val fromRule: CalculusRule = null
    final def parents = Seq.empty
  }
  case class FromFile(fileName: String, formulaName: String) extends ClauseAnnotation {
    final def pretty = s"file('$fileName',$formulaName)"
    final val fromRule: CalculusRule = null
    final def parents = Seq.empty
  }
  case class FromSystem(hint: String) extends ClauseAnnotation {
    final def pretty = s"introduced($hint)"
    final val fromRule: CalculusRule = null
    final def parents = Seq.empty
  }

  type ClauseProp = Int
  final val PropNoProp: ClauseProp = 0
  final val PropUnified: ClauseProp = 1
  final val PropBoolExt: ClauseProp = 2
  final val PropSOS: ClauseProp = 4
  final val PropNeedsUnification: ClauseProp = 8

  final def prettyProp(prop: ClauseProp): String = {
    val sb = new StringBuilder
    if (isPropSet(PropUnified, prop)) sb.append(" U ")
    if (isPropSet(PropBoolExt, prop)) sb.append(" BE ")
    if (isPropSet(PropSOS, prop)) sb.append(" SOS ")
    if (isPropSet(PropNeedsUnification, prop)) sb.append(" NU ")
    sb.toString()
  }
}


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
