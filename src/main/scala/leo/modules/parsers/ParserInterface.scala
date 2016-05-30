package leo.modules.parsers

import leo.modules.parsers.lexical.TPTPLexical


/**
  * Created by samuel on 20.03.16.
  */

trait ParserInterface[Output]
  //extends TPTPLexical
{
  val lexical: TPTPLexical

  type Token <: TPTPLexical#Token
  //type Token = lexical.Token
  //type Token = lexical.Token
  //type TokenPI = lexical.Token
  //Self: TPTPTokens =>
  //type TokenPI = Token

  type TokenStream[T] = Seq[T]

  def tokenize(input: String): TokenStream[Token]
  def parse(input: String): Either[String,(Output, TokenStream[Token])] =
    parse(
      tokenize(input)
    )
  def parse(input: TokenStream[Token]): Either[String,(Output, TokenStream[Token])]

  def tokenStreamFromSource(src: io.Source): TokenStream[Token]
  def parseSource(src: io.Source): Either[String,(Output, TokenStream[Token])] =
    parse(
      tokenStreamFromSource(src)
    )
}
