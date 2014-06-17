package leo.datastructures.internal

/**
 * Created by lex on 16.06.14.
 */
class DAGTermSet {
  abstract sealed class DAGNode extends Term {
    protected[internal] def inc(scopeIndex: Int): Term = ???

    /** Right-folding on terms. */
    def foldRight[A](symFunc: (Signature#Key) => A)(boundFunc: (Type, Int) => A)(absFunc: (Type, A) => A)(appFunc: (A, A) => A)(tAbsFunc: (A) => A)(tAppFunc: (A, Type) => A): A = ???

    /** Return the β-nf of the term */
    def betaNormalize: Term = ???

    protected[internal] def instantiate(scope: Int, by: Type): Term = ???

    // Substitutions
    def substitute(what: Term, by: Term): Term = ???

    def freeVars: Set[Term] = ???

    // Queries on terms
    def ty: Type = termTypes(this)

    val isTypeAbs: Boolean = false
    val isTypeApp: Boolean = false
    val isTermAbs: Boolean = false
    val isTermApp: Boolean = false
    val isAtom: Boolean = false
  }
  case class SymbolNode(key: Signature#Key) extends DAGNode {
    def pretty = Signature(key).name
    override val isAtom = true
  }
  case class BoundNode(scope: Int) extends DAGNode {
    def pretty = scope.toString
    override val isAtom = true
  }
  case class TermAbstractionNode(absTy: Type, body: DAGNode) extends DAGNode {
    def pretty = "[λ:"+ absTy.pretty +". " + body.pretty + "]"
    override val isTermAbs = true
  }
  case class TermApplicationNode(left: DAGNode, right: DAGNode) extends DAGNode {
    def pretty = "(" + left.pretty + " " + right.pretty + ")"
    override val isTermApp = true
  }
  case class TypeAbstractionNode(body: DAGNode) extends DAGNode {
    def pretty = "[Λ." + body.pretty + "]"
    override val isTypeAbs = true
  }
  case class TypeApplicationNode(left: DAGNode, right: Type) extends DAGNode {
    def pretty = "(" + left.pretty + " " + right.pretty + ")"
    override val isTypeApp = true
  }

  type Node = DAGNode

  private var atomTerms: Map[Signature#Key, Node] = Map.empty
  private var abstractionTerms: Map[Node, Map[Type, Node]] = Map.empty
  private var applicationTerms: Map[Node, Map[Node, Node]] = Map.empty
  private var termTypes: Map[Node, Type] = Map.empty

  def mkAtom(key: Signature#Key): Term = {
    atomTerms.contains(key) match {
      case false => {
        val node = SymbolNode(key)
        atomTerms += ((key, node))
        node
      }
      case true => atomTerms(key)
    }
  }

  def mkAbstraction(ty: Type)(body: Node): Term = {
    abstractionTerms.get(body) match {
        case None => {
            val node = TermAbstractionNode(ty, body)
            abstractionTerms += ((body, Map((ty, node))))
            node
        }
        case Some(map2) => map2.get(ty) match {
            case None => {
              val node = TermAbstractionNode(ty, body)
              abstractionTerms += ((body, map2.+((ty, node))))
              node
            }
            case Some(node) => node
        }
    }
  }


}
