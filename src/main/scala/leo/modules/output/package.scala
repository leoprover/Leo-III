package leo.modules

import leo.datastructures.{Kind, Type, Signature}

/**
  * Collection of traits, clases and utility objects relevant for
  * primarily for logging and output of TPTP problems.
  */
package object output {
  ///////////////////////////////
  // Naming of variables
  ///////////////////////////////

  @inline final private val asciiA = 65
  @inline final private val asciiZ = 90
  @inline final private val range = asciiZ - asciiA // range 0,1,....

  /**
    * Convert index i (variable in de-bruijn format) to a variable name corresponding to ASCII transformation as follows:
    * 0 ---> "A",
    * 1 ---> "B",
    * 25 ---> "Z",
    * 26 ---> "ZA", ... etc.
    */
  protected[output] final def intToName(i: Int): String = i match {
    case n if n <= range => s"${intToChar(i)}"
    case n if n > range => s"Z${intToName(i-range-1)}"
  }
  protected[output] final def intToChar(i: Int): Char = i match {
    case n if n <= range => (n + asciiA).toChar
    case _ => throw new IllegalArgumentException
  }

  private final val simpleNameRegex = "^([a-z]([a-zA-Z\\d_]*))|[\\d]*$"
  final def tptpEscapeName(str: String): String = {
    if (str.matches(simpleNameRegex)) str
    else s"'${str.replace("\\","\\\\").replace("'", "\\'")}'"
  }
  private final val simpleExpressionRegex = "^([a-z]|\\${1,2}[a-z])([a-zA-Z\\d_]*)$"
  final def tptpEscapeExpression(str: String): String = {
    if (str.matches(simpleExpressionRegex)) str
    else s"'${str.replace("\\","\\\\").replace("'", "\\'")}'"
  }

  /**
    * For a sequence `fvs` of free vars (implicitly universally quantified) and a function
    * `typeToTPTP`, return a tuple `(rep,map)` where
    *   - rep is a string representation of naming the free vars "A:tyA,B:tyB,...."
    *     from left to right,
    *   - map is a function Int -> String where for each i in fv,
    *     map(i) corresponds to the string representation of i in rep.
    */
  protected[output] final def clauseVarsToTPTP(fvs: Seq[(Int, Type)], typeToTPTP: Type => Signature => String)(sig: Signature): (String, Map[Int, String]) = {
    val fvCount = fvs.size
    val sb: StringBuffer = new StringBuffer()

    var resultBindingMap: Map[Int, String] = Map()
    var curImplicitlyQuantified = fvs
    var i = 0
    while(i < fvCount) {
      val (scope,ty) = curImplicitlyQuantified.head
      val name = intToName(fvCount - i - 1)
      sb.append(name); sb.append(":")
      sb.append(typeToTPTP(ty)(sig))
      resultBindingMap = resultBindingMap + (scope -> name)

      curImplicitlyQuantified = curImplicitlyQuantified.tail
      i = i + 1
      if (i < fvCount) sb.append(",")
    }
    (sb.toString, resultBindingMap)
  }

  protected[output] final def makeBVarList(tys: Seq[Type], offset: Int): Seq[(String, Type)] = {
    tys.zipWithIndex.map {case (ty, idx) => (intToName(offset + idx), ty)}
  }
  protected[output]  final def fusebVarListwithMap(bvarList: Seq[(String, Type)], oldbvarMap: Map[Int,String]): Map[Int, String] = {
    val newVarCount = bvarList.size
    val newVarsAsKeyValueList = bvarList.zipWithIndex.map {case ((name, ty),idx) => (newVarCount - idx, name)}
    oldbvarMap.map {case (k,v) => (k+newVarCount, v)} ++ Map(newVarsAsKeyValueList:_*)
  }
}
