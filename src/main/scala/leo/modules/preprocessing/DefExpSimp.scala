package leo.modules.preprocessing

import leo.datastructures.{Clause, Literal, Term,Signature}
import leo.modules.calculus.{Simp, CalculusRule}
import leo.modules.output.SZS_Theorem

/**
  * Created by lex on 5/12/16.
  */
  object DefExpSimp extends CalculusRule {
    override val name = "defexp_and_simp_and_etaexpand"
    override val inferenceStatus = Some(SZS_Theorem)
    def apply(t: Term)(implicit sig: Signature): Term = {
      val symb: Set[Signature#Key] = Set(sig("?").key, sig("&").key, sig("=>").key)
      Simplification.normalize(t.δ_expand_upTo(symb).betaNormalize.etaExpand)
    }

    def apply(cl: Clause)(implicit sig: Signature): Clause = {
      val litsIt = cl.lits.iterator
      var newLits: Seq[Literal] = Seq()
      while (litsIt.hasNext) {
        val lit = litsIt.next()
        if (lit.equational) {
          newLits = newLits :+ Simp(Literal.mkOrdered(apply(lit.left), apply(lit.right), lit.polarity)(sig))
        } else {
          newLits = newLits :+ Simp(Literal(apply(lit.left), lit.polarity))
        }
      }
      Clause(newLits)
    }
  }

