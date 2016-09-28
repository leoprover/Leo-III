package leo.modules.preprocessing

import leo.datastructures.Term._
import leo.datastructures._
import leo.datastructures.impl.Signature
import leo.modules.calculus.CalculusRule

import scala.collection.mutable


/**
  * Will perform a formula renaming (See Nonnengard 'Small Clause Normalforms')
  * as a preprocessing step.
  *
  * In formulas a subterm is replaced by a definition, if the resulting clauses yield a smaller clause normal form.
  *
  * Needs as preconditions:
  *  - Simplification
  *  - DefinitionExpantion
  *  - Extensionality
  */
object FormulaRenaming extends CalculusRule{
  override val name : String = "formula_renaming"
  /**
    * Stores a mapping for the unit equations to use the same descriptor
    * for equal definitions.
    */
  private var us: mutable.Map[Term, Term] = mutable.Map()

  // TODO open for parallel execution
  def setUnitStore(us: mutable.Map[Term, Term]): Unit = {
    this.us = us
  }

  def clearUnitStore(): Unit = {
    us.clear()
  }

  /**
    *
    * Replaces a subterm in a formula by a definition.
    * This happens if the resulting clause normal form would yield
    * less clauses, than the original
    *
    * @param c       The clause to normalize
    * @param delta    A difference in generated clauses, compared to the original.
    * @return
    */
  def apply(c: Clause, delta: Int = 1): (Clause, Seq[Clause]) = {
    val lits = c.lits
    val apps = lits.map(f => apply(f,delta))
    val nlits = apps.map(_._1)
    val cs = apps.flatMap(_._2)
    (Clause(nlits), cs)
  }

  def applyConjecture(c : Clause, delta: Int = 1) : (Clause, Seq[Clause]) = {
    assert(c.lits.size == 1, "The conjecture should contain exactly one clause.")
    if(c.lits.size > 1) return (c, Seq())
    val lit = c.lits.head
    val (nLit, units) = apply(Literal.flipPolarity(lit), delta)
    (Clause(Literal.flipPolarity(nLit)), units)
  }


  /**
    *
    * Replaces a subterm in a literal by a definition.
    * This happens if the resulting clause normal form would yield
    * less clauses, than the original
    *
    * @param l       The literal to normalize
    * @param delta    A difference in generated clauses, compared to the original.
    * @return
    */
  def apply(l: Literal, delta: Int): (Literal, Seq[Clause]) = {

    if(l.right == LitTrue() || l.right == LitFalse()) {
      val pol = if (l.polarity) 1 else -1
      val (t, c) = apply(l.left, pol, delta)
      (l.leftTermMap(_ => t), c)
    } else
      (l, Seq())
  }


  def apply(t: Term, polarity: Int, delta: Int): (Term, Seq[Clause]) = t match {
    case |||(l, r) =>
      val (l_rename, c1s) = apply(l, polarity, delta)
      val (r_rename, c2s) = apply(r, polarity, delta)
      // Bottom-up replacement


      var origin_size: Int = 0
      if (polarity > 0) {
        val origin_l: Int = cnf_size(l_rename, true)
        val origin_r: Int = cnf_size(r_rename, true)
        origin_size = origin_l * origin_r

        if (origin_size > origin_l + 1 + delta + origin_r) {
          // Replace left if it will get smaller
          val (new_def, impl) = introduce_definition(r_rename, polarity == 1)
          return (|||(l_rename, new_def), impl +: (c1s ++ c2s))
        }
      }
      (|||(l_rename, r_rename), c1s ++ c2s)
    case &(l, r) =>
      val (l_rename, c1s) = apply(l, polarity, delta)
      val (r_rename, c2s) = apply(r, polarity, delta)

      if (polarity < 0) {
        val origin_l: Int = cnf_size(l_rename, false)
        val origin_r: Int = cnf_size(r_rename, false)
        val origin_size: Int = origin_l * origin_r

        if (origin_size > origin_l + delta + 1 + origin_r) {
          // Replace left if it will get smaller
          val (new_def, impl) = introduce_definition(r_rename, polarity == 1)
          return (&(l_rename, new_def), impl +: (c1s ++ c2s))
        }
      }
      (&(l_rename, r_rename), c1s ++ c2s)
    case Not(t1) =>
      val (t2, c) = apply(t1, -1 * polarity, delta)
      (Not(t2), c)
    case Forall(ty :::> t1) =>
      val (t2, c) = apply(t1, polarity, delta)
      (Forall(\(ty) (t2)), c)
    case Exists(ty :::> t1) =>
      val (t2, c) = apply(t1, polarity, delta)
      (Exists(\(ty) (t2)), c)

    // Pass through unimportant structures
    case s@Symbol(_) => (s, Seq())
    case s@Bound(_, _) => (s, Seq())
    case s@(f ∙ args) => (s, Seq())
    case s@(ty :::> t) => (s, Seq())
    case s@(TypeLambda(t)) => (s, Seq())
    //    case _  => formula
  }

  //TODO Alles nochmal überprüfen
  protected[preprocessing] def cnf_size(t: Term, pol: Boolean): Int = t match {
    case |||(l, r) => if (pol) cnf_size(l, pol) * cnf_size(r, pol) else cnf_size(l, pol) + cnf_size(r, pol)
    case &(l, r) => if (pol) cnf_size(l, pol) + cnf_size(r, pol) else cnf_size(l, pol) * cnf_size(r, pol)
    case Not(t1) => cnf_size(t1, !pol)
    case Forall(ty :::> t1) => cnf_size(t1, pol)
    case Exists(ty :::> t1) => cnf_size(t1, pol)

    // Pass through unimportant structures
    case s@Symbol(_) => 1
    case s@Bound(_, _) => 1
    case f ∙ args => 1
    case ty :::> s => 1
    case TypeLambda(t) => 1
    //    case _  => formula
  }

  protected[preprocessing] def introduce_definition(t: Term, pol: Boolean): (Term, Clause) = {
    val definition =
      if (us.contains(t))
        us.get(t).get
      else {
        val s = Signature.get
        // Term was no yet introduced
        val newArgs = t.freeVars.toSeq // Arguments passed to the function to define
        val argtypes = newArgs.map(_.ty)

        val c = s.freshSkolemConst(Type.mkFunType(argtypes, t.ty)) // TODO other name (extra function in Signature)
        val ct = Term.mkTermApp(Term.mkAtom(c), newArgs).betaNormalize
        us.put(t, ct)
        ct
      }

    val impl = Clause(Literal(if (pol) Impl(definition, t) else Impl(t, definition), true))
    (definition, impl)
  }

}
