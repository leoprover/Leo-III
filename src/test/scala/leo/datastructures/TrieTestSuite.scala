package leo.datastructures

import leo.LeoTestSuite

/**
  * Created by lex on 28.02.16.
  */
class TrieTestSuite extends LeoTestSuite {

  test("test1") {
    val t:Trie[Int, String] = Trie()
    t.insert(Seq(1,2,3), "Hallo")
    t.insert(Seq(1,2,4), "Welt")
    t.insert(Seq(1,2,3), "ABC")
    t.insert(Seq(1,3,2), "DEF")

    println(s"alle: ${t.getAll.toString}")
    println(s"mit prefix 1: ${t.getPrefix(Seq(1)).toString}")
    println(s"mit prefix 2: ${t.getPrefix(Seq(2)).toString}")
    println(s"mit prefix 1.2: ${t.getPrefix(Seq(1,2)).toString}")
    println(s"mit prefix 1.1: ${t.getPrefix(Seq(1,1)).toString}")
    println(s"mit prefix 1.2.3: ${t.getPrefix(Seq(1,2,3)).toString}")
    println(s"mit key 1.2.3: ${t.get(Seq(1,2,3)).toString}")
    println(s"mit key 1.2: ${t.get(Seq(1,2)).toString}")
  }

  test("test2") {
    val t:Trie[Int, String] = FixedLengthTrie()
    t.insert(Seq(1,2,3), "Hallo")
    t.insert(Seq(1,2,4), "Welt")
    t.insert(Seq(1,2,3), "ABC")
    t.insert(Seq(1,3,2), "DEF")

    println(s"alle: ${t.getAll.toString}")
    println(s"mit prefix 1: ${t.getPrefix(Seq(1)).toString}")
    println(s"mit prefix 2: ${t.getPrefix(Seq(2)).toString}")
    println(s"mit prefix 1.2: ${t.getPrefix(Seq(1,2)).toString}")
    println(s"mit prefix 1.1: ${t.getPrefix(Seq(1,1)).toString}")
    println(s"mit prefix 1.2.3: ${t.getPrefix(Seq(1,2,3)).toString}")
    println(s"mit key 1.2.3: ${t.get(Seq(1,2,3)).toString}")
    println(s"mit key 1.2: ${t.get(Seq(1,2)).toString}")
  }

  test("test3") {
    try {
      val t: Trie[Int, String] = FixedLengthTrie()
      t.insert(Seq(1, 2, 3), "Hallo")
      t.insert(Seq(1, 2, 3, 4), "Welt")
      t.insert(Seq(1, 2, 3), "ABC")
      t.insert(Seq(1, 3, 2), "DEF")

      println(s"alle: ${t.getAll.toString}")
      println(s"mit prefix 1: ${t.getPrefix(Seq(1)).toString}")
      println(s"mit prefix 2: ${t.getPrefix(Seq(2)).toString}")
      println(s"mit prefix 1.2: ${t.getPrefix(Seq(1, 2)).toString}")
      println(s"mit prefix 1.1: ${t.getPrefix(Seq(1, 1)).toString}")
      println(s"mit prefix 1.2.3: ${t.getPrefix(Seq(1, 2, 3)).toString}")
      println(s"mit key 1.2.3: ${t.get(Seq(1, 2, 3)).toString}")
      println(s"mit key 1.2: ${t.get(Seq(1, 2)).toString}")

      fail("exception should have been thrown")
    } catch {
      case e : IllegalArgumentException => println("exception geschmissen, gut!")
    }
  }

}
