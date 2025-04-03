package com.esprit.pi.services;

import com.esprit.pi.entities.PasswordResetToken;
import com.esprit.pi.entities.UserVerification;
import com.esprit.pi.repositories.PasswordResetTokenRepository;
import com.esprit.pi.repositories.UserVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class VerificationService {

    @Autowired
    private UserVerificationRepository verificationRepository;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    public boolean verifyCode(String email, String code) {
        UserVerification userVerification = verificationRepository.findByEmail(email);
        if (userVerification != null && userVerification.getVerificationCode().equals(code)) {
            // Verification successful
            return true;
        }
        // Verification failed
        return false;
    }

    public String validatePasswordResetToken(String token) {
        String cleanedToken = token.trim().replaceAll("[\\r\\n]+", "");
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(cleanedToken);
        System.out.println("the output is " + passToken.getToken());

        return !isTokenFound(passToken) ? "invalidToken" : isTokenExpired(passToken) ? "expired" : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
