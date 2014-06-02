package datastructures.internal

import datastructures.Pretty

/**
 * Created by lex on 29.05.14.
 */
abstract class RefTerm extends Pretty {
  // Predicates on terms
  val isAtom: Boolean
  val isTermApp: Boolean
  val isTermAbs: Boolean
  val isTypeApp: Boolean
  val isTypeAbs: Boolean


  // Queries on terms
  def ty: Type
  def freeVars: Set[RefTerm]
  def herbrandUniverse: Set[RefTerm]
  // Substitutions
  def substitute(what: RefTerm, by: RefTerm): RefTerm

  // Other operations
  def betaNormalize: RefTerm

  protected[internal] def substituteBound(scopeIndex: Int, by: RefTerm): RefTerm
  protected[internal] def inc(scopeIndex: Int): RefTerm
}


object RefTerm {
//  def mkAtom = Atom(_)
  def mkBound = BoundNode(_,_)
  def mkTermApp = ApplicationNode(_,_)
  def mkTermAbs = AbstractionNode(_, _)
  def mkTypeApp(left: Type, right: Type): Term = ???
  def mkTypeAbs(hd: Variable, body: Term): Term = ???

  def \(hd: Type, body: Term): Term = ???

  def /\(hd: Variable, body: Term): Term = ???
}
