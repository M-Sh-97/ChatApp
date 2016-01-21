import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;

/**
 * @author M-Sh-97
 */
class MainForm extends JFrame {
  private JButton buttonConnecting,
		  buttonDisconnecting,
		  buttonContactAdding,
		  buttonContactRemoving,
		  buttonLoggingIn,
		  buttonLoggingOut,
		  buttonMessageSending,
		  buttonFileSending;
  private JTextField textFieldLocalUser,
		     textFieldRemoteUser,
		     textFieldIP;
  private JTextArea textAreaMessageTyping,
		    textAreaMessageHistory;
  private JTable tableLocalContact,
		 tableServerContact;
  private JLabel labelLocalUser,
		 labelRemoteUser,
		 labelIP,
		 labelLocalContact,
		 labelServerContact;
  private JScrollPane scrollMessageTypingSpace,
		      scrollTextAreaMessageHistory,
		      scrollTableLocalContact,
		      scrollTableServerContact;
  private JFileChooser fileChooser;
  private final Observer historyViewObserver;
  private Application logicModel;
  
  public MainForm(Application logic) {
    super();
    
    logicModel = logic;
        
    labelLocalUser = new JLabel();
    labelRemoteUser = new JLabel();
    labelIP = new JLabel();
    labelLocalContact = new JLabel();
    labelServerContact = new JLabel();
    buttonLoggingIn = new JButton();
    buttonLoggingOut = new JButton();
    buttonConnecting = new JButton();
    buttonDisconnecting = new JButton();    
    buttonMessageSending = new JButton();
    buttonFileSending = new JButton();
    buttonContactAdding = new JButton();
    buttonContactRemoving = new JButton();
    textFieldLocalUser = new JTextField();
    textFieldRemoteUser = new JTextField();
    textFieldIP = new JTextField();
    textAreaMessageTyping = new JTextArea();
    textAreaMessageHistory = new JTextArea();
    tableLocalContact = new JTable();
    tableServerContact = new JTable();
    scrollMessageTypingSpace = new JScrollPane();
    scrollTextAreaMessageHistory = new JScrollPane();
    scrollTableLocalContact = new JScrollPane();
    scrollTableServerContact = new JScrollPane();

    labelLocalUser.setHorizontalAlignment(JLabel.CENTER);
    labelLocalUser.setText("Локальный пользователь");
    labelLocalUser.setLabelFor(textFieldLocalUser);

    labelRemoteUser.setText("Удалённый пользователь");
    labelRemoteUser.setLabelFor(textFieldRemoteUser);

    labelIP.setText("Удалённый IP-адрес");
    labelIP.setLabelFor(textFieldIP);
    
    labelLocalContact.setHorizontalAlignment(JLabel.CENTER);
    labelLocalContact.setText("Сохранённые контакты");
    labelLocalContact.setLabelFor(tableLocalContact);
    
    labelServerContact.setHorizontalAlignment(JLabel.CENTER);
    labelServerContact.setText("Контакты на сервере");
    labelServerContact.setLabelFor(tableServerContact);
    
    buttonLoggingIn.setText("Войти");

    buttonLoggingOut.setText("Выйти");

    buttonConnecting.setText("Подсоединиться");

    buttonDisconnecting.setText("Отсоединиться");

    buttonMessageSending.setText("Отправить сообщение");

    buttonFileSending.setText("Отправить файл");

    buttonContactAdding.setText("Добавить в список");

    buttonContactRemoving.setText("Убрать из списка");
    
    textFieldRemoteUser.setEditable(false);
    
    Font maf = new Font("Roboto", 0, 15);
    
    textAreaMessageHistory.setEditable(false);
    textAreaMessageHistory.setFont(maf);
    textAreaMessageHistory.setLineWrap(true);
    textAreaMessageHistory.setWrapStyleWord(true);
    
    textAreaMessageTyping.setFont(maf);
    textAreaMessageTyping.setLineWrap(true);
    textAreaMessageTyping.setWrapStyleWord(true);
    
    tableLocalContact.setModel(logicModel.getLocalContactModel());
    tableLocalContact.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    
    tableServerContact.setModel(logicModel.getServerContactModel());
    tableLocalContact.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    scrollMessageTypingSpace.setPreferredSize(new Dimension(200, 60));
    scrollMessageTypingSpace.setViewportView(textAreaMessageTyping);

    scrollTextAreaMessageHistory.setPreferredSize(new Dimension(200, 200));
    scrollTextAreaMessageHistory.setViewportView(textAreaMessageHistory);

    scrollTableLocalContact.setViewportView(tableLocalContact);
    
    scrollTableServerContact.setViewportView(tableServerContact);
    
    blockDialogComponents(true);
    blockLocalUserInfo(false);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
	.addContainerGap()
	  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	    .addComponent(scrollTextAreaMessageHistory, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
	    .addComponent(scrollMessageTypingSpace, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
	    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addComponent(textFieldLocalUser, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
		.addComponent(labelLocalUser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addComponent(buttonLoggingOut, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		.addComponent(buttonLoggingIn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
		.addComponent(buttonDisconnecting, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		.addComponent(buttonConnecting, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	  .addComponent(scrollTableLocalContact, GroupLayout.PREFERRED_SIZE, 230, Short.MAX_VALUE)
          .addComponent(buttonContactRemoving, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(buttonMessageSending, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(buttonFileSending, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(scrollTableServerContact, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 230, Short.MAX_VALUE)
          .addComponent(buttonContactAdding, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(labelServerContact, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(labelLocalContact, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(buttonLoggingIn)
          .addComponent(textFieldRemoteUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonConnecting)
          .addComponent(labelLocalUser)
          .addComponent(labelRemoteUser))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(textFieldLocalUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonLoggingOut)
          .addComponent(labelIP)
          .addComponent(textFieldIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonDisconnecting))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonContactAdding)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	    .addComponent(labelLocalContact)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(scrollTableLocalContact, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonContactRemoving)
	    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	    .addComponent(labelServerContact)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(scrollTableServerContact, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
          .addComponent(scrollTextAreaMessageHistory))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonFileSending)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonMessageSending))
          .addComponent(scrollMessageTypingSpace, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
    pack();
    
    buttonLoggingIn.setMnemonic(KeyEvent.VK_J);
    buttonLoggingIn.setDisplayedMnemonicIndex(1);
    buttonLoggingOut.setMnemonic(KeyEvent.VK_S);
    buttonLoggingOut.setDisplayedMnemonicIndex(1);
    buttonConnecting.setMnemonic(KeyEvent.VK_L);
    buttonConnecting.setDisplayedMnemonicIndex(2);
    buttonDisconnecting.setMnemonic(KeyEvent.VK_N);
    buttonDisconnecting.setDisplayedMnemonicIndex(1);
    buttonMessageSending.setMnemonic(KeyEvent.VK_C);
    buttonMessageSending.setDisplayedMnemonicIndex(10);
    buttonContactAdding.setMnemonic(KeyEvent.VK_COMMA);
    buttonContactAdding.setDisplayedMnemonicIndex(2);
    buttonContactRemoving.setMnemonic(KeyEvent.VK_H);
    buttonContactRemoving.setDisplayedMnemonicIndex(2);
    buttonFileSending.setMnemonic(KeyEvent.VK_A);
    buttonFileSending.setDisplayedMnemonicIndex(10);
    
    fileChooser = new JFileChooser(".\\");
    fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
    fileChooser.setDialogTitle("Отправка файла");
    fileChooser.setApproveButtonText("Подтвердить выбор");
    fileChooser.setApproveButtonMnemonic(KeyEvent.VK_D);
    
    StringBuilder sm = new StringBuilder(Protocol.programName.length() + Protocol.version.length() + 1);
    sm.append(Protocol.programName);
    sm.append(Protocol.space);
    sm.append(Protocol.version);
    setTitle(sm.toString());
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(750, 500);

    historyViewObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	if (o instanceof HistoryModel)
	  if (((Vector<String>) arg).isEmpty()) {
	    textAreaMessageHistory.setText(null);
	  } else {
	    if (! textAreaMessageHistory.getText().isEmpty()) {
	      textAreaMessageHistory.append(Protocol.endOfLine);
	    }
	    HistoryModel.Message msgData = ((HistoryModel) o).getMessage(((HistoryModel) o).getSize() - 1);
	    textAreaMessageHistory.append(msgData.getNick() + ". " + logicModel.getDateFormat().format(msgData.getDate()) + "." + Protocol.endOfLine + msgData.getText());
	  }
      }
    };
    logicModel.getMessageHistoryModel().addObserver(historyViewObserver);

    buttonConnecting.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String fIP = textFieldIP.getText();
	logicModel.makeOutcomingCall(fIP);
      }
    });

    buttonDisconnecting.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	blockDialogComponents(true);
	logicModel.finishCall();
	logicModel.saveMessageHistory(textAreaMessageHistory.getText());
	blockRemoteUserInfo(false);
      }
    });

    buttonMessageSending.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String text = textAreaMessageTyping.getText();
	if (! text.isEmpty()) {
	  logicModel.sendMessage(text);
	  logicModel.addMessage(logicModel.getLocalUserNick(), text);
	  textAreaMessageTyping.setText(null);
	}
      }
    });
    
    buttonFileSending.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	File fp = showFileChoiceDialog();
	if (fp != null) {
	  logicModel.sendFile(fp);
	  logicModel.addMessage(logicModel.getLocalUserNick(), Protocol.fileMessageLeadingLabel + fp.getName());
	}
      }
    });

    buttonLoggingIn.addActionListener(new ActionListener() {
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

    buttonLoggingOut.addActionListener(new ActionListener() {
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
	int sr = tableLocalContact.getSelectedRow();
	if (sr >= 0) {
	  for (; sr < tableLocalContact.getSelectedRowCount() + sr; sr ++)
	    logicModel.removeContact(sr);
	  tableLocalContact.clearSelection();
	}
      }
    });

    tableLocalContact.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	if (tableLocalContact.getSelectedRows().length == 1) {
	  if (tableServerContact.getSelectedRows().length > 0)
	    tableServerContact.clearSelection();
	  ContactTableModel tm = (ContactTableModel) tableLocalContact.getModel();
	  int sr = tableLocalContact.getSelectedRow();
	  textFieldRemoteUser.setText(tm.getValueAt(sr, 0));
	  textFieldIP.setText(tm.getValueAt(sr, 1));
	}
      }
    });
    
    tableServerContact.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	if (tableServerContact.getSelectedRows().length == 1) {
	  if (tableLocalContact.getSelectedRows().length > 0)
	    tableLocalContact.clearSelection();
	  ContactTableModel tm = (ContactTableModel) tableServerContact.getModel();
	  int sr = tableServerContact.getSelectedRow();
	  textFieldRemoteUser.setText(tm.getValueAt(sr, 0));
	  textFieldIP.setText(tm.getValueAt(sr, 1));
	}
      }
    });

    tableLocalContact.addKeyListener(new KeyAdapter() {
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
	  buttonLoggingIn.doClick();
      }
    });

    textFieldIP.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == 10) // "Enter"
	  buttonConnecting.doClick();
      }
      
      @Override
      public void keyTyped(KeyEvent e) {
	if (! textFieldRemoteUser.getText().isEmpty()) {
	  int cc = e.getKeyChar();
	  if (! ((cc == KeyEvent.CHAR_UNDEFINED) || (cc == 10))) // "Enter"
	    textFieldRemoteUser.setText(null);
	}
      }
    });
  }

  public void showIncomingCallDialog(String nick, String address) {
    Object[] option = {"Принять", "Отклонить"};
    if (JOptionPane.showOptionDialog(this, "Пользователь " + nick + " с адреса " + address + " желает переписываться с Вами.", "Входящий запрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]) == 0) {
      logicModel.acceptIncomingCall();
      textFieldRemoteUser.setText(nick);
      textFieldIP.setText(address);
      blockRemoteUserInfo(true);
      blockDialogComponents(false);
    } else
      logicModel.rejectIncomingCall();
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
	buttonLoggingOut.doClick();
      setVisible(false);
      dispose();
      System.exit(0);
    }
  }
  
  public File showFileChoiceDialog() {
    if (fileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION)
      return fileChooser.getSelectedFile();
    else
      return null;
  }

  public final void blockDialogComponents(boolean blockingFlag) {
    buttonDisconnecting.setEnabled(! blockingFlag);
    textAreaMessageTyping.setEnabled(! blockingFlag);
    buttonMessageSending.setEnabled(! blockingFlag);
    buttonFileSending.setEnabled(! blockingFlag);
    textAreaMessageHistory.setEnabled(! blockingFlag);
    buttonLoggingOut.setEnabled(blockingFlag);
    buttonConnecting.setEnabled(blockingFlag);
    tableLocalContact.setEnabled(blockingFlag);
  }

  public final void blockLocalUserInfo(boolean blockingFlag) {
    buttonConnecting.setEnabled(blockingFlag);
    buttonContactAdding.setEnabled(blockingFlag);
    buttonContactRemoving.setEnabled(blockingFlag);
    textFieldRemoteUser.setEnabled(blockingFlag);
    textFieldIP.setEnabled(blockingFlag);
    tableLocalContact.setEnabled(blockingFlag);
    textFieldLocalUser.setEnabled(! blockingFlag);
    buttonLoggingIn.setEnabled(! blockingFlag);
    buttonLoggingOut.setEnabled(blockingFlag);
  }

  public final void blockRemoteUserInfo(boolean blockingFlag) {
    textFieldRemoteUser.setEnabled(! blockingFlag);
    textFieldIP.setEnabled(! blockingFlag);
  }

  public void showRemoteUserNick(String nick) {
    textFieldRemoteUser.setText(nick);
  }
  
  public void showRemoteAddress(String address) {
    textFieldIP.setText(address);
  }
}
