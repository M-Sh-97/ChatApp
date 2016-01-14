import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * @author M-Sh-97
 */
class MainForm extends JFrame {
  private JButton buttonConnect,
		  buttonDisconnect,
		  buttonContactAdding,
		  buttonContactRemoving,
		  buttonLogIn,
		  buttonLogOut,
		  buttonSend,
		  buttonHistorySaving;
  private JTextField textFieldLocalUser,
		     textFieldRemoteUser,
		     textFieldIP;
  private JTextArea messageTypingSpace,
		    messageHistory;
  private JTable contactTable;
  private JLabel labelLocalUser,
		 labelRemoteUser,
		 labelIP;
  private JScrollPane scrollMessageHistory,
		      scrollContactTable,
		      scrollMessageTypingSpace;
  private HistoryModel messageContainer;
  private final Observer historyViewObserver;
  private Application logicModel;
  
  public MainForm(Application logic) {
    super();
    
    logicModel = logic;
    messageContainer = logicModel.getMessageHistoryModel();
        
    labelLocalUser = new JLabel();
    textFieldLocalUser = new JTextField();
    buttonLogIn = new JButton();
    labelRemoteUser = new JLabel();
    textFieldRemoteUser = new JTextField();
    labelIP = new JLabel();
    textFieldIP = new JTextField();
    buttonConnect = new JButton();
    buttonDisconnect = new JButton();
    scrollContactTable = new JScrollPane();
    contactTable = new JTable();
    scrollMessageTypingSpace = new JScrollPane();
    messageTypingSpace = new JTextArea();
    buttonSend = new JButton();
    buttonLogOut = new JButton();
    buttonContactAdding = new JButton();
    scrollMessageHistory = new JScrollPane();
    messageHistory = new JTextArea();
    buttonContactRemoving = new JButton();
    buttonHistorySaving = new JButton();

    labelLocalUser.setHorizontalAlignment(JLabel.CENTER);
    labelLocalUser.setText("Локальный пользователь");

    buttonLogIn.setText("Войти");

    labelRemoteUser.setText("Удалённый пользователь");

    labelIP.setText("Удалённый IP-адрес");

    buttonConnect.setText("Подсоединиться");

    buttonDisconnect.setText("Отсоединиться");

    scrollContactTable.setViewportView(contactTable);

    scrollMessageTypingSpace.setPreferredSize(new Dimension(200, 60));
    scrollMessageTypingSpace.setViewportView(messageTypingSpace);

    buttonSend.setText("Отправить");

    buttonLogOut.setText("Выйти");

    buttonContactAdding.setText("Добавить в список");

    scrollMessageHistory.setPreferredSize(new Dimension(200, 200));
    scrollMessageHistory.setViewportView(messageHistory);

    buttonContactRemoving.setText("Убрать из списка");

    buttonHistorySaving.setText("Сохранить текст переписки");
    
    textFieldRemoteUser.setEditable(false);
    
    Font maf = new Font("Roboto", 0, 15);
    
    messageHistory.setEditable(false);
    messageHistory.setFont(maf);
    messageHistory.setLineWrap(true);
    messageHistory.setWrapStyleWord(true);
    
    messageTypingSpace.setFont(maf);
    messageTypingSpace.setLineWrap(true);
    messageTypingSpace.setWrapStyleWord(true);
    
    contactTable.setModel(logicModel.getContactModel());
    
    blockDialogComponents(true);
    blockLocalUserInfo(false);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
	.addContainerGap()
	  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	    .addComponent(scrollMessageHistory, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
	    .addComponent(scrollMessageTypingSpace, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
	    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addComponent(textFieldLocalUser, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
		.addComponent(labelLocalUser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addComponent(buttonLogOut, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		.addComponent(buttonLogIn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addComponent(labelIP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		.addComponent(labelRemoteUser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
	  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    .addGroup(layout.createSequentialGroup()
	      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addComponent(textFieldIP, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
		.addComponent(textFieldRemoteUser, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
	      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addComponent(buttonDisconnect, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		.addComponent(buttonConnect, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	  .addComponent(scrollContactTable, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
          .addComponent(buttonContactRemoving, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(buttonContactAdding, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(buttonSend, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(buttonHistorySaving, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(buttonLogIn)
          .addComponent(textFieldRemoteUser)
          .addComponent(buttonConnect)
          .addComponent(labelLocalUser)
          .addComponent(labelRemoteUser))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(textFieldLocalUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonLogOut)
          .addComponent(labelIP)
          .addComponent(textFieldIP)
          .addComponent(buttonDisconnect))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonContactAdding)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(scrollContactTable, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonContactRemoving))
          .addComponent(scrollMessageHistory))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonHistorySaving)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonSend))
          .addComponent(scrollMessageTypingSpace, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
    pack();
    
    buttonLogIn.setMnemonic(KeyEvent.VK_J);
    buttonLogIn.setDisplayedMnemonicIndex(1);
    buttonLogOut.setMnemonic(KeyEvent.VK_S);
    buttonLogOut.setDisplayedMnemonicIndex(1);
    buttonConnect.setMnemonic(KeyEvent.VK_L);
    buttonConnect.setDisplayedMnemonicIndex(2);
    buttonDisconnect.setMnemonic(KeyEvent.VK_N);
    buttonDisconnect.setDisplayedMnemonicIndex(1);
    buttonSend.setMnemonic(KeyEvent.VK_G);
    buttonSend.setDisplayedMnemonicIndex(2);
    buttonContactAdding.setMnemonic(KeyEvent.VK_COMMA);
    buttonContactAdding.setDisplayedMnemonicIndex(2);
    buttonContactRemoving.setMnemonic(KeyEvent.VK_H);
    buttonContactRemoving.setDisplayedMnemonicIndex(2);
    buttonHistorySaving.setMnemonic(KeyEvent.VK_OPEN_BRACKET);
    buttonHistorySaving.setDisplayedMnemonicIndex(2);
    
    StringBuilder sm = new StringBuilder(Protocol.programName.length() + Protocol.version.length() + 1);
    sm.append(Protocol.programName);
    sm.append(' ');
    sm.append(Protocol.version);
    setTitle(sm.toString());
    setSize(750, 500);

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

    buttonConnect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String fIP = textFieldIP.getText();
	logicModel.makeOutcomingCall(fIP);
      }
    });

    buttonDisconnect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	blockDialogComponents(true);
	logicModel.finishCall();
	try {
	  Thread.sleep(100);
	} catch (InterruptedException ex) {}
	logicModel.closeConnection();
	blockRemoteUserInfo(false);
      }
    });

    buttonSend.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String text = messageTypingSpace.getText();
	if (! text.isEmpty()) {
	  logicModel.sendMessage(text);
	  logicModel.addMessage(logicModel.getLocalUserNick(), text);
	  messageTypingSpace.setText(null);
	}
      }
    });

    buttonLogIn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String flun = textFieldLocalUser.getText();
	logicModel.logIn(flun);
	if (flun.isEmpty()) {
	  textFieldLocalUser.setText(Protocol.defaultLocalUserNick);
	}
	logicModel.loadContactsFromFile();
	logicModel.loadContactsFromServer();
	logicModel.startListeningForCalls();
	blockLocalUserInfo(true);
      }
    });

    buttonLogOut.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	logicModel.finishListeningForCalls();
	logicModel.saveContactsToFile();
	logicModel.logOut();
	textFieldRemoteUser.setText(null);
	textFieldIP.setText(null);
	blockLocalUserInfo(false);
      }
    });

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
	if (! logicModel.isBusy())
	  showExitDialog();
      }
    });

    buttonContactAdding.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (!((textFieldIP.getText().isEmpty()) || (textFieldRemoteUser.getText().isEmpty()))) {
	  logicModel.addContact(textFieldRemoteUser.getText(), textFieldIP.getText());
	}
      }
    });

    buttonContactRemoving.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int sr = contactTable.getSelectedRow();
	if (sr >= 0) {
	  logicModel.removeContact(sr);
	  contactTable.clearSelection();
	}
      }
    });
    
    buttonHistorySaving.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	logicModel.saveMessageHistory(messageHistory.getText());
      }
    });

    contactTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	if (contactTable.getSelectedRow() >= 0) {
	  String nick = contactTable.getModel().getValueAt(contactTable.getSelectedRow(), 0).toString();
	  String ip = contactTable.getModel().getValueAt(contactTable.getSelectedRow(), 1).toString();
	  textFieldRemoteUser.setText(nick);
	  textFieldIP.setText(ip);
	}
      }
    });

    contactTable.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 127) // "Delete"
	  buttonContactRemoving.doClick();
      }
    });

    textFieldLocalUser.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 10) // "Enter"
	  buttonLogIn.doClick();
      }
    });

    textFieldIP.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 10) // "Enter"
	  buttonConnect.doClick();
      }
      
      @Override
      public void keyTyped(KeyEvent e) {
	if (! textFieldRemoteUser.getText().isEmpty())
	  if (e.getKeyChar() != 10) // "Enter"
	    textFieldRemoteUser.setText(null);
      }
    });
  }

  public void showIncomingCallDialog(String nick, String IP) {
    Object[] option = {"Принять", "Отклонить"};
    if (JOptionPane.showOptionDialog(this, "Пользователь " + nick + " с адреса " + IP + " желает переписываться с Вами.", "Входящий запрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]) == 0) {
      logicModel.acceptIncomingCall();
      textFieldRemoteUser.setText(nick);
      textFieldIP.setText(IP);
      blockRemoteUserInfo(true);
      blockDialogComponents(false);
    } else {
      logicModel.rejectIncomingCall();
      try {
	Thread.sleep(100);
      } catch (InterruptedException ex) {}
      logicModel.closeConnection();
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
  
  public void showExitDialog() {
    Object[] option = {"Да", "Нет"};
    if (JOptionPane.showOptionDialog(this, "Вы действительно хотите выйти?", "Выход из программы", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]) == 0) {
      if (logicModel.getLocalUserNick() != null)
	buttonLogOut.doClick();
      hide();
      dispose();
      System.exit(0);
    }
  }

  public final void blockDialogComponents(boolean blockingFlag) {
    buttonDisconnect.setEnabled(! blockingFlag);
    messageTypingSpace.setEnabled(! blockingFlag);
    buttonSend.setEnabled(! blockingFlag);
    messageHistory.setEnabled(! blockingFlag);
    buttonLogOut.setEnabled(blockingFlag);
    buttonConnect.setEnabled(blockingFlag);
    contactTable.setEnabled(blockingFlag);
    if (blockingFlag) {
      if (messageContainer.getSize() > 0)
	buttonHistorySaving.setEnabled(true);
    }
    else
      if (buttonHistorySaving.isEnabled())
	buttonHistorySaving.setEnabled(false);
  }

  public final void blockLocalUserInfo(boolean blockingFlag) {
    buttonConnect.setEnabled(blockingFlag);
    buttonContactAdding.setEnabled(blockingFlag);
    buttonContactRemoving.setEnabled(blockingFlag);
    textFieldRemoteUser.setEnabled(blockingFlag);
    textFieldIP.setEnabled(blockingFlag);
    contactTable.setEnabled(blockingFlag);
    textFieldLocalUser.setEnabled(! blockingFlag);
    buttonLogIn.setEnabled(! blockingFlag);
    buttonLogOut.setEnabled(blockingFlag);
    if (! blockingFlag)
      if (buttonHistorySaving.isEnabled())
	buttonHistorySaving.setEnabled(false);
  }

  public final void blockRemoteUserInfo(boolean blockingFlag) {
    textFieldRemoteUser.setEnabled(! blockingFlag);
    textFieldIP.setEnabled(! blockingFlag);
  }

  public void showRemoteUserNick(String nick) {
    textFieldRemoteUser.setText(nick);
  }
}
