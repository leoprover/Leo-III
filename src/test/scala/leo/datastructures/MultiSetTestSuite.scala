package leo.datastructures

import leo.LeoTestSuite

/**
  * Created by lex on 11/9/16.
  */
class MultiSetTestSuite extends LeoTestSuite {
  test("Multiset Test 1") {
    var ms: Multiset[Int] = Multiset.empty
    println(ms.toString)
    assert(ms.isEmpty)
    ms = ms + 2
    println(ms.toString)
    assert(ms.multiplicity(2) == 1)
    assert(ms.nonEmpty)
    ms = ms + 3
    println(ms.toString)
    assert(ms.multiplicity(3) == 1)
    assert(ms.multiplicity(2) == 1)
    ms = ms + 2
    println(ms.toString)
    assert(ms.multiplicity(2) == 2)
  }

  test("Multiset Test 2") {
    val ms1 = Multiset[Int](1,2,3,4,5,1,2,3,4,5)
    println(ms1.toString)
    println(ms1.intersect(ms1).toString)
    assert(ms1.intersect(ms1) == ms1)
    val ms2 = ms1 sum ms1
    println(ms2.toString)
    assert(ms1 subset ms2)
    assert(ms2.forall(elem => ms2.multiplicity(elem) == 2*ms1.multiplicity(elem)))
  }

  test("Multiset Test 3") {
    val ms1 = Multiset[Int](Set(1,2,3))
    println(ms1.toString)
    val ms2 = Multiset[Int](Seq(3,2,1))
    println(ms2.toString)
    assert(ms1 == ms2)
  }
}
