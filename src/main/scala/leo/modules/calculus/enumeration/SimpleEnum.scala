package leo.modules.calculus.enumeration

import leo.datastructures.{Term, Type, Signature}
import Term.mkAtom

/**
 * Very simple test enumeration class that simply
 * enumerates all suitable constants from the signature.
 *
 * @author Alexander Steen
 */
object SimpleEnum extends Enumeration {
  def enum(ty: Type)(implicit sig:Signature): Iterable[Term] = {
    sig.constantsOfType(ty).filterNot(sig(_).isDefined).map(mkAtom(_))
  }
}
