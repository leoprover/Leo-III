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

  override def equals(o: Any) = o match {
    case ot : Subst => {
      shiftedBy == ot.shiftedBy && fronts == ot.fronts
    }
    case _ => false
  }

//  /** Convecience method: Convert all fronts in substitution to terms (if not a type substitution) */
//  def terms: Seq[Term] = {
//    fronts.map {
//      case BoundFront(i) => Term.mkBound(???, i)
//      case TermFront(t) => t
//      case _ => throw new IllegalArgumentException("#terms called on type substitution.")
//    }
//  }
}

/** Generic factory methods for substitutions. Current default implementation are
  * `RASubst` substitutions which allow constant time access to fronts. */
object Subst {
  import leo.datastructures.{RASubst => SubstImpl}

  val id: Subst    = SubstImpl.id
  val shift: Subst = SubstImpl.shift
  def shift(n: Int): Subst = SubstImpl.shift(n)

  def singleton(what: Int, by: Term): Subst = {
    var i = 1
    var subst: Vector[Front] = Range(1, what).map(BoundFront(_)).toVector
    new SubstImpl(what,subst :+ TermFront(by))

//
//    var s = Subst.id
//    for(idx <- 1 to (what-1)) {
//      s = BoundFront(idx) +: s
//    }
//
//    TermFront(by) +: s
  }

  def fromMap(map: Map[Int, Term]): Subst = {
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

  def fromMaps(termMap: Map[Int, Term], boundMap: Map[Int, Int]): Subst = {
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

  def fromShiftingSeq(seq: Seq[(Int, Int)]): Subst = {
    val map : Map[Int, Int] = Map.apply(seq:_*)
    fromMaps(Map(), map)
  }

  def fromSeq(seq: Seq[(Int, Term)]): Subst = {
    val map : Map[Int, Term] = Map.apply(seq:_*)
    fromMap(map)
  }

  // legacy
  //  def consWithEta(ft: Front, onto: Subst): Subst = ft match {
  //    case tf@TermFront(t) => Cons(TermFront(t.weakEtaContract(Subst.id, 0)), onto)
  //    case a => Cons(a, onto)
  //  }
}

/////////////////////////////////////////////////
// Substitutions as random-access lists (vectors)
// Are more involved than algebraic representations
// but allow constant time access to fronts
/////////////////////////////////////////////////

/** Substitutions as constant-time accessible vectors */
sealed protected class RASubst(shift: Int, fts: Vector[Front] = Vector.empty) extends Subst {

  lazy val normalize: Subst = new RASubst(shift, fts.map({_ match {
    case TermFront(t) => TermFront(t.betaNormalize)
    case a => a
  } }))

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

  def cons(ft: Front): Subst = new RASubst(shift, ft +: fts)
  lazy val sink: Subst = BoundFront(1) +: (this o new RASubst(1))

  lazy val pretty: String = fts.isEmpty match {
    case true => shift match {
      case 0 => "id"
      case k => s"↑$k"
    }
    case false => fts.map(_.pretty).mkString("•") ++ s"↑$shift"
  }

  lazy val isShift = fts.isEmpty
  lazy val shiftedBy = shift
  lazy val length = fts.length
  def drop(n: Int): Subst = new RASubst(shift, fts.drop(n))

  def substBndIdx(i: Int) = fts.length >= i match {
    case true => fts(i-1)
    case false => BoundFront(i+shift-fts.length)
  }
  lazy val fronts = fts
}


/** Factory methods for `RASubst`. */
object RASubst {
  val id: Subst    = new RASubst(0)
  val shift: Subst = new RASubst(1)
  def shift(n: Int): Subst = new RASubst(n)
}



/////////////////////////////////////////////////
// Substitutions as lists
// Are more straight-forward but suffer from
// linear traversal of fronts
/////////////////////////////////////////////////

/** Substitutions as algebraic data types (lists). */
sealed abstract class AlgebraicSubst extends Subst {
  def sink: Subst = (this o Shift(1)).cons(BoundFront(1))
}

/** Factory methods for `AlgebraicSubst` */
object AlgebraicSubst {
  def id: Subst    = Shift(0)
  def shift: Subst = shift(1)
  def shift(n: Int): Subst = Shift(n)
}

// Implementation of substitutions as lists

case class Shift(n: Int) extends AlgebraicSubst {
  def comp(other: Subst) = n match {
    case 0 => other
    case _ => other match {
      case Shift(0) => this
      case Shift(m) => Shift(n+m)
      case Cons(ft, s) => Shift(n-1).comp(s)
    }
  }

  def cons(ft: Front) = Cons(ft, this)

  val normalize = this

  val fronts = Seq.empty
  def substBndIdx(i: Int) = BoundFront(i + n)
  def drop(n: Int) = throw new IllegalArgumentException("Shift substitution does not contain any fronts to drop")
  val length = 0

  val shiftedBy = n
  val isShift = true

  /** Pretty */
  override def pretty = n match {
    case 0 => "id"
    case k => s"↑$k"
  }
}


case class Cons(ft: Front, subst: Subst) extends AlgebraicSubst {
  def comp(other: Subst) = other match {
    case Shift(0) => this
    case s => Cons(ft.substitute(s), subst.comp(s))
  }

  def cons(ft: Front) = Cons(ft, this)

  lazy val normalize = ft match {
    case BoundFront(_) => Cons(ft, subst.normalize)
    case TermFront(t)  => Cons(TermFront(t.betaNormalize), subst.normalize) //TODO: eta contract here
    case TypeFront(_) => Cons(ft, subst.normalize)
  }

  lazy val fronts = ft +: subst.fronts
  def substBndIdx(i: Int) = i match {
    case 1 => ft
    case _ => subst.substBndIdx(i-1)
  }
  def drop(n: Int) = n match {
    case 0 => this
    case _ => subst.drop(n-1)
  }
  lazy val length = 1 + subst.length

  lazy val shiftedBy = subst.shiftedBy
  val isShift = false

  /** Pretty */
  override def pretty = s"${ft.pretty}•${subst.pretty}"
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
  def substitute(subst: Subst) = subst.substBndIdx(n)

  /** Pretty */
  override def pretty = s"$n"
}
case class TermFront(term: Term) extends Front {
  def substitute(subst: Subst) = TermFront(term.termClosure((subst)))

  /** Pretty */
  override def pretty = term.pretty
}
case class TypeFront(typ: Type) extends Front {
  def substitute(subst: Subst) = TypeFront(typ.closure((subst)))

  /** Pretty */
  override def pretty = typ.pretty
}