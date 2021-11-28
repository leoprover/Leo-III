package leo.modules.procedures

import leo.datastructures.{Term, Type, Rat, Real}
import leo.datastructures.Term.local._

import scala.annotation.{switch, tailrec}

/**
  * Applies simplification transformations to formulas/terms.
  * The simplification result is given by the exhaustive (recursive) application of
  * the following rules (where Π denotes universal type quantification):
  *
  *   - `s \/ s -> s`
  *   - `~s \/ s -> T`
  *   - `s \/ T -> T`
  *   - `s \/ F -> s`
  *   - `s /\ s -> s`
  *   - `~s /\ s -> F`
  *   - `s /\ T -> s`
  *   - `s /\ F -> F`
  *   - `~ ~s -> s`
  *   - `~F -> T`
  *   - `~T -> F`
  *   - `t = t -> T`
  *   - `s = T -> s` *
  *   - `s = F -> ~s` *
  *   - `t != t -> F`
  *   - `s != T -> ~s` *
  *   - `s != F -> s` *
  *   - `∀x. s -> s` if `x` not free in `s`
  *   - `∃x. s -> s` if `x` not free in `s`
  *   - `Πx. s -> s` if `x` is not free in `s`
  *   - `n/m -> n'/m'` where `n/m` is a rational number and `n'/m'` is its canonical rational representation
  *   - `(w,d,e) -> (w',d',e')` where `r = (w,d,e)` is a real number and `r' = (w',d',e')` is its canonical representation
  *   - `$difference(x,y) -> $sum(x,$uminus(y))` where `x` and `y` are arbitrary terms
  *   - `$greatereq(x,y) -> $less(y,x) \/ x = y`
  *   - `$greater(x,y) -> $less(y,x)`
  *   - `$lesseq(x,y) -> $less(x,y) \/ x = y`
  *
  * The four cases marked with (*) are only applied if simplifying extensionally, cf. [[Simplification.apply]].
  *
  * @author Alexander Steen
  */
object Simplification extends Function1[Term, Term] {

  /**
    * Applies simplification to `term` using the transformation rules given in
    * the description of [[Simplification]].
    *
    * @param term The term to be simplified
    * @param extensional If set to `true`, the term will be simplified using the additional four extensional simplification rules.
    *
    * @return The term that is created by exhaustively applying all the rewriting rules given in [[Simplification]].
    */
  final def apply(term: Term, extensional: Boolean): Term = {
    val term0 = term.betaNormalize
    apply0(term0, extensional).betaNormalize
  }

  /**
    * Applies simplification to `term` using the transformation rules given in
    * the description of [[Simplification]], including the extensional ones.
    *
    * @param term The term to be simplified
    * @return The term that is created by exhaustively applying all the rewriting rules given in [[Simplification]].
    */
  final def apply(term: Term): Term = apply(term, extensional = true)

  // TODO: Check if the four simplifications are really "extensional" and not just straight-forward
  // Boolean (with equality) identities
  private[this] final def apply0(term: Term, extensional: Boolean): Term = {
    import leo.datastructures.Term.{:::>, TypeLambda, Bound, Symbol, ∙, Rational, Real}
    import leo.modules.HOLSignature.{Exists, Forall, TyForall, &, |||, LitTrue, LitFalse, ===, !===, Not, Impl, <=>,
      HOLDifference, HOLUnaryMinus, HOLSum, HOLLess, HOLLessEq, HOLGreaterEq, HOLGreater}

    @inline def simpTermOrType(arg: Either[Term, Type]): Either[Term, Type] = arg match {
      case Left(arg0) => Left(apply0(arg0, extensional))
      case Right(arg0) => Right(arg0)
    }

    term match {
      case Bound(_, _) => term
      case Symbol(_) => term
      case ty :::> body => mkTermAbs(ty, apply0(body, extensional))
      case TypeLambda(body) => mkTypeAbs(apply0(body, extensional))
      case Rational(n, d) => (mkRational _).tupled(normalizeRat(n, d))
      case Real(w,d,e) => (mkReal _).tupled(normalizeReal(w,d,e))
      case f ∙ args if f.isConstant && args.length <= 3 =>
        (f: @unchecked) match {
          case Symbol(id) =>
            (id: @switch) match {
              case |||.key =>
                val (left,right) = |||.unapply(term).get
                val simpLeft = apply0(left, extensional)
                val simpRight = apply0(right, extensional)
                (simpLeft, simpRight) match {
                  // - `s \/ s -> s`
                  case (l, r) if l == r       => l
                  // - `~s \/ s -> T`
                  case (l, Not(r)) if l == r  => LitTrue
                  case (Not(l), r) if l == r  => LitTrue
                  // - `s \/ T -> T`
                  case (_, LitTrue())         => LitTrue
                  case (LitTrue(), _)         => LitTrue
                  // - `s \/ F -> s`
                  case (l, LitFalse())        => l
                  case (LitFalse(), r)        => r
                  case (l, r)                 => mkTermApp(f, Seq(l,r))
                }
              case &.key =>
                val (left,right) = &.unapply(term).get
                val simpLeft = apply0(left, extensional)
                val simpRight = apply0(right, extensional)
                (simpLeft, simpRight) match {
                  // - `s /\ s -> s`
                  case (l, r) if l == r       => l
                  //  - `~s /\ s -> F`
                  case (l, Not(r)) if l == r  => LitFalse
                  case (Not(l), r) if l == r  => LitFalse
                  // - `s /\ T -> s`
                  case (l, LitTrue())         => l
                  case (LitTrue(), r)         => r
                  // - `s /\ F -> F`
                  case (_, LitFalse())        => LitFalse
                  case (LitFalse(), _)        => LitFalse
                  case (l, r)                 => mkTermApp(f, Seq(l,r))
                }
              case Impl.key =>
                val (left,right) = Impl.unapply(term).get
                val simpLeft = apply0(left, extensional)
                val simpRight = apply0(right, extensional)
                (simpLeft, simpRight) match {
                  case (_, LitTrue())   => LitTrue
                  case (LitFalse(), _)  => LitTrue
                  case (LitTrue(), r) =>  r
                  case (_, LitFalse()) =>
                    val intermediate = mkTermApp(mkAtom(Not.key, Not.ty), simpLeft)
                    apply0(intermediate, extensional)
                  case (l, r) if l == r => LitTrue()
                  case (l, r)           => mkTermApp(f, Seq(l,r))
                }
              case <=>.key =>
                val (left,right) = <=>.unapply(term).get
                val simpLeft = apply0(left, extensional)
                val simpRight = apply0(right, extensional)
                (simpLeft, simpRight) match {
                  case (l, LitTrue())   => l
                  case (LitTrue(), r)   =>  r
                  case (LitFalse(), _)  =>
                    val intermediate = mkTermApp(mkAtom(Not.key, Not.ty), simpRight)
                    apply0(intermediate, extensional)
                  case (_, LitFalse())  =>
                    val intermediate = mkTermApp(mkAtom(Not.key, Not.ty), simpLeft)
                    apply0(intermediate, extensional)
                  case (l, r) if l == r => LitTrue()
                  case (l, r)           => mkTermApp(f, Seq(l,r))
                }
              case Not.key =>
                val body = Not.unapply(term).get
                val simpBody = apply0(body, extensional)
                simpBody match {
                  // - `~T -> F`
                  case LitTrue()  => LitFalse
                  // - `~F -> T`
                  case LitFalse() => LitTrue
                  // - `~ ~s -> s`
                  case Not(body0) => body0
                  case _          => mkTermApp(f, simpBody)
                }
              case ===.key =>
                val (left,right) = ===.unapply(term).get
                val simpLeft = apply0(left, extensional)
                val simpRight = apply0(right, extensional)
                if (extensional) {
                  (simpLeft, simpRight) match {
                    // - `s = T -> s`
                    case (_, LitTrue()) => simpLeft
                    case (LitTrue(), _) => simpRight
                    // - `s = F -> ~s`
                    case (_, LitFalse()) =>
                      val intermediate = mkTermApp(mkAtom(Not.key, Not.ty), simpLeft)
                      apply0(intermediate, extensional)
                    case (LitFalse(), _) =>
                      val intermediate = mkTermApp(mkAtom(Not.key, Not.ty), simpRight)
                      apply0(intermediate, extensional)
                    // - `t = t -> T`
                    case (l, r) if l == r => LitTrue
                    case (l, r) => mkApp(f, Seq(Right(l.ty), Left(l), Left(r)))
                  }
                } else {
                  (simpLeft, simpRight) match {
                    // - `t = t -> T`
                    case (l, r) if l == r => LitTrue
                    case (l, r) => mkApp(f, Seq(Right(l.ty), Left(l), Left(r)))
                  }
                }
              case !===.key =>
                val (left,right) = !===.unapply(term).get
                val simpLeft = apply0(left, extensional)
                val simpRight = apply0(right, extensional)
                if (extensional) {
                  (simpLeft, simpRight) match {
                    // - `s != F -> s`
                    case (_, LitFalse()) => simpRight
                    case (LitFalse(), _) => simpLeft
                    // - `s != T -> ~s`
                    case (_, LitTrue()) =>
                      val intermediate = mkTermApp(mkAtom(Not.key, Not.ty), simpLeft)
                      apply0(intermediate, extensional)
                    case (LitTrue(), _) =>
                      val intermediate = mkTermApp(mkAtom(Not.key, Not.ty), simpRight)
                      apply0(intermediate, extensional)
                    // - `t != t -> F`
                    case (l, r) if l == r => LitFalse
                    case (l, r) => mkApp(f, Seq(Right(l.ty), Left(l), Left(r)))
                  }
                } else {
                  (simpLeft, simpRight) match {
                    // - `t != t -> F`
                    case (l, r) if l == r => LitFalse
                    case (l, r) => mkApp(f, Seq(Right(l.ty), Left(l), Left(r)))
                  }
                }
              case Forall.key =>
                val body = Forall.unapply(term).get
                val simpBody = apply0(body, extensional)
                simpBody match {
                  // - ∀x. s -> s if x not free in s
                  // - ∃x. s -> s if x not free in s
                  case _ :::> absBody if !absBody.looseBounds.contains(1) => absBody.lift(-1)
                  case _ => mkApp(f, Seq(Right(simpBody.ty._funDomainType), Left(simpBody)))
                }
              case Exists.key =>
                val body = Exists.unapply(term).get
                val simpBody = apply0(body, extensional)
                simpBody match {
                  // - ∀x. s -> s if x not free in s
                  // - ∃x. s -> s if x not free in s
                  case _ :::> absBody if !absBody.looseBounds.contains(1) => absBody.lift(-1)
                  case _ => mkApp(f, Seq(Right(simpBody.ty._funDomainType), Left(simpBody)))
                }
              case TyForall.key =>
                val body = TyForall.unapply(term).get
                val simpBody = apply0(body, extensional)
                simpBody match {
                  // - Πx. s -> s if x is not free in s
                  case TypeLambda(absBody) if !absBody.tyFV.contains(1) => absBody.lift(0, -1)
                  case _ => mkTermApp(f, simpBody)
                }
              case HOLDifference.key =>
                val (left, right) = HOLDifference.unapply(term).get
                val simpLeft = apply0(left, extensional)
                val simpRight = apply0(right, extensional)
                mkTermApp(mkTypeApp(HOLSum, simpLeft.ty), Seq(simpLeft, mkTermApp(mkTypeApp(HOLUnaryMinus, simpRight.ty), simpRight)))
              case HOLLessEq.key =>
                val (left, right) = HOLLessEq.unapply(term).get
                val simpLeft = apply0(left, extensional)
                val simpRight = apply0(right, extensional)
                mkTermApp(mkAtom(|||.key, |||.ty), Seq(mkTermApp(mkTypeApp(HOLLess, simpLeft.ty), Seq(simpLeft, simpRight)), ===(simpLeft, simpRight)))
              case HOLGreater.key =>
                val (left, right) = HOLGreater.unapply(term).get
                val simpLeft = apply0(left, extensional)
                val simpRight = apply0(right, extensional)
                mkTermApp(mkTypeApp(HOLLess, simpLeft.ty), Seq(simpRight, simpLeft))
              case HOLGreaterEq.key =>
                val (left, right) = HOLGreaterEq.unapply(term).get
                val simpLeft = apply0(left, extensional)
                val simpRight = apply0(right, extensional)
                mkTermApp(mkAtom(|||.key, |||.ty), Seq(mkTermApp(mkTypeApp(HOLLess, simpLeft.ty), Seq(simpRight, simpLeft)), ===(simpLeft, simpRight)))
              case _ => mkApp(f, args.map(simpTermOrType))
            }
        }
      case f ∙ args =>
        // f is a variable or a constant because `term` is in beta nf.
        mkApp(f, args.map(simpTermOrType))
    }
  }

  @tailrec private[this] final def gcd(a: BigInt, b: BigInt): BigInt = if (b == 0) a.abs else gcd(b, a % b)
  final def normalizeRat(n: BigInt, d: BigInt): Rat = {
    val sign: BigInt = d.sign
    val greatestCommonDivisor: BigInt = gcd(n ,d).abs * sign
    (n / greatestCommonDivisor, d / greatestCommonDivisor)
  }
  final def normalizeReal(wholePart: BigInt, decimalPlaces: BigInt, exponent: BigInt): Real = { // TODO
    //    val decimalPlacesWithoutTrailingZeroes = if (decimalPlaces != 0) decimalPlaces.toString.reverse.dropWhile(_ == '0').reverse.toInt else 0
    //    val decimalPlacesWithoutTrailingZeroesLength = decimalPlacesWithoutTrailingZeroes.toString.length
    //    val wholePartAsString = wholePart.toString
    //    if (wholePartAsString.length > 3) {
    //      val (newWholePart, newRest) = wholePartAsString.splitAt(3)
    //      val newDecimalPlaces = decimalPlaces.toString.prependedAll(newRest)
    //    }
    (wholePart, decimalPlaces, exponent)
  }
}
