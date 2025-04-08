package com.esprit.pi.services;

import com.esprit.pi.entities.ImageModel;
import com.esprit.pi.entities.Resources;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IResourcesService {
    public List<Resources> retrieveAllResources();

    public Resources retrieveResource(Long id_resource);

    public Resources addResource(Resources r);

    public void removeResource(Long id_resource);

    public Resources modifyResource(Resources r);

    /*
    public Resource affecterDocumentaResource(Resource r, Long id_document);

    public Resource affecterSubjectResource(Resource r, Long id_sujet);

     */
    Set<ImageModel> uploadImage(MultipartFile[] multipartFiles) throws IOException;

    Resources addResourceWithImages(Resources resource, MultipartFile[] files) throws IOException;

    }
