package leo.modules.calculus.enumeration

import leo.datastructures.{Term, Type, Signature}

/**
 * Created by lex on 07.04.15.
 */
trait Enumeration {
  def enum(ty: Type)(implicit sig: Signature): Iterable[Term]

  def enum(ty: Type, maxNumber: Int)(implicit sig: Signature): Iterable[Term] = enum(ty).take(maxNumber)
}
