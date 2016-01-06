/**
 * @author M-Sh-97
 */
class NickCommand extends Command {
  private final String nick;
  private final boolean busy;

  protected NickCommand(String nickInfo) {
    super(Command.CommandType.NICK);
    busy = nickInfo.endsWith(" " + Protocol.busyStatusPhrase);
    if (busy) {
      nick = nickInfo.substring(0, nickInfo.lastIndexOf(' '));
    } else {
      nick = nickInfo.substring(0);
    }
  }

  public String getNick() {
    return nick;
  }

  public boolean getBusyStatus() {
    return busy;
  }
}
