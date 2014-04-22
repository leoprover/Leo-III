package testtest

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

/**
 *
 * Template to use JUNIT for Scala with ScalaTest FrameWork.
 *
 * Created by Max Wisniewski on 4/22/14.
 */
class TestTemplate extends AssertionsForJUnit {

  // Empty initialization
  var testVar1 : Int = _
  var testVar2 : Boolean = _
  var testArray : List[Int] = _

  /**
   * Defining the initializer with Before.
   *
   * Try to use only ONE initializer.
   */
  @Before def initialize() {
    testVar1 = 5
    testVar2 = true
    testArray = List(1,2,3)
  }

  /**
   * All JUnit Assertions can be used. As assertEquals
   */
  @Test def verifyOne() {
    assertEquals(testVar1, 5);
  }

  /**
   * As assertTrue
   */
  @Test def verifyTwo() {
    assertTrue(testVar2)
  }

  /**
   * Use fail explizitly, when you want to check for an exception, that should fly
   */
  @Test def outOfBounds() {
    try {
      val b = testArray(3)
      fail()
    } catch {
      case e : Exception => // Was expected, so we do nothing
    }
  }

}
