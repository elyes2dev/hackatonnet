package com.esprit.pi.services;

import com.esprit.pi.dtos.SponsorApplicationDTO;
import com.esprit.pi.dtos.UserDTO;
import com.esprit.pi.dtos.VerificationResultDTO;
import com.esprit.pi.repositories.IRoleRepository;
import com.esprit.pi.repositories.ISponsorApplicationRepository;
import com.esprit.pi.repositories.ISponsorRewardRepository;
import com.esprit.pi.repositories.IUserRepository;
import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.Role;
import com.esprit.pi.entities.SponsorApplication;
import com.esprit.pi.entities.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class SponsorApplicationService implements ISponsorApplicationService{

    @Autowired
    ISponsorApplicationRepository sponsorApplicationRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    SponsorRewardService sponsorRewardService;
    @Autowired
    private EmailService emailService;
    @Autowired
    SponsorNotificationService sponsorNotificationService;
    @Autowired
    ISponsorRewardRepository sponsorRewardRepository;
    @Autowired
    OCRService ocrService;
    @Autowired
    CompanyVerifier companyVerifier;
    @Autowired
    SponsorVerificationService sponsorVerificationService;


    static final int EXPIRATION_MINUTES = 5; // 5 minutes for testing


    public SponsorApplication submitApplication(long userId, SponsorApplication application) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (sponsorApplicationRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("User already submitted an application.");
        }

        application.setUser(user);
        application.setStatus(ApplicationStatus.PENDING);
        application.setSubmittedAt(LocalDateTime.now());

        SponsorApplication savedApp = sponsorApplicationRepository.save(application);

        // 🔔 Send notification to admins
        String message = String.format(
                "New sponsor application submitted by %s (Company: %s)",
                user.getName(),
                application.getCompanyName()
        );
        sponsorNotificationService.createGlobalNotification(message);

        return savedApp;
    }

    public ResponseEntity<Map<String, Object>> aiVerifyApplication(int applicationId) {
        SponsorApplication application = getApplicationById(applicationId);
        String documentPath = "uploads/" + application.getDocumentPath();

        VerificationResultDTO result = sponsorVerificationService.verifyDocument(documentPath);

        Map<String, Object> response = new HashMap<>();
        response.put("message", result.getMessage());
        response.put("success", result.isSuccess());
        response.put("fieldMatches", result.getFieldMatches());
        response.put("detectedCompanyName", result.getDetectedCompanyName());

        if (result.isSuccess()) {
            approveApplication(applicationId);
            response.put("status", "APPROVED");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "REQUIRES_MANUAL_REVIEW");
            return ResponseEntity.status(422).body(response);
        }
    }


    public List<SponsorApplication> getAllApplications() {
        return sponsorApplicationRepository.findAll();
    }

    public SponsorApplication getApplicationById(int id) {
        return sponsorApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    public SponsorApplicationDTO getApplicationByIdDTO(int id) {
        SponsorApplication application = sponsorApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        User user = application.getUser();

        UserDTO userDTO = new UserDTO(user.getId(), user.getName(),user.getLastname(), user.getEmail());

        return new SponsorApplicationDTO(
                application.getId(),
                application.getCompanyName(),
                application.getCompanyLogo(),
                application.getDocumentPath(),
                application.getRegistrationNumber(),
                application.getWebsiteUrl(),
                application.getStatus(),
                application.getSubmittedAt(),
                application.getReviewedAt(),
                userDTO // Use UserDTO instead of separate fields
        );
    }

    public SponsorApplication approveApplication(int id) {
        SponsorApplication application = getApplicationById(id);
        application.setStatus(ApplicationStatus.APPROVED);
        application.setReviewedAt(LocalDateTime.now());

        User user = application.getUser();

        // Fetch the SPONSOR role from the database
        Role sponsorRole = roleRepository.findByName("SPONSOR")
                .orElseThrow(() -> new RuntimeException("Role SPONSOR not found"));

        // Add role if the user doesn't already have it
        if (!user.getRoles().contains(sponsorRole)) {
            user.getRoles().add(sponsorRole);
            userRepository.save(user);
        }

        // Initialize SponsorReward record for the new sponsor
        if (user.getSponsorReward() == null) {
            sponsorRewardService.createSponsorReward(user);
        }
        // ✅ Send approval email
        sendApplicationStatusEmail(user, "APPROVED");

        return sponsorApplicationRepository.save(application);
    }

    public SponsorApplication rejectApplication(int id) {
        SponsorApplication application = getApplicationById(id);
        application.setStatus(ApplicationStatus.REJECTED);
        application.setReviewedAt(LocalDateTime.now());

        // ✅ Send rejection email
        sendApplicationStatusEmail(application.getUser(), "REJECTED");

        return sponsorApplicationRepository.save(application);
    }

    @Transactional
    public void deleteApplication(int id) {
        SponsorApplication application = sponsorApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        User user = application.getUser();

        if (user != null) {
            // If the application was approved, handle the SponsorReward and SPONSOR role
            if (application.getStatus() == ApplicationStatus.APPROVED) {
                // Remove the sponsorReward if it exists
                if (user.getSponsorReward() != null) {
                    user.setSponsorReward(null);  // Remove the SponsorReward from the User
                }

                // Delete the associated SponsorReward if it exists
                sponsorRewardRepository.findBySponsorId(user.getId()).ifPresent(reward -> {
                    sponsorRewardRepository.delete(reward); // Delete the reward explicitly
                });

                // Remove SPONSOR role if the user has it
                Role sponsorRole = roleRepository.findByName("SPONSOR")
                        .orElseThrow(() -> new RuntimeException("Role SPONSOR not found"));

                if (user.getRoles().contains(sponsorRole)) {
                    user.getRoles().remove(sponsorRole); // Remove the role from the user
                    userRepository.save(user); // Save the user to persist the change
                }
            }

            // Clean up references (this part is applicable for both pending, approved, and rejected applications)
            user.setSponsorApplication(null);
            application.setUser(null);
            userRepository.save(user); // Save user to persist the change
        }

        // Finally, delete the application
        sponsorApplicationRepository.delete(application);
    }





    @Scheduled(cron = "0 * * * * ?") // Run every minute for testing
    public void autoRejectExpiredApplications() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusMinutes(EXPIRATION_MINUTES);

        List<SponsorApplication> expiredApplications = sponsorApplicationRepository
                .findByStatusAndSubmittedAtBefore(ApplicationStatus.PENDING, expirationThreshold);

        if (!expiredApplications.isEmpty()) {
            expiredApplications.forEach(application -> {
                application.setStatus(ApplicationStatus.REJECTED);
                application.setReviewedAt(LocalDateTime.now());
                sendApplicationStatusEmail(application.getUser(), "REJECTED (Expired)");
            });

            sponsorApplicationRepository.saveAll(expiredApplications);
            System.out.println("Auto-rejected " + expiredApplications.size() + " expired applications.");
        }
    }

    // Check for pending applications every 2 minutes
    @Scheduled(cron = "0 * * * * ?") // Run every minute for testing
    public void notifyAdminsForPendingApplications() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(2);

        List<SponsorApplication> pendingApplications = sponsorApplicationRepository
                .findByStatusAndSubmittedAtBeforeAndNotifiedFalse(ApplicationStatus.PENDING, threshold);

        if (!pendingApplications.isEmpty()) {
            pendingApplications.forEach(application -> {
                // Include company name in the notification message
                String message = String.format(
                        "Pending application from %s (Company: %s)",
                        application.getUser().getName(),
                        application.getCompanyName()
                );

                // Send notification
                sponsorNotificationService.createGlobalNotification(message);

                // Mark as notified
                application.setNotified(true);
                sponsorApplicationRepository.save(application);
            });
        }
    }

    private void sendApplicationStatusEmail(User user, String status) {
        String subject = "Hackathon Sponsor Application - Status Update";

        String content = String.format("""
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <style>
                body {
                    font-family: Arial, sans-serif;
                    line-height: 1.6;
                    color: #333;
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                    background-color: #f4f4f4;
                }
                .email-container {
                    background-color: white;
                    border-radius: 8px;
                    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                    overflow: hidden;
                }
                .header {
                    background-color: #0066cc;
                    color: white;
                    text-align: center;
                    padding: 20px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                .logo {
                    max-height: 60px;
                    margin-right: 20px;
                }
                .header h1 {
                    margin: 0;
                    font-size: 1.5em;
                }
                .content {
                    padding: 20px;
                }
                .status {
                    font-weight: bold;
                    text-transform: uppercase;
                    display: inline-block;
                    padding: 5px 10px;
                    border-radius: 4px;
                    margin-bottom: 15px;
                }
                .footer {
                    background-color: #f4f4f4;
                    color: #666;
                    text-align: center;
                    padding: 10px;
                    font-size: 0.8em;
                    border-top: 1px solid #e0e0e0;
                }
                .signature {
                    margin-top: 20px;
                    font-style: italic;
                    color: #555;
                }
                a {
                    color: #0066cc;
                    text-decoration: none;
                }
                a:hover {
                    text-decoration: underline;
                }
            </style>
        </head>
        <body>
            <div class="email-container">
                <div class="header">
                    <img src="cid:logoImage" alt="Hackathon Logo" class="logo">
                    <h1>Sponsor Application Status</h1>
                </div>

                <div class="content">
                    <p>Dear %s,</p>

                    <p>We are writing to inform you about the status of your sponsor application for our upcoming Hackathon event.</p>

                    <div class="status" style="background-color: %s; color: white;">
                        Application Status: %s
                    </div>

                    %s

                    <div class="signature">
                        <p>Best regards,<br>
                        The Hackathon Sponsorship Team</p>

                        <p>Questions? Contact us at <a href="mortadhabennaceur390@gmail.com">mortadhabennaceur390@gmail.com</a></p>
                    </div>
                </div>

                <div class="footer">
                    <p>© %d Hackathon Event | Empowering Innovation | <a href="https://www.ourhackathon.com">www.ourhackathon.com</a></p>
                </div>
            </div>
        </body>
        </html>
        """,
                user.getName(),
                determineStatusColor(status),
                status,
                getAdditionalMessage(status),
                java.time.Year.now().getValue()
        );

        emailService.sendEmailWithLogo(user.getEmail(), subject, content);
    }

    // Helper method to determine color based on status
    private String determineStatusColor(String status) {
        return switch (status.toUpperCase()) {
            case "APPROVED" -> "#28a745";  // Green for approved
            case "REJECTED" -> "#dc3545";  // Red for rejected
            case "PENDING" -> "#ffc107";   // Yellow for pending
            default -> "#007bff";          // Blue for other statuses
        };
    }

    // Helper method to add contextual messages
    private String getAdditionalMessage(String status) {
        return switch (status.toUpperCase()) {
            case "APPROVED" -> """
            <p>Congratulations! We are excited to have you as a valued sponsor for our upcoming Hackathon. 
            Our team will be in touch soon with further details about your sponsorship benefits and next steps.</p>
        """;
            case "REJECTED" -> """
            <p>After careful review, we regret to inform you that your application did not meet our current sponsorship criteria. 
            We appreciate your interest and encourage you to apply again in future events.</p>
        """;
            case "PENDING" -> """
            <p>Your application is currently under review. Our sponsorship team is carefully evaluating 
            your submission and will provide a final decision soon.</p>
        """;
            default -> """
            <p>Thank you for your interest in our Hackathon event. We will review your application 
            and get back to you with further information.</p>
        """;
        };
    }

}
