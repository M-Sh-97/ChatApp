/**
 * @author M-Sh-97
 */
interface Protocol {
    static final int port = 28411;
    static final String endOfLine = "\n",
                        defaultLocalIPAddress = "127.0.0.1",
                        encoding = "UTF-8",
                        defaultLocalNick = "unnamed",
                        version = "2015",
                        programName = "CHATAPP",
			serverAddress = "jdbc:mysql://files.litvinov.in.ua/chatapp_server?characterEncoding=utf-8&useUnicode=true",
			contactFileName = "'s contacts.dat",
  			acceptionCommandPhrase = "ACCEPTED",
  			rejectionCommandPhrase = "REJECTED",
	    		disconnectionCommandPhrase = "DISCONNECT",
			messageCommandPhrase = "MESSAGE",
  			nickCommandPhrase = "USER";
}
