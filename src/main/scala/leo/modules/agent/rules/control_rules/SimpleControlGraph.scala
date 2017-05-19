package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{Blackboard, DataStore, DataType}
import leo.modules.agent.rules._

trait AnnotatedClauseGraph extends RuleGraph[AnnotatedClause, AnnotatedClause] {
  override val outType: DataType[AnnotatedClause] = ResultType
}

/**
  *
  * Implements a proof procedure by
  *
  *
  *      -------------unify-------[Unify] <----Paramod---------
  *      |                                          |          |
  *  [ Passiv ]  <---CNF---- |                   Factor    [Active]
  *      |                   |                  PrimSubst     |
  *      |                   |                     |         Move
  *      | ---Select---> [Normalize] --Move--> [Generate] ----|
  *                       |      |
  *                       |-Simp-|
  *                       |-Lift-|
  *                       |-Func-|
  *
  */
class SimpleControlGraph(implicit val sig : Signature) extends AnnotatedClauseGraph {

  case object Normalize extends ClauseType
  case object Generate extends ClauseType
  case object Unify extends ClauseType

  // DS
  val passiveSet : UnprocessedSet = new UnprocessedSet()
  val activeSet : ProcessedSet = new ProcessedSet()
  val normalizeBarrier : AgentBarrier[AnnotatedClause] = new AgentBarrier(Normalize, 4)
  val generateBarrier : AgentBarrier[AnnotatedClause] = new AgentBarrier(Generate, 3)
  val normalizeSet : TypedSet[AnnotatedClause] = new TypedSet(Normalize)
  val generateSet : TypedSet[AnnotatedClause] = new TypedSet(Generate)
  val unifySet : TypedSet[AnnotatedClause] = new TypedSet(Unify)
  val resultSet : TypedSet[AnnotatedClause] = new TypedSet(ResultType)

  // Rules
  val select = new SelectionRule(Unprocessed, Normalize, selectNext, passiveSet,
    Seq(normalizeBarrier.lockType, generateBarrier.lockType, Processed, Unify))
  val simp = new RewriteRule(Normalize, Normalize, activeSet.get)
  val lift = new LiftEqRule(Normalize, Normalize)
  val func = new FuncExtRule(Normalize, Normalize)
  val cnf = new CNFRule(Normalize, Unprocessed)
  val moveNorm = new MovingRule(Normalize, Generate, normalizeBarrier)
  val moveGen = new MovingRule(Generate, Processed, generateBarrier)
  val factor = new FactorRule(Generate, Unify)
  val primSubst = new PrimsubstRule(Generate, Unify)
  val paramod = new ParamodRule(Generate, Unify)(activeSet)
  val unify = new UnificationRule(Unify, Unprocessed)
  val emptyCl = new EmptyClauseRule(ResultType, Unprocessed, Generate, Unify)

  def selectNext() : Boolean = {
    val n = normalizeSet.isEmpty
    val g = generateSet.isEmpty
    val u = unifySet.isEmpty
    n && g && u
  }

  override val rules: Iterable[Rule] =
    Seq(
      select,
      simp,
      lift,
      func,
      cnf,
      moveNorm,
      moveGen,
      factor,
      primSubst,
      paramod,
      unify,
      emptyCl
    )

  override val dataStructures: Iterable[DataStore] =
    Seq(
      passiveSet,
      activeSet,
      normalizeBarrier,
      generateBarrier,
      normalizeSet,
      generateSet,
      unifySet,
      resultSet
    )

  override val initType: DataType[AnnotatedClause] = Unprocessed

  override def fetchResult(implicit blackboard: Blackboard): Iterable[AnnotatedClause] = resultSet.get(ResultType)
}
