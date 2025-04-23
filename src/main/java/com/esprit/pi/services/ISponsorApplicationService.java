package com.esprit.pi.services;

import com.esprit.pi.dtos.SponsorApplicationDTO;
import com.esprit.pi.entities.SponsorApplication;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ISponsorApplicationService {
    SponsorApplication submitApplication(long userId, SponsorApplication application);
    ResponseEntity<Map<String, Object>> aiVerifyApplication(int applicationId);
    List<SponsorApplication> getAllApplications();
    SponsorApplication getApplicationById(int id);
    SponsorApplicationDTO getApplicationByIdDTO(int id);
    SponsorApplication approveApplication(int id);
    SponsorApplication rejectApplication(int id);
    void deleteApplication(int id);
    void autoRejectExpiredApplications();
}
