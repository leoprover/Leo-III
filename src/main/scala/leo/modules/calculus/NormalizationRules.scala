package leo.modules.calculus

import leo._
import leo.datastructures.Term.{:::>, TypeLambda}
import leo.datastructures.{Clause, HOLBinaryConnective, Subst, Type, _}
import leo.datastructures.impl.Signature
import leo.modules.output.{SZS_EquiSatisfiable, SZS_Theorem}
import leo.modules.preprocessing.Simplification

/**
  * Created by lex on 5/12/16.
  */

////////////////////////////////////////////////////////////////
////////// Normalization
////////////////////////////////////////////////////////////////
// TODO: Encode origin of boolext clauses so that they are not paramodulated
// with its ancestor clause.

object PolaritySwitch extends CalculusRule {
  val name = "polarity_switch"
  override val inferenceStatus = Some(SZS_Theorem)
  def canApply(t: Term): Boolean = t match {
    case Not(_) => true
    case _ => false
  }
  def canApply(l: Literal): Boolean = canApply(l.left) || canApply(l.right)

  def apply(t: Term): Term = t match {
    case Not(t2) => t2
    case _ => t
  }

  def apply(l: Literal): Literal = if (l.equational) {
    (l.left, l.right) match {
      case (Not(l2), Not(r2)) => Literal(l2, r2, l.polarity)
//      case (Not(l2), _) => Literal(l2, l.right, !l.polarity)
//      case (_, Not(r2)) => Literal(l.left, r2, !l.polarity) // FIXME: This is sound but we lose information, right?
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
  * Created by mwisnie on 4/4/16.
  */
object FullCNF extends CalculusRule {
  override def name: String = "cnf"
  final override val inferenceStatus = Some(SZS_EquiSatisfiable)
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
      case TypeLambda(_) => true
      case _ => false
    }
  } else false

  final def apply(vargen: leo.modules.calculus.FreshVarGen, cl: Clause): Seq[Clause] = {
    val lits = cl.lits
    val normLits = apply(vargen, lits)
    normLits.map{ls => Clause(ls)}
  }

  final def apply(vargen: leo.modules.calculus.FreshVarGen, l : Seq[Literal]) : (Seq[Seq[Literal]]) = {
    var acc : Seq[Seq[Literal]] = Seq(Seq())
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

  final def apply(vargen: leo.modules.calculus.FreshVarGen, l : Literal) : Seq[Seq[Literal]] = apply0(vargen.existingVars, vargen.existingTyVars, vargen, l)

  @inline
  final private def apply0(fvs: FVs, tyFVs: TyFVS, vargen: leo.modules.calculus.FreshVarGen, l : Literal) : Seq[Seq[Literal]] = if(!l.equational){
    l.left match {
      case Not(t) => apply0(fvs, tyFVs, vargen, Literal(t, !l.polarity))
      case &(lt,rt) if l.polarity => apply0(fvs, tyFVs, vargen, Literal(lt,true)) ++ apply0(fvs, tyFVs, vargen, Literal(rt,true))
      case &(lt,rt) if !l.polarity => multiply(apply0(fvs, tyFVs, vargen, Literal(lt,false)), apply0(fvs, tyFVs, vargen, Literal(rt, false)))
      case |||(lt,rt) if l.polarity => multiply(apply0(fvs, tyFVs, vargen, Literal(lt,true)), apply0(fvs, tyFVs, vargen, Literal(rt, true)))
      case |||(lt,rt) if !l.polarity => apply0(fvs, tyFVs, vargen, Literal(lt,false)) ++ apply0(fvs, tyFVs, vargen, Literal(rt,false))
      case Impl(lt,rt) if l.polarity => multiply(apply0(fvs, tyFVs, vargen, Literal(lt,false)), apply0(fvs, tyFVs, vargen, Literal(rt, true)))
      case Impl(lt,rt) if !l.polarity => apply0(fvs, tyFVs, vargen, Literal(lt,true)) ++ apply0(fvs, tyFVs, vargen, Literal(rt,false))
      case Forall(a@(ty :::> t)) if l.polarity => val v = vargen.next(ty); apply0(v +: fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, Term.mkBound(v._2, v._1)).betaNormalize, true))
      case Forall(a@(ty :::> t)) if !l.polarity => val sko = leo.modules.calculus.skTerm(ty, fvs, tyFVs); apply0(fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, sko).betaNormalize, false))
      case Exists(a@(ty :::> t)) if l.polarity => val sko = leo.modules.calculus.skTerm(ty, fvs, tyFVs); apply0(fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, sko).betaNormalize, true))
      case Exists(a@(ty :::> t)) if !l.polarity => val v = vargen.next(ty); apply0(v +: fvs, tyFVs, vargen, Literal(Term.mkTermApp(a, Term.mkBound(v._2, v._1)).betaNormalize, false))
      case TypeLambda(t) if l.polarity => apply0(fvs, tyFVs, vargen, Literal(t, true)) //FIXME add free type variables
      case term@TypeLambda(t) if !l.polarity => val sko = leo.datastructures.impl.Signature.get.freshSkolemTypeConst; apply0(fvs, tyFVs, vargen, Literal(Term.mkTypeApp(term, Type.mkType(sko)).betaNormalize, false))
      case _ => Seq(Seq(l))
    }
  } else {
    Seq(Seq(l))
  }

  private final def multiply[A](l : Seq[Seq[A]], r : Seq[Seq[A]]) : Seq[Seq[A]] = {
    var acc : Seq[Seq[A]] = Seq()
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
  val name = "lifteq"
  override val inferenceStatus = Some(SZS_Theorem)

  import leo.datastructures.{=== => EQ}
  def canApply(t: Term): Boolean = t match {
    case EQ(_,_) => true
    case _ => false
  }
  def canApply(lit: Literal): Boolean = !lit.equational && canApply(lit.left)
  type LiftLits = Seq[Literal]
  type OtherLits = Seq[Literal]
  def canApply(cl: Clause): (Boolean, LiftLits, OtherLits) = {
    var can = false
    var liftLits: LiftLits = Seq()
    var otherLits: OtherLits = Seq()
    val lits = cl.lits.iterator
    while (lits.hasNext) {
      val l = lits.next()
      if (canApply(l)) {
        liftLits = liftLits :+ l
        can = true
      } else {
        otherLits = otherLits :+ l
      }
    }
    (can, liftLits, otherLits)
  }
  def apply(liftLits: LiftLits, otherLits: OtherLits): Seq[Literal] = {
    liftLits.map(l => apply(l.left, l.polarity)) ++ otherLits
  }


  def apply(left: Term, polarity: Boolean): Literal = {
    val (l,r) = EQ.unapply(left).get
    if (polarity) {
      Literal(l,r,true)
    } else {
      Literal(l,r,false)
    }
  }

}


object ReplaceLeibnizEq extends CalculusRule {
  val name = "replace_leibeq"
  override val inferenceStatus = Some(SZS_Theorem)
  type Polarity = Boolean


  def canApply(cl: Clause): (Boolean, Map[Int, Term]) = {
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
                if (arg.compareTo(curEntry) == CMP_LT) {
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

  def apply(cl: Clause, bindings: Map[Int, Term]): (Clause, Subst) = {
    val gbMap = bindings.mapValues(t => Term.mkTermAbs(t.ty, ===(t.substitute(Subst.shift(1)), Term.mkBound(t.ty, 1))))
    val subst = Subst.fromMap(gbMap)
    val newLits = cl.lits.map(_.substitute(subst))
    (Clause((newLits)), subst)
  }
}

object ReplaceAndrewsEq extends CalculusRule {
  val name = "replace_andrewseq"
  override val inferenceStatus = Some(SZS_Theorem)

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

  def apply(cl: Clause, vars: Map[Int, Type]): (Clause, Subst) = {
    val gbMap = vars.mapValues {case ty => Term.λ(ty,ty)(===(Term.mkBound(ty,2), Term.mkBound(ty,1)))}
    val subst = Subst.fromMap(gbMap)
    val newLits = cl.lits.map(_.substitute(subst))
    (Clause((newLits)), subst)
  }
}

object RewriteSimp extends CalculusRule {
  val name = "rewrite"
  override val inferenceStatus = Some(SZS_Theorem)

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
  def apply(intoClause: Clause, intoConfigurations: Map[Int, IntoConfiguration] ): Clause = {
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

      val newLit = Literal(newLeft, newRight, lit.polarity)
      lits = lits.updated(litIndex, newLit)
    }

    Clause(lits)
  }

}

object ACSimp extends CalculusRule {
  val name = "ac_simp"
  override val inferenceStatus = Some(SZS_Theorem)

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
      case (a,b) => a.size < b.size

    }
  }

  def apply(t: Term, acSymbols: Set[Signature#Key]): Term = {
    acSymbols.foldLeft(t){case (term,symbol) => apply(term, symbol)}
  }

  def apply(t: Term, acSymbol: Term): Term = {
    import leo.datastructures.Term.{:::>, TermApp, TypeLambda, ∙}
    t match {
      case (ty :::> body) => Term.mkTermAbs(ty, apply(body, acSymbol))
      case TypeLambda(body) => Term.mkTypeAbs(apply(body, acSymbol))
      case TermApp(f, args) if f == acSymbol => {
        val acArgRes = apply0(args, acSymbol, Set()).toSeq.sortWith(lt)

        val newArgs = acArgRes.tail.foldRight(acArgRes.head) {case (arg,term) => Term.mkTermApp(acSymbol, Seq(arg, term))}
        //        Term.mkTermApp(f, newArgs)
        newArgs
      }
      case (f ∙ args) => Term.mkApp(f, args.map {case arg => arg.fold({case t => Left(apply(t, acSymbol))}, {case ty => Right(ty)})})
      case _ => t
    }
  }

  def apply0(symbolArgs: Seq[Term], acSymbol: Term, collectedArgs: Set[Term]): Set[Term] = {
    import leo.datastructures.Term.TermApp

    if (symbolArgs.isEmpty) collectedArgs
    else {
      val (hdArg, restArgs) = (symbolArgs.head, symbolArgs.tail)
      hdArg match {
        case TermApp(f, moreArgs) if f == acSymbol => apply0(restArgs, acSymbol, collectedArgs ++ apply0(moreArgs, acSymbol, Set()))
        case a => apply0(restArgs, acSymbol, collectedArgs + a)
      }
    }
  }

  def apply(lit: Literal, allACSymbols: Set[Signature#Key]): Literal = {
    val leftAC = lit.left.symbols intersect allACSymbols
    if (lit.equational) {
      val newLeft = if (leftAC.isEmpty) lit.left else apply(lit.left, leftAC)
      val rightAC = lit.right.symbols intersect allACSymbols
      val newRight = if (rightAC.isEmpty) lit.right else apply(lit.right, rightAC)
      if (newLeft == lit.left && newRight == lit.right) lit
      else Literal(newLeft, newRight, lit.polarity)
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

  def apply(cl: Clause, acSymbols: Set[Signature#Key]): Clause = {
    Clause(cl.lits.map(apply(_, acSymbols)))
  }
}

object Simp extends CalculusRule {
  val name = "simp"
  override val inferenceStatus = Some(SZS_Theorem)

  def apply(lit: Literal): Literal = if (lit.equational) {
    PolaritySwitch(eqSimp(Literal(Simplification.normalize(lit.left), Simplification.normalize(lit.right), lit.polarity)))
  } else {
    PolaritySwitch(Literal(Simplification.normalize(lit.left), lit.polarity))
  }

  def apply(lits: Seq[Literal]): Seq[Literal] = {
    //Out.finest(s"FVs:\n\t${cl.implicitlyBound.map(f => f._1 + ":" + f._2.pretty).mkString("\n\t")}")
    var newLits: Seq[Literal] = Seq()
    val litIt = lits.iterator
    while (litIt.hasNext) {
      val lit = apply(litIt.next())
      //      val lit = litIt.next()
      if (!Literal.isFalse(lit)) {
        if (!newLits.contains(lit)) {
          newLits = newLits :+ lit
        }
      }
    }
    val prefvs = newLits.map{_.fv}.fold(Set())((s1,s2) => s1 ++ s2)
    Out.finest(s"PREFVS:\n\t${prefvs.map(f => f._1 + ":" + f._2.pretty).mkString("\n\t")}")
    val fvs = prefvs.map(_._1).toSeq.sortWith {case (a,b) => a > b}

    assert(prefvs.size == fvs.size, "Duplicated free vars with different types")
    if (fvs.nonEmpty) {
      if (fvs.size != fvs.head) {
        Out.finest(s"FV Optimization needed")
        Out.finest(s"Old: \t${fvs.mkString("-")}")
        // gaps in fvs
        val newFvs = Seq.range(fvs.size, 0, -1)
        val subst = Subst.fromShiftingSeq(fvs.zip(newFvs))
        Out.finest(s"New: \t${newFvs.mkString("-")} ... subst: ${subst.pretty}")
        return (newLits.map(_.substitute(subst)))
      }
    }
    newLits
  }

  def apply(cl: Clause): Clause  = Clause(apply(cl.lits))

  def shallowSimp(cl: Clause): Clause = {
    Clause(shallowSimp0(cl))
  }

  def shallowSimp0(cl: Clause): Seq[Literal] = {
    var newLits: Seq[Literal] = Seq()
    val litIt = cl.lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      val normLit = eqSimp(lit)
      if (!Literal.isFalse(normLit)) {
        if (!newLits.contains(normLit)) {
          newLits = newLits :+ normLit
        }
      }
    }
    newLits
  }

  def eqSimp(l: Literal): Literal = {
    if (!l.equational) l
    else (l.left, l.right) match {
      case (a,b) if a == b => Literal(LitTrue(), l.polarity)
      case (a,b) => l
    }
  }

}

/**
  * Skolemization as preprocessing step.
  * It is assumed that the term originates from a unit clause
  * that has no implicitly quantified variables.
  *
  * Assumes that term is in NNF.
  */
object Skolemization extends CalculusRule {
  import leo.datastructures.Term._

  override val inferenceStatus = Some(SZS_EquiSatisfiable)
  val name = "skolemize"

  def apply(t: Term, s: Signature): Term = {
    apply0(miniscope(t), s, Seq())
  }

  private def apply0(t: Term, s: Signature, fvs: Seq[Term]): Term = {
    t match {
      case Exists(inner@(ty :::> body)) => {
        val skConst = Term.mkAtom(s.freshSkolemConst(Type.mkFunType(fvs.map(_.ty), ty)))
        val skTerm = Term.mkTermApp(skConst, fvs)
        val body2 = Term.mkTermApp(inner, skTerm).betaNormalize
        apply0(body2, s, fvs)
      }
      case Forall(ty :::> body) => {
        val newFvs = fvs.map(_.substitute(Subst.shift)) :+ Term.mkBound(ty, 1)
        val body2 = apply0(body, s, newFvs)
        Forall(λ(ty)(body2))
      }
      case (a & b) => &(apply0(a, s, fvs), apply0(b, s, fvs))
      case (a ||| b) => |||(apply0(a, s, fvs), apply0(b, s, fvs))
      case _ => t
    }
  }

  /**
    *
    * Moves a quantifier inward, such that the computed skolemterm
    * does only depend on the minimum amount of variables.
    *
    * @param formula - That will be skolemmized
    * @return - The formula with quantifiers most inward
    */
  def miniscope(formula : Term) : Term = formula match {
    case Exists (ty :::> t) => miniscope(t) match {
      case (t1 & t2) if !t2.looseBounds.contains(1) =>
        val left = miniscope(Exists(mkTermAbs(ty,t1)))
        val right = miniscope(t2.substitute(decreaseFVSubst))
        &(left,right)
      case (t1 & t2) if !t1.looseBounds.contains(1) =>
        val right = miniscope(Exists(mkTermAbs(ty,t2)))
        val left = miniscope(t1.substitute(decreaseFVSubst))
        &(left,right)
      case (t1 ||| t2) if !t2.looseBounds.contains(1) =>
        val left = miniscope(Exists(mkTermAbs(ty,t1)))
        val right = miniscope(t2.substitute(decreaseFVSubst))
        |||(left,right)
      case (t1 ||| t2) if !t1.looseBounds.contains(1) =>
        val right = miniscope(Exists(mkTermAbs(ty,t2)))
        val left = miniscope(t1.substitute(decreaseFVSubst))
        |||(left,right)
      case (t1 ||| t2) =>
        val left = miniscope(Exists(mkTermAbs(ty,t1)))
        val right = miniscope(Exists(mkTermAbs(ty,t2)))
        |||(left,right)
      // In neither of the above cases, move inwards
      case s@Symbol(_)            => s
      case s@Bound(_,i)           => if(i == 1) LitTrue() else s
      case f ∙ args   => Exists(λ(ty)(Term.mkApp(miniscope(f), args.map(_.fold({t => Left(miniscope(t))},Right(_))))))
      case ty2 :::> s  => Exists(λ(ty,ty2)(miniscope(s))) // TODO CHECK
      case TypeLambda(t2) => Exists(λ(ty)(mkTypeAbs(miniscope(t2)))) // TODO CHECK
      case _  => formula
    }

    //Same for Forall
    case Forall (ty :::> t) => miniscope(t) match {
      //First Case: Conjuction t1 & t2 and bound var not contained in one of the conjuncts
      case (t1 & t2) if !t2.looseBounds.contains(1) =>
        val left = miniscope(Forall(mkTermAbs(ty,t1)))
        val right = miniscope(t2.substitute(decreaseFVSubst))
        &(left,right)
      case (t1 & t2) if !t1.looseBounds.contains(1) =>
        val right = miniscope(Forall(mkTermAbs(ty,t2)))
        val left = miniscope(t1.substitute(decreaseFVSubst))
        &(left,right)
      //Second Case, Disjunction t1 || t2 and bound var not contained in one of the disjuncts
      case (t1 ||| t2) if !t2.looseBounds.contains(1) =>
        val left = miniscope(Forall(mkTermAbs(ty,t1)))
        val right = miniscope(t2.substitute(decreaseFVSubst))
        |||(left,right)
      case (t1 ||| t2) if !t1.looseBounds.contains(1) =>
        val right = miniscope(Forall(mkTermAbs(ty,t2)))
        val left = miniscope(t1.substitute(decreaseFVSubst))
        |||(left,right)
      // Both are bound, and it is a cunjunction
      case (t1 & t2) =>
        val left = miniscope(Forall(mkTermAbs(ty,t1)))
        val right = miniscope(Forall(mkTermAbs(ty,t2)))
        &(left,right)
      // In neither of the above cases, move inwards
      case s@Symbol(_)            => s
      case s@Bound(_,i)           => if(i == 1) LitFalse() else s
      case f ∙ args   => Forall(λ(ty)(mkApp(miniscope(f), args.map(_.fold({t => Left(miniscope(t))},Right(_))))))
      case ty2 :::> s  => Forall(λ(ty,ty2)(miniscope(s)))
      case TypeLambda(t2) => Forall(λ(ty)(mkTypeAbs(miniscope(t2))))
            case _  => formula
    }

    // In neither of the above cases, move inwards
    case s@Symbol(_)            => s
    case s@Bound(_,_)           => s
    case f ∙ args   => Term.mkApp(miniscope(f), args.map(_.fold({t => Left(miniscope(t))},Right(_))))
    case ty :::> s  => λ(ty)(miniscope(s))
    case TypeLambda(t) => mkTypeAbs(miniscope(t))
    //    case _  => formula

  }
  private val decreaseFVSubst: Subst = BoundFront(1) +: Subst.id
}