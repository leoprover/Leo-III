//package leo
//package modules.countersat
//
//import leo.Configuration
//import leo.datastructures._
//import leo.datastructures.impl.Signature
//import leo.modules.CLParameterParser
//import leo.modules.output.ToTPTP
//import org.scalatest.FunSuite
//import org.scalatest.junit.JUnitRunner
//
//import leo.datastructures.term.Term._
//
///**
// * Created by ryu on 2/26/15.
// */
//class HerbrandEnumerationTest extends FunSuite {
//  Configuration.init(new CLParameterParser(Array("arg0", "-v", "4")))
//
//  val s = Signature.get
//
//  val a = mkAtom(s.addUninterpreted("a",s.i))
//  val b = mkAtom(s.addUninterpreted("b",s.i))
//
//  val p = mkAtom(s.addUninterpreted("p", s.i ->: s.i ->: s.o))
//
//  val t1 = Forall(\(s.i)(
//      Exists(\(s.i)(mkTermApp(p, List(mkBound(s.i, 1), mkBound(s.i, 2)))))
//    ))
//
//  val c1 = Clause.mkClause(List(Literal(t1, true)), Derived)
//  test("Test t1"){
//    Out.output("Before: "+ToTPTP("test", c1, Role_Plain).output)
//    val domain = Map((s.i, List(a,b)))
//    Out.output("After: "+ToTPTP("test", FiniteHerbrandEnumeration.replaceQuant(c1, domain), Role_Plain).output)
//  }
//}
