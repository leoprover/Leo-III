package leo.modules.agent.rules
package control_rules
import leo.Configuration
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta}

/**
  * This rule selects a clause from Unprocessed
  * to be processed next.
  */
class SelectionRule(inType : DataType[AnnotatedClause],
                    outType : DataType[AnnotatedClause],
                    canSelectNext : () => Boolean,
                    unprocessed : UnprocessedSet,
                    blockable : Seq[DataType[AnnotatedClause]] = Seq()
                   )
                   (implicit signature : Signature)
  extends Rule{

  private val maxRound = try {Configuration.valueOf("ll").get.head.toInt} catch {case e : Exception => -1}
  private var actRound = 0

  override val name: String = "selection_rule"
  override val inTypes: Seq[DataType[Any]] = inType +: blockable
  override val outTypes: Seq[DataType[Any]] = Seq(outType)
  override def moving: Boolean = true
  override def canApply(r: Delta): Seq[Hint] = {
    if(canSelectNext() && unprocessed.unprocessedLeft){
      if(actRound >= maxRound && maxRound > 0){
        println(s"[Selection] (Round = ${actRound}) : Maximum number of iterations reached.")
        return Seq()
      }
      actRound += 1
      val c = unprocessed.nextUnprocessed // TODO Select multiple?
      println(s"[Selection] (Round = ${actRound}) : ${c.pretty(signature)}")
      Seq(new MoveHint(c, inType, outType))
    } else {
      Seq()
    }
  }
}


