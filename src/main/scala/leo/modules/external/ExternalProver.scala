package leo.modules.external

import leo.datastructures._
import leo.modules.output.{Output, ToTPTP}
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
  override val name: String = "leo2"
  val falseClause = AnnotatedClause(Clause(Seq(Literal.mkLit(LitTrue(), false))), Role_Conjecture, ClauseAnnotation.NoAnnotation, ClauseAnnotation.PropNoProp)

  override protected[external] def translateProblem(problem: Set[AnnotatedClause])(implicit sig : Signature): Seq[String] = {
    val toAxiom = problem map (c => AnnotatedClause(c.cl, Role_Axiom, c.annotation, c.properties))
    val outTPTP : Seq[Output] = ToTPTP(toAxiom + falseClause)
    outTPTP.map{x => x()}
  }

  override protected[external] def constructCall(args: Seq[String], timeout: Int, problemFileName: String): Seq[String] = {
    Seq(path, "-t", timeout.toString) ++ args ++ Seq(problemFileName)
  }
}