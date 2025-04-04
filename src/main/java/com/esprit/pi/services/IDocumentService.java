package com.esprit.pi.services;

import com.esprit.pi.entities.Document;

import java.util.List;
import java.util.Optional;

public interface IDocumentService {
    public Document saveDocuments(Document document);
    public Optional<Document> findById(Long id);
    public List<Document> findAll();
    public void deleteById(Long id);
}
