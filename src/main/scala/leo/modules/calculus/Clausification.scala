package leo.modules.calculus

import leo.datastructures._

/**
 *
 * Performs a step wise clausification, if there are no quantifiers involved.
 *
 * @author Max Wisniewski
 * @since 1/12/15
 */
object Clausification extends UnaryCalculusRule[Seq[Clause]]{
  def clausify(c : Clause) : Seq[Clause] = {
    val clausilits : Seq[Literal] = c.lits.filter(clausifiable)
    if(!clausilits.isEmpty) {
      val mLit = clausilits.max
      val pol = mLit.polarity
      val rLits = rmEl(c.lits, mLit)
      mLit.term match {
        case (a & b) if pol => return List(updateLits(c, appendLit(rLits, a,pol)), updateLits(c, appendLit(rLits, b, pol)))
        case (a & b) => return List(updateLits(c, appendLit(appendLit(rLits, a, pol), b, pol)))
        case (a ||| b) if !pol => return List(updateLits(c, appendLit(rLits, a,pol)), updateLits(c, appendLit(rLits, b, pol)))
        case (a ||| b) if pol => return List(updateLits(c, appendLit(appendLit(rLits, a, pol), b, pol)))
        case Not(a) => return List(updateLits(c, appendLit(rLits, a, !pol)))
        case _ => return Nil
      }
    }
    Nil
  }

  def canApply(cl: Clause) = cl.lits.exists(clausifiable(_))
  def apply(cl: Clause) = clausify(cl)
  val name = "cnf"

  private def updateLits(c : Clause, l : Seq[Literal]) : Clause = Clause.mkClause(l, c.implicitBindings, Derived)

  private def appendLit(ls : Seq[Literal], t : Term, pol : Boolean) : Seq[Literal] = Literal(t, pol) +: ls

  private def rmEl[A](l : Seq[A], a : A) : Seq[A]= l match {
      case Nil => Nil
      case (x +: xs) => if (x == a) xs else x +: rmEl(xs,a)
    }

  /**
   * A Literal is possibly clausifiable, if it is a toplevel conjunction / disjuntion.
   *
   * @param l - The literal to check
   * @return true iff it is disjuntiv / conjunctive
   */
  private def clausifiable(l : Literal) : Boolean = l.term match {
    case (a & b) => true
    case (a ||| b) => true
    case (Not(a))   => true
    case _        => false
  }
}
