import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.table.TableModel;

/**
 * @author M-Sh-97
 */
public class Application {
  private MainForm form;
  private String localUserNick,
		 remoteUserNick;
  private final ContactTableModel contactModel;
  private Connection incomingConnection,
		     outcomingConnection;
  private Caller caller;
  private CallListener callListener;
  private CallListenerThread callListenerThread;
  private CommandListenerThread outcomingCommandListener,
				incomingCommandListener;
  private final ServerConnection contactDataServer;
  private static enum Status {BUSY, SERVER_NOT_STARTED, OK, CLIENT_CONNECTED, REQUEST_FOR_CONNECT};
  private final Observer outcomingConnectionObserver,
			 incomingConnectionObserver,
			 incomingCallObserver;
  private final HistoryModel messageContainer;
  private Status status;
  private final SimpleDateFormat dateFormatter;

  public Application() {
    status = Status.OK;

    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    outcomingConnectionObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	EventQueue.invokeLater(new Runnable() {
	  @Override
	  public void run() {
	    if (o instanceof CommandListenerThread) {
	      if (((Command) arg).getType() == Command.CommandType.NICK) {
		caller.setRemoteUserNick(((NickCommand) arg).getNick());
		form.showRemoteUserNick(caller.getRemoteUserNick());
		form.blockRemoteUserInfo(true);
		try {
		  if (((NickCommand) arg).getBusyStatus()) {
		    closeConnection();
		    form.showBusyCalleeDialog();
		  } else
		    outcomingConnection.sendNickHello(localUserNick);
		} catch (IOException e) {
		  e.printStackTrace();
		}
	      } else if (((Command) arg).getType() == Command.CommandType.ACCEPT) {
		remoteUserNick = caller.getRemoteUserNick();
		messageContainer.clear();
		form.blockDialogComponents(false);
	      } else if (((Command) arg).getType() == Command.CommandType.REJECT) {
		closeConnection();
		form.showRejectedCallDialog();
	      } else if (((Command) arg).getType() == Command.CommandType.MESSAGE) {
		addMessage(caller.getRemoteUserNick(), ((MessageCommand) arg).getMessage());
	      } else if (((Command) arg).getType() == Command.CommandType.DISCONNECT) {
		closeConnection();
		form.showCallFinishDialog();
	      }
	    }
	  }
	});
      }
    };

    incomingConnectionObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	EventQueue.invokeLater(new Runnable() {
	  @Override
	  public void run() {
	    if (o instanceof CommandListenerThread) {
	      if (((Command) arg).getType() == Command.CommandType.NICK) {
		  callListener.setRemoteUserNick(((NickCommand) arg).getNick());
		  form.showIncomingCallDialog(callListener.getRemoteUserNick(), ((InetSocketAddress) callListener.getRemoteAddress()).getHostString());
	      } else if (((Command) arg).getType() == Command.CommandType.MESSAGE) {
		addMessage(callListener.getRemoteUserNick(), ((MessageCommand) arg).getMessage());
	      } else if (((Command) arg).getType() == Command.CommandType.DISCONNECT) {
		closeConnection();
		form.showCallFinishDialog();
	      }
	    }
	  }
	});
      }
    };

    incomingCallObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	if (o instanceof CallListenerThread)
	  try {
	    if (status == Status.OK || ((status == Status.REQUEST_FOR_CONNECT) && ((InetSocketAddress) callListener.getRemoteAddress()).getAddress().getHostAddress().startsWith("127.0.0."))) {
	      incomingConnection = ((Connection) arg);
	      status = Status.CLIENT_CONNECTED;
	      incomingCommandListener = new CommandListenerThread(incomingConnection);
	      incomingCommandListener.addObserver(incomingConnectionObserver);
	      try {
		incomingCommandListener.start();
		incomingConnection.sendNickHello(localUserNick);
	      } catch (IllegalThreadStateException ex) {
		closeConnection();
	      }
	    } else
	      ((Connection) arg).sendNickBusy(localUserNick);
	  } catch (IOException e) {
	    e.printStackTrace();
	  }
      }
    };

    Vector<String> header = new Vector<>(2);
    header.add("Пользователь");
    header.add("IP-адрес");
    Vector<Boolean> permissions = new Vector<>(2);
    permissions.add(Boolean.FALSE);
    permissions.add(Boolean.FALSE);
    contactModel = new ContactTableModel(header, 0, permissions);

    messageContainer = new HistoryModel();
    
    dateFormatter = new SimpleDateFormat("d.MM.yyyy H:mm:ss");

    contactDataServer = new ServerConnection(Protocol.serverAddress);

    form = new MainForm(this);
  }
  
  public MainForm getForm() {
    return form;
  }
  
  public TableModel getContactModel() {
    return contactModel;
  }
  
  
  public HistoryModel getMessageHistoryModel() {
    return messageContainer;
  }

  public String getLocalUserNick() {
    return localUserNick;
  }
  
  public String getRemoteUserNick() {
    return remoteUserNick;
  }
  
  public DateFormat getDateFormat() {
    return dateFormatter;
  }
  
  public boolean isBusy() {
    return status != Status.OK;
  }
  
  
  public void logIn(String newNick) {
    if (newNick.isEmpty())
      localUserNick = Protocol.defaultLocalUserNick;
    else
      localUserNick = newNick;
    contactDataServer.setLocalNick(localUserNick);
    contactDataServer.connect();
    status = Status.SERVER_NOT_STARTED;
  }

  public void logOut() {
    if (contactDataServer.isConnected())
      contactDataServer.disconnect();
    messageContainer.clear();
    clearContacts();
    localUserNick = null;
    remoteUserNick = null;
  }
  
  public void startListeningForCalls() {
    if (contactDataServer.isConnected())
      if (! contactDataServer.isNickOnline(localUserNick))
	contactDataServer.goOnline(Protocol.port);
    try {
      callListener = new CallListener(localUserNick);
      callListenerThread = new CallListenerThread(callListener);
      callListenerThread.addObserver(incomingCallObserver);
      status = Status.OK;
      callListenerThread.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void finishListeningForCalls() {
    if (contactDataServer.isConnected())
      if (contactDataServer.isNickOnline(localUserNick))
	contactDataServer.goOffline();
    callListenerThread.stop();
  }
  
  public void loadContactsFromServer() {
    if (contactDataServer.isConnected()) {
      String[] nicknames = contactDataServer.getAllNicks();
      Vector<Vector<String>> fln = (Vector<Vector<String>>) contactModel.getDataVector();
      for (String nick: nicknames) {
	Vector<String> row = new Vector<>(2);
	row.add(nick);
	String nickIP = contactDataServer.getIpForNick(nick);
	row.add(nickIP);
	int lni = 0;
	for (; lni < fln.size(); lni ++) {
	  if (fln.get(lni).contains(nick) && fln.get(lni).contains(nickIP))
	    break;
	}
	if (lni == fln.size())
	  contactModel.addRow(row);
      }
    }
  }
  
  public void loadContactsFromFile() {
    clearContacts();
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(localUserNick + Protocol.userDataFileExtension))) {
      while (bufferedReader.ready()) {
	Vector<String> tmp = new Vector<>();
	String nick = bufferedReader.readLine();
	String ip = bufferedReader.readLine();
	tmp.add(nick);
	tmp.add(ip);
	contactModel.addRow(tmp);
      }
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
	e.printStackTrace();
    }
  }
  
  public void saveContactsToFile() {
    try (FileWriter fileWriter = new FileWriter(localUserNick + Protocol.userDataFileExtension)) {
      for (int i = 0; i < contactModel.getRowCount(); i++) {
	fileWriter.write(contactModel.getValueAt(i, 0).toString() + Protocol.endOfLine);
	fileWriter.write(contactModel.getValueAt(i, 1).toString() + Protocol.endOfLine);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void addContact(String newNick, String newIP) {
    Vector<String> nc = new Vector<>(2);
    nc.add(newNick);
    nc.add(newIP);
    Vector<Vector<String>> fln = (Vector<Vector<String>>) contactModel.getDataVector();
    int lni = 0;
    for (; lni < fln.size(); lni ++) {
      if (fln.get(lni).contains(newNick) && fln.get(lni).contains(newIP))
	break;
    }
    if (lni == fln.size())
      contactModel.addRow(nc);
  }

  public void removeContact(int pos) {
    if (pos >= 0)
      contactModel.removeRow(pos);
  }

  public void clearContacts() {
    contactModel.clearDataVector();
  }
  
  public void makeOutcomingCall(String remoteIP) {
    caller = new Caller(localUserNick, remoteIP);
    try {
      if (status == Status.OK) {
	outcomingConnection = caller.call();
	status = Status.REQUEST_FOR_CONNECT;
	outcomingCommandListener = new CommandListenerThread(outcomingConnection);
	outcomingCommandListener.addObserver(outcomingConnectionObserver);
	outcomingCommandListener.start();
      }
    } catch (IOException e) {
      closeConnection();
      form.showConnectionFailDialog();
    }
  }

  public void acceptIncomingCall() {
    try {
      incomingConnection.accept();
      remoteUserNick = callListener.getRemoteUserNick();
      status = Status.BUSY;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void rejectIncomingCall() {
    try {
      incomingConnection.reject();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void finishCall() {
    if (outcomingConnection.isConnected())
      try {
	outcomingConnection.disconnect();
      } catch (IOException e) {
	e.printStackTrace();
      }
  }

  public void closeConnection() {
    if ((outcomingCommandListener != null) && (outcomingCommandListener.isListening())) {
      outcomingCommandListener.stop();
      outcomingCommandListener.deleteObservers();
    }
    if ((incomingCommandListener != null) && (incomingCommandListener.isListening())) {
      incomingCommandListener.stop();
      incomingCommandListener.deleteObservers();
    }
    status = Status.OK;
  }

  public void sendMessage(String text) {
    if (((outcomingConnection == null) ^ (incomingConnection == null)) || ((outcomingConnection.isClosed()) ^ (incomingConnection.isClosed())) || (((InetSocketAddress) caller.getRemoteAddress()).equals((InetSocketAddress) callListener.getListenAddress()))) {  
      if (! text.endsWith(Protocol.endOfLine))
	text = text + Protocol.endOfLine;
      try {
	if (! ((outcomingConnection == null) || (outcomingConnection.isClosed()))) {
	  outcomingConnection.sendMessage(text);
	}
	if (! ((incomingConnection == null) || (incomingConnection.isClosed()))) {
	  incomingConnection.sendMessage(text);
	}
      } catch (IOException e) {
	e.printStackTrace();
      }
    }
  }
  
  public void addMessage(String nick, String msgText) {
    if (msgText != null) {
      if (! msgText.endsWith(Protocol.endOfLine))
	msgText = msgText + Protocol.endOfLine;
      messageContainer.addMessage(nick, new Date(System.currentTimeMillis()), msgText);
    }
  }
  
  public void saveMessageHistory(String messageAreaText) {
    if (messageContainer.getSize() > 0) {
      StringBuilder fn = new StringBuilder();
      fn.append(localUserNick);
      fn.append(' ');
      fn.append('-');
      fn.append(' ');
      fn.append(remoteUserNick);
      fn.append(',');
      fn.append(' ');
      fn.append(dateFormatter.format(messageContainer.getMessage(0).getDate()));
      fn.append(' ');
      fn.append('-');
      fn.append(' ');
      fn.append(dateFormatter.format(messageContainer.getMessage(messageContainer.getSize() - 1).getDate()));
      fn.append(Protocol.userDataFileExtension);
      for (short index = 0; index < fn.length(); index ++) {
	if (fn.charAt(index) == ':')
	  fn.setCharAt(index, '-');
      }
      try (FileWriter mhw = new FileWriter(fn.toString())) {
	mhw.write(messageAreaText);
	mhw.flush();
      } catch (IOException e) {
	e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    Application chatApp = new Application();
    EventQueue.invokeLater(new Runnable() {
      public void run() {
	chatApp.getForm().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	chatApp.getForm().setLocationByPlatform(true);
	chatApp.getForm().show();
      }
    });
  }
}
