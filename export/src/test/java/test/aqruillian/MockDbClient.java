package test.aqruillian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Singleton;
import javax.enterprise.inject.Alternative;

import org.bson.types.ObjectId;

import mailinglistonline.server.export.database.DbClient;
import mailinglistonline.server.export.database.entities.Email;
import mailinglistonline.server.export.database.entities.Mailinglist;
import mailinglistonline.server.export.database.entities.MiniEmail;

import com.mongodb.gridfs.GridFSDBFile;

@Alternative
@Singleton
public class MockDbClient implements DbClient{
	List<Mailinglist> mailingLists = new ArrayList<Mailinglist>();
	GridFSDBFile alwaysReturnedFile = new GridFSDBFile();
	
	public MockDbClient()  {
	}
	
	private Email createSimpleEmail() {
		Email alwaysReturnedEmail = new Email();
		alwaysReturnedEmail.put("_id",new ObjectId("4edd40c86762e0fb12000003"));
		alwaysReturnedEmail.setFrom("test");
		return alwaysReturnedEmail;
	}

	@Override
	public synchronized void dropTable() {
	}
	
	@Override
	public long emailCount() {
		return 1;
	}

	@Override
	public synchronized long filesCount() {
		return 1;
	}

	@Override
	public GridFSDBFile findFileById(String id) {
		GridFSDBFile file = new GridFSDBFile();
		return file;
	}

	@Override
	public synchronized String getId(String messageId, String mailinglist) {
		return "1";
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#findFirstMessageWithMessageId(java.lang.String)
	 */
	@Override
	public Email findFirstMessageWithMessageId(String messageId) {
		return createSimpleEmail();
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getAllEmails()
	 */
	@Override
	public List<Email> getAllEmails() {
		return Collections.singletonList(createSimpleEmail());
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getEmailWithId(java.lang.String)
	 */
	@Override
	public Email getEmailWithId(String id) {
		return createSimpleEmail();
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getMessage(java.lang.String)
	 */
	@Override
	public synchronized MiniEmail getMessage(String mongoId) {
		return createSimpleEmail();
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#saveMessage(mailinglistonline.server.export.database.entities.Email)
	 */
	@Override
	public synchronized boolean saveMessage(Email email) throws IOException {
		return true;
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#deleteMessage(mailinglistonline.server.export.database.entities.Email)
	 */
	@Override
	public synchronized boolean deleteMessage(Email email) throws IOException {
		return true;
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#createFile(byte[], java.lang.String, java.lang.String)
	 */
	@Override
	public String createFile(byte[] bytes, String fileName, String contentType) {
		return "file";
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#deleteFile(java.lang.String)
	 */
	@Override
	public void deleteFile(String id) {
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#updateEmail(mailinglistonline.server.export.database.entities.Email)
	 */
	@Override
	public boolean updateEmail(Email newEmail) throws IOException {
		return true;
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getId(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public String getId(String messageId, ArrayList<String> mailinglist) {
		return createSimpleEmail().getId();
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getMailinglistRoot(java.lang.Object)
	 */
	@Override
	public List<Email> getMailinglistRoot(Object mailinglist) {
		return Collections.singletonList(createSimpleEmail());
	}

	public List<Email> getWholeThreadWithMessage(String id) {
		return Collections.singletonList(createSimpleEmail());
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
	public void addTagToEmail(String emailId, String tag) {
	}

	public List<Email> getEmailsNotStrictMatch(String mailinglist, String from,
			List<String> tags) {
		return Collections.singletonList(createSimpleEmail());
	}

	public List<Email> getEmailsFrom(String author) {
		return Collections.singletonList(createSimpleEmail());

	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#getEmails(java.lang.String, java.lang.Object, java.util.List, int, java.util.List, java.util.List)
	 */
	@Override
	public List<Email> getEmails(String from, Object mailinglist,
			List<String> tags, int count, List<String> ascending,
			List<String> descending) {
		return Collections.singletonList(createSimpleEmail());
	}

	/* (non-Javadoc)
	 * @see mailinglistonline.server.export.database.DbClient#removeTagFromEmail(java.lang.String, java.lang.String)
	 */
	@Override
	public void removeTagFromEmail(String id, String tag) {
	}

}