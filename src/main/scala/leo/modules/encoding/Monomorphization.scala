package leo.modules.encoding

import leo.datastructures.{Signature, Term, Type, Clause, Literal}
import scala.annotation.tailrec

/**
  * Translates polymorphic first-order problems into
  * monomorphic first-order problems.
  */
object Monomorphization {
  type MonoResult = (Problem, Signature)
  final def apply(problem: Problem)(implicit sig: Signature): (Problem, Signature) = {
    val clsIt = problem.iterator
    val newSig: Signature = TypedFOLEncodingSignature()
    var monoProblem: Problem = Set.empty
    while (clsIt.hasNext) {
      val cl = clsIt.next()
      monoProblem = monoProblem + apply0(cl, newSig)(sig)
    }
    (monoProblem, newSig)
  }

  private final def apply0(cl: Clause, newSig: Signature)(sig: Signature): Clause = {
    Clause(cl.lits.map(apply0(_, newSig)(sig)))
  }

  private final def apply0(lit: Literal, newSig: Signature)(sig: Signature): Literal = {
    if (lit.equational) {
      val newLeft = apply0(lit.left, newSig)(sig)
      val newRight = apply0(lit.right, newSig)(sig)
      Literal.mkLit(newLeft, newRight, lit.polarity)
    } else {
      Literal.mkLit(apply0(lit.left, newSig)(sig), lit.polarity)
    }
  }

  private final def apply0(t: Term, newSig: Signature)(implicit sig: Signature): Term = {
    import Term._
    t match {
      case (f@Symbol(id)) ∙ args => if (f.ty.isPolyType) {
        val (tyArgs, termArgs) = partitionArgs(args)
        val monoType = f.ty.instantiate(tyArgs)
        val name = monoInstanceName(id, tyArgs)(sig)
        val newF = if (newSig.exists(name)) local.mkAtom(newSig(name).key)(newSig)
        else local.mkAtom(newSig.addUninterpreted(name, convertType(monoType, sig, newSig)))(newSig)
        val newArgs = termArgs.map(arg => apply0(arg, newSig))
        local.mkTermApp(newF, newArgs)
      } else {
        assert(args.forall(_.isLeft), s"not all arguments of ${f.pretty(sig)} (type: ${f.ty.pretty(sig)}) terms in: ${t.pretty(sig)}")
        val name = escape(sig(id).name)
        val newF = if (newSig.exists(name)) local.mkAtom(newSig(name).key)(newSig)
        else local.mkAtom(newSig.addUninterpreted(name, convertType(sig(id)._ty, sig, newSig)))(newSig)
        val newArgs = args.map(arg => apply0(arg.left.get, newSig))
        local.mkTermApp(newF, newArgs)
      }
      case _ => throw new IllegalArgumentException
    }
  }

  final def convertType(ty: Type, oldSig: Signature, newSig: Signature): Type = {
    import Type._
    ty match {
      case BaseType(id) =>
        val name = escape(oldSig(id).name)
        if (newSig.exists(name)) mkType(newSig(name).key)
        else mkType(newSig.addBaseType(name))
      case ComposedType(id, args) =>
        val meta = oldSig(id)
        val name = escape(meta.name)
        val kind = meta._kind
        val convertedArgs = args.map(convertType(_, oldSig, newSig))
        if (newSig.exists(name)) mkType(newSig(name).key, convertedArgs)
        else mkType(newSig.addTypeConstructor(name, kind), convertedArgs)
      case in -> out =>
        val convertedIn = convertType(in, oldSig,newSig)
        val convertedOut = convertType(out, oldSig, newSig)
        mkFunType(convertedIn, convertedOut)
      case _ => throw new IllegalArgumentException
    }
  }

  private final def partitionArgs(args: Seq[Either[Term, Type]]): (Seq[Type], Seq[Term]) = partitionArgs0(Seq(), args)
  @tailrec final def partitionArgs0(acc: Seq[Type], args: Seq[Either[Term, Type]]): (Seq[Type], Seq[Term]) = {
    if (args.isEmpty) (acc, Seq.empty)
    else {
      val hd = args.head
      if (hd.isLeft) (acc, args.map(_.left.get))
      else partitionArgs0(acc :+ hd.right.get, args.tail)
    }
  }

  private final def monoInstanceName(id: Signature#Key, tyArgs: Seq[Type])(sig: Signature): String = {
    val sb: StringBuffer = new StringBuffer
    sb.append(safeName(sig(id).name))
    sb.append("_")
    val tyArgsIt = tyArgs.iterator
    while (tyArgsIt.hasNext) {
      val tyArg = tyArgsIt.next()
      sb.append(canonicalTyName(tyArg)(sig))
      if (tyArgsIt.hasNext) sb.append("_")
    }
    sb.toString
  }
  private final def canonicalTyName(ty: Type)(sig: Signature): String = {
    import Type._
    ty match {
      case BaseType(id) => sig(id).name.replaceAll("\\$", "D")
      case ComposedType(id, args) => s"${sig(id).name}_${args.map(canonicalTyName(_)(sig)).mkString("_")}"
      case _ => throw new IllegalArgumentException // bound, poly cannot happen, -> types should at this level be encoded to fun
    }
  }
}
