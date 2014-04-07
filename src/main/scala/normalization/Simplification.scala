package normalization

import tptp.Commons._
import tptp._
import tptp.fof.Logical

/**
 *
 * Simple object, that removes syntactic tatuologies
 * and idempotent operations.
 *
 *
 * Created by Max Wisniewski on 4/7/14.
 */
object Simplification extends AbstractNormalize{

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula: AnnotatedFormula): AnnotatedFormula = formula match {
    case TPIAnnotated(name, role, formula, anno) => TPIAnnotated(name, role, normalizeFOF(formula), anno)
    case THFAnnotated(name, role, formula, anno) => THFAnnotated(name, role, normalizeTHF(formula), anno)
    case TFFAnnotated(name, role, formula, anno) => TFFAnnotated(name, role, normalizeTFF(formula), anno)
    case FOFAnnotated(name, role, formula, anno) => TPIAnnotated(name, role, normalizeFOF(formula), anno)
    case CNFAnnotated(name, role, formula, anno) => CNFAnnotated(name, role, normalizeCNF(formula), anno)
  }

  /**
   * Simplification is always applicable. Hence this is constantly true
   *
   * @param formula - Formula to be checked
   * @return True
   */
  override def applicable(formula: AnnotatedFormula): Boolean = true


  private def normalizeFOF(formula : fof.Formula) : fof.Formula = formula match {
    case Logical(form) => Logical(normalizeFOFLogical(form))
    case a            => a    // Cannot be normalized I guess
  }

  private def normalizeFOFLogical(form : fof.LogicFormula) : fof.LogicFormula = form match {
    case fof.Binary(left, op, right) => fof.Binary(left, op, right)
    case fof.Unary(op, right) => fof.Unary(op, right)
    case fof.Quantified(quant, vars, formulas) => fof.Quantified(quant, vars, formulas)
    case fof.Atomic(atom) => fof.Atomic(atom)
    case fof.Inequality(left, right) => fof.Inequality(left, right)
  }

  private def normalizeTHF(formula : thf.Formula) : thf.Formula = formula

  private def normalizeTFF(formula : tff.Formula) : tff.Formula = formula

  private def normalizeCNF(formula : cnf.Formula) : cnf.Formula = formula
}
