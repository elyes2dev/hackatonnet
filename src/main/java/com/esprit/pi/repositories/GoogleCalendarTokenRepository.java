package com.esprit.pi.repositories;

import com.esprit.pi.entities.GoogleCalendarToken;
import com.esprit.pi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleCalendarTokenRepository extends JpaRepository<GoogleCalendarToken, Long> {
    Optional<GoogleCalendarToken> findByUser(User user);
    void deleteByUser(User user);
}