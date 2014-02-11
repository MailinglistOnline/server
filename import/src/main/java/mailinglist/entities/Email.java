/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailinglist.entities;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;




/**
 *
 * @author matej
 */

public class Email extends MiniEmail{

    private static final int message_snippet_length = 150;
	public static final String ROOT_MONGO_TAG = "root";
    public static final String IN_REPLY_TO_MONGO_TAG = "in-reply-to";
    public static final String REPLIES_MONGO_TAG = "replies";
    public static final String ATTACHMENTS_MONGO_TAG = "attachments";
    public static final String MAIN_CONTENT_MONGO_TAG = "mainContent";
    
    
    public Email() {
        super();
        put(REPLIES_MONGO_TAG,new ArrayList());
    }
    
    public MiniEmail getRoot() {
        return (MiniEmail)get(ROOT_MONGO_TAG);
    }

    public void setRoot(MiniEmail root) {
    	put(ROOT_MONGO_TAG, new MiniEmail(root));
    }

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

    public ArrayList<ContentPart> getMainContent() {
        ArrayList<ContentPart> list = (ArrayList<ContentPart>)get(MAIN_CONTENT_MONGO_TAG);
        return list;
    }

    public void setMainContent(List<ContentPart> mainContent) {
    	String mainText = mainContent.get(0).getContent();
    	String snippet =mainText.substring(0, Math.min(mainText.length(), message_snippet_length));
        put(MAIN_CONTENT_MONGO_TAG, mainContent);
        put(MESSAGE_SNIPPET_MONGO_TAG, snippet);
    }
    
    public ArrayList<ContentPart> getAttachments() {
        ArrayList<ContentPart> list =(ArrayList<ContentPart>) get(ATTACHMENTS_MONGO_TAG);
        return list;
    }

	public void removeReply(MiniEmail email) {
		 ArrayList<ContentPart> list = (ArrayList<ContentPart>)get(REPLIES_MONGO_TAG);
	        if(list == null) {
	            put(REPLIES_MONGO_TAG,new ArrayList());
	            list = (ArrayList<ContentPart>)get(REPLIES_MONGO_TAG);
	        }
	        list.remove(email);
		
	}
    
}


