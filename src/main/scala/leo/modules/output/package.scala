package leo.modules

import leo.datastructures.{Kind, Type, Signature}

/**
  * Collection of traits, clases and utility objects relevant for
  * primarily for logging and output of TPTP problems.
  */
package object output {
  ///////////////////////////////
  // TPTP reading stuff (find good location at some point)
  ///////////////////////////////
  final def readSZSResults(lines: Seq[String]): (Option[StatusSZS], Option[(DataformSZS, Seq[String])]) = {
    var szsStatus: Option[StatusSZS] = None
    var szsOutputForm: Option[DataformSZS] = None
    var szsOutput: Seq[String] = Seq.empty
    var listening: Boolean = false

    val linesIt = lines.iterator
    while (linesIt.hasNext) {
      val line = linesIt.next()
      if (line.startsWith("% SZS status") && szsStatus.isEmpty) {
        szsStatus = StatusSZS.answerLine(line)
      } else if (line.startsWith("% SZS output start")) {
        szsOutputForm = DataformSZS.outputFormatLine(line)
        listening = true
      } else if (line.startsWith("% SZS output end")) {
        listening = false
      } else if (listening) {
        szsOutput = szsOutput :+ line
      }
    }
    szsOutputForm match {
      case Some(value) => (szsStatus, Some(value -> szsOutput))
      case None => (szsStatus, None)
    }
  }

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

//  private final val simpleNameRegex = "^([a-z]([a-zA-Z\\d_]*))|[\\d]*$"
//  final def tptpEscapeName(str: String): String = {
//    if (str.matches(simpleNameRegex)) str
//    else s"'${str.replace("\\","\\\\").replace("'", "\\'")}'"
//  }
//  private final val simpleExpressionRegex = "^([a-z]|\\${1,2}[a-z])([a-zA-Z\\d_]*)$"
//  private final val definedSimpleConnectives = Vector("=", "!=", "&", "|", "~")
//  final def tptpEscapeExpression(str: String): String = {
//    if (str.matches(simpleExpressionRegex)) str
//    else if (definedSimpleConnectives.contains(str)) str
//    else s"'${str.replace("\\","\\\\").replace("'", "\\'")}'"
//  }


  final def unescapeTPTPName(name: String): String = {
    if (name.startsWith("'") && name.endsWith("'")) {
      name.tail.init
    } else name
  }

  final def escapeTPTPName(name: String): String = {
    val integerRegex = "^[+-]?[\\d]+$"
    if (name.matches(integerRegex)) name else escapeTPTPAtomicWord(name)
  }
  final def escapeTPTPAtomicWord(word: String): String = {
    val simpleLowerWordRegex = "^[a-z][a-zA-Z\\d_]*$"
    if (word.matches(simpleLowerWordRegex)) word
    else s"'${word.replace("\\", "\\\\").replace("'", "\\'")}'"
  }

  /**
    * For a sequence `fvs` of free vars (implicitly universally quantified) and a function
    * `typeToTPTP`, return a tuple `(rep,map)` where
    *   - rep is a string representation of naming the free vars "A:tyA,B:tyB,...."
    *     from left to right,
    *   - map is a function Int -> String where for each i in fv,
    *     map(i) corresponds to the string representation of i in rep.
    */
  protected[output] final def clauseVarsToTPTP(fvs: Seq[(Int, Type)], typeToTPTP: Type => String): (String, Map[Int, String]) = {
    val fvCount = fvs.size
    val sb: StringBuffer = new StringBuffer()

    var resultBindingMap: Map[Int, String] = Map()
    var curImplicitlyQuantified = fvs
    var i = 0
    while(i < fvCount) {
      val (scope,ty) = curImplicitlyQuantified.head
      val name = intToName(fvCount - i - 1)
      sb.append(name); sb.append(":")
      sb.append(typeToTPTP(ty))
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
