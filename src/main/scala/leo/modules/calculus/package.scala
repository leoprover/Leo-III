package leo.modules

import leo.datastructures.{Term, Clause}
import leo.modules.output.SuccessSZS

/**
 * Created by lex on 20.05.15.
 */
package object calculus {
  trait CalculusRule {
    def name: String
    def inferenceStatus: Option[SuccessSZS] = None
  }

  trait CalculusHintRule[Hint] extends CalculusRule {
    type HintType = Hint
  }

  trait UnaryCalculusRule[Res] extends (Clause => Res) with CalculusRule {
    def canApply(cl: Clause): Boolean
  }

  trait PolyadicCalculusRule[Res] extends ((Clause, Set[Clause]) => Res) with CalculusRule {
    def canApply(cl: Clause, cls: Set[Clause]): Boolean
  }

  trait UnaryCalculusHintRule[Res, Hint] extends ((Clause, Hint) => Res) with CalculusHintRule[Hint] {
    def canApply(cl: Clause): (Boolean, Hint)
  }

  trait BinaryCalculusRule[Res, Hint] extends ((Clause, Clause, Hint) => Res) with CalculusHintRule[Hint] {
    def canApply(cl1: Clause, cl2: Clause): (Boolean, Hint)
  }


  def mayUnify(s: Term, t: Term) = mayUnify0(s,t,5)

  protected def mayUnify0(s: Term, t: Term, depth: Int): Boolean = {
    if (s == t) return true
    if (s.freeVars.isEmpty && t.freeVars.isEmpty) return false // contains to vars, cannot be unifiable
    if (depth <= 0) return true
    if (s.ty != t.ty) return false
    if (s.headSymbol.ty != t.headSymbol.ty) return false


    // Match case on head symbols:
    // flex-flex always works*, flex-rigid also works*, rigid-rigid only in same symbols
    // * = if same type
    if (!s.headSymbol.isVariable && !t.headSymbol.isVariable) {
      //        println("rigid-rigid case")
      import leo.datastructures.Term._
      // rigid-rigid
      (s,t) match {
        case (Symbol(id1), Symbol(id2)) => id1 == id2
        case (Symbol(_), _) => false
        case (_, Symbol(_)) => false
        case (f1 ∙ args1, f2 ∙ args2) if args1.length > 0 && args2.length > 0 => mayUnify0(f1, f2, depth -1) && args1.zip(args2).forall{_ match {
          case (Left(t1), Left(t2)) => mayUnify0(t1, t2, depth -1)
          case (Right(ty1), Right(ty2)) => ty1 == ty2
          case _ => false
        } } // TODO: Do we need the first part? f1, f2 should be atoms
        case (_ :::> body1, _ :::> body2) => mayUnify0(body1, body2, depth)
        case _ => false
      }
    } else {
      true
    }
  }

}
