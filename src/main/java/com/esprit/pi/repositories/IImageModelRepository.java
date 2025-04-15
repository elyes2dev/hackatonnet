package com.esprit.pi.repositories;

import com.esprit.pi.entities.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IImageModelRepository extends JpaRepository<ImageModel, Long> {

}
