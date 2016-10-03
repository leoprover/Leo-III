package leo
package modules.calculus.splitting

import java.util.concurrent.atomic.AtomicInteger

import leo.datastructures.ClauseAnnotation.NoAnnotation
import leo.datastructures._
import Term._
import leo.datastructures._
import leo.modules.HOLSignature.{===, |||, Forall, &, Not, Exists}

/**
 *
 * This object offers means to generate axioms to
 * constrain the cardinality of a domain.
 *
 * A split for counter-satisfiability can be introduced
 * over enumerations of domain sizes.
 *
 */
object DomainConstrainedSplitting {

  val name : String = "domainConstrainedAxiom"
  val numb : AtomicInteger = new AtomicInteger(1)

  /**
   * Defines an axioms with the semantic
   *
   * exists (x_1,...x_n : t) forall (y : t) (y = x_1 \/ ... \/ y = x_n) /\ (x_1 != x_2 /\ .. /\ x_1 != x_n /\ x_2 != x_3 /\ ... /\ x_(n-1) != x_n)
   *
   * TODO: Maybe generate skolem variables and not quantification / Maybe not, since some provers do not like skolem varaibles
   *
   * There exist exactly n distinct objects in the domain `t`.
   *
   * @param n - Number of distinct objects
   * @param t - Domain the objects originate
   * @return A list of terms representing these axioms
   */
  def cardinalityTerms(n : Int)(t : Type) : Seq[Term]= {
    if (n<=0) {
      Out.warn("Tried to create empty domains.")
      return Nil
    }

    def equalToFirst(m : Int) : Term = m match {
      case 1 => ===(mkBound(t,1),mkBound(t,2))
      case _ => |||(===(mkBound(t,1),mkBound(t,m+1)),equalToFirst(m-1))
    }

    /*
     * Creates all unequal pairs of n integers, to say {1...n}x{1...n} \ {(1,1),...,(n,n)}
     */
    def mkPairs(n : Int) : List[(Int,Int)] = n match {
      case 1 => Nil
      case _ => (1 to (n-1)).toList.map{x => (x,n)} ::: mkPairs(n-1)
    }

    def mkAnd(l : List[Term]) : Term = l match {
      case t :: Nil => t
      case t :: ts  => &(t,mkAnd(ts))
    }

    val maximal : Term = mkExist(n)(t)(Forall(mkTermAbs(t, equalToFirst(n))))
    if (n == 1) return List(maximal)
    val distinct : Term = mkExist(n)(t)(mkAnd(mkPairs(n).map{case (a,b) => Not(===(mkBound(t,a),mkBound(t,b)))}))
    return List(maximal,distinct)
  }

  /**
   * Creates an exist quantifier over a list of variables, rather than one single variable.
 *
   * @param n - Number of variables to create
   * @param t - Type of the varibles.
   * @param term - Term to quantify over
   * @return exist (x_1 : t, ...., x_n : t) term
   */
  def mkExist(n : Int)(t : Type)(term : Term) : Term = n match {
    case 0 => term                                                                // TODO: Move whole funktion to Term
    case m if m > 0 => Exists (mkTermAbs(t, mkExist(m-1)(t)(term)))
    case _  => Out.warn("Tried to create a exist on a number of variables, but the number was negative. Creating no quantifier instead."); term
  }

  //TODO namensgebung fixen
  def cardinalityAxioms(n : Int)(t : Type) : Seq[AnnotatedClause] = {
    cardinalityTerms(n)(t).map{t => AnnotatedClause(Clause.mkClause(List(Literal.mkLit(t,true)), FromAxiom), Role_Axiom, NoAnnotation, ClauseAnnotation.PropNoProp)}
  }
}
