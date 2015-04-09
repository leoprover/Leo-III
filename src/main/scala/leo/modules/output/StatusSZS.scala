package leo
package modules.output

import leo.datastructures.Pretty

/**
 * Type of SZS status ontology values, as given by
 * [[http://www.cs.miami.edu/~tptp/cgi-bin/SeeTPTP?Category=Documents&File=SZSOntology]].
 *
 * @author Alexander Steen
 * @since 11.11.2014
 */
sealed abstract class StatusSZS extends Output with Pretty

/////////////////////////////
// Success SZS status cases
/////////////////////////////

/**
 * SZS Success status values, as given by the following ontology diagram:
 * {{{
 *                                 Success
 *                                   SUC
 *         ___________________________|_______________________________
 *        |         |    |                                  |         |
 *     UnsatPre  SatPre  |                             CtrSatPre CtrUnsatPre
 *       UNP       SAP   |                                 CSP       CUP
 *        |_______/ |    |                                  | \_______|
 *        |         |    |                                  |         |
 *     EquSat       | FiniteThm                             |     EquCtrSat
 *       ESA        |   FTH                                 |        ECS
 *        |         |   /                                   |         |
 *     Sat'ble   Theorem                                 CtrThm     CtrSat
 *       SAT       THM                                     CTH       CSA
 *      / | \______.|._____________________________________.|.______/ | \
 *     /  |         |                   |                   |         |  \
 * FinSat |         |                NoConq                 |  FinUns | FinCtrSat
 *  FSA   |         |                  NOC                  |     FUN |   FCS
 *        |         |_______________________________________|       | |
 *        |         |                   |                   |       | |
 *        |     SatAxThm             CtraAx              SatAxCth   | |
 *        |        STH                 CAX                 SCT      : |
 *       _|_________|_              ____|____              _|_________|_
 *      |      |      |            |         |            |      |  :   |
 *   Eqvlnt  TautC  WeakC      SatConCA   SatCCoCA      WkCC  UnsCon|CtrEqu
 *     EQV    TAC    WEC          SCA       SCC          WCC    UNC |  CEQ
 *    __|__   _|_   __|__        __|___   ___|__        __|__   _|_ |__|__
 *   |     | /   \ |     |      |      \ /      |      |     | /   \|     |
 *Equiv  Taut-  Weaker Weaker TauCon   WCon  UnsCon Weaker Weaker Unsat Equiv
 * Thm   ology  TautCo  Thm   CtraAx  CtraAx CtraAx CtrThm UnsCon -able CtrTh
 * ETH    TAU    WTC    WTH    TCA     WCA     UCA    WCT    WUC    UNS  ECT
 * }}}
 * taken from [[http://www.cs.miami.edu/~tptp/cgi-bin/SeeTPTP?Category=Documents&File=SZSOntology]].
 */
sealed abstract class SuccessSZS extends StatusSZS

/**
 * All models of Ax are models of C.
 * - F is valid, and C is a theorem of Ax.
 * - Possible dataforms are Proofs of C from Ax.
 */
case object SZS_Theorem extends SuccessSZS {
  val output = "Theorem"
  val pretty = "THM"
}

/**
 * Some interpretations are models of Ax, and
 * some models of Ax are models of C.
 * - F is satisfiable, and ~F is not valid.
 * - Possible dataforms are Models of Ax | C.
 */
case object SZS_Satisfiable extends SuccessSZS {
  val output = "Satisfiable"
  val pretty = "SAT"
}

/**
 * Some interpretations are models of Ax, and
 * some models of Ax are models of ~C.
 * - F is not valid, ~F is satisfiable, and C is not a theorem of Ax.
 * - Possible dataforms are Models of Ax | ~C.
 */
case object SZS_CounterSatisfiable extends SuccessSZS {
  val output = "CounterSatisfiable"
  val pretty = "CSA"
}

/**
 * All interpretations are models of Ax, and
 * all interpretations are models of ~C.
 * (i.e., no interpretations are models of C).
 * - F is unsatisfiable, ~F is valid, and ~C is a tautology.
 * - Possible dataforms are Proofs of Ax and of C, and Refutations of F.
 */
case object SZS_Unsatisfiable extends SuccessSZS {
  val output = "Unsatisfiable"
  val pretty = "UNS"
}

/**
 * No interpretations are models of Ax.
 * - F is valid, and anything is a theorem of Ax.
 * - Possible dataforms are Refutations of Ax.
 */
case object SZS_ContradictoryAxioms extends SuccessSZS {
  val output = "ContradictoryAxioms"
  val pretty = "CAX"
}

///////////////////////////////
// NoSuccess SZS status cases
///////////////////////////////


/**
 * Unsuccessful result status, as given by the NoSuccess ontology:
 * {{{
 *                                            NoSuccess
 *                                               NOS
 *                            ____________________|___________________
 *                           |                    |                   |
 *                         Open                Unknown             Assumed
 *                          OPN                  UNK             ASS(UNK,SUC)
 *                               _________________|_________________
 *                              |                 |                 |
 *                           Stopped         InProgress         NotTried
 *                             STP               INP               NTT
 *          ____________________|________________               ____|____
 *         |                    |                |             |         |
 *       Error               Forced           GaveUp           |    NotTriedYet
 *        ERR                  FOR              GUP            |        NTY
 *     ____|____            ____|____   _________|__________   |
 *    |         |          |         | |         |     |    |  |
 * OSError   InputEr      User   ResourceOut  Incompl  |  Inappro
 *   OSE       INE        USR        RSO        INC    |    IAP
 *           ___|___              ___|___             v
 *          |   |   |            |       |           to
 *      UseEr SynEr SemEr    Timeout MemyOut        ERR
 *         USE SYE SEE         TMO     MMO
 *                  |
 *              TypeError
 *                 TYE
 * }}}
 *
 * taken from [[http://www.cs.miami.edu/~tptp/cgi-bin/SeeTPTP?Category=Documents&File=SZSOntology]].
 */
sealed abstract class NoSuccessSZS extends StatusSZS

/** Software stopped due to an error. Please try to use more specific SZS status values if possible. */
case object SZS_Error extends NoSuccessSZS {
  val output = "Error"
  val pretty = "ERR"
}

/** Software stopped due to an ATP system usage error. **/
case object SZS_UsageError extends NoSuccessSZS {
  val output = "UsageError"
  val pretty = "USE"
}

case object SZS_InputError extends NoSuccessSZS {
  val output = "InputError"
  val pretty = "INE"
}

/** Software stopped due to an input syntax error. */
case object SZS_SyntaxError extends NoSuccessSZS {
  val output = "SyntaxError"
  val pretty = "SYE"
}

/** Software stopped due to an input type error (for typed logical data). */
case object SZS_TypeError extends NoSuccessSZS {
  val output = "TypeError"
  val pretty = "TYE"
}

/** Software was forced to stop by an external force. */
case object SZS_Forced extends NoSuccessSZS {
  val output = "Forced"
  val pretty = "FOR"
}

/** Software was forced to stop by the user. */
case object SZS_User extends NoSuccessSZS {
  val output = "User"
  val pretty = "USR"
}

/** Software stopped because the CPU time limit ran out. */
case object SZS_Timeout extends NoSuccessSZS {
  val output = "Timeout"
  val pretty = "TMO"
}

/** Software stopped because the memory limit ran out. */
case object SZS_MemoryOut extends NoSuccessSZS {
  val output = "MemoryOut"
  val pretty = "MMO"
}

/** Software gave up of its own accord. */
case object SZS_GaveUp extends NoSuccessSZS {
  val output = "GaveUp"
  val pretty = "GUP"
}

/** Software gave up because it cannot process this type of data. */
case object SZS_Inappropriate extends NoSuccessSZS {
  val output = "Inappropriate"
  val pretty = "IAP"
}

case object SZS_Unknown extends NoSuccessSZS {
  val output = "Unknown"
  val pretty = "UNK"
}

///////////////////////////////
// SZS Output factory methods
///////////////////////////////

/** SZS Output factory methods. */
object StatusSZS  {

  def answerLine(name : String) : Option[StatusSZS] = {
    if(name.startsWith("% SZS status")){
      apply(name.substring(13).takeWhile( _ != ' ').toString)
    } else None
  }

  def apply(name : String) : Option[StatusSZS] = name match {
    case SZS_Theorem.output => Some(SZS_Theorem)
    case SZS_Satisfiable.output => Some(SZS_Satisfiable)
    case SZS_CounterSatisfiable.output => Some(SZS_CounterSatisfiable)
    case SZS_Unsatisfiable.output => Some(SZS_Unsatisfiable)
    case SZS_ContradictoryAxioms.output => Some(SZS_ContradictoryAxioms)
    case SZS_Error.output => Some(SZS_Error)
    case SZS_InputError.output => Some(SZS_InputError)
    case SZS_SyntaxError.output => Some(SZS_SyntaxError)
    case SZS_TypeError.output => Some(SZS_TypeError)
    case SZS_Forced.output => Some(SZS_Forced)
    case SZS_User.output => Some(SZS_User)
    case SZS_Timeout.output => Some(SZS_Timeout)
    case SZS_MemoryOut.output => Some(SZS_MemoryOut)
    case SZS_GaveUp.output => Some(SZS_GaveUp)
    case SZS_Inappropriate.output => Some(SZS_Inappropriate)
    case SZS_Unknown.output => Some(SZS_Unknown)
    case _ => None
  }

  /** Create an `Output` object containing a TPTP-valid SZS-Output string for the given parameters. */
  def apply(szsStatus: StatusSZS, problemName: String, comment: String): Output =
      mkOutput(szsStatus,problemName,comment)
  /** Create an `Output` object containing a TPTP-valid SZS-Output string for the given parameters. */
  def apply(szsStatus: StatusSZS, problemName: String, comment: Output): Output =
      mkOutput(szsStatus,problemName,comment)
  /** Create an `Output` object containing a TPTP-valid SZS-Output string for the given parameters. */
  def apply(szsStatus: StatusSZS, problemName: String): Output =
    mkOutput(szsStatus,problemName,"")


  /** Create an `Output` object containing a TPTP-valid SZS-Output string for the given parameters. */
  private def mkOutput(szsStatus: StatusSZS, problemName: String, comment: String): Output = new Output {
    final val output = comment match {
      case null | "" => s"% SZS status ${szsStatus.output} for $problemName"
      case _ => s"% SZS status ${szsStatus.output} for $problemName : $comment"
    }
  }
  /** Create an `Output` object containing a TPTP-valid SZS-Output string for the given parameters. */
  private def mkOutput(szsStatus: StatusSZS, problemName: String, comment: Output): Output =
      mkOutput(szsStatus,problemName, comment.output)
}
