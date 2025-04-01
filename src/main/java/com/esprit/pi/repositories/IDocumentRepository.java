package com.esprit.pi.repositories;

import com.esprit.pi.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDocumentRepository extends JpaRepository<Document, Long> {

    @Query("SELECT d FROM Document d WHERE d.resource.id = :resourceId")
    List<Document> findByResourceId(@Param("resourceId") Long resourceId);
}
