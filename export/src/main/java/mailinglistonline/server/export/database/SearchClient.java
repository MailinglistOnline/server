package mailinglistonline.server.export.database;

import java.util.List;

import mailinglistonline.server.export.database.entities.MiniEmail;

public interface SearchClient {

	public List<MiniEmail> searchByContent(String content);
}
