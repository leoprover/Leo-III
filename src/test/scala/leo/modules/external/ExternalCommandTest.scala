package leo.modules.external

import leo.{Ignored, LeoTestSuite}

/**
  * Created by mwisnie on 2/29/16.
  */
class ExternalCommandTest extends LeoTestSuite {

  test("Command Test Echo", Ignored) {

    val result = ExternalCall.exec("echo hallo a");

    val out = result.out.toSeq

    assert(out.size == 1, "'echo hallo a' should deliver only one line.")
    assert(out.head == "hallo a", "'echo hallo a' should return 'hallo a'")
  }

  test("Command Test Cat", Ignored) {
    val inputLines = Seq("eins","zwei","drei")
    val result = ExternalCall.exec("cat",inputLines)

    val out = result.out.toSeq
    assert(out.size == 3, "'cat file' with 'eins\\nzwei\\ndrei' should return 3 Lines.")
    assert(out.head == "eins", "First element should be 'eins'")
    assert(out.tail.head == "zwei", "Second element should be 'zwei'")
    assert(out.tail.tail.head == "drei", "Third element should be 'drei'")
  }

  test("Command Chain Test", Ignored){
    val result = ExternalCall.exec("echo hallo & echo main")

    result.exitValue
    val out : Iterator[String] = result.out

    // TODO Why is it choking
//    assert(out.size == 2)

    while(out.hasNext){
      val next = out.next
      assert(next == "main" || next == "hallo")
    }
  }

  test("Termination Test", Ignored) {
    val t = System.currentTimeMillis()
    val result = ExternalCall.exec("sleep 100 & sleep 1000")

    // Killing in between
    result.kill()

    val ret = result.exitValue
    val t1 = System.currentTimeMillis()
    assert(t1-t < 10000, "Termination not successfull")
  }
}
