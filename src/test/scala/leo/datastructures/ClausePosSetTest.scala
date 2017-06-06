package leo.datastructures

import leo.LeoTestSuite

/**
  * Created by lex on 25.05.17.
  */
class ClausePosSetTest extends LeoTestSuite {
  test("Test 1") {
    val set: ClausePositionSet = ClausePositionSet.empty
    set.insert(ClausePosition(null, 0, false, Position(Vector())))
    set.insert(ClausePosition(null, 0, false, Position(Vector(1,2))))
    set.insert(ClausePosition(null, 0, false, Position(Vector(1))))
    set.insert(ClausePosition(null, 0, true, Position(Vector(2))))
    set.insert(ClausePosition(null, 0, false, Position(Vector(2))))
    set.insert(ClausePosition(null, 0, false, Position(Vector(2,1))))
    set.insert(ClausePosition(null, 1, false, Position(Vector(2,1))))

    import scala.collection.mutable
    var seen:mutable.Buffer[ClausePosition] = mutable.Buffer.empty
    var curIdx = -1
    val it = set.bfsIterator
    while(it.hasNext) {
      val elem = it.next()
      if (curIdx != elem.litIdx) {
        seen.clear()
        curIdx = elem.litIdx
      }
      println(elem.pretty)
      assert(seen.forall(x => !x.pos.seq.startsWith(elem.pos.seq) || x.side != elem.side))
      seen += elem

    }
  }
}
