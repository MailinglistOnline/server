/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailinglist;

import exceptions.MalformedMessageException;
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
import mailinglist.entities.ContentPart;
import mailinglist.entities.Email;

/**
 *
 * @author matej
 */
public class MessageManager {

    private DbClient dbClient;
    private final ArrayList<String> mailingLists;
    private static String MAILINGLISTS_PROPERTIES_FILE_NAME = "mailinglists.properties";

    public MessageManager(DbClient dbClient) throws IOException {
        this.dbClient = dbClient;

        mailingLists = new ArrayList<String>();
        Properties prop = new Properties();
        prop.load(DbClient.class.getClassLoader().getResourceAsStream((MAILINGLISTS_PROPERTIES_FILE_NAME)));
        String mailinglist = prop.getProperty("mailinglist." + 1);
        int i = 1;
        while (mailinglist != null) {
            mailingLists.add(mailinglist);
            i++;
            mailinglist = prop.getProperty("mailinglist." + i);
        }
    }

    public Email createMessage(MimeMessage message) throws MessagingException, IOException, MalformedMessageException {
        if (message.getMessageID() == null && message.getFrom() == null) {
            throw new MalformedMessageException();
        }
        Email email = new Email();
        email.setMessageId(message.getMessageID());
        email.setSentDate(message.getSentDate());

        String fromField = extractEmailAddress(message.getFrom()[0].toString());
        email.setFrom(fromField);
        Map<String, List<ContentPart>> list = getContentParts(message,false);
        email.setSubject(message.getSubject());
        email.setMainContent(list.get("main"));
        if(!list.get("attachments").isEmpty()) {
             email.setAttachments(list.get("attachments"));
        }
        Address[] addresses = message.getAllRecipients();
        for (Address ad : addresses) {
            InternetAddress iad = (InternetAddress) ad;
            if (mailingLists.contains(iad.getAddress())) {
                email.addMailingList(iad.getAddress());
            }

        }

        if (message.getHeader("In-Reply-To") != null) {
            String inReplyTo = dbClient.getId(message.getHeader("In-Reply-To")[0], email.getMessageMailingLists());
            email.setInReplyTo(inReplyTo);
        }
        //setRoot

        if (email.getInReplyTo() != null) {
            Email parent =(Email) dbClient.getMessage(email.getInReplyTo());
            
            if ("true".equals(parent.getRoot())) {
                email.setRoot(email.getInReplyTo());
            } else {
                email.setRoot(parent.getRoot());
            }
        } else {
            email.setRoot("true");
        }

        return email;

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

    public boolean saveMessage(Email message) throws MessagingException, IOException {
        dbClient.saveMessage(message);
        return true;
    }

    private Map<String, List<ContentPart>> getContentParts(Part p, boolean mainPartFound) throws
            MessagingException, IOException {
        Map<String, List<ContentPart>> list = new HashMap();
        list.put("main", new ArrayList<ContentPart>());
        list.put("attachments", new ArrayList<ContentPart>());
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
            return list;
        }

        return list;
    }

    public boolean createAndSaveMessage(MimeMessage mimeMessage) {
        try {
            Email message = createMessage(mimeMessage);
            saveMessage(message);
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
