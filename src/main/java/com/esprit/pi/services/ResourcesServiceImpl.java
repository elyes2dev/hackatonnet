package com.esprit.pi.services;

import com.esprit.pi.entities.Resources;
import com.esprit.pi.repositories.IResourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourcesServiceImpl implements IResourcesService {

    @Autowired
    private IResourcesRepository resourcesRepository;

    @Override
    public Resources save(Resources resources) {
        return resourcesRepository.save(resources);
    }

    @Override
    public Optional<Resources> findById(Long id) {
        return resourcesRepository.findById(id);
    }

    @Override
    public List<Resources> findAll() {
        return resourcesRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        resourcesRepository.deleteById(id);
    }
}