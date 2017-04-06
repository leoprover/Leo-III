package leo.modules.seqpproc

import leo.datastructures._
import leo.modules.output.{SZS_Unknown, StatusSZS}
import leo.modules.external.TptpProver

/**
  * Created by lex on 20.02.16.
  */
trait State[T <: ClauseProxy] extends Pretty with StateStatistics {
  def conjecture: T
  def negConjecture: T
  def symbolsInConjecture: Set[Signature#Key]
  def setConjecture(conj: T): Unit
  def setNegConjecture(negConj: T): Unit

  def signature: Signature
  def szsStatus: StatusSZS
  def setSZSStatus(szs: StatusSZS): Unit

  def isPolymorphic: Boolean
  def setPolymorphic: Unit

  def defConjSymbols(negConj: T): Unit
  def initUnprocessed(): Unit
  def unprocessedLeft: Boolean
  def unprocessed: Set[T]
  def nextUnprocessed: T
  def addUnprocessed(unprocessed: T): Unit
  def addUnprocessed(unprocessed: Set[T]): Unit
  def removeUnprocessed(unprocessed: Set[T]): Unit
  def processed: Set[T]
  def addProcessed(cl: T): Unit
  def removeProcessed(cls: Set[T]): Unit

  def rewriteRules: Set[T]
  def addRewriteRule(cl: T): Unit
  def nonRewriteUnits: Set[T]
  def addNonRewriteUnit(cl: T): Unit
  def removeUnits(cls: Set[T]): Unit

  def addChoiceFunction(f: Term): Unit
  def choiceFunctions: Map[Type, Set[Term]]
  final def choiceFunctions(ty: Type): Set[Term] = choiceFunctions.getOrElse(ty, Set())

  def setDerivationClause(cl: T): Unit
  def derivationClause: Option[T]

  def addInitial(cls: Set[T]): Unit
  def initialProblem: Set[T]
  def externalProvers: Set[TptpProver[T]]
  def addExternalProver(prover: TptpProver[T]): Unit
}

trait StateStatistics {
  // Statistics
  def noProofLoops: Long
  def incProofLoopCount(): Unit
  def noProcessedCl: Int
  def incTrivialCl(): Unit
  def noTrivialCl: Int
  def incForwardSubsumedCl(): Unit
  def incForwardSubsumedCl(n: Int): Unit
  def noForwardSubsumedCl: Int
  def incBackwardSubsumedCl(): Unit
  def incBackwardSubsumedCl(n: Int): Unit
  def noBackwardSubsumedCl: Int
  def incDescendantsDeleted(n: Int): Unit
  def noDescendantsDeleted: Int
  def incGeneratedCl(by: Int): Unit
  def noGeneratedCl: Int
  def incParamod(by: Int): Unit
  def noParamod: Int
  def incFactor(by: Int): Unit
  def noFactor: Int
  def choiceFunctionCount: Int
  def choiceInstantiations: Int
  def incChoiceInstantiations(n: Int): Unit
}

object State {
  def fresh[T <: ClauseProxy](sig: Signature): State[T] = new StateImpl[T](SZS_Unknown, sig)
}

protected[seqpproc] class StateImpl[T <: ClauseProxy](initSZS: StatusSZS, initSignature: Signature) extends State[T] {
  private var conjecture0: T = _
  private var negConjecture0: T = _
  private var current_szs = initSZS
  private var current_processed: Set[T] = Set()
  private var current_rewriterules: Set[T] = Set()
  private var current_nonRewriteUnits: Set[T] = Set()
  private var derivationCl: Option[T] = None
  private var current_externalProvers: Set[TptpProver[T]] = Set()

  private final val sig: Signature = initSignature
  private final val mpq: MultiPriorityQueue[T] = MultiPriorityQueue.empty

  private var symbolsInConjecture0: Set[Signature#Key] = Set.empty
  final def conjecture: T = conjecture0
  final def setConjecture(conj: T): Unit = {conjecture0 = conj }
  final def negConjecture: T = negConjecture0
  final def setNegConjecture(negConj: T): Unit = negConjecture0 = negConj
  final def symbolsInConjecture: Set[Signature#Key] = symbolsInConjecture0

  final def signature: Signature = sig
  final def szsStatus: StatusSZS = current_szs
  final def setSZSStatus(szs: StatusSZS): Unit =  {current_szs = szs}

  private var poly: Boolean = false
  final def isPolymorphic: Boolean = poly
  final def setPolymorphic: Unit = {poly = true}

  final def defConjSymbols(negConj: T): Unit = {
    assert(Clause.unit(negConj.cl))
    val lit = negConj.cl.lits.head
    assert(!lit.equational)
    val term = lit.left
    symbolsInConjecture0 = term.symbols.distinct intersect signature.allUserConstants
    leo.Out.trace(s"Set Symbols in conjecture: " +
      s"${symbolsInConjecture0.map(signature(_).name).mkString(",")}")
  }
  final def initUnprocessed(): Unit = {
    import leo.datastructures.ClauseProxyOrderings._
    val conjSymbols: Set[Signature#Key] = symbolsInConjecture0
    mpq.addPriority(litCount_conjRelSymb(conjSymbols, 0.005f, 100, 50).asInstanceOf[Ordering[T]])
    mpq.addPriority(goals_SymbWeight(100,20).asInstanceOf[Ordering[T]])
    mpq.addPriority(goals_litCount_SymbWeight(100,20).asInstanceOf[Ordering[T]])
    mpq.addPriority(nongoals_litCount_SymbWeight(100,20).asInstanceOf[Ordering[T]])
    mpq.addPriority(conjRelSymb(conjSymbols, 0.005f, 100, 50).asInstanceOf[Ordering[T]])
    mpq.addPriority(sos_conjRelSymb(conjSymbols, 0.05f, 2, 1).asInstanceOf[Ordering[T]])
    mpq.addPriority(oldest_first.asInstanceOf[Ordering[T]])
  }
  final def unprocessedLeft: Boolean = !mpq.isEmpty
  final def unprocessed: Set[T] = {
    if (mpq == null) leo.Out.comment("MPQ null")
    mpq.toSet
  }
  final private val prio_weights = Seq(10,1,1,2,10,2,1)
  private var cur_prio = 0
  private var cur_weight = 0
  final def nextUnprocessed: T = {
    leo.Out.trace(s"[###] Selecting with priority $cur_prio: element $cur_weight")
    if (cur_weight >= prio_weights(cur_prio)) {
      cur_weight = 0
      cur_prio = (cur_prio + 1) % mpq.priorities
    }
    val result = mpq.dequeue(cur_prio)
    cur_weight = cur_weight+1
    result
  }

  final def addUnprocessed(cl: T): Unit = {mpq.insert(cl)}
  final def addUnprocessed(cls: Set[T]): Unit = {mpq.insert(cls)}
  final def removeUnprocessed(cls: Set[T]): Unit = {mpq.remove(cls)}

  final def processed: Set[T] = current_processed
  final def addProcessed(cl: T): Unit = { current_processed = current_processed + cl }
  final def removeProcessed(cls: Set[T]): Unit = {current_processed = current_processed -- cls}

  final def rewriteRules: Set[T] = current_rewriterules
  final def addRewriteRule(cl: T): Unit = {current_rewriterules = current_rewriterules + cl}
  final def nonRewriteUnits: Set[T] = current_nonRewriteUnits
  final def addNonRewriteUnit(cl: T): Unit = {current_nonRewriteUnits = current_nonRewriteUnits + cl}
  final def removeUnits(cls: Set[T]): Unit = {
    current_rewriterules = current_rewriterules diff cls
    current_nonRewriteUnits = current_nonRewriteUnits diff cls
  }


  private var choiceFunctions0: Map[Type, Set[Term]] = Map()
  final def addChoiceFunction(f: Term): Unit = {
    if (choiceFunctions0.isDefinedAt(f.ty)) {
      choiceFunctions0 = choiceFunctions0 + ((f.ty, choiceFunctions0(f.ty) + f))
    } else choiceFunctions0 = choiceFunctions0 + ((f.ty, Set(f)))
  }
  final def choiceFunctions: Map[Type,Set[Term]] = choiceFunctions0
  final def choiceFunctionCount: Int = {choiceFunctions0.map {case (k,v) => v.size}.sum}

  final def setDerivationClause(cl: T): Unit = {derivationCl = Some(cl)}
  final def derivationClause: Option[T] = derivationCl

  private var initialProblem0: Set[T] = Set()
  final def addInitial(cls: Set[T]): Unit = {initialProblem0 = initialProblem0 union cls}
  final def initialProblem: Set[T] = initialProblem0

  final def externalProvers: Set[TptpProver[T]] = current_externalProvers
  final def addExternalProver(prover: TptpProver[T]): Unit =  {
    current_externalProvers = current_externalProvers + prover
  }
  // Statistics
  private var generatedCount: Int = 0
  private var loopCount: Int = 0
  private var rewriteCount: Long = 0L
  private var trivialCount: Int = 0
  private var forwardSubsumedCount: Int = 0
  private var backwardSubsumedCount: Int = 0
  private var descendantsDeleted: Int = 0
  private var factorCount: Int = 0
  private var paramodCount: Int = 0
  private var choiceInstantiations0: Int = 0

  final def noProofLoops: Long = loopCount
  final def noProcessedCl: Int = processed.size
  final def noGeneratedCl: Int = generatedCount
  final def noTrivialCl: Int = trivialCount
  final def noParamod: Int = paramodCount
  final def noFactor: Int = factorCount
  final def noForwardSubsumedCl: Int = forwardSubsumedCount
  final def noBackwardSubsumedCl: Int = backwardSubsumedCount
  final def noDescendantsDeleted: Int = descendantsDeleted
  final def choiceInstantiations: Int = choiceInstantiations0

  final def incProofLoopCount(): Unit = {loopCount += 1}
  final def incGeneratedCl(by: Int): Unit = {generatedCount += by}
  final def incTrivialCl(): Unit = {trivialCount += 1}
  final def incParamod(by: Int): Unit = {paramodCount += by}
  final def incFactor(by: Int): Unit = {factorCount += by}
  final def incForwardSubsumedCl(): Unit = {forwardSubsumedCount += 1}
  final def incBackwardSubsumedCl(): Unit = {backwardSubsumedCount += 1}
  final def incForwardSubsumedCl(n: Int): Unit = {forwardSubsumedCount += n}
  final def incBackwardSubsumedCl(n: Int): Unit = {backwardSubsumedCount += n}
  final def incDescendantsDeleted(n: Int): Unit = {descendantsDeleted += n}
  final def incChoiceInstantiations(n: Int): Unit = {choiceInstantiations0 += n}

  // Pretty
  final def pretty: String = s"State SZS: ${szsStatus.pretty}, #processed: $noProcessedCl"
}
