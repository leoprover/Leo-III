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
  def tokenize(input: String): Seq[Token]
  def parse(input: String): Either[String,(Output, Seq[Token])]
  def parse(input: Seq[Token]): Either[String,(Output, Seq[Token])]

}
