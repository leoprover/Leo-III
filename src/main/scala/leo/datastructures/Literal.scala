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
trait Literal extends Pretty {
  /** The unique, increasing literal number. */
  def id: Int
  /** The left side of the literal's equation.
    * Invariant: `left > right or !oriented` where > is a term ordering. */
  def left: Term
  /** The right side of the literal's equation.
    * Invariant: `!equational => right = $true`.
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

  // Further properties
  /** Returns true iff the literal is equational, i.e. iff `l` is an equation `s = t` with
    * `t != $true` and `t != $false`.*/
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
  @inline final lazy val fv: Set[(Int, Type)] = left.fv ++ right.fv
  /** Returns the set of free variables from `s = t` regarded as term. */
  @inline final lazy val tyFV: Set[Int] = left.tyFV union right.tyFV
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
  @inline final def compare(that: Literal)(implicit sig: Signature): CMP_Result = Literal.compare(this, that)(sig)


  // Utility functions
  @inline final def fold[A](f: (Term, Term, Boolean) => A): A = f(left,right,polarity)

  /** Creates a new literal with same polarity but with terms modified according to `f`.
    * Note that the literal is not oriented afterwards. */
  @inline final def termMap[A](f: (Term, Term) => (Term, Term)): Literal = {
    val (nl,nr) = f(left,right)
    Literal(nl,nr, polarity)
  }
  /** Creates a new literal with same polarity but with `left` modified according to `f`.
    * Note that the literal is not oriented afterwards. */
  @inline final def leftTermMap[A](f: Term => Term): Literal = termMap {case (l,r) => (f(l), r)}
  /** Creates a new literal with same polarity but with `right` modified according to `f`.
    * Note that the literal is not oriented afterwards. */
  @inline final def rightTermMap[A](f: Term => Term): Literal = termMap {case (l,r) => (l, f(r))}
  /** Apply substitution `(termSubst, typeSubst)` to literal (i.e. to both sides of the equation).
    * Result it beta-normalized.
    * Note that the literal is __not oriented__ afterwards. If you want to orient the result,
    * use [[substituteOrdered]] instead. */
  @inline final def substitute(termSubst : Subst, typeSubst: Subst = Subst.id) : Literal = termMap {case (l,r) => (l.substitute(termSubst, typeSubst),r.substitute(termSubst,typeSubst))}
  /** Apply substitution `(termSubst, typeSubst)` to literal (i.e. to both sides of the equation).
    * Result it beta-normalized and oriented if possible. */
  @inline final def substituteOrdered(termSubst : Subst, typeSubst: Subst = Subst.id)(implicit sig: Signature) : Literal = {
    val lsubst = left.substitute(termSubst, typeSubst)
    val rsubst = right.substitute(termSubst, typeSubst)
    Literal.mkOrdered(left,right,polarity)
  }
  @inline final def replaceAll(what : Term, by : Term) : Literal = termMap {case (l,r) => (l.replace(what,by), r.replace(what,by))}
  @inline final def unsignedEquals(that: Literal): Boolean = (left == that.left && right == that.right) || (left == that.right && right == that.left)

  // System function adaptions
  override final def equals(obj : Any) : Boolean = obj match {
    case ol:Literal if ol.polarity == polarity => unsignedEquals(ol)
    case _ => false
  }
  override final def hashCode() : Int = {
    val lh = left.hashCode();val rh = right.hashCode()
    if(polarity) lh^rh else ~(lh^rh)
  }
}

object Literal {
  import leo.datastructures.impl.{LiteralImpl => LitImpl}
  import leo.datastructures.Term.Symbol
  import leo.datastructures.Orderings._

  // Constructor methods
  /** Create new (equational) literal with equation `left = right`
    * and polarity `pol`. Note that the resulting literal is only
    * equational if neither `left` nor `right` are `$true/$false`. */
  @inline final def mkLit(t1: Term, t2: Term, pol: Boolean, oriented: Boolean = false): Literal = LitImpl.mkLit(t1,t2,pol,oriented)
  /** Creates a new (equational) literal of the two terms t1 and t2
    * and polarity `polarity`. During construction, the method
    * tries to order the two terms into and ordered equation left=right,
    * where left >= right wrt. term ordering. If this fails,
    * t1 will be used as left and t2 as right.
    * Note that the resulting literal is only
    * equational if both terms t1 and t2 and not equivalent to $true/$false. */
  @inline final def mkOrdered(t1: Term, t2: Term, pol: Boolean)(implicit sig: Signature): Literal = LitImpl.mkOrdered(t1,t2,pol)(sig)
  /** Create new (non-equational) literal with equation
    * `left = $true` and polarity `pol`. */
  @inline final def mkLit(t: Term, pol: Boolean): Literal = LitImpl.mkLit(t,pol)

  // Convenience methods
  /** Create new unordered (equational) literal with equation `left = right`
    * and positive polarity. Note that the resulting literal is only
    * equational if neither `left` nor `right` are `$true/$false`. */
  @inline final def mkPos(t1: Term, t2: Term, oriented: Boolean = false): Literal = mkLit(t1,t2,true, oriented)
  /** Create new unordered (equational) literal with equation `left = right`
    * and negative polarity. Note that the resulting literal is only
    * equational if neither `left` nor `right` are `$true/$false`.*/
  @inline final def mkNeg(t1: Term, t2: Term, oriented: Boolean = false): Literal = mkLit(t1,t2,false, oriented)
  /** Create new (equational) literal with equation `left = right`
    * and positive polarity. During construction, the method
    * tries to order the two terms into and ordered equation left=right,
    * where left >= right wrt. term ordering. If this fails,
    * t1 will be used as left and t2 as right.
    * Note that the resulting literal is only
    * equational if neither `left` nor `right` are `$true/$false`. */
  @inline final def mkPosOrdered(t1: Term, t2: Term)(implicit sig: Signature): Literal = mkOrdered(t1,t2,true)(sig)
  /** Create new (equational) literal with equation `left = right`
    * and negative polarity. During construction, the method
    * tries to order the two terms into and ordered equation left=right,
    * where left >= right wrt. term ordering. If this fails,
    * t1 will be used as left and t2 as right.
    * Note that the resulting literal is only
    * equational if neither `left` nor `right` are `$true/$false`.*/
  @inline final def mkNegOrdered(t1: Term, t2: Term)(implicit sig: Signature): Literal = mkOrdered(t1,t2,false)(sig)
  // Apply method redirections
  /** Create new unordered (equational) literal with equation `left = right`
    * and polarity `pol`. Note that the resulting literal is only
    * equational if neither `left` nor `right` are `$true/$false`. */
  @inline final def apply(t1: Term, t2: Term, pol: Boolean, oriented: Boolean = false) = mkLit(t1,t2,pol, oriented)
  /** Create new (non-equational) literal with equation
    * `left = $true` and polarity `pol`. */
  @inline final def apply(left: Term, pol: Boolean) = mkLit(left, pol)

  // Equation selection stuff
  type Side = Boolean
  final val leftSide: Side = true
  final val rightSide: Side = false

  /** Returns the specified side of the underlying equation. */
  @inline final def selectSide(l: Literal, side: Side): Term = if (side) l.left else l.right
  /** Returns the opposite side of the specified side. */
  @inline final def selectOtherSide(l: Literal, side: Side): Term = selectSide(l, !side)
  /** Returns the sides of the literal l = r where the first element is l if first==leftSide, r otherwise. */
  @inline final def getSidesOrdered(l: Literal, first: Side): (Term, Term) = if (first) (l.left, l.right) else (l.right,l.left)

  // Ordering stuff
  sealed abstract class LitMaxFlag
  case object LitStrictlyMax extends LitMaxFlag
  case object LitMax extends LitMaxFlag

  final def compare(a: Literal, b: Literal)(implicit sig: Signature): CMP_Result = {
    if (a == b) CMP_EQ
    else if (a.polarity == b.polarity) cmpSamePol(a,b)(sig)
    else if (a.polarity) cmpDiffPol(a,b)(sig)
    else Orderings.invCMPRes(cmpDiffPol(b,a)(sig))
  }
  final def maximalityOf(lits: Seq[Literal])(implicit sig: Signature): Map[LitMaxFlag, Seq[Literal]] = {
    var notmax: Seq[Literal] = Seq()
    var notstrictMax: Seq[Literal] = Seq()

    val maxIdx = lits.length-1
    var i = 0
    while (i <= maxIdx) {
      val l1 = lits(i)
      var j = i+1
      while (j <= maxIdx) {
        val l2 = lits(j)
        val cmp = l1.compare(l2)(sig)
        if (cmp == CMP_GT) {
          notmax = notmax :+ l2
          notstrictMax = notstrictMax :+ l2
        } else if (cmp == CMP_LT) {
          notmax = notmax :+ l1
          notstrictMax = notstrictMax :+ l1
        } else if (cmp == CMP_EQ) {
          notstrictMax = notstrictMax :+ l1
          notstrictMax = notstrictMax :+ l2
        } else {
          // NC

        }

        j += 1
      }
      i += 1
    }
    Map(LitStrictlyMax -> lits.diff(notstrictMax), LitMax -> lits.diff(notmax))
  }
  final def maxOf(lits: Seq[Literal])(implicit sig: Signature): Seq[Literal] = maximalityOf(lits)(sig)(LitMax)
  final def strictlyMaxOf(lits: Seq[Literal])(implicit sig: Signature): Seq[Literal] = maximalityOf(lits)(sig)(LitStrictlyMax)

  /** Compare two literals of same polarity*/
  private final def cmpSamePol(a: Literal, b: Literal)(sig: Signature): CMP_Result = {
    assert(a.polarity == b.polarity)
    assert(a != b) // This should have been catched in `compare`
    // TODO: Improve if oriented
    val (al,ar) = (a.left,a.right)
    val (bl,br) = (b.left,b.right)

    val albl = al.compareTo(bl)(sig)
    val albr = al.compareTo(br)(sig)
    val arbl = ar.compareTo(bl)(sig)
    val arbr = ar.compareTo(br)(sig)

    if ((albl == CMP_GT && albr == CMP_GT) ||
        (isGE(albl) && isGE(arbr)) ||
        (isGE(arbl) && isGE(albr)) ||
        (arbl == CMP_GT && arbr == CMP_GT)) CMP_GT
    else if ((albl == CMP_LT && arbl == CMP_LT) ||
             (isLE(albl) && isLE(arbr)) ||
             (isLE(arbl) && isLE(albr)) ||
             (albr == CMP_LT && arbr == CMP_LT)) CMP_LT
    else CMP_NC
  }

  /** Compare two literals of different polarity.
    * `a` must have positive polarity, `b` must have negative polarity.*/
  private final def cmpDiffPol(a: Literal, b: Literal)(sig: Signature): CMP_Result = {
    assert(a.polarity); assert(!b.polarity)
    // TODO: Improve if oriented
    val (al,ar) = (a.left,a.right)
    val (bl,br) = (b.left,b.right)

    val albl = al.compareTo(bl)(sig)
    val albr = al.compareTo(br)(sig)
    val arbl = ar.compareTo(bl)(sig)
    val arbr = ar.compareTo(br)(sig)
    if ((albl == CMP_GT && albr == CMP_GT) || (arbl == CMP_GT && arbr == CMP_GT)) CMP_GT
    else if ((isLE(albl) || isLE(albr)) && (isLE(arbl) || isLE(arbr))) CMP_LT
    else CMP_NC
  }

  // Utility methods
  /** Returns true iff the literal is trivial (i.e. l.left is syntactically equal to l.right). */
  final def trivial(l: Literal): Boolean = l.left == l.right
  /** If the method returns true both sides of the underlying equation are different/distinct. */
  final def distinctSides(l: Literal): Boolean = (l.left, l.right) match {
    case (Symbol(idl), Symbol(idr)) if idl != idr => idl <= leo.modules.HOLSignature.lastId && idr <= leo.modules.HOLSignature.lastId
      // TODO: Extend to 'distinct symbols' from TPTP
    case _ => false
  }
  /** Returns a term representation of the literal.
    * @return Term `s = t` if `polarity`; term `!(s = t)` if `!polarity`,
    *         where `s ≡ left and t ≡ right`. If `!equational` the equality may
    *         be contracted, e.g. `!s` instead of `s = $false` (here `polarity ≡ false`).*/
  final def asTerm(l: Literal): Term = {
    import leo.modules.HOLSignature.{=== => EQ, Not}
    if (l.equational) {
      if (l.polarity) EQ(l.left, l.right) else Not(EQ(l.left, l.right))
    } else {
      if (l.polarity) l.left else Not(l.left)
    }
  }
  /** Returns true iff the literal is trivially semantically equal to $true. */
  final def isTrue(l: Literal): Boolean = if (l.polarity) trivial(l) else distinctSides(l)
  /** Returns true iff the literal is trivially semantically equal to $false. */
  final def isFalse(l: Literal): Boolean = if (!l.polarity) trivial(l) else distinctSides(l)
  /** Returns a new literal with the same equation as this one, only with polarity flipped.
    * It is also oriented, if the original was oriented. */
  final def flipPolarity(l: Literal): Literal = if (l.equational) apply(l.left, l.right, !l.polarity, l.oriented) else apply(l.left, !l.polarity)
  /** Returns whether the literal is well-typed, i.e. if the underlying terms are well-typed and have the same type. */
  final def wellTyped(l: Literal): Boolean = {
    import leo.datastructures.Term.{wellTyped => wt}
    import leo.modules.HOLSignature.o
    if (l.equational) {
      wt(l.left) && wt(l.right) && l.left.ty == l.right.ty
    } else wt(l.left) && l.left.ty == o
  }
}

