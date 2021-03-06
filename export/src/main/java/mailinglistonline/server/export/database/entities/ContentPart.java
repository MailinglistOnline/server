/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailinglistonline.server.export.database.entities;

import com.mongodb.BasicDBObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity used to save information about the content,attachments(files, signature etc.) of the email.
 * @author Matej Briškár
 */
@XmlRootElement(name = "ContentPart")
public class ContentPart extends BasicDBObject{

    @XmlElement
    public String getType() {
        return getString("type");
    }

    public void setType(String type) {
        put("type", type);
    }
    @XmlElement
    public String getContent() {
        return getString("text");
    }

    public void setContent(String content) {
        put("text", content);
    }
    
    public String getLink() {
        return getString("link");
    }

    public void setLink(String link) {
        put("link", link);
    }
    
    
}
