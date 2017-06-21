package leo.datastructures.impl.orderings


import leo.datastructures.{Term, Signature, TermOrdering, CMP_Result, CMP_NC, CMP_EQ}
/**
  * Created by lex on 20.06.17.
  */
object NO_ORDERING extends TermOrdering
{
  final def compare(s: Term, t: Term)(implicit sig: Signature): CMP_Result = {
    if (s == t) CMP_EQ
    else CMP_NC
  }
}
