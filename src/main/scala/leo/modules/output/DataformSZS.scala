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
sealed abstract class DataformSZS extends Output with Pretty

/** Logical data. */
case object SZS_LogicalData extends DataformSZS {
  val apply = "LogicalData"
  val pretty = "LDa"
}

/** A proof. */
case object SZS_Proof extends DataformSZS {
  val apply = "Proof"
  val pretty = "Prf"
}

/** A refutation in clause normal form, including, for FOF Ax or C, the
  translation from FOF to CNF (without the FOF to CNF translation it's an
  IncompleteProof). */
case object SZS_CNFRefutation extends DataformSZS {
  val apply = "CNFRefutation"
  val pretty = "CRf"
}

/** A list of formulae. */
case object SZS_ListOfFormulae extends DataformSZS {
  val apply = "ListOfFormulae"
  val pretty = "Lof"
}

/** A list of THF formulae. */
case object SZS_ListOfTHF extends DataformSZS {
  val apply = "ListOfTHF"
  val pretty = "Lth"
}

/** Only an assurance of the success ontology value. */
case object SZS_Assurance extends DataformSZS {
  val apply = "Assurance"
  val pretty = "Ass"
}
