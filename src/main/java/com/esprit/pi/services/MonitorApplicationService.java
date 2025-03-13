package com.esprit.pi.services;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.MonitorApplication;
import com.esprit.pi.entities.PreviousExperience;
import com.esprit.pi.repositories.MonitorApplicationRepository;
import com.esprit.pi.repositories.PreviousExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MonitorApplicationService {

    @Autowired
    private MonitorApplicationRepository monitorApplicationRepository;

    @Autowired
    private PreviousExperienceRepository previousExperienceRepository;

    // Create
    @Transactional
    public MonitorApplication createApplication(MonitorApplication application) {
        // Set any defaults if needed
        if (application.getStatus() == null) {
            application.setStatus(ApplicationStatus.PENDING);
        }

        // Save the application first
        MonitorApplication savedApplication = monitorApplicationRepository.save(application);

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
    public List<MonitorApplication> getAllApplications() {
        return monitorApplicationRepository.findAll();
    }

    public Optional<MonitorApplication> getApplicationById(Long id) {
        return monitorApplicationRepository.findById(id);
    }

    public List<MonitorApplication> getApplicationsByMentorId(Long mentorId) {
        return monitorApplicationRepository.findByMentorId(mentorId);
    }

    public List<MonitorApplication> getApplicationsByStatus(ApplicationStatus status) {
        return monitorApplicationRepository.findByStatus(status);
    }

    public List<MonitorApplication> getApplicationsByExperience(boolean hasPreviousExperience) {
        return monitorApplicationRepository.findByHasPreviousExperience(hasPreviousExperience);
    }

    // Update
    @Transactional
    public MonitorApplication updateApplication(Long id, MonitorApplication application) {
        Optional<MonitorApplication> optionalApp = monitorApplicationRepository.findById(id);
        if (optionalApp.isPresent()) {
            MonitorApplication existingApp = optionalApp.get();
            // Transfer properties but keep ID, creation date and relationships
            existingApp.setApplicationText(application.getApplicationText());
            existingApp.setCv(application.getCv());
            existingApp.setUploadPaper(application.getUploadPaper());
            existingApp.setLinks(application.getLinks());
            existingApp.setHasPreviousExperience(application.isHasPreviousExperience());
            if (application.getStatus() != null) {
                existingApp.setStatus(application.getStatus());
            }

            return monitorApplicationRepository.save(existingApp);
        }
        return null;
    }

    // Update status specifically
    @Transactional
    public MonitorApplication updateApplicationStatus(Long id, ApplicationStatus newStatus) {
        Optional<MonitorApplication> optionalApp = monitorApplicationRepository.findById(id);
        if (optionalApp.isPresent()) {
            MonitorApplication app = optionalApp.get();
            app.setStatus(newStatus);
            return monitorApplicationRepository.save(app);
        }
        return null;
    }

    // Delete
    @Transactional
    public boolean deleteApplication(Long id) {
        if (monitorApplicationRepository.existsById(id)) {
            monitorApplicationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}