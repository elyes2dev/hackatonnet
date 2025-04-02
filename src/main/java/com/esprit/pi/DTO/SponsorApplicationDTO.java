package com.esprit.pi.DTO;

import com.esprit.pi.entities.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SponsorApplicationDTO {
    private int id;
    private String companyName;
    private String companyLogo;
    private String documentPath;
    private int registrationNumber;
    private String websiteUrl;
    private ApplicationStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;

    private UserDTO user; // Updated to use UserDTO
}