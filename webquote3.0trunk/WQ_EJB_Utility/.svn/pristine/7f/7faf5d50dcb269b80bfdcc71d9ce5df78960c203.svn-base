package com.avnet.emasia.webquote.utilities.ejb;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * mail authentication.
 * 
 * @author 914975
 */
public class MailAuthenticator extends Authenticator {

	String userName = null;
	String password = null;

	public MailAuthenticator() {
	}

	public MailAuthenticator(String username, String password) {
		this.userName = username;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, password);
	}
}
