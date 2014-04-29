package mailinglistonline.server.export.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import mailinglistonline.server.export.database.DatabaseConfiguration;

public class PropertiesParser {

	public static DatabaseConfiguration parseDatabaseConfigurationFile(String path) {
		DatabaseConfiguration configuration = new DatabaseConfiguration();
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(path));
		} catch (IOException e) {
			throw new IllegalArgumentException("Reading database configuration file threw and exception for file path " + path, e);
		}
		configuration.setDefaultPort(Integer.valueOf(prop.getProperty("defaultMongoPort")));
        configuration.setDatabaseUrl(prop.getProperty("defaultMongoUrl"));
        configuration.setDefaultDatabaseName(prop.getProperty("defaultDatabaseName"));
        configuration.setDefaultCollectionName(prop.getProperty("defaultCollection"));
        configuration.setUser(prop.getProperty("user"));
        configuration.setPassword(prop.getProperty("password"));
		return configuration;
	}
	
}
