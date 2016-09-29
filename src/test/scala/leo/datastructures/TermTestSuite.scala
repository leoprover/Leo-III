package leo.datastructures

import scala.language.implicitConversions

import leo.LeoTestSuite

/**
 * Created by lex on 23.04.15.
 */
class TermTestSuite extends LeoTestSuite {


  // Meta variable instantiation test
  import Term.{λ, intToBoundVar, mkMetaVar, mkAtom}
  import leo.Checked
  import impl.SignatureImpl

  val sig = SignatureImpl.get
  val o = sig.o
  val i = sig.i

  test("etaExpand - all binders of type i", Checked) {
    val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", (s.i->:s.i)->:s.i))
    val t = Term.λ(s.i)(Term.mkTermApp(a,Term.mkTermApp(Term.mkMetaVar(Type.mkFunType(s.i,s.i->:s.i), 1), Term.mkBound(s.i,1))))
    val m = Term.λ(s.i)(Term.mkTermApp(a,Term.λ(s.i)(Term.mkTermApp(
      Term.mkMetaVar(Type.mkFunType(s.i,s.i->:s.i), 1), List(Term.mkBound(s.i,2), Term.mkBound(s.i,1))))))

    println(s"t: ${t.pretty}")
    println(s"t eta expand: ${t.etaExpand.pretty}")

    println(s"m: ${m.pretty}")
    assert(m.equals(t.etaExpand))
  }

  test("etaExpand - the two binders of different type", Checked) {
    val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", ((s.i->:s.i)->:s.i)->:s.i))
    println("type of a: " + a.ty.pretty)
    val y = Term.mkMetaVar(Type.mkFunType(s.i,(s.i->:s.i)->:s.i), 1)
    println("type of sV1: " + y.ty.pretty)
    val t = Term.λ(s.i)(Term.mkTermApp(a,Term.mkTermApp(y,
      Term.mkBound(s.i,1))))
    println("type of t: " + t.ty.pretty)
    println("t: " + t.pretty)
    println("is t typed properly? " + Term.wellTyped(t))
    println("t.etaExpand: " + t.etaExpand.pretty)
    println("type of t.etaExpand: " + t.etaExpand.ty.pretty)
    println("is t.etaExpand typed properly? " + Term.wellTyped(t.etaExpand) + " - really?")

    val m = Term.λ(s.i)(Term.mkTermApp(a,Term.λ(s.i->:s.i)(Term.mkTermApp(
      y, List(Term.mkBound(s.i,2), Term.mkBound(s.i,1))))))
    /* m2 as explained in the email */
    val m2 = Term.λ(s.i)(Term.mkTermApp(a,Term.λ(s.i->:s.i)(Term.mkTermApp(
      y, List(Term.mkBound(s.i,2), Term.mkBound(s.i ->: s.i,1))))))
    println("the expected t.etaExpand: " + m.pretty)
    println("the expected t.etaExpand (what alex would expect): " + m2.pretty)
//    println("weird: when applying etaExpand to the above: " + m.etaExpand.pretty)
//    assert(m.equals(t.etaExpand))
    assert(m2.equals(t.etaExpand))
  }
}