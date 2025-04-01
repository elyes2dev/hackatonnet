package com.esprit.pi.repositories;

import com.esprit.pi.entities.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IImageModelRepository extends JpaRepository<ImageModel, Long> {
}
