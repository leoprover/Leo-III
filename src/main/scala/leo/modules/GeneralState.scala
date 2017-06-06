package leo.modules

import leo.datastructures.{Clause, ClauseProxy, Pretty, Signature}
import leo.modules.external.TptpProver
import leo.modules.prover.{RunStrategy, State}

/**
  *
  * Abstract state of the prover.
  * Capsules the signature of the current
  * prover run as well as the strategie
  * and important details from the original problem file
  *
  */
trait GeneralState[T <: ClauseProxy] extends Pretty {
  def copy: GeneralState[T]

  def conjecture: T
  def negConjecture: T
  def setConjecture(conj: T): Unit
  def setNegConjecture(negConj: T): Unit

  def signature: Signature

  def runStrategy: RunStrategy
  def setRunStrategy(runStrategy: RunStrategy): Unit

  def symbolsInConjecture: Set[Signature#Key]
  def defConjSymbols(negConj: T): Unit

  def addInitial(cls: Set[T]): Unit
  def initialProblem: Set[T]
  def externalProvers: Set[TptpProver[T]]
  def addExternalProver(prover: TptpProver[T]): Unit

  def isPolymorphic: Boolean
  def setPolymorphic(): Unit
}

object GeneralState {
  def fresh[T <: ClauseProxy](sig : Signature) : GeneralState[T] = new GeneralStateImp[T](sig)
  def fresh[T <: ClauseProxy](sig : Signature, strategy : RunStrategy) : GeneralState[T] = {
    val s = new GeneralStateImp[T](sig)
    s.setRunStrategy(strategy)
    s
  }
}

protected[modules] class GeneralStateImp[T <: ClauseProxy](sig : Signature) extends GeneralState[T] {
  protected var conjecture0: T = _
  protected var negConjecture0: T = _
  protected var runStrategy0: RunStrategy = _
  protected var symbolsInConjecture0: Set[Signature#Key] = Set.empty
  protected var current_externalProvers: Set[TptpProver[T]] = Set()
  protected var initialProblem0: Set[T] = Set()
  protected var poly: Boolean = false


  def copy: GeneralState[T] = {
    val state = new GeneralStateImp[T](sig)
    state.conjecture0 = conjecture0
    state.negConjecture0 = negConjecture0
    state.runStrategy0 = runStrategy0
    state.symbolsInConjecture0 = symbolsInConjecture0
    state
  }

  final def conjecture: T = conjecture0
  final def setConjecture(conj: T): Unit = {conjecture0 = conj }
  final def negConjecture: T = negConjecture0
  final def setNegConjecture(negConj: T): Unit = negConjecture0 = negConj

  final def signature: Signature = sig
  final def runStrategy: RunStrategy = runStrategy0
  final def setRunStrategy(runStrategy: RunStrategy): Unit = {runStrategy0 = runStrategy}

  final def symbolsInConjecture: Set[Signature#Key] = symbolsInConjecture0
  final def defConjSymbols(negConj: T): Unit = {
    assert(Clause.unit(negConj.cl))
    val lit = negConj.cl.lits.head
    assert(!lit.equational)
    val term = lit.left
    symbolsInConjecture0 = term.symbols.distinct intersect signature.allUserConstants
    leo.Out.trace(s"Set Symbols in conjecture: " +
      s"${symbolsInConjecture0.map(signature(_).name).mkString(",")}")
  }

  final def isPolymorphic: Boolean = poly
  final def setPolymorphic(): Unit = {poly = true}

  final def addInitial(cls: Set[T]): Unit = {initialProblem0 = initialProblem0 union cls}
  final def initialProblem: Set[T] = initialProblem0

  final def externalProvers: Set[TptpProver[T]] = current_externalProvers
  final def addExternalProver(prover: TptpProver[T]): Unit =  {
    current_externalProvers = current_externalProvers + prover
  }

  override def pretty: String = s"State (conj = ${conjecture0.pretty(sig)} , strategy = ${runStrategy0.pretty})"
}
