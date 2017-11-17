package leo.modules.modes

import leo.Configuration
import leo.datastructures._
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.output.SZS_UsageError
import leo.modules.parsers.Input
import leo.modules.prover.State
import leo.modules.{SZSException, termToClause}

/**
  * Created by lex on 4/25/17.
  */
object Normalization {
  type Definition = (Signature.Key, Term)
  type Axiom = Term
  type Conjecture = Term

  val renamingThreshold: Int = Configuration.RENAMING_THRESHHOLD
  val doCNF: Boolean = Configuration.isSet("cnf")
  val extractionType: Int = Configuration.EXTRACTION_TYPE
  val localXtract: Boolean = Configuration.isSet(s"xLocal")

  final def apply(parsedProblem: scala.Seq[AnnotatedFormula]): Unit = {
    implicit val sig: Signature = Signature.freshWithHOL()
    implicit val s : State[AnnotatedClause] = State.fresh(sig)
    val (defs, axioms0, conjecture) = effectiveInput(parsedProblem)

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
      resultClauses = resultClauses union process(ax)(s)
    }
    val finalAxResult = exhaustive(resultClauses)

    import leo.modules.external.{TPTPProblem, createTHFProblem}
    val newProb = if (newConjecture == null) createTHFProblem(finalAxResult, TPTPProblem.WITHDEF)
    else createTHFProblem(finalAxResult, TPTPProblem.WITHDEF, termToClause(newConjecture))

    println(newProb)
  }

  private final def exhaustive(cls: Set[Clause])(implicit s: State[AnnotatedClause]): Set[Clause] = {
    import leo.modules.calculus._
    implicit val sig: Signature = s.signature
    var changed = true
    var intermediate: Set[Clause] = cls

    while(changed) {
      changed = false
      val clsIt = intermediate.iterator
      intermediate = Set.empty
      while (clsIt.hasNext) {
        val cl = clsIt.next()
//        println(s"Process ${cl.pretty(sig)}")
        // Lift Eq first

        val (ca3, pLits, nLits, oLits) = LiftEq.canApply(cl)
        val liftCl = if(ca3) {
          Clause(LiftEq.apply(pLits, nLits, oLits))
        } else cl

//        println(s"  After lift ${liftCl.pretty(sig)}")

        val (ca, fELits, otherLits) = FuncExt.canApply(liftCl)
        val funcCl = if (ca) {
          changed = true
          val vargen = freshVarGen(liftCl)
          val funcLits = FuncExt(vargen, fELits)
          Clause(funcLits ++ otherLits)
        } else liftCl

//        println(s"  After funcExt ${funcCl.pretty(sig)}")

        val (ca2, bELits, otherLits2) = BoolExt.canApply(funcCl)
        val boolExtCls = if (ca2) {
          changed = true
          BoolExt(bELits, otherLits2)
        } else Set(funcCl)

//        println(s"  After boolExt\n    ${boolExtCls.map(c => c.pretty(sig)).mkString("\n    ")}")

        val cnf = if (doCNF) {boolExtCls.flatMap{c =>
          if (RenameCNF.canApply(c)) {
            changed = true
            RenameCNF(freshVarGen(c), s.renamingCash, c, renamingThreshold)
          } else Set(c)
        }} else boolExtCls

//        println(s"  After cnf\n    ${cnf.map(c => c.pretty(sig)).mkString("\n    ")}")

        intermediate = intermediate union cnf
      }
    }

    intermediate
  }

  private final def process(t: Term)(implicit s: State[AnnotatedClause]): Set[Clause] = {
    import leo.modules.calculus._
    // Simplification
    implicit val sig: Signature = s.signature
    val simplified = Simp.normalize(t)
    // Miniscope
    val mini = Miniscope.apply(simplified, true)
    // Argument extraction
    val (newC, additionalAxioms) = ArgumentExtraction.apply(mini, localXtract, extractionType)
    val x = additionalAxioms.flatMap(ax => process(Literal.asTerm(ax))).toSet
    // Renaming and CNF
    if (doCNF) {
      val cl = termToClause(newC)
      val vargen = freshVarGen(cl)
      val res0 = RenameCNF.apply(vargen, s.renamingCash, termToClause(newC), renamingThreshold)
      res0.toSet union x
    } else {
      val cl = termToClause(newC)
      x + cl
    }

  }

  private final def processConjecture(c: Conjecture)(implicit sig: Signature): (Term, Set[Term]) = {
    import leo.modules.calculus.{ArgumentExtraction, Miniscope, Simp}
    // Simplification
    val simplified = Simp.normalize(c)
    // Miniscope
    val mini = Miniscope.apply(simplified, true)
    // Argument extraction
    val (newC, additionalAxioms) = ArgumentExtraction.apply(mini, localXtract, extractionType)
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
          import leo.datastructures.Term.Symbol
          import leo.modules.HOLSignature.===
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
