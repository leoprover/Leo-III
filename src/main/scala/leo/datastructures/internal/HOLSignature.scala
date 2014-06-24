package leo.datastructures.internal

import Type.{typeKind, typeVarToType,superKind}
import Term.{mkAtom,mkBound,mkTermAbs,mkTermApp,mkTypeAbs}
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

  // Don't change the order of the elements in this lists.
  // If you do so, you may need to update the Signature implementation.

  // Built-in types
  val types = List(("$tType", superKind), // Key 0
    ("$o", typeKind), // Key 1
    ("$i", typeKind)) // Key 2

  // Fixed symbols
  import Type.{mkPolyType => forall}
  lazy val fixedConsts = List(("$true", o), // Key 3
    ("$false",                        o), // Key 4
    ("#box",                    o ->: o), // Key 5
    ("#diamond",                o ->: o), // Key 6
    ("~",                       o ->: o), // Key 7
    ("!",        forall((1 ->: o) ->: o)), // Key 8
    ("|",                 o ->: o ->: o), // Key 9
    ("=",        forall(1 ->: 1 ->: o))) // Key 10

  // Standard defined symbols
  lazy val definedConsts = List(("?", existsDef, forall((1 ->: o) ->: o)), // Key 11
    ("&",   andDef,  o ->: o ->: o), // Key 12
    ("=>",  implDef, o ->: o ->: o), // Key 13
    ("<=",  ifDef,   o ->: o ->: o), // Key 14
    ("<=>", iffDef,  o ->: o ->: o), // Key 15
    ("~&", nandDef,  o ->: o ->: o), // Key 16
    ("~|",  norDef,  o ->: o ->: o), // Key 17
    ("<~>",niffDef,  o ->: o ->: o)) // Key 18

  // Shorthands for later definitions
  private def not = mkAtom(7)
  private def all = mkAtom(8)
  private def disj = mkAtom(9)
  private def conj = mkAtom(12)
  private def impl = mkAtom(13)
  private def lpmi = mkAtom(14)

  // Definitions for default symbols
  protected def existsDef: Term = mkTypeAbs(
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

  // Implicits for better readability: Convert tuples of bound term variables and types / bound term and type variables
  // to according terms.
  implicit def intToBoundVar(in: (Int, Type)): Term = mkBound(in._2, in._1)
  implicit def intsToBoundVar(in: (Int, Int)): Term = mkBound(in._2, in._1)
}

/** Trait for binary connectives of HOL. They can be used as object representation of defined/fixed symbols. */
trait HOLBinaryConnective extends Function2[Term, Term, Term] {
  protected[HOLBinaryConnective] val key: Signature#Key

  /** Create the term that is constructed by applying two arguments to the binary connective. */
  override def apply(left: Term, right: Term): Term = mkTermApp(mkTermApp(mkAtom(key), left), right)

  def unapply(t: Term): Option[(Term,Term)] = t match {
    case (Symbol(`key`) ::: t1) ::: t2 => Some(t1,t2)
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
    case (Symbol(`key`) ::: t1) => Some(t1)
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
object ||| extends HOLBinaryConnective  { val key = 9 }
/** HOL equality */
object === extends HOLBinaryConnective  { val key = 10 }
/** HOL conjunction */
object & extends HOLBinaryConnective    { val key = 12 }
/** HOL implication */
object Impl extends HOLBinaryConnective { val key = 13 }
/** HOL if (reverse implication) */
object <= extends HOLBinaryConnective   { val key = 14 }
/** HOL iff */
object <=> extends HOLBinaryConnective  { val key = 15 }
/** HOL negated conjunction */
object ~& extends HOLBinaryConnective   { val key = 16 }
/** HOL negated disjunction */
object ~||| extends HOLBinaryConnective { val key = 17 }
/** HOL negated iff */
object <~> extends HOLBinaryConnective  { val key = 18 }

/** HOL forall */
object Forall extends HOLUnaryConnective { val key = 8 }
/** HOL exists */
object Exists extends HOLUnaryConnective { val key = 11 }
/** HOL negation */
object Not extends HOLUnaryConnective    { val key = 7 }

/** HOL frue constant */
object LitTrue extends HOLConstant      { val key = 3 }
/** HOL false constant */
object LitFalse extends HOLConstant     { val key = 4 }

