package com.esprit.pi.repositories;

import com.esprit.pi.entities.ListMentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListMentorRepository extends JpaRepository<ListMentor, Long> {
    List<ListMentor> findByMentorId(Long mentorId);
    List<ListMentor> findByHackathonId(Long hackathonId);
}