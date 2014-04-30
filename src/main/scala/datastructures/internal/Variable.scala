package datastructures.internal

/**
 * Created by lex on 30.04.14.
 */

object Variable {
  def mkTypeVar = TypeVar(_,_)
  def mkVar     = TermVar(_,_)
}

abstract sealed class Variable {
  def isTypeVar: Boolean
  def isTermVar: Boolean
}
case class TypeVar(name: String, varType: Kind) extends Variable {
  def isTypeVar = true
  def isTermVar = false
}
case class TermVar(name: String, varType: Type) extends Variable {
  def isTypeVar = false
  def isTermVar = true
}
