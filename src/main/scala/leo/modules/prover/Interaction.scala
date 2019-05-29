package leo.modules.prover

import leo.Configuration
import leo.datastructures.AnnotatedClause
import leo.modules.output.ToTPTP

import scala.collection.mutable
import scala.ref.WeakReference

/**
  * Created by lex on 8/10/17.
  */
object Interaction {
  type S = State[AnnotatedClause]
  type Trigger = S => Boolean
  type Func = Function0[Unit]

  /// private fields
  // local organization / meta stuff
  private var sleep: Boolean = false
  private val triggers: mutable.Map[Trigger, Func] = mutable.Map.empty
  // actual additional payload, clauses etc.
  protected[prover] val clauseCache: mutable.Map[Long, WeakReference[AnnotatedClause]] = mutable.Map.empty

  /// methods
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
    scala.io.StdIn.readLine().trim
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
  def trackClause(cl: AnnotatedClause): Unit = {
    if (!Configuration.GUIDED) return
    clauseCache += (cl.id -> WeakReference(cl))
  }
  def trackClause(cls: Set[AnnotatedClause]): Unit = {
    if (!Configuration.GUIDED) return
    val it = cls.iterator
    while (it.hasNext) {
      trackClause(it.next())
    }
  }

  /// commands
  Evaluator.register({in => in == "queue"}, { (_,state) =>
    println(state.asInstanceOf[StateImpl[AnnotatedClause]].mpq.pretty)
    false
  })
  Evaluator.register({in => in == "exit" || in == ""}, { (_,_) =>
    true
  })
  Evaluator.register({in => in == "kill"}, { (_,_) =>
    System.exit(0)
    true
  })
  Evaluator.register({in => in.startsWith("skip")}, { (in,state) =>
    sleep = true
    try {
      val in0 = in.drop(5)
      if (in0 == "") true
      else {
        val n = in0.toInt
        val m = state.noProofLoops
        addTrigger({s => s.noProofLoops >=  m + n})
        true
      }
    } catch {
      case _: Exception => println("Invalid input, try again")
        false
    }
  })
  Evaluator.register({in => in == "step"}, { (_, state) =>
    try {
      val m = state.noProofLoops
      addTrigger({s => s.noProofLoops >=  m + 1})
      true
    } catch {
      case _: Exception => println("Invalid input, try again"); false
    }
  })

  Evaluator.register({in => in.startsWith("pretty")}, { (in,state) =>
    try {
      val n = in.drop(7).toLong
      val cl = clauseCache(n).get.get
      println(cl.pretty(state.signature))
    } catch {
      case _: Exception => println("Invalid input, try again")
    }
    false
  })
  Evaluator.register({in => in.startsWith("tptp")}, { (in,state) =>
    try {
      val n = in.drop(5).toLong
      val cl = clauseCache(n).get.get
      println(ToTPTP.withAnnotation(cl)(state.signature))
    } catch {
      case _: Exception => println("Invalid input, try again")
    }
    false
  })
  Evaluator.register({in => in.startsWith("parents")}, { (in,state) =>
    try {
      val n = in.drop(8).toLong
      val cl = clauseCache(n).get.get
      val parents = leo.modules.proofOf(cl)
      val parentsAsTPTP = parents.map(ToTPTP.withAnnotation(_)(state.signature))
      println(parentsAsTPTP.mkString("\n"))
    } catch {
      case _: Exception => println("Invalid input, try again")
    }
    false
  })
  Evaluator.register({in => in == "peek"}, { (in,state) =>
    try {
      val state0 = state.asInstanceOf[StateImpl[AnnotatedClause]]
      val cl = state0.mpq.head(state0.cur_prio)
      println(cl.id.toString)
    } catch {
      case _: Exception => println("Invalid input, try again")
    }
    false
  })
  Evaluator.register({in => in.startsWith("eval")}, { (in,state) =>
    try {
      import leo.datastructures.ClauseProxyOrdering
      val in0 = in.drop(5)
      val nm = in0.split(" ")
      val n = nm(0).toLong
      val m = nm(1).toInt
      val cl = clauseCache(n).get.get
      val weight = state.asInstanceOf[StateImpl[AnnotatedClause]].mpq.priority(m).asInstanceOf[ClauseProxyOrdering[Seq[Double]]]
      val w = weight.weightOf(cl)
      println(w.toString)
    } catch {
      case _: Exception => println("Invalid input, try again")
    }
    false
  })
  Evaluator.register({in => in.startsWith("take")}, { (in,state) =>
    try {
      val in0 = in.drop(5)
      val n = in0.toLong
      val cl = clauseCache(n).get.get
      state.addToHotList(cl)
    } catch {
      case _: Exception => println("Invalid input, try again")
    }
    false
  })
  Evaluator.register({in => in == "processed"}, { (_,state) =>
    try {
      if (state.processed.isEmpty) {
        println("<empty>")
      } else {
        println(s"Processed clauses:")
        state.processed.foreach(cl =>
          println(cl.pretty(state.signature))
        )
      }
    } catch {
      case _: Exception => println("Invalid input, try again")
    }
    false
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
