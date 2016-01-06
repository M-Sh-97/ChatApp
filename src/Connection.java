import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
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
    if (input.ready()) {
      StringBuilder iit = new StringBuilder();
      String it;
      do {
	it = input.readLine();
	if (! (it == null) && ! it.isEmpty()) {
	  iit.append(it);
	  iit.append(Protocol.endOfLine);
	}
      } while (input.ready());
      return Command.getCommand(iit.toString());
    }
    else
      return null;
  }

  public void accept() throws IOException {
    output.write(Protocol.acceptionCommandPhrase + Protocol.endOfLine);
    output.flush();
  }

  public void reject() throws IOException {
    output.write(Protocol.rejectionCommandPhrase + Protocol.endOfLine);
    output.flush();
  }

  public void sendNickHello(String nickName) throws IOException {
    output.write(Protocol.programName + " " + Protocol.version + " " + Protocol.nickCommandPhrase + " " + nickName + Protocol.endOfLine);
    output.flush();
  }

  public void sendNickBusy(String nickName) throws IOException {
    output.write(Protocol.programName + " " + Protocol.version + " " + Protocol.nickCommandPhrase + " " + nickName + " busy" + Protocol.endOfLine);
    output.flush();
  }

  public void disconnect() throws IOException {
    output.write(Protocol.disconnectionCommandPhrase + Protocol.endOfLine);
    output.flush();
  }

  public void close() throws IOException {
    if (! connectionSocket.isClosed())
      connectionSocket.close();
  }

  public void sendMessage(String message) throws IOException {
    output.write(Protocol.messageCommandPhrase + Protocol.endOfLine);
    output.write(message);
    if (! message.endsWith(Protocol.endOfLine))
      output.write(Protocol.endOfLine);
    output.flush();
  }
}
