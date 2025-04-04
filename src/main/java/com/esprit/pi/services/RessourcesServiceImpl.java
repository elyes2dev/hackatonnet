package com.esprit.pi.services;

import com.esprit.pi.entities.Document;
import com.esprit.pi.entities.ImageModel;
import com.esprit.pi.entities.Resources;
import com.esprit.pi.entities.Workshop;
import com.esprit.pi.repositories.IDocumentRepository;
import com.esprit.pi.repositories.IImageModelRepository;
import com.esprit.pi.repositories.IResourcesRepository;
import com.esprit.pi.repositories.IWorkshopRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class RessourcesServiceImpl {
    private final IResourcesRepository resourcesRepository;
    private final IWorkshopRepository workshopRepository;
    private final IDocumentRepository documentRepository;
    private final IImageModelRepository imageRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public RessourcesServiceImpl(IResourcesRepository resourcesRepository,
                                 IWorkshopRepository workshopRepository,
                                 IDocumentRepository documentRepository,
                                 IImageModelRepository imageRepository,
                                 FileStorageService fileStorageService) {
        this.resourcesRepository = resourcesRepository;
        this.workshopRepository = workshopRepository;
        this.documentRepository = documentRepository;
        this.imageRepository = imageRepository;
        this.fileStorageService = fileStorageService;
    }

    // Resource CRUD Operations
    public Resources createResource(Long workshopId, Resources resource) {
        Workshop workshop = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new RuntimeException("Workshop not found with id: " + workshopId));
        resource.setWorkshop(workshop);
        return resourcesRepository.save(resource);
    }

    public Resources getResource(Long workshopId, Long resourceId) {
        return resourcesRepository.findByWorkshopIdAndId(workshopId, resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));
    }

    public List<Resources> getAllResourcesForWorkshop(Long workshopId) {
        return resourcesRepository.findByWorkshopId(workshopId);
    }

    public Resources updateResource(Long workshopId, Long resourceId, Resources resourceDetails) {
        Resources resource = getResource(workshopId, resourceId);
        resource.setName(resourceDetails.getName());
        resource.setDescription(resourceDetails.getDescription());
        resource.setNiveau(resourceDetails.getNiveau());
        return resourcesRepository.save(resource);
    }

    public void deleteResource(Long workshopId, Long resourceId) {
        Resources resource = getResource(workshopId, resourceId);
        // Delete associated files first
        resource.getDocuments().forEach(document -> {
            fileStorageService.deleteFile(document.getFilePath());
            documentRepository.delete(document);
        });
        resource.getImages().forEach(image -> {
            fileStorageService.deleteFile(image.getFilePath());
            imageRepository.delete(image);
        });
        resourcesRepository.delete(resource);
    }

    // Document Operations
    public Document getDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
    }

    public Document addDocumentToResource(Long resourceId, MultipartFile file) {
        Resources resource = resourcesRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        String fileName = fileStorageService.storeFile(file);

        Document document = new Document();
        document.setName(file.getOriginalFilename());
        document.setFilePath(fileName);
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setResource(resource);

        return documentRepository.save(document);
    }

    public void deleteDocument(Long documentId) {
        Document document = getDocument(documentId);
        fileStorageService.deleteFile(document.getFilePath());
        documentRepository.delete(document);
    }

    // Image Operations
    public ImageModel addImageToResource(Long resourceId, MultipartFile file) {
        Resources resource = resourcesRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        String fileName = fileStorageService.storeFile(file);

        ImageModel image = new ImageModel();
        image.setName(file.getOriginalFilename());
        image.setFilePath(fileName);
        image.setFileType(file.getContentType());
        image.setFileSize(file.getSize());
        image.setResource(resource);

        return imageRepository.save(image);
    }

    public void deleteImage(Long imageId) {
        ImageModel image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
        fileStorageService.deleteFile(image.getFilePath());
        imageRepository.delete(image);
    }
}