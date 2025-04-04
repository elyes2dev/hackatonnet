package com.esprit.pi.repositories;

import com.esprit.pi.entities.PasswordResetToken;
import com.esprit.pi.entities.UserVerification;
import com.sun.jdi.InterfaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    @Query("SELECT t FROM PasswordResetToken t WHERE t.token = :token")
    PasswordResetToken findByToken(@Param("token") String token);
}
