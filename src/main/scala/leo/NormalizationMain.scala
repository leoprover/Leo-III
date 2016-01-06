package leo

import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures._
import leo.datastructures.blackboard.{Store, FormulaStore}
import leo.datastructures.context.Context
import leo.datastructures.tptp.fof.Formula
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
          if (c.lits.size == 1 && c.lits.forall(_.polarity) && c.lits.forall(_.oriented)) {
            // Rewrite Rule
            rewrite.add(c.lits.head)
          } else {
            clauses.add(c)
          }
        }
      }

      // --------------------------------------------
      //            Normalization
      // --------------------------------------------




      // ---------------------------------------------
      //          Output (nach Std.)
      // ---------------------------------------------

      val itr = rewrite.iterator
      var counter : Int = 0
      while(itr.hasNext){
        val r = itr.next
        counter += 1
        println(ToTPTP(Store((counter).toString, Clause(r), Role_Axiom, Context(), 0, NoAnnotation)).output)
      }
      val itc = clauses.iterator
      while(itc.hasNext){
        val c = itc.next
        counter += 1
        println(ToTPTP(Store((counter).toString, c, Role_Axiom, Context(), 0, NoAnnotation)).output)
      }
      conjecture.foreach(c => println(ToTPTP(Store((counter+1).toString, c, Role_Conjecture, Context(), 0, NoAnnotation)).output))


      //println(s"Loaded:\n  ${forms.map(ToTPTP(_).output).mkString("\n  ")}")
    } catch {
      case e : SZSException =>  Out.output(SZSOutput(e.status, Configuration.PROBLEMFILE,e.getMessage))
    }
  }
}
