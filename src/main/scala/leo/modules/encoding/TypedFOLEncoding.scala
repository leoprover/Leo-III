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
  type Result = (EncodedProblem, Signature)

  final def apply(problem: Problem, les: LambdaEliminationStrategy)(implicit sig: Signature): Result = {
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()
    // Analyze problem and insert problem-specific symbols into signature (encoded types)
    val functionTable = EncodingAnalyzer.analyze(problem)
    val fIt = functionTable.iterator
    while (fIt.hasNext) {
      val (f, info) = fIt.next()
      val fMeta = sig(f)
      val foType = foTransformType(fMeta._ty, info)(sig, foSig)
      foSig.addUninterpreted(fMeta.name, foType)
    }
    // Translate
    val resultProblem: Problem = problem.map(translate(_, les)(sig, foSig))
    // Collect auxiliary definitions from used symbols
    val auxDefs: Set[Clause] = collectAuxDefs(foSig)
    // Collect auxiliary definitions from lambda elimination (if any)
    val auxDefsFromLES: Set[Clause] = ???
    (resultProblem union auxDefs union auxDefsFromLES, foSig)
  }

  private final def collectAuxDefs(foSig: TypedFOLEncodingSignature): Set[Clause] = {
    var result: Set[Clause] = Set.empty
    val symbIt = foSig.usedAuxSymbols.iterator
    while (symbIt.hasNext) {
      val symb = symbIt.next()
      val axiomForSymb = foSig.proxyAxiom(symb)
      if (axiomForSymb.isDefined)
        result += Clause(Literal.mkLit(axiomForSymb.get, true))
    }
    result
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
    * - `bool` if ti = `o`, 1 <= i < n,
    * - tn' = `bool` if tn = `o` and this type occurs as subterm, `o` otherwise,
    * - `rho(nu1', ..., num')` if ti = `rho(nu1, ... num)` is a applied type operator (!= `o`) and the
    * `nui` are recursively transformed this way,
    * - `fun(nu1', nu2')` if ti = `nu1 -> nu2` is a function type (can only happen if `i < n`)
    * - `X` if ti `X` is a type variable.
    *
    * @note A FO function type `(t1*...*tn) -> t` is represented by `t1 -> ... -> tn -> t`
    * since this format is used internally. We will later transform it into uncurried format
    * when printing TFF of FOF format.
    * @note Side-effects: User types occurring in the problem are inserted into `encodingSignature`
    */
  protected[encoding] final def foTransformType(typ: Type, symbolInfo: EncodingAnalyzer.SymbolInfo)
                                   (holSignature: Signature,
                                    encodingSignature: TypedFOLEncodingSignature): Type = {
    import leo.datastructures.mkPolyUnivType
    val monoBody = typ.monomorphicBody
    val funParamTypes0 = monoBody.funParamTypesWithResultType
    val transformedFunParamTypes0 = funParamTypes0.init.map(foTransformType0(_, true)(holSignature, encodingSignature))
    val transformedFunResultType0 = if (symbolInfo._2) foTransformType0(funParamTypes0.last, true)(holSignature, encodingSignature)
    else foTransformType0(funParamTypes0.last, false)(holSignature, encodingSignature)

    val funParamTypes = transformedFunParamTypes0 :+ transformedFunResultType0
    // If a minimum arity is known, use the first `arity` parameters as direct FO-like parameters and
    // the remaining ones as simulated ones (later applied via hApp)
    val arity = symbolInfo._1
    assert(arity <= funParamTypes.size)
    val directlyPassedTypes = funParamTypes.take(arity)
    val goalType = funParamTypes.drop(arity) // the types that need to be encoded via fun
    val funEncodedGoalType = encodingSignature.funTy(goalType)
    mkPolyUnivType(typ.polyPrefixArgsCount, Type.mkFunType(directlyPassedTypes, funEncodedGoalType))
  }

  /** Transforms a type `t` into its FO-encoded variant given by
    * `bool` if t = `o` and replaceO
    * - `rho(nu1', ..., num')` if t = `rho(nu1, ... num)` is a applied type operator and the
    * `nui` are recursively transformed this way,
    * - `fun(nu1', nu2')` if ti = `nu1 -> nu2` is a function type (can only happen if `i < n`)
    * - `X` if ti `X` is a type variable.
    *
    * @note Side-effects: User types occurring in the problem are inserted into `encodingSignature`
    */
  private final def foTransformType0(ty: Type, replaceO: Boolean)(holSignature: Signature,
                                               encodingSignature: TypedFOLEncodingSignature): Type = {
    import leo.datastructures.Type._
    import leo.modules.HOLSignature
    ty match {
      case HOLSignature.o => if (replaceO) encodingSignature.boolTy else TypedFOLEncodingSignature.o
      case HOLSignature.i => TypedFOLEncodingSignature.i
      case BaseType(tyId) =>
        val name = holSignature(tyId).name
        if (encodingSignature.exists(name)) Type.mkType(encodingSignature(name).key)
        else Type.mkType(encodingSignature.addBaseType(name))
      case ComposedType(tyConId, tyArgs) =>
        val tyConstructorMeta = holSignature(tyConId)
        val tyConstructorName = tyConstructorMeta.name
        val transformedArgTypes = tyArgs.map(foTransformType0(_, true)(holSignature, encodingSignature))
        if (encodingSignature.exists(tyConstructorName)) Type.mkType(encodingSignature(tyConstructorName).key, transformedArgTypes)
        else Type.mkType(encodingSignature.addTypeConstructor(tyConstructorName, tyConstructorMeta._kind), transformedArgTypes)
      case in -> out =>
        val transformedIn = foTransformType0(in,replaceO)(holSignature, encodingSignature)
        val transformedOut = foTransformType0(out,replaceO)(holSignature, encodingSignature)
        // Return lifted function type fun(in', out')
        encodingSignature.funTy(transformedIn, transformedOut)
      case _ => // bound type var, product type or union type
        // polytype should not happen
        assert(!ty.isPolyType)
        ty
    }
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
        val encodedType =  foTransformType0(ty, true)(holSignature, encodingSignature)
        val translatedBody = translate(body, les)(holSignature, encodingSignature)
        Forall(λ(encodedType)(translatedBody))
      case HOLExists(ty :::> body) =>
        val encodedType =  foTransformType0(ty, true)(holSignature, encodingSignature)
        val translatedBody = translate(body, les)(holSignature, encodingSignature)
        Exists(λ(encodedType)(translatedBody))
      case HOLTyForall(TypeLambda(body)) =>
        val translatedBody = translate(body,les)(holSignature, encodingSignature)
        TyForall(Λ(translatedBody))
      case HOLEq(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Eq(translatedLeft,translatedRight)
      case HOLNeq(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        Neq(translatedLeft,translatedRight)
      case HOLAnd(l,r) =>
        val translatedLeft = translate(l,les)(holSignature, encodingSignature)
        val translatedRight = translate(r,les)(holSignature, encodingSignature)
        And(translatedLeft, translatedRight)
      case HOLOr(l,r) =>
        val translatedLeft = translate(l,les)(holSignature, encodingSignature)
        val translatedRight = translate(r,les)(holSignature, encodingSignature)
        Or(translatedLeft, translatedRight)
      case HOLEquiv(l,r) =>
        val translatedLeft = translate(l,les)(holSignature, encodingSignature)
        val translatedRight = translate(r,les)(holSignature, encodingSignature)
        Equiv(translatedLeft, translatedRight)
      case HOLImpl(l,r) =>
        val translatedLeft = translate(l,les)(holSignature, encodingSignature)
        val translatedRight = translate(r,les)(holSignature, encodingSignature)
        Impl(translatedLeft, translatedRight)
      case HOLIf(l,r) =>
        val translatedLeft = translate(l,les)(holSignature, encodingSignature)
        val translatedRight = translate(r,les)(holSignature, encodingSignature)
        If(translatedLeft, translatedRight)
      case HOLNot(body) =>
        val translatedBody = translate(body,les)(holSignature, encodingSignature)
        Not(translatedBody)
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
          case Bound(boundTy, boundIdx) => mkBound(foTransformType0(boundTy, true)(holSignature, encodingSignature), boundIdx)
          case _ => f
        }
        assert(encodedHead.isAtom)
        val translatedTyArgs = args.takeWhile(_.isRight).map(ty => foTransformType0(ty.right.get, true)(holSignature, encodingSignature))
        val termArgs = args.dropWhile(_.isRight)
        leo.modules.Utility.myAssert(termArgs.forall(_.isLeft))
        val translatedTermArgs = termArgs.map(arg => translateTerm(arg.left.get, les)(holSignature, encodingSignature))
        // pass some arguments directly if possible
        val encodedHeadParamTypes = encodedHead.ty.monomorphicBody.funParamTypes
        assert(translatedTermArgs.size >= encodedHeadParamTypes.size)
        val directArgs = translatedTermArgs.take(encodedHeadParamTypes.size)
        val indirectArgs = translatedTermArgs.drop(encodedHeadParamTypes.size)

        val tyArgsApplied = Term.mkTypeApp(encodedHead, translatedTyArgs)
        val directArgsApplied = Term.mkTermApp(tyArgsApplied, directArgs)
        val allApplied = encodingSignature.hApp(directArgsApplied, indirectArgs)
        assert(allApplied.ty == o || allApplied.ty == encodingSignature.boolTy)
        if (allApplied.ty == encodingSignature.boolTy) encodingSignature.hBool(allApplied)
        else allApplied
      // Standard-case end, error cases follow
      case TypeLambda(_) => throw new IllegalArgumentException("naked type lambda at top level")
      case _ => throw new IllegalArgumentException("unexpected term occurred")
    }
  }

  private final def translateTerm(t: Term, les: LambdaEliminationStrategy)
                         (holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Term = {
    import Term._
    import leo.modules.HOLSignature.{Forall => HOLForall, Exists => HOLExists,
    & => HOLAnd, ||| => HOLOr, === => HOLEq, !=== => HOLNeq, <=> => HOLEquiv, Impl => HOLImpl, <= => HOLIf,
    Not => HOLNot, LitFalse => HOLFalse, LitTrue => HOLTrue}
    import encodingSignature._
    t match {
      // These cases can of course happen as subterms may contain arbitrary formulas in HOL
      case HOLForall(ty :::> body) =>
        val encodedType =  foTransformType0(ty, true)(holSignature, encodingSignature)
        val translatedBody = translateTerm(body, les)(holSignature, encodingSignature)
        proxyForall(λ(encodedType)(translatedBody))
      case HOLExists(ty :::> body) =>
        val encodedType =  foTransformType0(ty, true)(holSignature, encodingSignature)
        val translatedBody = translateTerm(body, les)(holSignature, encodingSignature)
        proxyExists(λ(encodedType)(translatedBody))
      case HOLEq(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        proxyEq(translatedLeft, translatedRight)
      case HOLNeq(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        proxyNeq(translatedLeft, translatedRight)
      case HOLAnd(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        proxyAnd(translatedLeft, translatedRight)
      case HOLOr(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        proxyOr(translatedLeft, translatedRight)
      case HOLEquiv(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        proxyEquiv(translatedLeft, translatedRight)
      case HOLImpl(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        proxyImpl(translatedLeft, translatedRight)
      case HOLIf(l,r) =>
        val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
        proxyIf(translatedLeft, translatedRight)
      case HOLNot(body) =>
        val translatedBody = translateTerm(body,les)(holSignature, encodingSignature)
        proxyNot(translatedBody)
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
          case Bound(boundTy, boundIdx) => mkBound(foTransformType0(boundTy, true)(holSignature, encodingSignature), boundIdx)
          case _ => f
        }
        assert(encodedHead.isAtom)
        val translatedTyArgs = args.takeWhile(_.isRight).map(ty => foTransformType0(ty.right.get, true)(holSignature, encodingSignature))
        val termArgs = args.dropWhile(_.isRight)
        leo.modules.Utility.myAssert(termArgs.forall(_.isLeft))
        val translatedTermArgs = termArgs.map(arg => translateTerm(arg.left.get, les)(holSignature, encodingSignature))
        // pass some arguments directly if possible
        val encodedHeadParamTypes = encodedHead.ty.monomorphicBody.funParamTypes
        assert(translatedTermArgs.size >= encodedHeadParamTypes.size)
        val directArgs = translatedTermArgs.take(encodedHeadParamTypes.size)
        val indirectArgs = translatedTermArgs.drop(encodedHeadParamTypes.size)

        val tyArgsApplied = Term.mkTypeApp(encodedHead, translatedTyArgs)
        val directArgsApplied = Term.mkTermApp(tyArgsApplied, directArgs)
        encodingSignature.hApp(directArgsApplied, indirectArgs)
      // Standard-case end, error cases follow
      case TypeLambda(_) => throw new IllegalArgumentException("naked type lambda at top level")
      case _ => throw new IllegalArgumentException("unexpected term occurred") // e.g. ForallTy
    }
  }

}

object EncodingAnalyzer {
  type MinArity = Int // term parameter count
  type ArgPos = Boolean // does the symbol occur at argument position?
  type SymbolInfo = (MinArity, ArgPos)
  type ArityTable = Map[Signature#Key, SymbolInfo]

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

  private final val fixedConnectives: Set[Signature#Key] = {
    import leo.modules.HOLSignature._
    Set(Not.key, &.key, |||.key, Impl.key, <=.key, <=>.key, Forall.key, Exists.key)
  }
  @tailrec final def analyze(t: Term): ArityTable = {
    import leo.datastructures.Term._

    t match {
      case _ :::> body => analyze(body)
      case TypeLambda(body) => analyze(body)
      case f ∙ args => f match {
        case Symbol(id) =>
          val argArity = arity(args)
          if (fixedConnectives.contains(id))
            merge(id -> (argArity, false), analyzeArgs(args))
          else
            merge(id -> (argArity, false), analyzeTermArgs(args))
        case _ => analyzeTermArgs(args)
      }
    }
  }
  @tailrec final def analyzeTerm(t: Term): ArityTable = {
    import leo.datastructures.Term._
    t match {
      case _ :::> body => analyzeTerm(body)
      case TypeLambda(body) => analyzeTerm(body)
      case f ∙ args => f match {
        case Symbol(id) =>
          val argArity = arity(args)
          merge(id -> (argArity, true), analyzeTermArgs(args))
        case _ => analyzeTermArgs(args)
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
  private final def analyzeTermArgs(args: Seq[Either[Term, Type]]): ArityTable = {
    var result: ArityTable = Map()
    val argsIt = args.iterator
    while (argsIt.hasNext) {
      val arg = argsIt.next()
      if (arg.isLeft) {
        val arityTable = analyzeTerm(arg.left.get)
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

  private final def merge(entry: (Signature#Key, SymbolInfo), t2: ArityTable): ArityTable = {
    val key = entry._1
    val info = entry._2
    val arity = info._1
    val argPos = info._2
    if (t2.contains(key)) {
      val (existingArity, existingArgPos) = t2(key)
      t2 + (key -> (arity.min(existingArity), argPos || existingArgPos))
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
  final def Not(t: Term): Term = Term.mkTermApp(Not, t)
  final val And: Term = mkAtom(6, ooo)
  final def And(l: Term, r: Term): Term = Term.mkTermApp(And, Seq(l,r))
  final val Or: Term = mkAtom(7, ooo)
  final def Or(l: Term, r: Term): Term = Term.mkTermApp(Or, Seq(l,r))
  final val Impl: Term = mkAtom(8, ooo)
  final def Impl(l: Term, r: Term): Term = Term.mkTermApp(Impl, Seq(l,r))
  final val If: Term = mkAtom(9, ooo)
  final def If(l: Term, r: Term): Term = Term.mkTermApp(If, Seq(l,r))
  final val Equiv: Term = mkAtom(10, ooo)
  final def Equiv(l: Term, r: Term): Term = Term.mkTermApp(Equiv, Seq(l,r))
  final val Eq: Term = mkAtom(11, aao)
  final def Eq(l: Term, r: Term): Term = Term.mkApp(Eq, Seq(Right(l.ty), Left(l), Left(r)))
  final val Neq: Term = mkAtom(12, aao)
  final def Neq(l: Term, r: Term): Term = Term.mkApp(Neq, Seq(Right(l.ty), Left(l), Left(r)))
  final val Forall: Term = mkAtom(13, aoo)
  final def Forall(body: Term): Term = Term.mkApp(Forall, Seq(Right(body.ty._funDomainType), Left(body)))
  final val Exists: Term = mkAtom(14, aoo)
  final def Exists(body: Term): Term = Term.mkApp(Forall, Seq(Right(body.ty._funDomainType), Left(body)))
  final val TyForall: Term = mkAtom(15, faoo)
  final def TyForall(body: Term): Term = Term.mkTermApp(TyForall, body)

  final def apply(): TypedFOLEncodingSignature = {
    import leo.datastructures.impl.SignatureImpl
    new SignatureImpl with TypedFOLEncodingSignature
  }

  import leo.datastructures.Kind
  import leo.datastructures.Kind.{superKind, *}
  private final val fixedTypes: Seq[(String, Kind)] = Seq(
    "$tType"  -> superKind,
    "$o"      -> *, // 1
    "$i"      -> * // 2
  )

  private final val fixedSymbols: Seq[(String, Type)] = Seq(
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
  /** Returns an FO-encoded function type. Given a non-empty sequence of types
    * `t1`,...,`tn`, this method returns the simulated function type
    * `fun(t1', fun(..., fun(tn-1', tn')...))`
    * where the ti' are recursively transformed by `foTransformType0`.
    *
    * @param tys A sequence of types representing the uncurried parameter types of a function.
    *            This sequence must not be empty.
    * @throws IllegalArgumentException if an empty sequence is passed for `tys`. */
  final def funTy(tys: Seq[Type]): Type = {
    if (tys.isEmpty) throw new IllegalArgumentException
    else funTy0(tys)
  }
  private final def funTy0(tys: Seq[Type]): Type = {
    if (tys.size == 1) tys.head
    else funTy(tys.head, funTy0(tys.tail))
  }

  ///// bool type constant
  lazy val boolTy_id: Signature#Key = {
    val id = addFixedTypeConstructor("$$bool", *)
    usedAuxSymbols0 += id
    id
  }
  lazy val boolTy: Type = mkType(boolTy_id)

  ///// hApp constant
  private final lazy val hApp_type: Type = ∀(∀(funTy(2,1) ->: Type.mkVarType(2) ->: Type.mkVarType(1)))
  lazy val hApp_id: Signature#Key = {
    val id = addFixed("$$hApp", hApp_type, None, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val hApp: Term = Term.mkAtom(hApp_id)(this)
  final def hApp(fun: Term, arg: Term): Term = {
    val (id, tyArgs) = Type.ComposedType.unapply(fun.ty).get
    assert(id == funTy_id)
    assert(tyArgs.size == 2)
    Term.mkApp(hApp, Seq(Right(tyArgs.head), Right(tyArgs.tail.head), Left(fun), Left(arg)))
  }
  @tailrec
  final def hApp(fun: Term, args: Seq[Term]): Term = {
    if (args.isEmpty) fun
    else {
      val hd = args.head
      hApp(hApp(fun, hd), args.tail)
    }
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
  final def proxyNot(body: Term): Term = Term.mkTermApp(proxyNot, body)
  ///// and/or proxy
  lazy val proxyAnd_id: Signature#Key = {
    val id = addUninterpreted("$$and", boolTy ->: boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyAnd: Term = Term.mkAtom(proxyAnd_id)(this)
  final def proxyAnd(l: Term, r: Term): Term = Term.mkTermApp(proxyAnd, Seq(l,r))

  lazy val proxyOr_id: Signature#Key = {
    val id = addUninterpreted("$$or", boolTy ->: boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyOr: Term = Term.mkAtom(proxyOr_id)(this)
  final def proxyOr(l: Term, r: Term): Term = Term.mkTermApp(proxyOr, Seq(l,r))
  ///// impl/if/equiv
  lazy val proxyImpl_id: Signature#Key = {
    val id = addUninterpreted("$$impl", boolTy ->: boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyImpl: Term = Term.mkAtom(proxyImpl_id)(this)
  final def proxyImpl(l: Term, r: Term): Term = Term.mkTermApp(proxyImpl, Seq(l,r))

  lazy val proxyIf_id: Signature#Key = {
    val id = addUninterpreted("$$if", boolTy ->: boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyIf: Term = Term.mkAtom(proxyIf_id)(this)
  final def proxyIf(l: Term, r: Term): Term = Term.mkTermApp(proxyIf, Seq(l,r))

  lazy val proxyEquiv_id: Signature#Key = {
    val id = addUninterpreted("$$equiv", boolTy ->: boolTy ->: boolTy, Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyEquiv: Term = Term.mkAtom(proxyEquiv_id)(this)
  final def proxyEquiv(l: Term, r: Term): Term = Term.mkTermApp(proxyEquiv, Seq(l,r))
  ///// forall/exists
  lazy val proxyForall_id: Signature#Key = {
    val id = addUninterpreted("$$forall", ∀((1 ->: boolTy) ->: boolTy), Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyForall: Term = Term.mkAtom(proxyForall_id)(this)
  final def proxyForall(body: Term): Term = Term.mkApp(proxyForall, Seq(Right(body.ty._funDomainType), Left(body)))

  lazy val proxyExists_id: Signature#Key = {
    val id = addUninterpreted("$$exists", ∀((1 ->: boolTy) ->: boolTy), Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyExists: Term = Term.mkAtom(proxyExists_id)(this)
  final def proxyExists(body: Term): Term = Term.mkApp(proxyExists, Seq(Right(body.ty._funDomainType), Left(body)))

  ///// eq / neq
  lazy val proxyEq_id: Signature#Key = {
    val id = addUninterpreted("$$eq", ∀(1 ->: 1 ->: boolTy), Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyEq: Term = Term.mkAtom(proxyEq_id)(this)
  final def proxyEq(l: Term, r: Term): Term = Term.mkApp(proxyEq, Seq(Right(l.ty), Left(l), Left(r)))

  lazy val proxyNeq_id: Signature#Key = {
    val id = addUninterpreted("$$neq", ∀(1 ->: 1 ->: boolTy), Signature.PropNoProp)
    usedAuxSymbols0 += id
    id
  }
  lazy val proxyNeq: Term = Term.mkAtom(proxyNeq_id)(this)
  final def proxyNeq(l: Term, r: Term): Term = Term.mkApp(proxyNeq, Seq(Right(l.ty), Left(l), Left(r)))

  /// Proxy symbol axioms
  private final lazy val proxyAxioms0: Map[Signature#Key, Term] = {
    import TypedFOLEncodingSignature._
    val X = Term.mkBound(boolTy, 1)
    val Y = Term.mkBound(boolTy, 2)
    val polyX = Term.mkBound(1, 1)
    val polyY = Term.mkBound(1, 2)
    import Term.λ
    Map(
      proxyTrue_id -> hBool(proxyTrue),
      proxyFalse_id -> Not(hBool(proxyFalse)),
      proxyNot_id -> Equiv(Not(hBool(X)), hBool(proxyNot(X))),
      proxyAnd_id -> Equiv(And(hBool(X), hBool(Y)), hBool(proxyAnd(X,Y))),
      proxyOr_id -> Equiv(Or(hBool(X), hBool(Y)), hBool(proxyOr(X,Y))),
      proxyImpl_id -> Equiv(Impl(hBool(X), hBool(Y)), hBool(proxyImpl(X,Y))),
      proxyIf_id -> Equiv(If(hBool(X), hBool(Y)), hBool(proxyIf(X,Y))),
      proxyEquiv_id -> Equiv(Equiv(hBool(X), hBool(Y)), hBool(proxyEquiv(X,Y))),
      proxyEq_id -> Equiv(Eq(polyX, polyY), hBool(proxyEq(polyX,polyY))),
      proxyNeq_id -> Equiv(Neq(polyX, polyY), hBool(proxyNeq(polyX,polyY))),
      proxyForall_id -> Equiv(Forall(λ(1)(hBool(Term.mkTermApp(Term.mkBound(1 ->: boolTy, 2), Term.mkBound(1,1))))),hBool(proxyForall(Term.mkBound(1 ->: boolTy, 1)))),
      proxyExists_id -> Equiv(Exists(λ(1)(hBool(Term.mkTermApp(Term.mkBound(1 ->: boolTy, 2), Term.mkBound(1,1))))),hBool(proxyExists(Term.mkBound(1 ->: boolTy, 1))))
    )
  }
  final def proxyAxiom(symbol: Signature#Key): Option[Term] = proxyAxioms0.get(symbol)

}

