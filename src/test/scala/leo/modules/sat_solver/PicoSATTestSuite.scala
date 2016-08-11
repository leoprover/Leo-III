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
  }

}
