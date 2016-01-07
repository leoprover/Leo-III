package leo

import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures._
import leo.datastructures.blackboard.{Store, FormulaStore}
import leo.datastructures.context.Context
import leo.datastructures.tptp.fof.Formula
import leo.modules.extraction_normalization.ArgumentExtraction
import leo.modules.{SZSOutput, SZSException, CLParameterParser}
import leo.modules.Utility._
import leo.modules.output.ToTPTP

import scala.collection.mutable

/**
  * Created by mwisnie on 1/6/16.
  */
object NormalizationMain {

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
        Configuration.help()
        return
      }
    }
    if (Configuration.HELP) {
      Configuration.help()
      return
    }

    // Begin of the normalization
    try {
      val forms = load(Configuration.PROBLEMFILE)

      // -------------------------------------------
      //          Datastructures
      //-------------------------------------------
      val clauses = mutable.Set[Clause]()       // All clauses of the problem file w/o rewrite
      val rewrite = mutable.Set[Literal]()      // All positive unit clauses of the problem file
      var conjecture : Option[Clause] = None            // The conjecture

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

      // --------------------------------------------
      //            Normalization
      // --------------------------------------------

      import leo.modules.extraction_normalization._

      clauses.foreach{c =>
        clauses.remove(c)
        clauses.add(Simplification(c))
      }
      rewrite.foreach{l =>
        rewrite.remove(l)
        rewrite.add(Simplification(l))
      }
      conjecture.foreach{c =>
        conjecture = Some(Simplification(c))
      }

      clauses.foreach{c =>
        clauses.remove(c)
        val (c1, units) = ArgumentExtraction(c)
        clauses.add(c1)
        units.foreach{case (l,r) => rewrite.add(Literal(l,r,true))}
      }
      rewrite.foreach{l =>
        rewrite.remove(l)
        val (l1, units) = ArgumentExtraction(l)
        rewrite.add(l1)
        units.foreach{case (l,r) => rewrite.add(Literal(l,r,true))}
      }
      conjecture.foreach{c =>
        val (c1, units) = ArgumentExtraction(c)
        conjecture = Some(c1)
        units.foreach{case (l,r) => rewrite.add(Literal(l,r,true))}
      }



      // ---------------------------------------------
      //          Output (nach Std.)
      // ---------------------------------------------

      //Typdefinitionen
      var counter : Int = 0
      val rewriteF : Seq[FormulaStore] = rewrite.toSeq.map{l => {counter += 1; Store(counter.toString, Clause(l), Role_Definition, Context(), 0, NoAnnotation)}}  // TODO is definition ok?
      val clauseF : Seq[FormulaStore] = clauses.toSeq.map{c => {counter += 1; Store(counter.toString, c, Role_Axiom, Context(), 0, NoAnnotation)}}
      val conjectureF : Seq[FormulaStore] = conjecture.toSeq.map{c => Store((counter+1).toString, c, Role_Conjecture, Context(), 0, NoAnnotation)}

      ToTPTP((rewriteF ++(clauseF ++ conjectureF))).foreach{o => println(o.output)}

      //println(s"Loaded:\n  ${forms.map(ToTPTP(_).output).mkString("\n  ")}")
    } catch {
      case e : SZSException =>  Out.output(SZSOutput(e.status, Configuration.PROBLEMFILE,e.getMessage))
    }
  }

  private def helpText : String = {
    val sb = StringBuilder.newBuilder
    sb.append("Normalize -- A Higher-Order Normalization Tool\n")
    sb.append("Christoph Benzmüller, Alexander Steen, Max Wisniewski and others.\n\n")
    sb.append("Usage: ... PROBLEM_FILE [OPTIONS]\n")
    sb.append("Options:\n")

    sb.append("-d [INT]\t\ŧ minimal depth of argument extraction.")
    sb.append("-e \t\t\ŧ if set, performs the procedure exhaustively.")

    sb.toString()
  }
}
