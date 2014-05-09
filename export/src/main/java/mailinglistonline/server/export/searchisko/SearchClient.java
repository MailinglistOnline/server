package mailinglistonline.server.export.searchisko;

import java.util.List;

import mailinglistonline.server.export.database.entities.Email;
import mailinglistonline.server.export.database.entities.MiniEmail;

public interface SearchClient {

	public List<MiniEmail> searchByContent(String content);
	public boolean updateEmail(Email email);
}
