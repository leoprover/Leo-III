package leo.modules.seqpproc

import leo.datastructures._
import leo.modules.output.{SZS_Unknown, StatusSZS}


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

  def unprocessedLeft: Boolean
  def unprocessed: Set[T]
  def nextUnprocessed: T
  def addUnprocessed(unprocessed: T): Unit
  def addUnprocessed(unprocessed: Set[T]): Unit
  def processed: Set[T]
  def setProcessed(p: Set[T]): Unit
  def addProcessed(cl: T): Unit

  def rewriteRules: Set[T]
  def addRewriteRule(cl: T): Unit

  def addChoiceFunction(f: Term): Unit
  def choiceFunctions: Map[Type, Set[Term]]
  final def choiceFunctions(ty: Type): Set[Term] = choiceFunctions.getOrElse(ty, Set())

  def setDerivationClause(cl: T): Unit
  def derivationClause: Option[T]
}

trait StateStatistics {
  // Statistics
  def noProcessedCl: Int
  def incTrivialCl(): Unit
  def noTrivialCl: Int
  def incForwardSubsumedCl(): Unit
  def incForwardSubsumedCl(n: Int): Unit
  def noForwardSubsumedCl: Int
  def incBackwardSubsumedCl(): Unit
  def incBackwardSubsumedCl(n: Int): Unit
  def noBackwardSubsumedCl: Int
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
  private var derivationCl: Option[T] = None

  private final val sig: Signature = initSignature
  private final val mpq: MultiPriorityQueue[T] = MultiPriorityQueue.empty
  mpq.addPriority(leo.datastructures.ClauseProxyOrderings.lex_weightAge.reverse.asInstanceOf[Ordering[T]])
  mpq.addPriority(leo.datastructures.ClauseProxyOrderings.fifo.asInstanceOf[Ordering[T]])
  mpq.addPriority(leo.datastructures.ClauseProxyOrderings.goalsfirst.reverse.asInstanceOf[Ordering[T]])
  mpq.addPriority(leo.datastructures.ClauseProxyOrderings.nongoalsfirst.reverse.asInstanceOf[Ordering[T]])
  final private val prio_weights = Seq(8,1,2,2)
  private var cur_prio = 0
  private var cur_weight = 0

  private var symbolsInConjecture0: Set[Signature#Key] = Set()
  final def conjecture: T = conjecture0
  final def setConjecture(conj: T): Unit = {
    conjecture0 = conj
    val conjClauseIt = conj.cl.lits.iterator
    while (conjClauseIt.hasNext) {
      val lit = conjClauseIt.next()
      symbolsInConjecture0 = symbolsInConjecture0 union lit.left.symbols.distinct
      symbolsInConjecture0 = symbolsInConjecture0 union lit.right.symbols.distinct
    }
    symbolsInConjecture0 = symbolsInConjecture0 intersect signature.allUserConstants
  }
  final def negConjecture: T = negConjecture0
  final def setNegConjecture(negConj: T): Unit = { negConjecture0 = negConj }
  final def symbolsInConjecture: Set[Signature#Key] = symbolsInConjecture0

  final def signature: Signature = sig
  final def szsStatus: StatusSZS = current_szs
  final def setSZSStatus(szs: StatusSZS): Unit =  {current_szs = szs}

  final def unprocessedLeft: Boolean = !mpq.isEmpty
  final def unprocessed: Set[T] = {
    if (mpq == null) leo.Out.comment("MPQ null")
    mpq.toSet
  }
  final def nextUnprocessed: T = {
    leo.Out.debug(s"[###] Selecting with priority $cur_prio: element $cur_weight")
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

  final def processed: Set[T] = current_processed
  final def setProcessed(c: Set[T]): Unit = {current_processed = c}
  final def addProcessed(cl: T): Unit = { current_processed = current_processed + cl }

  final def rewriteRules: Set[T] = current_rewriterules
  final def addRewriteRule(cl: T): Unit = {current_rewriterules = current_rewriterules + cl}

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

  // Statistics
  private var generatedCount: Int = 0
  private var rewriteCount: Int = 0
  private var trivialCount: Int = 0
  private var forwardSubsumedCount: Int = 0
  private var backwardSubsumedCount: Int = 0
  private var factorCount: Int = 0
  private var paramodCount: Int = 0
  private var choiceInstantiations0: Int = 0

  final def noProcessedCl: Int = processed.size
  final def noGeneratedCl: Int = generatedCount
  final def noTrivialCl: Int = trivialCount
  final def noParamod: Int = paramodCount
  final def noFactor: Int = factorCount
  final def noForwardSubsumedCl: Int = forwardSubsumedCount
  final def noBackwardSubsumedCl: Int = backwardSubsumedCount
  final def choiceInstantiations: Int = choiceInstantiations0

  final def incGeneratedCl(by: Int): Unit = {generatedCount += by}
  final def incTrivialCl(): Unit = {trivialCount += 1}
  final def incParamod(by: Int): Unit = {paramodCount += by}
  final def incFactor(by: Int): Unit = {factorCount += by}
  final def incForwardSubsumedCl(): Unit = {forwardSubsumedCount += 1}
  final def incBackwardSubsumedCl(): Unit = {backwardSubsumedCount += 1}
  final def incForwardSubsumedCl(n: Int): Unit = {forwardSubsumedCount += n}
  final def incBackwardSubsumedCl(n: Int): Unit = {backwardSubsumedCount += n}
  final def incChoiceInstantiations(n: Int): Unit = {choiceInstantiations0 += n}

  // Pretty
  final def pretty: String = s"State SZS: ${szsStatus.pretty}, #processed: $noProcessedCl"
}
