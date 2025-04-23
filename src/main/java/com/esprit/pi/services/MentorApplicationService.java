package com.esprit.pi.services;

import com.esprit.pi.entities.*;
import com.esprit.pi.repositories.MentorApplicationRepository;
import com.esprit.pi.repositories.PreviousExperienceRepository;
import com.esprit.pi.repositories.RoleRepository;
import com.esprit.pi.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MentorApplicationService {

    @Autowired
    private MentorApplicationRepository mentorApplicationRepository;

    @Autowired
    private PreviousExperienceRepository previousExperienceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RoleRepository roleRepository;

    // Create
    @Transactional
    public MentorApplication createApplication(MentorApplication application,
                                               MultipartFile cvFile,
                                               MultipartFile uploadPaperFile) throws IOException {
        // Set default status if null
        if (application.getStatus() == null) {
            application.setStatus(ApplicationStatus.PENDING);
        }

        // Save CV file
        if (cvFile != null && !cvFile.isEmpty()) {
            String cvFilename = fileStorageService.storeFile(cvFile);
            application.setCv(cvFilename);
        } else {
            throw new IllegalArgumentException("CV file is required");
        }

        // Save optional upload paper file
        if (uploadPaperFile != null && !uploadPaperFile.isEmpty()) {
            String uploadPaperFilename = fileStorageService.storeFile(uploadPaperFile);
            application.setUploadPaper(uploadPaperFilename);
        }

        // Handle previous experiences
        if (application.getPreviousExperiences() != null) {
            for (PreviousExperience experience : application.getPreviousExperiences()) {
                experience.setApplication(application);
            }
        }

        return mentorApplicationRepository.save(application);
    }

    // Read
    public List<MentorApplication> getAllApplications() {
        return mentorApplicationRepository.findAll();
    }

    public Optional<MentorApplication> getApplicationById(Long id) {
        return mentorApplicationRepository.findById(id);
    }

    public List<MentorApplication> getApplicationsByUserId(Long userId) {
        return mentorApplicationRepository.findByUserId(userId);
    }

    public List<MentorApplication> getApplicationsByStatus(ApplicationStatus status) {
        return mentorApplicationRepository.findByStatus(status);
    }

    public List<MentorApplication> getApplicationsByExperience(boolean hasPreviousExperience) {
        return mentorApplicationRepository.findByHasPreviousExperience(hasPreviousExperience);
    }

    // Update with file upload
    @Transactional
    public MentorApplication updateApplication(Long id,
                                               MentorApplication application,
                                               MultipartFile cvFile,
                                               MultipartFile uploadPaperFile) throws IOException {
        Optional<MentorApplication> optionalApp = mentorApplicationRepository.findById(id);
        if (optionalApp.isPresent()) {
            MentorApplication existingApp = optionalApp.get();

            // Update CV if new file provided
            if (cvFile != null && !cvFile.isEmpty()) {
                // We don't need to delete the old file since the storeFile method
                // will replace existing files with the same name
                String cvFilename = fileStorageService.storeFile(cvFile);
                existingApp.setCv(cvFilename);
            }

            // Update upload paper if new file provided
            if (uploadPaperFile != null && !uploadPaperFile.isEmpty()) {
                // We don't need to delete the old file since the storeFile method
                // will replace existing files with the same name
                String uploadPaperFilename = fileStorageService.storeFile(uploadPaperFile);
                existingApp.setUploadPaper(uploadPaperFilename);
            }

            // Update other fields
            existingApp.setApplicationText(application.getApplicationText());
            existingApp.setLinks(application.getLinks());
            existingApp.setHasPreviousExperience(application.isHasPreviousExperience());
            if (application.getStatus() != null) {
                existingApp.setStatus(application.getStatus());
            }

            return mentorApplicationRepository.save(existingApp);
        }
        return null;
    }

    // Update status specifically with automatic mentor activation
    @Transactional
    public MentorApplication updateApplicationStatus(Long id, ApplicationStatus newStatus) {
        Optional<MentorApplication> optionalApp = mentorApplicationRepository.findById(id);
        if (optionalApp.isPresent()) {
            MentorApplication app = optionalApp.get();
            ApplicationStatus oldStatus = app.getStatus();
            app.setStatus(newStatus);

            // If the application is approved, update the user to be a mentor
            if (newStatus == ApplicationStatus.APPROVED) {
                User user = app.getUser();
                if (user != null) {
                    Role mentorRole = roleRepository.findByName("MENTOR");
                    if (mentorRole == null) {
                        throw new RuntimeException("MENTOR role not found");
                    }
                    user.getRoles().add(mentorRole);
                    userRepository.save(user);
                sendStatusUpdateEmail(app, true);}
            }
            // If the application is rejected, send rejection email
            else if (newStatus == ApplicationStatus.REJECTED) {
                sendStatusUpdateEmail(app, false);
            }

            return mentorApplicationRepository.save(app);
        }
        return null;
    }


    /**
     * Sends an email notification to the applicant about their application status
     * @param application The mentor application
     * @param isApproved Whether the application was approved or rejected
     */
    private void sendStatusUpdateEmail(MentorApplication application, boolean isApproved) {
        User user = application.getUser();
        String userEmail = user.getEmail();
        String userName = user.getName() + " " + user.getLastname();

        String subject;
        String body;

        if (isApproved) {
            subject = "Congratulations! Your Mentor Application Has Been Approved";
            body = "Dear " + userName + ",\n\n" +
                    "We are pleased to inform you that your application to become a mentor has been approved. " +
                    "You can now access mentor features on our platform.\n\n" +
                    "Thank you for your interest in sharing your knowledge and expertise with our community.\n\n" +
                    "Best regards,\nThe Team";
        } else {
            subject = "Update on Your Mentor Application";
            body = "Dear " + userName + ",\n\n" +
                    "Thank you for your interest in becoming a mentor on our platform. " +
                    "After careful review, we regret to inform you that your application has not been approved at this time.\n\n" +
                    "We appreciate your understanding and encourage you to apply again in the future.\n\n" +
                    "Best regards,\nThe Team";
        }

        // Use the existing email service to send the notification
        emailService.sendSimpleEmail(userEmail, subject, body);
    }

    // Delete
    @Transactional
    public boolean deleteApplication(Long id) {
        try {
            // Check if application exists
            Optional<MentorApplication> applicationOpt = mentorApplicationRepository.findById(id);
            if (applicationOpt.isEmpty()) {
                return false;
            }

            MentorApplication application = applicationOpt.get();

            // Step 1: Remove the relationship with User (if it's bi-directional)
            // This is important to break the bidirectional reference that might cause issues
            User user = application.getUser();
            if (user != null && user.getMentorApplication() != null) {
                user.setMentorApplication(null);
                userRepository.save(user);
            }

            // Step 2: Clear collections explicitly
            if (application.getPreviousExperiences() != null) {
                // Handle previous experiences properly
                application.getPreviousExperiences().clear();
            }

            if (application.getLinks() != null) {
                application.getLinks().clear();
            }

            // Save changes before deletion to ensure collections are properly cleared
            mentorApplicationRepository.saveAndFlush(application);

            // Step 3: Now delete the application
            mentorApplicationRepository.delete(application);
            mentorApplicationRepository.flush();

            // Verify deletion
            return !mentorApplicationRepository.existsById(id);
        } catch (Exception e) {
            // Log the error with proper details
            return false;
        }
    }

    // Get file as Resource for download
    public Resource getFileAsResource(String filename) {
        return fileStorageService.loadFileAsResource(filename);
    }



}