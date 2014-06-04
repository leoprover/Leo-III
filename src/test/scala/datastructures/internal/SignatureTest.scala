package datastructures.internal

/**
 * Just a little test with signature and types
 * Created by lex on 05.05.14.
 */
object SignatureTest {
  def main(args: Array[String]) {
    val sig = Signature.get
    Signature.withHOL(sig)
    val const = sig.getAllConstants
    println("Key \t\t|" + "Symbol" + "\t\t|" + "Type/Kind" +"\t\t\t\t|" + "Type vars" + "\t\t|" + "Applicable with $i" + "\t|" + "Applicable with $o")
    println("--------------------------------------------")
    for (c <- const) {
      val meta = sig.getConstMeta(c)
      print(meta.getKey.toString + "\t\t\t|")
      print(meta.getName + "\t\t\t|"
            + typeOrKindToString(meta) + "\t\t\t|")
      meta.getType.foreach(x => print(x.typeVars.map(f => f.pretty)))
      print( "\t\t\t|")
      meta.getType.foreach(x => print(x.isApplicableWith(Type.i) + "\t\t\t\t|"))
      meta.getType.foreach(x => print(x.isApplicableWith(Type.o)))
      println()

    }
    println("####################################################################")

    val meta = sig.getConstMeta("<=>")
    val term = meta._getDefn
    println("Definition of <=>: " + term.pretty)
    println("Expanded \t\t : " + term.expandDefinitions(1).pretty)

    println(sig.getConstMeta("?")._getDefn.pretty)
  }

  def typeOrKindToString(meta: IsSignature#Meta): String = {
    meta.getType match {
      case Some(ty) => ty.pretty
      case None => {
        meta.getKind match {
          case Some(k) => k.pretty
          case None => "NONE"
        }
      }
    }
  }
}
