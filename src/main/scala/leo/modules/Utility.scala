package leo
package modules

import java.io.{PrintWriter, StringWriter, FileNotFoundException, File}

import leo.datastructures.ClauseAnnotation._
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


  private final def singleTermToClause(t: Term, role: Role): Clause = {
    Clause.mkClause(Seq(Literal.mkPos(t, LitTrue)), roleToClauseOrigin(role))
  }
  private final def roleToClauseOrigin(role: Role): ClauseOrigin = role match {
    case Role_Conjecture => FromConjecture
    case Role_NegConjecture => FromConjecture
    case _ => FromAxiom
  }


  def printSignature(): Unit = {
    import leo.datastructures.IsSignature.{lexStatus,multStatus}
    val s = Signature.get
    val sb = new StringBuilder()
    sb.append(s"Name\t|\tId\t|\tType/Kind\t|\tDef.\t|\tProperties")
    (s.allConstants).foreach { case c => {
      val c1 = s(c)
      sb.append(s"${c1.name}\t|\t")
      sb.append(s"${c1.key}\t|\t")
      c1.ty foreach { case ty => sb.append(s"${ty.pretty}\t|\t")}
      c1.kind foreach { case kind => sb.append(s"${kind.pretty}\t|\t")}
      if (c1.hasDefn)
        sb.append(s"${c1._defn.pretty}\t|\t")
      else
        sb.append(s"---\t|\t")
      if (c1.status == lexStatus) sb.append("lex,")
      if (c1.status == multStatus) sb.append("mult,")
      if (c1.isASymbol) sb.append("A,")
      if (c1.isCSymbol) sb.append("C,")
      if (c1.isExternal) sb.append("Ext")
      sb.append("\n")
    }
    }
    Out.output(sb.toString())
  }

  def printUserDefinedSignature(): Unit = {
    Out.output(s"Name\t|\tId\t|\tType/Kind\t|\tDef.\t|\tProperties")
    Out.output(userDefinedSignatureAsString)
  }
  def userDefinedSignatureAsString: String = {
    import leo.datastructures.IsSignature.{lexStatus,multStatus}
    val s = Signature.get
    val sb = new StringBuilder()
    (s.allUserConstants).foreach { case c => {
      val c1 = s(c)
      sb.append(s"${c1.name}\t|\t")
      sb.append(s"${c1.key}\t|\t")
      c1.ty foreach { case ty => sb.append(s"${ty.pretty}\t|\t")}
      c1.kind foreach { case kind => sb.append(s"${kind.pretty}\t|\t")}
      if (c1.hasDefn)
        sb.append(s"${c1._defn.pretty}\t|\t")
      else
        sb.append(s"---\t|\t")
      if (c1.status == lexStatus) sb.append("lex,")
      if (c1.status == multStatus) sb.append("mult,")
      if (c1.isASymbol) sb.append("A,")
      if (c1.isCSymbol) sb.append("C,")
      if (c1.isExternal) sb.append("Ext")
      sb.append("\n")
    }
    }
    sb.toString()
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
        val name = x.id.toString.take(maxNameSize)
        val role = x.role.pretty.take(maxRoleSize)
        val form = x.cl.pretty
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
        val name = x.id.toString.take(maxNameSize)
        val role = x.role.pretty.take(maxRoleSize)
        val form = x.cl.pretty
        val form1 = form.take(maxFormulaSize)
        val form2 = form.drop(maxFormulaSize).sliding(maxFormulaSize, maxFormulaSize)

        val nameOffset = maxNameSize - name.length
        val roleOffset = maxRoleSize - role.length
        println(name + " " * nameOffset + " | " + role + " " * roleOffset + " | " +  form1)
        form2.foreach(x => println(" " * maxNameSize + " | " + " " * maxRoleSize + " | "  + x))
    }
    println()
  }


//  def printDerivation(cl: ClauseProxy) : Unit = Out.output(derivationString(new HashSet[Int](), 0, cl, new StringBuilder()).toString())
//
//
//  private def derivationString(origin: Set[Int], indent : Int, cl: ClauseProxy, sb : StringBuilder) : StringBuilder = {
//    cl.annotation match {
//      case FromFile(_, _) => sb.append(downList(origin, indent)).append(mkTPTP(cl)).append("\n")
//      case InferredFrom(_, fs) => fs.foldRight(sb.append(downList(origin, indent)).append(mkTPTP(cl)).append("\n")){case (cls, sbu) => derivationString(origin.+(indent), indent+1,cls._1,sbu)}
//      case _ => sb.append(downList(origin, indent)).append(mkTPTP(cl)).append("\n")
//    }
////    f.origin.foldRight(sb.append(downList(origin, indent)).append(mkTPTP(f)).append("\t"*6+"("+f.reason+")").append("\n")){case (fs, sbu) => derivationString(origin.+(indent), indent+1,fs,sbu)}
//  }

  def userConstantsForProof(sig: Signature): String = {
    val sb: StringBuilder = new StringBuilder()
    sig.allUserConstants.foreach { case key =>
      val name = sig.apply(key).name
      sb.append(ToTPTP(name + "_type", key).output)
        sb.append("\n")
    }
    sb.dropRight(1).toString()
  }

  def printProof(cl: ClauseProxy) : Unit = {

    var sf : Set[ClauseProxy] = new HashSet[ClauseProxy]
    var proof : Seq[String] = Seq()

    def derivationProof(f: ClauseProxy)
    {
      if (!sf.exists(c => c.id == f.id)) {
        sf = sf + f
        f.annotation match {
          case InferredFrom(_, fs) =>
            fs.foreach(f => derivationProof(f._1))
            if (!Configuration.isSet("DEBUG"))
              proof = mkTPTP(f) +: proof
            else
              proof = f.pretty +: proof
          case _ =>
            if (!Configuration.isSet("DEBUG"))
              proof = mkTPTP(f) +: proof
            else
              proof = f.pretty +: proof
        }
      }
    }

    derivationProof(cl)
    Out.output(proof.reverse.mkString("\n"))
  }

  private def mkTPTP(cl : ClauseProxy) : String = {
    try{
      ToTPTP.withAnnotation(cl).output
    } catch {
      case e : Throwable => cl.pretty
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
