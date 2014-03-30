package searchisko;

import mailinglistonline.server.export.database.entities.Email;
import mailinglistonline.server.export.searchisko.SearchManager;
import mailinglistonline.server.export.searchisko.SearchiskoConfiguration;

public class SearchManagerProxy {

	private static String SERVER_PROPERTIES_FILE_NAME = "searchisko.properties";
	SearchManager searchManager;

	public SearchManagerProxy(){
		this.searchManager= new SearchManager(new SearchiskoConfiguration().readFromPropertyFile(
				SearchManager.class.getClassLoader().getResource((SERVER_PROPERTIES_FILE_NAME)).getPath()),true);
	}

	public boolean addEmail(Email email) {
		return searchManager.addEmail(email);
	}

	public boolean removeEmail(String id) {
		return searchManager.removeEmail(id);
	}

}