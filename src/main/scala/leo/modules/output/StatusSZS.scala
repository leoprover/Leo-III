package leo.modules.output

import leo.datastructures.Pretty

/**
 * Created by lex on 07.11.14.
 */
sealed abstract class StatusSZS extends Output with Pretty
case object THEOREM extends StatusSZS {
  val output = ???
  val pretty = "SZS_THEOREM"
}
