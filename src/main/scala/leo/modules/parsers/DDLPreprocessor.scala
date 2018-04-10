package leo.modules.parsers

import java.nio.file.Paths

import leo.datastructures.tptp.Commons.AnnotatedFormula

object DDLPreprocessor {

  final def apply(fileName: String): Seq[AnnotatedFormula] = {
    val input = Input.read0(Paths.get(fileName).toAbsolutePath.normalize())
    val output = leo.modules.embedding.DDLEmbedding(input)
    Input.parseProblem(output)
  }
}
