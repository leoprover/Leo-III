package leo.modules.external

import leo.datastructures._
import leo.modules.output._
import java.nio.file.{Files, Paths}

import leo.datastructures.ClauseAnnotation.NoAnnotation
import leo.modules.HOLSignature.LitTrue


/**
  * Object to construct provers from their paths.
  * Checks for the commands, for their executability.
  */
object ExternalProver {
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
}







class Leo2Prover(val path : String) extends TptpProver[AnnotatedClause]{
  /*
    TODO Build own Future object, that parses the szsStatus from the exitValue
    Support mkFuture or a Function (Process => Result) with a standard implementation
    to read the output stream



    */
  override val name: String = "leo2"
  val falseClause = AnnotatedClause(Clause(Seq(Literal.mkLit(LitTrue(), false))), Role_Conjecture, ClauseAnnotation.NoAnnotation, ClauseAnnotation.PropNoProp)

  override protected[external] def translateProblem(problem: Set[AnnotatedClause])(implicit sig : Signature): Seq[String] = {
    val toAxiom = problem map (c => AnnotatedClause(c.cl, Role_Axiom, c.annotation, c.properties))
    val outTPTP : Seq[Output] = ToTPTP(toAxiom + falseClause)
    val res = outTPTP.map{x => x()}
    println(s"\n\nTranslation : ${res.mkString("\n")}")
    res
  }

  override protected[external] def constructCall(args: Seq[String], timeout: Int, problemFileName: String): Seq[String] = {
    Seq(path, "-t", (timeout / 1000).toString) ++ args ++ Seq(problemFileName)
  }

  /**
    * Performs a translation of the result of the external process.
    *
    * Reads the exitValue of Leo2 and translates it to a result:
    * Leo2's exit values are defined by
    * Theorem -> 0
    * | Unsatisfiable -> 1
    * | Timeout -> 2
    * | ResourceOut -> 3
    * | GaveUp -> 4
    * | CounterSatisfiable -> 5
    * | Satisfiable -> 6
    * | Tautology -> 7
    * (*externally-forced status*)
    * | User -> 50
    * | Force -> 51
    * (*"strange" status*)
    * | Unknown -> 126
    * | Error -> 127
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

      // Binäre suche 12 Vergleiche vs 4
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
      new TptpResult[AnnotatedClause](originalProblem, szsStatus, exitValue, output, error)
    } catch {
      case e: Exception =>
        new TptpResult[AnnotatedClause](originalProblem, SZS_Error, 127, Seq(), Seq(e.getMessage))
    }
  }
}