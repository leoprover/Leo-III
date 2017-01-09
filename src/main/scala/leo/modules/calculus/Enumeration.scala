package leo.modules.calculus

import leo.datastructures.{Signature, Term, Type}

/**
  * Enumerates some (special) instances for universal variables.
  *
  * @author Alexander Steen
  */
object Enumeration {
  @inline final def instancesFromSignature(ty: Type)(implicit sig:Signature): Set[Term] =
    sig.constantsOfType(ty).filterNot(sig(_).isDefined).map(Term.mkAtom)

  import Term.{mkBound, λ}
  import leo.modules.HOLSignature.{LitFalse, LitTrue, Not, o}
  private final lazy val instanceTable: Map[Type, Set[Term]] = Map(
    o -> Set(LitTrue, LitFalse),
    (o ->: o) -> Set(λ(o)(LitTrue),λ(o)(LitFalse),λ(o)(mkBound(o, 1)),λ(o)(Not(mkBound(o, 1))))
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

  @inline final def specialInstances(ty: Type)(implicit sig: Signature): Set[Term] = instanceTable.getOrElse(ty, Set())
}
