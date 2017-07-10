package leo.modules

import leo.datastructures._
import leo.modules.external.TptpProver
import leo.modules.output.{SZS_Unknown, StatusSZS}
import leo.modules.prover.{FVIndex, RunStrategy}

import scala.collection.mutable

/**
  *
  * Abstract state of the prover.
  * Capsules the signature of the current
  * prover run as well as the strategie
  * and important details from the original problem file
  *
  */
trait GeneralState[T <: ClauseProxy] extends Pretty {
  def copyGeneral: GeneralState[T]

  def conjecture: T
  def negConjecture: T
  def setConjecture(conj: T): Unit
  def setNegConjecture(negConj: T): Unit

  def szsStatus: StatusSZS
  def setSZSStatus(szs: StatusSZS): Unit

  def setDerivationClause(cl: T): Unit
  def derivationClause: Option[T]

  def signature: Signature

  def timeout: Int
  def setTimeout(timeout: Int): Unit

  def runStrategy: RunStrategy
  def setRunStrategy(runStrategy: RunStrategy): Unit

  def symbolsInConjecture: Set[Signature.Key]
  def defConjSymbols(negConj: T): Unit

  def addChoiceFunction(f: Term): Unit
  def choiceFunctions: Map[Type, Set[Term]]
  final def choiceFunctions(ty: Type): Set[Term] = choiceFunctions.getOrElse(ty, Set())

  def addInitial(cls: Set[T]): Unit
  def initialProblem: Set[T]
  def externalProvers: Set[TptpProver[T]]
  def addExternalProver(prover: TptpProver[T]): Unit

  def isPolymorphic: Boolean
  def setPolymorphic(): Unit

  def domainConstr : Map[Type, Set[Term]]
  def addDomainConstr(ty : Type, instances : Set[Term]) : Unit

  def renamingCash : mutable.Map[Term, (Term, Boolean, Boolean)]
  def resetCash() : Unit
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
  protected var current_szs : StatusSZS = SZS_Unknown
  protected var conjecture0: T = _
  protected var negConjecture0: T = _
  protected var runStrategy0: RunStrategy = _
  protected var symbolsInConjecture0: Set[Signature.Key] = Set.empty
  protected var current_externalProvers: Set[TptpProver[T]] = Set()
  protected var initialProblem0: Set[T] = Set()
  protected var poly: Boolean = false
  protected var derivationCl: Option[T] = None
  protected var choiceFunctions0: Map[Type, Set[Term]] = Map()
  protected var timeout0: Int = _
  protected var domainConstr0 : Map[Type, Set[Term]] = Map()
  val renamingCash : mutable.Map[Term, (Term, Boolean, Boolean)] = mutable.Map()

  final def timeout: Int = timeout0
  final def setTimeout(timeout: Int): Unit = { timeout0 = timeout }


  def copyGeneral: GeneralState[T] = {
    val state = new GeneralStateImp[T](sig)
    state.conjecture0 = conjecture0
    state.negConjecture0 = negConjecture0
    state.runStrategy0 = runStrategy0
    state.symbolsInConjecture0 = symbolsInConjecture0
    state.choiceFunctions0 = choiceFunctions0
    state.current_externalProvers = current_externalProvers
    state.timeout0 = timeout0
    state.domainConstr0 = domainConstr0
    state
  }

  final def conjecture: T = conjecture0
  final def setConjecture(conj: T): Unit = {conjecture0 = conj }
  final def negConjecture: T = negConjecture0
  final def setNegConjecture(negConj: T): Unit = negConjecture0 = negConj

  final def signature: Signature = sig
  final def runStrategy: RunStrategy = runStrategy0
  final def setRunStrategy(runStrategy: RunStrategy): Unit = {runStrategy0 = runStrategy}

  final def symbolsInConjecture: Set[Signature.Key] = symbolsInConjecture0
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

  final def szsStatus: StatusSZS = current_szs
  final def setSZSStatus(szs: StatusSZS): Unit =  {current_szs = szs}

  final def setDerivationClause(cl: T): Unit = {derivationCl = Some(cl)}
  final def derivationClause: Option[T] = derivationCl

  final def addInitial(cls: Set[T]): Unit = {initialProblem0 = initialProblem0 union cls}
  final def initialProblem: Set[T] = initialProblem0

  final def addChoiceFunction(f: Term): Unit = {
    val choiceType = f.ty._funDomainType._funDomainType
    if (choiceFunctions0.isDefinedAt(choiceType)) {
      choiceFunctions0 = choiceFunctions0 + ((choiceType, choiceFunctions0(choiceType) + f))
    } else choiceFunctions0 = choiceFunctions0 + ((choiceType, Set(f)))
    val meta = sig(Term.Symbol.unapply(f).get)
    meta.updateProp(meta.flag | Signature.PropChoice)
  }
  final def choiceFunctions: Map[Type,Set[Term]] = choiceFunctions0
  final def choiceFunctionCount: Int = {choiceFunctions0.map {case (k,v) => v.size}.sum}

  final def externalProvers: Set[TptpProver[T]] = current_externalProvers
  final def addExternalProver(prover: TptpProver[T]): Unit =  {
    current_externalProvers = current_externalProvers + prover
  }



  override def domainConstr: Map[Type, Set[Term]] = domainConstr0
  override def addDomainConstr(ty : Type, instances : Set[Term]): Unit = domainConstr0 += ty -> instances


  override def resetCash(): Unit = renamingCash.clear()

  override def pretty: String = s"State (conj = ${conjecture0.pretty(sig)} , strategy = ${runStrategy0.pretty})"
}


trait FVState[T <: ClauseProxy] extends GeneralState[T] {
  def fVIndex : FVIndex
  def copyFVState : FVState[T]
}

object FVState {
  def fresh[T <: ClauseProxy](sig : Signature) : FVState[T] = new FVStateImpl[T](sig)
  def fresh[T <: ClauseProxy](sig : Signature, strategy : RunStrategy) : FVState[T] = {
    val s = new FVStateImpl[T](sig)
    s.setRunStrategy(strategy)
    s
  }
}

private class FVStateImpl[T <: ClauseProxy](sig : Signature) extends GeneralStateImp[T](sig) with FVState[T] {
  private var current_fVIndex = new FVIndex

  override def copyGeneral: GeneralState[T] = copyFVState

  def copyFVState : FVState[T] = {
    val state = new FVStateImpl[T](sig)
    state.conjecture0 = conjecture0
    state.negConjecture0 = negConjecture0
    state.runStrategy0 = runStrategy0
    state.symbolsInConjecture0 = symbolsInConjecture0
    state.choiceFunctions0 = choiceFunctions0
    state.domainConstr0 = domainConstr0
    state
  }

  override def fVIndex: FVIndex = current_fVIndex
}
