package leo.modules.agent.rules

import leo.datastructures.blackboard.{Blackboard, DataStore, DataType, ImmutableDelta}

/**
  * Wrapper for an agent graph.
  *
  * Contains a list of all connected rules
  * and the corresponding data structures,
  * as well as the entry point for new data.
  *
  */
trait RuleGraph[In, Out] {
  /**
    * All rules
    * (edges) of the graph.
    *
    * @return rules of the graph
    */
  def rules : Iterable[Rule]

  /**
    * All datastructures
    * (vertices) of the graph
    *
    * @return datastructures of the graph
    */
  def dataStructures : Iterable[DataStore]

  /**
    *
    * The initial type data has to be inserted.
    *
    * @return initial type of data
    */
  def initType : DataType[In]

  /**
    * The datatype of the result.
    *
    * @return type of the data storing results
    */
  def outType : DataType[Out]

  /**
    * Contains all DataTypes used throughout the graph
    */
  lazy val containedTypes : Iterable[DataType[Any]] = {
    rules.flatMap{x => x.inTypes union x.outTypes}
  }

  /**
    * Registers the complete graph in the blackboard.
    * All agents get their initi data as one compressed delta.
    *
    * If an empty initSet is passed, initial data can still be
    * passed into the graph.
    *
    * @param initSet Set of initial data
    * @param blackoard The blackboard all action is sceduled over
    */
  def initGraph(initSet : Iterable[In])(implicit blackoard : Blackboard) = {
    // Add DS
    dataStructures foreach (x => blackoard.addDS(x))

    // Add Rules
    rules foreach (x => blackoard.registerAgent(new RuleAgent(x)))

    // Add Data
    val delta = new ImmutableDelta(Map(initType -> initSet.toSeq))
    blackoard.submitDelta(delta)
  }

  def fetchResult(implicit blackboard: Blackboard) : Iterable[Out] = {
    blackboard.getData(outType)
  }
}
