package leo.modules.calculus


package object matching {
  import leo.datastructures.Term

  type UEq = (Term, Term)
}

package matching {

  /**
    * Created by lex on 6/4/16.
    */
  object FOMatching {

    protected def detExhaust(unsolved: Seq[UEq]): Unit = {}


    def testMatch(): Int = {
      ???
    }
  }

}