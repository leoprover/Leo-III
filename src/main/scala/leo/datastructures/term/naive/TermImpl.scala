package leo.datastructures.term.naive

import leo.datastructures._
import leo.datastructures.impl.{Signature, BoundTypeNode}
import leo.datastructures.term.Term

/**
 * Naive implementation of nameless lambda terms.
 * Uses inefficient reduction und substitution methods
 *
 * @author Alexander Steen
 * @since 02.06.2014
 */
sealed abstract class TermImpl extends Term {

  // Predicates on terms
  val isAtom = false
  val isConstant = false
  val isVariable = false
  val isTermAbs = false
  val isTypeAbs = false
  val isApp = false

  def instantiateWith(subst: Subst) = ???
  def full_δ_expand = partial_δ_expand(-1)

  def normalize(subst: Subst, subst2: Subst) = ???

  val locality = LOCAL
  val isLocal = true

  protected[naive] def decrementByOne(n: Int): Int = n match {
    case -1 => -1
    case  n => n-1
  }

  def closure(s: Subst) = ???
  def occurrences: Map[Term, Set[Position]] = ???
  def scopeNumber = ???
  def size = 0
  def langOrder = ???

  def replace(what: Term, by: Term): Term = ???
  def replaceAt(at: Position, by: Term): Term = ???

  /** Eta-contract term on root level if possible */
  def topEtaContract = ???
}

///////////////////
// Term symbols
///////////////////

protected[term] case class SymbolNode(id: Signature#Key) extends TermImpl {
  private lazy val sym = Signature.get.meta(id)

  // Predicates on terms
  override val isAtom = true
  override val isConstant = true

  // Handling def. expansion
  lazy val δ_expandable = sym.hasDefn
  def partial_δ_expand(rep: Int) = rep match {
    case 0 => this
    case n => sym.defn match {
      case None => this
      case Some(defn) => defn.partial_δ_expand(decrementByOne(n))
    }
  }

  lazy val head_δ_expandable = sym.hasDefn
  def head_δ_expand = partial_δ_expand(1)


  // Queries on terms
  def ty = sym._ty
  def freeVars = Set(this)
  val symbols = Set(id)
  def boundVars = Set()
  val looseBounds = Set[Int]()
  lazy val headSymbol = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    this
  }

  // Substitutions
  def substitute(what: Term, by: Term) = what match {
    case SymbolNode(s) if s == id => by
    case _                    => this
  }
  def inc(scopeIndex: Int) = this
  def instantiate(scope: Int, by: Type) = this

  // Other operations
  def typeCheck = true

  val betaNormalize = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    this
  }

  // Pretty printing
  def pretty = sym.name
}

protected[term] case class BoundNode(t: Type, scope: Int) extends TermImpl {
  override val isAtom = true
  override val isVariable = true

  // Handling def. expansion
  val δ_expandable = false
  def partial_δ_expand(rep: Int) = this

  val head_δ_expandable = false
  val head_δ_expand = this

  // Queries on terms
  def ty = t
  val freeVars = Set[Term]()
  val symbols = Set[Int]()
  val boundVars = Set[Term](this)
  val looseBounds = Set(scope)
  lazy val headSymbol = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    this
  }

  // Substitutions
  def substitute(what: Term, by: Term) = what match {
    case BoundNode(_, i) if i == scope => by
    case BoundNode(_, i) if i < scope => BoundNode(t, scope-1)
    case _                             => this
  }

  def inc(scopeIndex: Int) = scopeIndex match {
    case s if s <= scope => BoundNode(t, scope+1)
    case _ => this
  }

  def instantiate(sc: Int, by: Type) = {
    t match {
//      case ForallTypeNode(body) => BoundNode(body.substitute(BoundTypeNode(scope),by),scope)
      case _ => BoundNode(t.substitute(BoundTypeNode(sc),by),scope)
    }
  }




  // Other operations
  def typeCheck = true

  val betaNormalize = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    this
  }

  def expandDefinitions(rep: Int) = this

  // Pretty printing
  def pretty = scope.toString
}

protected[term] case class AbstractionNode(absType: Type, term: Term) extends TermImpl {
  override val isTermAbs = true


  // Handling def. expansion
  lazy val δ_expandable = term.δ_expandable
  def partial_δ_expand(rep: Int) = AbstractionNode(absType, term.partial_δ_expand(rep))

  lazy val head_δ_expandable = headSymbol.δ_expandable
  def head_δ_expand = AbstractionNode(absType, term.head_δ_expand)


  // Queries on terms
  def ty = absType ->: term.ty
  val freeVars = term.freeVars
  val symbols = term.symbols
  val boundVars = term.boundVars
  lazy val looseBounds = term.looseBounds.map(_ - 1).filter(_ > 0)
  lazy val headSymbol = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    term.headSymbol
  }

  // Substitutions
  def substitute(what: Term, by: Term) = what match {
   case BoundNode(t,i) => AbstractionNode(absType, term.substitute(BoundNode(t,i+1), by.inc(1)))
   case _ => AbstractionNode(absType, term.substitute(what,by))
  }

  def inc(scopeIndex: Int) = AbstractionNode(absType, term.inc(scopeIndex+1))

  def instantiate(scope: Int, by: Type) = AbstractionNode(absType.substitute(BoundTypeNode(scope),by),term.instantiate(scope,by))

   // Other operations
  def typeCheck = term.typeCheck

  lazy val betaNormalize = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    AbstractionNode(absType, term.betaNormalize)
  }

  // Pretty printing
  def pretty = "[λ." + term.pretty + "]"
}



protected[term] case class ApplicationNode(left: Term, right: Term) extends TermImpl {
  override val isApp = true


  // Handling def. expansion
  lazy val δ_expandable = left.δ_expandable || right.δ_expandable
  def partial_δ_expand(rep: Int) = ApplicationNode(left.partial_δ_expand(rep), right.partial_δ_expand(rep))

  lazy val head_δ_expandable = headSymbol.δ_expandable
  def head_δ_expand = ApplicationNode(left.head_δ_expand, right)

  // Queries on terms
  lazy val ty = {
    require(left.ty.isFunType, "Application node not well typed: "+this.pretty)
    left.ty._funCodomainType
  } // assume everything is well-typed

  val freeVars = left.freeVars ++ right.freeVars
  val symbols = left.symbols ++ right.symbols
  val boundVars = left.boundVars ++ right.boundVars
  lazy val looseBounds = left.looseBounds ++ right.looseBounds
  lazy val headSymbol = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    left.headSymbol
  }

  // Substitutions
  def substitute(what: Term, by: Term) = ApplicationNode(left.substitute(what,by), right.substitute(what,by))

  def inc(scopeIndex: Int) = ApplicationNode(left.inc(scopeIndex), right.inc(scopeIndex))

  def instantiate(scope: Int, by: Type) = ApplicationNode(left.instantiate(scope,by), right.instantiate(scope,by))

  // Other operations
  def typeCheck = left.ty.isFunType && left.ty._funDomainType == right.ty

  lazy val betaNormalize = {
    import leo.datastructures.term.Reductions
    Reductions.tick()

    val leftNF = left.betaNormalize
    val rightNF = right.betaNormalize

    leftNF match {
      case AbstractionNode(ty, body) => body.substitute(BoundNode(ty ,1), rightNF).betaNormalize
      case _ => ApplicationNode(leftNF, rightNF)
    }
  }

  // Pretty printing
  def pretty = "(" + left.pretty + " " + right.pretty + ")"
}


///////////////////
// Type symbols
///////////////////

protected[term] case class TypeAbstractionNode(term: Term) extends TermImpl {
  override val isTypeAbs = true

  // Handling def. expansion
  lazy val δ_expandable = term.δ_expandable
  def partial_δ_expand(rep: Int) = TypeAbstractionNode(term.partial_δ_expand(rep))

  lazy val head_δ_expandable = term.head_δ_expandable
  def head_δ_expand = TypeAbstractionNode(term.head_δ_expand)

  // Queries on terms
  lazy val ty = Type.mkPolyType(term.ty)
  val freeVars = term.freeVars
  val symbols = term.symbols
  val boundVars = term.boundVars
  lazy val looseBounds = term.looseBounds
  lazy val headSymbol = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    term.headSymbol
  }

  // Substitutions
  def substitute(what: Term, by: Term) = TypeAbstractionNode(term.substitute(what,by))
  def inc(scopeIndex: Int) = TypeAbstractionNode(term.inc(scopeIndex))
  def instantiate(scope: Int, by: Type) = TypeAbstractionNode(term.instantiate(scope+1,by))
  // Other operations
  def typeCheck = term.typeCheck

  lazy val betaNormalize = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    TypeAbstractionNode(term.betaNormalize)
  }

  // Pretty printing
  def pretty = "[Λ." + term.pretty + "]"
}

protected[term] case class TypeApplicationNode(left: Term, right: Type) extends TermImpl {
  override val isApp = true

  // Handling def. expansion
  lazy val δ_expandable = left.δ_expandable
  def partial_δ_expand(rep: Int) = TypeApplicationNode(left.partial_δ_expand(rep), right)

  lazy val head_δ_expandable = headSymbol.δ_expandable
  def head_δ_expand = TypeApplicationNode(left.head_δ_expand, right)

  // Queries on terms
  lazy val ty = {
    require(left.ty.isPolyType, "Type Application node not well typed: "+this.pretty)
    left.ty.instantiate(right)
  } // assume everything is well-typed

  val freeVars = left.freeVars
  val symbols = left.symbols
  val boundVars = left.boundVars
  lazy val looseBounds = left.looseBounds
  lazy val headSymbol = {
    import leo.datastructures.term.Reductions
    Reductions.tick()
    left.headSymbol
  }

  // Substitutions
  def substitute(what: Term, by: Term) = TypeApplicationNode(left.substitute(what,by), right)

  def inc(scopeIndex: Int) = TypeApplicationNode(left.inc(scopeIndex), right)

  def instantiate(scope: Int, by: Type) = TypeApplicationNode(left.instantiate(scope,by), right.substitute(BoundTypeNode(scope),by))
  // Other operations
  def typeCheck = left.ty.isPolyType && left.typeCheck

  lazy val betaNormalize = {
    import leo.datastructures.term.Reductions
    Reductions.tick()

    val leftNF = left.betaNormalize

    leftNF match {
      case TypeAbstractionNode(term) => term.instantiateBy(right).betaNormalize
      case _ => TypeApplicationNode(leftNF, right)
    }
  }

  // Pretty printing
  def pretty = "(" + left.pretty + " " + right.pretty + ")"
}


object TermImpl {
  def mkAtom = SymbolNode(_)
  def mkBound = BoundNode(_,_)
  def mkTermApp = ApplicationNode(_,_)
  def mkTermApp(func: Term, args: Seq[Term]): Term = args.foldLeft(func)((arg,f) => mkTermApp(arg,f))
  def mkTermAbs = AbstractionNode(_, _)
  def mkTypeApp = TypeApplicationNode(_,_)
  def mkTypeApp(func: Term, args: Seq[Type]): Term = args.foldLeft(func)((arg,f) => mkTypeApp(arg,f))
  def mkTypeAbs = TypeAbstractionNode(_)

  def mkApp(func: Term, args: Seq[Either[Term, Type]]): Term = args.foldLeft(func)((arg,f) => f.fold(mkTermApp(arg,_),mkTypeApp(arg,_)))

  def reset() = ()
}

