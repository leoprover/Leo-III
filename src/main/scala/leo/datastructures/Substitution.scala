package leo.datastructures

/**
 * Representation of substitution `s` that are basically
 * linear lists of terms or types or shifts.
 *
 * @author Alexander Steen
 * @since 06.08.2014
 *
 * @note Updated 03.11.2014: Implemented random-access substitutions, added documentation,
 *                           added factory-like pattern for different substitution implementations.
 */
sealed abstract class Subst extends Pretty {
  /** s.comp(s') = t
    * where t = s o s' */
  def comp(other: Subst): Subst
  def o = comp(_)

  /** If this is an substitution for terms, apply element-wise type substitution `typeSubst` to it, error otherwise. */
  def applyTypeSubst(typeSubst: Subst): Subst

  /** Prepend `ft` to this substitution */
  def cons(ft: Front): Subst
  /** Prepend `ft` to this substitution */
  def +:(ft: Front): Subst = this.cons(ft)

  /** Sink substitution inside lambda abstraction, i.e. create 1.s o ↑*/
  def sink: Subst

  /** Beta-normalize every term in this substitution */
  def normalize: Subst

  /** Returns true iff the substitution is of the form ↑^k^ for some k*/
  def isShift: Boolean
  /** Returns true iff the substitution is of form `a.s` for some front `a` and some substitution `s`*/
  def isConsd: Boolean = !isShift

  /** Returns value k for this substitution `this = a_1.a_2....a_n.↑^k`*/
  def shiftedBy: Int

  /** Returns the length of the cons'd fronts, i.e. value `n` for `this = a_1.a_2...a_n.↑^k` */
  def length: Int
  /** Drops the first n fronts of this substitution */
  def drop(n: Int): Subst

  /** Returns the front that results from substitution de-Bruijn index `i` with the underlying substitution */
  def substBndIdx(i: Int): Front
  /** Return all fronts as linear list */
  def fronts: Seq[Front]

  /** The domain of the substitution, i.e. the set `{x | xσ != σ}` of variables
    * not mapped to themselves.*/
  def domain: Set[Int]

  def restrict(domainPred: Int => Boolean): Subst

  override def equals(o: Any): Boolean = o match {
    case ot: Subst => shiftedBy == ot.shiftedBy && fronts == ot.fronts
    case _ => false
  }
}

/** Generic factory methods for substitutions. Current default implementation are
  * `RASubst` substitutions which allow constant time access to fronts. */
object Subst {
  import leo.datastructures.{RASubst => SubstImpl}

  final def id: Subst    = SubstImpl.id
  final def shift: Subst = SubstImpl.shift
  final def shift(n: Int): Subst = SubstImpl.shift(n)

  final def singleton(what: Int, by: Term): Subst = {
    val subst: Vector[Front] = Range(1, what).map(BoundFront).toVector
    new SubstImpl(what,subst :+ TermFront(by))
  }

  final def singleton(what: Int, by: Type): Subst = {
    val subst: Vector[Front] = Range(1, what).map(BoundFront).toVector
    new SubstImpl(what,subst :+ TypeFront(by))
  }

  final def fromMap(map: Map[Int, Term]): Subst = {
    if (map.isEmpty) {
      Subst.id
    } else {
      var subst: Vector[Front] = Vector()
      val maxIndex = map.keySet.max
      var i = 1
      while (i <= maxIndex) {
        subst = subst :+ map.get(i).fold(BoundFront(i):Front)(TermFront(_))
        i = i + 1
      }
      new SubstImpl(maxIndex, subst)
    }
  }

  final def fromMaps(termMap: Map[Int, Term], boundMap: Map[Int, Int]): Subst = {
    if (termMap.isEmpty && boundMap.isEmpty) {
      Subst.id
    } else {
      var subst: Vector[Front] = Vector()
      val maxIndex = (termMap.keySet ++ boundMap.keySet).max
      var i = 1
      while (i <= maxIndex) {
        if (termMap.isDefinedAt(i)) {
          subst = subst :+ TermFront(termMap(i))
        } else if (boundMap.isDefinedAt(i)) {
          subst = subst :+ BoundFront(boundMap(i))
        } else {
          subst = subst :+ BoundFront(i)
        }

        i = i + 1
      }
      new SubstImpl(maxIndex, subst)
    }
  }

  final def fromShiftingSeq(seq: Seq[(Int, Int)]): Subst = {
    val map : Map[Int, Int] = Map.apply(seq:_*)
    fromMaps(Map(), map)
  }

  final def fromSeq(seq: Seq[(Int, Term)]): Subst = {
    val map : Map[Int, Term] = Map.apply(seq:_*)
    fromMap(map)
  }
}

/////////////////////////////////////////////////
// Substitutions as random-access lists (vectors)
// Are more involved than algebraic representations
// but allow constant time access to fronts
/////////////////////////////////////////////////

/** Substitutions as constant-time accessible vectors */
sealed protected class RASubst(shift: Int, fts: Vector[Front] = Vector.empty) extends Subst {

  final def normalize: Subst = {
    var i = fts.size-1
    var newFts: Vector[Front] = Vector.empty
    var ftsDropped = 0; var finishedDrop = false
    while(i >= 0 && !finishedDrop) {
      val ft = fts(i)
      ft match {
        case BoundFront(idx) if idx == i+1 => ftsDropped = ftsDropped+1
          i = i-1
        case _ => finishedDrop = true
      }
    }
    while(i >= 0) {
      val ft = fts(i)
      ft match {
        case TermFront(t) => newFts = TermFront(t.betaNormalize) +: newFts
        case x => newFts = x +: newFts
      }
      i = i-1
    }
    new RASubst(shift-ftsDropped, newFts)
  }

  def comp(other: Subst): Subst = other.isShift match {
    case true if other.shiftedBy == 0 => this
    case true if this.isShift => new RASubst(shift + other.shiftedBy)
    case true => new RASubst(shift + other.shiftedBy, fts.map(_.substitute(other)))
    case _ if this.isShift && this.shiftedBy == 0 => other
    case _ => (shift - other.length) match {
      case n if n >= 0 => new RASubst(n+other.shiftedBy, fts.map(_.substitute(other)))
      case _ => new RASubst(other.shiftedBy, fts.map(_.substitute(other)) ++ other.fronts.drop(shift))
    }
  }

  final def applyTypeSubst(typeSubst: Subst): Subst = new RASubst(shift, fts.map {
    case TermFront(term) => TermFront(term.betaNormalize.typeSubst(typeSubst))
    case TypeFront(_) => throw new UnsupportedOperationException("applyTypeSubst on typeSubst")
    case other => other
  })

  final def cons(ft: Front): Subst = new RASubst(shift, ft +: fts)
  final def sink: Subst = BoundFront(1) +: (this o new RASubst(1))

  final def pretty: String = if (fts.isEmpty) {
    shift match {
      case 0 => "id"
      case k => s"↑$k"
    }
  } else fts.map(_.pretty).mkString("•") ++ s"↑$shift"

  final def isShift = fts.isEmpty
  final def shiftedBy = shift
  final def length = fts.length
  final def drop(n: Int): Subst = new RASubst(shift, fts.drop(n))

  final def substBndIdx(i: Int) = if (fts.length >= i) fts(i-1)
  else BoundFront(i+shift-fts.length)
  final def fronts = fts

  def domain: Set[Int] = {
    var result: Set[Int] = Set.empty
    var idx = 0
    while (idx < fts.size) {
      val entry = fts(idx)
      entry match {
        case TermFront(body) => result += idx+1 // FIXME: Check if term equals idx as variable?
        case TypeFront(body) => result += idx+1
        case BoundFront(scope) =>
          if (scope != idx+1) result += idx+1
      }
      idx = idx + 1
    }
    result
  }

  def restrict(domainPred: Int => Boolean): Subst = {
    var newFts: Vector[Front] = Vector.empty
    var i = 0
    while (i < fts.size) {
      if (domainPred(i+1)) {
        newFts = newFts :+ fts(i)
      } else {
        newFts = newFts :+ BoundFront(i+1)
      }
      i = i+1
    }
    new RASubst(shift, newFts)
  }
}

/** Factory methods for `RASubst`. */
object RASubst {
  final val id: Subst    = shift(0)
  final val shift: Subst = shift(1)
  final def shift(n: Int): Subst = new RASubst(n)
}


///////////////////////////////////////
// Fronts
////////////////////////////////////////

/** Fronts are the elements of substitutions that can be cons'd to them.
  * They may be either terms, types or bound variables (de-Bruijn indices). */
sealed abstract class Front extends Pretty {
  def substitute(subst: Subst): Front
}
case class BoundFront(n: Int) extends Front {
  def substitute(subst: Subst): Front = subst.substBndIdx(n)

  /** Pretty */
  final def pretty: String = s"$n"
}
case class TermFront(term: Term) extends Front {
  def substitute(subst: Subst) = TermFront(term.termClosure(subst))

  /** Pretty */
  final def pretty: String = term.pretty
}
case class TypeFront(typ: Type) extends Front {
  def substitute(subst: Subst) = TypeFront(typ.closure(subst))

  /** Pretty */
  final def pretty: String = typ.pretty
}