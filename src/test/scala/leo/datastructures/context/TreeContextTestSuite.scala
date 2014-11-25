package leo.datastructures.context

import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import leo.datastructures.context.Context
import leo.datastructures.context.impl.TreeContext

/**
 *
 * Tests the {@see TreeContext} Implementation of {@see Context}.
 *
 * @author Max Wisniewski
 * @since 11/25/14
 */
@RunWith(classOf[JUnitRunner])
class TreeContextTestSuite extends FunSuite{
  test("BaseContext empty"){
    val b : Context = new TreeContext // Creates an empty BaseContext
    assert(b.parentContext == null, "Parent Context is not empty")
    assert(b.childContext.isEmpty, "There are children in initial state.")
    assert(b.splitKind == NoSplit, "The initial context is already splitted.")
  }
}
