package mailinglistonline.server.export.database.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

import com.mongodb.BasicDBObject;

/**
 * Entity used to handle basic information about the email. This entity is good when only part of the information is needed.
 * The scenarios where this entity is useful:
 * 1. Basic information about the emails that the email is in connection with
 * 2. To provide information about the list of requested emails.
 * @author Matej Briškár
 */
@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class MiniEmail extends BasicDBObject{

	private static final long serialVersionUID = 3749184648068897751L;
	
	public static final String ID_MONGO_TAG = "_id";
    public static final String MAILINGLIST_MONGO_TAG = "mailinglist";
    public static final String MESSAGE_ID_MONGO_TAG = "message_id";
    public static final String SUBJECT_MONGO_TAG = "subject";
    public static final String DATE_MONGO_TAG = "date";
    public static final String FROM_MONGO_TAG = "from";
    // message snippet is the first x letters from the email
    public static final String MESSAGE_SNIPPET_MONGO_TAG = "message_snippet";
    public static final String TAGS_MONGO_TAG = "tags";
    public static final String HIGHLIGHTED = "highlighted";
    
    private Map<String,List<String>> highlighted;

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
    	if (email.getDate() !=null) {
    		setDate(email.getDate());
    	}
    	setFrom(email.getFrom());
    	setMessageSnippet(email.getMessageSnippet());
    	setTags(email.getTags());
    }
    

    @JsonProperty
    public String getId() {
        return getString(ID_MONGO_TAG);
    }

    @JsonProperty
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
    public Long getDate() {
    	Long date = (Long)get(DATE_MONGO_TAG);
        return date;
    }

    public void setDate(long sentDate) {
         put(DATE_MONGO_TAG, sentDate);
    }
    
    @XmlElement(name=HIGHLIGHTED)
    public Map<String,List<String>> getHighlighted() {
		return highlighted;
    }
    
    public void addHighLight(String key,String value) {
    	Map<String,List<String>> object = (Map<String,List<String>>)get(HIGHLIGHTED);
        if(object == null) {
            append(HIGHLIGHTED,new HashMap<String,List<String>>());
            object = (Map<String,List<String>>)get(HIGHLIGHTED);
        }
        if(object.get(key) == null ) {
        	object.put(key, new ArrayList<String>());
        }
        object.get(key).add(value);
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
    
    public void removeTag(String tag) {
        ArrayList<String>list = (ArrayList<String>)get(TAGS_MONGO_TAG);
        if(list == null) {
            throw new IllegalArgumentException("Email does not contain tag '" + tag + "'.");
        }
        list.remove(tag);
    }


	
}