package com.smart.Services;

import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailServices {
	//text send only
    public boolean sendEmail(String subject, String message, String to) {
    	//set important properties;
    	boolean f=false;
    	
    	String host="smtp.gmail.com";
    	Properties p=System.getProperties();
    	p.put("mail.smtp.host", host);
    	p.put("mail.smtp.port", "465");
    	p.put("mail.smtp.ssl.enable", "true");
    	p.put("mail.smtp.auth", "true");
    	
    	//get Session object
    	Session s=Session.getInstance(p,new Authenticator() {
			@Override
//		encrypt-	devvnyvbiacdnelwcov
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("email","password");
			}
        	});
    	s.setDebug(true);

    	//compose the message
    	MimeMessage m=new MimeMessage(s);
    	try {
    		m.setFrom("email");
    		m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    	m.setSubject(subject);
//    	m.setText(message);
    	
//    	for html send into email
    	m.setContent(message,"text/html");

    	Transport.send(m);
    	System.out.println("mail send successfully......");
    	f=true;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
 
    	}
 return f;
    }
}
