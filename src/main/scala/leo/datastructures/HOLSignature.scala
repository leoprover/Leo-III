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

  val letKey = 14
  val iteKey = 15

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
    ("$greatereq",forall(1 ->: 1 ->: o))) // Key 19

  // Standard defined symbols
  lazy val definedConsts = List(("?", existsDef, forall((1 ->: o) ->: o)), // Key 20
    ("&",   andDef,  o ->: o ->: o), // Key 21
    ("=>",  implDef, o ->: o ->: o), // Key 22
    ("<=",  ifDef,   o ->: o ->: o), // Key 23
    ("<=>", iffDef,  o ->: o ->: o), // Key 24
    ("~&", nandDef,  o ->: o ->: o), // Key 25
    ("~|",  norDef,  o ->: o ->: o), // Key 26
    ("<~>",niffDef,  o ->: o ->: o), // Key 27
    ("!=",  neqDef, forall(1 ->: 1 ->: o))) // Key 28

  /** The last id that was used by predefined HOL symbols. Keep up to date!*/
  val lastId = 28

  // Shorthands for later definitions
  private def not = mkAtom(10)
  private def all = mkAtom(11)
  private def disj = mkAtom(12)
  private def conj = mkAtom(21)
  private def impl = mkAtom(22)
  private def lpmi = mkAtom(23)
  private def eq = mkAtom(13)

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

/** HOL disjunction */
object ||| extends HOLBinaryConnective  { val key = 12 }
/** HOL equality */
object === extends HOLBinaryConnective  { val key = 13
  override def apply(left: Term, right: Term) = {

    mkApp(mkAtom(key), Seq(Right(left.ty), Left(left), Left(right)))
//    lazy val instantiated = mkTypeApp(mkAtom(key), left.ty)
//    mkTermApp(mkTermApp(instantiated, left), right)
  }

  override def unapply(t: Term): Option[(Term, Term)] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) @@@ t2 => Some((t1,t2))
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1), Left(t2))) => Some((t1, t2))
    case _ => None
  }
}
/** HOL conjunction */
object & extends HOLBinaryConnective    { val key = 21 }
/** HOL implication */
object Impl extends HOLBinaryConnective { val key = 22 }
/** HOL if (reverse implication) */
object <= extends HOLBinaryConnective   { val key = 23 }
/** HOL iff */
object <=> extends HOLBinaryConnective  { val key = 24 }
/** HOL negated conjunction */
object ~& extends HOLBinaryConnective   { val key = 25 }
/** HOL negated disjunction */
object ~||| extends HOLBinaryConnective { val key = 26 }
/** HOL negated iff */
object <~> extends HOLBinaryConnective  { val key = 27 }
/** HOL negated equality */
object !=== extends HOLBinaryConnective  { val key = 28
  override def apply(left: Term, right: Term) = {
    mkApp(mkAtom(key), Seq(Right(left.ty), Left(left), Left(right)))
//    lazy val instantiated = mkTypeApp(mkAtom(key), left.ty)
//    mkTermApp(mkTermApp(instantiated, left), right)
  }

  override def unapply(t: Term): Option[(Term,Term)] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) @@@ t2 => Some((t1,t2))
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1), Left(t2))) => Some((t1, t2))
    case _ => None
  }
}

/** HOL negation */
object Not extends HOLUnaryConnective    { val key = 10 }
/** HOL forall */
object Forall extends HOLUnaryConnective { val key = 11
  override def apply(arg: Term): Term = {
    mkApp(mkAtom(key), Seq(Right(arg.ty._funDomainType), Left(arg)))
//    lazy val instantiated = mkTypeApp(mkAtom(key), arg.ty._funDomainType)
//    mkTermApp(instantiated, arg)
  }

  override def unapply(t: Term): Option[Term] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) => Some(t1)
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1))) => Some(t1)
    case _ => None
  }
}
/** HOL exists */
object Exists extends HOLUnaryConnective { val key = 20
  override def apply(arg: Term): Term = {
    mkApp(mkAtom(key), Seq(Right(arg.ty._funDomainType), Left(arg)))
//    lazy val instantiated = mkTypeApp(mkAtom(key), arg.ty._funDomainType)
//    mkTermApp(instantiated, arg)
  }

  override def unapply(t: Term): Option[Term] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) => Some(t1)
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1))) => Some(t1)
    case _ => None
  }
}

/** HOL frue constant */
object LitTrue extends HOLConstant      { val key = 6 }
/** HOL false constant */
object LitFalse extends HOLConstant     { val key = 7 }

// other HOL connectives
/** If-Then-Else combinator */
object IF_THEN_ELSE extends Function3[Term, Term, Term, Term] {
  protected[IF_THEN_ELSE] val key = 15

  override def apply(cond: Term, thn: Term, els: Term): Term = {
    mkApp(mkAtom(key), Seq(Right(thn.ty), Left(cond), Left(thn), Left(els)))

//    lazy val instantiated = mkTypeApp(mkAtom(key), thn.ty)
//
//    mkTermApp(mkTermApp(mkTermApp(instantiated, cond), thn), els)
  }

  def unapply(t: Term): Option[(Term,Term, Term)] = t match {
    case (((Symbol(`key`) @@@@ _) @@@ t1) @@@ t2) @@@ t3 => Some((t1,t2,t3))
    case (Symbol(`key`) ∙ Seq(Right(_), Left(t1), Left(t2), Left(t3))) => Some((t1,t2,t3))
    case _ => None
  }
}

