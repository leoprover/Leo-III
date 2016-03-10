package leo.agents
package impl

import leo.datastructures.blackboard.impl.{FormulaDataStore, SZSStore}
import leo.datastructures.context.Context
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.modules.output.{Output, SZS_GaveUp, StatusSZS, ToTPTP}
import leo.modules.output.logger.Out

object SZSScriptAgent {
  /**
   * Creates a new [[SZSScriptAgent]] to run an external prover on a given set of formulas
   * obtained through messages in the blackboard.
   *
   * @param cmd - The filepath to an executable prover. This also might be a script to run the prover.
   * @param encodeOutput - A method to translate the Clauses to a suitable representation for the external prover
   * @param reinterpreteResult - May transform the result depending on the status of the current Context (in a CounterSAT case Theorem will prover CounterSatisfiyability)
   * @return An agent to run an external prover on the specified translation.
   */
  def apply(cmd : String)(encodeOutput : Set[ClauseProxy] => Seq[String])(reinterpreteResult : StatusSZS => StatusSZS) : Agent = new SZSScriptAgent(cmd)(encodeOutput)(reinterpreteResult)
}

/**
 * A Script agent to execute a external theorem prover
 * and scans the output for the SZS status and inserts it into the Blackboard.
 */
class SZSScriptAgent(cmd : String)(encodeOutput : Set[ClauseProxy] => Seq[String])(reinterpreteResult : StatusSZS => StatusSZS) extends ScriptAgent(cmd) {
  override val name = s"SZSScriptAgent ($cmd)"

  override def encode(fs : Set[ClauseProxy]) : Seq[String] = encodeOutput(fs)

  /**
   * Scans the `input` Stream for an SZS status.
   *
   * @param input - THe input stream, that will be scanned for the status
   * @param err - THe error stream. Will be ignored.
   * @param errno - The return value.
   * @return
   */
  override def handle(c : Context, input: Iterator[String], err: Iterator[String], errno: Int): Result = {
    val context = c   // TODO Fix
    val it = input
    val b = new StringBuilder

    while(it.hasNext){
      val line = it.next()
      b.append("  Out: "+line+"\n")
      getSZS(line) match {
        case Some(status) =>
          context.close()
          Out.info(s"[$name]: Got ${status.output} from the external prover.")
          return Result().insert(StatusType)(SZSStore(reinterpreteResult(status), context))
        case None         => ()
      }
    }
    Out.info(s"[$name]: No SZS status returned in\n${b.toString}")
    context.close()
    Result().insert(StatusType)(SZSStore(SZS_GaveUp, context))
  }

  /**
   * Returns some SZSStatus, if the line contains one. Else none.
   *
   * @param line - The line to scan for szs status
   * @return Some(StatusSZS) if the line contains one, else none.
   */
  def getSZS(line : String) : Option[StatusSZS] = StatusSZS.answerLine(line)

  override def filter(event: Event): Iterable[Task] = event match {
    case SZSScriptMessage(f,c) => createTask(f,c)
    case _                   => List()
  }

  private def createTask(f : ClauseProxy, c : Context) : Iterable[Task] = {
    Out.trace(s"[$name]: Got a task.")
    val conj : ClauseProxy = Store(negateClause(f.cl), Role_Conjecture, c)
    val context : Set[ClauseProxy] = FormulaDataStore.getAll(c){ bf => bf.id != f.id}.toSet[ClauseProxy]
    return List(new ScriptTask(cmd, context + conj, c, this))
  }

  private def negateClause(c : Clause) : Clause = {
    val lit : Literal = Literal(orLit(c.lits),false)
    return Clause.mkClause(List(lit), Derived)
  }

  private def orLit(l : Seq[Literal]) : Term = l match {
    case Seq()        => LitTrue
    case l1 +: Seq()  => if(l1.polarity) l1.term else Not(l1.term)
    case l1 +: ls   => if(l1.polarity) |||(l1.term, orLit(ls)) else |||(Not(l1.term), orLit(ls))
  }
}







/**
 * A message with f (the to be conjecture)
 *
 * @param f
 */
private class SZSScriptMessage(val f: AnnotatedClause, val c : Context) extends Message {}

/**
 * Object to create and deconstruct messages to the SZSScriptAgent.
 */
object SZSScriptMessage {
  /**
   * Creates a new Message to the SZSScriptAgent. The formula `f` will
   * be transformed into the conjecture for the external agent.
   *
   * @param f - The conjecture
   * @return Message for the SZSScriptAgent.
   */
  def apply(f: AnnotatedClause)(c : Context) : Message = new SZSScriptMessage(f,c)

  /**
   * Deconstructs an Event, if it is a Message to the SZSScriptAgent.
   *
   * @param m
   * @return
   */
  def unapply(m : Event) : Option[(AnnotatedClause, Context)] = m match {
    case m : SZSScriptMessage => Some((m.f,m.c))
    case _                    => None
  }
}