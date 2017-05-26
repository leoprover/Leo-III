package leo.modules.interleavingproc

import leo.Configuration
import leo.agents.{AbstractAgent, Agent, Task}
import leo.datastructures.{AnnotatedClause, Clause, Signature}
import leo.datastructures.blackboard._
import leo.modules.control.externalProverControl.ExtProverControl
import leo.modules.external.{Future, TptpProver, TptpResult}
import leo.modules.output.SZS_Unsatisfiable
import leo.modules.prover.extCallInference

/**
  * Created by mwisnie on 4/10/17.
  */
class ExternalAgent(state : BlackboardState, sig : Signature) extends AbstractAgent{
  override val name: String = "external_call"
  private val self = this
  private val maxExtCall : Int = Configuration.valueOf("MAX_ATP").fold(3)(x => x.headOption.fold(3)(x => try{x.toInt} catch {case _ :Exception => {3}}))
  private var lastCall : Long = 0

  override def filter(event: Event): Iterable[Task] = {
    var tasks : Seq[Task] = Seq()
    if(ExtProverControl.openCallsExist) {
      // ATM checkExternalResult returns only positive results
      val res = ExtProverControl.checkExternalResults(state.state)
      if(res.nonEmpty && res.headOption.nonEmpty){  // TODO Update
        val rres = res.head
        if(rres.szsStatus == SZS_Unsatisfiable) {
          tasks = new ExtResultTask(rres) +: tasks
        }
      }
    }

    val processed = state.state.processed
    if(state.state.noProofLoops >= lastCall + Configuration.ATP_CALL_INTERVAL) {
      lastCall = state.state.noProofLoops
      // Translate
      val it = state.state.externalProvers.iterator
      while(it.hasNext){
        val prover = it.next()
        tasks = new ExtCallTask(prover, processed) +: tasks
      }
    }
    tasks
  }

  override val init : Iterable[Task] = Seq()


  override def kill(): Unit = {
    ExtProverControl.killExternals()
    super.kill()
  }

  class ExtCallTask(ext : TptpProver[AnnotatedClause], problem : Set[AnnotatedClause]) extends Task {
    override val name: String = "extCall"
    override def run: Delta = {
      ExtProverControl.submitSingleProver(ext, problem, state.state)
      EmptyDelta
    }
    override val readSet: Map[DataType[Any], Set[Any]] = Map()
    override val writeSet: Map[DataType[Any], Set[Any]] = Map()
    override def bid: Double = 1.0 / maxExtCall
    override def getAgent: Agent = self
    override lazy val pretty: String = s"extCall(${problem.map(x => s"[${x.id}]").mkString(", ")})"
  }

  class ExtResultTask(res : TptpResult[AnnotatedClause]) extends Task {
    override val name: String = "extResult"
    println("Create")
    override def run: Delta = {
      val d = Result()
      val szs = res.szsStatus // TODO Check again here for correct szs status?
      val origin = res.problem
      leo.Out.trace(s"[ExtProver]: ${res.proverName} got the result ${szs.pretty}")
      val emptyClause = AnnotatedClause(Clause.empty, extCallInference(res.proverName, res.problem))
      d.insert(ProcessedClause)(emptyClause)
      d.insert(SZSStatus)(szs)
      d.insert(DerivedClause)(emptyClause)
      d
    }
    override val readSet: Map[DataType[Any], Set[Any]] = Map()
    override val writeSet: Map[DataType[Any], Set[Any]] = Map()
    override def bid: Double = 1.0 / maxExtCall
    override def getAgent: Agent = self
    override lazy val pretty: String = s"result(${res.proverName} -> ${res.szsStatus})"
  }
}
