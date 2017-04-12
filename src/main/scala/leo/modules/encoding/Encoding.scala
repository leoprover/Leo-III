package leo.modules.encoding

import leo.datastructures.Signature

/**
  * Facade for various [[leo.modules.encoding]] methods, including encoding from poly HOL,
  * mono HOL, from poly FOL, typed FOL to mono HOL, poly FOL, typed FOL
  * and untyped FOL (in combinations that are reasonable).
  *
  * @author Alexander Steen <a.steen@fu-berlinde>
  * @since March 2017
  */
object Encoding {
  type EncodingResult = (EncodedProblem, AuxiliaryFormulae, EncodingSignature)

  /** Encodes a polymorphic HOL/FOL problem into a corresponding
    * monomorphic HOL/FOL problem using heuristic monomorphization.
    *
    * Since the monomorphization process does not add artifial symbols etc.
    * there are no [[leo.modules.encoding.AuxiliaryFormulae]].
    *
    * @param problem The (possibly) polymorphic problem to be encoded
    * @param sig The signature under which `problem` is interpreted
    * @return A triple `(p',aux,s')` where
    *         - `p'` is the encoded monomorphic HOL/FOL problem
    *         - `aux` is the empty set
    *         - `s'` is a new signature under which `p'` is interpreted
    *
    * @see [[leo.modules.encoding.Monomorphization]] */
  final def mono(problem: Problem)
                            (implicit sig: Signature): EncodingResult = {
    val monoResult = Monomorphization(problem)(sig)
    (monoResult._1, Set.empty, monoResult._2)
  }

  //////////////////////////////////////////////
  // First-order translations
  //////////////////////////////////////////////

  /** HOL to poly typed/mono typed/untyped FOL */
  final def apply(problem: Problem, preplay: EncodingPreplay,
                     les: LambdaElimStrategy, typeEncoding: TypeEncoding)
                    (implicit sig: Signature): EncodingResult = {
    val (problem0, sig0) = applyPreplay(problem, preplay, sig)
    val (polyProblem, polyAux, polySig) = TypedFOLEncoding.apply(problem0, les)(sig0)
    val encoder = typeEncoding()
    encoder(polyProblem, polyAux)(polySig)
  }

  @inline private final def applyPreplay(problem: Problem, preplay: EncodingPreplay, sig: Signature): (Problem, Signature) = {
    if (preplay == EP_None) (problem, sig)
    else if (preplay == EP_Monomorphization) Monomorphization(problem)(sig)
    else throw new IllegalArgumentException
  }
}
