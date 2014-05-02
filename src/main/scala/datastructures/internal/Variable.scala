package datastructures.internal

/**
 *
 *
 * @author Alexander Steen
 * @since 30.04.2014
 */
object Variable {
  def mkTypeVar(name: String = {Variable.lastUsedIndex += 1; Variable.typeVarNames(Variable.lastUsedIndex)}, varType: Kind = BaseKind) = TypeVar(name, varTypels)
  def mkVar     = TermVar(_,_)

  lazy val typeVarNames: List[String] = Nil
  var lastUsedIndex = -1
}

abstract sealed class Variable {
  def isTypeVar: Boolean
  def isTermVar: Boolean

  //vielleicht local/global scope speichern? (Alex)
}
case class TypeVar(name: String, varType: Kind) extends Variable {
  def isTypeVar = true
  def isTermVar = false

  def #!(body: Type): ForallType = {
    ForallType(this, body)
  }
}
case class TermVar(name: String, varType: Type) extends Variable {
  def isTypeVar = false
  def isTermVar = true
}
