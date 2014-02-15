package mailinglistonline.server.export.searchisko;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mailinglistonline.server.export.database.entities.MiniEmail;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class SearchiskoResponseParser {
	int hits;
	private List<MiniEmail> emails = new ArrayList<MiniEmail>();
	
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
		List<Map<Integer, Map<String, Object>>> emails=(List<Map<Integer, Map<String, Object>>>) hitsMap.get("hits");
		for(Map<Integer, Map<String, Object>> jsonEmailAllInfo : emails) {
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
			Map<String, Object> highlightInside = (Map<String, Object> )jsonEmailAllInfo.get("highlight");
			List< String> mainContents = (List< String>)highlightInside.get("main_content.text");
			if(mainContents==null) {continue;}
			for(String highLightMainContent : mainContents) {
				miniEmail.addHighLightMainContent(highLightMainContent);
			}
			addEmail(miniEmail);
		}
		
	}
}
