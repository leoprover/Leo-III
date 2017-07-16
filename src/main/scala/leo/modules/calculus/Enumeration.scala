package leo.modules.calculus

import leo.datastructures.Term.{mkApp, mkBound, mkTermApp, λ}
import leo.datastructures.{Signature, Term, Type}
import leo.modules.output.SZS_Theorem
import leo.modules.HOLSignature.{o,Choice => HOLChoice, &, Impl, Not, ===, Exists}

/**
  * Enumerates some (special) instances for universal variables.
  *
  * @author Alexander Steen
  */
object Enumeration extends CalculusRule {
  override final val name = "instance"
  override final val inferenceStatus = SZS_Theorem

  final val REPLACE_ALL: Int = -1
  final val NO_REPLACE: Int = 0
  final val REPLACE_O: Int = 1
  final val REPLACE_OO: Int = 2
  final val REPLACE_OOO: Int = 4
  final val REPLACE_AO: Int = 8
  final val REPLACE_AAO: Int = 16
  final val REPLACE_SPECIAL: Int = 32

  @inline final def instancesFromSignature(ty: Type)(implicit sig:Signature): Set[Term] =
    sig.constantsOfType(ty).filterNot(sig(_).isDefined).map(Term.mkAtom)

  private final def ite_def: Term = λ(o,1,1)(
    HOLChoice(λ(1)(
      &(
        Impl(
        mkBound(o,4),
        ===(mkBound(1,1), mkBound(1,3))
        ),
        Impl(
          Not(mkBound(o,4)),
          ===(mkBound(1,1), mkBound(1,2))
        )
      )
    ))
  )

  import Term.{mkBound, λ}
  import leo.modules.HOLSignature.{LitFalse, LitTrue, |||}
  private final lazy val instanceTable: Map[Type, Set[Term]] = {
    val x = mkBound(o, 1)
    val y = mkBound(o, 2)
    val alpha = Type.mkVarType(1)
    val beta = Type.mkVarType(2)
    Map(
      o -> Set(LitTrue(), LitFalse()),
      (o ->: o) -> Set(λ(o)(LitTrue()),λ(o)(LitFalse()),λ(o)(x),λ(o)(Not(x))),
      (o ->: o ->: o) -> Set(
        λ(o,o)(LitTrue),
        λ(o,o)(LitFalse),
        λ(o,o)(y),
        λ(o,o)(x),

        λ(o,o)(&(y,x)),
        λ(o,o)(&(Not(y),Not(x))),
        λ(o,o)(&(Not(y),x)),
        λ(o,o)(&(y,Not(x))),

        λ(o,o)(|||(y,x)),
        λ(o,o)(|||(&(y,Not(x)),&(Not(y),x))),
        λ(o,o)(|||(&(Not(y),Not(x)),&(y,x))),

        λ(o,o)(Not(&(y,x))),
        λ(o,o)(Not(&(Not(y),x))),
        λ(o,o)(Not(&(y,Not(x)))),
        λ(o,o)(Not(x)),
        λ(o,o)(Not(y))
      ),
      (o ->: alpha ->: alpha ->: alpha) -> Set(ite_def),
      ((alpha ->: o) ->: alpha) -> Set(Term.mkTypeApp(HOLChoice, alpha),
        λ(alpha ->: o)(Term.mkTermApp(Term.mkTypeApp(HOLChoice, alpha), Seq(λ(alpha)(Not(mkTermApp(mkBound(alpha ->: o, 2),Seq(
          mkBound(alpha, 1)
        )))
        ))))
      ) //,
//      ((alpha ->: beta ->: o) ->: alpha) -> Set(
//        λ(alpha ->: beta ->: o)(
//          mkTermApp(Term.mkTypeApp(HOLChoice, alpha),
//            Seq(
//              λ(alpha)(
//                Exists(
//                  λ(alpha ->: beta)(
//                    mkTermApp(
//                      mkBound(alpha ->: beta ->: o, 3),
//                      Seq(mkBound(alpha, 2), mkTermApp(mkBound(alpha ->: beta, 1), mkBound(alpha, 2)))
//                    )
//                  )
//                )
//              )
//            )
//          )
//        )
//      ),
//      ((alpha ->: beta ->: o) ->: beta) -> Set(
//        λ(alpha ->: beta ->: o)(
//          mkTermApp(Term.mkTypeApp(HOLChoice, beta),
//            Seq(
//              λ(beta)(
//                Exists(
//                  λ(beta ->: alpha)(
//                    mkTermApp(
//                      mkBound(alpha ->: beta ->: o, 3),
//                      Seq(mkTermApp(mkBound(beta ->: alpha, 1), mkBound(beta,2)), mkBound(beta, 2))
//                    )
//                  )
//                )
//              )
//            )
//          )
//        )
//      )
    )
  }
  @inline final def specialInstances(ty: Type, replace: Int = REPLACE_ALL)(implicit sig: Signature): Set[Term] = {
    import leo.modules.HOLSignature.{o, LitTrue, LitFalse, === => EQ, !=== => NEQ}

    var feasibleTableEntry: Seq[(Type, TypeSubst)] = Seq.empty
    val entryKeysIt = instanceTable.keysIterator
    while (entryKeysIt.hasNext) {
      val key = entryKeysIt.next()
      val maybeUni = TypeUnification(key, ty)
      if (maybeUni.isDefined) {
        val subst = maybeUni.get
        feasibleTableEntry = feasibleTableEntry :+ (key, subst)
      }
    }
    var result: Set[Term] = Set.empty

    feasibleTableEntry foreach { case (key, uni) =>
      if (instanceTable.contains(key)) {
        if (leo.datastructures.isPropSet(REPLACE_O, replace) || leo.datastructures.isPropSet(REPLACE_OO, replace) || leo.datastructures.isPropSet(REPLACE_OOO, replace)) {
          val tempresult = instanceTable(key).map(_.typeSubst(uni))
          result = result union tempresult
        }
      }
    }

    import leo.datastructures.Term._
    val tyArgTypes = ty.funParamTypesWithResultType
    if (tyArgTypes.last == o) { // predicate
      if (leo.datastructures.isPropSet(REPLACE_AO, replace))
        result = result union Set(λ(tyArgTypes.init)(LitTrue()),λ(tyArgTypes.init)(LitFalse()))
      if (tyArgTypes.size == 3) { // equality only for a > a > o
      val ty1 = tyArgTypes.head; val ty2 = tyArgTypes.tail.head
        if (ty1 == ty2) {
          if (leo.datastructures.isPropSet(REPLACE_AAO, replace))
            result = result union Set(
              λ(ty1, ty2)(EQ(mkBound(ty1, 2),mkBound(ty1, 1))),
              λ(ty1, ty2)(NEQ(mkBound(ty1, 2),mkBound(ty1, 1))))
        }
      }
    }

    result
  }

  private final lazy val exhaustiveList: Seq[Type] = Seq(o, o ->: o, o ->: o ->: o)
  @inline final def exhaustive(ty: Type): Boolean = exhaustiveList.contains(ty)


}
