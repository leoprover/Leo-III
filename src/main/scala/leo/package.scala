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

  /** The main [[leo.modules.output.logger.Logging `Logging`]] facility of Leo-III. */
  def Out: logger.Out.type = logger.Out
}
