import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author M-Sh-97
 */
class CallListener {
  private String remoteUserNick, localUserNick;
  private ServerSocket ss;
  private boolean busy;
  private SocketAddress listenAddress, remoteAddress;

  public CallListener() throws IOException {
    this(Protocol.defaultLocalUserNick, Protocol.defaultLocalIPAddress);
  }

  public CallListener(String localNick) throws IOException {
    this(localNick, Protocol.defaultLocalIPAddress);
  }

  public CallListener(String localNick, String localIP) throws IOException {
    ss = new ServerSocket();
    ss.bind(new InetSocketAddress(localIP, Protocol.port));
    this.localUserNick = localNick;
    this.listenAddress = ss.getLocalSocketAddress();
  }

  public Connection getConnection() throws IOException {
    Socket socket = ss.accept();
    remoteAddress = socket.getRemoteSocketAddress();
    return new Connection(socket);
  }

  public String getLocalUserNick() {
    return localUserNick;
  }

  public boolean isBusy() {
    return busy;
  }

  public SocketAddress getListenAddress() {
    return listenAddress;
  }

  public String getRemoteUserNick() {
    return remoteUserNick;
  }

  public SocketAddress getRemoteAddress() {
    return remoteAddress;
  }

  public void setLocalUserNick(String localUserNick) {
    this.localUserNick = localUserNick;
  }

  public void setBusy(boolean busy) {
    this.busy = busy;
  }

  public void setListenAddress(SocketAddress listenAddress) {
    this.listenAddress = listenAddress;
  }

  public void setRemoteUserNick(String remoteUserNick) {
    this.remoteUserNick = remoteUserNick;
  }
  
  public void close() throws IOException {
    if (! ss.isClosed())
      ss.close();
  }
}
