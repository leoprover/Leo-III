// IMPORTANT : Keep the unused imports for loading in the Shell Project



import leo.modules.normalization.{Simplification, NegationNormal}
import leo.modules.churchNumerals.Numerals
import leo.datastructures.blackboard._
import leo.datastructures.internal._
import leo.datastructures.internal.Term._
import LeoShell._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.agents.impl._
import leo.agents.impl.NormalClauseAgent._
import leo.agents.impl.UtilAgents._



/**
 * Addition commands for an interactive session with the sbt cosole.
 *
 * May commands printing additional information to the console and returning
 * null to indicate an error, it isn't recommended to use LeoShell in a
 * productive environment.
 */
object LeoShell {
  import java.io.FileNotFoundException
  import java.io.File

  private var _pwd : String = new File(".").getCanonicalPath

  def pwd() = _pwd

  def ls() : Unit = {
    for (file <- new File(_pwd).listFiles()) {
      if(!file.getName.startsWith(".")) {
        if(file.isDirectory){
          println(file.getName+"/")
        } else {
          println(file.getName)
        }
      }
    }
  }

  def ls(regex : String) : Unit = {
    import scala.util.matching.Regex

    val reg = new Regex(regex)
    for (file <- new File(_pwd).listFiles()) {
      if(!file.getName.startsWith(".") && reg.findFirstIn(file.getName).nonEmpty) {
        if(file.isDirectory){
          println(file.getName+"/")
        } else {
          println(file.getName)
        }
      }
    }
  }

  def cd() : Unit = _pwd = new File(".").getCanonicalPath

  def cd(to : String) : Unit = {
    if(to.startsWith("/")){
      val f = new File(to)
      if(f != null && f.isDirectory) {
        _pwd = to
      } else {
        println(s"'$to' is not a directory")
      }
    } else {
     val toL = to.split("/")
     cdRek(_pwd, toL) match {
       case Left(x) => _pwd = x
       case Right(err) => println(err)
     }
    }
  }

  private def cdRek(from : String, to : Seq[String]) : Either[String, String] = {
    if(to.isEmpty) Left(from)
    else {
      cdRekStep(from, to.head) match {
        case Left(from1) => cdRek(from1, to.tail)
        case Right(err) => Right(err)
      }
    }
  }

  private def cdRekStep(from : String, to : String) : Either[String, String] = {
    if (to == ".."){
      return Left(from.split("/").init.mkString("/"))
    } else {
      val to1 = to
      if (to.last == '/') to1.init
      for (file <- (new File(from)).listFiles()) {
        if (file.getName == to1) {
          if (file.isDirectory) {
            return Left(from + "/" + to1)
          } else {
            return Right(s"The file '$to1' is not a directory.")
          }
        }
      }
      return Right(s"No such directory '$to1'.")
    }
  }

//  For the time beeing, we have no parser.
//  Comment in, iff it is written

  /**
   * List of currently loaded tptp files
   */
  private val loadedSet = collection.mutable.Set.empty[String]

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
            println("Parse error in file " + fileAbs + ": " + x)
          case Right(x) =>
            loadedSet += fileAbs
            x.getIncludes.foreach(x => loadRelative(x._1, path))
            println("Loaded " + fileAbs)
            val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
            processed foreach { case (name, form, role) =>
              Blackboard().addFormula(name, form, role)
            }
        }

      } catch {
        case ex : FileNotFoundException =>
          println(s"'$fileAbs' does not exist.")
      }
    }
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
        processed.foreach {case (name,form,role) => if(role != "definition" && role != "type") Blackboard().addFormula(name,form,role)}
      case Left(err) =>
        println(s"'$s' is not a valid formula: $err")
    }
  }

  /**
   * Adds a Formula to the Blackboard
   * @param name - Name of the formula
   * @param s - The term
   */
  def add(name : String, s : Term, role : String): Unit = {
    Blackboard().addFormula(name, s, role)
    println("Added "+name+"='$s' to the context.")
  }

  def add(name : String, s : Term) : Unit = add(name, s, "plain")

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

  def exit() = System.exit(0)

  /**
   * Shows all formulas in the current context.
   */
  def context(): Unit = {
    val maxSize = 85
    val maxNameSize = 25
    val maxRoleSize = 19
    val maxFormulaSize = maxSize -(maxNameSize + maxRoleSize + 6)

    println("Signature:")
    val s = Signature.get
    for(c <- s.allConstants) {
      val c1 = s(c)
      print(c1.name+" | ")
      print(c1.key+" | ")
      c1.ty foreach {case ty => print(ty.pretty + " | ")}
      c1.defn foreach {case defn => print(defn.pretty)}
      println()
    }

    println("Name" + " "*(maxNameSize-4) +  " | " + "Role" + " " * (maxRoleSize -4)+" | Formula")
    println("-"*maxSize)
    Blackboard().getFormulas.foreach {
      x =>
        val name = x.name.toString.take(maxNameSize)
        val role = x.role.toString.take(maxRoleSize)
        val form = x.formula.pretty
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

  /**
   * Deletes a formula by name from the context.
   */
  def rm(s: String) {
    if (Blackboard().rmFormulaByName(s))
      println(s"Removed $s from the context.")
    else
      println(s"There was no $s. Removed nothing.")
  }


  def simplify(f : Term) : Term = Simplification.normalize(f)

  def negNormal(f : Term) : Term = NegationNormal.normalize(f)

  def run() : Unit = Scheduler().signal()

  def pause() : Unit = Scheduler().pause()
}




