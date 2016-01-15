package leo

import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures._
import leo.datastructures.blackboard.{Store, FormulaStore}
import leo.datastructures.context.Context
import leo.datastructures.tptp.fof.Formula
import leo.modules.extraction_normalization._
import leo.modules.{SZSOutput, SZSException, CLParameterParser}
import leo.modules.Utility._
import leo.modules.output.ToTPTP
import leo.datastructures.impl.Signature

import scala.collection.mutable

/**
  * Created by mwisnie on 1/6/16.
  */
object NormalizationMain {

  /**
    * Set of clauses of the problem w/o rewrite
    */
  val clauses = mutable.Set[Clause]()

  /**
    * All positive unit clauses of the problem
    */
  val rewrite = mutable.Set[Literal]()

  /**
    * Conjecture of the problem
    */
  var conjecture : Option[Clause] = None

  var extract0r : ArgumentExtraction = ArgumentExtraction

  /**
    *
    * Reads from an input file and performs
    * <ol>
    *   <li> Argument Extraction </li>
    *   <li> Unit Clause Application </li>
    *   <li> Simplification </li>
    *   <li> ... </li>
    * </ol>
    *
    * @param args - See [[Configuration]] for argument treatment
    */
  def main(args : Array[String]): Unit ={
    try {
      Configuration.init(new CLParameterParser(args))
    } catch {
      case e: IllegalArgumentException => {
        Out.severe(e.getMessage)
        println(helpText)
        return
      }
    }
    if (Configuration.HELP) {
      println(helpText)
      return
    }

    // Begin of the normalization
    try {
      val forms = load(Configuration.PROBLEMFILE)

      Configuration.valueOf("d").foreach
      {s =>
        s.headOption.foreach
        {i =>
          try{
            val iv = i.toInt
            extract0r = new ArgumentExtraction({ x => x.size <= iv})    // NOTE : Change to modify maximal size. (Add debug output)
          } catch {
            case _ : Exception => ()
          }
        }
      }

      // -------------------------------------------
      //          Datastructures
      //-------------------------------------------


      val it : Iterator[FormulaStore] = forms.iterator
      while(it.hasNext){
        val f = it.next()
        if(f.role == Role_Conjecture) {
          conjecture = Some(f.clause)
        } else if (f.role == Role_NegConjecture) {
          conjecture = Some(f.clause.mapLit(_.flipPolarity))
        } else {
          val c = f.clause
          if (c.lits.size == 1 && c.lits.forall(_.equation) && c.lits.forall(_.oriented) && c.lits.forall(_.equational)) {
            // Rewrite Rule TODO better filter (if equational, aber left hat keine Boolschen variablen ... )
            rewrite.add(c.lits.head)
          } else {
            clauses.add(c)
          }
        }
      }

      if(Configuration.isSet("def")) {
        val s = Signature.get
        s.allUserConstants.foreach { k =>
          val m = s(k)
          m.defn.foreach { t =>
            rewrite.add(Literal(Term.mkAtom(k), t, true))
          }
        }
      }

      val argExt : Boolean = Configuration.isSet("a")
      val simpl : Boolean = Configuration.isSet("s")
      val rename : Boolean = Configuration.isSet("r")

      // --------------------------------------------
      //            Normalization
      // --------------------------------------------

      import leo.modules.extraction_normalization._

      var change = true

      while(change) {
        change = false
        if(simpl) change |= simplifyAll
        if(argExt) change |= extractAll
        if(rename) change |= renameAll
      }
      if(Configuration.isSet("p")){
        fullNormalizeAll
      }

      if(Configuration.isSet("e"))
        extensionalRewrite

      // ---------------------------------------------
      //          Output (nach Std.)
      // ---------------------------------------------

      //Typdefinitionen
      var counter : Int = 0
      val rewriteF : Seq[FormulaStore] = rewrite.toSeq.map{l => {counter += 1; Store(counter.toString, Clause(l), Role_Axiom, Context(), 0, NoAnnotation)}}  // TODO is definition ok?
      val clauseF : Seq[FormulaStore] = clauses.toSeq.map{c => {counter += 1; Store(counter.toString, c, Role_Axiom, Context(), 0, NoAnnotation)}}
      val conjectureF : Seq[FormulaStore] = conjecture.toSeq.map{c => Store((counter+1).toString, c, Role_Conjecture, Context(), 0, NoAnnotation)}

      //TODO Print and Format the time need for normalization
      ToTPTP((rewriteF ++(clauseF ++ conjectureF)), !Configuration.isSet("def")).foreach{o => println(o.output)}

      //println(s"Loaded:\n  ${forms.map(ToTPTP(_).output).mkString("\n  ")}")
    } catch {
      case e : SZSException =>  Out.output(SZSOutput(e.status, Configuration.PROBLEMFILE,e.getMessage))
    }
  }

  private def renameAll : Boolean = {
    var change = false
    val delta = Configuration.valueOf("rdelta").fold(1)(s => s.headOption.fold(1) { h =>
      try{
        h.toInt
      } catch {
        case _ : Exception => 1
      }
    })

    clauses.toSeq.foreach{ c =>
      clauses.remove(c)
      val c1 = Simplification(DefExpansion(c))
      val (nc, defs) = FormulaRenaming(c1, delta)
      clauses.add(nc)
      defs.foreach(clauses.add(_))
      change |= defs.nonEmpty
      //if(defs.nonEmpty) println(c.pretty+" defs:\n  "+defs.map(_.pretty).mkString("\n  "))
    }
    rewrite.toSeq.foreach{ l =>
      rewrite.remove(l)
      val l1 = Simplification(DefExpansion(l))
      val (lc, defs) = FormulaRenaming(l1, delta)
      rewrite.add(lc)
      defs.foreach(clauses.add(_))
      change |= defs.nonEmpty
      //if(defs.nonEmpty) println(l.pretty+" defs:\n  "+defs.map(_.pretty).mkString("\n  "))
    }
    conjecture.foreach{c =>
      val c1 = Simplification(DefExpansion(c))
      val (nc, defs) = FormulaRenaming(c1, delta)
      conjecture = Some(nc)
      defs.foreach(clauses.add(_))
      change |= defs.nonEmpty
      //if(defs.nonEmpty) println(c.pretty+" defs:\n  "+defs.map(_.pretty).mkString("\n  "))
    }
    change
  }

  /**
    * Simplifies all clauses (clauses, rewrite, conjecutre) of the problem.
    *
    * @return true if anything changed
    */
  private def simplifyAll : Boolean = {
    var change = false
    clauses.toSeq.foreach { c =>
      clauses.remove(c)
      val c1 = Simplification(c)
      clauses.add(c1)
      change |= c != c1
    }
    rewrite.toSeq.foreach { l =>
      rewrite.remove(l)
      val l1 = Simplification(l)
      rewrite.add(l1)
      change |= l != l1
    }
    conjecture.foreach { c =>
      val c1 = Simplification(c)
      conjecture = Some(c1)
      change |= c != c1
    }
    change
  }

  private def fullNormalizeAll : Boolean = {
    extensionalRewrite
    var change = false
    clauses.toSeq.foreach { c =>
      clauses.remove(c)
      val c1 = Simplification.polarityNorm(PrenexNormal(Skolemization(NegationNormal(Simplification(DefExpansion(Simplification(c)))))))
      //println(s"  ${c.pretty}\nprenex to\n  ${c1.pretty}\n")
      clauses.add(c1)
      change |= c != c1
    }
    rewrite.toSeq.foreach { l =>
      rewrite.remove(l)
      val l1 = Simplification.polarityNorm(PrenexNormal(Skolemization(NegationNormal(Simplification(DefExpansion(Simplification(l)))))))
      rewrite.add(l1)
      change |= l != l1
    }
    conjecture.foreach { c =>
      val cn = c.mapLit(_.flipPolarity)
      //println(s"Original: ${cn.pretty}")
      val cs = Simplification(cn)
      //println(s"Simp : ${cs.pretty}")
      val cd = DefExpansion(cs)
      //println(s"DefExp : ${cd.pretty}")
      val cs2 = Simplification(cd)
      //println(s"Simp : ${cs2.pretty}")
      val cnn = NegationNormal(cs2)
      //println(s"Neg : ${cnn.pretty}")
      val csk = Skolemization(cnn)
      //println(s"Skol : ${csk.pretty}")
      val cp = PrenexNormal(csk)
      //println(s"Prenex : ${cp.pretty}")
      assert(cp.lits.size == 1, "Conjecture was splitted.")
      val c1 = Simplification.polarityNorm(cp.mapLit(_.flipPolarity))
      //println(s"Simp : ${c1.pretty}")
      conjecture = Some(c1)
      change |= c != cp
    }
    change
  }

  /**
    * Extracts in one round from each Formula in the current state the arguments.
    *
    * @return
    */
  private def extractAll : Boolean = {
    var change = false

    clauses.toSeq.foreach { c =>
      clauses.remove(c)
      val (c1, units) = extract0r(c)
      clauses.add(c1)
      units.foreach { case (l, r) => rewrite.add(Literal(l, r, true)) }
      change |= units.isEmpty
    }
    rewrite.toSeq.foreach { l =>
      rewrite.remove(l)
      val (l1, units) = extract0r(l)
      rewrite.add(l1)
      units.foreach { case (l, r) => rewrite.add(Literal(l, r, true)) }
      change |= units.isEmpty
    }
    conjecture.foreach { c =>
      val (c1, units) = extract0r(c)
      conjecture = Some(c1)
      units.foreach { case (l, r) => rewrite.add(Literal(l, r, true)) }
      change |= units.isEmpty
    }
    change
  }

  /**
    * Performs boolean and functional extension on rewrite rules.
    */
  private def extensionalRewrite : Unit = {
    import leo.modules.calculus._
    import leo.modules.seqpproc.{FuncExt, BoolExt}
    rewrite.toSeq.foreach{ l : Literal =>
      rewrite.remove(l)
      val fun_l : Literal= if(FuncExt.canApply(l)) FuncExt(freshVarGen(Clause(l)), Seq(l)).headOption.fold(l)(l1 => l1) else l
      if(BoolExt.canApply(fun_l)) {
        val (bool_t, bool_f) = BoolExt(fun_l)
        clauses.add(Clause(bool_t))
        clauses.add(Clause(bool_f))
      } else {
        clauses.add(Clause(fun_l))
      }
    }
  }

  private def helpText : String = {
    val sb = StringBuilder.newBuilder
    sb.append("Normalize -- A Higher-Order Normalization Tool\n")
    sb.append("Christoph Benzmüller, Alexander Steen, Max Wisniewski and others.\n\n")
    sb.append("Usage: ... PROBLEM_FILE [OPTIONS]\n")
    sb.append("Options:\n")
    sb.append("-e \t\tFull extensional handeling for rewrite rules.\n")
    sb.append("-a \t\tEnables argument extraction.\n")
    sb.append("-s \t\tEnables simplification\n")
    sb.append("-p \t\tTranslates the problem into a prenex normal form.\n")
    sb.append("-r \t\tFormula renaming enable.\n")
    sb.append("--rdelta \t\tOffset for the renaming to be triggered.\n")
    sb.append("--def \t\tHandles definitions as unit equations.\n")
    sb.append("-d N \t\tMaximal depth of argument extraction\n")
    sb.append("--ne N \t\tNon exhaustively.  Will iterate N(=1 std) times.\n")
    sb.append("-h \t\tDisplay this help message\n")

    sb.toString()
  }
}
