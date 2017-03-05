package leo.modules.output

import leo.datastructures.{Clause, Role, ClauseProxy, Signature}

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
    if (freeVarsExist) {
      sb.append("! [")
      sb.append(tyFvs.reverse.map(i => s"T${intToName(i-1)}:$$tType").mkString(","))
      if (fvs.nonEmpty) sb.append(",")
      sb.append(fvs.reverse.map{ case (scope,ty) => s"${intToName(scope-1)}:${typeToTFF(ty)(sig)}" }.mkString(","))
      sb.append("] : (")
    }

    val litIt = lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      sb.append(litToTFF(fvs, tyFvs, lit)(sig))
      if (litIt.hasNext) sb.append(" | ")
    }

    if (freeVarsExist) {
      sb.append(")")
    }
    sb.toString
  }

  private final def litToTFF(fvs: Seq[(Int, Type)], tyFvs: Seq[Int], lit: Literal)(sig: Signature): String = {
    if (!lit.equational) {
      if (!lit.polarity) s"~${formulaToTFF(fvs, tyFvs, lit.left)(sig)}"
      else formulaToTFF(fvs, tyFvs, lit.left)(sig)
    } else {
      if (lit.polarity)
        s"${termToTFF(fvs, tyFvs, lit.left)(sig)} = ${termToTFF(fvs, tyFvs, lit.right)(sig)}"
      else
        s"${termToTFF(fvs, tyFvs, lit.left)(sig)} != ${termToTFF(fvs, tyFvs, lit.right)(sig)}"
    }
  }

  private final def formulaToTFF(fvs: Seq[(Int, Type)], tyFvs: Seq[Int], t: Term)(sig: Signature): String = {
    import leo.datastructures.Term.{TermApp, Symbol, :::>}
    import leo.modules.HOLSignature.{Forall, Exists}
    import leo.modules.Utility.myAssert

    myAssert({
      val oType = Type.mkType(sig("$o").key)
      t.ty == oType
    }, "Translating non-boolean term to a TFF formula.")

    val interpretedSymbols = sig.fixedSymbols // Also contains fixed type ids, but doesnt matter here

    t match {
      case Forall(_ :::> body) =>
        val bodyRes = formulaToTFF(fvs, tyFvs, body)(sig)
        s"!" // TODO
        throw new IllegalArgumentException
      case Exists(_ :::> body) =>
        val bodyRes = formulaToTFF(fvs, tyFvs, body)(sig)
        s"?" // TODO
        throw new IllegalArgumentException
      case TermApp(hd, args) =>
        hd match {
          case Symbol(id) => if (interpretedSymbols.contains(id)) {
            val meta = sig(id)
            assert(meta.hasType)
            val argCount = meta._ty.arity
            assert(argCount <= 2)
            assert(argCount == args.size)
            if (args.isEmpty) {
              meta.name
            } else if (args.size == 1) {
              s"${meta.name} (${formulaToTFF(fvs,tyFvs,args.head)(sig)})"
            } else {
              // two args
              val arg1 = args.head
              val arg2 = args.tail.head
              s"(${formulaToTFF(fvs, tyFvs,arg1)(sig)}) ${meta.name} (${formulaToTFF(fvs,tyFvs,arg2)(sig)})"
            }
          } else {
            // start term level, uninterpreted symbol
            val meta = sig(id)
            assert(meta.isUninterpreted)
            s"${meta.name}(${args.map(termToTFF(fvs, tyFvs, _)(sig)).mkString(",")})"
          }
          case _ => throw new IllegalArgumentException
        }
      case _ => throw new IllegalArgumentException
    }
  }

  private final def termToTFF(fvs: Seq[(Int, Type)], tyFvs: Seq[Int], t: Term)(sig: Signature): String = {
    import leo.modules.HOLSignature.o
    if (t.ty == o) throw new IllegalArgumentException

    import leo.datastructures.Term.{TermApp, Symbol, Bound}

    val interpretedSymbols = sig.fixedSymbols // Also contains fixed type ids, but doesnt matter here
    t match {
      case Bound(_, scope) => intToName(scope-1)
      case TermApp(hd, args) =>
        hd match {
          case Symbol(id) => if (interpretedSymbols.contains(id)) throw new IllegalArgumentException
          else {
            if (args.isEmpty) sig(id).name
            else s"${sig(id).name}(${args.map(termToTFF(fvs,tyFvs, _)(sig)).mkString(",")})"
          }
          case _ => throw new IllegalArgumentException
        }
      case _ => throw new IllegalArgumentException
    }
  }

  final private def typeToTFF(ty: Type)(sig: Signature): String = {
    import leo.datastructures.Type._

    ty match {
      case BoundType(scope) => "T"+intToName(scope-1)
      case BaseType(id) => sig(id).name
      case ComposedType(id, args) => s"${sig(id).name}(${args.map(typeToTFF(_)(sig)).mkString(",")})"
      case _ -> _ =>
        val paramTypes = ty.funParamTypesWithResultType
        s"((${paramTypes.init.map(typeToTFF(_)(sig)).mkString(" * ")}) > ${typeToTFF(paramTypes.last)(sig)})"
      case _ => throw new IllegalArgumentException
    }
  }

  ///////////////////////////////
  // Naming of variables
  ///////////////////////////////

  @inline final private val asciiA = 65
  @inline final private val asciiZ = 90
  @inline final private val range = asciiZ - asciiA // range 0,1,....

  /**
    * Convert index i (variable in de-bruijn format) to a variable name corresponding to ASCII transformation as follows:
    * 0 ---> "A",
    * 1 ---> "B",
    * 25 ---> "Z",
    * 26 ---> "ZA", ... etc.
    */
  final private def intToName(i: Int): String = i match {
    case n if n <= range => s"${intToChar(i)}"
    case n if n > range => s"Z${intToName(i-range-1)}"
  }
  final private def intToChar(i: Int): Char = i match {
    case n if n <= range => (n + asciiA).toChar
    case _ => throw new IllegalArgumentException
  }
}
