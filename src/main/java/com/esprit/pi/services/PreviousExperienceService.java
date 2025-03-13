package com.esprit.pi.services;

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
public class PreviousExperienceService {

    @Autowired
    private PreviousExperienceRepository previousExperienceRepository;

    @Autowired
    private MonitorApplicationRepository monitorApplicationRepository;

    // Create
    @Transactional
    public PreviousExperience createExperience(Long applicationId, PreviousExperience experience) {
        Optional<MonitorApplication> applicationOpt = monitorApplicationRepository.findById(applicationId);
        if (applicationOpt.isPresent()) {
            experience.setApplication(applicationOpt.get());
            return previousExperienceRepository.save(experience);
        }
        return null;
    }

    // Read
    public List<PreviousExperience> getAllExperiences() {
        return previousExperienceRepository.findAll();
    }

    public Optional<PreviousExperience> getExperienceById(Long id) {
        return previousExperienceRepository.findById(id);
    }

    public List<PreviousExperience> getExperiencesByApplicationId(Long applicationId) {
        return previousExperienceRepository.findByApplicationId(applicationId);
    }

    public List<PreviousExperience> getExperiencesByYear(int year) {
        return previousExperienceRepository.findByYear(year);
    }

    public List<PreviousExperience> getExperiencesByHackathonName(String keyword) {
        return previousExperienceRepository.findByHackathonNameContaining(keyword);
    }

    // Update
    @Transactional
    public PreviousExperience updateExperience(Long id, PreviousExperience experience) {
        Optional<PreviousExperience> existingExpOpt = previousExperienceRepository.findById(id);
        if (existingExpOpt.isPresent()) {
            PreviousExperience existingExp = existingExpOpt.get();
            // Update fields but preserve relationships
            existingExp.setYear(experience.getYear());
            existingExp.setDescription(experience.getDescription());
            existingExp.setHackathonName(experience.getHackathonName());
            existingExp.setNumberOfTeamsCoached(experience.getNumberOfTeamsCoached());

            return previousExperienceRepository.save(existingExp);
        }
        return null;
    }

    // Delete
    @Transactional
    public boolean deleteExperience(Long id) {
        if (previousExperienceRepository.existsById(id)) {
            previousExperienceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
