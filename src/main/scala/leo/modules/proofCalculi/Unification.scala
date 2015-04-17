package leo.modules.proofCalculi

import leo.datastructures.term.Term
import leo.datastructures.{Type, Subst}

trait Unification {

 type Substitute = (Subst,Seq[Type])

  /**
   *
   * @param t - First term to unify
   * @param s - Second term to unify
   * @param n - Offset for new implicit Bindings (n+1 will be the next binding). Number of parameters.
   * @return a stream of Substitution to make both terms equal, empty stream if they are not unifiable
   */
  def unify(t : Term, s : Term, n : Int) : Iterable[Subst]
}

/**
 * Tests solely for equality
 */
object IdComparison extends Unification{
  override def unify(t : Term, s : Term, n : Int) : Iterable[Subst] = if (s == t) Stream(Subst.id) else Stream.empty
}

// Look for TODO, TOFIX (and TOTEST in the corresponding test file)
// TODO: change List into a data structure more sutiable to sorting, etc.
/**
 * created on: 15/04/2015
 * author: Tomer Libal
 */
object HuetsPreUnification extends Unification {

  import leo.datastructures.term.Term._
  import leo.modules.proofCalculi.util.executionModels._
  import annotation.tailrec

  type UEq = Tuple2[Term,Term]

  def unify (t : Term, s : Term, n : Int) : Iterable[Subst] = {
    // TOFIX: t and s must be in eta long form

    // returns a stream whose head is a pre-unifier and whose body computes the next unifiers
    new NDStream[Subst](new MyConfiguration(List(Tuple2(t,s)), List()), MyFun) with BFSAlgorithm
  }

  protected def isFlexible(t: Term): Boolean = t.headSymbol match {
    case Bound(_, ind) if t.looseBounds.contains(ind) => true // flexible variable
    case _ => false // function symbol or bound variable
  }

  // tuples2 of terms are sorted according to terms and terms are sorted such that
  // rigid terms are before flexible ones
  // keeping the list always ordered like that gives us:
  // 1) all flex-flex are at the end and rigid-rigid are at the front
  // 2) if we always apply exhaustively delete and decomp on inserted equations, we have
  // 3) first equation is flex-flex -> problem is in pre-solved form
  // 4) first equation is rigid-rigid -> symbol clash
  // 5) apply bind or imitate+project
  // t is less than s only if it is not flexible and s is rigid
  private def sort(e1: UEq, e2: UEq) =
    (!isFlexible(e1._1) && !isFlexible(e1._2)) ||
    (isFlexible(e2._1) && isFlexible(e2._2))

  // computes the substitution from the solved problems
  protected def computeSubst(sproblems: List[UEq]): Subst = ???

  private def applySubstToList(s: Subst, l: List[UEq]): List[UEq] =
    l.map(e => (e._1.closure(s).betaNormalize,e._2.closure(s).betaNormalize))

  // apply exaustively delete, comp and bind on the set and sort it at the end
  @tailrec
  protected def detExhaust(uproblems: List[UEq], sproblems: List[UEq]): Tuple2[List[UEq], List[UEq]]  = {
    // apply delete
    val ind1 = uproblems.indexWhere(DeleteRule.canApply)
    if (ind1 > -1)
      detExhaust(uproblems.take(ind1) ++ uproblems.drop(ind1+1), sproblems)
    // apply decomp
    else {
      val ind2 = uproblems.indexWhere(DecompRule.canApply)
      if (ind2 > -1)
        detExhaust((DecompRule(uproblems(ind2)) ++ uproblems.take(ind2) ++ uproblems.drop(ind2+1)).sortWith(sort), sproblems)
    // apply bind
      else {
        val ind3 = uproblems.indexWhere(BindRule.canApply)
        if (ind3 > -1) {
          val be = uproblems(ind3)
          val sb = computeSubst(List(be))
          detExhaust(applySubstToList(sb, uproblems.take(ind3) ++ uproblems.drop(ind3+1)), be::applySubstToList(sb,sproblems))
        } else
    // none is applicable, do nothing
        (uproblems,sproblems)
      }
    }
  }

  // Huets rules
  trait HuetsRule[R] extends Function1[UEq, R] {
    // the functional apply applies the rule to an equation in order to produce other equations
    def canApply(e: UEq): Boolean // returns true if we can apply the rule
  }

  /**
   * equation is not oriented
   */
  object ImitateRule extends HuetsRule[UEq] {
    def apply(e: UEq) = e
    def canApply(e: UEq) = ???
  }

  /**
   * equation is not oriented
   */
  object ProjectRule extends HuetsRule[List[UEq]] {
    def apply(e: UEq) = List(e)
    def canApply(e: UEq) = ???
  }

  /**
   * BindRule tells if Bind is applicable
   * equation is not oriented
   * 1) compute a substitution from this equation
   * 2) apply this substitution on all equations in uproblems and sproblems
   * 3) insert the equation into sproblems
   */
  object BindRule extends HuetsRule[Unit] {
    def apply(e: UEq) = ()
    def canApply(e: UEq) = {
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      // check head is flexible
      if (!isFlexible(t)) false
      // getting flexible head
      val (_,x) = Bound.unapply(t.headSymbol).get
      // check t is eta equal to x
      // check it doesnt occur in s
      ???
    }
  }

  /**
   * returns true if the equation can be deleted
   */
  object DeleteRule extends HuetsRule[Unit] {
    def apply(e: UEq) = ()
    def canApply(e: UEq) = {
      val (t,s) = e
      t.equals(s)
    }
  }

  /**
   * returns the list of equations if the head symbols are the same function symbol.
   */
  object DecompRule extends HuetsRule[List[UEq]] {
    def apply(e: UEq) = e match {
      case (_ ∙ sq1, _ ∙ sq2) => ??? //TODO
      case _ => throw new IllegalArgumentException("impossible")
    }
    def canApply(e: UEq) = e match {
      case (hd1 ∙ _, hd2 ∙ _) if (hd1.equals(hd2)) => true
      case _ => false
    }
  }

  private def simplifyArguments(l: Seq[Either[Term,Type]]): Seq[Term] = l.filter(_.isLeft).map(_.left.get)

  // the state of the search space
  protected class MyConfiguration(val uproblems: List[UEq], val sproblems: List[UEq], val result: Option[Subst], val isTerminal: Boolean)
    extends Configuration[Subst] {
    def this(result: Option[Subst]) = this(List(), List(), result, true) // for success
    def this(l: List[UEq], s: List[UEq]) = this(l, s, None, false) // for in node
    def toStr  = uproblems.map(x => ("<"+x._1.pretty+", "+ x._2.pretty+">"))
  }

  // the transition function in the search space (returned list containing more than one element -> ND step, no element -> failed branch)
  protected object MyFun extends Function1[Configuration[Subst], List[Configuration[Subst]]] {

    import  scala.collection.mutable.ListBuffer

    // Huets procedure is defined here
    def apply(conf2: Configuration[Subst]): List[Configuration[Subst]] = {
      val conf = conf2.asInstanceOf[MyConfiguration]
      // we always assume conf.uproblems is sorted and that delete, decomp and bind were applied exaustively
      val (uproblems, sproblems) = detExhaust(conf.uproblems,conf.sproblems)

      // if uproblems is empty, then succeeds
      if (uproblems.isEmpty) List(new MyConfiguration(Some(computeSubst(sproblems))))
      // else consider top equation
      val (t,s) = uproblems.head
      // if it is rigid-rigid -> fail
      if (!isFlexible(t) && !isFlexible(s)) List()
      // if it is flex-flex -> succeeds and compute sub from the solved set
      if (isFlexible(t) && isFlexible(s)) List(new MyConfiguration(Some(computeSubst(sproblems))))

      // else we have a flex-rigid and we cannot apply bind

      val lb = new ListBuffer[MyConfiguration]
      // compute the imitate partial binding and add the new configuration
      lb.append(new MyConfiguration(ImitateRule(t,s)::uproblems, sproblems))

      // compute all the project partial bindings and add them to the return list
      ProjectRule(t,s).foreach (e => lb.append(new MyConfiguration(e::uproblems, sproblems)))

      lb.toList
    }
  }
}

// TODO: The next stuff should be stored in some general util package
/**
 * created on: 15/04/2015
 * author: Tomer Libal
 */
package util.executionModels {
  import collection.mutable
  import annotation.tailrec

  trait Configuration[S] {
    def result: Option[S]
    def isTerminal: Boolean // terminal nodes are not added to the configuration queue
  }

  //mutable, non deterministic, stream
  abstract class NDStream[S /*result type*/ ]( val initial: Configuration[S], val myFun: Configuration[S] => Iterable[Configuration[S]] ) extends Iterable[S] with SearchAlgorithm {

    type T = Configuration[S]
    private val results: mutable.Queue[S] = new mutable.Queue[S]()
    protected var hd: Option[S] = None
    protected def initDS: Unit = {
      add( initial )
      hd = nextVal
    }

    @tailrec
    protected final def nextVal: Option[S] = {
      val res = results.headOption
      if ( res != None ) {
        results.dequeue
        res
      } else {
        val conf = get
        if ( conf == None ) None
        else {
          val confs: Iterable[Configuration[S]] = myFun( conf.get )
          confs.foreach( x => {
            if ( x.result != None )
              results.enqueue( x.result.get );
            if ( !x.isTerminal )
              add( x )
          } )
          nextVal
        }
      }
    }

    // TOFIX: iterator can only be called once right now as the ndstream is mutable!
    var wasCalled = false
    def iterator: Iterator[S] =
      if (!wasCalled) new Iterator[S] {
        wasCalled = true
        def next: S =
          if (hd.isEmpty) throw new NoSuchElementException("Stream is empty")
          else {
            val ret = hd.get
            hd = nextVal
            ret
          }
        def hasNext: Boolean = !hd.isEmpty
    }
    else throw new UnsupportedOperationException("iterator for NDStream can right now be called only once!")
  }

import collection.mutable.{ Queue => MQueue }
import collection.immutable.Queue
import scala.math.Ordering.Implicits._

  trait SearchAlgorithm {
    type T
    protected def initDS: Unit // called by the algorithm and implemented by some object using it as the object is initialized before the trait
    protected def add( t: T ): Unit
    protected def get: Option[T]
  }

  trait BFSAlgorithm extends SearchAlgorithm {
    private val ds: MQueue[T] = new MQueue[T]()
    initDS // if the object requires the ds to be already existing, then it will not fail now
    protected def add( conf: T ): Unit = ds += conf
    protected def get: Option[T] = {
      val res = ds.headOption
      if ( res != None ) ds.dequeue
      res
    }
  }
}
