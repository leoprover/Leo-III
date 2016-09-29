package leo.modules.preprocessing

import leo.datastructures.{Clause, Literal, Term}
import leo.datastructures.impl.SignatureImpl
import leo.modules.calculus.Simp
import leo.modules.output.SZS_Theorem

/**
  * Created by lex on 5/12/16.
  */
  object DefExpSimp extends leo.modules.preprocessing.Normalization {
    override val name = "defexp_and_simp_and_etaexpand"
    override val inferenceStatus = Some(SZS_Theorem)
    def apply(t: Term): Term = {
      val sig = SignatureImpl.get
      val symb: Set[SignatureImpl#Key] = Set(sig("?").key, sig("&").key, sig("=>").key)
      Simplification.normalize(t.exhaustive_δ_expand_upTo(symb).betaNormalize.etaExpand)
    }

    def apply(cl: Clause): Clause = {
      val litsIt = cl.lits.iterator
      var newLits: Seq[Literal] = Seq()
      while (litsIt.hasNext) {
        val lit = litsIt.next()
        if (lit.equational) {
          newLits = newLits :+ Simp(Literal(apply(lit.left), apply(lit.right), lit.polarity))
        } else {
          newLits = newLits :+ Simp(Literal(apply(lit.left), lit.polarity))
        }
      }
      Clause(newLits)
    }
  }

