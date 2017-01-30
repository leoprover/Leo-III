package leo.modules.calculus

import leo.datastructures.{Literal, Signature, Term, Type}
import leo.datastructures.Term._
import leo.modules.HOLSignature._

/**
  * Performs a one step renaming,
  * if the resulting number of clauses would
  * be bigger for a normal cnf step.
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

  /**
    * Compares the blowup of the clauses of the original term,
    * and the introduction of a definition.
    *
    * @param t The term checked for introducing definitions.
    * @param polarity The polarity under which the term occures
    * @param THRESHHOLD The threshhold until we do not consider renaming
    * @return true, iff the cnf of the split smaller (+THRESHHOLD) than the original cnf
    */
  def canApply(t : Term, polarity : Boolean, THRESHHOLD : Int = 5) : Boolean = t match {
    case (a & b) if !polarity =>
      val as = size(a, polarity)
      val bs = size(b, polarity)
      as * bs > as + bs + THRESHHOLD
    case (a ||| b) if polarity =>
      val as = size(a, polarity)
      val bs = size(b, polarity)
      as * bs > as + bs + THRESHHOLD
    case Impl(a,b) if polarity =>
      val as = size(a, !polarity)
      val bs = size(b, polarity)
      as * bs > as + bs + THRESHHOLD
    case otherwise => false
  }

  /**
    * Replaces the topmost binary connectives one side argument with a new definition.
    *
    * Performs trivial checks for a blowup in which case (t, Seq()) is returned,
    * but a renaming is only guaranteed to succeed, if it is guarded by `canApply`.
    *
    * @param t The term, that should be replaced
    * @param polarity the polarity of the term
    * @param sig Signature in the current context
    * @return A tupel of the replaced term and a sequence of new Literals from the single defining clause.
    */
  def apply(t : Term, polarity : Boolean)(implicit sig : Signature) : (Term, Seq[Literal]) = t match {
    case (a & b) if !polarity =>
      val args : Seq[Term] = b.freeVars.toSeq
      val arg_ty : Seq[Type] = args.map(_.ty)
      val c_ty = Type.mkFunType(arg_ty, o)
      val c_def = mkTermApp(mkAtom(sig.freshSkolemConst(c_ty)), args)
      val defs = Seq(Literal(c_def, false), Literal(b, true))
      val t = &(a, c_def)
      (t, defs)
    case (a ||| b) if polarity =>
      val args : Seq[Term] = b.freeVars.toSeq
      val arg_ty : Seq[Type] = args.map(_.ty)
      val c_ty = Type.mkFunType(arg_ty, o)
      val c_def = mkTermApp(mkAtom(sig.freshSkolemConst(c_ty)), args)
      val defs = Seq(Literal(c_def, false), Literal(b, true))
      val t = |||(a, c_def)
      (t, defs)
    case Impl(a,b) if polarity =>
      val args : Seq[Term] = b.freeVars.toSeq
      val arg_ty : Seq[Type] = args.map(_.ty)
      val c_ty = Type.mkFunType(arg_ty, o)
      val c_def = mkTermApp(mkAtom(sig.freshSkolemConst(c_ty)), args)
      val defs = Seq(Literal(c_def, false), Literal(b, true))
      val t = Impl(a, c_def)
      (t, defs)
    case otherwise => (t, Seq())
  }
}
