package com.esprit.pi.DTO;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationResultDTO {
    private boolean success;
    private String detectedCompanyName;
    private String message;
    private Map<String, Boolean> fieldMatches; // e.g., {"company_name": true, "registration_number": false}
}
