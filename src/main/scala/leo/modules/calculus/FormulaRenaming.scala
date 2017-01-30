package leo.modules.calculus

import leo.datastructures.{Literal, Signature, Term, Type, Literal}
import leo.datastructures.Term._
import leo.modules.HOLSignature._

/**
  * Introduces names / definitions
  * for subformulas, if the resulting
  * formula will be smaller / generates a smaller CNF
  */
object FormulaRenaming {

  def size (t : Term, polarity : Boolean) : Int = t match {
    case (a & b) => if (polarity) size(a, polarity) + size(b, polarity) else size(a,polarity) * size(b, polarity)
    case (a ||| b) => if (polarity) size(a, polarity) * size(b, polarity) else size(a,polarity) + size(b, polarity)
    case Impl(a,b) => if (polarity) size(a, !polarity) * size(b, polarity) else size(a,polarity) + size(b, !polarity)
    case Not(a) => size(a, !polarity)
    case Exists(ty :::> s) => size(s, polarity)
    case Forall(ty :::> s) => size(s, polarity)
    case _ => 1
  }

  def apply(t : Term, polarity : Boolean, THRESHHOLD : Int = 5)(implicit sig : Signature) : (Term, Seq[Literal]) = t match {
    case (a & b) =>
      if(!polarity) {
        multcase(a, b, polarity, polarity, THRESHHOLD, &)
      } else {
        val (l, units) = apply(a, polarity, THRESHHOLD)
        val (r, units2) = apply(b,polarity, THRESHHOLD)
        (&(l,r), units ++ units2)
      }
    case (a ||| b)  =>
      if (polarity) {
        multcase(a, b, polarity, polarity, THRESHHOLD, |||)
      } else {
        val (l, units) = apply(a, polarity, THRESHHOLD)
        val (r, units2) = apply(b,polarity, THRESHHOLD)
        (|||(l,r), units ++ units2)
      }
    case Not(a) =>
      val (a1, units) = apply(a, !polarity, THRESHHOLD)
      (Not(a1), units)
    case Forall(ty :::> s) =>
      val (s1, units) = apply(s, polarity, THRESHHOLD)
      (Forall(\(ty)(s1)), units)
    case Exists(ty :::> s) =>
      val (s1, units) = apply(s, polarity, THRESHHOLD)
      (Exists(\(ty)(s1)), units)
    case Impl(a, b) => {
      if (polarity) {
        multcase(a, b, !polarity, polarity, THRESHHOLD, Impl)
      } else {
        val (l, units) = apply(a, !polarity, THRESHHOLD)
        val (r, units2) = apply(b,polarity, THRESHHOLD)
        (Impl(l,r), units ++ units2)
      }
    }
    case otherwise => (t, Seq())
  }

  private def multcase(left : Term, right : Term, lpolarity : Boolean, rpolarity : Boolean, THRESHHOLD : Int, op : HOLBinaryConnective)(implicit sig : Signature) : (Term, Seq[Literal]) = {
    val (a, units1) = apply(left, lpolarity, THRESHHOLD)
    val (b, units2) = apply(right, rpolarity, THRESHHOLD)
    val size_a = size(a, lpolarity)
    val size_b = size(b, rpolarity)
    val size_org = size_a * size_b
    val repl_size = size_a + size_b

    if(repl_size + THRESHHOLD < size_org) {
      // No outer bound variable test (we are not on predicate level here)
      // TODO all free variables of the clause???
      val args : Seq[Term] = b.freeVars.toSeq         // TODO pass list, only all quantified variables are set.
      val arg_ty : Seq[Type] = args.map(_.ty)
      val c_ty = Type.mkFunType(arg_ty, o)
      val c_def = mkTermApp(mkAtom(sig.freshSkolemConst(c_ty)), args)

      val unit = Literal(c_def, b, true)
      val t = op(a, c_def)
      (t, unit +: (units1 ++ units2))
    } else {
      (op(a, b), units1 ++ units2)
    }
  }
}
