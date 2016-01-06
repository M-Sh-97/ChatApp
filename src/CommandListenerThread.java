import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Observable;

/**
 * @author M-Sh-97
 */
class CommandListenerThread extends Observable implements Runnable {
  private Command lastCommand;
  private final Connection connection;
  private boolean stopped;
  private final Thread thisThread;

  public CommandListenerThread(Connection con) {
    connection = con;
    thisThread = new Thread(this);
  }

  public Command getLastCommand() {
    return lastCommand;
  }

  public boolean isListening() {
    return ! stopped;
  }

  public void start() {
    thisThread.start();
  }

  public void stop() {
    stopped = true;
    try {
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    do {
      try {
	Command checked = connection.receive();
	if (checked != null) {
	  lastCommand = checked;
	  setChanged();
	  notifyObservers(checked);
	}
      } catch (IOException | NoSuchElementException ex) {
	stop();
      }
    } while (! stopped);
  }
}
