package leo.datastructures

import leo.datastructures.term._
import Term.{intToBoundVar, intsToBoundVar, mkApp, mkAtom, mkTermAbs, mkTermApp, Λ}
import Term.{@@@, Symbol, ∙, @@@@}
import Type.{superKind, typeKind, typeVarToType}
import leo.datastructures.impl.Signature

import scala.language.implicitConversions

/** This type can be mixed-in to supply standard higher-order logic symbol definitions, including
 *
 *  1. Fixed (interpreted) symbols
 *  2. Defined symbols
 *  3. Standard base types
 *
 * These symbols must be inserted into the signature before all other symbols and in the order described below.
 *
 * Details:
 * It defines eight fixed symbols ($true, $false, #box, #diamond, ~, !, |, =),
 * eight defined symbols (?, &, =>, <=, <=>, ~|, ~&, <~>) and three types/kinds ($o, $i, *)
 * @author Alexander Steen
 * @since 02.05.2014
 * @note Updated 24.June 2014: Added remaining connectives from TPTP: ~|,~&, <~>
  *                            Added trait for binary/unary connectives
 */
trait HOLSignature {
  ////////////////////////////////
  // Hard wired fixed keys
  ////////////////////////////////
  val oKey = 1
  val o = Type.mkType(oKey)

  val iKey = 2
  val i = Type.mkType(iKey)

  val realKey = 3
  val real = Type.mkType(realKey)

  val ratKey = 4
  val rat = Type.mkType(ratKey)

  val intKey = 5
  val int = Type.mkType(intKey)

  val letKey = HOLSignature.letKey
  val iteKey = HOLSignature.iteKey

  // Don't change the order of the elements in this lists.
  // If you do so, you may need to update the Signature implementation.

  // Built-in types
  val types = List(("$tType", superKind), // Key 0
    ("$o", typeKind), // Key 1
    ("$i", typeKind), // Key 2
    ("$real", typeKind), // key 3
    ("$rat", typeKind), // Key 4
    ("$int", typeKind)) // Key 5

  // Fixed symbols
  import Type.{mkPolyType => forall}
  lazy val fixedConsts = List(("$true", o), // Key 6
    ("$false",                        o), // Key 7
    ("#box",                    o ->: o), // Key 8
    ("#diamond",                o ->: o), // Key 9
    ("~",                       o ->: o), // Key 10
    ("!",        forall((1 ->: o) ->: o)), // Key 11
    ("|",                 o ->: o ->: o), // Key 12
    ("=",        forall(1 ->: 1 ->: o)), // Key 13
    ("$$let",    forall(forall(2 ->: 1 ->: 1))), // Key 14
    ("$$ite",    forall(o ->: 1 ->: 1 ->: 1)),  // Key 15
    ("$less",    forall(1 ->: 1 ->: o)), // Key 16
    ("$lesseq",   forall(1 ->: 1 ->: o)), // Key 17
    ("$greater",  forall(1 ->: 1 ->: o)), // Key 18
    ("$greatereq",forall(1 ->: 1 ->: o)), // Key 19
    ("@+",        forall((1 ->: o) ->: 1)), // Key 20
    ("@-",        forall((1 ->: o) ->: 1)), // Key 21
    ("$uminus",   forall(1 ->: 1)), // Key 22
    ("$sum",      forall(1 ->: 1 ->: 1)), // Key 23
    ("$difference",forall(1 ->: 1 ->: 1)), // Key 24
    ("$product",   forall(1 ->: 1 ->: 1)), // Key 25
    ("$quotient",  forall(1 ->: 1 ->: 1)), // Key 26
    ("$quotient_e",forall(1 ->: 1 ->: 1)), // Key 27
    ("$quotient_t",forall(1 ->: 1 ->: 1)), // key 28
    ("$quotient_f",forall(1 ->: 1 ->: 1)), // Key 29
    ("$remainder_e",forall(1 ->: 1 ->: 1)), // Key 30
    ("$remainder_t",forall(1 ->: 1 ->: 1)), // key 31
    ("$remainder_f",forall(1 ->: 1 ->: 1)), // Key 32
    ("$floor",     forall(1 ->: 1)), // Key 33
    ("$ceiling",   forall(1 ->: 1)), // Key 34
    ("$truncate",  forall(1 ->: 1)), // Key 35
    ("$round",     forall(1 ->: 1)), // Key 36
    ("$to_int",      forall(1 ->: int)), // Key 37
    ("$to_rat",      forall(1 ->: rat)), // Key 38
    ("$to_real",      forall(1 ->: real)), // Key 39
      ("$is_rat",      forall(1 ->: o)), // Key 40
      ("$is_int",      forall(1 ->: o)) // Key 41
  )

  // Standard defined symbols
  lazy val definedConsts = List(("?", existsDef, forall((1 ->: o) ->: o)), // Key 42
    ("&",   andDef,  o ->: o ->: o), // Key 43
    ("=>",  implDef, o ->: o ->: o), // Key 44
    ("<=",  ifDef,   o ->: o ->: o), // Key 45
    ("<=>", iffDef,  o ->: o ->: o), // Key 46
    ("~&", nandDef,  o ->: o ->: o), // Key 47
    ("~|",  norDef,  o ->: o ->: o), // Key 48
    ("<~>",niffDef,  o ->: o ->: o), // Key 49
    ("!=",  neqDef, forall(1 ->: 1 ->: o))) // Key 50


  //////////////////////
  // enum end
  //////////////////////

  // Shorthands for later definitions
  private def not = mkAtom(HOLSignature.notKey)
  private def all = mkAtom(HOLSignature.forallKey)
  private def disj = mkAtom(HOLSignature.orKey)
  private def conj = mkAtom(HOLSignature.andKey)
  private def impl = mkAtom(HOLSignature.implKey)
  private def lpmi = mkAtom(HOLSignature.ifKey)
  private def eq = mkAtom(HOLSignature.eqKey)

  // Definitions for default symbols
  protected def existsDef: Term = Λ(
    mkTermAbs(1 ->: o,
      mkTermApp(not,
        mkTermApp(all,
          mkTermAbs(1,
            mkTermApp(not,
              mkTermApp((2, (1 ->: o)), (1, 1))))))))

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
          mkTermApp(lpmi, (2, o)), (1, o)))))

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
            mkTermApp(eq,
              (2, 1)),
              (1, 1))))))
}

object HOLSignature {
  ////////////////////
  // Systematic enumeration f signature symbols for easier changes later on
  ////////////////////
  private[datastructures] val trueKey = 6
  private[datastructures] val falseKey = trueKey + 1
  private[datastructures] val boxKey = falseKey + 1
  private[datastructures] val diamondKey = boxKey + 1
  private[datastructures] val notKey = diamondKey + 1
  private[datastructures] val forallKey = notKey + 1
  private[datastructures] val orKey = forallKey + 1
  private[datastructures] val eqKey = orKey + 1
  private[datastructures] val letKey = eqKey + 1
  private[datastructures] val iteKey = letKey + 1
  private[datastructures] val lessKey = iteKey + 1
  private[datastructures] val lessEqKey = lessKey + 1
  private[datastructures] val greaterKey =  lessEqKey + 1
  private[datastructures] val greaterEqKey = greaterKey + 1
  private[datastructures] val choiceKey = greaterEqKey + 1
  private[datastructures] val descKey = choiceKey + 1

  private[datastructures] val uminusKey = descKey + 1
  private[datastructures] val sumKey = uminusKey + 1
  private[datastructures] val diffKey = sumKey + 1
  private[datastructures] val prodKey = diffKey + 1
  private[datastructures] val quotKey = prodKey + 1
  private[datastructures] val quotEKey = quotKey + 1
  private[datastructures] val quotTKey = quotEKey + 1
  private[datastructures] val quotFKey = quotTKey + 1
  private[datastructures] val remainderEKey = quotFKey + 1
  private[datastructures] val remainderTKey = remainderEKey + 1
  private[datastructures] val remainderFKey = remainderTKey +1
  private[datastructures] val floorKey = remainderFKey + 1
  private[datastructures] val ceilKey = floorKey + 1
  private[datastructures] val truncateKey = ceilKey +1
  private[datastructures] val roundKey = truncateKey + 1
  private[datastructures] val toIntKey = roundKey + 1
  private[datastructures] val toRatKey = toIntKey +1
  private[datastructures] val toRealKey = toRatKey + 1
  private[datastructures] val isRatKey = toRealKey + 1
  private[datastructures] val isIntKey = isRatKey + 1

  private[datastructures] val existsKey = isIntKey + 1
  private[datastructures] val andKey = existsKey + 1
  private[datastructures] val implKey = andKey + 1
  private[datastructures] val ifKey = implKey + 1
  private[datastructures] val iffKey = ifKey + 1
  private[datastructures] val nandKey = iffKey + 1
  private[datastructures] val norKey = nandKey + 1
  private[datastructures] val niffKey = norKey + 1
  private[datastructures] val neqKey = niffKey + 1

  /** The last id that was used by predefined HOL symbols. Keep up to date!*/
  val lastId = neqKey
}

/** Trait for binary connectives of HOL. They can be used as object representation of defined/fixed symbols. */
trait HOLBinaryConnective extends Function2[Term, Term, Term] {
  protected[HOLBinaryConnective] val key: Signature#Key

  /** Create the term that is constructed by applying two arguments to the binary connective. */
  override def apply(left: Term, right: Term): Term = mkTermApp(mkAtom(key), Seq(left, right))

  def unapply(t: Term): Option[(Term,Term)] = t match {
    case (Symbol(`key`) @@@ t1) @@@ t2 => Some((t1,t2))
    case Symbol(`key`) ∙ Seq(Left(t1), Left(t2)) => Some((t1,t2))
    case _ => None
  }
}
object HOLBinaryConnective {
  /** Return the term corresponding to the connective the object represents */
  implicit def toTerm(conn: HOLBinaryConnective): Term = mkAtom(conn.key)
}

// For polymorphic (e.g. arithmetic) binary symbols, provide traits for apply/unapply standard prenex-polymorphic symbols
trait PolyBinaryConnective extends HOLBinaryConnective {
  override def apply(left: Term, right: Term): Term = mkApp(mkAtom(key), Seq(Right(left.ty), Left(left), Left(right)))

  override def unapply(t: Term): Option[(Term,Term)] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) @@@ t2 => Some((t1,t2))
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1), Left(t2))) => Some((t1, t2))
    case _ => None
  }
}

/** Trait for unary connectives of HOL. They can be used as object representation of defined/fixed symbols. */
trait HOLUnaryConnective extends Function1[Term, Term] {
  protected[HOLUnaryConnective] val key: Signature#Key

  /** Create the term that is constructed by applying an argument to the unary connective. */
  override def apply(arg: Term): Term = mkTermApp(mkAtom(key), arg)

  def unapply(t: Term): Option[Term] = t match {
    case (Symbol(`key`) @@@ t1) => Some(t1)
    case Symbol(`key`) ∙ Seq(Left(t1)) => Some(t1)
    case _ => None
  }
}
object HOLUnaryConnective {
  /** Return the term corresponding to the connective the object represents */
  implicit def toTerm(conn: HOLUnaryConnective): Term = mkAtom(conn.key)
}

// For polymorphic (e.g. arithmetic) unary symbols, provide traits for apply/unapply standard prenex-polymorphic symbols
trait PolyUnaryConnective extends HOLUnaryConnective {
  override def apply(arg: Term): Term = mkApp(mkAtom(key), Seq(Right(arg.ty), Left(arg)))

  override def unapply(t: Term): Option[Term] = t match {
    case (Symbol(`key`) @@@@ _) @@@ t1 => Some(t1)
    case Symbol(`key`) ∙ Seq(Right(_), Left(t1)) => Some(t1)
    case _ => None
  }
}

/** Trait for nullary symbols (constants) within HOL. */
trait HOLConstant extends Function0[Term] {
  protected[HOLConstant] val key: Signature#Key

  /** Create the term that is represented by the object */
  override def apply(): Term = mkAtom(key)

  def unapply(t: Term): Boolean = t match {
    case Symbol(`key`) => true
    case _             => false
  }
}
object HOLConstant {
  /** Return the term corresponding to the connective the object represents */
  implicit def toTerm(c: HOLConstant): Term = mkAtom(c.key)
}

////////////////////////////////////////
// Objects representing HOL connectives
////////////////////////////////////////
import HOLSignature._
/** HOL disjunction */
object ||| extends HOLBinaryConnective  { val key = orKey }
/** HOL equality */
object === extends PolyBinaryConnective  { val key = eqKey }
/** HOL conjunction */
object & extends HOLBinaryConnective    { val key = andKey }
/** HOL implication */
object Impl extends HOLBinaryConnective { val key = implKey }
/** HOL if (reverse implication) */
object <= extends HOLBinaryConnective   { val key = ifKey }
/** HOL iff */
object <=> extends HOLBinaryConnective  { val key = iffKey }
/** HOL negated conjunction */
object ~& extends HOLBinaryConnective   { val key = nandKey }
/** HOL negated disjunction */
object ~||| extends HOLBinaryConnective { val key = norKey }
/** HOL negated iff */
object <~> extends HOLBinaryConnective  { val key = niffKey }
/** HOL negated equality */
object !=== extends PolyBinaryConnective  { val key = neqKey }
/** HOL negation */
object Not extends HOLUnaryConnective    { val key = notKey }
/** HOL forall */
object Forall extends HOLUnaryConnective { val key = forallKey
  override def apply(arg: Term): Term = mkApp(mkAtom(key), Seq(Right(arg.ty._funDomainType), Left(arg)))

  override def unapply(t: Term): Option[Term] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) => Some(t1)
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1))) => Some(t1)
    case _ => None
  }
}
/** HOL exists */
object Exists extends HOLUnaryConnective { val key = existsKey
  override def apply(arg: Term): Term = mkApp(mkAtom(key), Seq(Right(arg.ty._funDomainType), Left(arg)))

  override def unapply(t: Term): Option[Term] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) => Some(t1)
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1))) => Some(t1)
    case _ => None
  }
}

/** HOL choice @+ */
object Choice extends HOLUnaryConnective { val key = choiceKey
  override def apply(arg: Term): Term = mkApp(mkAtom(key), Seq(Right(arg.ty._funDomainType), Left(arg)))

  override def unapply(t: Term): Option[Term] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) => Some(t1)
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1))) => Some(t1)
    case _ => None
  }
}
/** HOL description @- */
object Description extends HOLUnaryConnective { val key = descKey
  override def apply(arg: Term): Term = mkApp(mkAtom(key), Seq(Right(arg.ty._funDomainType), Left(arg)))

  override def unapply(t: Term): Option[Term] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) => Some(t1)
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1))) => Some(t1)
    case _ => None
  }
}

/** HOL frue constant */
object LitTrue extends HOLConstant      { val key = trueKey }
/** HOL false constant */
object LitFalse extends HOLConstant     { val key = falseKey }

///////////////////
// other HOL defined constants
///////////////////

/** $less */
object HOLLess extends PolyBinaryConnective { val key = lessKey }
/** $lesseq */
object HOLLessEq extends PolyBinaryConnective { val key = lessEqKey }
/** $greater */
object HOLGreater extends PolyBinaryConnective { val key = greaterKey }
/** $greatereq */
object HOLGreaterEq extends PolyBinaryConnective { val key = greaterEqKey }
// Further TF with arithmetic constants
/** $uminus | $sum | $difference | $product |
   $quotient | $quotient_e | $quotient_t | $quotient_f |
   $remainder_e | $remainder_t | $remainder_f |
   $floor | $ceiling | $truncate | $round |
   $to_int | $to_rat | $to_real
  */
object HOLUnaryMinus extends PolyUnaryConnective { val key = uminusKey}
object HOLFloor extends PolyUnaryConnective { val key = floorKey}
object HOLCeiling extends PolyUnaryConnective { val key = ceilKey}
object HOLTruncate extends PolyUnaryConnective { val key = truncateKey}
object HOLRound extends PolyUnaryConnective { val key = roundKey}
object HOLToInt extends PolyUnaryConnective { val key = toIntKey}
object HOLToRat extends PolyUnaryConnective { val key = toRatKey}
object HOLToReal extends PolyUnaryConnective { val key = toRealKey}
object HOLIsRat extends PolyUnaryConnective { val key = isRatKey}
object HOLIsInt extends PolyUnaryConnective { val key = isIntKey}
object HOLSum extends PolyBinaryConnective { val key = sumKey}
object HOLDifference extends PolyBinaryConnective { val key = diffKey}
object HOLProduct extends PolyBinaryConnective { val key = prodKey}
object HOLQuotient extends PolyBinaryConnective { val key = quotKey}
object HOLQuotientE extends PolyBinaryConnective { val key = quotEKey}
object HOLQuotientT extends PolyBinaryConnective { val key = quotTKey}
object HOLQuotientF extends PolyBinaryConnective { val key = quotFKey}
object HOLRemainderE extends PolyBinaryConnective { val key = remainderEKey}
object HOLRemainderT extends PolyBinaryConnective { val key = remainderTKey}
object HOLRemainderF extends PolyBinaryConnective { val key = remainderFKey}

/** If-Then-Else combinator */
object IF_THEN_ELSE extends Function3[Term, Term, Term, Term] {
  protected[IF_THEN_ELSE] val key = iteKey

  override def apply(cond: Term, thn: Term, els: Term): Term = mkApp(mkAtom(key), Seq(Right(thn.ty), Left(cond), Left(thn), Left(els)))

  def unapply(t: Term): Option[(Term,Term, Term)] = t match {
    case (((Symbol(`key`) @@@@ _) @@@ t1) @@@ t2) @@@ t3 => Some((t1,t2,t3))
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1), Left(t2), Left(t3))) => Some((t1,t2,t3))
    case _ => None
  }
}

