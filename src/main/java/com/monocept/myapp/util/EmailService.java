package com.monocept.myapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}") // Use quotes for property key
    private String fromMail;

    public void sendEmail(String subject, String body, List<String> recipients) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(body);
        message.setTo(recipients.toArray(new String[0])); // Convert list to array
        
        message.setFrom(fromMail); // Use the injected email address
        
        mailSender.send(message);
    }
}
