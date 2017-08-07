package leo.modules.agent.rules
package control_rules
import leo.Configuration
import leo.datastructures.{AnnotatedClause, Clause, Signature}
import leo.datastructures.blackboard.{DataType, Delta}
import leo.modules.GeneralState

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
                   (implicit state : GeneralState[AnnotatedClause])
  extends Rule{

  private val maxRound = try {Configuration.valueOf("ll").get.head.toInt} catch {case e : Exception => -1}
  var actRound = 0
  var work : Int = 0
  val maxWork : Int = try {Configuration.valueOf("nSelect").get.head.toInt} catch {case e : Exception => 1}

  override val name: String = "selection_rule"
  override val inTypes: Seq[DataType[Any]] = inType +: blockable
  override val outTypes: Seq[DataType[Any]] = Seq(outType)
  override def canApply(r: Delta): Seq[Hint] = {
    unprocessed.synchronized{
      work -= r.removes(inType).size  // TODO Save selected and only delete those
      val canSelect = canSelectNext()
      if(work == 0 && canSelect && unprocessed.unprocessedLeft){
        if(actRound >= maxRound && maxRound > 0){
          leo.Out.debug(s"[Selection] (Round = ${actRound}) : Maximum number of iterations reached.")
          return Seq()
        }
        actRound += 1
        var res : Seq[Hint] = Seq()
        while(work < maxWork && unprocessed.unprocessedLeft){
          val c = unprocessed.nextUnprocessed
          if (Clause.effectivelyEmpty(c.cl)) {
            return Seq()
          } else {
            work += 1
            leo.Out.debug(s"[Selection] (Round = ${actRound}) : ${c.pretty(state.signature)}")
            res = new MoveHint(c, inType, outType) +: res
          }
        }
        res
      } else {
//        println(s"UnprocessedLeft : ${unprocessed.unprocessedLeft}\n work : ${work}\n canSelect : ${canSelect}")
        Seq()
      }
    }
  }
}


