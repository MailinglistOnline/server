package mailinglistonline.server.export.searchisko;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SearchiskoConfiguration {
	private String searchiskoUrl;
	private String username;
	private String password;
	

	public String getSearchiskoUrl() {
		return searchiskoUrl;
	}
	public void setSearchiskoUrl(String searchiskoUrl) {
		this.searchiskoUrl = searchiskoUrl;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public SearchiskoConfiguration readFromPropertyFile(String path) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(path));
		} catch (IOException e) {
			throw new IllegalArgumentException("Readingsearchisko configuration file threw and exception for file path " + path, e);
		}
        searchiskoUrl = prop.getProperty("searchiskoUrl");
        username = prop.getProperty("username");
        password = prop.getProperty("password");
		return this;
	}
}
