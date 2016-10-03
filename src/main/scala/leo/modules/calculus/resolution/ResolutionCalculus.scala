package leo.modules.calculus.resolution

import leo.datastructures._
import leo.modules.HOLSignature.{LitFalse, Not, |||, &}
/**
 * This Class supports methods for the resolution calculus.
 *
 * @author Max Wisniewski
 * @since 8/11/14
 */
object ResolutionCalculus {

  /**
   *
   * Performs one resolution inference step. It is necessary, that
   * the resolving literal is in the front of both sequences.
   *
   * @param r1 - A pre-cnf A_1, A_2, ..., A_n
   * @param r2 - A pre-cnf not A_1, B_1, ..., B_m
   * @return A_2, ... A_n, B_1, ..., B_m, if the sequences where as requiered
   */
  def resoluteInfere(r1 : Seq[Term], r2 : Seq[Term]) : Option[Seq[Term]] = {
    (r1,r2) match {
      case (h1 :: Nil, h2 :: Nil) if isNegate(h1)(h2) => Some(LitFalse() :: Nil)
      case (h1 :: l1, h2 :: l2) if isNegate(h1)(h2) => simplify(l1,l2)
      case _                                        => None
    }
  }

  private def simplify(r1 : Seq[Term], r2 : Seq[Term]) : Option[Seq[Term]] = {
    var res : List[Term] = Nil
    for(a <- r1){
      if(!r2.exists(_ == a)){
        if(r2.exists(isNegate(a))){
          return None
        }
        res = a :: res
      }
    }
    Some(res++r2)
  }

  /**
   *
   * Takes two PreCNFs and moves two resolutables two the front, if the exists.
   *
   * @param r1 - First PreCNF
   * @param r2 - Snd PreCNF
   * @return Some (permutation with resolutables in front), None if no exist
   */
  def prepareResolute(r1 : Seq[Term])(r2 : Seq[Term]) : Option[(Seq[Term], Seq[Term])] = {
    def getPair() : Option[(Term,Term)] = {
      for (a <- r1; b <- r2) {
        if(isNegate(a)(b)) return Some((a,b))
      }
      None
    }

    getPair().fold(None : Option[(Seq[Term], Seq[Term])]){case (fst,snd) =>
      val l1 : List[Term] = r1.toList.filterNot(_ == fst)
      val l2 : List[Term] = r2.toList.filterNot(_ == snd)
      Some((fst :: l1, snd :: l2))
    }
  }

  /**
   * Checks if a Term is the negate of the other
   * @param t1 - Term 1
   * @param t2 - Term 2
   * @return true, if one is the negated other
   */
  def isNegate(t1 : Term)(t2 : Term) : Boolean = {
    (t1,t2) match {
      case (Not(a1), a2) if a1 == a2 => true
      case (a1, Not(a2)) if a1 == a2 => true
      case _                        => false
    }
  }

  /**
   *
   * One step in making the Formula in conjunction normal form.
   *
   * We assume Formulas in Prenex normal form (Only and/or, not in front of literals, skolemized)
   * and eliminated quantifiers.
   *
   * Tries only to infere the first Term of the List (List = conjuction of formulas)
   *
   * @param r - Current cnf (but not normal atm)
   * @return The first is resolved and either put in list or split in two cnf's
   */
  def conjunctionNormalize(r : Seq[Term]) : Iterable[Seq[Term]] = {
    r match {
      case h :: l => h match {
        case s ||| t    => (s :: t :: l) :: Nil
        case s & t  =>  (s :: l) :: (t :: l) :: Nil
        case _        => r :: Nil
      }
      case _      => r :: Nil
    }
  }

  /**
   *
   * Prepares the term to be in the form to perform one step conjunction Normalize
   *
   * @param r - PreCNF
   * @return - r with one normalizable term in front, if one exists
   */
  def prepareNormalize(r : Seq[Term]) : Option[Seq[Term]] = {
    var next : Option[Term] = None
    def rekurse(a : List[Term]) : List[Term] = {
      a match {
        case h :: l => h match {
          case s & t =>
            next = Some(h)
            l
          case s ||| t =>
            next = Some(h)
            l
          case _  => h :: rekurse(l)
        }
        case Nil  => Nil
      }
    }
    val tail = rekurse(r.toList)

    next match{
      case Some(a) => return Some(a :: tail)
      case None =>
//        println("Could not cnf : [ "+r.map(_.pretty).mkString(" , ")+" ]")
        return None
    }

//    next.fold(None : Option[List[Term]]){a => Some(a :: tail)}
  }
}
