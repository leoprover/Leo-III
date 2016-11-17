package leo.modules.calculus

import leo.{Checked, LeoTestSuite}
import leo.datastructures._
import Term._
import leo.modules.HOLSignature._

/**
  * Created by mwisnie on 11/15/16.
  */
class MiniscopingTest extends LeoTestSuite{

  //----------------------------------------
  //  One Step Rule Cases (Forall) - positive
  //----------------------------------------

  test("![X]: ~(p(X))", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))

    val origin = Forall(\(i)(Not(px)))
    val should = Not(Exists(\(i)(px)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("![X]: (p(X) & a)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Forall(\(i)(&(px,a)))
    val should = &(Forall(\(i)(px)), a)
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }


  test("![X]: (a & p(X))", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Forall(\(i)(&(a, px)))
    val should = &(a, Forall(\(i)(px)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("![X]: (p(X) & q(X)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val qx = mkTermApp(mkAtom(s.addUninterpreted("q", i ->: o)), mkBound(i, 1))

    val origin = Forall(\(i)(&(px, qx)))
    val should = &(Forall(\(i)(px)), Forall(\(i)(qx)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("![X]: (a | p(X))", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Forall(\(i)(|||(a, px)))
    val should = |||(a, Forall(\(i)(px)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }


  test("![X]: (p(X) | a)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Forall(\(i)(|||(px,a)))
    val should = |||(Forall(\(i)(px)), a)
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("![X]: (p(X) => a)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Forall(\(i)(Impl(px, a)))
    val should = Impl(Exists(\(i)(px)), a)
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("![X]: (a => p(X))", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Forall(\(i)(Impl(a, px)))
    val should = Impl(a, Forall(\(i)(px)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  //----------------------------------------
  //  One Step Rule Cases (Forall) - positive
  //----------------------------------------

  test("?[X]: ~(p(X))", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))

    val origin = Exists(\(i)(Not(px)))
    val should = Not(Forall(\(i)(px)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("?[X]: (p(X) | a)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Exists(\(i)(|||(px,a)))
    val should = |||(Exists(\(i)(px)), a)
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("?[X]: (a | p(X))", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Exists(\(i)(|||(a, px)))
    val should = |||(a, Exists(\(i)(px)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("?[X]: (p(X) | q(X)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val qx = mkTermApp(mkAtom(s.addUninterpreted("q", i ->: o)), mkBound(i, 1))

    val origin = Exists(\(i)(|||(px, qx)))
    val should = |||(Exists(\(i)(px)), Exists(\(i)(qx)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("?[X]: (p(X) & a)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Exists(\(i)(&(px,a)))
    val should = &(Exists(\(i)(px)), a)
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("?[X]: (a & p(X))", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Exists(\(i)(&(a, px)))
    val should = &(a, Exists(\(i)(px)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("?[X]: (p(X) => a)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Exists(\(i)(Impl(px,a)))
    val should = Impl(Forall(\(i)(px)), a)
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("?[X]: (a => p(X))", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val a = mkAtom(s.addUninterpreted("a", o))

    val origin = Exists(\(i)(Impl(a, px)))
    val should = Impl(a, Exists(\(i)(px)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("?[X]: (p(X) => q(X)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val qx = mkTermApp(mkAtom(s.addUninterpreted("q", i ->: o)), mkBound(i, 1))

    val origin = Exists(\(i)(Impl(px, qx)))
    val should = Impl(Forall(\(i)(px)), Exists(\(i)(qx)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  // -----------------------------
  //  Test Forall - negative
  // -----------------------------

  test("![X]: (p(X) | q(X)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val qx = mkTermApp(mkAtom(s.addUninterpreted("q", i ->: o)), mkBound(i, 1))

    val origin = Forall(\(i)(|||(px, qx)))
    val should = Forall(\(i)(|||(px, qx)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("![X]: (p(X) => q(X)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val qx = mkTermApp(mkAtom(s.addUninterpreted("q", i ->: o)), mkBound(i, 1))

    val origin = Forall(\(i)(Impl(px, qx)))
    val should = Forall(\(i)(Impl(px, qx)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  // --------------------------
  //  Test Exists - negative
  // ---------------------------

  test("?[X]: (p(X) & q(X)", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: o)), mkBound(i, 1))
    val qx = mkTermApp(mkAtom(s.addUninterpreted("q", i ->: o)), mkBound(i, 1))

    val origin = Exists(\(i)(&(px, qx)))
    val should = Exists(\(i)(&(px, qx)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  // -----------------------------------
  //  Complex Tests
  // -----------------------------------

  test("![X]: ?[Y]: ~(p(X,Y))", Checked) {
    implicit val s = getFreshSignature

    val px = mkTermApp(mkAtom(s.addUninterpreted("p", i ->: i ->: o)), Seq(mkBound(i, 2), mkBound(i, 1)))
    val origin = Forall(\(i)(Exists(\(i)(Not(px)))))
    val should = Not(Exists(\(i)(Forall(\(i)(px)))))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("![X]: ?[Y]: p(X) => p(Y))", Checked) {
    implicit val s = getFreshSignature

    val p = s.addUninterpreted("p", i ->: o)
    val px = mkTermApp(mkAtom(p), mkBound(i, 2))
    val py = mkTermApp(mkAtom(p), mkBound(i, 1))
    val origin = Forall(\(i)(Exists(\(i)(Impl(px, py)))))
    val should = Impl(Exists(\(i)(py)), Exists(\(i)(py)))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }

  test("![P]: ![X]: ?[Y]: p(X) => p(Y))", Checked) {
    implicit val s = getFreshSignature

    val pty = i ->: o
    val px = mkTermApp(mkBound(pty, 3), mkBound(i, 2))
    val py = mkTermApp(mkBound(pty, 3), mkBound(i, 1))
    val origin = Forall(\(pty)(Forall(\(i)(Exists(\(i)(Impl(px, py)))))))
    val should = Forall(\(pty)(Impl(Exists(\(i)(mkTermApp(mkBound(pty, 2), mkBound(i, 1)))), Exists(\(i)(mkTermApp(mkBound(pty, 2), mkBound(i, 1)))))))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }


  test("![C]: ?[P]: ![X]: ~(P(X) => P(C(P)))", Checked) {
    implicit val s = getFreshSignature

    //X o -> o
    //P (o-> o) -> o
    //C ((o -> o) -> o) -> (o -> o)
    val xty = (o ->: o)
    val x = mkBound(xty, 1)
    val pty = (o ->: o) ->: o
    val p = mkBound(pty, 2)
    val cty = ((o ->: o) ->: o) ->: (o ->: o)
    val c = mkBound(cty, 3)

    val origin = Forall(\(cty)(Exists(\(pty)(Forall(\(xty)(
      Not(
        Impl(
          mkTermApp(p, x),
          mkTermApp(p, mkTermApp(c, p))
        )
      )
    ))))))

    assert(Term.wellTyped(origin))

    val should = Not(Exists(\(cty)(Forall(\(pty)(
        Impl(
          Forall(\(xty)(mkTermApp(p, x))),
          mkTermApp(mkBound(pty, 1), mkTermApp(mkBound(cty, 2), mkBound(pty,1)))
        )
      )
    ))))
    val res = Miniscope(origin, true)

    assert(res == should, s"\n${origin.pretty(s)} should be miniscoped to\n  ${should.pretty(s)}\n but was\n  ${res.pretty(s)}")
  }
}
