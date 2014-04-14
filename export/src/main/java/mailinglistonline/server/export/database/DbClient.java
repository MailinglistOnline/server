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
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

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
import mailinglistonline.server.export.searchisko.SearchiskoConfiguration;
import mailinglistonline.server.export.searchisko.SearchiskoResponseParser;

import org.bson.types.ObjectId;

/**
 * 
 * @author Matej Briškár
 */

@Singleton
public class DbClient {
	private static final String MONGODB_FILES_COLLECTION = "fs";
	private static String DATABASE_PROPERTIES_FILE_NAME = "database.properties";
	private static String MAILINGLISTS_PROPERTIES_FILE_NAME = "mailinglists.properties";
	private static String MAIL_IS_ROOT_VALUE = null;

	List<Mailinglist> mailingLists = new ArrayList<Mailinglist>();
	DBCollection coll;
	SearchManager searchManager;
	MongoClient mongoClient;
	DB db;

	public DbClient() throws UnknownHostException, IOException {
		this(
				new DatabaseConfiguration()
						.readFromConfigurationFile(DbClient.class
								.getClassLoader()
								.getResource((DATABASE_PROPERTIES_FILE_NAME))
								.getPath()));
	}

	public DbClient(DatabaseConfiguration configuration) {
		searchManager = new SearchManager();
		try {
			connect(configuration.getDatabaseUrl(),
					configuration.getDefaultDatabaseName(),
					configuration.getDefaultPort(),
					configuration.getDefaultCollectionName());
		} catch (UnknownHostException e) {

		}
		readMailinglists();
	}

	public void readMailinglists() {
		Properties prop = new Properties();
		try {
			prop.load(DbClient.class.getClassLoader().getResourceAsStream(
					(MAILINGLISTS_PROPERTIES_FILE_NAME)));
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Unable to read mailinglist property file.");
		}
		String mailinglist = prop.getProperty("mailinglist." + 1);
		int i = 1;
		while (mailinglist != null) {
			Mailinglist mlist = new Mailinglist();
			mlist.setName(mailinglist);
			String description = prop.getProperty("mailinglist.description."
					+ i);
			mlist.setDescription(description);
			mailingLists.add(mlist);
			i++;
			mailinglist = prop.getProperty("mailinglist." + i);
		}

	}

	@Deprecated
	public DbClient(String mongoUrl, String databaseName, int mongoPort,
			String collectionName) throws UnknownHostException {
		// TODO: deprecated. change for the DatabaseConfiguration only
		connect(mongoUrl, databaseName, mongoPort, collectionName);
	}

	private synchronized void connect(String mongoUrl, String databaseName,
			int mongoPort, String collectionName) throws UnknownHostException {
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

	public DBCollection getFilesColl() {
		return db.getCollection(MONGODB_FILES_COLLECTION + ".files");
	}

	public DBCollection getColl() {
		return coll;
	}

	public synchronized void dropTable() {
		this.coll.drop();
		db.getCollection(MONGODB_FILES_COLLECTION + ".files").drop();
		;
		db.getCollection(MONGODB_FILES_COLLECTION + ".chunks").drop();
	}

	public long emailCount() {
		return coll.count();
	}

	public synchronized long filesCount() {
		return db.getCollection(MONGODB_FILES_COLLECTION + ".files").count();
	}

	public GridFSDBFile findFileById(String id) {
		GridFS gfs = new GridFS(db, MONGODB_FILES_COLLECTION);
		return gfs.findOne(new ObjectId(id));
	}

	/*
	 * Returns the ID of the object, which has the given message_ID and is in
	 * the same mailinglist
	 */
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
		return (Email) coll.findOne(new BasicDBObject("_id", new ObjectId(id)));
	}

	public synchronized MiniEmail getMessage(String mongoId) {
		return (MiniEmail) coll.findOne(new BasicDBObject("_id", new ObjectId(
				mongoId)));
	}

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

	public void deleteFile(String id) {
		GridFS files = new GridFS(db, MONGODB_FILES_COLLECTION);
		files.remove(new ObjectId(id));
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

	public List<Mailinglist> getMailingLists() {
		return mailingLists;
	}

	/*
	 * Should not be needed as the MiniEmail replies are now stored inside the
	 * email public List<Email> getEmailReplies(String id) { Email email =
	 * getEmailWithId(id); List<Email> result = new ArrayList<Email>(); for
	 * (String replyId : email.getReplies()) {
	 * result.add(getEmailWithId(replyId)); } return result; }
	 */

	public void addTagToEmail(String emailId, String tag) {
		Email email = getEmailWithId(emailId);
		if ((email.getTags() != null) && (email.getTags().contains(tag))) {
			return;
		}
		email.addTag(tag);
		try {
			updateEmail(email);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public List<MiniEmail> searchByContent(String content) {
		List<MiniEmail> emails = searchManager.searchEmailByContent(content);
		return emails;
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

}
