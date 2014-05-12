package datastructures.internal

import Type.{typeKind, superKind}
import Variable.{newTyVar}
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
  val types = List((Type.o.name,      typeKind),
                   (Type.i.name,      typeKind),
                   (typeKind.name,   superKind))

  val fixedConsts = List(("$true",                                Type.o),
                         ("$false",                               Type.o),
                         ("#box",                      Type.o ->: Type.o),
                         ("#diamond",                  Type.o ->: Type.o),
                         ("~",                         Type.o ->: Type.o),
                         ("!",          X #! ((X ->: Type.o) ->: Type.o)),
                         ("|",              Type.o ->: Type.o ->: Type.o),
                         ("=",                 X #! (X ->: X ->: Type.o)))

  val definedConsts = List(("?",   existsDef, X #! ((X ->: Type.o) ->: Type.o)),
                           ("&",   andDef,        Type.o ->: Type.o ->: Type.o),
                           ("=>",  implDef,       Type.o ->: Type.o ->: Type.o),
                           ("<=",  ifDef,         Type.o ->: Type.o ->: Type.o),
                           ("<=>", iffDef,        Type.o ->: Type.o ->: Type.o))

  private val X = newTyVar

  protected def existsDef: Term = null

  protected def andDef: Term = null

  protected def implDef: Term = null

  protected def ifDef: Term = null

  protected def iffDef: Term = null
}