package normalization

import datastructures.tptp.Commons._
import datastructures.tptp._
import datastructures.tptp.fof.Logical

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
    case FOFAnnotated(name, role, formula, anno) => FOFAnnotated(name, role, normalizeFOF(formula), anno)
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

  private val trueV = fof.Atomic(Commons.DefinedPlain(Commons.DefinedFunc("$true", List())))
  private val falseV = fof.Atomic(Commons.DefinedPlain(Commons.DefinedFunc("$false", List())))

  private def cTrue(f : fof.LogicFormula) : Boolean = f match {
      case fof.Atomic(atom) => atom match {
        case Commons.SystemPlain(sysfun) => sysfun match{
            case Commons.SystemFunc(name, _) =>  name == "$true"
            case _                          => false
        }
        case Commons.DefinedPlain(data) => data match {
            case DefinedFunc(name, _) => name == "$true"
            case _  => false
        }
        case _  => false
      }
      case _    => false
  }


  private def cFalse(f : fof.LogicFormula) : Boolean = f match {
    case fof.Atomic(atom) => atom match {
      case Commons.SystemPlain(sysfun) => sysfun match{
        case Commons.SystemFunc(name, _) =>  name == "$false"
        case _                          => false
      }
      case Commons.DefinedPlain(data) => data match {
        case DefinedFunc(name, _) => name == "$false"
        case _  => false
      }
      case _  => false
    }
    case _    => false
  }

  private def filterFree(f : fof.LogicFormula, vars : List[Variable]) : List[Variable] = f match {
    case fof.Binary(left, _, right) => filterFree(left, filterFree(right, vars))
    case fof.Unary(_, form) => filterFree(form,vars)
    case fof.Quantified(_,bvars, form) => vars filterNot bvars.contains
    case fof.Atomic(_)  => vars
    case fof.Inequality(_, _) => vars
  }

  private def normalizeFOFLogical(form : fof.LogicFormula) : fof.LogicFormula = form match {
    case fof.Binary(left, op, right) => {
      val leftR = normalizeFOFLogical(left)
      val rightR = normalizeFOFLogical(right)
      op match{
        case fof.& if leftR == rightR => leftR
        case fof.& if leftR == fof.Unary(fof.Not, rightR) || rightR == fof.Unary(fof.Not, leftR) => falseV
        case fof.& if cTrue(leftR) => leftR
        case fof.& if cTrue(rightR) => rightR
        case fof.& if cFalse(leftR) || cFalse(rightR) => falseV

        case fof.<=> if leftR == rightR => trueV
        case fof.<=> if cTrue(rightR) => leftR
        case fof.<=> if cTrue(leftR) => rightR
        case fof.<=> if cFalse(leftR) => fof.Unary(fof.Not, rightR)
        case fof.<=> if cFalse(rightR) => fof.Unary(fof.Not, leftR)

        case fof.Impl if cTrue(rightR) => trueV
        case fof.Impl if cFalse(rightR) => fof.Unary(fof.Not, rightR)
        case fof.Impl if rightR == leftR => trueV
        case fof.Impl if cTrue(leftR) => rightR
        case fof.Impl if cFalse(leftR) => trueV

        case fof.| if leftR == rightR => leftR
        case fof.| if leftR == fof.Unary(fof.Not, rightR) || rightR == fof.Unary(fof.Not, leftR) => trueV
        case fof.| if cTrue(leftR) || cTrue(rightR) => trueV
        case fof.| if cFalse(leftR) => rightR
        case fof.| if cFalse(rightR) => leftR

        case _ => fof.Binary(leftR, op, rightR)
      }

    }
    case fof.Unary(fof.Not, fof.Unary(fof.Not, right)) => right     // Remove double negation
    case fof.Unary(fof.Not, f) if cTrue(f) => falseV                        // Negate top and bottom
    case fof.Unary(fof.Not, f) if cFalse(f) => trueV

    case fof.Quantified(quant, vars, formulas) => {
      val formulaR = normalizeFOFLogical(formulas)
      val nvars = vars filterNot filterFree(formulaR, vars).contains
      if (nvars.isEmpty) formulaR else fof.Quantified(quant, nvars, formulaR)
    }
    case a => a                                                     // If no rule matches
  }

  private def normalizeTHF(formula : thf.Formula) : thf.Formula = formula

  private def normalizeTFF(formula : tff.Formula) : tff.Formula = formula

  private def normalizeCNF(formula : cnf.Formula) : cnf.Formula = formula
}
