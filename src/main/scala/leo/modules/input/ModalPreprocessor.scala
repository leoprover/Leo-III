package leo.modules.input

import leo.{Configuration, Out}
import leo.datastructures.TPTPAST.AnnotatedFormula
import transformation.{Wrappers => ModalProcessing}
import transformation.ModalTransformator.TransformationParameter

object ModalPreprocessor {

  final def canApply(problem0: Seq[AnnotatedFormula]): Boolean = {
    val maybeLogicSpecification = problem0.find(_.role == "logic")
    if (maybeLogicSpecification.isDefined) maybeLogicSpecification.get.symbols.contains("$modal")
    else {
      val symbolsInProblem = problem0.flatMap(_.symbols).toSet
      val boxSymbol = "$box"; val diamondSymbol = "$dia"
      symbolsInProblem.contains(boxSymbol) || symbolsInProblem.contains(diamondSymbol)
    }
  }

  final def apply(problem0: Seq[AnnotatedFormula]): Seq[AnnotatedFormula] = {
    val maybeLogicSpecification = problem0.find(_.role == "logic")
    if (maybeLogicSpecification.isDefined) {
      import java.util.logging
      val spec = maybeLogicSpecification.get
      assert(spec.symbols.contains("$modal"), "Non-classical logics other than modal logic not supported yet.")
      Out.info("Input problem is modal. Running modal-to-HOL transformation from semantics specification contained in the problem file ...")
      logging.Logger.getLogger("default").setLevel(logging.Level.WARNING)
      val result = ModalProcessing.convertModalToString(java.nio.file.Paths.get(Configuration.PROBLEMFILE))
      Input.parseProblem(result)
    } else {
      import transformation.{Wrappers => ModalProcessing}
      import java.util.logging
      Out.info("Input problem is modal. Running modal-to-HOL transformation from externally provided semantics specification ...")

      val tptpModalSystem = if (Configuration.isSet(Configuration.PARAM_MODAL_SYSTEM)) {
        if (Configuration.isSet(Configuration.PARAM_MODAL_AXIOMS)) Out.warn("Both a modal logic system and modal axioms are specified. Preferring the system over the individual axioms...")
        modalSystemToTPTPName(Configuration.MODAL_SYSTEM)
      } else if (Configuration.isSet(Configuration.PARAM_MODAL_AXIOMS)) {
        modalAxiomsToTPTPNames(Configuration.MODAL_AXIOMS)
      } else {
        Out.info(s"No modal system/modal axioms specified. Using default system ${Configuration.DEFAULT_MODALSYSTEM}.")
        modalSystemToTPTPName(Configuration.MODAL_SYSTEM)
      }
      if (!Configuration.isSet(Configuration.PARAM_MODAL_DOMAIN)) Out.info(s"No modal quantification semantics specified. Using default: ${Configuration.DEFAULT_MODALDOMAIN}.")
      val tptpModalDomain = modalDomainsToTPTPName(Configuration.MODAL_DOMAIN)
      if (!Configuration.isSet(Configuration.PARAM_MODAL_RIGIDITY)) Out.info(s"No modal rigidity specified. Using default: ${Configuration.DEFAULT_MODALRIGIDITY}.")
      val tptpModalRigidity = modalRigidityToTPTPName(Configuration.MODAL_RIGIDITY)
      if (!Configuration.isSet(Configuration.PARAM_MODAL_CONSEQUENCE)) Out.info(s"No modal consequence specified. Using default: ${Configuration.DEFAULT_MODALCONSEQUENCE}.")
      val tptpModalConsequence = modalConsequenceToTPTPName(Configuration.MODAL_CONSEQUENCE)

      val tptpModalSemanticSpecification = modalSemanticsToTPTPSpecification(tptpModalSystem,
        tptpModalDomain, tptpModalRigidity, tptpModalConsequence)

      logging.Logger.getLogger("default").setLevel(logging.Level.WARNING) // suppress logger of embedding tool
      val result = ModalProcessing.convertModalToString(java.nio.file.Paths.get(Configuration.PROBLEMFILE),
        null, null, null, tptpModalSemanticSpecification, null)
      if (Configuration.isSet("modal-debug")) {
        println(result)
        System.exit(0)
      }
      Input.parseProblem(result)
    }
  }

  private final def modalSystemToTPTPName(system: String): String = s"$$modal_system_${system.toUpperCase}"
  private final def modalAxiomsToTPTPNames(axioms: String): String = {
    val singleAxioms = axioms.split(",")
    val transformedAxioms = singleAxioms.map(ax => s"$$modal_axiom_${ax.toUpperCase}")
    transformedAxioms.mkString(",")
  }
  private final def modalDomainsToTPTPName(domains: String): String = s"$$${domains.toLowerCase}"
  private final def modalRigidityToTPTPName(rigidity: String): String = s"$$${rigidity.toLowerCase}"
  private final def modalConsequenceToTPTPName(consequence: String): String = s"$$${consequence.toLowerCase}"

  private final def modalSemanticsToTPTPSpecification(system: String, domains: String, rigidity: String, consequence: String): String = {
    s"thf(logic_spec, logic, ( $$modal :=\n    [$$constants := $rigidity,\n     $$quantification := $domains,\n     $$consequence := $consequence,\n     $$modalities := [ $system ]\n    ] ))."
  }
}
