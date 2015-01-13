// IMPORTANT : Keep the unused imports for loading in the Shell Project

import leo.datastructures.impl.Signature
import leo.datastructures.term.Term
import leo.datastructures.{Role, Clause, Role_Definition, Role_Type, Role_Unknown}
import leo.modules.normalization.{Simplification, NegationNormal}
import leo.modules.churchNumerals.Numerals
import leo.datastructures.blackboard._
import Term._
import LeoShell._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
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

  /**
   * Adds a Formula to the Blackboard
   * @param name - Name of the formula
   * @param cl - The clause
   */
  def add(name : String, cl : Clause, role : String): Unit = {
    Blackboard().addFormula(name, cl, Role(role), Context())
    println(s"Added $name='$cl' to the context.")
  }

  def add(name : String, cl : Clause) : Unit = add(name, cl, "plain")

  private def update(name : String, fS : FormulaStore) = {
    Blackboard().rmFormulaByName(name)
    val added = Blackboard().addFormula(fS)
    Blackboard().filterAll{a => a.filter(FormulaEvent(fS))}
  }

  def update(name : String, status : Int) : Unit = {
    Blackboard().getFormulaByName(name) match {
      case Some(fS) => update(name, fS.newStatus(status))
      case None => ()
    }
  }

  def update(name : String, r : Role) : Unit = {
    Blackboard().getFormulaByName(name) match {
      case Some(fS) => update(name, fS.newRole(r))
      case None => ()
    }
  }

  def update(name : String, s : Clause): Unit = {
    Blackboard().getFormulaByName(name) match {
      case Some(fS) => update(name, fS.newClause(s))
      case None => ()
    }
  }



  def exit() = System.exit(0)




  /**
   * Deletes a formula by name from the context.
   */
  def rm(s: String) {
    if (Blackboard().rmFormulaByName(s))
      println(s"Removed $s from the context.")
    else
      println(s"There was no $s. Removed nothing.")
  }

  def agentStatus() : Unit = {
    println("Agents: ")
    for((a,b) <- Blackboard().getAgents()) {
      println(a.name + " , "+ (if(a.isActive) "active" else "inactive") + " , "+ b +" budget , "+a.openTasks+" tasks")
    }
  }

  def run() : Unit = Scheduler().signal()

  def step() : Unit = Scheduler().step()

  def pause() : Unit = Scheduler().pause()
}




