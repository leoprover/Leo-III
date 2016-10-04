package leo.modules.preprocessing

import leo.datastructures.Term._
import leo.datastructures.impl.Signature
import leo.datastructures._
import leo.modules.calculus.CalculusRule
import scala.collection.JavaConverters._
import scala.collection.immutable.Set
import scala.collection.mutable


object ArgumentExtraction extends ArgumentExtraction(_ => true)

/**
  * Extracts Arguments of functions and introduces
  * descriptions for the new introduced term.
  *
  * @param filter Defines a filter on the arguments, that can be extracted.
  */
class ArgumentExtraction(filter : Term => Boolean) extends Function1[Clause, (Clause, Set[(Term, Term)])] with CalculusRule{

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
  def apply(c : Clause) : (Clause, Set[(Term, Term)]) = {
    val (lits, rewrites) : (Seq[Literal], Seq[Set[(Term, Term)]]) = c.map(apply(_)).unzip
    (Clause(lits), rewrites.flatten.toSet)
  }

  def apply(l : Literal) : (Literal, Set[(Term, Term)]) = {
    val (lt, unitsL) = apply(l.left)
    val (rt, unitsR) = apply(l.right)
    (l.termMap{ (_,_) => (lt,rt)}, unitsL union unitsR)
  }

  def apply(t : Term) : (Term, Set[(Term,Term)]) = {
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
        if (isUser(k)) {
          handleApp(h, args)
        } else {
          val mapRes = args.map(_.fold({
            at =>
              val (t, units) = apply(at)
              (Left(t), units)
          }
            , aty => (Right(aty), Set[(Term, Term)]())))
          (Term.mkApp(h, mapRes.map(_._1)), mapRes.flatMap(_._2).toSet)
        }
      case (h@Bound(_, _)) ∙ args => handleApp(h, args)
      case ty :::> s =>
        val (t1, units) = apply(s)
        (Term.mkTermAbs(ty, t1), units)
      case TypeLambda(t) =>
        val (t1, units) = apply(t)
        (Term.mkTypeAbs(t1), units)
    }
  }

  private def handleApp(h : Term, args : Seq[Either[Term,Type]]) : (Term, Set[(Term, Term)]) = {
    val mapRes : Seq[(Either[Term,Type], Set[(Term, Term)])] = args.map{a =>
      a.fold(at =>
        extractOrRekurse(at), aty => (Right(aty), Set[(Term,Term)]()))}
    val units = mapRes.flatMap(_._2)
    val args1 = mapRes.map(_._1)
    (Term.mkApp(h,args1), units.toSet)
  }

  private def extractOrRekurse(t : Term) : (Either[Term, Type], Set[(Term, Term)]) = {
    val s  = Signature.get
    if(us.contains(t)){
      return (Left(us.get(t).get), Set())
    }
    if(shouldExtract(t) && filter(t)) {

      val newArgs = t.freeVars.toSeq  // Arguments passed to the function to define
      val argtypes = newArgs.map(_.ty)

      val c = s.freshSkolemConst(Type.mkFunType(argtypes, t.ty)) // TODO other name (extra function in Signature)
      val ct = Term.mkTermApp(Term.mkAtom(c), newArgs).betaNormalize       // Head symbol + variables
      us.put(t, ct)
      (Left(ct), Set((t, ct)))
    } else {
      val (t1, units) = apply(t)
      (Left(t1), units)
    }
  }

  private def isUser(k : Signature#Key) : Boolean = {
    val s = Signature.get
    s.allUserConstants.contains(k)
  }

  private def shouldExtract(t : Term) : Boolean = {
    val s = Signature.get
    if(t.ty.funParamTypesWithResultType.last != s.o) return false

    if(t.isConstant) return false

    t.headSymbol match {
      case Symbol(k)    => !s.allUserConstants.contains(k)    // Extract if it is no user constant (can be treated in CNF)
      //case Bound(_, i)  => t.headSymbolDepth < i              // If it is a meta variable, it should be extracted TODO Fix the symbol depth (it treats type-lambdas as wll here)
      case _            => false                              // all other cases will generate non-treatable clauses.
    }
  }
}
