package com.esprit.pi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HackathonDTO {
    private Long id;
    private String title;
    private Date startDate;
    private Date endDate;
}
