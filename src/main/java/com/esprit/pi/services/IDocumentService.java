package com.esprit.pi.services;

import com.esprit.pi.entities.Document;
import com.esprit.pi.entities.ImageModel;

import java.util.List;
import java.util.Optional;

public interface IDocumentService {
    ImageModel saveDocument(ImageModel imageModel);
    List<ImageModel> saveDocuments(List<ImageModel> imageModels);

}
