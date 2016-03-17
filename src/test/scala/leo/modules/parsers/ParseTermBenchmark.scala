package leo.modules.parsers

import leo.modules.parsers.utils.GenerateTerm
import leo.modules.parsers.syntactical_new.termParser.TermParser._
import leo.{Checked, LeoTestSuite}


/**
  * Created by samuel on 17.03.16.
  */
class ParseTermBenchmark
  extends LeoTestSuite
{
  test("benchmark", Checked) {
    val numRuns = 1000
    for( length <- 10 to 1000 by 10 )
    {
      var t: Long = 0
      for(_ <- 0 to numRuns)
      {
        val term = GenerateTerm(length)
        //println(s"testing term: ${term}")

        val t0 = System.nanoTime
        val parseRet = parse(term)
        t += (System.nanoTime - t0)
      }
      println(s"length: ${length}, time: ${t/numRuns}")
    }
  }
}
