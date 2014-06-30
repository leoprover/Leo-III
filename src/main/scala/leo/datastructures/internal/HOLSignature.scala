package leo.datastructures.internal

import Type.{typeKind, typeVarToType,superKind}
import Term.{mkAtom,mkTermApp,λ, Λ,intsToBoundVar,intToBoundVar, mkTypeApp}
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
    ("$$ite",    forall(o ->: 1 ->: 1 ->: 1)))  // Key 15

  // Standard defined symbols
  lazy val definedConsts = List(("?", existsDef, forall((1 ->: o) ->: o)), // Key 16
    ("&",   andDef,  o ->: o ->: o), // Key 17
    ("=>",  implDef, o ->: o ->: o), // Key 18
    ("<=",  ifDef,   o ->: o ->: o), // Key 19
    ("<=>", iffDef,  o ->: o ->: o), // Key 20
    ("~&", nandDef,  o ->: o ->: o), // Key 21
    ("~|",  norDef,  o ->: o ->: o), // Key 22
    ("<~>",niffDef,  o ->: o ->: o), // Key 23
    ("!=",  neqDef, forall(1 ->: 1 ->: o))) // Key 24

  // Definitions for default symbols
  protected def existsDef: Term = Λ(
                                    λ(1 ->: o)(
                                      Not(
                                        Forall(
                                          λ(1)(
                                            Not(
                                              mkTermApp((2, (1 ->: o)), (1, 1))))))))

  protected def andDef: Term = λ(o,o)(
                                Not(
                                  |||(
                                    Not((2, o)),
                                    Not((1, o)))))

  protected def implDef: Term = λ(o,o)(
                                  |||(
                                    Not((2, o)),
                                    (1, o)
                                  ))

  protected def ifDef: Term = λ(o,o)(
                                |||(
                                  (2, o),
                                  Not((1, o))
                                ))

  protected def iffDef: Term = λ(o,o)(
                                &(
                                  Impl((2, o), (1, o)),
                                  <=  ((2, o), (1, o))))

  protected def nandDef: Term = λ(o,o)(
                                  |||(
                                    Not((2, o)),
                                    Not((1, o))))

  protected def norDef: Term = λ(o,o)(
                                Not(|||((2, o), (1, o))))

  protected def niffDef: Term = λ(o,o)(
                                  Not(
                                    &(
                                      Impl((2, o), (1, o)),
                                      <=  ((2, o), (1, o)))))

  protected def neqDef: Term = Λ(
                                λ(1,1)(
                                  Not(
                                    ===(
                                      (2,1),
                                      (1,1)))))
}

/** Trait for binary connectives of HOL. They can be used as object representation of defined/fixed symbols. */
trait HOLBinaryConnective extends Function2[Term, Term, Term] {
  protected[HOLBinaryConnective] val key: Signature#Key

  /** Create the term that is constructed by applying two arguments to the binary connective. */
  override def apply(left: Term, right: Term): Term = mkTermApp(mkTermApp(mkAtom(key), left), right)

  def unapply(t: Term): Option[(Term,Term)] = t match {
    case (Symbol(`key`) @@@ t1) @@@ t2 => Some(t1,t2)
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
  override def apply(left:Term, right: Term) = {
    val instantiated = mkTypeApp(mkAtom(key), left.ty)
    mkTermApp(mkTermApp(instantiated, left), right)
  }

  override def unapply(t: Term): Option[(Term,Term)] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) @@@ t2 => Some(t1,t2)
    case _ => None
  }
}
/** HOL conjunction */
object & extends HOLBinaryConnective    { val key = 17 }
/** HOL implication */
object Impl extends HOLBinaryConnective { val key = 18 }
/** HOL if (reverse implication) */
object <= extends HOLBinaryConnective   { val key = 19 }
/** HOL iff */
object <=> extends HOLBinaryConnective  { val key = 20 }
/** HOL negated conjunction */
object ~& extends HOLBinaryConnective   { val key = 21 }
/** HOL negated disjunction */
object ~||| extends HOLBinaryConnective { val key = 22 }
/** HOL negated iff */
object <~> extends HOLBinaryConnective  { val key = 23 }
/** HOL negated equality */
object !=== extends HOLBinaryConnective  { val key = 24
  override def apply(left:Term, right: Term) = {
    val instantiated = mkTypeApp(mkAtom(key), left.ty)
    mkTermApp(mkTermApp(instantiated, left), right)
  }

  override def unapply(t: Term): Option[(Term,Term)] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) @@@ t2 => Some(t1,t2)
    case _ => None
  }
}

/** HOL negation */
object Not extends HOLUnaryConnective    { val key = 10 }
/** HOL forall */
object Forall extends HOLUnaryConnective { val key = 11
  override def apply(arg: Term): Term = {
    val instantiated = mkTypeApp(mkAtom(key), arg.ty)
    mkTermApp(instantiated, arg)
  }

  override def unapply(t: Term): Option[Term] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) => Some(t1)
    case _ => None
  }
}
/** HOL exists */
object Exists extends HOLUnaryConnective { val key = 16
  override def apply(arg: Term): Term = {
    val instantiated = mkTypeApp(mkAtom(key), arg.ty)
    mkTermApp(instantiated, arg)
  }

  override def unapply(t: Term): Option[Term] = t match {
    case ((Symbol(`key`) @@@@ _) @@@ t1) => Some(t1)
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
    val instantiated = mkTypeApp(mkAtom(key), thn.ty)

    mkTermApp(mkTermApp(mkTermApp(instantiated, cond), thn), els)
  }

  def unapply(t: Term): Option[(Term,Term, Term)] = t match {
    case (((Symbol(`key`) @@@@ _) @@@ t1) @@@ t2) @@@ t3 => Some(t1,t2,t3)
    case _ => None
  }
}

