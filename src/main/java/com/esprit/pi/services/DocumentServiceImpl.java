package com.esprit.pi.services;

import com.esprit.pi.entities.Document;
import com.esprit.pi.repositories.IDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements IDocumentService {

    @Autowired
    private IDocumentRepository documentRepository;

    @Override
    public Document saveDocuments(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }
}
