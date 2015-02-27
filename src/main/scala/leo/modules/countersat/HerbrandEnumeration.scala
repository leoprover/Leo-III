package leo
package modules.countersat

import leo.datastructures._
import leo.datastructures.impl.Signature
import leo.datastructures.term.Term._
import leo.datastructures.term.Term

/**
 *
 * Object, that allows the replacement of
 * Quantifiers, by a given finite Domain.
 *
 * @author Max Wisniewski
 * @since 2/26/15
 */
object FiniteHerbrandEnumeration {

  /**
   * Replaces a Quantifier
   *
   * E/F (x:D) . P
   *
   * over D = {x_1, ..., x_n
   * with
   * P x_1 OR/AND ... P x_n
   *
   * @param c - clause to rid of Quantifier
   * @param domain - Map of a type to the objects he posses. for t:Type domain(t) = {c_1, ..., c_n} each c_i should have type t.
   * @return
   */
  def replaceQuant(c : Clause, domain : Map[Type, Seq[Term]]) : Clause = c.mapLit(_.termMap(replaceQuant(_, generateReplace(domain)).betaNormalize))

  private def replaceQuant(t : Term, domain : Map[Type, (Term,Term)]) : Term = t match {
    case Exists(p) if domain.contains(p.ty._funDomainType) => mkTermApp(domain(p.ty._funDomainType)._1, replaceQuant(p, domain))
    case Forall(p) if domain.contains(p.ty._funDomainType) => mkTermApp(domain(p.ty._funDomainType)._2, replaceQuant(p,domain))

      //If it is not a quantifier, continue iterating
    case s@Symbol(_)       => s
    case s@Bound(_,_)      => s
    case s @@@ t            => mkTermApp(replaceQuant(s, domain),replaceQuant(t,domain))  // Should not happen after beta normalize, unless s is irreduceable
    case f ∙ args           => mkApp(replaceQuant(f,domain), args.map(_.fold({t => Left(replaceQuant(t,domain))},(Right(_)))))
    case s @@@@ ty          => mkTypeApp(replaceQuant(s,domain), ty)
    case ty :::> s        => mkTermAbs(ty, replaceQuant(s,domain))
    case TypeLambda(t)    => mkTypeAbs(replaceQuant(t,domain))
  }

  /**
   * Generates suitable lambda expressions for the replacement.
   * t -> \z. z a ? z b ? z c ...
   * where the first element of the tupel has AND as ? and the second one has OR.
   * @param domain
   * @return
   */
  private def generateReplace(domain : Map[Type, Seq[Term]]) : Map[Type, (Term,Term)] = {
    domain.toList.map{x => (x._1, replace(x._1, x._2))}.toMap
  }

  private def replace(t : Type, iterable: Seq[Term]) : (Term,Term) = {
    val p = mkBound(t ->: Signature.get.o, 1)
    val applied = iterable.map{c => mkTermApp(p, c)}.toList
    (mkTermAbs(t, foldBy(|||, applied)), mkTermAbs(t, foldBy(&, applied)))
  }

  private def foldBy(t : Term, l : List[Term]) : Term = l match {
    case h :: Nil => h
    case h :: hs => mkTermApp(t, List(h, foldBy(t, hs)))
    case Nil => if(t == |||) LitFalse else LitTrue     // Should never occur
  }

}
