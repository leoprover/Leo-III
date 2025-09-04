package leo.modules.output

import leo.datastructures.Pretty

/**
  * Type of SZS dataform ontology values, as given by
  * [[http://www.cs.miami.edu/~tptp/cgi-bin/SeeTPTP?Category=Documents&File=SZSOntology]].
  * {{{
                                         LogicalData
                                            LDa
                    _________________________|____________________
                   |          |                                   |
                 None     Solution                              NotSoln
                  Non        Sol                                  NSo
           ___________________|__________________            ______|______
          |                   |                  |          |      |      |
       Proof           Interpretation        ListFrm     Assure IncoPrf IncoInt
        Prf                  Int                Lof        Ass    IPr    IIn
      ___|___                 |\            _____|_____
     |       |                | Model      |   |   |   |
  Derivn  Refutn              |  Mod    LiTHF/TFF/FOF/CNF
    Der     Ref               |/          Lth/Ltf/Lfo/Lcn
             |                |________    |___|___|___|
          CNFRef              |\       \         |
            CRf               | Partial Strictly |
                              | PIn/PMo SIn/SMo  |
                              |/_______/         |
                    __________|___________  _____|
                   |                      |/
             Domain Int/Mod       Herbrand Int/Mode
                DIn/DMo                HIn/HMo
            DPI/DPM/DSI/DSM        HPI/HPM/HSI/HSM
           ________|________          ____|____
          |        |        |        |         |
       Finite   Integer  Real     Formula  Saturation
       FIn/FMo  IIn/IMo  RIn/RMo  TIn/TMo     Sat
       FPI/FPM/ IPI/IPM/ RPI/RPM/ TPI/TPM/
       FSI/FSM  ISI/ISM  RSI/RSM  TSI/TSM}}}
  *
  *
  * @author Alexander Steen
  * @since 03.11.2017
  * @note Only relevant parts are implemented (further values may be implemented as necessary)
  */
sealed abstract class DataformSZS extends Output with Pretty {
  def longName: String
  def shortName: String
  override final def apply: String = longName
  override final def pretty: String = shortName
}

object DataformSZS {
  final def outputFormatLine(line : String) : Option[DataformSZS] = {
    if(line.startsWith("% SZS output start")) apply(line.drop(19).takeWhile(!_.isWhitespace))
    else None
  }

  final def apply(dataform: String): Option[DataformSZS] = {
    dataform match {
      case SZS_LogicalData.longName => Some(SZS_LogicalData)
      case SZS_Proof.longName => Some(SZS_Proof)
      case SZS_Refutation.longName => Some(SZS_Refutation)
      case SZS_CNFRefutation.longName => Some(SZS_CNFRefutation)
      case SZS_ListOfFormulae.longName => Some(SZS_ListOfFormulae)
      case SZS_ListOfTHF.longName => Some(SZS_ListOfTHF)
      case SZS_Assurance.longName => Some(SZS_Assurance)
      case _ => None
    }
  }
}
/** Logical data. */
case object SZS_LogicalData extends DataformSZS {
  override final val longName: String = "LogicalData"
  override final val shortName: String = "LDa"
}

/** A proof. */
case object SZS_Proof extends DataformSZS {
  override final val longName: String = "Proof"
  override final val shortName: String = "Prf"
}

case object SZS_Refutation extends DataformSZS {
  override final val longName: String = "Refutation"
  override final val shortName: String = "Ref"
}

/** A refutation in clause normal form, including, for FOF Ax or C, the
  translation from FOF to CNF (without the FOF to CNF translation it's an
  IncompleteProof). */
case object SZS_CNFRefutation extends DataformSZS {
  override final val longName: String = "CNFRefutation"
  override final val shortName: String = "CRf"
}

/** A list of formulae. */
case object SZS_ListOfFormulae extends DataformSZS {
  override final val longName: String = "ListOfFormulae"
  override final val shortName: String = "Lof"
}

/** A list of THF formulae. */
case object SZS_ListOfTHF extends DataformSZS {
  override final val longName: String = "ListOfTHF"
  override final val shortName: String = "Lth"
}

/** Only an assurance of the success ontology value. */
case object SZS_Assurance extends DataformSZS {
  override final val longName: String = "Assurance"
  override final val shortName: String = "Ass"
}
