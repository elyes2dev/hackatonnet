package com.esprit.pi;

import com.esprit.pi.services.EmailService;
import com.esprit.pi.utility.VerificationCodeGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;


    VerificationCodeGenerator verificationCodeGenerator;

    @Test
    public void testSendRealEmail() {
        String email = "amine.zneidi@yahoo.com";
        String code = VerificationCodeGenerator.generateVerificationCode();

        // Call method (actually sends email)
        emailService.sendVerificationEmail(email, code);


        assertTrue(true);
    }
}
