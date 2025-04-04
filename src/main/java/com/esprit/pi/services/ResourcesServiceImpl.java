package com.esprit.pi.services;

import com.esprit.pi.entities.ImageModel;
import com.esprit.pi.entities.Resources;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.IImageModelRepository;
import com.esprit.pi.repositories.IResourcesRepository;
import com.esprit.pi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ResourcesServiceImpl implements IResourcesService {
    @Autowired
    IResourcesRepository resourceRepository;
    @Autowired
    private IImageModelRepository imageRepository; // You

    @Override
    public List<Resources> retrieveAllResources() {
        return resourceRepository.findAll();
    }

    @Override
    public Resources retrieveResource(Long id_resource) {
        return resourceRepository.findById(id_resource).orElse(null);
    }

    @Override
    public Resources addResource(Resources r) {
        return resourceRepository.save(r);
    }

    @Override
    public void removeResource(Long id_resource) {
        resourceRepository.deleteById(id_resource);
    }

    public Resources modifyResource(Resources r) {
        if (r.getId() == null) {
            throw new IllegalArgumentException("Resource ID cannot be null");
        }
        return resourceRepository.save(r);
    }

    @Override
    public Set<ImageModel> uploadImage(MultipartFile[] multipartFiles) throws IOException {
        Set<ImageModel> imageModels = new HashSet<>();
        for (MultipartFile file : multipartFiles) {
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();
            byte[] bytes = file.getBytes();
            ImageModel imageModel = new ImageModel(originalFilename, contentType, bytes);
            imageModels.add(imageModel);
        }
        return imageModels;
    }

    @Override
    public Resources addResourceWithImages(Resources resource, MultipartFile[] files) throws IOException {
        Set<ImageModel> images = uploadImage(files);
        resource.setResourceImages(images);
        return resourceRepository.save(resource);
    }



}