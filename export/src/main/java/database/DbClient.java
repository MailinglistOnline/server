/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import database.entities.ContentPart;
import database.entities.Email;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.ejb.Stateless;
import javax.faces.bean.ApplicationScoped;

import javax.mail.MessagingException;
import org.bson.types.ObjectId;

/**
 *
 * @author matej
 */
@ApplicationScoped
public class DbClient {
    private static String DATABASE_PROPERTIES_FILE_NAME = "database.properties";
    private static String MAILINGLISTS_PROPERTIES_FILE_NAME = "mailinglists.properties";
    List<String> mailingLists = new ArrayList<String>();
    DBCollection coll;
    MongoClient mongoClient;

    public DbClient() throws UnknownHostException, IOException {
        Properties prop = new Properties();
        prop.load(DbClient.class.getClassLoader().getResourceAsStream((DATABASE_PROPERTIES_FILE_NAME)));
        Integer defaultPort = Integer.valueOf(prop.getProperty("defaultMongoPort"));
        String databaseUrl = prop.getProperty("defaultMongoUrl");
        String defaultDatabaseName = prop.getProperty("defaultDatabaseName");
        String defaultCollectionName = prop.getProperty("defaultCollection");
        connect(databaseUrl, defaultDatabaseName, defaultPort, defaultCollectionName);
        readMailinglists();

    }
    
    public void readMailinglists() throws IOException {
        Properties prop = new Properties();
        prop.load(DbClient.class.getClassLoader().getResourceAsStream((MAILINGLISTS_PROPERTIES_FILE_NAME)));
        String mailinglist = prop.getProperty("mailinglist." + 1);
        int i = 1;
        while (mailinglist != null) {
            mailingLists.add(mailinglist);
            i++;
            mailinglist = prop.getProperty("mailinglist." + i);
        }
        
    }

    public DbClient(String mongoUrl, String databaseName, int mongoPort, String collectionName) throws UnknownHostException {
        connect(mongoUrl, databaseName, mongoPort, collectionName);
    }

    private void connect(String mongoUrl, String databaseName, int mongoPort, String collectionName) throws UnknownHostException {
        mongoClient = new MongoClient(mongoUrl, mongoPort);
        DB db = mongoClient.getDB(databaseName);
        mongoClient.setWriteConcern(WriteConcern.SAFE);
        coll = db.getCollection(collectionName);
         coll.setObjectClass(Email.class);
        coll.setInternalClass(Email.MAIN_CONTENT_MONGO_TAG+ ".0", ContentPart.class);
        coll.setInternalClass(Email.MAIN_CONTENT_MONGO_TAG+ ".1", ContentPart.class);
        coll.setInternalClass(Email.MAIN_CONTENT_MONGO_TAG+ ".2", ContentPart.class);
        coll.setInternalClass(Email.MAIN_CONTENT_MONGO_TAG+ ".3", ContentPart.class);
        coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".0" , ContentPart.class);
        coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".1" , ContentPart.class);
        coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".2" , ContentPart.class);
        coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".3" , ContentPart.class);
        coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".4" , ContentPart.class);
        coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".5" , ContentPart.class);
    }
    
    public void closeConnection() {
        mongoClient.close();
    }

    

    public DBCollection getColl() {
        return coll;
    }

    public void dropTable() {
        this.coll.drop();
    }

    public long emailCount() {
        return coll.count();
    }

    public Email findFirstMessageWithMessageId(String messageId) {
        BasicDBObject idObj = new BasicDBObject("message_id", messageId);
        return (Email) coll.findOne(idObj);
    }

    public List<Email> getAllEmails() {
        coll.setObjectClass(Email.class);
        DBCursor cursor = coll.find();
        List<Email> emails = new ArrayList<Email>();
        try {
            while (cursor.hasNext()) {
                Email email = (Email) cursor.next();
                emails.add(email);
            }
        } finally {
            cursor.close();
        }
        return emails;
    }

    public Email getEmailWithId(String id) {
        return (Email)coll.findOne(new BasicDBObject("_id", new ObjectId(id)));
    }

    public List<Email> getEmailsFrom(String author) {
        DBCursor find = coll.find(new BasicDBObject("from",author));
        List<Email> emails = new ArrayList<Email>();
        while(find.hasNext()) {
            Email next = (Email) find.next();
            emails.add(next);
        }
        return emails;
    }
    
    public boolean saveMessage(Email email) throws IOException {

        if (getId(email.getMessageId(), email.getMessageMailingLists()) != null) {
            return false;
        } 
        coll.insert(email);
        if ( email.getInReplyTo() != null) {

            Email parent =(Email)coll.findOne(new ObjectId(email.getInReplyTo()));
            parent.addReply(email.getId());
            coll.save(parent);
        }
         return true;
        }
    
    
     public String getId(String messageId, ArrayList<String> mailinglist) {
        BasicDBObject emailObject = new BasicDBObject("message_id", messageId);
        emailObject.put("mailinglist", mailinglist);
        BasicDBObject findOne = (BasicDBObject) coll.findOne(emailObject);
        if (findOne == null) {
            return null;
        }
        return findOne.getString("_id");
    }

    public List<Email> getMailinglistRoot(String mailinglist) {
        BasicDBObject query= new BasicDBObject();
        query.append(Email.ROOT_MONGO_TAG, "true");
        query.append(Email.MAILINGLIST_MONGO_TAG,mailinglist);
        DBCursor find = coll.find(query);
        List<Email> emails = new ArrayList<Email>();
        while(find.hasNext()) {
            Email next = (Email) find.next();
            emails.add(next);
        }
        return emails;
    }

    public List<Email> getWholePathFromId(String id) {
       Email email= (Email) getEmailWithId(id);
       List<Email> replyPath=new ArrayList();
       if(email == null) {
           return replyPath;
       }
       BasicDBObject query;
       List<String> replyIds=new ArrayList<String>();
       replyIds.addAll(email.getReplies());
       
       replyPath.add(email);
       if(replyIds == null) {
           return replyPath;
       }
       for(int i =0; i< replyIds.size();i++) {
           String reply =replyIds.get(i);
           query=new BasicDBObject("_id",new ObjectId(reply));
           Email replyEmail=(Email)coll.findOne(query);
           replyIds.addAll(replyEmail.getReplies());
           replyPath.add(replyEmail);
       }
       return replyPath;
    }

    public List<String> getMailingLists() {
        return mailingLists;
    }

}
