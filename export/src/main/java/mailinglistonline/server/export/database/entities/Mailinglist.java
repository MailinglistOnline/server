package mailinglistonline.server.export.database.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity used to handle information about the mailinglists being processed.
 * 
 * @author Matej Briškár
 */
@XmlRootElement(name = "mailinglist")
public class Mailinglist{
	private String name;
	private String description;
	
	@XmlElement(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}