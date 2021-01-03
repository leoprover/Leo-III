package leo.modules.input

import java.nio.file.Paths

import leo.datastructures.TPTPAST.AnnotatedFormula

object DDLPreprocessor {

  final def apply(fileName: String): Seq[AnnotatedFormula] = {
    val input = Input.read0(Paths.get(fileName).toAbsolutePath.normalize()).mkString("\n") // a bit hacky; update DDL embedding
    val output: String = leo.modules.embedding.DDLEmbedding(input)
    Input.parseProblem(output)
  }
}
