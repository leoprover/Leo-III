package leo.modules.calculus.enumeration

import leo.datastructures.{Term, Type}

/**
 * Created by lex on 07.04.15.
 */
trait Enumeration {
  def enum(ty: Type): Iterable[Term]

  def enum(ty: Type, maxNumber: Int): Iterable[Term] = enum(ty).take(maxNumber)
}
