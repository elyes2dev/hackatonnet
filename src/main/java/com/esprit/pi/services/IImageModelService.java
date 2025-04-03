package com.esprit.pi.services;

import com.esprit.pi.entities.ImageModel;

import java.util.List;
import java.util.Optional;

public interface IImageModelService {
    ImageModel save(ImageModel imageModel);
    Optional<ImageModel> findById(Long id);
    List<ImageModel> findAll();
    void deleteById(Long id);
}
