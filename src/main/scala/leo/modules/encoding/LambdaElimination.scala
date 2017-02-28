package leo.modules.encoding

import leo.datastructures.{Signature, Term}


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
  import leo.datastructures.Term.{λ, mkBound}
  import leo.datastructures.Type
  import leo.datastructures.Type.∀

  private var usedSymbols: Set[Signature#Key] = Set.empty


  final private val Xty: Type = Type.mkVarType(1)
  final private val Yty: Type = Type.mkVarType(2)
  final private val Zty: Type = Type.mkVarType(3)
  /** Curry I combinator given by `I(x) = x`. */
  final lazy val combinator_I: Term = {
    val id = sig.addUninterpreted("$$comb_I", ∀(sig.funTy(Xty,Xty)))
    usedSymbols += id
    Term.mkAtom(id)(sig)
  }
  final def I(ty: Type): Term = Term.mkTypeApp(combinator_I, ty)
  /** Curry K combinator given by `K x y = x`.
    * `K :: ∀x.∀y. y -> $$fun(x,y)` */
  final lazy val combinator_K: Term = {
    val id = sig.addUninterpreted("$$comb_K", ∀(∀(Xty ->: sig.funTy(Yty, Xty))))
    usedSymbols += id
    Term.mkAtom(id)(sig)
  }
  final def K(ty1: Type, ty2: Type, arg: Term): Term = Term.mkApp(combinator_K, Seq(Right(ty1), Right(ty2), Left(arg)))
  /** Curry S combinator given by `S x y z = x z (y z)`. */
  final lazy val combinator_S: Term = {
    val id = sig.addUninterpreted("$$comb_S", ???)
    usedSymbols += id
    Term.mkAtom(id)(sig)
  }

  /** Curry B combinator given by `B x y z = x (y z)`. */
  final lazy val combinator_B: Term = {
    val id = sig.addUninterpreted("$$comb_B", ∀(∀(∀(sig.funTy(Xty, Yty) ->: sig.funTy(Zty, Xty) ->: sig.funTy(Zty, Yty)))))
    usedSymbols += id
    Term.mkAtom(id)(sig)
  }
  final def B(ty1: Type, ty2: Type, ty3: Type, arg1: Term, arg2: Term): Term = Term.mkApp(combinator_B,
    Seq(Right(ty1), Right(ty2), Right(ty3), Left(arg1), Left(arg2)))

  /** Curry C combinator given by `C x y z = x z y`. */
  final lazy val combinator_C: Term = {
    val id = sig.addUninterpreted("$$comb_C", ∀(Type.mkVarType(1) ->: Type.mkVarType(1)))
    usedSymbols += id
    Term.mkAtom(id)(sig)
  }

  override def eliminateLambda(t: Term)(holSignature: Signature): Term = {
    import leo.datastructures.Term._
    import leo.modules.encoding.TypedFOLEncoding.{translateTerm, foTransformType0}
    t match {
      case ty :::> body => body match {
          // Identity function
        case Bound(`ty`,1) => I(foTransformType0(ty, true)(holSignature, sig))
          // K
        case body0 if !body0.looseBounds.contains(1) =>
          val translatedTy = foTransformType0(ty, true)(holSignature, sig)
          val translatedBody = translateTerm(body0.lift(-1), this)(holSignature, sig)
          K(translatedTy, translatedBody.ty, eliminateLambda(translatedBody)(holSignature))
        case f ∙ args =>
          val lastArg = args.last

            if (lastArg.isLeft) {
              val lastTermArg = lastArg.left.get
              val allButLastArg = Term.mkApp(f, args.init)
              if (!allButLastArg.looseBounds.contains(1)) {
                // B
                // etacontract cannot happen since we are eta contracted
                assert(lastTermArg.ty == allButLastArg.ty._funDomainType)
                val translatedTy = foTransformType0(ty, true)(holSignature, sig)
                val translatedAllButLastArgCoDomainType = foTransformType0(allButLastArg.ty.codomainType, true)(holSignature, sig)
                val translatedAllButLastArgDomainType = foTransformType0(allButLastArg.ty._funDomainType, true)(holSignature, sig)
                val translatedAllButLastArg = translateTerm(allButLastArg.lift(-1), this)(holSignature, sig)
                val eliminatedAllButLastArg = eliminateLambda(translatedAllButLastArg)(holSignature)
                val translatedLastTermArg = translateTerm(lastTermArg, this)(holSignature, sig)
                val eliminatedLastTermArg = eliminateLambda(λ(translatedTy)(translatedLastTermArg))(holSignature)
                B(translatedTy, translatedAllButLastArgCoDomainType, translatedAllButLastArgDomainType, eliminatedAllButLastArg, λ(translatedTy)(eliminatedLastTermArg))
              } else if (!lastTermArg.looseBounds.contains(1)) {
                // C
                ???
              } else {
                // S
                ???
              }
            } else ???

         // No application, recurse and eliminate lambda on result
        case abs@(_ :::> _) => eliminateLambda(λ(ty)(eliminateLambda(abs)(holSignature)))(holSignature)
        case TypeLambda(_) => throw new IllegalArgumentException
        case _ => assert(false); throw new IllegalArgumentException

      }
      case _ => t
    }
  }

  override def getAuxiliaryDefinitions: Set[Term] = ???
}

protected[encoding] class LambdaElim_Turner(sig: TypedFOLEncodingSignature) extends LambdaElimination(sig) {

  override def eliminateLambda(t: Term)(holSignature: Signature): Term = ???

  override def getAuxiliaryDefinitions: Set[Term] = ???
}

protected[encoding] class LambdaElim_LambdaLifting(sig: TypedFOLEncodingSignature) extends LambdaElimination(sig) {

  override def eliminateLambda(t: Term)(holSignature: Signature): Term = ???

  override def getAuxiliaryDefinitions: Set[Term] = ???
}
