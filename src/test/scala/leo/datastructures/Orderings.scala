package leo
package datastructures

import leo.datastructures.impl.orderings.CPO_Naive
import leo.datastructures.Type._
import leo.datastructures.impl.Signature
import leo.modules.output.Output

/**
 * Created by lex on 9/29/15.
 */
class Orderings extends LeoTestSuite {
  test("Type order test", Checked) {
    val s = Signature.get

    Out.output(TypeCMPResult(s.o,s.i))
    Out.output(TypeCMPResult(s.i,s.o))
    Out.output(TypeCMPResult(s.i,s.i))
    Out.output(TypeCMPResult(s.o,s.o))
    Out.output(TypeCMPResult(s.o ->: s.o,s.o))
    Out.output(TypeCMPResult(s.o,s.o ->: s.o))
    Out.output(TypeCMPResult(s.o ->: s.o,s.o ->: s.o))
    Out.output(TypeCMPResult(s.o ->: (s.o ->: s.o),s.o ->: s.o))
    Out.output(TypeCMPResult((s.o ->: s.o) ->: s.o,s.o ->: s.o))
    Out.output(TypeCMPResult(s.o ->: s.o,s.o ->: s.i))
    Out.output(TypeCMPResult(s.i ->: s.o,s.o ->: s.i))
    Out.output(TypeCMPResult(∀(s.i ->: 1),∀(s.i ->: s.i ->: s.i)))
    Out.output(TypeCMPResult(∀(1 ->: s.i),∀(s.i ->: s.i ->: s.i)))
  }


  private case class TypeCMPResult(a: Type, b: Type) extends Output {
    lazy val output = s"Comparing\t${a.pretty}\t with \t${b.pretty}\tResult: ${cmpResToStr(CPO_Naive.compare(a,b))}"

    private final def cmpResToStr(cmpRes: CPO_Naive.CMP_Result): String = {
      cmpRes match {
        case CPO_Naive.CMP_EQ => "EQ"
        case CPO_Naive.CMP_GT => "GT"
        case CPO_Naive.CMP_LT => "LT"
        case CPO_Naive.CMP_NC => "NC"
      }
    }
  }
}
