package test.aqruillian;

import mailinglistonline.server.export.database.entities.Email;

import org.bson.types.ObjectId;

public class EmailCreator {
	public static Email createSimpleEmail() {
		Email alwaysReturnedEmail = new Email();
		alwaysReturnedEmail.put("_id",new ObjectId("4edd40c86762e0fb12000003"));
		alwaysReturnedEmail.setFrom("test");
		return alwaysReturnedEmail;
	}
	
	public static Email createSimpleEmail(String id) {
		Email alwaysReturnedEmail = new Email();
		alwaysReturnedEmail.put("_id",new ObjectId(id));
		alwaysReturnedEmail.setFrom("test");
		return alwaysReturnedEmail;
	}
}
