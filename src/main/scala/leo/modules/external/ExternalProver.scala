package leo.modules.external

import leo.datastructures._
import leo.modules.output._
import java.nio.file.{Files, Paths}

import leo.modules.HOLSignature.LitTrue


/**
  * Object to construct provers from their paths.
  * Checks for the commands, for their executability.
  */
object ExternalProver {

  /**
    * Additional time added to the timeout to wait for termination
    */
  final val WAITFORTERMINATION = 1

  /**
    * Creates a prover `name` with an executable at the path `path`.
    * Throws an [[NoSuchMethodException]] if this prover cannot be executed.
    *
    * @param name name of the prover
    * @param path path of the prover
    * @return an abstracted prover `name`
    */
  @throws[NoSuchMethodException]
  def createProver(name : String, path : String) : TptpProver[AnnotatedClause] = name match {
    case "leo2" => createLeo2(path)
    case "nitpick" => createNitpickProver(path)
    case _ => throw new NoSuchMethodException(s"There is no prover ${name} registered in the system.")
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
      throw new NoSuchMethodException(s"There is no prover '${path}' is not exectuable or does not exist.")
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
      throw new NoSuchMethodException(s"There is no prover '${path}' is not exectuable or does not exist.\nTip: Reference only to isablle and not nitpick directly.")
    }
  }

}




abstract class THFProver extends TptpProver[AnnotatedClause]{
  val falseClause = AnnotatedClause(Clause(Seq(Literal.mkLit(LitTrue(), false))), Role_Conjecture, ClauseAnnotation.NoAnnotation, ClauseAnnotation.PropNoProp)

  override protected[external] def translateProblem(problem: Set[AnnotatedClause])(implicit sig : Signature): Seq[String] = {
    val toAxiom = problem map (c => AnnotatedClause(c.cl, Role_Axiom, c.annotation, c.properties))
    val outTPTP : Seq[Output] = ToTPTP(toAxiom + falseClause)
    val res = outTPTP.map{x => x()}
    res
  }
}


class Leo2Prover(val path : String) extends THFProver{
  override val name: String = "leo2"


  override protected[external] def constructCall(args: Seq[String], timeout: Int, problemFileName: String): Seq[String] = {
    Seq(path, "-t", (timeout).toString) ++ args ++ Seq(problemFileName)
  }

  /**
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
  }
}


class NitpickProver(val path : String) extends THFProver {
  override def name: String = "nitpick"

  override protected def constructCall(args: Seq[String], timeout: Int, problemFileName: String): Seq[String] = {
    Seq(path, "tptp_nitpick", (timeout).toString) ++ Seq(problemFileName)
  }
}
