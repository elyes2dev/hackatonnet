package com.esprit.pi.repositories;

import com.esprit.pi.entities.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IHackathonRepository extends JpaRepository<Hackathon,Long> {
}
