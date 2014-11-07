package leo.modules.output

import leo.datastructures.internal.terms.Term
import leo.datastructures.internal.terms.Type
import leo.datastructures.internal.terms.:::>
import leo.datastructures.internal._
import scala.annotation.tailrec

/**
 * @author Alexander Steen
 * @since 07.11.2014
 */
object ToTPTP extends Function1[Term, Output] {

  def apply(t: Term): Output = new Output {
    def output = toTPTP(t)
  }

  private def toTPTP(t: Term): String = toTPTP0(t, Seq.empty)
  private def toTPTP0(t: Term, bVars: Seq[(String, Type)]): String = {
    val sig = Signature.get
    t match {
      // Constant symbols
      case Symbol(id) => sig(id).name
      // Unary connectives
      case Not(t2) => s"${sig(Not.key).name} (${toTPTP0(t2, bVars)})"
      case Forall(_) => val (bVarTys, body) = collectForall(t)
                        val newBVars = makeBVarList(bVarTys)
                        s"${sig(Forall.key).name} [${newBVars.map({case (s,t) => s"$s:${t.pretty}"}).mkString(",")}]: (${toTPTP(body)})" //...TODO
      case Exists(_) => val (bVarTys, body) = collectExists(t)
                        val newBVars = makeBVarList(bVarTys)
                        s"${sig(Exists.key).name} [${newBVars.map({case (s,t) => s"$s:${t.pretty}"}).mkString(",")}]: (${toTPTP(body)})"
      // Binary connectives
      //...

      case _ => throw new IllegalArgumentException("Unexpected term format during toTPTP conversion")
    }
  }


  // Utility methods

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

  private def makeBVarList(tys: Seq[Type]): Seq[(String, Type)] = ???

}
