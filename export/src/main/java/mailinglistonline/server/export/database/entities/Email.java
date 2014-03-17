/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailinglistonline.server.export.database.entities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonValue;



/**
 *
 * @author Matej Briškár
 */
@XmlRootElement(name = "email")

public class Email extends MiniEmail{

	public static final String ROOT_MONGO_TAG = "thread_root";
    public static final String IN_REPLY_TO_MONGO_TAG = "in_reply_to";
    public static final String REPLIES_MONGO_TAG = "replies";
    public static final String ATTACHMENTS_MONGO_TAG = "attachments";
    public static final String MAIN_CONTENT_MONGO_TAG = "main_content";
    public static final String SHARD_KEY_MONGO_TAG = "email_shard_key";
    
    
    public Email() {
        super();
        put(REPLIES_MONGO_TAG,new ArrayList());
    }
    
    @XmlElement(name=ROOT_MONGO_TAG)
    public MiniEmail getRoot() {
        return (MiniEmail)get(ROOT_MONGO_TAG);
    }
    
    @JsonValue
    public String getId() {
        return getString(ID_MONGO_TAG);
        
    }

    @JsonProperty("id")
    public void setId(String id) {
    	put(ID_MONGO_TAG, id);
        
    }

    public void setRoot(MiniEmail root) {
        put(ROOT_MONGO_TAG, new MiniEmail(root));
    }

    @XmlElement(name=IN_REPLY_TO_MONGO_TAG)
    public MiniEmail getInReplyTo() {
        return (MiniEmail) get(IN_REPLY_TO_MONGO_TAG);
    }
    
    public void setInReplyTo(MiniEmail inReplyTo) {
        put(IN_REPLY_TO_MONGO_TAG, new MiniEmail(inReplyTo));
    }

    
    
    public void setReplies(List<MiniEmail> replies) {
       put(REPLIES_MONGO_TAG,replies);
    }



    public void addReply(MiniEmail reply) {
        ArrayList<MiniEmail> list = (ArrayList<MiniEmail>)get(REPLIES_MONGO_TAG);
        if(list == null) {
            put(REPLIES_MONGO_TAG,new ArrayList());
            list = (ArrayList<MiniEmail>)get(REPLIES_MONGO_TAG);
        }
        list.add(new MiniEmail(reply));
    }
    
    @XmlElementWrapper(name=REPLIES_MONGO_TAG)
    @XmlElement(name="reply")
    public List<MiniEmail> getReplies() {
       return (ArrayList<MiniEmail>)get(REPLIES_MONGO_TAG);
    }

    public void addAttachment(ContentPart part) {
        ArrayList<ContentPart> list = (ArrayList<ContentPart>)get(ATTACHMENTS_MONGO_TAG);
        if(list == null) {
            put(ATTACHMENTS_MONGO_TAG,new ArrayList());
            list = (ArrayList<ContentPart>)get(ATTACHMENTS_MONGO_TAG);
        }
        list.add(part);
    }
    public void setAttachments(List<ContentPart> attachments) {
        put(ATTACHMENTS_MONGO_TAG,attachments);
    }

    @XmlElementWrapper(name=MAIN_CONTENT_MONGO_TAG)
    @XmlElement(name="alternative")
    public ArrayList<ContentPart> getMainContent() {
        ArrayList<ContentPart> list = (ArrayList<ContentPart>)get(MAIN_CONTENT_MONGO_TAG);
        return list;
    }

    public void setMainContent(List<ContentPart> mainContent) {
         put(MAIN_CONTENT_MONGO_TAG, mainContent);
    }
    
    @XmlElementWrapper(name=ATTACHMENTS_MONGO_TAG)
    @XmlElement(name="attachment")
    public ArrayList<ContentPart> getAttachments() {
        ArrayList<ContentPart> list =(ArrayList<ContentPart>) get(ATTACHMENTS_MONGO_TAG);
        return list;
    }
    
    public String getEmailShardKey() {
		return getString(SHARD_KEY_MONGO_TAG);
	}

	public void setEmailShardKey(String messageId) {
		put(SHARD_KEY_MONGO_TAG, messageId);
	}
    
}
