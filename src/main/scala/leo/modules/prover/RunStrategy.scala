package leo.modules.prover

import leo.datastructures.Pretty

/**
  * Representation of a single-run strategy.
  * An object essentially captures a concrete
  * parameter setting combination.
  *
  * @param share Timeout
  * @param primSubst Primsubst level
  * @param sos Set of support flag
  * @param unifierCount Maximum number of unifiers
  * @param uniDepth Search depth for pre-unification
  *
  * @author Alexander Steen
  */
final case class RunStrategy(share: Float,
                             primSubst: Int,
                             sos: Boolean,
                             unifierCount: Int,
                             uniDepth: Int,
                             boolExt: Boolean,
                             choice: Boolean,
                             renaming: Boolean,
                             funcspec: Boolean,
                             domConstr : Int) extends Pretty {
  def pretty: String = s"strategy<share($share),primSubst($primSubst),sos($sos)," +
    s"unifierCount($unifierCount),uniDepth($uniDepth),boolExt($boolExt),choice($choice)," +
    s"renaming($renaming),funcspec($funcspec)>"
}

object RunStrategy {
  import leo.Configuration

  /** Return the [[leo.modules.prover.RunStrategy]] that is given by
    * the default values of [[leo.Configuration]]. */
  def defaultStrategy: RunStrategy =
    RunStrategy(1f,
      Configuration.PRIMSUBST_LEVEL,
      Configuration.SOS,
      Configuration.UNIFIER_COUNT,
      Configuration.UNIFICATION_DEPTH,
      Configuration.DEFAULT_BOOLEXT,
      Configuration.DEFAULT_CHOICE,
      Configuration.DEFAULT_RENAMING,
      Configuration.DEFAULT_FUNCSPEC,
      Configuration.DEFAULT_DOMCONSTR)


  final def byName(str: String): RunStrategy = str match {
    case "s1" => s1
    case "s1b" => s1b
    case "s2" => s2
    case "s2b" => s2b
    case "s3" => s3
    case "s3b" => s3b
    case "default" => defaultStrategy
    case _ => defaultStrategy
  }

  /////////////////////////
  /// Some (maybe) useful strategies
  /////////////////////////

  def s1: RunStrategy = RunStrategy(
    share = 1,
    primSubst = 1,
    sos = false,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  true,
    funcspec = false,
    domConstr = 0)

  def s1b: RunStrategy = RunStrategy(
    share = 1,
    primSubst = 1,
    sos = false,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  false,
    funcspec = false,
    domConstr = 0)

  def s2: RunStrategy = RunStrategy(
    share = 1,
    primSubst = 3,
    sos = false,
    unifierCount = 3,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  true,
    funcspec = false,
    domConstr = 0)

  def s2b: RunStrategy = RunStrategy(
    share = 1,
    primSubst = 3,
    sos = false,
    unifierCount = 3,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming = false,
    funcspec = false,
    domConstr = 0)

  def s3: RunStrategy = RunStrategy(
    share = 1,
    primSubst = 1,
    sos = true,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  true,
    funcspec = false,
    domConstr = 0)

  def s3b: RunStrategy = RunStrategy(
    share = 1,
    primSubst = 3,
    sos = true,
    unifierCount = 3,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  true,
    funcspec = false,
    domConstr = 0)
}

