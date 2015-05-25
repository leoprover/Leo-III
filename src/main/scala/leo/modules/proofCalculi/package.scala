package leo.modules

import leo.datastructures.Clause
import leo.modules.output.SuccessSZS

/**
 * Created by lex on 20.05.15.
 */
package object proofCalculi {
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

  trait UnaryCalculusHintRule[Res, Hint] extends ((Clause, Hint) => Res) with CalculusHintRule[Hint] {
    def canApply(cl: Clause): (Boolean, Hint)
  }

  trait BinaryCalculusRule[Res, Hint] extends ((Clause, Clause, Hint) => Res) with CalculusHintRule[Hint] {
    def canApply(cl1: Clause, cl2: Clause): (Boolean, Hint)
  }
}
