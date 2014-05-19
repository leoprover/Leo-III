package datastructures.internal

/**
 * Created by lex on 19.05.14.
 */
object VarUtils {
  import Signature.{get => signature}


  def freshTypeVar(kind: Kind = TypeKind): Variable = {
    val sig = signature
    lastUsedIndex += 1
    var name: String = typeVarName + lastUsedIndex.toString

    while (sig.symbolExists(name)) {
      lastUsedIndex += 1
      name = typeVarName + lastUsedIndex.toString
    }

    val key = sig.addVariable(name, Right(kind))
    Variable.mkTypeVar(key, kind)
  }

  val typeVarName: String = "TV"
  var lastUsedIndex = -1


  implicit def strToType: PartialFunction[String, Type] = {
    case in: String if signature.getVarMeta(in).isTypeVariable => Variable.mkTypeVar(signature.getVarMeta(in).getKey)
  }

}
