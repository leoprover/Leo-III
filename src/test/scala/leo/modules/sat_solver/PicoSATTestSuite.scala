package leo.modules.sat_solver

import leo.{Checked, LeoTestSuite}

/**
  * Created by Hans-Jörg Schurr on 8/11/16.
  */
class PicoSATTestSuite extends LeoTestSuite{

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
    assert(context.solve() == PicoSAT.SAT)
    assert(context.state == PicoSAT.SAT)
    println("Test success: Satisfiable.")
  }

  test("Unsatisfiable problem.", Checked) {
    val context = PicoSAT(true)
    context.addClause(1)
    context.addClause(-1)
    assert(context.solve() == PicoSAT.UNSAT)
    assert(context.state == PicoSAT.UNSAT)
    println("Test success: Unsatisfiable problem.")
  }

}
