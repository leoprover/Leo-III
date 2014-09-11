package leo.datastructures.internal.terms

/**
 * Created by lex on 20.08.14.
 */

object TermBank extends Function0[TermBank] {
  private val _termbank: TermBank = ???

  def apply(): TermBank = _termbank
}


trait TermBank {
  import leo.datastructures.internal.Signature
  import leo.datastructures.internal.terms.{LOCAL, GLOBAL}

//  def local: TermBank

  def mkAtom(id: Signature#Key): Term
  def mkBound(t: Type, scope: Int): Term

  def mkTermApp(func: Term, arg: Term): Term
  def mkTermApp(func: Term, args: Seq[Term]): Term
  def mkTermAbs(t: Type, body: Term): Term

  def mkTypeApp(func: Term, arg: Type): Term
  def mkTypeApp(func: Term, args: Seq[Type]): Term
  def mkTypeAbs(body: Term): Term

  def mkApp(func: Term, args: Seq[Either[Term, Type]]): Term


  def insert(term: Term): Term = term.locality match {
    case LOCAL => insert0(term)
    case GLOBAL => term
  }
  protected[terms] def insert0(localTerm: Term): Term

  def reset(): Unit
}