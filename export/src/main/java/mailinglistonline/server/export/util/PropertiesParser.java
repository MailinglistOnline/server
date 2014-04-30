package mailinglistonline.server.export.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import mailinglistonline.server.export.database.DatabaseConfiguration;
import mailinglistonline.server.export.database.entities.Mailinglist;

public class PropertiesParser {

	public static DatabaseConfiguration parseDatabaseConfigurationFile(
			String path) {
		DatabaseConfiguration configuration = new DatabaseConfiguration();
		Properties prop = new Properties();
		try {
			return parseDatabaseConfigurationFile(new FileInputStream(path));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"Reading database configuration file threw and exception for file path "
							+ path, e);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(
					"Reading database configuration file threw and exception for file path "
							+ path, e);
		}
	}

	public static DatabaseConfiguration parseDatabaseConfigurationFile(
			InputStream stream) {
		DatabaseConfiguration configuration = new DatabaseConfiguration();
		Properties prop = new Properties();
		try {
			prop.load(stream);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"IOException occured when reading the stream", e);
		}
		configuration.setDefaultPort(Integer.valueOf(prop
				.getProperty("defaultMongoPort")));
		configuration.setDatabaseUrl(prop.getProperty("defaultMongoUrl"));
		configuration.setDefaultDatabaseName(prop
				.getProperty("defaultDatabaseName"));
		configuration.setDefaultCollectionName(prop
				.getProperty("defaultCollection"));
		configuration.setUser(prop.getProperty("user"));
		configuration.setPassword(prop.getProperty("password"));
		return configuration;
	}

	public static List<Mailinglist> parseMailinglistConfigurationFile(
			InputStream stream) {
		Properties prop = new Properties();
		List<Mailinglist> mailinglists = new ArrayList<Mailinglist>();
		try {
			prop.load(stream);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Unable to read mailinglist input stream.", e);
		}
		String mailinglist = prop.getProperty("mailinglist." + 1);
		int i = 1;
		while (mailinglist != null) {
			Mailinglist mlist = new Mailinglist();
			mlist.setName(mailinglist);
			String description = prop.getProperty("mailinglist.description."
					+ i);
			mlist.setDescription(description);
			mailinglists.add(mlist);
			i++;
			mailinglist = prop.getProperty("mailinglist." + i);
		}
		return mailinglists;
	}

}
