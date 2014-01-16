package searchisko;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import mailinglist.entities.Email;

@Path("/emails")
public interface SearchiskoEndpoint {

		@GET
	    @Path("/content/mailing_list_message/{sys_content_id}")
	    @Produces("application/json")
	    public Email getEmailById(@PathParam("sys_content_id") String id);
	    
		
		@POST
	    @Path("/content/mailing_list_message/{sys_content_id}")
	    @Produces("application/json")
	    public boolean sendEmail(@PathParam("sys_content_id") String id, Email email);
		
		@DELETE
	    @Path("/content/mailing_list_message/{sys_content_id}")
	    @Produces("application/json")
	    public boolean deleteEmail(@PathParam("sys_content_id") String id, @QueryParam("ignore_missing") boolean ignoreMissing);
	    
	    @GET
	    @Path("/replies/id")
	    @Produces("application/xml")
	    @Wrapped(element="emails")
	    public List<Email> getEmailReplies(@QueryParam("id") String id);
	    
	    @GET
	    @Path("/from")
	    @Produces("application/xml")
	    @Wrapped(element="emails")
	    public List<Email> getEmailByAuthor(@QueryParam("from") String author);

	    @GET
	    @Path("/mailinglist/roots/all")
	    @Produces("application/xml")
	    @Wrapped(element="emails")
	    public List<Email> getMailingListRoots(@QueryParam("mailinglist") String mailinglist);
	    
	    @GET
	    @Path("/mailinglist/roots/")
	    @Produces("application/xml")
	    @Wrapped(element="emails")
	    public List<Email> getMailingListRoots(@QueryParam("from") int fromNumber,@QueryParam("to") int toNumber,@QueryParam("mailinglist") String mailinglist);
	    
	    
	    @GET
	    @Path("/thread/")
	    @Produces("application/xml")
	    @Wrapped(element="emails")
	    public List<Email> getEmailPath(@QueryParam("id") String id);
	    
	    @GET
	    @Path("/mailinglist/latest/")
	    @Produces("application/xml")
	    @Wrapped(element="emails")
	    public List<Email> getMailinglistLatest(@QueryParam("mailinglist") String mailinglist, @QueryParam("number") int number);
	 

		@POST
	    @Path("/email/tag/")
	    public void addTag(@QueryParam("id") String id,@QueryParam("tag") String tag);


	
	
}
