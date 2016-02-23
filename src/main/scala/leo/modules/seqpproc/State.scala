package leo.modules.seqpproc

import leo.datastructures.IsSignature
import leo.datastructures.Clause
import leo.modules.output.StatusSZS

trait ClauseProxy {
  def cl: Clause
}

/**
  * Created by lex on 20.02.16.
  */
trait State[T <: ClauseProxy] {

  def signature: IsSignature
  def szsStatus: StatusSZS

  def axioms: Set[T]
  def disabledAxioms: Set[T]

  def unprocessed: Set[T]
  def addUnprocessed(unprocessed: T): Boolean
  def addUnprocessed(unprocessed: Set[T]): Boolean
  def processed: Set[T]
  def setProcessed(cl: T): Boolean

  def rewriteRules: Set[T]


  // Statistics
  def noProcessedCl: Int
  def noTrivialCl: Int
  def noForwardSubsumedCl: Int
  def noBackwardSubsumedCl: Int
  def noRewrittenCl: Int
  def noRewrittenLit: Int
  def noGeneratedCl: Int
  def noGeneratedLit: Int
  def noContextSplits: Int
  def noParamod: Int
  def noFactor: Int
}

object State {
  def fresh[T <: ClauseProxy](sig: IsSignature): State[T] = ???
}
