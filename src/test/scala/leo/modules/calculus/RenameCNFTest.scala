package leo.modules.calculus

import leo.datastructures.{Clause, Literal, Signature}
import leo.modules.parsers.Input
import leo.{Checked, LeoTestSuite}

/**
  * Created by mwisnie on 1/31/17.
  */
class RenameCNFTest extends LeoTestSuite{
  val testProblems : Seq[String] = Seq(
    "fof(1,axiom,a&b).",
    "fof(2,axiom,a|b).",
    "fof(5,axiom,(a&b)|(c&d)).",
    "fof(6,axiom,((a & b) | (c & d) | (e & f))).",
    "fof(7,axiom,a=>b)."
  )


  for(p <- testProblems)
    test(s"Test : ($p)", Checked) {
      implicit val sig: Signature = getFreshSignature
      val (_,l,_) = Input.readAnnotated(p)
      val s : StringBuilder = new StringBuilder
      s.append("CNF on\n  ")
      val pc = Clause(Literal(l,true))
      s.append(pc.pretty(sig))
      s.append("\n")
      val vargen1 = freshVarGen(pc)
      val vargen2 = freshVarGen(pc)
      val cnf1 = FullCNF(vargen1, pc)
      val cnf3 = RenameCNF(vargen2, pc)
      s.append(" >Full CNF\n   ")
      s.append(cnf1.map(_.pretty(sig)).mkString("\n   "))
      s.append("\n >Rename CNF\n   ")
      s.append(cnf3.map(_.pretty(sig)).mkString("\n   "))
      s.append(s"\n >Smaller ${cnf3.size} <= ${cnf1.size} : ${cnf3.size <= cnf1.size}")
      println(s.toString())
      assert(cnf3.size <= cnf1.size, "Renaming must make the cnf smaller.")
    }

}
