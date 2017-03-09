package leo.modules

import leo.datastructures.{Term, Type, Signature, Kind}
import Term.{intToBoundVar, intsToBoundVar, mkApp, mkAtom, mkTermAbs, mkTermApp, Λ,Symbol,∙}
import Kind.{superKind, * => typeKind}
import Type.typeVarToType

import scala.language.implicitConversions

/** HOL supplies standard higher-order logic symbol definitions, including
 *
 *  1. Fixed (interpreted) symbols
 *  2. Defined symbols
 *  3. Standard base types
 *
 * These symbols must be inserted into the signature for all HOL reasoning aspects to work.
 *
 * Details:
 * It defines eight fixed symbols ($true, $false, #box, #diamond, ~, !, |, =),
 * eight defined symbols (?, &, =>, <=, <=>, ~|, ~&, <~>) and three types/kinds ($o, $i, *)
 * @author Alexander Steen
 * @since 02.05.2014
 * @note Updated 24.June 2014: Added remaining connectives from TPTP: ~|,~&, <~>
 *                            Added trait for binary/unary connectives
 * @note Oct. 2016: Reworked to object
 */
object HOLSignature {
  ////////////////////
  // Systematic enumeration f signature symbols for easier changes later on
  ////////////////////
  private final val oKey = 1
  private final val iKey = 2
  private final val realKey = 3
  private final val ratKey = 4
  private final val intKey = 5
  private final val trueKey = 6
  private final val falseKey = trueKey + 1
  private final val notKey = falseKey + 1
  private final val forallKey = notKey + 1
  private final val orKey = forallKey + 1
  private final val eqKey = orKey + 1
  private final val letKey = eqKey + 1
  private final val iteKey = letKey + 1
  private final val lessKey = iteKey + 1
  private final val lessEqKey = lessKey + 1
  private final val greaterKey = lessEqKey + 1
  private final val greaterEqKey = greaterKey + 1
  private final val choiceKey = greaterEqKey + 1
  private final val descKey = choiceKey + 1

  private final val uminusKey = descKey + 1
  private final val sumKey = uminusKey + 1
  private final val diffKey = sumKey + 1
  private final val prodKey = diffKey + 1
  private final val quotKey = prodKey + 1
  private final val quotEKey = quotKey + 1
  private final val quotTKey = quotEKey + 1
  private final val quotFKey = quotTKey + 1
  private final val remainderEKey = quotFKey + 1
  private final val remainderTKey = remainderEKey + 1
  private final val remainderFKey = remainderTKey + 1
  private final val floorKey = remainderFKey + 1
  private final val ceilKey = floorKey + 1
  private final val truncateKey = ceilKey + 1
  private final val roundKey = truncateKey + 1
  private final val toIntKey = roundKey + 1
  private final val toRatKey = toIntKey + 1
  private final val toRealKey = toRatKey + 1
  private final val isRatKey = toRealKey + 1
  private final val isIntKey = isRatKey + 1
  // Th1 constants
  private final val tyForallKey = isIntKey + 1

  private final val existsKey = tyForallKey + 1
  private final val andKey = existsKey + 1
  private final val implKey = andKey + 1
  private final val ifKey = implKey + 1
  private final val iffKey = ifKey + 1
  private final val nandKey = iffKey + 1
  private final val norKey = nandKey + 1
  private final val niffKey = norKey + 1
  private final val neqKey = niffKey + 1

  /** The last id that was used by predefined HOL symbols. Keep up to date! */
  val lastId = neqKey

  final val o = Type.mkType(oKey)
  final val i = Type.mkType(iKey)
  final val real = Type.mkType(realKey)
  final val rat = Type.mkType(ratKey)
  final val int = Type.mkType(intKey)

  private final val oo = o ->: o
  private final val ooo = o ->: o ->: o
  import Type.{mkPolyType => forall}
  private final val aao = forall(1 ->: 1 ->: o)
  private final val aoo = forall((1 ->: o) ->: o)
  private final val aa = forall(1 ->: 1)
  private final val aaa = forall(1 ->: 1 ->: 1)
  private final val faoo = forall(o) ->: o

  // Shorthands for later definitions
  private final val not = mkAtom(notKey, Not.ty)
  private final val all = mkAtom(forallKey, Forall.ty)
  private final val disj = mkAtom(orKey, |||.ty)
  private final val conj = mkAtom(andKey, &.ty)
  private final val impl = mkAtom(implKey, Impl.ty)
  private final val lpmi = mkAtom(ifKey, <=.ty)
  private final val eq = mkAtom(eqKey, ===.ty)

  // Definitions for default symbols
  protected def existsDef: Term = Λ(
    mkTermAbs(1 ->: o,
      mkTermApp(not,
        mkTermApp(Term.mkTypeApp(all, 1),
          mkTermAbs(1,
            mkTermApp(not,
              mkTermApp((2, 1 ->: o), (1, 1))))))))

  protected def andDef: Term = mkTermAbs(o,
    mkTermAbs(o,
      mkTermApp(not,
        mkTermApp(
          mkTermApp(disj, mkTermApp(not, (2, o))),
          mkTermApp(not, (1, o))))))

  protected def implDef: Term = mkTermAbs(o,
    mkTermAbs(o,
      mkTermApp(
        mkTermApp(disj, mkTermApp(not, (2, o))),
        (1, o))))

  protected def ifDef: Term = mkTermAbs(o,
    mkTermAbs(o,
      mkTermApp(
        mkTermApp(disj, (2, o)),
        mkTermApp(not, (1, o)))))

  protected def iffDef: Term = mkTermAbs(o,
    mkTermAbs(o,
      mkTermApp(
        mkTermApp(conj, mkTermApp(
          mkTermApp(impl, (2, o)), (1, o))),
        mkTermApp(
          mkTermApp(impl, (1, o)), (2, o)))))

  protected def nandDef: Term = mkTermAbs(o,
    mkTermAbs(o,
      mkTermApp(
        mkTermApp(disj, mkTermApp(not, (2, o))),
        mkTermApp(not, (1, o)))))

  protected def norDef: Term = mkTermAbs(o,
    mkTermAbs(o,
      mkTermApp(not,
        mkTermApp(
          mkTermApp(disj, (2, o)),
          (1, o)))))

  protected def niffDef: Term = mkTermAbs(o,
    mkTermAbs(o,
      mkTermApp(not,
        mkTermApp(
          mkTermApp(conj, mkTermApp(
            mkTermApp(impl, (2, o)), (1, o))),
          mkTermApp(
            mkTermApp(lpmi, (2, o)), (1, o))))))

  protected def neqDef: Term = Λ(
    mkTermAbs(1,
      mkTermAbs(1,
        mkTermApp(not,
          mkTermApp(
            mkTermApp(Term.mkTypeApp(eq, 1),
              (2, 1)),
            (1, 1))))))

  /** Trait for binary connectives of HOL. They can be used as object representation of defined/fixed symbols. */
  trait HOLBinaryConnective extends Function2[Term, Term, Term] {
    val key: Signature#Key
    val ty: Type

    /** Create the term that is constructed by applying two arguments to the binary connective. */
    override def apply(left: Term, right: Term): Term = mkTermApp(mkAtom(key,ty), Vector(left, right))

    def unapply(t: Term): Option[(Term,Term)] = t match {
      case Symbol(`key`) ∙ Seq(Left(t1), Left(t2)) => Some((t1,t2))
      case _ => None
    }
  }
  object HOLBinaryConnective {
    /** Return the term corresponding to the connective the object represents */
    implicit def toTerm(conn: HOLBinaryConnective): Term = mkAtom(conn.key, conn.ty)
  }

  // For polymorphic (e.g. arithmetic) binary symbols, provide traits for apply/unapply standard prenex-polymorphic symbols
  trait PolyBinaryConnective extends HOLBinaryConnective {
    override def apply(left: Term, right: Term): Term = mkApp(mkAtom(key, ty), Vector(Right(left.ty), Left(left), Left(right)))

    override def unapply(t: Term): Option[(Term,Term)] = t match {
      case (Symbol(`key`) ∙ Seq(Right(_), Left(t1), Left(t2))) => Some((t1, t2))
      case _ => None
    }
  }

  /** Trait for unary connectives of HOL. They can be used as object representation of defined/fixed symbols. */
  trait HOLUnaryConnective extends Function1[Term, Term] {
    val key: Signature#Key
    val ty: Type

    /** Create the term that is constructed by applying an argument to the unary connective. */
    override def apply(arg: Term): Term = mkTermApp(mkAtom(key,ty), arg)

    def unapply(t: Term): Option[Term] = t match {
      case Symbol(`key`) ∙ Seq(Left(t1)) => Some(t1)
      case _ => None
    }
  }
  object HOLUnaryConnective {
    /** Return the term corresponding to the connective the object represents */
    implicit def toTerm(conn: HOLUnaryConnective): Term = mkAtom(conn.key, conn.ty)
  }

  // For polymorphic (e.g. arithmetic) unary symbols, provide traits for apply/unapply standard prenex-polymorphic symbols
  trait PolyUnaryConnective extends HOLUnaryConnective {
    override def apply(arg: Term): Term = mkApp(mkAtom(key,ty), Vector(Right(arg.ty), Left(arg)))

    override def unapply(t: Term): Option[Term] = t match {
      case Symbol(`key`) ∙ Seq(Right(_), Left(t1)) => Some(t1)
      case _ => None
    }
  }

  /** Trait for nullary symbols (constants) within HOL. */
  trait HOLConstant extends Function0[Term] {
    val key: Signature#Key
    val ty: Type

    /** Create the term that is represented by the object */
    override def apply(): Term = mkAtom(key,ty)

    def unapply(t: Term): Boolean = t match {
      case Symbol(`key`) => true
      case _             => false
    }
  }
  object HOLConstant {
    /** Return the term corresponding to the connective the object represents */
    implicit def toTerm(c: HOLConstant): Term = mkAtom(c.key, c.ty)
  }

  ////////////////////////////////////////
  // Objects representing HOL connectives
  ////////////////////////////////////////


  /** HOL disjunction */
  object ||| extends HOLBinaryConnective  { val key = orKey; val ty = ooo }
  /** HOL equality */
  object === extends PolyBinaryConnective  { val key = eqKey; val ty = aao }
  /** HOL conjunction */
  object & extends HOLBinaryConnective    { val key = andKey; val ty = ooo }
  /** HOL implication */
  object Impl extends HOLBinaryConnective { val key = implKey; val ty = ooo }
  /** HOL if (reverse implication) */
  object <= extends HOLBinaryConnective   { val key = ifKey; val ty = ooo }
  /** HOL iff */
  object <=> extends HOLBinaryConnective  { val key = iffKey; val ty = ooo }
  /** HOL negated conjunction */
  object ~& extends HOLBinaryConnective   { val key = nandKey; val ty = ooo }
  /** HOL negated disjunction */
  object ~||| extends HOLBinaryConnective { val key = norKey; val ty = ooo }
  /** HOL negated iff */
  object <~> extends HOLBinaryConnective  { val key = niffKey; val ty = ooo }
  /** HOL negated equality */
  object !=== extends PolyBinaryConnective  { val key = neqKey; val ty = aao }
  /** HOL negation */
  object Not extends HOLUnaryConnective    { val key = notKey; val ty = oo }
  /** HOL forall */
  object Forall extends HOLUnaryConnective { val key = forallKey; val ty = aoo
    override def apply(arg: Term): Term = mkApp(mkAtom(key,ty), Vector(Right(arg.ty._funDomainType), Left(arg)))

    override def unapply(t: Term): Option[Term] = t match {
      case (Symbol(`key`) ∙ Seq(Right(_), Left(t1))) => Some(t1)
      case _ => None
    }
  }
  /** HOL exists */
  object Exists extends HOLUnaryConnective { val key = existsKey; val ty = aoo
    override def apply(arg: Term): Term = mkApp(mkAtom(key,ty), Vector(Right(arg.ty._funDomainType), Left(arg)))

    override def unapply(t: Term): Option[Term] = t match {
      case (Symbol(`key`) ∙ Seq(Right(_), Left(t1))) => Some(t1)
      case _ => None
    }
  }

  /** HOL choice @+ */
  object Choice extends HOLUnaryConnective { val key = choiceKey; val ty = forall((1 ->: o) ->: 1)
    override def apply(arg: Term): Term = mkApp(mkAtom(key,ty), Vector(Right(arg.ty._funDomainType), Left(arg)))

    override def unapply(t: Term): Option[Term] = t match {
      case (Symbol(`key`) ∙ Seq(Right(_), Left(t1))) => Some(t1)
      case _ => None
    }
  }
  /** HOL description @- */
  object Description extends HOLUnaryConnective { val key = descKey; val ty = forall((1 ->: o) ->: 1)
    override def apply(arg: Term): Term = mkApp(mkAtom(key,ty), Vector(Right(arg.ty._funDomainType), Left(arg)))

    override def unapply(t: Term): Option[Term] = t match {
      case (Symbol(`key`) ∙ Seq(Right(_), Left(t1))) => Some(t1)
      case _ => None
    }
  }

  /** HOL frue constant */
  object LitTrue extends HOLConstant      { val key = trueKey; val ty = o }
  /** HOL false constant */
  object LitFalse extends HOLConstant     { val key = falseKey; val ty = o }

  ///////////////////
  // other HOL defined constants
  ///////////////////

  /** $less */
  object HOLLess extends PolyBinaryConnective { val key = lessKey; val ty = aao }
  /** $lesseq */
  object HOLLessEq extends PolyBinaryConnective { val key = lessEqKey; val ty = aao }
  /** $greater */
  object HOLGreater extends PolyBinaryConnective { val key = greaterKey; val ty = aao }
  /** $greatereq */
  object HOLGreaterEq extends PolyBinaryConnective { val key = greaterEqKey; val ty = aao }
  // Further TF with arithmetic constants
  /** $uminus | $sum | $difference | $product |
     $quotient | $quotient_e | $quotient_t | $quotient_f |
     $remainder_e | $remainder_t | $remainder_f |
     $floor | $ceiling | $truncate | $round |
     $to_int | $to_rat | $to_real
    */
  object HOLUnaryMinus extends PolyUnaryConnective { val key = uminusKey; val ty = aa}
  object HOLFloor extends PolyUnaryConnective { val key = floorKey; val ty = aa}
  object HOLCeiling extends PolyUnaryConnective { val key = ceilKey; val ty = aa}
  object HOLTruncate extends PolyUnaryConnective { val key = truncateKey; val ty = aa}
  object HOLRound extends PolyUnaryConnective { val key = roundKey; val ty = aa}
  object HOLToInt extends PolyUnaryConnective { val key = toIntKey; val ty = forall(1 ->: int)}
  object HOLToRat extends PolyUnaryConnective { val key = toRatKey; val ty = forall(1 ->: rat)}
  object HOLToReal extends PolyUnaryConnective { val key = toRealKey; val ty = forall(1 ->: real)}
  object HOLIsRat extends PolyUnaryConnective { val key = isRatKey; val ty = forall(1 ->: o)}
  object HOLIsInt extends PolyUnaryConnective { val key = isIntKey; val ty = forall(1 ->: o)}
  object HOLSum extends PolyBinaryConnective { val key = sumKey; val ty = aaa}
  object HOLDifference extends PolyBinaryConnective { val key = diffKey; val ty = aaa}
  object HOLProduct extends PolyBinaryConnective { val key = prodKey; val ty = aaa}
  object HOLQuotient extends PolyBinaryConnective { val key = quotKey; val ty = aaa}
  object HOLQuotientE extends PolyBinaryConnective { val key = quotEKey; val ty = aaa}
  object HOLQuotientT extends PolyBinaryConnective { val key = quotTKey; val ty = aaa}
  object HOLQuotientF extends PolyBinaryConnective { val key = quotFKey; val ty = aaa}
  object HOLRemainderE extends PolyBinaryConnective { val key = remainderEKey; val ty = aaa}
  object HOLRemainderT extends PolyBinaryConnective { val key = remainderTKey; val ty = aaa}
  object HOLRemainderF extends PolyBinaryConnective { val key = remainderFKey; val ty = aaa}

  /** If-Then-Else combinator */
  object IF_THEN_ELSE extends Function3[Term, Term, Term, Term] {
    val key = iteKey
    val ty = forall(o ->: 1 ->: 1 ->: 1)

    override def apply(cond: Term, thn: Term, els: Term): Term = mkApp(mkAtom(key,ty), Vector(Right(thn.ty), Left(cond), Left(thn), Left(els)))

    def unapply(t: Term): Option[(Term,Term, Term)] = t match {
      case (Symbol(`key`) ∙ Seq(Right(_), Left(t1), Left(t2), Left(t3))) => Some((t1,t2,t3))
      case _ => None
    }
  }

  ///////////////////
  // TH1 HOL constants
  ///////////////////
  /** HOL forall */
  object TyForall extends HOLUnaryConnective { val key = tyForallKey; val ty = faoo
    override def apply(arg: Term): Term = mkTermApp(mkAtom(key,ty), arg)

    override def unapply(t: Term): Option[Term] = t match {
      case Symbol(`key`) ∙ Seq(Left(body)) => Some(body)
      case _ => None
    }
  }

  ///////////////////
  // collecting all objects in lists
  ///////////////////

  import Signature.{lexStatus, multStatus}

  val multProp = multStatus * Signature.PropStatus
  val lexProp = lexStatus * Signature.PropStatus

  // Built-in types
  final val types = List(("$tType", superKind),
    ("$o", typeKind),
    ("$i", typeKind),
    ("$real", typeKind),
    ("$rat", typeKind),
    ("$int", typeKind))

  // Fixed symbols
  import Signature.{PropAC => ac, PropCommutative => c}

  lazy val fixedConsts = List(
    ("$true", LitTrue.ty, multProp),
    ("$false", LitFalse.ty, multProp),
    ("~", Not.ty, multProp),
    ("!", Forall.ty, multProp),
    ("|", |||.ty, multProp | ac),
    ("=", ===.ty, multProp | c),
    ("$$let", forall(forall(2 ->: 1 ->: 1)), multProp),
    ("$$ite", IF_THEN_ELSE.ty, multProp),
    ("$less", HOLLess.ty, lexProp),
    ("$lesseq", HOLLessEq.ty, lexProp),
    ("$greater", HOLGreater.ty, lexProp),
    ("$greatereq", HOLGreaterEq.ty, lexProp),
    ("@+", forall((1 ->: o) ->: 1), multProp),
    ("@-", forall((1 ->: o) ->: 1), multProp),
    ("$uminus", HOLUnaryMinus.ty, multProp),
    ("$sum", HOLSum.ty, multProp | ac),
    ("$difference", HOLDifference.ty, lexProp),
    ("$product", HOLProduct.ty, multProp | ac),
    ("$quotient", HOLQuotient.ty, lexProp),
    ("$quotient_e", HOLQuotientE.ty, lexProp),
    ("$quotient_t", HOLQuotientT.ty, lexProp),
    ("$quotient_f", HOLQuotientF.ty, lexProp),
    ("$remainder_e", HOLRemainderE.ty, lexProp),
    ("$remainder_t", HOLRemainderT.ty, lexProp),
    ("$remainder_f", HOLRemainderF.ty, lexProp),
    ("$floor", HOLFloor.ty, multProp),
    ("$ceiling", HOLCeiling.ty, multProp),
    ("$truncate", HOLTruncate.ty, multProp),
    ("$round", HOLRound.ty, multProp),
    ("$to_int", HOLToInt.ty, multProp),
    ("$to_rat", HOLToRat.ty, multProp),
    ("$to_real", HOLToReal.ty, multProp),
    ("$is_rat", HOLIsInt.ty, multProp),
    ("$is_int", HOLIsRat.ty, multProp),
    ("!>", TyForall.ty, multProp)
  )

  // Standard defined symbols
  lazy val definedConsts = List(
    ("?", existsDef, Exists.ty, multProp),
    ("&", andDef, &.ty, multProp | ac),
    ("=>", implDef, Impl.ty, lexProp),
    ("<=", ifDef, <=.ty, lexProp),
    ("<=>", iffDef, <=>.ty, multProp | ac),
    ("~&", nandDef, ~&.ty, multProp),
    ("~|", norDef, ~|||.ty, multProp),
    ("<~>", niffDef, <~>.ty, multProp),
    ("!=", neqDef, !===.ty, multProp))


  //////////////////////
  // enum end
  //////////////////////
}
