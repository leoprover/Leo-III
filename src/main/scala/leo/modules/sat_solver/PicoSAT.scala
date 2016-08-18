package leo.modules.sat_solver

import scala.util.Try
import ch.jodersky.jni.nativeLoader

/**
  * Represents a stateful PicoSAT context.
  *
  * Objects of this class represent independent PicoSAT contexts. Since PicoSAT is an iterative solver the
  * contexts are stateful. After solving additional clauses can be added.
  *
  * @author Hans-Jörg Schurr
  * @since 7/27/16.
  */
class PicoSAT private (enableTracing: Boolean) {
  import PicoSAT.State

  protected var context = picosat_init()
  if(context == 0)
    throw new OutOfMemoryError()

  protected val tracing = if(enableTracing)
    if (picosat_enable_trace_generation(context) != 0)
      true
    else throw new UnsupportedOperationException("Trace generation not supported.")
  else false

  /**
    * @return Returns the last result of calling [[PicoSAT.solve()]] or [[PicoSAT.Unknown]] if not yet called.
    */
  def state = {
    State(picosat_res(context))
  }

  /**
    * Resets the solver contexts by deleting the current one and creating a new one. If tracing was previously enabled
    * it will be enabled again.
    */
  def reset = {
    picosat_reset(context)
    context = picosat_init()
    if(tracing)
      picosat_enable_trace_generation(context)
  }

  /** Allocates a new, yet unused, variable. For subsequent calls to functions on this context thread this variable
    * as if it has been used.
    *
    * @return Returns a new unused variable.
    */
  def freshVariable = {
    picosat_inc_max_var(context)
  }

  /** Adds a new clause to the context. A clause consists of a list of integer values representing literals. A negative
    * value represents the negated variable. It is not necessary to use [[PicoSAT.freshVariable]] to allocate a variable
    * first. Adding a clause resets the assignment and state.
    */
  def addClause(lits:Iterable[Int]) : Int = {
    lits.foreach(picosat_add(context,_))
    picosat_add(context, 0)
  }
  def addClause(lits:Int*) : Int = {
    addClause(lits)
  }

  /* Solves the current context.
   * @return The result of attempting to solve the context.
   */
  def solve() : State= {
    State(picosat_sat(context, -1))
  }

  override def finalize(): Unit = picosat_reset(context)

  @native def picosat_init() : Long
  @native def picosat_enable_trace_generation(context: Long) : Int
  @native def picosat_reset(context: Long) : Unit
  @native def picosat_res(context: Long) : Int
  @native def picosat_inc_max_var(context: Long) : Int
  @native def picosat_add(context: Long, lit: Int) : Int
  @native def picosat_sat(context: Long, decision_limit: Int) : Int
}

/*
 * Companion object used to create PicoSAT contexts.
 */
@nativeLoader("picosat0")
object PicoSAT {
  /**
   * Returns a new PicoSAT context.
   *
   * @param enableTracing Enables tracing used to genrates cores. Enabling tracing results in the solver using more
    *                      memory.
   */
  def apply(enableTracing: Boolean = false) : PicoSAT = {
    new PicoSAT(enableTracing)
  }

  /**
    * @return Returns the version of PicoSAT loaded.
    */
  def version = {
    Integer.parseInt(picosat_version())
  }

  /**
    * @return Returns the API version the bindings are build against. [[PicoSAT.version]] must be at least
    *         [[PicoSAT.apiVersion]].
    */
  def apiVersion = {
    picosat_api_version()
  }

  /**
    * Used to represent the state a context is in.
    */
  sealed abstract class State(code: Int)
  object State {
    def apply(code: Int): State = code match {
      case 0 => Unknown
      case 10 => SAT
      case 20 => UNSAT
    }
  }
  case object Unknown extends State(0)
  case object SAT extends State(10)
  case object UNSAT extends State(20)

	@native def picosat_version() : String
	@native def picosat_api_version() : Int
}