package mailinglistonline.server.export.searchisko;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import mailinglistonline.server.export.database.entities.MiniEmail;

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
		
		public List<MiniEmail> searchEmailByContent(String mainContent) {
			SearchiskoResponseParser parser= new SearchiskoResponseParser();
			parser.parse(emailClient.searchEmailByContent(mainContent, true));
			return parser.getEmails();
		}
	
}
