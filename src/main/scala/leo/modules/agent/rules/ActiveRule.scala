package leo.modules.agent.rules
import leo.datastructures.blackboard.{Blackboard, DataType, Delta, EmptyDelta}

/**
  * Created by mwisnie on 6/13/17.
  */
class ActiveRule[A](rule: Rule, val inTypes : DataType[A]*)(cond : () => Boolean)
                   (implicit val blackboard : Blackboard) extends Rule {
  override val name: String = s"active(${rule.name})"

  override val moving: Boolean = false

  override val outTypes: Seq[DataType[Any]] = Seq()
  private var executed = false // TODO Remove after unregister of activation rule

  override def canApply(r: Delta): Seq[Hint] = {
    if(executed) return Seq()
    val smthHappend : Boolean = inTypes.exists {dt => r.inserts(dt).nonEmpty || r.updates(dt).nonEmpty || r.removes(dt).nonEmpty}
    if(smthHappend && cond()){
      executed = true
      Seq(ActiveHint)
    } else {
      Seq()
    }
  }

  object ActiveHint extends Hint {
    override def apply(): Delta = {
      leo.Out.debug(s"[Activate] Activated ${rule.name}")
      val newAgent = new RuleAgent(rule)
      blackboard.registerAgent(newAgent)
      // TODO Unregister this agent
      EmptyDelta
    }
    override val read: Map[DataType[Any], Set[Any]] = Map()
    override val write: Map[DataType[Any], Set[Any]] = Map()
  }
}
