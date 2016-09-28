package leo.modules.preprocessing

import leo.datastructures.Term.:::>
import leo.datastructures._
import leo.modules.calculus._
import leo.modules.output.SZS_Theorem

/**
  * Created by mwisnie on 11.04.16.
  */
object StepCNF extends CalculusRule {
  override def name: String = "cnf"
  final override val inferenceStatus = Some(SZS_Theorem)

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

  final def apply(vargen : leo.modules.calculus.FreshVarGen,l : Literal) : CNF = if(!l.equational){
    l.left match {
      case Not(t) => One(Literal(t, !l.polarity))
      case &(lt,rt) if l.polarity => Alpha(Literal(lt,true), Literal(rt,true))
      case &(lt,rt) if !l.polarity => Beta(Literal(lt,false), Literal(rt, false))
      case |||(lt,rt) if l.polarity => Beta(Literal(lt,true), Literal(rt, true))
      case |||(lt,rt) if !l.polarity => Alpha(Literal(lt,false), Literal(rt,false))
      case Impl(lt,rt) if l.polarity => Beta(Literal(lt,false), Literal(rt, true))
      case Impl(lt,rt) if !l.polarity => Alpha(Literal(lt,true), Literal(rt,false))
      case Forall(a@(ty :::> t)) if l.polarity => val newVar = vargen(ty); One(Literal(Term.mkTermApp(a, newVar).betaNormalize, true))
      case Forall(a@(ty :::> t)) if !l.polarity => val sko = leo.modules.calculus.skTerm(ty, vargen.existingVars, vargen.existingTyVars); One(Literal(Term.mkTermApp(a, sko).betaNormalize, false))
      case Exists(a@(ty :::> t)) if l.polarity => val sko = leo.modules.calculus.skTerm(ty, vargen.existingVars, vargen.existingTyVars); One(Literal(Term.mkTermApp(a, sko).betaNormalize, true))
      case Exists(a@(ty :::> t)) if !l.polarity => val newVar = vargen(ty); One(Literal(Term.mkTermApp(a, newVar).betaNormalize, false))
      case _ => None(l)
    }
  } else None(l)


  final def step(vargen : leo.modules.calculus.FreshVarGen, ls : Seq[Literal]) : Seq[Seq[Literal]] = {
    val (norm, l+:rest) = ls.span(l => !canApply(l))
    val c = norm ++ rest
    apply(vargen, l) match {
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
  final def apply(vargen : leo.modules.calculus.FreshVarGen, ls : Seq[Seq[Literal]]) : Seq[Seq[Literal]] = {
    val (norm, rest) = ls.span(ls1 => !canApply(ls1))
    rest match {
      case Seq()  => ls
      case (a +: c) => (c ++ step(vargen,a)) ++ norm
    }
  }

  final def exhaust(c : Clause) : Seq[Clause] = {
    var ls = Seq(c.lits)
    val vargen = freshVarGen(c)
    while(ls exists canApply){
      ls = apply(vargen, ls)
    }
    ls.map(ls1 => Clause(ls1))
  }
}

