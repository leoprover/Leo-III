package leo.modules.output

import leo.datastructures._
import leo.datastructures.impl.Signature
import Term._
import leo.datastructures.Type._
import leo.datastructures._
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
 */
object ToTPTP {
  ///////////////////////
  // Methods on ClauseProxys
  ///////////////////////

  /** Return a textual representation of `f` in THF, i.e. returns
    * "thf(id, term, role)." where
    * `id` equals `f.cl.id`, `term` is a representation of `f.cl` as term,
    * `role` equals `f.role.pretty`. */
  final def toString(f: ClauseProxy): String = toTPTP(f.id.toString, f.cl, f.role)
  /** See toString(ClauseProxy).
    * The textual representation is returned as an `Output` object. */
  final def output(f: ClauseProxy): Output = new Output {
    def apply = toTPTP(f.id.toString, f.cl, f.role)
  }

  /**
    * Return a textual representation of `f` with (optional) annotation in THF, i.e. returns
    * "thf(id, term, role, annotation)." where
    * `id` equals `f.cl.id`, `term` is a representation of `f.cl` as term,
    * `role` equals `f.role.pretty`, `annotation` equals a representation of `f.annotation`. */
  final def withAnnotation(cl: ClauseProxy): String = {
      //      val normclause = leo.modules.calculus.Simp(cl.cl)
      toTPTP(cl.id.toString, cl.cl, cl.role, cl.annotation)
  }
  /** See withAnnotation(ClauseProxy).
    * The textual representation is returned as an `Output` object. */
  final def outputWithAnnotation(cl: ClauseProxy): Output = new Output {
    def apply = toTPTP(cl.id.toString, cl.cl, cl.role, cl.annotation)
  }

  ///////////////////////
  // Methods on other term inputs
  ///////////////////////

  // TODO: Check if ordering is important (i.e. all types before definitions)
  def apply[A <: ClauseProxy](formulas : Set[A]): Seq[Output] = {
    var out: Seq[Output] = Seq()
    Signature.get.allUserConstants foreach { k =>
      val constDecl = output(k)
      out = constDecl +: out
    }
    formulas foreach {formula =>
      out = ToTPTP.output(formula) +: out}
    out.reverse
  }

  //TODO END

  ///////////////////////
  // Methods on symbols/definitions
  ///////////////////////

  final def apply(k: Signature#Key): String = {
    val constant = Signature.get.apply(k)
    val cname = if (constant.name.startsWith("'") && constant.name.endsWith("'")) {
      "'" + constant.name.substring(1, constant.name.length-1).replaceAll("\\\\", """\\\\""").replaceAll("\\'", """\\'""") + "'"
    } else {
      constant.name
    }
    if (constant.hasType) {
      // Its a term constant or a definition
      // Print out type declaration (needed in all cases)
      val tyDecl = s"thf(${cname}_type, type, $cname: ${toTPTP(constant._ty)})."
      // If its a definition, also print definition afterwards
      if (constant.hasDefn) {
        tyDecl + s"\nthf(${cname}_def, definition, $cname = ${toTPTP0(constant._defn)})."
      } else
        tyDecl
    } else {
      // Its a type constant
      assert(constant.hasKind)
      s"thf($cname, ${Role_Type.pretty}, $cname: ${toTPTP(constant._kind)})."
    }
  }
  final def output(k: Signature#Key): Output = new Output {
    final def apply() = ToTPTP(k)
  }


  ///////////////////////////////
  // Translation of other data structures
  ///////////////////////////////

  def apply(subst: Subst): Output = new Output {
    override def apply: String = {
      if (subst.length == 0) {
        ""
      } else {
        val sb = new StringBuilder
        var i = 1
        val max = subst.length
        while (i < max) {
          val erg = subst.substBndIdx(i)
          sb.append(s"bind($i, $$thf(${erg.pretty})),")
          i = i+1
        }
        sb.append(s"bind($max, $$thf(${subst.substBndIdx(max).pretty}))")
        sb.toString()
      }
    }
  }

  ///////////////////////////////
  // Translation of clause to THF formula
  ///////////////////////////////
  private def toTPTP(name: String, cl: Clause, role: Role, clauseAnnotation: ClauseAnnotation = ClauseAnnotation.NoAnnotation): String = {
    val sb = new StringBuffer()
    if (cl.implicitlyBound.nonEmpty) {
      // make universal quantification and then print term
      val (prefix, bVarMap) = clauseImplicitsToTPTPQuantifierList(cl.implicitlyBound)
      sb.append(s"! [$prefix]: (")
      sb.append(clauseToTPTP(cl, bVarMap))
      sb.append(")")
    } else {
      // only print term
      sb.append(clauseToTPTP(cl, Map()))
    }

    // Output whole tptp thf statement
    if (clauseAnnotation == ClauseAnnotation.NoAnnotation)
      s"thf($name,${role.pretty},(${sb.toString}))."
    else
      s"thf($name,${role.pretty},(${sb.toString}),${clauseAnnotation.pretty})."
  }
  // #new vars - index + #oldvars
  private def clauseToTPTP(cl: Clause, bVarMap: Map[Int, String]): String = {
    val sb = new StringBuilder
    if (cl.lits.isEmpty) {
      sb.append(toTPTP0(LitFalse))
    } else {
      val litIt = cl.lits.iterator
      while (litIt.hasNext) {
        val lit = litIt.next()
        sb.append("(")
        if (lit.equational) {
          val (left,right) = (lit.left, lit.right)
          if (lit.polarity)
            sb.append(toTPTP0(===(left,right), bVarMap))
          else
            sb.append(toTPTP0(Not(===(left,right)), bVarMap))
        } else {
          val term = lit.left
          if (lit.polarity)
            sb.append(toTPTP0(term,bVarMap))
          else
            sb.append(toTPTP0(Not(term), bVarMap))
        }
        sb.append(")")
        if (litIt.hasNext) sb.append(" | ")
      }
    }
    sb.toString()
  }

  private def toTPTP0(t: Term, bVars: Map[Int,String] = Map()): String = "("+{
    val sig = Signature.get
    t match {
      // Constant symbols
      case Symbol(id) => val name = sig(id).name
        if (name.startsWith("'") && name.endsWith("'")) {
          "'" + name.substring(1, name.length-1).replaceAll("\\\\", """\\\\""").replaceAll("\\'", """\\'""") + "'"
        } else {
          name
        }
      // Give Bound variables names
      case m@MetaVar(_,scope) => "mv"+scope
      case Bound(ty, scope) => bVars(scope)
      // Unary connectives
      case Not(t2) => s"${sig(Not.key).name} (${toTPTP0(t2, bVars)})"
      case Forall(_) => val (bVarTys, body) = collectForall(t)
                        val newBVars = makeBVarList(bVarTys, bVars.size)
                        s"! [${newBVars.map({case (s,ty) => s"$s:${toTPTP(ty)}"}).mkString(",")}]: (${toTPTP0(body, fusebVarListwithMap(newBVars, bVars))})"
      case Exists(_) => val (bVarTys, body) = collectExists(t)
                        val newBVars = makeBVarList(bVarTys, bVars.size)
                        s"? [${newBVars.map({case (s,ty) => s"$s:${toTPTP(ty)}"}).mkString(",")}]: (${toTPTP0(body, fusebVarListwithMap(newBVars, bVars))})"
      // Binary connectives
      case t1 ||| t2 => s"${toTPTP0(t1, bVars)} ${sig(|||.key).name} ${toTPTP0(t2, bVars)}"
      case t1 === t2 => s"${toTPTP0(t1, bVars)} ${sig(===.key).name} ${toTPTP0(t2, bVars)}"
      case t1 & t2 => s"${toTPTP0(t1, bVars)} ${sig(&.key).name} ${toTPTP0(t2, bVars)}"
      case t1 Impl t2 => s"${toTPTP0(t1, bVars)} ${sig(Impl.key).name} ${toTPTP0(t2, bVars)}"
      case t1 <= t2  => s"${toTPTP0(t1, bVars)} ${sig(<=.key).name} ${toTPTP0(t2, bVars)}"
      case t1 <=> t2 => s"${toTPTP0(t1, bVars)} ${sig(<=>.key).name} ${toTPTP0(t2, bVars)}"
      case t1 ~& t2 => s"${toTPTP0(t1, bVars)} ${sig(~&.key).name} ${toTPTP0(t2, bVars)}"
      case t1 ~||| t2 => s"${toTPTP0(t1, bVars)} ${sig(~|||.key).name} ${toTPTP0(t2, bVars)}"
      case t1 <~> t2 => s"${toTPTP0(t1, bVars)} ${sig(<~>.key).name} ${toTPTP0(t2, bVars)}"
      case t1 !=== t2 => s"${toTPTP0(t1, bVars)} ${sig(!===.key).name} ${toTPTP0(t2, bVars)}"
      // General structure
      case _ :::> _ => val (bVarTys, body) = collectLambdas(t)
                       val newBVars = makeBVarList(bVarTys, bVars.size)
                       s"^ [${newBVars.map({case (s,ty) => s"$s:${toTPTP(ty)}"}).mkString(",")}]: (${toTPTP0(body, fusebVarListwithMap(newBVars, bVars))})"
      case f ∙ args => args.foldLeft(toTPTP0(f, bVars))({case (str, arg) => s"($str @ ${toTPTP0(arg.fold(identity, _ => throw new IllegalArgumentException), bVars)})"})
      // Others should be invalid
      case _ => throw new IllegalArgumentException("Unexpected term format during toTPTP conversion")
    }
  }+")"

  ///////////////////////////////
  // Translation of THF types
  ///////////////////////////////


  private def toTPTP(ty: Type): String = ty match {
    case BaseType(id) => Signature.get(id).name
    case BoundType(scope) => throw new IllegalArgumentException("TPTP THF backward translation of polymorphic types not supported yet")
    case t1 -> t2 => s"(${toTPTP(t1)} > ${toTPTP(t2)})"
    case t1 * t2 => s"(${toTPTP(t1)} * ${toTPTP(t2)})"
    case t1 + t2 => s"(${toTPTP(t1)} + ${toTPTP(t2)})"
    case ∀(t) => throw new IllegalArgumentException("TPTP THF backward translation of polymorphic types not supported yet")
    /**s"${Signature.get(Forall.key).name} []: ${toTPTP(t)}"*/
  }

  private def toTPTP(k: Kind): String = {
    import leo.datastructures.Kind.->
    k match {
      case Kind.typeKind => "$tType"
      case k1 -> k2 => if (k1.isTypeKind)
        s"$$tType > ${toTPTP(k2)}"
      else
        s"(${toTPTP(k1)}) > ${toTPTP(k2)}"
    }
  }


  ///////////////////////////////
  // Utility methods
  ///////////////////////////////

  /** Gather consecutive all-quantifications (nameless). */
  private def collectForall(t: Term): (Seq[Type], Term) = {
    collectForall0(Seq.empty, t)
  }
  @tailrec
  private def collectForall0(vars: Seq[Type], t: Term): (Seq[Type], Term) = {
    t match {
      case Forall(ty :::> b) => collectForall0(vars :+ ty, b)
      case Forall(_) => throw new IllegalArgumentException("Unexcepted body term in all quantification decomposition.")
      case _ => (vars, t)
    }
  }
  /** Gather consecutive exist-quantifications (nameless). */
  private def collectExists(t: Term): (Seq[Type], Term) = {
    collectExists0(Seq.empty, t)
  }
  @tailrec
  private def collectExists0(vars: Seq[Type], t: Term): (Seq[Type], Term) = {
    t match {
      case Exists(ty :::> b) => collectExists0(vars :+ ty, b)
      case Exists(_) => throw new IllegalArgumentException("Unexcepted body term in existsl quantification decomposition.")
      case _ => (vars, t)
    }
  }

  /** Gather consecutive lambda-abstractions (nameless).
    * Returns [t1, t2, ..., tn] where t1 is the outermost type */
  private def collectLambdas(t: Term): (Seq[Type], Term) = {
    collectLambdas0(Seq.empty, t)
  }
  @tailrec
  private def collectLambdas0(vars: Seq[Type], t: Term): (Seq[Type], Term) = {
    t match {
      case ty :::> b => collectLambdas0(vars :+ ty, b)
      case _ => (vars, t)
    }
  }

  private def makeBVarList(tys: Seq[Type], offset: Int): Seq[(String, Type)] = tys.zipWithIndex.map {case (ty, idx) => (intToName(idx+offset), ty)}
  private def fusebVarListwithMap(bvarList: Seq[(String, Type)], oldbvarMap: Map[Int,String]): Map[Int, String] = {
    val newVarCount = bvarList.size
    val newVarsAsMap = bvarList.zipWithIndex.map {case ((name, ty),idx) => (newVarCount - idx, name)}
    oldbvarMap.map {case (k,v) => (k+newVarCount.size, v)} ++ Map(newVarsAsMap:_*)
  }
  private def clauseImplicitsToTPTPQuantifierList(implicitlyQuantified: Seq[(Int, Type)]): (String, Map[Int, String]) = {
    val count = implicitlyQuantified.size
    val sb = new StringBuilder
    var resultBindingMap: Map[Int, String] = Map()

    var curImplicitlyQuantified = implicitlyQuantified
    var i = 0
    while(i < count) {
      val (scope,ty) = curImplicitlyQuantified.head
      curImplicitlyQuantified = curImplicitlyQuantified.tail
      val name = intToName(i)
      sb.append(s"$name: ${toTPTP(ty)}")
      resultBindingMap = resultBindingMap + (scope -> name)
      i = i + 1
      if (i < count) sb.append(",")
    }

    (sb.toString(), resultBindingMap)
  }

  // Convert i-th variable to a variable name corresponding to ASCII transformation `intToName`
  // 0 ---> "A"
  // 1 ---> "B"
  // 25 ---> "Z"
  // 26 ---> "ZA"
  // etc.

  private val asciiA = 65
  private val asciiZ = 90
  private val range = asciiZ - asciiA // range 0,1,....

  private def intToName(i: Int): String = i match {
    case n if n <= range => s"${intToChar(i)}"
    case n if n > range => s"Z${intToName(i-range-1)}"
  }

  private def intToChar(i: Int): Char = i match {
    case n if n <= range => (n + asciiA).toChar
    case _ => throw new IllegalArgumentException
  }

}
