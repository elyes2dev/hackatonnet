package com.esprit.pi.controllers;

import com.esprit.pi.services.SponsorNotificationService;
import com.esprit.pi.entities.SponsorNotification;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class SponsorNotificationController {

    @Autowired
    SponsorNotificationService sponsorNotificationService;

    // Get all unread notifications (visible to all admins)
    @GetMapping("/unread")
    public List<SponsorNotification> getUnreadNotifications() {
        return sponsorNotificationService.getUnreadNotifications();
    }

    // Mark a notification as read
    @PostMapping("/mark-as-read/{id}")
    public void markAsRead(@PathVariable Long id) {
        sponsorNotificationService.markAsRead(id);
    }
}
