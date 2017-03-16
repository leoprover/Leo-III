package leo.datastructures.context

import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.{Checked, Configuration, LeoTestSuite}
import leo.datastructures.AnnotatedClause
import leo.datastructures.context.impl._
import leo.modules.parsers.CLParameterParser

/**
 * Created by ryu on 11/25/14.
 */
class TreeContextSetTestSuite extends LeoTestSuite {

//  Configuration.init(new CLParameterParser(Array("arg0", "-v", "4")))
  // b is root,

  test("One context",Checked){
    Context.clear()
    val b : Context = Context()
    b.split(BetaSplit, 2)
    val list = b.childContext.toList
    val l = list(0)
    val r = list(1)

    // l is the first and r is the second child

    val s : ContextSet[Int] = new TreeContextSet[Int]()
    assert(s.getAll(b).isEmpty, "The root context should contain no elements")

    s.add(4, l)
    assert(s.getAll(b).isEmpty, "The root context should contain no elements")
    assert(s.getAll(r).isEmpty, "The second context should contain no elements")
    assert(s.getAll(l).contains(4), "The first context should contain the 4")

    s.add(3,b)
    assert(s.getAll(b).contains(3), "The root context should contain the 3")
    assert(s.getAll(r).contains(3), "The second context should contain the 3")
    assert(s.getAll(l).contains(3), "The first context should contain the 3")
  }

  test("Insertion Before Split",Checked) {
    Context.clear()
    val b : Context = Context()

    val s : ContextSet[Int] = new TreeContextSet[Int]()
    s.add(3,b)

    b.split(BetaSplit, 2)
    val list = b.childContext.toList
    val l = list(0)
    val r = list(1)

    assert(s.getAll(b).contains(3), "The root context should contain the 3")
    assert(s.getAll(r).contains(3), "The second context should contain the 3")
    assert(s.getAll(l).contains(3), "The first context should contain the 3")
  }

  test("Empty get all",Checked) {
    val s : ContextSet[AnnotatedClause] = new TreeContextSet[AnnotatedClause]()

    assert(s.getAll.isEmpty, "Initial the context set is empty.")
  }

  test("Get All",Checked) {
    Context.clear()
    val b : Context = Context()

    val s : ContextSet[Int] = new TreeContextSet[Int]()
    s.add(3,b)

    b.split(BetaSplit, 2)
    val list = b.childContext.toList
    val l = list(0)
    val r = list(1)
    s.add(4, l)
    s.add(5,r)

    val a = s.getAll

    assert(a.contains(3), "All should contain 3.")
    assert(a.contains(4), "All should contain 4.")
    assert(a.contains(5), "All should contain 5.")
  }

  test("Empty Blackboard Get All",Checked) {
    val a = FormulaDataStore.getFormulas

    assert(a.isEmpty, "All should be empty initial.")
  }

  test("Removal in subcontext"){
    Context.clear()
    val b : Context = Context()

    val s : ContextSet[Int] = new TreeContextSet[Int]()

    b.split(BetaSplit, 3)
    val list = b.childContext.toList
    val l = list(0)
    val m = list(1)
    val r = list(2)
    s.add(5, b)
    assert(s.contains(5, l), "Initially all contain the element")
    assert(s.contains(5, m), "Initially all contain the element")
    assert(s.contains(5, r), "Initially all contain the element")

    s.remove(5, m)
    assert(s.contains(5, l), "Left remains untouched")
    assert(!s.contains(5, m), "The middel should no longer contain the element")
    assert(s.contains(5, r), "Right remains untouched")
  }
}
