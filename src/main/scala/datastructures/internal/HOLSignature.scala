package datastructures.internal

import Type.{typeKind, typeVarToType}
import VarUtils.{freshTypeVar}
/** This type can be mixed-in to supply standard higher-order logic symbol definitions, including
 *
 *  1. Fixed (interpreted) symbols
 *  2. Defined symbols
 *  3. Standard base types
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
  val types = List(("$o",      typeKind),
                   ("$i",      typeKind))

  lazy val fixedConsts = List(("$true",                                Type.o),
                         ("$false",                               Type.o),
                         ("#box",                      Type.o ->: Type.o),
                         ("#diamond",                  Type.o ->: Type.o),
                         ("~",                         Type.o ->: Type.o),
                         ("!",          X #! ((X ->: Type.o) ->: Type.o)),
                         ("|",              Type.o ->: Type.o ->: Type.o),
                         ("=",                 X #! (X ->: X ->: Type.o)))

  lazy val definedConsts = List(("?",   existsDef, X #! ((X ->: Type.o) ->: Type.o)),
                           ("&",   andDef,        Type.o ->: Type.o ->: Type.o),
                           ("=>",  implDef,       Type.o ->: Type.o ->: Type.o),
                           ("<=",  ifDef,         Type.o ->: Type.o ->: Type.o),
                           ("<=>", iffDef,        Type.o ->: Type.o ->: Type.o))

  private lazy val X = freshTypeVar()

  protected def existsDef: Term = null

  protected def andDef: Term = null

  protected def implDef: Term = null

  protected def ifDef: Term = null

  protected def iffDef: Term = null
}