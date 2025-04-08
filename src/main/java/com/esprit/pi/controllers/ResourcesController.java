package com.esprit.pi.controllers;

import com.esprit.pi.dtos.ResourceRequest;
import com.esprit.pi.entities.ImageModel;
import com.esprit.pi.entities.Resources;
import com.esprit.pi.entities.Workshop;
import com.esprit.pi.entities.skillEnum;
import com.esprit.pi.repositories.IWorkshopRepository;
import com.esprit.pi.services.FileStorageService;
import com.esprit.pi.services.IImageModelService;
import com.esprit.pi.services.IResourcesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workshops/{workshopId}/resources")
public class ResourcesController {

    @Autowired
    private IResourcesService resourcesService;

    @Autowired
    private IWorkshopRepository workshopRepository; // You'll need this to validate workshop existence


    @Autowired
    private FileStorageService fileStorageService; // You'll need this to validate workshop existence


    @Autowired
    private IImageModelService imageModelService; // You'll need this to validate workshop existence

    // Get all resources for a specific workshop
    @GetMapping
    public ResponseEntity<List<Resources>> getAllWorkshopResources(@PathVariable Long workshopId) {
        if (!workshopRepository.existsById(workshopId)) {
            return ResponseEntity.notFound().build();
        }
        List<Resources> resources = resourcesService.retrieveAllResources().stream()
                .filter(r -> r.getWorkshop().getId().equals(workshopId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    // Get a specific resource by ID within a workshop
    @GetMapping("/{resourceId}")
    public ResponseEntity<Resources> getResourceById(
            @PathVariable Long workshopId,
            @PathVariable Long resourceId) {

        Resources resource = resourcesService.retrieveResource(resourceId);
        if (resource == null || !resource.getWorkshop().getId().equals(workshopId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resource);
    }

    // Create a new resource for a workshop (with optional files)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resources> createResource(
            @PathVariable Long workshopId,
            @RequestParam("resource") String resourceJson,
            @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {

        // Find and validate workshop exists
        Workshop workshop = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new RuntimeException("Workshop not found with id: " + workshopId));

        // Parse JSON to Resource object
        ObjectMapper objectMapper = new ObjectMapper();
        Resources resource = objectMapper.readValue(resourceJson, Resources.class);
        resource.setWorkshop(workshop);

        // Handle file upload if present
        if (files != null && files.length > 0) {
            Resources savedResource = resourcesService.addResourceWithImages(resource, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedResource);
        } else {
            Resources savedResource = resourcesService.addResource(resource);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedResource);
        }
    }

    // Update a resource
    @PutMapping("/{resourceId}")
    public ResponseEntity<Resources> updateResource(
            @PathVariable Long workshopId,
            @PathVariable Long resourceId,
            @RequestBody Resources resourceDetails) {

        Resources existingResource = resourcesService.retrieveResource(resourceId);
        if (existingResource == null || !existingResource.getWorkshop().getId().equals(workshopId)) {
            return ResponseEntity.notFound().build();
        }

        // Update fields
        existingResource.setName(resourceDetails.getName());
        existingResource.setDescription(resourceDetails.getDescription());
        existingResource.setNiveau(resourceDetails.getNiveau());

        Resources updatedResource = resourcesService.modifyResource(existingResource);
        return ResponseEntity.ok(updatedResource);
    }

    // Delete a resource
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long workshopId,
            @PathVariable Long resourceId) {

        Resources resource = resourcesService.retrieveResource(resourceId);
        if (resource == null || !resource.getWorkshop().getId().equals(workshopId)) {
            return ResponseEntity.notFound().build();
        }

        resourcesService.removeResource(resourceId);
        return ResponseEntity.noContent().build();
    }

    // Upload images to an existing resource
    @PostMapping("/{resourceId}/images")
    public ResponseEntity<Resources> uploadImagesToResource(
            @PathVariable Long workshopId,
            @PathVariable Long resourceId,
            @RequestParam("files") MultipartFile[] files) throws IOException {

        Resources resource = resourcesService.retrieveResource(resourceId);
        if (resource == null || !resource.getWorkshop().getId().equals(workshopId)) {
            return ResponseEntity.notFound().build();
        }

        Set<ImageModel> newImages = resourcesService.uploadImage(files);
        if (resource.getResourceImages() == null) {
            resource.setResourceImages(new HashSet<>());
        }
        resource.getResourceImages().addAll(newImages);

        Resources updatedResource = resourcesService.modifyResource(resource);
        return ResponseEntity.ok(updatedResource);
    }

    @GetMapping("/{resourceId}/images/{imageId}/download")
    public ResponseEntity<org.springframework.core.io.Resource> downloadImage(
            @PathVariable Long resourceId,
            @PathVariable Long imageId) {
        // Get the image model
        ImageModel imageModel = imageModelService.getImageById(imageId);
        if (imageModel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Create an in-memory resource with the byte[] data from the database
        org.springframework.core.io.Resource resource = new ByteArrayResource(imageModel.getPicByte());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageModel.getType()))  // Use the correct media type from your entity
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + imageModel.getPath() + "\"")
                .body(resource);
    }



}

