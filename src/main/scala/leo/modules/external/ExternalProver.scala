package leo.modules.external

import java.nio.file.{Files, Paths, Path}
import java.nio.charset.StandardCharsets

import scala.io.{BufferedSource, Codec}
import leo.Configuration
import leo.datastructures._

/**
  * Object to construct provers from their paths.
  * Checks for the commands, for their executability.
  */
object ExternalProver {

  /**
    * Additional time added to the timeout to wait for termination
    */
  final val WAITFORTERMINATION = 1
  final val SCRIPTDIR_NAME: String = "scripts"
  final val SCRIPTDIR: Path = Configuration.LEODIR.resolve(SCRIPTDIR_NAME)
  final val LIMITEDRUN: Path = SCRIPTDIR.resolve("TreeLimitedRun")

  /**
    * Creates a prover `name` with an executable at the path `path`.
    * Throws an [[NoSuchMethodException]] if this prover cannot be executed.
    *
    * @param name name of the prover
    * @param path path of the prover
    * @return an abstracted prover `name`
    */
  @throws[NoSuchMethodException]
  def createProver(name : String, path : String) : TptpProver[AnnotatedClause] = {
    createDirectories()
    name match {
      case "leo2" => createLeo2(path)
      case "nitpick" => createNitpickProver(path)
      case "cvc4" => createCVC4(path)
      case "alt-ergo" => createAltErgo(path)
      case _ => throw new NoSuchMethodException(s"$name not supported by the system.")
    }
  }
  private final def createDirectories(): Unit = {
    val scriptsPath = SCRIPTDIR
    if (!Files.exists(scriptsPath)) Files.createDirectory(scriptsPath)
    // Concrete files to create if not existent:
    val limitedRunPath = scriptsPath.resolve("TreeLimitedRun")
    if (!Files.exists(limitedRunPath)) {
      val file = Files.createFile(limitedRunPath)
      val limitedRunScript = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/scripts/TreeLimitedRun"))(Codec.ISO8859)
      Files.write(file, limitedRunScript.mkString.getBytes(StandardCharsets.ISO_8859_1))
      file.toFile.setExecutable(true)
    }
    val cvc4RunPath = scriptsPath.resolve(CVC4.executeScriptName)
    if (!Files.exists(cvc4RunPath)) {
      val file = Files.createFile(cvc4RunPath)
      Files.write(file, CVC4.executeScript.mkString.getBytes(StandardCharsets.UTF_8))
      file.toFile.setExecutable(true)
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
  def createLeo2(path : String) : Leo2Prover = {
    val p = Paths.get(path)
    if(Files.exists(p) && Files.isExecutable(p)) {
      val convert = p.toAbsolutePath.toString
      leo.Out.debug(s"Created Leo2 prover with path '$convert'")
      new Leo2Prover(convert)
    } else {
      throw new NoSuchMethodException(s"'$path' is not exectuable or does not exist.")
    }
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
  def createNitpickProver(path : String) : NitpickProver = {
    val p = Paths.get(path)
    if(Files.exists(p) && Files.isExecutable(p)) {
      val convert = p.toAbsolutePath.toString
      leo.Out.debug(s"Created Nitpick prover with path '$convert' (Isabelle)")
      new NitpickProver(convert)
    } else {
      throw new NoSuchMethodException(s"'$path' is not exectuable or does not exist.\nTip: Reference only to isablle and not nitpick directly.")
    }
  }

  final def createCVC4(path : String) : CVC4 = {
    val p = Paths.get(path)
    if(Files.exists(p) && Files.isExecutable(p)) {
      val convert = p.toAbsolutePath.toString
      leo.Out.debug(s"Created CVC4 prover with path '$convert'")
      CVC4(SCRIPTDIR.resolve(CVC4.executeScriptName).toString, convert)
    } else {
      throw new NoSuchMethodException(s"'$path' is not exectuable or it does not exist.")
    }
  }

  final def createAltErgo(path: String): AltErgo = {
    val p = Paths.get(path)
    if(Files.exists(p) && Files.isExecutable(p)) {
      val convert = p.toAbsolutePath.toString
      val proc = Command("which why3").exec()
      proc.waitFor()
      if (proc.exitValue != 0) throw new NoSuchMethodException("Why3 executable not found in path. why3 is necessary to run AltErgo. Please add why3 to PATH and retry.")
      else {
        val proc = Command("why3 --list-provers | grep Alt-Ergo").exec()
        proc.waitFor()
        if (proc.exitValue != 0) {
          val proc = Command(s"why3 config --add-prover alt-ergo $convert").exec()
          proc.waitFor()
          if (proc.exitValue != 0) throw new NoSuchMethodException("Registration of AltErgo in why3 not successful. Check debug logs.")
        }
        leo.Out.debug(s"Create AltErgo prover with path '$convert'")
        AltErgo(convert)
      }
    } else {
      throw new NoSuchMethodException(s"There is no prover '$path'. It is not exectuable or it does not exist.")
    }
  }

  final def limitedRun(timeout: Int, args: Seq[String]): Seq[String] = {
    Seq(LIMITEDRUN.toString, String.valueOf(timeout), String.valueOf(timeout)) ++ args
  }
}


class CVC4(execScript: String, val path: String) extends TptpProver[AnnotatedClause] {
  final val name: String = "cvc4"
  final val capabilities: Capabilities.Info = Capabilities(Capabilities.TFF -> Seq())

  protected[external] def constructCall(args: Seq[String], timeout: Int,
                                        problemFileName: String): Seq[String] = {
    ExternalProver.limitedRun(timeout, Seq(execScript, path, problemFileName))
  }
}
object CVC4 {
  @inline final def apply(execScript: String, path: String): CVC4 = new CVC4(execScript, path)
  final val executeScriptName: String = "run-script-cascj8-tfa"
  final def executeScript: BufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/scripts/" + executeScriptName))
}

class AltErgo(val path: String) extends TptpProver[AnnotatedClause] {
  final val name: String = "AltErgo"
  final val capabilities: Capabilities.Info = Capabilities(Capabilities.TFF -> Seq(Capabilities.Polymorphism))

  protected[external] def constructCall(args: Seq[String], timeout: Int,
                                        problemFileName: String): Seq[String] = {
    ExternalProver.limitedRun(timeout, Seq("why3", "prove", "-F", "tptp", "-t", String.valueOf(timeout), "-P", "Alt-Ergo", problemFileName))
  }
}
object AltErgo {
  @inline final def apply(path: String): AltErgo = new AltErgo(path)
}



class Leo2Prover(val path : String) extends TptpProver[AnnotatedClause] {
  override val name: String = "leo2"

  final val capabilities: Capabilities.Info = Capabilities(Capabilities.THF -> Seq())

  override protected[external] def constructCall(args: Seq[String], timeout: Int, problemFileName: String): Seq[String] = {
    ExternalProver.limitedRun(timeout, Seq(path, "-t", timeout.toString) ++ args ++ Seq(problemFileName))
  }

  /*/**
    * Performs a translation of the result of the external process.
    *
    * Reads the exitValue of Leo2 and translates it to a result.
    *
    * @param originalProblem the original set of formulas, passed to the process
    * @param process         the process itself
    * @return the result of the external prover, run on the originalProblem
    */
  override protected def translateResult(originalProblem: Set[AnnotatedClause], process: KillableProcess): TptpResult[AnnotatedClause] = {
    try{
      val exitValue = process.exitValue
      val output = scala.io.Source.fromInputStream(process.output).getLines().toSeq
      val error = scala.io.Source.fromInputStream(process.error).getLines().toSeq

      // Tests the possible exitValues of leo2
      val szsStatus = exitValue match {
        case 0 => SZS_Theorem
        case 2 => SZS_Timeout
        case 5 => SZS_CounterSatisfiable
        case 1 => SZS_Unsatisfiable
        case 3 => SZS_GaveUp // Should be Resource Out
        case 4 => SZS_GaveUp
        case 6 => SZS_Satisfiable
        case 7 => SZS_Theorem // Should be Tautology
        case 50 => SZS_User
        case 51 => SZS_Forced
        case 126 => SZS_Unknown
        case 127 => SZS_Error
        case _ => SZS_Unknown
      }
      new TptpResultImpl(originalProblem, szsStatus, exitValue, output, error)
    } catch {
      case e: Exception =>
        new TptpResultImpl(originalProblem, SZS_Error, 127, Seq(), Seq(e.getMessage))
    }
  }*/
}


class NitpickProver(val path : String) extends TptpProver[AnnotatedClause] {
  override def name: String = "nitpick"

  final val capabilities: Capabilities.Info = Capabilities(Capabilities.THF -> Seq())

  override protected def constructCall(args: Seq[String], timeout: Int, problemFileName: String): Seq[String] = {
    ExternalProver.limitedRun(timeout, Seq(path, "tptp_nitpick", timeout.toString) ++ Seq(problemFileName))
  }
}
