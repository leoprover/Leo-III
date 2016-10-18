package leo.modules.output

import leo.datastructures.{ClauseProxy, Signature}

/**
  * Created by lex on 03.09.16.
  */
object ToTFF {
  final def apply(cl: ClauseProxy)(implicit sig: Signature): String = {
    val res = apply0(cl.cl.implicitlyBound, cl.cl.typeVars, cl.cl.lits)(sig)
    s"tff(${cl.id},${cl.role.pretty},($res))."
  }

  import leo.datastructures.{Literal, Type, Term}
  private final def apply0(fvs: Seq[(Int, Type)], tyFvs: Set[Int], lits: Seq[Literal])(sig: Signature): String = {
    if (tyFvs.nonEmpty) throw new IllegalArgumentException

    val sb = new StringBuffer()
    if (fvs.nonEmpty) {
      sb.append("! [")
      sb.append(fvs.reverse.map{ case (scope,ty) => s"${intToName(scope-1)}:${typeToTFF(ty)(sig)}" }.mkString(","))
      sb.append("] : (")
    }

    val litIt = lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      sb.append("(")
      sb.append(litToTFF(fvs, lit)(sig))
      sb.append(")")
      if (litIt.hasNext) sb.append(" | ")
    }

    if (fvs.nonEmpty) {
      sb.append(")")
    }
    sb.toString
  }

  private final def litToTFF(fvs: Seq[(Int, Type)], lit: Literal)(sig: Signature): String = {
    if (!lit.equational) {
      if (!lit.polarity) {
        s"~ (${formulaToTFF(fvs, lit.left)(sig)})"
      } else formulaToTFF(fvs, lit.left)(sig)
    } else {
      if (lit.polarity)
        s"${termToTFF(fvs, lit.left)(sig)} = ${termToTFF(fvs, lit.right)(sig)}"
      else
        s"${termToTFF(fvs, lit.left)(sig)} != ${termToTFF(fvs, lit.right)(sig)}"
    }
  }

  private final def formulaToTFF(fvs: Seq[(Int, Type)], t: Term)(sig: Signature): String = {
    import leo.datastructures.Term.{TermApp, Symbol, :::>}
    import leo.modules.HOLSignature.{o, Forall, Exists}

    if (t.ty != o) throw new IllegalArgumentException

    val interpretedSymbols = sig.fixedSymbols // Also contains fixed type ids, but doesnt matter here

    t match {
      case Forall(_ :::> body) =>
        val bodyRes = formulaToTFF(fvs, body)(sig)
        s"!" // TODO
        throw new IllegalArgumentException
      case Exists(_ :::> body) =>
        val bodyRes = formulaToTFF(fvs, body)(sig)
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
              s"${meta.name} (${formulaToTFF(fvs,args.head)(sig)})"
            } else {
              // two args
              val arg1 = args.head
              var arg2 = args.tail.head
              s"(${formulaToTFF(fvs, arg1)(sig)}) ${meta.name} (${formulaToTFF(fvs,arg2)(sig)})"
            }
          } else {
            // start term level, uninterpreted symbol
            val meta = sig(id)
            assert(meta.isUninterpreted)
            s"${meta.name}(${args.map(termToTFF(fvs, _)(sig)).mkString(",")})"
          }
          case _ => throw new IllegalArgumentException
        }
      case _ => throw new IllegalArgumentException
    }
  }

  private final def termToTFF(fvs: Seq[(Int, Type)], t: Term)(sig: Signature): String = {
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
            else s"${sig(id).name}(${args.map(termToTFF(fvs, _)(sig)).mkString(",")})"
          }
          case _ => throw new IllegalArgumentException
        }
      case _ => throw new IllegalArgumentException
    }
  }

  final private def typeToTFF(ty: Type)(sig: Signature): String = {
    import leo.datastructures.Type._

    ty match {
      case BoundType(scope) => "T"+intToName(scope)
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
