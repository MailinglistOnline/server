/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailinglist;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import mailinglist.entities.ContentPart;
import mailinglist.entities.Email;
import mailinglist.entities.MiniEmail;

import org.bson.types.ObjectId;

/**
 * DbClient responsible for the communication with the 
 * @author matej
 */
public class DbClient {

	// fs is default also
    private static final String MONGODB_FILES_COLLECTION = "fs";

	private static String DATABASE_PROPERTIES_FILE_NAME = "database.properties";
    
    List<String> mailingLists;
    MongoClient mongoClient;
    DBCollection coll;
    DB db;

    public DbClient() throws UnknownHostException, IOException {
        Properties prop = new Properties();
        prop.load(DbClient.class.getClassLoader().getResourceAsStream((DATABASE_PROPERTIES_FILE_NAME)));
        Integer defaultPort = Integer.valueOf(prop.getProperty("defaultMongoPort"));
        String databaseUrl = prop.getProperty("defaultMongoUrl");
        String defaultDatabaseName = prop.getProperty("defaultDatabaseName");
        String defaultCollectionName = prop.getProperty("defaultCollection");
        connect(databaseUrl, defaultDatabaseName, defaultPort, defaultCollectionName);

    }

    public DbClient(String mongoUrl, String databaseName, int mongoPort, String collectionName) throws UnknownHostException {
        connect(mongoUrl, databaseName, mongoPort, collectionName);
    }
    
    public void closeConnection() {
        mongoClient.close();
    }

    private synchronized void connect(String mongoUrl, String databaseName, int mongoPort, String collectionName) throws UnknownHostException {
    	mongoClient = new MongoClient(mongoUrl, mongoPort);
        db = mongoClient.getDB(databaseName);
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

    public synchronized boolean saveMessage(Email email) throws IOException {
    	// check if the message is not already saved
        if (getId(email.getMessageId(), email.getMessageMailingList()) != null) {
        	if (email.getAttachments() !=null) {
        		for(ContentPart part: email.getAttachments()) {
        			if(part.getLink() !=null) {
        				deleteFile(part.getLink());
        			}
        		}
        	}
            return false;
        } 
        
        
        coll.insert(email);
        if ( email.getInReplyTo() != null && email.getInReplyTo().getId()!=null) {
            Email parent =(Email)coll.findOne(new ObjectId(email.getInReplyTo().getId()));
            parent.addReply(email);
            coll.save(parent);
        }
         return true;
    }
    
    public String createFile(byte[] bytes, String fileName, String contentType) {
    	GridFS files = new GridFS(db, MONGODB_FILES_COLLECTION);
    	GridFSInputFile gfsFile =files.createFile(bytes);
    	if(fileName!=null) {
    		gfsFile.setFilename(fileName);
    	}
    	gfsFile.setContentType(contentType);
    	gfsFile.save();
    	return gfsFile.getId().toString();
    }
    public void deleteFile(String id) {
    	GridFS files = new GridFS(db, MONGODB_FILES_COLLECTION);
    	files.remove(new ObjectId(id));
    }
    
    public synchronized boolean deleteMessage(Email email) throws IOException {
        coll.remove(email);
        for(ContentPart cp : email.getAttachments()) {
        	if(cp.getLink() != null) {
        		deleteFile(cp.getLink());
        	}
        }
        if ( email.getInReplyTo() != null && email.getInReplyTo() !=null) {
            Email parent =(Email)coll.findOne(new ObjectId(email.getInReplyTo().getId()));
            parent.removeReply(email);
            coll.save(parent);
        }
         return true;
        }
       
    public DBCollection getColl() {
        return coll;
    }
    
    public DBCollection getFilesColl() {
        return db.getCollection(MONGODB_FILES_COLLECTION + ".files");
    }
    
    public synchronized MiniEmail getMessage(String mongoId) {
        return (MiniEmail) coll.findOne(new BasicDBObject("_id",new ObjectId(mongoId)));
    }


    public synchronized void dropTable() {
        this.coll.drop();
        db.getCollection(MONGODB_FILES_COLLECTION + ".files").drop();;
        db.getCollection(MONGODB_FILES_COLLECTION + ".chunks").drop();
    }

    public synchronized long emailCount() {
        return coll.count();
    }
    

    public synchronized long filesCount() {
        return db.getCollection(MONGODB_FILES_COLLECTION + ".files").count();
    }
    
    public GridFSDBFile findFileById(String id) {
    	GridFS gfs = new GridFS(db, MONGODB_FILES_COLLECTION);
    	return gfs.findOne(new ObjectId(id));
    }

    public synchronized DBObject findFirstMessageWithMessageId(String messageId) {
        BasicDBObject idObj = new BasicDBObject("message_id", messageId);
        return coll.findOne(idObj);

    }

    // should not be used anymore
    public synchronized List<Email> getAllEmails() {
        coll.setObjectClass(Email.class);
         
        DBCursor cursor = coll.find();
        List<Email> objects = new ArrayList<Email>();
        try {
            while (cursor.hasNext()) {
                objects.add((Email) cursor.next());
            }
        } finally {
            cursor.close();
        }
        return objects;
    }
    
    /*
     * Returns the ID of the object, which has the given message_ID and is in the same mailinglist
     */
    public synchronized String getId(String messageId, String mailinglist) {
        BasicDBObject emailObject = new BasicDBObject(Email.MESSAGE_ID_MONGO_TAG, messageId);
        emailObject.put(Email.MAILINGLIST_MONGO_TAG, mailinglist);
        BasicDBObject findOne = (BasicDBObject) coll.findOne(emailObject);
        if (findOne == null) {
            return null;
        }
        return findOne.getString("_id");
    }



}