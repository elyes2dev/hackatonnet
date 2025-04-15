package com.esprit.pi.services;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.MentorApplication;
import com.esprit.pi.entities.PreviousExperience;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.MentorApplicationRepository;
import com.esprit.pi.repositories.PreviousExperienceRepository;
import com.esprit.pi.repositories.UserRepository;
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
            app.setStatus(newStatus);

            // If the application is approved, update the user to be a mentor
            if (newStatus == ApplicationStatus.APPROVED) {
                User user = app.getUser();
                // user.setMentor(true);************ to do when merged with user
                userRepository.save(user);
            }

            return mentorApplicationRepository.save(app);
        }
        return null;
    }

    // Delete
    @Transactional
    public boolean deleteApplication(Long id) throws IOException {
        Optional<MentorApplication> optionalApp = mentorApplicationRepository.findById(id);
        if (optionalApp.isPresent()) {
            MentorApplication app = optionalApp.get();

            // No file deletion needed since we're just deleting the application record
            // The file management is handled separately

            // Delete the application
            mentorApplicationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Get file as Resource for download
    public Resource getFileAsResource(String filename) {
        return fileStorageService.loadFileAsResource(filename);
    }
}