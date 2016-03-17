package leo.modules.parsers.syntactical_new.combinators


trait Combinators {

  type ParserError = String
  type TokenStream[Token] = Seq[Token]
  type ParserRet[I,A] = Either[ParserError, (A,TokenStream[I])]

  def withSideEffect[Token,A](action: => Unit)(p: Parser[Token,A]) = new Parser[Token,A] {
    override def apply(stream: TokenStream[Token]) = {
      action
      p.apply(stream)
    }
  }

  abstract class Parser[Token,+A]
    extends ((TokenStream[Token]) => ParserRet[Token,A]) {

    def map[B](f: (A) => B) = {
      val this_ = this
      new Parser[Token,B] {
        override def apply(stream: TokenStream[Token]) =
          this_.apply(stream).right.map { case (x,s) => (f(x), s) }
      }
    }

    def flatMap[B](f: (A) => Parser[Token,B]): Parser[Token,B] = {
      val this_ = this
      new Parser[Token,B] {
        override def apply(stream: TokenStream[Token]) = {
          this_.apply(stream).right.flatMap {
            case (x, newStream) =>
              f(x).apply(newStream)
          }
        }
      }
    }

    def ~[B](other: Parser[Token,B]): Parser[Token,(A,B)] =
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
  }

  def ret[Token,A](x: A) = new Parser[Token,A] {
    def apply(stream: TokenStream[Token]) =
      Right(x, stream)
  }

  def parseIf[Token](errMsg: Token => String)(cond: Token => Boolean) = new Parser[Token,Token] {
    override def apply(stream: TokenStream[Token]) =
      stream match {
        case x +: rest =>
          if( cond(x) ) Right((x,rest)) else Left(errMsg(x))
        case _ => Left("error: eof")
      }
  }

  def tok[Token](t: Token) = parseIf((x: Token) => errMsg(t)(x))(_ == t)

  def errMsg[Token](expected: Token)(found: Any) = s"expected ${expected.toString} but ${found.toString} found"

}
