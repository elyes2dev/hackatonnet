package com.esprit.pi.repositories;

import com.esprit.pi.entities.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWorkshopRepository extends JpaRepository<Workshop, Long> {
}
