package com.esprit.pi.repositories;

import com.esprit.pi.entities.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IResourcesRepository extends JpaRepository<Resources, Long> {
    @Query("SELECT r FROM Resources r WHERE r.workshop.id = :workshopId")
    List<Resources> findByWorkshopId(@Param("workshopId") Long workshopId);

    @Query("SELECT r FROM Resources r WHERE r.workshop.id = :workshopId AND r.id = :id")
    Optional<Resources> findByWorkshopIdAndId(@Param("workshopId") Long workshopId, @Param("id") Long id);
}
