package leo.datastructures.internal.terms

import leo.datastructures.internal.{HOLBinaryConnective, Signature, Type, Term}

object Bound {
  def unapply(t: TermImpl): Option[(Type, Int)] = t match {
    case Root(BoundIndex(t,scope), SNil) => Some((t, scope))
    case _                               => None
  }
}

object Symbol {
  def unapply(t: TermImpl): Option[Signature#Key] = t match {
    case Root(UiAtom(id), SNil)  => Some(id)
    case Root(DefAtom(id), SNil) => Some(id)
    case _                       => None
  }
}

object TermAbstraction {
  def unapply(t: TermImpl): Option[(Type, Term)] = t match {
    case TermAbstr(t, body) => Some((t, body))
    case _                  => None
  }
}

object @@@ extends HOLBinaryConnective {
  import TermImpl.headToTerm

  val key = Integer.MIN_VALUE

  def unapply(t: TermImpl): Option[(Term, Seq[Either[Term, Type]])] = t match {
    case Root(hd, args)    => Some((headToTerm(hd), args.asTerms))
    case Redex(left, args) => Some((left, args.asTerms))
    case _                 => None
  }

//  override def apply(left: TermImpl, right: TermImpl): Term = TermImpl.mkTermApp(left,right)
}



