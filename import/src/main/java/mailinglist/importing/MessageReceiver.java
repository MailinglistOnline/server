/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailinglist.importing;

import java.io.IOException;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import mailinglist.DbClient;
import mailinglist.MessageManager;

/**
 *
 * @author matej
 */
public class MessageReceiver {

    
    public static void main(String[] args) throws MessagingException, IOException {
        Session s = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(s, System.in);
        System.out.println("Message received through the terminal");
        System.out.println("Message ID: " + message.getMessageID());
        System.out.println("Message content: " + message.getContent().toString());
        DbClient messageSaver;
        boolean sendMessageAlsoToSearchisko = true;
        if(args.length == 5) {
            messageSaver= new DbClient(args[0], args[1], Integer.valueOf(args[2]), args[3]);
            sendMessageAlsoToSearchisko=Boolean.valueOf(args[4]);
        } else {
            messageSaver = new DbClient();
        }
        MessageManager manager= new MessageManager(messageSaver,sendMessageAlsoToSearchisko);
        manager.createAndSaveMessage(message);
        messageSaver.closeConnection();
    }
}
