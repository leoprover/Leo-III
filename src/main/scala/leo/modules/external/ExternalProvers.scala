package leo.modules.external

import java.nio.file.{Files, Path, Paths}
import java.nio.charset.StandardCharsets
import java.nio.file.attribute.PosixFilePermissions

import scala.io.{BufferedSource, Codec}
import leo.Configuration
import leo.datastructures._

/**
  * Object to construct provers from their paths.
  * Checks for the commands, for their executability.
  */
object ExternalProvers {
  private final val SCRIPTDIR_NAME: String = "ext_scripts"
  private final lazy val SCRIPTDIR: Path = Configuration.LEODIR.resolve(SCRIPTDIR_NAME)
  private final lazy val LIMITEDRUN: Path = SCRIPTDIR.resolve("TreeLimitedRun")

  final def cleanup(): Unit = {
    if (Configuration.isSet("atpdebug")) return

    try {
      if (Files.exists(LIMITEDRUN)) LIMITEDRUN.toFile.delete()
      val cvc4RunScript = SCRIPTDIR.resolve(CVC4.executeScriptName)
      if (Files.exists(cvc4RunScript)) cvc4RunScript.toFile.delete()
      if (Files.exists(SCRIPTDIR)) SCRIPTDIR.toFile.delete()
    } catch {
      case e:Exception =>
        leo.Out.warn("Exception while cleaning up temporary files:")
        leo.Out.warn(e.toString)
    }

  }
  /**
    * Creates a prover `name` with an executable at the path `path`.
    * Throws an [[NoSuchMethodException]] if this prover cannot be executed.
    *
    * @param name name of the prover
    * @param path path of the prover
    * @return an abstracted prover `name`
    */
  @throws[NoSuchMethodException]
  final def createProver(name: String, path : String) : TPTPProver[AnnotatedClause] = {
    createTreeLimitedRunScript()
    name match {
      case "leo2" => createLeo2(path)
      case "nitpick" => createNitpickProver(path)
      case "cvc4" => createCVC4(path)
      case "vampire" => createVampire(path)
      case "iprover" => createIProver(path)
      case "e" | "eprover" => createEProver(path)
      case "satallax" => createSatallax(path)
      case "zipperposition" => createZipperposition(path)
      case _ => throw new NoSuchMethodException(s"$name not supported by Leo-III. Valid values are: leo2, nitpick, cvc4," +
                                                  "vampire, iprover, eprover, satallax, zipperposition.")
    }
  }
  private final def createTreeLimitedRunScript(): Unit = {
    if (!Files.exists(SCRIPTDIR)) Files.createDirectory(SCRIPTDIR)
    assert(Files.exists(SCRIPTDIR), "SCRIPTDIR not created")
    if (!Files.exists(LIMITEDRUN)) {
      val filePermissionAttribute = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr--"))
      val file = Files.createFile(LIMITEDRUN, filePermissionAttribute)
      val limitedRunScript = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/scripts/TreeLimitedRun"))(Codec.ISO8859)
      Files.write(file, limitedRunScript.mkString.getBytes(StandardCharsets.ISO_8859_1))
      file.toFile.setExecutable(true)
      assert(Files.exists(LIMITEDRUN), "LIMITEDRUN not created")
    }
  }
  private final def serviceToPath(cmd : String) : Path = {
    import scala.sys.process._
    val p = Paths.get(cmd)
    if(Files.exists(p) && Files.isExecutable(p)){
      p
    } else {
      if (Configuration.isSet("atpdebug")) {
        Process(Seq("which", cmd)).!(ProcessLogger(line => println(line), line => println(line)))
      }

      val redirectStdErrLogger = ProcessLogger(_ => ())
      val which0 = Seq("which", cmd) lazyLines_! redirectStdErrLogger
      val which = which0.headOption
      if (which.isDefined) {
        val p2 = Paths.get(which.get)
        if(Files.exists(p2) && Files.isExecutable(p2)){
          p2
        } else {
          throw new NoSuchMethodException(s"'$cmd' is not executable or does not exist.")
        }
      } else {
        throw new NoSuchMethodException(s"'$cmd' is not executable or does not exist.")
      }
    }
  }

  /**
    * Creates LEO-II with the executable `path`.
    * Throws an [[NoSuchMethodException]] if this prover cannot be executed.
    *
    * @param path path of the prover
    * @return an abstracted instance of LEO-II
    */
  @throws[NoSuchMethodException]
  private def createLeo2(path : String) : Leo2Prover = {
    val p = if(path == "") serviceToPath("leo") else serviceToPath(path)
    val convert = p.toAbsolutePath.toString
    leo.Out.debug(s"Created Leo2 prover with path '$convert'")
    if (Configuration.isSet("atpdebug")) {
      import scala.sys.process._
      val answer = Process.apply(Seq(convert, "--version")).lazyLines_!
      leo.Out.comment(s"Leo 2 debug info:")
      leo.Out.comment(answer.mkString)
    }
    new Leo2Prover(convert)
  }

  @throws[NoSuchMethodException]
  private def createSatallax(path : String) : SatallaxProver = {
    val p = if(path == "") serviceToPath("satallax") else serviceToPath(path)
    val convert = p.toAbsolutePath.toString
    leo.Out.debug(s"Created Satallax prover with path '$convert'")
    if (Configuration.isSet("atpdebug")) {
      import scala.sys.process._
      val answer = Process.apply(Seq(convert, "-V")).lazyLines_!
      leo.Out.comment(s"Satllax debug info:")
      leo.Out.comment(answer.mkString)
    }
    new SatallaxProver(convert)
  }

  @throws[NoSuchMethodException]
  private def createEProver(path : String) : EProver = {
    val p = if(path == "") serviceToPath("eprover") else serviceToPath(path)
    val convert = p.toAbsolutePath.toString
    leo.Out.debug(s"Created EProver prover with path '$convert'")
    if (Configuration.isSet("atpdebug")) {
      import scala.sys.process._
      val answer = Process.apply(Seq(convert, "--version")).lazyLines_!
      leo.Out.comment(s"EProver debug info:")
      leo.Out.comment(answer.mkString)
    }
    new EProver(convert)
  }

  @throws[NoSuchMethodException]
  private def createIProver(path : String) : IProver = {
    val p = if(path == "") serviceToPath("iprover") else serviceToPath(path)
    val convert = p.toAbsolutePath.toString
    leo.Out.debug(s"Created IProver prover with path '$convert'")
    if (Configuration.isSet("atpdebug")) {
      import scala.sys.process._
      val answer = Process.apply(Seq(convert, "--help")).lazyLines_!
      leo.Out.comment(s"IProver debug info:")
      leo.Out.comment(answer.mkString)
    }
    new IProver(convert)
  }

  /**
    * Creates a callable instance of Nitpick.
    * The given `path` has to reference the isabelle system.
    *
    * Throws an [[NoSuchMethodException]] if this prover cannot be executed.
    *
    * @param path path of the prover
    * @return an abstracted instance of Nitpick
    */
  @throws[NoSuchMethodException]
  private def createNitpickProver(path : String) : NitpickProver = {
    val p = if(path == "") serviceToPath("isabelle") else serviceToPath(path)
    val convert = p.toAbsolutePath.toString
    leo.Out.debug(s"Created Nitpick prover with path '$convert' (Isabelle)")
    new NitpickProver(convert)
  }

  private final def createCVC4(path : String) : CVC4 = {
    createCVC4RunScript()
    val p = if(path == "") serviceToPath("cvc4") else serviceToPath(path)
    val convert = p.toAbsolutePath.toString
    leo.Out.debug(s"Created CVC4 prover with path '$convert'")
    if (Configuration.isSet("atpdebug")) {
      import scala.sys.process._
      val answer = Process.apply(Seq(convert, "--version")).lazyLines_!
      leo.Out.comment(s"Cvc4 debug info:")
      leo.Out.comment(answer.mkString)
    }
    CVC4(SCRIPTDIR.resolve(CVC4.executeScriptName).toString, convert)
  }
  private final def createCVC4RunScript(): Unit = {
    val cvc4RunPath = SCRIPTDIR.resolve(CVC4.executeScriptName)
    if (!Files.exists(cvc4RunPath)) {
      val filePermissionAttribute = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr--"))
      val file = Files.createFile(cvc4RunPath, filePermissionAttribute)
      Files.write(file, CVC4.executeScript.mkString.getBytes(StandardCharsets.UTF_8))
      file.toFile.setExecutable(true)
    }
  }

  private final def createVampire(path: String) : Vampire = {
    val p = if(path == "") serviceToPath("vampire") else serviceToPath(path)
    val convert = p.toAbsolutePath.toString
    leo.Out.debug(s"Created Vampire prover with path '$convert'")
    new Vampire(convert)
  }

  @throws[NoSuchMethodException]
  private def createZipperposition(path : String) : Zipperposition = {
    val p = if(path == "") serviceToPath("zipperposition") else serviceToPath(path)
    val convert = p.toAbsolutePath.toString
    leo.Out.debug(s"Created Zipperposition prover with path '$convert'")
    if (Configuration.isSet("atpdebug")) {
      import scala.sys.process._
      val answer = Process.apply(Seq(convert, "--version")).lazyLines_!
      leo.Out.comment(s"Zipperposition debug info:")
      leo.Out.comment(answer.mkString)
    }
    new Zipperposition(convert)
  }

  final def limitedRun(timeout: Int, args: Seq[String]): Seq[String] = {
    Seq(LIMITEDRUN.toString, String.valueOf(timeout), String.valueOf(timeout)) ++ args
  }
}

//////////////////////////////////////////////////////////////////////
// Individual TPTP provers
//////////////////////////////////////////////////////////////////////

class Vampire(val path : String) extends TPTPProver[AnnotatedClause] {
  final val name: String = "vampire"
  final val capabilities: Capabilities.Info = Capabilities(Capabilities.TFF -> Seq())

  protected[external] def constructCall(args: Seq[String], timeout: Int,
                                        problemFileName: String): Seq[String] = {
    ExternalProvers.limitedRun(timeout+2, Seq(path, "--mode", "casc", "-t", timeout.toString, problemFileName))
  }
}

class CVC4(execScript: String, val path: String) extends TPTPProver[AnnotatedClause] {
  final val name: String = "cvc4"
  final val capabilities: Capabilities.Info = Capabilities(Capabilities.TFF -> Seq())

  protected[external] def constructCall(args: Seq[String], timeout: Int,
                                        problemFileName: String): Seq[String] = {
    ExternalProvers.limitedRun(timeout+2, Seq(execScript, path, problemFileName))
  }
}
object CVC4 {
  @inline final def apply(execScript: String, path: String): CVC4 = new CVC4(execScript, path)
  final val executeScriptName: String = "run-script-cascj8-tfa"
  final def executeScript: BufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream(s"/scripts/$executeScriptName"))
}

class EProver(val path : String) extends TPTPProver[AnnotatedClause] {
  final val name: String = "e"
  final val capabilities: Capabilities.Info = Capabilities(Capabilities.TFF -> Seq())

  protected[external] def constructCall(args: Seq[String], timeout: Int,
                                        problemFileName: String): Seq[String] = {
    ExternalProvers.limitedRun(timeout+2, Seq(path,
      "--delete-bad-limit=2000000000",
      "--definitional-cnf=24",
      "--output-level=0",
      "--proof-object=1",
      "--auto-schedule",
      "--split-clauses=4",
      "--split-reuse-defs",
      "--simul-paramod",
      "--forward-context-sr",
      "--destructive-er-aggressive",
      "--destructive-er",
      "--presat-simplify",
      "--prefer-initial-clauses",
      "--term-ordering=KBO6",
      "-WSelectMaxLComplexAvoidPosPred",
      "-winvfreqrank",
      "-c1",
      "-Ginvfreqconjmax",
      "-F1",
      s"--cpu-limit=${timeout.toString}", problemFileName))
  }

  // E uses "#" instead of the TPTP standard "%"
  override def transformOutput(output: Seq[String]): Seq[String] =
    output.map { line =>
      if (line.startsWith("#")) '%' +: line.tail
      else line
    }
}

class IProver(val path : String) extends TPTPProver[AnnotatedClause] {
  final val name: String = "iprover"
  final val capabilities: Capabilities.Info = Capabilities(Capabilities.TFF -> Seq(), Capabilities.CNF -> Seq())

  protected[external] def constructCall(args: Seq[String], timeout: Int,
                                        problemFileName: String): Seq[String] = {


    ExternalProvers.limitedRun(timeout, Seq(path, "--time_out_real", timeout.toString, problemFileName) ++ args)
  }
}

class Zipperposition(val path: String) extends TPTPProver[AnnotatedClause] {
  final val name: String = "Zipperposition"
  final val capabilities: Capabilities.Info = Capabilities(Capabilities.TFF -> Seq(Capabilities.Polymorphism))

  protected[external] def constructCall(args: Seq[String], timeout: Int,
                                        problemFileName: String): Seq[String] = {
    ExternalProvers.limitedRun(timeout+2, Seq(path, problemFileName, "--timeout", String.valueOf(timeout)))
  }
}
object Zipperposition {
  @inline final def apply(path: String): Zipperposition = new Zipperposition(path)
}



class Leo2Prover(val path : String) extends TPTPProver[AnnotatedClause] {
  override val name: String = "leo2"

  final val capabilities: Capabilities.Info = Capabilities(Capabilities.THF -> Seq())

  override protected[external] def constructCall(args: Seq[String], timeout: Int, problemFileName: String): Seq[String] = {
    val timeout0 = if (timeout < 60) 60 else timeout
    val call0 = Seq(path, "-t", timeout0.toString) ++ args ++ Seq(problemFileName)
    ExternalProvers.limitedRun(timeout, call0)
  }
}

class SatallaxProver(val path : String) extends TPTPProver[AnnotatedClause] {
  override val name: String = "satallax"

  final val capabilities: Capabilities.Info = Capabilities(Capabilities.THF -> Seq())

  override protected[external] def constructCall(args: Seq[String], timeout: Int, problemFileName: String): Seq[String] = {
    val timeout0 = if (timeout < 60) 60 else timeout
    val call0 = Seq(path, "-p", "tstp" ,"-t" , timeout0.toString) ++ args ++ Seq(problemFileName)
    ExternalProvers.limitedRun(timeout, call0)
  }
}


class NitpickProver(val path : String) extends TPTPProver[AnnotatedClause] {
  override def name: String = "nitpick"

  final val capabilities: Capabilities.Info = Capabilities(Capabilities.THF -> Seq())

  // nitpick needs a lot of time for start-up. give it 15seconds more
  override protected def constructCall(args: Seq[String], timeout: Int, problemFileName: String): Seq[String] = {
    ExternalProvers.limitedRun(timeout+15, Seq(path, "tptp_nitpick", timeout.toString) ++ Seq(problemFileName))
  }
}
