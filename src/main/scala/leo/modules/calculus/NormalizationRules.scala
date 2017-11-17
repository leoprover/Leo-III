package leo.modules.calculus

import leo._
import leo.datastructures.Term.{:::>, TypeLambda}
import leo.datastructures.{Clause, Subst, Type, _}
import leo.modules.HOLSignature.{!===, &, ===, Exists, Forall, Impl, LitFalse, LitTrue, Not, TyForall, |||}
import leo.modules.output.{SZS_EquiSatisfiable, SZS_Theorem, SuccessSZS}

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * Created by lex on 5/12/16.
  */

////////////////////////////////////////////////////////////////
////////// Normalization
////////////////////////////////////////////////////////////////

object DefExpSimp extends CalculusRule {
  final val name = "defexp_and_simp_and_etaexpand"
  final val inferenceStatus = SZS_Theorem

  final def apply(t: Term)(implicit sig: Signature): Term = {
    val symb: Set[Signature.Key] = Set(sig("?").key, sig("&").key, sig("=>").key)
    Simp.normalize(t.δ_expand_upTo(symb).betaNormalize.etaExpand)
  }

  final def apply(cl: Clause)(implicit sig: Signature): Clause = {
    val litsIt = cl.lits.iterator
    var newLits: Seq[Literal] = Vector()
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


// TODO: Encode origin of boolext clauses so that they are not paramodulated
// with its ancestor clause.

object PolaritySwitch extends CalculusRule {
  final val name = "polarity_switch"
  final val inferenceStatus = SZS_Theorem
  final def canApply(t: Term): Boolean = t match {
    case Not(_) => true
    case _ => false
  }
  final def canApply(l: Literal): Boolean = canApply(l.left) || canApply(l.right)

  final def apply(t: Term): Term = t match {
    case Not(t2) => t2
    case _ => t
  }

  final def apply(l: Literal): Literal = if (l.equational) {
    (l.left, l.right) match {
      case (Not(l2), Not(r2)) => Literal(l2, r2, l.polarity, l.oriented)
      case _ => l
    }
  } else {
    l.left match {
      case Not(l2) => Literal(l2, !l.polarity)
      case _ => l
    }
  }
}

/**
  * Created by mwisnie on 11.04.16.
  */
object  StepCNF extends CalculusRule {
  final val name: String = "cnf"
  final val inferenceStatus = SZS_Theorem

  trait CNF
  case class Alpha(l : Literal, r : Literal) extends CNF
  case class Beta(l : Literal, r : Literal) extends CNF
  case class One(l : Literal) extends CNF
  case class None(l : Literal) extends CNF


  final def canApply(l: Literal): Boolean = if (!l.equational) {
    l.left match {
      case Not(t) => true
      case s ||| t => true
      case s & t => true
      case s Impl t => true
      //      case s <=> t => true
      case Forall(ty :::> t) => true
      case Exists(ty :::> t) => true
      case _ => false
    }
  } else false

  final def canApply(ls : Seq[Literal]) : Boolean = ls exists canApply
  //FIXME: Do not duplicate free vars. See FullCNF for solution.
  final def apply(vargen : leo.modules.calculus.FreshVarGen,l : Literal)(implicit sig: Signature) : CNF = if(!l.equational){
    l.left match {
      case Not(t) => One(Literal(t, !l.polarity))
      case &(lt,rt) if l.polarity => Alpha(Literal(lt,true), Literal(rt,true))
      case &(lt,rt) if !l.polarity => Beta(Literal(lt,false), Literal(rt, false))
      case |||(lt,rt) if l.polarity => Beta(Literal(lt,true), Literal(rt, true))
      case |||(lt,rt) if !l.polarity => Alpha(Literal(lt,false), Literal(rt,false))
      case Impl(lt,rt) if l.polarity => Beta(Literal(lt,false), Literal(rt, true))
      case Impl(lt,rt) if !l.polarity => Alpha(Literal(lt,true), Literal(rt,false))
      case Forall(a@(ty :::> t)) if l.polarity => val newVar = vargen(ty); One(Literal(Term.mkTermApp(a, newVar).betaNormalize, true))
      case Forall(a@(ty :::> t)) if !l.polarity => val sko = leo.modules.calculus.skTerm(ty, vargen.existingVars, vargen.existingTyVars)(sig); One(Literal(Term.mkTermApp(a, sko).betaNormalize, false))
      case Exists(a@(ty :::> t)) if l.polarity => val sko = leo.modules.calculus.skTerm(ty, vargen.existingVars, vargen.existingTyVars)(sig); One(Literal(Term.mkTermApp(a, sko).betaNormalize, true))
      case Exists(a@(ty :::> t)) if !l.polarity => val newVar = vargen(ty); One(Literal(Term.mkTermApp(a, newVar).betaNormalize, false))
      case _ => None(l)
    }
  } else None(l)


  final def step(vargen : leo.modules.calculus.FreshVarGen, ls : Seq[Literal])(implicit sig: Signature) : Seq[Seq[Literal]] = {
    val (norm, l+:rest) = ls.span(l => !canApply(l))
    val c = norm ++ rest
    apply(vargen, l)(sig) match {
      case Alpha(a,b) =>  Seq(a +: c, b +: c)
      case Beta(a,b)  => Seq(a +: b +: c)
      case One(a)     => Seq(a +: c)
      case None(a)    => Seq(ls)
    }
  }

  /**
    * Searches the first Clause and the first Literal, that are not in cnf and applies one rule to them.
    *
    * @param vargen
    * @param ls Sequence of clauses
    * @return A sequence of the same clauses, where one literal was applied with cnf
    */
  final def apply(vargen : leo.modules.calculus.FreshVarGen, ls : Seq[Seq[Literal]])(implicit sig: Signature) : Seq[Seq[Literal]] = {
    val (norm, rest) = ls.span(ls1 => !canApply(ls1))
    rest match {
      case Seq()  => ls
      case (a +: c) => (c ++ step(vargen,a)(sig)) ++ norm
    }
  }

  final def exhaust(c : Clause)(implicit sig: Signature) : Seq[Clause] = {
    var ls = Seq(c.lits)
    val vargen = freshVarGen(c)
    while(ls exists canApply){
      ls = apply(vargen, ls)(sig)
    }
    ls.map(ls1 => Clause(ls1))
  }
}


object RenameCNF extends CalculusRule {
  final val name : String = "cnf"
  final val inferenceStatus = SZS_EquiSatisfiable
  type FVs = FullCNF.FVs
  type TyFVS = FullCNF.TyFVS

  @inline
  final def canApply(l : Literal) : Boolean = FullCNF.canApply(l)

  final def canApply(cl: Clause): Boolean = cl.lits.exists(canApply)

  final def apply(vargen : leo.modules.calculus.FreshVarGen, cashExtracts : mutable.Map[Term, (Term, Boolean, Boolean)], cl : Clause, THRESHHOLD : Int = 0)(implicit sig: Signature) : Seq[Clause] = {
    val lits = cl.lits
    val normLits = apply(vargen, cashExtracts, lits, THRESHHOLD)
    normLits.map{ls => Clause(ls)}
  }

  final def apply(vargen: leo.modules.calculus.FreshVarGen, cashExtracts : mutable.Map[Term, (Term, Boolean, Boolean)], l : Seq[Literal], THRESHHOLD : Int)(implicit sig: Signature): (Seq[Seq[Literal]]) = {
    var acc : Seq[Seq[Literal]] = Seq(Seq())
    val it : Iterator[Literal] = l.iterator
    while(it.hasNext){
      val nl = it.next()
      apply(vargen, cashExtracts, nl, THRESHHOLD) match {
        case Seq(Seq(lit)) => acc = acc.map{normLits => lit +: normLits}
        case norms =>  acc = multiply(norms, acc)
      }
    }
    acc
  }

  final def apply(vargen: leo.modules.calculus.FreshVarGen, cashExtracts : mutable.Map[Term, (Term, Boolean, Boolean)], l : Literal,THRESHHOLD : Int)(implicit sig: Signature): Seq[Seq[Literal]] = apply0(vargen.existingVars, vargen.existingTyVars, vargen, cashExtracts, l, THRESHHOLD)

  @inline
  final private def apply0(fvs: FVs, tyFVs: TyFVS, vargen: leo.modules.calculus.FreshVarGen, cashExtracts : mutable.Map[Term, (Term, Boolean, Boolean)], l : Literal, THRESHHOLD : Int)(implicit sig: Signature): Seq[Seq[Literal]] = if(!l.equational){
    if(FormulaRenaming.canApply(l, THRESHHOLD)) {
      val (replLit, defl1, defl2) = FormulaRenaming.apply(l, cashExtracts)
      if(defl1 == null && defl2 == null){
        apply0(fvs, tyFVs, vargen, cashExtracts, replLit, THRESHHOLD)
      } else {
        assert(defl1 != null && defl2 != null, "Non consistent definition returend in formula renaming.")
        apply0(fvs, tyFVs, vargen, cashExtracts, replLit, THRESHHOLD) ++ multiply(apply0(fvs, tyFVs, vargen, cashExtracts, defl1, THRESHHOLD), apply0(fvs, tyFVs, vargen, cashExtracts, defl2, THRESHHOLD))
      }
    } else {
    l.left match {
      case Not(t) => apply0(fvs, tyFVs, vargen, cashExtracts, Literal(t, !l.polarity), THRESHHOLD)
      case &(lt,rt) if l.polarity => apply0(fvs, tyFVs, vargen, cashExtracts, Literal(lt,true), THRESHHOLD) ++ apply0(fvs, tyFVs, vargen, cashExtracts, Literal(rt,true), THRESHHOLD)
      case &(lt,rt) if !l.polarity => multiply(apply0(fvs, tyFVs, vargen, cashExtracts, Literal(lt,false), THRESHHOLD), apply0(fvs, tyFVs, vargen, cashExtracts, Literal(rt, false), THRESHHOLD))
      case |||(lt,rt) if l.polarity => multiply(apply0(fvs, tyFVs, vargen, cashExtracts, Literal(lt,true),THRESHHOLD), apply0(fvs, tyFVs, vargen, cashExtracts, Literal(rt, true),THRESHHOLD))
      case |||(lt,rt) if !l.polarity => apply0(fvs, tyFVs, vargen, cashExtracts, Literal(lt,false),THRESHHOLD) ++ apply0(fvs, tyFVs, vargen, cashExtracts, Literal(rt,false),THRESHHOLD)
      case Impl(lt,rt) if l.polarity => multiply(apply0(fvs, tyFVs, vargen, cashExtracts, Literal(lt,false),THRESHHOLD), apply0(fvs, tyFVs, vargen, cashExtracts, Literal(rt, true),THRESHHOLD))
      case Impl(lt,rt) if !l.polarity => apply0(fvs, tyFVs, vargen, cashExtracts, Literal(lt,true),THRESHHOLD) ++ apply0(fvs, tyFVs, vargen, cashExtracts, Literal(rt,false),THRESHHOLD)
      case Forall(a@(ty :::> t)) if l.polarity => val v = vargen.next(ty); apply0(v +: fvs, tyFVs, vargen, cashExtracts, Literal(Term.mkTermApp(a, Term.mkBound(v._2, v._1)).betaNormalize.etaExpand, true),THRESHHOLD)
      case Forall(a@(ty :::> t)) if !l.polarity => val sko = leo.modules.calculus.skTerm(ty, fvs, tyFVs); apply0(fvs, tyFVs, vargen, cashExtracts, Literal(Term.mkTermApp(a, sko).betaNormalize.etaExpand, false),THRESHHOLD)
      case Exists(a@(ty :::> t)) if l.polarity => val sko = leo.modules.calculus.skTerm(ty, fvs, tyFVs); apply0(fvs, tyFVs, vargen, cashExtracts, Literal(Term.mkTermApp(a, sko).betaNormalize.etaExpand, true),THRESHHOLD)
      case Exists(a@(ty :::> t)) if !l.polarity => val v = vargen.next(ty); apply0(v +: fvs, tyFVs, vargen, cashExtracts, Literal(Term.mkTermApp(a, Term.mkBound(v._2, v._1)).betaNormalize.etaExpand, false),THRESHHOLD)
      case TyForall(a@TypeLambda(t)) if l.polarity => val ty = vargen.next(); apply0(fvs, ty +: tyFVs, vargen, cashExtracts, Literal(Term.mkTypeApp(a, Type.mkVarType(ty)).betaNormalize.etaExpand, true),THRESHHOLD)
      case TyForall(a@TypeLambda(t)) if !l.polarity => val sko = leo.modules.calculus.skType(tyFVs); apply0(fvs, tyFVs, vargen, cashExtracts, Literal(Term.mkTypeApp(a, sko).betaNormalize.etaExpand, false),THRESHHOLD)
      case _ => Seq(Seq(l))
    }}
  } else {
    Seq(Seq(l))
  }

  private[calculus] final def multiply[A](l : Seq[Seq[A]], r : Seq[Seq[A]]) : Seq[Seq[A]] = FullCNF.multiply(l,r)
}

/**
  * Created by mwisnie on 4/4/16.
  */
object FullCNF extends CalculusRule {
  final val name: String = "cnf"
  final val inferenceStatus = SZS_EquiSatisfiable
  type FVs = Seq[(Int, Type)]
  type TyFVS = Seq[Int]

  final def canApply(l: Literal): Boolean = if (!l.equational) {
    l.left match {
      case Not(_) => true
      case _ ||| _ => true
      case _ & _ => true
      case _ Impl _ => true
      case Forall(_ :::> _) => true
      case Exists(_ :::> _) => true
      case TyForall(TypeLambda(_)) => true
      case _ => false
    }
  } else false

  final def canApply(cl: Clause): Boolean = if (Clause.empty(cl)) false
  else {
    val litIt = cl.lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      if (canApply(lit)) return true
    }
    false
  }

  final def apply(vargen: leo.modules.calculus.FreshVarGen, cl: Clause)(implicit sig: Signature): Seq[Clause] = {
    val lits = cl.lits
    val normLits = apply(vargen, lits)
    normLits.map{ls => Clause(ls)}
  }

  final def apply(vargen: leo.modules.calculus.FreshVarGen, l : Seq[Literal])(implicit sig: Signature): (Seq[Seq[Literal]]) = {
    var acc : Seq[Seq[Literal]] = Vector(Vector())
    val it : Iterator[Literal] = l.iterator
    while(it.hasNext){
      val nl = it.next()
      apply(vargen, nl) match {
        case Seq(Seq(lit)) => acc = acc.map{normLits => lit +: normLits}
        case norms =>  acc = multiply(norms, acc)
      }
    }
    acc
  }

  final def apply(vargen: leo.modules.calculus.FreshVarGen, l : Literal)(implicit sig: Signature): Seq[Seq[Literal]] = apply0(vargen.existingVars, vargen.existingTyVars, vargen, l)

  @inline
  final private def apply0(fvs: FVs, tyFVs: TyFVS, vargen: leo.modules.calculus.FreshVarGen, l : Literal)(implicit sig: Signature): Seq[Seq[Literal]] = if(!l.equational){
    l.left match {
      case Not(t) => apply0(fvs, tyFVs, vargen, Literal(t, !l.polarity))
      case &(lt,rt) if l.polarity => apply0(fvs, tyFVs, vargen, Literal(lt,true)) ++ apply0(fvs, tyFVs, vargen, Literal(rt,true))
      case &(lt,rt) if !l.polarity => multiply(apply0(fvs, tyFVs, vargen, Literal(lt,false)), apply0(fvs, tyFVs, vargen, Literal(rt, false)))
      case |||(lt,rt) if l.polarity => multiply(apply0(fvs, tyFVs, vargen, Literal(lt,true)), apply0(fvs, tyFVs, vargen, Literal(rt, true)))
      case |||(lt,rt) if !l.polarity => apply0(fvs, tyFVs, vargen, Literal(lt,false)) ++ apply0(fvs, tyFVs, vargen, Literal(rt,false))
      case Impl(lt,rt) if l.polarity => multiply(apply0(fvs, tyFVs, vargen, Literal(lt,false)), apply0(fvs, tyFVs, vargen, Literal(rt, true)))
      case Impl(lt,rt) if !l.polarity => apply0(fvs, tyFVs, vargen, Literal(lt,true)) ++ apply0(fvs, tyFVs, vargen, Literal(rt,false))
      case Forall(a@(ty :::> t)) if l.polarity =>
        import leo.modules.HOLSignature.{o, LitTrue, LitFalse}
        if (false /*ty == o*/) {
          apply0(fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, LitTrue).betaNormalize.etaExpand, true)) ++ apply0(fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, LitFalse).betaNormalize.etaExpand, true))
        } else {
          val v = vargen.next(ty); apply0(v +: fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, Term.mkBound(v._2, v._1)).betaNormalize.etaExpand, true))
        }

      case Forall(a@(ty :::> t)) if !l.polarity => val sko = leo.modules.calculus.skTerm(ty, fvs, tyFVs); apply0(fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, sko).betaNormalize.etaExpand, false))
      case Exists(a@(ty :::> t)) if l.polarity => val sko = leo.modules.calculus.skTerm(ty, fvs, tyFVs); apply0(fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, sko).betaNormalize.etaExpand, true))
      case Exists(a@(ty :::> t)) if !l.polarity =>
        import leo.modules.HOLSignature.{o, LitTrue, LitFalse}
        if (false /*ty == o*/) {
          apply0(fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, LitTrue).betaNormalize.etaExpand, false)) ++ apply0(fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, LitFalse).betaNormalize.etaExpand, false))
        } else {
          val v = vargen.next(ty); apply0(v +: fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, Term.mkBound(v._2, v._1)).betaNormalize.etaExpand, false))
        }

      case TyForall(a@TypeLambda(t)) if l.polarity => val ty = vargen.next(); apply0(fvs, ty +: tyFVs, vargen, Literal(Term.mkTypeApp(a, Type.mkVarType(ty)).betaNormalize.etaExpand, true))
      case TyForall(a@TypeLambda(t)) if !l.polarity => val sko = leo.modules.calculus.skType(tyFVs); apply0(fvs, tyFVs, vargen, Literal(Term.mkTypeApp(a, sko).betaNormalize.etaExpand, false))
      case _ => Vector(Vector(l))
    }
  } else {
    Vector(Vector(l))
  }

  private[calculus] final def multiply[A](l : Seq[Seq[A]], r : Seq[Seq[A]]) : Seq[Seq[A]] = {
    var acc : Seq[Seq[A]] = Vector()
    val itl = l.iterator
    while(itl.hasNext) {
      val llist = itl.next()
      val itr = r.iterator
      while(itr.hasNext){
        val rlist = itr.next()
        acc = (llist ++ rlist) +: acc
      }
    }
    acc
  }
}


object LiftEq extends CalculusRule {
  final val name = "lifteq"
  final val inferenceStatus = SZS_Theorem

  type Lift = Int
  final val NO_LIFT: Lift = 0
  final val NEG_LIFT: Lift = -1
  final val POS_LIFT: Lift = 1

  final def canApply(t: Term): Lift = t match {
    case ===(_,_) => POS_LIFT
    case !===(_,_) => NEG_LIFT
    case _ => NO_LIFT
  }
  final def canApply(lit: Literal): Lift = if (lit.equational) NO_LIFT else canApply(lit.left)

  type PosLiftLits = Seq[Literal]
  type NegLiftLits = Seq[Literal]
  type OtherLits = Seq[Literal]
  final def canApply(cl: Clause): (Boolean, PosLiftLits, NegLiftLits, OtherLits) = {
    var can = false
    var posLiftLits: PosLiftLits = Vector()
    var negLiftLits: NegLiftLits = Vector()
    var otherLits: OtherLits = Vector()
    val lits = cl.lits.iterator
    while (lits.hasNext) {
      val l = lits.next()
      val canLift = canApply(l)
      if (canLift == POS_LIFT) {
        posLiftLits = posLiftLits :+ l
        can = true
      } else if (canLift == NEG_LIFT) {
        negLiftLits = negLiftLits :+ l
        can = true
      } else {
        otherLits = otherLits :+ l
      }
    }
    (can, posLiftLits, negLiftLits, otherLits)
  }

  final def apply(posLiftLits: PosLiftLits, negLiftLits: NegLiftLits, otherLits: OtherLits)(implicit sig: Signature): Seq[Literal] = {
    posLiftLits.map(l => apply(POS_LIFT, l.left, l.polarity)(sig)) ++ negLiftLits.map(l => apply(NEG_LIFT, l.left, l.polarity)(sig)) ++ otherLits
  }

  final def apply(lift: Lift, t: Term, polarity: Boolean)(implicit sig: Signature): Literal = {
    assert(lift != NO_LIFT)
    val (l,r) = if (lift == POS_LIFT) {
      ===.unapply(t).get
    } else {
      // lift == NEG_LIFT
      !===.unapply(t).get
    }
    assert(l.isBetaNormal, s"${l.pretty(sig)} // ${l.toString}")
    assert(r.isBetaNormal, s"${r.pretty(sig)} // ${r.toString}")
    if (lift == POS_LIFT) {
      Literal.mkOrdered(l,r,polarity)(sig)
    } else {
      // lift == NEG_LIFT
      Literal.mkOrdered(l,r,!polarity)(sig)
    }

  }
}


object ReplaceLeibnizEq extends CalculusRule {
  final val name = "replace_leibeq"
  final val inferenceStatus = SZS_Theorem
  type Polarity = Boolean


  def canApply(cl: Clause)(implicit sig: Signature): (Boolean, Map[Int, Term]) = {
    import leo.datastructures.Term.{Bound, TermApp}
    var gbTermMap: Map[Int, Term] = Map()
    var flexHeadSet: Set[Int] = Set()
    val litIt = cl.lits.iterator
    while(litIt.hasNext) {
      val lit = litIt.next()
      if (lit.flexHead) {
        val (head,args) = TermApp.unapply(lit.left).get
        assert(head.isVariable)
        if (args.size == 1) {
          val (headType, headIndex) = Bound.unapply(head).get
          val arg = args.head
          if (!(arg.looseBounds contains headIndex)) {
            if (lit.polarity) {
              flexHeadSet = flexHeadSet + headIndex
            } else {
              if (gbTermMap contains headIndex) {
                val curEntry = gbTermMap(headIndex)
                if (arg.compareTo(curEntry)(sig) == CMP_LT) {
                  gbTermMap = gbTermMap + (headIndex -> arg)
                }
              } else {
                gbTermMap = gbTermMap + (headIndex -> arg)
              }

            }
          }

        }
      }
    }
    val resMap = gbTermMap.filterKeys(k => flexHeadSet.contains(k))
    (resMap.nonEmpty, resMap)
  }

  def apply(cl: Clause, bindings: Map[Int, Term])(implicit sig: Signature): (Clause, Subst) = {
    val gbMap = bindings.mapValues(t => Term.mkTermAbs(t.ty, ===(t.substitute(Subst.shift(1)), Term.mkBound(t.ty, 1))))
    val subst = Subst.fromMap(gbMap)
    val newLits = cl.lits.map(_.substituteOrdered(subst)(sig))
    (Clause(newLits), subst)
  }
}

object ReplaceAndrewsEq extends CalculusRule {
  final val name = "replace_andrewseq"
  final val inferenceStatus = SZS_Theorem

  def canApply(cl: Clause): (Boolean, Map[Int, Type]) = {
    import leo.datastructures.Term.{Bound, TermApp}
    var varMap: Map[Int, Type] = Map()

    val litIt = cl.lits.iterator
    while(litIt.hasNext) {
      val lit = litIt.next()
      if (lit.flexHead) {
        val (head,args) = TermApp.unapply(lit.left).get
        assert(head.isVariable)
        if (args.size == 2) {
          val (headType, headIndex) = Bound.unapply(head).get
          val (arg1,arg2) = (args.head,args.tail.head)
          if (arg1 == arg2 && !(arg1.looseBounds contains headIndex)) {
            if (!lit.polarity) {
              if (!varMap.contains(headIndex)) {
                varMap = varMap + (headIndex -> arg1.ty)
              }
            }
          }

        }
      }
    }

    (varMap.nonEmpty, varMap)
  }

  def apply(cl: Clause, vars: Map[Int, Type])(implicit sig: Signature): (Clause, Subst) = {
    val gbMap = vars.mapValues {ty => Term.λ(ty,ty)(===(Term.mkBound(ty,2), Term.mkBound(ty,1)))}
    val subst = Subst.fromMap(gbMap)
    val newLits = cl.lits.map(_.substituteOrdered(subst)(sig))
    (Clause(newLits), subst)
  }
}

object RewriteSimp extends CalculusRule {
  final val name = "rewrite"
  final val inferenceStatus = SZS_Theorem

//  /**
//    * Apply a rewrite using `rewriteRule` on a specific literal in `ìntoClause`.
//    * @param replaceBy The term which is placed at `intoPosition`
//    * @param intoClause The clause the rewrite is applied on
//    * @param intoIndex The literal index, at which the rewrite is performed
//    * @param intoSide The side of the literal what is rewritten
//    * @param intoPosition The position of the subterm in the literal which is rewritten
//    */
//  def apply(replaceBy: Term,
//            intoClause: Clause, intoIndex: Int, intoSide: Literal.Side, intoPosition: Position): Clause = {
//    assert(intoClause.lits.isDefinedAt(intoIndex))
//    val lit = intoClause.lits(intoIndex)
//    val (replaceIn, otherSide) = Literal.getSidesOrdered(lit, intoSide)
//    val replaced = replaceIn.replaceAt(intoPosition, replaceBy)
//    val newLit = Literal(replaced, otherSide, lit.polarity)
//    Clause(intoClause.lits.updated(intoIndex, newLit))
//  }


  /**
    * An IntoConfiguration assigns a side of a literal `l` a pair (p,t) which denotes that at positions p in the respective side
    * of `l` a rewrite takes place, replacing `s` by `t`, where `s` is the subterm of the respective side of `l` at position p.
    *
    * We assume that the set P = {(p_i,t_i)} only contains non-overlapping most general positions in the sense
    * that if P constains an element (p,_) then there is no element (p',_) in P such that p' is a sub position of p.
    */
  type IntoConfiguration = Map[Literal.Side, Set[(Position, Term)]]

  /**
    * Replace all occurrences
    *
    * @param intoClause The clause in which the rewrite takes place
    * @param intoConfigurations The configuration of the rewrite procedure: See [[IntoConfiguration]] for details and important restrictions.
    *                           For each literal at index i that is rewritten, an entry i -> conf is required in `intoConfiguration`.
    */
  def apply(intoClause: Clause, intoConfigurations: Map[Int, IntoConfiguration])(implicit sig: Signature): Clause = {
    var lits = intoClause.lits
    val litIndices = intoConfigurations.keySet
    val litIndicesIt = litIndices.iterator
    while (litIndicesIt.hasNext) {
      val litIndex = litIndicesIt.next()
      val lit = intoClause.lits(litIndex)
      val confs = intoConfigurations(litIndex)
      // assume that all positions do not intersect, we only have most general positions  in this set
      val (left,right) = (lit.left, lit.right)

      // (1) left normalization
      val leftConfs = confs.getOrElse(Literal.leftSide, Set())
      val newLeft = leftConfs.foldLeft(left) {case (curTerm, (pos, replaceBy)) => curTerm.replaceAt(pos, replaceBy)}

      // (2) right normalization
      val rightConfs = confs.getOrElse(Literal.rightSide, Set())
      val newRight = rightConfs.foldLeft(right) {case (curTerm, (pos, replaceBy)) => curTerm.replaceAt(pos, replaceBy)}

      val newLit = Literal.mkOrdered(newLeft, newRight, lit.polarity)(sig)
      lits = lits.updated(litIndex, newLit)
    }

    Clause(lits)
  }

}

object ACSimp extends CalculusRule {
  final val name = "ac_simp"
  final val inferenceStatus = SZS_Theorem

  def lt(a: Term, b: Term): Boolean = {
    import leo.datastructures.Term.{Bound, Symbol}
    (a,b) match {
      case (Bound(_,i1), Bound(_, i2)) => i1 < i2
      case (Bound(_,_), _) => true
      case (Symbol(k1), Symbol(k2)) => k1 < k2
      case (Symbol(_), Bound(_,_)) => false
      case (Symbol(_), _) => true
      case (_, Bound(_,_)) => false
      case (_, Symbol(_)) => false
      case (_,_) => a.size < b.size

    }
  }

  def apply(t: Term, acSymbols: Set[Signature.Key]): Term = {
    acSymbols.foldLeft(t){case (term,symbol) => apply(term, symbol)}
  }

  def apply(t: Term, acSymbol: Signature.Key): Term = {
    import leo.datastructures.Term.{:::>, TermApp, TypeLambda, ∙, Symbol}
    t match {
      case (ty :::> body) => Term.mkTermAbs(ty, apply(body, acSymbol))
      case TypeLambda(body) => Term.mkTypeAbs(apply(body, acSymbol))
      case TermApp(f, args) => {
        f match {
          case Symbol(id) if id == acSymbol =>
            val acArgRes = apply0(args, acSymbol, Set()).toSeq.sortWith(lt)
            val newArgs = acArgRes.tail.foldRight(acArgRes.head) {case (arg,term) => Term.mkTermApp(f, Vector(arg, term))}
            //        Term.mkTermApp(f, newArgs)
            newArgs
          case _ => Term.mkTermApp(f, args.map(apply(_, acSymbol)))
        }
      }
      case (f ∙ args) => Term.mkApp(f, args.map {case arg => arg.fold({case t => Left(apply(t, acSymbol))}, {case ty => Right(ty)})})
      case _ => t
    }
  }

  def apply0(symbolArgs: Seq[Term], acSymbol: Signature.Key, collectedArgs: Set[Term]): Set[Term] = {
    import leo.datastructures.Term.{TermApp, Symbol}

    if (symbolArgs.isEmpty) collectedArgs
    else {
      val (hdArg, restArgs) = (symbolArgs.head, symbolArgs.tail)
      hdArg match {
        case TermApp(Symbol(id), moreArgs) if id == acSymbol => apply0(restArgs, acSymbol, collectedArgs ++ apply0(moreArgs, acSymbol, Set()))
        case a => apply0(restArgs, acSymbol, collectedArgs + a)
      }
    }
  }

  def apply(lit: Literal, allACSymbols: Set[Signature.Key])(implicit sig: Signature): Literal = {
    val leftAC = lit.left.symbols.distinct intersect allACSymbols
    if (lit.equational) {
      val newLeft = if (leftAC.isEmpty) lit.left else apply(lit.left, leftAC)
      val rightAC = lit.right.symbols.distinct intersect allACSymbols
      val newRight = if (rightAC.isEmpty) lit.right else apply(lit.right, rightAC)
      if (newLeft == lit.left && newRight == lit.right) lit
      else Literal.mkOrdered(newLeft, newRight, lit.polarity) // TODO: Orient?
    } else {
      if (leftAC.isEmpty) lit
      else {
        val norm = apply(lit.left, leftAC)
        if (norm != lit.left)
          Literal(norm, lit.polarity)
        else
          lit
      }
    }
  }

  def apply(cl: Clause, acSymbols: Set[Signature.Key])(implicit sig: Signature): Clause = {
    Clause(cl.lits.map(apply(_, acSymbols)(sig)))
  }
}


object DomainConstraintInstances extends CalculusRule {
  override def name: String = "domainConstraint"
  override def inferenceStatus: SuccessSZS = SZS_Theorem  // TODO what is the status?

  final def apply(c : Clause, domain : Map[Type, Set[Term]], maxInstances : Int)(implicit sig : Signature) : Set[Clause] = {
    var currentInstances = 1
    var clauses = Set(c)
    // consider only variables with domain constraints
    val varToInstance = c.implicitlyBound.filter(v => domain.contains(v._2))
    if(varToInstance.isEmpty) return Set(c)
    // Sort for amount of minimal terms to instanciate to maximize the amount of vaiables eliminated until maxInstances is reached
    val vars = varToInstance.sortBy(v => domain(v._2).size).iterator
    while(vars.hasNext) {
      val (i, ty) = vars.next()
      val terms : Set[Term] = domain(ty)
      if(maxInstances > 0 && currentInstances * terms.size > maxInstances) return clauses
      else {
        // Go over all clauses
        clauses = clauses.flatMap(
          c => terms.map(
            t =>
              // Substitute all literals
              // TODO Ordered here or later?
              // TODO not stable, if variables are remerged
              c.mapLit(l => l.substitute(Subst.singleton(i, t)))
          ))
      }
      currentInstances += terms.size
    }
    clauses
  }
}


object SimplifyReflect extends CalculusRule {
  val name: String = "simplifyReflect"
  val inferenceStatus: SuccessSZS = SZS_Theorem

  def canApplyPos(cl: Clause, lit: Literal, posUnit: Literal): Boolean = {
    assert(!lit.polarity)
    assert(posUnit.polarity)
    isEqualOrMatchable(cl, lit, posUnit)
    // TODO: This could also work on subterms
  }

  def canApplyNeg(cl: Clause, lit: Literal, negUnit: Literal): Boolean = {
    assert(lit.polarity)
    assert(!negUnit.polarity)
    isEqualOrMatchable(cl, lit, negUnit)
  }

  private final def isEqualOrMatchable(cl: Clause, lit: Literal, unit: Literal): Boolean = {
    if (lit.left.ty != unit.left.ty) return false
    if (lit.unsignedEquals(unit)) true
    else {
      if (unit.fv.nonEmpty) {
        val (unitLeft, unitRight) = (unit.left.lift(cl.maxImplicitlyBound), unit.right.lift(cl.maxImplicitlyBound))
        val (litLeft, litRight) = (lit.left, lit.right)
        val vargen = freshVarGen(cl)
        vargen.addVars(unitLeft.fv); vargen.addVars(unitRight.fv)
        val match1 = Matching.applyList(vargen.copy, Seq((unitLeft, litLeft), (unitRight, litRight)))
        if (match1.nonEmpty) {
          true
        } else {
          val match2 = Matching.applyList(vargen.copy, Seq((unitLeft, litRight), (unitRight, litLeft)))
          if (match2.nonEmpty) true
          else false
        }
      } else false
    }
  }
}

object Simp extends CalculusRule {
  final val name = "simp"
  final val inferenceStatus = SZS_Theorem

  final private def eqSimp(l: Literal)(implicit sig: Signature): Literal = {
    if (!l.equational) {
      Literal(internalNormalize(l.left), l.polarity)
    } else {
      val normLeft = internalNormalize(l.left)
      val normRight = internalNormalize(l.right)
      (normLeft, normRight) match {
        case (a,b) if a == b => Literal(LitTrue(), l.polarity)
        case _ => Literal.mkOrdered(normLeft, normRight, l.polarity)
      }
    }
  }

  private final val CANNOTAPPLY = 0
  private final val VARLEFT = 1
  private final val VARRIGHT = 2
  final private def solvedUniEq(lit: Literal): (Int, Int) = {
    if (lit.uni) {
      val left = lit.left
      val right = lit.right
      val leftIsVariable = getVariableModuloEta(left)
      if (leftIsVariable > 0) {
        if(!right.looseBounds.contains(leftIsVariable)) (VARLEFT, leftIsVariable) else (CANNOTAPPLY, -1)
      } else {
        val rightIsVariable = getVariableModuloEta(right)
        if (rightIsVariable > 0) {
          if(!left.looseBounds.contains(rightIsVariable)) (VARRIGHT, rightIsVariable) else (CANNOTAPPLY, -1)
        } else (CANNOTAPPLY, -1)
      }
    } else (CANNOTAPPLY, -1)
  }

  final def apply(lit: Literal)(implicit sig: Signature): Literal = PolaritySwitch(eqSimp(lit))

  /** Only directly use this method if you really know what you are doing.
    * It applies destructive equality resolution and thus needs to be applied to the clause as a whole.
    * Applying this method only to a subset of literals of a clause is in general unsound.
    * Use apply(clause) instead to make sure you simplify clauses as a whole. */
  final def apply(lits: Seq[Literal])(implicit sig: Signature): Seq[Literal] = {
    var newLits: Seq[Literal] = Vector.empty
    var curSubst: Subst = Subst.id
    val litIt = lits.iterator
    while (litIt.hasNext) {
      val lit0 = litIt.next().substituteOrdered(curSubst)
      val lit = apply(lit0)(sig)

      if (!Literal.isFalse(lit)) {
        if (!newLits.contains(lit)) {
          val (maybeSolvedUniEq, idx) = solvedUniEq(lit)
          if (maybeSolvedUniEq == CANNOTAPPLY) {
            newLits = newLits :+ lit
          } else {
            val term = if (maybeSolvedUniEq == VARLEFT) lit.right else lit.left
            val subst = Subst.singleton(idx, term)
            curSubst = curSubst.comp(subst)
          }
        }
      }
    }
    if (curSubst != Subst.id) {
      Out.debug(s"It happend!")
      Out.debug(s"Old lits: ${Clause(lits).pretty(sig)}")
      Out.debug(s"Subst: ${curSubst.normalize.pretty}")
      newLits = newLits.map(l => l.substituteOrdered(curSubst.normalize))
      Out.debug(s"New lits post: ${Clause(newLits).pretty(sig)}")
    }

    val prefvs = newLits.flatMap(_.fv).distinct
    val fvs = prefvs.map(_._1).distinct.sortWith {case (a,b) => a > b}
    val tyFVs = lits.flatMap(_.tyFV).distinct.sortWith {case (a,b) => a > b}

    Out.finest(s"PREFVS:\n\t${prefvs.map(f => f._1 + ":" + f._2.pretty).mkString("\n\t")}")
    Out.finest(s"FVS:\n\t${fvs.map(_.toString).mkString("\n\t")}")
    Out.finest(s"TYFVS:\n\t${tyFVs.mkString("\n\t")}")

    assert(prefvs.size == fvs.size, "Duplicated free vars with different types")

    if (tyFVs.nonEmpty && tyFVs.size != tyFVs.head) {
      Out.finest(s"Ty FV Optimization needed")
      Out.finest(s"Old: \t${tyFVs.mkString("-")}")
      val newTyFvs = Seq.range(tyFVs.size, 0, -1)
      val tySubst = Subst.fromShiftingSeq(tyFVs.zip(newTyFvs))
      Out.finest(s"New: \t${newTyFvs.mkString("-")} ... subst: ${tySubst.pretty}")
      // Same with term variables
      if (fvs.nonEmpty && fvs.size != fvs.head) {
        Out.finest(s"FV Optimization needed")
        Out.finest(s"Old: \t${fvs.mkString("-")}")
        // gaps in fvs
        val newFvs = Seq.range(fvs.size, 0, -1)
        val subst = Subst.fromShiftingSeq(fvs.zip(newFvs))
        Out.finest(s"New: \t${newFvs.mkString("-")} ... subst: ${subst.pretty}")
        newLits.map(_.applyRenamingSubstitution(subst.applyTypeSubst(tySubst), tySubst))
      } else {
        newLits.map(_.applyRenamingSubstitution(Subst.id, tySubst))
      }
    } else  if (fvs.nonEmpty && fvs.size != fvs.head) {
      Out.finest(s"FV Optimization needed")
      Out.finest(s"Old: \t${fvs.mkString("-")}")
      // gaps in fvs
      val newFvs = Seq.range(fvs.size, 0, -1)
      val subst = Subst.fromShiftingSeq(fvs.zip(newFvs))
      Out.finest(s"New: \t${newFvs.mkString("-")} ... subst: ${subst.pretty}")
      newLits.map(_.applyRenamingSubstitution(subst))
    } else newLits
  }

  final def apply(cl: Clause)(implicit sig: Signature): Clause = Clause(apply(cl.lits)(sig))

  final def shallowSimp(lits: Seq[Literal])(implicit sig: Signature): Seq[Literal] = {
    var newLits: Seq[Literal] = Vector.empty
    val litIt = lits.iterator
    while (litIt.hasNext) {
      val lit0 = litIt.next()
      val lit = apply(lit0)(sig)
      if (!Literal.isFalse(lit)) {
        if (!newLits.contains(lit)) {
          newLits = newLits :+ lit
        }
      }
    }
    newLits
  }

  final def shallowSimp(cl: Clause)(implicit sig: Signature): Clause = {
    Clause(shallowSimp(cl.lits)(sig))
  }

  final def detUniInferences(cl: Clause)(implicit sig: Signature): Seq[Clause] = {
    val (posLits, negLits) = (cl.posLits, cl.negLits)
    val processedNegLits = detUniInferences0(negLits, Vector(Vector.empty))(sig)
    processedNegLits.map(nLits => Clause(posLits ++ nLits))
  }
  private final def detUniInferences0(literals: Seq[Literal], acc: Seq[Seq[Literal]])(sig: Signature): Seq[Seq[Literal]] = {
    if (literals.isEmpty) acc
    else {
      val hd = literals.head
      val left = hd.left; val right = hd.right
      val (leftBody, leftAbstractions) = collectLambdas(left)
      val (rightBody, rightAbstractions) = collectLambdas(right)
      assert(leftAbstractions == rightAbstractions, s"Abstraction count does not match:\n\t${left.pretty(sig)}\n\t${right.pretty(sig)}")
      val canApplyDecomp = HuetsPreUnification.DecompRule.canApply((leftBody, rightBody), leftAbstractions.size)
      if (canApplyDecomp._1) {
        leo.Out.finest(s"[UniLitSimp] Can apply Decomp on ${hd.pretty(sig)}")
        if (canApplyDecomp._2.isDefined) {
          val tySubst = canApplyDecomp._2.get
          if (tySubst == Subst.id) {
            // not need to apply tySubst
            val newEqs = HuetsPreUnification.DecompRule((leftBody, rightBody), leftAbstractions)
            val newLits = newEqs.map {case (l,r) => Literal.mkNegOrdered(l,r)(sig)}
            detUniInferences0(literals.tail, acc.map(lits => lits :+ hd) ++ acc.map(lits => lits ++ newLits))(sig)
          } else {
            detUniInferences0(literals.tail, acc.map(lits => lits :+ hd))(sig)
            // TODO
          }
        } else {
          leo.Out.finest(s"[UniLitSimp] Could apply Decomp but typed are non-unifiable")
          detUniInferences0(literals.tail, acc.map(lits => lits :+ hd))(sig)
        }
      } else {
        detUniInferences0(literals.tail, acc.map(lits => lits :+ hd))(sig)
      }
    }
  }

  final def uniLitSimp(l: Seq[Literal])(implicit sig: Signature): (TypeSubst, Seq[Literal]) = {
    leo.modules.myAssert(l.forall(a => !a.polarity))
    val (subst, simpRes) = uniLitSimp0(Vector.empty, l.map(lit => (lit.left, lit.right)).toVector, Subst.id)(sig)
    val simpResAsLits = simpRes.map(eq => Literal.mkNegOrdered(eq._1, eq._2)(sig))
    (subst, simpResAsLits)
  }
  /** Given a unification literal `l` where `l = [a,b]^f`
    * this method returns a sequence of literals (l_i) where each l_i is a (nested)
    * argument to applications of possibly common head symbols in a and b (Decomp rule).
    */
  final def uniLitSimp(l: Literal)(implicit sig: Signature): (TypeSubst, Seq[Literal]) = {
    assert(!l.polarity)
    val (subst, simpRes) = uniLitSimp0(Vector.empty, Vector((l.left, l.right)), Subst.id)(sig)
    val simpResAsLits = simpRes.map(eq => Literal.mkNegOrdered(eq._1, eq._2)(sig))
    (subst, simpResAsLits)
  }
  final def uniLitSimp(left: Term, right: Term)(implicit sig: Signature): (TypeSubst, Seq[Literal]) = {
    val (subst, simpRes) = uniLitSimp0(Vector.empty, Vector((left, right)), Subst.id)(sig)
    val simpResAsLits = simpRes.map(eq => Literal.mkNegOrdered(eq._1, eq._2)(sig))
    (subst, simpResAsLits)
  }

  @tailrec
  private final def uniLitSimp0(processed: Seq[(Term, Term)], unprocessed: Seq[(Term, Term)], subst: TypeSubst)(sig: Signature): (TypeSubst, Seq[(Term, Term)]) = {
    if (unprocessed.isEmpty) (subst, processed)
    else {
      val hd = unprocessed.head
      leo.Out.finest(s"[UniLitSimp] Next unsolved: ${hd._1.pretty(sig)} = ${hd._2.pretty(sig)}")
      val left = hd._1.etaExpand; val right = hd._2.etaExpand
      if (left == right) {
        leo.Out.finest(s"[UniLitSimp] Triv")
        uniLitSimp0(processed, unprocessed.tail, subst)(sig)
      } else {
        val (leftBody, leftAbstractions) = collectLambdas(left)
        val (rightBody, rightAbstractions) = collectLambdas(right)
        assert(leftAbstractions == rightAbstractions, s"Abstraction count does not match:\n\t${left.pretty(sig)}\n\t${right.pretty(sig)}")
        val canApplyDecomp = HuetsPreUnification.DecompRule.canApply((leftBody, rightBody), leftAbstractions.size)
        if (canApplyDecomp._1) {
          leo.Out.finest(s"[UniLitSimp] Can apply Decomp")
          if (canApplyDecomp._2.isDefined) {
            val tySubst = canApplyDecomp._2.get
            if (tySubst == Subst.id) {
              // not need to apply tySubst
              val newEqs = HuetsPreUnification.DecompRule((leftBody, rightBody), leftAbstractions)
              val newUnprocessed = newEqs ++ unprocessed.tail
              uniLitSimp0(processed, newUnprocessed, subst.comp(tySubst))(sig)
            } else {
              val newEqs = HuetsPreUnification.DecompRule((leftBody.typeSubst(tySubst), rightBody.typeSubst(tySubst)), leftAbstractions.map(_.substitute(tySubst)))
              leo.Out.finest(s"type unification can be solved: ${tySubst.pretty}")
              val newUnprocessed = newEqs ++ unprocessed.tail.map{case (l,r) => (l.typeSubst(tySubst), r.typeSubst(tySubst))}
              uniLitSimp0(processed.map{case (l,r) => (l.typeSubst(tySubst), r.typeSubst(tySubst))}, newUnprocessed, subst.comp(tySubst))(sig)
            }
          } else {
            leo.Out.finest(s"[UniLitSimp] Could apply Decomp but typed are non-unifiable")
            uniLitSimp0(hd +: processed, unprocessed.tail, subst)(sig)
          }
        } else {
          leo.Out.finest(s"[UniLitSimp] Cannot apply Decomp")
          uniLitSimp0(hd +: processed, unprocessed.tail, subst)(sig)
        }
      }
    }
  }

  //////////////////////////////////
  //// Simplification implementation by Max
  //////////////////////////////////
  def normalize(t: Term): Term = internalNormalize(t)

  private def internalNormalize(formula: Term): Term = Term.insert(norm(formula.betaNormalize).betaNormalize)

  import leo.datastructures.Term.{Symbol, Bound, ∙}
  import leo.datastructures.Term.local._
  import leo.modules.HOLSignature.<=>
  private def norm(formula : Term) : Term = formula match {
    // First normalize, then match
    case s === t =>
      (norm(s), norm(t)) match {
        case (s1,t1) if s1 == t1 => LitTrue
        case (LitTrue(),t1) => t1
        case (s1,LitTrue()) => s1
        case (LitFalse(), t1) => Not(t1)
        case (s1, LitFalse()) => Not(s1)
        case (s1,t1)             => ===(s1,t1)
      }
    case s & t =>
      (norm(s), norm(t)) match {
        case (s1, t1) if s1 == t1     => s1
        case (s1, Not(t1)) if s1 == t1  => LitFalse
        case (Not(s1), t1) if s1 == t1  => LitFalse
        case (s1, LitTrue())            => s1
        case (LitTrue(), t1)            => t1
        case (_, LitFalse())           => LitFalse
        case (LitFalse(), _)           => LitFalse
        case (s1, t1)                 => &(s1,t1)
      }
    case (s ||| t) =>
      (norm(s),norm(t)) match {
        case (s1,t1) if s1 == t1      => s1
        case (s1, Not(t1)) if s1 == t1   => LitTrue
        case (Not(s1),t1) if s1 == t1   => LitTrue
        case (_, LitTrue())            => LitTrue
        case (LitTrue(), _)            => LitTrue
        case (s1, LitFalse())           => s1
        case (LitFalse(), t1)           => t1
        case (s1, t1)                 => |||(s1,t1)
      }
    case s <=> t =>
      val (ns, nt) = (norm(s), norm(t))
      val res : Term = (ns, nt) match {
        case (s1, t1) if s1 == t1   => LitTrue
        case (s1, LitTrue())        => s1
        case (LitTrue(), t1)        => t1
        case (s1, LitFalse())       => norm(Not(s1))
        case (LitFalse(), t1)       => norm(Not(t1))
        case (s1, t1)               => &(Impl(s1,t1),Impl(t1,s1))
      }
      return res
    case s Impl t =>
      (norm(s), norm(t)) match {
        case (s1, t1) if s1 == t1 => LitTrue
        case (_, LitTrue())        => LitTrue
        case (s1, LitFalse())       => norm(Not(s1))
        case (LitTrue(), t1)        => t1
        case (LitFalse(), _)       => LitTrue
        case (s1,t1)                => Impl(s1,t1)
      }
    case Not(s) => norm(s) match {
      case LitTrue()    => LitFalse
      case LitFalse()   => LitTrue
      case Not(s1)      => s1
      case s1           => Not(s1)
    }
    case Forall(t) => norm(t) match {
      case ty :::> t1 =>
        if (t1.looseBounds.contains(1))
          Forall(mkTermAbs(ty, t1))
        else
          removeUnbound(mkTermAbs(ty,t1))
      case t1         => Forall(t1)
    }
    case Exists(t) => norm(t) match {
      case ty :::> t1 =>
        if (t1.looseBounds.contains(1))
          Exists(mkTermAbs(ty, t1))
        else
          removeUnbound(mkTermAbs(ty,t1))
      case t1         => Exists(t1)
    }

    // Pass through unimportant structures
    case s@Symbol(_)            => s
    case s@Bound(_,_)           => s
    case f ∙ args   => Term.mkApp(norm(f), args.map(_.fold({t => Left(norm(t))},(Right(_)))))
    case ty :::> s  => Term.mkTermAbs(ty, norm(s))
    case TypeLambda(t) => Term.mkTypeAbs(norm(t))
  }

  /**
    * Removes the quantifier from a formula, that is free, by instantiating it
    * and betanormalization.
    *
    * @param formula Abstraction with not bound variable.
    * @return the term without the function.
    */
  private def removeUnbound(formula : Term) : Term = formula match {
    case ty :::> t =>
      //      println("Removed the abstraction in '"+formula.pretty+"'.")
      mkTermApp(formula,mkBound(ty,-4711)).betaNormalize
    case _        => formula
  }
}

/**
  * Moves Quantifiers as far into the
  * term as possible
  */
object Miniscope extends CalculusRule {
  import leo.datastructures.Term._
  import leo.modules.HOLSignature._

  type QUANT_LIST = Vector[(Boolean, Type)]
  type QUANT_ITERATOR = Iterator[(Boolean, Type)]

  type PUSH_TYPE = Int
  @inline final val BOTH : PUSH_TYPE = 3
  @inline final val NONE : PUSH_TYPE = 0
  @inline final val LEFT : PUSH_TYPE = 1
  @inline final val RIGHT : PUSH_TYPE = 2

  final val inferenceStatus = SZS_Theorem
  final val name = "miniscope"

  final def apply(t : Term, pol : Boolean)(implicit sig: Signature): Term = {
    apply0(t, pol, Vector[(Boolean, Type)]())
  }

  /**
    *
    * Performs miniscoping.
    * quants is a stack of removed quantifiers, where
    * (true, ty) --> Forall(\(ty)...)
    * (false, ty) --> Exists(\(ty)...)
    *
    * @param t The term to miniscope
    * @param pol The current polarity
    * @param quants The current quantifier
    * @param sig the signature
    * @return a miniscoped term
    */
  private def apply0(t : Term, pol : Boolean, quants : QUANT_LIST)(implicit sig : Signature): Term = {
    t match {
      case Exists(ty :::> body) => apply0(body, pol, quants :+ (!pol, ty))
      case Forall(ty :::> body) => apply0(body, pol, quants :+ (pol, ty))
      case Not(a) => Not(apply0(a, !pol, quants))
      case (a & b) =>
        val (rest, leftQ, leftSub, rightQ, rightSub) = pushQuants(a, b, quants, pol, pol)
        val amini = apply0(a.substitute(leftSub).betaNormalize, pol, leftQ)
        val bmini = apply0(b.substitute(rightSub).betaNormalize, pol, rightQ)
        prependQuantList(&(amini, bmini), pol, rest)
      case (a ||| b) =>
        val (rest, leftQ, leftSub, rightQ, rightSub) = pushQuants(a, b, quants, pol, !pol)
        val amini = apply0(a.substitute(leftSub).betaNormalize, pol, leftQ)
        val bmini = apply0(b.substitute(rightSub).betaNormalize, pol, rightQ)
        prependQuantList(|||(amini, bmini), pol, rest)
      case Impl(a, b) =>
        val (rest, leftQ, leftSub, rightQ, rightSub) = pushQuants(a, b, quants, pol, !pol)
        val amini = apply0(a.substitute(leftSub), !pol, leftQ)
        val bmini = apply0(b.substitute(rightSub), pol, rightQ)
        prependQuantList(Impl(amini, bmini), pol, rest)
      case other =>
        prependQuantList(other, pol, quants.reverseIterator)
    }
  }

  /**
    *
    * @param left The left side of the operator
    * @param right The right side of the operator
    * @param quants The quantifiers seen to this point
    * @param pol the current polarity
    * @param and if(true) op = AND else op = OR
    * @return
    */
  @inline private final def pushQuants(left : Term, right : Term, quants : QUANT_LIST, pol : Boolean, and : Boolean) : (QUANT_ITERATOR, QUANT_LIST, Subst, QUANT_LIST, Subst) = {
    val it = quants.reverseIterator
    var leftQ : QUANT_LIST = Vector() // Quantifiers pushed left
    var leftSubst : Seq[Int] = Seq()  // Substitution (reversed) removed Quants left
    var rightQ : QUANT_LIST = Vector()  // Quantifiers pushed right
    var rightSubst : Seq[Int] = Seq()  // Substitution (reversed) removed Quants right
    var loop = 1
    while(it.hasNext){
      val q@(quant , ty) = it.next
      val push = testPush(left, right, loop, quant, and)
      if(push != 0) {
        if ((push & LEFT) == LEFT) leftQ = q +: leftQ // Push the quantifier left if possible
        val nFrontl = leftQ.size
        leftSubst = (if(nFrontl > 0) nFrontl else 1) +: leftSubst      // Update indizes

        if((push & RIGHT) == RIGHT) rightQ = q +: rightQ
        val nFrontr = rightQ.size
        rightSubst = (if(nFrontr > 0) nFrontr else 1)  +: rightSubst
      } else {
        val lSub = revListToSubst(leftSubst, leftQ.size)
        val rSub = revListToSubst(rightSubst, rightQ.size)
        return (Iterator(q)++it, leftQ, lSub, rightQ, rSub)
      }
      loop += 1
    }
    return (it, leftQ, revListToSubst(leftSubst, leftQ.size), rightQ, revListToSubst(rightSubst, rightQ.size))
  }

  @inline private final def revListToSubst(preSubst : Seq[Int], shift : Int) = {
    var s : Subst = Subst.shift(shift)
    val it = preSubst.iterator
    while(it.hasNext){
      s = BoundFront(it.next()) +: s
    }
    s
  }

  @inline private final def testPush(left : Term, right : Term, bound : Int, quant : Boolean, and : Boolean) : PUSH_TYPE = {
    var result = 0
    if (left.looseBounds.contains(bound)) result |= 1
    if (right.looseBounds.contains(bound)) result |= 2

    if((!quant && and || quant && !and) && result == 3) 0
    else result
  }


  /**
    * @param quants Reverse Iterator of the quantifier prefix
    */
  private def prependQuantList(t : Term, pol : Boolean, quants : QUANT_ITERATOR) : Term = {
    var itTerm : Term = t
    while(quants.hasNext){
      val (q, ty) = quants.next()
      itTerm = quantToTerm(q, pol)(\(ty)(itTerm))
    }
    itTerm
  }

  private def quantToTerm(quant : Boolean, pol : Boolean) : HOLUnaryConnective = {
    val realQuant = if(pol) quant else !quant
    if(realQuant) Forall else Exists
  }
}
