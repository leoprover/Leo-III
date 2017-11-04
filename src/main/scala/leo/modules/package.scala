package leo

import java.io.{PrintWriter, StringWriter}

import leo.datastructures._
import leo.modules.calculus.CalculusRule
import leo.modules.output.{DataformSZS, Output, StatusSZS, ToTPTP}
import leo.modules.proof_object.CompressProof

import scala.annotation.elidable
import scala.collection.immutable.HashSet

/**
  * Utility methods for printing debug and similar information
  *
  * @author Max Wisniewski, Alexander Steen
  * @since 12/1/14
  */
package object modules {
  class SZSException(val status : StatusSZS, message : String = "", val debugMessage: String = "", cause : Throwable = null) extends RuntimeException(message, cause)

  case class SZSResult(status : StatusSZS, problem: String, furtherInfo: String = "") extends Output {
    override def apply: String = if (furtherInfo == null || furtherInfo == "") {
      s"% SZS status ${status.apply} for $problem"
    } else {
      s"% SZS status ${status.apply} for $problem : $furtherInfo"
    }
  }

  case class SZSOutput(dataform : DataformSZS, problem: String, content: String, furtherInfo: String = "") extends Output {
    override def apply: String = if (furtherInfo == null || furtherInfo == "") {
      s"% SZS output start ${dataform.apply} for $problem\n" +
        content + "\n" +
        s"% SZS output end ${dataform.apply} for $problem"
    } else {
      s"% SZS output start ${dataform.apply} for $problem : $furtherInfo\n" +
        content + "\n" +
        s"% SZS output end ${dataform.apply} for $problem"
    }
  }

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
    sb.append(s"Name\t|\tId\t|\tType/Kind\t|\tDef.\t|\tProperties\n")
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

  def userSignatureToTPTP(constants: Set[Signature.Key])(implicit sig: Signature): String = {
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

  final def compressedProofOf(important : Set[CalculusRule])(cl: ClauseProxy) : Proof = {
    var sf : Set[ClauseProxy] = new HashSet[ClauseProxy]
    var proof : Proof = Vector()
    def compress(cl : ClauseProxy) : ClauseProxy = CompressProof.compressAnnotation(cl.asInstanceOf[AnnotatedClause])(CompressProof.lastImportantStep(important))

    def derivationProof(f: ClauseProxy): Unit = {
      if (!sf.exists(c => c.id == f.id)) {
        sf = sf + f
        f.annotation.parents.foreach(f => derivationProof(compress(f)))
        proof = f +: proof
        //        f.annotation match {
        //          case InferredFrom(_, fs) =>
        //            fs.foreach(f => derivationProof(f._1))
        //              proof = f +: proof
        //          case _ =>
        //              proof = f +: proof
        //        }
      }
    }
    derivationProof(compress(cl))
    proof.reverse
  }

  final def proofOf(cl: ClauseProxy): Proof = {
    var sf : Set[ClauseProxy] = new HashSet[ClauseProxy]
    var proof : Proof = Vector()

    def derivationProof(f: ClauseProxy): Unit = {
      if (!sf.exists(c => c.id == f.id)) {
        sf = sf + f
        f.annotation.parents.foreach(f => derivationProof(f))
        proof = f +: proof
        //        f.annotation match {
        //          case InferredFrom(_, fs) =>
        //            fs.foreach(f => derivationProof(f._1))
        //              proof = f +: proof
        //          case _ =>
        //              proof = f +: proof
        //        }
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

  final def symbolsInProof(p: Proof): Set[Signature.Key] = {
    p.flatMap(cl => cl.cl.lits.flatMap(l => l.left.symbols.distinct ++ l.right.symbols.distinct)).toSet
  }

  final def axiomsInProof(p: Proof): Set[ClauseProxy] = {
    p.filter(_.role == Role_Axiom).toSet
  }

  final def conjInProof(p: Proof): Boolean = {
    p.exists(_.role == Role_Conjecture)
  }


  /////////////////////////////////////////////////////////////
  /// Other stuff.
  /////////////////////////////////////////////////////////////

  @inline final def termToClause(t: Term, polarity: Boolean = true): Clause = {
    Clause.mkClause(Vector(Literal.mkLit(t, polarity)))
  }


  def stackTraceAsString(e: Throwable): String = {
    val sw = new StringWriter()
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
  }

  @elidable(elidable.FINE) final def myAssert(condition: => Boolean): Unit = {assert(condition)}
  @elidable(elidable.FINE) final def myAssert(condition: => Boolean, msg: => String): Unit = {assert(condition, msg)}
}
