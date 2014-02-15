package mailinglistonline.server.export;
    
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import mailinglistonline.server.export.database.DbClient;
import mailinglistonline.server.export.database.entities.Email;
import mailinglistonline.server.export.database.entities.MiniEmail;

//import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matej Briškár
 */

@RequestScoped
@Path("/emails")
public class EmailResource {
    
    @Inject
    DbClient dbClient;
    
    public EmailResource() throws UnknownHostException, IOException {
        
    }
    
    @GET
    @Path("/all")
    @Produces("application/json")
    //@Wrapped(element="emails")
    public List<Email> getAllEmails() {
        return dbClient.getAllEmails();
    }
    
    @GET
    @Path("/email/id")
    @Produces("application/json")
    //@Wrapped(element="emails")
    public Email getEmailById(@QueryParam("id") String id) {
         return dbClient.getEmailWithId(id);

    }
     
    /* Should not be needed anymore because the replies are in the emails now
     
    @GET
    @Path("/replies/id")
    @Produces("application/xml")
    @Wrapped(element="emails")
    public List<Email> getEmailReplies(@QueryParam("id") String id) {
         ArrayList<Email> list= new ArrayList<Email>();
         return dbClient.getEmailReplies(id);
    }
    */
    
    @GET
    @Path("/from")
    @Produces("application/json")
    //@Wrapped(element="emails")
    public List<Email> getEmailByAuthor(@QueryParam("from") String author) {
        return dbClient.getEmailsFrom(author);
         
    }
    
    @GET
    @Path("/mailinglist/roots/all")
    @Produces("application/json")
    //@Wrapped(element="emails")
    public List<Email> getMailingListRoots(@QueryParam("mailinglist") String mailinglist) {
        return dbClient.getMailinglistRoot(mailinglist);
         
    }
    
    @GET
    @Path("/mailinglist/roots/")
    @Produces("application/json")
    //@Wrapped(element="emails")
    public List<Email> getMailingListRoots(@QueryParam("from") int fromNumber,@QueryParam("to") int toNumber,@QueryParam("mailinglist") String mailinglist) {
        List<Email> list=dbClient.getMailinglistRoot(mailinglist);
        if(fromNumber > toNumber) {
            return new ArrayList<Email>();
        }
        if(toNumber>list.size()-1) {
            toNumber=list.size();
        }
        return list.subList(fromNumber, toNumber);
         
    }
    
    
    @GET
    @Path("/thread/")
    @Produces("application/json")
    //@Wrapped(element="emails")
    public List<Email> getEmailPath(@QueryParam("id") String id) {
        List<Email> list=dbClient.getWholeThreadWithMessage(id);
        return list;
         
    }
    
    @GET
    @Path("/from/")
    @Produces("application/json")
    //@Wrapped(element="emails")
    public List<Email> getEmailsFromAddress(@QueryParam("from") String from) {
        List<Email> list=dbClient.getEmailsFromAddress(from);
        return list;
    }
    
    @GET
    @Path("/mailinglist/latest/")
    @Produces("application/json")
    //@Wrapped(element="emails")
    public List<Email> getMailinglistLatest(@QueryParam("mailinglist") String mailinglist, @QueryParam("number") int number) {
    	return dbClient.getMailinglistLatest(mailinglist, number);
    }
    
    @GET
    @Path("/email/")
    @Produces("application/json")
	public List<Email> getEmails(@QueryParam("mailinglist") String mailinglist, @QueryParam("from") String from,
			@QueryParam("tag") List<String> tag)
    {
    	return dbClient.getEmailsNotStrictMatch(mailinglist,from,tag);
    }
    
    @POST
    @Path("/email/tag/")
    public void addTag(@QueryParam("id") String id,@QueryParam("tag") String tag) {
    	dbClient.addTagToEmail(id,tag);
    }
    
    // NOW COMES THE SEARCH  METHODS:
    @GET
    @Path("/search/content")
    @Produces("application/json")
    //@Wrapped(element="emails")
    public List<MiniEmail> searchEmailByContent(@QueryParam("content") String content) {
    	return dbClient.searchByContent(content);
    }
    
    
 
}
