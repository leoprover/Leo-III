package leo.modules.output

/**
  * Trait for wrapping strings. Might be handy if the computation of the strings
  * are somewhat involved/complex and you just want to compute them if they are
  * indeed needed.
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  * @since 07.11.14
  */
trait Output extends Function0[String]{}


