/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailinglist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import mailinglistonline.server.export.database.DbClient;
import mailinglistonline.server.export.database.MongoDbClient;
import mailinglistonline.server.export.database.entities.ContentPart;
import mailinglistonline.server.export.database.entities.Email;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import searchisko.SearchManagerProxy;

import com.sun.mail.util.BASE64DecoderStream;

import exceptions.MalformedMessageException;

/**
 *
 * @author Matej Briškár
 */
public class MessageManager {

    private DbClient dbClient;
    private SearchManagerProxy searchManager;
    private final ArrayList<String> mailingLists;
	private boolean sendMessagesToSearchisko=true;
    private static String MAILINGLISTS_PROPERTIES_FILE_NAME = "mailinglists.properties";

    public MessageManager(DbClient dbClient, boolean sendMessagesToSearchisko) throws IOException {
    	this(dbClient);
    	this.sendMessagesToSearchisko=sendMessagesToSearchisko;
    }
    public MessageManager(DbClient dbClient) throws IOException {
        this.dbClient = dbClient;
        searchManager = new SearchManagerProxy();
        mailingLists = new ArrayList<String>();
        Properties prop = new Properties();
        prop.load(MongoDbClient.class.getClassLoader().getResourceAsStream((MAILINGLISTS_PROPERTIES_FILE_NAME)));
        String mailinglist = prop.getProperty("mailinglist." + 1);
        int i = 1;
        while (mailinglist != null) {
            mailingLists.add(mailinglist);
            i++;
            mailinglist = prop.getProperty("mailinglist." + i);
        }
    }


    /*
     * List of emails can be returned if there are more found mailinglists in header
     * for the processed email
     */
    public List<Email> createMessage(MimeMessage message) throws MessagingException, IOException, MalformedMessageException {
        if (message.getMessageID() == null && message.getFrom() == null) {
            throw new MalformedMessageException();
        }
        Email email = new Email();
        email.setMessageId(message.getMessageID());
        if(message.getSentDate() != null) {
        	email.setDate(message.getSentDate().getTime());
        }
        String fromField = extractEmailAddress(message.getFrom()[0].toString());
        email.setFrom(fromField);
        Map<String, List<ContentPart>> list = getContentParts(message,false);
        email.setSubject(message.getSubject());
        email.setMainContent(list.get("main"));
        if(!list.get("attachments").isEmpty()) {
             email.setAttachments(list.get("attachments"));
        }
        Address[] addresses = message.getAllRecipients();
        List<String> stringAddresses = new ArrayList<String>();
        List<String> mailingListAddresses = new ArrayList<String>();
        for (Address ad : addresses) {
            InternetAddress iad = (InternetAddress) ad;
            stringAddresses.add(iad.getAddress());
            if (mailingLists.contains(iad.getAddress().toLowerCase())) {
            	mailingListAddresses.add(iad.getAddress().toLowerCase());
            }
        }
        if (mailingListAddresses.isEmpty()) {
            System.out.println("Not found mailinglist in addresses :" + stringAddresses);
            System.out.println("Email with messageId " + email.getMessageId() + " was not registered");
            return null;
        }
       
        // now create a clone of the email for each mailinglist
        List<Email> emails = new ArrayList<Email>();
        for (String mailinglist: mailingListAddresses) {
        	Email clone = (Email) email.clone();
        	//mailinglist-specific data setters
        	clone.setMailingList(mailinglist);
        	if (message.getHeader("In-Reply-To") != null) {
        		setInReplyToFor(clone, message.getHeader("In-Reply-To")[0],clone.getMessageMailingList());
        	}
        	setRootFor(clone);
        	emails.add(clone);
        }
        return emails;

    }
    
    private void setRootFor(Email email) {
    	if (email.getInReplyTo() != null) {
            Email parent =(Email) dbClient.getMessage(email.getInReplyTo().getId());
            if (parent.getRoot() == null) {
                email.setRoot(email.getInReplyTo());
            } else {
                email.setRoot(parent.getRoot());
            }
        } else {
        	// if it is a root email, it does not point to another email
            email.setRoot(null);
        }
        
    }

    private String extractEmailAddress(String address) {
        Pattern emailPattern = Pattern.compile("\\S+@\\S+");
        Matcher matcher = emailPattern.matcher(address);
        if (matcher.find()) {
            String email = matcher.group();
            if (email.startsWith("<") && email.endsWith(">")) {
                email = email.substring(1, email.length() - 1);
            }
            return email;
        }
        return null;

    }
    
    private void setInReplyToFor(Email email, String inReplyToMessageID,String mailingListAddress) {
    	String inReplyToID = dbClient.getId(inReplyToMessageID, mailingListAddress);
        if(inReplyToID != null) {
            email.setInReplyTo(dbClient.getMessage(inReplyToID));
        } else {
            email.setInReplyTo(null);
        }
    }

    public boolean saveMessage(Email message) throws MessagingException, IOException {
    	if (dbClient.saveMessage(message)) {
    		if(!sendMessagesToSearchisko) {
    			return true;
    		}
    		if ( searchManager.addEmail(message)) {
    			return true;
    		} else {
    			dbClient.deleteMessage(message);
    			return false;
    		}
    	} else {
    		return false;
    	}
    }

    private Map<String, List<ContentPart>> getContentParts(Part p, boolean mainPartFound) throws
            MessagingException, IOException {
        Map<String, List<ContentPart>> list = new HashMap();
        list.put("main", new ArrayList<ContentPart>());
        list.put("attachments", new ArrayList<ContentPart>());
        if(!Part.ATTACHMENT.equals(p.getDisposition())) {
        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            if (p.isMimeType("text/html")) {
                ContentPart cp = new ContentPart();
                cp.setType("text/html");
                cp.setContent(s);
                if (!mainPartFound) {
                    list.get("main").add(cp);
                } else {
                    list.get("attachments").add(cp);
                }
                return list;
            } else {
                ContentPart cp = new ContentPart();
                cp.setType("text/plain");
                cp.setContent(s);
                if (!mainPartFound) {
                    list.get("main").add(cp);
                } else {
                    list.get("attachments").add(cp);
                }
                return list;
            }

        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    ContentPart cp = new ContentPart();
                    cp.setType("text/plain");
                    cp.setContent(bp.getContent().toString());
                    if (!mainPartFound) {
                        list.get("main").add(cp);
                    } else {
                        list.get("attachments").add(cp);
                    }
                } else if (bp.isMimeType("text/html")) {
                    ContentPart cp = new ContentPart();
                    cp.setType("text/html");
                    cp.setContent(bp.getContent().toString());
                    if (!mainPartFound) {
                        list.get("main").add(cp);
                    } else {
                        list.get("attachments").add(cp);
                    }
                } else {
                    if (!mainPartFound) {
                        list.get("main").addAll(getContentParts(bp,mainPartFound).get("main"));
                    } else {
                        list.get("attachments").addAll(getContentParts(bp,mainPartFound).get("attachments"));;
                    }
                }
            }
            mainPartFound=true;
            return list;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                if (!mainPartFound) {
                        list.get("main").addAll(getContentParts(mp.getBodyPart(i),mainPartFound).get("main"));
                        mainPartFound=true;
                    } else {
                        list.get("attachments").addAll(getContentParts(mp.getBodyPart(i),mainPartFound).get("attachments"));
                    }
            }
        }
        } else {
        	ContentPart cp = new ContentPart();
        	int indexForParsingContentType= p.getContentType().indexOf("name=");
        	String parsedContentType = null;
        	if (indexForParsingContentType != -1) {
   			 parsedContentType = p.getContentType().substring(0,indexForParsingContentType-2);
        	} else {
        		parsedContentType=p.getContentType();
        	}
        	
        	cp.setType(parsedContentType);
        	if(p.getContent() instanceof BASE64DecoderStream) {
        		BASE64DecoderStream base64DecoderStream = (BASE64DecoderStream)p.getContent();
        		 byte[] byteArray = IOUtils.toByteArray(base64DecoderStream);
        		 byte[] encodeBase64 = Base64.encodeBase64(byteArray);
        		 int indexOfFileName = p.getContentType().indexOf("name=");
        		 String fileName = null;
        		 if (indexOfFileName != -1) {
        			 fileName=p.getContentType().substring(indexOfFileName+6);
        			 fileName=fileName.substring(0,fileName.length()-1);
        		 }
        		 String fileId=dbClient.createFile(encodeBase64,fileName,parsedContentType);
        		 cp.setLink(fileId);
        	}
        	list.get("attachments").add(cp);
        }

        return list;
    }

    public boolean createAndSaveMessage(MimeMessage mimeMessage) {
        try {
            List<Email> messages = createMessage(mimeMessage);
            if (messages != null && !messages.isEmpty()) {
            	for (Email message : messages) {
            		saveMessage(message);
            	}
            }
            return true;
        } catch (MessagingException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.INFO, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedMessageException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
