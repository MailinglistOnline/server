package test;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

import mailinglist.importing.MboxImporter;
import mailinglistonline.server.export.database.DbClient;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * This tests do not run from the maven test phase, because they end with 'Tests'. These classes are just giving the output
 * of the importing part, which has no limitations right now (=> no assertions).
 */
public class PerformanceOutputTests {

    public static final String TEST_MAILS_PATH = "src/test/java/mboxes/test-mails";
    public static final String TEST_MAILS_PATH2 ="src/test/java/mboxes/test-mails2";
    public static final String SIMPLE_MAIL_PATH ="src/test/java/mboxes/simpleMail";
    public static final String MBOX_FOLDER_PATH ="src/test/java/mboxes/folder";
    private DbClient dbClient;
    private int mongoPort = 27017;
    private String mongoUrl = "localhost";
    private String databaseName = "testdb";
    private String collectionName = "test";

    public PerformanceOutputTests() throws UnknownHostException {
        dbClient = new DbClient(mongoUrl, databaseName, mongoPort, collectionName);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        dbClient.dropTable();
    }

    @Test
    public void testMboxNumberOfMessages() throws UnknownHostException, NoSuchProviderException, MessagingException, IOException {
    	long before = System.currentTimeMillis();
    	MboxImporter mbox = new MboxImporter(dbClient,false);
    	for (int i =0;i<100;i++) {
    		mbox.importMbox(TEST_MAILS_PATH);
            tearDown();
    	}
    	long after = System.currentTimeMillis();
    	System.out.println("The importing took " + (after-before) + "ms");

    }

}