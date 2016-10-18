package leo.datastructures.impl.orderings

import leo.datastructures.{Precedence, Signature, _}
import leo.modules.output.logger.Out


object Prec_SigInduced extends Precedence {
  final def compare(x: Const, y: Const)(implicit sig: Signature) = intToCMPRes(x,y)
}

object Prec_Arity extends Precedence {
  final def compare(x: Const, y: Const)(implicit sig: Signature) = {
    val (metaX, metaY) = (sig(x), sig(y))
    if (metaX.ty.isEmpty || metaY.ty.isEmpty) {
      Out.debug("Comparing unrelated symbols from signature for precedence.")
      CMP_NC
    }
    if (metaX._ty.arity == metaY._ty.arity)
      intToCMPRes(x,y)
    else intToCMPRes(metaX._ty.arity,metaY._ty.arity)
  }
}

object Prec_ArityOrder extends Precedence {
  final def compare(x: Const, y: Const)(implicit sig: Signature) = {
    val (metaX, metaY) = (sig(x), sig(y))
    if (metaX.ty.isEmpty || metaY.ty.isEmpty) {
      Out.debug("Comparing unrelated symbols from signature for precedence.")
      CMP_NC
    }
    if (metaX._ty.order == metaX._ty.order) {
      if (metaX._ty.arity == metaY._ty.arity)
        intToCMPRes(x, y)
      else intToCMPRes(metaX._ty.arity, metaY._ty.arity)
    } else intToCMPRes(metaX._ty.order,metaY._ty.order)
  }
}

object Prec_ArityInvOrder extends Precedence {
  final def compare(x: Const, y: Const)(implicit sig: Signature) = {
    val (metaX, metaY) = (sig(x), sig(y))
    if (metaX.ty.isEmpty || metaY.ty.isEmpty) {
      Out.debug("Comparing unrelated symbols from signature for precedence.")
      CMP_NC
    }
    if (metaX._ty.order == metaX._ty.order) {
      if (metaX._ty.arity == metaY._ty.arity)
        intToCMPRes(x, y)
      else intToCMPRes(metaX._ty.arity, metaY._ty.arity)
    } else intToCMPRes(metaY._ty.order,metaX._ty.order)
  }
}

object Prec_Arity_UnaryFirst extends Precedence {
  final def compare(x: Const, y: Const)(implicit sig: Signature) = {
    val (metaX, metaY) = (sig(x), sig(y))
    if (metaX.ty.isEmpty || metaY.ty.isEmpty) {
      Out.debug("Comparing unrelated symbols from signature for precedence.")
      CMP_NC
    }
    if (metaX._ty.arity == metaY._ty.arity) intToCMPRes(x,y)
    else if (metaX._ty.arity == 1) CMP_GT
    else if (metaY._ty.arity == 1) CMP_LT
    else intToCMPRes(metaX._ty.arity,metaY._ty.arity)
  }
}

object Prec_ArityOrder_UnaryFirst extends Precedence {
  final def compare(x: Const, y: Const)(implicit sig: Signature) = {
    val (metaX, metaY) = (sig(x), sig(y))
    if (metaX.ty.isEmpty || metaY.ty.isEmpty) {
      Out.debug("Comparing unrelated symbols from signature for precedence.")
      CMP_NC
    }
    if (metaX._ty.order == metaX._ty.order) {
      if (metaX._ty.arity == metaY._ty.arity) intToCMPRes(x,y)
      else if (metaX._ty.arity == 1) CMP_GT
      else if (metaY._ty.arity == 1) CMP_LT
      else intToCMPRes(metaX._ty.arity,metaY._ty.arity)
    } else intToCMPRes(metaX._ty.order,metaY._ty.order)
  }
}
object Prec_ArityInvOrder_UnaryFirst extends Precedence {
  final def compare(x: Const, y: Const)(implicit sig: Signature) = {
    val (metaX, metaY) = (sig(x), sig(y))
    if (metaX.ty.isEmpty || metaY.ty.isEmpty) {
      Out.debug("Comparing unrelated symbols from signature for precedence.")
      CMP_NC
    }
    if (metaX._ty.order == metaX._ty.order) {
      if (metaX._ty.arity == metaY._ty.arity) intToCMPRes(x,y)
      else if (metaX._ty.arity == 1) CMP_GT
      else if (metaY._ty.arity == 1) CMP_LT
      else intToCMPRes(metaX._ty.arity,metaY._ty.arity)
    } else intToCMPRes(metaY._ty.order,metaX._ty.order)
  }
}
