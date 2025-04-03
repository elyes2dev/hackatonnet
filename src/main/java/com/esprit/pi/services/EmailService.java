package com.esprit.pi.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    public JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Email Verification Code");
        message.setText("Your verification code is: " + verificationCode);
        mailSender.send(message);
    }

    public void sendRecoveryEmail(String toEmail,String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Email Verification Code");
        message.setText("Recovery" + "http://localhost:9100/pi/auth/changePassword?token=" + token );
        mailSender.send(message);
    }
}
