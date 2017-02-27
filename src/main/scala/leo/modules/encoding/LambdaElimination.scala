package leo.modules.encoding

import leo.datastructures.Term

/**
  * Created by lex on 24.02.17.
  */
trait LambdaEliminationStrategy {
  def eliminateLambda(t: Term): Term = ???
}

object LES_SKI extends LambdaEliminationStrategy {
  import leo.datastructures.Term.{λ, mkBound}
  /** Curry I combinator given by `I(x) = x`. */
  final val combinator_I_def: Term = λ(1)(mkBound(1,1))
  /** Curry I combinator given by `K x y = x`. */
  final def combinator_K: Term = ???
  /** Curry I combinator given by `S x y z = x z (y z)`. */
  final def combinator_S: Term = ???
  /** Curry I combinator given by `B x y z = x (y z)`. */
  final def combinator_B: Term = ???
  /** Curry I combinator given by `C x y z = x z y`. */
  final def combinator_C: Term = ???
}

object LES_Turner extends LambdaEliminationStrategy

object LES_LambdaLifting extends LambdaEliminationStrategy
