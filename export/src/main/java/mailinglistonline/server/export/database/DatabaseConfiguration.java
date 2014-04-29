package mailinglistonline.server.export.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfiguration {
	private Integer defaultPort;
    private String databaseUrl;
    private String defaultDatabaseName;
    private String defaultCollectionName;
	private String user;
	private String password;
    
    public Integer getDefaultPort() {
		return defaultPort;
	}
	public void setDefaultPort(Integer defaultPort) {
		this.defaultPort = defaultPort;
	}
	public String getDatabaseUrl() {
		return databaseUrl;
	}
	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDefaultDatabaseName() {
		return defaultDatabaseName;
	}
	public void setDefaultDatabaseName(String defaultDatabaseName) {
		this.defaultDatabaseName = defaultDatabaseName;
	}
	public String getDefaultCollectionName() {
		return defaultCollectionName;
	}
	public void setDefaultCollectionName(String defaultCollectionName) {
		this.defaultCollectionName = defaultCollectionName;
	}
	
	
}
