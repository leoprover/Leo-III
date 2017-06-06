package leo.modules.agent.rules

import leo.{Checked, LeoTestSuite}
import leo.datastructures.blackboard.ImmutableDelta

/**
  * Created by mwisnie on 5/16/17.
  */
class AgentBarrierTest extends LeoTestSuite {
  test("Creation Test", Checked){
    val barrier = new AgentBarrier(StringType, 5)
    assert(barrier.get(LockType(StringType)).size == 0)
  }

  test("Init Barrier", Checked){
    val barrier = new AgentBarrier(StringType, 5)
    val delta = new ImmutableDelta(Map(StringType -> Seq("a")))
    barrier.updateResult(delta)
    assert(barrier.isLocked("a"))
  }

  test("Free Barrier", Checked){
    val barrier = new AgentBarrier(StringType, 3)
    val delta = new ImmutableDelta(Map(StringType -> Seq("a")))
    val lockDelta = new ImmutableDelta(Map(barrier.lockType -> Seq("a")))
    barrier.updateResult(delta)
    barrier.updateResult(lockDelta)
    assert(barrier.isLocked("a"))
    barrier.updateResult(lockDelta)
    assert(barrier.isLocked("a"))
    barrier.updateResult(lockDelta)
    assert(!barrier.isLocked("a"))
  }

  test("Multi Lock interleave", Checked) {
    val barrier = new AgentBarrier(StringType, 3)
    val delta1 = new ImmutableDelta(Map(StringType -> Seq("a")))
    val delta2 = new ImmutableDelta(Map(StringType -> Seq("b")))
    val lockDelta1 = new ImmutableDelta(Map(barrier.lockType -> Seq("a")))
    val lockDelta2 = new ImmutableDelta(Map(barrier.lockType -> Seq("b")))

    barrier.updateResult(delta1)
    barrier.updateResult(lockDelta1)

    assert(barrier.isLocked("a"))
    barrier.updateResult(delta2)
    barrier.updateResult(lockDelta2)
    barrier.updateResult(lockDelta1)

    assert(barrier.isLocked("a"))
    assert(barrier.isLocked("b"))

    barrier.updateResult(lockDelta2)

    assert(barrier.isLocked("a"))
    assert(barrier.isLocked("b"))

    barrier.updateResult(lockDelta1)

    assert(!barrier.isLocked("a"))
    assert(barrier.isLocked("b"))

    barrier.updateResult(lockDelta2)

    assert(!barrier.isLocked("a"))
    assert(!barrier.isLocked("b"))

  }
}
