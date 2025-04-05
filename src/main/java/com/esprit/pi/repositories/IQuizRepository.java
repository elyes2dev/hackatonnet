package com.esprit.pi.repositories;

import com.esprit.pi.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IQuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByWorkshopId(Long workshopId);

    @Modifying
    @Query("DELETE FROM Quiz q WHERE q.id_quiz = :id")
    void deleteQuizById(Long id);

}
