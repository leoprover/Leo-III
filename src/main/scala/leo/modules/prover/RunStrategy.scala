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
                             uniDepth: Int,
                             boolExt: Boolean,
                             choice: Boolean) extends Pretty {
  def pretty: String = s"strategy<timeout($timeout),primSubst($primSubst),sos($sos)," +
    s"unifierCount($unifierCount),uniDepth($uniDepth),boolExt($boolExt),choice($choice)>"

  override def equals(obj: scala.Any): Boolean = obj match {
    case other: RunStrategy =>
      primSubst == other.primSubst &&
        sos == other.sos &&
        unifierCount == other.unifierCount &&
        uniDepth == other.uniDepth &&
        boolExt == other.boolExt &&
        choice == other.choice
    case _ => false
  }

  override def hashCode(): Int = primSubst.hashCode() ^ sos.hashCode() ^
    unifierCount.hashCode() ^ uniDepth.hashCode() ^ boolExt.hashCode() ^ choice.hashCode()
}

