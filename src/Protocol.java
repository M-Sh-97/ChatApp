/**
 * @author M-Sh-97
 */
interface Protocol {
    static final int port = 28411;
    static final String endOfLine = "\n",
			space = " ",
                        defaultLocalIPAddress = "0.0.0.0",
                        encoding = "UTF-8",
                        defaultLocalUserNick = "Без имени",
                        version = "4.0",
                        programName = "ChatApp",
			serverAddress = "jdbc:mysql://files.litvinov.in.ua/chatapp_server?characterEncoding=utf-8&useUnicode=true",
			userDataFileExtension = ".dat",
			acceptionCommandPhrase = "ACCEPTED",
			rejectionCommandPhrase = "REJECTED",
			disconnectionCommandPhrase = "DISCONNECT",
			messageCommandPhrase = "MESSAGE",
			nickCommandPhrase = "USER",
			busyStatusPhrase = "BUSY",
			fileCommandPhrase = "FILE",
			fileMessageLeadingLabel = "[Файл]";
}
