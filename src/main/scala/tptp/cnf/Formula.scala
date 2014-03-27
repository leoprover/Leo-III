package tptp.cnf

import tptp.Commons._

/**
 * Created by lex on 3/23/14.
 */
case class Formula(literals: List[Literal])

sealed abstract class Literal
case class Positive(formula: AtomicFormula) extends Literal
case class Negative(formula: AtomicFormula) extends Literal
case class Inequality(left: Term, right: Term) extends Literal