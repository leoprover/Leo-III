package leo.modules.parsers

import leo.datastructures.tptp.Commons.{TPTPInput}
import leo.datastructures.internal.{Signature, IsSignature, Term, Type}

import Term.{mkAtom}
import Type.{mkFunKind}

/**
 * Processing module from TPTP input.
 * Declarations are inserted into the given Signature,
 * terms are returned in internal term representation.
 *
 * @author Alexander Steen
 * @since 18.06.2014
 */
object InputProcessing {
  // (Formula name, Term, Formula Role)
  type Result = (String, Term, String)

  def process(input: TPTPInput)(implicit sig: IsSignature): Result = {
    val a = sig.allConstants
    for (i <- a) {
      sig(i)
    }
  }


  implicit def globalSignature: IsSignature = Signature.get
}