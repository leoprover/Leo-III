package leo
package modules.calculus.splitting

import leo.datastructures.Clause
import leo.datastructures.context.{NoSplit, BetaSplit, AlphaSplit, SplitKind}
import leo.datastructures._
import leo.modules.HOLSignature.{<=>, Impl, &, Not}

/**
 *
 * Common trait for a Split Rule in the Calculus.
 *
 * The implementation should satisfy subsumption,
 * namely `C ----> C_1 | C_2`  (C split into C_1 and C_2)
 * both
 *     C_1 => C
 *     C_2 => C
 * hence every split is reducing the size of the context.
 *
 * @author Max Wisniewski
 * @since 29/1/15
 */
trait Split {

  def name : String

  /**
   *
   * Performs both, check and Split at the same time.
   *
   * Returns a Sequence of generated Clauses together with
   * the reason of the split (Alpha = &&, Beta = ||).
   *
   * Whereby the the generated new contexts will be started with a sequence of new clauses.
   *
   * @param c - The clause to be split
   * @return Some(cs,s) with cs the splits and s the reason, None if no split with this rule is possible.
   */
  def split(c : Clause) : Option[(Seq[Seq[Clause]],SplitKind)]

  /**
   * Forces a split on c. If no split is possible an empty sequence of clauses is returned with `NoSplit` as a reason.
   *
   * @param c - The clause to be split.
   * @return (cs,s) from split if possible, ([],NoSplit) otherwise
   */
  def forceSplit(c : Clause) : (Seq[Seq[Clause]],SplitKind) = split(c) match {
    case Some(x)  => x
    case None     =>
      Out.warn(s"[Splitting-Calculus]:\n  Forced split on\n   ${c.pretty}\n but was not splitable.\n Returned input with no split")
      (Nil, NoSplit)
  }

  /**
   * Checks for the clause, if it is splitable.
   *
   * @param c - The clause to check for splitability
   * @return True if split returns Some(..), False otherwise
   */
  def splitable(c : Clause) : Boolean = split(c).isDefined
}

/**
 * First test for a splitting rule. Only removes equivalence.
 *
 * @author Max Wisniewski
 * @since 28/1/15
 */
object NaiveSplitting extends Split {

  val name = "NaiveSplit"

  /**
   * Returns a list of split clauses, if the initial clause is considered to be splitted.
   *
   * @param c - Given clause
   * @return Some(cs) with
   */
  override def split(c : Clause) : Option[(Seq[Seq[Clause]],SplitKind)] = {
    val maxLit = c.lits.max(Orderings.simple(LiteralWeights.fifo))
    val rLits = c.lits.filterNot(_ != maxLit)   // Remove maximal Literal

    Literal.asTerm(maxLit) match { // TODO: These matches never apply?!
      case (a <=> b) if maxLit.polarity =>
        val left = Impl(a,b)
        val right = Impl(b,a)
        return Some(List(List(createClause(Literal(left,true),rLits)),List(createClause(Literal(right,true),rLits))), AlphaSplit)
      case (a <=> b) =>             // if ! maxLit.polarity
        val left = &(a, Not(b))
        val right = &(Not(a),b)
        return Some(List(List(createClause(Literal(left,true),rLits)),List(createClause(Literal(right,false),rLits))), BetaSplit)
      case _  => return None
    }
  }

  protected[splitting] def createClause(l : Literal, cl : Seq[Literal]) : Clause = Clause.mkClause(l +: cl)
}

/**
 * Tries to split a Clause s.t. it is more Horn Afterwards.
 *
 *      A,B  --->  C,D
 *  --------------------------
 *    A -> C     |   B ---> D
 *
 *    Where C,D should be strictly decreasing.
 *
 * Neither A,B nor C,D have to be disjoined.
 *
 * @param f - Weight to split the literals.
 */
class HornSplit(f : Literal => Int) extends Split {

  val name = "GenericHornSplit"

  override def split(c : Clause) : Option[(Seq[Seq[Clause]], SplitKind)] = {
    val (neg,pos) = splitBy(c.lits, (l : Literal) => !l.polarity)   // neg => pos

    //val (a1,a2) = halfWeight(pos,f)
    val (l,r) = halfWeight(pos,f)


    if (l.isEmpty || r.isEmpty) return None   // No split available
    // TODO : Check for independency
    val leftClause = Clause.mkClause(neg ++ l, Derived)
    val rightClause = Clause.mkClause(neg ++ r, Derived)
    return Some((List(List(leftClause),List(rightClause))), AlphaSplit)
  }

  /**
   * Splits a sequence `s` into a sequence `a` of all elements of s for which a predicate p holds
   * and a sequence `b` of the rest.
   */
  protected[splitting] def splitBy[A](s : Seq[A], p : A => Boolean) : (Seq[A],Seq[A]) = {
    s.foldRight((Seq.empty[A],Seq.empty[A])){case (e,(a,b)) => if(p(e)) (e +: a,b) else (a,e +: b)}
  }

  /**
   * Dump implementation since it can be arbitrarily bad conditioned (Sorting by weight fixes the problem, in some sense)
   *
   */
  protected[splitting] def halfWeight[A](s : Seq[A], f : A => Int) : (Seq[A],Seq[A])= {
    val goal = s.foldLeft(0){(a,x) => a+f(x)} / 2
    // TODO: Sorting for good approximation
    s.foldRight((0,(List.empty[A],List.empty[A]))){case (e,(size,(a,b))) => if (size >= goal) (size,(a, e+: b)) else (size+f(e),(e +: a,b))}._2
  }
}

object ClauseHornSplit extends HornSplit((l : Literal) => 1) {
  override val name = "UnitWeightHornSplit"
}

object LiteralHornSplit extends HornSplit((l : Literal) => l.weight) {
  override val name : String = "ClauseWeightHornSplit"
}