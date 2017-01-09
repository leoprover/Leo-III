package leo
package modules

import java.io.{PrintWriter, StringWriter}

import leo.datastructures.ClauseAnnotation._
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures._
import leo.datastructures.context.Context
import leo.modules.output._

import scala.collection.immutable.HashSet

/**
 * Utility methods for printing debug and similar information
 *
 * @author Max Wisniewski, Alexander Steen
 * @since 12/1/14
 */
object Utility {

  /////////////////////////////////////////////////////////////
  /// Signature queries
  /////////////////////////////////////////////////////////////

  def printSignature(s: Signature): Unit = {
    Out.output(signatureAsString(s))
  }

  def printUserDefinedSignature(sig: Signature): Unit = {
    Out.output(s"Name\t|\tId\t|\tType/Kind\t|\tDef.\t|\tProperties")
    Out.output(userDefinedSignatureAsString(sig))
  }

  def signatureAsString(s: Signature): String = {
    import leo.datastructures.Signature.{lexStatus,multStatus}
    val sb = new StringBuilder()
    sb.append(s"Name\t|\tId\t|\tType/Kind\t|\tDef.\t|\tProperties")
    s.allConstants.foreach { c => {
      val c1 = s(c)
      sb.append(s"${c1.name}\t|\t")
      sb.append(s"${c1.key}\t|\t")
      c1.ty foreach { ty => sb.append(s"${ty.pretty(s)}\t|\t")}
      c1.kind foreach { kind => sb.append(s"${kind.pretty}\t|\t")}
      if (c1.hasDefn)
        sb.append(s"${c1._defn.pretty(s)}\t|\t")
      else
        sb.append(s"---\t|\t")
      if (c1.status == lexStatus) sb.append("lex,")
      if (c1.status == multStatus) sb.append("mult,")
      if (c1.isASymbol) sb.append("A,")
      if (c1.isCSymbol) sb.append("C,")
      if (isPropSet(Signature.PropSkolemConstant, c1.flag)) sb.append("SK, ")
      if (isPropSet(Signature.PropFixed, c1.flag)) sb.append("Fix, ")
      if (isPropSet(Signature.PropChoice, c1.flag)) sb.append("Choice fun, ")
      if (c1.isExternal) sb.append("Ext")
      sb.append("\n")
    }
    }
    sb.toString()
  }

  def userDefinedSignatureAsString(s: Signature): String = {
    import leo.datastructures.Signature.{lexStatus,multStatus}
    val sb = new StringBuilder()
    s.allUserConstants.foreach { c => {
      val c1 = s(c)
      sb.append(s"${c1.name}\t|\t")
      sb.append(s"${c1.key}\t|\t")
      c1.ty foreach { ty => sb.append(s"${ty.pretty(s)}\t|\t")}
      c1.kind foreach { kind => sb.append(s"${kind.pretty}\t|\t")}
      if (c1.hasDefn)
        sb.append(s"${c1._defn.pretty(s)}\t|\t")
      else
        sb.append(s"---\t|\t")
      if (c1.status == lexStatus) sb.append("lex,")
      if (c1.status == multStatus) sb.append("mult,")
      if (c1.isASymbol) sb.append("A,")
      if (c1.isCSymbol) sb.append("C,")
      if (isPropSet(Signature.PropSkolemConstant, c1.flag)) sb.append("SK, ")
      if (isPropSet(Signature.PropFixed, c1.flag)) sb.append("Fix, ")
      if (isPropSet(Signature.PropChoice, c1.flag)) sb.append("Choice fun, ")
      if (c1.isExternal) sb.append("Ext")
      sb.append("\n")
    }
    }
    sb.toString()
  }

  def userConstantsForProof(sig: Signature): String = {
    val sb: StringBuilder = new StringBuilder()
    sig.allUserConstants.foreach { key =>
      val name = sig.apply(key).name
      sb.append(ToTPTP(key)(sig))
        sb.append("\n")
    }
    sb.dropRight(1).toString()
  }

  def userSignatureToTPTP(constants: Set[Signature#Key])(implicit sig: Signature): String = {
    val sb: StringBuilder = new StringBuilder()
    sig.allUserConstants.intersect(constants.union(sig.typeSymbols)).foreach { key =>
      val name = sig.apply(key).name
      sb.append(ToTPTP(key))
      sb.append("\n")
    }
    sb.dropRight(1).toString()
  }

  /////////////////////////////////////////////////////////////
  /// Proof printing and associated methods
  /////////////////////////////////////////////////////////////
  type Proof = Seq[ClauseProxy]

  final def proofOf(cl: ClauseProxy): Proof = {
    var sf : Set[ClauseProxy] = new HashSet[ClauseProxy]
    var proof : Proof = Vector()

    def derivationProof(f: ClauseProxy): Unit = {
      if (!sf.exists(c => c.id == f.id)) {
        sf = sf + f
        f.annotation match {
          case InferredFrom(_, fs) =>
            fs.foreach(f => derivationProof(f._1))
              proof = f +: proof
          case _ =>
              proof = f +: proof
        }
      }
    }
    derivationProof(cl)
    proof.reverse
  }

  final def proofToTPTP(proof: Proof)(implicit sig: Signature): String = {
    if (Configuration.isSet("DEBUG"))
      proof.map(_.pretty(sig)).mkString("\n")
    else
      proof.map(mkTPTP(_)(sig)).mkString("\n")
  }

  private def mkTPTP(cl : ClauseProxy)(sig: Signature) : String = {
    try{
      ToTPTP.withAnnotation(cl)(sig)
    } catch {
      case e : Throwable => leo.Out.warn(s"Could not translate: ${cl.pretty}.\n Error: ${e.toString}"); cl.pretty
    }
  }

  final def symbolsInProof(p: Proof): Set[Signature#Key] = {
    p.flatMap(cl => cl.cl.lits.flatMap(l => l.left.symbols.distinct ++ l.right.symbols.distinct)).toSet
  }

  final def axiomsInProof(p: Proof): Set[ClauseProxy] = {
    p.filter(_.role == Role_Axiom).toSet
  }


  /////////////////////////////////////////////////////////////
  /// Other stuff.
  /////////////////////////////////////////////////////////////

  @inline final def termToClause(t: Term, polarity: Boolean = true): Clause = {
    Clause.mkClause(Seq(Literal.mkLit(t, polarity)))
  }

  def stackTraceAsString(e: Throwable): String = {
    val sw = new StringWriter()
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
  }

  /////////////////////////////////////////////////////////////
  /// Old, unused and should soon get deleted or moved to a reasonable location:
  /////////////////////////////////////////////////////////////

  /**
    * Shows all formulas in the current context.
    */
  def context(sig: Signature): Unit = {
    println("Signature:")
    printSignature(sig)
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

}

class SZSException(val status : StatusSZS, message : String = "", val debugMessage: String = "", cause : Throwable = null) extends RuntimeException(message, cause)

case class SZSOutput(status : StatusSZS, problem: String, furtherInfo: String = "") extends Output {
  override def apply: String = if (furtherInfo == "") {
    s"% SZS status ${status.apply} for $problem"
  } else {
    s"% SZS status ${status.apply} for $problem : $furtherInfo"
  }
}
