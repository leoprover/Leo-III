package leo.modules.agent.rules

import leo.datastructures.ClauseProxy

/**
  *
  * Agent to execute the [[Rule]] interface.
  * Used to instanciate ground inference rules
  * and composed rules.
  *
  * @author Max Wisniewski
  * @since 10/25/16
  */
trait RuleAgent {

}

trait Rule {

//  /**
//    * Checks the rule for applicability.
//    * The data given to the rule is directly incorperated
//    * into consideration. Possible missing formulas are
//    * retrieved from the blackboard.
//    *
//    * Returns a true, if it is applicable and
//    * a hint. Otherwise false.
//    *
//    * @param data Data pluged
//    * @return
//    */
//  @inline def canApply(data: Data*) : Option[Hint] = canApply(data)

  /**
    * Checks the rule for applicability.
    * Returns a true, if it is applicable and
    * a hint. Otherwise false.
    *
    * @param data Data directly as input (not queued through the blackboard)
    * @return Some(hint) if the rule is applicable, None otherwise
    */
  def canApply(data : Seq[Data]) : Option[Hint]

  /**
    * Applies the rule right away
    *
    * @param data - The data directly passed into the rule
    * @return sequence of new data, sequence of deleted data
    */
  def apply(data : Seq[Data]) : (Seq[Data], Seq[Data]) = canApply(data) match {
    case Some(hint) => hint.apply()
    case None => (Seq(), Seq())
  }

//  /**
//    * Applies the rule right away
//    *
//    * @param data - The data directly passed into the rule
//    * @return sequence of new data, sequence of deleted data
//    */
//  @inline def apply(data : Data*) : (Seq[Data], Seq[Data]) = apply(data)
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
  def apply() : (Seq[Data], Seq[Data])
}

/**
  *
  * An additional constraint added to a Rule
  *
  * @param cond a boolean applied to the data of the rule itself
  * @param a
  */
class IfRule(cond : Seq[Data] => Boolean, a : Rule) extends Rule {
  /**
    * Additional filter on the the given input.
    *
    * @param data
    * @return
    */
  override def canApply(data: Seq[Data]): Option[Hint] = if(cond(data)) a.canApply(data) else None
}

/**
  *
  * Branching depending on the directly passed data
  *
  * @param cond Condition on the data
  * @param a Rule applied if `cond` is true
  * @param b Rule applied if `cond` is false
  */
class IfElseRule(cond : Seq[Data] => Boolean, a : Rule, b : Rule) extends Rule {
  /**
    * Additional filter on the the given input.
    *
    * @param data Data from the blackboard scanned
    * @return
    */
  override def canApply(data: Seq[Data]): Option[Hint] = if(cond(data)) a.canApply(data) else b.canApply(data)
}

/**
  *
  * Performs a [[Rule]] `a` as long as `cond` is evaluated
  * as true
  *
  * @param cond The loop condition
  * @param a The rule to apply in each loop iteration
  */
class WhileRule(cond : Seq[Data] => Boolean, a : Rule) extends Rule {
  override def canApply(data: Seq[Data]): Option[Hint] = if(cond(data)) a.canApply(data).map(x => WhileHint(x)) else None

  case class WhileHint(h : Hint) extends Hint {
    override def apply(): (Seq[Data], Seq[Data]) = {
      var (res, del) = h.apply()

//      while()

      (res, del)
    }
  }
}

/**
  *
  * This composed Rule will go through the first rule `a`
  * normally and applies the second rule to the result.
  *
  * Performs `b` on the complete result of `a`
  *
  * @param a The first rule to apply
  * @param b The second rule to apply
  */
class ComposedRule(a : Rule, b : Rule) extends Rule {
  override def canApply(data: Seq[Data]): Option[Hint] = a.canApply(data).map(x => ComposedHint(x))

  case class ComposedHint(h : Hint) extends Hint {
    override def apply(): (Seq[Data], Seq[Data]) = {
      val (res, del) = h.apply()
      val (res2, del2) = b.apply(res)
      (res.filterNot(del2.contains(_)) ++ res2, del ++ del2)
    }
  }
}

/**
  *
  * Performs `b` on every Result of `a`
  *
  * @param a The first rule
  * @param b The second rule
  */
class Split(a : Rule, b : Rule) extends Rule {
  override def canApply(data: Seq[Data]): Option[Hint] = a.canApply(data).map(x => SplitHint(x))

  case class SplitHint(h : Hint) extends Hint {
    override def apply(): (Seq[Data], Seq[Data]) = {
      val (res, del) = h.apply()
      var res2 : Seq[Data] = Seq()
      var del2 :Seq[Data] = del
      val resIt = res.iterator
      while(resIt.hasNext){
        val n = resIt.next
        val (r,d) = b.apply(Seq(n))
        if(!d.contains(n)) res2 :+= n
        res2 ++= r
        del2 ++= d
      }
      (res2,del2)
    }
  }
}

/**
  * Maybe adorned data from the blackboard.
  */
trait Data {
}
