package mailinglistonline.server.export.database.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

@XmlRootElement(name = "miniemail")
@XmlAccessorOrder
public class MiniEmail extends BasicDBObject{

    public static final String ID_MONGO_TAG = "_id";
    public static final String MAILINGLIST_MONGO_TAG = "mailinglist";
    public static final String MESSAGE_ID_MONGO_TAG = "message_id";
    public static final String SUBJECT_MONGO_TAG = "subject";
    public static final String DATE_MONGO_TAG = "date";
    public static final String FROM_MONGO_TAG = "from";
    // message snippet is the first x letters from the email
    public static final String MESSAGE_SNIPPET_MONGO_TAG = "message_snippet";
    public static final String TAGS_MONGO_TAG = "tags";
    public static final String HIGHLIGHTED_MAIN_CONTENT = "highlighted_main_content";
    
    private List<String> highlightedTexts = new ArrayList<String>();
    public MiniEmail() {
        super();
    }
    /*
     * Needed when adding miniemail to mongodb
     */
    public MiniEmail(MiniEmail email) {
    	setId(email.getId());
    	setMailingList(email.getMessageMailingList());
    	setMessageId(email.getMessageId());
    	setSubject(email.getSubject());
    	setDate(email.getDate());
    	setFrom(email.getFrom());
    	setMessageSnippet(email.getMessageSnippet());
    	setTags(email.getTags());
    }
    
    @XmlElement(name=ID_MONGO_TAG)
    public String getId() {
        return new ObjectId(getString(ID_MONGO_TAG)).toString();
        
    }

    public void setId(String id) {
    	put(ID_MONGO_TAG, id);
        
    }

    public void setMailingList(String mailinglist) {
    	put(MAILINGLIST_MONGO_TAG, mailinglist);
    }
    
    @XmlElement(name=MESSAGE_ID_MONGO_TAG)
    public String getMessageId() {
        return getString(MESSAGE_ID_MONGO_TAG);
    }

    public void setMessageId(String messageId) {
        put(MESSAGE_ID_MONGO_TAG, messageId);
    }
    
    @XmlElement(name=MESSAGE_SNIPPET_MONGO_TAG)
    public String getMessageSnippet() {
        return getString(MESSAGE_SNIPPET_MONGO_TAG);
    }

    public void setMessageSnippet(String messageSnippet) {
        put(MESSAGE_SNIPPET_MONGO_TAG, messageSnippet);
    }

    @XmlElement(name=SUBJECT_MONGO_TAG)
    public String getSubject() {
        return getString(SUBJECT_MONGO_TAG);
    }

    public void setSubject(String subject) {
         put(SUBJECT_MONGO_TAG, subject);
    }
    
    @XmlElement(name=DATE_MONGO_TAG)
    public long getDate() {
        return getLong(DATE_MONGO_TAG);
    }

    public void setDate(long sentDate) {
         put(DATE_MONGO_TAG, sentDate);
    }
    
    @XmlElement(name=HIGHLIGHTED_MAIN_CONTENT)
    public List<String> getHighlightedTexts() {
		return highlightedTexts;
    }
    
    public void setHighlightedTexts(List<String> texts) {
    	highlightedTexts=texts;
    }
    
    public void addHighLight(String highLight) {
    	highlightedTexts.add(highLight);
	}
    
    
    @XmlElement(name=MAILINGLIST_MONGO_TAG)
    public String getMessageMailingList() {
       return getString(MAILINGLIST_MONGO_TAG);
       
    }
    @XmlElement(name=FROM_MONGO_TAG)
    public String getFrom() {
        return getString(FROM_MONGO_TAG);
    }

    public void setFrom(String from) {
         put(FROM_MONGO_TAG, from);
    }
        
    @XmlElement(name=TAGS_MONGO_TAG)
    public ArrayList<String> getTags() {
        ArrayList<String> list =(ArrayList<String>) get(TAGS_MONGO_TAG);
        return list;
    }
    
    public void setTags(List<String> tags) {
        put(TAGS_MONGO_TAG,tags);
    }
    
    public void addTag(String tag) {
        ArrayList<String>list = (ArrayList<String>)get(TAGS_MONGO_TAG);
        if(list == null) {
            append(TAGS_MONGO_TAG,new ArrayList<String>());
            list = (ArrayList<String>)get(TAGS_MONGO_TAG);
        }
        list.add(tag);
    }
	
}