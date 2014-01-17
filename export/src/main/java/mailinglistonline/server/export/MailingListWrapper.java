package mailinglistonline.server.export;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matej
 */
@XmlRootElement(name="mailinglists")
public class MailingListWrapper {
    /*
     * Zakomponovat CDI a zmazat tento wrapper
     */
    
    private List<String> mailinglists;

    @XmlElement(name="mailing_list")
    public List<String> getMailinglists() {
        return mailinglists;
    }

    public void setMailinglists(List<String> mailinglists) {
        this.mailinglists = mailinglists;
    }
    
    
    
}
