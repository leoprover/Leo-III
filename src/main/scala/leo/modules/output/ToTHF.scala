package leo.modules.output

import leo.datastructures._
import Term._
import leo.datastructures.Type._
import leo.modules.HOLSignature.{!===, &, <=, <=>, <~>, ===, Choice, Exists, Forall, Impl, LitFalse, Not, TyForall, |||, ~&, ~|||}
import leo.modules.SZSException

import scala.annotation.tailrec

/**
  * Translation module that takes internal terms or types and translates them
  * to a TPTP representation (in THF format).
  * Translation can be done directly into a string by methods `toString`
  * or indirect into an `Output` object by the `output` method.
  *
  * @see [[Term]], [[leo.datastructures.ClauseProxy]], [[leo.modules.output.Output]]
  * @author Alexander Steen
  * @since 07.11.2014
  * @todo Merge lambda and type lambda in backward translation, can we?
  * @todo Check if ordering is important (i.e. all types before definitions), see apply[A <: ClauseProxy](formulas : Set[A]): Seq[Output]
  */
object ToTHF {
  ///////////////////////
  // Methods on ClauseProxys
  ///////////////////////

  /** Return a textual representation of `f` in THF, i.e. returns
    * "thf(id, term, role)." where
    * `id` equals `f.cl.id`, `term` is a representation of `f.cl` as term,
    * `role` equals `f.role.pretty`. */
  final def toString(f: ClauseProxy)(implicit sig: Signature): String = toTPTP(f.id.toString, f.cl, f.role)(sig)
  /** See toString(ClauseProxy).
    * The textual representation is returned as an `Output` object. */
  final def output(f: ClauseProxy)(implicit sig: Signature): Output = new Output {
    def apply() = toTPTP(f.id.toString, f.cl, f.role)(sig)
  }

  /**
    * Return a textual representation of `f` with (optional) annotation in THF, i.e. returns
    * "thf(id, term, role, annotation)." where
    * `id` equals `f.cl.id`, `term` is a representation of `f.cl` as term,
    * `role` equals `f.role.pretty`, `annotation` equals a representation of `f.annotation`. */
  final def withAnnotation(cl: ClauseProxy)(implicit sig: Signature): String = {
      //      val normclause = leo.modules.calculus.Simp(cl.cl)
      toTPTP(cl.id.toString, cl.cl, cl.role, cl.annotation)(sig)
  }
  /** See withAnnotation(ClauseProxy).
    * The textual representation is returned as an `Output` object. */
  final def outputWithAnnotation(cl: ClauseProxy)(implicit sig: Signature): Output = new Output {
    def apply(): String = toTPTP(cl.id.toString, cl.cl, cl.role, cl.annotation)(sig)
  }

  ///////////////////////
  // Methods on symbols/definitions
  ///////////////////////

  final def apply(k: Signature.Key, typeOnly: Boolean = false)(implicit sig: Signature): String = {
    val constant = sig.apply(k)
    val symbolName = constant.name
    if (constant.hasType) {
      val name = s"${unescapeTPTPName(symbolName)}_decl"
      // Its a term constant or a definition
      // Print out type declaration (needed in all cases)
      val tyDecl = s"thf(${escapeTPTPName(name)}, type, $symbolName: ${typeToTHF(constant._ty)(sig)})."
      // If its a definition, also print definition afterwards
      if (constant.hasDefn && !typeOnly) s"${tyDecl}\n${definitionToTPTP(k)(sig)}"
      else tyDecl
    } else {
      // Its a type constant
      assert(constant.hasKind)
      val name = s"${unescapeTPTPName(symbolName)}_type"
      s"thf(${escapeTPTPName(name)}, type, $symbolName: ${toTPTP(constant._kind)})."
    }
  }

  final def output(k: Signature.Key)(implicit sig: Signature): Output = new Output {
    final def apply(): String = ToTHF(k)(sig)
  }

  final def definitionToTPTP(k: Signature.Key)(implicit sig: Signature): String = {
    val constant = sig.apply(k)
    if (constant.hasDefn) {
      val symbolName = constant.name
      val name = s"${unescapeTPTPName(symbolName)}_def"
      s"thf(${escapeTPTPName(name)}, definition, $symbolName = (${toTPTP0(constant._defn, 0)(sig)}) )."
    } else ""
  }

  final def printDefinitions(sig : Signature) : String = {
    val sb : StringBuilder = new StringBuilder
    val keys1 = sig.allUserConstants.iterator
    val keys2 = sig.allUserConstants.iterator
    while(keys1.hasNext){
      val k = keys1.next()
      if(sig(k).hasDefn) {
        val symbolName = sig(k).name
        val name = s"${unescapeTPTPName(symbolName)}_decl"
        sb.append(s"thf(${escapeTPTPName(name)}, type, $name: ${typeToTHF(sig(k)._ty)(sig)}).\n")
      }
    }
    while(keys2.hasNext){
      val k = keys2.next()
      if(sig(k).hasDefn){
        sb.append(definitionToTPTP(k)(sig))
        sb.append("\n")
      }
    }
    sb.toString()
  }

  final def apply(sig: Signature): String = {
    val sb: StringBuilder = new StringBuilder
    for (id <- sig.typeConstructors intersect sig.allUserConstants) {
      val symbolName = sig(id).name
      val name = s"${unescapeTPTPName(symbolName)}_type"
      sb.append(s"thf(${escapeTPTPName(name)}, type, $symbolName: ${toTPTP(sig(id)._kind)}).\n")
    }
    for (id <- sig.uninterpretedSymbols) {
      val symbolName = sig(id).name
      val name = s"${unescapeTPTPName(symbolName)}_decl"
      sb.append(s"thf(${escapeTPTPName(name)}, type, $symbolName: ${typeToTHF(sig(id)._ty)(sig)}).\n")
    }
    sb.toString()
  }




  ///////////////////////////////
  // Translation of other data structures
  ///////////////////////////////

  /**
    * Generate an [[Output]] for substitutions. According to Geoff's proposal .... (e-mail on Oct 26, 2020):
    *
    * ```<general_data>         :== bind(<variable>,<formula_data>) | bind_type(<variable>,<bound_type>)```
    *
    * ```<bound_type>           ::= $thf(<thf_top_level_type>) | $tff(<tff_top_level_type>)```
    *
    *
    * it's bugged, see ...
    * {{{
    * thf(7166,plain,(! [C:(($o > $o) > $o),B:($o > $o),A:($o > $o)] : (((A) = (B)) | ((A) = (^ [D:$o]: (($true)))) | ((A) = (^ [D:$o]: (D))) | ((B) = (^ [D:$o]: (($true)))) | ((B) = (^ [D:$o]: (D))) | ((^ [D:$o]: (($true))) = (^ [D:$o]: (D))) | (sk1 @ (^ [D:$o]: (($true))) @ (A)) | (sk1 @ (^ [D:$o]: (($true))) @ (B)) | (sk1 @ (^ [D:$o]: (D)) @ (A)) | (sk1 @ (^ [D:$o]: (D)) @ (B)) | (~ (C @ (A))) | (C @ (B)))),inference(pattern_uni,[status(thm)],[7165:[bind(A, $thf(A)),bind(B, $thf(B)),bind(C, $thf(A)),bind(D, $thf(B))1•2•λ[ty(1)]. (2:ty(1) -> ty(1) ⋅ (1:ty(1) ⋅ (⊥);⊥))•λ[ty(1)]. (3:ty(1) -> ty(1) ⋅ (1:ty(1) ⋅ (⊥);⊥))↑4]])).
    *
    * thf(7706,plain,(! [B:($o > $o),A:($o > $o)] : (((A) = (B)) | ((A) = (^ [C:$o]: (($true)))) | ((A) = (^ [C:$o]: (C))) | ((B) = (^ [C:$o]: (($true)))) | ((B) = (^ [C:$o]: (C))) | ((^ [C:$o]: (($true))) = (^ [C:$o]: (C))) | (sk1 @ (^ [C:$o]: (($true))) @ (A)) | (sk1 @ (^ [C:$o]: (($true))) @ (B)) | (sk1 @ (^ [C:$o]: (C)) @ (A)) | (sk1 @ (^ [C:$o]: (C)) @ (B)) | (~ ((A) = (A))) | ((A) = (B)))),inference(replace_leibeq,[status(thm)],[7166:[
    * bind(A, $thf(?)),bind(B, $thf(@)),bind(C, $thf((=) @ ($o > $o) @ ?))1•2•3•4•λ[ty(1) -> ty(1)]. (const(11, ∀. 1 -> 1 -> ty(1)) ⋅ (ty(1) -> ty(1);λ[ty(1)]. (3:ty(1) -> ty(1) ⋅ (1:ty(1) ⋅ (⊥);⊥));1:ty(1) -> ty(1) ⋅ (⊥);⊥))↑5]])).
    *}}}
    * for `GRA028^1.p`
    *
    * @param termsubst
    * @param typesubst
    * @param implicitlyBound
    * @param tyVars
    * @param sig
    * @return
    */

  final def apply(termsubst: Subst, typesubst: Subst, implicitlyBound: Seq[(Int, Type)], tyVars: Seq[Int])(implicit sig: Signature): Output = new Output {
    override def apply(): String = {
      var sb = new StringBuilder
      if (termsubst.length > 0) {
        val (_,varmap) = clauseImplicitsToTPTPQuantifierList(implicitlyBound)(sig)
        val varmapMaxKey = if (varmap.nonEmpty) varmap.keySet.max else 0
        val varmapSize = varmap.size
        var i = 1
        val max = termsubst.length
        while (i <= max) { // TODO: Clean up this mess.
          if (varmap.keySet.contains(i)) {
            val erg = termsubst.substBndIdx(i)
            try {
              erg match {
                case TermFront(t) =>
                  val newVars = t.looseBounds.map(k => (k, intToName(varmapSize + k - varmapMaxKey - 1)))
                  val varmap2 = varmap ++ newVars
                  sb.append(s"bind(${varmap.apply(i)}, $$thf(${toTPTP0(t, tyVars.size, varmap2)(sig)}))")
                case BoundFront(j) => sb.append(s"bind(${varmap.apply(i)}, $$thf(${intToName(varmapSize + j - varmapMaxKey - 1)}))")
                case _ => throw new SZSException(SZS_Error, "Types in term substitution")
              }
            } catch {
              case e: Exception => leo.Out.warn(s"Could not translate substitution entry to TPTP format, Exception raised:\n${e.toString}")
                sb.append(s"bind($i, $$$$data(${erg.pretty}))")
            }
            sb.append(",")
          }
          i = i + 1
        }
        if (sb.nonEmpty) sb = sb.init
      }
      if (typesubst.length > 0) {
        if (sb.nonEmpty) sb.append(",")
        (1 to typesubst.length).foreach { i =>
          val erg = typesubst.substBndIdx(i)
          try {
            erg match {
              case BoundFront(n) => sb.append(s"bind_type(T${intToName(i-1)},$$thf(T${intToName(n-1)}))")
              case TermFront(_) => throw new SZSException(SZS_Error, "Term in type substitution")
              case TypeFront(typ) => sb.append(s"bind_type(T${intToName(i-1)},$$thf(${typeToTHF1(typ)(sig)}))")
            }
          } catch {
            case e: Exception => leo.Out.warn(s"Could not translate substitution entry to TPTP format, Exception raised:\n${e.toString}")
            sb.append(s"bind_type($i, $$$$data(${erg.pretty}))")
          }
          sb.append(",")
        }
        if (sb.nonEmpty) sb = sb.init
      }
      sb.toString()
    }
  }

  ///////////////////////////////
  // Translation of clause to THF formula
  ///////////////////////////////
  final def toTPTP(name: String, cl: Clause, role: Role, clauseAnnotation: ClauseAnnotation = null)(sig: Signature): String = {
    val sb = new StringBuffer()
    val freeVarsExist = cl.implicitlyBound.nonEmpty || cl.typeVars.nonEmpty
    if (freeVarsExist) {
      sb.append("! [")
      sb.append(cl.typeVars.reverse.map(i => s"T${intToName(i-1)}:$$tType").mkString(","))
      if (cl.typeVars.nonEmpty && cl.implicitlyBound.nonEmpty) sb.append(",")
      val (namedFVEnumeration, bVarMap) = clauseVarsToTPTP(cl.implicitlyBound, typeToTHF1(_)(sig))
      sb.append(namedFVEnumeration)
      sb.append("] : (")
      sb.append(clauseToTPTP(cl, cl.typeVars.size, bVarMap)(sig))
      sb.append(")")
    } else sb.append(clauseToTPTP(cl, 0, Map())(sig)) // only print term

    // Output whole tptp thf statement
    val escapedName = escapeTPTPName(name)
    if (clauseAnnotation == null)
      s"thf($escapedName,${role.pretty},(${sb.toString}))."
    else {
      if (clauseAnnotation == ClauseAnnotation.NoAnnotation)
        s"thf($escapedName,${role.pretty},(${sb.toString}))."
      else
        s"thf($escapedName,${role.pretty},(${sb.toString}),${clauseAnnotation.pretty})."
    }
//    val clauseFreeVars = cl.implicitlyBound.map { case (i,t) => s"$i:${t.pretty(sig)}"}.mkString("[", ",", "]")
//    val clauseFreeTypeVars = cl.typeVars.map(_.toString).mkString("[", ",", "]")
//    s"$result % vars = $clauseFreeVars; tyVars = $clauseFreeTypeVars"
  }

  final private def clauseToTPTP(cl: Clause, tyVarCount: Int, bVarMap: Map[Int, String])(sig: Signature): String = {
    val sb = new StringBuilder
    if (cl.lits.isEmpty) {
      sb.append(toTPTP0(LitFalse,tyVarCount)(sig))
    } else {
      val litIt = cl.lits.iterator
      while (litIt.hasNext) {
        val lit = litIt.next()
        if (lit.equational) {
          val (left,right) = (lit.left, lit.right)
          if (lit.polarity)
            left match {
              case Bound(_,_) | Symbol(_) => right match {
                case Bound(_,_) | Symbol(_) => sb.append(s"(${toTPTP0(left,tyVarCount, bVarMap)(sig)} = ${toTPTP0(right,tyVarCount, bVarMap)(sig)})")
                case _ => sb.append(s"(${toTPTP0(left,tyVarCount,bVarMap)(sig)} = (${toTPTP0(right,tyVarCount,bVarMap)(sig)}))")
              }
              case _ => right match {
                case Bound(_,_) | Symbol(_) => sb.append(s"((${toTPTP0(left,tyVarCount,bVarMap)(sig)}) = ${toTPTP0(right,tyVarCount,bVarMap)(sig)})")
                case _ => sb.append(s"((${toTPTP0(left,tyVarCount,bVarMap)(sig)}) = (${toTPTP0(right,tyVarCount,bVarMap)(sig)}))")
              }
            }
          else
            left match {
              case Bound(_,_) | Symbol(_) => right match {
                case Bound(_,_) | Symbol(_) => sb.append(s"(${toTPTP0(left,tyVarCount,bVarMap)(sig)} != ${toTPTP0(right,tyVarCount,bVarMap)(sig)})")
                case _ => sb.append(s"(${toTPTP0(left,tyVarCount,bVarMap)(sig)} != (${toTPTP0(right,tyVarCount,bVarMap)(sig)}))")
              }
              case _ => right match {
                case Bound(_,_) | Symbol(_) => sb.append(s"((${toTPTP0(left,tyVarCount,bVarMap)(sig)}) != ${toTPTP0(right,tyVarCount,bVarMap)(sig)})")
                case _ => sb.append(s"((${toTPTP0(left,tyVarCount,bVarMap)(sig)}) != (${toTPTP0(right,tyVarCount,bVarMap)(sig)}))")
              }
            }
        } else {
          val term = lit.left
          term match {
            case Bound(_,_) | Symbol(_) => if (lit.polarity)
                sb.append(toTPTP0(term,tyVarCount,bVarMap)(sig))
              else
                sb.append(s"${sig(Not.key).name} (${toTPTP0(term,tyVarCount, bVarMap)(sig)})")
            case _ => if (lit.polarity)
                sb.append(s"(${toTPTP0(term,tyVarCount,bVarMap)(sig)})")
              else
                sb.append(s"(${sig(Not.key).name} (${toTPTP0(term, tyVarCount,bVarMap)(sig)}))")
          }

        }
        if (litIt.hasNext) sb.append(" | ")
      }
    }
    sb.toString()
  }

  final private def toTPTP0(t: Term, tyVarCount: Int, bVars: Map[Int,String] = Map())(sig: Signature): String = {
    t match {
      // Constant symbols that are (unapplied) connectives, they need to be ()'d
      case Symbol(id) if sig(id).isFixedSymbol =>
        val name = sig(id).name
        s"($name)"
      // Constant symbols
      case Symbol(id) => val name = sig(id).name
        name
      // Numbers
      case Integer(n) => n.toString
      case Rational(n,d) => s"$n/$d"
      case Real(w,d,e) => if (e == 0) s"$w.$d" else s"$w.${d}E$e"
      // Give Bound variables names
      case Bound(_, scope) => bVars(scope)
      // Unary connectives
      case Not(t2) => s"${sig(Not.key).name} (${toTPTP0(t2,tyVarCount, bVars)(sig)})"
      case Forall(_) => val (bVarTys, body) = collectForall(t)
                        val newBVars = makeBVarList(bVarTys, bVars.size)
        body match {
          case Forall(_) | Exists(_) | Not(_) => s"${sig(Forall.key).name} [${newBVars.map({case (s,ty) => s"$s:${typeToTHF1(ty)(sig)}"}).mkString(",")}]: ${toTPTP0(body,tyVarCount, fusebVarListwithMap(newBVars, bVars))(sig)}"
          case _ => s"${sig(Forall.key).name} [${newBVars.map({case (s,ty) => s"$s:${typeToTHF1(ty)(sig)}"}).mkString(",")}]: (${toTPTP0(body,tyVarCount, fusebVarListwithMap(newBVars, bVars))(sig)})"
        }

      case Exists(_) => val (bVarTys, body) = collectExists(t)
                        val newBVars = makeBVarList(bVarTys, bVars.size)
        body match {
          case Forall(_) | Exists(_) | Not(_) => s"${sig(Exists.key).name} [${newBVars.map({case (s,ty) => s"$s:${typeToTHF1(ty)(sig)}"}).mkString(",")}]: ${toTPTP0(body, tyVarCount, fusebVarListwithMap(newBVars,bVars))(sig)}"
          case _ => s"${sig(Exists.key).name} [${newBVars.map({case (s,ty) => s"$s:${typeToTHF1(ty)(sig)}"}).mkString(",")}]: (${toTPTP0(body, tyVarCount, fusebVarListwithMap(newBVars,bVars))(sig)})"
        }
      case TyForall(_) => val (tyAbsCount, body) = collectTyForall(t)
        s"! [${(1 to tyAbsCount).map(i => "T" + intToName(i - 1) + ": $tType").mkString(",")}]: (${toTPTP0(body, tyVarCount+tyAbsCount, bVars)(sig)})"
      case Choice(_) => val (bVarTys, body) = collectChoice(t)
                        val newBVars = makeBVarList(bVarTys, bVars.size)
        body match {
          case Forall(_) | Exists(_) | Not(_) => s"${sig(Choice.key).name} [${newBVars.map({case (s,ty) => s"$s:${typeToTHF1(ty)(sig)}"}).mkString(",")}]: ${toTPTP0(body, tyVarCount, fusebVarListwithMap(newBVars,bVars))(sig)}"
          case _ => s"${sig(Choice.key).name} [${newBVars.map({case (s,ty) => s"$s:${typeToTHF1(ty)(sig)}"}).mkString(",")}]: (${toTPTP0(body, tyVarCount, fusebVarListwithMap(newBVars,bVars))(sig)})"
        }
      // Binary connectives
      case t1 ||| t2 => t1 match {
        case _ ||| _| Not(_) | Forall(_) | Exists(_) => t2 match {
          case _ ||| _ | Not(_) | Forall(_) | Exists(_) => s"${toTPTP0(t1, tyVarCount, bVars)(sig)} ${sig(|||.key).name} ${toTPTP0(t2, tyVarCount,bVars)(sig)}"
          case _ => s"${toTPTP0(t1, tyVarCount, bVars)(sig)} ${sig(|||.key).name} (${toTPTP0(t2, tyVarCount, bVars)(sig)})"
        }
        case _ => t2 match {
          case _ ||| _ | Not(_) | Forall(_) | Exists(_) => s"(${toTPTP0(t1, tyVarCount, bVars)(sig)}) ${sig(|||.key).name} ${toTPTP0(t2, tyVarCount, bVars)(sig)}"
          case _ => s"(${toTPTP0(t1, tyVarCount, bVars)(sig)}) ${sig(|||.key).name} (${toTPTP0(t2, tyVarCount, bVars)(sig)})"
        }
      }
      case t1 & t2 => t1 match {
        case _ & _| Not(_) | Forall(_) | Exists(_) => t2 match {
          case _ & _ | Not(_) | Forall(_) | Exists(_) => s"${toTPTP0(t1, tyVarCount, bVars)(sig)} ${sig(&.key).name} ${toTPTP0(t2, tyVarCount,bVars)(sig)}"
          case _ => s"${toTPTP0(t1, tyVarCount,bVars)(sig)} ${sig(&.key).name} (${toTPTP0(t2,tyVarCount, bVars)(sig)})"
        }
        case _ => t2 match {
          case _ & _ | Not(_) | Forall(_) | Exists(_) => s"(${toTPTP0(t1,tyVarCount, bVars)(sig)}) ${sig(&.key).name} ${toTPTP0(t2,tyVarCount, bVars)(sig)}"
          case _ => s"(${toTPTP0(t1,tyVarCount, bVars)(sig)}) ${sig(&.key).name} (${toTPTP0(t2, tyVarCount,bVars)(sig)})"
        }
      }
      case left === right => left match {
        case Bound(_,_) | Symbol(_) => right match {
          case Bound(_,_) | Symbol(_) => s"${toTPTP0(left,tyVarCount,bVars)(sig)} ${sig(===.key).name} ${toTPTP0(right,tyVarCount,bVars)(sig)}"
          case _ => s"${toTPTP0(left,tyVarCount,bVars)(sig)} ${sig(===.key).name} (${toTPTP0(right,tyVarCount,bVars)(sig)})"
        }
        case _ => right match {
          case Bound(_,_) | Symbol(_) => s"(${toTPTP0(left,tyVarCount,bVars)(sig)}) ${sig(===.key).name} ${toTPTP0(right,tyVarCount,bVars)(sig)}"
          case _ => s"(${toTPTP0(left,tyVarCount,bVars)(sig)}) ${sig(===.key).name} (${toTPTP0(right,tyVarCount,bVars)(sig)})"
        }
      }
      case left !=== right => left match {
        case Bound(_,_) | Symbol(_) => right match {
          case Bound(_,_) | Symbol(_) => s"${toTPTP0(left,tyVarCount,bVars)(sig)} ${sig(!===.key).name} ${toTPTP0(right,tyVarCount,bVars)(sig)}"
          case _ => s"${toTPTP0(left,tyVarCount,bVars)(sig)} ${sig(!===.key).name} (${toTPTP0(right,tyVarCount,bVars)(sig)})"
        }
        case _ => right match {
          case Bound(_,_) | Symbol(_) => s"(${toTPTP0(left,tyVarCount,bVars)(sig)}) ${sig(!===.key).name} ${toTPTP0(right,tyVarCount,bVars)(sig)}"
          case _ => s"(${toTPTP0(left,tyVarCount,bVars)(sig)}) ${sig(!===.key).name} (${toTPTP0(right,tyVarCount,bVars)(sig)})"
        }
      }
      case t1 Impl t2 => s"(${toTPTP0(t1,tyVarCount, bVars)(sig)}) ${sig(Impl.key).name} (${toTPTP0(t2, tyVarCount,bVars)(sig)})"
      case t1 <= t2  => s"(${toTPTP0(t1,tyVarCount, bVars)(sig)}) ${sig(<=.key).name} (${toTPTP0(t2, tyVarCount,bVars)(sig)})"
      case t1 <=> t2 => s"(${toTPTP0(t1,tyVarCount, bVars)(sig)}) ${sig(<=>.key).name} (${toTPTP0(t2, tyVarCount,bVars)(sig)})"
      case t1 ~& t2 => s"(${toTPTP0(t1,tyVarCount, bVars)(sig)}) ${sig(~&.key).name} (${toTPTP0(t2, tyVarCount,bVars)(sig)})"
      case t1 ~||| t2 => s"(${toTPTP0(t1,tyVarCount, bVars)(sig)}) ${sig(~|||.key).name} (${toTPTP0(t2, tyVarCount,bVars)(sig)})"
      case t1 <~> t2 => s"(${toTPTP0(t1,tyVarCount, bVars)(sig)}) ${sig(<~>.key).name} (${toTPTP0(t2, tyVarCount,bVars)(sig)})"
      // General structure
      case _ :::> _ =>
        val t0 = t.etaContract
        if (t != t0) toTPTP0(t0, tyVarCount, bVars)(sig)
        else {
          val (bVarTys, body) = collectLambdas(t)
          val newBVars = makeBVarList(bVarTys, bVars.size)
          body match {
            case Forall(_) | Exists(_) | Not(_) => s"^ [${newBVars.map({case (s,ty) => s"$s:${typeToTHF1(ty)(sig)}"}).mkString(",")}]: ${toTPTP0(body, tyVarCount,fusebVarListwithMap(newBVars, bVars))(sig)}"
            case _ => s"^ [${newBVars.map({case (s,ty) => s"$s:${typeToTHF1(ty)(sig)}"}).mkString(",")}]: (${toTPTP0(body, tyVarCount,fusebVarListwithMap(newBVars, bVars))(sig)})"
          }
        }
      case TypeLambda(_) => val (tyAbsCount, body) = collectTyLambdas(0, t)
        s"^ [${(1 to tyAbsCount).map(i => "T" + intToName(i - 1) + ": $tType").mkString(",")}]: (${toTPTP0(body, tyVarCount+tyAbsCount,bVars)(sig)})"
      case _@Symbol(id) ∙ args if leo.modules.input.InputProcessing.adHocPolymorphicArithmeticConstants.contains(id) =>
        val translatedF = sig(id).name
        val translatedArgs: Seq[String] = args.tail.map(argToTPTP(_, tyVarCount, bVars)(sig)) // drop type argument as it's implicit in the TPTP representation
        s"$translatedF @ ${translatedArgs.mkString(" @ ")}"
      case f ∙ args =>
        val translatedF = toTPTP0(f,tyVarCount, bVars)(sig)
        val translatedArgs: Seq[String] = args.map(argToTPTP(_, tyVarCount, bVars)(sig))
        s"$translatedF @ ${translatedArgs.mkString(" @ ")}"
      // Others should be invalid
      case _ => throw new IllegalArgumentException("Unexpected term format during toTPTP conversion")
    }
  }

  private[this] final def argToTPTP(arg: Either[Term, Type], tyVarCount: Int, bVars: Map[Int, String])(sig: Signature): String = {
    arg match {
      case Left(termArg) => termArg match {
        case Bound(_, _) | Symbol(_) => toTPTP0(termArg,tyVarCount, bVars)(sig)
        case _ => s"(${toTPTP0(termArg,tyVarCount, bVars)(sig)})"
      }
      case Right(tyArg) => typeToTHF1(tyArg)(sig)
    }
  }

  ///////////////////////////////
  // Translation of THF types
  ///////////////////////////////
  final private def toTPTP(k: Kind): String = {
    import leo.datastructures.Kind.{*,->}
    k match {
      case * => "$tType"
      case k1 -> k2 => if (k1.isTypeKind)
        s"$$tType > ${toTPTP(k2)}"
      else
        s"(${toTPTP(k1)}) > ${toTPTP(k2)}"
    }
  }

  final private def typeToTHF(ty: Type)(sig: Signature): String = ty match {
    case ∀(_) => val (tyAbsCount, bodyTy) = collectForallTys(0, ty)
      s"!> [${(1 to tyAbsCount).map(i => s"T${intToName(i - 1)}: $$tType").mkString(",")}]: ${typeToTHF1(bodyTy)(sig)}"
    case _ => typeToTHF1(ty)(sig)
  }
  final private def typeToTHF1(ty: Type)(sig: Signature): String = ty match {
    case BaseType(id) => sig(id).name
    case ComposedType(id, args) => s"(${sig(id).name} @ ${args.map(typeToTHF1(_)(sig)).mkString(" @ ")})"
    case BoundType(scope) => "T" + intToName(scope-1)
    case t1 -> t2 => s"(${typeToTHF1(t1)(sig)} > ${typeToTHF1(t2)(sig)})"
    case ProductType(tys) => tys.map(typeToTHF1(_)(sig)).mkString("[", ",", "]")
    case ∀(_) => throw new IllegalArgumentException("Polytype should have been caught before")
  }

  ///////////////////////////////
  // Utility methods
  ///////////////////////////////
  // Term quantification collection
  /** Gather consecutive all-quantifications (nameless). */
  final private def collectForall(t: Term): (Seq[Type], Term) = {
    collectForall0(Seq.empty, t)
  }
  @tailrec
  @inline final private def collectForall0(vars: Seq[Type], t: Term): (Seq[Type], Term) = {
    t match {
      case Forall(ty :::> b) => collectForall0(vars :+ ty, b)
      case Forall(_) => collectForall0(vars, t.etaExpand)
      case _ => (vars, t)
    }
  }

  /** Gather consecutive all-type-quantifications (nameless). */
  final private def collectTyForall(t: Term): (Int, Term) = {
    collectTyForall0(0, t)
  }
  @tailrec
  @inline final private def collectTyForall0(vars: Int, t: Term): (Int, Term) = {
    t match {
      case TyForall(TypeLambda(body)) => collectTyForall0(vars +1, body)
      case TyForall(_) => throw new IllegalArgumentException("Unexcepted body term in type-forall quantification decomposition.")
      case _ => (vars, t)
    }
  }


  /** Gather consecutive exist-quantifications (nameless). */
  final private def collectExists(t: Term): (Seq[Type], Term) = {
    collectExists0(Seq.empty, t)
  }
  @tailrec
  @inline final private def collectExists0(vars: Seq[Type], t: Term): (Seq[Type], Term) = {
    t match {
      case Exists(ty :::> b) => collectExists0(vars :+ ty, b)
      case Exists(_) => collectExists0(vars, t.etaExpand)
      case _ => (vars, t)
    }
  }

  /** Gather consecutive all-quantifications (nameless). */
  final private def collectChoice(t: Term): (Seq[Type], Term) = {
    collectChoice0(Seq.empty, t)
  }
  @tailrec
  @inline final private def collectChoice0(vars: Seq[Type], t: Term): (Seq[Type], Term) = {
    t match {
      case Choice(ty :::> b) => collectChoice0(vars :+ ty, b)
      case Choice(body) => collectChoice0(vars, Choice(body.etaExpand))
      case _ => (vars, t)
    }
  }

  /** Gather consecutive lambda-abstractions (nameless).
    * Returns [t1, t2, ..., tn] where t1 is the outermost type */
  final private def collectLambdas(t: Term): (Seq[Type], Term) = {
    collectLambdas0(Seq.empty, t)
  }
  @tailrec
  @inline final private def collectLambdas0(vars: Seq[Type], t: Term): (Seq[Type], Term) = {
    t match {
      case ty :::> b => collectLambdas0(vars :+ ty, b)
      case _ => (vars, t)
    }
  }

  @tailrec
  private final def collectTyLambdas(count: Int, t: Term): (Int, Term) = {
    t match {
      case TypeLambda(body) => collectTyLambdas(count+1, body)
      case _ => (count, t)
    }
  }

  // Type quantification collection
  @tailrec
  private final def collectForallTys(count: Int, ty: Type): (Int, Type) = {
    ty match {
      case ∀(t) => collectForallTys(count+1, t)
      case _ => (count, ty)
    }
  }


  final private def clauseImplicitsToTPTPQuantifierList(implicitlyQuantified: Seq[(Int, Type)])(sig: Signature): (String, Map[Int, String]) = {
    val count = implicitlyQuantified.size
    var sb: Seq[String] = Seq()
    var resultBindingMap: Map[Int, String] = Map()

    var curImplicitlyQuantified = implicitlyQuantified
    var i = 0
    while(i < count) {
      val (scope,ty) = curImplicitlyQuantified.head
      curImplicitlyQuantified = curImplicitlyQuantified.tail
      val name = intToName(count - i - 1)
      sb = s"$name:${typeToTHF(ty)(sig)}" +: sb
      resultBindingMap = resultBindingMap + (scope -> name)
      i = i + 1
    }
    (sb.mkString(","), resultBindingMap)
  }
}
