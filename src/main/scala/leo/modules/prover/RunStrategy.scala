package leo.modules.prover

import leo.datastructures.Pretty

/**
  * Representation of a single-run strategy.
  * An object essentially captures a concrete
  * parameter setting combination.
  *
  * @param timeout Timeout
  * @param primSubst Primsubst level
  * @param sos Set of support flag
  * @param unifierCount Maximum number of unifiers
  * @param uniDepth Search depth for pre-unification
  *
  * @author Alexander Steen
  */
final case class RunStrategy(timeout: Int,
                      primSubst: Int,
                      sos: Boolean,
                      unifierCount: Int,
                      uniDepth: Int) extends Pretty {
  def pretty: String = s"strategy<timeout($timeout),primSubst($primSubst),sos($sos)," +
    s"unifierCount($unifierCount),uniDepth($uniDepth)>"
}

