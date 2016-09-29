package leo
package modules.calculus.splitting

import leo.Configuration
import leo.datastructures.impl.SignatureImpl
import leo.modules.CLParameterParser
import leo.modules.output.ToTPTP
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * Created by max on 19.02.2015.
 */
class DomainConstrainedSplittingTest extends FunSuite{
//  Configuration.init(new CLParameterParser(Array("arg0", "-v", "4")))

  test("Cardinality 1", Ignored){
    val ax = DomainConstrainedSplitting.cardinalityTerms(1)(SignatureImpl.get.o)
    Out.output("\nCardinality 1 Test:")
    ax foreach {t => Out.output(t.pretty)}
  }

  test("Cardinality 2", Ignored){
    Out.output("\nCardinality 2 Test:")
    val ax = DomainConstrainedSplitting.cardinalityTerms(2)(SignatureImpl.get.o)
    ax foreach {t => Out.output(t.pretty)}
  }

  test("Cardinality 3", Ignored){
    Out.output("\nCardinality 3 Test:")
    val ax = DomainConstrainedSplitting.cardinalityTerms(3)(SignatureImpl.get.o)
    ax foreach {t => Out.output(t.pretty)}
  }

  test("Cardinality 4", Ignored){
    Out.output("\nCardinality 4 Test:")
    val ax = DomainConstrainedSplitting.cardinalityTerms(4)(SignatureImpl.get.o)
    ax foreach {t => Out.output(t.pretty)}
  }

  test("Cardinality 5", Ignored){
    Out.output("\nCardinality 5 Test:")
    val ax = DomainConstrainedSplitting.cardinalityTerms(5)(SignatureImpl.get.o)
    ax foreach {t => Out.output(t.pretty)}
  }

  test("Cardinality FormulaStore 1", Ignored){
    val ax = DomainConstrainedSplitting.cardinalityAxioms(1)(SignatureImpl.get.o)
    Out.output("\nCardinality 1 Test:")
    Out.output((ax map {t => Out.output(ToTPTP.output(t))}).mkString("\n"))
  }

  test("Cardinality FormulaStore 2", Ignored){
    Out.output("\nCardinality 2 Test:")
    val ax = DomainConstrainedSplitting.cardinalityAxioms(2)(SignatureImpl.get.o)
    Out.output((ax map {t => Out.output(ToTPTP.output(t))}).mkString("\n"))
  }

  test("Cardinality FormulaStore 3", Ignored){
    Out.output("\nCardinality 3 Test:")
    val ax = DomainConstrainedSplitting.cardinalityAxioms(3)(SignatureImpl.get.o)
    Out.output((ax map {t => Out.output(ToTPTP.output(t))}).mkString("\n"))
  }

  test("Cardinality FormulaStore 4", Ignored){
    Out.output("\nCardinality 4 Test:")
    val ax = DomainConstrainedSplitting.cardinalityAxioms(4)(SignatureImpl.get.o)
    Out.output((ax map {t => Out.output(ToTPTP.output(t))}).mkString("\n"))
  }

  test("Cardinality FormulaStore 5", Ignored){
    Out.output("\nCardinality 5 Test:")
    val ax = DomainConstrainedSplitting.cardinalityAxioms(5)(SignatureImpl.get.o)
    Out.output((ax map {t => Out.output(ToTPTP.output(t))}).mkString("\n"))
  }
}
