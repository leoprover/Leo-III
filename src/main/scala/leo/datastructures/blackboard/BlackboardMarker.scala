package leo.datastructures.blackboard

/**
 * Marker Interface for any Event for the Agents
 */
trait Event {

}

/**
 * Marker Interface for a message from Agent to Agent
 *
 * @author Max Wisniewski
 * @since 11/19/14
 */
trait Message extends Event {

}

/**
 * Capsules a Formula that was recently added or modified in the blackboard.
 * @param f - Modified formula
 */
private class FormulaEventC(f : FormulaStore) extends Event {
  def getF : FormulaStore = f
}

/**
 * Matching Object for the {@see FormulaEvent}.
 */
object FormulaEvent{

  def apply(f : FormulaStore) : Event = new FormulaEventC(f)

  def unapply(e : Event) : Option[FormulaStore] = e match {
    case f : FormulaEventC => Some(f.getF)
    case _                => None
  }
}