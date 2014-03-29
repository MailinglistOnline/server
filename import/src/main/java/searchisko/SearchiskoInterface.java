package searchisko;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import mailinglistonline.server.export.database.entities.Email;

@Path("/v1/rest")
public interface SearchiskoInterface {

		@GET
	    @Path("/content/mlonline_email/{sys_content_id}")
	    @Produces("application/json")
	    public Email getEmailById(@PathParam("sys_content_id") String id);
	    
		
		@POST
	    @Path("/content/mlonline_email/{sys_content_id}")
		@Consumes("application/json")
	    public void sendEmail(Email email, @PathParam("sys_content_id") String id);
		
		@DELETE
	    @Path("/content/mlonline_email/{sys_content_id}")
	    @Produces("application/json")
	    public void deleteEmail(@PathParam("sys_content_id") String id, @QueryParam("ignore_missing") boolean ignoreMissing);
	    
}
