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
  def unify(vargen: FreshVarGen, t : Term, s : Term) : Iterable[Subst]

  val name = "unification"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)
}

/**
 * Tests solely for equality
 */
object IdComparison extends Unification{
  override def unify(vargen: FreshVarGen, t : Term, s : Term) : Iterable[Subst] = if (s == t) Stream(Subst.id) else Stream.empty
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

  def unify (vargen: FreshVarGen, t1 : Term, s1 : Term) : Iterable[Subst] = {

    val t = t1.etaExpand
    val s = s1.etaExpand

    // returns a stream whose head is a pre-unifier and whose body computes the next unifiers
    new NDStream[Subst](new MyConfiguration(List(Tuple2(t,s)), List()), new MyFun(vargen)) with BFSAlgorithm
  }

  protected def isFlexible(t: Term): Boolean = t.headSymbol match {
    case Bound(_, _) => true // flexible variable
    case _ => false // function symbol (or bound variable <- really, does that exist? I think every bound variable will be
    // instantiated with a skolem term, or is it? )
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
    // Alex: Added check on empty sproblems list. That is correct, is it?
    if (sproblems.isEmpty) Subst.id
    else {
      val maxIdx: Int = Bound.unapply(sproblems.maxBy(e => Bound.unapply(e._1).get._2)._1).get._2
      var sub = Subst.shift(maxIdx)
      for (i <- 1 to maxIdx)
        sproblems.find(e => Bound.unapply(e._1).get._2 == maxIdx - i + 1) match {
          case Some((_,t)) => sub = sub.cons(TermFront(t))
          case _ => sub = sub.cons(BoundFront(maxIdx - i + 1))
        }
      sub
    }
  }

  // bug one: see output - a sub with a term at index 15 replaces a bound variable at index 16 and not 15
  // bug two: negative fresh variables cause some term functions to throw index out of bound exception, to get it, simply change back
  // the fresh variables counter to negative. I changed it into positive to see if there are other bugs
  private def applySubstToList(s: Subst, l: Seq[UEq]): Seq[UEq] =
    l.map(e => (e._1.closure(s).betaNormalize,e._2.closure(s).betaNormalize))

  // apply exaustively delete, comp and bind on the set and sort it at the end
  @tailrec
  protected def detExhaust(vargen: FreshVarGen, uproblems: Seq[UEq], sproblems: Seq[UEq]): Tuple2[Seq[UEq], Seq[UEq]]  = {
    leo.Out.trace(s"Unsolved: ${uproblems.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")

    // apply delete
    val ind1 = uproblems.indexWhere(DeleteRule.canApply)
    if (ind1 > -1) {
    leo.Out.finest("Apply Delete")
    detExhaust(vargen, uproblems.take(ind1) ++ uproblems.drop(ind1 + 1), sproblems)
    // apply decomp
    } else {
      val ind2 = uproblems.indexWhere(DecompRule.canApply)
      if (ind2 > -1) {
        leo.Out.finest("Apply Decomp")
        detExhaust(vargen, (DecompRule(vargen, uproblems(ind2)) ++ uproblems.take(ind2) ++ uproblems.drop(ind2 + 1)).sortWith(sort), sproblems)
        // apply bind
      } else {
        val ind3 = uproblems.indexWhere(BindRule.canApply)
        if (ind3 > -1) {
          leo.Out.finest("Apply Bind")
          val be = BindRule(vargen, uproblems(ind3))
          val sb = computeSubst(List(be))
          detExhaust(vargen, applySubstToList(sb, uproblems.take(ind3) ++ uproblems.drop(ind3 + 1)), applySubstToList(sb, sproblems) :+ be)
        } else {
          // apply Func /* by Alex */
          val ind4 = uproblems.indexWhere(FuncRule.canApply)
          if (ind4 > -1) {
            leo.Out.finest(s"Can apply func on: ${uproblems(ind4)._1.pretty} == ${uproblems(ind4)._2.pretty}")
            detExhaust(vargen, (uproblems.take(ind4) :+ FuncRule(vargen, uproblems(ind4))) ++ uproblems.drop(ind4 + 1), sproblems)}
          else {
            // none is applicable, do nothing
            (uproblems, sproblems)
          }
        }

      }
    }

  }

  /**
   *  all terms are flex variables
   */
  private def computeDefaultSub(vargen: FreshVarGen, ls: Seq[Term]): Subst = {
    val it = ls.iterator
    var map : Map[Int, Term] = Map()
    while (it.hasNext) {
      val flex = it.next()
      val (ty, id) = Bound.unapply(flex).get
      val tys = ty.funParamTypesWithResultType
      val newVar = vargen.next(tys.last)
      map = map + (id -> λ(tys.init)(Term.mkBound(tys.last, newVar._1+tys.init.size)))
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

  /* New Version by Alex. Maps all variables of same type to same new var. */
  private def computeDefaultSub2(vargen: FreshVarGen, ls: Seq[(Term, Term)]): Subst = {
    val lsIt = ls.iterator
    var tyToVarMap: Map[Type, Term] = Map()
    var substMap: Map[Int, Term] = Map()
    while (lsIt.hasNext) {
      val (l, r) = lsIt.next()
      val (lHead, rHead) = (Bound.unapply(l.headSymbol).get, Bound.unapply(r.headSymbol).get)
      if (tyToVarMap.contains(lHead._1)) {
        val t = tyToVarMap(lHead._1)
        substMap = substMap + (lHead._2 -> t) + (rHead._2 -> t)
      } else {
        val ty = lHead._1.funParamTypesWithResultType
        val newVar = vargen.next(ty.last)
        val t = λ(ty.init)(Term.mkBound(newVar._2, newVar._1+ty.init.size))
        tyToVarMap = tyToVarMap + (lHead._1 -> t)
        substMap = substMap + (lHead._2 -> t) + (rHead._2 -> t)
      }
    }

    Subst.fromMap(substMap)
  }

  /*// n is arity of variable
  // m is arity of head
  // hdSymb is head
  // y1,..,yn are new bound variable
  // x1,..,xm are new free variables
  protected[modules] def partialBinding(typ: Type, hdSymb: Term) = {
    val ys = typ.funParamTypes.zip(List.range(1,typ.arity+1)).map(p => Term.mkBound(p._1,p._2))
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
  }*/

  // Huets rules
  trait HuetsRule[R] extends Function2[FreshVarGen,UEq, R] {
    // the functional apply applies the rule to an equation in order to produce other equations
    def canApply(e: UEq): Boolean // returns true if we can apply the rule
  }

  /* new rules by Alex */

  object FuncRule extends HuetsRule[UEq] {

    def apply(varGen: FreshVarGen, e: UEq): UEq = {
      leo.Out.trace(s"Func rule on ${e._1.pretty} = ${e._2.pretty}")
      val funArgTys = e._1.ty.funParamTypes
      val skTerms = funArgTys.map(leo.modules.calculus.skTerm(_, varGen.existingVars))
      (Term.mkTermApp(e._1, skTerms).betaNormalize, Term.mkTermApp(e._2, skTerms).betaNormalize)
    }

    def canApply(e: UEq) = {
      // we can apply it if the sides of the equation have functional type
      assert(e._1.ty == e._2.ty, s"Func Rule: Both UEq sides have not-matching type:\n\t${e._1.pretty}\n\t${e._1.ty.pretty}\n\t${e._2.pretty}\n\t${e._2.ty.pretty}")
      e._1.ty.isFunType
    }
  }

  /* new rules end*/

  // not to forget that the approximations must be in eta-long-form
  /**
   * 4a
   * equation is not oriented
   */
  object ImitateRule extends HuetsRule[UEq] {

    def apply(vargen: FreshVarGen, e: UEq): UEq = {
      leo.Out.trace(s"Apply Imitate")
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      val res = (t.headSymbol,partialBinding(vargen, t.headSymbol.ty,  s.headSymbol))
      leo.Out.trace(s"Result of Imitate: ${res._1.pretty} = ${res._2.pretty}")
      res
    }
      // must make sure s doesnt have as head a bound variable
    def canApply(e: UEq) = {
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      s.headSymbol match {
        // cannot be flexible and fail on bound variable
        case Bound(_,_) => assert(false, "ImitateRule: Should not happen, right?");false // FIXME
        case _ => true
      }
    }
  }

  /**
   * 4b
   * equation is not oriented
    * TODO: Alex: I filtered out all of those bound vars that have non-compatible type. Is that correct?
   */
  object ProjectRule extends HuetsRule[Seq[UEq]] {
    def apply(vargen: FreshVarGen, e: UEq): Seq[UEq] = {
      leo.Out.trace(s"Apply Project")
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      val bvars = t.headSymbol.ty.funParamTypes.zip(List.range(1,t.headSymbol.ty.arity+1)).map(p => Term.mkBound(p._1,p._2)) // TODO
      leo.Out.finest(s"BVars in Projectrule: ${bvars.map(_.pretty).mkString(",")}")
      //Filter only those bound vars that are itself types with result type == type of general binding
      val funBVars = bvars.filter(_.ty.funParamTypesWithResultType.last == t.headSymbol.ty)
      leo.Out.finest(s"Function type BVars in Projectrule: ${funBVars.map(_.pretty).mkString(",")}")
      val res = funBVars.map(e => (t.headSymbol,partialBinding(vargen, t.headSymbol.ty, e)))

      leo.Out.trace(s"Result of Project:\n\t${res.map(eq => eq._1.pretty ++ " = " ++ eq._2.pretty).mkString("\n\t")}")

      res
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
    def apply(vargen: FreshVarGen, e: UEq) = {
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
        val (_,x) = Bound.unapply(t.headSymbol).get
      // check t is eta equal to x
        if (!t.headSymbol.etaExpand.equals(t) && !t.equals(t.headSymbol)) false
      // check it doesnt occur in s
        else !s.looseBounds.contains(x)
      }
    }
  }

  /**
   * 1
   * returns true if the equation can be deleted
   */
  object DeleteRule extends HuetsRule[Unit] {
    def apply(vargen: FreshVarGen, e: UEq) = ()
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
    def apply(vargen: FreshVarGen, e: UEq) = e match {
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
  protected class MyFun(vargen: FreshVarGen) extends Function1[Configuration[Subst], Seq[Configuration[Subst]]] {

    import  scala.collection.mutable.ListBuffer

    // Huets procedure is defined here
    def apply(conf2: Configuration[Subst]): Seq[Configuration[Subst]] = {
      val conf = conf2.asInstanceOf[MyConfiguration]
      // we always assume conf.uproblems is sorted and that delete, decomp and bind were applied exaustively
      val (uproblems, sproblems) = detExhaust(vargen, conf.uproblems,conf.sproblems)
      leo.Out.trace(s"Finished detExhaust")
      // if uproblems is empty, then succeeds
      if (uproblems.isEmpty) {
        List(new MyConfiguration(Some(computeSubst(sproblems))))
      }
      // else consider top equation
      else {
        val (t,s) = uproblems.head
        leo.Out.finest(s"selected: ${t.pretty} = ${s.pretty}")
        // if it is rigid-rigid -> fail
        if (!isFlexible(t) && !isFlexible(s)) List()
        else {
          // if it is flex-flex -> all equations are flex-flex -> succeeds and compute sub from the solved set
          // TOFIX compute a substitution for all types that maps all variables in the uproblems set to the same term
          // and then compose this subtitution to the one generated by computeSubst
          if (isFlexible(t) && isFlexible(s)) {
            leo.Out.finest(s"Flex-flex")
            leo.Out.finest(s"${uproblems.map{case (l,r) => l.pretty ++ "=" ++ r.pretty}.mkString("\t")}")
//            val defSub = computeDefaultSub(vargen, uproblems.foldLeft(List[Term]())((ls,e) => e._1.headSymbol::e._2.headSymbol::ls))
            /* the one above is from tomer, the one below by alex: I tried to implement the toFix annotation above */
            // FIXME Is that right? I think we lose completeness here
            val defSub = computeDefaultSub2(vargen, uproblems)
//            println(s"default Sub: ${defSub.pretty}")
//            println(s"with other: ${defSub.comp(computeSubst(sproblems)).normalize.pretty}")
            List(new MyConfiguration(Some(defSub.comp(computeSubst(sproblems)))))
          } else {
            leo.Out.finest(s"flex-rigid")
            // else we have a flex-rigid and we cannot apply bind

            val lb = new ListBuffer[MyConfiguration]
            // compute the imitate partial binding and add the new configuration
            if (ImitateRule.canApply(t,s)) lb.append(new MyConfiguration(ImitateRule(vargen, (t,s))+:uproblems, sproblems))

            // compute all the project partial bindings and add them to the return list
            ProjectRule(vargen, (t,s)).foreach (e => lb.append(new MyConfiguration(e+:uproblems, sproblems)))

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
