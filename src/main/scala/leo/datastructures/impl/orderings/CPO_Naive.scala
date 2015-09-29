package leo.datastructures.impl.orderings

import leo.datastructures.{Term, Type}
import leo.datastructures.impl.Signature
import leo.modules.output.logger.Out

import scala.annotation.tailrec

/**
 * Computability path Ordering (Core CPO)
 * by Blanqui, Jouannaud, Rubio
 *
 * @author Alexander Steen <a.steen@fu-berlin.de>
 * @since 29.09.2015
 */
object CPO_Naive {
  type CMP_Result = Byte
  final val CMP_EQ: CMP_Result = 0.toByte
  final val CMP_LT: CMP_Result = 1.toByte
  final val CMP_GT: CMP_Result = 2.toByte
  final val CMP_NC: CMP_Result = 3.toByte

  /////////////////////////////////////////////////////////////////
  /// Exported functions
  /////////////////////////////////////////////////////////////////

  ////////////////////////////////////
  // Comparisons of types
  ////////////////////////////////////

  // Core function for comparisons
  def gt(a: Type, b: Type): Boolean = gt0(a,b)

  // Defined by other functions
  def ge(a: Type, b: Type): Boolean = ge0(a,b)
  def lt(a: Type, b: Type): Boolean = gt(b,a)
  def le(a: Type, b: Type): Boolean = ge(b,a)

  def compare(a: Type, b: Type): CMP_Result = {
    if (a == b) CMP_EQ
    else if (gt(a,b)) CMP_GT
    else if (lt(a,b)) CMP_LT
    else CMP_NC
  }
  def canCompare(a: Type, b: Type): Boolean = compare(a,b) != CMP_NC

  ////////////////////////////////////
  // Comparisons of terms
  ////////////////////////////////////

  // Core function for comparisons
  def gt(s: Term, t: Term): Boolean ={
    ge(s.ty, t.ty) && gt0(s,t, Set())
  }
  def gt(s: Term, t: Term, bound: Set[Term]): Boolean = {
    ge(s.ty, t.ty) && gt0(s,t, bound)
  }

  // Defined by other functions
  def ge(s: Term, t: Term): Boolean = s == t || gt(s,t)
  def lt(s: Term, t: Term): Boolean = gt(t,s)
  def le(s: Term, t: Term): Boolean = s == t || lt(s,t)

  def compare(s: Term, t: Term): CMP_Result = {
    if (s == t) CMP_EQ
    else if (gt(s,t)) CMP_GT
    else if (lt(s,t)) CMP_LT
    else CMP_NC
  }
  def canCompare(s: Term, t: Term): Boolean = compare(s,t) != CMP_NC

  ////////////////////////////////////
  // Common comparison-related operations
  ////////////////////////////////////


  /////////////////////////////////////////////////////////////////
  /// Internal functions
  /////////////////////////////////////////////////////////////////

  // ###############################################################################
  ////////////////////////////////////
  // Comparisons of types
  ////////////////////////////////////
  private def gt0(a: Type, b: Type): Boolean = {
    import leo.datastructures.Type.{BaseType,->,∀}

    if (a == b) return false
    if (a.isBoundTypeVar) return false
    /* a is base type */
    if (a.isBaseType) {
      val aId = BaseType.unapply(a).get

      if (b.isBaseType) {
        val bId = BaseType.unapply(b).get
        return gt_baseType(aId, bId)
      }
      if (b.isFunType) {
        val (bI, bO) = ->.unapply(b).get
        return gt0(a, bI) && gt0(a, bO)
      }
      // TODO: Are there further meaningful cases?
    }
    /* a is function type */
    if (a.isFunType) {
      val (aI, aO) = ->.unapply(a).get

      if (ge0(aO, b)) return true

      if (b.isFunType) {
        val (bI, bO) = ->.unapply(b).get
        if (eq_type(aI,bI)) return gt0(aO,bO)
      }
      // TODO: Are there further meaningful cases?
    }
    /* adaption for quantified types */
    if (a.isPolyType) {
      val aO = ∀.unapply(a).get

      if (gt0(aO, b)) return true

      if (b.isPolyType) {
        val bO = ∀.unapply(b).get
        return gt0(aO,bO)
      }
      // TODO: Are there further meaningful cases?
    }
    /* adaption end */
    // TODO: We dont know what to do with other cases
    false
  }

  private def ge0(a: Type, b: Type): Boolean = { // TODO: Do we need that?
    import leo.datastructures.Type.{BaseType,->,∀}

    if (a == b) return true
    if (a.isBaseType) {
      val aId = BaseType.unapply(a).get

      if (b.isBaseType) {
        val bId = BaseType.unapply(b).get
        return ge_baseType(aId, bId)
      }
      if (b.isFunType) {
        val (bI, bO) = ->.unapply(b).get
        return gt0(a, bI) && gt0(a, bO)
      }
      // TODO: Are there further meaningful cases?
    }
    if (a.isFunType) {
      val (aI, aO) = ->.unapply(a).get

      if (ge0(aO, b)) return true

      if (b.isFunType) {
        val (bI, bO) = ->.unapply(b).get
        if (eq_type(aI,bI)) return ge0(aO,bO)
      }
      // TODO: Are there further meaningful cases?
    }
    /* adaption for quantified types */
    if (a.isPolyType) {
      val aO = ∀.unapply(a).get

      if (gt0(aO, b)) return true

      if (b.isPolyType) {
        val bO = ∀.unapply(b).get
        return gt0(aO,bO)
      }
      // TODO: Are there further meaningful cases?
    }
    /* adaption end */
    // TODO: We dont know what to do with other cases
    false
  }

  // Two types are equal wrt to the type ordering if they are
  // syntacticallly equal of they are equal base types (wrt to ordering of base types).
  private def eq_type(a: Type, b: Type): Boolean = {
    import leo.datastructures.Type.BaseType
    if (a == b) return true
    if (a.isBaseType && b.isBaseType) {
      val (aId, bId) = (BaseType.unapply(a).get, BaseType.unapply(b).get)
      return eq_baseType(aId,bId)
    }
    false
  }
  // Well-founded ordering of base types (sort)
  private def gt_baseType(bt1: Signature#Key, bt2: Signature#Key): Boolean = bt1 > bt2
  private def ge_baseType(bt1: Signature#Key, bt2: Signature#Key): Boolean = eq_baseType(bt1,bt2) || gt_baseType(bt1,bt2)
  private def eq_baseType(bt1: Signature#Key, bt2: Signature#Key): Boolean = bt1 == bt2

  // ###############################################################################
  ////////////////////////////////////
  // Comparisons of terms
  ////////////////////////////////////

  private final def gt0Stat(s: Seq[Term], t: Seq[Term], status: Int): Boolean = {
    if (status == Signature.get.lexStatus) {
      gt0Lex(s,t)
    } else if (status == Signature.get.multStatus) {
      gt0Mult(s,t)
    } else {
      // This should not happen
      Out.severe("[CPO_Naive] Status compare called with unknown status")
      false
    }
  }

  @tailrec
  private final def gt0Lex(s: Seq[Term], t: Seq[Term]): Boolean = {
    if (s.nonEmpty && t.nonEmpty) {
      if (s.head == t.head) {
        gt0Lex(s.tail,t.tail)
      } else {
        gt(s.head,t.head)
      }
    } else false
  }

  private final def gt0Mult(s: Seq[Term], t: Seq[Term]): Boolean = {
    if (s.nonEmpty && t.nonEmpty) {
      ???
    } else false
  }

  final private def gt0(s: Term, t: Term, x: Set[Term]): Boolean = {
    import leo.datastructures.Term.{:::>, Bound, MetaVar, Symbol, TypeLambda, ∙}

    if (s.isApp || s.isConstant) {
      val (f,args) = ∙.unapply(s).get

      f match {
        case Symbol(idf) =>
        /* f(t) > ... cases */
          var fargList: Seq[Term] = Seq()
          if(Signature(idf)._ty.isPolyType) {
            if (args.head.isRight) {
              // take first argument, rest will be terms (?)
              fargList = filterTermArgs(args.tail)
              Out.info("Removed corresponding type argument from spine for polymorphic head symbol.")
            } else {
              fargList = filterTermArgs(args)
              Out.severe("Polymorphic head symbol but no type argument in spine at head.")
            }
          } else {
            fargList = filterTermArgs(args)
          }

          /* case 1: f(t) > v */
          if (fargList.exists(gt0WithType(_, t, Set()))) return true

          /* case 2+3: f(t) > g(u) and case 4: f(t) > uv*/
          if (t.isApp || t.isConstant) {
            val (g,args2) = ∙.unapply(t).get


            g match {
              case Symbol(idg) => /* case 2+3 */

                var gargList: Seq[Term] = Seq()
                if(Signature(idg)._ty.isPolyType) {
                  if (args2.head.isRight) {
                    // take first argument, rest will be terms (?)
                    gargList = filterTermArgs(args2.tail)
                    Out.info("Removed corresponding type argument from spine for polymorphic head symbol.")
                  } else {
                    gargList = filterTermArgs(args2)
                    Out.severe("Polymorphic head symbol but no type argument in spine at head.")
                  }
                } else {
                  gargList = filterTermArgs(args2)
                }

                if (precedence(idf, idg) == 0) {
                  return gargList.forall(gt0(s,_, x)) && gt0Stat(fargList,gargList,Signature(idf).status)
                } else if (precedence(idf, idg) > 0) {
                  return gargList.forall(gt0(s,_, x))
                } else {
                  return false
                }
              case _ => /* case 4*/
                ???
            }
          }
          /* case 5: f(t) > lambda yv*/
          if (t.isTermAbs) {
            val (_,body) = :::>.unapply(t).get
            return gt0(s,body,x)
          }
          /* case 6: f(t) > y */
          if (t.isVariable) {
            return Bound.unapply(t).isDefined
          }
          // otherwise, fail
          return false
        case _ => /* tu > .... cases */
          ???
      }
    } else if (s.isTermAbs) {
      ???
    } else if (s.isTypeAbs) {
      ???
    } else
      Out.severe("Comparing unrecognized term. This is considered a bug! Please report.")
      false
  }

  def typegt(s: Type, t: Type): Boolean = ???

  def precedence(s: Signature#Key, t: Signature#Key): Int = t - s



  private def filterTermArgs(args: Seq[Either[Term, Type]]): Seq[Term] = args match {
    case Seq() => Seq()
    case Seq(h, rest@_*) => h match {
      case Left(term) => term +: filterTermArgs(rest)
      case Right(_) => filterTermArgs(rest)
    }
  }
}
