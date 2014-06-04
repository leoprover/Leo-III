package datastructures.internal

import Type.{typeKind, typeVarToType}
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
  // Don't change the order of the elements in this list.
  // If you do so, you may need to update the Signature implementation.
  val types = List(("$o",      typeKind), // Key 1
                   ("$i",      typeKind)) // Key 2

  import Type.{mkPolyType => forall}

  lazy val fixedConsts = List(("$true",                           Type.o), // Key 3
                         ("$false",                               Type.o), // Key 4
                         ("#box",                      Type.o ->: Type.o), // Key 5
                         ("#diamond",                  Type.o ->: Type.o), // Key 6
                         ("~",                         Type.o ->: Type.o), // Key 7
                         ("!",        forall ((1 ->: Type.o) ->: Type.o)), // Key 8
                         ("|",              Type.o ->: Type.o ->: Type.o), // Key 9
                         ("=",                forall (1 ->: 1 ->: Type.o))) // Key 10

  lazy val definedConsts = List(("?",   existsDef, forall ((1 ->: Type.o) ->: Type.o)), // Key 11
                           ("&",   andDef,        Type.o ->: Type.o ->: Type.o), // Key 12
                           ("=>",  implDef,       Type.o ->: Type.o ->: Type.o), // Key 13
                           ("<=",  ifDef,         Type.o ->: Type.o ->: Type.o), // Key 14
                           ("<=>", iffDef,        Type.o ->: Type.o ->: Type.o)) // Key 15

  private def o = Type.mkType(1)
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
                                            mkTermApp((2,(1 ->: o)), (1,1))))))))

  protected def andDef: Term = mkTermAbs(o,
                                mkTermAbs(o,
                                  mkTermApp(not,
                                    mkTermApp(
                                      mkTermApp(disj, mkTermApp(not, (2,o))),
                                                      mkTermApp(not, (1,o))))))

  protected def implDef: Term = mkTermAbs(o,
                                  mkTermAbs(o,
                                    mkTermApp(
                                      mkTermApp(disj, mkTermApp(not, (2,o))),
                                                      (1,o))))

  protected def ifDef: Term =  mkTermAbs(o,
                                  mkTermAbs(o,
                                    mkTermApp(
                                      mkTermApp(disj, (2,o)),
                                                      mkTermApp(not, (1,o)))))

  protected def iffDef: Term = mkTermAbs(o,
                                  mkTermAbs(o,
                                    mkTermApp(
                                      mkTermApp(conj, mkTermApp(
                                                        mkTermApp(impl, (2,o)), (1,o))),
                                                      mkTermApp(
                                                        mkTermApp(lpmi, (2,o)), (1,o)))))



  implicit def intToBoundVar(in: (Int, Type)): Term = mkBound(in._2,in._1)
  implicit def intsToBoundVar(in: (Int, Int)): Term = mkBound(in._2,in._1)
}