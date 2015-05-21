package leo
package modules

import java.io.{PrintWriter, StringWriter, FileNotFoundException, File}

import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures._
import leo.datastructures.blackboard._
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

  /** The working directory in which the executable was launched from. */
  val wd: String = System.getenv("user.dir")

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

    Out.debug(s"Loading ${rel.mkString("/")}/${file}.")
    val (fileAbs, path) = newPath(rel, file)
    if (!loadedSet(fileAbs)) {
      try {
        val source = scala.io.Source.fromFile(fileAbs, "utf-8")
        val input = new CharArrayReader(source.toArray)
        val parsed = TPTP.parseFile(input)
        source.close()    // Close at this point. Otherwise we would have many files open with many includes.

        parsed match {
          case Left(x) =>
            Out.severe("Parse error in file " + fileAbs + ": " + x)
            throw new SZSException(SZS_SyntaxError)
          case Right(x) =>
            loadedSet += fileAbs
            x.getIncludes.foreach(x => loadRelative(x._1, path))

            val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
            processed foreach { case (name, form, role) => if(role != Role_Definition && role != Role_Type && role != Role_Unknown) {
              val f = Store(name, form.mapLit(_.termMap(TermIndex.insert(_))), role, Context(), 0, FromFile(fileAbs, name))
              if (FormulaDataStore.addFormula(f))
                Blackboard().filterAll(_.filter(DataEvent(f, FormulaType)))
              }
            }
        }

      } catch {
        case ex : FileNotFoundException =>
          // If not relative, then search in TPTP env variable
          val tptp = System.getenv("TPTP")
          if (tptp != null) {
            Out.debug(s"Loading ${tptp}/${file}.")
            val tptpHome = tptp.split("/")
            val (fileAbs, path) = newPath(tptpHome, file)
            if (!loadedSet(fileAbs)) {
              try {
                val source = scala.io.Source.fromFile(fileAbs, "utf-8")
                val input = new CharArrayReader(source.toArray)
                val parsed = TPTP.parseFile(input)
                source.close() // Close at this point. Otherwise we would have many files open with many includes.

                parsed match {
                  case Left(x) =>
                    Out.severe("Parse error in file " + fileAbs + ": " + x)
                    throw new SZSException(SZS_SyntaxError)
                  case Right(x) =>
                    loadedSet += fileAbs
                    x.getIncludes.foreach(x => loadRelative(x._1, path))

                    val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
                    processed foreach { case (name, form, role) => if(role != Role_Definition && role != Role_Type && role != Role_Unknown) {
                      val f = Store(name, form.mapLit(_.termMap(TermIndex.insert(_))), role, Context(), 0, FromFile(fileAbs, name))
                      if (FormulaDataStore.addFormula(f))
                        Blackboard().filterAll(_.filter(DataEvent(f, FormulaType)))
                      }
                    }
                }


              } catch {
                case ex: FileNotFoundException => Out.severe("Problem file not found."); throw new SZSException(SZS_InputError, s"File $file not found", s"with rel ${rel.mkString("/")}", ex)
                case e: Throwable => throw new SZSException(SZS_InputError, e.getMessage, "", e)
              }
            }
          } else {
            Out.severe("Problem file not found."); throw new SZSException(SZS_InputError, s"File $file not found", s"with rel ${rel.mkString("/")}", ex)
          }
        case e: Throwable => throw new SZSException(SZS_InputError, e.getMessage, "", e)
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
        processed match { case (name, form, role) => if(role != Role_Definition && role != Role_Type && role != Role_Unknown) {
          val f = Store(name, form.mapLit(_.termMap(TermIndex.insert(_))), role, Context(), 0, NoAnnotation)
          if (FormulaDataStore.addFormula(f))
            Blackboard().filterAll(_.filter(DataEvent(f, FormulaType)))
        }
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

  def formulaContext(c : Context): Unit ={
    val maxSize = 85
    val maxNameSize = 25
    val maxRoleSize = 19
    val maxFormulaSize = maxSize -(maxNameSize + maxRoleSize + 6)

    println(s"Formulas in Context(id=${c.contextID})")
    println("Name" + " "*(maxNameSize-4) +  " | " + "Role" + " " * (maxRoleSize -4)+" | Formula (in nameless spine representation)")
    println("-"*maxSize)
    FormulaDataStore.getFormulas(c).foreach {
      x =>
        val name = x.name.toString.take(maxNameSize)
        val role = x.role.pretty.take(maxRoleSize)
        val form = x.clause.pretty + " ("+x.status+") (status="+x.status+")"
        val form1 = form.take(maxFormulaSize)
        val form2 = form.drop(maxFormulaSize).sliding(maxFormulaSize, maxFormulaSize)

        val nameOffset = maxNameSize - name.length
        val roleOffset = maxRoleSize - role.length
        println(name + " " * nameOffset + " | " + role + " " * roleOffset + " | " +  form1)
        form2.foreach(x => println(" " * maxNameSize + " | " + " " * maxRoleSize + " | "  + x))
    }
    println()
  }

  def formulaContext() : Unit = {
    val maxSize = 85
    val maxNameSize = 25
    val maxRoleSize = 19
    val maxFormulaSize = maxSize -(maxNameSize + maxRoleSize + 6)

    println("Name" + " "*(maxNameSize-4) +  " | " + "Role" + " " * (maxRoleSize -4)+" | Formula (in nameless spine representation)")
    println("-"*maxSize)
    FormulaDataStore.getFormulas.foreach {
      x =>
        val name = x.name.toString.take(maxNameSize)
        val role = x.role.pretty.take(maxRoleSize)
        val form = x.clause.pretty + " ("+x.status+") (status="+x.status+")"
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
    FormulaDataStore.getFormulaByName(s).
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
    for((a,b) <- Blackboard().getAgents) {
      println(a.name + " , "+ (if(a.isActive) "active" else "inactive") + " , "+ b +" budget , "+a.openTasks+" tasks")
    }
  }

  def printDerivation(f : FormulaStore) : Unit = Out.output(derivationString(new HashSet[Int](), 0, f, new StringBuilder()).toString())

  private def derivationString(origin: Set[Int], indent : Int, f: FormulaStore, sb : StringBuilder) : StringBuilder = {
    f.annotation match {
      case FromFile(_, _) => sb.append(downList(origin, indent)).append(mkTPTP(f)).append("\n")
      case InferredFrom(_, fs) => fs.foldRight(sb.append(downList(origin, indent)).append(mkTPTP(f)).append("\n")){case (fs, sbu) => derivationString(origin.+(indent), indent+1,fs,sbu)}
      case _ => sb.append(downList(origin, indent)).append(mkTPTP(f)).append("\n")
    }
//    f.origin.foldRight(sb.append(downList(origin, indent)).append(mkTPTP(f)).append("\t"*6+"("+f.reason+")").append("\n")){case (fs, sbu) => derivationString(origin.+(indent), indent+1,fs,sbu)}
  }

  def printProof(f : FormulaStore) : Unit = {

    var sf : Set[FormulaStore] = new HashSet[FormulaStore]
    var proof : Seq[String] = Seq()

    def derivationProof(f: FormulaStore)
    {
      if (!sf.contains(f)) {
        sf = sf + f
        f.annotation match {
          case InferredFrom(_, fs) =>
            fs.foreach(derivationProof(_))
            proof = mkTPTP(f) +: proof
          case _ =>
            proof = mkTPTP(f) +: proof
        }
      }
    }

    derivationProof(f)
    Out.output(proof.reverse.mkString("\n"))
  }

  private def mkTPTP(f : FormulaStore) : String = {
    try{
      ToTPTP.withAnnotation(f).output
    } catch {
      case e : Throwable => f.pretty
    }
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


  def stackTraceAsString(e: Throwable): String = {
    val sw = new StringWriter()
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
  }

}

class SZSException(val status : StatusSZS, message : String = "", val debugMessage: String = "", cause : Throwable = null) extends RuntimeException(message, cause)

case class SZSOutput(status : StatusSZS, problem: String, furtherInfo: String = "") extends Output {
  override def output: String = if (furtherInfo == "") {
    s"% SZS status ${status.output} for $problem"
  } else {
    s"% SZS status ${status.output} for $problem : $furtherInfo"
  }
}
