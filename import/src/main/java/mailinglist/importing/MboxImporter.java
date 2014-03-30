/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailinglist.importing;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;

import mailinglist.MessageManager;
import mailinglistonline.server.export.database.DatabaseConfiguration;
import mailinglistonline.server.export.database.DbClient;
import net.fortuna.mstor.MStorFolder;

/**
 *
 * @author Matej Briškár
 */
public class MboxImporter {

    private static final long TIME_TO_WAIT_FOR_THREADS = 50;
    private static String DATABASE_PROPERTIES_FILE_NAME = "database.properties";
	private DbClient messageSaver;
	private boolean saveAlsoToSearchisko;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchProviderException, MessagingException, IOException {
    	DbClient msgSaver =new DbClient(new DatabaseConfiguration().readFromConfigurationFile(
    			MboxImporter.class.getClassLoader().getResource((DATABASE_PROPERTIES_FILE_NAME)).getPath()));
        MboxImporter mbox = new MboxImporter(msgSaver,true);
        if(!args[0].contains("/")) {
        	args[0]="./"+args[0];
        }

        if (args.length == 1) {
            File file = new File(args[0]);
            if (file.isDirectory()) {
                mbox.importMboxDirectory(args[0]);
            } else {
                mbox.importMbox(args[0]);
            }

        } else {
            System.out.println("Call the method with one parameter (mbox path)");
        }

    }

    public MboxImporter(DbClient msgSaver, boolean saveAlsoToSearchisko) throws UnknownHostException {
        this.saveAlsoToSearchisko=saveAlsoToSearchisko;
    	messageSaver = msgSaver;
    }

    public void importMboxDirectory(String directoryPath) throws NoSuchProviderException, IOException, MessagingException {
    	ExecutorService executor =Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    	File directory = new File(directoryPath);
        File[] subfiles = directory.listFiles();
        String mboxDirectory = directoryPath;
        Properties props = new Properties();
        props.setProperty("mstor.mbox.metadataStrategy", "none");
        Session session = Session.getDefaultInstance(props);
        final MessageManager manager = new MessageManager(messageSaver,saveAlsoToSearchisko);
        Store store = session.getStore(new URLName("mstor:" + mboxDirectory));
        store.connect();
        for (File file : subfiles) {
            if (file.isDirectory()) {
                importMboxDirectory(file.getAbsolutePath());
            }
            String mboxFile = file.getName();
            Folder inbox = store.getDefaultFolder().getFolder(mboxFile);
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.getMessages();
            System.out.println("Importing" + messages.length + "messages.");
            for (final Message m : messages) {
            	executor.execute(new Runnable() {
                    public void run() {
                    	manager.createAndSaveMessage((MimeMessage) m);
                    }
                });
                    
            } 
        }
        executor.shutdown();

        try {
        	executor.awaitTermination(TIME_TO_WAIT_FOR_THREADS, TimeUnit.SECONDS);
         } catch (InterruptedException ex) {
         }
        store.close();
    }

    public void importMbox(String mboxPath) throws NoSuchProviderException, MessagingException, IOException {
        File file = new File(mboxPath);
        String mboxFile = file.getName();
        String mboxDirectory = file.getParentFile().getAbsolutePath();
        Properties props = new Properties();
        props.setProperty("mstor.mbox.metadataStrategy", "none");
        Session session = Session.getDefaultInstance(props);
        Store store = session.getStore(new URLName("mstor:" + mboxDirectory));
        store.connect();
        MStorFolder inbox = (MStorFolder) store.getDefaultFolder().getFolder(mboxFile);
        inbox.open(Folder.READ_ONLY);
        Message[] messages = inbox.getMessages();
        System.out.println("Importing" + messages.length + "messages.");
        MessageManager manager = new MessageManager(messageSaver,saveAlsoToSearchisko);
        for (Message m : messages) {
                manager.createAndSaveMessage((MimeMessage) m);
        }
        store.close();
        System.out.println("Done.");

    }
}
