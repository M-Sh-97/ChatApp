import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author M-Sh-97
 */
class Caller {
  private String localNick, remoteNick;
  private SocketAddress remoteAddress;
  private Socket s;
  private CallStatus status;
  static enum CallStatus {OK, NOT_ACCESSIBLE, BUSY, REJECTED, NO_SERVICE};

  public Caller() {
    this(Protocol.defaultLocalNick, new InetSocketAddress(Protocol.defaultLocalIPAddress, Protocol.port));
  }

  public Caller(String localNick) {
    this(localNick, new InetSocketAddress(Protocol.defaultLocalIPAddress, Protocol.port));
  }

  public Caller(String localNick, SocketAddress remoteAddress) {
    this.localNick = localNick;
    this.remoteAddress = remoteAddress;
  }

  public Caller(String localNick, String IP) {
    this(localNick, new InetSocketAddress(IP, Protocol.port));
  }

  public Connection call() throws IOException {
    s = new Socket();
    s.connect(remoteAddress);
    return new Connection(s);
  }

  public String getLocalNick() {
    return localNick;
  }

  public SocketAddress getRemoteAddress() {
    return remoteAddress;
  }

  public String getRemoteNick() {
    return remoteNick;
  }

  public CallStatus getStatus() {
    return status;
  }

  public void setLocalNick(String localNick) {
    this.localNick = localNick;
  }

  public void setRemoteAddress(SocketAddress remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  public void setRemoteNick(String remoteNick) {
    this.remoteNick = remoteNick;
  }
  
  public void close() throws IOException {
    if (! s.isClosed())
      s.close();
  }
}
