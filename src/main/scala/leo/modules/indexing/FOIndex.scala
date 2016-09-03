package leo.modules.indexing

import leo.datastructures.ClauseProxy


class FOIndex {
  import scala.collection._

  private final val clauses: mutable.Set[ClauseProxy] = mutable.Set()

  final def insert(cl: ClauseProxy): Boolean = if (FOIndex.typedFirstOrder(cl)) {clauses.+=(cl); true} else false
  final def remove(cl: ClauseProxy): Boolean = clauses.remove(cl)
  final def contains(cl: ClauseProxy): Boolean = clauses.contains(cl)

  final def isEmpty: Boolean = clauses.isEmpty
  final def iterator: Iterator[ClauseProxy] = clauses.iterator
}


object FOIndex {
  final def apply(): FOIndex = new FOIndex

  final def typedFirstOrder(cl: ClauseProxy): Boolean = typedFirstOrder0(cl.cl.implicitlyBound, cl.cl.typeVars, cl.cl.lits)

  import leo.datastructures.{Literal, Type, Term}
  private final def typedFirstOrder0(fvs: Seq[(Int, Type)], tyFvs: Set[Int], lits: Seq[Literal]): Boolean = {
    if (tyFvs.nonEmpty) return false
    val litIt = lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      if (!typedFirstOrderLit(fvs, lit)) return false
    }
    true
  }

  private final def typedFirstOrderLit(fvs: Seq[(Int, Type)], lit: Literal): Boolean = {
    if (!lit.equational) {
      typedFirstOrderFormula(fvs, lit.left)
    } else {
      typedFirstOrderTerm(fvs, lit.left) && typedFirstOrderTerm(fvs, lit.right)
    }
  }

  private final def typedFirstOrderFormula(fvs: Seq[(Int, Type)], t: Term): Boolean = {
    import leo.datastructures.Term.{TermApp, Symbol, :::>}
    import leo.datastructures.{Forall, Exists, ===, !===}
    import leo.datastructures.impl.Signature

    if (t.ty != Signature.get.o) return false

    val interpretedSymbols = Signature.get.fixedSymbols // Also contains fixed type ids, but doesnt matter here

    t match {
      case Forall(_ :::> body) => typedFirstOrderFormula(fvs, body)
      case Exists(_ :::> body) => typedFirstOrderFormula(fvs, body)
      case (l === r) => typedFirstOrderTerm(fvs, l) && typedFirstOrderTerm(fvs, r)
      case (l !=== r) => typedFirstOrderTerm(fvs, l) && typedFirstOrderTerm(fvs, r)
      case TermApp(hd, args) => {
        hd match {
          case Symbol(id) => if (interpretedSymbols.contains(id)) {
            args.forall(typedFirstOrderFormula(fvs, _))
          } else {
            // start term level, uninterpreted symbol
            assert(Signature.get(id).isUninterpreted)
            args.forall(typedFirstOrderTerm(fvs, _))
          }
          case _ => false
        }
      }
      case _ => false
    }
  }

  private final def typedFirstOrderTerm(fvs: Seq[(Int, Type)], t: Term): Boolean = {
    if (t.ty == leo.datastructures.impl.Signature.get.o) return false

    import leo.datastructures.Term.{TermApp, Symbol, Bound}
    import leo.datastructures.{===, !===}
    import leo.datastructures.impl.Signature

    val interpretedSymbols = Signature.get.fixedSymbols // Also contains fixed type ids, but doesnt matter here
    t match {
      case Bound(_, _) => true
      case TermApp(hd, args) => {
        hd match {
          case Symbol(id) => if (interpretedSymbols.contains(id)) false
          else args.forall(typedFirstOrderTerm(fvs, _))
          case _ => false
        }
      }
      case _ => false
    }
  }
}
