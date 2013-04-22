    
import database.DbClient;
import database.entities.Email;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.RequestScoped;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matej
 */

@RequestScoped
@Path("/emails")
public class EmailResource {
    
    @Inject
    DbClient dbClient;
    
    public EmailResource() throws UnknownHostException, IOException {
        dbClient = new DbClient();
    }
    
    @GET
    @Path("/all")
    @Produces("application/xml")
    @Wrapped(element="emails")
    public List<Email> getAllEmails() {
        return dbClient.getAllEmails();
    }
    
     @GET
    @Path("/email")
    @Produces("application/xml")
   // @Wrapped(element="emails")
    public Email getEmailById(@QueryParam("id") String id) {
         ArrayList<Email> list= new ArrayList<Email>();
         return dbClient.getEmailWithId(id);

    }
    
    @GET
    @Path("/email")
    @Produces("application/xml")
    @Wrapped(element="emails")
    public List<Email> getEmailByAuthor(@QueryParam("from") String author) {
        return dbClient.getEmailsFrom(author);
         
    }
    
    @GET
    @Path("/mailinglist/roots/all")
    @Produces("application/xml")
    @Wrapped(element="emails")
    public List<Email> getMailingListRoots(@QueryParam("mailinglist") String mailinglist) {
        return dbClient.getMailinglistRoot(mailinglist);
         
    }
    
    @GET
    @Path("/mailinglist/roots/")
    @Produces("application/xml")
    @Wrapped(element="emails")
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
    @Produces("application/xml")
    @Wrapped(element="emails")
    public List<Email> getEmailPath(@QueryParam("id") String id) {
        List<Email> list=dbClient.getWholePathFromId(id);
        return list;
         
    }
    
 
}
