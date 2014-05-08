package mailinglistonline.server.export;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import mailinglistonline.server.export.database.DbClient;
import mailinglistonline.server.export.database.MongoDbClient;
import mailinglistonline.server.export.database.SearchClient;
import mailinglistonline.server.export.database.entities.Email;
import mailinglistonline.server.export.database.entities.MiniEmail;

import org.bson.types.ObjectId;

/**
 * 
 * @author Matej Briškár
 */

@ApplicationScoped
@Path("/emails")
public class EmailResource {

	private static final String ALL_REGEX = ".*";
	private static final String MAILINGLIST_REPRESENTING_ALL = "all";
	@Inject
	DbClient dbClient;
	@Inject
	SearchClient searchClient;

	public EmailResource() throws UnknownHostException, IOException {

	}

	@GET
	@Path("/all")
	@Produces("application/json")
	public List<Email> getAllEmails() {
		return normalizeIds(dbClient.getAllEmails());
	}

	// should be deleted after merge to only one method
	@GET
	@Path("/email/{id}")
	@Produces("application/json")
	public Email getEmailById(@PathParam("id") String id) {
		Email email = dbClient.getEmailWithId(id);
		if(email == null) {
			return null;
		}
		ObjectId objectId = (ObjectId) email.get("_id");
		email.setId(objectId.toStringMongod());
		return email;

	}

	@GET
	@Path("")
	@Produces("application/json")
	public List<Email> getEmails(
			@QueryParam("from") String author,
			@QueryParam("mailinglist") String mailinglist,
			@QueryParam("tags") List<String> tags,
			@QueryParam("count") int count,
			@QueryParam("descending") List<String> descending,
			@QueryParam("ascending") List<String> ascending) {
		
		Object mailinglistObject;
		if (MAILINGLIST_REPRESENTING_ALL.equals(mailinglist)) {
			mailinglistObject=Pattern.compile(ALL_REGEX);
		} else {
			mailinglistObject=mailinglist;
		}
		return normalizeIds(dbClient.getEmails(author, mailinglistObject, tags,count,ascending,descending));
	}

	@GET
	@Path("/{mailinglist}/roots/all")
	@Produces("application/json")
	public List<Email> getMailingListRoots(
			@PathParam("mailinglist") String mailinglist) {
		if (MAILINGLIST_REPRESENTING_ALL.equals(mailinglist)) {
			return normalizeIds(dbClient.getMailinglistRoot(Pattern
					.compile(ALL_REGEX)));
		} else {
			return normalizeIds(dbClient.getMailinglistRoot(mailinglist));
		}

	}

	@GET
	@Path("/{mailinglist}/roots/")
	@Produces("application/json")
	public List<Email> getMailingListRoots(@QueryParam("from") int fromNumber,
			@QueryParam("to") int toNumber,
			@PathParam("mailinglist") String mailinglist) {
		if(toNumber == 0) {
			return getMailingListRoots(mailinglist);
		}
		List<Email> list = normalizeIds(dbClient
				.getMailinglistRoot(mailinglist));
		if (fromNumber > toNumber) {
			return new ArrayList<Email>();
		}
		if (toNumber > list.size() - 1) {
			toNumber = list.size();
		}
		return list.subList(fromNumber, toNumber);

	}

	@GET
	@Path("/thread/")
	@Produces("application/json")
	public List<Email> getEmailPath(@QueryParam("id") String id) {
		List<Email> list = normalizeIds(dbClient.getWholeThreadWithMessage(id));
		return list;

	}

	/*@GET
	@Path("/email/")
	@Produces("application/json")
	public List<Email> getEmails(@QueryParam("mailinglist") String mailinglist,
			@QueryParam("from") String from, @QueryParam("tag") List<String> tag) {
		return normalizeIds(dbClient.getEmailsNotStrictMatch(mailinglist, from,
				tag));
	}*/

	@POST
	@Path("/email/tag/")
	public void addTag(@QueryParam("id") String id,
			@QueryParam("tag") String tag) {
		dbClient.addTagToEmail(id, tag);
	}
	
	@DELETE
	@Path("/email/tag/")
	public void removeTag(@QueryParam("id") String id,
			@QueryParam("tag") String tag) {
		dbClient.removeTagFromEmail(id, tag);
	}

	// NOW COMES THE SEARCH METHODS:
	@GET
	@Path("/search/content")
	@Produces("application/json")
	public List<MiniEmail> searchEmailByContent(
			@QueryParam("content") String content) {
		// TODO: when used searchisko filtering, do not forget for the all
		List<MiniEmail> emails = searchClient.searchByContent(content);
		return emails;
	}

	/*
	 * When being parsed to json, the parser does not look on the get methods,
	 * it is behaving differently because MiniEmails extends Map. No need to
	 * send 4 objects in the id field.
	 */
	private List<Email> normalizeIds(List<Email> miniEmails) {
		for (MiniEmail email : miniEmails) {
			ObjectId id = (ObjectId) email.get("_id");
			email.setId(id.toStringMongod());
		}
		return miniEmails;
	}

}
