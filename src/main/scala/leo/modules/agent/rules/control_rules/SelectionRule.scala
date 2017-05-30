package leo.modules.agent.rules
package control_rules
import leo.Configuration
import leo.datastructures.{AnnotatedClause, Clause, Signature}
import leo.datastructures.blackboard.{DataType, Delta}

/**
  * This rule selects a clause from Unprocessed
  * to be processed next.
  */
class SelectionRule(inType : DataType[AnnotatedClause],
                    outType : DataType[AnnotatedClause],
                    canSelectNext : () => Boolean,
                    unprocessed : UnprocessedSet,
                    blockable : Seq[DataType[Any]] = Seq()
                   )
                   (implicit signature : Signature)
  extends Rule{

  private val maxRound = try {Configuration.valueOf("ll").get.head.toInt} catch {case e : Exception => -1}
  var actRound = 0
  var work : Int = 0
  val maxWork : Int = 1

  override val name: String = "selection_rule"
  override val inTypes: Seq[DataType[Any]] = inType +: blockable
  override val outTypes: Seq[DataType[Any]] = Seq(outType)
  override def moving: Boolean = true
  override def canApply(r: Delta): Seq[Hint] = {
    unprocessed.synchronized{
//      println(work)
      if(r.removes(inType).nonEmpty) work -= 1
      if(work < maxWork && canSelectNext() && unprocessed.unprocessedLeft){
        if(actRound >= maxRound && maxRound > 0){
          leo.Out.debug(s"[Selection] (Round = ${actRound}) : Maximum number of iterations reached.")
          return Seq()
        }
        val c = unprocessed.nextUnprocessed // TODO Select multiple?
        if(Clause.effectivelyEmpty(c.cl)){
          Seq()
        } else {
          actRound += 1
          work += 1
          leo.Out.debug(s"[Selection] (Round = ${actRound}) : ${c.pretty(signature)}")
          Seq(new MoveHint(c, inType, outType))
        }
      } else {
        Seq()
      }
    }
  }
}


