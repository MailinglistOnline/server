package mailinglistonline.server.export.searchisko;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import mailinglistonline.server.export.database.entities.Email;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;



public class SearchManager {

	    private static String SERVER_PROPERTIES_FILE_NAME = "searchisko.properties";
		private String searchiskoUrl;
		SearchiskoInterface emailClient;

		public SearchManager() throws IOException {
			Properties prop = new Properties();
			RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
	        prop.load(SearchManager.class.getClassLoader().getResourceAsStream((SERVER_PROPERTIES_FILE_NAME)));
	        searchiskoUrl = prop.getProperty("searchiskoUrl");
	        emailClient = ProxyFactory.create(SearchiskoInterface.class, searchiskoUrl);
		}
		
		public SearchiskoResponse searchEmailByContent(String mainContent) {
			SearchiskoResponse response= new SearchiskoResponse();
			Map<String, Object> map =emailClient.searchEmailByContent(mainContent, true);
			Map<String, Object> hitsMap=(Map<String, Object>) map.get("hits");
			Map<String, Map<String, Object>> emails=(Map<String, Map<String, Object>>) hitsMap.get("hits");
			for(Map<String, Object> jsonEmail : emails.values()) {
				Email email = new Email();
				email.setId((String)jsonEmail.get("_id"));
				Map<String, Object> jsonFields = (Map<String, Object>)jsonEmail.get("fields");
				email.setSubject((String)jsonFields.get("subject"));
				response.addEmail(email);
				//TODO: make it better, mainContent etc...
			}
			return response;
			
			
			
		}
	
}
