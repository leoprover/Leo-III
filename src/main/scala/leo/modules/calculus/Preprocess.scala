package leo.modules.calculus


import leo.datastructures._
import leo.datastructures.ClauseAnnotation.{InferredFrom, NoAnnotation}
import leo.datastructures.Term._
import leo.modules.output.{SZS_Theorem, SuccessSZS}


/**
  * Applies a preprocessing exhaustively to all clauses.
  *
  * @author Max Wisniewski
  * @since 6/5/16
  */
object Preprocess {


  def argumentExtraction(cs : Set[AnnotatedClause])(implicit sig: Signature) : Set[AnnotatedClause] = {
    var open : Set[AnnotatedClause] = cs
    var ready : Set[AnnotatedClause] = Set()
    while(open.nonEmpty) {
      val it = open.iterator
      open = Set()
      while(it.hasNext){
        val c = it.next()
        val (cl1, terms) = ArgumentExtraction(c.cl)(sig)
        val cs : Set[AnnotatedClause] = terms.map { case (l, r) => AnnotatedClause(Clause(Literal(l, r, true)), Role_Plain, NoAnnotation, ClauseAnnotation.PropNoProp) }
        open = cs ++ open
        val c2 = if (terms.nonEmpty) AnnotatedClause(cl1, c.role, InferredFrom(ExtractedArgument, cs+c), ClauseAnnotation.PropNoProp) else c
        ready += c2
      }
    }
    ready
  }

  def formulaRenaming(cs : Set[AnnotatedClause])(implicit sig: Signature) : Set[AnnotatedClause] = {
    cs.flatMap{c =>
      val (c1, c1s) = FormulaRenaming(c.cl)(sig)
      val c2s : Set[AnnotatedClause] = (c1s map {c2 => AnnotatedClause(c2, Role_Plain, NoAnnotation, ClauseAnnotation.PropNoProp)}).toSet
      val c2 = if(c1s.nonEmpty) AnnotatedClause(c1, c.role, InferredFrom(FormulaRenamed, c2s+c), ClauseAnnotation.PropNoProp) else c
      c2s + c2
    }
  }


  def equalityExtraction(cs : Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = {
    cs map {c =>
      val (b1, subst1) = ReplaceAndrewsEq.canApply(c.cl)
      val cl1 = if(b1) ReplaceAndrewsEq(c.cl, subst1)._1 else c.cl
      val (b2, subst2) = ReplaceLeibnizEq.canApply(cl1)(sig)
      val cl2 = if(b2) ReplaceLeibnizEq(cl1, subst2)._1 else cl1
      if (b1 || b2)
        AnnotatedClause(cl2, c.role, InferredFrom(ReplaceEQ,c), ClauseAnnotation.PropNoProp)
      else
        c
    }
  }
  private object ReplaceEQ extends CalculusRule {
    override val name : String = "replace_equality"
    override val inferenceStatus : Option[SuccessSZS] = Some(SZS_Theorem)
  }
  private object ExtractedArgument extends CalculusRule {
    override val name : String = "argument_extraction"
    override val inferenceStatus : Option[SuccessSZS] = Some(SZS_Theorem)
  }
  private object FormulaRenamed extends CalculusRule {
    override val name : String = "formula_renamed"
    override val inferenceStatus : Option[SuccessSZS] = Some(SZS_Theorem)
  }
}

object ArgumentExtraction extends ArgumentExtraction(_ => true)

/**
  * Extracts Arguments of functions and introduces
  * descriptions for the new introduced term.
  *
  * @param filter Defines a filter on the arguments, that can be extracted.
  */
class ArgumentExtraction(filter : Term => Boolean) extends CalculusRule{
  import scala.collection.mutable
  override val name : String = "argument_extraction"

  /**
    * Stores a mapping for the unit equations to use the same descriptor
    * for equal definitions.
    */
  private var us : mutable.Map[Term, Term] = mutable.Map()  // TODO open for parallel execution
  def setUnitStore(us : mutable.Map[Term, Term]) : Unit = {
    this.us = us
  }
  def clearUnitStore() : Unit = {
    us.clear()
  }

  /**
    * <p>
    * Extracts in a clause `c` all arguments of type `o` of a function symbol
    * and introduces unit equations to maintain the state of the symbols.
    * </p>
    *
    * <p>
    * The extracted unit equations are not themselves extracted and can be considered
    * for further simplification
    * </p>
    *
    * @param c The clause to be extracted
    * @return The clause with extracted arguments and a Map of unit equations
    */
  def apply(c : Clause)(implicit sig: Signature) : (Clause, Set[(Term, Term)]) = {
    val (lits, rewrites) : (Seq[Literal], Seq[Set[(Term, Term)]]) = c.map(apply(_)).unzip
    (Clause(lits), rewrites.flatten.toSet)
  }

  def apply(l : Literal)(implicit sig: Signature) : (Literal, Set[(Term, Term)]) = {
    val (lt, unitsL) = apply(l.left)
    val (rt, unitsR) = apply(l.right)
    (l.termMap{ (_,_) => (lt,rt)}, unitsL union unitsR)
  }

  def apply(t : Term)(implicit sig: Signature) : (Term, Set[(Term,Term)]) = {
    t match {
      case s@Symbol(_) => (s, Set())
      case s@Bound(_, _) => (s, Set())
      //      case ===(l,r) if l.ty != Signature.get.o  =>
      //        val (l1, units1) = extractOrRekurse(l)
      //        val (r1, units2) = extractOrRekurse(r)
      //        (===(l1.fold(t => t, _ => l), r1.fold(t => t, _ => r)), units1 union units2)
      //      case !===(l,r) if l.ty != Signature.get.o =>
      //        val (t1, units) = apply(===(l,r))
      //        (Not(t1),units)
      case (h@Symbol(k)) ∙ args =>
        if (isUser(k)(sig)) {
          handleApp(h, args)(sig)
        } else {
          val mapRes = args.map(_.fold({
            at =>
              val (t, units) = apply(at)
              (Left(t), units)
          }
            , aty => (Right(aty), Set[(Term, Term)]())))
          (Term.mkApp(h, mapRes.map(_._1)), mapRes.flatMap(_._2).toSet)
        }
      case (h@Bound(_, _)) ∙ args => handleApp(h, args)(sig)
      case ty :::> s =>
        val (t1, units) = apply(s)
        (Term.mkTermAbs(ty, t1), units)
      case TypeLambda(t) =>
        val (t1, units) = apply(t)
        (Term.mkTypeAbs(t1), units)
    }
  }

  private def handleApp(h : Term, args : Seq[Either[Term,Type]])(sig: Signature) : (Term, Set[(Term, Term)]) = {
    val mapRes : Seq[(Either[Term,Type], Set[(Term, Term)])] = args.map{a =>
      a.fold(at =>
        extractOrRekurse(at)(sig), aty => (Right(aty), Set[(Term,Term)]()))}
    val units = mapRes.flatMap(_._2)
    val args1 = mapRes.map(_._1)
    (Term.mkApp(h,args1), units.toSet)
  }

  private def extractOrRekurse(t : Term)(s: Signature) : (Either[Term, Type], Set[(Term, Term)]) = {
    if(us.contains(t)){
      return (Left(us.get(t).get), Set())
    }
    if(shouldExtract(t)(s) && filter(t)) {

      val newArgs = t.freeVars.toSeq  // Arguments passed to the function to define
      val argtypes = newArgs.map(_.ty)

      val c = s.freshSkolemConst(Type.mkFunType(argtypes, t.ty)) // TODO other name (extra function in Signature)
      val ct = Term.mkTermApp(Term.mkAtom(c)(s), newArgs).betaNormalize       // Head symbol + variables
      us.put(t, ct)
      (Left(ct), Set((t, ct)))
    } else {
      val (t1, units) = apply(t)(s)
      (Left(t1), units)
    }
  }

  private final def isUser(k : Signature#Key)(s: Signature) : Boolean = {
    s.allUserConstants.contains(k)
  }

  private def shouldExtract(t : Term)(s: Signature) : Boolean = {
    import leo.modules.HOLSignature.o
    if(t.ty.funParamTypesWithResultType.last != o) return false

    if(t.isConstant) return false

    t.headSymbol match {
      case Symbol(k)    => !s.allUserConstants.contains(k)    // Extract if it is no user constant (can be treated in CNF)
      //case Bound(_, i)  => t.headSymbolDepth < i              // If it is a meta variable, it should be extracted TODO Fix the symbol depth (it treats type-lambdas as wll here)
      case _            => false                              // all other cases will generate non-treatable clauses.
    }
  }
}

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
  import scala.collection.mutable
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
  def apply(c: Clause, delta: Int = 1)(implicit sig: Signature): (Clause, Seq[Clause]) = {
    val lits = c.lits
    val apps = lits.map(f => apply(f,delta))
    val nlits = apps.map(_._1)
    val cs = apps.flatMap(_._2)
    (Clause(nlits), cs)
  }

  def applyConjecture(c : Clause, delta: Int = 1)(implicit sig: Signature) : (Clause, Seq[Clause]) = {
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
  def apply(l: Literal, delta: Int)(implicit sig: Signature): (Literal, Seq[Clause]) = {
    import leo.modules.HOLSignature.{LitTrue, LitFalse}
    if(l.right == LitTrue() || l.right == LitFalse()) {
      val pol = if (l.polarity) 1 else -1
      val (t, c) = apply(l.left, pol, delta)
      (l.leftTermMap(_ => t), c)
    } else
      (l, Seq())
  }

  import leo.modules.HOLSignature.{|||, &, Not, Forall, Exists, Impl}
  def apply(t: Term, polarity: Int, delta: Int)(implicit sig: Signature): (Term, Seq[Clause]) = t match {
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
  protected[calculus] def cnf_size(t: Term, pol: Boolean): Int = t match {
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

  protected[calculus] def introduce_definition(t: Term, pol: Boolean)(implicit s: Signature): (Term, Clause) = {
    val definition =
      if (us.contains(t))
        us.get(t).get
      else {
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