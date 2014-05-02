package datastructures.internal

import Type.{mkTypeVarType, getBaseKind}
import Variable.{mkTypeVar, typeVarNames}
/** This type can be mixed-in to supply standard higher-order logic symbol definitions, including
 *
 *  1. Fixed (interpreted) symbols
 *  2. Defined symbols
 *  3. Standard base types
 *
 * @author Alexander Steen
 * @since 02.05.2014
 */
trait HOLSignature {
  val types = List((Type.o.name,      getBaseKind),
                   (Type.i.name,      getBaseKind),
                   (getBaseKind.name,   SuperKind))

  val fixedConsts = List(("$true",                                              Type.o),
                         ("$false",                                             Type.o),
                         ("#box",                                    Type.o ->: Type.o),
                         ("#diamond",                                Type.o ->: Type.o),
                         ("~",                                       Type.o ->: Type.o),
                         ("!",        mkTypeVar("X") #!  (("X" ->: Type.o) ->: Type.o)),
                         ("|",                            Type.o ->: Type.o ->: Type.o),
                         ("=",              mkTypeVar("X") #! ("X" ->: "X" ->: Type.o)))

  val definedConsts = List(("?",   existsDef, mkTypeVar("X") #! (("X" ->: Type.o) ->: Type.o)),
                           ("&",   andDef,                      Type.o ->: Type.o ->: Type.o),
                           ("=>",  implDef,                     Type.o ->: Type.o ->: Type.o),
                           ("<=",  ifDef,                       Type.o ->: Type.o ->: Type.o),
                           ("<=>", iffDef,                      Type.o ->: Type.o ->: Type.o))

  protected def existsDef: Term = null

  protected def andDef: Term = null

  protected def implDef: Term = null

  protected def ifDef: Term = null

  protected def iffDef: Term = null
}