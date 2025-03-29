package com.esprit.pi.Service;

import com.esprit.pi.Repository.ISponsorNotificationRepository;
import com.esprit.pi.entities.SponsorNotification;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class SponsorNotificationService {

    @Autowired
    ISponsorNotificationRepository sponsorNotificationRepository;

    // Create a global notification for all admins
    public void createGlobalNotification(String message) {
        SponsorNotification notification = SponsorNotification.builder()
                .message(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
        sponsorNotificationRepository.save(notification);
    }

    // Fetch all unread notifications (global for all admins)
    public List<SponsorNotification> getUnreadNotifications() {
        return sponsorNotificationRepository.findByIsReadFalse();
    }

    // Mark a notification as read
    public void markAsRead(Long notificationId) {
        SponsorNotification notification = sponsorNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        sponsorNotificationRepository.save(notification);
    }
}
