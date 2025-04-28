package com.esprit.pi.repositories;

import com.esprit.pi.entities.ListMentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListMentorRepository extends JpaRepository<ListMentor, Long> {
    List<ListMentor> findByMentorId(Long mentorId);
    List<ListMentor> findByHackathonId(Long hackathonId);



    /**
     * Find a mentor's registration for a specific hackathon
     *
     * @param mentorId The ID of the mentor user
     * @param hackathonId The ID of the hackathon
     * @return The ListMentor record if found
     */
    Optional<ListMentor> findByMentorIdAndHackathonId(Long mentorId, Long hackathonId);
}