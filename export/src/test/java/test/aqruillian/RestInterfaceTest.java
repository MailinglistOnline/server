package test.aqruillian;

import java.io.File;
import java.net.URL;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import mailinglistonline.server.export.EmailResource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.cdi.beans.BeansDescriptor;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RestInterfaceTest {

	private static final String RESOURCE_PREFIX = "rest/"
			+ EmailResource.class.getAnnotation(Path.class).value()
					.substring(1);
	private static final String WEBXML_SRC = "src/main/webapp/WEB-INF/web.xml";

	@Deployment(testable = false)
	public static WebArchive createDeployment() {
		BeansDescriptor beansXml = Descriptors.create(BeansDescriptor.class);
		beansXml.alternativeClass(MockDbClient.class);
		WebArchive webArchive = ShrinkWrap
				.create(WebArchive.class, "test.war")
				.setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
				.addPackages(true, "mailinglistonline.server.export")
				.addClass(MockDbClient.class)
				.addAsResource("test-database.properties",
						"database.properties")
				.addAsResource("mailinglists.properties")
				.addAsResource("searchisko.properties")
				.addAsWebInfResource(
						new StringAsset(beansXml.exportAsString()), "beans.xml");
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.importRuntimeAndTestDependencies().asFile();
		for (File file : libs) {
			if(file.getName().contains("resteasy")) {
				continue;
			}
			webArchive.addAsLibrary(file);
		}
		return webArchive;
	}

	@ArquillianResource
	URL deploymentUrl;

	@Test
	public void testGetEmailById() throws Exception {
		String id = "firstId";
		String url = deploymentUrl.toString() + RESOURCE_PREFIX + "/email/" + id;
		ClientRequest request = new ClientRequest(url);
		request.header("Accept", MediaType.APPLICATION_JSON);
		ClientResponse<String> responseObj = request.get(String.class);
		String response = responseObj.getEntity().trim();
		Assert.assertEquals(200, responseObj.getStatus());
		Assert.assertTrue(response.contains("\"from\":\"test\""));
		Assert.assertTrue(response.contains("\"_id\""));
	}
	
	@Test
	public void testGetAllEmails() throws Exception {
		String url = deploymentUrl.toString() + RESOURCE_PREFIX + "/all";
		ClientRequest request = new ClientRequest(url);
		request.header("Accept", MediaType.APPLICATION_JSON);
		ClientResponse<String> responseObj = request.get(String.class);
		String response = responseObj.getEntity().trim();
		Assert.assertEquals(200, responseObj.getStatus());
		Assert.assertTrue(response.contains("\"from\":\"test\""));
		Assert.assertTrue(response.contains("\"_id\""));
	}


}
