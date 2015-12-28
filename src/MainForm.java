import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
		  buttonChangeLocalNick,
		  sendButton;
  private JTextField textFieldIp,
		     textFieldNick,
		     textFieldLocalNick,
		     myText;
  private JTable tableFriends;
  private JTextArea messageHistory;
  private JButton logOutButton;
  private HistoryModel messageContainer;
  private final Observer historyViewObserver;
  private Application logicModel;
  
  public MainForm(Application logic) {
    super();
    logicModel = logic;
    setContentPane(rootPanel);
    setSize(750, 500);
    setTitle(Protocol.programName + " " + Protocol.version);

    connect.setEnabled(false);
    disconnect.setEnabled(false);
    textFieldIp.setEnabled(false);
    textFieldNick.setEnabled(false);
    tableFriends.setEnabled(false);
    buttonAddFriends.setEnabled(false);
    buttonRemoveFriends.setEnabled(false);
    myText.setEnabled(false);
    sendButton.setEnabled(false);
    logOutButton.setEnabled(false);

    tableFriends.setModel(logicModel.getContactModel());
    tableFriends.setAutoscrolls(true);

    messageHistory.setAutoscrolls(true);
    messageContainer = logicModel.getMessageHistoryModel();

    historyViewObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	if (((Vector<String>) arg).isEmpty()) {
	  messageHistory.setText("");
	} else {
	  if (!messageHistory.getText().isEmpty()) {
	    messageHistory.append(Protocol.endOfLine);
	  }
	  HistoryModel.Message msgData = messageContainer.getMessage(messageContainer.getSize() - 1);
	  messageHistory.append(msgData.getNick() + ". " + msgData.getDate().toString() + "." + Protocol.endOfLine + msgData.getText() + Protocol.endOfLine);
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
	blockRemoteUserInfo(false);
      }
    });

    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String text = myText.getText();
	if (!text.isEmpty()) {
	  logicModel.sendMessage(text);
	  logicModel.addMessage(logicModel.getLocalNick(), text);
	  myText.setText("");
	}
      }
    });

    buttonChangeLocalNick.addActionListener(new ActionListener() {
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
	blockLocalUserInfo(false);
      }
    });

    this.addWindowListener(new WindowListener() {
      @Override
      public void windowOpened(WindowEvent e) {}

      @Override
      public void windowClosing(WindowEvent e) {
	if (! logicModel.isBusy()) {
	  Object[] option = {"Да", "Нет"};
	  int n = JOptionPane.showOptionDialog(e.getComponent(), "Вы действительно хотите выйти?", "Выход из программы", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
	  if (n == 0) {
	    if (logicModel.getLocalNick() != null)
	      logOutButton.doClick();
	    e.getWindow().setVisible(false);
	    System.exit(0);
	  }
	}
      }

      @Override
      public void windowClosed(WindowEvent e) {}

      @Override
      public void windowIconified(WindowEvent e) {}

      @Override
      public void windowDeiconified(WindowEvent e) {}

      @Override
      public void windowActivated(WindowEvent e) {}

      @Override
      public void windowDeactivated(WindowEvent e) {}
    });

    buttonAddFriends.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (!((textFieldIp.getText().isEmpty()) || (textFieldNick.getText().isEmpty()))) {
	  logicModel.addContact(textFieldNick.getText(), textFieldIp.getText());
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
	  textFieldNick.setText(nick);
	  textFieldIp.setText(ip);
	}
      }
    });

    tableFriends.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 127) { // "Delete"
	  buttonRemoveFriends.doClick();
	}
      }
    });

    textFieldLocalNick.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 10) { // "Enter"
	  buttonChangeLocalNick.doClick();
	}
      }
    });

    myText.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 10) { // "Enter"
	  sendButton.doClick();
	}
      }
    });

    textFieldIp.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 10) { // "Enter"
	  connect.doClick();
	}
      }
    });
  }

  public void showIncomingCallDialog(String nick, String IP) {
    Object[] option = {"Принять", "Отклонить"};
    int n = JOptionPane.showOptionDialog(this, "Пользователь " + nick + " с адреса " + IP + " желает переписываться с Вами.", "Входящий запрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
    if (n == 0) {
      logicModel.acceptIncomingCall();
      textFieldNick.setText(nick);
      textFieldIp.setText(IP);
      blockRemoteUserInfo(true);
      blockDialogComponents(false);
    } else {
      logicModel.rejectIncomingCall();
    }
  }

  public void showCallFinishDialog() {
    blockDialogComponents(true);
    JOptionPane.showMessageDialog(this, "Удалённый пользователь отсоединился", "Конец переписки", JOptionPane.INFORMATION_MESSAGE);
    blockRemoteUserInfo(false);
  }

  public void showCallRetryDialog() {
    Object[] option = {"Повторить звонок", "Вернуться"};
    int n = JOptionPane.showOptionDialog(this, "Удалённый пользователь занят. Попробовать ещё раз?", "Занято", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
    if (n == 0) {
      connect.doClick();
    } else {
      blockRemoteUserInfo(false);
    }
  }

  public void showRecallDialog() {
    Object[] option = {"Перезвонить", "Вернуться"};
    int n = JOptionPane.showOptionDialog(this, "Удалённый пользователь отклонил Ваш запрос", "Повтор исходящего запроса", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
    if (n == 0) {
      connect.doClick();
    } else {
      blockRemoteUserInfo(false);
    }
  }

  public void showNoConnectionDialog() {
    JOptionPane.showMessageDialog(this, "Невозможно подсоединиться", "Неуспешное соединение", JOptionPane.INFORMATION_MESSAGE);
    blockRemoteUserInfo(false);
  }

  public void blockDialogComponents(boolean blockingFlag) {
    disconnect.setEnabled(! blockingFlag);
    connect.setEnabled(blockingFlag);
    myText.setEnabled(! blockingFlag);
    sendButton.setEnabled(! blockingFlag);
    if (! blockingFlag) {
      messageContainer.clear();
    }
    messageHistory.setEnabled(! blockingFlag);
  }

  public void blockLocalUserInfo(boolean blockingFlag) {
    connect.setEnabled(blockingFlag);
    buttonAddFriends.setEnabled(blockingFlag);
    buttonRemoveFriends.setEnabled(blockingFlag);
    textFieldIp.setEnabled(blockingFlag);
    tableFriends.setEnabled(blockingFlag);
    textFieldLocalNick.setEnabled(! blockingFlag);
    buttonChangeLocalNick.setEnabled(! blockingFlag);
    logOutButton.setEnabled(blockingFlag);
  }

  public void blockRemoteUserInfo(boolean blockingFlag) {
    textFieldIp.setEnabled(! blockingFlag);
  }

  public void showRemoteNick(String nick) {
    textFieldNick.setText(nick);
  }
}
