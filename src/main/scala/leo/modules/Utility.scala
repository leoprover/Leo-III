package leo
package modules

import java.io.{FileNotFoundException, File}

import leo.datastructures.{TermIndex, Role_Definition, Role_Unknown, Role_Type}
import leo.datastructures.blackboard.{FormulaStore, Blackboard}
import leo.datastructures.context.Context
import leo.datastructures.impl.Signature
import leo.modules.output._

import scala.collection.immutable.HashSet

/**
 * Stuff to do smth.
 *
 * @author Max Wisniewski
 * @since 12/1/14
 */
object Utility {
  /**
   * List of currently loaded tptp files
   */
  private val loadedSet = collection.mutable.Set.empty[String]

  private val _pwd = (new File(".")).getCanonicalPath

  /**
   * Loads a tptp file and saves the formulas in the context.
   */
  def load(file: String): Unit = {
    if (file.charAt(0) != '/') {
      // Relative load
      loadRelative(file, _pwd.split('/'))
    } else {
      // Absolute load
      val pwd = file.split('/')
      loadRelative(pwd.last, pwd.init)
    }
  }

  private def loadRelative(file : String, rel : Array[String]): Unit = {
    import scala.util.parsing.input.CharArrayReader
    import leo.modules.parsers.TPTP
    import leo.modules.parsers.InputProcessing


    val (fileAbs, path) = newPath(rel, file)
    if (!loadedSet(fileAbs)) {
      try {
        val source = scala.io.Source.fromFile(fileAbs, "utf-8")
        val input = new CharArrayReader(source.toArray)
        val parsed = TPTP.parseFile(input)
        source.close()    // Close at this point. Otherwise we would have many files open with many includes.

        parsed match {
          case Left(x) =>
            //            println("Parse error in file " + fileAbs + ": " + x)
            throw new SZSException(SZS_SyntaxError)
          case Right(x) =>
            loadedSet += fileAbs
            x.getIncludes.foreach(x => loadRelative(x._1, path))
            //            println("Loaded " + fileAbs)
            val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
            processed foreach { case (name, form, role) => if(role != Role_Definition && role != Role_Type && role != Role_Unknown)
              Blackboard().addFormula(name, form.mapLit(_.termMap(TermIndex.insert(_))), role, Context())
            }
        }

      } catch {
        case ex : FileNotFoundException =>
          // If not relative, then search in TPTP env variable
          val tptpHome = System.getenv("TPTP").split("/")
          val (fileAbs, path) = newPath(tptpHome, file)
          if (!loadedSet(fileAbs)) {
            try {
              val source = scala.io.Source.fromFile(fileAbs, "utf-8")
              val input = new CharArrayReader(source.toArray)
              val parsed = TPTP.parseFile(input)
              source.close()    // Close at this point. Otherwise we would have many files open with many includes.

              parsed match {
                case Left(x) =>
                  //            println("Parse error in file " + fileAbs + ": " + x)
                  throw new SZSException(SZS_SyntaxError)
                case Right(x) =>
                  loadedSet += fileAbs
                  x.getIncludes.foreach(x => loadRelative(x._1, path))
                  //            println("Loaded " + fileAbs)
                  val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
                  processed foreach { case (name, form, role) => if(role != Role_Definition && role != Role_Type && role != Role_Unknown)
                    Blackboard().addFormula(name, form.mapLit(_.termMap(TermIndex.insert(_))), role, Context())
                  }
              }

            } catch {
              case ex : FileNotFoundException => throw new SZSException(SZS_InputError)
              case _ : Throwable => throw new SZSException(SZS_InputError)
            }
          }
      }
    }
  }

  private def parseAbsolute(fileAbs : String, path : String) : Unit = {

  }

  /**
   * Returns the new absolute Path and the absolute directory
   *
   * @param oldDir - Old absolute Path to directory
   * @param relPath - relative path to new file
   */
  private def newPath(oldDir : Array[String], relPath : String) : (String, Array[String]) = {
    val relSplit  = relPath.split('/')
    val path = oldDir.take(oldDir.length - relSplit.count(_ == ".."))
    val absPath = path ++ relSplit.dropWhile(x => x == "..")
    (absPath.mkString("/"), absPath.init)
  }

  /**
   * Parses and adds a TPTP formula.
   */
  def add(s: String): Unit = {
    import leo.modules.parsers.TPTP
    import leo.modules.parsers.InputProcessing

    TPTP.parseFormula(s) match {
      case Right(a) =>
        val processed = InputProcessing.process(Signature.get)(a)
        processed.foreach {case (name,form,role) => if(role != Role_Definition && role != Role_Type && role != Role_Unknown)
          Blackboard().addFormula(name, form.mapLit(_.termMap(TermIndex.insert(_))), role, Context())
        }
      case Left(err) =>
        Out.severe(s"'$s' is not a valid formula: $err")
    }
  }

  def printSignature(): Unit = {
    val s = Signature.get
    println("Name | Id | (Type) | (Def)")
    (s.allConstants).foreach { case c => {
      val c1 = s(c)
      print(c1.name + " | ")
      print(c1.key + " |")
      c1.ty foreach { case ty => print(ty.pretty + " | ")}
      c1.defn foreach { case defn => print(defn.pretty)}
      println()
    }
    }
  }

  def printUserDefinedSignature(): Unit = {
    val s = Signature.get
    (s.allUserConstants).foreach { case c => {
      val c1 = s(c)
      print(c1.name + " | ")
      print(c1.key + " | ")
      c1.ty foreach { case ty => print(ty.pretty + " | ")}
      c1.defn foreach { case defn => print(defn.pretty)}
      println()
    }
    }
  }

  /**
   * Shows all formulas in the current context.
   */
  def context(): Unit = {
    println("Signature:")
    printSignature()
    println("Blackboard context:")
    formulaContext()
  }

  def formulaContext() : Unit = {
    val maxSize = 85
    val maxNameSize = 25
    val maxRoleSize = 19
    val maxFormulaSize = maxSize -(maxNameSize + maxRoleSize + 6)

    println("Name" + " "*(maxNameSize-4) +  " | " + "Role" + " " * (maxRoleSize -4)+" | Formula")
    println("-"*maxSize)
    Blackboard().getFormulas.foreach {
      x =>
        val name = x.name.toString.take(maxNameSize)
        val role = x.role.pretty.take(maxRoleSize)
        val form = x.clause.pretty + " ("+x.status+") (context="+x.context.contextID+")"
        val form1 = form.take(maxFormulaSize)
        val form2 = form.drop(maxFormulaSize).sliding(maxFormulaSize, maxFormulaSize)

        val nameOffset = maxNameSize - name.length
        val roleOffset = maxRoleSize - role.length
        println(name + " " * nameOffset + " | " + role + " " * roleOffset + " | " +  form1)
        form2.foreach(x => println(" " * maxNameSize + " | " + " " * maxRoleSize + " | "  + x))
    }
    println()
  }

  /**
   * Returns the formula with the given name in the context.
   * The formula is not ready to manipulate in parallel with this access.
   */
  def get(s: String) : FormulaStore =
    Blackboard().getFormulaByName(s).
      getOrElse{
      println(s"There is no formula named '$s'.")
      null
    }
  /**
   * Deletes all formulas from the current context.
   */
  def clear(): Unit = {
    Blackboard().clear()
    loadedSet.clear()
  }

  /** Reset the signature to standard hol connectives */
  def clearSignature(): Unit = {
    Signature.resetWithHOL(Signature.get)
  }

  def agentStatus() : Unit = {
    println("Agents: ")
    for((a,b) <- Blackboard().getAgents()) {
      println(a.name + " , "+ (if(a.isActive) "active" else "inactive") + " , "+ b +" budget , "+a.openTasks+" tasks")
    }
  }

  def printDerivation(f : FormulaStore) : Unit = Out.output(derivationString(new HashSet[Int](), 0, f, new StringBuilder()).toString())

  private def derivationString(origin: Set[Int], indent : Int, f: FormulaStore, sb : StringBuilder) : StringBuilder = {
    f.origin.foldRight(sb.append(downList(origin, indent)).append(ToTPTP(f).output).append("\t"*6+"("+f.reason+")").append("\n")){case (fs, sbu) => derivationString(origin.+(indent), indent+1,fs,sbu)}
  }

  private def downList(origin: Set[Int], indent : Int) : String = {
    val m = if(origin.isEmpty) 0 else origin.max
    List.range(0, indent).map { x => origin.contains(x) match {
      case true if x < m => " | "
      case true => " |-"
      case false if m < x => "---"
      case false => "   "
    }}.foldRight(""){(a,b) => a+b}
  }

}

class SZSException(val status : StatusSZS) extends RuntimeException("SZS status "+status.output)

case class SZSOutput(status : StatusSZS) extends Output {
  override def output: String = "% SZS status "+status.output
}
