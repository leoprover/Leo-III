import leo.modules.output.logger

/**
  * Root package of the Leo-III prover. It collects basic types and classe and,
  * in particular, contains the main entry-point for the Leo-III executable (see
  * [[leo.Main]]).
  * 
  * == Package structure ==
  *
  * The sub packages are:
  * 
  *   - [[leo.datastructures]] which consists of all traits and implementations
  *     of general purpose and logical data types (such as [[leo.datastructures.Term `Term`]],
  *     [[leo.datastructures.Type `Type`]] or [[leo.datastructures.Literal `Literal`]]).
  *   - [[leo.modules]] contains all functionality-related packages such as
  *     basic algorithms (e.g. [[leo.modules.calculus.Unification `Unification`]],
  *     [[leo.modules.calculus.Matching `Matching`]]), calculus rules (e.g.
  *     [[leo.modules.calculus.OrderedParamod `OrderedParamod`]],
  *     [[leo.modules.calculus.OrderedEqFac `OrderedEqFac`]]),
  *     input and output functionality and further pocedures.
  */
package object leo {

  type TermOrdering = leo.datastructures.TermOrdering

  final val version: String = "1.6.10" // TODO: Read that somehow from build.sbt
  final val brand: String =
    raw"""
       | __                             ______  ______  ______
       |/\ \                           /\__  _\/\__  _\/\__  _\
       |\ \ \         __    ___        \/_/\ \/\/_/\ \/\/_/\ \/
       | \ \ \      /'__`\ / __`\  _______\ \ \   \ \ \   \ \ \
       |  \ \ \____/\  __//\ \/\ \/\______\\_\ \__ \_\ \__ \_\ \__
       |   \ \_____\ \____\ \____/\/______//\_____\/\_____\/\_____\
       |    \/_____/\/____/\/___/          \/_____/\/_____/\/_____/  v${version}
       |""".stripMargin

  /** The main [[leo.modules.output.logger.Logging `Logging`]] facility of Leo-III. */
  def Out: logger.Out.type = logger.Out
}
