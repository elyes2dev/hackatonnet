package com.esprit.pi.services;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.MentorApplication;
import com.esprit.pi.entities.PreviousExperience;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.MentorApplicationRepository;
import com.esprit.pi.repositories.PreviousExperienceRepository;
import com.esprit.pi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Create
    @Transactional
    public MentorApplication createApplication(MentorApplication application) {
        // Set any defaults if needed
        if (application.getStatus() == null) {
            application.setStatus(ApplicationStatus.PENDING);
        }

        if (application.getPreviousExperiences() != null) {
            for (PreviousExperience experience : application.getPreviousExperiences()) {
                experience.setApplication(application);
            }
        }

        // Save the application first
        MentorApplication savedApplication = mentorApplicationRepository.save(application);

        // If there are previous experiences, set their application reference and save them
        if (application.getPreviousExperiences() != null) {
            for (PreviousExperience exp : application.getPreviousExperiences()) {
                exp.setApplication(savedApplication);
            }
            // No need to save experiences individually, they'll be cascaded
        }

        return savedApplication;
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

    // Update
    @Transactional
    public MentorApplication updateApplication(Long id, MentorApplication application) {
        Optional<MentorApplication> optionalApp = mentorApplicationRepository.findById(id);
        if (optionalApp.isPresent()) {
            MentorApplication existingApp = optionalApp.get();
            // Transfer properties but keep ID, creation date and relationships
            existingApp.setApplicationText(application.getApplicationText());
            existingApp.setCv(application.getCv());
            existingApp.setUploadPaper(application.getUploadPaper());
            existingApp.setLinks(application.getLinks());
            existingApp.setHasPreviousExperience(application.isHasPreviousExperience());
            if (application.getStatus() != null) {
                existingApp.setStatus(application.getStatus());
            }

            return mentorApplicationRepository.save(existingApp);
        }
        return null;
    }

    // Update status specifically
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
    public boolean deleteApplication(Long id) {
        if (mentorApplicationRepository.existsById(id)) {
            mentorApplicationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}