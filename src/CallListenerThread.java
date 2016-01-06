import java.io.IOException;
import java.util.Observable;

/**
 * @author M-Sh-97
 */
class CallListenerThread extends Observable implements Runnable {
  private Connection lastConnection;
  private final CallListener cl;
  private boolean stopped;
  private final Thread thisThread;

  public CallListenerThread(CallListener listener) throws IOException {
    cl = listener;
    thisThread = new Thread(this);
  }

  public void start() {
    thisThread.start();
  }

  public void stop() {
    stopped = true;
    try {
      cl.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    do {
      try {
	Connection checked = cl.getConnection();
	if (checked != null) {
	  lastConnection = checked;
	  setChanged();
	  notifyObservers(lastConnection);
	}
      } catch (IOException ex) {
	stop();
      }
    } while (! stopped);
  }

  public Connection getLastConnection() {
    return lastConnection;
  }

  public CallListener getCallListener() {
    return cl;
  }
}
