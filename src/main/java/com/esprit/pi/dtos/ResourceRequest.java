package com.esprit.pi.dtos;

import com.esprit.pi.entities.Resources;

public class ResourceRequest {

    private Long workshopId;
    private Resources resource;

    // Getters and Setters
    public Long getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(Long workshopId) {
        this.workshopId = workshopId;
    }

    public Resources getResource() {
        return resource;
    }

    public void setResource(Resources resource) {
        this.resource = resource;
    }
}

