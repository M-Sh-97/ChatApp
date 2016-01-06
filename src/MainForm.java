import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author M-Sh-97
 */
class MainForm extends JFrame {
  private JPanel rootPanel;
  private JButton connect,
		  disconnect,
		  buttonAddFriends,
		  buttonRemoveFriends,
		  logInButton,
		  sendButton,
		  logOutButton;
  private JTextField textFieldIp,
		     textFieldRemoteNick,
		     textFieldLocalNick;
  private JTextArea messageTypingSpace,
		    messageHistory;
  private JTable tableFriends;
  private HistoryModel messageContainer;
  private final Observer historyViewObserver;
  private Application logicModel;
  
  public MainForm(Application logic) {
    super();
    logicModel = logic;
    setContentPane(rootPanel);
    setSize(750, 500);
    setTitle(Protocol.programName + " " + Protocol.version);

    tableFriends.setModel(logicModel.getContactModel());

    messageContainer = logicModel.getMessageHistoryModel();
    
    logInButton.setMnemonic(KeyEvent.VK_J);
    logInButton.setDisplayedMnemonicIndex(1);
    logOutButton.setMnemonic(KeyEvent.VK_S);
    logOutButton.setDisplayedMnemonicIndex(1);
    connect.setMnemonic(KeyEvent.VK_L);
    connect.setDisplayedMnemonicIndex(2);
    disconnect.setMnemonic(KeyEvent.VK_N);
    disconnect.setDisplayedMnemonicIndex(1);
    sendButton.setMnemonic(KeyEvent.VK_G);
    sendButton.setDisplayedMnemonicIndex(2);
    buttonAddFriends.setMnemonic(KeyEvent.VK_COMMA);
    buttonAddFriends.setDisplayedMnemonicIndex(2);
    buttonRemoveFriends.setMnemonic(KeyEvent.VK_H);
    buttonRemoveFriends.setDisplayedMnemonicIndex(2);

    historyViewObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	if (((Vector<String>) arg).isEmpty()) {
	  messageHistory.setText(null);
	} else {
	  if (! messageHistory.getText().isEmpty()) {
	    messageHistory.append(Protocol.endOfLine);
	  }
	  HistoryModel.Message msgData = messageContainer.getMessage(messageContainer.getSize() - 1);
	  messageHistory.append(msgData.getNick() + ". " + logicModel.getDateFormat().format(msgData.getDate()) + "." + Protocol.endOfLine + msgData.getText());
	}
      }
    };
    messageContainer.addObserver(historyViewObserver);

    connect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String fIP = textFieldIp.getText();
	logicModel.makeOutcomingCall(fIP);
      }
    });

    disconnect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	blockDialogComponents(true);
	logicModel.finishCall();
	logicModel.closeConnection();
	blockRemoteUserInfo(false);
      }
    });

    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String text = messageTypingSpace.getText();
	if (! text.isEmpty()) {
	  logicModel.sendMessage(text);
	  logicModel.addMessage(logicModel.getLocalNick(), text);
	  messageTypingSpace.setText(null);
	}
      }
    });

    logInButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String fln = textFieldLocalNick.getText();
	logicModel.logIn(fln);
	if (fln.isEmpty()) {
	  textFieldLocalNick.setText(Protocol.defaultLocalNick);
	}
	logicModel.loadContactsFromFile();
	logicModel.loadContactsFromServer();
	logicModel.startListeningForCalls();
	blockLocalUserInfo(true);
      }
    });

    logOutButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	logicModel.finishListeningForCalls();
	logicModel.saveContactsToFile();
	logicModel.logOut();
	messageHistory.setText(null);
	blockLocalUserInfo(false);
      }
    });

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
	if (! logicModel.isBusy()) {
	  Object[] option = {"Да", "Нет"};
	  int n = JOptionPane.showOptionDialog(e.getComponent(), "Вы действительно хотите выйти?", "Выход из программы", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
	  if (n == 0) {
	    if (logicModel.getLocalNick() != null)
	      logOutButton.doClick();
	    MainForm.this.hide();
	    MainForm.this.dispose();
	    System.exit(0);
	  }
	}
      }
    });

    buttonAddFriends.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (!((textFieldIp.getText().isEmpty()) || (textFieldRemoteNick.getText().isEmpty()))) {
	  logicModel.addContact(textFieldRemoteNick.getText(), textFieldIp.getText());
	}
      }
    });

    buttonRemoveFriends.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int sr = tableFriends.getSelectedRow();
	if (sr >= 0) {
	  logicModel.removeContact(sr);
	  tableFriends.clearSelection();
	}
      }
    });

    tableFriends.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	if (tableFriends.getSelectedRow() > 0) {
	  String nick = tableFriends.getModel().getValueAt(tableFriends.getSelectedRow(), 0).toString();
	  String ip = tableFriends.getModel().getValueAt(tableFriends.getSelectedRow(), 1).toString();
	  textFieldRemoteNick.setText(nick);
	  textFieldIp.setText(ip);
	}
      }
    });

    tableFriends.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 127) // "Delete"
	  buttonRemoveFriends.doClick();
      }
    });

    textFieldLocalNick.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 10) // "Enter"
	  logInButton.doClick();
      }
    });

    textFieldIp.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 10) // "Enter"
	  connect.doClick();
      }
      
      @Override
      public void keyTyped(KeyEvent e) {
	if (! textFieldRemoteNick.getText().isEmpty())
	  if (e.getKeyChar() != 10) // "Enter"
	    textFieldRemoteNick.setText(null);
      }
    });
  }

  public void showIncomingCallDialog(String nick, String IP) {
    Object[] option = {"Принять", "Отклонить"};
    if (JOptionPane.showOptionDialog(this, "Пользователь " + nick + " с адреса " + IP + " желает переписываться с Вами.", "Входящий запрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]) == 0) {
      logicModel.acceptIncomingCall();
      textFieldRemoteNick.setText(nick);
      textFieldIp.setText(IP);
      blockRemoteUserInfo(true);
      blockDialogComponents(false);
    } else {
      logicModel.rejectIncomingCall();
    }
  }

  public void showCallFinishDialog() {
    blockDialogComponents(true);
    JOptionPane.showMessageDialog(this, "Удалённый пользователь отсоединился.", "Конец переписки", JOptionPane.INFORMATION_MESSAGE);
    blockRemoteUserInfo(false);
  }

  public void showBusyCalleeDialog() {
    JOptionPane.showMessageDialog(this, "Удалённый пользователь занят.", "Занято", JOptionPane.INFORMATION_MESSAGE);
    blockRemoteUserInfo(false);
  }

  public void showRejectedCallDialog() {
    JOptionPane.showMessageDialog(this, "Удалённый пользователь отклонил Ваш запрос на соединение.", "Отклонённый запрос", JOptionPane.INFORMATION_MESSAGE);
    blockRemoteUserInfo(false);
  }

  public void showConnectionFailDialog() {
    JOptionPane.showMessageDialog(this, "Невозможно подсоединиться.", "Неуспешное соединение", JOptionPane.INFORMATION_MESSAGE);
    blockRemoteUserInfo(false);
  }

  public void blockDialogComponents(boolean blockingFlag) {
    disconnect.setEnabled(! blockingFlag);
    messageTypingSpace.setEnabled(! blockingFlag);
    sendButton.setEnabled(! blockingFlag);
    messageHistory.setEnabled(! blockingFlag);
    logOutButton.setEnabled(blockingFlag);
    connect.setEnabled(blockingFlag);
  }

  public void blockLocalUserInfo(boolean blockingFlag) {
    connect.setEnabled(blockingFlag);
    buttonAddFriends.setEnabled(blockingFlag);
    buttonRemoveFriends.setEnabled(blockingFlag);
    textFieldIp.setEnabled(blockingFlag);
    tableFriends.setEnabled(blockingFlag);
    textFieldLocalNick.setEnabled(! blockingFlag);
    logInButton.setEnabled(! blockingFlag);
    logOutButton.setEnabled(blockingFlag);
  }

  public void blockRemoteUserInfo(boolean blockingFlag) {
    textFieldIp.setEnabled(! blockingFlag);
  }

  public void showRemoteNick(String nick) {
    textFieldRemoteNick.setText(nick);
  }
}
