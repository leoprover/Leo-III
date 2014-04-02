package leoshell.commands


  /**
   * Trait for any command that should be usable in the shell.
   * @author{Max Wisniewski}
   */
  trait Command {
    /**
     * Name of the command i.e. the name the function has
     */
    val name: String

    /**
     * Long description of the command
     */
    val infoText: String

    /**
     * Short description of the command
     */
    val helpText: String

    /**
     * Text for initializing it in the shell
     */
    val initText: String

    /**
     * Initializes the object for starting issues
     */
    def init()

  }
