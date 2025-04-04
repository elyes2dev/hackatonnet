package com.esprit.pi.services;

import com.esprit.pi.entities.ImageModel;
import com.esprit.pi.repositories.IImageModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageModelServiceImpl implements IImageModelService {
    @Autowired
    private IImageModelRepository imageRepository;

    @Override
    public ImageModel getImageById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }
}