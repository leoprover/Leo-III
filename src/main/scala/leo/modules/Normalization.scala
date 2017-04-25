package leo.modules

import leo.Configuration
import leo.datastructures._
import leo.modules.Utility.termToClause
import leo.modules.output.SZS_UsageError
import leo.modules.parsers.Input

/**
  * Created by lex on 4/25/17.
  */
object Normalization {
  type Definition = (Signature#Key, Term)
  type Axiom = Term
  type Conjecture = Term

  final def apply(): Unit = {
    implicit val sig: Signature = Signature.freshWithHOL()
    val input0 = Input.parseProblem(Configuration.PROBLEMFILE)
    val (defs, axioms0, conjecture) = effectiveInput(input0)

    var axioms = axioms0
    val defsIt = defs.iterator
    while (defsIt.hasNext) {
      val (id, definition) = defsIt.next()
      val (newDef, additionalAx) = processConjecture(definition)
      axioms = axioms union additionalAx
      sig.addDefinition(id, newDef)
    }
    val newConjecture = if (conjecture != null) {
      val (newConj, additionalAxioms) = processConjecture(conjecture)(sig)
      axioms = axioms union additionalAxioms
      newConj
    } else null

    var resultClauses: Set[Clause] = Set.empty
    val axiomsIt = axioms.iterator
    while (axiomsIt.hasNext) {
      val ax = axiomsIt.next()
      resultClauses = resultClauses union process(ax)(sig)
    }

    import leo.modules.external.{createTHFProblem, TPTPProblem}
    val newProb = if (newConjecture == null) createTHFProblem(resultClauses, TPTPProblem.WITHDEF)
    else createTHFProblem(resultClauses, TPTPProblem.WITHDEF, termToClause(newConjecture))

    println(newProb)
  }

  private final def process(t: Term)(implicit sig: Signature): Set[Clause] = {
    import leo.modules.calculus.{Simp, Miniscope, ArgumentExtraction, RenameCNF, freshVarGen}
    // Simplification
    val simplified = Simp.normalize(t)
    // Miniscope
    val mini = Miniscope.apply(simplified, true)
    // Argument extraction
    val (newC, additionalAxioms) = ArgumentExtraction.apply(mini, ???,???)
    val cl = termToClause(newC)
    val vargen = freshVarGen(cl)
    val res0 = RenameCNF.apply(vargen, termToClause(newC), ???)
    res0.toSet union additionalAxioms.map(Clause.apply).toSet
  }

  private final def processConjecture(c: Conjecture)(implicit sig: Signature): (Term, Set[Term]) = {
    import leo.modules.calculus.{Simp, Miniscope, ArgumentExtraction}
    // Simplification
    val simplified = Simp.normalize(c)
    // Miniscope
    val mini = Miniscope.apply(simplified, true)
    // Argument extraction
    val (newC, additionalAxioms) = ArgumentExtraction.apply(mini, ???,???)
    (newC, additionalAxioms.map(Literal.asTerm).toSet)
  }

  private final def effectiveInput(input: Seq[tptp.Commons.AnnotatedFormula])
                                  (implicit sig: Signature): (Set[Definition], Set[Axiom], Conjecture) = {
    var definitions: Set[Definition] = Set.empty
    var axioms: Set[Axiom] = Set.empty
    var conj: Conjecture = null

    val inputIt = input.iterator
    while (inputIt.hasNext) {
      val formula = inputIt.next()
      formula.role match {
        case Role_Type.pretty => Input.processFormula(formula)(sig)
        case Role_Definition.pretty =>
          import leo.modules.HOLSignature.===
          import leo.datastructures.Term.Symbol
          val alteredFormula = formula.updateRole("axiom")
          val (_, f, _) =  Input.processFormula(alteredFormula)(sig)
          f match {
            case ===(Symbol(id), definition) => definitions = definitions + ((id, definition))
            case _ => axioms = axioms + f
          }
        case Role_Conjecture.pretty =>
          if (conj == null) {
            val (_,f,_) = Input.processFormula(formula)(sig)
            conj = f
          } else throw new SZSException(SZS_UsageError, "Only one conjecture allowed per problem.")
        case _ =>
          val (_,f,_) = Input.processFormula(formula)(sig)
          axioms = axioms + f
      }
    }
    (definitions, axioms, conj)
  }
}
