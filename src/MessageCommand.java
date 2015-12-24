/**
 * @author M-Sh-97
 */
class MessageCommand extends Command {
  private final String text;

  protected MessageCommand(String messageText) {
    super(Command.CommandType.MESSAGE);
    text = messageText;
  }

  public String getMessage() {
    return text;
  }
}
