package leo.datastructures.internal
import scala.language.implicitConversions

import Term.{\,/\,mkBound,mkTermApp => app,intToBoundVar, intsToBoundVar,mkTypeApp}
import Type.{typeVarToType}

object Church {

  def main(args: Array[String]) {
    val sig = Signature.get
    Signature.withHOL(sig)


    val zero: Term = /\(\(1 ->: 1,1)((1,1)))
    val Nat = zero.ty

    val succ: Term = /\(\((1 ->: 1) ->: 1 ->: 1, 1 ->: 1, 1)(app(
                                                                app((3,(1 ->: 1) ->: 1 ->: 1),(2,1 ->: 1)),
                                                                app((2,1 ->: 1),(1,1)))))
//    println("type(zero)= " + zero.ty.pretty)
//    println("type(succ)= " + succ.ty.pretty)

    val succzero = /\(app(mkTypeApp(succ, 1),mkTypeApp(zero, 1)))
    val succzeroInstance = (app(mkTypeApp(succ, 1),mkTypeApp(zero, 1))).betaNormalize
//    println("type(succ zero)= " + succzero.betaNormalize.ty.pretty)
//    println("succ zero= " + succzero.betaNormalize.pretty)

//    val succInstance = mkTypeApp(succ,1).betaNormalize
//    val natInstance = mkTypeApp(zero,1).betaNormalize
//    val nInstance = mkTypeApp(zero,succInstance.ty).betaNormalize

//    val add = /\(\(zero.ty, zero.ty)   (app(app(mkTypeApp((2,zero.ty),succInstance.ty),    succInstance)   ,  mkTypeApp((1,zero.ty),1))))
//
    val add = {
      val nInstance = mkBound((1 ->: 1) ->: 1 ->: 1,2).instantiateBy((1 ->: 1) ->: 1 ->: 1).betaNormalize
      val mInstance = mkBound((1 ->: 1) ->: 1 ->: 1,1).betaNormalize
      val succInstance = mkTypeApp(succ, 1).betaNormalize

      \(Nat, Nat)(/\( app(app(nInstance, succInstance), mInstance))).betaNormalize
    }
    println(add.pretty)
    println(app(app(add,succzeroInstance),succzeroInstance).pretty)
    println(app(app(add,succzeroInstance),succzeroInstance).betaNormalize.pretty)
    val two = app(app(add,succzeroInstance),succzeroInstance).betaNormalize

    println(app(app(add,succzeroInstance),succzeroInstance).betaNormalize.pretty)


  }

}
