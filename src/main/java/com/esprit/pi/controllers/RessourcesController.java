package com.esprit.pi.controllers;

import com.esprit.pi.entities.Document;
import com.esprit.pi.entities.ImageModel;
import com.esprit.pi.entities.Resources;
import com.esprit.pi.services.FileStorageService;
import com.esprit.pi.services.RessourcesServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/workshops/{workshopId}/resources")
public class RessourcesController {
    private final RessourcesServiceImpl ressourcesServiceImpl;
    private final FileStorageService fileStorageService;

    @Autowired
    public RessourcesController(RessourcesServiceImpl ressourcesServiceImpl,
                                FileStorageService fileStorageService) {
        this.ressourcesServiceImpl = ressourcesServiceImpl;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resources> createResource(
            @PathVariable Long workshopId,
            @RequestPart("resource") String resourceJson,
            @RequestPart(required = false) MultipartFile[] documents,
            @RequestPart(required = false) MultipartFile[] images) {

        // Manually convert JSON to Resources object
        ObjectMapper objectMapper = new ObjectMapper();
        Resources resource;
        try {
            resource = objectMapper.readValue(resourceJson, Resources.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }

        Resources createdResource = ressourcesServiceImpl.createResource(workshopId, resource);

        // Handle document uploads
        if (documents != null) {
            for (MultipartFile document : documents) {
                ressourcesServiceImpl.addDocumentToResource(createdResource.getId(), document);
            }
        }

        // Handle image uploads
        if (images != null) {
            for (MultipartFile image : images) {
                ressourcesServiceImpl.addImageToResource(createdResource.getId(), image);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdResource);
    }


    // READ - Get single resource
    @GetMapping("/{resourceId}")
    public ResponseEntity<Resources> getResource(
            @PathVariable Long workshopId,
            @PathVariable Long resourceId) {
        Resources resource = ressourcesServiceImpl.getResource(workshopId, resourceId);
        return ResponseEntity.ok(resource);
    }

    // READ - Get all resources for workshop
    @GetMapping
    public ResponseEntity<List<Resources>> getAllResources(
            @PathVariable Long workshopId) {
        List<Resources> resources = ressourcesServiceImpl.getAllResourcesForWorkshop(workshopId);
        return ResponseEntity.ok(resources);
    }

    // UPDATE
    @PutMapping("/{resourceId}")
    public ResponseEntity<Resources> updateResource(
            @PathVariable Long workshopId,
            @PathVariable Long resourceId,
            @RequestBody Resources resourceDetails) {
        Resources updatedResource = ressourcesServiceImpl.updateResource(workshopId, resourceId, resourceDetails);
        return ResponseEntity.ok(updatedResource);
    }

    // DELETE
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long workshopId,
            @PathVariable Long resourceId) {
        ressourcesServiceImpl.deleteResource(workshopId, resourceId);
        return ResponseEntity.noContent().build();
    }

    // FILE DOWNLOAD
    @GetMapping("/{resourceId}/documents/{documentId}/download")
    public ResponseEntity<org.springframework.core.io.Resource> downloadDocument(
            @PathVariable Long documentId) {
        Document document = ressourcesServiceImpl.getDocument(documentId);
        org.springframework.core.io.Resource file = fileStorageService.loadFileAsResource(document.getFilePath());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getName() + "\"")
                .body(file);
    }

    // ADDITIONAL FILE UPLOAD ENDPOINTS

    @PostMapping("/{resourceId}/documents")
    public ResponseEntity<Document> uploadDocument(
            @PathVariable Long resourceId,
            @RequestParam("file") MultipartFile file) {
        Document document = ressourcesServiceImpl.addDocumentToResource(resourceId, file);
        return ResponseEntity.ok(document);
    }

    @PostMapping("/{resourceId}/images")
    public ResponseEntity<ImageModel> uploadImage(
            @PathVariable Long resourceId,
            @RequestParam("file") MultipartFile file) {
        ImageModel image = ressourcesServiceImpl.addImageToResource(resourceId, file);
        return ResponseEntity.ok(image);
    }
}