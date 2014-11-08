package leo.modules.output

import leo.datastructures.internal.terms.Term
import leo.datastructures.internal.terms.Type
import leo.datastructures.internal.terms.{:::>, @@@, ∙, Bound, Symbol}
import leo.datastructures.internal._
import scala.annotation.tailrec
import leo.datastructures.blackboard.FormulaStore

/**
 * @author Alexander Steen
 * @since 07.11.2014
 */
object ToTPTP extends Function1[FormulaStore, Output] {

  def apply(f: FormulaStore): Output = new Output {
    def output = toTPTP(f.name, f.simpleFormula, f.role)
  }

  def apply(name: String, t: Term, role: String): Output = new Output {
    def output = toTPTP(name, t, role)
  }

  def output(f: FormulaStore) = toTPTP(f.name, f.simpleFormula, f.role)
  def output(name: String, t: Term, role: String) = toTPTP(name, t, role)

  ///////////////////////////////
  // Translation of THF formula
  ///////////////////////////////

  private def toTPTP(name: String, t: Term, role: String): String = s"thf(X, Y, (${toTPTP0(t, Seq.empty)}))."
  private def toTPTP0(t: Term, bVars: Seq[(String, Type)]): String = {
    val sig = Signature.get
    t match {
      // Constant symbols
      case Symbol(id) => sig(id).name
      // Give Bound variables names
      case Bound(ty, scope) => bVars(scope)._1
      // Unary connectives
      case Not(t2) => s"${sig(Not.key).name} (${toTPTP0(t2, bVars)})"
      case Forall(_) => val (bVarTys, body) = collectForall(t)
                        val newBVars = makeBVarList(bVarTys)
                        s"${sig(Forall.key).name} [${newBVars.map({case (s,t) => s"$s:${t.pretty}"}).mkString(",")}]: (${toTPTP0(body, newBVars.reverse ++ bVars)})"
      case Exists(_) => val (bVarTys, body) = collectExists(t)
                        val newBVars = makeBVarList(bVarTys)
                        s"${sig(Exists.key).name} [${newBVars.map({case (s,t) => s"$s:${t.pretty}"}).mkString(",")}]: (${toTPTP0(body, newBVars.reverse ++ bVars)})"
      // Binary connectives
      case t1 ||| t2 => s"${toTPTP0(t1, bVars)} ${sig(|||.key).name} ${toTPTP0(t2, bVars)}"
      case t1 === t2 => s"${toTPTP0(t1, bVars)} ${sig(===.key).name} ${toTPTP0(t2, bVars)}"
      case t1 & t2 => s"${toTPTP0(t1, bVars)} ${sig(&.key).name} ${toTPTP0(t2, bVars)}"
      case t1 Impl t2 => s"${toTPTP0(t1, bVars)} ${sig(Impl.key).name} ${toTPTP0(t2, bVars)}"
      case t1 <= t2  => s"${toTPTP0(t1, bVars)} ${sig(<=.key).name} ${toTPTP0(t2, bVars)}"
      case t1 <=> t2 => s"${toTPTP0(t1, bVars)} ${sig(<=>.key).name} ${toTPTP0(t2, bVars)}"
      case t1 ~& t2 => s"${toTPTP0(t1, bVars)} ${sig(~&.key).name} ${toTPTP0(t2, bVars)}"
      case t1 ~||| t2 => s"${toTPTP0(t1, bVars)} ${sig(~|||.key).name} ${toTPTP0(t2, bVars)}"
      case t1 <~> t2 => s"${toTPTP0(t1, bVars)} ${sig(<~>.key).name} ${toTPTP0(t2, bVars)}"
      case t1 !=== t2 => s"${toTPTP0(t1, bVars)} ${sig(!===.key).name} ${toTPTP0(t2, bVars)}"
      // General structure
      case _ :::> _ => val (bVarTys, body) = collectLambdas(t)
                       val newBVars = makeBVarList(bVarTys)
                       s"^ [${newBVars.map({case (s,t) => s"$s:${t.pretty}"}).mkString(",")}]: (${toTPTP0(body, newBVars.reverse ++ bVars)})"
      case t1 @@@ t2 => s"${toTPTP0(t1, bVars)} @ ${toTPTP0(t2, bVars)}"
      case f ∙ args => args.foldLeft(toTPTP0(f, bVars))({case (str, arg) => s"($str @ ${toTPTP0(arg.fold(identity, ???), bVars)})"})
      // Others should be invalid
      case _ => throw new IllegalArgumentException("Unexpected term format during toTPTP conversion")
    }
  }


  // Utility methods

  /** Gather consecutive all-quantifications (nameless). */
  private def collectForall(t: Term): (Seq[Type], Term) = {
    collectForall0(Seq.empty, t)
  }
  @tailrec
  private def collectForall0(vars: Seq[Type], t: Term): (Seq[Type], Term) = {
    t match {
      case Forall(ty :::> b) => collectForall0(vars :+ ty, b)
      case Forall(_) => throw new IllegalArgumentException("Unexcepted body term in all quantification decomposition.")
      case _ => (vars, t)
    }
  }
  /** Gather consecutive exist-quantifications (nameless). */
  private def collectExists(t: Term): (Seq[Type], Term) = {
    collectExists0(Seq.empty, t)
  }
  @tailrec
  private def collectExists0(vars: Seq[Type], t: Term): (Seq[Type], Term) = {
    t match {
      case Exists(ty :::> b) => collectExists0(vars :+ ty, b)
      case Exists(_) => throw new IllegalArgumentException("Unexcepted body term in existsl quantification decomposition.")
      case _ => (vars, t)
    }
  }

  /** Gather consecutive lambda-abstractions (nameless). */
  private def collectLambdas(t: Term): (Seq[Type], Term) = {
    collectLambdas0(Seq.empty, t)
  }
  @tailrec
  private def collectLambdas0(vars: Seq[Type], t: Term): (Seq[Type], Term) = {
    t match {
      case ty :::> b => collectLambdas0(vars :+ ty, b)
      case _ => (vars, t)
    }
  }

  private def makeBVarList(tys: Seq[Type]): Seq[(String, Type)] = ???

}
