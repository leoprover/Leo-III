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
final case class RunStrategy(name: String,
                             share: Float,
                             primSubst: Int,
                             sos: Boolean,
                             unifierCount: Int,
                             uniDepth: Int,
                             boolExt: Boolean,
                             choice: Boolean,
                             renaming: Boolean,
                             funcspec: Boolean,
                             domConstr : Int,
                             specialInstances: Int) extends Pretty {
  def pretty: String = s"strategy<name($name),share($share),primSubst($primSubst),sos($sos)," +
    s"unifierCount($unifierCount),uniDepth($uniDepth),boolExt($boolExt),choice($choice)," +
    s"renaming($renaming),funcspec($funcspec), domConstr($domConstr),specialInstances($specialInstances)>"

  def runStandandalone : Boolean = {
//    primSubst >= 2
    false
  }
}

object RunStrategy {
  import leo.Configuration

  /** Return the [[leo.modules.prover.RunStrategy]] that is given by
    * the default values of [[leo.Configuration]]. */
  def defaultStrategy: RunStrategy =
    RunStrategy("default",
      1f,
      Configuration.PRIMSUBST_LEVEL,
      Configuration.SOS,
      Configuration.UNIFIER_COUNT,
      Configuration.UNIFICATION_DEPTH,
      Configuration.DEFAULT_BOOLEXT,
      Configuration.DEFAULT_CHOICE,
      Configuration.RENAMING_SET,
      Configuration.FUNCSPEC,
      Configuration.DEFAULT_DOMCONSTR,
      Configuration.PRE_PRIMSUBST_LEVEL)


  final def byName(str: String): RunStrategy = str match {
    case "s1" => s1
    case "s1a" => s1a
    case "s1b" => s1b
    case "s2" => s2
    case "s3" => s3
    case "s3a" => s3a
    case "s3b" => s3b
    case "s3bb" => s3bb
    case "s4" => s4
    case "s5a" => s5a
    case "s5b" => s5b
    case "s5c" => s5c
    case "s6" => s6
    case "default" => defaultStrategy
    case _ => defaultStrategy
  }

  /////////////////////////
  /// Some (maybe) useful strategies
  /////////////////////////

  def s1: RunStrategy = RunStrategy(
    name = "s1",
    share = 1,
    primSubst = 1,
    sos = false,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  true,
    funcspec = false,
    domConstr = 0,
    specialInstances = -1)

  def s1a: RunStrategy = RunStrategy(
    name = "s1a",
    share = 1,
    primSubst = 1,
    sos = false,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = false,
    renaming =  true,
    funcspec = false,
    domConstr = 0,
    specialInstances = 31)

  def s1b: RunStrategy = RunStrategy(
    name = "s1b",
    share = 1,
    primSubst = 1,
    sos = false,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  false,
    funcspec = false,
    domConstr = 0,
    specialInstances = -1)

  def s2: RunStrategy = RunStrategy(
    name = "s2",
    share = 1,
    primSubst = 2,
    sos = false,
    unifierCount = 3,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  true,
    funcspec = false,
    domConstr = 0,
    specialInstances = -1)

  def s3: RunStrategy = RunStrategy(
    name = "s3",
    share = 0.5f,
    primSubst = 1,
    sos = true,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  true,
    funcspec = false,
    domConstr = 0,
    specialInstances = -1)

  def s3a: RunStrategy = RunStrategy(
    name = "s3a",
    share = 0.5f,
    primSubst = 1,
    sos = true,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = false,
    renaming =  true,
    funcspec = false,
    domConstr = 0,
    specialInstances = -1)

  def s3b: RunStrategy = RunStrategy(
    name = "s3b",
    share = 0.5f,
    primSubst = 3,
    sos = true,
    unifierCount = 3,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  true,
    funcspec = false,
    domConstr = 0,
    specialInstances = -1)

  def s3bb: RunStrategy = RunStrategy(
    name = "s3bb",
    share = 0.5f,
    primSubst = 3,
    sos = true,
    unifierCount = 3,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = false,
    renaming =  false,
    funcspec = false,
    domConstr = 0,
    specialInstances = -1)

  def s4: RunStrategy = RunStrategy(
    name = "s4",
    share = 0.25f,
    primSubst = 3,
    sos = false,
    unifierCount = 3,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming = false,
    funcspec = true,
    domConstr = 0,
    specialInstances = -1)

  def s5a : RunStrategy = RunStrategy (
    name = "s5a",
    share = 1,
    primSubst = 1,
    sos = false,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  true,
    funcspec = false,
    domConstr = -1,
    specialInstances = -1
  )

  def s5b: RunStrategy = RunStrategy(
    name = "s5b",
    share = 1,
    primSubst = 1,
    sos = false,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  false,
    funcspec = false,
    domConstr = -1,
    specialInstances = -1)

  def s5c: RunStrategy = RunStrategy(
    name = "s5c",
    share = 0.5f,
    primSubst = 3,
    sos = true,
    unifierCount = 3,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = true,
    renaming =  true,
    funcspec = false,
    domConstr = -1,
    specialInstances = -1)

  def s6: RunStrategy = RunStrategy(
    name = "s6",
    share = 0.5f,
    primSubst = 1,
    sos = false,
    unifierCount = 1,
    uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
    boolExt = true,
    choice = false,
    renaming =  true,
    funcspec = false,
    domConstr = 0,
    specialInstances = -1)
}

