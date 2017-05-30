package leo.modules.agent.rules

import leo.datastructures.blackboard.{DataStore, DataType, Delta, ImmutableDelta}


trait Rule {
  def name : String

  /**
    * Defines the ordered Datatypes as input for
    * this rule.
    *
    * @return
    */
  def inTypes : Seq[DataType[Any]]

  /**
    *
    * This flag indicates, that the data is moved
    * from one set to another, irregardless of a change.
    *
    * @return true, if the original clause should be moved
    */
  def moving : Boolean

  /**
    * Defines the type of results for the application
    * of the rule
    *
    * @return
    */
  def outTypes : Seq[DataType[Any]]

  /**
    * The list of data structures observed as input.
    * The datastructures should reflect the choice of [[inTypes]].
    * @return List of all search structures for the input
    */
  def observedDataStructures : Seq[DataStore] = Nil // Default no datastructure

  /**
    * Checks the rule for applicability.
    * Returns a true, if it is applicable and
    * a hint. Otherwise false.
    *
    * @param r Data directly as input (not queued through the blackboard)
    * @return Some(hint) if the rule is applicable, None otherwise
    */
  def canApply(r : Delta) : Seq[Hint]

  /**
    * Applies the rule right away
    *
    * @param r - The data directly passed into the rule
    * @return sequence of new data, sequence of deleted data
    */
  def apply(r : Delta) : Seq[Delta] = canApply(r) map (_.apply())
}


/**
  * THe class hint can be implemented by each rule.
  * This hint
  */
trait Hint {

  /**
    *
    * If the rule is applicable on `data`
    * the rule is applied with the help of `hint`.
    *
    * @return A sequence of generated ClauseProxies and deleted ClauseProxies
    */
  def apply() : Delta

  /**
    * Data only read by the hint
    */
  def read : Map[DataType[Any], Set[Any]]

  /**
    * Data written by the hint
    */
  def write : Map[DataType[Any], Set[Any]]
}

class ReleaseLockHint[A](dt : DataType[A], d : A) extends Hint {
  override final val apply: Delta = new ImmutableDelta(Map(LockType(dt) -> Seq(d)))
  override final val read: Map[DataType[Any], Set[Any]] = Map()
  override final val write: Map[DataType[Any], Set[Any]] = Map()
}

class MoveHint[A](d : A, from : DataType[A], to : DataType[A]) extends Hint {
  override final val apply: Delta = new ImmutableDelta(Map(to -> Seq(d)), Map(from -> Seq(d)))
  override final val read: Map[DataType[Any], Set[Any]] = Map()
  override final val write: Map[DataType[Any], Set[Any]] = Map(from -> Set(d))
}

class CopyHint[A](d : A, to : DataType[A]) extends Hint {
  override final val apply: Delta = new ImmutableDelta(Map(to -> Seq(d)))
  override final val read: Map[DataType[Any], Set[Any]] = Map()
  override final val write: Map[DataType[Any], Set[Any]] = Map()
}

class DeleteHint[A](d : A, from : DataType[A]) extends Hint {
  override final val apply: Delta = new ImmutableDelta(Map(), Map(from -> Seq(d)))
  override final val read: Map[DataType[Any], Set[Any]] = Map()
  override final val write: Map[DataType[Any], Set[Any]] = Map(from -> Set(d))
}

///**
//  *
//  * An additional constraint added to a Rule
//  *
//  * @param cond a boolean applied to the data of the rule itself
//  * @param a
//  */
//class IfRule(cond : Delta => Boolean, a : Rule) extends Rule {
//  /**
//    * Additional filter on the the given input.
//    *
//    * @param r
//    * @return
//    */
//  override def canApply(r : Delta): Seq[Hint] = if(cond(r)) a.canApply(r) else Seq()
//
//  override def name: String = s"if (cond) ${a.name}"
//}
//
///**
//  *
//  * Branching depending on the directly passed data
//  *
//  * @param cond Condition on the data
//  * @param a Rule applied if `cond` is true
//  * @param b Rule applied if `cond` is false
//  */
//class IfElseRule(cond : Delta => Boolean, a : Rule, b : Rule) extends Rule {
//  /**
//    * Additional filter on the the given input.
//    *
//    * @param r Data from the blackboard scanned
//    * @return
//    */
//  override def canApply(r: Delta): Seq[Hint] = if(cond(r)) a.canApply(r) else b.canApply(r)
//
//  override def name: String = s"if (cond) ${a.name} else ${b.name}"
//}
//
///**
//  *
//  * Performs a [[Rule]] `a` as long as `cond` is evaluated
//  * as true
//  *
//  * @param cond The loop condition
//  * @param a The rule to apply in each loop iteration
//  */
//class WhileRule(cond : Delta => Boolean, a : Rule) extends Rule {
//  override def canApply(r: Delta): Seq[Hint] = if(cond(r)) a.canApply(r).map(x => WhileHint(x)) else Seq()
//
//  case class WhileHint(h : Hint) extends Hint {
//    override def apply(): Delta = {
//      var r = h.apply()
////      while()
//
//      r
//    }
//    override val read: Map[DataType[Any], Set[Any]] = h.read
//    override val write: Map[DataType[Any], Set[Any]] = h.write
//  }
//
//  override def name: String = s"while (cond) ${a.name}"
//}
//
///**
//  *
//  * This composed Rule will go through the first rule `a`
//  * normally and applies the second rule to the result.
//  *
//  * Performs `b` on the complete result of `a`
//  *
//  * @param a The first rule to apply
//  * @param b The second rule to apply
//  */
//class ComposedRule(a : Rule, b : Rule) extends Rule {
//  override def canApply(r: Delta): Seq[Hint] = a.canApply(r).map(x => ComposedHint(x))
//
//  case class ComposedHint(h : Hint) extends Hint {
//    override def apply(): Delta = {
//      val r = h.apply()
//      val r2 = b.apply(r)
//      // TODO Merge!!!
//      r2.head
//    }
//    override def read: Map[DataType[Any], Set[Any]] = ???
//    override def write: Map[DataType[Any], Set[Any]] = ???
//  }
//
//  override def name: String = s"${a.name}; ${b.name}"
//}

///**
//  *
//  * Performs `b` on every Result of `a`
//  *
//  * @param a The first rule
//  * @param b The second rule
//  */
//class Split(a : Rule, b : Rule) extends Rule {
//  override def canApply(data: Seq[Data]): Option[Hint] = a.canApply(data).map(x => SplitHint(x))
//
//  case class SplitHint(h : Hint) extends Hint {
//    override def apply(): (Seq[Data], Seq[Data]) = {
//      val (res, del) = h.apply()
//      var res2 : Seq[Data] = Seq()
//      var del2 :Seq[Data] = del
//      val resIt = res.iterator
//      while(resIt.hasNext){
//        val n = resIt.next
//        val (r,d) = b.apply(Seq(n))
//        if(!d.contains(n)) res2 :+= n
//        res2 ++= r
//        del2 ++= d
//      }
//      (res2,del2)
//    }
//  }
//
//  override def name: String = s"(${a.name} foreach \x -> ${b.name}(x))"
//}
