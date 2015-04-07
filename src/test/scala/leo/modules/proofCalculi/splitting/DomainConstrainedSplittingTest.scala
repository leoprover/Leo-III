package leo
package modules.proofCalculi.splitting

import leo.Configuration
import leo.datastructures.impl.Signature
import leo.modules.CLParameterParser
import leo.modules.output.ToTPTP
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * Created by max on 19.02.2015.
 */
@RunWith(classOf[JUnitRunner])
class DomainConstrainedSplittingTest extends FunSuite{
  Configuration.init(new CLParameterParser(Array("arg0", "-v", "4")))

  test("Cardinality 1"){
    val ax = DomainConstrainedSplitting.cardinalityTerms(1)(Signature.get.o)
    Out.output("\nCardinality 1 Test:")
    ax foreach {t => Out.output(t.pretty)}
  }

  test("Cardinality 2"){
    Out.output("\nCardinality 2 Test:")
    val ax = DomainConstrainedSplitting.cardinalityTerms(2)(Signature.get.o)
    ax foreach {t => Out.output(t.pretty)}
  }

  test("Cardinality 3"){
    Out.output("\nCardinality 3 Test:")
    val ax = DomainConstrainedSplitting.cardinalityTerms(3)(Signature.get.o)
    ax foreach {t => Out.output(t.pretty)}
  }

  test("Cardinality 4"){
    Out.output("\nCardinality 4 Test:")
    val ax = DomainConstrainedSplitting.cardinalityTerms(4)(Signature.get.o)
    ax foreach {t => Out.output(t.pretty)}
  }

  test("Cardinality 5"){
    Out.output("\nCardinality 5 Test:")
    val ax = DomainConstrainedSplitting.cardinalityTerms(5)(Signature.get.o)
    ax foreach {t => Out.output(t.pretty)}
  }

  test("Cardinality FormulaStore 1"){
    val ax = DomainConstrainedSplitting.cardinalityAxioms(1)(Signature.get.o)
    Out.output("\nCardinality 1 Test:")
    Out.output((ax map {t => Out.output(ToTPTP(t).output)}).mkString("\n"))
  }

  test("Cardinality FormulaStore 2"){
    Out.output("\nCardinality 2 Test:")
    val ax = DomainConstrainedSplitting.cardinalityAxioms(2)(Signature.get.o)
    Out.output((ax map {t => Out.output(ToTPTP(t).output)}).mkString("\n"))
  }

  test("Cardinality FormulaStore 3"){
    Out.output("\nCardinality 3 Test:")
    val ax = DomainConstrainedSplitting.cardinalityAxioms(3)(Signature.get.o)
    Out.output((ax map {t => Out.output(ToTPTP(t).output)}).mkString("\n"))
  }

  test("Cardinality FormulaStore 4"){
    Out.output("\nCardinality 4 Test:")
    val ax = DomainConstrainedSplitting.cardinalityAxioms(4)(Signature.get.o)
    Out.output((ax map {t => Out.output(ToTPTP(t).output)}).mkString("\n"))
  }

  test("Cardinality FormulaStore 5"){
    Out.output("\nCardinality 5 Test:")
    val ax = DomainConstrainedSplitting.cardinalityAxioms(5)(Signature.get.o)
    Out.output((ax map {t => Out.output(ToTPTP(t).output)}).mkString("\n"))
  }
}
