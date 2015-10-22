package leo.datastructures

import leo.Configuration

/**
 * Interface for literals, companion object `Literal` provides constructor methods.
 *
 * Literals are (directed) equations `s = t` of terms.
 *
 * @author Alexander Steen
 * @since 07.11.2014
 * @note Oct. 2015: Substantially updated (literals as equations)
 */
trait Literal extends Pretty with Ordered[Literal] with HasCongruence[Literal] {
  /** The unique, increasing literal number. */
  def id: Int
  /** The left side of the literal's equation.
    * Invariant: `left > right or !oriented` where > is a term ordering. */
  def left: Term
  /** The left side of the literal's equation.
    * Invariant: `!equational => right = $true or right = $false`.
    * Invariant: `left > right or !oriented` where `>` is a term ordering. */
  def right: Term
  /** The polarity of the literal.
    * I.e. whether the literals occurs
    * as negative equation `s != t` or as positive equation `s = t`,
    * Positive polarity is encoded by the Boolean value `true`,
    * negative polarity by Boolean value `false`.*/
  def polarity: Boolean
  /** Whether the equation could have been oriented wrt. a term ordering `>`. */
  def oriented: Boolean

  /** Returns a term representation of the literal.
    * @return Term `s = t` if `polarity`; term `!(s = t)` if `!polarity`,
    *         where `s ≡ left and t ≡ right`. If `!equational` the equality may
    *         be contracted, e.g. `!s` instead of `s = $false` (here `polarity ≡ true`).*/
  def term: Term

  // Further properties
  /** Returns true iff the literal is equational, i.e. iff `l` is an equation `s = t` and not
    * `s = $true` or `s = $false`.*/
  def equational: Boolean
  /** Returns true iff the literal is propositional. */
  def propositional: Boolean
  /** Returns true iff the literal is a positive equation. */
  def equation: Boolean
  /** Returns true iff the literal is an unification constraint. */
  def uni: Boolean
  /** Returns true iff the literal is a flex-flex unification constraint,*/
  def flexflex: Boolean
  /** Returns true iff the literal has a flexible head. */
  def flexHead: Boolean

  /** Returns the set of free variables from `s = t` regarded as term. */
  @inline final lazy val fv: Set[Term] = left.freeVars ++ right.freeVars
  /** Returns the set of meta variables from `s = t` regarded as term. */
  @inline final lazy val metaVars: Set[(Type, Int)] = left.metaVars ++ right.metaVars
  /** Returns true iff the equation `s = t` is ground. */
  @inline final lazy val ground: Boolean = left.ground && right.ground

  /** The weight of the literal as determined by the underlying literal weighting. */
  @inline final lazy val weight: Int = Configuration.LITERAL_WEIGHTING.weightOf(this)
  /** Comparison of two literals using a literal ordering given by (literals `l`,`l'`):
    * `l > l'` iff `mc(l) >>> mc(l')`
    * where `>>>` is the twofold multiset-extension `(((>)mul)mul)` of `>`,
    * and, for any literal `l`,
    * `mc(l) ≡ {{s},{t}}` if `l.polarity` and `mc(l) ≡ {{s,t}}` if `!l.polarity`,
    * and `(s,t) ≡ (l.left,l.right)`.
    * */
  @inline final def compare(that: Literal): Int = Configuration.LITERAL_ORDERING.compare(this, that)


  // Utility functions

  @inline final def fold[A](f: (Term, Term, Boolean) => A): A = f(left,right,polarity)

  @inline final def termMap[A](f: (Term, Term) => (Term, Term)): Literal = {
    val (nl,nr) = f(left,right)
    Literal(nl,nr, polarity)
  }
  @inline final def leftTermMap[A](f: Term => Term): Literal = termMap {case (l,r) => (f(l), r)}
  @inline final def rightTermMap[A](f: Term => Term): Literal = termMap {case (l,r) => (l, f(r))}
  @inline final def newLeft(newLeft: Term): Literal = termMap {case (l,r) => (newLeft,r)}
  @inline final def newRight(newRight: Term): Literal = termMap {case (l,r) => (l,newRight)}
  /** Returns a new literal with the same equation as this one, only with polarity flipped.*/
  @inline final lazy val flipPolarity: Literal = {
    if (polarity)
      Literal(left,right,false)
    else
      Literal(left,right,true)
  }

  @inline final def substitute(s : Subst) : Literal = termMap {case (l,r) => (l.substitute(s).betaNormalize,r.substitute(s).betaNormalize)}
  @inline final def replaceAll(what : Term, by : Term) : Literal = termMap {case (l,r) => (l.replace(what,by), r.replace(what,by))}


  // TODO: Do we need that anymore?
  def cong(that: Literal): Boolean = (this.polarity == that.polarity) && (this.term == that.term)

  // System function adaptions
  override final def equals(obj : Any) : Boolean = obj match {
    case ol:Literal if ol.polarity == polarity => (ol.left == left && ol.right == right) || (ol.left == right && ol.right == left)
    case _ => false
  }
  override final def hashCode() : Int = {
    val lh = left.hashCode();val rh = right.hashCode()
    if(polarity) lh^rh else ~(lh^rh)
  }
}

object Literal extends Function3[Term, Term, Boolean, Literal] {
  import leo.datastructures.impl.{LiteralImpl => LitImpl}

  // Constructor methods
  /** Create new (equational) literal with equation `left = right`
    * and polarity `polarity`. Note that the resulting literal is only
    * equational if neither `left` nor `right` are `$true/$false`. */
  @inline final def mkLit(t1: Term, t2: Term, pol: Boolean): Literal = LitImpl.mkLit(t1,t2,pol)
  /** Create new (non-equational) literal with equation
    * `left = right ≡ $true/$false` and polarity `polarity`. */
  @inline final def mkLit(t: Term, right: Boolean, pol: Boolean): Literal = LitImpl.mkLit(t,right,pol)

  // Convenience methods
  /** Create new (non-equational) literal with equation
    * `left = right ≡ $true/$false` and polarity `true`. */
  @inline final def mkLit(t: Term, right: Boolean): Literal = mkLit(t, right, true)
  /** Create new (equational) literal with equation `left = right`
    * and positive polarity. Note that the resulting literal is only
    * equational if neither `left` nor `right` are `$true/$false`. */
  @inline final def mkPos(t1: Term, t2: Term): Literal = mkLit(t1,t2,true)
  /** Create new (equational) literal with equation `left = right`
    * and negative polarity. Note that the resulting literal is only
    * equational if neither `left` nor `right` are `$true/$false`.*/
  @inline final def mkNeg(t1: Term, t2: Term): Literal = mkLit(t1,t2,false)

  // Apply method redirections
  /** Create new (equational) literal with equation `left = right`
    * and polarity `polarity`. Note that the resulting literal is only
    * equational if neither `left` nor `right` are `$true/$false`. */
  @inline override final def apply(t1: Term, t2: Term, polarity: Boolean) = mkLit(t1,t2,polarity)
  /** Create new (non-equational) literal with equation
    * `left = right ≡ $true/$false` and polarity `true`. */
  @inline final def apply(left: Term, right: Boolean) = mkLit(left, right)
}

