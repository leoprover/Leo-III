package leo.modules.calculus

import leo.datastructures._
import leo.modules.normalization.NegationNormal

/**
 * Created by lex on 25.05.15.
 */
object FuncExt extends UnaryCalculusHintRule[Clause, (Seq[Literal], Seq[Literal])] {
  def canApply(cl: Clause): (Boolean, (Seq[Literal], Seq[Literal])) = {
    var it = cl.lits.iterator
    var boolExtLits: Seq[Literal] = Seq()
    var otherLits: Seq[Literal] = Seq()
    while (it.hasNext) {
      val lit = it.next()
      lit.term match {
        case (left === _) if left.ty.isFunType => boolExtLits = boolExtLits :+ lit
        case _ => otherLits = otherLits :+ lit
      }
    }
    (boolExtLits.nonEmpty, (boolExtLits, otherLits))
  }

  def apply(cl: Clause, boolExtLits_otherLits: (Seq[Literal], Seq[Literal])) = {
    val boolExtLits = boolExtLits_otherLits._1
    val otherLits = boolExtLits_otherLits._2
    var groundLits: Seq[Literal] = Seq()
    val it = boolExtLits.iterator
    while (it.hasNext) {
      val lit = it.next()
      val (left, right) = ===.unapply(lit.term).get
      if (lit.polarity) {
        // FuncPos, insert fresh var
        val freshVar = Term.mkFreshMetaVar(left.ty._funDomainType)
        groundLits = groundLits :+ Literal.mkEqLit(Term.mkTermApp(left, freshVar).betaNormalize,Term.mkTermApp(right, freshVar).betaNormalize)
      } else {
        // FuncNeg, insert skolem term
        // get freevars of clause
        val fvs = cl.freeVars.toSeq
        val fv_types = fvs.map(_.ty)
        import leo.datastructures.impl.Signature
        val skConst = Term.mkAtom(Signature.get.freshSkolemVar(Type.mkFunType(fv_types, left.ty._funDomainType)))
        val skTerm = Term.mkTermApp(skConst, fvs)
        groundLits = groundLits :+ Literal.mkUniLit(Term.mkTermApp(left, skTerm).betaNormalize,Term.mkTermApp(right, skTerm).betaNormalize)
      }
    }
    TrivRule.teqf(TrivRule.simpEq(Clause.mkClause(otherLits ++ groundLits, Derived)))
  }

  def name = "func_ext"
}


object BoolExt extends UnaryCalculusHintRule[Clause, (Seq[Literal], Seq[Literal])] {
  def canApply(cl: Clause): (Boolean, (Seq[Literal], Seq[Literal])) = {
    var it = cl.lits.iterator
    var boolExtLits: Seq[Literal] = Seq()
    var otherLits: Seq[Literal] = Seq()
    while (it.hasNext) {
      val lit = it.next()
      import leo.datastructures.impl.Signature
      lit.term match {
        case (left === _) if left.ty == Signature.get.o => boolExtLits = boolExtLits :+ lit
        case _ => otherLits = otherLits :+ lit
      }
    }
    (boolExtLits.nonEmpty, (boolExtLits, otherLits))
  }

  def apply(v1: Clause, boolExtLits_otherLits: (Seq[Literal], Seq[Literal])) = {
    val boolExtLits = boolExtLits_otherLits._1
    val otherLits = boolExtLits_otherLits._2
    var groundLits: Seq[Literal] = Seq()
    val it = boolExtLits.iterator
    while (it.hasNext) {
      val lit = it.next()
      val (left, right) = ===.unapply(lit.term).get
      groundLits = groundLits :+ Literal.mkLit(<=>(left,right).full_δ_expand.betaNormalize, lit.polarity)
    }
    NegationNormal.normalize(Simp(Clause.mkClause(otherLits ++ groundLits, Derived)))
  }

  def name = "bool_ext"
}
/** alternative boolean extensionality rule:
  * {{{
  *   C \/ [s1_o = t1_o]^alpha1 \/ [s2_o = t2_o]^alpha2 \/ .... \/ [sn_o = tn_o]^alphan
  * --------------------------------------------------------------------------------------
  *   C \/ [s1]^t \/ [t1]^f \/ [s2]^t \/ [t2]^f ... \/ [sn]^t \/ [tn]^f
  *   ....
  *   C \/ [s1]^f \/ [t1]^t \/ [s2]^f \/ [t2]^t ... \/ [sn]^f \/ [tn]^t
  * }}}
  *
  * That are, n^2^ many new clauses
  * */
object BoolExtAlt extends UnaryCalculusHintRule[Set[Clause], (Seq[Literal], Seq[Literal])] {
  def canApply(cl: Clause): (Boolean, (Seq[Literal], Seq[Literal])) = {
    var it = cl.lits.iterator
    var boolExtLits: Seq[Literal] = Seq()
    var otherLits: Seq[Literal] = Seq()
    while (it.hasNext) {
      val lit = it.next()
      import leo.datastructures.impl.Signature
      lit.term match {
        case (left === _) if left.ty == Signature.get.o => boolExtLits = boolExtLits :+ lit
        case _ => otherLits = otherLits :+ lit
      }
    }
    (boolExtLits.nonEmpty, (boolExtLits, otherLits))
  }

  def apply(v1: Clause, boolExtLits_otherLits: (Seq[Literal], Seq[Literal])) = {
    val boolExtLits = boolExtLits_otherLits._1
    val otherLits = boolExtLits_otherLits._2
    var groundLits: Seq[Seq[Literal]] = Seq(otherLits)
    val it = boolExtLits.iterator
    while (it.hasNext) {
      val lit = it.next()
      val (left, right) = ===.unapply(lit.term).get
      var newGroundLits : Seq[Seq[Literal]] = Seq()
      groundLits.foreach( lits =>
        if (lit.polarity) {
          newGroundLits = newGroundLits :+ (lits :+ Literal.mkNegLit(left) :+ Literal.mkPosLit(right)) :+ (lits :+ Literal.mkPosLit(left) :+ Literal.mkNegLit(right))
        } else {
          newGroundLits = newGroundLits :+ (lits :+ Literal.mkNegLit(left) :+ Literal.mkNegLit(right)) :+ (lits :+ Literal.mkPosLit(left) :+ Literal.mkPosLit(right))
        }
      )
      groundLits = newGroundLits
      //      groundLits = groundLits :+ Literal.mkLit(<=>(left,right).full_δ_expand.betaNormalize, lit.polarity)
    }
    groundLits.map(lits => TrivRule.teqf(TrivRule.simpEq(Clause.mkClause(lits, Derived)))).filterNot(TrivRule.teqt(_)).toSet
    //    NegationNormal.normalize(ion.normalize(Clause.mkClause(otherLits ++ groundLits, Derived)))
  }

  def name = "bool_ext_alt"
}