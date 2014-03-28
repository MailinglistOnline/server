package searchisko;

import java.io.IOException;
import java.util.Properties;

import mailinglist.entities.Email;

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


private static String SERVER_PROPERTIES_FILE_NAME = "searchisko.properties";
		private String searchiskoUrl;
		SearchiskoInterface searchiskoProxy;
		String username;
		String password;
		
		public SearchManager() throws IOException {
			RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
	        readSearchiskoConfigurationParameters();
	        createSearchiskoProxyConnection();
		}
		
		/*
		 * Parse the properties (url, username, password) for the Searchisko
		 */
		private void readSearchiskoConfigurationParameters() throws IOException {
			Properties prop = new Properties();
			prop.load(SearchManager.class.getClassLoader().getResourceAsStream((SERVER_PROPERTIES_FILE_NAME)));
	        searchiskoUrl = prop.getProperty("searchiskoUrl");
	        username = prop.getProperty("username");
	        password = prop.getProperty("password");
		}
		
		private void createSearchiskoProxyConnection() {
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        Credentials credentials = new UsernamePasswordCredentials(username,password);
	        httpClient.getCredentialsProvider().setCredentials(
	                org.apache.http.auth.AuthScope.ANY, credentials);
	        ClientExecutor clientExecutor = new ApacheHttpClient4Executor(httpClient);
	        searchiskoProxy = ProxyFactory.create(SearchiskoInterface.class, searchiskoUrl, clientExecutor);
		}
		
		public boolean addEmail(Email email) {
			ObjectId id =(ObjectId)email.get("_id");
			email.setId(id.toStringMongod());
			searchiskoProxy.sendEmail(email,email.getId());
			return true;
		}
		
		public boolean removeEmail(String id) {
			searchiskoProxy.deleteEmail(id, true);
			return true;
		}
		
}