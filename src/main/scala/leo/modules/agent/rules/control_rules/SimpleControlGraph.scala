package leo.modules.agent.rules.control_rules

import leo.datastructures.AnnotatedClause
import leo.datastructures.blackboard.{Blackboard, DataStore, DataType}
import leo.modules.{FVState, SZSException}
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

  case object Init extends ClauseType
  case object Processed extends ClauseType
  case object Unprocessed extends ClauseType
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
  var passiveSet : UnprocessedSet = _
  var activeSet : ProcessedSet = _
  var normalizeBarrier : AgentBarrier[AnnotatedClause] = _
  var generateBarrier : AgentBarrier[AnnotatedClause] = _
  var normalizeSet : TypedSet[AnnotatedClause] = _
  var generateSet : TypedSet[AnnotatedClause] = _
  var unifySet : TypedSet[AnnotatedClause]= _
  var resultSet : TypedSet[AnnotatedClause]= _
  var doneSet : TypedSet[(Long, AnnotatedClause)]=_
  var preprocessSet : TypedSet[AnnotatedClause] = _

  // Rules
  var select : SelectionRule= _
  var simp : RewriteRule= _
  var lift : LiftEqRule= _
  var func : FuncExtRule= _
  var cnf : CNFRule= _
  var moveGen : MovingRule[AnnotatedClause]= _
  var moveNorm : ForwardSubsumptionRule= _
  var factor : FactorRule= _
  var primSubst : PrimsubstRule= _
  var paramod : ParamodRule= _
  var unify : UnificationRule= _
  var emptyCl : EmptyClauseRule= _
  var done : ParamodDoneRule=_
  var choice : ChoiceRule=_
  var preprocess : PreprocessRule=_
  var activateSelect : ActiveRule[AnnotatedClause]=_

  def selectNext() : Boolean = {
    val n = normalizeSet.isEmpty
    val g = generateSet.isEmpty
    val u = unifySet.isEmpty
    val d = doneSet.isEmpty

//    println(s"[Selection] Normalize.isEmpty=${n}, Generate.isEmpty=${g}, Unify.isEmpty=${u}, Done.isEmpty=${doneSet.isEmpty}")

    n && g && u && d
  }

  def startSelect() : Boolean = {
    val p = preprocessSet.isEmpty
    p
  }

  var rules: Iterable[Rule] = Seq[Rule]()

  var dataStructures: Iterable[DataStore] = Seq[DataStore]()

  override val initType: DataType[AnnotatedClause] = Init

  override def initGraph(initSet: Iterable[AnnotatedClause])(implicit blackoard: Blackboard): Unit = {

    passiveSet = new UnprocessedSet(Unprocessed)
    activeSet = new ProcessedSet(Processed)
    normalizeBarrier = new AgentBarrier(Normalize, 4)
    generateBarrier = if(state.runStrategy.choice) new AgentBarrier(Generate, 4) else new AgentBarrier(Generate, 3)
    normalizeSet = new TypedSet(Normalize)
    generateSet  = new TypedSet(Generate)
    unifySet = new TypedSet(Unify)
    resultSet = new TypedSet(ResultType)
    doneSet = new TypedSet(Done)
    preprocessSet = new TypedSet(Init)

    // Rules
    select = new SelectionRule(Unprocessed, Normalize, selectNext, passiveSet,
      Seq(Normalize, normalizeBarrier.lockType, generateBarrier.lockType, Processed, Unify, Done))
    activateSelect = new ActiveRule[AnnotatedClause](select, Init)(startSelect)
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
    choice = new ChoiceRule(Generate, Unify, Unprocessed)
    preprocess = new PreprocessRule(Init, Unprocessed)

    rules = {
      val r = Seq(
        activateSelect,
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
        done,
        preprocess
      )
      if (state.runStrategy.choice) choice +: r
      else r
    }

    dataStructures = Seq(
      passiveSet,
      activeSet,
      normalizeBarrier,
      generateBarrier,
      normalizeSet,
      generateSet,
      unifySet,
      resultSet,
      doneSet,
      preprocessSet
    )
    super.initGraph(initSet)
  }

  override def fetchResult(implicit blackboard: Blackboard): Iterable[AnnotatedClause] = resultSet.get(ResultType)
}
