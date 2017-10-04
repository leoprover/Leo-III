package leo.datastructures.impl.orderings

import leo.Configuration
import leo.datastructures.{Term, Type}
import leo.datastructures._
import leo.modules.output.logger.Out

import scala.annotation.tailrec

/**
 * Computability path Ordering (Core CPO)
 * by Blanqui, Jouannaud, Rubio
 *
 * @author Alexander Steen <a.steen@fu-berlin.de>
 * @since 29.09.2015
 */
object TO_CPO_Naive extends TermOrdering {
  /////////////////////////////////////////////////////////////////
  /// Exported functions
  /////////////////////////////////////////////////////////////////

  /* Comparisons of types */

  // Core function for comparisons
  @inline final def gt(a: Type, b: Type): Boolean = gt0(a,b)
  @inline final def gteq(a: Type, b: Type): Boolean = ge0(a,b)

  // Defined by gt/ge
  @inline final def lt(a: Type, b: Type): Boolean = gt(b,a)
  @inline final def lteq(a: Type, b: Type): Boolean = gteq(b,a)

  @inline final def compare(a: Type, b: Type): CMP_Result = {
    if (a == b) CMP_EQ
    else if (gt(a,b)) CMP_GT
    else if (lt(a,b)) CMP_LT
    else CMP_NC
  }
  @inline final def canCompare(a: Type, b: Type): Boolean = compare(a,b) != CMP_NC


  /* Comparisons of terms */

  // Core function for comparisons
  @inline final def gt(s: Term, t: Term)(implicit sig: Signature): Boolean = gteq(s.ty, t.ty) && gt0(s,t, 0)(sig)
  @inline final def gt(s: Term, t: Term, depth: Int)(implicit sig: Signature): Boolean = gteq(s.ty, t.ty) && gt0(s,t, depth)(sig)
  @inline final def gtMult(s: Seq[Term], t: Seq[Term])(implicit sig: Signature): Boolean = gt0Mult(s,t)(sig)

  @inline final def gteq(s: Term, t: Term)(implicit sig: Signature): Boolean = gteq(s.ty, t.ty) && ge0(s,t, 0)(sig)
  @inline final def gteq(s: Term, t: Term, depth: Int)(implicit sig: Signature): Boolean = gteq(s.ty, t.ty) && ge0(s,t, depth)(sig)

  // Defined by gt/ge
  @inline final def lt(s: Term, t: Term)(implicit sig: Signature): Boolean = gt(t,s)(sig)
  @inline final def lteq(s: Term, t: Term)(implicit sig: Signature): Boolean = gteq(t,s)(sig)

  @inline final def compare(s: Term, t: Term)(implicit sig: Signature): CMP_Result = {
    if (s == t) CMP_EQ
    else if (gt(s,t)(sig)) CMP_GT
    else if (lt(s,t)(sig)) CMP_LT
    else CMP_NC
  }
  @inline final def canCompare(s: Term, t: Term)(implicit sig: Signature): Boolean = compare(s,t)(sig) != CMP_NC

  /* Common comparison-related operations */

  // Nothing


  /////////////////////////////////////////////////////////////////
  /// Internal functions
  /////////////////////////////////////////////////////////////////

  // ###############################################################################

  // well-founded ordering of symbols in signature
  final private def precedence(s: Signature.Key, t: Signature.Key)(sig: Signature): CMP_Result = Configuration.PRECEDENCE.compare(s,t)(sig)

  // Well-founded ordering of base types (sort)
  final private def gt_baseType(bt1: Signature.Key, bt2: Signature.Key): Boolean = bt1 > bt2
  final private def ge_baseType(bt1: Signature.Key, bt2: Signature.Key): Boolean = eq_baseType(bt1,bt2) || gt_baseType(bt1,bt2)
  final private def eq_baseType(bt1: Signature.Key, bt2: Signature.Key): Boolean = bt1 == bt2

  ////////////////////////////////////
  // Comparisons of types
  ////////////////////////////////////
  final private def gt0(a: Type, b: Type): Boolean = {
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

      if (ge0(aO, b)) return true

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

  final private def ge0(a: Type, b: Type): Boolean = {
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
  @inline final private def eq_type(a: Type, b: Type): Boolean = {
    import leo.datastructures.Type.BaseType
    if (a == b) return true
    if (a.isBaseType && b.isBaseType) {
      val (aId, bId) = (BaseType.unapply(a).get, BaseType.unapply(b).get)
      return eq_baseType(aId,bId)
    }
    false
  }

  // ###############################################################################
  ////////////////////////////////////
  // Comparisons of terms
  ////////////////////////////////////

  @inline private final def gt0Stat(a: Term, s: Seq[Term], t: Seq[Term], depth: Int, status: Int)(sig: Signature): Boolean = {
    import leo.datastructures.Signature.{lexStatus,multStatus}
    if (status == lexStatus) {
//      println("lex")
      if (s.length > t.length){
        alleq(s,t,t.length)
      } else gt0Lex(a,s,t,depth)(sig)
    } else if (status == multStatus) {
//      println("mult")
      gt0Mult(s,t)(sig)
    } else {
      // This should not happen
      Out.severe("[CPO_Naive] Status compare called with unknown status")
      false
    }
  }

  @tailrec
  private final def gt0Lex(a: Term, s: Seq[Term], t: Seq[Term], depth: Int)(sig: Signature): Boolean = {
    if (s.nonEmpty && t.nonEmpty) {
      if (s.head == t.head) {
        gt0Lex(a,s.tail,t.tail,depth)(sig)
      } else {
        gt(s.head,t.head)(sig) && t.tail.forall(gt0(a,_,depth)(sig))
      }
    } else false
  }

  @tailrec
  private final def alleq(s: Seq[Term], t: Seq[Term], n: Int): Boolean = {
    if (n == 0) true
    else if (s.head == t.head) {
      alleq(s.tail,t.tail,n-1)
    }
    else false
  }

  private final def gt0Mult(s: Seq[Term], t: Seq[Term])(sig: Signature): Boolean = {
    if (s.nonEmpty && t.isEmpty) true
    else if (s.nonEmpty && t.nonEmpty) {
      val sameElements = s.intersect(t)
      val remSameS = s.diff(sameElements)
      val remSameT = t.diff(sameElements)
      if (remSameS.isEmpty && remSameT.isEmpty) false
      else gt0Mult0(remSameS, remSameT)(sig)
    } else false
  }

  @tailrec
  private final def gt0Mult0(s: Seq[Term], t: Seq[Term])(sig: Signature): Boolean = {
    if (t.isEmpty) true
    else if (s.nonEmpty && t.nonEmpty) {
      val sn = s.head
      val tIt = t.iterator
      var keepT: Seq[Term] = Vector.empty
      while (tIt.hasNext) {
        val tn = tIt.next()
        if (!gt(sn, tn)(sig)) {
          keepT = keepT :+ tn
        }
      }
      gt0Mult0(s.tail,keepT)(sig)
    } else false
  }

  final private def gt0(s: Term, t: Term, depth: Int)(sig: Signature): Boolean = {
    import leo.datastructures.Term.{:::>, Bound, Symbol, TypeLambda, ∙}
    import leo.datastructures.Term.local.mkApp

    if (s == t) return false
    if (s.isVariable) return false
    // #######################################################
    /* adaption for type abstractions*/
    if (s.isTypeAbs) {
      val sO = TypeLambda.unapply(s).get
      if (gteq(sO,t,depth)(sig)) return true
      if (t.isTypeAbs) {
        val tO = TypeLambda.unapply(t).get
        return gt0(sO,tO,depth)(sig)
      }
      return false
    }
    else
    /* adaption end */
    // #######################################################
    // #############
    // All \-rules (\>, \=, \!=, \X) without \eta
    // #############
    if (s.isTermAbs) {
      val (sInTy, sO) = :::>.unapply(s).get

      if (gteq(sO,t,depth)(sig)) return true
      if (t.isTermAbs) {
        val (tInTy, tO) = :::>.unapply(t).get

        if (sInTy == tInTy) return gt0(sO, tO, depth)(sig)
        else return gt0(s, tO, depth)(sig)
      }
      /* case 15: \x.s >= y */
      if (t.isVariable) {
        return Bound.unapply(t).get._2 <= depth
      }
      return false
    }
    // #######################################################
    else
    if (s.isConstant) {
      //      println(s"constant: ${s.pretty(sig)}")
      //      println(s"t: ${t.pretty(sig)}")
      if (t.isVariable) {
        Bound.unapply(t).get._2 <= depth
      } else if (t.isTermAbs) {
        /* case 5: f(t) > lambda yv*/
        val (_, tO) = :::>.unapply(t).get
        gt0(s,tO,depth+1)(sig)
      } else if (t.isApp || t.isAtom) {
        val idf = Symbol.unapply(s).get
        val (g,args2) = ∙.unapply(t).get
        val gargList: Seq[Term] = effectiveArgs(g.ty, args2)
        if (g.isConstant) {
          val idg = Symbol.unapply(g).get
          if (precedence(idf, idg)(sig) == CMP_EQ) {
            if(gt0Stat(s,Vector.empty, gargList, depth, sig(idf).status)(sig)) return true
          } else if (precedence(idf, idg)(sig) == CMP_GT) {
            if(gargList.forall(gt0(s, _, depth)(sig))) return true
          } else {
            return false
          }
        }
        if (gargList.nonEmpty) {
          gt0(s, mkApp(g, args2.init), depth)(sig) && gt0(s, gargList.last, depth)(sig)
        } else false
      } else false
    }
    // #######################################################
    else
    if (s.isApp) {
//      println(s"s.isApp: ${s.pretty(sig)}")
      /* case 6: f(t) >= y and */
      /* case 10: @(s,t) >= y */
      if (t.isVariable) {
//        println(s"t.isVariable (check ${Bound.unapply(t).get._2 <= depth})")
        if(Bound.unapply(t).get._2 <= depth) return true
      }

      val (f,args) = ∙.unapply(s).get
      val fargList: Seq[Term] = effectiveArgs(f.ty,args)

      if (fargList.nonEmpty) {
        if (t.isTermAbs) {
          val (_, tO) = :::>.unapply(t).get
          if (f.isConstant) {
            /* case 5: f(t) > lambda yv*/
            if (gt0(s,tO,depth+1)(sig)) return true
          }
          /* case 9: @(s,t) > lambda yv*/
          if (gt0(s, tO, depth)(sig)) return true // TODO need that? probably b/c vars at head position
        }
        if (t.isConstant) {
//                    println(s"t.isConstant: ${t.pretty(sig)}")
          if (f.isConstant) {
//                        println(s"f.isConstant: ${f.pretty(sig)}")
            val idf = Symbol.unapply(f).get
            val idg = Symbol.unapply(t).get
            return Orderings.isGE(precedence(idf, idg)(sig))
          }
        }
        if (t.isApp) {
//          println(s"t.isApp: ${t.pretty(sig)}")
          val (g,args2) = ∙.unapply(t).get
          val gargList: Seq[Term] = effectiveArgs(g.ty,args2)

          /* case 2+3: f(t) > g(u) */
          if (f.isConstant && g.isConstant) {
//            println(s"f.isConstant (${f.pretty(sig)}) && g.isConstant (${g.pretty(sig)})")
            val idf = Symbol.unapply(f).get
            val idg = Symbol.unapply(g).get
            try {
              if (precedence(idf, idg)(sig) == CMP_EQ) {
//                println("precedence EQ")
                if (gt0Stat(s,fargList, gargList, depth, sig(idf).status)(sig)) return true
              } else if (precedence(idf, idg)(sig) == CMP_GT) {
//                println("precedence GT")
                if (gargList.forall(gt0(s, _, depth)(sig))) return true
              } else {
//                println("precedence else")
//                return false
              }
            } catch {
              case e:AssertionError =>
                Out.severe(e.getMessage)
                Out.output("TERM s: \t\t " + s.pretty)
                Out.output("TERM t: \t\t " + t.pretty)
                throw e
              case e: Exception =>
                println(idf)
                println(f.pretty)
                throw e
            }
          }

          if (gargList.nonEmpty) {
//            println("gargList.nonEmpty")
            /* case 4: f(t) > @(u,v)*/
            if (f.isConstant)
              if(gt0(s, mkApp(g, args2.init), depth)(sig) && gt0(s, gargList.last, depth)(sig)) return true

            /* case 8: @(s,t) > @(u,v) */
            val s2 = mkApp(f,args.init)
            val t2 = mkApp(g, args2.init)
            if (s2 == t2) {
              if (gt0(fargList.last, gargList.last,depth)(sig)) return true
            }
            if ((gt(s2,t2,depth)(sig) || gteq(fargList.last,t2,depth)(sig) || gt(s,t2)(sig))
              && (gt(s2,gargList.last,depth)(sig) || gteq(fargList.last,gargList.last,depth)(sig) || gt(s,gargList.last)(sig))) return true
          }
        }
        /* case 1: f(t) >= v */
        if (f.isConstant) {
//          println("f.isConstant (case 1)")
          if (fargList.exists(gteq(_, t)(sig))) return true
        }
        /* case 7: @(s,t) > v */
        if (ge0(mkApp(f,args.init),t,depth)(sig)) return true
        if (gteq(fargList.last,t,depth)(sig)) return true
        false
      } else false
    }
    // #######################################################
    else {
      assert(false)
      false
    }
  }


  @inline final private def ge0(s: Term, t: Term, depth: Int)(sig: Signature): Boolean = {
    if (s == t) true
    else gt0(s,t,depth)(sig)
  }


  final private def effectiveArgs(forTy: Type, args: Seq[Either[Term, Type]]): Seq[Term] = {
    assert(args.take(forTy.polyPrefixArgsCount).forall(_.isRight), s"Number of expected type arguments (${forTy.polyPrefixArgsCount}) do not match ty abstraction count: \n\t Type: ${forTy.pretty}\n\tArgs: ${args.map(_.fold(_.pretty,_.pretty))}")
    filterTermArgs(args.drop(forTy.polyPrefixArgsCount))
  }

 final private def filterTermArgs(args: Seq[Either[Term, Type]]): Seq[Term] = {
    if (args.isEmpty) Vector.empty
    else {
      val hd = args.head
      if (hd.isLeft) {
        hd.left.get +: filterTermArgs(args.tail)
      } else filterTermArgs(args.tail)
    }
  }
}
