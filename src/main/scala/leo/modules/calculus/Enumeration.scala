package leo.modules.calculus

import leo.datastructures.{Signature, Term, Type}
import leo.modules.output.SZS_Theorem

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
  final val REPLACE_AO: Int = 4
  final val REPLACE_AAO: Int = 8
  @inline final def instancesFromSignature(ty: Type)(implicit sig:Signature): Set[Term] =
    sig.constantsOfType(ty).filterNot(sig(_).isDefined).map(Term.mkAtom)

  import Term.{mkBound, λ}
  import leo.modules.HOLSignature.{LitFalse, LitTrue, Not, o}
  private final lazy val instanceTable: Map[Type, Set[Term]] = Map(
    o -> Set(LitTrue(), LitFalse()),
    (o ->: o) -> Set(λ(o)(LitTrue()),λ(o)(LitFalse()),λ(o)(mkBound(o, 1)),λ(o)(Not(mkBound(o, 1))))
    // more to come, e.g. for ooo:
    //        λ(o,o)(LitTrue),
    //        λ(o,o)(LitFalse),
    //        λ(o,o)(2),
    //        λ(o,o)(1),
    //
    //        λ(o,o)(&(2,1)),
    //        λ(o,o)(&(Not(2),Not(1))),
    //        λ(o,o)(&(Not(2),1)),
    //        λ(o,o)(&(2,Not(1))),
    //
    //        λ(o,o)(|||(2,1)),
    //        λ(o,o)(|||(&(2,Not(1)),&(Not(2),1))),
    //        λ(o,o)(|||(&(Not(2),Not(1)),&(2,1))),
    //
    //        λ(o,o)(Not(&(2,1))),
    //        λ(o,o)(Not(&(Not(2),1))),
    //        λ(o,o)(Not(&(2,Not(1)))),
    //        λ(o,o)(Not(1)),
    //        λ(o,o)(Not(2))
  )
  @inline final def specialInstances(ty: Type, replace: Int = REPLACE_ALL)(implicit sig: Signature): Set[Term] = {
    import leo.modules.HOLSignature.{o, LitTrue, LitFalse, === => EQ, !=== => NEQ}
    if (instanceTable.contains(ty))
      if (leo.datastructures.isPropSet(REPLACE_O, replace) || leo.datastructures.isPropSet(REPLACE_OO, replace))
        instanceTable(ty)
      else Set()
    else {
      import leo.datastructures.Term._
      val tyArgTypes = ty.funParamTypesWithResultType
      var result: Set[Term] = Set()
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
  }

  private final lazy val exhaustiveList: Seq[Type] = Seq(o, o ->: o)
  @inline final def exhaustive(ty: Type): Boolean = exhaustiveList.contains(ty)


}
