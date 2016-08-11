package leo.modules.sat_solver

import scala.util.Try
import ch.jodersky.jni.nativeLoader

/**
  * Created by Hans-Jörg Schurr on 7/27/16.
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

  def state = {
    State(picosat_res(context))
  }

  def reset = {
    picosat_reset(context)
    context = picosat_init()
    if(tracing)
      picosat_enable_trace_generation(context)
  }

  def freshVariable = {
    picosat_inc_max_var(context)
  }

  def addClause(lits:Iterable[Int]) : Int = {
    lits.foreach(picosat_add(context,_))
    picosat_add(context, 0)
  }
  def addClause(lits:Int*) : Int = {
    addClause(lits)
  }

  def solve : State= {
    State(picosat_sat(context))
  }

  override def finalize(): Unit = picosat_reset(context)

  @native def picosat_init() : Long
  @native def picosat_enable_trace_generation(context: Long) : Int
  @native def picosat_reset(context: Long) : Unit
  @native def picosat_res(context: Long) : Int
  @native def picosat_inc_max_var(context: Long) : Int
  @native def picosat_add(context: Long, lit: Int) : Int
  @native def picosat_sat(context: Long) : Int
}

@nativeLoader("picosat0")
object PicoSAT {
  def apply(enableTracing: Boolean = false) : PicoSAT = {
    if(!libraryLoaded)
      loadLibrary
    new PicoSAT(enableTracing)
  }

  def version = {
    Integer.parseInt(picosat_version())
  }

  def apiVersion = {
    picosat_api_version()
  }

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

  private var libraryLoaded = false

  private def loadLibrary = {
    //System.loadLibrary("picosat")

    if (apiVersion > version)
      throw new UnsupportedOperationException
        ("PicoSAT version " + version  + " too old. At least version " + apiVersion + " is needed.")

    libraryLoaded = true;
  }

	@native def picosat_version() : String
	@native def picosat_api_version() : Int
  @native def picosat_init() : Long
}