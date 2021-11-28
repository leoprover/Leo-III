package leo.modules.output

import leo.datastructures.{Clause, ClauseProxy, Kind, Role, Signature}

import scala.annotation.tailrec

/**
  * Created by lex on 03.09.16.
  */
object ToTFF {
  final def apply(cl: ClauseProxy)(implicit sig: Signature): String = apply(cl.cl, cl.role, cl.id.toString)(sig)

  final def apply(cl: Clause, role: Role, name: String)(implicit sig: Signature): String = {
    val res = apply0(cl.implicitlyBound, cl.typeVars, cl.lits)(sig)
    s"tff($name,${role.pretty},($res))."
  }

  import leo.datastructures.{Literal, Type, Term}
  private final def apply0(fvs: Seq[(Int, Type)], tyFvs: Seq[Int], lits: Seq[Literal])(sig: Signature): String = {
    val sb = new StringBuffer()
    val freeVarsExist = fvs.nonEmpty || tyFvs.nonEmpty
    val fvMap: Map[Int, String] = if (freeVarsExist) {
      sb.append("! [")
      sb.append(tyFvs.reverse.map(i => s"T${intToName(i-1)}:$$tType").mkString(","))
      if (tyFvs.nonEmpty && fvs.nonEmpty) sb.append(",")
      val (namedFVEnumeration, fvMap) = clauseVarsToTPTP(fvs, typeToTFF0(_)(sig))
      sb.append(namedFVEnumeration)
      sb.append("] : (")
      fvMap
    } else Map.empty

    val litIt = lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      sb.append(litToTFF(fvMap, tyFvs.size, lit)(sig))
      if (litIt.hasNext) sb.append(" | ")
    }

    if (freeVarsExist) {
      sb.append(")")
    }
    sb.toString
  }

  private final def litToTFF(fvMap: Map[Int, String], tyFvCount: Int, lit: Literal)(sig: Signature): String = {
    if (!lit.equational) {
      if (!lit.polarity) s"~(${formulaToTFF(fvMap, tyFvCount, lit.left)(sig)})"
      else formulaToTFF(fvMap, tyFvCount, lit.left)(sig)
    } else {
      if (lit.polarity)
        s"${termToTFF(fvMap, tyFvCount, lit.left)(sig)} = ${termToTFF(fvMap, tyFvCount, lit.right)(sig)}"
      else
        s"${termToTFF(fvMap, tyFvCount, lit.left)(sig)} != ${termToTFF(fvMap, tyFvCount, lit.right)(sig)}"
    }
  }


  private final def formulaToTFF(fvMap: Map[Int, String], tyFvCount: Int, t: Term)(sig: Signature): String = {
    import leo.datastructures.Term.{Symbol,∙}
    import leo.modules.myAssert

    myAssert({
      val oType = Type.mkType(sig("$o").key)
      t.ty == oType
    }, "Translating non-boolean term to a TFF formula.")

    val interpretedSymbols = sig.fixedSymbols // Also contains fixed type ids, but doesnt matter here

    val forall = Term.mkAtom(sig("!").key)(sig)
    val exists = Term.mkAtom(sig("?").key)(sig)
    val tyforall = Term.mkAtom(sig("!>").key)(sig)
    val equality = Term.mkAtom(sig("=").key)(sig)
    val neg_equality = Term.mkAtom(sig("!=").key)(sig)

    t match {
      case `forall` ∙ _ =>
        val (bVarTys, body) = collectForall(t, forall)
        val newBVars = makeBVarList(bVarTys, fvMap.size)
        s"! [${newBVars.map{case (name,ty) => s"$name:${typeToTFF0(ty)(sig)}"}.mkString(",")}]: (${formulaToTFF(fusebVarListwithMap(newBVars, fvMap), tyFvCount, body)(sig)})"
      case `exists` ∙ _ =>
        val (bVarTys, body0) = collectExists(t, exists)
        val newBVars = makeBVarList(bVarTys, fvMap.size)
        s"? [${newBVars.map{case (name,ty) => s"$name:${typeToTFF0(ty)(sig)}"}.mkString(",")}]: (${formulaToTFF(fusebVarListwithMap(newBVars, fvMap), tyFvCount, body0)(sig)})"
      case `tyforall` ∙ _ =>
        val (absCount, body) = collectTyForall(t, tyforall)
        val varlist = (1 to absCount).map(i => intToName(i-1+tyFvCount))
        s"! [${varlist.map{name => s"T$name:$$tType"}.mkString(",")}]: (${formulaToTFF(fvMap, tyFvCount+absCount, body)(sig)})"
      case `equality` ∙ Seq(Right(_), Left(left), Left(right)) =>
        s"${termToTFF(fvMap, tyFvCount, left)(sig)} = ${termToTFF(fvMap, tyFvCount, right)(sig)}"
      case `neg_equality` ∙ Seq(Right(_), Left(left), Left(right)) =>
        s"${termToTFF(fvMap, tyFvCount, left)(sig)} != ${termToTFF(fvMap, tyFvCount, right)(sig)}"
      case Symbol(id) ∙ args if leo.modules.input.InputProcessing.adHocPolymorphicArithmeticConstants.contains(id) =>
        val translatedF = tptpEscapeExpression(sig(id).name)
        s"$translatedF(${args.tail.map(termOrTypeToTFF(fvMap, tyFvCount, _)(sig)).mkString(",")})"
      case Symbol(id) ∙ args =>
        if (interpretedSymbols.contains(id)) {
          // Formula level (binary/unary)
          val meta = sig(id)
          assert(meta.hasType)
          val argCount = meta._ty.arity
          assert(argCount <= 2, s"arg count of ${meta.name} > 2"); assert(argCount == args.size, s"argCount of ${meta.name} does not match argsize")
          if (args.isEmpty) {
            meta.name
          } else if (args.size == 1) {
            assert(args.head.isLeft)
            s"${meta.name} (${formulaToTFF(fvMap,tyFvCount,args.head.left.get)(sig)})"
          } else {
            // two args
            val arg1 = args.head
            val arg2 = args.tail.head
            assert(arg1.isLeft); assert(arg2.isLeft)
            s"((${formulaToTFF(fvMap, tyFvCount,arg1.left.get)(sig)}) ${meta.name} (${formulaToTFF(fvMap,tyFvCount,arg2.left.get)(sig)}))"
          }
        } else {
          // Term level/predicate level
          val meta = sig(id)
          assert(meta.isUninterpreted)
          val name = tptpEscapeExpression(meta.name)
          if (args.isEmpty) name
          else s"$name(${args.map(termOrTypeToTFF(fvMap, tyFvCount, _)(sig)).mkString(",")})"
        }
      case _ => throw new IllegalArgumentException("formulaToTFF(.)")
    }
  }

  private final def termOrTypeToTFF(fvMap: Map[Int, String],  tyFvCount: Int, termOrType: Either[Term, Type])(sig: Signature): String = {
    if (termOrType.isLeft) termToTFF(fvMap, tyFvCount, termOrType.left.get)(sig)
    else typeToTFF0(termOrType.right.get)(sig)
  }

  private final def termToTFF(fvMap: Map[Int, String], tyFvCount: Int, t: Term)(sig: Signature): String = {
    import leo.datastructures.Term.{∙, Symbol, Bound, Integer, Rational, Real}
    val interpretedSymbols = sig.fixedSymbols
    t match {
      case Bound(_, scope) => fvMap(scope)
      case Integer(n) => n.toString
      case Rational(n,d) => s"$n/$d"
      case Real(w,d,e) => if (e == 0) s"$w.$d" else s"$w.${d}E$e"
      case Symbol(id) ∙ args if leo.modules.input.InputProcessing.adHocPolymorphicArithmeticConstants.contains(id) =>
        val translatedF = tptpEscapeExpression(sig(id).name)
        s"$translatedF(${args.tail.map(termOrTypeToTFF(fvMap, tyFvCount, _)(sig)).mkString(",")})"
      case Symbol(id) ∙ args =>
          if (interpretedSymbols.contains(id)) throw new IllegalArgumentException("termToTFF(.) #1")
          else {
            val name = tptpEscapeExpression(sig(id).name)
            if (args.isEmpty) name
            else s"$name(${args.map(termOrTypeToTFF(fvMap, tyFvCount, _)(sig)).mkString(",")})"
          }
      case _ => throw new IllegalArgumentException("termToTFF(.) #2")
    }
  }

  final private def kindToTFF(k: Kind): String = {
    import leo.datastructures.Kind.{*,->,superKind}
    k match {
      case `*` => "$tType"
      case `superKind` => "#"
      case _ -> _ =>  funKindFOStyle(k)
    }
  }
  final private def funKindFOStyle(k: Kind): String = funKindFOStyle0(k, Seq.empty)
  final private def funKindFOStyle0(k: Kind, acc: Seq[Kind]): String = {
    import leo.datastructures.Kind.{*,->,superKind}
    k match {
      case k1 -> k2 =>  funKindFOStyle0(k2, acc :+ k1)
      case _ => s"(${acc.map(kindToTFF).mkString(" * ")}) > ${kindToTFF(k)}"
    }
  }

  final private def typeToTFF(ty: Type)(sig: Signature): String = {
    import leo.datastructures.Type.∀
    ty match {
      case ∀(_) => val (tyAbsCount, bodyTy) = collectForallTys(0, ty)
        s"!> [${(1 to tyAbsCount).map(i => s"T${intToName(i - 1)}: $$tType").mkString(",")}]: ${typeToTFF0(bodyTy)(sig)}"
      case _ => typeToTFF0(ty)(sig)
    }
  }

  final private def typeToTFF0(ty: Type)(sig: Signature): String = {
    import leo.datastructures.Type._

    ty match {
      case BoundType(scope) => "T"+intToName(scope-1)
      case BaseType(id) => tptpEscapeExpression(sig(id).name)
      case ComposedType(id, args) => s"${tptpEscapeExpression(sig(id).name)}(${args.map(typeToTFF0(_)(sig)).mkString(",")})"
      case _ -> _ =>
        val paramTypes = ty.funParamTypesWithResultType
        val inTypes = paramTypes.init
        val inTypesIt = inTypes.iterator
        val outType = paramTypes.last
        val sb: StringBuffer = new StringBuffer()
        if (inTypes.size > 1) sb.append("(")
        while (inTypesIt.hasNext) {
          val inTy = inTypesIt.next()
          if (inTy.isFunType) sb.append(s"(${typeToTFF0(inTy)(sig)})")
          else sb.append(typeToTFF0(inTy)(sig))
          if (inTypesIt.hasNext) sb.append("*")
        }
        if (inTypes.size > 1) sb.append(")")
        sb.append(">")
        sb.append(typeToTFF0(outType)(sig))
        sb.toString
      case ProductType(tys) => tys.map(typeToTFF0(_)(sig)).mkString("[", ",", "]")
      case ∀(_) => throw new IllegalArgumentException("Illegal nested polymorphic type detected.")
    }
  }

  final def apply(sig: Signature): String = {
    val sb: StringBuilder = new StringBuilder
    for (id <- sig.typeConstructors intersect sig.allUserConstants) {
      val name = tptpEscapeName(sig(id).name)
      val name_type = tptpEscapeName(sig(id).name+"_type")
      sb.append("tff(")
      sb.append(name_type)
      sb.append(",type,(")
      sb.append(name)
      sb.append(":")
      sb.append(kindToTFF(sig(id)._kind))
      sb.append(")).\n")
    }
    for (id <- sig.uninterpretedSymbols) {
      val name = tptpEscapeName(sig(id).name)
      val name_type = tptpEscapeName(sig(id).name+"_type")
      sb.append("tff(")
      sb.append(name_type)
      sb.append(",type,(")
      sb.append(name)
      sb.append(":")
      sb.append(typeToTFF(sig(id)._ty)(sig))
      sb.append(")).\n")
    }
    sb.toString()
  }

  ///////////////////////////////
  // Naming of variables
  ///////////////////////////////
  /** Gather consecutive all-quantifications (nameless).
    * Returns a tuple `((ty_i)_i, b)` where b is equal to `t` if all prefix-
    * universal quantification are dropped. The ordering of the `ty_i` is in order
    * (from left to right) as the quantifiers occured.
    * {{{collectForall(!_a.!_b.!_c.t) = ([a,b,c],t)}}}. */
  final private def collectForall(t: Term, forallConst: Term): (Seq[Type], Term) = {
    collectQuant(Seq.empty, t, forallConst)
  }

  /** Gather consecutive existential quantifications (nameless).
    * Returns a tuple `((ty_i)_i, b)` where b is equal to `t` if all prefix-
    * existential quantification are dropped. The ordering of the `ty_i` is in order
    * (from left to right) as the quantifiers occured.
    * {{{collectExists(?_a.?_b.?_c.t) = ([a,b,c],t)}}}. */
  final private def collectExists(t: Term, existsConst: Term): (Seq[Type], Term) = {
    collectQuant(Seq.empty, t, existsConst)
  }

  @tailrec
  @inline final private def collectQuant(vars: Seq[Type], t: Term, quant: Term): (Seq[Type], Term) = {
    import leo.datastructures.Term.{∙, :::>}
    t match {
      case `quant` ∙ Seq(Right(_), Left(absType :::> body)) => collectQuant(vars :+ absType, body, quant)
      case `quant` ∙ Seq(Right(absType), Left(body)) => collectQuant(vars :+ absType, body, quant)
      case _ => (vars, t)
    }
  }

  /** Gather consecutive universal type quantifications.
    * Returns a tuple `(i, b)` where b is equal to `t` if all prefix-
    * universal type quantification are dropped and `i` is the number of such quantifications.
    * {{{collectTyForall(!>.!>.t) = (2,t)}}}. */
  final private def collectTyForall(t: Term, tyExistsConst: Term): (Int, Term) = {
    collectTyForall0(0, t, tyExistsConst)
  }

  @tailrec
  @inline final private def collectTyForall0(count: Int, t: Term, tyForallConst: Term): (Int, Term) = {
    import leo.datastructures.Term.{TermApp, TypeLambda}
    t match {
      case TermApp(`tyForallConst`, Seq(TypeLambda(body))) => collectTyForall0(1+count, body, tyForallConst)
      case _ => (count, t)
    }
  }

  // Type quantification collection
  @tailrec
  private final def collectForallTys(count: Int, ty: Type): (Int, Type) = {
    import leo.datastructures.Type.∀
    ty match {
      case ∀(t) => collectForallTys(count+1, t)
      case _ => (count, ty)
    }
  }
}
