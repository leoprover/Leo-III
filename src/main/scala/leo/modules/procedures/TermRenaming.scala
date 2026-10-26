package leo.modules.procedures



sealed abstract class TermRenaming
object TermRenaming {
  final def apply(): TermRenaming = new TermRenamingImpl()

  private final class TermRenamingImpl extends TermRenaming
}
