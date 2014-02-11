package mailinglistonline.server.export;

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

import mailinglistonline.server.export.database.DbClient;
import mailinglistonline.server.export.database.entities.Mailinglist;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matej Briškár
 */
@ApplicationScoped
@Path("/mailinglists")
public class MailingListResource {
    @Inject
    DbClient dbClient;
    
    
    public MailingListResource() {
    }

    @GET
    @Path("/all")
    @Produces("application/xml")
    @Wrapped(element="mailinglists")
    public List<Mailinglist> getAllEmails() {
        List<Mailinglist> list =dbClient.getMailingLists();
        return list;

    }
    
    
 
 
}
