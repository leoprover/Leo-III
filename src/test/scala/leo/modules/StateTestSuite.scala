package leo.modules

import leo.LeoTestSuite
import leo.datastructures.{AnnotatedClause, Signature}
import leo.modules.prover.State

/**
  * Created by mwisnie on 6/7/17.
  */
class StateTestSuite extends LeoTestSuite{
  test("Copy state as general state"){
    implicit val sig = Signature.freshWithHOL()
    val s : GeneralState[AnnotatedClause] = State.fresh(sig)
    val s1 = s.copyGeneral
    assert(s1.isInstanceOf[GeneralState[AnnotatedClause]])
  }
}
