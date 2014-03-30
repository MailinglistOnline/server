package mailinglistonline.server.export.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfiguration {
	private Integer defaultPort;
    private String databaseUrl;
    private String defaultDatabaseName;
    private String defaultCollectionName;
    
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
	
	public DatabaseConfiguration readFromConfigurationFile(String path) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(path));
		} catch (IOException e) {
			throw new IllegalArgumentException("Reading database configuration file threw and exception for file path " + path, e);
		}
		this.defaultPort = Integer.valueOf(prop.getProperty("defaultMongoPort"));
        this.databaseUrl = prop.getProperty("defaultMongoUrl");
        this.defaultDatabaseName = prop.getProperty("defaultDatabaseName");
        this.defaultCollectionName = prop.getProperty("defaultCollection");
		return this;
	}
}
