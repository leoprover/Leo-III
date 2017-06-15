package leo.modules.encoding

import leo.Out
import leo.datastructures.{Clause, Literal, Signature, Term, Type}
import scala.annotation.tailrec

/**
  * Object for transforming higher-order problems into polymorphic first-order problems.
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  * @since February 2017
  */
object TypedFOLEncoding {
  type Result = (EncodedProblem, AuxiliaryFormulae, EncodingSignature)

  final def apply(problem: Problem, les: LambdaElimStrategy)(implicit sig: Signature): Result = {
    import leo.modules.termToClause
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()
    // Analyze problem and insert problem-specific symbols into signature (encoded types)
    val functionTable = EncodingAnalyzer.analyze(problem)
    val fIt = functionTable.iterator
    var proxyAxioms: Set[Clause] = Set.empty
    while (fIt.hasNext) {
      val (f, info) = fIt.next()
      val fMeta = sig(f)
      if (fMeta.isFixedSymbol) {
        // if a fixed symbol occurs in the arity table it means it was used as a subterm
        // so we need to add the associated proxySymbol with the given minimal arity
        // to the signature
        val foType = foTransformType(fMeta._ty, info)(sig, foSig)
        val id = foSig.addUninterpreted(TypedFOLEncodingSignature.proxyOf(fMeta.name), foType)
        proxyAxioms += termToClause(foSig.proxyAxiomOf(id))
      } else {
        val foType = foTransformType(fMeta._ty, info)(sig, foSig)
        foSig.addUninterpreted(escape(fMeta.name), foType)
      }
    }
    if (foSig.exists(safeName(TypedFOLEncodingSignature.boolTy_name))) {
      val trueProxy = TypedFOLEncodingSignature.proxyOf("$true")
      val falseProxy = TypedFOLEncodingSignature.proxyOf("$false")
      if (!foSig.exists(trueProxy)) {
        val id = foSig.addUninterpreted(trueProxy, foSig.boolTy)
        proxyAxioms += termToClause(foSig.proxyAxiomOf(id))
      }
      if (!foSig.exists(falseProxy)) {
        val id = foSig.addUninterpreted(falseProxy, foSig.boolTy)
        proxyAxioms += termToClause(foSig.proxyAxiomOf(id))
      }
    }
    // Translate
    val lambdaEliminator = les(foSig)
    val resultProblem: Problem = problem.map(translate(_, lambdaEliminator)(sig, foSig))
    // Collect auxiliary definitions from lambda elimination (if any)
    val auxDefsFromLES: Set[Clause] = lambdaEliminator.getAuxiliaryDefinitions.map(leo.modules.termToClause(_))
    (resultProblem, proxyAxioms union auxDefsFromLES, foSig)
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
  protected[encoding] final def foTransformType0(ty: Type, replaceO: Boolean)(holSignature: Signature,
                                               encodingSignature: TypedFOLEncodingSignature): Type = {
    import leo.datastructures.Type._
    import leo.modules.HOLSignature
    ty match {
      case HOLSignature.o => if (replaceO) encodingSignature.boolTy else TypedFOLEncodingSignature.o
      case HOLSignature.i => TypedFOLEncodingSignature.i
      case BaseType(tyId) =>
        val name = escape(holSignature(tyId).name)
        if (encodingSignature.exists(name)) Type.mkType(encodingSignature(name).key)
        else Type.mkType(encodingSignature.addBaseType(name))
      case ComposedType(tyConId, tyArgs) =>
        val tyConstructorMeta = holSignature(tyConId)
        val tyConstructorName = escape(tyConstructorMeta.name)
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
  final def translate(cl: Clause, les: LambdaElimination)
                     (holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Clause = {
    leo.Out.finest(s"Translating clause: ${cl.pretty(holSignature)}")
    Clause(cl.lits.map(translate(_, les)(holSignature, encodingSignature)))
  }

  final def translate(lit: Literal, les: LambdaElimination)
                     (holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Literal = {
    leo.Out.finest(s"Translating literal: ${lit.pretty(holSignature)}")
    if (lit.equational) {
      assert(lit.left.ty == lit.right.ty)
      if (lit.left.ty == leo.modules.HOLSignature.o) {
        // this is actually an equivalence in first-order world
        val translatedLeft = translate(lit.left, les)(holSignature, encodingSignature)
        val translatedRight = translate(lit.right, les)(holSignature, encodingSignature)
        Literal.mkLit(TypedFOLEncodingSignature.Equiv(translatedLeft, translatedRight), lit.polarity)
      } else {
        // standard equality between terms
        val translatedLeft = translateTerm(lit.left, les)(holSignature, encodingSignature)
        val translatedRight = translateTerm(lit.right, les)(holSignature, encodingSignature)
        Literal.mkLit(translatedLeft, translatedRight, lit.polarity)
      }
    } else {
      val translatedLeft = translate(lit.left, les)(holSignature, encodingSignature)
      leo.Out.finest(s"TranslatedLeft: ${translatedLeft.pretty(encodingSignature)}")
      Literal.mkLit(translatedLeft, lit.polarity)
    }
  }

  final def translate(t: Term, les: LambdaElimination)
                     (holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Term = {
    import Term.local._
    import Term.{:::>, TypeLambda, Bound, Symbol, ∙}
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
        assert(l.ty == r.ty)
        if (l.ty == o) {
          // It is basically an equiv
          val translatedLeft = translate(l,les)(holSignature, encodingSignature)
          val translatedRight = translate(r,les)(holSignature, encodingSignature)
          Equiv(translatedLeft,translatedRight)
        } else {
          val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
          val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
          Eq(translatedLeft,translatedRight)
        }
      case HOLNeq(l,r) =>
        assert(l.ty == r.ty)
        if (l.ty == o) {
          // It is basically an equiv
          val translatedLeft = translate(l,les)(holSignature, encodingSignature)
          val translatedRight = translate(r,les)(holSignature, encodingSignature)
          Not(Equiv(translatedLeft,translatedRight))
        } else {
          val translatedLeft = translateTerm(l,les)(holSignature, encodingSignature)
          val translatedRight = translateTerm(r,les)(holSignature, encodingSignature)
          Neq(translatedLeft,translatedRight)
        }
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
      // Non-CNF cases end
      // Standard-case begin
      case f ∙ args =>
        val encodedHead = f match {
          case Symbol(id) =>
            val fName = escape(holSignature(id).name)
            mkAtom(encodingSignature(fName).key)(encodingSignature)
          case Bound(boundTy, boundIdx) => mkBound(foTransformType0(boundTy, true)(holSignature, encodingSignature), boundIdx)
          case _ => f
        }
        assert(encodedHead.isAtom)
        val translatedTyArgs = args.takeWhile(_.isRight).map(ty => foTransformType0(ty.right.get, true)(holSignature, encodingSignature))
        val termArgs = args.dropWhile(_.isRight)
        leo.modules.myAssert(termArgs.forall(_.isLeft))
        val translatedTermArgs = termArgs.map(arg => translateTerm(arg.left.get, les)(holSignature, encodingSignature))
        // pass some arguments directly if possible
        val encodedHeadParamTypes = encodedHead.ty.monomorphicBody.funParamTypes
        assert(translatedTermArgs.size >= encodedHeadParamTypes.size)
        val directArgs = translatedTermArgs.take(encodedHeadParamTypes.size)
        val indirectArgs = translatedTermArgs.drop(encodedHeadParamTypes.size)

        val tyArgsApplied = mkTypeApp(encodedHead, translatedTyArgs)
        val directArgsApplied = mkTermApp(tyArgsApplied, directArgs)
        val allApplied = encodingSignature.hApp(directArgsApplied, indirectArgs)
        leo.modules.myAssert(allApplied.ty == o || allApplied.ty == encodingSignature.boolTy)
        if (allApplied.ty != o) encodingSignature.hBool(allApplied)
        else allApplied
      // Standard-case end, error cases follow
      case _ :::> _ => throw new IllegalArgumentException("naked lambda at top level")//les.eliminateLambda(lambda)(holSignature)
      case TypeLambda(_) => throw new IllegalArgumentException("naked type lambda at top level")
      case _ => throw new IllegalArgumentException("unexpected term occurred")
    }
  }

  protected[encoding] final def translateTerm(t: Term, les: LambdaElimination)
                         (holSignature: Signature, encodingSignature: TypedFOLEncodingSignature): Term = {
    import Term.local._
    import Term.{Bound, :::>, ∙, Symbol, TypeLambda}
    import leo.modules.HOLSignature.{Forall => HOLForall, Exists => HOLExists,
    & => HOLAnd, ||| => HOLOr, === => HOLEq, !=== => HOLNeq, <=> => HOLEquiv, Impl => HOLImpl, <= => HOLIf,
    Not => HOLNot, LitFalse => HOLFalse, LitTrue => HOLTrue}
    import encodingSignature._
    leo.Out.finest(s"TranslateTerm: ${t.pretty(holSignature)}")
    t match {
      case lambda@(_ :::> _) => les.eliminateLambda(lambda)(holSignature)
      case f ∙ args =>
        val encodedHead = f match {
          case Symbol(id) => leo.Out.finest(s"Translate symbol: $id"); id match {
            case HOLTrue.key => proxyTrue
            case HOLFalse.key => proxyFalse
            case HOLNot.key => proxyNot
            case HOLOr.key => proxyOr
            case HOLAnd.key => proxyAnd
            case HOLEq.key => proxyEq
            case HOLNeq.key => proxyNeq
            case HOLForall.key => proxyForall
            case HOLExists.key => proxyExists
            case HOLImpl.key => proxyImpl
            case HOLIf.key => proxyIf
            case HOLEquiv.key => proxyEquiv
            case _ =>
              val fName = escape(holSignature(id).name)
              mkAtom(encodingSignature(fName).key)(encodingSignature)
          }
          case Bound(boundTy, boundIdx) => leo.Out.finest(s"Translate bound $boundIdx of type ${boundTy.pretty(holSignature)}"); mkBound(foTransformType0(boundTy, true)(holSignature, encodingSignature), boundIdx)
          case _ => assert(false); f
        }
        Out.finest(s"encodedHead: ${encodedHead.pretty(encodingSignature)}")
        Out.finest(s"encodedHead: ${encodedHead.ty.pretty(encodingSignature)}")
        assert(encodedHead.isAtom)
        val translatedTyArgs = args.takeWhile(_.isRight).map(ty => foTransformType0(ty.right.get, true)(holSignature, encodingSignature))
        val termArgs = args.dropWhile(_.isRight)
        leo.modules.myAssert(termArgs.forall(_.isLeft))
        val translatedTermArgs = termArgs.map(arg => translateTerm(arg.left.get, les)(holSignature, encodingSignature))
        // pass some arguments directly if possible
        val encodedHeadParamTypes = encodedHead.ty.monomorphicBody.funParamTypes
        assert(translatedTermArgs.size >= encodedHeadParamTypes.size, s"original head: ${f.pretty(holSignature)}\n" +
          s"original type: ${f.ty.pretty(holSignature)}" +
          s"\nencodedhead: ${encodedHead.pretty(encodingSignature)},\n encodedHeadParamsize: ${encodedHeadParamTypes.size},\n" +
          s" encodedHeadTy:${encodedHead.ty.pretty(encodingSignature)},\n arg size ${translatedTermArgs.size}")
        val directArgs = translatedTermArgs.take(encodedHeadParamTypes.size)

        val indirectArgs = translatedTermArgs.drop(encodedHeadParamTypes.size)

        val tyArgsApplied = mkTypeApp(encodedHead, translatedTyArgs)
        leo.Out.finest(s"tyArgsApplied: ${tyArgsApplied.pretty(encodingSignature)}")
        val directArgsApplied = mkTermApp(tyArgsApplied, directArgs)
        leo.Out.finest(s"directArgsApplied: ${directArgsApplied.pretty(encodingSignature)}")
        val result = encodingSignature.hApp(directArgsApplied, indirectArgs)
        leo.Out.finest(s"result: ${result.pretty(encodingSignature)}")
        result
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
  type ArityTable = Map[Signature.Key, SymbolInfo]

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
      assert(lit.left.ty == lit.right.ty)
      if (lit.left.ty == leo.modules.HOLSignature.o)
        merge(analyzeFormula(lit.left), analyzeFormula(lit.right)) // can be treated as <=> at top-level
      else
        merge(analyzeTerm(lit.left), analyzeTerm(lit.right))
    } else analyzeFormula(lit.left)
  }

  private final val fixedConnectives: Set[Signature.Key] = { // logical fixed constants
    // without equality since it starts term level. quantifiers are dealt with separately.
    import leo.modules.HOLSignature._
    Set(Not.key, &.key, |||.key, Impl.key, <=.key, <=>.key)
  }
  final def analyzeFormula(t: Term): ArityTable = {
    import leo.datastructures.Term._
    import leo.modules.HOLSignature.{Forall, Exists, TyForall, ===, !===, o}
    t match {
      case Forall(_ :::> body) =>
//        merge(Forall.key -> (1, false), analyzeFormula(body))
        analyzeFormula(body)
      case Exists(_ :::> body) =>
//        merge(Exists.key -> (1, false), analyzeFormula(body))
        analyzeFormula(body)
      case TyForall(TypeLambda(body)) =>
//        merge(TyForall.key -> (1, false), analyzeFormula(body))
        analyzeFormula(body)
      case l === r =>
        assert(l.ty == r.ty)
        if (l.ty == o) merge(analyzeFormula(l), analyzeFormula(r))
        else merge(analyzeTerm(l), analyzeTerm(r))
      case l !=== r =>
        assert(l.ty == r.ty)
        if (l.ty == o) merge(analyzeFormula(l), analyzeFormula(r))
        else merge(analyzeTerm(l), analyzeTerm(r))
      case f ∙ args => f match {
        case Symbol(id) =>
          val argArity = arity(args)
          if (fixedConnectives.contains(id))
//            merge(id -> (argArity, false), analyzeArgs(args))
            analyzeArgs(args)
          else
            merge(id -> (argArity, false), analyzeTermArgs(args))
        case _ => analyzeTermArgs(args)
      }
      case _ :::> _ => throw new IllegalArgumentException("naked lambda at formula level") //analyzeFormula(body)
      case TypeLambda(_) => throw new IllegalArgumentException("naked type lambda at formula level")  // analyzeFormula(body)
    }
  }
  @tailrec final def analyzeTerm(t: Term): ArityTable = {
    import leo.datastructures.Term._
    t match {
      case f ∙ args => f match {
        case Symbol(id) =>
          val argArity = arity(args)
          merge(id -> (argArity, true), analyzeTermArgs(args))
        case _ => analyzeTermArgs(args)
      }
      case _ :::> body => analyzeTerm0(body, 1)
      case TypeLambda(body) => analyzeTerm(body) // TODO: ???
    }
  }
  @tailrec final def analyzeTerm0(t: Term, depth: Int): ArityTable = {
    import leo.datastructures.Term._
    t match {
      case _ :::> body => analyzeTerm0(body, depth+1)
      case f ∙ args => f match {
        case Symbol(id) =>
          val argArity = safeArity(args, depth)
          merge(id -> (argArity, true), analyzeTermArgs0(args, depth))
        case _ => analyzeTermArgs0(args,depth)
      }
      case TypeLambda(body) => analyzeTerm0(body, depth) // TODO: ???
    }
  }

  private final def arity(args: Seq[Either[Term, Type]]): MinArity = {
    val termArgs = args.dropWhile(_.isRight)
    leo.modules.myAssert(termArgs.forall(_.isLeft))
    termArgs.size
  }
  private final def safeArity(args: Seq[Either[Term, Type]], depth: Int): MinArity = {
    val termArgs = args.dropWhile(_.isRight)
    leo.modules.myAssert(termArgs.forall(_.isLeft))
    // count those are from left to right which do not include
    // bound vars from lambda binders freely.
    val safeArgs = termArgs.takeWhile(_.left.get.looseBounds.forall(_ > depth))
    safeArgs.size
  }

  private final def analyzeArgs(args: Seq[Either[Term, Type]]): ArityTable = {
    var result: ArityTable = Map()
    val argsIt = args.iterator
    while (argsIt.hasNext) {
      val arg = argsIt.next()
      if (arg.isLeft) {
        val arityTable = analyzeFormula(arg.left.get)
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
  private final def analyzeTermArgs0(args: Seq[Either[Term, Type]], depth: Int): ArityTable = {
    var result: ArityTable = Map()
    val argsIt = args.iterator
    while (argsIt.hasNext) {
      val arg = argsIt.next()
      if (arg.isLeft) {
        val arityTable = analyzeTerm0(arg.left.get, depth)
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

  private final def merge(entry: (Signature.Key, SymbolInfo), t2: ArityTable): ArityTable = {
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

  import leo.datastructures.Term.local._
  final val True: Term = mkAtom(3, o)
  final val False: Term = mkAtom(4, o)
  final val Not: Term = mkAtom(5, oo)
  final def Not(t: Term): Term = mkTermApp(Not, t)
  final val And: Term = mkAtom(6, ooo)
  final def And(l: Term, r: Term): Term = mkTermApp(And, Seq(l,r))
  final val Or: Term = mkAtom(7, ooo)
  final def Or(l: Term, r: Term): Term = mkTermApp(Or, Seq(l,r))
  final val Impl: Term = mkAtom(8, ooo)
  final def Impl(l: Term, r: Term): Term = mkTermApp(Impl, Seq(l,r))
  final val If: Term = mkAtom(9, ooo)
  final def If(l: Term, r: Term): Term = mkTermApp(If, Seq(l,r))
  final val Equiv: Term = mkAtom(10, ooo)
  final def Equiv(l: Term, r: Term): Term = mkTermApp(Equiv, Seq(l,r))
  final val Eq: Term = mkAtom(11, aao)
  final def Eq(l: Term, r: Term): Term = mkApp(Eq, Seq(Right(l.ty), Left(l), Left(r)))
  final val Neq: Term = mkAtom(12, aao)
  final def Neq(l: Term, r: Term): Term = mkApp(Neq, Seq(Right(l.ty), Left(l), Left(r)))
  final val Forall: Term = mkAtom(13, aoo)
  final def Forall(body: Term): Term = mkApp(Forall, Seq(Right(body.ty._funDomainType), Left(body)))
  final val Exists: Term = mkAtom(14, aoo)
  final def Exists(body: Term): Term = mkApp(Forall, Seq(Right(body.ty._funDomainType), Left(body)))
  final val TyForall: Term = mkAtom(15, faoo)
  final def TyForall(body: Term): Term = mkTermApp(TyForall, body)

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

  // Names
  //// Aux symbols
  final val funTy_name: String = "fun"
  final val boolTy_name: String = "bool"
  final val hApp_name: String = "app"
  final val hBool_name: String = "hBool"
  //// proxies
  final val proxyTrue_name: String = "true"
  final val proxyFalse_name: String = "false"
  final val proxyNot_name: String = "not"
  final val proxyAnd_name: String = "and"
  final val proxyOr_name: String = "or"
  final val proxyImpl_name: String = "impl"
  final val proxyIf_name: String = "if"
  final val proxyEquiv_name: String = "equiv"
  final val proxyForall_name: String = "forall"
  final val proxyExists_name: String = "exists"
  final val proxyEq_name: String = "eq"
  final val proxyNeq_name: String = "neq"

  final val proxyOf: Map[String, String] = Map(
    "$true" -> safeName(proxyTrue_name),
    "$false" -> safeName(proxyFalse_name),
    "~" -> safeName(proxyNot_name),
    "&" -> safeName(proxyAnd_name),
    "|" -> safeName(proxyOr_name),
    "=>" -> safeName(proxyImpl_name),
    "<=" -> safeName(proxyIf_name),
    "<=>" -> safeName(proxyEquiv_name),
    "!" -> safeName(proxyForall_name),
    "?" -> safeName(proxyExists_name),
    "=" -> safeName(proxyEq_name),
    "!=" -> safeName(proxyNeq_name)
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

  import TypedFOLEncodingSignature._

  // Definitions of auxiliary symbols for FO encoding
  /// Meta symbols
  ///// fun type constant
  lazy val funTy_id: Signature.Key = addTypeConstructor(safeName(funTy_name), * ->: * ->: *)
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
  lazy val boolTy_id: Signature.Key = addTypeConstructor(safeName(boolTy_name), *)
  lazy val boolTy: Type = mkType(boolTy_id)

  ///// hApp constant
  private final lazy val hApp_type: Type = ∀(∀(funTy(2,1) ->: Type.mkVarType(2) ->: Type.mkVarType(1)))
  lazy val hApp_id: Signature.Key = addUninterpreted(safeName(hApp_name), hApp_type, Signature.PropNoProp)
  import leo.datastructures.Term.local._
  lazy val hApp: Term = mkAtom(hApp_id)(this)
  final def hApp(fun: Term, arg: Term): Term = {
    leo.Out.finest(s"hApp fun/arg invoke with ...")
    leo.Out.finest(s"fun ${fun.pretty}")
    leo.Out.finest(s"fun.ty ${fun.ty.pretty}")
    leo.Out.finest(s"arg ${arg.pretty}")
    leo.Out.finest(s"arg.ty ${arg.ty.pretty}")
    val (id, tyArgs) = Type.ComposedType.unapply(fun.ty).get
    assert(id == funTy_id)
    assert(tyArgs.size == 2)
//    Out.finest(s"hApp call")
//    Out.finest(s"fun: ${fun.pretty}")
//    Out.finest(s"funty: ${fun.ty.pretty}")
//    Out.finest(s"arg: ${arg.pretty}")
//    Out.finest(s"argty: ${arg.ty.pretty}")
//    Out.finest(s"fun tyArg(0): ${tyArgs.head.pretty}")
//    Out.finest(s"fun tyArg(1): ${tyArgs.tail.head.pretty}")
//    Out.finest(s"hApp instance: ${mkApp(hApp, Seq(Right(tyArgs.head), Right(tyArgs.tail.head))).ty.pretty}")
    mkApp(hApp, Seq(Right(tyArgs.head), Right(tyArgs.tail.head), Left(fun), Left(arg)))
  }
  @tailrec
  final def hApp(fun: Term, args: Seq[Term]): Term = {
    if (args.isEmpty) fun
    else {
      val hd = args.head
      val partiallyApplied = hApp(fun, hd)
      hApp(partiallyApplied, args.tail)
    }
  }
  private final def encodedFunSpaces(ty: Type): (Type, Type) = {
    import leo.datastructures.Type.ComposedType
    val res = ComposedType.unapply(ty)
    if (res.isDefined) {
      val id = res.get._1
      if (id == funTy_id) {
        val args = res.get._2
        assert(args.size == 2)
        (args.head, args.tail.head)
      } else throw new IllegalArgumentException(s"Invoking domain/codomain type on non-functional type ${ty.pretty} in TypedFOLEncoding.")
    } else throw new IllegalArgumentException(s"Invoking domain/codomain type on non-functional type ${ty.pretty} in TypedFOLEncoding.")
  }

  ///// hBool constant
  private final lazy val hBool_type: Type = boolTy ->: o
  lazy val hBool_id: Signature.Key = addUninterpreted(safeName(hBool_name), hBool_type, Signature.PropNoProp)
  lazy val hBool: Term = mkAtom(hBool_id)(this)
  final def hBool(boolTerm: Term): Term = mkTermApp(hBool, boolTerm)

  /// Proxy symbols
  @inline final private def proxyId(proxyName: String): Signature.Key = {
    val name = safeName(proxyName)
    if (exists(name)) {
      usedProxies += apply(name).key
      apply(name).key
    }
    else throw new IllegalArgumentException
  }

  final private def applyArgs(func: Term, args: Seq[Term]): Term = {
    val funcTyArgTypes = func.ty.funParamTypes
    val directArgCount = funcTyArgTypes.size
    assert(args.size >= directArgCount)
    val directArgs = args.take(directArgCount)
    val indirectArgs = args.drop(directArgCount)
    val partiallyAppliedFunc = mkTermApp(func, directArgs)
    hApp(partiallyAppliedFunc, indirectArgs)
  }
  ///// True/False proxy
  lazy val proxyTrue_id: Signature.Key = proxyId(proxyTrue_name)
  lazy val proxyTrue: Term = mkAtom(proxyTrue_id)(this)

  lazy val proxyFalse_id: Signature.Key = proxyId(proxyFalse_name)
  lazy val proxyFalse: Term = mkAtom(proxyFalse_id)(this)

  ///// Not proxy
  lazy val proxyNot_id: Signature.Key = proxyId(proxyNot_name)
  lazy val proxyNot: Term = mkAtom(proxyNot_id)(this)
  final def proxyNot(body: Term): Term = applyArgs(proxyNot, Seq(body))

  ///// and/or proxy
  lazy val proxyAnd_id: Signature.Key = proxyId(proxyAnd_name)
  lazy val proxyAnd: Term = mkAtom(proxyAnd_id)(this)
  final def proxyAnd(l: Term, r: Term): Term = applyArgs(proxyAnd, Seq(l,r))

  lazy val proxyOr_id: Signature.Key = proxyId(proxyOr_name)
  lazy val proxyOr: Term = mkAtom(proxyOr_id)(this)
  final def proxyOr(l: Term, r: Term): Term = applyArgs(proxyOr, Seq(l,r))

  ///// impl/if/equiv
  lazy val proxyImpl_id: Signature.Key = proxyId(proxyImpl_name)
  lazy val proxyImpl: Term = mkAtom(proxyImpl_id)(this)
  final def proxyImpl(l: Term, r: Term): Term = applyArgs(proxyImpl, Seq(l,r))

  lazy val proxyIf_id: Signature.Key = proxyId(proxyIf_name)
  lazy val proxyIf: Term = mkAtom(proxyIf_id)(this)
  final def proxyIf(l: Term, r: Term): Term = applyArgs(proxyIf, Seq(l,r))

  lazy val proxyEquiv_id: Signature.Key = proxyId(proxyEquiv_name)
  lazy val proxyEquiv: Term = mkAtom(proxyEquiv_id)(this)
  final def proxyEquiv(l: Term, r: Term): Term = applyArgs(proxyEquiv, Seq(l,r))

  ///// forall/exists
  lazy val proxyForall_id: Signature.Key = proxyId(proxyForall_name)
  lazy val proxyForall: Term = mkAtom(proxyForall_id)(this)
  final def proxyForall(body: Term): Term = {
    val domainType = encodedFunSpaces(body.ty)._1
    applyArgs(mkTypeApp(proxyForall, domainType), Seq(body))
  }

  lazy val proxyExists_id: Signature.Key = proxyId(proxyExists_name)
  lazy val proxyExists: Term = mkAtom(proxyExists_id)(this)
  final def proxyExists(body: Term): Term = {
    val domainType = encodedFunSpaces(body.ty)._1
    applyArgs(mkTypeApp(proxyExists,domainType), Seq(body))
  }

  ///// eq / neq
  lazy val proxyEq_id: Signature.Key = proxyId(proxyEq_name)
  lazy val proxyEq: Term = mkAtom(proxyEq_id)(this)
  final def proxyEq(l: Term, r: Term): Term = applyArgs(mkTypeApp(proxyEq, l.ty), Seq(l, r))

  lazy val proxyNeq_id: Signature.Key = proxyId(proxyNeq_name)
  lazy val proxyNeq: Term = mkAtom(proxyNeq_id)(this)
  final def proxyNeq(l: Term, r: Term): Term = applyArgs(mkTypeApp(proxyNeq, l.ty), Seq(l, r))

  private var usedProxies: Set[Signature.Key] = Set.empty
  final def proxiesUsed: Set[Signature.Key] = usedProxies

  /// Proxy symbol axioms
  final def proxyAxiomOf(proxyId: Signature.Key): Term = {
    val X = mkBound(boolTy, 1)
    val Y = mkBound(boolTy, 2)
    val polyX = mkBound(1, 1)
    val polyY = mkBound(1, 2)
    deSafeName(meta(proxyId).name) match {
      case `proxyTrue_name` => hBool(proxyTrue)
      case `proxyFalse_name` => Not(hBool(proxyFalse))
      case `proxyNot_name` => Equiv(Not(hBool(X)), hBool(proxyNot(X)))
      case `proxyAnd_name` => Equiv(And(hBool(X), hBool(Y)), hBool(proxyAnd(X,Y)))
      case `proxyOr_name` => Equiv(Or(hBool(X), hBool(Y)), hBool(proxyOr(X,Y)))
      case `proxyImpl_name` => Equiv(Impl(hBool(X), hBool(Y)), hBool(proxyImpl(X,Y)))
      case `proxyIf_name` => Equiv(If(hBool(X), hBool(Y)), hBool(proxyIf(X,Y)))
      case `proxyEquiv_name` => Equiv(Equiv(hBool(X), hBool(Y)), hBool(proxyEquiv(X,Y)))
      case `proxyEq_name` => Equiv(Eq(polyX, polyY), hBool(proxyEq(polyX,polyY)))
      case `proxyNeq_name` => Equiv(Neq(polyX, polyY), hBool(proxyNeq(polyX,polyY)))
      case `proxyForall_name` => Equiv(Forall(λ(1)(hBool(hApp(mkBound(funTy(1,boolTy), 2), mkBound(1,1))))),hBool(proxyForall(mkBound(funTy(1,boolTy), 1))))
      case `proxyExists_name` => Equiv(Exists(λ(1)(hBool(hApp(mkBound(funTy(1,boolTy), 2), mkBound(1,1))))),hBool(proxyExists(mkBound(funTy(1,boolTy), 1))))
      case _ => throw new IllegalArgumentException("Given id is not an proxy.")
    }
  }
}

