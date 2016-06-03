package leo.modules.parsers.utils

import scala.util.Random


object GenerateTerm {
  val maxAtomLength = 10
  val minSizeChildren = 4
  val numChildren = 3
  def apply(maxSize: Int): String = {
    def calcExpr(headSizeMin: Int, genHead: (Int) => String): String = {
      val headLength = headSizeMin +
        ((maxAtomLength-headSizeMin) min Rnd.nextInt{
          val temp = maxSize/3-headSizeMin+1
          if (temp > 0) temp else 1
        }
        )
      val restLength = maxSize - headLength
      val restLengthIfChildren = restLength - 2 - (numChildren-1)
      if( (restLengthIfChildren - numChildren*minSizeChildren) >= 0 ) { // (3 is an experimental value)
      //if( (restLength - 2 - (numChildren-1)) / numChildren > 3 ) { // (3 is an experimental value)
        val head = genHead(headLength)
        val div = divide(restLengthIfChildren - numChildren*minSizeChildren, numChildren)
        val divNew = div map (_ + minSizeChildren)
        val ret = head + "(" + (divNew map apply reduce(_ + "," + _)) + ")"
        /*
        println(
          s"""maxSize: ${maxSize},
            |headLength: ${headLength},
            |restLength: ${restLength},
            |restLengthIfChildren: ${restLengthIfChildren},
            |head: ${head},
            |div: ${div}
            |divNew: ${divNew}
            |ret: ${ret}
        """.stripMargin
        )
        assert( ret.length == maxSize)
        */
        ret
      }
      else {
        val head = genHead(maxSize)
        head
      }
    }
    val termType = Rnd.nextInt(6)
    val ret = termType match {
      // plain_term:
      case 0 => // lower_word [(arguments)]
        calcExpr(lowerWordMin,genLowerWord)
      case 1 => // single_quoted [(arguments)]
        calcExpr(singleQuotedMin,genSingleQuoted)
      // defined_atom:
      case 2 => // integer
        if (maxSize > 5)
          apply(maxSize)
        else
          genInteger(maxSize)
        /*
        val temp = genInteger(maxSize)
        try {
          temp.toInt
          temp
        }
        catch { case _ => apply(maxSize) }
        */
      case 3 => // rational
        if (maxSize > 5)
          apply(maxSize)
        else
          genRational(maxSize)
      /*
      case 4 => // real
        genReal(maxSize)
      */
      // defined_atomic_term
      case 4 => // dollar_word [(arguments)]
        calcExpr(dollarWordMin,genDollarWord)
      case 5 => // dollar_dollar_word [(arguments)]
        calcExpr(dollarDollarWordMin,genDollarDollarWord)
      // variable
      case 6 =>
        genUpperWord(maxSize)
    }
    //println( s"maxSize: ${maxSize}, termType: ${termType}, ret: ${ret}" )
    assert( ret.length == maxSize)
    ret
  }

  def divide(x: Int, div: Int): List[Int] = {
    if(div == 1)
      List(x)
    else if( x == 0)
      List.fill(div)(x)
    else {
      val thisBucket = Rnd.nextInt(x)
      thisBucket :: divide(x-thisBucket,div-1)
    }
  }

  def genAtomicWord(maxSize: Int) =
    if( Rnd.nextBoolean() ) genLowerWord(maxSize) else genSingleQuoted(maxSize)

  val lowerWordMin = 1
  def genLowerWord(maxSize: Int): String = {
    val head = randomChars(lower)(1)
    val tail = randomChars(alphanumeric)(maxSize-1)
    return head + tail
  }

  val singleQuotedMin = 3
  def genSingleQuoted(maxSize: Int) = "'" + randomChars(sq_char)(1 max (maxSize-2)) + "'"

  //def genNumber(maxSize: Int) = ???
  def genDistinctObject(maxSize: Int) = "\"" + randomChars(do_char)(maxSize-2) + "\""

  val integerMin = 2
  def genInteger(maxSize: Int) = {
    var ret = (if( Rnd.nextBoolean() ) (randomChars("+-")(1)) else "")
    if( maxSize - ret.length == 1 && Rnd.nextInt(100) < 5 ) {
        ret += "0"
    }
    else {
      ret += (
        randomChars(nonZeroNumeric)(1) +
        randomChars(numeric)(maxSize-ret.length-1)
      )
    }
    ret
  }
  val rationalMin = 4
  def genRational(maxSize: Int) = {
    var ret = genInteger(maxSize - 2)
    ret += ("/" +
      randomChars(nonZeroNumeric)(1) +
      randomChars(numeric)(maxSize-ret.length-1 -1)
      )
    ret
  }

  //def genReal(maxSize: Int) =

  val dollarWordMin = lowerWordMin + 1
  def genDollarWord(maxSize: Int) =
    "$" + genLowerWord(maxSize-1)

  val dollarDollarWordMin = dollarWordMin + 1
  def genDollarDollarWord(maxSize: Int) =
  "$$" + genLowerWord(maxSize-2)

  val upperWordMin = 1
  def genUpperWord(maxSize: Int) =
    randomChar(upper) + randomChars(alphanumeric)(maxSize-1)

  //<do_char>            ::: ([\40-\41\43-\133\135-\176]|[\\]["\\])
  val do_char = (('\40' to '\41') ++ ('\43' to '\133') ++ ('\135' to '\176')).mkString

  /*
  %---Space and visible characters upto ~, except ' and \
  <sq_char>            ::: ([\40-\46\50-\133\135-\176]|[\\]['\\])
  */
  val sq_char = (('\40' to '\46') ++ ('\50' to '\133') ++ ('\135' to '\176')).mkString

  val lower: String = ('a' to 'z').mkString // reduce ((l,r) => List(l).mkString + List(r))
  val upper: String = ('A' to 'Z').mkString
  val nonZeroNumeric: String = "123456789"
  val numeric: String = "0" + nonZeroNumeric
  val alphanumeric: String = lower + upper + numeric + "_"

  def randomChars(chars: String)(length: Int): String = {
    var ret = ""
    val count = 0 max length
    (0 until count) foreach { x =>
      //println(x)
      ret += randomChar(chars)
    }
    ret
  }
  def randomChar(chars: String): Char =
    chars.charAt(Rnd.nextInt(chars.length))

  val Rnd = new Random
}

/**
  * Created by samuel on 16.03.16.
  */
/*
trait Generators[A] {

  def const(x: A) = new Generator {
    def length = 1
    def apply() = List(List(x))
  }

  abstract class Generator
    extends Function0[Seq[Seq[A]]]
  {
    def length: Int

    def |(other: Generator): Generator = {
    }

    /*
    def ~(other: Generator): Generator =
      flatMap {
        (x) => other.map { (y) => (x,y) }
      }

    def ~>[B](other: Parser[Token,B]): Parser[Token,B] =
      for{
        _ <- this
        y <- other
      }
      yield y

    def |[B >: A](other: Parser[Token,B]): Parser[Token,B] =
    {
      val this_ = this
      new Parser[Token,B] {
        override def apply(stream: TokenStream[Token]) = {
          this_.apply(stream) match {
            case Left(_) =>
              other.apply(stream)
            case Right(x) => Right(x)
          }
        }
      }
    }
    */

    def map(f: (A) => A) = {
      val this_ = this
      new Generator{
        def length = this_.length
        def apply() =
          this_.apply()
      }
    }

    def flatMap(f: (A) => Generator): Generator = {
      val this_ = this
      new Generator {
        lazy val thisApplied = this_.apply
        override def length: Int =
        override def apply(): Seq[Seq[A]] =
          f(thisApplied)
      }
        /*
        override def apply(stream: TokenStream[Token]) = {
          this_.apply(stream).right.flatMap {
            case (x, newStream) =>
              f(x).apply(newStream)
          }
        }
      }
      */
    }

  }
}
*/
