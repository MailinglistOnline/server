
import database.DbClient;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matej
 */
@ApplicationScoped
@Path("/mailinglists")
public class MailingListResource {

    
    
    public MailingListResource() throws UnknownHostException, IOException {
        dbClient = new DbClient();
    }
    @Inject
    DbClient dbClient;

    
    @GET
    @Path("/all")
    @Produces("application/xml")
    @Wrapped(element="mailinglists")
    public MailingListWrapper getAllEmails() {
        List<String> list =dbClient.getMailingLists();
       MailingListWrapper wrapper= new MailingListWrapper();
       wrapper.setMailinglists(list);
        return wrapper;

    }
    
    
 
 
}
