package com.esprit.pi.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

@Service
@AllArgsConstructor
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

    /*public void sendRecoveryEmail(String toEmail,String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Email Verification Code");
        message.setText("Recovery Email : " + "http://localhost:9100/auth/changePassword?token=" + token );
        mailSender.send(message);
    }*/

    public void sendRecoveryEmail(String toEmail, String token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String resetLink = "http://localhost:9100/auth/changePassword?token=" + token;

            String htmlMsg = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head><style>" +
                    "  .btn { background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }" +
                    "  .container { font-family: Arial, sans-serif; padding: 20px; }" +
                    "</style></head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h2>Password Recovery</h2>" +
                    "<p>Click the button below to reset your password:</p>" +
                    "<a href='" + resetLink + "' class='btn'>Reset Password</a>" +
                    "<p>If you didn’t request this, please ignore this email.</p>" +
                    "</div>" +
                    "</body></html>";

            helper.setTo(toEmail);
            helper.setSubject("Reset Your Password");
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();

        }
    }


    // ✅ Updated email sender to attach the image correctly
    public void sendEmailWithLogo(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            // Ensure the image exists in /resources/static/
            helper.addInline("logoImage", new ClassPathResource("static/hackathon-logo.jpg"));

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);  // true means HTML email
        javaMailSender.send(message);
    }
}
