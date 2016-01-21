import java.util.Base64;

/**
 *
 * @author M-Sh-97
 */
class FileCommand extends Command {
  private final String title,
		       base64Code;
  private final byte[] rawData;
  
  protected FileCommand(String fileInfo) {
    super(Command.CommandType.FILE);
    base64Code = fileInfo;
    int fnli = base64Code.indexOf(Protocol.endOfLine, 0);
    if (fnli > -1) {
      title = base64Code.substring(0, fnli);
      int snli = base64Code.length();
      if (base64Code.endsWith(Protocol.endOfLine))
	snli = snli - Protocol.endOfLine.length();
      rawData = Base64.getUrlDecoder().decode(base64Code.substring(fnli + 1, snli));
    } else {
      title = null;
      rawData = Base64.getUrlDecoder().decode(base64Code);
    }
  }
  
  public String getTitle() {
    return title;
  }
  
  public byte[] getRawData() {
    return rawData;
  }
  
  public String getBase64Code() {
    return base64Code;
  }
}
