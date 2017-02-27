package leo.modules.encoding


import leo.datastructures.{Clause, Literal, Signature, Term, Type}
import leo.modules.HOLSignature

import scala.annotation.tailrec

/**
  * Created by lex on 21.02.17.
  */
object TypedFOLEncoding {
  type Problem = Set[Clause]
  type EncodedProblem = Problem
  type AuxiliaryDefs = Seq[Term]
  type Result = (Signature, EncodedProblem, AuxiliaryDefs)

  final def apply(problem: Problem, les: LambdaEliminationStrategy)(implicit sig: Signature): Result = {
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()
    // Analyze problem
    val functionTable = EncodingAnalyzer.analyze(problem)
    val fIt = functionTable.iterator
    while (fIt.hasNext) {
      val (f, arity) = fIt.next()
      val fMeta = sig(f)
      val foType = foTransformType(fMeta._ty, arity)(sig, foSig)
      foSig.addUninterpreted(fMeta.name, foType)
    }
    // Introduce auxiliary constants
    ???
    // Introduce problem-specific constant symbols (or maybe on-the-fly?)
    ???
    // Translate
    ???
  }

  /** Transform a type `t1 -> t2 -> ... -> tn` (tn not a function type) to a "first-order encoding equivalent".
    * More precisely, we transform it to
    * `(t1' * ... * tk') -> fun(tk+1', fun(...,fun(tn-1', tn')..)))`
    * where `1 <= k <= n` is the minimum arity of the function symbol (of this type) used in a given problem.
    * Hence, the first `k` arguments are passed directly, the remaining `n-k+1` parameters are then passed
    * by the `hApp` operator. (Note that if `k = n` of course the above type simplifies into
    * `(t1' * t2' * ... * tn-1') -> tn'`).
    *
    * The transformed parameter types ti' (1 <= i <= n) are given by:
    * - `rho(nu1', ..., num')` if ti = `rho(nu1, ... num)` is a applied type operator and the
    * `nui` are recursively transformed this way,
    * - `fun(nu1', nu2')` if ti = `nu1 -> nu2` is a function type (can only happen if `i < n`)
    * - `X` if ti `X` is a type variable.
    *
    * @note A FO function type `(t1*...*tn) -> t` is represented by `t1 -> ... -> tn -> t`
    * since this format is used internally. We will later transform it into uncurried format
    * when printing TFF of FOF format.
    * @note Side-effects: User types occurring in the problem are inserted into `encodingSignature`
    */
  private final def foTransformType(typ: Type, arity: EncodingAnalyzer.MinArity)(holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Type = {
    import leo.datastructures.mkPolyUnivType
    val monoBody = typ.monomorphicBody
    val funParamTypes = monoBody.funParamTypesWithResultType
    mkPolyUnivType(typ.polyPrefixArgsCount, Type.mkFunType(funParamTypes.map(foTransformType0(_)(holSignature, encodingSignature))))
  }
  private final def foTransformType0(ty: Type)(holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Type = {
    import leo.datastructures.Type._
    ty match {
      case HOLSignature.o => TypedFOLEncodingSignature.o
      case HOLSignature.i => TypedFOLEncodingSignature.i
      case BaseType(tyId) =>
        val name = holSignature(tyId).name
        if (encodingSignature.exists(name)) Type.mkType(encodingSignature(name).key)
        else Type.mkType(encodingSignature.addBaseType(name))
      case ComposedType(tyConId, tyArgs) =>
        val tyConstructorMeta = holSignature(tyConId)
        val tyConstructorName = tyConstructorMeta.name
        val transformedArgTypes = tyArgs.map(foTransformType0(_)(holSignature, encodingSignature))
        if (encodingSignature.exists(tyConstructorName)) Type.mkType(encodingSignature(tyConstructorName).key, transformedArgTypes)
        else Type.mkType(encodingSignature.addTypeConstructor(tyConstructorName, tyConstructorMeta._kind), transformedArgTypes)
      case in -> out => ???
      case _ => // bound type var, product type or union type
        // polytype should not happen
        assert(!ty.isPolyType)
        ty
    }
  }

  final def translate(cl: Clause): Clause = Clause(cl.lits.map(translate))

  final def translate(lit: Literal): Literal = if (lit.equational) {
    val translatedLeft = translate(lit.left)
    val translatedRight = translate(lit.right)
    Literal.mkLit(translatedLeft, translatedRight, lit.polarity)
  } else {
    val translatedLeft = translate(lit.left)
    Literal.mkLit(translatedLeft, lit.polarity)
  }

  final def translate(t: Term): Term = {
    import Term._
    import leo.modules.HOLSignature.{Forall, Exists, TyForall}
    t match {
      case Forall(ty :::> body) => ???
      case Exists(ty :::> body) => ???
      case TyForall(body) => ???
      case ty :::> body => ???
      case TypeLambda(body) => ???
      case f ∙ args => ???
    }
    ???
  }
}




object EncodingAnalyzer {
  type MinArity = Int // term parameter count
  type ArityTable = Map[Signature#Key, MinArity]

  final def analyze(clauses: Set[Clause]): ArityTable = {
    var result: ArityTable = Map()
    val clsLit = clauses.iterator
    while (clsLit.hasNext) {
      val cl = clsLit.next()
      result = merge(analyze(cl), result)
    }
    result
  }
  final def analyze(cl: Clause): ArityTable = {
    var result: ArityTable = Map()
    val litsIt = cl.lits.iterator
    while (litsIt.hasNext) {
      val lit = litsIt.next()
      result = merge(analyze(lit), result)
    }
    result
  }

  final def analyze(lit: Literal): ArityTable = {
    if (lit.equational) {
      merge(analyze(lit.left), analyze(lit.right))
    } else analyze(lit.left)
  }

  @tailrec
  final def analyze(t: Term): ArityTable = {
    import leo.datastructures.Term._
    t match {
      case _ :::> body => analyze(body)
      case TypeLambda(body) => analyze(body)
      case f ∙ args => f match {
        case Symbol(id) =>
          val argArity = arity(args)
          merge(id -> argArity, analyzeArgs(args))
        case _ => analyzeArgs(args)
      }
    }
  }

  private final def arity(args: Seq[Either[Term, Type]]): MinArity = {
    val termArgs = args.dropWhile(_.isRight)
    leo.modules.Utility.myAssert(termArgs.forall(_.isLeft))
    termArgs.size
  }
  @inline private final def partitionWhile[A](predicate: A => Boolean, seq: Seq[A]): (Seq[A], Seq[A]) =
    partitionWhile(predicate, seq, Seq())
  @inline private final def partitionWhile[A](predicate: A => Boolean, seq: Seq[A], helper: Seq[A]): (Seq[A], Seq[A]) = {
    if (seq.isEmpty) (helper.reverse, seq)
    else {
      val hd = seq.head
      if (predicate(hd)) partitionWhile(predicate, seq.tail, hd +: helper)
      else (helper.reverse, seq)
    }
  }
  private final def analyzeArgs(args: Seq[Either[Term, Type]]): ArityTable = {
    var result: ArityTable = Map()
    val argsIt = args.iterator
    while (argsIt.hasNext) {
      val arg = argsIt.next()
      if (arg.isLeft) {
        val arityTable = analyze(arg.left.get)
        result = merge(arityTable, result)
      }
    }
    result
  }

  private final def merge(t1: ArityTable, t2: ArityTable): ArityTable = {
    var result: ArityTable = t2
    val entryIt = t1.iterator
    while (entryIt.hasNext) {
      val entry = entryIt.next()
      result = merge(entry, result)
    }
    result
  }

  private final def merge(entry: (Signature#Key, MinArity), t2: ArityTable): ArityTable = {
    val key = entry._1
    val arity = entry._2
    if (t2.contains(key)) {
      val existingEntry = t2(key)
      if (existingEntry > arity)
        t2 + entry // change map entry
      else t2
    } else t2 + entry
  }
}

object TypedFOLEncodingSignature {
  import leo.datastructures.Kind

  // Hard-wired constants. Only change if you know what you're doing!
  final val o: Type = Type.mkType(1)
  final val i: Type = Type.mkType(2)

  private final val oo: Type = o ->: o
  private final val ooo: Type = o ->: o ->: o
  final val folTrue: Term = Term.mkAtom(3, o)
  final val folFalse: Term = Term.mkAtom(4, o)
  // ...

  def apply(): TypedFOLEncodingSignature = {
    import leo.datastructures.impl.SignatureImpl
    new SignatureImpl with TypedFOLEncodingSignature
  }

  import leo.datastructures.Kind.{superKind, *}
  private final val fixedTypes: Map[String, Kind] = Map(
    "$tType"  -> superKind,
    "$o"      -> *, // 1
    "$i"      -> * // 2
  )
  import leo.datastructures.Type.∀
  private final val fixedSymbols: Map[String, Type] = Map(
    "$true"   -> o, // 3
    "$false"  -> o, // 4
    "~"       -> oo,
    "&"       -> ooo,
    "|"       -> ooo,
    "=>"      -> ooo,
    "<="      -> ooo,
    "<=>"     -> ooo,
    "="       -> ∀(1 ->: 1 ->: o),
    "!="      -> ∀(1 ->: 1 ->: o),
    "!"       -> ∀((1 ->: o) ->: o),
    "?"       -> ∀((1 ->: o) ->: o)
  )
}
trait TypedFOLEncodingSignature extends Signature {
  import leo.datastructures.Kind.*
  import leo.datastructures.Type.{mkType, ∀}

  // Init standard symbols
  for (ty <- TypedFOLEncodingSignature.fixedTypes) {
    addFixedTypeConstructor(ty._1, ty._2)
  }
  for (sym <- TypedFOLEncodingSignature.fixedSymbols) {
    addFixed(sym._1, sym._2, None, Signature.PropNoProp)
  }

  lazy val funTy_id = { addFixedTypeConstructor("$$fun", * ->: * ->: *) }
  def funTy(in: Type, out: Type): Type = mkType(funTy_id, Seq(in, out))

  private final val hApp_type: Type = ∀(∀((funTy(2,1) * 2).->:(1)))
}

