package com.esprit.pi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketDTO {
    public String description;
    public String status;
    public Long userId;
}

