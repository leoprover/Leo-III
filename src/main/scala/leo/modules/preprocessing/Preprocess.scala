package leo.modules.preprocessing

import leo.datastructures._
import leo.datastructures.ClauseAnnotation.{InferredFrom, NoAnnotation}
import leo.modules.calculus.{CalculusRule, ReplaceAndrewsEq, ReplaceLeibnizEq}
import leo.modules.output.{SZS_Theorem, SuccessSZS}

/**
  * Applies a preprocessing exhaustively to all clauses.
  *
  * @author Max Wisniewski
  * @since 6/5/16
  */
object Preprocess {


  def argumentExtraction(cs : Set[AnnotatedClause])(implicit sig: Signature) : Set[AnnotatedClause] = {
    var open : Set[AnnotatedClause] = cs
    var ready : Set[AnnotatedClause] = Set()
    while(open.nonEmpty) {
      val it = open.iterator
      open = Set()
      while(it.hasNext){
        val c = it.next()
        val (cl1, terms) = ArgumentExtraction(c.cl)(sig)
        val cs : Set[AnnotatedClause] = terms.map { case (l, r) => AnnotatedClause(Clause(Literal(l, r, true)), Role_Plain, NoAnnotation, ClauseAnnotation.PropNoProp) }
        open = cs ++ open
        val c2 = if (terms.nonEmpty) AnnotatedClause(cl1, c.role, InferredFrom(ExtractedArgument, cs+c), ClauseAnnotation.PropNoProp) else c
        ready += c2
      }
    }
    ready
  }

  def formulaRenaming(cs : Set[AnnotatedClause])(implicit sig: Signature) : Set[AnnotatedClause] = {
    cs.flatMap{c =>
      val (c1, c1s) = FormulaRenaming(c.cl)(sig)
      val c2s : Set[AnnotatedClause] = (c1s map {c2 => AnnotatedClause(c2, Role_Plain, NoAnnotation, ClauseAnnotation.PropNoProp)}).toSet
      val c2 = if(c1s.nonEmpty) AnnotatedClause(c1, c.role, InferredFrom(FormulaRenamed, c2s+c), ClauseAnnotation.PropNoProp) else c
      c2s + c2
    }
  }


  def equalityExtraction(cs : Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = {
    cs map {c =>
      val (b1, subst1) = ReplaceAndrewsEq.canApply(c.cl)
      val cl1 = if(b1) ReplaceAndrewsEq(c.cl, subst1)._1 else c.cl
      val (b2, subst2) = ReplaceLeibnizEq.canApply(cl1)(sig)
      val cl2 = if(b2) ReplaceLeibnizEq(cl1, subst2)._1 else cl1
      if (b1 || b2)
        AnnotatedClause(cl2, c.role, InferredFrom(ReplaceEQ,c), ClauseAnnotation.PropNoProp)
      else
        c
    }
  }
  private object ReplaceEQ extends CalculusRule {
    override val name : String = "replace_equality"
    override val inferenceStatus : Option[SuccessSZS] = Some(SZS_Theorem)
  }
  private object ExtractedArgument extends CalculusRule {
    override val name : String = "argument_extraction"
    override val inferenceStatus : Option[SuccessSZS] = Some(SZS_Theorem)
  }
  private object FormulaRenamed extends CalculusRule {
    override val name : String = "formula_renamed"
    override val inferenceStatus : Option[SuccessSZS] = Some(SZS_Theorem)
  }
}
