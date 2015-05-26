package leo.modules.calculus

import leo.datastructures.{Term, Type, Subst}
import leo.modules.output.SZS_EquiSatisfiable

trait Unification extends CalculusRule {

 type Substitute = (Subst,Seq[Type])

  /**
   *
   * @param t - First term to unify
   * @param s - Second term to unify
   * @return a stream of Substitution to make both terms equal, empty stream if they are not unifiable
   */
  def unify(t : Term, s : Term) : Iterable[Subst]

  val name = "unification"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)
}

/**
 * Tests solely for equality
 */
object IdComparison extends Unification{
  override def unify(t : Term, s : Term) : Iterable[Subst] = if (s == t) Stream(Subst.id) else Stream.empty
}

// Look for TODO, TOFIX (and TOTEST in the corresponding test file)
// TODO: change List into a data structure more sutiable to sorting, etc.
/**
 * created on: 15/04/2015
 * author: Tomer Libal
 */
object HuetsPreUnification extends Unification {

  import Term._
  import leo.datastructures.TermFront
  import leo.datastructures.BoundFront
  import leo.modules.calculus.util.executionModels._
  import annotation.tailrec

  type UEq = Tuple2[Term,Term]

  def unify (t1 : Term, s1 : Term) : Iterable[Subst] = {

    val t = t1.etaExpand
    val s = s1.etaExpand

    // returns a stream whose head is a pre-unifier and whose body computes the next unifiers
    new NDStream[Subst](new MyConfiguration(List(Tuple2(t,s)), List()), MyFun) with BFSAlgorithm
  }

  protected def isFlexible(t: Term): Boolean = t.headSymbol match {
    case MetaVar(_, _) => true // flexible variable
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
  protected def computeSubst(sproblems: Seq[UEq]): Subst = {
    val maxIdx: Int = MetaVar.unapply(sproblems.maxBy(e => MetaVar.unapply(e._1).get._2)._1).get._2
    var sub = Subst.id
    for (i <- 1 to maxIdx)
      sproblems.find(e => MetaVar.unapply(e._1).get._2 == maxIdx - i + 1) match {
        case Some((_,t)) => sub = sub.cons(TermFront(t))
        case _ => sub = sub.cons(BoundFront(maxIdx - i + 1))
    }
    sub
  }

  // bug one: see output - a sub with a term at index 15 replaces a bound variable at index 16 and not 15
  // bug two: negative fresh variables cause some term functions to throw index out of bound exception, to get it, simply change back
  // the fresh variables counter to negative. I changed it into positive to see if there are other bugs
  private def applySubstToList(s: Subst, l: Seq[UEq]): Seq[UEq] =
    l.map(e => (e._1.substitute(s).betaNormalize,e._2.substitute(s).betaNormalize))

  // apply exaustively delete, comp and bind on the set and sort it at the end
  @tailrec
  protected def detExhaust(uproblems: Seq[UEq], sproblems: Seq[UEq]): Tuple2[Seq[UEq], Seq[UEq]]  = {
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
          val be = BindRule(uproblems(ind3))
          val sb = computeSubst(List(be))
          detExhaust(applySubstToList(sb, uproblems.take(ind3) ++ uproblems.drop(ind3+1)), applySubstToList(sb,sproblems):+ be)
        } else
    // none is applicable, do nothing
        (uproblems,sproblems)
      }
    }
  }

  /**
   *  all terms are flex variables
   */
  private def computeDefaultSub(ls: Seq[Term]): Subst = {
    val it = ls.iterator
    var map : Map[Int, Term] = Map()
    while (it.hasNext) {
      val flex = it.next()
      val (ty, id) = MetaVar.unapply(flex).get
      val tys = ty.funParamTypesWithResultType

      map = map + (id -> λ(tys.init)(Term.mkFreshMetaVar(tys.last)))
    }
    Subst.fromMap(map)

    //val maxIdx: Int = Bound.unapply(ls.maxBy(e => Bound.unapply(e._1).get._2)._1).get._2
//    var sub = Subst.id
    /*for (i <- 1 to maxIdx)
      ls.find(e => Bound.unapply(e._1).get._2 == maxIdx - i + 1) match {
        case Some((typ,t)) => {
          sub = sub.cons()
        }
        case _ => sub = sub.cons(BoundFront(maxIdx - i + 1))
    }*/
//    sub
  }

  // n is arity of variable
  // m is arity of head
  // hdSymb is head
  // y1,..,yn are new bound variable
  // x1,..,xm are new free variables
  protected[calculus] def partialBinding(typ: Type, hdSymb: Term) = {
    val ys = typ.funParamTypes.zip(List.range(1,typ.funArity+1)).map(p => Term.mkBound(p._1,p._2))
    val xs =
      if (ys.isEmpty)
        hdSymb.ty.funParamTypes.map(p => Term.mkFreshMetaVar(p))
      else {
        val ysTyp = Type.mkFunType(ys.map(_.ty))
        hdSymb.ty.funParamTypes.map(p => Term.mkTermApp(Term.mkFreshMetaVar(Type.mkFunType(ysTyp,p)), ys))
      }
    val t = Term.mkTermApp(hdSymb,xs)

    val aterm = Term.λ(ys.map(_.ty))(t)
    aterm.etaExpand
  }

  // Huets rules
  trait HuetsRule[R] extends Function1[UEq, R] {
    // the functional apply applies the rule to an equation in order to produce other equations
    def canApply(e: UEq): Boolean // returns true if we can apply the rule
  }

  // not to forget that the approximations must be in eta-long-form
  /**
   * 4a
   * equation is not oriented
   */
  object ImitateRule extends HuetsRule[UEq] {

    def apply(e: UEq): UEq = {
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      (t.headSymbol,partialBinding(t.headSymbol.ty,  s.headSymbol))
    }
      // must make sure s doesnt have as head a bound variable
    def canApply(e: UEq) = {
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      s.headSymbol match {
        // cannot be flexible and fail on bound variable
        case Bound(_,_) => false
        case _ => true
      }
    }
  }

  /**
   * 4b
   * equation is not oriented
   */
  object ProjectRule extends HuetsRule[Seq[UEq]] {
    def apply(e: UEq): Seq[UEq] = {
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      val bvars = t.headSymbol.ty.funParamTypes.zip(List.range(1,t.headSymbol.ty.funArity+1)).map(p => Term.mkBound(p._1,p._2))
      bvars.map(e => (t.headSymbol,partialBinding(t.headSymbol.ty, e)))
    }
    def canApply(e: UEq) = ??? // always applicable on flex-rigid equations not under application of Bind
  }

  /**
   * 3
   * BindRule tells if Bind is applicable
   * equation is not oriented
   * return an equation (x,s) substitution is computed from this equation later
   */
  object BindRule extends HuetsRule[UEq] {
    def apply(e: UEq) = {
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      // getting flexible head
      (t.headSymbol,s)
    }
    def canApply(e: UEq) = {
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      // check head is flexible
      if (!isFlexible(t)) false
      // getting flexible head
      else {
        val (_,x) = MetaVar.unapply(t.headSymbol).get
      // check t is eta equal to x
        if (!t.headSymbol.etaExpand.equals(t) && !t.equals(t.headSymbol)) false
      // check it doesnt occur in s
        else !s.metaIndices.contains(x)
      }
    }
  }

  /**
   * 1
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
   * 2
   * returns the list of equations if the head symbols are the same function symbol.
   */
  object DecompRule extends HuetsRule[Seq[UEq]] {
    def apply(e: UEq) = e match {
      case (_ ∙ sq1, _ ∙ sq2) => (simplifyArguments(sq1)).zip(simplifyArguments(sq2))
      case _ => throw new IllegalArgumentException("impossible")
    }
    def canApply(e: UEq) = e match {
      case (hd1 ∙ _, hd2 ∙ _) if (hd1.equals(hd2)) && !isFlexible(hd1) => true
      case _ => false
    }
  }

  /**
   * Alex advices we can ignore all types in the list (for now)
   */
  private def simplifyArguments(l: Seq[Either[Term,Type]]): Seq[Term] = l.filter(_.isLeft).map(_.left.get)

  // the state of the search space
  protected class MyConfiguration(val uproblems: Seq[UEq], val sproblems: Seq[UEq], val result: Option[Subst], val isTerminal: Boolean)
    extends Configuration[Subst] {
    def this(result: Option[Subst]) = this(List(), List(), result, true) // for success
    def this(l: Seq[UEq], s: Seq[UEq]) = this(l, s, None, false) // for in node
    override def toString  = "{" + uproblems.flatMap(x => ("<"+x._1.pretty+", "+ x._2.pretty+">")) + "}"
  }

  // the transition function in the search space (returned list containing more than one element -> ND step, no element -> failed branch)
  protected object MyFun extends Function1[Configuration[Subst], Seq[Configuration[Subst]]] {

    import  scala.collection.mutable.ListBuffer

    // Huets procedure is defined here
    def apply(conf2: Configuration[Subst]): Seq[Configuration[Subst]] = {
      val conf = conf2.asInstanceOf[MyConfiguration]
      // we always assume conf.uproblems is sorted and that delete, decomp and bind were applied exaustively
      val (uproblems, sproblems) = detExhaust(conf.uproblems,conf.sproblems)

      // if uproblems is empty, then succeeds
      if (uproblems.isEmpty) {
        List(new MyConfiguration(Some(computeSubst(sproblems))))
      }
      // else consider top equation
      else {
        val (t,s) = uproblems.head
        // if it is rigid-rigid -> fail
        if (!isFlexible(t) && !isFlexible(s)) List()
        else {
          // if it is flex-flex -> all equations are flex-flex -> succeeds and compute sub from the solved set
          // TOFIX compute a substitution for all types that maps all variables in the uproblems set to the same term
          // and then compose this subtitution to the one generated by computeSubst
          if (isFlexible(t) && isFlexible(s)) {
            val defSub = computeDefaultSub(uproblems.foldLeft(List[Term]())((ls,e) => e._1.headSymbol::e._2.headSymbol::ls))
            List(new MyConfiguration(Some(defSub.comp(computeSubst(sproblems)))))
          } else {

            // else we have a flex-rigid and we cannot apply bind

            val lb = new ListBuffer[MyConfiguration]
            // compute the imitate partial binding and add the new configuration
            if (ImitateRule.canApply(t,s)) lb.append(new MyConfiguration(ImitateRule(t,s)+:uproblems, sproblems))

            // compute all the project partial bindings and add them to the return list
            ProjectRule(t,s).foreach (e => lb.append(new MyConfiguration(e+:uproblems, sproblems)))

            lb.toList
          }
        }
      }
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

    protected var MAX_DEPTH : Int = 60  // TODO Load from Configurations

    type T = (Configuration[S], Int)  // Configuration and Depth in the search
    private val results: mutable.Queue[S] = new mutable.Queue[S]()
    protected var hd: Option[S] = None
    protected val hdFunc: () => Option[S] = () => nextVal
    protected var terminal: Boolean = false
    protected def initDS: Unit = {
      add( (initial, 0) )
      hd = hdFunc()
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
        else if (conf.get._2 < MAX_DEPTH) {
          val confs: Iterable[Configuration[S]] = { myFun( conf.get._1 )}
          confs.foreach( x => {
            if ( x.result != None )
              results.enqueue( x.result.get )
            if ( !x.isTerminal ) {
              //println("New configuration in Depth "+conf.get._2)
              add((x, conf.get._2 + 1))
            }
          } )
          nextVal
        } else {
          None
        }
      }
    }

    // TOFIX: iterator can only be called once right now as the ndstream is mutable!
    var wasCalled = false
    def iterator: Iterator[S] =
      if (!wasCalled) new Iterator[S] {
        wasCalled = true
        def next: S = {
          if (hd.isEmpty && terminal) throw new NoSuchElementException("Stream is empty")
          else {
            if (hd.isEmpty) {hd = hdFunc(); if (hd.isEmpty) {terminal = true;throw new NoSuchElementException("Stream is empty")} }
            val ret = hd.get
            hd = None
            ret
          }
        }
        def hasNext: Boolean = {
          if (hd.isEmpty) {
            if (terminal) false
            else {
              hd = hdFunc()
              if (hd.isEmpty) {
                terminal = true
                false
              } else
                true
            }
          }
          else true
        }
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
