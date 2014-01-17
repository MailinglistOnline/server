package searchisko;

import java.io.IOException;
import java.util.Properties;

import mailinglist.entities.Email;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class SearchManager {


private static String SERVER_PROPERTIES_FILE_NAME = "searchisko.properties";
		private String searchiskoUrl;
		SearchiskoInterface searchiskoProxy;

		public SearchManager() throws IOException {
			Properties prop = new Properties();
			RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
	        prop.load(SearchManager.class.getClassLoader().getResourceAsStream((SERVER_PROPERTIES_FILE_NAME)));
	        searchiskoUrl = prop.getProperty("searchiskoUrl");
	        searchiskoProxy = ProxyFactory.create(SearchiskoInterface.class, searchiskoUrl);
		}
		
		public boolean addEmail(Email email) {
			searchiskoProxy.sendEmail(email.getId(), email);
		}
		
		public boolean removeEmail(String id) {
			searchiskoProxy.deleteEmail(id, true);
			return true;
		}
		
}