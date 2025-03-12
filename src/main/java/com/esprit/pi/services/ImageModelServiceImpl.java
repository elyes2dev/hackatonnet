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
    private IImageModelRepository imageModelRepository;

    @Override
    public ImageModel save(ImageModel imageModel) {
        return imageModelRepository.save(imageModel);
    }

    @Override
    public Optional<ImageModel> findById(Long id) {
        return imageModelRepository.findById(id);
    }

    @Override
    public List<ImageModel> findAll() {
        return imageModelRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        imageModelRepository.deleteById(id);
    }
}