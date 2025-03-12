package com.esprit.pi.services;

import com.esprit.pi.entities.Workshop;
import com.esprit.pi.repositories.IWorkshopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkshopServiceImpl implements IWorkshopService {

    @Autowired
    private IWorkshopRepository workshopRepository;

    @Override
    public Workshop addWorkshop(Workshop workshop) {
        return workshopRepository.save(workshop);
    }

    @Override
    public Optional<Workshop> findById(Long id) {
        return workshopRepository.findById(id);
    }

    @Override
    public List<Workshop> findAll() {
        return workshopRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        workshopRepository.deleteById(id);
    }
}
