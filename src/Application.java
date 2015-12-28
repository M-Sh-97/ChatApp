import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * @author M-Sh-97
 */
public class Application {
  private MainForm form;
  private String localNick;
  private DefaultTableModel contactModel;
  private Connection incomingConnection,
		     outcomingConnection;
  private Caller caller;
  private CallListener callListener;
  private CallListenerThread callListenerThread;
  private CommandListenerThread commandListener;
  private final ServerConnection contactDataServer;
  private static enum Status {BUSY, SERVER_NOT_STARTED, OK, CLIENT_CONNECTED, REQUEST_FOR_CONNECT};
  private static enum ConnectionStatus {AS_SERVER, AS_CLIENT, AS_NULL};
  private final Observer outcomingConnectionObserver,
			 incomingConnectionObserver,
			 incomingCallObserver;
  private final HistoryModel messageContainer;
  private ConnectionStatus currentSuccessConnection;
  private Status status;

  public Application() {
    currentSuccessConnection = ConnectionStatus.AS_NULL;
    status = Status.OK;
    
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    }
    
    outcomingConnectionObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	SwingUtilities.invokeLater(new Runnable() {
	  @Override
	  public void run() {
	    if (((Command) arg).getType() == Command.CommandType.NICK) {
		if (o instanceof CommandListenerThread) {
		  caller.setRemoteNick(((NickCommand) arg).getNick());
		  form.showRemoteNick(caller.getRemoteNick());
		  form.blockRemoteUserInfo(true);
		  try {
		    if (((NickCommand) arg).getBusyStatus()) {
		      status = Status.OK;
		      form.showCallRetryDialog();
		    } else
		      outcomingConnection.sendNickHello(localNick);
		  } catch (IOException e1) {
		    e1.printStackTrace();
		  }
		}
	    } else if (((Command) arg).getType() == Command.CommandType.ACCEPT) {
		if (o instanceof CommandListenerThread) {
		  form.blockDialogComponents(false);
		}
	    } else if (((Command) arg).getType() == Command.CommandType.REJECT) {
		if (o instanceof CommandListenerThread) {
		  currentSuccessConnection = ConnectionStatus.AS_NULL;
		  form.showRecallDialog();
		}
	    } else if (((Command) arg).getType() == Command.CommandType.MESSAGE) {
		if (o instanceof CommandListenerThread) {
		  addMessage(caller.getRemoteNick(), ((MessageCommand) arg).getMessage());
		}
	    } else if (((Command) arg).getType() == Command.CommandType.DISCONNECT) {
		if (o instanceof CommandListenerThread) {
		  finishCall();
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
	SwingUtilities.invokeLater(new Runnable() {
	  @Override
	  public void run() {
	    if (((Command) arg).getType() == Command.CommandType.NICK) {
	      	callListener.setRemoteNick(((NickCommand) arg).getNick());
		form.showIncomingCallDialog(callListener.getRemoteNick(), ((InetSocketAddress) callListener.getRemoteAddress()).getHostString());
//	    } else if (((Command) arg).getType() == Command.CommandType.ACCEPT) {
//		if (o instanceof CommandListenerThread) {}
//	    } else if (((Command) arg).getType() == Command.CommandType.REJECT) {
//		if (o instanceof CommandListenerThread) {}
	    } else if (((Command) arg).getType() == Command.CommandType.MESSAGE) {
		if (o instanceof CommandListenerThread) {
		  addMessage(callListener.getRemoteNick(), ((MessageCommand) arg).getMessage());
		}
	    } else if (((Command) arg).getType() == Command.CommandType.DISCONNECT) {
		if (o instanceof CommandListenerThread) {
		  finishCall();
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
	    if (status == Status.OK || ((status == Status.REQUEST_FOR_CONNECT) && (((InetSocketAddress) callListener.getRemoteAddress()).getAddress().equals(((InetSocketAddress) caller.getRemoteAddress()).getAddress())))) {
	      incomingConnection = ((Connection) arg);
	      currentSuccessConnection = ConnectionStatus.AS_SERVER;
	      status = Status.CLIENT_CONNECTED;
	      commandListener = new CommandListenerThread(incomingConnection);
	      commandListener.addObserver(incomingConnectionObserver);
	      commandListener.start();
	      incomingConnection.sendNickHello(localNick);
	    } else
	      ((Connection) arg).sendNickBusy(localNick);
	  } catch (IOException e1) {
	    e1.printStackTrace();
	  }
      }
    };
    
    Vector<String> header = new Vector<String>(2);
    header.add("Пользователь");
    header.add("IP-адрес");
    contactModel = new DefaultTableModel(header, 0);
    
    messageContainer = new HistoryModel();
    
    contactDataServer = new ServerConnection(Protocol.serverAddress);
    
    form = new MainForm(this);
  }
  
  public String getLocalNick() {
    return localNick;
  }
  
  public void acceptIncomingCall() {
    try {
      incomingConnection.accept();
      status = Status.BUSY;
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  
  public void rejectIncomingCall() {
    try {
      incomingConnection.reject();
      currentSuccessConnection = ConnectionStatus.AS_NULL;
      status = Status.OK;
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  
  public void logIn(String newNick) {
    if (newNick.isEmpty())
      localNick = Protocol.defaultLocalNick;
    else
      localNick = newNick;
    contactDataServer.setLocalNick(localNick);
    contactDataServer.connect();
    status = Status.SERVER_NOT_STARTED;
  }

  public void logOut() {
    if (contactDataServer.isConnected())
      contactDataServer.disconnect();
    localNick = null;
  }
  
  public void startListeningForCalls() {
    if (contactDataServer.isConnected())
      if (! contactDataServer.isNickOnline(localNick))
	contactDataServer.goOnline(Protocol.port);
    try {
      callListener = new CallListener(localNick);
      callListenerThread = new CallListenerThread(callListener);
      callListenerThread.addObserver(incomingCallObserver);
      status = Status.OK;
      callListenerThread.start();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  
  public void finishListeningForCalls() {
    if (contactDataServer.isConnected())
      if (contactDataServer.isNickOnline(localNick))
	contactDataServer.goOffline();
    callListenerThread.stop();
  }
  
  public void loadContactsFromServer() {
    if (contactDataServer.isConnected()) {
      String[] nicknames = contactDataServer.getAllNicks();
      Vector<Vector<String>> fln = (Vector<Vector<String>>) contactModel.getDataVector();
      for (String nick: nicknames) {
	Vector<String> row = new Vector<String>(2);
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
//    else throw new SQLException();
  }
  
  public void finishCall() {
    try {
      //System.out.println(currentSuccessConnection);
      if (currentSuccessConnection == ConnectionStatus.AS_SERVER) {
	incomingConnection.disconnect();
	incomingConnection.close();
      } else
	if (currentSuccessConnection == ConnectionStatus.AS_CLIENT) {
	  outcomingConnection.disconnect();
	  outcomingConnection.close();
	}
      currentSuccessConnection = ConnectionStatus.AS_NULL;
      commandListener.stop();
      commandListener.deleteObservers();
      status = Status.OK;
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  
  public void sendMessage(String text) {
    try {
      if (currentSuccessConnection == ConnectionStatus.AS_SERVER) {
	incomingConnection.sendMessage(text);
      } else 
	if (currentSuccessConnection == ConnectionStatus.AS_CLIENT) {
	  outcomingConnection.sendMessage(text);
      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  
  public void addContact(String newNick, String newIP) {
    Vector<String> nc = new Vector<String>(2);
    nc.add(newNick);
    nc.add(newIP);
    Vector<Vector<String>> fln = (Vector<Vector<String>>) contactModel.getDataVector();
    int lni = 0;
    for (; lni < fln.size(); lni ++) {
      if (fln.get(lni).contains(newNick) && fln.get(lni).contains(newIP))
	break;
    }
    if (lni < fln.size())
      contactModel.addRow(nc);
  }
  
  public void removeContact(int pos) {
    if (pos >= 0)
      contactModel.removeRow(pos);
  }
  
  public void loadContactsFromFile() {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(localNick + Protocol.contactFileName))) {
      while (bufferedReader.ready()) {
	Vector<String> tmp = new Vector<String>();
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
    try (FileWriter fileWriter = new FileWriter(localNick + Protocol.contactFileName)) {
      for (int i = 0; i < contactModel.getRowCount(); i++) {
	fileWriter.write(contactModel.getValueAt(i, 0).toString() + Protocol.endOfLine);
	fileWriter.write(contactModel.getValueAt(i, 1).toString() + Protocol.endOfLine);
      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  
  public void makeOutcomingCall(String remoteIP) {
    caller = new Caller(localNick, remoteIP);
    try {
      if (status == Status.OK) {
	outcomingConnection = caller.call();
	currentSuccessConnection = ConnectionStatus.AS_CLIENT;
	status = Status.REQUEST_FOR_CONNECT;
	commandListener = new CommandListenerThread(outcomingConnection);
	commandListener.addObserver(outcomingConnectionObserver);
	commandListener.start();
      }
    } catch (IOException e1) {
//      e1.printStackTrace();
      form.showNoConnectionDialog();
    }
  }
  
  public void addMessage(String nick, String msgText) {
    if (msgText != null) {
      messageContainer.addMessage(nick, new Date(System.currentTimeMillis()), msgText);
    }
  }
  
  public TableModel getContactModel() {
    return contactModel;
  }
  
  public HistoryModel getMessageHistoryModel() {
    return messageContainer;
  }
  
  public MainForm getForm() {
    return form;
  }
  
  public boolean isBusy() {
    return status != Status.OK;
  }

  public static void main(String[] args) {
    Application chatApp = new Application();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
	chatApp.getForm().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	chatApp.getForm().setLocationByPlatform(true);
	chatApp.getForm().setVisible(true);
      }
    });
  }
}
