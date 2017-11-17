package leo.modules.calculus

import leo.datastructures.{Literal, Signature, Term, Type}
import leo.datastructures.Term._
import leo.modules.HOLSignature._

import scala.collection.mutable

/**
  * Performs a one step renaming,
  * if the resulting number of clauses would
  * be bigger for a normal cnf step.
  */
object FormulaRenaming {

//  private val cashExtracts : mutable.Map[Term, (Term, Boolean, Boolean)]
//    = new mutable.HashMap[Term, (Term, Boolean, Boolean)]()
//  def resetCash() : Unit = cashExtracts.clear()


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
    * @param l The literal checked for introducing definitions.
    * @param THRESHHOLD The threshhold until we do not consider renaming
    * @return true, iff the cnf of the split smaller (+THRESHHOLD) than the original cnf
    */
  def canApply(l : Literal, THRESHHOLD : Int = 0) : Boolean = if(!l.equational) canApply(l.left, l.polarity, THRESHHOLD) else false

  private def canApply(t : Term, polarity : Boolean, THRESHHOLD : Int) : Boolean = t match {
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
    * After a successfull test with `canApply` the result has exactly two elements in the sequence.
    *
    * @param l The literal, that should be replaced
    * @param sig Signature in the current context
    * @return A tupel of the replaced term and a sequence of two Literals
    *         repersenting the defining clause. On (t, null, null)
    *         either no renaming happend or a cashed definition was applied.
    */
  def apply(l : Literal, cashExtracts : mutable.Map[Term, (Term, Boolean, Boolean)])(implicit sig : Signature) : (Literal, Literal, Literal) = {
    if(!l.equational) {
      val (l1, l2, l3) = apply(l.left, l.polarity, cashExtracts)
      (Literal(l1, l.polarity), l2, l3)
    } else (l, null, null)
  }

  private def apply(t : Term, polarity : Boolean, cashExtracts : mutable.Map[Term, (Term, Boolean, Boolean)])(implicit sig : Signature) : (Term, Literal, Literal) = t match {
    case (a & b) if !polarity =>
      if(cashExtracts.contains(a)) {
        val (a1, pos, neg) = cashExtracts(a)
        if(neg) {
          (&(a1, b), null, null)
        } else {
          cashExtracts.put(a, (a1, true, true))
          (&(a1, b), Literal(a1, true), Literal(a, false))
        }
      } else if(cashExtracts.contains(b)){
        val (b1, pos, neg) = cashExtracts(b)
        if(neg) {
          (&(a, b1), null, null)
        } else {
          cashExtracts.put(b, (b1, true, true))
          (&(a, b1), Literal(b1, true), Literal(b, false))
        }
      } else {
        val args : Seq[Term] = b.freeVars.toSeq
        val arg_ty : Seq[Type] = args.map(_.ty)
        val c_ty = normalizeType(Type.mkFunType(arg_ty, o), b.tyFV.size)
        val c_ty2 = mkPolyTyAbstractionType(b.tyFV.size, c_ty)
        val c_def0 = mkTypeApp(mkAtom(sig.freshSkolemConst(c_ty2)), b.tyFV.toSeq.sortWith{case (i,j) => i > j}.map(Type.mkVarType))
        val c_def = mkTermApp(c_def0, args)
        cashExtracts.put(b, (c_def, false, true))   // Tested previously, should not contain b
        val t = &(a, c_def)
        (t, Literal(c_def, true), Literal(b, false))
      }
    case (a ||| b) if polarity =>
      if(cashExtracts.contains(a)) {
        val (a1, pos, neg) = cashExtracts(a)
        if(pos) {
          (|||(a1, b), null, null)
        } else {
          cashExtracts.put(a, (a1, true, true))
          (|||(a1, b), Literal(a1, false), Literal(a, true))
        }
      } else if(cashExtracts.contains(b)){
        val (b1, pos, neg) = cashExtracts(b)
        if(pos) {
          (|||(a, b1), null, null)
        } else {
          cashExtracts.put(b, (b1, true, true))
          (|||(a, b1), Literal(b1, false), Literal(b, true))
        }
      } else {
        val args: Seq[Term] = b.freeVars.toSeq
        val arg_ty: Seq[Type] = args.map(_.ty)
        val c_ty = normalizeType(Type.mkFunType(arg_ty, o), b.tyFV.size)
        val c_ty2 = mkPolyTyAbstractionType(b.tyFV.size, c_ty)
        val c_def0 = mkTypeApp(mkAtom(sig.freshSkolemConst(c_ty2)), b.tyFV.toSeq.sortWith{case (i,j) => i > j}.map(Type.mkVarType))
        val c_def = mkTermApp(c_def0, args) // Tested previously, should not contain b
        cashExtracts.put(b, (c_def, true, false))
        val t = |||(a, c_def)
        (t, Literal(c_def, false), Literal(b, true))
      }
    case Impl(a,b) if polarity =>
      if(cashExtracts.contains(a)) {
        val (a1, pos, neg) = cashExtracts(a)
        if(neg) {
          (Impl(a1, b), null, null)
        } else {
          cashExtracts.put(a, (a1, true, true))
          (Impl(a1, b), Literal(a1, true), Literal(a, false))
        }
      } else if(cashExtracts.contains(b)){
        val (b1, pos, neg) = cashExtracts(b)
        if(pos) {
          (Impl(a, b1), null, null)
        } else {
          cashExtracts.put(b, (b1, true, true))
          (Impl(a, b1), Literal(b1, false), Literal(b, true))
        }
      } else {
        val args: Seq[Term] = b.freeVars.toSeq
        val arg_ty: Seq[Type] = args.map(_.ty)
        val c_ty = normalizeType(Type.mkFunType(arg_ty, o), b.tyFV.size)
        val c_ty2 = mkPolyTyAbstractionType(b.tyFV.size, c_ty)
        val c_def0 = mkTypeApp(mkAtom(sig.freshSkolemConst(c_ty2)), b.tyFV.toSeq.sortWith{case (i,j) => i > j}.map(Type.mkVarType))
        val c_def = mkTermApp(c_def0, args)
        cashExtracts.put(b, (c_def, true, false)) // Tested previously, should not contain b
        val t = Impl(a, c_def)
        (t, Literal(c_def, false), Literal(b, true))
      }
    case otherwise => (t, null, null)
  }
}
