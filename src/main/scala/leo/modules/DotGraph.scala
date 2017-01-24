package leo.modules

import leo.datastructures.Term._
import leo.datastructures._

/**
 *
 * Encapsulated a graph representation of terms in the GraphViz Dot format.
 * Single terms can be added with the `insertTerm` method, a graph may be
 * created directly using the companion object, i.e. by invocation of
 * `DotGraph(t)` where t is a Term.
 *
 * @author Alexander Steen
 * @since 11.09.2014
 * @see [[http://www.graphviz.org/pdf/dotguide.pdf]]
 */
class DotGraph {
  private var str: String = ""
  private var nodeCounter: Int = 0

  private val bottomLabel = "⊥"

  /** Inserts the given term `t` to the underlying graph. */
  def insertTerm(t: Term)(implicit sig: Signature): String = {
    t match {
      case Symbol(id)            => append(node(id.toString, sig(id).name))
                                    append(node("bottom", bottomLabel))
                                    val p = next()
                                    append(node(p, "∙"))
                                    append(edge(p, id.toString))
                                    append(edge(p, "bottom"))
                                    p
      case Bound(ty,scope)        =>
                                    append(node(scope.toString ++ ty.pretty,  scope.toString ++ " type: (" ++ ty.pretty ++ ")"))
                                    append(node("bottom", bottomLabel))
                                    val p = next()
                                    append(node(p, "∙"))
                                    append(edge(p, scope.toString ++ ty.pretty))
                                    append(edge(p, "bottom"))
                                    p
      case f ∙ args               => val p1 = insertTerm(f)
                                     val p2 = insertArgs(args)
                                     val p = next()
                                     append(node(p, "∙"))
                                     append(edge(p, p1))
                                     append(edge(p, p2))
                                     p
      case ty :::> t2              => val p1 = insertTerm(t2)
                                      val p = next()
                                      append(node(p, "λ"))
                                      append(edge(p, p1))
                                      p

      case TypeLambda(t2)          => val p1 = insertTerm(t2)
                                      val p = next()
                                      append(node(p, "Λ"))
                                      append(edge(p, p1))
                                      p
    }
  }

  /** Inserts lists of arguments (spines) to the graph */
  protected def insertArgs(args: Seq[Either[Term, Type]])(implicit sig: Signature): String = {
    args match {
      case Seq() => append(node("bottom", bottomLabel))
                    "bottom"
      case Seq(hd, rest@_*) => val p1 = hd.fold(insertTerm, {ty => val p = next();append(node(p,ty.pretty));p })
        val p2 = insertArgs(rest)
        val p = next()
        append(node(p, ";"))
        append(edge(p, p1))
        append(edge(p, p2))
        p
    }
  }

  /** Append `s` to the graph body, where `s` is a string in dot-format.
    * A '\n'-character is implicitly added at the end of the input. */
  def append(s: String): Unit = {
    str += s
    str += "\n"
  }

  /** Get next unique node name */
  protected def next(): String = {
    val res = "node" + nodeCounter
    nodeCounter += 1
    res
  }

  /** Yields the dot string representation of a node with name `name` */
  def node(name: String): String = "\"" ++ name ++ "\";"
  /** Yields the dot string representation of a node with name `name` and label `label` */
  def node(name: String, label: String): String = "\"" ++ name ++ "\" [label=\""++ label ++"\"];"
  /** Yields the dot string representation of a edge with from node `from` to node `to` */
  def edge(from: String, to: String): String = "\"" ++ from ++ "\" -> \"" ++ to ++ "\";"

  /** Return the complete dot representation of the graph */
  override def toString: String = "digraph {\n" ++ str ++ "}"
}

object DotGraph {

  /** Create a new DotGraph containing the term `t` */
  def apply(t: Term)(implicit sig: Signature): DotGraph = {
    val g = new DotGraph
    g.insertTerm(t)(sig)
    g
  }

}
