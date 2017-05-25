package leo.modules.calculus

import leo.datastructures.Term.{:::>, Bound, Symbol, TypeLambda, ∙}
import leo.datastructures._
import leo.modules.HOLSignature._
import leo.modules.SZSException
import leo.modules.output.{SZS_Error, SZS_Theorem, SuccessSZS}

import scala.collection.mutable

/**
  *
  * Rule extracting all arguments of a term
  * and introducing new definitions.
  *
  * This rule assumes a prenex form with implicit quantifiers.
  *
  */
object ArgumentExtraction extends CalculusRule {


  // TODO Make ArgumentExtraction to a class to avoid clashes in cashing
  private val cashExtracts : mutable.Map[Term, Term] = new mutable.HashMap[Term, Term]()
  def resetCash() : Unit = cashExtracts.clear()

  /**
    * The type of extracted arguments.
    */
  type ExtractionType = Int
  /**
    * Extract only arguments of type 'o'.
    */
  val BooleanType = 0
  /**
    * Extracts arguments with predicate type 'a >: o'.
    * The headsymbol has to be a system constant (&, |, =>, <=>, etc.)
    */
  val PredicateType = 1
  /**
    * Extract all arguments with function type 'a >: b'
    * if they are not just eta-long forms of constants / variables.
    */
  val FunctionType = 2

  override def name: String = "argument-extraction"

  /**
    * The argument extraction is a theorem of the initial
    * formula and the extracted definitions.
    * @return
    */
  override def inferenceStatus: SuccessSZS = SZS_Theorem

  /**
    * Tests the term 't' for any occurences of lambdas of a sequence
    * of locally bound variables.
    *
    * @param t the term to check
    * @param lambdas amount of lambas checked. n -> [1..n] for \.\. ... \. n times
    */
  private def containsLocalBounds(t : Term, lambdas : Int) : Boolean = {
    val vars = t.fv.iterator
    while(vars.hasNext){
      if(vars.next._1 <= lambdas) return true
    }
    false
  }

  /**
    * Tests if the term is either a variable
    * or a user defined symbol.
    *
    * @param t the head symbol of a term to check
    * @return true, if argument extraction can be applied to the term, which headsymbol this is.
    */
  private def isHeadApplicable(t : Term)(implicit sig : Signature) : Boolean = {
    if(t.isVariable) true
    else {
      t match {
        case Symbol(k) => sig.allUserConstants.contains(k)
        case _ => throw new SZSException(SZS_Error, s"The term is not in beta normalform. Headsymbol ${t.pretty(sig)} occured.")
      }
    }
  }

  /**
    * Tests the term t. An argument on a applicable position. If it can be considered for extraction.
    *
    * @param t The argument to look at
    * @param lambdas The amount of lambdas we are currently under
    * @param extType the type of extractable arguments.
    * @return true, iff the argument should be extracted.
    */
  private def shouldExtract(t : Term, lambdas : Int, extractLocal : Boolean, extType : ExtractionType)(implicit sig : Signature) : Boolean = {
    if(!extractLocal && containsLocalBounds(t, lambdas)) return false
    else {
      extType match {
        case BooleanType =>
          if(t.ty != o) return false
          t.headSymbol match {
            case hd@Symbol(k) => !sig.allUserConstants.contains(k) && !hd.ty.isBaseType
            case _ => false
          }
        case PredicateType =>
          if(t.ty.funParamTypesWithResultType.last != o) return false
          t.headSymbol match {
            case hd@Symbol(k) => !sig.allUserConstants.contains(k) && !hd.ty.isBaseType
            case _ => false
          }
        case FunctionType =>
          if(!t.ty.isFunType) {
            if(t.ty != o) return false
            t.headSymbol match {
              case hd@Symbol(k) => !sig.allUserConstants.contains(k) && !hd.ty.isBaseType
              case _ => false
            }
          } else {
            // TODO If in eta-contract there are still leading lambdas
            ???
          }
      }
    }
  }



  /**
    * Extracts all boolean arguments, applicable
    * for further handeling of calculus rules and
    * introduces defining literals for them.
    *
    * Example:
    * forall X. p(\Y.X /\ Y)
    * ->
    * (
    * forall X. p(f(X)),
    * Seq(
    * [f(X) = X /\ Y]t
    * )
    *
    * All terms with boolean / predicate / function type are extracted.
    * Clause free variables are considered arguments and outer variables prohibt
    * an extraction (since the paramodultion cannot instanciate otherwise)
    *
    * The replacement is shallow. If an argument is extracted, the extracted term is not iterated for further extraction.
    * The new introduced literals are
    * (a) to be processed by CNF
    * (b) to be processed by FuncExt
    *
    * @param t - The term, from which the arguments should be extracted. The term has to be in beta-normalform
    *          and for the best result it should be skolemized in prenex form with implicit bound variables
    * @return A tupel of the new term (with replaced arguments), and a sequence of Literal, representing the
    *         newly defined arguments.
    */
  def apply(t : Term, extractLocal : Boolean, extType : ExtractionType)
           (implicit sig : Signature) : (Term, Seq[Literal])
  = shallowApply(t, 0, extractLocal, extType)

  /**
    * Extracts all boolean arguments, applicable
    * for further handeling of calculus rules and
    * introduces defining literals for them.
    *
    * Example:
    * forall X. p(\Y.X /\ Y)
    * ->
    * (
    * forall X. p(f(X)),
    * Seq(
    * [f(X) = X /\ Y]t
    * )
    *
    * All terms with boolean / predicate / function type are extracted.
    * Clause free variables are considered arguments and outer variables prohibt
    * an extraction (since the paramodultion cannot instanciate otherwise)
    *
    * The replacement is shallow. If an argument is extracted, the extracted term is not iterated for further extraction.
    * The new introduced literals are
    * (a) to be processed by CNF
    * (b) to be processed by FuncExt
    *
    * @param l - The Literal, from which the arguments should be extracted. The term has to be in beta-normalform
    *          and for the best result it should be skolemized in prenex form with implicit bound variables
    * @return A tupel of the new term (with replaced arguments), and a sequence of Literal, representing the
    *         newly defined arguments.
    */
  def apply(l : Literal, extractLocal : Boolean, extType : ExtractionType)(implicit sig : Signature) : (Literal, Seq[Literal]) = {
    val (left, defsl) = shallowApply(l.left, 0, extractLocal, extType)
    val (right, defsr) = shallowApply(l.right, 0 , extractLocal, extType)
    (Literal(left, right, l.polarity), defsl ++ defsr)
  }

  /**
    * Extracts all boolean arguments, applicable
    * for further handeling of calculus rules and
    * introduces defining literals for them.
    *
    * Example:
    * forall X. p(\Y.X /\ Y)
    * ->
    * (
    * forall X. p(f(X)),
    * Seq(
    * [f(X) = X /\ Y]t
    * )
    *
    * All terms with boolean / predicate / function type are extracted.
    * Clause free variables are considered arguments and outer variables prohibt
    * an extraction (since the paramodultion cannot instanciate otherwise)
    *
    * The replacement is shallow. If an argument is extracted, the extracted term is not iterated for further extraction.
    * The new introduced literals are
    * (a) to be processed by CNF
    * (b) to be processed by FuncExt
    *
    * @param c - The Literal, from which the arguments should be extracted. The term has to be in beta-normalform
    *          and for the best result it should be skolemized in prenex form with implicit bound variables
    * @return A tupel of the new term (with replaced arguments), and a sequence of Literal, representing the
    *         newly defined arguments.
    */
  def apply(c : Clause, extractLocal : Boolean, extType : ExtractionType)(implicit sig : Signature) : (Clause, Seq[Literal]) = {
    val lits = c.lits.iterator
    var ergLits : Seq[Literal] = Seq()
    var defs : Seq[Literal] = Seq()
    while(lits.hasNext){
      val l = lits.next()
      val (newL, newDefs) = apply(l, extractLocal, extType)
      ergLits = newL +: ergLits
      defs = newDefs ++ defs
    }
    (Clause(ergLits), defs)
  }

  private def shallowApply(t : Term, lambdas : Int,
                           extractLocal : Boolean,
                           extType : ExtractionType)
                          (implicit sig : Signature) : (Term, Seq[Literal]) = t match {
    case s@Symbol(_)            => (s, Seq())
    case s@Bound(_,_)           => (s, Seq())
    case f ∙ args  =>
//      println(s"Called @ ${t.pretty(sig)}")
      var defs : Seq[Literal] = Seq()
      val it = args.iterator
      var args1 : Seq[Either[Term, Type]] = Seq()
      val fapplicable = isHeadApplicable(f)
      while(it.hasNext){
        it.next() match {
          case Left(lt) if fapplicable & shouldExtract(lt, lambdas, extractLocal, extType) =>
//            println(s" + Arg Was term @ ${lt.pretty(sig)} and applicable")
            if(cashExtracts.contains(lt)){
              args1 = Left(cashExtracts(lt)) +: args1
            } else {
              val freevars = lt.freeVars.toSeq // X
              val c_ty = Type.mkFunType(freevars.map(_.ty), lt.ty) // ty(X1) -> ... -> ty(Xn) -> ty(l)
              // TODO other nameing?
              // TODO Cashing for terms?
              val c_def = Term.mkAtom(sig.freshSkolemConst(c_ty), c_ty) // c
              val newT: Term = Term.mkTermApp(c_def, freevars) // c X
              defs = Literal(newT, lt, true) +: defs // c X = lt
              cashExtracts.put(lt, newT)
              args1 = Left(newT) +: args1
            }
          case Left(lt) =>
//            println(s"   Arg was term @ ${lt.pretty(sig)} but not applicable (headApplicable ${fapplicable})")
            val (t1, defs1) = shallowApply(lt, lambdas, extractLocal, extType)
            args1 = Left(t1) +: args1
            defs = defs1 ++ defs
          case Right(rt) =>
//            println(s"   Arg was type @ ${rt.pretty(sig)} hence dropped")
            args1 = Right(rt) +: args1
        }
      }
      (Term.mkApp(f, args1.reverse), defs)
    case ty :::> s  =>
      val (s1, defs) = shallowApply(s, lambdas + 1,extractLocal, extType)
      (Term.mkTermAbs(ty, s1), defs)
    case TypeLambda(s) =>
      val (s1, defs) = shallowApply(s, lambdas + 1, extractLocal, extType)
      (Term.mkTypeAbs(s1), defs)
  }
}
