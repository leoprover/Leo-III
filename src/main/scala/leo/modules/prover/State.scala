package leo.modules.prover

import leo.datastructures._
import leo.modules._
import leo.modules.external.{Future, TptpProver, TptpResult}
import leo.modules.prover.State.LastCallStat

/**
  * Created by lex on 20.02.16.
  */
trait State[T <: ClauseProxy] extends FVState[T] {
  /////////////////////
  // Unprocessed/processed management
  /////////////////////
  /** Initialize the collection of unprocessed clauses.
    * This must be called exactly once prior to any other method on unprocessed clauses.*/
  def initUnprocessed(): Unit
  /** Returns true if the collection of unprocessed clauses is not empty.
    *
    * @note This method should be used instead
    * of {{{unprocessed.nonEmpty}}}. */
  def unprocessedLeft: Boolean
  /** A set representation of the current collection of unprocessed clauses.
    * May be expensive to call since it involved an iteration of various underlying
    * structures.
    *
    * @note Do not use this method to check if there are unprocessed clauses left,
    * i.e. do not use `unprocessed.nonEmpty` or similar. Use [[leo.modules.prover.State#unprocessedLeft]] instead.*/
  def unprocessed: Set[T]
  /** Returns the heuristically chosen 'best' next clause. The heuristic is chosen by the implementation. */
  def nextUnprocessed: T
  /** Add `unprocessed` to the collection of unprocessed clauses. */
  def addUnprocessed(unprocessed: T): Unit
  /** Add all of `unprocessed` to the collection of unprocessed clauses. */
  def addUnprocessed(unprocessed: Set[T]): Unit
  /** Remove `unprocessed` from the collection of unprocessed clauses. */
  def removeUnprocessed(unprocessed: Set[T]): Unit

  /** Add `cl` to the hot list. Clauses on the hot list may be
    * chosen with a higher priority than any other unprocessed clauses by
    * [[leo.modules.prover.State#nextUnprocessed]].
    * Precondition: `cl` is an unprocessed clause within the underlying collection. */
  def addToHotList(cl: T): Unit
  def addToHotList(cls: Set[T]): Unit
  /** Returns the current hot list. */
  def hotList: Seq[T]

  /** The set of processed clauses. Note that clauses might be
    * removed if e.g. shown to be subsumed by other clauses. */
  def processed: Set[T]
  /** The `cl` to the set of processed clauses. */
  def addProcessed(cl: T): Unit
  /** Remove all in `cls` from the set of processed clauses. */
  def removeProcessed(cls: Set[T]): Unit

  /////////////////////
  // Special clauses
  /////////////////////
  def rewriteRules: Set[T]
  def addRewriteRule(cl: T): Unit
  def nonRewriteUnits: Set[T]
  def addNonRewriteUnit(cl: T): Unit
  def removeUnits(cls: Set[T]): Unit

  /////////////////////
  // Further utility
  /////////////////////
  /** Returns, if existent, a derivation of the empty clause.
    * Results equals `null` if no proof found so far. */
  def proof: Proof
  def setProof(proof: Proof): Unit
  @deprecated("I dont know what this actually does and when you are allowed to call it. Please avoid it." +
    "Or specify it. And implement it.", "Leo III 1.1")
  def copy : State[T]

  /////////////////////
  // Handling of external calls
  /////////////////////
  def openExtCalls: Map[TptpProver[T], Set[Future[TptpResult[T]]]]
  def removeOpenExtCalls(prover: TptpProver[T], calls: Set[Future[TptpResult[T]]]): Unit
  def addOpenExtCall(prover: TptpProver[T], call: Future[TptpResult[T]]): Unit
  def nextQueuedCall(prover: TptpProver[T]): Set[T]
  def queuedCallExists(prover: TptpProver[T]): Boolean
  def enqueueCall(prover: TptpProver[T], problem: Set[T]): Unit

  def lastCall: LastCallStat[T]
  def setLastCallStat(lcs: LastCallStat[T]): Unit

  def incTranslations : Int
  def decTranslations : Int
  def getTranslations : Int
}

object State {
  def fresh[T <: ClauseProxy](sig: Signature): State[T] = new StateImpl[T](sig)

  abstract class LastCallStat[T <: ClauseProxy] {
    private var lastLoopCount0: Long = 0
    private var lastProcessedSize0: Int = 0
    private var lastTime0: Long = 0
    private var lastProblem0: Set[T] = _

    def lastLoopCount: Long = lastLoopCount0
    def lastProcessedSize: Int = lastProcessedSize0
    def lastTime: Long = lastTime0
    def lastProblem: Set[T] = if (lastProblem0 == null) Set.empty else lastProblem0

    def shouldCall(problem: Set[T])(implicit state: State[T]): Boolean

    def calledNow(problem: Set[T])(implicit state: State[T]): Unit = {
      lastLoopCount0 = state.noProofLoops
      lastProcessedSize0 = state.noProcessedCl
      lastTime0 = System.currentTimeMillis()
      lastProblem0 = problem
    }

    def fresh: LastCallStat[T]
  }
}

protected[prover] class StateImpl[T <: ClauseProxy](final val sig: Signature) extends FVStateImpl[T](sig) with State[T]{
  import scala.collection.mutable
  /////////// internal fields ///////////////
  /////////////////////
  // Unprocessed/processed management
  /////////////////////
  private final val currentProcessed: mutable.Set[T] = mutable.Set.empty
  protected[prover] final val mpq: MultiPriorityQueue[T] = MultiPriorityQueue.empty
  final private val prio_weights = Seq(4,2,1,2,1)
  protected[prover] var cur_prio = 0
  private var cur_weight = 0
  private val hotlist0: mutable.Queue[T] = mutable.Queue.empty
  /////////////////////
  // Special clauses
  /////////////////////
  private final val currentRewriteRules: mutable.Set[T] = mutable.Set.empty
  private final val currentNonRewriteUnits: mutable.Set[T] = mutable.Set.empty
  /////////////////////
  // Further utility
  /////////////////////
  private var proof0: Proof = _
  /////////////////////
  // Handling of external calls
  /////////////////////
  private var openExtCalls0: Map[TptpProver[T], Set[Future[TptpResult[T]]]] = Map.empty
  private var queuedTranslations : Int = 0
  private var extCallStat: LastCallStat[T] = _
  private var queuedExtCalls0: Map[TptpProver[T], Vector[Set[T]]] = Map.empty

  /////////// Methods ///////////////
  /////////////////////
  // Unprocessed/processed management
  /////////////////////
  final def initUnprocessed(): Unit = {
    import leo.datastructures.ClauseProxyOrderings._
    val conjSymbols: Set[Signature.Key] = symbolsInConjecture0
    mpq.addPriority(litCount_conjRelSymb(conjSymbols, 0.005f, 10, 3).asInstanceOf[Ordering[T]])
    mpq.addPriority(goals_SymbWeight(100,20).asInstanceOf[Ordering[T]])
    mpq.addPriority(nongoals_SymbWeight(100,20).asInstanceOf[Ordering[T]])
    mpq.addPriority(sos_conjRelSymb(conjSymbols, 0.05f, 2, 1).asInstanceOf[Ordering[T]])
    mpq.addPriority(oldest_first.asInstanceOf[Ordering[T]])
  }
  final def unprocessedLeft: Boolean = !mpq.isEmpty
  final def unprocessed: Set[T] = mpq.toSet

  final def nextUnprocessed: T = {
    if (hotlist0.isEmpty) {
      leo.Out.trace(s"[###] Selecting with priority $cur_prio: element $cur_weight")
      leo.Out.trace(s"[###] mpq.priorities ${mpq.priorityCount}")
      if (cur_weight > prio_weights(cur_prio)-1) {
        leo.Out.trace(s"[###] limit exceeded (limit: ${prio_weights(cur_prio)}) (cur_weight: $cur_weight)")
        cur_weight = 0
        cur_prio = (cur_prio + 1) % mpq.priorityCount
        leo.Out.trace(s"[###] cur_prio set to $cur_prio")
      }
      val result = mpq.dequeue(cur_prio)
      cur_weight = cur_weight+1
      result
    } else {
      leo.Out.trace(s"[###] Take from hotlist")
      val result = hotlist0.dequeue()
      mpq.remove(result)
      result
    }
  }

  final def addUnprocessed(cl: T): Unit = {mpq.insert(cl)}
  final def addUnprocessed(cls: Set[T]): Unit = {mpq.insert(cls)}
  final def removeUnprocessed(cls: Set[T]): Unit = {mpq.remove(cls)}

  final def processed: Set[T] = currentProcessed.toSet
  final def addProcessed(cl: T): Unit = { currentProcessed += cl }
  final def removeProcessed(cls: Set[T]): Unit = {currentProcessed --= cls}

  final def rewriteRules: Set[T] = currentRewriteRules.toSet
  final def addRewriteRule(cl: T): Unit = {currentRewriteRules += cl}
  final def nonRewriteUnits: Set[T] = currentNonRewriteUnits.toSet
  final def addNonRewriteUnit(cl: T): Unit = {currentNonRewriteUnits += cl}
  final def removeUnits(cls: Set[T]): Unit = {
    currentRewriteRules --= cls
    currentNonRewriteUnits --= cls
  }
  /////////////////////
  // Special clauses
  /////////////////////
  def addToHotList(cl: T): Unit = {hotlist0.+=(cl)}
  def addToHotList(cls: Set[T]): Unit = {hotlist0 ++= cls}
  def hotList: Seq[T] = hotlist0.toSeq
  /////////////////////
  // Further utility
  /////////////////////
  def proof: Proof = proof0
  def setProof(proof: Proof): Unit = {proof0 = proof}

  override final def copy: State[T] = {
    val state = new StateImpl[T](sig.copy)
    state.current_szs = current_szs
    state.conjecture0 = conjecture0
    state.negConjecture0 = negConjecture0
    //    state.currentProcessed = ??? //current_processed
//    state.current_rewriterules = current_rewriterules
//    state.current_nonRewriteUnits = current_nonRewriteUnits
    state.derivationCl = derivationCl
    state.current_externalProvers = current_externalProvers
    state.runStrategy0 = runStrategy0
    state.symbolsInConjecture0 = symbolsInConjecture0
    state.choiceFunctions0 = choiceFunctions0
    state.initialProblem0 = initialProblem0
    state.poly = poly
    state.current_externalProvers = current_externalProvers
    state.timeout0 = timeout0
    state.domainConstr0 = domainConstr0
    if (lastCall != null) state.extCallStat = lastCall.fresh
    state
  }
  override final def copyGeneral : GeneralState[T] = {
    val state = new StateImpl[T](sig)
    state.current_szs = current_szs
    state.conjecture0 = conjecture0
    state.negConjecture0 = negConjecture0
    //    state.current_processed = ??? // current_processed
//    state.current_rewriterules = current_rewriterules
//    state.current_nonRewriteUnits = current_nonRewriteUnits
    state.derivationCl = derivationCl
    state.current_externalProvers = current_externalProvers
    state.runStrategy0 = runStrategy0
    state.symbolsInConjecture0 = symbolsInConjecture0
    state.choiceFunctions0 = choiceFunctions0
    state.initialProblem0 = initialProblem0
    state.poly = poly
    state.current_externalProvers = current_externalProvers
    state.timeout0 = timeout0
    state.domainConstr0 = domainConstr0
    state
  }
  override def copyFVState: FVState[T] = copy

  /////////////////////
  // Handling of external calls
  /////////////////////
  def openExtCalls: Map[TptpProver[T], Set[Future[TptpResult[T]]]] = openExtCalls0
  def lastCall: LastCallStat[T] = extCallStat
  def setLastCallStat(lcs: LastCallStat[T]): Unit = {extCallStat = lcs}
  def removeOpenExtCalls(prover: TptpProver[T], calls: Set[Future[TptpResult[T]]]): Unit = {
    if (openExtCalls0.isDefinedAt(prover)) {
      val openCalls = openExtCalls0(prover)
      val newCalls = openCalls diff calls
      if (newCalls.isEmpty) openExtCalls0 = openExtCalls0 - prover
      else openExtCalls0 = openExtCalls0 + (prover -> newCalls)
    }
  }
  def addOpenExtCall(prover: TptpProver[T], call: Future[TptpResult[T]]): Unit = {
    if (openExtCalls0.isDefinedAt(prover)) {
      val openCalls = openExtCalls0(prover)
      openExtCalls0 = openExtCalls0 + (prover -> openCalls.+(call))
    } else {
      openExtCalls0 = openExtCalls0 + (prover -> Set(call))
    }
  }

  override def incTranslations: Int = synchronized{
    queuedTranslations += 1
    queuedTranslations
  }
  override def decTranslations: Int = synchronized{
    queuedTranslations -= 1
    queuedTranslations
  }
  override def getTranslations: Int = synchronized{
    queuedTranslations
  }

  type Pick = Boolean
  val HEAD: Pick = false
  val TAIL: Pick = true

  var lastPick: Pick = HEAD

  def nextQueuedCall(prover: TptpProver[T]): Set[T] = {
    if (queuedExtCalls0.isDefinedAt(prover)) {
      val list = queuedExtCalls0(prover)
      if (list.isEmpty) throw new NoSuchElementException("nextQueueCall on empty queueExtCalls entry")
      else {
        val (result, newList) = if (lastPick == HEAD) {
          lastPick = TAIL
          (list.last, list.init)
        } else {
          lastPick = HEAD
          (list.head, list.tail)
        }
        queuedExtCalls0 = queuedExtCalls0 + (prover -> newList)
        result
      }
    } else {
      throw new NoSuchElementException("nextQueueCall on empty queueExtCalls")
    }
  }
  def queuedCallExists(prover: TptpProver[T]): Boolean = {
    if (queuedExtCalls0.isDefinedAt(prover)) {
      queuedExtCalls0(prover).nonEmpty
    } else false
  }
  def enqueueCall(prover: TptpProver[T], problem: Set[T]): Unit = {
    if (queuedExtCalls0.isDefinedAt(prover)) {
      val list = queuedExtCalls0(prover)
      val list0 = list :+ problem
      queuedExtCalls0 = queuedExtCalls0 + (prover -> list0)
    } else {
      queuedExtCalls0 = queuedExtCalls0 + (prover -> Vector(problem))
    }
  }
  
  // Pretty
  override final def pretty: String = s"State SZS: ${szsStatus.pretty}, #processed: $noProcessedCl"
}

class FVIndex {
  import leo.modules.indexing.{CFF, FVIndex}

  val maxFeatures: Int = 100
  var initialized = false
  var features: Seq[CFF] = Vector.empty
  var index = FVIndex()
  def clauseFeatures: Seq[CFF] = features

  protected[modules] final def reset(): Unit = {
    initialized = false
    features = Vector.empty
    index = FVIndex()
  }
}
