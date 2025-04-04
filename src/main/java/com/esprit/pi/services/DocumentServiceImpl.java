package com.esprit.pi.services;

import com.esprit.pi.entities.Document;
import com.esprit.pi.entities.ImageModel;
import com.esprit.pi.repositories.IDocumentRepository;
import com.esprit.pi.repositories.IImageModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements IDocumentService {

    @Autowired
    IImageModelRepository documentRepository;

    @Override
    public ImageModel saveDocument(ImageModel imageModel) {
        return documentRepository.save(imageModel);
    }

    @Override
    public List<ImageModel> saveDocuments(List<ImageModel> imageModels) {
        return documentRepository.saveAll(imageModels);
    }
}
