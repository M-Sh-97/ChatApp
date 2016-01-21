/**
 * @author M-Sh-97
 */
class Command {
  private final Command.CommandType type;

  static enum CommandType {NICK, DISCONNECT, ACCEPT, REJECT, MESSAGE, FILE};

  protected Command(CommandType t) {
    type = t;
  }

  public static Command getCommand(String text) {
    String capital_text = text.toUpperCase();
    int user_index = capital_text.indexOf(Protocol.space + Protocol.nickCommandPhrase + Protocol.space, Protocol.programName.length());
    if ((capital_text.indexOf(Protocol.programName.toUpperCase() + Protocol.space, 0) == 0) && (user_index > Protocol.programName.length() + 1)) {
      return new NickCommand(text.substring(user_index + Protocol.nickCommandPhrase.length() + 2, text.length() - 1));
    } else
      if (capital_text.indexOf(Protocol.disconnectionCommandPhrase, 0) == 0) {
	return new Command(CommandType.DISCONNECT);
      } else
	if (capital_text.indexOf(Protocol.acceptionCommandPhrase, 0) == 0) {
	  return new Command(CommandType.ACCEPT);
	} else
	  if (capital_text.indexOf(Protocol.rejectionCommandPhrase, 0) == 0) {
	    return new Command(CommandType.REJECT);
	  } else
	    if (capital_text.indexOf(Protocol.messageCommandPhrase, 0) == 0) {
	      return new MessageCommand(text.substring(Protocol.messageCommandPhrase.length() + Protocol.endOfLine.length()));
	    } else
	      if (capital_text.indexOf(Protocol.fileCommandPhrase, 0) == 0) {
		return new FileCommand(text.substring(Protocol.fileCommandPhrase.length() + Protocol.endOfLine.length()));
	      } else
		return null;
  }

  public CommandType getType() {
    return type;
  }
}
