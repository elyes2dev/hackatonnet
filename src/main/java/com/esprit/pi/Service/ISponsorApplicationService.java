package com.esprit.pi.Service;

import com.esprit.pi.entities.SponsorApplication;

import java.util.List;

public interface ISponsorApplicationService {
    SponsorApplication submitApplication(long userId, SponsorApplication application);
    List<SponsorApplication> getAllApplications();
    SponsorApplication getApplicationById(int id);
    SponsorApplication approveApplication(int id);
    SponsorApplication rejectApplication(int id);
    void deleteApplication(int id);
}
