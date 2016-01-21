import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.NoSuchElementException;

/**
 * @author M-Sh-97
 */
class Connection {

  private final Socket connectionSocket;
  private final BufferedReader input;
  private final BufferedWriter output;

  public Connection(Socket s) throws IOException {
    connectionSocket = s;
    input = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream(), Protocol.encoding));
    output = new BufferedWriter(new OutputStreamWriter(this.connectionSocket.getOutputStream(), Protocol.encoding));
  }

  public boolean isClosed() {
    return connectionSocket.isClosed();
  }

  public boolean isConnected() {
    return connectionSocket.isConnected();
  }

  public Command receive() throws IOException, NoSuchElementException {
    StringBuilder iit = new StringBuilder();
    String it;
    do {
      it = input.readLine();
      if (! ((it == null) || it.isEmpty())) {
	iit.append(it);
	iit.append(Protocol.endOfLine);
      }
    } while (input.ready());
    return Command.getCommand(iit.toString());
  }

  public void accept() throws IOException {
    output.write(Protocol.acceptionCommandPhrase);
    output.write(Protocol.endOfLine);
    output.flush();
  }

  public void reject() throws IOException {
    output.write(Protocol.rejectionCommandPhrase);
    output.write(Protocol.endOfLine);
    output.flush();
  }

  public void sendNick(String nickName, boolean busyStatus) throws IOException {
    output.write(Protocol.programName);
    output.write(Protocol.space);
    output.write(Protocol.version);
    output.write(Protocol.space);
    output.write(Protocol.nickCommandPhrase);
    output.write(Protocol.space);
    output.write(nickName);
    if (busyStatus) {
      output.write(Protocol.space);
      output.write(Protocol.busyStatusPhrase);
    }
    output.write(Protocol.endOfLine);
    output.flush();
  }

  public void disconnect() throws IOException {
    output.write(Protocol.disconnectionCommandPhrase);
    output.write(Protocol.endOfLine);
    output.flush();
  }

  public void close() throws IOException {
    if (! connectionSocket.isClosed())
      connectionSocket.close();
  }

  public void sendMessage(String message) throws IOException {
    output.write(Protocol.messageCommandPhrase);
    output.write(Protocol.endOfLine);
    output.write(message);
    if (! message.endsWith(Protocol.endOfLine))
      output.write(Protocol.endOfLine);
    output.flush();
  }
  
  public void sendFile(String title, byte[] rawData) throws IOException {
    output.write(Protocol.fileCommandPhrase);
    output.write(Protocol.endOfLine);
    output.write(title);
    output.write(Protocol.endOfLine);
    output.write(new String(Base64.getUrlEncoder().encode(rawData)));
    output.write(Protocol.endOfLine);
    output.flush();
  }
}
