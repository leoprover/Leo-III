package leo.modules.encoding

import leo.datastructures.Signature
/**
  * Created by lex on 21.02.17.
  */
abstract sealed class TypeEncoding extends Function0[TypeEncoder]
case object PolyGuards extends TypeEncoding {
  final def apply(): TypeEncoder = ???
}
case object MonoGuards extends TypeEncoding {
  final def apply(): TypeEncoder = ???
}
case object MonoGuardsLight extends TypeEncoding {
  final def apply(): TypeEncoder = ???
}
case object PolyTags extends TypeEncoding {
  final def apply(): TypeEncoder = ???
}
case object MonoTags extends TypeEncoding {
  final def apply(): TypeEncoder = ???
}
case object MonoTagsLight extends TypeEncoding {
  final def apply(): TypeEncoder = ???
}
case object MonoNative extends TypeEncoding {
  final def apply(): TypeEncoder = MonoNativeEncoder
}
case object PolyNative extends TypeEncoding {
  final def apply(): TypeEncoder = PolyNativeEncoder
}

trait TypeEncoder {
  type EncodingResult = (EncodedProblem, AuxiliaryFormulae, EncodingSignature)

  def apply(problem: Problem, auxiliaryFormulae: AuxiliaryFormulae)
           (implicit sig: Signature): EncodingResult
}

/////////////////////////
// Simple Encoder
/////////////////////////

protected[encoding] object MonoNativeEncoder extends TypeEncoder {
  final def apply(problem: Problem, auxiliaryFormulae: AuxiliaryFormulae)
           (implicit sig: Signature): EncodingResult = {
    Encoding.mono(problem union auxiliaryFormulae)(sig)
  }
}

protected[encoding] object PolyNativeEncoder extends TypeEncoder {
  final def apply(problem: Problem, auxiliaryFormulae: AuxiliaryFormulae)
                 (implicit sig: Signature): EncodingResult = {
    (problem, auxiliaryFormulae, sig)
  }
}
