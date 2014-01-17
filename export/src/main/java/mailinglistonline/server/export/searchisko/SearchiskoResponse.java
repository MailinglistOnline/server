package mailinglistonline.server.export.searchisko;

import java.util.List;

import mailinglistonline.server.export.database.entities.Email;

public class SearchiskoResponse {
	int hits;
	private List<Email> emails;
	
	public SearchiskoResponse() {
		
	}
	
	public void setEmails(List<Email> emails) {
		this.emails=emails;
	}
	
	public void addEmail(Email email) {
		emails.add(email);
	}
	
	public List<Email> getEmails() {
		return emails;
	}
}
