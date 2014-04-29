package mailinglistonline.server.export.searchisko;

import java.util.List;

import mailinglistonline.server.export.database.entities.Email;
import mailinglistonline.server.export.database.entities.MiniEmail;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bson.types.ObjectId;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;



public class SearchManager {

	    private static String SERVER_PROPERTIES_FILE_NAME = "/searchisko.properties";
		private SearchiskoConfiguration configuration;
		SearchiskoInterface emailClient;

		public SearchManager() {
			this(new SearchiskoConfiguration().readFromPropertyFile(
					SearchManager.class.getClass().getResource((SERVER_PROPERTIES_FILE_NAME)).getPath()),false);
		}
		
		public SearchManager(SearchiskoConfiguration configuration, boolean authentication) {
			this.configuration=configuration;
			RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
			if(authentication) {
				emailClient =createSearchiskoProxyConnectionWithCredentials();
			} else {
				emailClient =createSearchiskoProxyConnectionWithoutCredentials();
			}
		}
		
		public List<MiniEmail> searchEmailByContent(String mainContent) {
			SearchiskoResponseParser parser= new SearchiskoResponseParser();
			parser.parse(emailClient.searchEmailByContent(mainContent, true));
			return parser.getEmails();
		}
		
		private SearchiskoInterface createSearchiskoProxyConnectionWithCredentials() {
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        Credentials credentials = new UsernamePasswordCredentials(configuration.getUsername(),configuration.getPassword());
	        httpClient.getCredentialsProvider().setCredentials(
	                org.apache.http.auth.AuthScope.ANY, credentials);
	        ClientExecutor clientExecutor = new ApacheHttpClient4Executor(httpClient);
	        return ProxyFactory.create(SearchiskoInterface.class, configuration.getSearchiskoUrl(), clientExecutor);
		}
		
		private SearchiskoInterface createSearchiskoProxyConnectionWithoutCredentials() {
			return  ProxyFactory.create(SearchiskoInterface.class, configuration.getSearchiskoUrl());
		}
		
		
		public boolean addEmail(Email email) {
			ObjectId id =(ObjectId)email.get("_id");
			email.setId("mlonline_email-" + id.toStringMongod());
			email.put("mongo_id",id.toStringMongod());
			emailClient.sendEmail(email,id.toStringMongod());
			return true;
		}
		
		public boolean removeEmail(String id) {
			emailClient.deleteEmail(id, true);
			return true;
		}
	
}
