/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import mailinglistonline.server.export.database.DatabaseConfiguration;
import mailinglistonline.server.export.database.DbClient;
import mailinglistonline.server.export.database.entities.Email;
import mailinglistonline.server.export.database.entities.MiniEmail;
import mailinglistonline.server.export.util.PropertiesParser;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 *
 * @author Matej Briškár
 */
@RunWith(Arquillian.class)
public class DbClientTests {

    private DbClient dbClient;
    private ArrayList<Email> insertedEmails;

    public DbClientTests() throws UnknownHostException, IOException {
        DatabaseConfiguration configuration = PropertiesParser.parseDatabaseConfigurationFile(DbClient.class
				.getClass()
				.getResource((DbClient.DATABASE_PROPERTIES_FILE_NAME))
				.getPath());
        configuration.setDefaultCollectionName("test");
        dbClient = new DbClient(configuration);
        dbClient.dropTable();
    }
    

    @Before
    public void setUp() throws IOException {
        insertedEmails = new ArrayList<Email>();
        Email email1 = new Email();
        email1.setFrom("from1@from1.sk");
        email1.setMessageId("message1");
        email1.setRoot(null);
        email1.setMailingList("mailinglist1");
        //dbClient.(email1);
        insertedEmails.add(email1);

        Email email2 = new Email();
        email2.setFrom("from2@from2.sk");
        email2.setMessageId("message2");
        email2.setRoot(email1);
        email2.setInReplyTo(email1);
       // email2.addMailingList("mailinglist1");
        //dbClient.saveMessage(email2);
        insertedEmails.add(email2);

        Email email3 = new Email();
        email3.setFrom("from3@from3.sk");
        email3.setMessageId("message3");
        email3.setRoot(email1);
        email3.setInReplyTo(email1);
        email3.setMailingList("mailinglist2");
        //dbClient.saveMessage(email3);
        insertedEmails.add(email3);

        Email email4 = new Email();
        email4.setFrom("from4@from4.sk");
        email4.setMessageId("message4");
        email4.setRoot(email1);
        email4.setInReplyTo(email3);
        email4.setMailingList("mailinglist1");
        //dbClient.saveMessage(email4);
        insertedEmails.add(email4);

        Email email5 = new Email();
        email5.setFrom("from2@from2.sk");
        email5.setMessageId("message5");
        email5.setRoot(null);
        email5.setMailingList("mailinglist1");
        //dbClient.saveMessage(email5);
        insertedEmails.add(email5);

    }

    @After
    public void tearDown() {
        dbClient.dropTable();
    }

    @Test
    public void getEmailById() {
        Email email = dbClient.getEmailWithId(insertedEmails.get(0).getId());
        email.setReplies(new ArrayList<MiniEmail>());
        assertEquals(email,insertedEmails.get(0));
    }

    @Test
    public void getAllEmails() {
        assertEquals(5, dbClient.getAllEmails().size());
        List<Email> allEmails = dbClient.getAllEmails();
        for (Email email : allEmails) {
            email.setReplies(new ArrayList<MiniEmail>());
        }
        for (int i = 0; i < insertedEmails.size(); i++) {
            Email email =insertedEmails.get(i);
            assertTrue(allEmails.contains(email));
        }
    }

    @Test
    public void getEmailByAuthor() {
        List<Email> fromEmails = dbClient.getEmailsFrom("from1@from1.sk");
        assertEquals(1, fromEmails.size());
        assertEquals(insertedEmails.get(0).getId(),fromEmails.get(0).getId());
        
        fromEmails = dbClient.getEmailsFrom("from2@from2.sk");
        for (Email email : fromEmails) {
            email.setReplies(new ArrayList<MiniEmail>());
        }
        assertEquals(2, fromEmails.size());
        assertTrue(insertedEmails.contains(fromEmails.get(0)));
        assertTrue(insertedEmails.contains(fromEmails.get(1)));
    }

    @Test
    public void getMailingListRoots() {
        List<Email> pathEmails = dbClient.getMailinglistRoot("mailinglist1");
        for (Email email : pathEmails) {
            email.setReplies(new ArrayList<MiniEmail>());
        }
        
        assertEquals(pathEmails.size(),2);
        assertTrue(pathEmails.contains(insertedEmails.get(0)));
        assertTrue(pathEmails.contains(insertedEmails.get(4)));
    }

   /* @Test
    public void getEmailPath() {
        List<Email> pathEmails = dbClient.getWholePathFromId(insertedEmails.get(0).getId());
        for (Email email : pathEmails) {
            email.setReplies(new ArrayList<MiniEmail>());
        }
        assertTrue(pathEmails.contains(insertedEmails.get(0)));
        assertTrue(pathEmails.contains(insertedEmails.get(1)));
        assertTrue(pathEmails.contains(insertedEmails.get(2)));
        assertTrue(pathEmails.contains(insertedEmails.get(3)));
        
    }*/
}
