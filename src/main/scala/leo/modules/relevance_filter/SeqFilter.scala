package leo.modules.relevance_filter

import leo.Configuration
import leo.datastructures.ClauseAnnotation.FromFile
import leo.datastructures._
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.parsers.Input.processFormula

/**
  * Performs a sequential run of the [[RelevanceFilter]] and returns a Sequence of
  * Clauses, considered for the RelevanceFiltering
  */
object SeqFilter {
  def apply(formulas : Iterable[AnnotatedFormula])(implicit sig: Signature) : Iterable[ClauseProxy] = {
    var res : Seq[ClauseProxy] = Seq()
    var taken : Iterable[AnnotatedFormula] = Seq()

    val initIt = formulas.iterator
    while(initIt.hasNext){
      val form = initIt.next()
      PreFilterSet.addNewFormula(form)
      if(form.role == Role_Conjecture.pretty || form.role == Role_NegConjecture.pretty){
        taken = Seq(form)
      }
    }

    var round : Int = 0
    while(taken.nonEmpty){

      // Take all formulas (save the newly touched symbols
      val newsymbs : Iterable[String] = taken.flatMap(f => PreFilterSet.useFormula(f))

      // Translate all taken formulas to clauses
      taken.foreach{f =>
        val (name, term, role) = processFormula(f)(sig)
        val nc : ClauseProxy = AnnotatedClause(Clause(Literal(term, true)), role, FromFile(Configuration.PROBLEMFILE, name), ClauseAnnotation.PropNoProp)
        res = nc +: res
      }

      // Obtain all formulas, that have a
      val possibleCandidates : Iterable[AnnotatedFormula] = PreFilterSet.getCommonFormulas(newsymbs)

      // Take the new formulas
      taken = possibleCandidates.filter(f => RelevanceFilter(round)(f))

      round += 1
    }

    res
  }
}
