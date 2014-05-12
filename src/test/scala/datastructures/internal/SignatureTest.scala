package datastructures.internal

/**
 * Just a little test with signature and types
 * Created by lex on 05.05.14.
 */
object SignatureTest {
  def main(args: Array[String]) {
    val sig = Signature.createWithHOL
    val const = sig.getDefinedSymbols
    println("Symbol" + "\t\t|" + "Type" +"\t\t\t\t\t|" + "Type vars" + "\t\t|" + "Applicable with $i" + "\t|" + "Applicable with $o")
    println("--------------------------------------------")
    for (c <- const) {
      val meta = sig.getConstMeta(c)
      println(meta.getName + "\t\t\t|"
            + meta._getType.pretty + "\t\t\t|"
            + meta._getType.getTypeVars.map(f => f.pretty) + "\t\t\t|"
            + meta._getType.isApplicableWith(Type.i) + "\t\t\t\t|"
            + meta._getType.isApplicableWith(Type.o))

    }
  }
}
