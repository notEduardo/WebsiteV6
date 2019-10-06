package com.eduardo.web.controller;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.web.model.Friend;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class FriendController{
	
	@Autowired
	public JavaMailSender emailSender;
	 
	@PostMapping(path="/friend")
    public String getBook(@RequestBody Friend request) {
		String status = "SUCCESS";
		String html = makeHTML(request);
		
	     MimeMessage message = emailSender.createMimeMessage();
	     MimeMessageHelper helper = new MimeMessageHelper(message);
	     try {
			helper.setTo("eduardoplusplus@gmail.com");
			helper.setFrom(new InternetAddress("EduardoCanHelp.com"));
		    helper.setSubject("New Friend");
		    helper.setText(html, true);
		    emailSender.send(message);  
		} catch (MessagingException e1) {
			e1.printStackTrace();
			status = "FAIL";
		}
		
		return "{\"status\" : \"" + status + "\"}";
    }

	private String makeHTML(Friend f){
		String html = "<div style=\"width: 100%; color: blue;\">";
		
		html += "<h1> Hi my name is " + f.getName() + "</h1>";
		html += "<h3>" + f.getComment() + "</h3>";
		html += "<br>";
		html += "<h4>Phone: " + f.getPhone() + "</h4>";
		html += "<h4>Email: " + f.getEmail() + "</h4>";
		
		html += "</div>";
		return html;
	}
}
