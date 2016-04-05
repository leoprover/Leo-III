package leo.modules.preprocessing

import leo.datastructures.Term.:::>
import leo.datastructures._
import leo.modules.calculus.CalculusRule
import leo.modules.output.SZS_Theorem

/**
  * Created by mwisnie on 4/4/16.
  */
object FullCNF extends CalculusRule {
  override def name: String = "cnf"
  final override val inferenceStatus = Some(SZS_Theorem)

  type FormulaCharacter = Byte
  final val none: FormulaCharacter = 0.toByte
  final val alpha: FormulaCharacter = 1.toByte
  final val beta: FormulaCharacter = 2.toByte
  final val one: FormulaCharacter = 3.toByte  // A bit hacky, we want to omit ++ operations below
  //  final val four: FormulaCharacter = 4.toByte  // A bit hacky, we want to omit ++ operations below

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

  final def apply(vargen: leo.modules.calculus.FreshVarGen, cl: Clause): Seq[Clause] = {
    println("C")
    val lits = cl.lits
    val normLits = apply(vargen, lits)
    normLits.map{ls => Clause(ls)}
  }

  final def apply(vargen : leo.modules.calculus.FreshVarGen, l : Seq[Literal]) : (Seq[Seq[Literal]]) = {
    var acc : Seq[Seq[Literal]] = Seq(Seq())
    val it : Iterator[Literal] = l.iterator
    while(it.hasNext){
      val nl = it.next()
      apply(vargen, nl) match {
        case Seq(Seq(l)) => acc = acc.map{normLits => l +: normLits}
        case norms =>  acc = multiply(norms, acc)
      }
    }
    acc
  }

  final def apply(vargen : leo.modules.calculus.FreshVarGen, l : Literal) : Seq[Seq[Literal]] = if(!l.equational){
    l.left match {
      case Not(t) => apply(vargen, Literal(t, !l.polarity))
      case &(lt,rt) if l.polarity => apply(vargen,Literal(lt,true)) ++ apply(vargen, Literal(rt,true))
      case &(lt,rt) if !l.polarity => multiply(apply(vargen, Literal(lt,false)), apply(vargen, Literal(rt, false)))
      case |||(lt,rt) if l.polarity => multiply(apply(vargen, Literal(lt,true)), apply(vargen, Literal(rt, true)))
      case |||(lt,rt) if !l.polarity => apply(vargen,Literal(lt,false)) ++ apply(vargen, Literal(rt,false))
      case Impl(lt,rt) if l.polarity => multiply(apply(vargen, Literal(lt,false)), apply(vargen, Literal(rt, true)))
      case Impl(lt,rt) if !l.polarity => apply(vargen,Literal(lt,true)) ++ apply(vargen, Literal(rt,false))
      case Forall(ty :::> t) if l.polarity => val newVar = vargen(ty); apply(vargen, Literal(Term.mkTermApp(t, newVar.betaNormalize), true))
      case Forall(ty :::> t) if !l.polarity => val sko = leo.modules.calculus.skTerm(ty, vargen.existingVars); apply(vargen, Literal(Term.mkTermApp(t, sko).betaNormalize, false))
      case Exists(ty :::> t) if l.polarity => val sko = leo.modules.calculus.skTerm(ty, vargen.existingVars); apply(vargen, Literal(Term.mkTermApp(t, sko).betaNormalize, true))
      case Exists(ty :::> t) if !l.polarity => val newVar = vargen(ty); apply(vargen, Literal(Term.mkTermApp(t, newVar.betaNormalize), false))
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
