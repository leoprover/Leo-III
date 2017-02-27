package leo.modules.encoding

import leo.datastructures.{Clause, Literal, Signature, Term, Type}
import scala.annotation.tailrec

/**
  * Object for transforming higher-order problems into polymorphic first-order problems.
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  * @since February 2017
  * @todo Prefixing for safe translation
  */
object TypedFOLEncoding {
  type Problem = Set[Clause]
  type EncodedProblem = Problem
  type AuxiliaryDefs = Seq[Term]
  type Result = (Signature, EncodedProblem, AuxiliaryDefs)

  final def apply(problem: Problem, les: LambdaEliminationStrategy)(implicit sig: Signature): Result = {
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()
    // Analyze problem and insert problem-specific symbols into signature (encoded types)
    val functionTable = EncodingAnalyzer.analyze(problem)
    val fIt = functionTable.iterator
    while (fIt.hasNext) {
      val (f, arity) = fIt.next()
      val fMeta = sig(f)
      val foType = foTransformType(fMeta._ty, arity)(sig, foSig)
      foSig.addUninterpreted(fMeta.name, foType)
    }
    // Translate
    val resultProblem: Problem = problem.map(translate(_, les)(sig, foSig))
    // Collect auxiliary definitions from used symbols
    val auxDefs: AuxiliaryDefs = ???
    (foSig, resultProblem, auxDefs)
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
  private final def foTransformType(typ: Type, arity: EncodingAnalyzer.MinArity)
                                   (holSignature: Signature,
                                    encodingSignature: TypedFOLEncodingSignature): Type = {
    import leo.datastructures.mkPolyUnivType
    val monoBody = typ.monomorphicBody
    val funParamTypes = monoBody.funParamTypesWithResultType
    // If a minimum arity is known, use the first `arity` parameters as direct FO-like parameters and
    // the remaining ones as simulated ones (later applies via hApp)
    if (arity > 0) {
      assert(arity < funParamTypes.size) // since result type is included
      val directlyPassedTypes = funParamTypes.take(arity)
      val transformedDirectlyPassedTypes = directlyPassedTypes.map(foTransformType0(_)(holSignature, encodingSignature))
      val goalType = funParamTypes.drop(arity)
      val transformedGoalType = goalType.map(foTransformType0(_)(holSignature, encodingSignature))
      val funEncodedGoalType = encodeFunType(transformedGoalType)(holSignature, encodingSignature)
      mkPolyUnivType(typ.polyPrefixArgsCount, Type.mkFunType(transformedDirectlyPassedTypes, funEncodedGoalType))
    } else mkPolyUnivType(typ.polyPrefixArgsCount, encodeFunType(funParamTypes.map(foTransformType0(_)(holSignature, encodingSignature)))(holSignature, encodingSignature))
  }
  /** Transforms a type `t` into its FO-encoded variant given by
    * - `rho(nu1', ..., num')` if t = `rho(nu1, ... num)` is a applied type operator and the
    * `nui` are recursively transformed this way,
    * - `fun(nu1', nu2')` if ti = `nu1 -> nu2` is a function type (can only happen if `i < n`)
    * - `X` if ti `X` is a type variable.
    *
    * @note Side-effects: User types occurring in the problem are inserted into `encodingSignature`
    */
  private final def foTransformType0(ty: Type)(holSignature: Signature,
                                               encodingSignature: TypedFOLEncodingSignature): Type = {
    import leo.datastructures.Type._
    import leo.modules.HOLSignature
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
      case in -> out =>
        val transformedIn = foTransformType0(in)(holSignature, encodingSignature)
        val transformedOut = foTransformType0(out)(holSignature, encodingSignature)
        // Return lifted function type fun(in', out')
        encodingSignature.funTy(transformedIn, transformedOut)
      case _ => // bound type var, product type or union type
        // polytype should not happen
        assert(!ty.isPolyType)
        ty
    }
  }
  /** Returns an FO-encoded function type. Given a non-empty sequence of types
    * `t1`,...,`tn`, this method returns the simulated function type
    * `fun(t1', fun(..., fun(tn-1', tn')...))`
    * where the ti' are recursively transformed by `foTransformType0`.
    *
    * @param tys A sequence of types representing the uncurried parameter types of a function.
    *            This sequence must not be empty.
    * @note Side-effects: See `foTransformType0`.
    * @throws IllegalArgumentException if an empty sequence is passed for `tys`. */
  private final def encodeFunType(tys: Seq[Type])(holSignature: Signature,
                                                  encodingSignature: TypedFOLEncodingSignature): Type = {
    if (tys.isEmpty) throw new IllegalArgumentException
    else encodeFunType0(tys)(holSignature, encodingSignature)
  }
  private final def encodeFunType0(tys: Seq[Type])(holSignature: Signature,
                                                   encodingSignature: TypedFOLEncodingSignature): Type = {
    if (tys.size == 1) foTransformType0(tys.head)(holSignature, encodingSignature)
    else encodingSignature.funTy(foTransformType0(tys.head)(holSignature, encodingSignature), encodeFunType0(tys.tail)(holSignature, encodingSignature))
  }

  /**
    * Translate the HO clause `cl` to a FO equivalent.
    *
    * @note Side-effects: May insert auxiliary operators such as `hApp` or `hBool` to
    *       `encodingSignature`
    */
  final def translate(cl: Clause, les: LambdaEliminationStrategy)
                     (holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Clause =
    Clause(cl.lits.map(translate(_, les)(holSignature, encodingSignature)))

  final def translate(lit: Literal, les: LambdaEliminationStrategy)
                     (holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Literal = if (lit.equational) {
    val translatedLeft = translate(lit.left, les)(holSignature, encodingSignature)
    val translatedRight = translate(lit.right, les)(holSignature, encodingSignature)
    Literal.mkLit(translatedLeft, translatedRight, lit.polarity)
  } else {
    val translatedLeft = translate(lit.left, les)(holSignature, encodingSignature)
    Literal.mkLit(translatedLeft, lit.polarity)
  }

  final def translate(t: Term, les: LambdaEliminationStrategy)
                     (holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Term = {
    import Term._
    import leo.modules.HOLSignature.{Forall => HOLForall, Exists => HOLExists, TyForall => HOLTyForall,
    & => HOLAnd, ||| => HOLOr, === => HOLEq, !=== => HOLNeq, <=> => HOLEquiv, Impl => HOLImpl, <= => HOLIf,
    Not => HOLNot, LitFalse => HOLFalse, LitTrue => HOLTrue}
    import TypedFOLEncodingSignature._
    t match {
      // cases from here should not really happen if invoked from func-ext treated CNF problem.
      // But, oh well, why not support it anyway
      case HOLForall(ty :::> body) =>
        val encodedType =  foTransformType0(ty)(holSignature, encodingSignature)
        val translatedBody = translate(body, les)(holSignature, encodingSignature)
        Term.mkApp(Forall, Seq(Right(encodedType), Left(λ(encodedType)(translatedBody))))
      case HOLExists(ty :::> body) =>
        val encodedType =  foTransformType0(ty)(holSignature, encodingSignature)
        val translatedBody = translate(body, les)(holSignature, encodingSignature)
        Term.mkApp(Exists, Seq(Right(encodedType), Left(λ(encodedType)(translatedBody))))
      case HOLTyForall(TypeLambda(body)) =>
        val translatedBody = translate(body,les)(holSignature, encodingSignature)
        Term.mkTermApp(TyForall, Λ(translatedBody))
      case HOLEq(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(Eq, Seq(translatedLeft, translatedRight))
      case HOLNeq(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(Neq, Seq(translatedLeft, translatedRight))
      case HOLAnd(l,r) =>
        val translatedLeft = translate(l,les)(holSignature, encodingSignature)
        val translatedRight = translate(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(And, Seq(translatedLeft, translatedRight))
      case HOLOr(l,r) =>
        val translatedLeft = translate(l,les)(holSignature, encodingSignature)
        val translatedRight = translate(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(Or, Seq(translatedLeft, translatedRight))
      case HOLEquiv(l,r) =>
        val translatedLeft = translate(l,les)(holSignature, encodingSignature)
        val translatedRight = translate(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(Equiv, Seq(translatedLeft, translatedRight))
      case HOLImpl(l,r) =>
        val translatedLeft = translate(l,les)(holSignature, encodingSignature)
        val translatedRight = translate(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(Impl, Seq(translatedLeft, translatedRight))
      case HOLIf(l,r) =>
        val translatedLeft = translate(l,les)(holSignature, encodingSignature)
        val translatedRight = translate(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(If, Seq(translatedLeft, translatedRight))
      case HOLNot(body) =>
        val translatedBody = translate(body,les)(holSignature, encodingSignature)
        Term.mkTermApp(Not, translatedBody)
      case HOLFalse() => False
      case HOLTrue() => True
      case lambda@(_ :::> _) => les.eliminateLambda(lambda)
      // Non-CNF cases end
      // Standard-case begin
      case f ∙ args =>
        val encodedHead = f match {
          case Symbol(id) =>
            val fName = holSignature(id).name
            mkAtom(encodingSignature(fName).key)(encodingSignature)
          case _ => f
        }
        assert(encodedHead.isAtom)
        val translatedArgs: Seq[Either[Term, Type]] = args.map(arg =>
          if (arg.isLeft) Left(translateTerm(arg.left.get, les)(holSignature, encodingSignature))
          else Right(foTransformType0(arg.right.get)(holSignature, encodingSignature))  )
        Term.mkApp(encodedHead, translatedArgs)
      // Standard-case end, error cases follow
      case TypeLambda(_) => throw new IllegalArgumentException("naked type lambda at top level")
      case _ => throw new IllegalArgumentException("unexpected term occurred")
    }
  }

  final def translateTerm(t: Term, les: LambdaEliminationStrategy)
                         (holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Term = {
    import Term._
    import leo.modules.HOLSignature.{Forall => HOLForall, Exists => HOLExists, TyForall => HOLTyForall,
    & => HOLAnd, ||| => HOLOr, === => HOLEq, !=== => HOLNeq, <=> => HOLEquiv, Impl => HOLImpl, <= => HOLIf,
    Not => HOLNot, LitFalse => HOLFalse, LitTrue => HOLTrue}
    import encodingSignature._
    t match {
      // These cases can of course happen as subterms may contain arbitrary formulas in HOL
      case HOLForall(ty :::> body) =>
        val encodedType =  foTransformType0(ty)(holSignature, encodingSignature)
        val translatedBody = translateTerm(body, les)(holSignature, encodingSignature)
        Term.mkApp(proxyForall, Seq(Right(encodedType), Left(λ(encodedType)(translatedBody))))
      case HOLExists(ty :::> body) =>
        val encodedType =  foTransformType0(ty)(holSignature, encodingSignature)
        val translatedBody = translateTerm(body, les)(holSignature, encodingSignature)
        Term.mkApp(proxyExists, Seq(Right(encodedType), Left(λ(encodedType)(translatedBody))))
      case HOLTyForall(TypeLambda(body)) =>
        ??? // TODO FIXME
      case HOLEq(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(proxyEq, Seq(translatedLeft, translatedRight))
      case HOLNeq(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(proxyNeq, Seq(translatedLeft, translatedRight))
      case HOLAnd(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(proxyAnd, Seq(translatedLeft, translatedRight))
      case HOLOr(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(proxyOr, Seq(translatedLeft, translatedRight))
      case HOLEquiv(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(proxyEquiv, Seq(translatedLeft, translatedRight))
      case HOLImpl(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(proxyImpl, Seq(translatedLeft, translatedRight))
      case HOLIf(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Term.mkTermApp(proxyIf, Seq(translatedLeft, translatedRight))
      case HOLNot(body) =>
        val translatedBody = translateTerm(body,les)(holSignature, encodingSignature)
        Term.mkTermApp(proxyNot, translatedBody)
      case HOLFalse() => proxyFalse
      case HOLTrue() => proxyTrue
      case lambda@(_ :::> _) => les.eliminateLambda(lambda)
      // Non-CNF cases end
      // Standard-case begin
      case f ∙ args =>
        val encodedHead = f match {
          case Symbol(id) =>
            val fName = holSignature(id).name
            mkAtom(encodingSignature(fName).key)(encodingSignature)
          case _ => f
        }
        assert(encodedHead.isAtom)
        val translatedArgs: Seq[Either[Term, Type]] = args.map(arg =>
          if (arg.isLeft) Left(translateTerm(arg.left.get, les)(holSignature, encodingSignature))
          else Right(foTransformType0(arg.right.get)(holSignature, encodingSignature))  )
        Term.mkApp(encodedHead, translatedArgs)
      // Standard-case end, error cases follow
      case TypeLambda(_) => throw new IllegalArgumentException("naked type lambda at top level")
      case _ => throw new IllegalArgumentException("unexpected term occurred")
    }
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
  import leo.datastructures.Type.{mkType, ∀}
  // Hard-wired constants. Only change if you know what you're doing!
  final val o: Type = mkType(1)
  final val i: Type = mkType(2)

  private final val oo: Type = o ->: o
  private final val ooo: Type = o ->: o ->: o
  private final val aao: Type = ∀(1 ->: 1 ->: o)
  private final val aoo: Type = ∀((1 ->: o) ->: o)
  private final val faoo: Type = ∀(o) ->: o

  import leo.datastructures.Term.mkAtom
  final val True: Term = mkAtom(3, o)
  final val False: Term = mkAtom(4, o)
  final val Not: Term = mkAtom(5, oo)
  final val And: Term = mkAtom(6, ooo)
  final val Or: Term = mkAtom(7, ooo)
  final val Impl: Term = mkAtom(8, ooo)
  final val If: Term = mkAtom(9, ooo)
  final val Equiv: Term = mkAtom(10, ooo)
  final val Eq: Term = mkAtom(11, aao)
  final val Neq: Term = mkAtom(12, aao)
  final val Forall: Term = mkAtom(13, aoo)
  final val Exists: Term = mkAtom(14, aoo)
  final val TyForall: Term = mkAtom(15, faoo)

  final def apply(): TypedFOLEncodingSignature = {
    import leo.datastructures.impl.SignatureImpl
    new SignatureImpl with TypedFOLEncodingSignature
  }

  import leo.datastructures.Kind
  import leo.datastructures.Kind.{superKind, *}
  private final val fixedTypes: Map[String, Kind] = Map(
    "$tType"  -> superKind,
    "$o"      -> *, // 1
    "$i"      -> * // 2
  )

  private final val fixedSymbols: Map[String, Type] = Map(
    "$true"   -> o, // 3
    "$false"  -> o, // 4
    "~"       -> oo,
    "&"       -> ooo,
    "|"       -> ooo,
    "=>"      -> ooo,
    "<="      -> ooo,
    "<=>"     -> ooo,
    "="       -> aao,
    "!="      -> aao,
    "!"       -> aoo,
    "?"       -> aoo,
    "!>"      -> faoo
  )
}

trait TypedFOLEncodingSignature extends Signature {
  import leo.datastructures.Kind.*
  import leo.datastructures.Type.{mkType, ∀}

  private var usedAuxSymbols0: Set[Signature#Key] = Set.empty
  final def usedAuxSymbols: Set[Signature#Key] = usedAuxSymbols0

  // Init standard symbols
  for (ty <- TypedFOLEncodingSignature.fixedTypes) {
    addFixedTypeConstructor(ty._1, ty._2)
  }
  for (sym <- TypedFOLEncodingSignature.fixedSymbols) {
    addFixed(sym._1, sym._2, None, Signature.PropNoProp)
  }

  // Definitions of auxiliary symbols for FO encoding
  /// Meta symbols
  ///// fun type constant
  lazy val funTy_id: Signature#Key = {
    val id = addFixedTypeConstructor("$$fun", * ->: * ->: *)
    usedAuxSymbols0 += id
    id
  }
  final def funTy(in: Type, out: Type): Type = mkType(funTy_id, Seq(in, out))

  ///// bool type constant
  lazy val boolTy_id: Signature#Key = {
    val id = addFixedTypeConstructor("$$bool", *)
    usedAuxSymbols0 += id
    id
  }
  lazy val boolTy: Type = mkType(boolTy_id)

  ///// hApp constant
  private final lazy val hApp_type: Type = ∀(∀((funTy(2,1) * 2).->:(1)))
  lazy val hApp_id: Signature#Key = {
    val id = addFixed("$$hApp", hApp_type, None, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val hApp: Term = Term.mkAtom(hApp_id)(this)
  final def hApp(fun: Term, arg: Term): Term = {
    Term.mkApp(hApp, Seq(Right(fun.ty), Right(arg.ty), Left(fun), Left(arg)))
  }

  import TypedFOLEncodingSignature.o
  ///// hBool constant
  private final lazy val hBool_type: Type = boolTy ->: o
  lazy val hBool_id: Signature#Key = {
    val id = addFixed("$$hBool", hBool_type, None, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val hBool: Term = Term.mkAtom(hBool_id)(this)
  final def hBool(boolTerm: Term): Term = Term.mkTermApp(hBool, boolTerm)

  /// Proxy symbols
  ///// True/False proxy
  lazy val proxyTrue_id: Signature#Key = {
    val id = addUninterpreted("$$true", boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyTrue: Term = Term.mkAtom(proxyTrue_id)(this)

  lazy val proxyFalse_id: Signature#Key = {
    val id = addUninterpreted("$$false", boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyFalse: Term = Term.mkAtom(proxyFalse_id)(this)
  ///// Not proxy
  lazy val proxyNot_id: Signature#Key = {
    val id = addUninterpreted("$$not", boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyNot: Term = Term.mkAtom(proxyNot_id)(this)
  ///// and/or proxy
  lazy val proxyAnd_id: Signature#Key = {
    val id = addUninterpreted("$$and", boolTy ->: boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyAnd: Term = Term.mkAtom(proxyAnd_id)(this)

  lazy val proxyOr_id: Signature#Key = {
    val id = addUninterpreted("$$or", boolTy ->: boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyOr: Term = Term.mkAtom(proxyOr_id)(this)
  ///// impl/if/equiv
  lazy val proxyImpl_id: Signature#Key = {
    val id = addUninterpreted("$$impl", boolTy ->: boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyImpl: Term = Term.mkAtom(proxyImpl_id)(this)

  lazy val proxyIf_id: Signature#Key = {
    val id = addUninterpreted("$$if", boolTy ->: boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyIf: Term = Term.mkAtom(proxyIf_id)(this)

  lazy val proxyEquiv_id: Signature#Key = {
    val id = addUninterpreted("$$equiv", boolTy ->: boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyEquiv: Term = Term.mkAtom(proxyEquiv_id)(this)
  ///// forall/exists
  lazy val proxyForall_id: Signature#Key = {
    val id = addUninterpreted("$$forall", ∀((1 ->: o) ->: o), Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyForall: Term = Term.mkAtom(proxyForall_id)(this)

  lazy val proxyExists_id: Signature#Key = {
    val id = addUninterpreted("$$exists", ∀((1 ->: o) ->: o), Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyExists: Term = Term.mkAtom(proxyExists_id)(this)

  ///// eq / neq
  lazy val proxyEq_id: Signature#Key = {
    val id = addUninterpreted("$$eq", ∀(1 ->: 1 ->: o), Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyEq: Term = Term.mkAtom(proxyEq_id)(this)

  lazy val proxyNeq_id: Signature#Key = {
    val id = addUninterpreted("$$neq", ∀(1 ->: 1 ->: o), Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyNeq: Term = Term.mkAtom(proxyNeq_id)(this)
}

