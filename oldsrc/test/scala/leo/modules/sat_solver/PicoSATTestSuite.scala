/*
package leo.modules.sat_solver

import leo.{Checked, LeoTestSuite}

/**
  * Created by Hans-Jörg Schurr on 8/11/16.
  */
class PicoSATTestSuite extends LeoTestSuite{

  def loadSatProblem(c: PicoSAT) = {
    c.addClause(List(-1, -3, -2))
    c.addClause(List(-2, -3, -1))
    c.addClause(List(3))
  }

  test("Load PicoSAT", Checked) {
    val apiVersion = PicoSAT.apiVersion
    val version = PicoSAT.version
    println(s"PicoSAT version: $version. API version: $apiVersion.")
    assert(apiVersion >= 953)
    assert(version >= 965)
    assert(version >= apiVersion)
    println("Test sccess: PicoSAT loaded.")
  }

  test("Get PicoSAT context", Checked) {
    val context = PicoSAT(true)
    assert(context.state == PicoSAT.Unknown, "State of fresh context not unknown.")
    println("Test success: Got PicoSAT context.")
  }

  test("Satisfiable problem.", Checked) {
    val context = PicoSAT(true)
    val clause = List(1, -1)
    context.addClause(clause)
    assert(context.numAddedClauses == 1)
    assert(context.solve() == PicoSAT.SAT)
    assert(context.state == PicoSAT.SAT)
    println("Test success: Satisfiable.")
  }

  test("Unsatisfiable problem.", Checked) {
    val context = PicoSAT(true)
    context.addClause(1)
    context.addClause(-1)
    assert(context.numAddedClauses == 2)
    assert(context.solve() == PicoSAT.UNSAT)
    assert(context.state == PicoSAT.UNSAT)
    println("Test success: Unsatisfiable problem.")
  }

  test("Reset works.", Checked) {
    val context = PicoSAT(true)
    loadSatProblem(context)
    assert(context.numAddedClauses == 3)
    assert(context.solve() == PicoSAT.SAT)
    context.reset()
    assert(context.state == PicoSAT.Unknown)
    assert(context.numAddedClauses == 0)
    println("Test success: Reset works.")
  }

  test("Fresh variables.", Checked) {
    val context = PicoSAT(true)
    context.addClause(1)
    context.addClause(-1)
    assert(context.freshVariable == 2)
    context.adjust(10)
    assert(context.freshVariable == 11)
    println("Test success: Fresh variables.")
  }

  test("Adjustment functions callable.", Checked) {
    val context = PicoSAT(true)
    loadSatProblem(context)
    context.setGlobalDefaultPhase(3)
    context.setDefaultPhase(1, -3)
    context.setMoreImportant(1)
    context.setLessImportant(3)
    assert(context.solve() == PicoSAT.SAT)
    context.resetPhases()
    context.resetScores()
    context.removeLearnedClauses(50)
    println("Test success: Adjustment functions callable.")
  }

  test("Statistics.", Checked) {
    val context = PicoSAT(true)
    assert(context.numAddedClauses == 0)
    assert(context.numVariables == 0)

    loadSatProblem(context)

    assert(context.numAddedClauses == 3)
    assert(context.numVariables == 3)
    assert(context.timeSpendSolving == 0)

    // TODO: Since the result is in seconds this fails.
    //context.solve()
    //assert(context.timeSpendSolving > 0)

    println("Test success: Statistics.")
  }

  test("Get Assignment", Checked) {
    val context = PicoSAT(true)
    loadSatProblem(context)

    assert(context.getAssignment(1) == None)
    assert(context.getAssignment(2) == None)
    assert(context.getAssignment(3) == None)

    context.solve()

    assert(context.getAssignment(1) == Some(false))
    assert(context.getAssignment(2) == Some(false))
    assert(context.getAssignment(3) == Some(true))
    assertResult(Some(true))(context.getAssignmentToplevel(3))

    println("Test success: Get Assignment.")
  }

  test("Inconsistency", Checked) {
    val context = PicoSAT(true)
    loadSatProblem(context)

    context.addClause()

    assertResult(true)(context.inconsistent)
    println("Test success: Inconsitency.")
  }

  test("Assummtions", Checked) {
    val context = PicoSAT(true)
    loadSatProblem(context)

    context.assume(1)
    assertResult(context.solve())(PicoSAT.SAT)

    context.assume(1)
    context.assume(2)
    assertResult(context.solve())(PicoSAT.UNSAT)
    assert(context.failedAssumption(1))
    assert(context.failedAssumption(2))

    assertResult(context.failedAssumptions)(Array(1,2))

    println("Test success: Assumtions.")
  }

  test("Changed", Checked) {
    val context = PicoSAT(true)
    loadSatProblem(context)
    context.solve()
    context.solve()
    assert(!context.changed)
    println("Test success: Changed.")
  }

  test("Cores", Checked) {
    val context = PicoSAT(true)
    context.addClause(1, 3)
    context.addClause(2, 3)
    context.addClause(-3)
    context.addClause(-1)
    context.solve()

    assert(context.coreLiteral(1))
    assert(!context.coreLiteral(2))
    assert(context.usedLiteral(1))

    assert(context.coreClause(0))
    assert(!context.coreClause(1))

    println("Test success: Cores.")
  }

}
*/
