package leo.modules.proof_object

import leo.datastructures.ClauseAnnotation.{CompressedRule, InferredFrom}
import leo.datastructures._
import leo.modules.calculus._
import leo.modules.output.{Output, SuccessSZS}

/**
  *
  * Contains multiple methods to compress
  * and work with proof objects
  *
  * @author Max Wisniewski
  * @since 28/2/2017
  */
object CompressProof {

  final val stdImportantInferences : Set[CalculusRule] = Set(Choice, PrimSubst, OrderedEqFac, OrderedParamod, NegateConjecture)

  /**
    *
    * Tracks the list of InferredFrom Rules back, until
    * a rule application of `important` or a split in the tree.
    * All intermediate rule applications are queued in one annotation
    *
    * @param cl The clause, which's last important rule application is checked.
    * @param important A set of `ìmportant` rules, that need to be tracked.
    * @tparam A Specific type of clause proxy
    */
  def lastImportantStep[A <: ClauseProxy](important : Set[CalculusRule])(cl : A) : ClauseAnnotation = {
    var pickedUpRules : Seq[(CalculusRule, Output)] = Seq()
    var curAn = cl.annotation
    while(true){
      curAn match {
        case anno@InferredFrom(rule, cws) =>
          assert(cws.nonEmpty, "An empty inferred from occured")
          // If it is branching we cannot compress with this method
          // or we reached an important rule
          if(cws.size > 1 || important.contains(rule) || cws.head._1.role == Role_NegConjecture) {
            return CompressedRule(pickedUpRules.reverse, anno)
          } else {
            // Otherwise compress further
            val (next, output) = cws.head
            pickedUpRules = (rule, output) +: pickedUpRules
            curAn = next.annotation
          }
        case CompressedRule(rules, anno) => return CompressedRule(pickedUpRules.reverse ++ rules, anno)
        case endAnnotation : ClauseAnnotation =>
          if(pickedUpRules.isEmpty)
            return endAnnotation
          else
            return CompressedRule(pickedUpRules.reverse, endAnnotation)
      }
    }
    null // Should never be reached
  }

  def compressAnnotation(cl : AnnotatedClause)(compress : AnnotatedClause => ClauseAnnotation) : AnnotatedClause = {
    new AnnotatedClause(cl.id, cl.cl, cl.role, compress(cl),cl.properties)
  }
}