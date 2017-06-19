package leo

import scala.annotation.tailrec


/**
  * Package object for datastructure-related traits, functions and stuff.
  * @since 01/06/15
  */
package object datastructures {
  /////////////////////////////////
  // More or less general traits used throughout the project
  /////////////////////////////////

  /** Supplement trait for custom toString methods. */
  trait Pretty {
    def pretty: String
  }
  /** Another supplement trait for custom toString methods for signature-dependent
    * objects (e.g. terms, types). */
  trait Prettier {
    /** Pretty representation of the underlying object as string. */
    def pretty(sig: Signature): String
  }

  /////////////////////////////////
  // Weighting of literals/clauses
  /////////////////////////////////
  /**
    * Interface for weighting objects such as clauses or literals.
    * A smaller weight means that the object should have "more priority" depending
    * on the current context.
    * Every weight defines an ordering by `x <= y :<=> x.weight <= y.weight`,
    * this can be obtained by using the `SimpleOrdering`.
    *
    * @author Alexander Steen
    * @since 25.11.2014
    */
  trait Weight[What] {
    def weightOf[A <: What](w: A): Int
  }

  object ClauseProxyWeights {
    import impl.orderings._
    /** Weighting that gives a higher ('worse') weight for newer clauses. */
    final val fifo: ClauseProxyWeight = CPW_FIFO
    /** Clause weighting that assigns the number of literals in the clause as weight. */
    final val litCount: ClauseProxyWeight = CPW_LitCount
    /** Clause weighting that assigns the maximum of all literals weights as weight. */
    final val maxLitWeight: ClauseProxyWeight = CPW_MaxLitWeight
    /** Clause weighting that assigns the sum of all literals weights as weight. */
    final val litWeightSum: ClauseProxyWeight = CPW_LitWeightSum
  }

  object LiteralWeights {
    import impl.orderings._
    /** Simple weighting function that gives every literal the same weight. */
    final val const: LiteralWeight = LW_Constant
    /** Literal weighting that gives preference (i.e. gives lower weight) to older literals. */
    final val fifo: LiteralWeight = LW_FIFO
    /** Literal weighting that uses the enclosed term's size as weight. */
    final val termsize: LiteralWeight = LW_TermSize
  }

  /////////////////////////////////
  // Ordering related library functions
  /////////////////////////////////
  type CMP_Result = Byte
  /** Comparison result: Equal */
  final val CMP_EQ: CMP_Result = 0.toByte
  /** Comparison result: Less-than */
  final val CMP_LT: CMP_Result = 1.toByte
  /** Comparison result: Greater-than */
  final val CMP_GT: CMP_Result = 2.toByte
  /** Comparison result: Not-comparable (unknown) */
  final val CMP_NC: CMP_Result = 3.toByte

  /**
    * Collection of Ordering relations of terms, clauses, etc.
    *
    * @author Alexander Steen
    * @since 20.08.14
    */
  object Orderings {
    @inline final def isComparable(x: CMP_Result): Boolean = (x & ~CMP_EQ) != 0
    @inline final def isGE(x: CMP_Result): Boolean = (x & (CMP_EQ | CMP_GT)) != 0
    @inline final def isLE(x: CMP_Result): Boolean = (x & (CMP_EQ | CMP_LT)) != 0
    @inline final def invCMPRes(x: CMP_Result): CMP_Result = {
      if (x == CMP_GT) CMP_LT
      else if (x == CMP_LT) CMP_GT
      else x
    }
    final def intToCMPRes(x: Int, y: Int): CMP_Result = {
      if (x > y) CMP_GT
      else if (x < y) CMP_LT
      else CMP_EQ
    }

    /** Return a (simple) ordering that is induced by a weighting. */
    def simple[A](weighting: Weight[A]) = new Ordering[A] {
      def compare(a: A, b: A) = weighting.weightOf(a) - weighting.weightOf(b)
    }

    val intOrd = new Ordering[Int] {
      def compare(a: Int, b: Int) = a-b
    }

    def lift[A](f: A => A => Int): Ordering[A] = new Ordering[A] {
      def compare(x: A, y: A) = f(x)(y)
    }

    //  def lexOrd[A](ord: QuasiOrdering[A]): QuasiOrdering[Seq[A]] = new QuasiOrdering[Seq[A]] {
    //    def compare(x: Seq[A], y: Seq[A]) = (x.length - y.length) match {
    //      case 0 => (x,y) match {
    //        case (Seq(), Seq()) => Some(0)
    //        case (Seq(xHead, xTail@_*), Seq(yHead, yTail@_*)) => ord.compare(xHead, yHead) match {
    //          case Some(0) => compare(xTail, yTail)
    //          case res => res
    //        }
    //      }
    //      case res => Some(res)
    //    }
    //  }

    final def mult[A](gt: (A,A) => Boolean): Seq[A] => Seq[A] => Boolean = {
      s => t => {
        if (s.nonEmpty && t.isEmpty) true
        else if (s.nonEmpty && t.nonEmpty) {
          val sameElements = s.intersect(t)
          val remSameS = s.diff(sameElements)
          val remSameT = t.diff(sameElements)
          if (remSameS.isEmpty && remSameT.isEmpty) false
          else mult0(gt, remSameS, remSameT)
        } else false
      }
    }

    @tailrec
    final private def mult0[A](gt: (A,A) => Boolean, s: Seq[A], t: Seq[A]): Boolean = {
      if (t.isEmpty) true
      else if (s.nonEmpty && t.nonEmpty) {
        val sn = s.head
        val tIt = t.iterator
        var keepT: Seq[A] = Seq()
        while (tIt.hasNext) {
          val tn = tIt.next()
          if (!gt(sn,tn)) {
            keepT = keepT :+ tn
          }
        }
        mult0(gt, s.tail,keepT)
      } else false
    }

    final def lexCombination[A](org1: Ordering[A], ord2: Ordering[A], ords: Ordering[A]*): Ordering[A] = {
      new Ordering[A] {
        final def compare(x: A, y: A): Int = {
          val cmp1Result = org1.compare(x,y)
          if (cmp1Result != 0) cmp1Result
          else {
            val cmp2Result = ord2.compare(x,y)
            if (cmp2Result != 0) cmp2Result
            else {
              val ordsIt = ords.iterator
              while(ordsIt.hasNext) {
                val ord = ordsIt.next()
                val ordResult = ord.compare(x,y)
                if (ordResult != 0) return ordResult
              }
              0 // Equal in all orderings
            }
          }
        }
      }
    }
  }


  /////////////////////
  // Precedences
  /////////////////////

  trait Precedence {
    type Const = Signature.Key
    def compare(x: Const, y: Const)(implicit sig: Signature): CMP_Result
    def gt(x: Const, y: Const)(implicit sig: Signature): Boolean = compare(x,y)(sig) == CMP_GT
    def ge(x: Const, y: Const)(implicit sig: Signature): Boolean = compare(x,y)(sig) == CMP_GT || compare(x,y)(sig) == CMP_EQ
    def lt(x: Const, y: Const)(implicit sig: Signature): Boolean = compare(x,y)(sig) == CMP_LT
    def le(x: Const, y: Const)(implicit sig: Signature): Boolean = compare(x,y)(sig) == CMP_LT || compare(x,y)(sig) == CMP_EQ

    protected final def intToCMPRes(x: Int, y: Int): CMP_Result = {
      if (x > y) CMP_GT
      else if (x < y) CMP_LT
      else CMP_EQ
    }
  }

  object Precedence {
    import leo.datastructures.impl.orderings._

    final val sigInduced: Precedence = Prec_SigInduced
    final val arity: Precedence = Prec_Arity
    final val arity_UnaryFirst: Precedence = Prec_Arity_UnaryFirst
    final val arityOrder: Precedence = Prec_ArityOrder
    final val arityInvOrder: Precedence = Prec_ArityInvOrder
    final val arityOrder_UnaryFirst: Precedence = Prec_ArityOrder_UnaryFirst
    final val arityInvOrder_UnaryFirst: Precedence = Prec_ArityInvOrder_UnaryFirst
  }

  /////////////////////
  // Clause proxy orderings for clause selection
  /////////////////////
  object ClauseProxyOrderings {
    import impl.orderings._

    // Since the orderings are used in a scala priorityqueue which prefers greater elemens, we need to reverse the
    // element orderings in the compounds
    final val oldest_first: ClauseProxyOrdering = CPO_OldestFirst.reverse
    final def goals_SymbWeight(varWeight: Int, symbWeight: Int): ClauseProxyOrdering =
      Orderings.lexCombination(
        CPO_GoalsFirst2.reverse,
        new CPO_SymbolWeight(varWeight, symbWeight).reverse,
        oldest_first)
    final def goals_litCount_SymbWeight(varWeight: Int, symbWeight: Int): ClauseProxyOrdering =
      Orderings.lexCombination(
        CPO_GoalsFirst2.reverse,
        CPO_SmallerFirst.reverse,
        new CPO_SymbolWeight(varWeight, symbWeight).reverse,
        oldest_first)
    final def nongoals_litCount_SymbWeight(varWeight: Int, symbWeight: Int): ClauseProxyOrdering =
      Orderings.lexCombination(
        CPO_NonGoalsFirst2.reverse,
        CPO_SmallerFirst.reverse,
        new CPO_SymbolWeight(varWeight, symbWeight).reverse,
        oldest_first)
    final def conjRelSymb(conjSymbols: Set[Signature.Key],
                                   conjSymbolFactor: Float,
                                   varWeight: Int, symbWeight: Int): ClauseProxyOrdering =
      Orderings.lexCombination(
        new CPO_ConjRelativeSymbolWeight(conjSymbols, conjSymbolFactor, varWeight, symbWeight).reverse,
        oldest_first)
    final def litCount_conjRelSymb(conjSymbols: Set[Signature.Key],
                              conjSymbolFactor: Float,
                              varWeight: Int, symbWeight: Int): ClauseProxyOrdering =
      Orderings.lexCombination(
        CPO_SmallerFirst.reverse,
        new CPO_ConjRelativeSymbolWeight(conjSymbols, conjSymbolFactor, varWeight, symbWeight).reverse,
        oldest_first)
    final def sos_conjRelSymb(conjSymbols: Set[Signature.Key],
                              conjSymbolFactor: Float,
                              varWeight: Int, symbWeight: Int): ClauseProxyOrdering =
      Orderings.lexCombination(
        CPO_SOSFirst.reverse,
        new CPO_ConjRelativeSymbolWeight(conjSymbols, conjSymbolFactor, varWeight, symbWeight).reverse,
        oldest_first)
  }

  ///////////////////////
  /// Type Orderings
  ///////////////////////
  // none



  /////////////////////////////////
  // Further data structures/traits
  /////////////////////////////////
  /**
    * Configuration (i.e. state) for NDStream.
    *
    * @author Tomer Libal <shaolintl@gmail.com>
    * @since 15/04/2015
    */
  trait SearchConfiguration[S] {
    def result: Option[S]
    def isTerminal: Boolean // terminal nodes are not added to the configuration queue
  }

  /**
    * Mutable, non deterministic, stream
    *
    * @author Tomer Libal <shaolintl@gmail.com>
    * @since 15/04/2015
    */
  abstract class NDStream[S /*result type*/ ](val initial: SearchConfiguration[S], val myFun: SearchConfiguration[S] => Iterable[SearchConfiguration[S]] ) extends Iterable[S] with SearchAlgorithm {
    import scala.collection.mutable

    type T = SearchConfiguration[S]
    private val results: mutable.Queue[S] = new mutable.Queue[S]()
    protected var hd: Option[S] = None
    protected var terminal: Boolean = false
    protected def initDS(): Unit = {
      add(initial)
      hd = nextVal
    }

    @tailrec
    protected final def nextVal: Option[S] = {
      val res = results.headOption
      if (res.isDefined) {
        results.dequeue
        res
      } else {
        val conf = get
        if (conf.isEmpty) None
        else {
          val confs: Iterable[SearchConfiguration[S]] = { myFun( conf.get )}
          confs.foreach( x => {
            if (x.result.isDefined)
              results.enqueue( x.result.get )
            if (!x.isTerminal) {
              add(x)
            }
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
        def next: S = {
          if (hd.isEmpty && terminal) throw new NoSuchElementException("Stream is empty")
          else {
            if (hd.isEmpty) {hd = nextVal; if (hd.isEmpty) {terminal = true;throw new NoSuchElementException("Stream is empty")} }
            val ret = hd.get
            hd = None
            ret
          }
        }
        def hasNext: Boolean = {
          if (hd.isEmpty) {
            if (terminal) false
            else {
              hd = nextVal
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

  /**
    * Trait for search algorithms
    *
    * @author Tomer Libal <shaolintl@gmail.com>
    * @since 15/04/2015
    */
  trait SearchAlgorithm {
    type T
    protected def initDS(): Unit // called by the algorithm and implemented by some object using it as the object is initialized before the trait
    protected def add( t: T ): Unit
    protected def get: Option[T]
  }

  /**
    * Abstract breadth-first search implementation of a search algorithm.
    *
    * @author Tomer Libal <shaolintl@gmail.com>
    * @since 15/04/2015
    */
  trait BFSAlgorithm extends SearchAlgorithm {
    import collection.mutable

    private val ds: mutable.Queue[T] = new mutable.Queue[T]()
    initDS() // if the object requires the ds to be already existing, then it will not fail now
    protected def add( conf: T ): Unit = ds += conf
    protected def get: Option[T] = {
      val res = ds.headOption
      if (res.isDefined) ds.dequeue
      res
    }
  }


  /** Class for objects that have a congruence defined on them (that is probably different from equality). */
  trait HasCongruence[A] {
    /** Returns `true` iff `this` is congruent to `that`. */
    def cong(that: A): Boolean
  }

  /**
    * A datatype for time instants that are totally comparable.
    *
    * @since 19.05.2015
    */
  sealed trait TimeStamp extends Comparable[TimeStamp] with Pretty

  object TimeStamp extends Function0[TimeStamp] {
    import java.util.concurrent.atomic.AtomicLong

    /**
      * Gives the current `TimeStamp`, i.e. a timestamp that is stricly smaller
      * than any timestamp retrieved before this call.
      * @return Current `TimeStamp`
      */
    def apply(): TimeStamp = TimeStampImpl(timeStampCounter.incrementAndGet())


    /// Local implementation
    private val timeStampCounter : AtomicLong = new AtomicLong(0)

    private case class TimeStampImpl(time: Long) extends TimeStamp {
      def compareTo(o: TimeStamp) = o match {
        case TimeStampImpl(oTime) => time.compareTo(oTime)
      }

      def pretty = s"$time"
    }
  }

  trait ZippingSeqIterator[A] {
    def hasNext: Boolean
    def hasPrev: Boolean
    def next(): A
    def prev(): A
    def leftOf: Seq[A]
    def rightOf: Seq[A]
  }
  class SeqZippingSeqIterator[A](seq: Seq[A]) extends ZippingSeqIterator[A] {
    var l: Seq[A] = Seq()
    var r: Seq[A] = seq
    var cur: A = null.asInstanceOf[A]

    def hasPrev = l.nonEmpty
    def hasNext = r.nonEmpty
    def next() = {
      if (!hasNext) throw new NoSuchElementException("next on empty right side of zipper")
      val elem = r.head
      if (cur != null) {
        l = cur +: l
      }
      r = r.tail
      cur = elem
      elem
    }
    def prev() = {
      if (!hasPrev) throw new NoSuchElementException("prev on empty left side of zipper")
      val elem = l.head
      r = cur +: r
      l = l.tail
      cur = elem
      elem
    }
    def leftOf = l.reverse
    def rightOf = r
  }


  ///////////////////////////////
  // Utility functions
  ///////////////////////////////
  @inline final def isPropSet(prop: Int, in: Int): Boolean = (prop & in) == prop
  @inline final def addProp(prop: Int, in: Int): Int = prop | in
  @inline final def deleteProp(prop: Int, in: Int): Int = ~prop & in

  final def fuseMaps[A,B](map1: Map[A,Set[B]], map2: Map[A,Set[B]]): Map[A, Set[B]] = {
    map2.foldLeft(map1)({case (intermediateMap, (k,v)) =>
      if (!intermediateMap.contains(k))
        intermediateMap + (k -> v)
      else
        intermediateMap + (k -> (intermediateMap(k) ++ v))
    })
  }
  final def mergeMapsBy[A, B](map1: Map[A, B], map2: Map[A, B], f: (B,B) => B)(defaultValue: B): Map[A, B] = {
    var resultMap = map2
    val map1It = map1.iterator
    while (map1It.hasNext) {
      val (entry, value) = map1It.next()
      resultMap = resultMap + (entry -> f(resultMap.getOrElse(entry,defaultValue), value))
    }
    resultMap
  }
  @inline final def addMaps[A](map1: Map[A, Int], map2: Map[A, Int]): Map[A, Int] = mergeMapsBy(map1,map2,(a:Int,b:Int) => a+b)(0)

  // Further utility functions
  /** Given a sequence of terms `terms = (t_i)_i` return the nested disjunction
    * {{{t_1 ∨ (t_2 ∨ ( ... ∨ t_n) ... ) )}}}
    * `LitFalse` is (t_i)_i is empty. */
  final def mkDisjunction(terms: Seq[Term]): Term = {
    import leo.modules.HOLSignature.{LitFalse, |||}
    terms match {
      case Seq() => LitFalse()
      case Seq(t, ts@_*) => ts.foldLeft(t)({case (disj, t2) => |||(disj, t2)})
    }
  }
  /** Given a sequence of terms `terms = (t_i)_i` return the nested conjunction
    * {{{t_1 ∧ (t_2 ∧ ( ... ∧ t_n) ... ) )}}}
    * `LitTrue` is (t_i)_i is empty. */
  final def mkConjunction(terms: Seq[Term]): Term = {
    import leo.modules.HOLSignature.{LitTrue, &}
    terms match {
      case Seq() => LitTrue()
      case Seq(t, ts@_*) => ts.foldLeft(t)({case (disj, t2) => &(disj, t2)})
    }
  }
  /** Given a sequence `bindings = (ty_i)_i` of types and a term `term`, return the nested universal quantification
    * {{{∀ty_1.∀ty_2....∀ty_n. term}}} */
  final def mkPolyUnivQuant(bindings: Seq[Type], term: Term): Term = {
    import leo.datastructures.Term.λ
    import leo.modules.HOLSignature.Forall
    bindings.foldRight(term)((ty,t) => Forall(λ(ty)(t)))
  }
  /** Given a sequence `bindings = (ty_i)_i` of types and a term `term`, return the nested existential quantification
    * {{{∃ty_1.∃ty_2....∃ty_n. term}}} */
  final def mkPolyExistQuant(bindings: Seq[Type], term: Term): Term = {
    import leo.datastructures.Term.λ
    import leo.modules.HOLSignature.Exists
    bindings.foldRight(term)((ty,t) => Exists(λ(ty)(t)))
  }

  final def mkPolyUnivType(count: Int, body: Type): Type = if (count <= 0) body
  else Type.∀(mkPolyUnivType(count-1, body))

  /** For a term `t` return `(t', (ty_i)_i)` where `t'` is the subterm of `t`
    * that is acquired by stripping all prefix-lambdas (term abstraction) and
    * the (ty_i)_i are the respective types that lambdas abstract over. E.g.
    * {{{collectLambdas(λi.λo. t') = (t', (i,o))}}} */
  final def collectLambdas(t: Term): (Term, Seq[Type]) = collectLambdas0(t, Seq())
  @tailrec
  private final def collectLambdas0(t: Term, abstractions: Seq[Type]): (Term, Seq[Type]) = {
    import leo.datastructures.Term.:::>
    t match {
      case ty :::> body => collectLambdas0(body, ty +: abstractions)
      case _ => (t, abstractions.reverse)
    }
  }

  /** if `term` is eta-equivalent to a (free or bound) variable, return true.
    * False otherwise.
    * @param depth Optionally assume that `term` had originally a prefix of `depth` lambdas. */
  final def isVariableModuloEta(term: Term, depth: Int = 0): Boolean =
    getVariableModuloEta(term, depth) > 0
  /** if `term` is eta-equivalent to a (free or bound) variable, return the index,
    * else returns a value <= 0. Note that the return value is adjusted (decremented)
    * by the number of leading lambdas in `term`.
    * @param depth Optionally assume that `term` had originally a prefix of `depth` lambdas. */
  final def getVariableModuloEta(term: Term, depth: Int = 0): Int =
    getVariableModuloEta0(term, depth)

  @tailrec
  private final def getVariableModuloEta0(term: Term, extraAbstractions: Int): Int = {
    import leo.datastructures.Term.{Bound, TermApp,:::>}
    term match {
      case _ :::> body => getVariableModuloEta0(body, extraAbstractions+1)
      case TermApp(Bound(_, idx), args) if idx > extraAbstractions =>
        /* Head is bound outside of original `arg`*/
        if (extraAbstractions == args.size && etaArgs(args)) {
          idx-extraAbstractions
        } else -1
      case _ => -1
    }
  }

  /** Returns true iff args is the sequence of arguments (n) (n-1) ... (1) or eta-equivalent */
  @tailrec
  final private def etaArgs(args: Seq[Term]): Boolean = {
    import leo.datastructures.Term.Bound
    if (args.isEmpty) true
    else {
      val hd = args.head
      hd match {
        case Bound(_, idx) if idx == args.size => etaArgs(args.tail)
        case _ => val possiblyBoundVar = getVariableModuloEta(hd)
          if (possiblyBoundVar == args.size) etaArgs(args.tail)
          else false
      }
    }
  }

  final def partitionArgs(args: Seq[Either[Term, Type]]): (Seq[Type], Seq[Term]) = partitionArgs0(Vector(), args)
  @tailrec private final def partitionArgs0(acc: Seq[Type], args: Seq[Either[Term, Type]]): (Seq[Type], Seq[Term]) = {
    if (args.isEmpty) (acc, Seq.empty)
    else {
      val hd = args.head
      if (hd.isLeft) (acc, args.map(_.left.get))
      else partitionArgs0(acc :+ hd.right.get, args.tail)
    }
  }

  final def typeArgs(args: Seq[Either[Term, Type]]): Seq[Type] = typeArgs0(Vector(), args)
  @tailrec private final def typeArgs0(acc: Seq[Type], args: Seq[Either[Term, Type]]): Seq[Type] = {
    if (args.isEmpty) acc
    else {
      val hd = args.head
      if (hd.isLeft) acc
      else typeArgs0(acc :+ hd.right.get, args.tail)
    }
  }

  @tailrec final def termArgs(args: Seq[Either[Term, Type]]): Seq[Term] = {
    if (args.isEmpty) Seq.empty
    else {
      val hd = args.head
      if (hd.isLeft) args.map(_.left.get)
      else termArgs(args.tail)
    }
  }
}