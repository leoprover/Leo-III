package leo.modules

/**
  * Created by lex on 28.02.16.
  */
package object indexing {
  import leo.datastructures.Clause

  type FeatureVector = Seq[Int]
  type ClauseFeature = Int
  type CFF = Clause => Int
}
