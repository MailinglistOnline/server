/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database.entities;

import com.mongodb.BasicDBObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author matej
 */

@XmlRootElement(name = "email")
@XmlAccessorOrder
public class Email extends BasicDBObject{

    public static final String ID_MONGO_TAG = "_id";
    public static final String ROOT_MONGO_TAG = "root";
    public static final String IN_REPLY_TO_MONGO_TAG = "in-reply-to";
    public static final String REPLIES_MONGO_TAG = "replies";
    public static final String ATTACHMENTS_MONGO_TAG = "attachments";
    public static final String MAILINGLIST_MONGO_TAG = "mailinglist";
    public static final String MESSAGE_ID_MONGO_TAG = "message_id";
    public static final String SUBJECT_MONGO_TAG = "subject";
    public static final String DATE_MONGO_TAG = "date";
    public static final String FROM_MONGO_TAG = "from";
    public static final String MAIN_CONTENT_MONGO_TAG = "mainContent";
    public static final String TAGS_MONGO_TAG = "tags";
    
    
    public Email() {
        super();
        put(REPLIES_MONGO_TAG,new ArrayList());
    }
    
    @XmlElement(name="id")
    public String getId() {
        return getString(ID_MONGO_TAG);
        
    }
    @XmlElement(name="root")
    public String getRoot() {
        return getString(ROOT_MONGO_TAG);
    }

    public void setRoot(String root) {
        put(ROOT_MONGO_TAG, root);
    }
    
    public void setReplies(List<String> replies) {
       put(REPLIES_MONGO_TAG,replies);
    }


    @XmlElement(name="in_reply_to")
    public String getInReplyTo() {
        return getString(IN_REPLY_TO_MONGO_TAG);
    }
    
    public void addReply(String replyId) {
        ArrayList<String> list = (ArrayList<String>)get(REPLIES_MONGO_TAG);
        if(list == null) {
            put(REPLIES_MONGO_TAG,new ArrayList());
            list = (ArrayList<String>)get(REPLIES_MONGO_TAG);
        }
        list.add(replyId);
    }
    
    @XmlElementWrapper(name="replies")
    @XmlElement(name="reply")
     public List<String> getReplies() {
       return (ArrayList<String>)get(REPLIES_MONGO_TAG);
    }

    public void setInReplyTo(String inReplyTo) {
        put(IN_REPLY_TO_MONGO_TAG, inReplyTo);
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

    public void addMailingList(String mailinglist) {
        ArrayList<String>list = (ArrayList<String>)get(MAILINGLIST_MONGO_TAG);
        if(list == null) {
            append(MAILINGLIST_MONGO_TAG,new ArrayList<String>());
            list = (ArrayList<String>)get(MAILINGLIST_MONGO_TAG);
        }
        list.add(mailinglist);
    }
    @XmlElement(name="message_id")
    public String getMessageId() {
        return getString(MESSAGE_ID_MONGO_TAG);
    }

    public void setMessageId(String messageId) {
        put(MESSAGE_ID_MONGO_TAG, messageId);
    }
    @XmlElement(name="subject")
    public String getSubject() {
        return getString(SUBJECT_MONGO_TAG);
    }

    public void setSubject(String subject) {
         put(SUBJECT_MONGO_TAG, subject);
    }
    @XmlElement(name="sent_date")
    public Date getSentDate() {
        return getDate(DATE_MONGO_TAG);
    }

    public void setSentDate(Date sentDate) {
         put(DATE_MONGO_TAG, sentDate);
    }
    
    @XmlElementWrapper(name="mailing_lists")
    @XmlElement(name="mailing_list")
    public ArrayList<String> getMessageMailingLists() {
       ArrayList<String> list = (ArrayList<String>)get(MAILINGLIST_MONGO_TAG);
       return list;
       
    }
    @XmlElement(name="from")
    public String getFrom() {
        return getString(FROM_MONGO_TAG);
    }

    public void setFrom(String from) {
         put(FROM_MONGO_TAG, from);
    }
    
    

    @XmlElementWrapper(name="main_content")
    @XmlElement(name="alternative")
    public ArrayList<ContentPart> getMainContent() {
        ArrayList<ContentPart> list = (ArrayList<ContentPart>)get(MAIN_CONTENT_MONGO_TAG);
        return list;
    }

    public void setMainContent(List<ContentPart> mainContent) {
         put(MAIN_CONTENT_MONGO_TAG, mainContent);
    }
    
    @XmlElementWrapper(name="attachments")
    @XmlElement(name="attachment")
    public ArrayList<ContentPart> getAttachments() {
        ArrayList<ContentPart> list =(ArrayList<ContentPart>) get(ATTACHMENTS_MONGO_TAG);
        return list;
    }
    
    
    @XmlElement(name="tags")
    public ArrayList<String> getTags() {
        ArrayList<String> list =(ArrayList<String>) get(TAGS_MONGO_TAG);
        return list;
    }
    
    public void setTags(List<String> tags) {
        put(TAGS_MONGO_TAG,tags);
    }


}
