package leo.modules.extraction_normalization

import leo.datastructures._
import leo.datastructures.Term._
import leo.datastructures.impl.Signature

import scala.collection.mutable


/**
  * Will perform a formula renaming (See Nonnengard 'Small Clause Normalforms')
  * as a preprocessing step.
  *
  * In formulas a subterm is replaced by a definition, if the resulting clauses yield a smaller clause normal form.
  */
object FormulaRenaming {

  /**
    * Stores a mapping for the unit equations to use the same descriptor
    * for equal definitions.
    */
  private var us : mutable.Map[Term, Term] = mutable.Map()  // TODO open for parallel execution
  def setUnitStore(us : mutable.Map[Term, Term]) : Unit = {
    this.us = us
  }
  def clearUnitStore() : Unit = {
    us.clear()
  }

  /**
    *
    * Replaces a subterm in a formula by a definition.
    * This happens if the resulting clause normal form would yield
    * less clauses, than the original
    *
    * @param c       The clause to normalize
    * @param delta    A difference in generated clauses, compared to the original.
    * @return
    */
  def apply(c : Clause, delta : Int = 1) : Seq[Clause]= ???


  /**
    *
    * Replaces a subterm in a literal by a definition.
    * This happens if the resulting clause normal form would yield
    * less clauses, than the original
    *
    * @param l       The literal to normalize
    * @param delta    A difference in generated clauses, compared to the original.
    * @return
    */
  def apply(l : Literal, polarity : Int, delta : Int) : (Literal, Seq[Clause]) = ???



  def apply(t : Term, polarity : Int, delta : Int) : (Term, Seq[Clause]) = t match {
    case |||(l,r)     =>
      val (l_rename,c1s) = apply(l, polarity, delta)
      val (r_rename,c2s) = apply(r, polarity, delta)
      // Bottom-up replacement

      if(polarity > 0){
        val origin_l : Int = cnf_size(l_rename, true)
        val origin_r : Int = cnf_size(r_rename, true)
        val origin_size : Int = origin_l * origin_r
        if(origin_size > origin_l + delta && origin_l < origin_r){
          // Replace lelft if it will get smaller
        }

      } else if (polarity < 0) {

      } else {

      }
      ???
    case &(l,r)       => ???
    case <=>(l,r)     =>  ???
    case ===(l,r)     => ???
    case !===(l,r)    => ???
    case Impl(l,r)    => ???
    case <=(l,r)      => ???
    case ~&(l,r)      => ???
    case ~|||(l,r)    => ???
    case <~>(l,r)     => ???
    case Not(t1)      => ???
    case Forall(ty :::> t1) => ???
    case Exists(ty :::> t1) => ???

    // Pass through unimportant structures
    case s@Symbol(_)            => ???
    case s@Bound(_,_)           => ???
    case f ∙ args               => ???
    case ty :::> s              => ???
    case TypeLambda(t)          => ???
    //    case _  => formula
  }

  //TODO Alles nochmal überprüfen
  protected[extraction_normalization] def cnf_size(t : Term, pol : Boolean) : Int = t match {
    case |||(l,r)     => if(pol) cnf_size(l,pol)*cnf_size(r,pol) else cnf_size(l,!pol)*cnf_size(r,!pol)
    case &(l,r)       => if(pol) cnf_size(l,pol)+cnf_size(r,pol) else cnf_size(l,!pol)*cnf_size(r,!pol)
    case <=>(l,r)     => if(pol) cnf_size(l,!pol)*cnf_size(r, pol) + cnf_size(l,pol)*cnf_size(r, !pol)
                          else cnf_size(l,pol)*cnf_size(r, pol) + cnf_size(l, !pol)*cnf_size(r,!pol)
    case <~>(l,r)     => if(!pol) cnf_size(l,!pol)*cnf_size(r, pol) + cnf_size(l,pol)*cnf_size(r, !pol)
    else cnf_size(l,pol)*cnf_size(r, pol) + cnf_size(l, !pol)*cnf_size(r,!pol)
    case ===(l,r)     => if(t.ty == Signature.get.o) cnf_size(<=>(l,r), pol) else 1
    case !===(l,r)    => if(t.ty == Signature.get.o) cnf_size(<=>(l,r), !pol) else 1
    case Impl(l,r)    => if(pol) cnf_size(l, !pol)*cnf_size(r, pol) else cnf_size(l,pol)+cnf_size(r,!pol)
    case <=(l,r)      => if(pol) cnf_size(l, pol)*cnf_size(r, !pol) else cnf_size(l,!pol)+cnf_size(r,pol)
    case ~&(l,r)      => cnf_size(&(l,r),!pol)
    case ~|||(l,r)    => cnf_size(|||(l,r), !pol)
    case Not(t1)      => cnf_size(t1, !pol)
    case Forall(ty :::> t1) => cnf_size(t1, pol)
    case Exists(ty :::> t1) => cnf_size(t1, pol)

    // Pass through unimportant structures
    case s@Symbol(_)            => 1
    case s@Bound(_,_)           => 1
    case f ∙ args               => 1
    case ty :::> s              => 1
    case TypeLambda(t)          => 1
    //    case _  => formula
  }

  protected[extraction_normalization] def introduce_definition(t : Term, pol : Boolean) : Term = {
    val definition =
      if(us.contains(t))
        us.get(t).get
      else {
        val s = Signature.get
        // Term was no yet introduced
        val newArgs = t.freeVars.toSeq  // Arguments passed to the function to define
        val argtypes = newArgs.map(_.ty)

        val c = s.freshSkolemVar(Type.mkFunType(argtypes, t.ty)) // TODO other name (extra function in Signature)
        val ct = Term.mkTermApp(Term.mkAtom(c), newArgs).betaNormalize
        us.put(t, ct)
        ct
      }

    val impl = if(pol) Impl(definition, t) else Impl(t, definition)
    impl
  }

}
