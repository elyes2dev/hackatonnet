package com.esprit.pi.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService implements IEmailService{

    public JavaMailSender mailSender;

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
}
