package leo.modules.prover

import leo.Configuration
import leo.datastructures.AnnotatedClause

import scala.collection.mutable

/**
  * Created by lex on 8/10/17.
  */
object Interaction {
  type S = State[AnnotatedClause]
  type Trigger = S => Boolean
  type Func = Function0[Unit]

  private var sleep: Boolean = false
  private val triggers: mutable.Map[Trigger, Func] = mutable.Map.empty
   def apply(implicit state: State[AnnotatedClause]): Unit = {
     if (!Configuration.GUIDED) return

     checkTriggers()
     if (sleep) return
     var done: Boolean = false
     while (!done) {
       val in = ask()
       if (in == null) {
         done = true
       } else {
         if (in == "exit") done = true
         else done = Evaluator(in)
       }
     }
   }
  private def ask(): String = {
    print("> ")
    scala.io.StdIn.readLine()
  }

  private def checkTriggers()(implicit state: S): Unit = {
    val t = triggers.keysIterator
    while(t.hasNext) {
      val t0 = t.next()
      if (t0(state)) {
        sleep = false
        println("#triggered")
        val func = triggers(t0)
        if (func != null) func.apply()
        triggers.remove(t0)
      }
    }
  }
  def addTrigger(t: Trigger, action: Func = null): Unit = {
    triggers.+=(t -> action)
  }

  Evaluator.register({in => in == "queues"}, { (in,state) =>
    println(state.queues().pretty)
    false
  })
  Evaluator.register({in => in == "exit"}, { (in,state) =>
    true
  })
  Evaluator.register({in => in.startsWith("sleep")}, { (in,state) =>
    sleep = true
    try {
      val n = in.drop(6).toInt
      val m = state.noProofLoops
      addTrigger({s => s.noProofLoops ==  m + n})
      true
    } catch {
      case _: Exception => println("Invalid input, try again")
        false
    }
  })
  Evaluator.register({in => in.startsWith("pretty")}, { (in,state) =>
    sleep = true
    try {
      val n = in.drop(7).toInt

      false
    } catch {
      case _: Exception => println("Invalid input, try again")
        false
    }
  })

  object Evaluator {
    type Predicate = String => Boolean
    type Action = Function2[String, S, Boolean]
    private val registered: mutable.Map[Predicate, Action] = mutable.Map.empty

    def register(predicate: Predicate, action: Action): Unit = {
      registered.+=(predicate -> action)
    }

    def apply(input: String)(implicit state: S): Boolean = {
      val ps = registered.keysIterator
      while (ps.hasNext) {
        val p = ps.next()
        if (p(input)) {
          return registered(p).apply(input,state)
        }
      }
      println("Unrecognized command. Type \"exit\" to exit interactive shell.")
      false
    }
  }
}
