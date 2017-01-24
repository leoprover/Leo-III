package leo.modules.indexing

import leo.datastructures.{Signature, ClauseProxy}

class FOIndex {
  import scala.collection._

  private final val clauses: mutable.Set[ClauseProxy] = mutable.Set()

  final def insert(cl: ClauseProxy)(implicit sig: Signature): Boolean = if (FOIndex.typedFirstOrder(cl)(sig)) {clauses.+=(cl); true} else false
  final def remove(cl: ClauseProxy): Boolean = clauses.remove(cl)
  final def contains(cl: ClauseProxy): Boolean = clauses.contains(cl)

  final def isEmpty: Boolean = clauses.isEmpty
  final def iterator: Iterator[ClauseProxy] = clauses.iterator
}


object FOIndex {
  final def apply(): FOIndex = new FOIndex

  final def typedFirstOrder(cl: ClauseProxy)(implicit sig: Signature): Boolean = typedFirstOrder0(cl.cl.implicitlyBound, cl.cl.typeVars, cl.cl.lits)(sig)

  import leo.datastructures.{Literal, Type, Term}
  private final def typedFirstOrder0(fvs: Seq[(Int, Type)], tyFvs: Set[Int], lits: Seq[Literal])(sig: Signature): Boolean = {
    if (tyFvs.nonEmpty) return false
    val litIt = lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      if (!typedFirstOrderLit(fvs, lit)(sig)) return false
    }
    true
  }

  private final def typedFirstOrderLit(fvs: Seq[(Int, Type)], lit: Literal)(sig: Signature): Boolean = {
    if (!lit.equational) {
      typedFirstOrderFormula(fvs, lit.left)(sig)
    } else {
      typedFirstOrderTerm(fvs, lit.left)(sig) && typedFirstOrderTerm(fvs, lit.right)(sig)
    }
  }

  private final def typedFirstOrderFormula(fvs: Seq[(Int, Type)], t: Term)(sig: Signature): Boolean = {
    import leo.datastructures.Term.{TermApp, Symbol, :::>}
    import leo.modules.HOLSignature.{o, Forall, Exists, ===, !===}

    if (t.ty != o) return false

    val interpretedSymbols = sig.fixedSymbols // Also contains fixed type ids, but doesnt matter here

    t match { // FIXME: Check type of abstraction, or not?
      case Forall(_ :::> body) => typedFirstOrderFormula(fvs, body)(sig)
      case Exists(_ :::> body) => typedFirstOrderFormula(fvs, body)(sig)
      case (l === r) => typedFirstOrderTerm(fvs, l)(sig) && typedFirstOrderTerm(fvs, r)(sig)
      case (l !=== r) => typedFirstOrderTerm(fvs, l)(sig) && typedFirstOrderTerm(fvs, r)(sig)
      case TermApp(hd, args) => hd match {
          case Symbol(id) => if (interpretedSymbols.contains(id)) {
            args.forall(typedFirstOrderFormula(fvs, _)(sig))
          } else {
            // start term level, uninterpreted symbol
            assert(sig(id).isUninterpreted)
            args.forall(typedFirstOrderTerm(fvs, _)(sig))
          }
          case _ => false
        }
      case _ => false
    }
  }

  private final def typedFirstOrderTerm(fvs: Seq[(Int, Type)], t: Term)(sig: Signature): Boolean = {
    import leo.modules.HOLSignature.o
    if (t.ty == o) return false

    import leo.datastructures.Term.{TermApp, Symbol, Bound}

    val interpretedSymbols = sig.fixedSymbols // Also contains fixed type ids, but doesnt matter here
    t match {
      case Bound(_, _) => true
      case TermApp(hd, args) =>
        hd match {
          case Symbol(id) => if (interpretedSymbols.contains(id)) false
          else args.forall(typedFirstOrderTerm(fvs, _)(sig))
          case _ => false
        }
      case _ => false
    }
  }
}
