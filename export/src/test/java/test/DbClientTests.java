/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import mailinglistonline.server.export.database.DbClient;
import mailinglistonline.server.export.database.entities.Email;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 *
 * @author matej
 */
@RunWith(Arquillian.class)
public class DbClientTests {

	//@Inject I have to create dbClient with not default parameters. In the future, use Alternative or producer method???
    private DbClient dbClient;
    private int mongoPort = 27017;
    private String mongoUrl = "localhost";
    private String databaseName = "testdb";
    private String collectionName = "test";
    private ArrayList<Email> insertedEmails;

    // i am not using injection
   /* @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(DbClient.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }*/
    
    public DbClientTests() throws UnknownHostException, IOException {
        dbClient = new DbClient(mongoUrl, databaseName, mongoPort, collectionName);
        dbClient.dropTable();
    }
    

    @Before
    public void setUp() throws IOException {
        insertedEmails = new ArrayList<Email>();
        Email email1 = new Email();
        email1.setFrom("from1@from1.sk");
        email1.setMessageId("message1");
        email1.setRoot("true");
        email1.addMailingList("mailinglist1");
        dbClient.saveMessage(email1);
        insertedEmails.add(email1);

        Email email2 = new Email();
        email2.setFrom("from2@from2.sk");
        email2.setMessageId("message2");
        email2.setRoot(email1.getId());
        email2.setInReplyTo(email1.getId());
        email2.addMailingList("mailinglist1");
        dbClient.saveMessage(email2);
        insertedEmails.add(email2);

        Email email3 = new Email();
        email3.setFrom("from3@from3.sk");
        email3.setMessageId("message3");
        email3.setRoot(email1.getId());
        email3.setInReplyTo(email1.getId());
        email3.addMailingList("mailinglist2");
        dbClient.saveMessage(email3);
        insertedEmails.add(email3);

        Email email4 = new Email();
        email4.setFrom("from4@from4.sk");
        email4.setMessageId("message4");
        email4.setRoot(email1.getId());
        email4.setInReplyTo(email3.getId());
        email4.addMailingList("mailinglist1");
        dbClient.saveMessage(email4);
        insertedEmails.add(email4);

        Email email5 = new Email();
        email5.setFrom("from2@from2.sk");
        email5.setMessageId("message5");
        email5.setRoot("true");
        email5.addMailingList("mailinglist1");
        dbClient.saveMessage(email5);
        insertedEmails.add(email5);

    }

    @After
    public void tearDown() {

        dbClient.dropTable();
    }

    @Test
    public void getEmailById() {
        Email email = dbClient.getEmailWithId(insertedEmails.get(0).getId());
        email.setReplies(new ArrayList<String>());
        assertEquals(email,insertedEmails.get(0));
        
    }

    @Test
    public void getAllEmails() {
        assertEquals(5, dbClient.getAllEmails().size());
        List<Email> allEmails = dbClient.getAllEmails();
        for (Email email : allEmails) {
            email.setReplies(new ArrayList<String>());
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
            email.setReplies(new ArrayList<String>());
        }
        assertEquals(2, fromEmails.size());
        assertTrue(insertedEmails.contains(fromEmails.get(0)));
        assertTrue(insertedEmails.contains(fromEmails.get(1)));
    }

    @Test
    public void getMailingListRoots() {
        List<Email> pathEmails = dbClient.getMailinglistRoot("mailinglist1");
        for (Email email : pathEmails) {
            email.setReplies(new ArrayList<String>());
        }
        
        assertEquals(pathEmails.size(),2);
        assertTrue(pathEmails.contains(insertedEmails.get(0)));
        assertTrue(pathEmails.contains(insertedEmails.get(4)));
    }

    @Test
    public void getEmailPath() {
        List<Email> pathEmails = dbClient.getWholePathFromId(insertedEmails.get(0).getId());
        for (Email email : pathEmails) {
            email.setReplies(new ArrayList<String>());
        }
        assertTrue(pathEmails.contains(insertedEmails.get(0)));
        assertTrue(pathEmails.contains(insertedEmails.get(1)));
        assertTrue(pathEmails.contains(insertedEmails.get(2)));
        assertTrue(pathEmails.contains(insertedEmails.get(3)));
        
    }
}
