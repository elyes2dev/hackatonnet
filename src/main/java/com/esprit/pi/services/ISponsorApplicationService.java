package com.esprit.pi.services;

import com.esprit.pi.dtos.SponsorApplicationDTO;
import com.esprit.pi.entities.SponsorApplication;

import java.util.List;

public interface ISponsorApplicationService {
    SponsorApplication submitApplication(long userId, SponsorApplication application);
    List<SponsorApplication> getAllApplications();
    SponsorApplication getApplicationById(int id);
    SponsorApplicationDTO getApplicationByIdDTO(int id);
    SponsorApplication approveApplication(int id);
    SponsorApplication rejectApplication(int id);
    void deleteApplication(int id);
    void autoRejectExpiredApplications();
}
