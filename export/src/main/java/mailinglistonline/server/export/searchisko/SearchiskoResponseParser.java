package mailinglistonline.server.export.searchisko;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.registry.infomodel.User;

import mailinglistonline.server.export.database.entities.Email;
import mailinglistonline.server.export.database.entities.MiniEmail;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class SearchiskoResponseParser {
	int hits;
	private List<MiniEmail> emails;
	
	public SearchiskoResponseParser() {
		
	}
	
	public void setEmails(List<MiniEmail> emails) {
		this.emails=emails;
	}
	
	public void addEmail(MiniEmail email) {
		emails.add(email);
	}
	
	public List<MiniEmail> getEmails() {
		return emails;
	}

	public void parse(Map<String,Object> map) {
		Map<String, Object> hitsMap=(Map<String, Object>) map.get("hits");
		hits=(Integer) hitsMap.get("total");
		Map<Integer, Map<String, Object>> emails=(Map<Integer, Map<String, Object>>) hitsMap.get("hits");
		for(Map<String, Object> jsonEmailAllInfo : emails.values()) {
			Map<String, Object> jsonEmail = (Map<String, Object> )jsonEmailAllInfo.get("fields");
			ObjectMapper mapper = new ObjectMapper();
			MiniEmail miniEmail = new MiniEmail();
			try {
				miniEmail = mapper.readValue(mapper.writeValueAsString(jsonEmail), MiniEmail.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonGenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, Object> highlightInside = (Map<String, Object> )jsonEmailAllInfo.get("highlights");
			Map<Integer, String> mainContents = (Map<Integer, String> )jsonEmailAllInfo.get("main_content.text");
			for(String highLight : mainContents.values()) {
				miniEmail.addHighLight(highLight);
			}
		}
		
	}
}
