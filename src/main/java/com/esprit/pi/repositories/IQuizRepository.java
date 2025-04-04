package com.esprit.pi.repositories;

import com.esprit.pi.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IQuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByWorkshopId(Long workshopId);
}
