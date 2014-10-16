package leo.datastructures.internal.terms.naive

import leo.datastructures.internal.Signature
import leo.datastructures.internal.terms.{Type, Term, Subst}

/**
 * Created by lex on 16.06.14.
 */
object DAGTermSet {
  abstract sealed class DAGNode extends Term {
    protected[internal] def inc(scopeIndex: Int): Term = ???

    /** Right-folding on terms. */
    def foldRight[A](symFunc: (Signature#Key) => A)(boundFunc: (Type, Int) => A)(absFunc: (Type, A) => A)(appFunc: (A, A) => A)(tAbsFunc: (A) => A)(tAppFunc: (A, Type) => A): A = ???

    /** Return the β-nf of the term */
    def betaNormalize: Term = ???

    protected[internal] def instantiate(scope: Int, by: Type): Term = ???

    // Substitutions
    def substitute(what: Term, by: Term): Term = ???

    def freeVars: Set[Term] = Set.empty // TODO
    def boundVars = ???

    // Handling def. expansion
    def δ_expandable = ???
    def partial_δ_expand(rep: Int) = ???
    def full_δ_expand = ???

    def head_δ_expandable = ???
    def head_δ_expand= ???

    // Queries on terms
    def ty: Type = termTypes(this)

    def headSymbol = ???

    val isTypeAbs: Boolean = false
    val isTermAbs: Boolean = false
    val isAtom: Boolean = false
    val isApp = false

    def scopeNumber = ???

    def typeCheck = true
    def instantiateWith(subst: Subst) = ???

    def closure(s: Subst) = ???
    def normalize(s: Subst, s2: Subst) = ???
    def size = 0
  }
  case class SymbolNode(key: Signature#Key) extends DAGNode {
    def pretty = Signature(key).name
    override val isAtom = true
  }
  case class BoundNode(boundType: Type, scope: Int) extends DAGNode {
    def pretty = scope.toString
    override val isAtom = true
  }
  case class TermAbstractionNode(absTy: Type, body: DAGNode) extends DAGNode {
    def pretty = "[λ:"+ absTy.pretty +". " + body.pretty + "]"
    override val isTermAbs = true

    override def typeCheck = body.typeCheck
  }
  case class TermApplicationNode(left: DAGNode, right: DAGNode) extends DAGNode {
    def pretty = "(" + left.pretty + " " + right.pretty + ")"
    override val isApp = true

    override def typeCheck = left.ty.isFunType && left.ty._funDomainType == right.ty && right.typeCheck
  }
  case class TypeAbstractionNode(body: DAGNode) extends DAGNode {
    def pretty = "[Λ." + body.pretty + "]"
    override val isTypeAbs = true

    override def typeCheck  = body.typeCheck
  }
  case class TypeApplicationNode(left: DAGNode, right: Type) extends DAGNode {
    def pretty = "(" + left.pretty + " " + right.pretty + ")"
    override val isApp = true

    override def typeCheck = left.typeCheck
  }

  type Node = DAGNode

  private var terms: Set[Node] = Set.empty
  private var atomTerms: Map[Signature#Key, Node] = Map.empty
  private var abstractionTerms: Map[Node, Map[Type, Node]] = Map.empty
  private var applicationTerms: Map[Node, Map[Node, Node]] = Map.empty
  private var termTypes: Map[Node, Type] = Map.empty

  def mkAtom(key: Signature#Key): Term = {
    atomTerms.get(key) match {
      case None => {
        val node = SymbolNode(key)
        atomTerms += ((key, node))
        Signature(key).ty match {
          case Some(t) => termTypes += ((node, t))
          case None => termTypes += ((node, Type.mkType(Signature.get.freshTypeVar)))
        }
        terms += node
        node
      }
      case Some(node) => node
    }
  }

  def mkBound(ty: Type, scope: Int): Term = {
//    terms.contains(BoundNode(ty,scope))
//    terms += ()
    BoundNode(ty, scope) // TODO: share it
  }

  def mkAbstraction(ty: Type)(body: Node): Term = {
    abstractionTerms.get(body) match {
        case None => {
            val node = TermAbstractionNode(ty, body)
            abstractionTerms += ((body, Map((ty, node))))
            termTypes += ((node, Type.mkFunType(ty, body.ty)))
            terms += node
            node
        }
        case Some(map2) => map2.get(ty) match {
            case None => {
              val node = TermAbstractionNode(ty, body)
              abstractionTerms += ((body, map2.+((ty, node))))
              termTypes += ((node, Type.mkFunType(ty, body.ty)))
              terms += node
              node
            }
            case Some(node) => node
        }
    }
  }

  def mkApplication(left: Node, right: Node): Term = {
    applicationTerms.get(left) match {
      case None => {
        // No Map exists for (left X)
        val node = TermApplicationNode(left,right)
        applicationTerms += ((left, Map((right,node))))
        left.ty.funCodomainType match {
          case None => throw new IllegalArgumentException("Left side of application not a function")
          case Some(t) => termTypes += ((node, t))
        }
        terms += node
        node
      }
      case Some(map2) => {
        map2.get(right) match {
          case None => {
            val node = TermApplicationNode(left,right)
            applicationTerms += ((left, (map2.+((right,node)))))
            left.ty.funCodomainType match {
              case None => throw new IllegalArgumentException("Left side of application not a function")
              case Some(t) => termTypes += ((node, t))
            }
            terms += node
            node
          }
          case Some(node) => node
        }
      }
    }
  }


}
