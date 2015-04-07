package leo.modules.proofCalculi.enumeration

import leo.datastructures.Type
import leo.datastructures.term.Term.mkAtom
import leo.datastructures.term.Term
import leo.datastructures.impl.Signature

/**
 * Very simple test enumeration class that simply
 * enumerates all suitable constants from the signature.
 *
 * @author Alexander Steen
 */
object SimpleEnum extends Enumeration {
  def enum(ty: Type): Iterable[Term] = {
    val sig = Signature.get

    sig.constantsOfType(ty).filterNot(sig(_).isDefined).map(mkAtom(_))
  }
}
