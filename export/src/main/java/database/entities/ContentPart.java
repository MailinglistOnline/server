/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database.entities;

import com.mongodb.BasicDBObject;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author matej
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
    
}
