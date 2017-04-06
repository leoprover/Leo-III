package leo.modules.encoding

import leo.datastructures.{Signature, Term, Type}
import leo.Out
import leo.modules.Utility


/**
  * A LambdaElimStrategy is an identifier for a concrete technique for eliminating
  * lambda abstraction inside higher-order problems.
  * Invoking [[leo.modules.encoding.LambdaElimStrategy#apply]] returns a
  * new instance of that (stateful) elimination procedure wrt. to a certain
  * [[leo.modules.encoding.TypedFOLEncodingSignature]].
  *
  * @see [[leo.modules.encoding.LambdaElimination]]
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
sealed trait LambdaElimStrategy extends Function1[TypedFOLEncodingSignature, LambdaElimination]
object LambdaElimStrategy_SKI extends LambdaElimStrategy {
  final def apply(sig: TypedFOLEncodingSignature): LambdaElimination = new LambdaElim_SKI(sig)
}
object LambdaElimStrategy_Turner extends LambdaElimStrategy {
  final def apply(sig: TypedFOLEncodingSignature): LambdaElimination = new LambdaElim_Turner(sig)
}
object LambdaElimStrategy_LambdaLifting extends LambdaElimStrategy {
  final def apply(sig: TypedFOLEncodingSignature): LambdaElimination = new LambdaElim_LambdaLifting(sig)
}

/**
  * A LambdaElimination escapsulates a concrete implementation of a
  * procedure for elimination lambda abstraction inside higher-order terms.
  * Popular examples are (i) replacing lambda abstraction by SKI combinators
  * (as done by [[leo.modules.encoding.LambdaElim_SKI]]) or by introducing super-combinators
  * (as done  by [[leo.modules.encoding.LambdaElim_LambdaLifting]]).
  *
  * @note Since, during the transformation, auxiliary constants may be employed,
  *       the transformation is stateful in the sense that the LambdaElimination instance
  *       will keep track of used auxiliary symbols.
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  * @since February 2017
  */
protected[encoding] abstract class LambdaElimination(sig: TypedFOLEncodingSignature) {
  /** Given a term `t`, this method returns a term `t'` that does not contain
    * any lambda abstraction^*^ such that `t'` is an first-order encoding
    * of `t`.
    *
    * @note Side-effects:
    *       - This method inserts new symbols into `sig`
    *       - This method manipulates the internal state of
    *       the concrete [[leo.modules.encoding.LambdaElimination]] instance.
    */
  def eliminateLambda(t: Term)(holSignature: Signature): Term

  /** Returns a set of terms which represent auxiliary axioms of symbols used for the translation. */
  def getAuxiliaryDefinitions: Set[Term]
}

protected[encoding] class LambdaElim_SKI(sig: TypedFOLEncodingSignature) extends LambdaElimination(sig) {
  import leo.datastructures.Term.local._
  import leo.datastructures.Type.{∀, mkVarType}
  import sig.{hApp,funTy}

  private var usedSymbols: Set[Signature#Key] = Set.empty

  /* Combinator definitions from here */
  // Shorthands used for type definitions later
  final private val Xty: Type = mkVarType(1)
  final private val Yty: Type = mkVarType(2)
  final private val Zty: Type = mkVarType(3)

  ////////////
  // I combinator
  ////////////
  /** The name of combinator I.
    * @see [[leo.modules.encoding.LambdaElim_SKI#combinator_I]] */
  final val combinator_I_name: String = "comb_I"
  /** Combinator I's type:  `∀a. fun(a,a)`
    * @see [[leo.modules.encoding.LambdaElim_SKI#combinator_I]] */
  final lazy val combinator_I_type: Type = ∀(funTy(Xty,Xty))
  private final lazy val combinator_I_key: Signature#Key = {sig.addUninterpreted(safeName(combinator_I_name), combinator_I_type)}
  /** Curry I combinator given by `I x = x`. */
  final lazy val combinator_I: Term = {
    val id = combinator_I_key
    usedSymbols += id
    mkAtom(id)(sig)
  }
  final def I(ty: Type): Term = mkTypeApp(combinator_I, ty)

  ////////////
  // K combinator
  ////////////
  /** The name of combinator K.
    * @see [[leo.modules.encoding.LambdaElim_SKI#combinator_K]] */
  final val combinator_K_name: String = "comb_K"
  /** Combinator K's type: `∀a.∀b. fun(a,fun(b,a))`
    * @see [[leo.modules.encoding.LambdaElim_SKI#combinator_K]] */
  final lazy val combinator_K_type: Type = ∀(∀(funTy(Yty,funTy(Xty, Yty))))
  private final lazy val combinator_K_key: Signature#Key = {sig.addUninterpreted(safeName(combinator_K_name), combinator_K_type)}
  /** Curry K combinator given by `K x y = x`, where
    * `K : ∀a.∀b. fun(a,fun(b,a))` */
  final lazy val combinator_K: Term = {
    val id = combinator_K_key
    usedSymbols += id
    mkAtom(id)(sig)
  }
  final def K(ty1: Type, ty2: Type, arg: Term): Term = {
    Out.finest("application of K")
    Out.finest(s"ty1: ${ty1.pretty}")
    Out.finest(s"ty2: ${ty2.pretty}")
    Out.finest(s"arg: ${arg.pretty}")
    Out.finest(s"K instance type: ${mkTypeApp(combinator_K, Seq(ty1, ty2)).ty.pretty}")

    hApp(mkTypeApp(combinator_K, Seq(ty1, ty2)),arg)
  }

  ////////////
  // S combinator
  ////////////
  /** The name of combinator S.
    * @see [[leo.modules.encoding.LambdaElim_SKI#combinator_K]] */
  final val combinator_S_name: String = "comb_S"
  /** Combinator S's type:  `∀a.∀b.∀c. fun(fun(a,fun(b,c)),fun(fun(a,b),fun(a,c)))`
    * @see [[leo.modules.encoding.LambdaElim_SKI#combinator_S]] */
  final lazy val combinator_S_type: Type = ∀(∀(∀(funTy(funTy(Zty, funTy(Yty, Xty)),funTy(funTy(Zty, Yty),funTy(Zty, Xty))))))
  private final lazy val combinator_S_key: Signature#Key = {sig.addUninterpreted(safeName(combinator_S_name),combinator_S_type)}
  /** Curry S combinator given by `S x y z = x z (y z)`. */
  final lazy val combinator_S: Term = {
    val id = combinator_S_key
    usedSymbols += id
    mkAtom(id)(sig)
  }
  final def S(ty1: Type, ty2: Type, ty3: Type, arg1: Term, arg2: Term): Term = {
    hApp(mkTypeApp(combinator_S, Seq(ty1, ty2, ty3)), Seq(arg1, arg2))
  }

  ////////////
  // B combinator
  ////////////
  /** The name of combinator B.
    * @see [[leo.modules.encoding.LambdaElim_SKI#combinator_B]] */
  final val combinator_B_name: String = "comb_B"
  /** Combinator B's type:  `∀a.∀b.∀c. fun(fun(b,c),fun(fun(a,b), fun(a,c)))`
    * @see [[leo.modules.encoding.LambdaElim_SKI#combinator_B]] */
  final lazy val combinator_B_type: Type = ∀(∀(∀(funTy(funTy(Yty,Xty),funTy(funTy(Zty,Yty),funTy(Zty,Xty))))))
  private final lazy val combinator_B_key: Signature#Key = {sig.addUninterpreted(safeName(combinator_B_name),combinator_B_type)}
  /** Curry B combinator given by `B x y z = x (y z)` where
    * `B: ∀a.∀b.∀c. fun(fun(b,c),fun(fun(a,b), fun(a,c)))`. */
  final lazy val combinator_B: Term = {
    val id = combinator_B_key
    usedSymbols += id
    mkAtom(id)(sig)
  }
  final def B(ty1: Type, ty2: Type, ty3: Type, arg1: Term, arg2: Term): Term = {
    val tyAppliedB: Term = mkTypeApp(combinator_B, Seq(ty1, ty2, ty3))
    hApp(tyAppliedB, Seq(arg1,arg2))
  }

  ////////////
  // C combinator
  ////////////
  /** The name of combinator C.
    * @see [[leo.modules.encoding.LambdaElim_SKI#combinator_C]] */
  final val combinator_C_name: String = "comb_C"
  /** Combinator C's type:  `∀a.∀b.∀c. fun(fun(a,fun(b,c)), fun(b, fun(a,c))) `
    * @see [[leo.modules.encoding.LambdaElim_SKI#combinator_C]] */
  final lazy val combinator_C_type: Type = ∀(∀(∀(funTy(funTy(Zty, funTy(Yty, Xty)),funTy(Yty, funTy(Zty, Xty))))))
  private final lazy val combinator_C_key: Signature#Key = {sig.addUninterpreted(safeName(combinator_C_name),combinator_C_type)}
  /** Curry C combinator given by `C x y z = x z y`. */
  final lazy val combinator_C: Term = {
    val id = combinator_C_key
    usedSymbols += id
    mkAtom(id)(sig)
  }
  final def C(ty1: Type, ty2: Type, ty3: Type, arg1: Term, arg2: Term): Term = {
    hApp(mkTypeApp(combinator_C, Seq(ty1, ty2, ty3)),Seq(arg1,arg2))
  }

  /* Lambda elimination procedure from here */
  /** entry point */
  def eliminateLambdaNew(t: Term)(holSignature: Signature): Term = {
    import leo.datastructures.Term._
    import TypedFOLEncoding.translateTerm

    t match {
      case ty :::> body =>
        if (body.isTermAbs) {
          Out.finest(s"recurse on: ${body.pretty(holSignature)}")
          val innerElim = eliminateLambdaNew(body)(holSignature)
          Out.finest(s"end-recurse on: ${innerElim.pretty(sig)}")
          assert(Term.wellTyped(innerElim))
          eliminateLambdaNewShallow(ty, innerElim)(holSignature)
        } else eliminateLambdaNew0(ty, body)(holSignature)
      case _ => /* nolambda */ translateTerm(t, this)(holSignature, sig)
    }
  }

  /** eliminate a top-level lambda abstraction λ(absType)(absBody) where
    * it is assumed that absBody was already recursively transformed to FO. Hence,
    * terms need not to be converted again. */
  def eliminateLambdaNewShallow(absType: Type, absBody: Term)(holSignature: Signature): Term = {
    Out.finest(s"[S] ${absType.pretty(holSignature)} :::> ${absBody.pretty(sig)}")
    import leo.datastructures.Term._
    import leo.modules.encoding.TypedFOLEncoding.foTransformType0
    import leo.datastructures.Type.ComposedType
    absBody match {
      case Bound(varTy, 1) => //Out.finest("[S] I")
        val translatedTy = foTransformType0(absType, true)(holSignature, sig)
        assert(varTy == translatedTy)
        I(translatedTy)
      case _ if !free(absBody, 1) =>
        Out.finest("[S] K")
        val translatedTy = foTransformType0(absType, true)(holSignature, sig)
        Out.finest(s"translatedTy: ${translatedTy.pretty(sig)}")
        K(absBody.ty, translatedTy, absBody)
      case f ∙ args =>
        val lastArg = args.last
        if (lastArg.isLeft) {
          val termLastArg = lastArg.left.get
          val allButLastArg = if (f == sig.hApp) {
            assert(args.size == 4)
            args(2).left.get
          } else {
            assert(false)
            Term.mkApp(f, args.init)
          }

          val (funid, tyargs) = ComposedType.unapply(allButLastArg.ty).get
          assert(funid == sig.funTy_id)
          assert(tyargs.size == 2)
          assert(termLastArg.ty == tyargs.head)

          val allButLastArgCoDomainType = tyargs(1)
          val allButLastArgDomainType = tyargs.head

          Out.finest(s"[S] head: " + f.pretty(sig))
          Out.finest("[S] allButLastArg: " + allButLastArg.pretty(sig))
          Out.finest("[S] lastArg: " + termLastArg.pretty(sig))
          if (!free(allButLastArg, 1)) {
            // eta or B
            if (termLastArg.isVariable && Bound.unapply(termLastArg).get._2 == 1) {
              Out.finest("[S] eta")
              allButLastArg.lift(-1)
            } else {
              Out.finest("[S] B")
//              `∀a.∀b.∀c. fun(fun(b,c),fun(fun(a,b), fun(a,c)))`
              val translatedTy = foTransformType0(absType, true)(holSignature, sig)
              val translatedAllButLastArg = allButLastArg.lift(-1)
              val translatedTermLastArg = eliminateLambdaNewShallow(absType, termLastArg)(holSignature)
              B(translatedTy, allButLastArgDomainType, allButLastArgCoDomainType, translatedAllButLastArg, translatedTermLastArg)
            }
          } else if (!free(termLastArg,1)) {
            // C
            Out.finest("[S] C")
            // `∀a.∀b.∀c. fun(fun(a,fun(b,c)), fun(b, fun(a,c))) `
            val translatedTy = foTransformType0(absType, true)(holSignature, sig)
            val translatedAllButLastArg = eliminateLambdaNewShallow(absType,allButLastArg)(holSignature)
            val translatedTermLastArg = termLastArg.lift(-1)
            C(translatedTy, allButLastArgDomainType, allButLastArgCoDomainType, translatedAllButLastArg, translatedTermLastArg)
          } else { // occurs in both free
            // S
            Out.finest(s"[S] S")
//            `∀a.∀b.∀c. fun(fun(a,fun(b,c)),fun(fun(a,b),fun(a,c)))`
            val translatedTy = foTransformType0(absType, true)(holSignature, sig)
            val translatedAllButLastArg = eliminateLambdaNewShallow(absType,allButLastArg)(holSignature)
            val translatedTermLastArg = eliminateLambdaNewShallow(absType,termLastArg)(holSignature)
            S(translatedTy, allButLastArgDomainType, allButLastArgCoDomainType, translatedAllButLastArg, translatedTermLastArg)
          }
        } else {
          ???
        }
      case _ => // this would be: (1) TypeLambda, (2) Lambda.
        // Cannot happen since (1) is not in the valid term fragment
        // (2) was checked by eliminateLambda before
        throw new IllegalArgumentException
    }
  }

  def eliminateLambdaNew0(absType: Type, absBody: Term)(holSignature: Signature): Term = {
    import leo.datastructures.Term._
    import leo.modules.encoding.TypedFOLEncoding.foTransformType0
    Out.finest(s"eliminateLambdaNew0: ${absType.pretty(holSignature)} :::> ${absBody.pretty(holSignature)}")
    absBody match {
      case Bound(`absType`, 1) =>
        Out.finest("I")
        I(foTransformType0(absType, true)(holSignature, sig))
      case _ if !free(absBody, 1) =>
        Out.finest("K")
        val translatedTy = foTransformType0(absType, true)(holSignature, sig)
        val translatedBody = eliminateLambdaNew(absBody.lift(-1))(holSignature)
        K(translatedBody.ty, translatedTy, translatedBody)
      case f ∙ args =>
        val lastArg = args.last
        if (lastArg.isLeft) {
          val termLastArg = lastArg.left.get
          val allButLastArg = Term.mkApp(f, args.init)
          Out.finest(s"head: " + f.pretty(holSignature))
          Out.finest("allButLastArg: " + allButLastArg.pretty(holSignature))
          Out.finest(s"matches not: ${leo.modules.HOLSignature.Not.unapply(absBody).isDefined}")
          Out.finest(s"matches not: ${leo.modules.HOLSignature.Not.unapply(allButLastArg).isDefined}")
          Out.finest("lastArg: " + termLastArg.pretty(holSignature))
          if (!free(allButLastArg, 1)) {
            // eta or B
            if (termLastArg.isVariable && Bound.unapply(termLastArg).get._2 == 1) {
              Out.finest("eta")
              Out.finest(s"Remaining: ${allButLastArg.lift(-1).pretty(holSignature)}")
              eliminateLambdaNew(allButLastArg.lift(-1))(holSignature)
            } else {
              Out.finest("B")
              assert(termLastArg.ty == allButLastArg.ty._funDomainType)
              val translatedTy = foTransformType0(absType, true)(holSignature, sig)
              val translatedAllButLastArgCoDomainType = foTransformType0(allButLastArg.ty.codomainType, true)(holSignature, sig)
              val translatedAllButLastArgDomainType = foTransformType0(allButLastArg.ty._funDomainType, true)(holSignature, sig)
              val translatedAllButLastArg = eliminateLambdaNew(allButLastArg.lift(-1))(holSignature)
              val x = λ(absType)(termLastArg)
              Out.finest("lambda stuf " + x.pretty(holSignature))
              val translatedTermLastArg = eliminateLambdaNew(x)(holSignature)
              Out.finest(s"translatedTermLastArg: ${translatedTermLastArg.pretty(sig)}")
              B(translatedTy, translatedAllButLastArgDomainType, translatedAllButLastArgCoDomainType, translatedAllButLastArg, translatedTermLastArg)
            }
          } else if (!free(termLastArg,1)) {
            // C
            Out.finest(s"C")
            assert(termLastArg.ty == allButLastArg.ty._funDomainType)
            val translatedTy = foTransformType0(absType, true)(holSignature, sig)
            val translatedAllButLastArgCoDomainType = foTransformType0(allButLastArg.ty.codomainType, true)(holSignature, sig)
            val translatedAllButLastArgDomainType = foTransformType0(allButLastArg.ty._funDomainType, true)(holSignature, sig)
            val translatedAllButLastArg = eliminateLambdaNew(λ(absType)(allButLastArg))(holSignature)
            val translatedTermLastArg = eliminateLambdaNew(termLastArg.lift(-1))(holSignature)
            C(translatedTy, translatedAllButLastArgDomainType, translatedAllButLastArgCoDomainType, translatedAllButLastArg, translatedTermLastArg)
          } else { // occurs in both free
            // S
            Out.finest(s"S")
            assert(termLastArg.ty == allButLastArg.ty._funDomainType)
            val translatedTy = foTransformType0(absType, true)(holSignature, sig)
            val translatedAllButLastArgCoDomainType = foTransformType0(allButLastArg.ty.codomainType, true)(holSignature, sig)
            val translatedAllButLastArgDomainType = foTransformType0(allButLastArg.ty._funDomainType, true)(holSignature, sig)
            val translatedAllButLastArg = eliminateLambdaNew(λ(absType)(allButLastArg))(holSignature)
            val translatedTermLastArg = eliminateLambdaNew(λ(absType)(termLastArg))(holSignature)
            S(translatedTy, translatedAllButLastArgDomainType, translatedAllButLastArgCoDomainType, translatedAllButLastArg, translatedTermLastArg)
          }
        } else {
          ???
        }
      case _ => // this would be: (1) TypeLambda, (2) Lambda.
        // Cannot happen since (1) is not in the valid term fragment
        // (2) was checked by eliminateLambda before
        throw new IllegalArgumentException
    }
  }

  @inline private final def free(t: Term, idx: Int): Boolean = t.looseBounds.contains(idx)

  override def eliminateLambda(t: Term)(holSignature: Signature): Term = eliminateLambdaNew(t)(holSignature)

  override def getAuxiliaryDefinitions: Set[Term] = {
    var result: Set[Term] = Set.empty
    for (usedSymbol <- usedSymbols) {
      result += axiomOf(usedSymbol)
    }
    result
  }

  private final def axiomOf(comb: Signature#Key): Term = {
    import Term.local.mkBound
    val X: Term = mkBound(Xty,1)
    val Y: Term = mkBound(Yty,2)
    val Z: Term = mkBound(Zty,3)
    import TypedFOLEncodingSignature._
    val combName = sig(comb).name
    deSafeName(combName) match {
      case `combinator_I_name` => Eq(hApp(I(Xty), X), X)
      case `combinator_K_name` => Eq(hApp(K(Yty, Xty, Y), X), Y)
      case `combinator_S_name` => Eq( // `S x y z = x z (y z)`
        hApp(S(Zty, Yty, Xty, mkBound(funTy(Zty, funTy(Yty, Xty)),3), mkBound(funTy(Zty, Yty),2)), mkBound(Zty,1)),
        hApp(mkBound(funTy(Zty, funTy(Yty, Xty)),3),Seq(mkBound(Zty,1), hApp(mkBound(funTy(Zty, Yty),2), mkBound(Zty, 1))))
      )
      case `combinator_B_name` => Eq(
        hApp(B(Zty, Yty, Xty, mkBound(funTy(Yty, Xty), 3), mkBound(funTy(Zty, Yty), 2)), mkBound(Zty,1)),
        hApp(mkBound(funTy(Yty, Xty), 3),hApp(mkBound(funTy(Zty, Yty), 2),mkBound(Zty,1)))
      )
      case `combinator_C_name` => Eq(
        hApp(C(Zty, Yty, Xty, mkBound(funTy(Zty, funTy(Yty, Xty)), 3), mkBound(Yty,2)), mkBound(Zty,1)),
        hApp(mkBound(funTy(Zty, funTy(Yty, Xty)), 3), Seq(mkBound(Zty,1), mkBound(Yty,2)))
      )
      case _ => throw new IllegalArgumentException
    }
  }
}

protected[encoding] class LambdaElim_Turner(sig: TypedFOLEncodingSignature) extends LambdaElimination(sig) {

  override def eliminateLambda(t: Term)(holSignature: Signature): Term = ???

  override def getAuxiliaryDefinitions: Set[Term] = ???
}

protected[encoding] class LambdaElim_LambdaLifting(sig: TypedFOLEncodingSignature) extends LambdaElimination(sig) {

  override def eliminateLambda(t: Term)(holSignature: Signature): Term = ???

  override def getAuxiliaryDefinitions: Set[Term] = ???
}
