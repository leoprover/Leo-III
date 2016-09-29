package leo.modules.calculus.enumeration

import leo.datastructures.{Term, Type}
import Term.mkAtom
import leo.datastructures.impl.SignatureImpl

/**
 * Very simple test enumeration class that simply
 * enumerates all suitable constants from the signature.
 *
 * @author Alexander Steen
 */
object SimpleEnum extends Enumeration {
  def enum(ty: Type): Iterable[Term] = {
    val sig = SignatureImpl.get

    sig.constantsOfType(ty).filterNot(sig(_).isDefined).map(mkAtom(_))
  }
}
