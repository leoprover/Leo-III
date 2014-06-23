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
 * These symbols must be inserted into the signature before all other symbols and in the order described below!
 *
 * Details:
 * It defines eight fixed symbols ($true, $false, #box, #diamond, ~, !, |, =),
 * five defined symbols (?, &, =>, <=, <=>) and three types ($o, $i, *)
 * @author Alexander Steen
 * @since 02.05.2014
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

  val types = List(("$tType", superKind), // Key 0
    ("$o", typeKind), // Key 1
    ("$i", typeKind)) // Key 2


  import Type.{mkPolyType => forall}

  lazy val fixedConsts = List(("$true", o), // Key 3
    ("$false",                        o), // Key 4
    ("#box",                    o ->: o), // Key 5
    ("#diamond",                o ->: o), // Key 6
    ("~",                       o ->: o), // Key 7
    ("!",        forall((1 ->: o) ->: o)), // Key 8
    ("|",                 o ->: o ->: o), // Key 9
    ("=",        forall(1 ->: 1 ->: o))) // Key 10

  lazy val definedConsts = List(("?", existsDef, forall((1 ->: o) ->: o)), // Key 11
    ("&",   andDef,  o ->: o ->: o), // Key 12
    ("=>",  implDef, o ->: o ->: o), // Key 13
    ("<=",  ifDef,   o ->: o ->: o), // Key 14
    ("<=>", iffDef,  o ->: o ->: o)) // Key 15

  private def not = mkAtom(7)

  private def all = mkAtom(8)

  private def disj = mkAtom(9)

  private def conj = mkAtom(12)

  private def impl = mkAtom(13)

  private def lpmi = mkAtom(14)

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


  implicit def intToBoundVar(in: (Int, Type)): Term = mkBound(in._2, in._1)

  implicit def intsToBoundVar(in: (Int, Int)): Term = mkBound(in._2, in._1)
}

  object <=> {
    def unapply(t: Term): Option[(Term,Term)] = t match {
      case (Symbol(15) ::: t1) ::: t2 => Some(t1,t2)
      case _ => None
    }

    def apply(t : Term, s :  Term) : Term = mkTermApp(mkTermApp(mkAtom(15), t),s)
  }

  object <= {
    def unapply(t: Term): Option[(Term,Term)] = t match {
      case (Symbol(14) ::: t1) ::: t2 => Some(t1,t2)
      case _ => None
    }
  }

  object Impl {
    def unapply(t: Term): Option[(Term,Term)] = t match {
      case (Symbol(13) ::: t1) ::: t2 => Some(t1,t2)
      case _ => None
    }

    def apply(t : Term, s : Term) : Term = mkTermApp(mkTermApp(mkAtom(13), t),s)
  }

  object & {
    def unapply(t: Term): Option[(Term,Term)] = t match {
      case (Symbol(12) ::: t1) ::: t2 => Some(t1,t2)
      case _ => None
    }

    def apply(t : Term, s : Term) : Term = mkTermApp(mkTermApp(mkAtom(12), t), s)
  }

  object ||| {
    def unapply(t: Term): Option[(Term,Term)] = t match {
      case (Symbol(9) ::: t1) ::: t2 => Some(t1,t2)
      case _ => None
    }

    def apply(t : Term, s : Term) : Term  = mkTermApp(mkTermApp(mkAtom(9), t),s)
  }

  object Forall {
    def unapply(t: Term): Option[Term] = t match {
      case (Symbol(8) ::: t1) => Some(t1)
      case _ => None
    }

    def apply(t : Term) : Term = mkTermApp(mkAtom(8), t)
  }

  object Exists {
    def unapply(t: Term): Option[Term] = t match {
      case (Symbol(11) ::: t1) => Some(t1)
      case _ => None
    }

    def apply(t : Term) : Term = mkTermApp(mkAtom(11), t)
  }

  object === {
    def unapply(t: Term): Option[(Term,Term)] = t match {
      case (Symbol(10) ::: t1) ::: t2 => Some(t1,t2)
      case _ => None
    }

    def apply(t: Term, s: Term): Term = mkTermApp(mkTermApp(mkAtom(10),t),s)
  }

  object Not {
    def unapply(t: Term): Option[(Term)] = t match {
      case (Symbol(7) ::: t1) => Some(t1)
      case _ => None
    }

    def apply(t : Term) : Term = mkTermApp(mkAtom(7), t)
  }

  object LitTrue {
    def unapply(t: Term): Boolean = t match {
      case Symbol(3) => true
      case _ => false
    }
    def apply() : Term = Term.mkAtom(3)
  }

  object LitFalse {
    def unapply(t: Term): Boolean = t match {
      case Symbol(4) => true
      case _ => false
    }
    def apply() : Term = Term.mkAtom(4)
  }

