import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import javax.swing.table.TableModel;

/**
 * @author M-Sh-97
 */
public class Application {
  private MainForm form;
  private String localUserNick,
		 remoteUserNick;
  private final ContactTableModel localContactModel,
				  serverContactModel;
  private Connection incomingConnection,
		     outcomingConnection;
  private Caller caller;
  private CallListener callListener;
  private CallListenerThread callListenerThread;
  private CommandListenerThread outcomingCommandListener,
				incomingCommandListener;
  private final ServerConnection contactDataServer;
  private final Observer outcomingConnectionObserver,
			 incomingConnectionObserver,
			 incomingCallObserver;
  private final HistoryModel messageContainer;
  private final SimpleDateFormat dateFormat;
  private final File contactFolder, historyFolder, fileFolder;

  public Application() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    outcomingConnectionObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	if (o instanceof CommandListenerThread) {
	  if (((Command) arg).getType() == Command.CommandType.NICK) {
	    caller.setRemoteUserNick(((NickCommand) arg).getNick());
	    form.showRemoteUserNick(caller.getRemoteUserNick());
	    form.showRemoteAddress(((InetSocketAddress) caller.getRemoteAddress()).getHostString());
	    form.blockRemoteUserInfo(true);
	    try {
	      if (((NickCommand) arg).getBusyStatus()) {
		closeConnection();
		caller.setCallStatus(Caller.CallStatus.BUSY);
		form.showBusyCalleeDialog();
	      } else
		outcomingConnection.sendNick(localUserNick, false);
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  } else
	    if (((Command) arg).getType() == Command.CommandType.ACCEPT) {
	      remoteUserNick = caller.getRemoteUserNick();
	      caller.setCallStatus(Caller.CallStatus.OK);
	      messageContainer.clear();
	      form.blockDialogComponents(false);
	    } else
	      if (((Command) arg).getType() == Command.CommandType.REJECT) {
		closeConnection();
		caller.setCallStatus(Caller.CallStatus.REJECTED);
		form.showRejectedCallDialog();
	      } else
		if (((Command) arg).getType() == Command.CommandType.MESSAGE) {
		  addMessage(remoteUserNick, ((MessageCommand) arg).getMessage());
		} else
		  if (((Command) arg).getType() == Command.CommandType.FILE) {
		    addFileMessage(remoteUserNick, ((FileCommand) arg).getTitle());
		    saveFile(((FileCommand) arg).getTitle(), ((FileCommand) arg).getRawData());
		  } else
		    if (((Command) arg).getType() == Command.CommandType.DISCONNECT) {
		      closeConnection();
		      form.showCallFinishDialog();
		    }
	}
      }
    };

    incomingConnectionObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	if (o instanceof CommandListenerThread) {
	  if (((Command) arg).getType() == Command.CommandType.NICK) {
	      callListener.setRemoteUserNick(((NickCommand) arg).getNick());
	      form.showIncomingCallDialog(callListener.getRemoteUserNick(), ((InetSocketAddress) callListener.getRemoteAddress()).getHostString());
	  } else
	    if (((Command) arg).getType() == Command.CommandType.MESSAGE) {
	      if ((outcomingConnection == null) || (outcomingConnection.isClosed()))
		addMessage(remoteUserNick, ((MessageCommand) arg).getMessage());
	    } else
	      if (((Command) arg).getType() == Command.CommandType.FILE) {
		if ((outcomingConnection == null) || (outcomingConnection.isClosed()))
		  addFileMessage(remoteUserNick, ((FileCommand) arg).getTitle());
		saveFile(((FileCommand) arg).getTitle(), ((FileCommand) arg).getRawData());
	      } else
		if (((Command) arg).getType() == Command.CommandType.DISCONNECT) {
		  closeConnection();
		  form.showCallFinishDialog();
		}
	}
      }
    };

    incomingCallObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	if (o instanceof CallListenerThread)
	  try {
	    if (isBusy() && ! ((InetSocketAddress) callListener.getRemoteAddress()).getAddress().getHostAddress().startsWith("127.0.0."))
	      ((Connection) arg).sendNick(localUserNick, true);
	    else {
	      incomingConnection = ((Connection) arg);
	      incomingCommandListener = new CommandListenerThread(incomingConnection);
	      incomingCommandListener.addObserver(incomingConnectionObserver);
	      try {
		incomingCommandListener.start();
		incomingConnection.sendNick(localUserNick, false);
	      } catch (IllegalThreadStateException ex) {
		closeConnection();
	      }
	    }
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
    
    localContactModel = new ContactTableModel(header, 0, permissions);
    serverContactModel = new ContactTableModel(header, 0, permissions);

    messageContainer = new HistoryModel();
    
    dateFormat = new SimpleDateFormat("d.MM.yyyy H:mm:ss");

    contactDataServer = new ServerConnection(Protocol.serverAddress);
    
    contactFolder = new File(".\\Контакты");
    if (! contactFolder.exists())
      contactFolder.mkdir();
    historyFolder = new File(".\\Переписки");
    if (! historyFolder.exists())
      historyFolder.mkdir();
    fileFolder = new File(".\\Файлы");
    if (! fileFolder.exists())
      fileFolder.mkdir();

    form = new MainForm(this);
  }
  
  public MainForm getForm() {
    return form;
  }
  
  public TableModel getLocalContactModel() {
    return localContactModel;
  }
  
  public TableModel getServerContactModel() {
    return serverContactModel;
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
    return dateFormat;
  }
  
  public boolean isBusy() {
    return ! (((outcomingConnection == null) || outcomingConnection.isClosed()) && ((incomingConnection == null) || incomingConnection.isClosed()));
  }
  
  
  public void logIn(String newNick) {
    if (newNick.isEmpty())
      localUserNick = Protocol.defaultLocalUserNick;
    else
      localUserNick = newNick;
    contactDataServer.setLocalNick(localUserNick);
    contactDataServer.connect();
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
      for (String nick: contactDataServer.getAllNicks()) {
	Vector<String> row = new Vector<>(2);
	row.add(nick);
	row.add(contactDataServer.getIpForNick(nick));
	serverContactModel.addRow(row);
      }
    }
  }
  
  public void loadContactsFromFile() {
    clearContacts();
    StringBuilder fn = new StringBuilder();
    fn.append(contactFolder.getPath());
    fn.append('\\');
    fn.append(localUserNick);
    fn.append(Protocol.userDataFileExtension);
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fn.toString()))) {
      while (bufferedReader.ready()) {
	Vector<String> tmp = new Vector<>();
	String nick = bufferedReader.readLine();
	String ip = bufferedReader.readLine();
	tmp.add(nick);
	tmp.add(ip);
	localContactModel.addRow(tmp);
      }
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
	e.printStackTrace();
    }
  }
  
  public void saveContactsToFile() {
    StringBuilder fn = new StringBuilder();
    fn.append(contactFolder.getPath());
    fn.append('\\');
    fn.append(localUserNick);
    fn.append(Protocol.userDataFileExtension);
    try (FileWriter fileWriter = new FileWriter(fn.toString())) {
      for (int i = 0; i < localContactModel.getRowCount(); i++) {
	fileWriter.write(localContactModel.getValueAt(i, 0) + Protocol.endOfLine);
	fileWriter.write(localContactModel.getValueAt(i, 1) + Protocol.endOfLine);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void addContact(String newNick, String newIP) {
    Vector<String> nc = new Vector<>(2);
    nc.add(newNick);
    nc.add(newIP);
    Vector<Vector<String>> fln = (Vector<Vector<String>>) localContactModel.getDataVector();
    int lni = 0;
    for (; lni < fln.size(); lni ++) {
      if (fln.get(lni).contains(newNick) && fln.get(lni).contains(newIP))
	break;
    }
    if (lni == fln.size())
      localContactModel.addRow(nc);
  }

  public void removeContact(int pos) {
    if (pos >= 0)
      localContactModel.removeRow(pos);
  }

  public void clearContacts() {
    localContactModel.clearDataVector();
    serverContactModel.clearDataVector();
  }
  
  public void makeOutcomingCall(String remoteIP) {
    caller = new Caller(localUserNick, remoteIP);
    try {
      if (! isBusy()) {
	outcomingConnection = caller.call();
	outcomingCommandListener = new CommandListenerThread(outcomingConnection);
	outcomingCommandListener.addObserver(outcomingConnectionObserver);
	outcomingCommandListener.start();
      }
    } catch (IOException e) {
      closeConnection();
      caller.setCallStatus(Caller.CallStatus.NOT_ACCESSIBLE);
      form.showConnectionFailDialog();
    }
  }

  public void acceptIncomingCall() {
    try {
      incomingConnection.accept();
      remoteUserNick = callListener.getRemoteUserNick();
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
    if ((outcomingCommandListener != null) && outcomingCommandListener.isListening()) {
      outcomingCommandListener.stop();
      outcomingCommandListener.deleteObservers();
    }
    if ((incomingCommandListener != null) && incomingCommandListener.isListening()) {
      incomingCommandListener.stop();
      incomingCommandListener.deleteObservers();
    }
  }

  public void sendMessage(String text) {
    if (! text.endsWith(Protocol.endOfLine))
      text = text + Protocol.endOfLine;
    try {
      if (! ((outcomingConnection == null) || outcomingConnection.isClosed()))
	outcomingConnection.sendMessage(text);
      else
	if (! ((incomingConnection == null) || incomingConnection.isClosed()))
	  incomingConnection.sendMessage(text);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void addMessage(String nick, String msgText) {
    if (msgText != null) {
      if (! msgText.endsWith(Protocol.endOfLine))
	msgText = msgText + Protocol.endOfLine;
      messageContainer.addMessage(nick, new Date(System.currentTimeMillis()), msgText);
    }
  }
  
  public void addFileMessage(String nick, String fileName) {
    StringBuilder fmt = new StringBuilder(Protocol.fileMessageLeadingLabel.length() + fileName.length() + 2);
    fmt.append(Protocol.fileMessageLeadingLabel);
    fmt.append(Protocol.space);
    fmt.append(fileName);
    fmt.append(Protocol.endOfLine);
    addMessage(nick, fmt.toString());
  }
  
  public void sendFile(File f) {
    try (BufferedInputStream bfi = new BufferedInputStream(new FileInputStream(f))) {
      Vector<Byte> pcrd = new Vector<>(1024);
      int tv = bfi.read();
      while (tv > -1) {
	pcrd.addElement((Byte) (byte) tv);
	tv = bfi.read();
      }
      tv = pcrd.size();
      byte[] crd = new byte[tv];
      for (int index = 0; index < tv; index ++)
	crd[index] = pcrd.get(index).byteValue();
      if (! ((outcomingConnection == null) || outcomingConnection.isClosed()))
	outcomingConnection.sendFile(f.getName(), crd);
      else
	if (! ((incomingConnection == null) || incomingConnection.isClosed()))
	  incomingConnection.sendFile(f.getName(), crd);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void saveMessageHistory(String messageAreaText) {
    if (messageContainer.getSize() > 0) {
      StringBuilder fn = new StringBuilder();
      fn.append(historyFolder.getPath());
      fn.append('\\');
      fn.append(localUserNick);
      fn.append(Protocol.space);
      fn.append('-');
      fn.append(Protocol.space);
      fn.append(remoteUserNick);
      fn.append(',');
      fn.append(Protocol.space);
      fn.append(dateFormat.format(messageContainer.getMessage(0).getDate()));
      fn.append(Protocol.space);
      fn.append('-');
      fn.append(Protocol.space);
      fn.append(dateFormat.format(messageContainer.getMessage(messageContainer.getSize() - 1).getDate()));
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
  
  public void saveFile(String name, byte[] rawData) {
    StringBuilder mn = new StringBuilder();
    mn.append(fileFolder.getPath());
    mn.append('\\');
    mn.append(name);
    File rf = new File(mn.toString());
    if (rf.exists()) {
      short febd = (short) mn.lastIndexOf(".");
      if ((febd >= 0) && (mn.indexOf(Protocol.space, febd + 1) < febd))
	febd = (short) (mn.length() - febd);
      else
	febd = 0;
      short obf = (short) (mn.length() - febd + 1);
      mn.insert(obf - 1, new char[] {' ', '(', '1', ')'});
      short an = 1;
      rf = new File(mn.toString());
      while (rf.exists()) {
	an ++;
	mn.replace(obf + 1, mn.length() - febd - 1, String.valueOf(an));
	rf = new File(mn.toString());
      }
    }
    try {
      rf.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try (BufferedOutputStream bfo = new BufferedOutputStream(new FileOutputStream(rf))) {
      bfo.write(rawData);
      bfo.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Application chatApp = new Application();
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
	MainForm mf = chatApp.getForm();
	mf.setLocationByPlatform(true);
	mf.setVisible(true);
      }
    });
  }
}
