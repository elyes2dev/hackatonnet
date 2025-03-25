package com.esprit.pi.Service;

import com.esprit.pi.Repository.IRoleRepository;
import com.esprit.pi.Repository.ISponsorApplicationRepository;
import com.esprit.pi.Repository.IUserRepository;
import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.Role;
import com.esprit.pi.entities.SponsorApplication;
import com.esprit.pi.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    static final int EXPIRATION_MINUTES = 2; // 2 minutes for testing


    public SponsorApplication submitApplication(long userId, SponsorApplication application) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (sponsorApplicationRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("User already submitted an application.");
        }

        application.setUser(user);
        application.setStatus(ApplicationStatus.PENDING);
        application.setSubmittedAt(LocalDateTime.now());
        return sponsorApplicationRepository.save(application);
    }

    public List<SponsorApplication> getAllApplications() {
        return sponsorApplicationRepository.findAll();
    }

    public SponsorApplication getApplicationById(int id) {
        return sponsorApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
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

        return sponsorApplicationRepository.save(application);
    }

    public SponsorApplication rejectApplication(int id) {
        SponsorApplication application = getApplicationById(id);
        application.setStatus(ApplicationStatus.REJECTED);
        application.setReviewedAt(LocalDateTime.now());
        return sponsorApplicationRepository.save(application);
    }

    public void deleteApplication(int id) {
        sponsorApplicationRepository.deleteById(id);
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
            });

            sponsorApplicationRepository.saveAll(expiredApplications);
            System.out.println("Auto-rejected " + expiredApplications.size() + " expired applications.");
        }
    }

}
