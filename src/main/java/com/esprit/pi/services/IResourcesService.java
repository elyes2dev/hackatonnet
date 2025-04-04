package com.esprit.pi.services;

import com.esprit.pi.entities.Resources;

import java.util.List;
import java.util.Optional;

public interface IResourcesService {
    public Resources save(Resources resources);
    public Optional<Resources> findById(Long id);
    public List<Resources> findAll();
    public void deleteById(Long id);
}
