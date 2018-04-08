package com.thumbsup.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class MailIt {
	
    @Autowired
    public JavaMailSender emailSender;	
	
	
	public void tryMail(String to, String subject, String text) {
		System.out.println("trying");
	          SimpleMailMessage message = new SimpleMailMessage(); 
	          message.setTo(to); 
	          message.setFrom("Your Stash<phelanphirewood@gmail.com>");
	          message.setSubject(subject); 
	          message.setText(text);
	          emailSender.send(message);		
	}

	

}
