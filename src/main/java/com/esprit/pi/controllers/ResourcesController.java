package com.esprit.pi.controllers;

import com.esprit.pi.dtos.ResourceRequest;
import com.esprit.pi.entities.Resources;
import com.esprit.pi.entities.Workshop;
import com.esprit.pi.repositories.IWorkshopRepository;
import com.esprit.pi.services.IResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/resources")
public class ResourcesController {

    @Autowired
    private IResourcesService resourcesService;

    @Autowired
    private IWorkshopRepository workshopRepository; // Add this to access the

    // Create or update a resource
    // Create or update a resource
    @PostMapping
    public ResponseEntity<Resources> createOrUpdateResource(@RequestBody ResourceRequest resourceRequest) {
        Workshop workshop = workshopRepository.findById(resourceRequest.getWorkshopId())
                .orElse(null);

        // Get the Resource object from the request
        Resources resource = resourceRequest.getResource();
        // Set the Workshop entity on the Resource object
        resource.setWorkshop(workshop);

        // Save the resource
        Resources savedResource = resourcesService.save(resource);
        return new ResponseEntity<>(savedResource, HttpStatus.CREATED);
    }



    // Get a resource by ID
    @GetMapping("/{id}")
    public ResponseEntity<Resources> getResourceById(@PathVariable Long id) {
        Optional<Resources> resource = resourcesService.findById(id);
        return resource.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all resources
    @GetMapping
    public ResponseEntity<List<Resources>> getAllResources() {
        List<Resources> resources = resourcesService.findAll();
        return ResponseEntity.ok(resources);
    }

    // Delete a resource by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourceById(@PathVariable Long id) {
        if (resourcesService.findById(id).isPresent()) {
            resourcesService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

