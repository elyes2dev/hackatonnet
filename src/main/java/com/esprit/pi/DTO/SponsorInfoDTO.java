package com.esprit.pi.DTO;

import com.esprit.pi.entities.SponsorReward;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SponsorInfoDTO {
    private Long sponsorId;
    private String name;
    private String lastname;
    private String companyName;
    private String companyLogo;
    private String websiteUrl;
    private int reputationPoints;
    private SponsorReward.SponsorBadge badge;
    private String badgeIcon; // For emoji representation
}
