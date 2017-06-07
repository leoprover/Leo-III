package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{Blackboard, DataStore, DataType}
import leo.modules.{FVState, GeneralState, SZSException}
import leo.modules.agent.rules._
import leo.modules.output.SZS_Error

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
class SimpleControlGraph(implicit val state : FVState[AnnotatedClause]) extends AnnotatedClauseGraph {

  case object Normalize extends ClauseType
  case object Generate extends ClauseType
  case object Unify extends ClauseType
  case object Done extends DataType[(Long, AnnotatedClause)]{
    override def convert(d: Any): (Long, AnnotatedClause) = d match {
      case (id : Long, c : AnnotatedClause) => (id,c)
      case _ => throw new SZSException(SZS_Error, s"Tried to convert $d to Done.")
    }
  }

  // DS
  var passiveSet : UnprocessedSet = null
  var activeSet : ProcessedSet = null
  var normalizeBarrier : AgentBarrier[AnnotatedClause] = null
  var generateBarrier : AgentBarrier[AnnotatedClause] = null
  var normalizeSet : TypedSet[AnnotatedClause] = null
  var generateSet : TypedSet[AnnotatedClause] = null
  var unifySet : TypedSet[AnnotatedClause]= null
  var resultSet : TypedSet[AnnotatedClause]= null
  var doneSet : TypedSet[(Long, AnnotatedClause)]=null

  // Rules
  var select : SelectionRule= null
  var simp : RewriteRule= null
  var lift : LiftEqRule= null
  var func : FuncExtRule= null
  var cnf : CNFRule= null
  var moveGen : MovingRule[AnnotatedClause]= null
  var moveNorm : ForwardSubsumptionRule= null
  var factor : FactorRule= null
  var primSubst : PrimsubstRule= null
  var paramod : ParamodRule= null
  var unify : UnificationRule= null
  var emptyCl : EmptyClauseRule= null
  var done : ParamodDoneRule=null

  def selectNext() : Boolean = {
    val n = normalizeSet.isEmpty
    val g = generateSet.isEmpty
    val u = unifySet.isEmpty
    val d = doneSet.isEmpty

//    println(s"[Selection] Normalize.isEmpty=${n}, Generate.isEmpty=${g}, Unify.isEmpty=${u}, Done.isEmpty=${doneSet.isEmpty}")

    n && g && u && d
  }

  var rules: Iterable[Rule] = Seq[Rule]()

  var dataStructures: Iterable[DataStore] = Seq[DataStore]()

  override val initType: DataType[AnnotatedClause] = Unprocessed

  override def initGraph(initSet: Iterable[AnnotatedClause])(optionalHint: Option[AnnotatedClause] = None)(implicit blackoard: Blackboard): Unit = {

    passiveSet = new UnprocessedSet(optionalHint)
    activeSet = new ProcessedSet()
    normalizeBarrier = new AgentBarrier(Normalize, 4)
    generateBarrier = new AgentBarrier(Generate, 3)
    normalizeSet = new TypedSet(Normalize)
    generateSet  = new TypedSet(Generate)
    unifySet = new TypedSet(Unify)
    resultSet = new TypedSet(ResultType)
    doneSet = new TypedSet(Done)

    // Rules
    select = new SelectionRule(Unprocessed, Normalize, selectNext, passiveSet,
      Seq(Normalize, normalizeBarrier.lockType, generateBarrier.lockType, Processed, Unify, Done))
    simp = new RewriteRule(Normalize, Normalize, activeSet.get)
    lift = new LiftEqRule(Normalize, Normalize)
    func = new FuncExtRule(Normalize, Normalize)
    cnf = new CNFRule(Normalize, Unprocessed)
    moveGen = new MovingRule(Generate, Processed, generateBarrier)
    moveNorm = new ForwardSubsumptionRule(Normalize, activeSet, Some(Generate, normalizeBarrier))
    factor = new FactorRule(Generate, Unify, Unprocessed)
    primSubst = new PrimsubstRule(Generate, Unify, Unprocessed)
    paramod = new ParamodRule(Generate, Unify, Done, Unprocessed)(activeSet)
    unify = new UnificationRule(Unify, Unprocessed)
    emptyCl = new EmptyClauseRule(ResultType, Unprocessed, Generate, Unify)
    done = new ParamodDoneRule(Done, Unify, Generate, Unprocessed, doneSet, generateSet)(activeSet)

    rules = Seq(
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
      emptyCl,
      done
    )
    dataStructures = Seq(
      passiveSet,
      activeSet,
      normalizeBarrier,
      generateBarrier,
      normalizeSet,
      generateSet,
      unifySet,
      resultSet,
      doneSet
    )
    super.initGraph(initSet)(None)
  }

  override def fetchResult(implicit blackboard: Blackboard): Iterable[AnnotatedClause] = resultSet.get(ResultType)
}
