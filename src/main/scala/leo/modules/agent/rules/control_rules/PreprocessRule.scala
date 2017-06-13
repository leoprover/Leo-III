package leo.modules.agent.rules
package control_rules
import leo.Out
import leo.datastructures._
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.GeneralState
import leo.modules.control.Control
import leo.modules.proof_object.CompressProof

/**
  * Created by mwisnie on 6/13/17.
  */
class PreprocessRule(inType : DataType[AnnotatedClause],
                     outType : DataType[AnnotatedClause])
                    (implicit val state : GeneralState[AnnotatedClause])
  extends Rule{

  override val name: String = s"Preprocess(${inType} -> ${outType})"

  override val inTypes: Seq[DataType[Any]] = Seq(inType)

  override val moving: Boolean = true

  override val outTypes: Seq[DataType[Any]] = Seq(outType)

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType) // Todo Updates??
    val it = ins.iterator


    var hints : Seq[Hint] = Seq()
    // Experiment with batch sizes
    while(it.hasNext){
      val c = it.next()
      hints = new PreprocessHint(c) +: hints
    }
    hints
  }

  class PreprocessHint(c : AnnotatedClause) extends Hint {
    override def apply(): Delta = {
      val r = Result()
      r.remove(inType)(c)
      val ups = preprocess(c)
      // Update the initial set for state
      // TODO Move to blackboard??
      state.addInitial(ups)
      val it = ups.iterator
      while(it.hasNext){
        val c1 = it.next()
        if(!Clause.trivial(c1.cl)) {
          leo.Out.debug(s"[Preprocess] Clause preprocessed \n  ${c.pretty(state.signature)}\n  -->\n   ${
            CompressProof.compressAnnotation(c1)(CompressProof.lastImportantStep(CompressProof.stdImportantInferences))
            .pretty(state.signature)}")
          r.insert(outType)(c1)
        } else {
          leo.Out.debug(s"[Preprocess] Clause was trivial \n  ${c.pretty(state.signature)}")
        }
      }
      r
    }
    override val read: Map[DataType[Any], Set[Any]] = Map()
    override val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(c))
  }

  // Update with SeqLoop
  final def preprocess(cur: AnnotatedClause): Set[AnnotatedClause] = {
    implicit val sig: Signature = state.signature
    var result: Set[AnnotatedClause] = Set()

    // Fresh clause, that means its unit and nonequational
    assert(Clause.unit(cur.cl), "clause not unit")
    val lit = cur.cl.lits.head
    assert(!lit.equational, "initial literal equational")

    // Def expansion and simplification
    val expanded = Control.expandDefinitions(cur)
    val polarityswitchedAndExpanded = Control.switchPolarity(expanded)
    // We may instantiate here special symbols for universal variables
    // Its BEFORE miniscope because their are less quantifiers and maybe
    // some universal quantification may vanish after extensional instantiation
    // Run simp here again to eliminate connectives with true/false as operand due
    // to ext. instantiation.
    result = Control.specialInstances(polarityswitchedAndExpanded)

    result = result.flatMap { cl =>
      Control.cnf(Control.miniscope(cl))
    }

    result = result.map {cl =>
      leo.Out.trace(s"[Choice] Search for instance in ${cl.id}")
      val isChoiceSpec = Control.detectChoiceClause(cl)(state)
      if (isChoiceSpec) {
        // replace clause by a trivial one: [[true]^t]
        leo.Out.debug(s"[Choice] Removed ${cl.id}")
        import leo.modules.HOLSignature.LitTrue
        AnnotatedClause(leo.modules.termToClause(LitTrue), ClauseAnnotation.FromSystem("redundant"))
      } else cl
    }
    // Add detected equalities as primitive ones
    result = result union Control.convertDefinedEqualities(result)

    // To equation if possible and then apply func ext
    // AC Simp if enabled, then Simp.
    result = result.map { cl =>
      var result = cl
      result = Control.liftEq(result)
      result = Control.funcext(result) // Maybe comment out? why?
    val possiblyAC = Control.detectAC(result)
      if (possiblyAC.isDefined) {
        val symbol = possiblyAC.get._1
        val spec = possiblyAC.get._2
        val sig = state.signature
        val oldProp = sig(symbol).flag
        if (spec) {
          Out.trace(s"[AC] A/C specification detected: ${result.id} is an instance of commutativity")
          sig(symbol).updateProp(addProp(Signature.PropCommutative, oldProp))
        } else {
          Out.trace(s"[AC] A/C specification detected: ${result.id} is an instance of associativity")
          sig(symbol).updateProp(addProp(Signature.PropAssociative, oldProp))
        }
      }
      result = Control.acSimp(result)
      result = Control.simp(result)
      if (!state.isPolymorphic && result.cl.typeVars.nonEmpty) state.setPolymorphic()
      result
    }
    // Pre-unify new clauses or treat them extensionally and remove trivial ones
    result = Control.extPreprocessUnify(result)(state)
    result = result.filterNot(cw => Clause.trivial(cw.cl))
    result
  }
}
