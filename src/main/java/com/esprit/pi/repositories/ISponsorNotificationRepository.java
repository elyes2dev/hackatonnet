package com.esprit.pi.repositories;

import com.esprit.pi.entities.SponsorNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISponsorNotificationRepository extends JpaRepository<SponsorNotification,Long> {
    List<SponsorNotification> findByIsReadFalse(); // Get all unread notifications
}
