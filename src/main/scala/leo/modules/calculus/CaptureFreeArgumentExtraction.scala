package leo.modules.calculus

import leo.datastructures.{Literal, Term}
import leo.modules.output.{SZS_Theorem, SuccessSZS}

/**
  *
  * Rule extracting all arguments of a term
  * and introducing new definitions.
  *
  */
object CaptureFreeArgumentExtraction extends CalculusRule {

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
  override def inferenceStatus: Option[SuccessSZS] = Some(SZS_Theorem)

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
    * TODO FUnctional extensionaliy immediatly?
    *
    * @param t - The term, from which the arguments should be extracted
    * @param polarity - The polarity under which the term is looked at
    * @return A tupel of the new term (with replaced arguments), and a sequence of Literal, representing the
    *         newly defined arguments.
    */
  def apply(t : Term, polarity : Boolean, extType : ExtractionType) : (Term, Seq[Literal]) = {
    ???
  }
}
