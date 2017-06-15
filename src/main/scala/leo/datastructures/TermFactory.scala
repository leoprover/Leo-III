package leo.datastructures

/**
 * Term creation factory.
 *
 * @see [[Term]]
 *
 * @author Alexander Steen
 * @since 29.10.2014
 */
trait TermFactory {
  /** Create constant symbol term `c` where `c` is associated to identifier `id` in [[Signature]] */
  def mkAtom(id: Signature.Key)(implicit sig: Signature): Term
  /** Create bound index with de-Bruijn index `scope` and type `t` */
  def mkBound(t: Type, scope: Int): Term

  /** Create application term `(func arg)` */
  def mkTermApp(func: Term, arg: Term): Term
  /** Create application term `(func arg_1 arg_2 ... arg_n)`
    * where `args = Seq(arg_1, ..., arg_n)` */
  def mkTermApp(func: Term, args: Seq[Term]): Term
  /** Create abstraction term `(λ:t. body)` */
  def mkTermAbs(t: Type, body: Term): Term

  /** Create type application term `(func arg)` */
  def mkTypeApp(func: Term, arg: Type): Term
  /** Create type application term `(func arg_1 arg_2 ... arg_n)`
    * where `args = Seq(arg_1, ..., arg_n)` */
  def mkTypeApp(func: Term, args: Seq[Type]): Term
  /** Create abstraction term `(Λ. body)` */
  def mkTypeAbs(body: Term): Term

  /** Create term/type application term `(func arg_1 arg_2 ... arg_n)`
    * where `args = Seq(arg_1, ..., arg_n)` and `arg_i` is either a term or a type */
  def mkApp(func: Term, args: Seq[Either[Term, Type]]): Term


  // Pretty operators

  /** Creates a new term abstraction with parameter type `hd` and body `body`. Pretty variant of `mkTermAbs` */
  def \(hd: Type)(body: Term): Term = mkTermAbs(hd, body)
  /** Creates a new term abstraction with parameter type `hd` and body `body`. Pretty variant of `\` */
  def λ(hd: Type)(body: Term): Term = mkTermAbs(hd, body)
  /** Creates a nested term abstraction of the form `λ:hd.(λ:h1.(λ:h2.(...(λ:hn,body)..)))` for hi ∈ hds */
  def \(hd: Type, hds: Type*)(body: Term): Term = {
    \(hd)(hds.foldRight(body)(\(_)(_)))
  }
  /** Creates a nested term abstraction of the form `λ:hd.(λ:h1.(λ:h2.(...(λ:hn,body)..)))` for hi ∈ hds */
  def λ(hd: Type, hds: Type*)(body: Term): Term = {
    \(hd)(hds.foldRight(body)(\(_)(_)))
  }
  /** Creates a nested term abstraction of the form `λ:hd.(λ:h1.(λ:h2.(...(λ:hn,body)..)))` for hi ∈ hds */
  def λ(hds: Seq[Type])(body: Term): Term = {
    hds.foldRight(body)(\(_)(_))
  }

  /** Shorthand for `mkTypeAbs` */
  def /\(body: Term): Term = mkTypeAbs(body)
  /** Shorthand for `mkTypeAbs`, pretty variant of `/\` */
  def Λ(body: Term): Term = mkTypeAbs(body)

}
