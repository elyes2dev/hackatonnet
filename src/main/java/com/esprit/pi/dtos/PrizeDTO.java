package com.esprit.pi.dtos;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.Prize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrizeDTO {
    private long id;
    private Prize.PrizeType prizeType;
    private BigDecimal amount;  // If prizeType = MONEY
    private String productName; // If prizeType = PRODUCT
    private String productDescription; // If prizeType = PRODUCT
    private ApplicationStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private Prize.PrizeCategory prizeCategory;

    private UserDTO sponsor; // Full user object
    private HackathonDTO hackathon; // Full hackathon object
}
