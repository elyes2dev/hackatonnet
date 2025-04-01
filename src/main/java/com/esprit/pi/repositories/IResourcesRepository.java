package com.esprit.pi.repositories;

import com.esprit.pi.entities.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IResourcesRepository extends JpaRepository<Resources, Long> {
}
