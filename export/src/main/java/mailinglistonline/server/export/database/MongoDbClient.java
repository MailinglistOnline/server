/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailinglistonline.server.export.database;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import mailinglistonline.server.export.database.entities.ContentPart;
import mailinglistonline.server.export.database.entities.Email;
import mailinglistonline.server.export.database.entities.Mailinglist;
import mailinglistonline.server.export.database.entities.MiniEmail;
import mailinglistonline.server.export.searchisko.SearchManager;
import mailinglistonline.server.export.util.PropertiesParser;

import org.bson.types.ObjectId;

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

/**
 * MongoDB implementation of the DbClient interface. Accessing and storing emails in the MongoDB instance.
 * @author Matej Briškár
 */

@Singleton
public class MongoDbClient implements DbClient{
	private static final String MONGODB_FILES_COLLECTION = "fs";
	public static final String DATABASE_PROPERTIES_FILE_NAME = "/database.properties";
	private static String MAILINGLISTS_PROPERTIES_FILE_NAME = "/mailinglists.properties";
	private static String MAIL_IS_ROOT_VALUE = null;

	List<Mailinglist> mailingLists = new ArrayList<Mailinglist>();
	DBCollection coll;
	SearchManager searchManager;
	MongoClient mongoClient;
	DB db;

	public MongoDbClient() throws UnknownHostException, IOException {
		this(PropertiesParser.parseDatabaseConfigurationFile(MongoDbClient.class
				.getResourceAsStream(DATABASE_PROPERTIES_FILE_NAME)));
	}

	public MongoDbClient(DatabaseConfiguration configuration) {
		searchManager = new SearchManager();
		try {
			connect(configuration.getDatabaseUrl(),
					configuration.getDefaultDatabaseName(),
					configuration.getDefaultPort(),
					configuration.getDefaultCollectionName(),
					configuration.getUser(),
					configuration.getPassword());
		} catch (UnknownHostException e) {

		}
		readMailinglists();
	}

	public void readMailinglists() {
		mailingLists = PropertiesParser.parseMailinglistConfigurationFile((MongoDbClient.class
				.getResourceAsStream(MAILINGLISTS_PROPERTIES_FILE_NAME)));
	}

	@Deprecated
	public MongoDbClient(String mongoUrl, String databaseName, int mongoPort,
			String collectionName) throws UnknownHostException {
		// TODO: deprecated. change for the DatabaseConfiguration only
		connect(mongoUrl, databaseName, mongoPort, collectionName, null, null);
	}

	private synchronized void connect(String mongoUrl, String databaseName,
			int mongoPort, String collectionName, String user, String password) throws UnknownHostException {
		mongoClient = new MongoClient(mongoUrl, mongoPort);
		db = mongoClient.getDB(databaseName);
		if(user != null && password !=null) {
			db.authenticate(user, password.toCharArray());
		}
		mongoClient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
		coll = db.getCollection(collectionName);
		coll.setObjectClass(Email.class);
		// MongoDB JAVA-254 issue
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
		coll.setInternalClass(Email.MAIN_CONTENT_MONGO_TAG + ".0",
				ContentPart.class);
		coll.setInternalClass(Email.MAIN_CONTENT_MONGO_TAG + ".1",
				ContentPart.class);
		coll.setInternalClass(Email.MAIN_CONTENT_MONGO_TAG + ".2",
				ContentPart.class);
		coll.setInternalClass(Email.MAIN_CONTENT_MONGO_TAG + ".3",
				ContentPart.class);
		coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".0",
				ContentPart.class);
		coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".1",
				ContentPart.class);
		coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".2",
				ContentPart.class);
		coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".3",
				ContentPart.class);
		coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".4",
				ContentPart.class);
		coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".5",
				ContentPart.class);
		coll.setInternalClass(Email.ATTACHMENTS_MONGO_TAG + ".5",
				ContentPart.class);
	}

	@PreDestroy
	public void closeConnection() {
		mongoClient.close();
		// files db probably also need to be closed
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getFilesColl()
	 */
	public DBCollection getFilesColl() {
		return db.getCollection(MONGODB_FILES_COLLECTION + ".files");
	}

	public DBCollection getColl() {
		return coll;
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#dropTable()
	 */
	@Override
	public synchronized void dropTable() {
		this.coll.drop();
		db.getCollection(MONGODB_FILES_COLLECTION + ".files").drop();
		;
		db.getCollection(MONGODB_FILES_COLLECTION + ".chunks").drop();
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#emailCount()
	 */
	@Override
	public long emailCount() {
		return coll.count();
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#filesCount()
	 */
	@Override
	public synchronized long filesCount() {
		return db.getCollection(MONGODB_FILES_COLLECTION + ".files").count();
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#findFileById(java.lang.String)
	 */
	@Override
	public GridFSDBFile findFileById(String id) {
		GridFS gfs = new GridFS(db, MONGODB_FILES_COLLECTION);
		return gfs.findOne(new ObjectId(id));
	}

	/*
	 * Returns the ID of the object, which has the given message_ID and is in
	 * the same mailinglist
	 */
	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getId(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized String getId(String messageId, String mailinglist) {
		BasicDBObject emailObject = new BasicDBObject(
				Email.MESSAGE_ID_MONGO_TAG, messageId);
		emailObject.put(Email.MAILINGLIST_MONGO_TAG, mailinglist);
		BasicDBObject findOne = (BasicDBObject) coll.findOne(emailObject);
		if (findOne == null) {
			return null;
		}
		return findOne.getString("_id");
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#findFirstMessageWithMessageId(java.lang.String)
	 */
	@Override
	public Email findFirstMessageWithMessageId(String messageId) {
		BasicDBObject idObj = new BasicDBObject(MiniEmail.MESSAGE_ID_MONGO_TAG, messageId);
		return (Email) coll.findOne(idObj);
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getAllEmails()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getEmailWithId(java.lang.String)
	 */
	@Override
	public Email getEmailWithId(String id) {
		return (Email) coll.findOne(new BasicDBObject("_id", new ObjectId(id)));
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getMessage(java.lang.String)
	 */
	@Override
	public synchronized MiniEmail getMessage(String mongoId) {
		return (MiniEmail) coll.findOne(new BasicDBObject("_id", new ObjectId(
				mongoId)));
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#saveMessage(mailinglistonline.server.export.database.entities.Email)
	 */
	@Override
	public synchronized boolean saveMessage(Email email) throws IOException {
		// check if the message is not already saved
		if (getId(email.getMessageId(), email.getMessageMailingList()) != null) {
			if (email.getAttachments() != null) {
				for (ContentPart part : email.getAttachments()) {
					if (part.getLink() != null) {
						deleteFile(part.getLink());
					}
				}
			}
			return false;
		}

		coll.insert(email);
		email.setEmailShardKey(email.getMessageMailingList() + email.getId());
		coll.save(email);
		if (email.getInReplyTo() != null
				&& email.getInReplyTo().getId() != null) {
			Email parent = (Email) coll.findOne(new ObjectId(email
					.getInReplyTo().getId()));
			parent.addReply(email);
			coll.save(parent);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#deleteMessage(mailinglistonline.server.export.database.entities.Email)
	 */
	@Override
	public synchronized boolean deleteMessage(Email email) throws IOException {
		coll.remove(email);
		for (ContentPart cp : email.getAttachments()) {
			if (cp.getLink() != null) {
				deleteFile(cp.getLink());
			}
		}
		if (email.getInReplyTo() != null && email.getInReplyTo() != null) {
			Email parent = (Email) coll.findOne(new ObjectId(email
					.getInReplyTo().getId()));
			parent.removeReply(email);
			coll.save(parent);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#createFile(byte[], java.lang.String, java.lang.String)
	 */
	@Override
	public String createFile(byte[] bytes, String fileName, String contentType) {
		GridFS files = new GridFS(db, MONGODB_FILES_COLLECTION);
		GridFSInputFile gfsFile = files.createFile(bytes);
		if (fileName != null) {
			gfsFile.setFilename(fileName);
		}
		gfsFile.setContentType(contentType);
		gfsFile.save();
		return gfsFile.getId().toString();
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#deleteFile(java.lang.String)
	 */
	@Override
	public void deleteFile(String id) {
		GridFS files = new GridFS(db, MONGODB_FILES_COLLECTION);
		files.remove(new ObjectId(id));
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#updateEmail(mailinglistonline.server.export.database.entities.Email)
	 */
	@Override
	public boolean updateEmail(Email newEmail) throws IOException {
		coll.save(newEmail);
		return true;
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getId(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public String getId(String messageId, ArrayList<String> mailinglist) {
		BasicDBObject emailObject = new BasicDBObject("message_id", messageId);
		emailObject.put("mailinglist", mailinglist);
		BasicDBObject findOne = (BasicDBObject) coll.findOne(emailObject);
		if (findOne == null) {
			return null;
		}
		return findOne.getString("_id");
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getMailinglistRoot(java.lang.Object)
	 */
	@Override
	public List<Email> getMailinglistRoot(Object mailinglist) {
		BasicDBObject query = new BasicDBObject();
		query.append(Email.ROOT_MONGO_TAG, MAIL_IS_ROOT_VALUE);
		query.append(Email.MAILINGLIST_MONGO_TAG, mailinglist);
		DBCursor find = coll.find(query);
		List<Email> emails = new ArrayList<Email>();
		while (find.hasNext()) {
			Email next = (Email) find.next();
			emails.add(next);
		}
		find.close();
		return emails;
	}

	public List<Email> getWholeThreadWithMessage(String id) {
		Email email = (Email) getEmailWithId(id);
		List<Email> replyPath = new ArrayList();
		if (email == null) {
			return replyPath;
		}
		BasicDBObject query;
		List<MiniEmail> replyIds = new ArrayList<MiniEmail>();
		replyIds.addAll(email.getReplies());

		replyPath.add(email);
		if (replyIds == null) {
			return replyPath;
		}
		for (int i = 0; i < replyIds.size(); i++) {
			MiniEmail reply = replyIds.get(i);
			query = new BasicDBObject("_id", new ObjectId(reply.getId()));
			Email replyEmail = (Email) coll.findOne(query);
			replyIds.addAll(replyEmail.getReplies());
			replyPath.add(replyEmail);
		}
		return replyPath;
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getMailingLists()
	 */
	@Override
	public List<Mailinglist> getMailingLists() {
		return mailingLists;
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#addTagToEmail(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean addTagToEmail(String emailId, String tag) {
		Email email = getEmailWithId(emailId);
		if ((email.getTags() != null) && (email.getTags().contains(tag))) {
			return false;
		}
		email.addTag(tag);
		try {
			updateEmail(email);
			return true;
		} catch (IOException e) {

			e.printStackTrace();
		}
		return false;
	}

	public List<Email> getEmailsNotStrictMatch(String mailinglist, String from,
			List<String> tags) {
		BasicDBObject query = new BasicDBObject();
		coll.setObjectClass(Email.class);
		query.append(Email.MAILINGLIST_MONGO_TAG, "/.*" + mailinglist + ".*/");
		query.append(Email.FROM_MONGO_TAG, "/.*" + from + ".*/");
		DBObject inTagObject = new BasicDBObject();
		inTagObject.put("$in", tags);
		query.append(Email.TAGS_MONGO_TAG, inTagObject);
		DBCursor cursor = coll.find(query);
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

	public List<Email> getEmailsFrom(String author) {
		DBCursor find = coll.find(new BasicDBObject("from", author));
		List<Email> emails = new ArrayList<Email>();
		while (find.hasNext()) {
			Email next = (Email) find.next();
			emails.add(next);
		}
		find.close();
		return emails;

	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getEmails(java.lang.String, java.lang.Object, java.util.List, int, java.util.List, java.util.List)
	 */
	@Override
	public List<Email> getEmails(String from, Object mailinglist,
			List<String> tags, int count, List<String> ascending,
			List<String> descending) {
		BasicDBObject query = new BasicDBObject();
		coll.setObjectClass(Email.class);
		if (mailinglist != null && !mailinglist.equals("")) {
			query.append(Email.MAILINGLIST_MONGO_TAG, mailinglist);
		}
		if (from != null && !from.equals("")) {
			query.append(Email.FROM_MONGO_TAG, from);
		}
		if (tags != null && tags.size() != 0) {
			DBObject inTagObject = new BasicDBObject();
			inTagObject.put("$in", tags);
			query.append(Email.TAGS_MONGO_TAG, inTagObject);
		}
		DBCursor cursor = coll.find(query);
		BasicDBObject sortingObject = new BasicDBObject();
		if (ascending != null && ascending.size() != 0) {
			for (String sortParameter : ascending) {
				sortingObject.append(sortParameter, 1);
			}
		}
		if (descending != null && descending.size() != 0) {
			for (String sortParameter : descending) {
				sortingObject.append(sortParameter, -1);
			}
		}
		if (sortingObject.size() != 0) {
			cursor.sort(sortingObject);
		}
		if (count != 0) {
			cursor.limit(count);
		}
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

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#removeTagFromEmail(java.lang.String, java.lang.String)
	 */
	@Override
	public void removeTagFromEmail(String id, String tag) {
		Email email = getEmailWithId(id);
		if ((email.getTags() == null) || (!email.getTags().contains(tag))) {
			return;
		}
		email.removeTag(tag);
		try {
			updateEmail(email);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public List<Email> getMailinglistRoot(Object mailinglist, int fromNumber,
			int toNumber) {
		BasicDBObject query = new BasicDBObject();
		query.append(Email.ROOT_MONGO_TAG, MAIL_IS_ROOT_VALUE);
		query.append(Email.MAILINGLIST_MONGO_TAG, mailinglist);
		DBCursor find = coll.find(query);
		find.skip(fromNumber);
		find.limit(toNumber-fromNumber);
		List<Email> emails = new ArrayList<Email>();
		while (find.hasNext()) {
			Email next = (Email) find.next();
			emails.add(next);
		}
		find.close();
		return emails;
	}
	
	public int getMailinglistRootCount(String mailinglist) {
		// should be refactored to cursor.count()
		List<Email> mailinglistRoot = getMailinglistRoot(mailinglist);
		return mailinglistRoot.size();
	};

}
