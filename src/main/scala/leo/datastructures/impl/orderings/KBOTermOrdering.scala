package leo.datastructures.impl.orderings

import leo.datastructures.impl.orderings.KBOTermOrdering.Ordinal.normalize
import leo.datastructures.{CMP_EQ, CMP_GT, CMP_LT, CMP_NC, CMP_Result, Multiset, Orderings, Precedence, Signature, Term, TermOrdering, Type, termArgs}

trait SymbolWeighting {
  def apply(symbol: Signature.Key)(implicit sig: Signature): Int
}

final class KBOTermOrdering(weightFunction: SymbolWeighting, prec: Precedence) extends TermOrdering {
  import KBOTermOrdering.Ordinal

  final val name: String = "HKBO"

  /**
    * Let ⊳ be a precedence (partial ordering) on the constants from the signature Σ,
    * and w: Σ -> N a weight function on the constant symbols.
    * Let s and t be two terms of some type, and let > denote the (partial) ordering
    * induced by KBOTermOrderning.
    *
    * Then, s > t holds iff
    * - ....
    *
    *
    * > ≥
    *
    * ⊵
    *
    * @param s
    * @param t
    * @param sig
    * @return
    */

  final def compare(s: Term, t: Term)(implicit sig: Signature): CMP_Result = {
    import leo.datastructures.Term._
    val result = {
      if (s.isVariable || t.isVariable) {
        if (t.isVariable) {
          if (s == t) CMP_EQ
          else {
            if (s.freeVars.contains(t)) CMP_GT
            else CMP_NC
          }
        } else {
          if (t.freeVars.contains(s)) CMP_LT
          else CMP_NC
        }
      } else {
        val sWeight = termWeight(s)
        val tWeight = termWeight(t)

        if (sWeight > tWeight) {
          compareVars(s.vars, t.vars) match {
            case CMP_GT | CMP_EQ => CMP_GT
            case _ => CMP_NC
          }
        } else if (sWeight < tWeight) {
          compareVars(s.vars, t.vars) match {
            case CMP_LT | CMP_EQ => CMP_LT
            case _ => CMP_NC
          }
        } else {
          (s,t) match {
            case (TermApp(Symbol(f1), args1), TermApp(Symbol(f2), args2)) =>
              prec.compare(f1,f2) match {
                case CMP_NC => CMP_NC
                case CMP_GT =>
                  compareVars(s.vars, t.vars) match {
                    case CMP_GT | CMP_EQ => CMP_GT
                    case _ => CMP_NC
                  }
                case CMP_LT =>
                  compareVars(s.vars, t.vars) match {
                    case CMP_LT | CMP_EQ => CMP_LT
                    case _ => CMP_NC
                  }
                case CMP_EQ =>
                  lexCompare(args1, args2) match {
                    case CMP_GT => compareVars(s.vars, t.vars) match {
                      case CMP_GT | CMP_EQ => CMP_GT
                      case _ => CMP_NC
                    }
                    case CMP_LT => compareVars(s.vars, t.vars) match {
                      case CMP_LT | CMP_EQ => CMP_LT
                      case _ => CMP_NC
                    }
                    case x => x
                  }
              }
            case (TermApp(Bound(_,idx1), args1), TermApp(Bound(_,idx2), args2)) =>
              if (idx1 == idx2) {
                // same as f(args) > g(args) with f ~ g
                lexCompare(args1, args2) match {
                  case CMP_GT => compareVars(s.vars, t.vars) match {
                    case CMP_GT | CMP_EQ => CMP_GT
                    case _ => CMP_NC
                  }
                  case CMP_LT => compareVars(s.vars, t.vars) match {
                    case CMP_LT | CMP_EQ => CMP_LT
                    case _ => CMP_NC
                  }
                  case x => x
                }
              } else CMP_NC
            case _ =>
              val hds = s.headSymbol
              val hdt = t.headSymbol
              (hds, hdt) match {
                case (Symbol(f1), Symbol(f2)) =>
                  prec.compare(f1,f2) match {
                    case CMP_GT =>
                      compareVars(s.vars, t.vars) match {
                        case CMP_GT | CMP_EQ => CMP_GT
                        case _ => CMP_NC
                      }
                    case CMP_LT =>
                      compareVars(s.vars, t.vars) match {
                        case CMP_LT | CMP_EQ => CMP_LT
                        case _ => CMP_NC
                      }
                    case CMP_NC => CMP_NC
                    case CMP_EQ =>
                      compareVars(s.vars, t.vars) match {
                        case CMP_EQ => CMP_EQ
                        case _ => CMP_NC
                      }
                  }
                case _ =>
                  if (s == t) CMP_EQ else CMP_NC
              }
          }
        }
      }
    }
    println(s"compare(\n" +
      s"\t${s.pretty(sig)},\n\t${t.pretty(sig)}\n" +
    "):", s"KBO: ${Orderings.pretty(result)}", s"CPO: ${Orderings.pretty(TO_CPO_Naive.compare(s,t))}")
    result
  }

  def lexCompare(left: Seq[Term], right: Seq[Term])(implicit sig: Signature): CMP_Result = {
    var todo = left.zip(right)
    var result: Option[CMP_Result] = None
    while (todo.nonEmpty && result.isEmpty) {
      val current = todo.head
      todo = todo.tail
      compare(current._1, current._2) match {
        case CMP_GT =>
          result = Some(CMP_GT)
        case CMP_LT =>
          result = Some(CMP_LT)
        case CMP_NC =>
          result = Some(CMP_NC)
        case _ => ()
      }
    }
    if (result.isEmpty) CMP_EQ
    else result.get
  }

  def compareVars(sVars: Multiset[Int], tVars: Multiset[Int]): CMP_Result = {
    val subsetST = sVars.subset(tVars)
    val subsetTS = tVars.subset(sVars)
    if (subsetST && subsetTS) CMP_EQ
    else if (subsetST) CMP_LT
    else if (subsetTS) CMP_GT
    else CMP_NC
  }
//  final def compare(s: Term, t: Term)(implicit sig: Signature): CMP_Result = {
//    val sVars = s.vars
//    val tVars = t.vars
//    if (sVars.subset(tVars)) {
//      val res = compare0(t, s, 0, 0)(sig)
//      Orderings.invCMPRes(res)
//    } else if (tVars.subset(sVars)) {
//      compare0(s, t, 0, 0)(sig)
//    } else CMP_NC
//  }
//  /** s is potentially larger than t, because vars(s) > vars(t) */
//  final def compare0(s: Term, t: Term, sDepth: Int, tDepth: Int)(implicit sig: Signature): CMP_Result = {
//    import leo.datastructures.Term._
//    s match {
//      case Bound(ty,idx) if idx > sDepth => t match {
//        case Bound(ty2, idx2) if idx == idx2 => CMP_EQ
//        case _ => CMP_NC
//      }
//      case TermApp(f, args) =>
//        t match {
//          case Bound(_, idx) if idx > tDepth =>
//            if(s.looseBounds.filter(_ > sDepth).contains(idx)) CMP_GT
//            else CMP_NC
//          case _ if s == t => CMP_EQ
//          case _ =>
//            val sWeight = termWeight(s)
//            val tWeight = termWeight(t)
//            println("sWeight", sWeight.toString)
//            println("tWeight", tWeight.toString)
//            if (sWeight > tWeight) CMP_GT
//            else if (sWeight == tWeight) {
//              val sHd = s.headSymbol
//              val tHd = t.headSymbol
//              (sHd, tHd) match {
//                case (Symbol(sId), Symbol(tId)) =>
//                  prec.compare(sId,tId) match {
//                    case CMP_GT => CMP_GT
//                    case CMP_EQ => ???
//                    case _ => CMP_NC
//                  }
//              }
//            } else CMP_NC
//        }
//      case _ :::> _ =>
//        val sWeight = termWeight(s)
//        val tWeight = termWeight(t)
//        if (sWeight > tWeight) CMP_GT
//        else if (sWeight == tWeight) {
//          if (s == t) CMP_EQ
//          else CMP_NC
//        } else CMP_NC
//    }
//  }

  final def termWeight(s: Term)(implicit sig: Signature): Ordinal = {
    termWeight2(s)(sig)
  }

  final def termWeight1(s: Term)(implicit sig: Signature): Ordinal = {
    import leo.datastructures.Term._
    s match {
      case Symbol(key) => ordinalWeight(sig(key)._ty, weightFunction(key)(sig))
      case Bound(ty, _) => ordinalWeight(ty, 1)
      case ty :::> body =>
        val wx = termWeight1(Term.local.mkBound(ty, 1))
        val shiftedwx = ((wx - (wx.lsp - 1)) << 1) + wx.lsp
        termWeight1(body) + shiftedwx
      case TermApp(f, args) =>
        var weight = termWeight1(f)
        for (arg <- args) {
          val wArg = termWeight1(arg)
          val shiftedwArg = ((wArg - (wArg.lsp - 1)) << 1)
          weight = weight - shiftedwArg + wArg.lsp
        }
        weight
    }
  }
  final def termWeight2(s: Term)(implicit sig: Signature): Ordinal = {
    import leo.datastructures.Term._
    s match {
      case Symbol(key) => ordinalWeight(sig(key)._ty, weightFunction(key)(sig))
      case Bound(ty, _) => ordinalWeight(ty, 1)
      case ty :::> body =>
        val wx = termWeight2(Term.local.mkBound(ty, 1))
        val shiftedwx = ((wx - (wx.lsp - 1)) << 1) + wx.lsp
        termWeight2(body) + shiftedwx
      case TermApp(f, args) =>
        var weight = termWeight2(f)
        for (arg <- args) {
          val wArg = termWeight2(arg)
//          val shiftedwArg = ((wArg - (wArg.lsp - 1)) << 1)
          weight = weight + wArg
        }
        weight
    }
  }

  final def ordinalWeight(ty: Type, simpleWeight: Int): Ordinal = {
    if (ty.isBaseType) Ordinal(simpleWeight)
    else if (ty.isFunType) {
      var result: Ordinal = Ordinal(simpleWeight)
      var todo = Seq((1, ty))
      while (todo.nonEmpty) {
        val (ord,p) = todo.head
        todo = todo.tail
        val ar = p.arity
        result = result + (Ordinal(ar) << ord)
        todo = Seq.concat(todo, p.funParamTypes.map(t => (ord+1,t)))
      }
      result
    } else if (ty.isPolyType) {
      ordinalWeight(ty.monomorphicBody, simpleWeight)
    } else {
      Ordinal(1)
    }
  }

}
object KBOTermOrdering {
  final class Ordinal(val elems: Seq[Int]) extends AnyVal {

    def +(that: Ordinal): Ordinal = {
      val this0 = if (elems.size < that.elems.size) {
        Seq.concat(Seq.fill(that.elems.size-elems.size)(0), elems)
      } else elems
      val that0 = if (elems.size > that.elems.size) {
        Seq.concat(Seq.fill(elems.size-that.elems.size)(0), that.elems)
      } else that.elems
      assert(this0.size == that0.size)
      val res = this0.zip(that0).map(x => x._1 + x._2)
      Ordinal(res)
    }
    def +(that: Int): Ordinal = this + Ordinal(that)
    def -(that: Ordinal): Ordinal = {
      val this0 = if (elems.size < that.elems.size) {
        Seq.concat(Seq.fill(that.elems.size-elems.size)(0), elems)
      } else elems
      val that0 = if (elems.size > that.elems.size) {
        Seq.concat(Seq.fill(elems.size-that.elems.size)(0), that.elems)
      } else that.elems
      assert(this0.size == that0.size)
      val res = this0.zip(that0).map(x => x._1 - x._2)
      Ordinal(normalize(res))
    }
    def -(that: Int): Ordinal = this - Ordinal(that)
    def <<(n: Int): Ordinal = {
      Ordinal(Seq.concat(elems, Seq.fill(n)(0)))
    }
    def <(that: Ordinal): Boolean = {
      val intermediate = this - that
      intermediate.elems.takeWhile(_ < 0).nonEmpty
    }
    def >(that: Ordinal): Boolean = {
      that < this
    }
    def lsp: Int = {
      elems.last
    }
    def msp: Int = {
      elems.head
    }
    override def toString: String = elems.toString
  }
  object Ordinal {
    def apply(elems: Seq[Int]): Ordinal = {
      assert(elems.nonEmpty)
      new Ordinal(normalize(elems))
    }
    def apply(n: Int): Ordinal = {
      new Ordinal(Seq(n))
    }
    def apply(n: Int, ns: Int*): Ordinal = {
      apply(n +: ns)
    }
    private[Ordinal] def normalize(elems: Seq[Int]): Seq[Int] = {
      val intermediate = elems.dropWhile(_ == 0)
      if (intermediate.isEmpty) Seq(0) else intermediate
    }
  }


}