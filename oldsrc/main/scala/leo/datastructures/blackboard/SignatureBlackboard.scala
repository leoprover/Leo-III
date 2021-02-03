package leo.datastructures.blackboard


import leo.datastructures.Signature

/**
  * Created by lex on 10/15/16.
  */
object SignatureBlackboard {
  private var _sig: Signature = _

  final def set(sig: Signature): Unit = {_sig = sig}
  final def get: Signature = _sig
}
