package leo.datastructures

/**
 * Term index data structure
 *
 * @author Alexander Steen
 * @since 16.10.2014
 */
object TermIndex {
  protected[TermIndex] var termset: Set[Term] = Set.empty
  protected[TermIndex] var symbol_of: Map[Signature#Key, Set[Term]] = Map.empty
  protected[TermIndex] var headsymbol_of: Map[Term, Set[Term]] = Map.empty
  protected[TermIndex] var occurs_in: Map[Term, Set[(Term, Position)]] = Map.empty
  protected[TermIndex] var occurs_at: Map[Term, Map[Position, Set[Term]]] = Map.empty


  def terms: Set[Term] = termset
  def contains(t: Term): Boolean = termset.contains(t)

  def insert(term: Term): Term = {
    val t = term.betaNormalize.etaExpand
    val t2 = if (!Term.contains(t))
      Term.insert(t)
     else
      t

    // Force computation of lazy values
    t2.headSymbol
    t2.freeVars
    t2.occurrences

    // insert to data structures
    for (s <- t2.symbols) {
      symbol_of.get(s) match {
        case None => symbol_of += ((s,Set(t2)))
        case Some(set) => symbol_of += ((s, set + t2))
      }
    }
    val hs = t2.headSymbol
    headsymbol_of.get(hs) match {
      case None => headsymbol_of += ((hs, Set(t2)))
      case Some(set) => headsymbol_of += ((hs, set + t2))
    }
    insertSubterms(t2, t2, Position.root)
    termset += t2

    t2
  }

  def byHeadsymbol(head: Term): Set[Term] = headsymbol_of.getOrElse(head, Set())
  def bySymbol(sym: Signature#Key): Set[Term] = symbol_of.getOrElse(sym, Set())

  def bySubterm(subterm: Term): Set[(Term, Position)] = occurs_in.getOrElse(subterm, Set())
  def bySubtermAtPos(subterm: Term, pos: Position): Set[(Term)] = occurs_at.get(subterm) match {
    case None => Set()
    case Some(inner) => inner.getOrElse(pos, Set())
  }

  protected def insertSubterms(term: Term, subterm: Term, position: Position): Unit = {
    occurs_in.get(subterm) match {
      case None => occurs_in += ((subterm, Set((term, position))))
      case Some(set) => occurs_in += ((subterm, set + ((term, position))))
    }

    occurs_at.get(subterm) match {
      case None => occurs_at += ((subterm, Map((position, Set(term)))))
      case Some(inner) => inner.get(position) match {
        case None => occurs_at += ((subterm, inner + ((position, Set(term)))))
        case Some(set) => occurs_at += ((subterm, inner + ((position, set + term))))
      }
    }
    import Term.{Bound, Symbol, ∙, :::>, TypeLambda}
    subterm match {
      case Bound(t,scope) => ()
      case Symbol(id)     => ()
      case f ∙ args       => insertSubterms(term, f, position.headPos); var i = 1
                              for(arg <- args) {
                                arg match {
                                  case Left(t) => insertSubterms(term, t, position.argPos(i)); i = i+1
                                  case Right(_) => ()
                                }
                              }
      case ty :::> s      => insertSubterms(term, s, position.abstrPos)
      case TypeLambda(t)  => insertSubterms(term, t, position.abstrPos)
    }
  }
}

