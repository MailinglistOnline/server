/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailinglistonline.server.export.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.faces.bean.ApplicationScoped;
import javax.mail.MessagingException;

import mailinglistonline.server.export.database.entities.ContentPart;
import mailinglistonline.server.export.database.entities.Email;
import mailinglistonline.server.export.database.entities.Mailinglist;
import mailinglistonline.server.export.database.entities.MiniEmail;
import mailinglistonline.server.export.searchisko.SearchManager;
import mailinglistonline.server.export.searchisko.SearchiskoResponseParser;

import org.bson.types.ObjectId;

/**
 *
 * @author matej
 */

@Singleton
public class DbClient {
    private static String DATABASE_PROPERTIES_FILE_NAME = "database.properties";
    private static String MAILINGLISTS_PROPERTIES_FILE_NAME = "mailinglists.properties";

    List<Mailinglist> mailingLists = new ArrayList<Mailinglist>();
    DBCollection coll;
    SearchManager searchManager;
    MongoClient mongoClient;

    public DbClient() throws UnknownHostException, IOException {
        Properties prop = new Properties();
        searchManager = new SearchManager();
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
        	Mailinglist mlist = new Mailinglist();
        	mlist.setName(mailinglist);
        	String description = prop.getProperty("mailinglist.description." + i);
        	mlist.setDescription(description);
            mailingLists.add(mlist);
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
         coll.setInternalClass(Email.IN_REPLY_TO_MONGO_TAG, MiniEmail.class);
         coll.setInternalClass(Email.ROOT_MONGO_TAG, MiniEmail.class);
         coll.setInternalClass(Email.REPLIES_MONGO_TAG + ".0", MiniEmail.class);
         coll.setInternalClass(Email.REPLIES_MONGO_TAG + ".1", MiniEmail.class);
         coll.setInternalClass(Email.REPLIES_MONGO_TAG + ".2", MiniEmail.class);
         coll.setInternalClass(Email.REPLIES_MONGO_TAG + ".3", MiniEmail.class);
         coll.setInternalClass(Email.REPLIES_MONGO_TAG + ".4", MiniEmail.class);
         coll.setInternalClass(Email.REPLIES_MONGO_TAG + ".5", MiniEmail.class);
         coll.setInternalClass(Email.REPLIES_MONGO_TAG + ".6", MiniEmail.class);
         coll.setInternalClass(Email.REPLIES_MONGO_TAG + ".7", MiniEmail.class);
         coll.setInternalClass(Email.REPLIES_MONGO_TAG + ".8", MiniEmail.class);
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
    
    @PreDestroy
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

    public List<MiniEmail> getAllEmails() {
        coll.setObjectClass(Email.class);
        DBCursor cursor = coll.find();
        List<MiniEmail> emails = new ArrayList<MiniEmail>();
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

    public List<MiniEmail> getEmailsFrom(String author) {
        DBCursor find = coll.find(new BasicDBObject("from",author));
        List<MiniEmail> emails = new ArrayList<MiniEmail>();
        while(find.hasNext()) {
            Email next = (Email) find.next();
            emails.add(next);
        }
        return emails;
    }
    
  
    public boolean updateEmail(Email newEmail) throws IOException {

        coll.save(newEmail);

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

    public List<MiniEmail> getMailinglistRoot(String mailinglist) {
        BasicDBObject query= new BasicDBObject();
        query.append(Email.ROOT_MONGO_TAG, "true");
        query.append(Email.MAILINGLIST_MONGO_TAG,mailinglist);
        DBCursor find = coll.find(query);
        List<MiniEmail> emails = new ArrayList<MiniEmail>();
        while(find.hasNext()) {
            Email next = (Email) find.next();
            emails.add(next);
        }
        return emails;
    }

    public List<MiniEmail> getWholeThreadWithMessage(String id) {
       Email email= (Email) getEmailWithId(id);
       List<MiniEmail> replyPath=new ArrayList();
       if(email == null) {
           return replyPath;
       }
       BasicDBObject query;
       List<MiniEmail> replyIds=new ArrayList<MiniEmail>();
       replyIds.addAll(email.getReplies());
       
       replyPath.add(email);
       if(replyIds == null) {
           return replyPath;
       }
       for(int i =0; i< replyIds.size();i++) {
           MiniEmail reply =replyIds.get(i);
           query=new BasicDBObject("_id",new ObjectId(reply.getId()));
           Email replyEmail=(Email)coll.findOne(query);
           replyIds.addAll(replyEmail.getReplies());
           replyPath.add(replyEmail);
       }
       return replyPath;
    }

    public List<Mailinglist> getMailingLists() {
        return mailingLists;
    }

   /* Should not be needed as the MiniEmail replies are now stored inside the email
    *  public List<Email> getEmailReplies(String id) {
        Email email = getEmailWithId(id);
        List<Email> result = new ArrayList<Email>();
        for (String replyId : email.getReplies()) {
            result.add(getEmailWithId(replyId));
        }
        return result;
    }*/
    
    public void addTagToEmail(String emailId, String tag) {
		Email email = getEmailWithId(emailId);
		email.addTag(tag);
		try {
			updateEmail(email);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
    
    public List<Email> getMailinglistLatest(String mailinglist, int number) {
		BasicDBObject query= new BasicDBObject();
		coll.setObjectClass(Email.class);
		query.append(Email.MAILINGLIST_MONGO_TAG,mailinglist);
		DBObject orderBy = new BasicDBObject();
		orderBy.put(Email.DATE_MONGO_TAG, -1);
        DBCursor cursor = coll.find(query).sort(orderBy);
        List<Email> emails = new ArrayList<Email>();
        try {
            while (cursor.hasNext() && number >0) {
                Email email = (Email) cursor.next();
                emails.add(email);
                number = number -1;
            }
        } finally {
            cursor.close();
        }
        return emails;
	}
    

    

    public List<MiniEmail> searchByContent(String content) {
    	List<MiniEmail> emails = searchManager.searchEmailByContent(content);
    	return emails;
    }

	public List<Email> getEmailsFromAddress(String from) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Email> getEmailsNotStrictMatch(String mailinglist, String from, List<String> tags) {
		BasicDBObject query= new BasicDBObject();
		coll.setObjectClass(Email.class);
		query.append(Email.MAILINGLIST_MONGO_TAG,"/.*"+mailinglist+".*/");
		query.append(Email.FROM_MONGO_TAG,"/.*"+from+".*/");
		DBObject inTagObject = new BasicDBObject();
		inTagObject.put("$in", tags);
		query.append(Email.TAGS_MONGO_TAG,inTagObject);
        DBCursor cursor = coll.find(query);
        List<Email> emails = new ArrayList<Email>();
        try {
            while (cursor.hasNext() ) {
                Email email = (Email) cursor.next();
                emails.add(email);
            }
        } finally {
            cursor.close();
        }
        return emails;
	}

	

	

}
