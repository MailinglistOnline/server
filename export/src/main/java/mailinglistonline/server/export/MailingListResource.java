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

/**
 * The REST interface to provide the processing mailinglists.
 * @author Matej Briškár
 */
@ApplicationScoped
@Path("/mailinglists")
public class MailingListResource {
    @Inject
    DbClient dbClient;

    @GET
    @Path("/all")
    @Produces("application/json")
    public List<Mailinglist> getAllEmails() {
        List<Mailinglist> list =dbClient.getMailingLists();
        return list;

    }
    
    
 
 
}
