package com.esprit.pi.repositories;

import com.esprit.pi.entities.PasswordResetToken;
import com.esprit.pi.entities.UserVerification;
import com.sun.jdi.InterfaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
