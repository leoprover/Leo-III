package leo.modules.procedures



sealed abstract class TermRenaming
object TermRenaming {
  final def apply(): TermRenaming = new TermRenamingImpl()

  private[this] final class TermRenamingImpl extends TermRenaming
}
