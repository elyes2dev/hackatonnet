package com.esprit.pi.services;

import com.esprit.pi.entities.Workshop;

import java.util.List;
import java.util.Optional;

public interface IWorkshopService {
    public Workshop addWorkshop(Workshop workshop);
    public Optional<Workshop> findById(Long id);
    public List<Workshop> findAll();
    public void deleteById(Long id);
}
